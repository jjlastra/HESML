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

package hesmlstsclient;

import hesml.HESMLversion;
import hesmlsts.benchmarks.ISentenceSimilarityBenchmark;
import hesmlsts.benchmarks.impl.SentenceSimBenchmarkFactory;
import hesmlsts.measures.ComMixedVectorsMeasureType;
import hesmlsts.measures.ISentenceSimilarityMeasure;
import hesmlsts.measures.StringBasedSentenceSimilarityMethod;
import hesmlsts.measures.impl.SentenceSimilarityFactory;
import hesmlsts.preprocess.CharFilteringType;
import hesmlsts.preprocess.IWordProcessing;
import hesmlsts.preprocess.NERType;
import hesmlsts.preprocess.TokenizerType;
import hesmlsts.preprocess.impl.PreprocessingFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import hesml.taxonomyreaders.obo.IOboOntology;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

/**
 * This class implements a basic client application of the HESML for sentence similarity
 * 
 * We can execute the experiments from a family of methods
 * Examples:
 *      java -jar dist/HESMLSTSclient.jar -s   -> string-based methods
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
        
        System.out.println("Running HESMLSTSClient V2R1 (2.1.0.0, January 2022) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
                + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        System.out.println("");
        
        // We get the start time

        long startFileProcessingTime = System.currentTimeMillis();
        
        long endTime = 0;
        long minutes = 0;
        long seconds = 0;
        
        System.out.println("Execute only string-based methods");
        
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
        
        // We can execute only the experiment LiBlock-NER, NER or BEST METHODS experiments

        totalCombinations += executeBestCombinationMethods("BESTCOMBS");
   
        
        // We measure the elapsed time to run the experiments
        
        seconds = (System.currentTimeMillis() - startFileProcessingTime) / 1000;
        
        System.out.println("-------------------------------------------------------");
        System.out.println("------------- HESMLSTSclient --------------------------");
        System.out.println("Finished measures experiments");
        System.out.println("Processed a total of " + totalCombinations + 
                            " combinations in = " + seconds + " (seconds)");
        System.out.println("-------------------------------------------------------"
                         + "-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        
        System.out.println("Copying results to HESML_DATA/ReproducibleExperiments...");
        
        String source = "/home/user/HESML/HESML_Library/ReproducibleExperiments";
        File srcDir = new File(source);

        String destination = "/home/user/HESML_DATA/ReproducibleResults";
        File destDir = new File(destination);

        FileUtils.copyDirectory(srcDir, destDir);
        
        
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
     * Test the best comnbination methods.
     * 
     *  * Preprocessing configured as Blagec2019.
     * @return
     * @throws IOException 
     * @param sentences1: first sentences of the dataset
     * @param sentences2: second sentences of the dataset
     */
    
    private static int executeBestCombinationMethods(String outputFileNames) 
            throws IOException, InterruptedException, Exception
    {
        // Initialize the result
        
        int totalCombinations = 0;

        // We iterate the methods and create the measures
        
        ArrayList<ISentenceSimilarityMeasure> measuresLst = new ArrayList<>();
        
        IWordProcessing bestStringWordProcessingBlockDistanceLi = PreprocessingFactory.getWordProcessing(
                            m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                            TokenizerType.WhiteSpace, 
                            true, NERType.None,
                            CharFilteringType.BIOSSES);

            // We define the lambda values

            double lambda = 0.5;

            // We define the pre-processing methods

            // Initialize the string measure

            ISentenceSimilarityMeasure stringMeasure = SentenceSimilarityFactory.getStringBasedMeasure(
                                "BlockDistance_" + bestStringWordProcessingBlockDistanceLi.getLabel(),
                                StringBasedSentenceSimilarityMethod.BlockDistance, 
                                bestStringWordProcessingBlockDistanceLi);

            // We add the LiBlock method using Ctakes
        
            IWordProcessing bestStringWordProcessingLiBlockCtakes = PreprocessingFactory.getWordProcessing(
                            m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                            TokenizerType.StanfordCoreNLPv4_2_0, 
                            true, NERType.None,
                            CharFilteringType.Default);

            measuresLst.add(SentenceSimilarityFactory.getLiBlockMeasure(
                     "LiBlock", 
                     bestStringWordProcessingLiBlockCtakes, stringMeasure,
                     lambda, ComMixedVectorsMeasureType.NoneOntology));
            
            // We define the pre-processing method

            IWordProcessing bestStringWordProcessingJaccard = PreprocessingFactory.getWordProcessing(
                            m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                            TokenizerType.WhiteSpace, 
                            true, NERType.None,
                            CharFilteringType.BIOSSES);

            // We add the LiBlock method not expanded

            measuresLst.add(SentenceSimilarityFactory.getStringBasedMeasure(
                     StringBasedSentenceSimilarityMethod.Jaccard.name(),
                     StringBasedSentenceSimilarityMethod.Jaccard, 
                     bestStringWordProcessingJaccard));

            // Update the total of combinations

            totalCombinations++;

            // We define the pre-processing method

            IWordProcessing bestStringWordProcessingBlockDistance = PreprocessingFactory.getWordProcessing(
                            m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                            TokenizerType.WhiteSpace, 
                            true, NERType.None,
                            CharFilteringType.BIOSSES);

            // We add the method

            measuresLst.add(SentenceSimilarityFactory.getStringBasedMeasure(
                     StringBasedSentenceSimilarityMethod.BlockDistance.name(),
                     StringBasedSentenceSimilarityMethod.BlockDistance, 
                     bestStringWordProcessingBlockDistance));

            // Update the total of combinations

            totalCombinations++;

            // We define the pre-processing method

            IWordProcessing bestStringWordProcessingLevenshtein = PreprocessingFactory.getWordProcessing(
                            m_strBaseDir + m_strStopWordsDir + "Biosses2017StopWords.txt", 
                            TokenizerType.WhiteSpace, 
                            false, NERType.None,
                            CharFilteringType.None);

            // We add the method 

            measuresLst.add(SentenceSimilarityFactory.getStringBasedMeasure(
                     StringBasedSentenceSimilarityMethod.Levenshtein.name(),
                     StringBasedSentenceSimilarityMethod.Levenshtein, 
                     bestStringWordProcessingLevenshtein));

            // Update the total of combinations

            totalCombinations++;

            // We define the pre-processing method

            IWordProcessing bestStringWordProcessingOverlapCoefficient = PreprocessingFactory.getWordProcessing(
                            m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                            TokenizerType.StanfordCoreNLPv4_2_0, 
                            true, NERType.None,
                            CharFilteringType.Default);

            // We add the method 

            measuresLst.add(SentenceSimilarityFactory.getStringBasedMeasure(
                     StringBasedSentenceSimilarityMethod.OverlapCoefficient.name(),
                     StringBasedSentenceSimilarityMethod.OverlapCoefficient, 
                     bestStringWordProcessingOverlapCoefficient));

            // Update the total of combinations

            totalCombinations++;

            // We define the pre-processing method

            IWordProcessing bestStringWordProcessingQgram = PreprocessingFactory.getWordProcessing(
                            m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                            TokenizerType.WhiteSpace, 
                            true, NERType.None,
                            CharFilteringType.BIOSSES);

            // We add the method

            measuresLst.add(SentenceSimilarityFactory.getStringBasedMeasure(
                     StringBasedSentenceSimilarityMethod.Qgram.name(),
                     StringBasedSentenceSimilarityMethod.Qgram, 
                     bestStringWordProcessingQgram));

        
        
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
}