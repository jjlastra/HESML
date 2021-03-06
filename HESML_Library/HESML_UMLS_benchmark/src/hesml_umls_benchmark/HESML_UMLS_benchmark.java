/*
 * Copyright (C) 2016-2021 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml_umls_benchmark;

import hesml.HESMLversion;
import hesml.configurators.IntrinsicICModelType;
import hesml.measures.GroupwiseMetricType;
import hesml.measures.GroupwiseSimilarityMeasureType;
import hesml.measures.SimilarityMeasureType;
import hesml_umls_benchmark.benchmarks.AnnotateDataset;
import hesml_umls_benchmark.benchmarks.BenchmarkFactory;
import java.sql.*;  
import java.util.HashSet;

/**
 * This class implements the benchmark application used to compare
 * the performance of the HESML-UMLS library with the UMLS::Similarity [2]
 * and SML [3] Libraries.
 * 
 * [1] HESML-UMLS paper.
 * 
 * [2] B.T. McInnes, T. Pedersen, S.V.S. Pakhomov,
 * UMLS-Interface and UMLS-Similarity : open source software for measuring
 * paths and semantic similarity, in: Proc. of the Annual Symposium of the
 * American Medical Informatics Association, ncbi.nlm.nih.gov,
 * San Francisco, CA, 2009: pp. 431–435.
 * 
 * [3] S. Harispe, S. Ranwez, S. Janaqi, J. Montmain, The semantic measures
 * library and toolkit: fast computation of semantic similarity and relatedness
 * using biomedical ontologies, Bioinformatics. 30 (2014) 740–742.
 * 
 * @author j.lastra
 */

public class HESML_UMLS_benchmark
{
    /**
     * Filenames and directories of the SNOMD-CT files and UMLS CUI file
     */

    private static final String m_strGoOntologyFilename = "../GeneOntology/go.obo";
    private static final String m_strMeSHdir = "../UMLS/MeSH_Nov2019";
    private static final String m_strMeSHXmlFilename = "desc2019.xml";
    private static final String m_strUMLSdir = "../UMLS/UMLS2019AB";
    private static final String m_strSnomedDir = "../UMLS/SNOMED_Nov2019";
    private static final String m_strSNOMED_conceptFilename = "sct2_Concept_Snapshot_US1000124_20190901.txt";
    private static final String m_strSNOMED_relationshipsFilename = "sct2_Relationship_Snapshot_US1000124_20190901.txt";
    private static final String m_strSNOMED_descriptionFilename = "sct2_Description_Snapshot-en_US1000124_20190901.txt";
    private static final String m_strUmlsCuiMappingFilename = "MRCONSO.RRF";
    private static final String m_strMedSTSfilename = "../SentenceSimDatasets/MedSTS_subset30_normalized.tsv";    
    private static final String m_strMedSTSLargefilename = "../SentenceSimDatasets/MedStsFullNormalized.tsv";  
    private static final String m_str1millionFilename = "../UMLS_Datasets/1MPairs_NoPreprocess.txt";  
    private static final String m_str4pairsTestFilename = "../UMLS_Datasets/4pairsTest.txt";  
    
    /**
     * Resources directories for WordNet
     * 
     * m_strBaseDir: the base directory with the resources
     * m_strWordNetDatasetsDir: Subdirectory with all the WordNet datasets
     * m_strWordNet3_0_Dir: Subdirectory with WordNet v3.0 dictionary
     */
    
    private static final String  m_strBaseDir = "../";
    private static final String  m_strWordNet3_0_Dir = "Wordnet-3.0/dict";
    
    /**
     * Variable for multi-threading or sequential execution
     */
    
    private static boolean m_multithreading;
    
    /**
     * Main function. This function executes all experiments reported in
     * the HEMSL-UMLS introductory paper [1].
     * 
     * [1] J.J. Lastra-Díaz, A. Lara-Clares, A. García-Serrano,
     * HESML: an efficient semantic measures library for the biomedical
     * domain with a reproducible benchmark, Submitted for Publication. (2020).
     * S
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    
    public static void main(String[] args) throws Exception
    {
        // We print the HESML version
        
        System.out.println("Running HESML_UMLS_benchmark, May 2020) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        // User message
        
        System.out.println("\n---------------------------------------------");
        System.out.println("This program reproduces the experiments reported in the paper below:");
        System.out.println("\tJ.J. Lastra-Díaz, A. Lara-Clares, A. García-Serrano,");
        System.out.println("\tHESML: a real-time semantic measures library for the biomedical domain with a reproducible survey,");
        System.out.println("\tSubmitted for Publication. (2020).");
        
        // We initialize the input parameters
        
        String strOutputDir = "../ReproducibleExperiments/HESMLV1R5_paper/RawOutputFiles";
        
        // We set the multithreading or sequential execution
        
        m_multithreading = false;
        
        // We check the input arguments

        if (args.length == 1)
        {          
            // We set the multithreading option
            
            m_multithreading = "multithreading".equals(args[0]);
        }
        
        // We show the execution mode
        
        String executionModeMessage = m_multithreading? "multithreading":"sequential";
        System.out.println("*** Executing the experiments in " + executionModeMessage + " mode ***");

        
        // We show the input arguments
        
        System.out.println("\nOutput directory for raw experimental data = \"" + strOutputDir + "\"");
        System.out.println("---------------------------------------------");
        
        // We check if the UMLS database is correctly installed.
        
        testDbConnection();
        
        // We intialize the stopwatch
        
        long startTime = System.currentTimeMillis();
        long stoptime = 0L;
        
        /**
         * Experiment 1: we compare the performance of the `HESML, SML and
         * UMLS::Similarity by evaluating the similarity of a sequence
         * of randomly generated UMLS concept pairs using the SNOMED-CT US ontology.
         */
        
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println("------Starting RunRandomConceptsExperiment---");
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        
        RunRandomConceptsExperiment(strOutputDir, UMLSOntologyType.SNOMEDCT_US);
        
        // We show the overalll running time
        
        stoptime = System.currentTimeMillis();
        
        System.out.println("Overall running time (seconds) Experiment 1 = "
            + ((stoptime - startTime) / 1000.0));

        /**
         * Experiment 2: we compare the performance of the HEMSL, SML and
         * UMLS::Similarity by evaluating the similarity of a sequence
         * of randomly generated UMLS concept pairs using the MeSH ontology.
         */
        
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println("------Starting RunRandomConceptsExperiment2--");
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        
        RunRandomConceptsExperiment(strOutputDir, UMLSOntologyType.MeSH);
        
        // We show the overalll running time
        
        stoptime = System.currentTimeMillis();
        
        System.out.println("Overall running time (seconds) Experiment 2 = "
            + ((stoptime - startTime) / 1000.0));

        /**
         * Experiment 3: we compare the performance of HESML and SML on
         * the Gene Ontology (GO).
         */
        
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println("----Starting RunRandomGOConceptsExperiment---");
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        
        RunRandomGOConceptsExperiment(strOutputDir);
        
        // We show the overalll running time
        
        stoptime = System.currentTimeMillis();
        
        System.out.println("Overall running time (seconds) Experiment 3 = "
            + ((stoptime - startTime) / 1000.0));
        
        /**
         * Experiment 4: we evaluate the approximation quality of the novel
         * Ancestor-based Shortest Path Length (AncSPL) algorithm.
         */
        
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println("Starting RunSnomedAncSPLCorrelationExperiment");
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");

        RunSnomedAncSPLCorrelationExperiment(strOutputDir);
        
        // We show the overalll running time
        
        stoptime = System.currentTimeMillis();
        
        System.out.println("Overall running time (seconds) Experiment 4 = "
            + ((stoptime - startTime) / 1000.0));

        /**
         * Experiment 5: we evaluate the approximation quality of the novel
         * Ancestor-based Shortest Path Length (AncSPL) algorithm.
         */
        
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println("--Starting RunGoAncSPLCorrelationExperiment--");
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");

        RunGoAncSPLCorrelationExperiment(strOutputDir);

        /**
         * Experiment 5.1: we evaluate the approximation quality of the novel
         * Ancestor-based Shortest Path Length (AncSPL) algorithm.
         */
        
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println("--Starting RunWordNetAncSPLCorrelationExperiment--");
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");

        RunWordNetAncSPLCorrelationExperiment(strOutputDir);
        
        // We show the overalll running time
        
        stoptime = System.currentTimeMillis();
        
        System.out.println("Overall running time (seconds) Experiment 5 = "
            + ((stoptime - startTime) / 1000.0));
        
        /**
         * Experiment 6: scalability of the AncSPL algorithm with regards to
         * the dimension of the subgraph of SNOMED-CT concepts
         */
        
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println("Starting RunAncSPLSubgraphScalabilityExperiments");
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        
        RunAncSPLSubgraphScalabilityExperiments(strOutputDir);
        
        // We show the overalll running time
        
        stoptime = System.currentTimeMillis();
        
        System.out.println("Overall running time (seconds) Experiment 6 = "
            + ((stoptime - startTime) / 1000.0));

        /**
         * Experiment 7: statistical benchmark of the AncSPL algorithm with regards to
         * the distance between SNOME-CT concepts
         */
        
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println("Starting RunSnomedAncSPLStatisticalExperiment");
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        
        RunSnomedAncSPLStatisticalExperiment(strOutputDir);
        
        // We show the overalll running time
        
        stoptime = System.currentTimeMillis();
        
        System.out.println("Overall running time (seconds) Experiment 7 = "
            + ((stoptime - startTime) / 1000.0));

        /**
         * Experiment 8: statistical benchmark of the AncSPL algorithm with regards to
         * the distance between SNOME-CT concepts
         */
        
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println("--Starting RunGoAncSPLStatisticalExperiment--");
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        
        RunGoAncSPLStatisticalExperiment(strOutputDir);

        /**
         * Experiment 8.1: statistical benchmark of the AncSPL algorithm with regards to
         * the distance between WordNet concepts
         */
        
        System.out.println("----------------------------------------------");
        System.out.println("----------------------------------------------");
        System.out.println("Starting RunWordNetAncSPLStatisticalExperiment");
        System.out.println("----------------------------------------------");
        System.out.println("----------------------------------------------");
        
        RunWordNetAncSPLStatisticalExperiment(strOutputDir);
        
        // We show the overalll running time
        
        stoptime = System.currentTimeMillis();
        
        System.out.println("Overall running time (seconds) Experiment 8 = "
            + ((stoptime - startTime) / 1000.0));
        
        /**
         * Experiment 9: we compare the performance of the HEMSL, SML and
         * UMLS::Similarity by evaluating the MedSTS sentence similarity
         * dataset.
         */
        
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println("---Starting RunSentenceSimilarityExperiment--");
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        
        RunSentenceSimilarityExperiment(strOutputDir);
        
        // We show the overalll running time
        
        stoptime = System.currentTimeMillis();
        
        System.out.println("Overall running time (seconds) Experiment 9 = "
            + ((stoptime - startTime) / 1000.0));
        
        /**
         * Experiment 10: comparison of two large GO annotated files describing
         * the proteins of the Homo Sapiens and Mus  MUsculus organisms.
         */
        
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println("------Starting RunLargeGOExperiment----------");
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        
        RunLargeGOExperiment(strOutputDir);
        
        // We show the overalll running time
        
        stoptime = System.currentTimeMillis();
        
        System.out.println("Overall running time (seconds) = "
            + ((stoptime - startTime) / 1000.0));
    }

    /**
     * This function returns the number of random samples used to evaluate
     * a library on a specific ontology a similarity measure wit the aim
     * of setting reasonable running times. It is needed because the large
     * difference in performance of the libraries being evaluated.
     * @param library
     * @param measureType
     * @param ontology
     * @return 
     */
    
    private static int getRandomSamplesCountPerLibrary(
            SemanticLibraryType     library,
            SimilarityMeasureType   measureType,
            UMLSOntologyType        ontology)
    {
        // We initialize the output
        
        int randomSamples = 100;
        
        // We set some methods to be skipped by SML
        
        HashSet<SimilarityMeasureType> slowMeasures = new HashSet<>();
        HashSet<SimilarityMeasureType> smlNonImplementedMeasures = new HashSet<>();
        
        // We register some measure types with differences for SML and UMLS::Similarity
        
        slowMeasures.add(SimilarityMeasureType.Rada);
        
        smlNonImplementedMeasures.add(SimilarityMeasureType.AncSPLRada);
        smlNonImplementedMeasures.add(SimilarityMeasureType.WuPalmerFast);
                        
        // We set the value according to the library
        
        switch (library)
        {
            case HESML:
                if((ontology == UMLSOntologyType.SNOMEDCT_US)
                                && slowMeasures.contains(measureType))
                {
                    randomSamples = 15;
                }
                else
                {
                    randomSamples = 10000000;
                }
                
                break;
                
            case SML:
                
                if (smlNonImplementedMeasures.contains(measureType))
                {
                    randomSamples = 0;
                }
                else if (!slowMeasures.contains(measureType))
                {
                    randomSamples = 10000000;
                }
                else
                {
                    randomSamples = (ontology == UMLSOntologyType.MeSH) ? 15 : 0;
                }
                
                break;
                
            case UMLS_SIMILARITY:
                
                if (measureType == SimilarityMeasureType.AncSPLRada)
                {
                    randomSamples = 0;
                }
                else
                {
                    randomSamples = (measureType == SimilarityMeasureType.Lin) ? 500 : 15;
                }
                
                break;
        }
        
        // We return the result
        
        return (randomSamples);
    }
    
    /**
     * This funtion returns the number of random samples used to evaluate
     * a library on a specific ontology a similarity measure wit the aim
     * of setting reasonable running times. It is needed becasue the large
     * difference in performance of the libraries being evañuated.
     * @param library
     * @param measureType
     * @return 
     */
    
    private static int getRandomSamplesCountGO(
            SemanticLibraryType     library,
            SimilarityMeasureType   measureType)
    {
        // We initialize the output
        
        int randomSamples = 100;
        
        // We set some methods to be skipped by SML
        
        HashSet<SimilarityMeasureType> pathMeasures = new HashSet<>();
        HashSet<SimilarityMeasureType> smlNonImplementedMeasures = new HashSet<>();
        
        // We register some measure types with differences for SML and UMLS::Similarity
        
        pathMeasures.add(SimilarityMeasureType.Rada);
        
        smlNonImplementedMeasures.add(SimilarityMeasureType.AncSPLRada);
        smlNonImplementedMeasures.add(SimilarityMeasureType.WuPalmerFast);
                        
        // We set the value according to the library
        
        switch (library)
        {
            case HESML:
                
                randomSamples = pathMeasures.contains(measureType) ? 20 : 10000000;
                
                break;
                
            case SML:
                
                if (smlNonImplementedMeasures.contains(measureType))
                {
                    randomSamples = 0;
                }
                else
                {
                    randomSamples = pathMeasures.contains(measureType) ? 20 : 10000000;                
                }
                
                break;
        }
        
        // We return the result
        
        return (randomSamples);
    }
    
    /**
     * This function executes the benchamrk which evaluates the similarity fo
     * a random sequence of concept pairs.
     * @param strRawOutputDir
     * @param ontologyType
     * @throws Exception 
     */
    
    private static void RunRandomConceptsExperiment(
            String            strRawOutputDir, 
            UMLSOntologyType  ontologyType) throws Exception
    {
        /** 
         * We set the vector of libraries to be compared
         */
        
        SemanticLibraryType[] libraries = new SemanticLibraryType[]{
                                                    SemanticLibraryType.UMLS_SIMILARITY,
                                                    SemanticLibraryType.HESML,
                                                    SemanticLibraryType.SML};
        
        // We set the measures being evaluated
                                                    
        SimilarityMeasureType[] measureTypes = new SimilarityMeasureType[]{
                                                    SimilarityMeasureType.Rada,
                                                    SimilarityMeasureType.AncSPLRada,
                                                    SimilarityMeasureType.Lin,
                                                    SimilarityMeasureType.WuPalmerFast};
                
        // We build the vector of raw output filenames
        
        String[] strOutputFilenames = new String[measureTypes.length];
        
        for (int i = 0; i < strOutputFilenames.length; i++)
        {
            strOutputFilenames[i] = "raw_output_" + measureTypes[i] + "_" + ontologyType + ".csv";
        }
        
        // We create a list of threads 
        
        Thread[] threads = new Thread[measureTypes.length];
        
        /**
         * We compare the performance of HESML, SML and UMLS::Similarity by evaluating
         * different similarity measures on a random sequence of concept pairs.
         */
        
        int nRuns = 6;
        
        for (int i = 0; i < measureTypes.length; i++)
        {
            // We create the running-time vector

            int[] nRandomSamplesPerLibrary = new int[libraries.length];
            
            /**
             * We set the number of random concept pairs evaluated by each library
             * with the aim of computing the average running times. Because of the
             * running times could span different orders of magnitude the number
             * of concept pairs need to be different to provide reasonable
             * experimentation times.
             */

            for (int j = 0; j < libraries.length; j++)
            {
                nRandomSamplesPerLibrary[j] = getRandomSamplesCountPerLibrary(libraries[j],
                                                measureTypes[i], ontologyType);
            }
        
            // We set the benchmark
            
            IBioLibraryExperiment benchmark = null;
            
            // We build the benchmark according tor the underlying ontology
            
            switch (ontologyType)
            {
                case SNOMEDCT_US:
                
                    benchmark = BenchmarkFactory.createSnomedConceptBenchmark(
                                    libraries, ontologyType, measureTypes[i],
                                    IntrinsicICModelType.Seco, nRandomSamplesPerLibrary,
                                    nRuns, m_strSnomedDir, m_strSNOMED_conceptFilename,
                                    m_strSNOMED_relationshipsFilename,
                                    m_strSNOMED_descriptionFilename,
                                    m_strUMLSdir, m_strUmlsCuiMappingFilename);
                    
                    break;
                    
                case MeSH:
                    
                    benchmark = BenchmarkFactory.createMeSHConceptBenchmark(
                                    libraries, ontologyType, measureTypes[i],
                                    IntrinsicICModelType.Seco, nRandomSamplesPerLibrary,
                                    nRuns, m_strMeSHdir, m_strMeSHXmlFilename,
                                    m_strUMLSdir, m_strUmlsCuiMappingFilename);
                    
                    break;
            }
            
            // We define the output file
            
            String outputPath = strRawOutputDir + "/" + strOutputFilenames[i];
            
            // We add the new thread to the array 

            threads[i] = new Thread(new BioBenchmarkThread(benchmark, outputPath)); 
        }
        
        // We run the experiments
        
        execute_experiments(threads);
        
        // Debug information - This message should not appear before the termination of all threads
        
        System.out.println("****************************************************************************");
        System.out.println("*********** Finished executing all the threads in experiment ***************");
        System.out.println("****************************************************************************");
    }
    
    /**
     * This function executes the benchmark which evaluates the similarity fo
     * a random sequence of GO concept pairs.
     * @param strRawOutputDir
     * @throws Exception 
     */
    
    private static void RunRandomGOConceptsExperiment(
            String  strRawOutputDir) throws Exception
    {
        // We set the vector of libraries to be compared
        
        SemanticLibraryType[] libraries = new SemanticLibraryType[]{
                                                    SemanticLibraryType.SML,
                                                    SemanticLibraryType.HESML};

        // We set the measures being evaluated
                                                    
        SimilarityMeasureType[] measureTypes = new SimilarityMeasureType[]{
                                                    SimilarityMeasureType.Rada,
                                                    SimilarityMeasureType.AncSPLRada,
                                                    SimilarityMeasureType.Lin};
                
        // We build the vector of raw output filenames
        
        String[] strOutputFilenames = new String[measureTypes.length];
        
        for (int i = 0; i < strOutputFilenames.length; i++)
        {
            strOutputFilenames[i] = "raw_output_" + measureTypes[i] + "_" + "GO.csv";
        }

        // We create a list of threads 
        
        Thread[] threads = new Thread[measureTypes.length];
        
        /**
         * We compare the performance of HESML and SML by evaluating
         * different similarity measures on a random sequence of concept pairs.
         */
        
        int nRuns = 6;
        
        for (int i = 0; i < measureTypes.length; i++)
        {
            // We create the running-time vector

            int[] nRandomSamplesPerLibrary = new int[libraries.length];
        
            /**
             * We set the number of random concept pairs evaluated by each library
             * with the aim of computing the average running times. Because of the
             * running times could span different orders of magnitude the number
             * of concept pairs need to be different to provide reasonable
             * experimentation times.
             */

            for (int j = 0; j < libraries.length; j++)
            {
                nRandomSamplesPerLibrary[j] = getRandomSamplesCountGO(libraries[j], measureTypes[i]);
            }
        
            // We build the benchmark according tor the underlying ontology
            
            IBioLibraryExperiment benchmark = BenchmarkFactory.createGOConceptBenchmark(
                            libraries, measureTypes[i], IntrinsicICModelType.Seco,
                            nRandomSamplesPerLibrary, nRuns, m_strGoOntologyFilename);
        
            // We define the output file
            
            String outputPath = strRawOutputDir + "/" + strOutputFilenames[i];
            
            // We add the new thread to the array 

            threads[i] = new Thread(new BioBenchmarkThread(benchmark, outputPath)); 
        }
        
        // We run the experiments
        
        execute_experiments(threads);
        
        // Debug information - This message should not appear before the termination of all threads
        
        System.out.println("****************************************************************************");
        System.out.println("*********** Finished executing all the threads in experiment ***************");
        System.out.println("****************************************************************************");
    }
    
    /**
     * This function executes the benchmark which evaluates the similarity of
     * the sentence pairs in the MedSTS dataset.
     * @param strRawOutputDir
     * @param ontologyType
     * @throws Exception 
     */
    
    private static void RunSentenceSimilarityExperiment(
            String  strRawOutputDir) throws Exception
    {
        /**
         * We set the vector of libraries to be compared
         */
        
        SemanticLibraryType[] libraries = new SemanticLibraryType[]{
                                                    SemanticLibraryType.UMLS_SIMILARITY,
                                                    SemanticLibraryType.SML,
                                                    SemanticLibraryType.HESML,
                                                    SemanticLibraryType.HESML};
        
        // We set the measures being evaluated
                                                    
        SimilarityMeasureType[] measureTypes = new SimilarityMeasureType[]{
                                                    SimilarityMeasureType.Rada,
                                                    SimilarityMeasureType.AncSPLRada,
                                                    SimilarityMeasureType.Lin,
                                                    SimilarityMeasureType.WuPalmerFast};
        
        // Create the array with the datasets per library
        
        String[] strDatasetPaths = new String[]{m_strMedSTSfilename,
                                                m_strMedSTSfilename,
                                                m_strMedSTSfilename,
                                                m_str1millionFilename};

        // We build the vector of raw output filenames
        
        String[] strOutputFilenames = new String[measureTypes.length];
        
        for (int i = 0; i < strOutputFilenames.length; i++)
        {
            strOutputFilenames[i] = "raw_output_" + measureTypes[i] + "_MedSTS.csv";
        }
        
        // We initialize the annotation object
        
        AnnotateDataset annotateDatasets = new AnnotateDataset(strDatasetPaths);
        
        // We iterate the datasets and annotate them
        
        for (String strDatasetPath : strDatasetPaths)
        {          
            // We annotate the dataset

            Thread annotateFirstSentences = new Thread(new BioAnnotationThread(annotateDatasets, strDatasetPath, "firstSentence")); 
            Thread annotateSecondSentences = new Thread(new BioAnnotationThread(annotateDatasets, strDatasetPath, "secondSentence")); 
            
            // We start both threads
            
            System.out.println("Annotating first sentences from the dataset " + strDatasetPath);
            
            annotateFirstSentences.start();
            
            System.out.println("Annotating second sentences from the dataset " + strDatasetPath);
            
            annotateSecondSentences.start();
            
            // We wait until both threads have finished
            
            annotateFirstSentences.join();
            annotateSecondSentences.join();
            
            // We join the pairs of sentences
            
            System.out.println("Joinning first sentences from the dataset " + strDatasetPath);
            
            annotateDatasets.joinAnnotatedDatasets(strDatasetPath);
        }
        
        // We create a list of threads for the experiments execution
        
        Thread[] threads = new Thread[measureTypes.length];
        
        /**
         * Experiment 3: we compare the performance of the HEMSL, SML and
         * UMLS::Similarity libraries by evaluating 30 sentence pairs
         * extract from the MedSTS dataset [1], and 1 million sentence pairs
         * extracted from the BioC corpus.
         * [1] Wang, Yanshan, Naveed Afzal, Sunyang Fu, Liwei Wang, 
         * Feichen Shen, Majid Rastegar-Mojarad, and Hongfang Liu. 2018. 
         * “MedSTS: A Resource for Clinical Semantic Textual Similarity.” 
         * Language Resources and Evaluation, October.
         */
        
        for (int i = 0; i < measureTypes.length; i++)
        {
            IBioLibraryExperiment benchmark = BenchmarkFactory.createMeSHSentenceBenchmark(
                                                libraries, measureTypes[i],
                                                IntrinsicICModelType.Seco, strDatasetPaths, 
                                                m_strMeSHdir, m_strMeSHXmlFilename,
                                                m_strUMLSdir, m_strUmlsCuiMappingFilename,
                                                annotateDatasets);

            // We define the output file
            
            String outputPath = strRawOutputDir + "/" + strOutputFilenames[i];
            
            // We add the new thread to the array 

            threads[i] = new Thread(new BioBenchmarkThread(benchmark, outputPath)); 
        }
        
        // We run the experiments
        
        execute_experiments(threads);
        
        // Debug information - This message should not appear before the termination of all threads
        
        System.out.println("****************************************************************************");
        System.out.println("*********** Finished executing all the threads in experiment ***************");
        System.out.println("****************************************************************************");
    }
       
    /**
     * This function runs the scalability experiment for the AncSPL algorithm.
     * We evaluate the average speed as a function of the dimension of the
     * subgraph used by the AncSPL algorithm
     * @param strRawOutputDir 
     */
    
    private static void RunAncSPLSubgraphScalabilityExperiments(
        String  strRawOutputDir) throws Exception
    {
        // We create and evaluate the banchmark for SNOMED-CT
        
        IBioLibraryExperiment snomedBenchmark = BenchmarkFactory.createSnomedAncSPLSubgraphScalabilityBenchmark(
                                                m_strSnomedDir, m_strSNOMED_conceptFilename,
                                                m_strSNOMED_relationshipsFilename,
                                                m_strSNOMED_descriptionFilename,
                                                m_strUMLSdir, m_strUmlsCuiMappingFilename);
        
        
        snomedBenchmark.run(strRawOutputDir + "/" + "raw_SNOMED_AnsSPL_subgraph_groups.csv");
        snomedBenchmark.clear();
        
        // We create and evaluate the banchmark for GO
        
        IBioLibraryExperiment goBenchmark = BenchmarkFactory.createGoAncSPLSubgraphScalabilityTest(
                                                m_strGoOntologyFilename);
        
        
        goBenchmark.run(strRawOutputDir + "/" + "raw_GO_AnsSPL_subgraph_groups.csv");
        goBenchmark.clear();
        
        // We create and evaluate the banchmark for WordNet
        
        IBioLibraryExperiment wordNetBenchmark = BenchmarkFactory.createWordNetAncSPLSubgraphScalabilityTest(
                                                m_strBaseDir, m_strWordNet3_0_Dir);
        
        
        wordNetBenchmark.run(strRawOutputDir + "/" + "raw_WordNet_AnsSPL_subgraph_groups.csv");
        wordNetBenchmark.clear();
    }
    
    /**
     * This function runs the statistical experiment for the AncSPL algorithm.
     * This benchmark computes the exact and approximated distance values for
     * a collection of random concept pairs to allow an statistical analysis
     * of the approximation quality of the AncSPL algorithm.
     * @param strRawOutputDir 
     */
    
    private static void RunSnomedAncSPLStatisticalExperiment(
        String  strRawOutputDir) throws Exception
    {
        // We create the banchmark
        
        IBioLibraryExperiment benchmark = BenchmarkFactory.createAncSPLStatisticalBenchmark(
                                                m_strSnomedDir, m_strSNOMED_conceptFilename,
                                                m_strSNOMED_relationshipsFilename,
                                                m_strSNOMED_descriptionFilename,
                                                m_strUMLSdir, m_strUmlsCuiMappingFilename);
        
        // We compute the exact and approximated (AncSPL) distance values between
        // 1000 random SNOMED-CT concepts
        
        benchmark.run(strRawOutputDir + "/" + "raw_AnsSPL_SNOMED_statisticalData_test.csv");
        
        // We release all resources
        
        benchmark.clear();
    }
    
    /**
     * This function runs the statistical experiment for the AncSPL algorithm.
     * This benchmark computes the exact and approximated distance values for
     * a collection of random concept pairs to allow an statistical analysis
     * of the approximation quality of the AncSPL algorithm.
     * @param strRawOutputDir 
     */
    
    private static void RunGoAncSPLStatisticalExperiment(
        String  strRawOutputDir) throws Exception
    {
        // We create the banchmark
        
        IBioLibraryExperiment benchmark = BenchmarkFactory.createAncSPLStatisticalBenchmark(
                                                m_strGoOntologyFilename);
        
        // We compute the exact and approximated (AncSPL) distance values between
        // 1000 random SNOMED-CT concepts
        
        benchmark.run(strRawOutputDir + "/" + "raw_AnsSPL_GO_statisticalData_test.csv");
        
        // We release all resources
        
        benchmark.clear();
    }
    
    /**
     * This function runs the statistical experiment for the AncSPL algorithm.
     * This benchmark computes the exact and approximated distance values for
     * a collection of random concept pairs to allow an statistical analysis
     * of the approximation quality of the AncSPL algorithm.
     * @param strRawOutputDir 
     */
    
    private static void RunWordNetAncSPLStatisticalExperiment(
        String  strRawOutputDir) throws Exception
    {
        // We create the banchmark
        
        IBioLibraryExperiment benchmark = BenchmarkFactory.createAncSPLStatisticalBenchmark(
                                                m_strBaseDir, 
                                                m_strWordNet3_0_Dir);
        
        // We compute the exact and approximated (AncSPL) distance values between
        // 1000 random SNOMED-CT concepts
        
        benchmark.run(strRawOutputDir + "/" + "raw_AnsSPL_WordNet_statisticalData_test.csv");
        
        // We release all resources
        
        benchmark.clear();
    }

    /**
     * Experiment 4: we evaluate the approximation quality of the novel
     * Ancestor-based Shortest Path Length (AncSPL) algorithm in the SNOMED-CT
     * ontology.
     * @param strRawOutputDir
     * @param strMedSTSfilename
     */
    
    private static void RunSnomedAncSPLCorrelationExperiment(
            String  strRawOutputDir) throws Exception
    {
        // We define the random seed
        
        int seed = 500;
        
        // We set the measures being evaluated
                                                    
        SimilarityMeasureType[][] measureTypes = new SimilarityMeasureType[3][2];
        
        measureTypes[0][0] = SimilarityMeasureType.Rada;
        measureTypes[0][1] = SimilarityMeasureType.AncSPLRada;
        measureTypes[1][0] = SimilarityMeasureType.LeacockChodorow;
        measureTypes[1][1] = SimilarityMeasureType.AncSPLLeacockChodorow;
        measureTypes[2][0] = SimilarityMeasureType.CosineNormWeightedJiangConrath;
        measureTypes[2][1] = SimilarityMeasureType.AncSPLCosineNormWeightedJiangConrath;
        
        // We build the vector of raw output filenames
        
        String[] strOutputFilenames = new String[measureTypes.length];
        
        for (int i = 0; i < strOutputFilenames.length; i++)
        {
            strOutputFilenames[i] = "raw_output_SNOMED_" + measureTypes[i][1] + "_exp4.csv";
        }
        
        // We create a list of threads
        
        Thread[] threads = new Thread[measureTypes.length];
        
        // We compare the correlation between two measures
        
        for (int i = 0; i < measureTypes.length; i++)
        {
            // We create the benchmark
            
            IBioLibraryExperiment benchmark = BenchmarkFactory.createSnomedAncSPLBenchmark(
                                                IntrinsicICModelType.Seco,
                                                measureTypes[i][0],
                                                measureTypes[i][1],
                                                1000, m_strSnomedDir, m_strSNOMED_conceptFilename,
                                                m_strSNOMED_relationshipsFilename,
                                                m_strSNOMED_descriptionFilename,
                                                m_strUMLSdir, m_strUmlsCuiMappingFilename, seed);
            
            // We define the output file
            
            String outputPath = strRawOutputDir + "/" + strOutputFilenames[i];
            
            // We add the new thread to the array 

            threads[i] = new Thread(new BioBenchmarkThread(benchmark, outputPath)); 
        }
        
        // We run the experiments
        
        execute_experiments(threads);
        
        // Debug information - This message should not appear before the termination of all threads
        
        System.out.println("****************************************************************************");
        System.out.println("*********** Finished executing all the threads in experiment ***************");
        System.out.println("****************************************************************************");
    }
    
    /**
     * Experiment 4.1: we evaluate the approximation quality of the novel
     * Ancestor-based Shortest Path Length (AncSPL) algorithm in the WordNet
     * ontology.
     * @param strRawOutputDir
     * @param strMedSTSfilename
     */
    
    private static void RunWordNetAncSPLCorrelationExperiment(
            String  strRawOutputDir) throws Exception
    {
        // We define the random seed
        
        int seed = 550;
        
        // We set the measures being evaluated
                                                    
        SimilarityMeasureType[][] measureTypes = new SimilarityMeasureType[3][2];
        
        measureTypes[0][0] = SimilarityMeasureType.Rada;
        measureTypes[0][1] = SimilarityMeasureType.AncSPLRada;
        measureTypes[1][0] = SimilarityMeasureType.LeacockChodorow;
        measureTypes[1][1] = SimilarityMeasureType.AncSPLLeacockChodorow;
        measureTypes[2][0] = SimilarityMeasureType.CosineNormWeightedJiangConrath;
        measureTypes[2][1] = SimilarityMeasureType.AncSPLCosineNormWeightedJiangConrath;
        
        // We build the vector of raw output filenames
        
        String[] strOutputFilenames = new String[measureTypes.length];
        
        for (int i = 0; i < strOutputFilenames.length; i++)
        {
            strOutputFilenames[i] = "raw_output_WordNet_" + measureTypes[i][1] + "_exp4.csv";
        }
        
        // We create a list of threads
        
        Thread[] threads = new Thread[measureTypes.length];
        
        // We compare the correlation between two measures
        
        for (int i = 0; i < measureTypes.length; i++)
        {
            // We create the benchmark
            
            IBioLibraryExperiment benchmark = BenchmarkFactory.createWordNetAncSPLBenchmark(
                                                IntrinsicICModelType.Seco,
                                                measureTypes[i][0],
                                                measureTypes[i][1],
                                                1000, m_strBaseDir, m_strWordNet3_0_Dir, seed);
            
            // We define the output file
            
            String outputPath = strRawOutputDir + "/" + strOutputFilenames[i];
            
            // We add the new thread to the array 
            
            threads[i] = new Thread(new BioBenchmarkThread(benchmark, outputPath)); 
        }
        
        // We run the experiments
        
        execute_experiments(threads);
        
        // Debug information - This message should not appear before the termination of all threads
        
        System.out.println("****************************************************************************");
        System.out.println("*********** Finished executing all the threads in experiment ***************");
        System.out.println("****************************************************************************");
    }
    
    /**
     * Experiment 4: we evaluate the approximation quality of the novel
     * Ancestor-based Shortest Path Length (AncSPL) algorithm in the GO
     * ontology.
     * @param strRawOutputDir
     * @param strMedSTSfilename
     */
    
    private static void RunGoAncSPLCorrelationExperiment(
            String  strRawOutputDir) throws Exception
    {
        // We define the random seed
        
        int seed = 500;
        
        // We set the measures being evaluated
                                                    
        SimilarityMeasureType[][] measureTypes = new SimilarityMeasureType[3][2];
        
        measureTypes[0][0] = SimilarityMeasureType.Rada;
        measureTypes[0][1] = SimilarityMeasureType.AncSPLRada;
        measureTypes[1][0] = SimilarityMeasureType.LeacockChodorow;
        measureTypes[1][1] = SimilarityMeasureType.AncSPLLeacockChodorow;
        measureTypes[2][0] = SimilarityMeasureType.CosineNormWeightedJiangConrath;
        measureTypes[2][1] = SimilarityMeasureType.AncSPLCosineNormWeightedJiangConrath;
        
        // We build the vector of raw output filenames
        
        String[] strOutputFilenames = new String[measureTypes.length];
        
        for (int i = 0; i < strOutputFilenames.length; i++)
        {
            strOutputFilenames[i] = "raw_output_GO_" + measureTypes[i][1] + "_exp4.csv";
        }
        
        // We create a list of threads 
        
        Thread[] threads = new Thread[measureTypes.length];

        // We compare the correlation between two path-based measures
        
        for (int i = 0; i < measureTypes.length; i++)
        {
            // We create the benchmark
            
            IBioLibraryExperiment benchmark = BenchmarkFactory.createGoAncSPLBenchmark(
                                                IntrinsicICModelType.Seco,
                                                measureTypes[i][0],
                                                measureTypes[i][1],
                                                10000, m_strGoOntologyFilename, seed);
            
            // We define the output file
            
            String outputPath = strRawOutputDir + "/" + strOutputFilenames[i];
            
            // We add the new thread to the array 

            threads[i] = new Thread(new BioBenchmarkThread(benchmark, outputPath)); 
        }
        
        // We run the experiments
        
        execute_experiments(threads);
        
        // Debug information - This message should not appear before the termination of all threads
        
        System.out.println("****************************************************************************");
        System.out.println("*********** Finished executing all the threads in experiment ***************");
        System.out.println("****************************************************************************");
    }
    
    /**
     * This function runs the large GO experiment which compares all proteins
     * of the Homo sapiens (goa_human.gaf) and Canis lupus familiaris (goa_dog.gaf)
     * organisms contained in two
     * large GO Annotated Files in GAF file format.
     * SNOMED-CT concepts.
     * @param strRawOutputDir 
     */
    
    private static void RunLargeGOExperiment(
        String  strRawOutputDir) throws Exception
    {
        // We set the GO ontology filename
        
        String strHomoSapiensGafFilename = "../GO_datasets/goa_human.gaf"; 
        String strDogGafFilename = "../GO_datasets/goa_dog.gaf";
        
        // We build the suffix list for the raw output files
        
        String[] strGroupwiseMeasures = new String[]{GroupwiseSimilarityMeasureType.SimLP.toString(),
                                                GroupwiseSimilarityMeasureType.SimUI.toString(),
                                                GroupwiseSimilarityMeasureType.SimGIC.toString(),
                                                "BMA-Lin-Seco"
//                                                "AVG-AncSPLRada"
        };
        
        // We build the vector of raw output filenames
        
        String[] strOutputFilenames = new String[strGroupwiseMeasures.length];
        
        for (int i = 0; i < strOutputFilenames.length; i++)
        {
            strOutputFilenames[i] = "raw_output_" + strGroupwiseMeasures[i] + "_largeGO_test.csv";
        }
        
        // We create the benchmark and threads
        
        IBioLibraryExperiment[] bioExperiments = new IBioLibraryExperiment[4];
        
        bioExperiments[0] = BenchmarkFactory.createLargeGOConceptBenchmark(GroupwiseSimilarityMeasureType.SimLP,
                            m_strGoOntologyFilename, strHomoSapiensGafFilename, strDogGafFilename);

        bioExperiments[1] = BenchmarkFactory.createLargeGOConceptBenchmark(GroupwiseSimilarityMeasureType.SimUI,
                            m_strGoOntologyFilename, strHomoSapiensGafFilename, strDogGafFilename);

        bioExperiments[2] = BenchmarkFactory.createLargeGOConceptBenchmark(IntrinsicICModelType.Seco,
                            m_strGoOntologyFilename, strHomoSapiensGafFilename, strDogGafFilename);

        bioExperiments[3] = BenchmarkFactory.createLargeGOConceptBenchmark(
                            GroupwiseMetricType.BestMatchAverage, SimilarityMeasureType.Lin,
                            IntrinsicICModelType.Seco, m_strGoOntologyFilename,
                            strHomoSapiensGafFilename, strDogGafFilename);

//        bioExperiments[4] = BenchmarkFactory.createLargeGOConceptBenchmark(
//                            GroupwiseMetricType.Average, SimilarityMeasureType.AncSPLRada,
//                            IntrinsicICModelType.Seco, m_strGoOntologyFilename,
//                            strHomoSapiensGafFilename, strDogGafFilename);
        
        // We create a list of threads 
        
        Thread[] threads = new Thread[bioExperiments.length];
        
        // We compare create the benchmarks
        
        for (int i = 0; i < bioExperiments.length; i++)
        {
            // We define the output file
            
            String outputPath = strRawOutputDir + "/" + strOutputFilenames[i];
            
            // We add the new thread to the array 

            threads[i] = new Thread(new BioBenchmarkThread(bioExperiments[i], outputPath)); 
        }
        
        // We run the experiments
        
        execute_experiments(threads);
        
        // Debug information - This message should not appear before the termination of all threads
        
        System.out.println("****************************************************************************");
        System.out.println("*********** Finished executing all the threads in experiment ***************");
        System.out.println("****************************************************************************");
    }
    
    /**
     * Function for testing if the UMLS database is correctly installed.
     * 
     * This function check which sources (vocabularies) are indexed in the UMLS database.
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    
    public static void testDbConnection() throws ClassNotFoundException, SQLException
    {
        // We check if the UMLS:Sim MySQL database is working
        
        try{
            System.out.println("Initializing UMLS database test...");
            
            System.out.println("Checking MySQL driver for Java...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Ok");
            
            System.out.println("Checking MySQL connection...");
            Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/umls","root","");  
            System.out.println("Ok");
            
            System.out.println("Execute testing query...");
            Statement stmt=con.createStatement();  
            ResultSet rs=stmt.executeQuery("select distinct(sab) from MRREL;");  
            System.out.println("Ok");
            
            System.out.println("List of available vocabularies:");
            while(rs.next())  
                System.out.println("   " + rs.getString(1));  
            
            // Closing connection
            
            con.close();  
        } 
        catch(ClassNotFoundException | SQLException e)
        { 
            // If the MySQL database is not working, it's not possible to evaluate UMLS:Sim experiments
            System.err.println(e);
        }  
        
    }
    
    /**
     * This function starts and wait the experiment threads
     * 
     * If multi-threading mode, this function starts all the threads 
     * and then waits until all the threads have finished.
     * 
     * If sequential mode, start and wait until finish each thread.
     * 
     * @param threads 
     */
    private static void execute_experiments(Thread[] threads) throws InterruptedException
    {
        // We run the experiments
        
        if(m_multithreading)
        {
            // If multithreading, first we start all the threads
            
            for (Thread thread : threads) 
            {
                System.out.println("Starting thread " + thread.toString());
                        
                // Start the experiment thread

                thread.start();
            }

            // We wait until other threads have finished their execution

            for (Thread thread : threads) 
            {
                thread.join();
            }
        }
        else
        {
            // If sequential mode, we start and wait until finish each thread
            
            for (Thread thread : threads) 
            {
                // Start the experiment thread and wait until finish

                thread.start();
                thread.join();
            }
        }
    }
}