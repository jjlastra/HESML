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

package hesmlststestclient;

import hesml.HESMLversion;
import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml.sts.benchmarks.ISentenceSimilarityBenchmark;
import hesml.sts.benchmarks.impl.SentenceSimBenchmarkFactory;
import hesml.sts.measures.ComMixedVectorsMeasureType;
import hesml.sts.measures.ISentenceSimilarityMeasure;
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

public class HESMLSTSTestclient
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
        
        loadOntologies(true);
        
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
         * Testing function - WordNet issue evaluating twice the same measure
         * 
         * ***********************************************
         * ***********************************************
         */
        
        totalCombinations += testWordNetIssue();
        
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
            Logger.getLogger(HESMLSTSTestclient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // We execute the experiments
        
        executeExperiments(measuresLst, "tests");
    }
    
    /**
     * Test the WordNet issue - Executing twice the same measure gives different results
     */
    
    private static int testWordNetIssue() throws Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;
        
        // We iterate the methods and create the measures
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();

        // We get the intrinsic IC model if anyone has been defined

        IntrinsicICModelType icModelTypeWBSM = IntrinsicICModelType.Seco;
       
        // We iterate word processing combinations

        IWordProcessing bestWBSMWordProcessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.WhiteSpace, 
                        true, NERType.None,
                        CharFilteringType.None);
        
        // We create the measures with the same preprocessing configuration

//        measuresLst.add(SentenceSimilarityFactory.getWBSMMeasure(
//                        "WBSM_" + SimilarityMeasureType.AncSPLRada.name(),
//                        bestWBSMWordProcessing,
//                        m_WordNetDbSingleton, 
//                        m_WordNetTaxonomySingleton, 
//                        SimilarityMeasureType.AncSPLRada, 
//                        icModelTypeWBSM));
//        
//         measuresLst.add(SentenceSimilarityFactory.getWBSMMeasure(
//                        "WBSM_" + SimilarityMeasureType.AncSPLRada.name(),
//                        bestWBSMWordProcessing,
//                        m_WordNetDbSingleton, 
//                        m_WordNetTaxonomySingleton, 
//                        SimilarityMeasureType.AncSPLRada, 
//                        icModelTypeWBSM));
//         
//         measuresLst.add(SentenceSimilarityFactory.getWBSMMeasure(
//                        "WBSM_" + SimilarityMeasureType.AncSPLRada.name(),
//                        bestWBSMWordProcessing,
//                        m_WordNetDbSingleton, 
//                        m_WordNetTaxonomySingleton, 
//                        SimilarityMeasureType.AncSPLRada, 
//                        icModelTypeWBSM));
//         
//         measuresLst.add(SentenceSimilarityFactory.getWBSMMeasure(
//                        "WBSM_" + SimilarityMeasureType.AncSPLRada.name(),
//                        bestWBSMWordProcessing,
//                        m_WordNetDbSingleton, 
//                        m_WordNetTaxonomySingleton, 
//                        SimilarityMeasureType.AncSPLRada, 
//                        icModelTypeWBSM));
         
         /**
         * ****************************************************
         * ****************************************************
         * Starting COMMixed methods
         * ****************************************************
         * ****************************************************
         */
         
         // We define the string and WBSM similarity methods
         
        StringBasedSentenceSimilarityMethod stringMethod = StringBasedSentenceSimilarityMethod.Jaccard;
        SimilarityMeasureType wbsmMethod = SimilarityMeasureType.AncSPLWeightedJiangConrath;
         
        // Initialize the string measure

        ISentenceSimilarityMeasure stringMeasure = SentenceSimilarityFactory.getStringBasedMeasure(
                            stringMethod.name() + "_" + bestWBSMWordProcessing.getLabel(),
                            stringMethod, 
                            bestWBSMWordProcessing);

        // We create the measure

        measuresLst.add(SentenceSimilarityFactory.getComMixedVectorsMeasureWordNet(
                "COMMixed_WBSM-" + wbsmMethod +"_String-" + stringMethod, 
                bestWBSMWordProcessing,
                m_WordNetDbSingleton, m_WordNetTaxonomySingleton,  
                wbsmMethod, IntrinsicICModelType.Seco, stringMeasure,
                0.25, ComMixedVectorsMeasureType.SingleOntology));

        // We load the ontologies

//        loadOntologies(false);
        
        // We create the measure
        
        measuresLst.add(SentenceSimilarityFactory.getComMixedVectorsMeasureWordNet(
                "COMMixed_WBSM-" + wbsmMethod +"_String-" + stringMethod, 
                bestWBSMWordProcessing,
                m_WordNetDbSingleton, m_WordNetTaxonomySingleton,  
                wbsmMethod, IntrinsicICModelType.Seco, stringMeasure,
                0.25, ComMixedVectorsMeasureType.SingleOntology));
      
        // Update the total of combinations

        totalCombinations = 2;
        
        // We execute the experiments
        
        executeExperiments(measuresLst, "TestWordNetIssues");

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
}