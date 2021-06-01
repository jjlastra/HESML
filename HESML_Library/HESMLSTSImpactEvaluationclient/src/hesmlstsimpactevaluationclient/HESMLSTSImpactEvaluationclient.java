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
import hesml.sts.benchmarks.ISentenceSimilarityBenchmark;
import hesml.sts.benchmarks.impl.SentenceSimBenchmarkFactory;
import hesml.sts.measures.ComMixedVectorsMeasureType;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.MLPythonLibrary;
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
        
        // We get the start time

        long startFileProcessingTime = System.currentTimeMillis();

        // Loading message

        System.out.println("Loading and running all the combination experiments");


//        executeStringMeasures();
//        executeCOMMixedMeasures();
        
        executeStringMeasuresNER();
        executeCOMMixedMeasuresNER();
        
//        executeBERTMeasures();

        // We measure the elapsed time to run the experiments

        long endTime = System.currentTimeMillis();
        long minutes = (endTime - startFileProcessingTime) / 60000;
        long seconds = (endTime - startFileProcessingTime) / 1000;

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
    
    private static void executeStringMeasures() 
            throws IOException, InterruptedException, Exception
    {
        // We define the methods to be evaluated

        ArrayList<StringBasedSentenceSimilarityMethod> methods = new ArrayList<>();
        
        methods.add(StringBasedSentenceSimilarityMethod.Jaccard);
        methods.add(StringBasedSentenceSimilarityMethod.BlockDistance);
        methods.add(StringBasedSentenceSimilarityMethod.Levenshtein);
        methods.add(StringBasedSentenceSimilarityMethod.OverlapCoefficient);
        methods.add(StringBasedSentenceSimilarityMethod.Qgram);
        
        ArrayList<IWordProcessing> wordProcessingCombinations = getAllPreprocessingConfigurations(false);
        
        System.out.println("Calculating the combination of " + wordProcessingCombinations.size() + " word processing configurations");

        // We iterate the methods and create the measures
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        for(StringBasedSentenceSimilarityMethod method: methods)
        {
            // We iterate word processing combinations
            
            for(IWordProcessing wordProcessing : wordProcessingCombinations)
            {
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
        
        
        String strOutputFileNameBIOSSES = m_outputFilesDirPath + "raw_similarity_BIOSSES_stringMeasures.csv";
        String strOutputFileNameMedSTS = m_outputFilesDirPath + "raw_similarity_MedSTSFull_stringMeasures.csv";
        String strOutputFileNameCTR = m_outputFilesDirPath + "raw_similarity_CTR_stringMeasures.csv";
        
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
     * Test the COM measures with all the possible preprocessing configurations
     * 
     *  * Preprocessing configured as Blagec2019.
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static void executeCOMMixedMeasures() 
            throws IOException, InterruptedException, Exception
    {
        // We initialize the measures list

        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();

        
        loadOntologies();

        // We get the intrinsic IC model if anyone has been defined

        IntrinsicICModelType icModelType = IntrinsicICModelType.Seco;
        
        
        // Create the best string combination.
        

        IWordProcessing wordPreprocessingBestString = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.WhiteSpace, 
                        true, NERType.None,
                        CharFilteringType.BIOSSES);
        
//        IWordProcessing wordPreprocessingBestOntology = PreprocessingFactory.getWordProcessing(
//                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
//                        TokenizerType.WhiteSpace, 
//                        true, NERType.Ctakes,
//                        CharFilteringType.BIOSSES);

//        ArrayList<IWordProcessing> wordProcessingCombinations = getAllPreprocessingConfigurations();
//        
        ArrayList<IWordProcessing> wordProcessingCombinationsWithNERs = getAllPreprocessingConfigurations(true);
        
        // Initialize the COMMixed Types
        
        ComMixedVectorsMeasureType[] comMixedVectorsMeasuresType = {
                                                ComMixedVectorsMeasureType.PooledAVG,
                                                ComMixedVectorsMeasureType.PooledMin,
                                                ComMixedVectorsMeasureType.UMLS,
                                                ComMixedVectorsMeasureType.WordNet,
                                                };
        
        // We iterate word processing combinations
            
//        for(IWordProcessing wordProcessing : wordProcessingCombinations)
//        {
            // Initialize the measure

            ISentenceSimilarityMeasure stringMeasure = SentenceSimilarityFactory.getStringBasedMeasure(
                                "BlockDistance_" + wordPreprocessingBestString.getLabel(),
                                StringBasedSentenceSimilarityMethod.BlockDistance, 
                                wordPreprocessingBestString);
                
            for(IWordProcessing wordProcessingWithNERs : wordProcessingCombinationsWithNERs)
            {
                // We iterate the methods and create the measures

                for(ComMixedVectorsMeasureType comMixedVectorsMeasureType : comMixedVectorsMeasuresType)
                {
                    ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getComMixedVectorsMeasure(
                                        "COMMixed_AncSPLRada_"+comMixedVectorsMeasureType.name() + "_" + wordProcessingWithNERs.getLabel(), 
                                        wordProcessingWithNERs,
                                        m_SnomedOntology, m_vertexesSnomed, m_taxonomySnomed, m_WordNetDbSingleton, m_WordNetTaxonomySingleton, 
                                        SimilarityMeasureType.AncSPLRada,
                                        SimilarityMeasureType.AncSPLRada, icModelType, stringMeasure,
                                        0.5, comMixedVectorsMeasureType);
                    measuresLst.add(measure);
                }
            }
//        }
        
        // We create the vector to return the collection of sentence similarity measures
        
        ISentenceSimilarityMeasure[] measures = new ISentenceSimilarityMeasure[measuresLst.size()];
        
        // We copy the measures to the vector and release the temporary list
        
        measuresLst.toArray(measures);
        measuresLst.clear();
        
        // We read the configuration of the experiment
        
        String strOutputFileNameBIOSSES = m_outputFilesDirPath + "raw_similarity_BIOSSES_comMixedMeasures.csv";
        String strOutputFileNameMedSTS = m_outputFilesDirPath + "raw_similarity_MedSTSFull_comMixedMeasures.csv";
        String strOutputFileNameCTR = m_outputFilesDirPath + "raw_similarity_CTR_comMixedMeasures.csv";
        
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
     * Test all the string measures.
     * 
     *  * Preprocessing configured as Blagec2019.
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static void executeBERTMeasures() 
            throws IOException, InterruptedException, Exception
    {
        // We define the models to be evaluated

        String[][] modelPaths = new String[1][6];
        
        /**
         * The structure for each model paths is:
         * 
         * strBertDir + strBERTPretrainedModelFilename, 
         * strPythonScriptsDirectory, 
         * strPythonVirtualEnvironmentDir, 
         * strPythonScriptsDirectory + strPythonScript
         */
        
//        modelPaths[0][0] = "../BERTExperiments/BERTPretrainedModels/oubiobert-base-uncased";
//        modelPaths[0][1] = "../BERTExperiments/";
//        modelPaths[0][2] = "../BERTExperiments/PytorchExperiments/venv/bin/python";
//        modelPaths[0][3] = "../BERTExperiments/PytorchExperiments/extractBERTvectors.py";
//        modelPaths[0][4] = "seiya/oubiobert-base-uncased";
//        modelPaths[0][5] = "oubiobert-base-uncased";
        
//        modelPaths[0][0] = "../BERTExperiments/BERTPretrainedModels/scibert_scivocab_uncased";
//        modelPaths[0][1] = "../BERTExperiments/";
//        modelPaths[0][2] = "../BERTExperiments/PytorchExperiments/venv/bin/python";
//        modelPaths[0][3] = "../BERTExperiments/PytorchExperiments/extractBERTvectors.py";
//        modelPaths[0][4] = "allenai/scibert_scivocab_uncased";
//        modelPaths[0][5] = "scibert_scivocab_uncased";
        
        
//        modelPaths[0][0] = "../BERTExperiments/BERTPretrainedModels/BiomedNLP-PubMedBERT-base-uncased-abstract";
//        modelPaths[0][1] = "../BERTExperiments/";
//        modelPaths[0][2] = "../BERTExperiments/PytorchExperiments/venv/bin/python";
//        modelPaths[0][3] = "../BERTExperiments/PytorchExperiments/extractBERTvectors.py";
//        modelPaths[0][4] = "microsoft/BiomedNLP-PubMedBERT-base-uncased-abstract";
//        modelPaths[0][5] = "BiomedNLP-PubMedBERT-base-uncased-abstract";
        
        modelPaths[0][0] = "../BERTExperiments/BERTPretrainedModels/BiomedNLP-PubMedBERT-base-uncased-abstract-fulltext";
        modelPaths[0][1] = "../BERTExperiments/";
        modelPaths[0][2] = "../BERTExperiments/PytorchExperiments/venv/bin/python";
        modelPaths[0][3] = "../BERTExperiments/PytorchExperiments/extractBERTvectors.py";
        modelPaths[0][4] = "microsoft/BiomedNLP-PubMedBERT-base-uncased-abstract-fulltext";
        modelPaths[0][5] = "BiomedNLP-PubMedBERT-base-uncased-abstract-fulltext";

        
        int total_models = 1;
        
        // We create the combinations but we don't assign the BERT model names
        
        ArrayList<IWordProcessing> wordProcessingCombinations = getAllPreprocessingConfigurationsForBERT(
                "../BERTExperiments/",
                "../BERTExperiments/venv/bin/python",
                "../BERTExperiments/WordPieceTokenization.py", ""); 
        
        System.out.println("Calculating the combination of " + wordProcessingCombinations.size() + " word processing configurations");

        // We iterate the methods and create the measures
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        for(int i=0; i<total_models; i++)
        {
            // We iterate word processing combinations
            
            for(IWordProcessing wordProcessing : wordProcessingCombinations)
            {
                // We set the BERT model in the preprocessing
                
                wordProcessing.setBERTModel(modelPaths[i][0]);
                
                ISentenceSimilarityMeasure measure = 
                        SentenceSimilarityFactory.getBERTPytorchSentenceEmbeddingMethod(
                            modelPaths[i][5] + wordProcessing.getLabel(), 
                            SentenceEmbeddingMethod.BERTEmbeddingModel,
                            MLPythonLibrary.Pytorch,
                            wordProcessing, 
                            modelPaths[i][4], 
                            modelPaths[i][1], 
                            modelPaths[i][2], 
                            modelPaths[i][3]);
                
                measuresLst.add(measure);
            }
        }
        
        // We create the vector to return the collection of sentence similarity measures
        
        ISentenceSimilarityMeasure[] measures = new ISentenceSimilarityMeasure[measuresLst.size()];
        
        // We copy the measures to the vector and release the temporary list
        
        measuresLst.toArray(measures);
        measuresLst.clear();
        
        // We read the configuration of the experiment
        
        
        String strOutputFileNameBIOSSES = m_outputFilesDirPath + "raw_similarity_BIOSSES_stringMeasures.csv";
        String strOutputFileNameMedSTS = m_outputFilesDirPath + "raw_similarity_MedSTSFull_stringMeasures.csv";
        String strOutputFileNameCTR = m_outputFilesDirPath + "raw_similarity_CTR_stringMeasures.csv";
        
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
     * Get all the word preprocessing configurations for non-bert models
     */
    
    private static ArrayList<IWordProcessing> getAllPreprocessingConfigurationsWithNER(IWordProcessing wordPreprocessingBestString) throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();

        ArrayList<NERType> nerTypeCombs = new ArrayList<>();
        nerTypeCombs.add(NERType.None);
        nerTypeCombs.add(NERType.MetamapLite);
        nerTypeCombs.add(NERType.MetamapSNOMEDCT);
//        nerTypeCombs.add(NERType.MetamapMESH);
        nerTypeCombs.add(NERType.Ctakes);
        
        // We iterate all the combinations
        
        for(NERType ner : nerTypeCombs)
        {
            // We set the NER type
            
            wordPreprocessingBestString.setNERType(ner);
            
            // We add the wordprocessing method
            
            wordProcessingCombinations.add(wordPreprocessingBestString);
        }
      
        // Return the result
        
        return (wordProcessingCombinations);
    }
    
    /**
     * Get all the word preprocessing configurations for non-bert models
     */
    
    private static ArrayList<IWordProcessing> getAllPreprocessingConfigurations(boolean usingNER) throws IOException
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
        
        if(usingNER)
            nerTypeCombs.add(NERType.Ctakes);
        else
            nerTypeCombs.add(NERType.None);
        
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
     * Get all the word preprocessing configurations for BERT-based models
     */
    
    private static ArrayList<IWordProcessing> getAllPreprocessingConfigurationsForBERT(
            String strPythonScriptsDirectory,
            String strPythonVirtualEnvironmentDir,
            String strPythonScript,
            String strBERTPretrainedModelFilename) throws IOException
    {
        // Initialize the result
        
        ArrayList<IWordProcessing> wordProcessingCombinations = new ArrayList<>();

        
        ArrayList<String> stopWordsCombs = new ArrayList<>();
//        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt");
//        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt");
        stopWordsCombs.add(m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt");
        
        ArrayList<TokenizerType> tokenizerTypeCombs = new ArrayList<>();
        tokenizerTypeCombs.add(TokenizerType.WordPieceTokenizer);
        
        ArrayList<NERType> nerTypeCombs = new ArrayList<>();
        nerTypeCombs.add(NERType.None);
//        nerTypeCombs.add(NERType.MetamapLite);
//        nerTypeCombs.add(NERType.MetamapSNOMEDCT);
//        nerTypeCombs.add(NERType.MetamapMESH);
        
        ArrayList<CharFilteringType> charFilteringTypeCombs = new ArrayList<>();
        charFilteringTypeCombs.add(CharFilteringType.None);
//        charFilteringTypeCombs.add(CharFilteringType.BIOSSES);
//        charFilteringTypeCombs.add(CharFilteringType.Blagec2019);
//        charFilteringTypeCombs.add(CharFilteringType.Default);
        
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
                                        lw,
                                        ner,
                                        charfiltering,
                                        strPythonScriptsDirectory,
                                        strPythonVirtualEnvironmentDir,
                                        strPythonScript,
                                        strBERTPretrainedModelFilename));
                        }
                    }
                }
            }
        }
        
        // Return the result
        
        return (wordProcessingCombinations);
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
    
    private static void executeStringMeasuresNER() 
            throws IOException, InterruptedException, Exception
    {
        // We define the methods to be evaluated

        ArrayList<StringBasedSentenceSimilarityMethod> methods = new ArrayList<>();
        
        methods.add(StringBasedSentenceSimilarityMethod.Jaccard);
        methods.add(StringBasedSentenceSimilarityMethod.BlockDistance);
        methods.add(StringBasedSentenceSimilarityMethod.Levenshtein);
        methods.add(StringBasedSentenceSimilarityMethod.OverlapCoefficient);
        methods.add(StringBasedSentenceSimilarityMethod.Qgram);
        
        // Create the best string combination.
        
        IWordProcessing wordPreprocessingBestString = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.WhiteSpace, 
                        true, NERType.None,
                        CharFilteringType.BIOSSES);
        
        ArrayList<IWordProcessing> wordProcessingCombinations = getAllPreprocessingConfigurationsWithNER(wordPreprocessingBestString);
        
        System.out.println("Calculating the combination of " + wordProcessingCombinations.size() + " word processing configurations");

        // We iterate the methods and create the measures
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        for(StringBasedSentenceSimilarityMethod method: methods)
        {
            // We iterate word processing combinations
            
            for(IWordProcessing wordProcessing : wordProcessingCombinations)
            {
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
        
        
        String strOutputFileNameBIOSSES = m_outputFilesDirPath + "raw_similarity_BIOSSES_stringMeasures_NER.csv";
        String strOutputFileNameMedSTS = m_outputFilesDirPath + "raw_similarity_MedSTSFull_stringMeasures_NER.csv";
        String strOutputFileNameCTR = m_outputFilesDirPath + "raw_similarity_CTR_stringMeasures_NER.csv";
        
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
     * Test the COM measures with all the possible preprocessing configurations
     * 
     *  * Preprocessing configured as Blagec2019.
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static void executeCOMMixedMeasuresNER() 
            throws IOException, InterruptedException, Exception
    {
        // We initialize the measures list

        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();

        
        loadOntologies();

        // We get the intrinsic IC model if anyone has been defined

        IntrinsicICModelType icModelType = IntrinsicICModelType.Seco;
        
        
        // Create the best string combination.
        

        IWordProcessing wordPreprocessingBestString = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.WhiteSpace, 
                        true, NERType.None,
                        CharFilteringType.BIOSSES);
        
        IWordProcessing wordPreprocessingBestOntology = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.StanfordCoreNLPv4_2_0, 
                        true, NERType.Ctakes,
                        CharFilteringType.Default);
   
        ArrayList<IWordProcessing> wordProcessingCombinationsWithNERs = getAllPreprocessingConfigurationsWithNER(wordPreprocessingBestOntology);
        
        // Initialize the COMMixed Types
        
        ComMixedVectorsMeasureType[] comMixedVectorsMeasuresType = {
                                                ComMixedVectorsMeasureType.PooledAVG,
                                                ComMixedVectorsMeasureType.PooledMin,
                                                ComMixedVectorsMeasureType.UMLS,
                                                ComMixedVectorsMeasureType.WordNet,
                                                };
        
        // We iterate word processing combinations
        // Initialize the measure

        ISentenceSimilarityMeasure stringMeasure = SentenceSimilarityFactory.getStringBasedMeasure(
                            "BlockDistance_" + wordPreprocessingBestString.getLabel(),
                            StringBasedSentenceSimilarityMethod.BlockDistance, 
                            wordPreprocessingBestString);

        for(IWordProcessing wordProcessingWithNERs : wordProcessingCombinationsWithNERs)
        {
            // We iterate the methods and create the measures

            for(ComMixedVectorsMeasureType comMixedVectorsMeasureType : comMixedVectorsMeasuresType)
            {
                ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getComMixedVectorsMeasure(
                                    "COMMixed_AncSPLRada_"+comMixedVectorsMeasureType.name() + "_" + wordProcessingWithNERs.getLabel(), 
                                    wordProcessingWithNERs,
                                    m_SnomedOntology, m_vertexesSnomed, m_taxonomySnomed, m_WordNetDbSingleton, m_WordNetTaxonomySingleton, 
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
        
        String strOutputFileNameBIOSSES = m_outputFilesDirPath + "raw_similarity_BIOSSES_comMixedMeasures_NER.csv";
        String strOutputFileNameMedSTS = m_outputFilesDirPath + "raw_similarity_MedSTSFull_comMixedMeasures_NER.csv";
        String strOutputFileNameCTR = m_outputFilesDirPath + "raw_similarity_CTR_comMixedMeasures_NER.csv";
        
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
}