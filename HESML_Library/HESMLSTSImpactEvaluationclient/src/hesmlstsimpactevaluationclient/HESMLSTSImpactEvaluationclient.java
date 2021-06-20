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

package hesmlstsimpactevaluationclient;

import hesml.HESMLversion;
import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.WordEmbeddingFileType;
import hesml.sts.benchmarks.ISentenceSimilarityBenchmark;
import hesml.sts.benchmarks.impl.SentenceSimBenchmarkFactory;
import hesml.sts.measures.ComMixedVectorsMeasureType;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SWEMpoolingMethod;
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
import hesml.taxonomyreaders.obo.IOboOntology;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import hesml.taxonomyreaders.wordnet.impl.WordNetFactory;
import java.io.IOException;
import java.util.ArrayList;

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
     * m_strStopWordsDir: Subdirectory with all the stop words files
     * m_strWordNetDatasetsDir: Subdirectory with all the WordNet datasets
     * m_strWordNet3_0_Dir: Subdirectory with WordNet v3.0 dictionary
     */
    
    private static final String  m_strBaseDir = "../";
    private static final String  m_strStopWordsDir = "StopWordsFiles/";
    
    private static final String  m_strWordNetDatasetsDir = m_strBaseDir + "/Wordnet-3.0/dict";
    private static final String  m_strWordNetDBDir = "data.noun";
    
    /**
     * Filenames and directory for the SNOMED-CT files
     */

    private static String m_strSnomedDir = "../UMLS/SNOMED_Nov2019";
    private static final String m_strSnomedConceptFilename = "sct2_Concept_Snapshot_US1000124_20190901.txt";
    private static final String m_strSnomedRelationshipsFilename = "sct2_Relationship_Snapshot_US1000124_20190901.txt";
    private static final String m_strSnomedDescriptionFilename = "sct2_Description_Snapshot-en_US1000124_20190901.txt";

    /** 
     * Filename and directory for the UMLS CUI mapping file
     */
    
    private static final String m_strUmlsCuiMappingFilename = "MRCONSO.RRF";
    private static final String m_strUMLSdir = "../UMLS/UMLS2019AB";    
    
    /**
     * Filenames and directory for the MeSH ontology
     */
    
    private static final String m_strMeSHdir = "../UMLS/MeSH_Nov2019";
    private static final String m_strMeSHdescriptorFilename = "desc2020.xml";
    
    /**
     * Output files path
     */
    
    private static final String m_strDatasetDir = "../SentenceSimDatasets/";
    private static final String m_outputFilesDirPath = "../ReproducibleExperiments/BioSentenceSimilarity_paper/BioSentenceSimFinalRawOutputFiles/";

    /**
     * Dataset filenames
     */
    
    private static final String m_strDatasetFileNameBIOSSES = "BIOSSESNormalized.tsv";
    private static final String m_strDatasetFileNameMedSTS = "MedStsFullNormalized.tsv";
    private static final String m_strDatasetFileNameCTR = "CTRNormalized_averagedScore.tsv";
    
    /**
     * Dataset filenames (test)
     */
    
//    private static final String m_strDatasetFileNameBIOSSES = "test/BIOSSESNormalized.tsv";
//    private static final String m_strDatasetFileNameMedSTS = "test/MedStsFullNormalized.tsv";
//    private static final String m_strDatasetFileNameCTR = "test/CTRNormalized_averagedScore.tsv";
    
    
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
    private static IVertexList m_vertexesMesh = null;
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
        boolean showUsage = false;  // Show usage
        
        // We print the HESML version
        
        System.out.println("Running HESMLSTSClient V2R1 (2.1.0.0, February 2020) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
                + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        System.out.println("");
        
        // Loading message

        System.out.println("Loading and running all the preprocessing combination experiments");
        
        ArrayList<IWordProcessing> allWordProcessingCombinations = getAllPreprocessingConfigurations(NERType.None);
        
        ArrayList<IWordProcessing> wordProcessingCombinationsWithCtakes = getAllPreprocessingConfigurations(NERType.Ctakes);
        
        ArrayList<IWordProcessing> swWordProcessingCombinations = getStopWordsPreprocessingConfigurations();
        ArrayList<IWordProcessing> cfWordProcessingCombinations = getCharFilteringPreprocessingConfigurations();
        ArrayList<IWordProcessing> tokenizerWordProcessingCombinations = getTokenizerPreprocessingConfigurations();
        ArrayList<IWordProcessing> lcProcessingCombinations = getLowerCasePreprocessingConfigurations();
        
        // Initialize the total number of combinations
        
        int totalCombinations = 0;
        
        // List with all the pooling methods for WE-based methods
        
        ArrayList<SWEMpoolingMethod> poolingMethods = new ArrayList<>();
        poolingMethods.add(SWEMpoolingMethod.Max);
        poolingMethods.add(SWEMpoolingMethod.Average);
        poolingMethods.add(SWEMpoolingMethod.Min);
        poolingMethods.add(SWEMpoolingMethod.Sum);
        
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
        
//        System.out.println("-------------------------------------------------");
//        System.out.println("-------------------------------------------------");
//        System.out.println("Starting String-based measures experiments");
//        System.out.println("-------------------------------------------------");
//        System.out.println("-------------------------------------------------");
//        
//        // Compute all preprocessing combinations
//        
//        totalCombinations += executeStringMeasures(allWordProcessingCombinations, "Stringbased_ALLPreprocessingCombs");
//        
//        // Compute the best string combination.
//        
//        ArrayList<IWordProcessing> stringWordProcessingCombinationsNER = 
//                getAllPreprocessingConfigurationsWithNER(m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
//                        TokenizerType.WhiteSpace, 
//                        true, 
//                        CharFilteringType.BIOSSES);
//        
//        totalCombinations += executeStringMeasures(stringWordProcessingCombinationsNER, "Stringbased_BESTPreproComb_NER");
//        
//        // We measure the elapsed time to run the experiments
//
//        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;
//
//        System.out.println("-------------------------------------------------");
//        System.out.println("-------------------------------------------------");
//        System.out.println("Finished String-based measures experiments");
//        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
//        System.out.println("-------------------------------------------------");
//        System.out.println("-------------------------------------------------");
        
        
        /**
         * ***********************************************
         * ***********************************************
         * 
         * EXPERIMENT 2. Starting our WE-based measures experiments
         * 
         * ***********************************************
         * ***********************************************
         */
        
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");
//        System.out.println("Starting our preprocessed WE-based measures experiments");
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");
//        
//        // Reset the total combinations
//        
//        totalCombinations = 0;
//        
//        // Compute all the executions
//        
//        for(SWEMpoolingMethod pooling : poolingMethods)
//        {
//            totalCombinations += executeOurWEMeasures(swWordProcessingCombinations, "OurWEStopWords", pooling);
//            totalCombinations += executeOurWEMeasures(cfWordProcessingCombinations, "OurWECharFiltering", pooling);
//            totalCombinations += executeOurWEMeasures(tokenizerWordProcessingCombinations, "OurWETokenizer", pooling);
//            totalCombinations += executeOurWEMeasures(lcProcessingCombinations, "OurWELC", pooling);
//        }
//        
//        // We measure the elapsed time to run the experiments
//
//        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;
//
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");
//        System.out.println("Finished our preprocessed WE-based measures experiments");
//        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");
        
        /**
         * ***********************************************
         * ***********************************************
         * 
         * EXPERIMENT 3. Starting WBSM measures experiments
         * 
         * ***********************************************
         * ***********************************************
         */
        
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");
//        System.out.println("Starting ontology-based measures experiments");
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");
//        
//        // Reset the total combinations
//        
//        totalCombinations = 0;
//        
//        // We load the ontologies
//        
//        loadOntologies();
//
//        
//        totalCombinations += executeWBSMMeasures(allWordProcessingCombinations, "WBSM");
//         
//        
//        // We measure the elapsed time to run the experiments
//
//        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;
//
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");
//        System.out.println("Finished ontology-based measures experiments");
//        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");
        
/**
         * ***********************************************
         * ***********************************************
         * 
         * EXPERIMENT 4. Starting Our COMMixed measures experiments
         * 
         * ***********************************************
         * ***********************************************
         */
        
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");
//        System.out.println("Starting COM Mixed ontology-based measures experiments");
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");
//        
//        // Reset the total combinations
//        
//        totalCombinations = 0;
//        
//        // We load the ontologies
//        
//        loadOntologies();
//
//        // Create the best string combination.
//        
//        IWordProcessing wordPreprocessingBestString = PreprocessingFactory.getWordProcessing(
//                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
//                        TokenizerType.WhiteSpace, 
//                        true, NERType.None,
//                        CharFilteringType.BIOSSES);
//        
//        // Initialize the COMMixed Types
//        
//        ComMixedVectorsMeasureType[] comMixedVectorsMeasuresType = {
//                                                ComMixedVectorsMeasureType.PooledAVG,
//                                                ComMixedVectorsMeasureType.PooledMin,
//                                                ComMixedVectorsMeasureType.PooledMax,
//                                                ComMixedVectorsMeasureType.UMLS,
//                                                ComMixedVectorsMeasureType.WordNet,
//                                                };
//        
//        totalCombinations += executeCOMMixedMeasures(
//                wordPreprocessingBestString,
//                wordProcessingCombinationsWithCtakes, 
//                comMixedVectorsMeasuresType, 
//                "COMMixed");
//         
//        // We measure the elapsed time to run the experiments
//
//        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;
//
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");
//        System.out.println("Finished COM Mixed ontology-based measures experiments");
//        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
//        System.out.println("-------------------------------------------------------");
//        System.out.println("-------------------------------------------------------");

        /**
         * ***********************************************
         * ***********************************************
         * 
         * FINAL EXPERIMENT. Execute the best combination for each method
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
        
        // We load the ontologies
        
        loadOntologies();
        
        // For each family of methods, define the best word processing combination
        
        IWordProcessing bestStringWordProcessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.WhiteSpace, 
                        true, NERType.None,
                        CharFilteringType.BIOSSES);
        
        IWordProcessing bestWBSMWordProcessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.StanfordCoreNLPv4_2_0, 
                        true, NERType.None,
                        CharFilteringType.Default);
        
        IWordProcessing bestOurWEProcessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt", 
                        TokenizerType.BioCNLPTokenizer, 
                        true, NERType.None,
                        CharFilteringType.None);
        
        IWordProcessing wordPreprocessingBestComMixedWordnet = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt", 
                        TokenizerType.StanfordCoreNLPv4_2_0, 
                        true, NERType.None,
                        CharFilteringType.Default);

        totalCombinations += executeBestCombinationMethods(
                bestStringWordProcessing, 
                bestWBSMWordProcessing,
                bestOurWEProcessing,
                wordPreprocessingBestComMixedWordnet,
                ComMixedVectorsMeasureType.WordNet,
                SWEMpoolingMethod.Max,
                "BESTCOMBS");
        
        // We measure the elapsed time to run the experiments
        
        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;
        
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        System.out.println("Finished ontology-based measures experiments");
        System.out.println("Processed a total of " + totalCombinations + " combinations in = " + seconds + " (seconds)");
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
            String outputFileNames,
            SWEMpoolingMethod   pooling) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        // We create the configuration of the experiment
        
        String strOutputFileNameBIOSSES = m_outputFilesDirPath + "raw_similarity_BIOSSES_SWEMMeasures_" + outputFileNames + ".csv";
        String strOutputFileNameMedSTS = m_outputFilesDirPath + "raw_similarity_MedSTSFull_SWEMMeasures_" + outputFileNames + ".csv";
        String strOutputFileNameCTR = m_outputFilesDirPath + "raw_similarity_CTR_SWEMMeasures_" + outputFileNames + ".csv";
        
        // We define the models to be evaluated
        
        ArrayList<String> modelsFastextVecBased = new ArrayList<>();
        
        modelsFastextVecBased.add("bioc_skipgram_defaultchar.vec");
//        modelsFastextVecBased.add("bioc_skipgram_Nonechar200dim.vec");
//        modelsFastextVecBased.add("stanford_skipgram_nonechar200dim.vec");
//        modelsFastextVecBased.add("stanf_skipgram_defaultchar200dim.vec");
        
        // We define the base model dir
        
        String strBaseModelDir = "../WordEmbeddings/";
        
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
        
        // We create the vector to return the collection of sentence similarity measures
        
        ISentenceSimilarityMeasure[] measures = new ISentenceSimilarityMeasure[measuresLst.size()];
        
        // We copy the measures to the vector and release the temporary list
        
        measuresLst.toArray(measures);
        measuresLst.clear();
        
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
    
    private static int executeCOMMixedMeasures(
        IWordProcessing wordPreprocessingBestString,
        ArrayList<IWordProcessing> wordProcessingCombinations,
        ComMixedVectorsMeasureType[] comMixedVectorsMeasuresType,
        String outputFileNames) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        // We initialize the measures list

        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();

        // We get the intrinsic IC model if anyone has been defined

        IntrinsicICModelType icModelType = IntrinsicICModelType.Seco;
        
        // Initialize the measure

        ISentenceSimilarityMeasure stringMeasure = SentenceSimilarityFactory.getStringBasedMeasure(
                            "BlockDistance_" + wordPreprocessingBestString.getLabel(),
                            StringBasedSentenceSimilarityMethod.BlockDistance, 
                            wordPreprocessingBestString);

        for(IWordProcessing wordProcessingWithNERs : wordProcessingCombinations)
        {
            // We iterate the methods and create the measures

            for(ComMixedVectorsMeasureType comMixedVectorsMeasureType : comMixedVectorsMeasuresType)
            {
                ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getComMixedVectorsMeasure(
                                    "COMMixed_AncSPLRada_BlockDistance_" + comMixedVectorsMeasureType.name() + "_" + wordProcessingWithNERs.getLabel(), 
                                    wordProcessingWithNERs,
                                    m_SnomedOntology, m_vertexesSnomed, m_taxonomySnomed, 
                                    m_WordNetDbSingleton, m_WordNetTaxonomySingleton, 
                                    SimilarityMeasureType.AncSPLRada,
                                    SimilarityMeasureType.AncSPLRada, icModelType, stringMeasure,
                                    0.5, comMixedVectorsMeasureType);
                measuresLst.add(measure);
            }
        }
        
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
        wordMeasures.add(SimilarityMeasureType.WuPalmerFast);
        wordMeasures.add(SimilarityMeasureType.Lin);
        wordMeasures.add(SimilarityMeasureType.AncSPLCaiStrategy1);
        
        
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
                // We create a copy of the wordProcessing object for avoid multithreading errors

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
    
    private static int executeBestCombinationMethods(
        IWordProcessing bestStringWordProcessingCombinations, 
        IWordProcessing bestWBSMWordProcessing,
        IWordProcessing bestOurWEProcessing,
        IWordProcessing wordPreprocessingBestComMixedWordnet,
        ComMixedVectorsMeasureType comMixedVectorsMeasureTypeWordNet,
        SWEMpoolingMethod bestOurWEPooling,
        String outputFileNames) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        // We iterate the methods and create the measures
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
//        /**
//         * ****************************************************
//         * ****************************************************
//         * Starting String-based methods
//         * ****************************************************
//         * ****************************************************
//         */
//        
//        // We define the methods to be evaluated
//
//        ArrayList<StringBasedSentenceSimilarityMethod> methods = new ArrayList<>();
//        
//        methods.add(StringBasedSentenceSimilarityMethod.Jaccard);
//        methods.add(StringBasedSentenceSimilarityMethod.BlockDistance);
//        methods.add(StringBasedSentenceSimilarityMethod.Levenshtein);
//        methods.add(StringBasedSentenceSimilarityMethod.OverlapCoefficient);
//        methods.add(StringBasedSentenceSimilarityMethod.Qgram);
//        
//        
//        // We iterate word processing combinations
//
//        for(StringBasedSentenceSimilarityMethod method: methods)
//        {
//            // We create a copy of the wordProcessing object for avoid multithreading errors
//
//            ISentenceSimilarityMeasure measure = 
//                    SentenceSimilarityFactory.getStringBasedMeasure(
//                        method.name(),
//                        method, 
//                        bestStringWordProcessingCombinations);
//            measuresLst.add(measure);
//            
//            // Update the total of combinations
//            
//            totalCombinations++;
//        }
//        
//        /**
//         * ****************************************************
//         * ****************************************************
//         * Starting Our WE methods
//         * ****************************************************
//         * ****************************************************
//         */
//        
//        // We define the models to be evaluated
//        
//        ArrayList<String> modelsFastextVecBased = new ArrayList<>();
//        
////        modelsFastextVecBased.add("bioc_skipgram_defaultchar.vec");
//        modelsFastextVecBased.add("bioc_skipgram_Nonechar200dim.vec");
////        modelsFastextVecBased.add("stanford_skipgram_nonechar200dim.vec");
////        modelsFastextVecBased.add("stanf_skipgram_defaultchar200dim.vec");
//        
//        // We define the base model dir
//        
//        String strBaseModelDir = "../WordEmbeddings/";
//     
//        // We iterate the methods and create the measures
//        
//        for(String model : modelsFastextVecBased)
//        {
//            // Get the model name without file extensions
//        
//            String label = model.replace(".vec", "").replace(".bin", "");
//
//            // We create the measure
//
//            ISentenceSimilarityMeasure measure = 
//                SentenceSimilarityFactory.getSWEMMeasure(
//                        label + "_" + bestOurWEPooling.name(),
//                        bestOurWEPooling,
//                        WordEmbeddingFileType.FastTextVecWordEmbedding, 
//                        bestOurWEProcessing,
//                        strBaseModelDir + model);
//
//            measuresLst.add(measure);
//        }
//        
//        /**
//         * ****************************************************
//         * ****************************************************
//         * Starting WBSM methods
//         * ****************************************************
//         * ****************************************************
//         */
//        
//        // We define the word similarity measures to be compared
//        
//        ArrayList<SimilarityMeasureType> wordMeasuresWBSM = new ArrayList<>();
//        wordMeasuresWBSM.add(SimilarityMeasureType.AncSPLWeightedJiangConrath);
//        wordMeasuresWBSM.add(SimilarityMeasureType.AncSPLRada);
//        wordMeasuresWBSM.add(SimilarityMeasureType.AncSPLCosineNormWeightedJiangConrath);
//        wordMeasuresWBSM.add(SimilarityMeasureType.WuPalmerFast);
//        wordMeasuresWBSM.add(SimilarityMeasureType.Lin);
//        wordMeasuresWBSM.add(SimilarityMeasureType.AncSPLCaiStrategy1);
//        
//        
//        // We get the intrinsic IC model if anyone has been defined
//
//        IntrinsicICModelType icModelTypeWBSM = IntrinsicICModelType.Seco;
//       
//        // We iterate word processing combinations
//
//        for(SimilarityMeasureType wordMeasure : wordMeasuresWBSM)
//        {
//            // We create a copy of the wordProcessing object for avoid multithreading errors
//
//            ISentenceSimilarityMeasure measure = 
//                    SentenceSimilarityFactory.getWBSMMeasure(
//                            "WBSM_" + wordMeasure.name(),
//                            bestWBSMWordProcessing,
//                            m_WordNetDbSingleton, 
//                            m_WordNetTaxonomySingleton, 
//                            wordMeasure, 
//                            icModelTypeWBSM);
//
//            measuresLst.add(measure);
//            
//            // Update the total of combinations
//            
//            totalCombinations++;
//        }
        
        /**
         * ****************************************************
         * ****************************************************
         * Starting COMMixed methods
         * ****************************************************
         * ****************************************************
         */
        
        // We get the intrinsic IC model if anyone has been defined

        IntrinsicICModelType icModelTypeCOMMixed = IntrinsicICModelType.Seco;
       
        // Initialize the measure
        

        ISentenceSimilarityMeasure bestStringMeasure = SentenceSimilarityFactory.getStringBasedMeasure(
                            "BlockDistance_" + bestStringWordProcessingCombinations.getLabel(),
                            StringBasedSentenceSimilarityMethod.BlockDistance, 
                            bestStringWordProcessingCombinations);
       
        wordPreprocessingBestComMixedWordnet = PreprocessingFactory.getWordProcessing(
               m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt", 
               TokenizerType.StanfordCoreNLPv4_2_0, 
               true, NERType.Ctakes,
               CharFilteringType.Default);
        
        // We iterate the methods and create the measures
        
        measuresLst.add(SentenceSimilarityFactory.getComMixedVectorsMeasure(
                                    "COMMixed_AncSPLRada_WordNet_BlockDistance_a" + 
                                            bestStringMeasure.getLabel() + "_" + 
                                            wordPreprocessingBestComMixedWordnet.getLabel(), 
                                    wordPreprocessingBestComMixedWordnet,
                                    m_SnomedOntology, m_vertexesSnomed, m_taxonomySnomed, 
                                    m_WordNetDbSingleton, m_WordNetTaxonomySingleton, 
                                    SimilarityMeasureType.AncSPLRada,
                                    SimilarityMeasureType.AncSPLRada, 
                                    icModelTypeCOMMixed, bestStringMeasure,
                                    0.5, comMixedVectorsMeasureTypeWordNet));

        
        measuresLst.add(SentenceSimilarityFactory.getComMixedVectorsMeasure(
                                    "COMMixed_AncSPLRada_WordNet_BlockDistance_b" + 
                                            bestStringMeasure.getLabel() + "_" + 
                                            wordPreprocessingBestComMixedWordnet.getLabel(), 
                                    wordPreprocessingBestComMixedWordnet,
                                    m_SnomedOntology, m_vertexesSnomed, m_taxonomySnomed, 
                                    m_WordNetDbSingleton, m_WordNetTaxonomySingleton, 
                                    SimilarityMeasureType.AncSPLRada,
                                    SimilarityMeasureType.AncSPLRada, 
                                    icModelTypeCOMMixed, bestStringMeasure,
                                    0.5, comMixedVectorsMeasureTypeWordNet));
        

        
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

        // Return the result
        
        return (totalCombinations);
    }
    
    /**
     * Load ontologies
     */
    private static void loadOntologies() throws Exception
    {
        // We create the singleton instance of the WordNet database and taxonomy

        if (m_WordNetDbSingleton == null)
        {
            // We load the singleton instance of WordNet-related objects. It is done to
            // avoid the memory cost of multiple instances of WordNet when multiple
            // instances of the WBSM measure are created.
            
            m_WordNetDbSingleton = WordNetFactory.loadWordNetDatabase(m_strWordNetDatasetsDir, m_strWordNetDBDir);    
            m_WordNetTaxonomySingleton = WordNetFactory.buildTaxonomy(m_WordNetDbSingleton);  

            // We pre-process the taxonomy to compute all the parameters
            // used by the intrinsic IC-computation methods

            m_WordNetTaxonomySingleton.computesCachedAttributes();
        }
        
        // We create the singleton instance of the UMLS database and taxonomy

        if (m_SnomedOntology == null)
        {
            // We load the SNOMED ontology and get the vertex list of its taxonomy

            m_SnomedOntology = SnomedCtFactory.loadSnomedDatabase(m_strSnomedDir,
                                    m_strSnomedConceptFilename,
                                    m_strSnomedRelationshipsFilename,
                                    m_strSnomedDescriptionFilename,
                                    m_strUMLSdir, m_strUmlsCuiMappingFilename);

            m_taxonomySnomed = m_SnomedOntology.getTaxonomy();
            m_vertexesSnomed = m_taxonomySnomed.getVertexes();
        }
    }
    
            
    /**
     * Get all the word pre-processing configurations for non-bert models
     */
    
    private static ArrayList<IWordProcessing> getAllPreprocessingConfigurationsWithNER(
                        String stopWordsDir, 
                        TokenizerType tokenizerType, 
                        boolean lw,
                        CharFilteringType charFilteringType) throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();

        ArrayList<NERType> nerTypeCombs = new ArrayList<>();
        nerTypeCombs.add(NERType.MetamapLite);
        nerTypeCombs.add(NERType.Ctakes);
        nerTypeCombs.add(NERType.MetamapSNOMEDCT);
        nerTypeCombs.add(NERType.None);
//        nerTypeCombs.add(NERType.MetamapMESH);
        
        // We iterate all the combinations
        
        for(NERType ner : nerTypeCombs)
        {
            // We add the wordprocessing method
            
            wordProcessingCombinations.add(PreprocessingFactory.getWordProcessing(
                        stopWordsDir, 
                        tokenizerType, 
                        lw, ner,
                        charFilteringType));
        }
      
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
        
        ArrayList<NERType> nerTypeCombs = new ArrayList<>();
        
//        if(usingNER)
//        {
//            nerTypeCombs.add(NERType.Ctakes);
//            nerTypeCombs.add(NERType.MetamapLite);
//            nerTypeCombs.add(NERType.MetamapSNOMEDCT);
//            nerTypeCombs.add(NERType.None);
//        }
//        else
//            nerTypeCombs.add(NERType.None);
        
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
    
    private static ArrayList<IWordProcessing> getStopWordsPreprocessingConfigurations() throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();

        
        ArrayList<String> stopWordsCombs = new ArrayList<>();
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt");
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt");
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt");
        
        ArrayList<TokenizerType> tokenizerTypeCombs = new ArrayList<>();
        tokenizerTypeCombs.add(TokenizerType.WhiteSpace);
//        tokenizerTypeCombs.add(TokenizerType.BioCNLPTokenizer);
//        tokenizerTypeCombs.add(TokenizerType.StanfordCoreNLPv4_2_0);
        
        ArrayList<NERType> nerTypeCombs = new ArrayList<>();
        
        nerTypeCombs.add(NERType.None);
        
        ArrayList<CharFilteringType> charFilteringTypeCombs = new ArrayList<>();
//        charFilteringTypeCombs.add(CharFilteringType.None);
//        charFilteringTypeCombs.add(CharFilteringType.BIOSSES);
//        charFilteringTypeCombs.add(CharFilteringType.Blagec2019);
        charFilteringTypeCombs.add(CharFilteringType.Default);
        
        ArrayList<Boolean> lowerCasingCombs = new ArrayList<>();
        lowerCasingCombs.add(true);
//        lowerCasingCombs.add(false);
        
        // We iterate all the combinations
        
        for(String stopword : stopWordsCombs)
        {
            for(TokenizerType tokenizer : tokenizerTypeCombs)
            {
                for(NERType ner : nerTypeCombs)
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
        }
        
        // Return the result
        
        return (wordProcessingCombinations);
    }
    
    /**
     * Get all the word preprocessing configurations for non-bert models
     */
    
    private static ArrayList<IWordProcessing> getCharFilteringPreprocessingConfigurations() throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();

        
        ArrayList<String> stopWordsCombs = new ArrayList<>();
//        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt");
//        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt");
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt");
        
        ArrayList<TokenizerType> tokenizerTypeCombs = new ArrayList<>();
        tokenizerTypeCombs.add(TokenizerType.WhiteSpace);
//        tokenizerTypeCombs.add(TokenizerType.BioCNLPTokenizer);
//        tokenizerTypeCombs.add(TokenizerType.StanfordCoreNLPv4_2_0);
        
        ArrayList<NERType> nerTypeCombs = new ArrayList<>();
        
        nerTypeCombs.add(NERType.None);
        
        ArrayList<CharFilteringType> charFilteringTypeCombs = new ArrayList<>();
        charFilteringTypeCombs.add(CharFilteringType.None);
        charFilteringTypeCombs.add(CharFilteringType.BIOSSES);
        charFilteringTypeCombs.add(CharFilteringType.Blagec2019);
        charFilteringTypeCombs.add(CharFilteringType.Default);
        
        ArrayList<Boolean> lowerCasingCombs = new ArrayList<>();
        lowerCasingCombs.add(true);
//        lowerCasingCombs.add(false);
        
        // We iterate all the combinations
        
        for(String stopword : stopWordsCombs)
        {
            for(TokenizerType tokenizer : tokenizerTypeCombs)
            {
                for(NERType ner : nerTypeCombs)
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
        }
        
        // Return the result
        
        return (wordProcessingCombinations);
    }
    
    /**
     * Get all the word preprocessing configurations for non-bert models
     */
    
    private static ArrayList<IWordProcessing> getTokenizerPreprocessingConfigurations() throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();

        
        ArrayList<String> stopWordsCombs = new ArrayList<>();
//        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt");
//        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt");
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt");
        
        ArrayList<TokenizerType> tokenizerTypeCombs = new ArrayList<>();
        tokenizerTypeCombs.add(TokenizerType.WhiteSpace);
        tokenizerTypeCombs.add(TokenizerType.BioCNLPTokenizer);
        tokenizerTypeCombs.add(TokenizerType.StanfordCoreNLPv4_2_0);
        
        ArrayList<NERType> nerTypeCombs = new ArrayList<>();
        
        nerTypeCombs.add(NERType.None);
        
        ArrayList<CharFilteringType> charFilteringTypeCombs = new ArrayList<>();
//        charFilteringTypeCombs.add(CharFilteringType.None);
//        charFilteringTypeCombs.add(CharFilteringType.BIOSSES);
//        charFilteringTypeCombs.add(CharFilteringType.Blagec2019);
        charFilteringTypeCombs.add(CharFilteringType.Default);
        
        ArrayList<Boolean> lowerCasingCombs = new ArrayList<>();
        lowerCasingCombs.add(true);
//        lowerCasingCombs.add(false);
        
        // We iterate all the combinations
        
        for(String stopword : stopWordsCombs)
        {
            for(TokenizerType tokenizer : tokenizerTypeCombs)
            {
                for(NERType ner : nerTypeCombs)
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
        }
        
        // Return the result
        
        return (wordProcessingCombinations);
    }
    
    /**
     * Get all the word preprocessing configurations for non-bert models
     */
    
    private static ArrayList<IWordProcessing> getLowerCasePreprocessingConfigurations() throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();

        
        ArrayList<String> stopWordsCombs = new ArrayList<>();
//        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt");
//        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt");
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt");
        
        ArrayList<TokenizerType> tokenizerTypeCombs = new ArrayList<>();
        tokenizerTypeCombs.add(TokenizerType.WhiteSpace);
//        tokenizerTypeCombs.add(TokenizerType.BioCNLPTokenizer);
//        tokenizerTypeCombs.add(TokenizerType.StanfordCoreNLPv4_2_0);
        
        ArrayList<NERType> nerTypeCombs = new ArrayList<>();
        
        nerTypeCombs.add(NERType.None);
        
        ArrayList<CharFilteringType> charFilteringTypeCombs = new ArrayList<>();
//        charFilteringTypeCombs.add(CharFilteringType.None);
//        charFilteringTypeCombs.add(CharFilteringType.BIOSSES);
//        charFilteringTypeCombs.add(CharFilteringType.Blagec2019);
        charFilteringTypeCombs.add(CharFilteringType.Default);
        
        ArrayList<Boolean> lowerCasingCombs = new ArrayList<>();
        lowerCasingCombs.add(true);
        lowerCasingCombs.add(false);
        
        // We iterate all the combinations
        
        for(String stopword : stopWordsCombs)
        {
            for(TokenizerType tokenizer : tokenizerTypeCombs)
            {
                for(NERType ner : nerTypeCombs)
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
        }
        
        // Return the result
        
        return (wordProcessingCombinations);
    }
}