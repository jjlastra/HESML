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
import hesml.sts.benchmarks.impl.SentenceSimBenchmarkFactory;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.StringBasedSentenceSimilarityMethod;
import hesml.sts.measures.impl.SentenceSimilarityFactory;
import hesml.sts.preprocess.CharFilteringType;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.TokenizerType;
import hesml.sts.preprocess.impl.PreprocessingFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.parser.ParseException;

/**
 * This class implements a basic client application of the HESML similarity
 * measures library introduced in the paper below.
 * 
 * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * HESML: a scalable ontology-based semantic similarity measures
 * library with a set of reproducible experiments and a replication dataset.
 * Submitted for publication in Information Systems Journal.
 * 
 * @author j.lastra
 */

public class HESMLSTSclient
{
    /**
     * Resources directories.
     * 
     * m_strBaseDir: the base directory with the resources
     * m_strStopWordsDir: Subdirectory with all the stop words files
     */
    
    private static final String  m_strBaseDir = "../";
    private static final String  m_strStopWordsDir = m_strBaseDir + "StopWordsFiles/";

    
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
        
        // We read the incoming parameters and load the reproducible
        // experiments defined by the user in a XML-based file.
        
        if ((args.length > 0) && args[0].endsWith(".stsexp"))
        {
            // Running of a reproducible STS experiment file

            File inputFile = new File(args[0]);  // Get the file path
            
            // We check the existence of the file

            if (inputFile.exists())
            {
                // We get the start time

                long startFileProcessingTime = System.currentTimeMillis();

                // Loading message

                System.out.println("Loading and running the experiments defined in "
                        + inputFile.getName());

                // We set the Schema file for the *.stsexp
                
                // We parse the input file in order to recover all
                // STS experiments. Schema file is assumed to be in
                // same folder that the experiments file.

                SentenceSimBenchmarkFactory.runXmlBenchmarksFile(inputFile.getPath());

                // We measure the elapsed time to run the experiments

                long endTime = System.currentTimeMillis();
                long minutes = (endTime - startFileProcessingTime) / 60000;
                long seconds = (endTime - startFileProcessingTime) / 1000;

                System.out.println("Overall elapsed loading and computation time (minutes) = " + minutes);
                System.out.println("Overall elapsed loading and computation time (seconds) = " + seconds);
            }
        }
        else
        {
            showUsage = true;
        }
        
        // For a wrong calling to the program, we show the usage.
        
        if (showUsage)
        {
            System.err.println("\nIn order to properly use the HESMLSTSClient program");
            System.err.println("you should call it using any of the two methods shown below:\n");
            System.err.println("(1) C:> java -jar dist\\HESMLSTSClient.jar <reproexperiment.stsexp>");
        }
        
        // We call the SampleExperiments() function
        
        SampleExperiments();
    }
    
    /**
     * This function runs some basic experiments to show the functionality
     * of HEMSL-STS library.
     */
    
    private static void SampleExperiments() throws IOException, InterruptedException, ParseException, Exception
    {
        // Initialize the sentences to be tested.
        // sentences1 are first sentences
        // sentences2 are second sentences
        
        String[] sentences1 = { "It has recently been shown that Craf is essential for Kras G12D-induced NSCLC.",
                                "The Bcl-2 inhibitor ABT-737 induces regression of solid tumors  and its derivatives "
                                + "are in the early clinical phase as cancer therapeutics; however, it targets Bcl-2, Bcl-XL, "
                                + "and Bcl-w, but not Mcl-1, which induces resistance against apoptotic cell death triggered by ABT-737."};
        String[] sentences2 = { "It has recently become evident that Craf is essential for the onset of Kras-driven "
                                + "non-small cell lung cancer.",
                                "Recently, it has been reported that ABT-737 is not cytotoxic to all tumors cells, and "
                                + "that chemoresistance to ABT-737 is dependent on appreciable levels of Mcl-1 expression, "
                                + "the one Bcl-2 family member it does not effectively inhibit."};
        
        // Execute the tests
        
        testStringMeasures(sentences1, sentences2);
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
    
    private static void testStringMeasures(
            String[] sentences1,
            String[] sentences2) throws IOException, InterruptedException, Exception
    {
        // Initialize the preprocessing method and measures
        
        IWordProcessing wordPreprocessing = null;
        ISentenceSimilarityMeasure measure = null;
        
        // Initialize the hashmap for testing
        
        HashMap<String, StringBasedSentenceSimilarityMethod> tests = new HashMap<>();
        
        // Create a Wordpreprocessing object as Blagec2019 does.
        
        wordPreprocessing = PreprocessingFactory.getWordProcessing(
                        m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                        TokenizerType.StanfordCoreNLPv3_9_1, 
                        true, 
                        CharFilteringType.Blagec2019);
        
        // Add the string based methods to test
        
        tests.put("Jaccard Measure",            StringBasedSentenceSimilarityMethod.Jaccard);
        tests.put("Block Distance Measure",     StringBasedSentenceSimilarityMethod.BlockDistance);
        tests.put("Levenshtein Measure",        StringBasedSentenceSimilarityMethod.Levenshtein);
        tests.put("Overlap Coefficient Measure",StringBasedSentenceSimilarityMethod.OverlapCoefficient);
        tests.put("Qgram Measure",              StringBasedSentenceSimilarityMethod.Qgram);
        
        // Iterate the map 
        
        for (Map.Entry<String, StringBasedSentenceSimilarityMethod> testMeasure : tests.entrySet())
        {
            // Get the name of the measure and the type
            
            String strMeasureName = testMeasure.getKey();
            StringBasedSentenceSimilarityMethod similarityMethod = testMeasure.getValue();
            
            // Initialize the measure
            
            measure = SentenceSimilarityFactory.getStringBasedMeasure(
                            strMeasureName,
                            similarityMethod, 
                            wordPreprocessing);
            
            // Get the similarity scores for the lists of sentences
            
            double[] simScores = measure.getSimilarityValues(sentences1, sentences2);
            
            // Print the results - For testing purposes
            
            System.out.println("Scores for " + strMeasureName + ": ");
            for (int i = 0; i < simScores.length; i++)
            {
                double score = simScores[i];
                System.out.println("---- Sentence " + i + " : " + score);
            }
        }
        measure.clear();
    }
}