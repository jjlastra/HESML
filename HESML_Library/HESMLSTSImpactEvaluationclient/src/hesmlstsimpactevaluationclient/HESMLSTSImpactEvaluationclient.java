/* 
 * Copyright (C) 2016-2022 Universidad Nacional de Educación a Distancia (UNED)
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

package hesmlstsimpactevaluationclient;

import hesml.HESMLversion;
import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.WordEmbeddingFileType;
import hesml.sts.benchmarks.ISentenceSimilarityBenchmark;
import hesml.sts.benchmarks.impl.SentenceSimBenchmarkFactory;
import hesml.sts.measures.BERTpoolingMethod;
import hesml.sts.measures.ComMixedVectorsMeasureType;
import hesml.sts.measures.ICombinedSentenceSimilarityMeasure;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.MLPythonLibrary;
import hesml.sts.measures.SWEMpoolingMethod;
import hesml.sts.measures.SentenceEmbeddingMethod;
import hesml.sts.measures.StringBasedSentenceSimilarityMethod;
import hesml.sts.measures.impl.SentenceSimilarityFactory;
import hesml.sts.preprocess.CharFilteringType;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.NERType;
import hesml.sts.preprocess.TokenizerType;
import hesml.sts.preprocess.impl.PreprocessingFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import hesml.taxonomyreaders.mesh.impl.MeSHFactory;
import hesml.taxonomyreaders.obo.IOboOntology;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import hesml.taxonomyreaders.wordnet.impl.WordNetFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a basic client application of the HESML for sentence similarity
 * 
 * @author alicia and j.lastra
 */

public class HESMLSTSImpactEvaluationclient
{
    /**
     * Resources directories.
     * 
     * m_strBaseDir: the base directory with the resources
     * m_strDataDirectory: The base directory with the external resources
     * m_strStopWordsDir: Subdirectory with all the stop words files
     * m_strWordNetDatasetsDir: Subdirectory with all the WordNet datasets
     * m_strWordNet3_0_Dir: Subdirectory with WordNet v3.0 dictionary
     */
    
    private static final String  m_strBaseDir = "/home/user/HESML/HESML_Library/";
    private static final String  m_strDataDirectory = "/home/user/HESML_DATA/";
    private static final String  m_strStopWordsDir = "StopWordsFiles/";
    
    private static final String  m_strWordNetDatasetsDir = m_strBaseDir + "/Wordnet-3.0/dict";
    private static final String  m_strWordNetDBDir = "data.noun";
    
    /**
     * Filenames and directory for the SNOMED-CT files
     */

    private static String m_strSnomedDir = m_strDataDirectory + "UMLS/SNOMED_Nov2019";
    private static final String m_strSnomedConceptFilename = "sct2_Concept_Snapshot_US1000124_20190901.txt";
    private static final String m_strSnomedRelationshipsFilename = "sct2_Relationship_Snapshot_US1000124_20190901.txt";
    private static final String m_strSnomedDescriptionFilename = "sct2_Description_Snapshot-en_US1000124_20190901.txt";

    /** 
     * Filename and directory for the UMLS CUI mapping file
     */
    
    private static final String m_strUmlsCuiMappingFilename = "MRCONSO.RRF";
    private static final String m_strUMLSdir = m_strDataDirectory + "UMLS/UMLS2019AB";    
    
    /**
     * Filenames and directory for the MeSH ontology
     */
    
    private static final String m_strMeSHdir = m_strDataDirectory + "UMLS/MeSH_Nov2019";
    private static final String m_strMeSHdescriptorFilename = "desc2020.xml";
    
    /**
     * Output files path
     */
    
    private static final String m_strDatasetDir = m_strDataDirectory + "SentenceSimDatasets/";
    private static final String m_outputFilesDirPath = m_strBaseDir + "ReproducibleExperiments/BioSentenceSimilarity_paper/BioSentenceSimFinalRawOutputFiles/";
    
    /**
     * Dataset filenames
     */
    
    private static final String m_strDatasetFileNameBIOSSES = "BIOSSESNormalized.tsv";
    private static final String m_strDatasetFileNameMedSTS = "MedStsFullNormalized.tsv";
    private static final String m_strDatasetFileNameCTR = "CTRNormalized_averagedScore.tsv";
   
    /**
    * Filename of the OBO ontology
    */
    
    private static final String m_strOboFilename = "";
    
    /**
     * Singleton instance of the WordNet DB
     */
    
    private static IWordNetDB   m_WordNetDbSingleton = null;
    
    /**
     * Singleton instance of the WordNet taxonomy
     */
    
    private static ITaxonomy    m_WordNetTaxonomySingleton = null;
    
    // Singleton instances of biomedical ontologies and taxonomy

    private static ISnomedCtOntology  m_SnomedOntology = null;          
    private static IMeSHOntology m_MeshOntology = null;    
    private static IOboOntology m_OboOntology = null;    
    private static IVertexList m_vertexesSnomed = null;
    private static ITaxonomy   m_taxonomySnomed = null;
    private static ITaxonomy   m_taxonomyMesh = null;

    /**
     * This function loads an input XML file detailing a
     * set of reproducible experiments on sentence similarity.
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    
    public static void main(String[] args) throws IOException, InterruptedException, Exception
    {
        // We print the HESML version
        
        System.out.println("Running HESMLSTSClient V2R1 (2.1.0.0, February 2020) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
                + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        System.out.println("");
        
        // We load the ontologies
        
        loadOntologies(false);
        
        // We get the start time

        long startFileProcessingTime = System.currentTimeMillis();
        
        long endTime = 0;
        long minutes = 0;
        long seconds = 0;
        
        // Reset the total combinations
        
        int totalCombinations = 0;
        
        /**
         * Testing NER tools.
         * 
         * First, we test the external NER tools to ensure it's all working
         * 
         * Remember to start the services and export the UMLS license number: 
         *  [HESMLDIR]/public_mm:
         *          ~/HESML_DATA/apache-ctakes-4.0.0.1-src/desc# ./bin/skrmedpostctl start
         *          ~/HESML_DATA/apache-ctakes-4.0.0.1-src/desc# ./bin/wsdserverctl start
         *          ~/HESML_DATA/apache-ctakes-4.0.0.1-src/desc# ./bin/mmserver -R SNOMEDCT &
         *  ~/HESML_DATA/apache-ctakes-4.0.0.1-src/desc# export ctakes_umls_apikey=XXXXXXXXXXXXXX
         * 
         * 
         * After executing the tests, read the result file:
         * tail -n50 /home/user/HESML/HESML_Library/ReproducibleExperiments/BioSentenceSimilarity_paper/BioSentenceSimFinalRawOutputFiles/raw_similarity_BIOSSES_tests.csv
         */
        
//        testExternalTools();
        
        /**
         * ***********************************************
         * ***********************************************
         * 
         * PREPROCESSING EXPERIMENTS. Execute the pre-processing experiments
         * 
         * Warning: High computational requirements and time consuming
         * 
         * ***********************************************
         * ***********************************************
         */
        
        totalCombinations += executePreprocessingExperiments();
        
        // We measure the elapsed time to run the experiments
        
        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;
        
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        System.out.println("Finished measures experiments");
        System.out.println("Processed a total of " + totalCombinations + 
                " combinations in = " + seconds + " (seconds)");
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");

        // We measure the elapsed time to run the experiments

        endTime = System.currentTimeMillis();
        minutes = (endTime - startFileProcessingTime) / 60000;
        seconds = (endTime - startFileProcessingTime) / 1000;

        System.out.println("Overall elapsed loading and computation time (minutes) = " + minutes);
        System.out.println("Overall elapsed loading and computation time (seconds) = " + seconds);
    }
    
    /**
     * This function test all the external tools (Metamap and CTakes) before executing the experiments
     */
    
    private static void testExternalTools() throws Exception
    {
        // We initialize the list of measures with different preprocessing methods
            
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
            
        try {
            
            // First, we initialize the preprocessing method using Metamap
            
            IWordProcessing metamapWordProcessing = PreprocessingFactory.getWordProcessing(
                    m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt",
                    TokenizerType.WhiteSpace,
                    true, NERType.MetamapSNOMEDCT,
                    CharFilteringType.BIOSSES);
            
            measuresLst.add(SentenceSimilarityFactory.getStringBasedMeasure(
                            "testStringMeasureMetamap",
                            StringBasedSentenceSimilarityMethod.Jaccard, 
                            metamapWordProcessing));
            
            // Second, we initialize the preprocessing method using Metamap Lite
            
            IWordProcessing metamapLiteWordProcessing = PreprocessingFactory.getWordProcessing(
                    m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt",
                    TokenizerType.WhiteSpace,
                    true, NERType.MetamapLite,
                    CharFilteringType.BIOSSES);
            
            measuresLst.add(SentenceSimilarityFactory.getStringBasedMeasure(
                            "testStringMeasureMetamapLite",
                            StringBasedSentenceSimilarityMethod.Jaccard, 
                            metamapLiteWordProcessing));
            
            // First, we initialize the preprocessing method using Metamap Lite
            
            IWordProcessing ctakesWordProcessing = PreprocessingFactory.getWordProcessing(
                    m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt",
                    TokenizerType.WhiteSpace,
                    true, NERType.Ctakes,
                    CharFilteringType.BIOSSES);
            
            measuresLst.add(SentenceSimilarityFactory.getStringBasedMeasure(
                            "testStringMeasurectakes",
                            StringBasedSentenceSimilarityMethod.Jaccard, 
                            ctakesWordProcessing));
            
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(HESMLSTSImpactEvaluationclient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // We execute the experiments
        
        executeExperiments(measuresLst, "tests");
    }
    
    /**
     * This function executes all the pre-processing experiments at once
     * @return
     * @throws IOException
     * @throws Exception 
     */
    
    private static int executePreprocessingExperiments() throws IOException, Exception
    {
        // Initialize the total number of combinations
        
        int totalCombinations = 0;
        
        // Loading message

        System.out.println("Loading and running all the preprocessing combination experiments");
        
        // We get the start time

        long startFileProcessingTime = System.currentTimeMillis();
        
        long endTime = 0;
        long minutes = 0;
        long seconds = 0;
        
        /**
         * ***********************************************
         * ***********************************************
         * 
         * EXPERIMENT 1. Starting String-based measures experiments
         * 
         * ***********************************************
         * ***********************************************
         */
        
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        System.out.println("Starting String-based measures experiments");
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        
        // Compute all preprocessing combinations
        
        totalCombinations += executeStringMeasures(
                getAllPreprocessingConfigurations(NERType.None), 
                getAllPreprocessingConfigurations(NERType.Ctakes), 
                "Stringbased_ALLPreprocessingCombs");
        
        // We measure the elapsed time to run the experiments

        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;

        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        System.out.println("Finished String-based measures experiments");
        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        
        /**
         * ***********************************************
         * ***********************************************
         * 
         * EXPERIMENT 2. Starting our WE-based measures experiments
         * 
         * ***********************************************
         * ***********************************************
         */
        
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        System.out.println("Starting our preprocessed WE-based measures experiments");
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        
        // Reset the total combinations
        
        totalCombinations = 0;
        
        // Compute all the executions
        
        totalCombinations += executeOurWEMeasures(getStopWordsPreprocessingConfigurations(false, NERType.None), "OurWEStopWords");
        totalCombinations += executeOurWEMeasures(getCharFilteringPreprocessingConfigurations(false, NERType.None), "OurWECharFiltering");
        totalCombinations += executeOurWEMeasures(getTokenizerPreprocessingConfigurations(NERType.None), "OurWETokenizer");
        totalCombinations += executeOurWEMeasures(getLowerCasePreprocessingConfigurations(false, NERType.None), "OurWELC");
        
        // We measure the elapsed time to run the experiments

        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;

        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        System.out.println("Finished our preprocessed WE-based measures experiments");
        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        
        /**
         * ***********************************************
         * ***********************************************
         * 
         * EXPERIMENT 3. Starting WBSM and UBSM measures experiments
         * 
         * ***********************************************
         * ***********************************************
         */
        
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        System.out.println("Starting ontology-based measures experiments");
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        
        // Reset the total combinations
        
        totalCombinations = 0;
        
        // We calculate the best preprocessing configurations for WBSM
        
        totalCombinations += executeWBSMMeasures(
                getStopWordsPreprocessingConfigurations(false, NERType.None), "WBSM_PreprocessingCombsStopWords");
        
        totalCombinations += executeWBSMMeasures(
                getCharFilteringPreprocessingConfigurations(false, NERType.None), "WBSM_PreprocessingCombsCharFiltering");
        
        totalCombinations += executeWBSMMeasures(
                getTokenizerPreprocessingConfigurations(NERType.None), "WBSM_PreprocessingCombsTokenizer");
        
        totalCombinations += executeWBSMMeasures(
                getLowerCasePreprocessingConfigurations(false, NERType.None), "WBSM_PreprocessingCombsLC");

        // We calculate the best word measure combination for WBSM measures based on the best preprocessing partial results
        
        // We define the preprocessing configuration
        
        ArrayList<IWordProcessing> bestWordPartialPreprocessingConfigWBSM = new ArrayList<>();
        
        // We create the WBSM preprocessing configuration with the best results

        IWordProcessing bestWBSMWordProcessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.StanfordCoreNLPv4_2_0, 
                        true, NERType.None,
                        CharFilteringType.BIOSSES);
        
        bestWordPartialPreprocessingConfigWBSM.add(bestWBSMWordProcessing);
        
          totalCombinations += executeWBSMMeasures(
                bestWordPartialPreprocessingConfigWBSM, "WBSM_PreprocessingCombsBestMeasure");
        
//         We calculate the best preprocessing configurations for UBSM using CTakes as default NER
        
        totalCombinations += executeUBSMMeasures(
                getStopWordsPreprocessingConfigurations(false, NERType.Ctakes), "UBSM_PreprocessingCombsStopWords", NERType.Ctakes);
        
        totalCombinations += executeUBSMMeasures(
                getCharFilteringPreprocessingConfigurations(false, NERType.Ctakes), "UBSM_PreprocessingCombsCharFiltering", NERType.Ctakes);
        
        totalCombinations += executeUBSMMeasures(
                getTokenizerPreprocessingConfigurations(NERType.Ctakes), "UBSM_PreprocessingCombsTokenizer", NERType.Ctakes);
        
        totalCombinations += executeUBSMMeasures(
                getLowerCasePreprocessingConfigurations(false, NERType.Ctakes), "UBSM_PreprocessingCombsLC", NERType.Ctakes);
        

        // We calculate the best word measure combination for UBSM measures based on the best preprocessing partial results
        
        // We define the preprocessing configuration
        
        ArrayList<IWordProcessing> bestWordPartialPreprocessingConfigUBSM = new ArrayList<>();
        
        // We create the WBSM preprocessing configuration with the best results

        IWordProcessing bestUBSMWordProcessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.StanfordCoreNLPv4_2_0, 
                        true, NERType.MetamapSNOMEDCT,
                        CharFilteringType.BIOSSES);
        
        bestWordPartialPreprocessingConfigUBSM.add(bestUBSMWordProcessing);
        
        // We execute the UBSM measures
        
        totalCombinations += executeUBSMMeasures(
                bestWordPartialPreprocessingConfigUBSM, "UBSM_PreprocessingCombsBestMeasure", NERType.MetamapSNOMEDCT);
        
        
        // We execute the best-and-worst COM combinations experiment
        
        totalCombinations +=  executeCOMMeasures("COMBestWorst");
        
        // We measure the elapsed time to run the experiments

        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;

        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        System.out.println("Finished ontology-based measures experiments");
        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        
        /**
         * ***********************************************
         * ***********************************************
         * 
         * EXPERIMENT 4. Starting SWEM measures experiments
         * 
         * ***********************************************
         * ***********************************************
         */
        
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        System.out.println("Starting SWEM-based measures experiments");
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        
        // List with all the pooling methods for WE-based methods
        
        ArrayList<SWEMpoolingMethod> poolingMethods = new ArrayList<>();
        poolingMethods.add(SWEMpoolingMethod.Max);
        poolingMethods.add(SWEMpoolingMethod.Average);
        poolingMethods.add(SWEMpoolingMethod.Min);
        poolingMethods.add(SWEMpoolingMethod.Sum);
        
        // We define the models to be evaluated
        
        ArrayList<String> modelsFastextVecBased_part1 = new ArrayList<>();
        ArrayList<String> modelsFastextVecBased_part2 = new ArrayList<>();
        
        // We only add a sample of each WE model, we do not select all the available models for execution time restrictions
        
        modelsFastextVecBased_part1.add("bioconceptvec_fasttext.txt");
        modelsFastextVecBased_part2.add("PubMed-and-PMC-w2v.txt");
        
        ArrayList<String> modelsBioWordVecBased = new ArrayList<>();
        
        modelsBioWordVecBased.add("bio_embedding_intrinsic");
        modelsBioWordVecBased.add("BioNLP2016_PubMed-shuffle-win-2.bin");
        
        // Reset the total combinations
        
        totalCombinations = 0;
        
        // Compute all the executions
        
        for(SWEMpoolingMethod pooling : poolingMethods)
        {
            totalCombinations += executeSWEMMeasures(modelsFastextVecBased_part1, 
                    getStopWordsPreprocessingConfigurations(false, NERType.None), "SWEMStopWords_part1" + pooling.name(), 
                    WordEmbeddingFileType.FastTextVecWordEmbedding, pooling);
            totalCombinations += executeSWEMMeasures(modelsFastextVecBased_part1, 
                    getCharFilteringPreprocessingConfigurations(false, NERType.None), "SWEMCharFiltering_part1" + pooling.name(), 
                    WordEmbeddingFileType.FastTextVecWordEmbedding, pooling);
            totalCombinations += executeSWEMMeasures(modelsFastextVecBased_part1, 
                    getTokenizerPreprocessingConfigurations(NERType.None), "SWEMTokenizer_part1" + pooling.name(), 
                    WordEmbeddingFileType.FastTextVecWordEmbedding, pooling);
            totalCombinations += executeSWEMMeasures(modelsFastextVecBased_part1, 
                    getLowerCasePreprocessingConfigurations(false, NERType.None), "SWEMLC_part1" + pooling.name(), 
                    WordEmbeddingFileType.FastTextVecWordEmbedding, pooling);
            
            totalCombinations += executeSWEMMeasures(modelsFastextVecBased_part2, 
                    getStopWordsPreprocessingConfigurations(false, NERType.None), "SWEMStopWords_part2" + pooling.name(), 
                    WordEmbeddingFileType.FastTextVecWordEmbedding, pooling);
            totalCombinations += executeSWEMMeasures(modelsFastextVecBased_part2, 
                    getCharFilteringPreprocessingConfigurations(false, NERType.None), "SWEMCharFiltering_part2" + pooling.name(), 
                    WordEmbeddingFileType.FastTextVecWordEmbedding, pooling);
            totalCombinations += executeSWEMMeasures(modelsFastextVecBased_part2, 
                    getTokenizerPreprocessingConfigurations(NERType.None), "SWEMTokenizer_part2" + pooling.name(), 
                    WordEmbeddingFileType.FastTextVecWordEmbedding, pooling);
            totalCombinations += executeSWEMMeasures(modelsFastextVecBased_part2, 
                    getLowerCasePreprocessingConfigurations(false, NERType.None), "SWEMLC_part2" + pooling.name(), 
                    WordEmbeddingFileType.FastTextVecWordEmbedding, pooling);
            
            totalCombinations += executeSWEMMeasures(modelsBioWordVecBased, 
                    getStopWordsPreprocessingConfigurations(false, NERType.None), "SWEMStopWords_part3" + pooling.name(), 
                    WordEmbeddingFileType.BioWordVecBinaryWordEmbedding, pooling);
            totalCombinations += executeSWEMMeasures(modelsBioWordVecBased, 
                    getCharFilteringPreprocessingConfigurations(false, NERType.None), "SWEMCharFiltering_part3" + pooling.name(), 
                    WordEmbeddingFileType.BioWordVecBinaryWordEmbedding, pooling);
            totalCombinations += executeSWEMMeasures(modelsBioWordVecBased, 
                    getTokenizerPreprocessingConfigurations(NERType.None), "SWEMTokenizer_part3" + pooling.name(), 
                    WordEmbeddingFileType.BioWordVecBinaryWordEmbedding, pooling);
            totalCombinations += executeSWEMMeasures(modelsBioWordVecBased, 
                    getLowerCasePreprocessingConfigurations(false, NERType.None), "SWEMLC_part3" + pooling.name(), 
                    WordEmbeddingFileType.BioWordVecBinaryWordEmbedding, pooling);
        }
        
        // We measure the elapsed time to run the experiments

        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;

        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        System.out.println("Finished SWEM-based measures experiments");
        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");

        /**
         * ***********************************************
         * ***********************************************
         * 
         * EXPERIMENT 5. Starting BERT measures experiments
         * 
         * ***********************************************
         * ***********************************************
         */
        
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        System.out.println("Starting BERT methods experiments");
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        
        // We define the number of models to be evaluated
        
        int total_models = 17;
        
        // We define the models to be evaluated

        String[][] modelPaths = getBERTModelPathList(total_models);
        
        // Reset the total combinations
        
        totalCombinations = 0;
        
        // Compute all the executions
        
        totalCombinations += executeBERTMeasures(
        modelPaths, total_models, getStopWordsPreprocessingConfigurations(true, NERType.None), "BERTStopWords");

        totalCombinations += executeBERTMeasures(
        modelPaths, total_models, getCharFilteringPreprocessingConfigurations(true, NERType.None), "BERTCharFiltering");

        totalCombinations += executeBERTMeasures(
        modelPaths, total_models, getLowerCasePreprocessingConfigurations(true, NERType.None), "BERTLC");
        
        // We measure the elapsed time to run the experiments

        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;

        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        System.out.println("Finished BERT methods experiments");
        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        
        /**
         * ***********************************************
         * ***********************************************
         * 
         * EXPERIMENT 6. Starting Sent2Vec experiments
         * 
         * ***********************************************
         * ***********************************************
         */
        
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        System.out.println("Starting Sent2Vec experiments");
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        
        // Compute all preprocessing combinations
        
        totalCombinations += executeSent2VecMeasures(getStopWordsPreprocessingConfigurations(false, NERType.None), "Sent2VecStopWords");
        totalCombinations += executeSent2VecMeasures(getCharFilteringPreprocessingConfigurations(false, NERType.None), "Sent2VecCharFiltering");
        totalCombinations += executeSent2VecMeasures(getTokenizerPreprocessingConfigurations(NERType.None), "Sent2VecTokenizer");
        totalCombinations += executeSent2VecMeasures(getLowerCasePreprocessingConfigurations(false, NERType.None), "Sent2VecLC");
        
        // We measure the elapsed time to run the experiments

        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;

        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        System.out.println("Finished Sent2Vec measures experiments");
        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        
        /**
         * ***********************************************
         * ***********************************************
         * 
         * EXPERIMENT 7. Starting USE experiments
         * 
         * ***********************************************
         * ***********************************************
         */
        
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        System.out.println("Starting USE experiments");
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        
        // Compute all preprocessing combinations
        
        totalCombinations += executeUSEMeasures(getStopWordsPreprocessingConfigurations(false, NERType.None), "USEStopWords");
        totalCombinations += executeUSEMeasures(getCharFilteringPreprocessingConfigurations(false, NERType.None), "USECharFiltering");
        totalCombinations += executeUSEMeasures(getTokenizerPreprocessingConfigurations(NERType.None), "USETokenizer");
        totalCombinations += executeUSEMeasures(getLowerCasePreprocessingConfigurations(false, NERType.None), "USELC");
        
        // We measure the elapsed time to run the experiments

        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;

        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        System.out.println("Finished USE measures experiments");
        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        
        /**
         * ***********************************************
         * ***********************************************
         * 
         * EXPERIMENT 8. Starting Flair experiments
         * 
         * ***********************************************
         * ***********************************************
         */
        
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        System.out.println("Starting Flair experiments");
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        
        // Compute all preprocessing combinations
        
        totalCombinations += executeFlairMeasures(getStopWordsPreprocessingConfigurations(false, NERType.None), "FlairStopWords");
        totalCombinations += executeFlairMeasures(getCharFilteringPreprocessingConfigurations(false, NERType.None), "FlairCharFiltering");
        totalCombinations += executeFlairMeasures(getTokenizerPreprocessingConfigurations(NERType.None), "FlairTokenizer");
        totalCombinations += executeFlairMeasures(getLowerCasePreprocessingConfigurations(false, NERType.None), "FlairLC");
        
        // We measure the elapsed time to run the experiments

        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;

        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        System.out.println("Finished Flair measures experiments");
        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        
        // Return the result
        
        return (totalCombinations);
    }
    
    /**
     * Test all the string measures.
     * 
     *  * Preprocessing configured as Blagec2019.
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static int executeStringMeasures(
        ArrayList<IWordProcessing> wordProcessingCombinations, 
        ArrayList<IWordProcessing> wordProcessingCombinationsWithCtakes,
        String outputFileNames) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        // We define the methods to be evaluated

        ArrayList<StringBasedSentenceSimilarityMethod> methods = new ArrayList<>();
        
        methods.add(StringBasedSentenceSimilarityMethod.Jaccard);
        methods.add(StringBasedSentenceSimilarityMethod.BlockDistance);
        methods.add(StringBasedSentenceSimilarityMethod.Levenshtein);
        methods.add(StringBasedSentenceSimilarityMethod.OverlapCoefficient);
        methods.add(StringBasedSentenceSimilarityMethod.Qgram);
        
        // We calculate the total of combinations
        
        totalCombinations = wordProcessingCombinations.size() * methods.size();
        
        System.out.println("Calculating the combination of " + totalCombinations + " word processing configurations");
        
        // We iterate the methods and create the measures
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        // We iterate word processing combinations

        for(StringBasedSentenceSimilarityMethod method: methods)
        {
            for(IWordProcessing wordProcessing : wordProcessingCombinations)
            {
                // We create a copy of the wordProcessing object for avoid multithreading errors
                
                ISentenceSimilarityMeasure measure = 
                        SentenceSimilarityFactory.getStringBasedMeasure(
                            method.name() + "_" + wordProcessing.getLabel(),
                            method, 
                            wordProcessing);
                measuresLst.add(measure);
            }
        }
        
        // We also tests all the LiMixed-based pre-processing combinations
        
        // Initialize the string measure with its pre-processing configuration
        
        IWordProcessing bestStringWordProcessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.WhiteSpace, 
                        true, NERType.None,
                        CharFilteringType.BIOSSES);

        ISentenceSimilarityMeasure stringMeasure = SentenceSimilarityFactory.getStringBasedMeasure(
                            "BlockDistance_" + bestStringWordProcessing.getLabel(),
                            StringBasedSentenceSimilarityMethod.BlockDistance, 
                            bestStringWordProcessing);
        
        // We add all the different configurations for the measure
        
        for(IWordProcessing wordProcessing : wordProcessingCombinationsWithCtakes)
        {
            // We create a copy of the wordProcessing object
            
            measuresLst.add(SentenceSimilarityFactory.getLiBlockMeasure(
                 "LiBlock_" + wordProcessing.getLabel(), 
                 wordProcessing, stringMeasure,
                 0.5, ComMixedVectorsMeasureType.NoneOntology));
        }
        
        // We execute the experiments
        
        executeExperiments(measuresLst, outputFileNames);

        // Return the result
        
        return (totalCombinations);
    }
    
    /**
     * Test all the string measures.
     * 
     *  * Preprocessing configured as Blagec2019.
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static int executeOurWEMeasures(
            ArrayList<IWordProcessing> wordProcessingCombinations, 
            String outputFileNames) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        // List with all the pooling methods for WE-based methods
        
        ArrayList<SWEMpoolingMethod> poolingMethods = new ArrayList<>();
        poolingMethods.add(SWEMpoolingMethod.Max);
        poolingMethods.add(SWEMpoolingMethod.Average);
        poolingMethods.add(SWEMpoolingMethod.Min);
        poolingMethods.add(SWEMpoolingMethod.Sum);

        // We define the models to be evaluated
        
        ArrayList<String> modelsFastextVecBased = new ArrayList<>();
        
        modelsFastextVecBased.add("bioc_skipgram_defaultchar.vec");
        
        // We define the base model dir
        
        String strBaseModelDir = m_strDataDirectory + "WordEmbeddings/";
        
        totalCombinations = wordProcessingCombinations.size()
                        *modelsFastextVecBased.size() * poolingMethods.size();
        
        System.out.println("Calculating the combination of " 
                + totalCombinations + " configurations");

        // We create the measures list 
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        // We iterate the methods and create the measures
        
        for(String model : modelsFastextVecBased)
        {
            // We iterate word processing combinations
            
            for(IWordProcessing wordProcessing : wordProcessingCombinations)
            {
                // We iterate the pooling methods 
                
                for(SWEMpoolingMethod pooling : poolingMethods)
                {
                    // Get the model name without file extensions

                    String label = model.replace(".vec", "").replace(".bin", "");

                    // We create the measure

                    ISentenceSimilarityMeasure measure = 
                        SentenceSimilarityFactory.getSWEMMeasure(
                                label + "__" + wordProcessing.getLabel() + "_" + pooling.name(),
                                pooling,
                                WordEmbeddingFileType.FastTextVecWordEmbedding, 
                                wordProcessing,
                                strBaseModelDir + model);

                    measuresLst.add(measure);
                }
            }
        }
        
        // We execute the experiments
        
        executeExperiments(measuresLst, outputFileNames);
        
        // Return the result
        
        return (totalCombinations);
    }
    
    /**
     * Test all the string measures.
     * 
     *  * Preprocessing configured as Blagec2019.
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static int executeSWEMMeasures(
            ArrayList<String> modelsFastextVecBased,
            ArrayList<IWordProcessing> wordProcessingCombinations, 
            String outputFileNames,
            WordEmbeddingFileType wordEmbeddingFileType,
            SWEMpoolingMethod   pooling) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;

        // We define the base model dir
        
        String strBaseModelDir = m_strDataDirectory + "WordEmbeddings/";
        
        totalCombinations = wordProcessingCombinations.size()
                        *modelsFastextVecBased.size();
        
        System.out.println("Calculating the combination of " 
                + totalCombinations + " configurations");

        // We create the measures list 
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        // We iterate the methods and create the measures
        
        for(String model : modelsFastextVecBased)
        {
            // We iterate word processing combinations
            
            for(IWordProcessing wordProcessing : wordProcessingCombinations)
            {
                // Get the model name without file extensions
        
                String label = model.replace(".vec", "").replace(".bin", "").replace(".txt", "");

                // We create the measure

                ISentenceSimilarityMeasure measure = 
                    SentenceSimilarityFactory.getSWEMMeasure(
                            label + "__" + wordProcessing.getLabel() + "_" + pooling.name(),
                            pooling,
                            wordEmbeddingFileType, 
                            wordProcessing,
                            strBaseModelDir + model);

                measuresLst.add(measure);
            }
        }
        
        // We execute the experiments
        
        executeExperiments(measuresLst, outputFileNames);
        
        // Return the result
        
        return (totalCombinations);
    }
    
    /**
     * Test all the Sent2Vec measures.
     * 
     * @return
     */
    
    private static int executeSent2VecMeasures(
            ArrayList<IWordProcessing> wordProcessingCombinations, 
            String outputFileNames) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        System.out.println("Calculating the combination of " 
                + totalCombinations + " configurations");

        // We create the measures list 
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        // We load and register a BERT measure from the XML file 

        String strSent2vecModelDir = m_strDataDirectory + "SentenceEmbeddings/";
        String strSent2vecModelFile = "BioSentVec_PubMed_MIMICIII-bigram_d700.bin";
        String strPythonScriptsDirectory = "../Sent2vecExperiments/";
        String strPythonVirtualEnvironmentDir = "python3";
        String strPythonScript = "extractSent2vecvectors.py";
        
        // We iterate word processing combinations
            
        for(IWordProcessing wordProcessing : wordProcessingCombinations)
        {
            ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getSent2vecMethodMeasure(
                        "Sent2vec_" + wordProcessing.getLabel(), 
                        SentenceEmbeddingMethod.ParagraphVector,
                        wordProcessing, 
                        strSent2vecModelDir + strSent2vecModelFile, 
                        strPythonScriptsDirectory + strPythonScript,
                        strPythonVirtualEnvironmentDir,
                        strPythonScriptsDirectory);
            
            // We add the measure
            
            measuresLst.add(measure);
            
            // We update the combinations
            
            totalCombinations++;
        }  
        
        // We execute the experiments
        
        executeExperiments(measuresLst, outputFileNames);
        
        // Return the result
        
        return (totalCombinations);
    }
    
    /**
     * Test all the USE measures.
     * 
     * @return
     */
    
    private static int executeUSEMeasures(
            ArrayList<IWordProcessing> wordProcessingCombinations, 
            String outputFileNames) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        System.out.println("Calculating the combination of " 
                + totalCombinations + " configurations");

        // We create the measures list 
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        // We load and register a USE measure from the XML file 

        String strUSEModelURL = "https://tfhub.dev/google/universal-sentence-encoder/4";
        String strPythonScriptsDirectory = "../UniversalSentenceEncoderExperiments/";
        String strPythonVirtualEnvironmentDir = "python3";
        String strPythonScript = "extractUniversalSentenceEncoderVectors.py";

        // We iterate word processing combinations
            
        for(IWordProcessing wordProcessing : wordProcessingCombinations)
        {
            ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getUSESentenceEmbeddingMethod(
                    "USE_" + wordProcessing.getLabel(), 
                    SentenceEmbeddingMethod.USEModel,
                    wordProcessing, 
                    strUSEModelURL, 
                    strPythonScriptsDirectory + strPythonScript,
                    strPythonVirtualEnvironmentDir,
                    strPythonScriptsDirectory);
            
            // We add the measure
            
            measuresLst.add(measure);
            
            // We update the combinations
            
            totalCombinations++;
        }  
        
        // We execute the experiments
        
        executeExperiments(measuresLst, outputFileNames);
        
        // Return the result
        
        return (totalCombinations);
    }
    
    /**
     * Test all the Flair measures.
     * 
     * @return
     */
    
    private static int executeFlairMeasures(
            ArrayList<IWordProcessing> wordProcessingCombinations, 
            String outputFileNames) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        System.out.println("Calculating the combination of " 
                + totalCombinations + " configurations");

        // We create the measures list 
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();

        // We load and register a Flair measure from the XML file 

        String strFlairModelURL = m_strDataDirectory + "/FlairEmbeddings/embeddings/pubmed-backward.pt," + m_strDataDirectory + "/FlairEmbeddings/embeddings/pubmed-forward.pt";
        String strPythonScriptsDirectoryFlair = "../FlairEmbeddings/";
        String strPythonVirtualEnvironmentDirFlair = "python3";
        String strPythonScriptFlair = "extractFlairVectors.py";

        // We iterate word processing combinations
            
        for(IWordProcessing wordProcessing : wordProcessingCombinations)
        {
            ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getFlairEmbeddingMethod(
                            "Flair_" + wordProcessing.getLabel(),  
                            SentenceEmbeddingMethod.Flair,
                            wordProcessing, 
                            strFlairModelURL, 
                            strPythonScriptsDirectoryFlair + strPythonScriptFlair,
                            strPythonVirtualEnvironmentDirFlair,
                            strPythonScriptsDirectoryFlair);
            
            // We add the measure
            
            measuresLst.add(measure);
            
            // We update the combinations
            
            totalCombinations++;
        }  
        
        // We execute the experiments
        
        executeExperiments(measuresLst, outputFileNames);
        
        // Return the result
        
        return (totalCombinations);
    }
    
    /**
     * Test all the string measures.
     * 
     *  * Preprocessing configured as Blagec2019.
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static int executeWBSMMeasures(
        ArrayList<IWordProcessing> wordProcessingCombinations, 
        String outputFileNames) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        // We define the word similarity measures to be compared
        
        ArrayList<SimilarityMeasureType> wordMeasures = new ArrayList<>();
        wordMeasures.add(SimilarityMeasureType.AncSPLWeightedJiangConrath);
        wordMeasures.add(SimilarityMeasureType.AncSPLRada);
        wordMeasures.add(SimilarityMeasureType.AncSPLCosineNormWeightedJiangConrath);
        wordMeasures.add(SimilarityMeasureType.AncSPLCaiStrategy1);
        wordMeasures.add(SimilarityMeasureType.JiangConrath);
        
        // We get the intrinsic IC model if anyone has been defined

        IntrinsicICModelType icModelType = IntrinsicICModelType.Seco;
        
        // We calculate the total of combinations
        
        totalCombinations = wordProcessingCombinations.size() * wordMeasures.size();
        
        System.out.println("Calculating the combination of " + totalCombinations + " word processing configurations");
        
        // We iterate the methods and create the measures
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        // We iterate word processing combinations

        for(SimilarityMeasureType wordMeasure : wordMeasures)
        {
            for(IWordProcessing wordProcessing : wordProcessingCombinations)
            {
                // We create the measure

                ISentenceSimilarityMeasure measure = 
                        SentenceSimilarityFactory.getWBSMMeasure(
                                "WBSM_" + wordMeasure.name() + "_" + wordProcessing.getLabel(),
                                wordProcessing,
                                m_WordNetDbSingleton, 
                                m_WordNetTaxonomySingleton, 
                                wordMeasure, 
                                icModelType);

                measuresLst.add(measure);
            }
        }
        
        // We execute the experiments
        
        executeExperiments(measuresLst, outputFileNames);

        // Return the result
        
        return (totalCombinations);
    }
    
    /**
     * Test all the UBSM measures.
     * 
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static int executeUBSMMeasures(
        ArrayList<IWordProcessing> wordProcessingCombinations, 
        String outputFileNames,
        NERType nerType) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        // We define the word similarity measures to be compared
        
        ArrayList<SimilarityMeasureType> wordMeasures = new ArrayList<>();
        wordMeasures.add(SimilarityMeasureType.AncSPLWeightedJiangConrath);
        wordMeasures.add(SimilarityMeasureType.AncSPLRada);
        wordMeasures.add(SimilarityMeasureType.AncSPLCosineNormWeightedJiangConrath);
        wordMeasures.add(SimilarityMeasureType.AncSPLCaiStrategy1);
        wordMeasures.add(SimilarityMeasureType.JiangConrath);
        
        // We get the intrinsic IC model if anyone has been defined

        IntrinsicICModelType icModelType = IntrinsicICModelType.Seco;
        
        // We calculate the total of combinations
        
        totalCombinations = wordProcessingCombinations.size() * wordMeasures.size();
        
        System.out.println("Calculating the combination of " + totalCombinations + " word processing configurations");
        
        // We iterate the methods and create the measures
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        // We iterate word processing combinations

        for(SimilarityMeasureType wordMeasure : wordMeasures)
        {
            for(IWordProcessing wordProcessing : wordProcessingCombinations)
            {
                // We initialize the measure
                
                ISentenceSimilarityMeasure measure = null;
                
                // We create the measure depending on the NER type
                
                switch (nerType) 
                {
                    case Ctakes:
                    case MetamapSNOMEDCT:
                        
                        // We create the measure
                        
                        measure =
                                SentenceSimilarityFactory.getUBSMMeasureSnomed(
                                        "UBSM_" + wordMeasure.name() + "_" + wordProcessing.getLabel(),
                                        wordProcessing,
                                        m_SnomedOntology, m_vertexesSnomed, m_taxonomySnomed,
                                        wordMeasure,
                                        icModelType);
                        break;
                        
                    case MetamapLite:
                        
                        // We create the measure
                        
                        measure =
                                SentenceSimilarityFactory.getUBSMMeasureMeSH(
                                        "UBSM_" + wordMeasure.name() + "_" + wordProcessing.getLabel(),
                                        wordProcessing,
                                        m_MeshOntology,
                                        wordMeasure,
                                        icModelType);
                        break;
                        
                    default:
                        
                        // Throw exception
                        
                        throw new Exception("Error: NER type selected has not been implemented for the UBSM measure");
                }

                measuresLst.add(measure);
            }
        }
        
        // We execute the experiments
        
        executeExperiments(measuresLst, outputFileNames);

        // Return the result
        
        return (totalCombinations);
    }
    
    /**
     * Test all the UBSM measures.
     * 
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static int executeCOMMeasures(
        String                          outputFileNames) 
            throws IOException, InterruptedException, Exception
    {
       // Initialize the result
        
        int totalCombinations = 0;
        
        // We iterate the methods and create the measures
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        /**
         * Best COM configuation
         */
        
        // We define the best UBSM and WBSM pre-processing methods

        IWordProcessing bestUBSMWordProcessingCOM = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.Ctakes,
                CharFilteringType.BIOSSES);

        IWordProcessing bestWBSMWordProcessingCOM = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.StanfordCoreNLPv4_2_0, 
                        true, NERType.None,
                        CharFilteringType.BIOSSES);

        // We get the intrinsic IC model if anyone has been defined

        IntrinsicICModelType icModelTypeCOM = IntrinsicICModelType.Seco;

        // We calculate the best measure combination

        ISentenceSimilarityMeasure[] measures = new ISentenceSimilarityMeasure[2];    

        // We initialize WBSM and UBSM methods

        ISentenceSimilarityMeasure measureWBSM = 
                SentenceSimilarityFactory.getWBSMMeasure(
                        "bestWBSM_" + SimilarityMeasureType.AncSPLRada.name(),
                        bestWBSMWordProcessingCOM,
                        m_WordNetDbSingleton, 
                        m_WordNetTaxonomySingleton, 
                        SimilarityMeasureType.AncSPLRada, 
                        icModelTypeCOM);

        ISentenceSimilarityMeasure measureUBSM =
            SentenceSimilarityFactory.getUBSMMeasureSnomed(
                    "bestUBSM_" + SimilarityMeasureType.AncSPLRada.name(),
                    bestUBSMWordProcessingCOM,
                    m_SnomedOntology, m_vertexesSnomed, m_taxonomySnomed,
                    SimilarityMeasureType.AncSPLRada,
                    icModelTypeCOM);

        // We add the measures to a list

        measures[0] = measureWBSM;
        measures[1] = measureUBSM;

        // We create the COM measure

        ICombinedSentenceSimilarityMeasure measure = SentenceSimilarityFactory.getCOMMeasure(
            "bestCOM_" + measures[0].getLabel() + "_" + measures[1].getLabel(),
            0.5,
            measures);

        // We add the measure

        measuresLst.add(measure);

        // Update the total of combinations

        totalCombinations++;
        
        /**
         * Worst COM configuation
         */
        
        // We define the worst UBSM and WBSM pre-processing methods

        IWordProcessing worstUBSMWordProcessingCOM = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "noneStopWords.txt", 
                TokenizerType.WhiteSpace, 
                true, NERType.MetamapSNOMEDCT,
                CharFilteringType.None);

        IWordProcessing worstWBSMWordProcessingCOM = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "noneStopWords.txt", 
                        TokenizerType.WhiteSpace, 
                        true, NERType.None,
                        CharFilteringType.None);

        // We calculate the worst measure combination

        ISentenceSimilarityMeasure[] worstMeasures = new ISentenceSimilarityMeasure[2];    

        // We initialize WBSM and UBSM methods

        ISentenceSimilarityMeasure worstMeasureWBSM = 
                SentenceSimilarityFactory.getWBSMMeasure(
                        "worstWBSM_" + SimilarityMeasureType.AncSPLRada.name(),
                        worstWBSMWordProcessingCOM,
                        m_WordNetDbSingleton, 
                        m_WordNetTaxonomySingleton, 
                        SimilarityMeasureType.AncSPLRada, 
                        icModelTypeCOM);

        ISentenceSimilarityMeasure worstMeasureUBSM =
            SentenceSimilarityFactory.getUBSMMeasureSnomed(
                    "worstUBSM_" + SimilarityMeasureType.AncSPLRada.name(),
                    worstUBSMWordProcessingCOM,
                    m_SnomedOntology, m_vertexesSnomed, m_taxonomySnomed,
                    SimilarityMeasureType.AncSPLRada,
                    icModelTypeCOM);

        // We add the measures to a list

        worstMeasures[0] = worstMeasureWBSM;
        worstMeasures[1] = worstMeasureUBSM;

        // We create the COM measure

        ICombinedSentenceSimilarityMeasure worstMeasure = SentenceSimilarityFactory.getCOMMeasure(
            "worstCOM_" + worstMeasures[0].getLabel() + "_" + worstMeasures[1].getLabel(),
            0.5,
            worstMeasures);

        // We add the measure

        measuresLst.add(worstMeasure);

        // Update the total of combinations

        totalCombinations++;
        
        // We execute the experiments
        
        executeExperiments(measuresLst, outputFileNames);
        
        // Return the result 
        
        return (totalCombinations);
    }
    
    /**
     * Test all the string measures.
     * 
     *  * Preprocessing configured as Blagec2019.
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static int executeBERTMeasures(
            String[][] modelPaths,
            int total_models,
            ArrayList<IWordProcessing> wordProcessingCombinations,
            String outputFileNames) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        // We iterate the methods and create the measures
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        for(int i=0; i<total_models; i++)
        {
            // We iterate word processing combinations
            
            for(IWordProcessing wordProcessing : wordProcessingCombinations)
            {
                // We set the BERT model in the preprocessing
                
                wordProcessing.setBERTModel(modelPaths[i][0]);
               
                // Select the python library
                
                MLPythonLibrary mllibrary = MLPythonLibrary.Tensorflow;
                if("Pytorch".equals(modelPaths[i][6]))
                    mllibrary = MLPythonLibrary.Pytorch;
                
                // Create the measure
                
                ISentenceSimilarityMeasure measure = null;
                
                String[] poolingLayers = new String[1];
                poolingLayers[0] = "-2";
                
                // The constructor for Tensorflow differs from Pytorch
                
                if(mllibrary == MLPythonLibrary.Tensorflow)
                {
                    measure = SentenceSimilarityFactory.getBERTTensorflowSentenceEmbeddingMethod(
                            wordProcessing.getLabel(), 
                            SentenceEmbeddingMethod.BERTEmbeddingModel,
                            mllibrary,
                            wordProcessing, 
                            modelPaths[i][5], 
                            modelPaths[i][7],
                            modelPaths[i][8],
                            modelPaths[i][1], 
                            modelPaths[i][2], 
                            modelPaths[i][3],
                            BERTpoolingMethod.REDUCE_MEAN, 
                            poolingLayers);
                }
                else
                {
                    measure = 
                        SentenceSimilarityFactory.getBERTPytorchSentenceEmbeddingMethod(
                            modelPaths[i][5] + wordProcessing.getLabel(), 
                            SentenceEmbeddingMethod.BERTEmbeddingModel,
                            mllibrary,
                            wordProcessing, 
                            modelPaths[i][4], 
                            modelPaths[i][1], 
                            modelPaths[i][2], 
                            modelPaths[i][3]);
                }
                
                // Add the measure to the list
                
                measuresLst.add(measure);
                
                // We update the combinations
                
                totalCombinations++;
            }
        }
        
        // We execute the experiments
        
        executeExperiments(measuresLst, outputFileNames);

        // Return the result
        
        return (totalCombinations);
    }
    
    /**
     * We execute the experiments and write the output file
     * 
     * @return 
     */
    private static void executeExperiments(
            ArrayList<ISentenceSimilarityMeasure> measuresLst,
            String outputFileNames
    ) throws Exception
    {
        // We create the vector to return the collection of sentence similarity measures
        
        ISentenceSimilarityMeasure[] measures = new ISentenceSimilarityMeasure[measuresLst.size()];
        
        // We copy the measures to the vector and release the temporary list
        
        measuresLst.toArray(measures);
        measuresLst.clear();
        
        // We read the configuration of the experiment
        
        String strOutputFileNameBIOSSES = m_outputFilesDirPath + "raw_similarity_BIOSSES_" + outputFileNames + ".csv";
        String strOutputFileNameMedSTS = m_outputFilesDirPath + "raw_similarity_MedSTSFull_" + outputFileNames + ".csv";
        String strOutputFileNameCTR = m_outputFilesDirPath + "raw_similarity_CTR_" + outputFileNames + ".csv";
        
        // We create the benchmarks for all measuers and dataset
        
        ISentenceSimilarityBenchmark benchmarkBIOSSES = SentenceSimBenchmarkFactory.getSingleDatasetBenchmark(
                                                    measures, m_strDatasetDir,
                                                    m_strDatasetFileNameBIOSSES, strOutputFileNameBIOSSES);
        
        benchmarkBIOSSES.evaluateBenchmark(true);
        
        ISentenceSimilarityBenchmark benchmarkMedSTS = SentenceSimBenchmarkFactory.getSingleDatasetBenchmark(
                                                    measures, m_strDatasetDir,
                                                    m_strDatasetFileNameMedSTS, strOutputFileNameMedSTS);
        
        benchmarkMedSTS.evaluateBenchmark(true);
        
        ISentenceSimilarityBenchmark benchmarkCTR = SentenceSimBenchmarkFactory.getSingleDatasetBenchmark(
                                                    measures, m_strDatasetDir,
                                                    m_strDatasetFileNameCTR, strOutputFileNameCTR);
        
        benchmarkCTR.evaluateBenchmark(true);
    }
    
    /**
     * Load ontologies
     */
    private static void loadOntologies(boolean useWordNetCache) throws Exception
    {
//        // We create the singleton instance of the WordNet database and taxonomy
//
//        if (m_WordNetDbSingleton == null || useWordNetCache == false)
//        {
//            // We load the singleton instance of WordNet-related objects. It is done to
//            // avoid the memory cost of multiple instances of WordNet when multiple
//            // instances of the WBSM measure are created.
//            
//            m_WordNetDbSingleton = WordNetFactory.loadWordNetDatabase(m_strWordNetDatasetsDir, m_strWordNetDBDir);    
//            m_WordNetTaxonomySingleton = WordNetFactory.buildTaxonomy(m_WordNetDbSingleton);  
//
//            // We pre-process the taxonomy to compute all the parameters
//            // used by the intrinsic IC-computation methods
//
//            m_WordNetTaxonomySingleton.computesCachedAttributes();
//        }
//        
//        // We create the singleton instance of the UMLS database and taxonomy
//
//        if (m_SnomedOntology == null)
//        {
//            // We load the SNOMED ontology and get the vertex list of its taxonomy
//
//            m_SnomedOntology = SnomedCtFactory.loadSnomedDatabase(m_strSnomedDir,
//                                    m_strSnomedConceptFilename,
//                                    m_strSnomedRelationshipsFilename,
//                                    m_strSnomedDescriptionFilename,
//                                    m_strUMLSdir, m_strUmlsCuiMappingFilename);
//
//            m_taxonomySnomed = m_SnomedOntology.getTaxonomy();
//            m_vertexesSnomed = m_taxonomySnomed.getVertexes();
//        }
//        
//        // We create the singleton instance of the UMLS database and taxonomy
//
//        if (m_MeshOntology == null)
//        {
//            // We load the MeSH ontology and get the vertex list of its taxonomy
//
//            m_MeshOntology = MeSHFactory.loadMeSHOntology(
//                                    m_strMeSHdir + "/" + m_strMeSHdescriptorFilename,
//                                    m_strUMLSdir + "/" + m_strUmlsCuiMappingFilename);
//
//            m_taxonomyMesh = m_MeshOntology.getTaxonomy();
//        }
    }
    
            
    /**
     * Get all the word pre-processing configurations for non-bert models
     */
    
    private static ArrayList<IWordProcessing> getNERPreprocessingConfigurations(
            NERType nerType) throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();

        // We add the wordprocessing method

         wordProcessingCombinations.add(PreprocessingFactory.getWordProcessing(
                    m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt", 
                    TokenizerType.WhiteSpace, 
                    true, nerType,
                    CharFilteringType.Default));
        
        // Return the result
        
        return (wordProcessingCombinations);
    }
    
    /**
     * Get all the word preprocessing configurations for non-bert models
     */
    
    private static ArrayList<IWordProcessing> getAllPreprocessingConfigurations(NERType ner) throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();

        
        ArrayList<String> stopWordsCombs = new ArrayList<>();
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt");
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt");
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt");
        
        ArrayList<TokenizerType> tokenizerTypeCombs = new ArrayList<>();
        tokenizerTypeCombs.add(TokenizerType.WhiteSpace);
        tokenizerTypeCombs.add(TokenizerType.BioCNLPTokenizer);
        tokenizerTypeCombs.add(TokenizerType.StanfordCoreNLPv4_2_0);
        
        ArrayList<CharFilteringType> charFilteringTypeCombs = new ArrayList<>();
        charFilteringTypeCombs.add(CharFilteringType.None);
        charFilteringTypeCombs.add(CharFilteringType.BIOSSES);
        charFilteringTypeCombs.add(CharFilteringType.Blagec2019);
        charFilteringTypeCombs.add(CharFilteringType.Default);
        
        ArrayList<Boolean> lowerCasingCombs = new ArrayList<>();
        lowerCasingCombs.add(true);
        lowerCasingCombs.add(false);
        
        // We iterate all the combinations
        
        for(String stopword : stopWordsCombs)
        {
            for(TokenizerType tokenizer : tokenizerTypeCombs)
            {
                for(CharFilteringType charfiltering : charFilteringTypeCombs)
                {
                    for(Boolean lw : lowerCasingCombs)
                    {
                        wordProcessingCombinations.add( 
                                PreprocessingFactory.getWordProcessing(
                                stopword, 
                                tokenizer,
                                lw, ner,
                                charfiltering));
                    }
                }
            }
        }
        
        // Return the result
        
        return (wordProcessingCombinations);
    }
    
     /**
     * Get all the word preprocessing configurations for non-bert models
     */
    
    private static ArrayList<IWordProcessing> getStopWordsPreprocessingConfigurations(
            boolean isBERT,
            NERType nerType) throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();

        
        ArrayList<String> stopWordsCombs = new ArrayList<>();
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt");
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt");
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt");
        
        String strPythonScriptsDirectory = "../BERTExperiments/";
        String strPythonVirtualEnvironmentDir = "python3";
        String strPythonScript = "../BERTExperiments/WordPieceTokenization.py";
        String strBERTPretrainedModelFilename = "";

        // We iterate all the combinations
        
        for(String stopword : stopWordsCombs)
        {
            // Add the word processing combination

            if(!isBERT)
                wordProcessingCombinations.add( 
                    PreprocessingFactory.getWordProcessing(
                    stopword, 
                    TokenizerType.WhiteSpace,
                    true, nerType,
                    CharFilteringType.Default));
            else
                wordProcessingCombinations.add( 
                    PreprocessingFactory.getWordProcessing(
                        stopword,
                        TokenizerType.WordPieceTokenizer,
                        true,
                        nerType,
                        CharFilteringType.Default,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename));  
        }
        
        // Return the result
        
        return (wordProcessingCombinations);
    }
    
    /**
     * Get all the word preprocessing configurations for non-bert models
     */
    
    private static ArrayList<IWordProcessing> getCharFilteringPreprocessingConfigurations(
            boolean isBERT,
            NERType nerType) throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();

        ArrayList<CharFilteringType> charFilteringTypeCombs = new ArrayList<>();
        charFilteringTypeCombs.add(CharFilteringType.None);
        charFilteringTypeCombs.add(CharFilteringType.BIOSSES);
        charFilteringTypeCombs.add(CharFilteringType.Blagec2019);
        charFilteringTypeCombs.add(CharFilteringType.Default);

        String strPythonScriptsDirectory = "../BERTExperiments/";
        String strPythonVirtualEnvironmentDir = "python3";
        String strPythonScript = "../BERTExperiments/WordPieceTokenization.py";
        String strBERTPretrainedModelFilename = "";

        // We iterate all the combinations
        
        for(CharFilteringType charfiltering : charFilteringTypeCombs)
        {
            // Add the word processing combination

            if(!isBERT)
                wordProcessingCombinations.add( 
                    PreprocessingFactory.getWordProcessing(
                    m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt", 
                    TokenizerType.WhiteSpace,
                    true, 
                    nerType,
                    charfiltering));
            else
                wordProcessingCombinations.add( 
                    PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        nerType,
                        charfiltering,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename));
        }
            
        // Return the result
        
        return (wordProcessingCombinations);
    }
    
    /**
     * Get all the word preprocessing configurations for non-bert models
     */
    
    private static ArrayList<IWordProcessing> getTokenizerPreprocessingConfigurations(
            NERType nerType) throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();
        
        ArrayList<TokenizerType> tokenizerTypeCombs = new ArrayList<>();
        tokenizerTypeCombs.add(TokenizerType.WhiteSpace);
        tokenizerTypeCombs.add(TokenizerType.BioCNLPTokenizer);
        tokenizerTypeCombs.add(TokenizerType.StanfordCoreNLPv4_2_0);
 
        // We iterate all the combinations
        
        for(TokenizerType tokenizer : tokenizerTypeCombs)
        {
            // We add the preprocessing configurations
            
            wordProcessingCombinations.add( 
                    PreprocessingFactory.getWordProcessing(
                    m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt", 
                    tokenizer,
                    true, 
                    nerType,
                    CharFilteringType.Default));
        }
        
        // Return the result
        
        return (wordProcessingCombinations);
    }
    
    /**
     * Get all the word preprocessing configurations for non-bert models
     */
    
    private static ArrayList<IWordProcessing> getLowerCasePreprocessingConfigurations(
            boolean isBERT,
            NERType nerType) throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();
        
        ArrayList<Boolean> lowerCasingCombs = new ArrayList<>();
        lowerCasingCombs.add(true);
        lowerCasingCombs.add(false);
        
        String strPythonScriptsDirectory = "../BERTExperiments/";
        String strPythonVirtualEnvironmentDir = "python3";
        String strPythonScript = "../BERTExperiments/WordPieceTokenization.py";
        String strBERTPretrainedModelFilename = "";

        // We iterate all the combinations

        for(Boolean lw : lowerCasingCombs)
        {
            // Add the word processing combination

            if(!isBERT)
                wordProcessingCombinations.add( 
                    PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt", 
                        TokenizerType.WhiteSpace,
                        lw, nerType,
                        CharFilteringType.Default));
            else
                wordProcessingCombinations.add( 
                    PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        lw,
                        nerType,
                        CharFilteringType.Default,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename));
        }
             
        // Return the result
        
        return (wordProcessingCombinations);
    }
    
    /**
     * We get the list of available BERT models for the evaluation
     * @param total_models
     * @return 
     */
    
    private static String[][] getBERTModelPathList(int total_models)
    {
        // We define the models to be evaluated

        String[][] modelPaths = new String[total_models][9];
        
        /**
         * The structure for each model paths is:
         * 
         * strBertDir + strBERTPretrainedModelFilename, 
         * strPythonScriptsDirectory, 
         * strPythonVirtualEnvironmentDir, 
         * strPythonScriptsDirectory + strPythonScript
         */
        
        modelPaths[0][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/oubiobert-base-uncased";
        modelPaths[0][1] = "../BERTExperiments/";
        modelPaths[0][2] = "python3";
        modelPaths[0][3] = "../BERTExperiments/PytorchExperiments/extractBERTvectors.py";
        modelPaths[0][4] = "seiya/oubiobert-base-uncased";
        modelPaths[0][5] = "oubiobert-base-uncased";
        modelPaths[0][6] = "Pytorch";
        modelPaths[0][7] = "";
        modelPaths[0][8] = "";
        
        modelPaths[1][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/scibert_scivocab_uncased";
        modelPaths[1][1] = "../BERTExperiments/";
        modelPaths[1][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[1][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[1][4] = "allenai/scibert_scivocab_uncased";
        modelPaths[1][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/scibert_scivocab_uncased";
        modelPaths[1][6] = "Tensorflow";
        modelPaths[1][7] = "";
        modelPaths[1][8] = "";
        
        modelPaths[2][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/PubMedBERT-base-uncased-abstract";
        modelPaths[2][1] = "../BERTExperiments/";
        modelPaths[2][2] = "python3";
        modelPaths[2][3] = "../BERTExperiments/PytorchExperiments/extractBERTvectors.py";
        modelPaths[2][4] = "microsoft/BiomedNLP-PubMedBERT-base-uncased-abstract";
        modelPaths[2][5] = "BiomedNLP-PubMedBERT-base-uncased-abstract";
        modelPaths[2][6] = "Pytorch";
        modelPaths[2][7] = "";
        modelPaths[2][8] = "";
        
        modelPaths[3][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/PubMedBERT-base-uncased-abstract-fulltext";
        modelPaths[3][1] = "../BERTExperiments/";
        modelPaths[3][2] = "python3";
        modelPaths[3][3] = "../BERTExperiments/PytorchExperiments/extractBERTvectors.py";
        modelPaths[3][4] = "microsoft/BiomedNLP-PubMedBERT-base-uncased-abstract-fulltext";
        modelPaths[3][5] = "BiomedNLP-PubMedBERT-base-uncased-abstract-fulltext";
        modelPaths[3][6] = "Pytorch";
        modelPaths[3][7] = "";
        modelPaths[3][8] = "";
        
        modelPaths[4][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_v1.0_pubmed";
        modelPaths[4][1] = "../BERTExperiments/";
        modelPaths[4][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[4][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[4][4] = "";
        modelPaths[4][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_v1.0_pubmed";
        modelPaths[4][6] = "Tensorflow";
        modelPaths[4][7] = "";
        modelPaths[4][8] = "";
        
        modelPaths[5][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_v1.0_pmc";
        modelPaths[5][1] = "../BERTExperiments/";
        modelPaths[5][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[5][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[5][4] = "";
        modelPaths[5][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_v1.0_pmc";
        modelPaths[5][6] = "Tensorflow";
        modelPaths[5][7] = "";
        modelPaths[5][8] = "";
        
        modelPaths[6][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_v1.0_pubmed_pmc";
        modelPaths[6][1] = "../BERTExperiments/";
        modelPaths[6][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[6][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[6][4] = "";
        modelPaths[6][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_v1.0_pubmed_pmc";
        modelPaths[6][6] = "Tensorflow";
        modelPaths[6][7] = "";
        modelPaths[6][8] = "";
        
        modelPaths[7][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/NCBI_BERT_pubmed_mimic_uncased_L-12_H-768_A-12";
        modelPaths[7][1] = "../BERTExperiments/";
        modelPaths[7][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[7][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[7][4] = "";
        modelPaths[7][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/NCBI_BERT_pubmed_mimic_uncased_L-12_H-768_A-12";
        modelPaths[7][6] = "Tensorflow";
        modelPaths[7][7] = "";
        modelPaths[7][8] = "";
        
        modelPaths[8][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/NCBI_BERT_pubmed_mimic_uncased_L-24_H-1024_A-16";
        modelPaths[8][1] = "../BERTExperiments/";
        modelPaths[8][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[8][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[8][4] = "";
        modelPaths[8][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/NCBI_BERT_pubmed_mimic_uncased_L-24_H-1024_A-16";
        modelPaths[8][6] = "Tensorflow";
        modelPaths[8][7] = "";
        modelPaths[8][8] = "";
        
        modelPaths[9][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/NCBI_BERT_pubmed_uncased_L-12_H-768_A-12";
        modelPaths[9][1] = "../BERTExperiments/";
        modelPaths[9][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[9][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[9][4] = "";
        modelPaths[9][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/NCBI_BERT_pubmed_uncased_L-12_H-768_A-12";
        modelPaths[9][6] = "Tensorflow";
        modelPaths[9][7] = "";
        modelPaths[9][8] = "";
        
        modelPaths[10][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/NCBI_BERT_pubmed_uncased_L-24_H-1024_A-16";
        modelPaths[10][1] = "../BERTExperiments/";
        modelPaths[10][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[10][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[10][4] = "";
        modelPaths[10][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/NCBI_BERT_pubmed_uncased_L-24_H-1024_A-16";
        modelPaths[10][6] = "Tensorflow";
        modelPaths[10][7] = "";
        modelPaths[10][8] = "";
        
        modelPaths[11][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/Bio+ClinicalBERT";
        modelPaths[11][1] = "../BERTExperiments/";
        modelPaths[11][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[11][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[11][4] = "";
        modelPaths[11][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_v1.0_pubmed_pmc";
        modelPaths[11][6] = "Tensorflow";
        modelPaths[11][7] = "model.ckpt-150000.index";
        modelPaths[11][8] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/Bio+ClinicalBERT";
        
        modelPaths[12][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/Bio+DischargeSummaryBERT";
        modelPaths[12][1] = "../BERTExperiments/";
        modelPaths[12][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[12][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[12][4] = "";
        modelPaths[12][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_v1.0_pubmed_pmc";
        modelPaths[12][6] = "Tensorflow";
        modelPaths[12][7] = "model.ckpt-100000.index";
        modelPaths[12][8] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/Bio+DischargeSummaryBERT";
        
        modelPaths[13][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/clinicalBERT";
        modelPaths[13][1] = "../BERTExperiments/";
        modelPaths[13][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[13][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[13][4] = "";
        modelPaths[13][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_v1.0_pubmed_pmc";
        modelPaths[13][6] = "Tensorflow";
        modelPaths[13][7] = "model.ckpt-150000.index";
        modelPaths[13][8] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/clinicalBERT";
        
        modelPaths[14][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/DischargeSummaryBERT";
        modelPaths[14][1] = "../BERTExperiments/";
        modelPaths[14][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[14][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[14][4] = "";
        modelPaths[14][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_v1.0_pubmed_pmc";
        modelPaths[14][6] = "Tensorflow";
        modelPaths[14][7] = "model.ckpt-100000.index";
        modelPaths[14][8] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/DischargeSummaryBERT";
        
        modelPaths[15][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_v1.1_pubmed";
        modelPaths[15][1] = "../BERTExperiments/";
        modelPaths[15][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[15][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[15][4] = "";
        modelPaths[15][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_v1.1_pubmed";
        modelPaths[15][6] = "Tensorflow";
        modelPaths[15][7] = "";
        modelPaths[15][8] = "";
        
        modelPaths[16][0] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_large_v1.1_pubmed";
        modelPaths[16][1] = "../BERTExperiments/";
        modelPaths[16][2] = m_strDataDirectory + "BERTExperiments/venv/bin/python";
        modelPaths[16][3] = "../BERTExperiments/extractBERTvectors.py";
        modelPaths[16][4] = "";
        modelPaths[16][5] = m_strDataDirectory + "BERTExperiments/BERTPretrainedModels/biobert_large_v1.1_pubmed";
        modelPaths[16][6] = "Tensorflow";
        modelPaths[16][7] = "";
        modelPaths[16][8] = "";
        
        // Return the result
        
        return (modelPaths);
    }
}