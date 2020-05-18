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
import hesml_umls_benchmark.ISnomedSimilarityLibrary;
import hesml_umls_benchmark.SnomedBasedLibraryType;
import hesml_umls_benchmark.LibraryType;
import hesml_umls_benchmark.snomedlibraries.UMLSSimilarityLibrary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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

class SentencesEvalBenchmark extends UMLSLibBenchmark
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
    private final LibraryType               m_vocabulary;

    /**
     * Path to the input dataset for evaluating sentences
     */
    
    protected String m_strDatasetPath;
    protected String[][] m_dataset;
    
    
    /**
     * Metamap Lite instance
     */
    
    protected MetaMapLite m_metaMapLiteInst;
    
    /**
     * Precalculated similarities for Pedersen Library
     */
    
    ArrayList<String> m_preCalculatedSimilarities;
    Iterator<String> m_preCalculatedSimilaritiesIterator;

    /**
     * Constructor of the random concept pairs benchmark
     * @param libraries
     * @param similarityMeasure
     * @param icModel
     * @param strDatasetPath
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @throws Exception 
     */

    SentencesEvalBenchmark(
            SnomedBasedLibraryType[]    libraries,
            LibraryType                 vocabulary,
            SimilarityMeasureType       similarityMeasure,
            IntrinsicICModelType        icModel,
            String                      strDatasetPath,
            String                      strSnomedDir,
            String                      strSnomedDBconceptFileName,
            String                      strSnomedDBRelationshipsFileName,
            String                      strSnomedDBdescriptionFileName,
            String                      strUmlsDir,
            String                      strSNOMED_CUI_mappingfilename) throws Exception
    {
        // We initialize the base class
        
        super(libraries, strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename);    
        
        // We initialize the attributes of the object
        
        m_MeasureType = similarityMeasure;
        m_icModel = icModel;
        m_vocabulary = vocabulary;
        m_strDatasetPath = strDatasetPath;
        m_dataset = null;
        m_preCalculatedSimilarities = new ArrayList<>();
        m_preCalculatedSimilaritiesIterator = null;
        
        System.out.println("Loading the sentence similarity dataset from path: " + m_strDatasetPath);
        this.loadDatasetBenchmark();
        
        System.out.println("Loading the Metamap Lite Instance...");
        this.loadMetamapLite();
        
        System.out.println("Annotating the dataset with Metamap Lite...");
        this.annotateDataset();
    }
    
    /**
     * This function executes the benchmark and saves the raw results into
     * the output file.
     */
    
    @Override
    public void run(String strOutputFilename) throws Exception
    {
        // We create the output data matrix and fill the row headers
        
        String[][] strOutputDataMatrix = new String[m_dataset.length + 1][m_Libraries.length + 1];
        
        // We fill the first row header
        
        strOutputDataMatrix[0][0] = "#run";
        
        // We evaluate the performance of the HESML library
        
        for (int iLib = 0; iLib < m_Libraries.length; iLib++)
        {
            // Debugginf message
            
            System.out.println("---------------------------------");
            System.out.println("\t" + m_Libraries[iLib].getLibraryType().toString()
                    + " library: evaluating the similarity from sentence pairs.");
            
            // We set the row header
            
            strOutputDataMatrix[0][iLib + 1] = m_Libraries[iLib].getLibraryType().toString()
                                        + "-" + m_MeasureType.toString();
            
            // We load SNOMED and the resources of the library
            
            m_Libraries[iLib].loadSnomed();
            
            // We set the similarity measure to be used
            
            m_Libraries[iLib].setSimilarityMeasure(m_icModel, m_MeasureType);
            
            // We evaluate the library
            
            CopyRunningTimesToMatrix(strOutputDataMatrix,
                EvaluateLibrary(m_Libraries[iLib]), iLib + 1);
            
            // We release the database and resources used by the library
            
            m_Libraries[iLib].unloadSnomed();
        }
        
        // We write the output raw data
        
        WriteCSVfile(strOutputDataMatrix, strOutputFilename);
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
            ISnomedSimilarityLibrary    library) throws Exception
    {
        // We initialize the output vector
        
        double[] runningTimes = new double[m_dataset.length];
        double accumulatedTime = 0.0;
        
        // We initialize the calculation time for Perl script
            
        double accumulatedTimePerl = 0.0;
        double totalPerlCalculations = 0.0;
        
        // UMLS_SIMILARITY library gets all the iterations at one time
        // The rest of the libraries execute the benchmark n times
        
        if (library.getLibraryType() == SnomedBasedLibraryType.UMLS_SIMILARITY)
        {
            // We make a casting to the UMLS::Similarity library
            
            UMLSSimilarityLibrary pedersenLib = (UMLSSimilarityLibrary) library;
            
            // We extract the candidates for concept-pair similarity calculations and execute the Perl script

            this.precalculateWithPerl(pedersenLib);
            
            // We execute multiple times the benchmark to calculate all the sentences

            for (int iRun = 0; iRun < m_dataset.length; iRun++)
            {
                // We initializa the stopwatch

                long startTime = System.currentTimeMillis();

                // We evaluate the random concept pairs

                double similarity = getSimilarityValuesWithPedersen(m_dataset[iRun][0], m_dataset[iRun][1]);
                
                // We compute the elapsed time in seconds

                runningTimes[iRun] = (System.currentTimeMillis() - startTime) / 1000.0;
                
                accumulatedTime += runningTimes[iRun];
            }
            
            // We calculate the average running time for precalculate Pedersen similarities
            
            // Calculate the accumulated time for each iteration
                
            for(String cuisSimilarityAndRunningTime : m_preCalculatedSimilarities)
            {
                String[] cuisSimilarityAndRunningTimeList = cuisSimilarityAndRunningTime.split(",");
                
                accumulatedTimePerl += Double.valueOf(cuisSimilarityAndRunningTimeList[3]);
            }

            totalPerlCalculations = m_preCalculatedSimilarities.size();
        }
        else
        {
            // We execute multiple times the benchmark to compute a stable running time

            for (int iRun = 0; iRun < m_dataset.length; iRun++)
            {
                // We initializa the stopwatch

                long startTime = System.currentTimeMillis();

                // We evaluate the random concept pairs

                for (int i = 0; i < m_dataset.length; i++)
                {
                    double similarity = getSimilarityValue(m_dataset[i][0], m_dataset[i][1], library);
                }

                // We compute the elapsed time in seconds

                runningTimes[iRun] = (System.currentTimeMillis() - startTime) / 1000.0;

                accumulatedTime += runningTimes[iRun];
            }
        }
        
        // We compute the averga running time
        
        double averageRuntime = accumulatedTime / m_dataset.length;
        
        // We print the average results
        
        System.out.println("# UMLS sentence pairs evaluated = " + m_dataset.length);
        
        System.out.println(library.getLibraryType() + " Average time for Java script (secs) = "
                + averageRuntime);
        
        // The UMLS similarity library measure the performance in two steps
        // (a) the running time in HESML for iterating one time the dataset and getting the results from a preloaded arraylist
        // (b) the running time in the Perl model for calculating the similarity between each pair of concepts.
        
        if (library.getLibraryType() == SnomedBasedLibraryType.UMLS_SIMILARITY)
        {
            double averageRuntimePerlPerConceptPair = accumulatedTimePerl / totalPerlCalculations;
            double averageRuntimePerlPerSentencePair = accumulatedTimePerl / m_dataset.length;
            
            System.out.println(library.getLibraryType() + " Total accumulated time for Perl script (secs) = "
                    + accumulatedTimePerl);
            
            System.out.println(library.getLibraryType() + " Average time for Perl script per concept pair (secs) = "
                    + averageRuntimePerlPerConceptPair);
            
            System.out.println(library.getLibraryType() + " Average time for Perl script values per sentence pair (secs) = "
                    + averageRuntimePerlPerSentencePair);

            System.out.println(library.getLibraryType() + " Average evaluation speed (#evaluation/second) = "
                + ((double)m_dataset.length) / (averageRuntime + averageRuntimePerlPerSentencePair));
        }
        else
        {
            System.out.println(library.getLibraryType() + " Average evaluation speed (#evaluation/second) = "
                + ((double)m_dataset.length) / averageRuntime);
        }
        
        // We return the results
        
        return (runningTimes);
    }
    
    /**
     * The method returns the similarity value between two sentences 
     * using the WBSM measure.
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return double similarity value
     * @throws IOException 
     */
    
    public double getSimilarityValuesWithPedersen(
            String                      strRawSentence1, 
            String                      strRawSentence2) 
            throws IOException, 
            InterruptedException, Exception
    {
        // We initialize the output score
        
        double similarity = 0.0;
        
        // We initialize the semantic vectors
        
        double[] semanticVector1 = null;
        double[] semanticVector2 = null;
        
        // We initialize the dictionary vector
        
        ArrayList<String> dictionary = null;
        
        // Preprocess the sentences and get the tokens for each sentence
        
        String[] lstWordsSentence1 = strRawSentence1.replaceAll("\\p{Punct}", "").split(" ");
        String[] lstWordsSentence2 = strRawSentence2.replaceAll("\\p{Punct}", "").split(" ");
        
        // 1. Construct the joint set of distinct words from S1 and S2 (dictionary)
                
        dictionary = constructDictionaryList(lstWordsSentence1, lstWordsSentence2);
        
        // 2. Initialize the semantic vectors.
        
        semanticVector1 = constructSemanticVector(dictionary, lstWordsSentence1);
        semanticVector2 = constructSemanticVector(dictionary, lstWordsSentence2);
        
        // 3. Use WordNet to construct the semantic vector
        
        semanticVector1 = computeSemanticVectorForPedersen(semanticVector1, dictionary);
        semanticVector1 = computeSemanticVectorForPedersen(semanticVector2, dictionary);
        
        // 4. Compute the cosine similarity between the semantic vectors
        
        similarity = computeCosineSimilarity(semanticVector1, semanticVector2);

        // Return the similarity value
        
        return (similarity);
    }
    
    
    /**
     * This function gets all the future calculations for the Perl script.
     * 
     * * The order of the calculations will be the same after executing Perl script.
     */
    
    private void precalculateWithPerl(
                UMLSSimilarityLibrary pedersenLib) throws Exception
    {
        // Initialize the result
        
        ArrayList<String> cuiCodePairsCalculations = new ArrayList<>();
        
        // We execute iterate the dataset and get all the future similarity calculations in a vector.

        for (int i = 0; i < m_dataset.length; i++)
        {
            // We initialize the semantic vectors

            double[] semanticVector1 = null;
            double[] semanticVector2 = null;
            
            // We get the raw sentences
            
            String strRawSentence1 = m_dataset[i][0];
            String strRawSentence2 = m_dataset[i][1];

            // We initialize the dictionary vector

            ArrayList<String> dictionary = null;

            // Preprocess the sentences and get the tokens for each sentence

            String[] lstWordsSentence1 = strRawSentence1.replaceAll("\\p{Punct}", "").split(" ");
            String[] lstWordsSentence2 = strRawSentence2.replaceAll("\\p{Punct}", "").split(" ");

            // 1. Construct the joint set of distinct words from S1 and S2 (dictionary)

            dictionary = constructDictionaryList(lstWordsSentence1, lstWordsSentence2);

            // 2. Initialize the semantic vectors.

            semanticVector1 = constructSemanticVector(dictionary, lstWordsSentence1);
            semanticVector2 = constructSemanticVector(dictionary, lstWordsSentence2);
            
            // 3. Extract all the future calculations and add them to the list.
            
            // Merge both lists into the final list
            
            cuiCodePairsCalculations.addAll(addCandidatesForCalculateWordSimilarity(semanticVector1, dictionary));
            cuiCodePairsCalculations.addAll(addCandidatesForCalculateWordSimilarity(semanticVector2, dictionary));                 
        }
        
        // Iterate the arraylist and create the matrix
        
        String[][] cuiPairList = new String[cuiCodePairsCalculations.size()][2];
                
        for(int i=0; i<cuiCodePairsCalculations.size(); i++)
        {
            String[] cuiPair = cuiCodePairsCalculations.get(i).split(",");
            
            cuiPairList[i][0] = cuiPair[0];
            cuiPairList[i][1] = cuiPair[1];
        }
        
        // Execute the Perl script
        
        m_preCalculatedSimilarities = pedersenLib.getCUIsSimilaritiesAndRunningTimes(cuiPairList, LibraryType.MSH);
        
        // Initialize the iterator with the data
        
        m_preCalculatedSimilaritiesIterator = m_preCalculatedSimilarities.iterator();
    }
    
    /**
     * This function add all the possible candidates for calculate word similarity 
     * before execute the Perl script from a semantic vector
     * 
     * For each vector position, check if the value is zero.
     * 
     * @param semanticVector
     * @return 
     */
    
    private ArrayList<String> addCandidatesForCalculateWordSimilarity(
            double[]                    semanticVector,
            ArrayList<String>           dictionary) throws Exception
    {
        // Initialize the result
        
        ArrayList<String> listCandidates = new ArrayList<>();
        
        // Compute the semantic vector value in each position
        
        for (int i = 0; i < semanticVector.length; i++)
        {
            if((semanticVector[i] != 1.0) && (isCuiCode(dictionary.get(i)) == true))
            {
                for (Iterator<String> it = dictionary.iterator(); it.hasNext();) {
                    String wordDict = it.next();
                    // If it is a CUI code, add to the list
                   
                    if(isCuiCode(wordDict) == true)
                    {
                        listCandidates.add(dictionary.get(i) + "," + wordDict);
                    }
                }
            }
        }

        // Return the result
      
        return (listCandidates);
    }
    
    /**
     * The method returns the similarity value between two sentences 
     * using the WBSM measure.
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return double similarity value
     * @throws IOException 
     */
    
    public double getSimilarityValue(
            String                      strRawSentence1, 
            String                      strRawSentence2,
            ISnomedSimilarityLibrary    library) 
            throws IOException, 
            InterruptedException, Exception
    {
        // We initialize the output score
        
        double similarity = 0.0;
        
        // We initialize the semantic vectors
        
        double[] semanticVector1 = null;
        double[] semanticVector2 = null;
        
        // We initialize the dictionary vector
        
        ArrayList<String> dictionary = null;
        
        // Preprocess the sentences and get the tokens for each sentence
        
        String[] lstWordsSentence1 = strRawSentence1.replaceAll("\\p{Punct}", "").split(" ");
        String[] lstWordsSentence2 = strRawSentence2.replaceAll("\\p{Punct}", "").split(" ");
        
        // 1. Construct the joint set of distinct words from S1 and S2 (dictionary)
                
        dictionary = constructDictionaryList(lstWordsSentence1, lstWordsSentence2);
        
        // 2. Initialize the semantic vectors.
        
        semanticVector1 = constructSemanticVector(dictionary, lstWordsSentence1);
        semanticVector2 = constructSemanticVector(dictionary, lstWordsSentence2);

        // 3. Use WordNet to construct the semantic vector
        
        semanticVector1 = computeSemanticVector(semanticVector1, dictionary, library);
        semanticVector2 = computeSemanticVector(semanticVector2, dictionary, library);
        
        // 4. Compute the cosine similarity between the semantic vectors
        
        similarity = computeCosineSimilarity(semanticVector1, semanticVector2);
        
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
     * This method initializes the semantic vectors.
     * Each vector has the words from the dictionary vector.
     * If the word exists in the sentence of the semantic vector, the value is 1.
     * 
     * @param lstWordsSentence1
     * @param lstWordsSentence2
     * @throws FileNotFoundException 
     */
    
    private double[] constructSemanticVector(
            ArrayList<String>   dictionary,
            String[]            lstWordsSentence) 
    {
        // Initialize the semantic vector
        
        double[] semanticVector = new double[dictionary.size()];
        
        // Convert arrays to set to facilitate the operations 
        // (this method do not preserve the word order)
        
        Set<String> setWordsSentence1 = new HashSet<>(Arrays.asList(lstWordsSentence));

        // For each list of words of a sentence
        // If the value is in the sentence, the value of the semantic vector will be 1.
        
        int count = 0;
        for (String word : dictionary)
        {
            // We check if the first sentence contains the word
            
            double wordVectorComponent = setWordsSentence1.contains(word) ? 1.0 : 0.0;

            semanticVector[count] = wordVectorComponent;
            count++;
        } 
        
        // Return the result
        
        return (semanticVector);
    }
    
    /**
     * Constructs the dictionary set with all the distinct words from the sentences.
     * 
     * @param lstWordsSentence1
     * @param lstWordsSentence2
     * @throws FileNotFoundException 
     */
    
    private ArrayList<String> constructDictionaryList(
            String[] lstWordsSentence1, 
            String[] lstWordsSentence2) 
    {
        // Initialize the set
        
        ArrayList<String> dictionary = null;
        
        // Create a linked set with the ordered union of the two sentences
        
        Set<String> setOrderedWords = new LinkedHashSet<>();
        setOrderedWords.addAll(Arrays.asList(lstWordsSentence1));
        setOrderedWords.addAll(Arrays.asList(lstWordsSentence2));
        
        // Copy the linked set to the arraylist
        
        dictionary = new ArrayList<>(setOrderedWords);
        
        // Return the result
        
        return (dictionary);
    }
    
    /**
     * Compute the values from the semantic vector in the positions with zeros.
     * 
     * For each vector position, check if the value is zero.
     * If the value is zero, compute the word similarity with the dictionary
     * using word similarity measures and get the maximum value.
     * 
     * @param semanticVector
     * @return 
     */
    
    private double[] computeSemanticVectorForPedersen(
            double[]                    semanticVector,
            ArrayList<String>           dictionary) throws Exception
    {
        // Initialize the result m_preCalculatedSimilarities
        
        double[] semanticVectorComputed = new double[semanticVector.length];
        
        // Compute the semantic vector value in each position
        
        for (int i = 0; i < semanticVector.length; i++)
        {
            String word = dictionary.get(i);
            
            if((semanticVector[i] != 1.0) && (isCuiCode(dictionary.get(i)) == true))
            {
                double maxValue = 0.0;
                
                for (String wordDict : dictionary)
                {
                    // If it is a CUI code, add to the list

                    if(isCuiCode(wordDict) == true)
                    {   
                        String data = m_preCalculatedSimilaritiesIterator.next();
                        
                        String[] calculationResult = data.split(",");
                        
                        // Get the similarity between the words
                        
                        double similarityScore = Double.valueOf(calculationResult[2]);

                        // If the returned value is greater, set the new similarity value

                        maxValue = maxValue < similarityScore ? similarityScore : maxValue;
                    }
                }
                
                semanticVectorComputed[i] = maxValue;
            }
            else
            {
                semanticVectorComputed[i] = semanticVector[i];
            }
        }

        // Return the result
        
        return (semanticVectorComputed);
    }
    
    /**
     * Compute the values from the semantic vector in the positions with zeros.
     * 
     * For each vector position, check if the value is zero.
     * If the value is zero, compute the word similarity with the dictionary
     * using word similarity measures and get the maximum value.
     * 
     * @param semanticVector
     * @return 
     */
    
    private double[] computeSemanticVector(
            double[]                    semanticVector,
            ArrayList<String>           dictionary,
            ISnomedSimilarityLibrary    library) throws Exception
    {
        // Initialize the result
        
        double[] semanticVectorComputed = new double[semanticVector.length];
        
        
        // Compute the semantic vector value in each position
        
        for (int i = 0; i < semanticVector.length; i++)
        {
            // If the value is zero, get the word similarity
            
            double wordVectorComponent = semanticVector[i] == 1.0 ? 1.0 : 
                        getWordSimilarityScore(dictionary.get(i), dictionary, library);
  
            semanticVectorComputed[i] = wordVectorComponent;
        }

        // Return the result
        
        return (semanticVectorComputed);
    }
    
    /**
     * Get the maximum similarity value comparing a word with a list of words.
     * 
     * @param word
     * @param dictionary
     * @return double
     */
    
    private double getWordSimilarityScore(
            String                      word,
            ArrayList<String>           dictionary,
            ISnomedSimilarityLibrary    library) throws Exception
    {
        // Initialize the result
        
        double maxValue = 0.0;
        
        // Iterate the dictionary and compare the similarity 
        // between the pivot word and the dictionary words to get the maximum value.
        
        for (String wordDict : dictionary)
        {
            // Get the similarity between the words
            double similarityScore = library.getSimilarity(word, wordDict);
            
            // If the returned value is greater, set the new similarity value
            
            maxValue = maxValue < similarityScore ? similarityScore : maxValue;
        }
        
        // Return the result
        
        return (maxValue);
    }
    
    /**
     * This function loads the dataset matrix with the sentence pairs for the evaluation.
     * 
     * @throws Exception 
     */
    
    private void loadDatasetBenchmark() throws Exception
    {
        // Chech if file exists
        
        File csvFile = new File(m_strDatasetPath);
        if (!csvFile.isFile()) {
            throw new Exception("Error: Semantic sentence similarity dataset not found at path: " 
                    + m_strDatasetPath);
        }
        
        // Read the file and fills the matrix with the sentences
        
        String row;
        
        // Initialize the sentences
        
        ArrayList<String> first_sentences = new ArrayList<>();
        ArrayList<String> second_sentences = new ArrayList<>();
        
        // Read the benchmark CSV 
        
        BufferedReader csvReader = new BufferedReader(new FileReader(m_strDatasetPath));
        while ((row = csvReader.readLine()) != null) 
        {
            // Split the line by tab
            
            String[] sentencePairs = row.split("\t");
            
            // Fill the matrix with the sentences
            
            first_sentences.add(sentencePairs[0]);
            second_sentences.add(sentencePairs[1]);
        }
        csvReader.close();
        
        // Fill the dataset data
        
        m_dataset = new String[first_sentences.size()][2];
        
        for (int i = 0; i < first_sentences.size(); i++)
        {
            m_dataset[i][0] = first_sentences.get(i);
            m_dataset[i][1] = second_sentences.get(i);
        }
    }
    
    /**
     * This function iterates the dataset and annotates all the sentences.
     */
    
    private void annotateDataset() throws IOException, Exception
    {
        //Initialize the auxiliar dataset
        
        String[][] dataset = m_dataset;
        
        // Iterate the dataset
        
        for (int i = 0; i < m_dataset.length; i++)
        {
            // Select the sentences
            
            String first_sentence = m_dataset[i][0];
            String second_sentence = m_dataset[i][1];
            
            // Annotate the sentences
            
            String annotated_first_sentence = this.annotateSentence(first_sentence);
            String annotated_second_sentence = this.annotateSentence(second_sentence);
            
            // And assign the annotated sentences to the auxiliary matrix
            
            dataset[i][0] = annotated_first_sentence;
            dataset[i][1] = annotated_second_sentence;
        }
        
        // Replace the auxiliar dataset
        
        m_dataset = dataset;
    }
    
    /**
     * This function annotates a sentence with CUI codes replacing 
     * keywords with codes in the same sentence.
     * 
     * 
     * @return 
     */
    
    private String annotateSentence(
            String sentence) throws InvocationTargetException, IOException, Exception
    {
        // Initialize the result
        
        String annotatedSentence = sentence;
        
        // Processing Section

        // Each document must be instantiated as a BioC document before processing
        
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
        
        // Return the result
        
        return annotatedSentence;
    }
    
    /**
     * This function loads the Metamap Lite instance before executing the queries.
     */
    
    private void loadMetamapLite() throws ClassNotFoundException, 
                                    InstantiationException, NoSuchMethodException, 
                                    IllegalAccessException, IOException
    {
        // Initialization Section
        
        Properties myProperties = new Properties();
        
        // Select the 2018AB database
        
        myProperties.setProperty("metamaplite.index.directory", "../HESML_UMLS_benchmark/public_mm_lite/data/ivf/2018ABascii/USAbase/");
        myProperties.setProperty("opennlp.models.directory", "../HESML_UMLS_benchmark/public_mm_lite/data/models/");
        myProperties.setProperty("opennlp.en-pos.bin.path", "../HESML_UMLS_benchmark/public_mm_lite/data/models/en-pos-maxent.bin");
        myProperties.setProperty("opennlp.en-sent.bin.path", "../HESML_UMLS_benchmark/public_mm_lite/data/models/en-sent.bin");
        myProperties.setProperty("opennlp.en-token.bin.path", "../HESML_UMLS_benchmark/public_mm_lite/data/models/en-token.bin");
        
        myProperties.setProperty("metamaplite.sourceset", "MSH");

        m_metaMapLiteInst = new MetaMapLite(myProperties);
    }
    
    /**
     * Filter if a String is or not a CUI code
     */
    
    private boolean isCuiCode(String word)
    {
        //Initialize the result
        
        boolean isCui = false;
        
        
        if(word.matches("C\\d\\d\\d\\d\\d\\d\\d"))
        {
            isCui = true;
        }
        
        // Return the result
        return (isCui);
    }
}
