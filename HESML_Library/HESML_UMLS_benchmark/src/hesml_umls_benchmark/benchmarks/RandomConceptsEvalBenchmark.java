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

import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml_umls_benchmark.UMLSOntologyType;
import hesml_umls_benchmark.SemanticLibraryType;
import hesml_umls_benchmark.semantclibrarywrappers.SimilarityLibraryWrapper;
import hesml_umls_benchmark.semantclibrarywrappers.UMLSSemanticLibraryWrapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import hesml_umls_benchmark.ISemanticLibrary;
import javax.xml.stream.XMLStreamException;

/**
 * This class implements a benchmark to compare the performance
 * of the UMLS similarity libraries in the evaluation of the degree
 * of similairty between randdom concept pairs in SNOMED-CT.
 * @author j.lastra
 */

class RandomConceptsEvalBenchmark extends SemanticLibraryBenchmark
{
    /**
     * Column offset for the main attributes extratec from concept and
     * relationsship files.
     */
    
    private static final int CONCEPT_ID = 0;
    private static final int ACTIVE_ID = 2;
    
    /**
     * Setup parameters of the semantic similarity measure
     */
    
    private SimilarityMeasureType   m_MeasureType;
    private IntrinsicICModelType    m_icModel;

    /**
     * Number of random concept pairs and runs for averaging the time
     */
    
    protected int[] m_nRandomSamplesPerLibrary;
    protected int   m_nRuns;
    
    /**
     * Ontology type being evaluated
     */
    
    private UMLSOntologyType  m_ontologyType;

    /**
     * Constructor of the random concept pairs benchmark
     * @param libraries
     * @param ontology
     * @param similarityMeasure
     * @param icModel
     * @param nRandomSamplesPerLibrary
     * @param nRuns
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @throws Exception 
     */

    RandomConceptsEvalBenchmark(
            SemanticLibraryType[]    libraries,
            UMLSOntologyType      ontology,
            SimilarityMeasureType       similarityMeasure,
            IntrinsicICModelType        icModel,
            int[]                       nRandomSamplesPerLibrary,
            int                         nRuns,
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
        m_ontologyType = ontology;
        m_nRandomSamplesPerLibrary = nRandomSamplesPerLibrary;
        m_nRuns = nRuns;
    }
    
    /**
     * Constructor of the random concept pairs benchmark
     * @param libraries
     * @param ontology
     * @param similarityMeasure
     * @param icModel
     * @param nRandomSamplesPerLibrary
     * @param nRuns
     * @param strMeSHDir
     * @param strMeSHXmlFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @throws Exception 
     */

    RandomConceptsEvalBenchmark(
            SemanticLibraryType[]   libraries,
            UMLSOntologyType  ontology,
            SimilarityMeasureType   similarityMeasure,
            IntrinsicICModelType    icModel,
            int[]                   nRandomSamplesPerLibrary,
            int                     nRuns,
            String                  strMeSHDir,
            String                  strMeSHXmlFileName,
            String                  strUmlsDir,
            String                  strSNOMED_CUI_mappingfilename) throws Exception
    {
        // We initialize the base class
        
        super(libraries, strMeSHDir, strMeSHXmlFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename);    
        
        // We initialize the attributes of the object
        
        m_MeasureType = similarityMeasure;
        m_icModel = icModel;
        m_ontologyType = ontology;
        m_nRandomSamplesPerLibrary = nRandomSamplesPerLibrary;
        m_nRuns = nRuns;
    }
    
    /**
     * This function executes the benchmark and saves the raw results into
     * the output file.
     */
    
    @Override
    public void run(String strOutputFilename) throws Exception
    {
        // We create the output data matrix and fill the row headers
        
        String[][] strOutputDataMatrix = buildOutputDataMatrix();
        
        // We evaluate the performance of the HESML library
        
        for (int iLib = 0, j = 1; iLib < m_Libraries.length; iLib++, j += 2)
        {
            // Debugginf message
            
            System.out.println("---------------------------------");
            System.out.println("\t" + m_Libraries[iLib].getLibraryType().toString()
                    + " library: evaluating the similarity between "
                    + m_nRandomSamplesPerLibrary[iLib]
                    + " random concept pairs in " + m_nRuns + " runs");
            
            // We get the collection of random UMLS CUI pairs

            String[][] cuiPairs = getRandonCUIpairs(m_nRandomSamplesPerLibrary[iLib]);
            
            // We load SNOMED and the resources of the library
            
            m_Libraries[iLib].loadOntology();
            
            // We evaluate the library
            
            double[] runningTimes = EvaluateLibrary(m_Libraries[iLib], cuiPairs, m_nRuns);
            
            // We copy the results to the raw output matrix
            
            for (int iRun = 0; iRun < runningTimes.length; iRun++)
            {
                strOutputDataMatrix[iRun + 1][0] = Integer.toString(iRun + 1);
                strOutputDataMatrix[iRun + 1][j] = Integer.toString(m_nRandomSamplesPerLibrary[iLib]);
                strOutputDataMatrix[iRun + 1][j + 1] = Double.toString(runningTimes[iRun]);
            }
            
            // We release the database and resources used by the library
            
            m_Libraries[iLib].unloadOntology();
        }
        
        // We write the output raw data
        
        WriteCSVfile(strOutputDataMatrix, strOutputFilename);
    }
    
    /**
     * This function builds the raw output matrix and fills the row headers.
     * @return 
     */
    
    private String[][] buildOutputDataMatrix()
    {
        // We create the output data matrix and fill the row headers
        
        String[][] strOutputDataMatrix = new String[m_nRuns + 1][2 * m_Libraries.length + 1];
        
        // We fill the row headers
        
        strOutputDataMatrix[0][0] = "#run";
        
        for (int i = 0, j = 1; i < m_Libraries.length; i++, j += 2)
        {
            strOutputDataMatrix[0][j] = "#pairs";
            strOutputDataMatrix[0][j + 1] = m_Libraries[i].getLibraryType().toString()
                                            + "-" + m_MeasureType.toString();
        }
        // We reurn the result
        
        return (strOutputDataMatrix);
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
            String[][]          umlsCuiPairs,
            int                 nRuns) throws Exception
    {
        // We initialize the output vector
        
        double[] runningTimes = new double[nRuns];
        
        // We set the similarity measure to be used. SML library does not provide
        // practical running times for te Rada measure, thus we detect this case
        // and skip its evaluation.

        library.setSimilarityMeasure(m_icModel, m_MeasureType);
        
        if (nRuns == 0)
        {
            for (int i = 0; i < runningTimes.length; i++)
            {
                runningTimes[i] = Double.NaN;
            }
        }
        else
        {
            // We initializa the time counter
            
            double accumulatedTime = 0.0;
            
            // UMLS_SIMILARITY library gets all the iterations at one time
            // The rest of the libraries execute the benchmark n times

            if (library.getLibraryType() == SemanticLibraryType.UMLS_SIMILARITY)
            {
                // We make a casting to the UMLS::Similarity library

                UMLSSemanticLibraryWrapper pedersenLib = (UMLSSemanticLibraryWrapper) library;

                // We evaluate the similarity of a list of pairs of concepts at once.
                // The function also returns the running times for each run
                // similarityWithRunningTimes[similarity_i][runningTime_ị]

                double[][] similarityWithRunningTimes = pedersenLib.getSimilaritiesAndRunningTimes(
                                                            umlsCuiPairs, m_ontologyType);

                // Calculate the accumulated time for each iteration

                for (int i = 0; i < similarityWithRunningTimes.length; i++)
                {
                    accumulatedTime += similarityWithRunningTimes[i][1];
                }
            }
            else
            {
                // We exucte multiple times the benchmark to compute a stable running time

                for (int iRun = 0; iRun < nRuns; iRun++)
                {
                    // We initializa the stopwatch

                    long startTime = System.currentTimeMillis();

                    // We evaluate the random concept pairs

                    for (int i = 0; i < umlsCuiPairs.length; i++)
                    {
                        library.getSimilarity(umlsCuiPairs[i][0], umlsCuiPairs[i][1]);
                    }

                    // We compute the elapsed time in seconds

                    runningTimes[iRun] = (System.currentTimeMillis() - startTime) / 1000.0;

                    accumulatedTime += runningTimes[iRun];
                }
            }
        
            // We compute the averga running time

            double averageRuntime = accumulatedTime / nRuns;

            // We print the average results

            System.out.println("# UMLS concept pairs evaluated = " + umlsCuiPairs.length);
            System.out.println(library.getLibraryType() + " Average time (secs) = "
                    + averageRuntime);

            System.out.println(library.getLibraryType()
                    + " Average evaluation speed (#evaluation/second) = "
                    + ((double)umlsCuiPairs.length) / averageRuntime);
            
            System.out.println("Similarity measure evaluated = " + m_MeasureType.toString());
            System.out.println("Ontology used = " + m_ontologyType.toString());
        }
        
        // We return the results
        
        return (runningTimes);
    }
    
    /**
     * This function generates a vector of random UMLS concept pairs which
     * will be used to evaluate the performance of the libraries. The CUI pairs
     * always exist in the underlying ontology being evaluated.
     * @param snomedTaxonomy
     * @param nPairs
     * @return 
     */
    
    private String[][] getRandonCUIpairs(
            int nPairs) throws FileNotFoundException, IOException, XMLStreamException 
    {
        // We define the CUI keys vector
        
        String[] strAllValidCUIs;
        
        // We get the list of CUIs mapping to SNOMED or MeSH concepts
        
        if (m_ontologyType == UMLSOntologyType.SNOMEDCT_US)
        {
            HashMap<String, HashSet<Long>> cuiToUmlsOntologyId = 
                    SimilarityLibraryWrapper.readMappingCuiToSnomedIds(
                        m_strSnomedDir, m_strSnomedDBconceptFileName,
                        m_strUmlsDir, m_strSNOMED_CUI_mappingfilename);
            
            // We copy the CUI codes with valkid SNOMED concepts
            
            strAllValidCUIs = new String[cuiToUmlsOntologyId.size()];
            
            cuiToUmlsOntologyId.keySet().toArray(strAllValidCUIs);
            cuiToUmlsOntologyId.clear();
        }
        else
        {
            HashMap<String, HashSet<String>> cuiToMeshOntology =
                    SimilarityLibraryWrapper.readMappingCuiToMeSHIds(
                        m_strMeShDir, m_strMeSHdescriptionFileName,
                        m_strUmlsDir, m_strSNOMED_CUI_mappingfilename);
                 
            // We copy the CUI codes with valid MeSH concepts
                       
            strAllValidCUIs = new String[cuiToMeshOntology.size()];
            
            cuiToMeshOntology.keySet().toArray(strAllValidCUIs);
            cuiToMeshOntology.clear();
        }
        
        // We create a vector of random CUI pairs which are contained in SNOMED
        
        String[][] umlsCuiPairs = new String[nPairs][2];
        
        // We create a random number
        
        Random rand = new Random(500);
        
        // We get the number of CUI nodes mapped to the ontology
        
        long nConcepts = strAllValidCUIs.length;
        
        // We generate the ranomdon node pairs
        
        for (int i = 0; i < nPairs; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                int conceptIndex = (int)(rand.nextDouble() * (nConcepts - 1));
                
                umlsCuiPairs[i][j] = strAllValidCUIs[conceptIndex];
            }
        }
        
        // We return the output
        
        return (umlsCuiPairs);
    }
}
