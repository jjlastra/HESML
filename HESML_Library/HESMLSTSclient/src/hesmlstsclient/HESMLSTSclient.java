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

package hesmlstsclient;

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

/**
 * This class implements a basic client application of the HESML for sentence similarity
 * 
 * @author alicia and j.lastra
 */

public class HESMLSTSclient
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
         * ***********************************************
         * ***********************************************
         * 
         * FINAL EXPERIMENT. Execute the best combination for each method
         * 
         * ***********************************************
         * ***********************************************
         */
        
        totalCombinations += executeBestCombinationMethods("BESTCOMBS");
        
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
     * Test all the string measures.
     * 
     *  * Preprocessing configured as Blagec2019.
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static int executeBestCombinationMethods(
        String outputFileNames) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        // We define the base model dir for the Word Embedding experiments
        
        String strBaseModelDir = m_strDataDirectory + "/WordEmbeddings/";
        
        // We iterate the methods and create the measures
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        /**
         * ****************************************************
         * ****************************************************
         * Starting String-based experiment
         * ****************************************************
         * ****************************************************
         */
        
        // For each family of methods, define the best word processing combination
        
        IWordProcessing bestStringWordProcessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.WhiteSpace, 
                        true, NERType.None,
                        CharFilteringType.BIOSSES);
        
        // We define the methods to be evaluated

        ArrayList<StringBasedSentenceSimilarityMethod> methods = new ArrayList<>();
        
        methods.add(StringBasedSentenceSimilarityMethod.Jaccard);
        methods.add(StringBasedSentenceSimilarityMethod.BlockDistance);
        methods.add(StringBasedSentenceSimilarityMethod.Levenshtein);
        methods.add(StringBasedSentenceSimilarityMethod.OverlapCoefficient);
        methods.add(StringBasedSentenceSimilarityMethod.Qgram);
        
        // We iterate word processing combinations

        for(StringBasedSentenceSimilarityMethod method: methods)
        {
            // We create a copy of the wordProcessing object for avoid multithreading errors

            ISentenceSimilarityMeasure measure = 
                    SentenceSimilarityFactory.getStringBasedMeasure(
                        method.name(),
                        method, 
                        bestStringWordProcessing);
            measuresLst.add(measure);
            
            // Update the total of combinations
            
            totalCombinations++;
        }
        
        /**
         * ****************************************************
         * ****************************************************
         * Starting Our WE experiment
         * ****************************************************
         * ****************************************************
         */
        
        IWordProcessing bestOurWEProcessing = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.None,
                CharFilteringType.Default);
        
        // We define the models to be evaluated
        
        ArrayList<String> modelsFastextVecBased = new ArrayList<>();
        
        modelsFastextVecBased.add("bioc_skipgram_defaultchar.vec");
        
        // We iterate the methods and create the measures
        
        for(String model : modelsFastextVecBased)
        {
            // Get the model name without file extensions
        
            String label = model.replace(".vec", "").replace(".bin", "");

            // We create the measure

            ISentenceSimilarityMeasure measure = 
                SentenceSimilarityFactory.getSWEMMeasure(
                        label + "_" + SWEMpoolingMethod.Min.name(),
                        SWEMpoolingMethod.Min,
                        WordEmbeddingFileType.FastTextVecWordEmbedding, 
                        bestOurWEProcessing,
                        strBaseModelDir + model);

            measuresLst.add(measure);
        }
        
        /**
         * ****************************************************
         * ****************************************************
         * Starting SWEM experiments
         * ****************************************************
         * ****************************************************
         */
        
        // We define the best pre-processing method for each measure
        
        IWordProcessing[] bestSWEMProcessingsFastextVecBased = new IWordProcessing[8];
        
        bestSWEMProcessingsFastextVecBased[0] = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.None,
                CharFilteringType.Default);
        
        bestSWEMProcessingsFastextVecBased[1] = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.None,
                CharFilteringType.Default);
        
        bestSWEMProcessingsFastextVecBased[2] = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.None,
                CharFilteringType.Default);
        
        bestSWEMProcessingsFastextVecBased[3] = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.None,
                CharFilteringType.Default);
        
        bestSWEMProcessingsFastextVecBased[4] = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.None,
                CharFilteringType.Default);
        
        bestSWEMProcessingsFastextVecBased[5] = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.None,
                CharFilteringType.Default);
        
        bestSWEMProcessingsFastextVecBased[6] = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.None,
                CharFilteringType.Default);
        
        bestSWEMProcessingsFastextVecBased[7] = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.None,
                CharFilteringType.Default);
        
        
        IWordProcessing[] bestSWEMProcessingsBioWordVecBased = new IWordProcessing[4];
        
        bestSWEMProcessingsBioWordVecBased[0] = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.None,
                CharFilteringType.BIOSSES);
        
        bestSWEMProcessingsBioWordVecBased[1] = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.None,
                CharFilteringType.BIOSSES);
        
        bestSWEMProcessingsBioWordVecBased[2] = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                false, NERType.None,
                CharFilteringType.Default);
        
        bestSWEMProcessingsBioWordVecBased[3] = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                false, NERType.None,
                CharFilteringType.Default);
   
        
        String[] modelsFastextVecBasedSWEM = new String[8];
        
        modelsFastextVecBasedSWEM[0] = "bioconceptvec_fasttext.txt";
        modelsFastextVecBasedSWEM[1] = "bioconceptvec_glove.txt";
        modelsFastextVecBasedSWEM[2] = "bioconceptvec_word2vec_cbow.txt";
        modelsFastextVecBasedSWEM[3] = "bioconceptvec_word2vec_skipgram.txt";
        modelsFastextVecBasedSWEM[4] = "PubMed_CBOW.txt";
        modelsFastextVecBasedSWEM[5] = "PubMed_Glove.txt";
        modelsFastextVecBasedSWEM[6] = "PubMed_SkipGramNegSampling.txt";
        modelsFastextVecBasedSWEM[7] = "PubMed-and-PMC-w2v.txt";
        
        String[] modelsBioWordVecBasedSWEM = new String[4];
        
        modelsBioWordVecBasedSWEM[0] = "bio_embedding_extrinsic";
        modelsBioWordVecBasedSWEM[1] = "bio_embedding_intrinsic";
        modelsBioWordVecBasedSWEM[2] = "BioNLP2016_PubMed-shuffle-win-2.bin";
        modelsBioWordVecBasedSWEM[3] = "BioNLP2016_PubMed-shuffle-win-30.bin";
        
        // Iterate the FastText-based models
        
        for(int i=0; i<modelsFastextVecBasedSWEM.length; i++)
        {
            // We ge the model
            
            String model = modelsFastextVecBasedSWEM[i];
            
            // Get the model name without file extensions
        
            String label = model.replace(".vec", "").replace(".bin", "").replace(".txt", "");

            // We create the measure

            measuresLst.add(SentenceSimilarityFactory.getSWEMMeasure(
                        label,
                        SWEMpoolingMethod.Average,
                        WordEmbeddingFileType.FastTextVecWordEmbedding, 
                        bestSWEMProcessingsFastextVecBased[i],
                        strBaseModelDir + model));
        }
        
        // Iterate the FastText-based models
        
        for(int i=0; i<modelsBioWordVecBasedSWEM.length; i++)
        {
            // We ge the model
            
            String model = modelsBioWordVecBasedSWEM[i];
            
            // Get the model name without file extensions
        
            String label = model.replace(".vec", "").replace(".bin", "").replace(".txt", "");

            // We create the measure

            measuresLst.add(SentenceSimilarityFactory.getSWEMMeasure(
                        label,
                        SWEMpoolingMethod.Average,
                        WordEmbeddingFileType.BioWordVecBinaryWordEmbedding, 
                        bestSWEMProcessingsBioWordVecBased[i],
                        strBaseModelDir + model));
        }
        
        /**
         * ****************************************************
         * ****************************************************
         * Starting WBSM experiment
         * ****************************************************
         * ****************************************************
         */
        
        IWordProcessing bestWBSMWordProcessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.StanfordCoreNLPv4_2_0, 
                        true, NERType.None,
                        CharFilteringType.BIOSSES);

        // We define the word similarity measures to be compared
        
        ArrayList<SimilarityMeasureType> wordMeasuresWBSM = new ArrayList<>();
        wordMeasuresWBSM.add(SimilarityMeasureType.AncSPLWeightedJiangConrath);
        wordMeasuresWBSM.add(SimilarityMeasureType.AncSPLRada);
        wordMeasuresWBSM.add(SimilarityMeasureType.AncSPLCosineNormWeightedJiangConrath);
        wordMeasuresWBSM.add(SimilarityMeasureType.WuPalmerFast);
        wordMeasuresWBSM.add(SimilarityMeasureType.Lin);
        wordMeasuresWBSM.add(SimilarityMeasureType.AncSPLCaiStrategy1);
        
        // We get the intrinsic IC model if anyone has been defined

        IntrinsicICModelType icModelTypeWBSM = IntrinsicICModelType.Seco;
       
        // We iterate word processing combinations

        for(SimilarityMeasureType wordMeasure : wordMeasuresWBSM)
        {
            // We create a copy of the wordProcessing object for avoid multithreading errors

            ISentenceSimilarityMeasure measure = 
                    SentenceSimilarityFactory.getWBSMMeasure(
                            "WBSM_" + wordMeasure.name(),
                            bestWBSMWordProcessing,
                            m_WordNetDbSingleton, 
                            m_WordNetTaxonomySingleton, 
                            wordMeasure, 
                            icModelTypeWBSM);

            measuresLst.add(measure);
            
            // Update the total of combinations
            
            totalCombinations++;
        }

        /**
         * ****************************************************
         * ****************************************************
         * Starting UBSM experiment
         * ****************************************************
         * ****************************************************
         */
        
        IWordProcessing bestUBSMWordProcessing = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.MetamapSNOMEDCT,
                CharFilteringType.BIOSSES);

        // We define the word similarity measures to be compared
        
        ArrayList<SimilarityMeasureType> wordMeasuresUBSM = new ArrayList<>();
        wordMeasuresUBSM.add(SimilarityMeasureType.AncSPLWeightedJiangConrath);
        wordMeasuresUBSM.add(SimilarityMeasureType.AncSPLRada);
        wordMeasuresUBSM.add(SimilarityMeasureType.AncSPLCosineNormWeightedJiangConrath);
        wordMeasuresUBSM.add(SimilarityMeasureType.WuPalmerFast);
        wordMeasuresUBSM.add(SimilarityMeasureType.Lin);
        wordMeasuresUBSM.add(SimilarityMeasureType.AncSPLCaiStrategy1);
        
        // We get the intrinsic IC model if anyone has been defined

        IntrinsicICModelType icModelTypeUBSM = IntrinsicICModelType.Seco;
       
        // We iterate word processing combinations

        for(SimilarityMeasureType wordMeasure : wordMeasuresUBSM)
        {
            // We create a copy of the wordProcessing object 

            ISentenceSimilarityMeasure measure =
                    SentenceSimilarityFactory.getUBSMMeasureSnomed(
                            "UBSM_" + wordMeasure.name() + "_" + bestUBSMWordProcessing.getLabel(),
                            bestUBSMWordProcessing,
                            m_SnomedOntology, m_vertexesSnomed, m_taxonomySnomed,
                            wordMeasure,
                            icModelTypeUBSM);

            measuresLst.add(measure);
            
            // Update the total of combinations
            
            totalCombinations++;
        }

        /**
         * ****************************************************
         * ****************************************************
         * Starting COM experiment
         * ****************************************************
         * ****************************************************
         */
        
        // We get the intrinsic IC model if anyone has been defined

        IntrinsicICModelType icModelTypeCOM = IntrinsicICModelType.Seco;
       
        // We calculate the best measure combination
          ISentenceSimilarityMeasure[] measures = new ISentenceSimilarityMeasure[2];    

            ISentenceSimilarityMeasure measureWBSM = 
                    SentenceSimilarityFactory.getWBSMMeasure(
                            "WBSM_" + SimilarityMeasureType.AncSPLRada.name(),
                            bestWBSMWordProcessing,
                            m_WordNetDbSingleton, 
                            m_WordNetTaxonomySingleton, 
                            SimilarityMeasureType.AncSPLRada, 
                            icModelTypeCOM);
                ISentenceSimilarityMeasure measureUBSM =
                    SentenceSimilarityFactory.getUBSMMeasureSnomed(
                            "UBSM_" + SimilarityMeasureType.AncSPLWeightedJiangConrath.name(),
                            bestUBSMWordProcessing,
                            m_SnomedOntology, m_vertexesSnomed, m_taxonomySnomed,
                            SimilarityMeasureType.AncSPLWeightedJiangConrath,
                            icModelTypeCOM);

        measures[0] = measureWBSM;
        measures[1] = measureUBSM;

        // We create a copy of the wordProcessing object 

        ICombinedSentenceSimilarityMeasure measure = SentenceSimilarityFactory.getCOMMeasure(
            "COM_" + measures[0].getLabel() + "_" + measures[1].getLabel(),
            0.25,
            measures);

        measuresLst.add(measure);

        // Update the total of combinations

        totalCombinations++;
        
        
        /**
         * ****************************************************
         * ****************************************************
         * Starting COMMixed experiment
         * ****************************************************
         * ****************************************************
         */
        
        // We define the lambda values
        
        double[] lambda = {0.25,0.5,0.75};
        
        // Initialize the string measure

        ISentenceSimilarityMeasure stringMeasure = SentenceSimilarityFactory.getStringBasedMeasure(
                            "BlockDistance_" + bestStringWordProcessing.getLabel(),
                            StringBasedSentenceSimilarityMethod.BlockDistance, 
                            bestStringWordProcessing);
        
        // We execute the best COMMixed methods with different lambdas configurations
        
        for(int i=0; i < lambda.length; i++)
        {
            // We add the measures
            
            measuresLst.add(SentenceSimilarityFactory.getComMixedVectorsMeasureSnomedCT(
                     "COMMixed_UBSM-" + SimilarityMeasureType.AncSPLWeightedJiangConrath +"_String-" + StringBasedSentenceSimilarityMethod.BlockDistance + "_lambda"+lambda[i], 
                     bestUBSMWordProcessing,
                     m_SnomedOntology, m_taxonomySnomed,  
                     SimilarityMeasureType.AncSPLWeightedJiangConrath, IntrinsicICModelType.Seco, stringMeasure,
                     lambda[i], ComMixedVectorsMeasureType.SingleOntology));

             measuresLst.add(SentenceSimilarityFactory.getComMixedVectorsMeasureWordNetSnomedCTPooled(
                     "COMMixed_WBSM_UBSM_String_" + ComMixedVectorsMeasureType.PooledMin.name() + "_lambda"+lambda[i], 
                     bestWBSMWordProcessing,
                     bestUBSMWordProcessing,
                     m_SnomedOntology, m_taxonomySnomed,  
                     m_WordNetDbSingleton, m_WordNetTaxonomySingleton, 
                     SimilarityMeasureType.AncSPLRada,
                     SimilarityMeasureType.AncSPLWeightedJiangConrath, 
                     IntrinsicICModelType.Seco, stringMeasure,
                     lambda[i], ComMixedVectorsMeasureType.PooledMin));
        }

        /**
         * ****************************************************
         * ****************************************************
         * Starting BERT experiment
         * ****************************************************
         * ****************************************************
         */
        
        String strPythonScriptsDirectory = "../BERTExperiments/";
        String strPythonVirtualEnvironmentDir = "python3";
        String strPythonScript = "../BERTExperiments/WordPieceTokenization.py";
        String strBERTPretrainedModelFilename = "";
        
        // We configure the best preprocessing method for each model
        
        IWordProcessing[] bestBERTProcessing = new IWordProcessing[15];
        
        bestBERTProcessing[0] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.Default,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[1] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.BIOSSES,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[2] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.Default,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[3] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.Default,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[4] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.BIOSSES,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[5] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.BIOSSES,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[6] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.BIOSSES,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[7] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.BIOSSES,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[8] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.BIOSSES,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[9] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.Blagec2019,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[10] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.BIOSSES,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[11] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.Blagec2019,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[12] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        true,
                        NERType.None,
                        CharFilteringType.Blagec2019,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[13] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        false,
                        NERType.None,
                        CharFilteringType.Blagec2019,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        bestBERTProcessing[14] = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt",
                        TokenizerType.WordPieceTokenizer,
                        false,
                        NERType.None,
                        CharFilteringType.Blagec2019,
                        strPythonScriptsDirectory,
                        strPythonVirtualEnvironmentDir,
                        strPythonScript,
                        strBERTPretrainedModelFilename);
        
        int total_models = 15;
        
        String[][] modelPaths = getBERTModelPathList(total_models);
        
        for(int i=0; i<total_models; i++)
        {
            // We set the BERT model in the preprocessing
                
            bestBERTProcessing[i].setBERTModel(modelPaths[i][0]);

            // Select the python library

            MLPythonLibrary mllibrary = MLPythonLibrary.Tensorflow;
            if("Pytorch".equals(modelPaths[i][6]))
                mllibrary = MLPythonLibrary.Pytorch;

            // Create the measure

            String[] poolingLayers = new String[1];
            poolingLayers[0] = "-2";

            // The constructor for Tensorflow differs from Pytorch

            if(mllibrary == MLPythonLibrary.Tensorflow)
            {
                measuresLst.add(SentenceSimilarityFactory.getBERTTensorflowSentenceEmbeddingMethod(
                        bestBERTProcessing[i].getLabel(), 
                        SentenceEmbeddingMethod.BERTEmbeddingModel,
                        mllibrary,
                        bestBERTProcessing[i], 
                        modelPaths[i][5], 
                        modelPaths[i][7],
                        modelPaths[i][8],
                        modelPaths[i][1], 
                        modelPaths[i][2], 
                        modelPaths[i][3],
                        BERTpoolingMethod.REDUCE_MEAN, 
                        poolingLayers));
            }
            else
            {
                measuresLst.add(SentenceSimilarityFactory.getBERTPytorchSentenceEmbeddingMethod(
                        bestBERTProcessing[i].getLabel(), 
                        SentenceEmbeddingMethod.BERTEmbeddingModel,
                        mllibrary,
                        bestBERTProcessing[i], 
                        modelPaths[i][4], 
                        modelPaths[i][1], 
                        modelPaths[i][2], 
                        modelPaths[i][3]));
            }
        }
            
        /**
         * ****************************************************
         * ****************************************************
         * Starting Sent2Vec experiment
         * ****************************************************
         * ****************************************************
         */
        
        IWordProcessing bestSent2VecWordProcessing = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.None,
                CharFilteringType.BIOSSES);
        
        // We load and register a Sent2Vec measure from the XML file 

        String strSent2vecModelDir = m_strDataDirectory + "/SentenceEmbeddings/";
        String strSent2vecModelFile = "BioSentVec_PubMed_MIMICIII-bigram_d700.bin";
        String strPythonScriptsDirectorySent2vec = "../Sent2vecExperiments/";
        String strPythonVirtualEnvironmentDirSent2vec = "../Sent2vecExperiments/venv/bin/python3";
        String strPythonScriptSent2vec = "extractSent2vecvectors.py";
        
        measuresLst.add(SentenceSimilarityFactory.getSent2vecMethodMeasure(
                        "Sent2vec_" + strSent2vecModelFile.replace(".bin", ""), 
                        SentenceEmbeddingMethod.ParagraphVector,
                        bestSent2VecWordProcessing, 
                        strSent2vecModelDir + strSent2vecModelFile, 
                        strPythonScriptsDirectorySent2vec + strPythonScriptSent2vec,
                        strPythonVirtualEnvironmentDirSent2vec,
                        strPythonScriptsDirectorySent2vec));
        
        /**
         * ****************************************************
         * ****************************************************
         * Starting USE experiment
         * ****************************************************
         * ****************************************************
         */
        
        IWordProcessing bestUSEWordProcessing = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                false, NERType.None,
                CharFilteringType.Default);
        
        String strUSEModelURL = "https://tfhub.dev/google/universal-sentence-encoder/4";
        String strPythonScriptsDirectoryUSE = "../UniversalSentenceEncoderExperiments/";
        String strPythonVirtualEnvironmentDirUSE = "python3";
        String strPythonScriptUSE = "extractUniversalSentenceEncoderVectors.py";

        // We iterate word processing combinations
            
        measuresLst.add(SentenceSimilarityFactory.getUSESentenceEmbeddingMethod(
                    "USE", 
                    SentenceEmbeddingMethod.USEModel,
                    bestUSEWordProcessing, 
                    strUSEModelURL, 
                    strPythonScriptsDirectoryUSE + strPythonScriptUSE,
                    strPythonVirtualEnvironmentDirUSE,
                    strPythonScriptsDirectoryUSE));
        
        /**
         * ****************************************************
         * ****************************************************
         * Starting Flair experiment
         * ****************************************************
         * ****************************************************
         */
        
        IWordProcessing bestFlairWordProcessing = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "NoneStopWords.txt", 
                TokenizerType.WhiteSpace, 
                false, NERType.None,
                CharFilteringType.BIOSSES);
        
        String strFlairModelURL = "../FlairEmbeddings/embeddings/pubmed-backward.pt,../FlairEmbeddings/embeddings/pubmed-forward.pt";
        String strPythonScriptsDirectoryFlair = "../FlairEmbeddings/";
        String strPythonVirtualEnvironmentDirFlair = "python3";
        String strPythonScriptFlair = "extractFlairVectors.py";

        measuresLst.add(SentenceSimilarityFactory.getFlairEmbeddingMethod(
                            "Flair",  
                            SentenceEmbeddingMethod.Flair,
                            bestFlairWordProcessing, 
                            strFlairModelURL, 
                            strPythonScriptsDirectoryFlair + strPythonScriptFlair,
                            strPythonVirtualEnvironmentDirFlair,
                            strPythonScriptsDirectoryFlair));

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
        // We create the singleton instance of the WordNet database and taxonomy

        if (m_WordNetDbSingleton == null || useWordNetCache == false)
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
        
        // We create the singleton instance of the UMLS database and taxonomy

        if (m_MeshOntology == null)
        {
            // We load the MeSH ontology and get the vertex list of its taxonomy

            m_MeshOntology = MeSHFactory.loadMeSHOntology(
                                    m_strMeSHdir + "/" + m_strMeSHdescriptorFilename,
                                    m_strUMLSdir + "/" + m_strUmlsCuiMappingFilename);

            m_taxonomyMesh = m_MeshOntology.getTaxonomy();
        }
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
        
        // Return the result
        
        return (modelPaths);
    }
}