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

package hesmlstsdatasetpreprocessingclient;

import hesml.HESMLversion;
import hesml.sts.documentreader.HSTSDocumentType;
import hesml.sts.preprocess.CharFilteringType;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.NERType;
import hesml.sts.preprocess.TokenizerType;
import hesml.sts.preprocess.impl.PreprocessingFactory;
import hesml.sts.sentencesextractor.SentenceExtractorType;
import hesml.sts.sentencesextractor.SentenceSplitterType;
import hesml.sts.sentencesextractor.impl.SentenceExtractorFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import javax.xml.stream.XMLStreamException;

/**
 * This class implements the sentence splitting of documents 
 * for the input of some trainning methods.
 * 
 * The output of this method is a file (or a list of files) with one sentence per line.
 * 
 * @author alicia
 */

public class HESMLSTSDatasetPreprocessingclient
{
    /**
     * Resources directories.
     * 
     * m_strBaseDir: the base directory with the resources
     * m_strStopWordsDir: Subdirectory with all the stop words files
     * m_bioCManuscriptCorpusDir: Subdirectory with the BioC corpus*
     *      * It can be divided into subdirectories, 
     *          HESML will iterate between them and extract the documents.
     * m_preprocessedDocumentsOutputDir: Output path.
     */
    
    private static final String  m_strBaseDir = "/home/user/HESML_DATA/";
    private static final String  m_strStopWordsDir = m_strBaseDir + "StopWordsFiles/";
    private static String  m_bioCManuscriptCorpusDir = m_strBaseDir + "BioCManuscriptCorpus/";
    private static String  m_preprocessedDocumentsOutputDir = m_strBaseDir + "PreprocessedPMCOpenAccessSubset/";
    private static String  m_preprocessedDocumentsOutputName = "";
    
    /**
     * This function loads an input XML file detailing a
     * set of reproducible experiments on sentence similarity.
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    
    public static void main(String[] args) throws IOException, InterruptedException, Exception
    {
        
        boolean   showUsage = false;  // Show usage
        
        // We print the HESML version
        
        System.out.println("Running HESMLSTSClient V2R1 (2.1.0.0, October 2019) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        // We read the incoming parameters and load the reproducible
        // experiments defined by the user in a XML-based file.
        
        // Set the commom arguments
            
        HSTSDocumentType documentType = HSTSDocumentType.BioCXMLUnicode;
        SentenceSplitterType sentenceSplitterType = SentenceSplitterType.BioCSentenceSplitter;
        SentenceExtractorType preprocessType = SentenceExtractorType.D0;
        boolean saveAllSentencesToASingleFile = Boolean.FALSE;
        TokenizerType tokenizerType = null;
        CharFilteringType charFilteringType = null;
        
        if ((args.length > 0))
        {
            // Get the preprocessing experiment

            String experimentName = "BioCNLPTokenizer_Default";

            switch(experimentName)
            {
                case "BioCNLPTokenizer_Default":

                    tokenizerType = TokenizerType.BioCNLPTokenizer;
                    charFilteringType = CharFilteringType.Default;
                    
                    // Set the output filename automatically
                    
                    m_preprocessedDocumentsOutputName = "BioCNLPTokenizer_Default.txt";
                    
                    break;
                    
                case "BioCNLPTokenizer_None":
                    
                    tokenizerType = TokenizerType.BioCNLPTokenizer;
                    charFilteringType = CharFilteringType.None;
                    
                    // Set the output filename automatically
                    
                    m_preprocessedDocumentsOutputName = "BioCNLPTokenizer_None.txt";
                    
                    break;
                    
                case "StanfordCoreNLP_Default":
                    
                    tokenizerType = TokenizerType.StanfordCoreNLPv4_2_0;
                    charFilteringType = CharFilteringType.Default;
                    
                    // Set the output filename automatically
                    
                    m_preprocessedDocumentsOutputName = "StanfordCoreNLP_Default.txt";
                    
                    break;
                    
                case "StanfordCoreNLP_None":
                    
                    tokenizerType = TokenizerType.StanfordCoreNLPv4_2_0;
                    charFilteringType = CharFilteringType.None;
                    
                    // Set the output filename automatically
                    
                    m_preprocessedDocumentsOutputName = "StanfordCoreNLP_None.txt";
                    
                    break;
            }
            
            // If the file output exists, remove before append the new sentences
            
            File outputFile = new File(m_preprocessedDocumentsOutputDir 
                    + m_preprocessedDocumentsOutputName);
            outputFile.delete();
        }
        else
        {
            System.err.println("\nIn order to properly use the HESMLSTSClient program");
            System.err.println("you should call it using any of the two methods shown below:\n");
            System.err.println("(1) C:> java -jar dist/HESMLSTSDatasetPreprocessingclient.jar <BioCNLPTokenizer_None>");
        }
        
        // Perform the preprocess

        testD0(documentType, sentenceSplitterType, preprocessType, 
                tokenizerType, charFilteringType, saveAllSentencesToASingleFile);
    }
    
    /**
     *  Function only for testing purposes. 
     * Set an input BioC files directory and run the preprocess pipeline to create D0 documents.
     * -> D0 documents: tokenization and sentence splitting*
     *      * In BioC documents the tokenization and sentence splitting is carried out using the bioc library.
     * -> D1 documents: metamap annotations @todo not implemented yet
     * @throws IOException
     * @throws XMLStreamException
     * 
     * @todo establish input directory and read the tree. Output the results using the same structure than the input directories.
     */
    
    private static void testD0(
            HSTSDocumentType        documentType,
            SentenceSplitterType    sentenceSplitterType,
            SentenceExtractorType   preprocessType,
            TokenizerType           tokenizerType,
            CharFilteringType       charFilteringType,
            boolean                 saveAllSentencesToASingleFile) 
            throws IOException, XMLStreamException, 
            FileNotFoundException, InterruptedException, Exception
    {
        //Calculate the execution time of the method
        
        Instant start = Instant.now();
        
        // Create a Wordpreprocessing object
        
        IWordProcessing wordPreprocessing = PreprocessingFactory.getWordProcessing(
                m_strBaseDir + m_strStopWordsDir + "nltk2018StopWords.txt", 
                tokenizerType, 
                true, NERType.Ctakes,
                charFilteringType);
        
        // Create the output subdirectories
        
        File outputDir = new File(m_preprocessedDocumentsOutputDir);
        outputDir.mkdirs();
        
        // Run the sentence extractor pipeline
        
        SentenceExtractorFactory.runSentenceExtractorPipeline(
                                m_bioCManuscriptCorpusDir, 
                                m_preprocessedDocumentsOutputDir, 
                                m_preprocessedDocumentsOutputName,
                                documentType, 
                                sentenceSplitterType,
                                preprocessType,
                                wordPreprocessing,
                                saveAllSentencesToASingleFile);
        
        //Calculate the execution time 
        
        Instant end = Instant.now();
        Duration interval = Duration.between(start, end);
        System.out.println("\nExecution time in seconds: " + interval.getSeconds());
        System.out.println("\nExecution time in minutes: " + (interval.getSeconds() / 60));
        System.out.println("\nExecution time in hours: " + ((interval.getSeconds() / 60) / 60));
    } 
}