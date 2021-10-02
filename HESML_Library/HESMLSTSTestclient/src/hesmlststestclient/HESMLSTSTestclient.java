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

import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Position;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

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
import java.util.List;


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

//    private static String m_strSnomedDir = m_strDataDirectory + "UMLS/SNOMED_Nov2019";
//    private static final String m_strSnomedConceptFilename = "sct2_Concept_Snapshot_US1000124_20190901.txt";
//    private static final String m_strSnomedRelationshipsFilename = "sct2_Relationship_Snapshot_US1000124_20190901.txt";
//    private static final String m_strSnomedDescriptionFilename = "sct2_Description_Snapshot-en_US1000124_20190901.txt";
    
    private static String m_strSnomedDir = m_strDataDirectory + "UMLS/SNOMED_JUL2020";
    private static final String m_strSnomedConceptFilename = "sct2_Concept_Snapshot_INT_20200731.txt";
    private static final String m_strSnomedRelationshipsFilename = "sct2_Relationship_Snapshot_INT_20200731.txt";
    private static final String m_strSnomedDescriptionFilename = "sct2_Description_Snapshot-en_INT_20200731.txt";

    /** 
     * Filename and directory for the UMLS CUI mapping file
     */
    
    private static final String m_strUmlsCuiMappingFilename = "MRCONSO.RRF";
    private static final String m_strUMLSdir = m_strDataDirectory + "UMLS/UMLS2020AA";    
    
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
     
        // We get the start time

        long startFileProcessingTime = System.currentTimeMillis();
        
        long endTime = 0;
        long minutes = 0;
        long seconds = 0;
        
        // Initialize the result
        
        String sentence = "NSCLC";
        
        String annotatedSentence = sentence;

        // Process the sentence using the Metamap objects


        MetaMapApi api = new MetaMapApiImpl();
        
        api.setOptions("-y");
        api.setOptions("-R SNOMEDCT_US");  
        List<Result> resultList = api.processCitationsFromString(sentence);
        
        
        Result result = resultList.get(0);

        int diff_lenght = 0;
        
        for (Utterance utterance: result.getUtteranceList()) {
            System.out.println("Utterance:");
            System.out.println(" Id: " + utterance.getId());
            System.out.println(" Utterance text: " + utterance.getString());
            System.out.println(" Position: " + utterance.getPosition());
            
            for (PCM pcm: utterance.getPCMList()) {
                System.out.println("Phrase:");
                System.out.println(" text: " + pcm.getPhrase().getPhraseText());
                
                System.out.println("Candidates:");
                
                
                System.out.println("Mappings:");
                for (Mapping map: pcm.getMappingList()) {
                  System.out.println(" Map Score: " + map.getScore());
                  for (Ev mapEv: map.getEvList()) {
                    System.out.println("   Score: " + mapEv.getScore());
                    System.out.println("   Concept Id: " + mapEv.getConceptId());
                    System.out.println("   Concept Name: " + mapEv.getConceptName());
                    System.out.println("   Preferred Name: " + mapEv.getPreferredName());
                    System.out.println("   Matched Words: " + mapEv.getMatchedWords());
                    System.out.println("   Semantic Types: " + mapEv.getSemanticTypes());
                    System.out.println("   MatchMap: " + mapEv.getMatchMap());
                    System.out.println("   MatchMap alt. repr.: " + mapEv.getMatchMapList());
                    System.out.println("   is Head?: " + mapEv.isHead());
                    System.out.println("   is Overmatch?: " + mapEv.isOvermatch());
                    System.out.println("   Sources: " + mapEv.getSources());
                    System.out.println("   Positional Info: " + mapEv.getPositionalInfo());
                    
                    List<Position> pos = mapEv.getPositionalInfo();
                    
                    for(Position p : mapEv.getPositionalInfo())
                    {
                        long pos_ini = p.getX() - diff_lenght;
                        long num_chars = p.getY();
                        
                        int total_lenght = annotatedSentence.length();
                        
                        int pos_end = (int) (pos_ini + num_chars);
                        
                        int cuiLenght = mapEv.getConceptId().length();
                        
                        diff_lenght += (int) (num_chars - cuiLenght);
                        
                        annotatedSentence = annotatedSentence.substring(0, (int) pos_ini) + mapEv.getConceptId() + annotatedSentence.substring(pos_end, total_lenght);
                    }

                  }
                }
            }
        }
        
        // We load the ontologies
        
        loadOntologies(false);
        
        // Initialize the result
        
        int totalCombinations = 0;
        
        // We define the best pre-processing WBSM method
        
        IWordProcessing bestWBSMWordProcessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.StanfordCoreNLPv4_2_0, 
                        true, NERType.None,
                        CharFilteringType.BIOSSES);
        
        // We define the best UBSM pre-processing method
        
        IWordProcessing bestUBSMWordProcessing = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.MetamapSNOMEDCT,
                CharFilteringType.BIOSSES);
        
        // We define the lambda values
        
        double lambda = 0.5;
        
        // We define the pre-processing methods
        
        IWordProcessing bestStringExpandedWordProcessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.WhiteSpace, 
                        true, NERType.MetamapExpandPreferredNames,
                        CharFilteringType.BIOSSES);
        
        IWordProcessing bestUBSMWordProcessingSnomed = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.MetamapSNOMEDCT,
                CharFilteringType.BIOSSES);
        
        IWordProcessing bestUBSMWordProcessingMeSH = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                TokenizerType.StanfordCoreNLPv4_2_0, 
                true, NERType.MetamapMESH,
                CharFilteringType.BIOSSES);
        
        // For each family of methods, define the best word processing combination
        
        IWordProcessing bestStringWordProcessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.WhiteSpace, 
                        true, NERType.None,
                        CharFilteringType.BIOSSES);
        
        // Initialize the string measure

        ISentenceSimilarityMeasure stringMeasure = SentenceSimilarityFactory.getStringBasedMeasure(
                            "BlockDistance_" + bestStringWordProcessing.getLabel(),
                            StringBasedSentenceSimilarityMethod.BlockDistance, 
                            bestStringWordProcessing);
        
        ISentenceSimilarityMeasure measure = SentenceSimilarityFactory.getComMixedVectorsMeasureWordNetSnomedCTPooled(
                 "COMMixed_Mixed_String_" + ComMixedVectorsMeasureType.Mixed.name() + "_lambda"+lambda, 
                 bestWBSMWordProcessing,
                 bestUBSMWordProcessingSnomed,
                 m_SnomedOntology, m_taxonomySnomed,  
                 m_WordNetDbSingleton, m_WordNetTaxonomySingleton, 
                 SimilarityMeasureType.AncSPLRada,
                 SimilarityMeasureType.AncSPLWeightedJiangConrath, 
                 IntrinsicICModelType.Seco, stringMeasure,
                 lambda, ComMixedVectorsMeasureType.PooledMax);

        
        String firstSents = "It has recently been shown that Craf is essential for Kras G12D-induced NSCLC.";
        String secondSent = "It has recently become evident that Craf is essential for the onset of Kras-driven non-small cell lung cancer";
        
        measure.prepareForEvaluation();
        measure.getSimilarityValue(firstSents, secondSent);

        // We measure the elapsed time to run the experiments

        endTime = System.currentTimeMillis();
        minutes = (endTime - startFileProcessingTime) / 60000;
        seconds = (endTime - startFileProcessingTime) / 1000;

        System.out.println("Overall elapsed loading and computation time (minutes) = " + minutes);
        System.out.println("Overall elapsed loading and computation time (seconds) = " + seconds);
    }
    
    
    /**
     * This function loads WordNet, UMLS and MeSH ontologies before executing the experiments.
     * 
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
}