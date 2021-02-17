/*
 * Copyright (C) 2016-2020 Universidad Nacional de Educación a Distancia (UNED)
 *
 * This program is free software for non-commercial use:
 * you can redistribute it and/or modify it under the terms of the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * (CC BY-NC-SA 4.0) as published by the Creative Commons Corporation,
 * either version 4 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * section 5 of the CC BY-NC-SA 4.0 License for more details.
 *
 * You should have received a copy of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0) 
 * license along with this program. If not,
 * see <http://creativecommons.org/licenses/by-nc-sa/4.0/>.
 *
 */

package hesml_umls_benchmark.benchmarks;

import bioc.BioCDocument;
import gov.nih.nlm.nls.metamap.document.FreeText;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import gov.nih.nlm.nls.ner.MetaMapLite;
import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml_umls_benchmark.SemanticLibraryType;
import hesml_umls_benchmark.semantclibrarywrappers.UMLSSemanticLibraryWrapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import hesml_umls_benchmark.ISemanticLibrary;
import hesml_umls_benchmark.UMLSOntologyType;
import java.util.Map;

/**
 * This class implements a benchmark to compare the performance
 * of the UMLS similarity libraries in the evaluation of the degree
 * of similarity between pairs of sentences from a given dataset
 * using the UBSM similarity measure from:
 * 
 * Sogancioglu, Gizem, Hakime Öztürk, and Arzucan Özgür. 2017. 
 * “BIOSSES: A Semantic Sentence Similarity Estimation System 
 * for the Biomedical Domain.” Bioinformatics  33 (14): i49–58.
 * @author alicia
 */

class MeSHSentencesEvalBenchmark extends SemanticLibraryBenchmark
{
    /**
     * Column offset for the main attributes extracted from concept and
     * relationship files.
     */
    
    private static final int CONCEPT_ID = 0;
    private static final int ACTIVE_ID = 2;
    
    /**
     * Setup parameters of the semantic similarity measure
     */
    
    private SimilarityMeasureType           m_MeasureType;
    private final IntrinsicICModelType      m_icModel;

    /**
     * Path to the input dataset for evaluating sentences
     */
    HashMap<ISemanticLibrary, ArrayList<ArrayList<String>>> m_datasets;
    
    /**
     * Metamap Lite instance
     */
    
    protected MetaMapLite m_metaMapLiteInst;
    
    /**
     * Cached similarity values between concept pairs
     */
    
    HashMap<CuiPair, Double> m_CachedSimilarityValues;
    
    /**
     * Cached concept pairs 
     */
    
    HashSet<CuiPair> m_CachedCuiPairs;
    
    /**
     * Overall running time to get all cached similarity values
     */
    
    private double m_overallCachingTime;

    /**
     * Constructor of the sentence pairs benchmark
     * @param libraries
     * @param similarityMeasure
     * @param icModel
     * @param strDatasetPath
     * @param strMeSHdir
     * @param strMeSHXmlConceptFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @throws Exception 
     */

    MeSHSentencesEvalBenchmark(
            SemanticLibraryType[]               libraries,
            SimilarityMeasureType               similarityMeasure,
            IntrinsicICModelType                icModel,
            HashMap<SemanticLibraryType,String> strDatasetPaths,
            String                              strMeSHdir,
            String                              strMeSHXmlConceptFileName,
            String                              strUmlsDir,
            String                              strSNOMED_CUI_mappingfilename) 
            throws Exception
    {
        // We initialize the base class
        
        super(libraries, strMeSHdir, strMeSHXmlConceptFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename);    
        
        // We initialize the attributes of the object
        
        m_MeasureType = similarityMeasure;
        m_icModel = icModel;
        m_CachedSimilarityValues = new HashMap<>();
        m_datasets = new HashMap<>();
        m_CachedCuiPairs = new HashSet<>();
        
        // We load the dataset
        
        loadDatasetBenchmark(strDatasetPaths);
        
        // We load METAMAP Lite
        
        loadMetamapLite();
        
        // We annotate all CUI ceoncept mentions in all sentences of the dataste
        
        annotateDatasets();
    }
    
    /**
     * This function releases the resources used by the benchmark.
     */
    
    @Override
    public void clear()
    {
        // We release the resources
        
        m_CachedSimilarityValues.clear();
        
        // We call the base clase clear()
        
        super.clear();
    }
    
    /**
     * This function executes the benchmark and saves the raw results into
     * the output file.
     */
    
    @Override
    public void run(
            String  strOutputFilename) throws Exception
    {
        // We initilizae the number of runs
        
        int nRuns = 5;
        
        // We create the output data matrix and fill the row headers
        
        String[][] strOutputDataMatrix = new String[nRuns + 3][m_Libraries.length + 1];
        
        // We fill the first row header
        
        strOutputDataMatrix[0][0] = "#run";
        strOutputDataMatrix[nRuns+1][0] = "Total sentences";
        strOutputDataMatrix[nRuns+2][0] = "Total CUI comparisons";
        
        // We evaluate the performance of libraries
        
        for (int iLib = 0; iLib < m_Libraries.length; iLib++)
        {
            // Debugginf message
            
            System.out.println("---------------------------------");
            System.out.println("\t" + m_Libraries[iLib].getLibraryType().toString()
                    + " library: evaluating the similarity between sentence pairs.");
            
            // We set the row header
            
            strOutputDataMatrix[0][iLib + 1] = m_Libraries[iLib].getLibraryType().toString()
                                        + "-" + m_MeasureType.toString();
            
            // We set the total sentences in the output matrix
            
            strOutputDataMatrix[nRuns+1][iLib + 1] = String.valueOf(m_datasets.get(m_Libraries[iLib]).get(0).size());

            // We compute the list of CUI pairs that will be evaluated

            constructCuiPairsSet(m_Libraries[iLib]);
            
            // We set the total of CUI codes comparisons
            
            strOutputDataMatrix[nRuns+2][iLib + 1] = String.valueOf(m_CachedCuiPairs.size());
            
            // We set the output value
            
            double[] runningTimes;
            
            // We set the similarity measure to be used
            
            if (m_Libraries[iLib].setSimilarityMeasure(m_icModel, m_MeasureType))
            {
                // We load MeSH ontology and the resources of the library

                m_Libraries[iLib].loadOntology();

                // We evaluate the library

                runningTimes = EvaluateLibrary(m_Libraries[iLib], nRuns);

                // We release the database and resources used by the library

                m_Libraries[iLib].unloadOntology();
            }
            else
            {
                // The library does not implement the measure.
                // We set the running times to NaN
                
                runningTimes = getNullRunningTimes(nRuns);
            }
            
            // We save the results
            
            CopyRunningTimesToMatrix(strOutputDataMatrix, runningTimes, iLib + 1);
        }
        
        // We write the output raw data
        
        writeCSVfile(strOutputDataMatrix, strOutputFilename);
    }
    
    /**
     * This function fills the data matrix with the running times reported
     * in the experiments.
     * @param strOutputDataMatrix
     * @param runningTimesInSecs
     * @param iCol 
     */
    
    private void CopyRunningTimesToMatrix(
            String[][]  strOutputDataMatrix,
            double[]    runningTimesInSecs,
            int         iCol)
    {
        // We copy the values
        
        for (int iRun = 0; iRun < runningTimesInSecs.length; iRun++)
        {
            strOutputDataMatrix[iRun + 1][0] = Integer.toString(iRun);
            strOutputDataMatrix[iRun + 1][iCol] = Double.toString(runningTimesInSecs[iRun]);
        }
    }
    
    /**
     * This function sets the Seco et al.(2004)[1] and the Lin similarity
     * measure [2] using HESML [3] and SML [4] libraries. Then, the function
     * evaluates the Lin similarity [2] of the set of random concept pairs.
     * 
     * [1] N. Seco, T. Veale, J. Hayes,
     * An intrinsic information content metric for semantic similarity
     * in WordNet, in: R. López de Mántaras, L. Saitta (Eds.), Proceedings
     * of the 16th European Conference on Artificial Intelligence (ECAI),
     * IOS Press, Valencia, Spain, 2004: pp. 1089–1094.
     * 
     * [2] D. Lin, An information-theoretic definition of similarity,
     * in: Proceedings of the 15th International Conference on Machine
     * Learning, Madison, WI, 1998: pp. 296–304.
     * 
     * [3] J.J. Lastra-Díaz, A. García-Serrano, M. Batet, M. Fernández, F. Chirigati,
     * HESML: a scalable ontology-based semantic similarity measures library
     * with a set of reproducible experiments and a replication dataset,
     * Information Systems. 66 (2017) 97–118.
     * 
     * [4] S. Harispe, S. Ranwez, S. Janaqi, J. Montmain,
     * The semantic measures library and toolkit: fast computation of semantic
     * similarity and relatedness using biomedical ontologies,
     * Bioinformatics. 30 (2014) 740–742.
     * 
     * @param library
     * @param umlsCuiPairs
     * @param nRuns
     */
    
    private double[] EvaluateLibrary(
            ISemanticLibrary    library,
            int                 nRuns) throws Exception
    {
        // We initialize the output vector
        
        double[] runningTimes = new double[nRuns];
        
        // We execute multiple times the benchmark to compute a stable running time

        double accumulatedEvalTime = 0.0;
        
        for (int iRun = 0; iRun < nRuns; iRun++)
        {
            // Becaause of the large running times of the UMLS_SIMILARITY library,
            // this library is evaluated just one time, whilst the rest of 
            // libraries are evaluated n times
            // We compute all valus in one unique query

            boolean pedersenLib = (library.getLibraryType() == SemanticLibraryType.UMLS_SIMILARITY);

            if (pedersenLib && (m_CachedSimilarityValues.size() == 0))
            {
                buildCachedSimilarityValues((UMLSSemanticLibraryWrapper) library);
            }
            
            // We initialize the stopwatch

            long startTime = System.currentTimeMillis();

            // We evaluate the concept pairs
            
            for (int i = 0; i < m_datasets.get(library).get(0).size(); i++)
            {
                double similarity = getUBSMsimilarityValue(
                        m_datasets.get(library).get(0).get(i), 
                        m_datasets.get(library).get(1).get(i),
                        library);
            }

            // We compute the elapsed time in seconds

            runningTimes[iRun] = (System.currentTimeMillis() - startTime) / 1000.0;

            // We accumulate the query time for the UMLS::Similarity library
            // becuase it has bnen pre-calculated before
            
            if (pedersenLib)
            {
                runningTimes[iRun] += m_overallCachingTime;
                m_CachedSimilarityValues.clear();
            }
            
            // We accumulate the overall time
            
            accumulatedEvalTime += runningTimes[iRun];
        }
        
        // We compute the averga running time
        
        double averageRuntime = accumulatedEvalTime / nRuns;
        
        // We print the average results
        
        System.out.println("# UMLS sentence pairs evaluated = " + m_datasets.get(library).get(0).size());
        
        System.out.println(library.getLibraryType()
                + " Average speed (#sentence pairs/second) = "
                + ((double)m_datasets.get(library).get(0).size() / averageRuntime));
        
        // We return the results
        
        return (runningTimes);
    }
    
    /**
     * This function computes the similarity values among all CUI pairs
     * appearing in the dataset.
     */
    
    private void buildCachedSimilarityValues(
            UMLSSemanticLibraryWrapper pedersenLib) throws InterruptedException, Exception
    {
        // We reset the cache
        
        m_CachedSimilarityValues.clear();
        
        // We copy all CUI pairs into a String matrix
        
        String[][] allPairs = new String[m_CachedCuiPairs.size()][2];
        
        int ipair = 0;
        
        for (CuiPair pair: m_CachedCuiPairs)
        {
            allPairs[ipair][0] = pair.getCuiCode1();
            allPairs[ipair++][1] = pair.getCuiCode2();
        }
        
        // We get the similarity and running time per concept pair
       
        double[][] simValues = pedersenLib.getSimilaritiesAndRunningTimes(
                                allPairs, UMLSOntologyType.MeSH);
       
       // We store all values in the cache
       
       m_overallCachingTime = 0.0;
       
       for (int i = 0; i < allPairs.length; i++)
       {          
           // We regiter the CUI pair
           
           m_CachedSimilarityValues.put(new CuiPair(allPairs[i][0], allPairs[i][1]),
                   new Double(simValues[i][0]));
           
           // We accumulate the overall running time
           
           m_overallCachingTime += simValues[i][1];
       }
              
       // Warning message
       
       System.out.println("Pre-calculation of UMLS::Similarity value (seconds) = "
            + m_overallCachingTime);
    }
    
    /**
     * This function construct the set of CUI pairs that will be evaluated by the library
     * @return 
     */
    
    private HashSet<CuiPair> constructCuiPairsSet(ISemanticLibrary library)
    {
        // We clear the object

        m_CachedCuiPairs.clear();
        
        // We analyze all sentences to extract all CUI pairs for each sentences  
        
        for (int i = 0; i < m_datasets.get(library).get(0).size(); i++)
        {
            // Preprocess the sentences and get the tokens for each sentence

            String[] lstWordsSentence1 = m_datasets.get(library).get(0).get(i).replaceAll("\\p{Punct}", "").split(" ");
            String[] lstWordsSentence2 = m_datasets.get(library).get(1).get(i).replaceAll("\\p{Punct}", "").split(" ");

            // 1. We build the joint set of distinct CUI codes from S1 and S2 (dictionary)

            String[] dictionary = getCuisDictionary(lstWordsSentence1, lstWordsSentence2);

            // We obtain the matrxi with all combinations of two CUI concepts
       
            registerSentencePairs(dictionary, m_CachedCuiPairs);
        }
        
        // We return the result
        
        return (m_CachedCuiPairs);
    }
    
    /**
     * This function computes all CUI pairs
     * @param strAllCuiCodesInDataset
     * @return 
     */
    
    private void registerSentencePairs(
            String[]            strAllCuiCodesInSentencePair,
            HashSet<CuiPair>    allCuiPairs)
    {
        // We get hte number of distinct CUI codes
        
        int nCodes = strAllCuiCodesInSentencePair.length;
        
        // We fill the matrix
        
        for (int i = 0; i < nCodes; i++)
        {
            for (int j = 0; j < nCodes; j++)
            {
                allCuiPairs.add(new CuiPair(strAllCuiCodesInSentencePair[i],
                        strAllCuiCodesInSentencePair[j]));
            }
        }
    }
    
    /**
     * This function decomposes the sentece into tokens.
     * @param strRawSentence
     * @return 
     */
    
    private String[] getTokenDecomposition(
        String  strRawSentence)
    {
        return (strRawSentence.replaceAll("\\p{Punct}", "").split(" "));
    }
    
    /**
     * The method returns the similarity value between two sentences 
     * using the UBSM measure [1].
     * 
     * [1]G. Sogancioglu, H. Öztürk, A. Özgür,
     * BIOSSES: a semantic sentence similarity estimation system for
     * the biomedical domain, Bioinformatics. 33 (2017) i49–i58.
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return double similarity value
     * @throws IOException 
     */
    
    private double getUBSMsimilarityValue(
            String                      strRawSentence1, 
            String                      strRawSentence2,
            ISemanticLibrary    library) throws IOException,
                                                    InterruptedException, Exception
    {
        // We initialize the output score
        
        double similarity = Double.NEGATIVE_INFINITY;
        
        // Preprocess the sentences and get the tokens for each sentence
        
        String[] lstWordsSentence1 = strRawSentence1.replaceAll("\\p{Punct}", "").split(" ");
        String[] lstWordsSentence2 = strRawSentence2.replaceAll("\\p{Punct}", "").split(" ");
        
        // 1. We build the joint set of distinct CUI codes from S1 and S2 (dictionary)
                
        String[] dictionary = getCuisDictionary(lstWordsSentence1, lstWordsSentence2);
        
        // We need to discard thoese sentences without CUIs
        
        if (dictionary.length > 0)
        {
            // 2. We get the semantic vectors.

            double[] semanticVector1 = buildSemanticVector(library, dictionary, lstWordsSentence1);
            double[] semanticVector2 = buildSemanticVector(library, dictionary, lstWordsSentence2);

            // 2. Compute the cosine similarity between the semantic vectors

            similarity = computeCosineSimilarity(semanticVector1, semanticVector2);
        }
        
        // Return the similarity value
        
        return (similarity);
    }
    
    /**
     * This method compute the cosine similarity of the HashMap values.
     * 
     * @param sentence1Map
     * @param sentence2Map
     * @return 
     */
    
    private double computeCosineSimilarity(
            double[] sentence1Vector,
            double[] sentence2Vector)
    {
        // Initialize the result
        
        double similarity = 0.0;

        // We check the validity of the word vectors. They could be null if
        // any word is not contained in the vocabulary of the embedding.
        
        if ((sentence1Vector != null) && (sentence2Vector != null))
        {
            // We compute the cosine similarity function (dot product)
            
            for (int i = 0; i < sentence1Vector.length; i++)
            {
                similarity += sentence1Vector[i] * sentence2Vector[i];
            }
            
            // We divide by the vector norms
            
            similarity /= (getVectorNorm(sentence1Vector)
                        * getVectorNorm(sentence2Vector));
        }
        
        // Return the result
        
        return (similarity);
    }
    
    /**
     * This function computes the Euclidean norm of the input vector
     * @param vector
     * @return 
     */
    
    public static double getVectorNorm(
        double[]    vector)
    {
        double norm = 0.0;  // Returned value
        
        // We compute the acumulated square-coordinates
        
        for (int i = 0; i < vector.length; i++)
        {
            norm += vector[i] * vector[i];
        }
        
        // Finally, we compute the square root
        
        norm = Math.sqrt(norm);
        
        // We return the result
        
        return (norm);
    }
    
    /**
     * Each entry of the output binary-weighted vector is set to
     * 1 or zero depending if the input sentence words are included in the
     * dictionary.
     * @param lstWordsSentence1
     * @param lstWordsSentence2
     * @throws FileNotFoundException 
     */
    
    private double[] buildSemanticVector(
            ISemanticLibrary    similarityLibrary,
            String[]                    cuiDictionary,
            String[]                    lstWordsSentence) throws Exception 
    {
        // Initialize the semantic vector
        
        double[] semanticVector = new double[cuiDictionary.length];
        
        // Flag indicating if the library is UMLS::Similatity
        
        boolean isPedersenLibrary = (similarityLibrary.getLibraryType() == SemanticLibraryType.UMLS_SIMILARITY);
        
        // Convert arrays to set to reduce the number of operations 
        // (this method do not preserve the word order)
        
        Set<String> setWordsSentence1 = new HashSet<>(Arrays.asList(lstWordsSentence));

        // We compute the weight for each CUI position in the dictionary
        
        for (int i = 0; i < semanticVector.length; i++)
        {
            // We get the i-esim base word
            
            String  iCuiToken = cuiDictionary[i];
            
            // We initialize the similarity to the lowest value.
            // IMPORTANT NOTE: many similairty values are not nomralized, thus
            // their extremal values are not 0 and 1.
            
            semanticVector[i] = Double.NEGATIVE_INFINITY;
            
            // We look for the highest similarity score as regards all
            // words in the sentence.
            
            for (int j = 0; j < lstWordsSentence.length; j++)
            {
                // We get the next word in the sentence

                String word = lstWordsSentence[j];
                
                // We only consider the Cui words present in the sentence
                
                if (isCuiCode(word))
                {
                    // We compute the degree of similarity between both CUIs.
                    // Note that we used the cached values for the case of
                    // UMLS::Similarity library becasu this library takes
                    // much time to start.
                    
                    double wordSimilarity = !isPedersenLibrary ?
                                        similarityLibrary.getSimilarity(iCuiToken, word)
                                        : getCachedSimilarity(iCuiToken, word);
                    
                    if (wordSimilarity > semanticVector[i])
                    {
                        semanticVector[i] = wordSimilarity;
                    }
                }
            }
        } 
        
        // We release the word set
        
        setWordsSentence1.clear();
        
        // Return the result
        
        return (semanticVector);
    }
    
    /**
     * This fucntion returns the degree of similarity between two CUI concepts
     * using the pre-calculated values.
     * @param strFirstUmlsCUI
     * @param strSecondUmlsCUI
     * @return 
     */

    private double getCachedSimilarity(
            String  strFirstUmlsCUI,
            String  strSecondUmlsCUI) throws Exception
    {
        // We create a concept pair to retirve the result
        
        CuiPair pair = new CuiPair(strFirstUmlsCUI, strSecondUmlsCUI);
        
        // We retirve the similarity
        
        double similarity = m_CachedSimilarityValues.containsKey(pair) ? 
                            m_CachedSimilarityValues.get(pair) : 0.0;
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * Constructs the dictionary set with all the distinct words from both
     * input sentences.
     * @param lstWordsSentence1
     * @param lstWordsSentence2
     * @throws FileNotFoundException 
     */
    
    private String[] getCuisDictionary(
            String[] lstWordsSentence1, 
            String[] lstWordsSentence2) 
    {
        // Create a linked set with the ordered union of the two sentences
        
        HashSet<String> cuiDictionary = new HashSet<>();
        
        // We filter all CUI codes
        
        for (int i = 0; i < lstWordsSentence1.length; i++)
        {
            if (isCuiCode(lstWordsSentence1[i])) cuiDictionary.add(lstWordsSentence1[i]);
        }

        for (int i = 0; i < lstWordsSentence2.length; i++)
        {
            if (isCuiCode(lstWordsSentence2[i])) cuiDictionary.add(lstWordsSentence2[i]);
        }
        
        // Copy the linked set to the output vector
        
        String[] sentencePairDictionary = new String[cuiDictionary.size()];
        
        cuiDictionary.toArray(sentencePairDictionary);
        
        // We release the auxiliary set
        
        cuiDictionary.clear();
        
        // Return the result
        
        return (sentencePairDictionary);
    }
    
    /**
     * This function loads the dataset matrix with the sentence pairs for the evaluation.
     * @param strDatasetFilename 
     * @throws Exception 
     */
    
    private void loadDatasetBenchmark(
        HashMap<SemanticLibraryType,String>  strDatasetFilenames) throws Exception
    {
        System.out.println("Loading the sentence similarity datasets");
        
        // First, we initialize the hashmap m_datasets
        
        for(ISemanticLibrary lib : m_Libraries)
        {
            m_datasets.put(lib, new ArrayList<>());
        }
        
        // We iterate the libraries and load the dataset files per each library
        
        for (Map.Entry<ISemanticLibrary, ArrayList<ArrayList<String>>> entry : m_datasets.entrySet())
        {
            // We get the library and dataset path
            
            ISemanticLibrary library = entry.getKey();
            
            String strDatasetPath = strDatasetFilenames.get(library.getLibraryType());
            
            // Initialize the sentences
        
            ArrayList<String> first_sentences = new ArrayList<>();
            ArrayList<String> second_sentences = new ArrayList<>();
            
            // Read the benchmark CSV 
        
            BufferedReader csvReader = new BufferedReader(new FileReader(strDatasetPath));

            String strRowLine;

            while ((strRowLine = csvReader.readLine()) != null) 
            {
                // Split the line by tab

                String[] sentencePairs = strRowLine.split("\t");

                // Fill the matrix with the sentences

                first_sentences.add(sentencePairs[0]);
                second_sentences.add(sentencePairs[1]);
            }
            
            // We initialize the array with the pairs of sentences
            
            ArrayList<ArrayList<String>> pairs = new ArrayList<>();
            
            pairs.add(first_sentences);
            pairs.add(second_sentences);
            
            // We fill the first and second sentences per library
            
            m_datasets.put(library, pairs);

            // We close the file

            csvReader.close();
        }
    }
    
    /**
     * This function annotates all the sentences with CUI instances.
     */
    
    private void annotateDatasets() throws IOException, Exception
    {
        // Warning message
        
        System.out.println("Annotating the dataset with Metamap Lite...");
        
        // We annotate ecach sentence of the datasets
        
        for (Map.Entry<ISemanticLibrary, ArrayList<ArrayList<String>>> entry : m_datasets.entrySet()) 
        {
            // We get the library and dataset path
            
            ISemanticLibrary library = entry.getKey();
            ArrayList<ArrayList<String>> sentences = entry.getValue();
            
            // We create an auxiliary array with the sentence pairs
            
            ArrayList<ArrayList<String>> sentencesAnnotated = new ArrayList<>();
            
            // We create two auxiliary arrays 
            
            ArrayList<String> firstSentencesAnnotated = new ArrayList<>();
            ArrayList<String> secondSentencesAnnotated = new ArrayList<>();
            
            // We annotate all the sentences
            
            for (int i = 0; i < sentences.get(0).size(); i++) 
            { 		      
                // Annotate the sentences and add to the array
                
                firstSentencesAnnotated.add(annotateSentence(sentences.get(0).get(i)));
                secondSentencesAnnotated.add(annotateSentence(sentences.get(1).get(i)));
            } 
            
            // Add the pairs of sentences to the array
            
            sentencesAnnotated.add(firstSentencesAnnotated);
            sentencesAnnotated.add(secondSentencesAnnotated);
            
            m_datasets.put(library, sentencesAnnotated);
        }
    }
    
    /**
     * This function annotates a sentence with CUI codes replacing 
     * keywords with codes in the same sentence.
     * @return 
     */
    
    private String annotateSentence(
            String sentence) throws InvocationTargetException, IOException, Exception
    {
        // Initialize the result
        
        String annotatedSentence = sentence;
        
        // Processing Section

        // Each document must be instantiated as a BioC document before processing
        
        // The sentence should have almost an alphanumeric character
        
        if(sentence.matches("^.*[a-zA-Z0-9]+.*$"))
        {
            BioCDocument document = FreeText.instantiateBioCDocument(sentence);
        
            // Proccess the document with Metamap

            List<Entity> entityList = m_metaMapLiteInst.processDocument(document);

            // For each keyphrase, select the first CUI candidate and replace in text.

            for (Entity entity: entityList) 
            {
                for (Ev ev: entity.getEvSet()) 
                {
                    // Replace in text

                    annotatedSentence = annotatedSentence.replaceAll(entity.getMatchedText(), ev.getConceptInfo().getCUI());
                }
            }
        }
        
        // Return the result
        
        return (annotatedSentence);
    }
    
    /**
     * This function loads the Metamap Lite instance before executing the queries.
     */
    
    private void loadMetamapLite() throws ClassNotFoundException, 
                                    InstantiationException, NoSuchMethodException, 
                                    IllegalAccessException, IOException
    {
        // Warning message
        
        System.out.println("Loading the Metamap Lite Instance...");

        // Initialization Section
        
        Properties myProperties = new Properties();
        
        // Select the 2018AB database
        
        myProperties.setProperty("metamaplite.index.directory", "../public_mm_lite/data/ivf/2018ABascii/USAbase/");
        myProperties.setProperty("opennlp.models.directory", "../public_mm_lite/data/models/");
        myProperties.setProperty("opennlp.en-pos.bin.path", "../public_mm_lite/data/models/en-pos-maxent.bin");
        myProperties.setProperty("opennlp.en-sent.bin.path", "../public_mm_lite/data/models/en-sent.bin");
        myProperties.setProperty("opennlp.en-token.bin.path", "../public_mm_lite/data/models/en-token.bin");
        
        myProperties.setProperty("metamaplite.sourceset", "MSH");

        // We create the METAMAP maname
        
        m_metaMapLiteInst = new MetaMapLite(myProperties);
    }
    
    /**
     * Filter if a String is or not a CUI code
     */
    
    private boolean isCuiCode(String word)
    {
        return (word.matches("C\\d\\d\\d\\d\\d\\d\\d"));
    }
}