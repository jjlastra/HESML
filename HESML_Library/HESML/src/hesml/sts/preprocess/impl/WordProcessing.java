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

package hesml.sts.preprocess.impl;


import bioc.BioCDocument;
import gov.nih.nlm.nls.metamap.document.FreeText;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import gov.nih.nlm.nls.ner.MetaMapLite;
import hesml.sts.preprocess.CharFilteringType;
import hesml.sts.preprocess.ITokenizer;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.TokenizerType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *  This class configures the preprocess method and preprocess the sentences.
 * @author alicia
 */

class WordProcessing implements IWordProcessing
{
    // Configure if the text would be lowercased.
    
    private final boolean m_lowercaseNormalization; 
    
    // Configure the concept annotation using Metamap
    
    private final boolean m_conceptsAnnotation;
    
    // Metamap Lite instance
    
    protected MetaMapLite m_metaMapLiteInst;

    // Set the tokenization method
    
    private final TokenizerType m_tokenizerType; 
    
    // Set the filtering method
    
    private final CharsFiltering m_charFilter; 

    // Stopwords filename and hashset. 
    // If empty, there's not a stopwords preprocessing.
    
    private final String m_strStopWordsFileName;
    
    // The Temp and Python directories are used in some 
    // tokenizer methods that uses the Python wrapper.
    // Path to the temp directory.
    
    private final String m_tempDir;
    
    // Python executable using the virtual environment.
    
    private final String m_pythonVenvDir;
    
    // Python script wrapper
    
    private final String m_pythonScriptDir;
    
    // Path to the pretrained model embedding 
    
    private final String m_modelDirPath;
    
    // Set with all the stop words
    
    private HashSet<String> m_stopWordsHashSet;
    
    // Define an internal Pattern parameter for checking if the input sentence 
    // has at least one alphanumeric character.
    
    private final Pattern m_pattern;

    /**
     * Constructor with parameters
     * @param tokenizerType tokenizer type used in the method
     * @param lowercaseNormalization true if lowercased
     * @param strStopWordsFileName stopWords file path
     * @param charFilteringType char filtering method used
     */
    
    WordProcessing(
            TokenizerType       tokenizerType,
            boolean             lowercaseNormalization,
            boolean             conceptsAnnotation,
            String              strStopWordsFileName,
            CharFilteringType   charFilteringType) throws IOException
    {
        // We save the preprocessing parameters
        
        m_tokenizerType = tokenizerType;
        m_lowercaseNormalization = lowercaseNormalization;
        m_strStopWordsFileName = strStopWordsFileName;
        m_charFilter = new CharsFiltering(charFilteringType);
        m_conceptsAnnotation = conceptsAnnotation;
        
        // Initialize Metamap to null
        
        m_metaMapLiteInst = null;
        
        // Load Metamap Lite if necessary
        
        if(m_conceptsAnnotation) try {
            loadMetamapLite();
        } catch (IllegalAccessException ex) {
            Logger.getLogger(WordProcessing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WordProcessing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(WordProcessing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(WordProcessing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Initialize the temporal dirs to null.
        
        m_tempDir = null;
        m_pythonVenvDir = null;
        m_pythonScriptDir = null;
        m_modelDirPath = null;
       
        // load the stop words in the constructor once
        
        getStopWords();
        
        // Compile the pattern in the constructor for efficiency reasons.
        
        m_pattern = Pattern.compile("[[:alnum:]]");
    }
    
    /**
     * Constructor with parameters when using the python wrapper.
     * 
     * @param tokenizerType tokenizer type used in the method
     * @param lowercaseNormalization true if lowercased
     * @param strStopWordsFileName stopWords file path
     * @param charFilteringType char filtering method used
     * 
     */
    
    WordProcessing(
            TokenizerType       tokenizerType,
            boolean             lowercaseNormalization,
            boolean             conceptsAnnotation,
            String              strStopWordsFileName,
            CharFilteringType   charFilteringType,
            String              tempDir,
            String              pythonVenvDir,
            String              pythonScriptDir,
            String              modelDirPath) throws IOException 
    {
        // We saev the tokeniztion parameters
        
        m_tokenizerType = tokenizerType;
        m_lowercaseNormalization = lowercaseNormalization;
        m_strStopWordsFileName = strStopWordsFileName;
        m_charFilter = new CharsFiltering(charFilteringType);
        m_conceptsAnnotation = conceptsAnnotation;
        
        // Initialize Metamap to null
        
        m_metaMapLiteInst = null;
        
        // Initialize the temporal dirs to null.
        
        m_tempDir = tempDir;
        m_pythonVenvDir = pythonVenvDir;
        m_pythonScriptDir = pythonScriptDir;
        m_modelDirPath = modelDirPath;
        
        // load the stop words
        
        getStopWords();
        
        // Load Metamap Lite if necessary
        
        if(m_conceptsAnnotation) try {
            loadMetamapLite();
        } catch (IllegalAccessException | ClassNotFoundException | 
                InstantiationException | NoSuchMethodException ex) {
            Logger.getLogger(WordProcessing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Compile the pattern in the constructor for efficiency reasons.
        
        m_pattern = Pattern.compile("[[:alnum:m_metaMapLiteInst]]");
    }

    /**
     * This function releases all resources used by the object.
     */
    
    @Override
    public void clear()
    {
        // Clear the objects after use them.
        
        m_charFilter.clear();
        m_stopWordsHashSet.clear();
    }
    
    /**
     * Get the tokens from a string sentence.
     * @param strRawSentence
     * @return String[] array of ordered tokens
     */
    
    @Override
    public String[] getWordTokens(
            String  strRawSentence) throws IOException, InterruptedException, Exception
    {
        // Initialize tokens
        
        String[] tokens = {};
        
        // We initialize the filtered sentence
        
        String strFilteredSentence = new String(strRawSentence);
        
        // If the sentence has at least one alphanumeric character, preprocess it.
        
        if(strFilteredSentence.length() > 2 
                && m_pattern.matcher(strFilteredSentence).find())
        {           
            // Annotate the text (if set)
            
            if(m_conceptsAnnotation) 
                strFilteredSentence = annotateSentence(strFilteredSentence);

            // Tokenize the text
            
            ITokenizer tokenizer = null;
            
            if(m_tokenizerType == TokenizerType.WordPieceTokenizer)
                tokenizer = new Tokenizer(m_tokenizerType, m_pythonVenvDir, 
                                            m_pythonScriptDir, m_modelDirPath);
            else
                tokenizer = new Tokenizer(m_tokenizerType);

            // We split the sentence into tokens

            String[] tokens_tokenized = tokenizer.getTokens(strFilteredSentence);

            // Initialize an auxiliary arraylist to store the preprocessed words

            ArrayList<String> lstWordsPreprocessed = new ArrayList();

            // Iterate the tokens and preprocess them
            // Preprocess each token and add to the output

            for (String token : tokens_tokenized)
            {
                String preprocessedToken = token;

                // Lowercase if true

                if (m_lowercaseNormalization) preprocessedToken = preprocessedToken.toLowerCase();

                // Filter the punctuation marks

                preprocessedToken = m_charFilter.filter(preprocessedToken);

                // Remove the word if its a stop word

                if(isStopword(preprocessedToken))
                    preprocessedToken = "";

                // Is there is a word, add to the new sentence

                if(preprocessedToken.length() > 0)
                {
                    lstWordsPreprocessed.add(preprocessedToken);
                }
            }

            // Convert the arraylist to a string array

            tokens = lstWordsPreprocessed.toArray(new String[0]);

            // Clear the arraylist

            lstWordsPreprocessed.clear();
        }
        
        // Return the tokens
        
        return (tokens);
    }
    
    /**
     * Get the stop words list
     * 
     * @throws FileNotFoundException
     * @throws IOException 
     */
    
    private void getStopWords() throws FileNotFoundException, IOException
    {
        // Initialize the set
        
        m_stopWordsHashSet = new HashSet<>();
        
        // Check if there is a stop words file and load it
        
        if ((m_strStopWordsFileName.length() > 0)
                && Files.exists(Paths.get(m_strStopWordsFileName)))
        {
            // Read the file and return the hash set

            FileReader fileReader = new FileReader(new File(m_strStopWordsFileName));
            BufferedReader buffer = new BufferedReader(fileReader);

            String line;

            while((line=buffer.readLine()) != null)
            {
                String stop = line.replaceAll(" ", "");
                m_stopWordsHashSet.add(stop);
            }

            // Close the file

            buffer.close();
            fileReader.close();
        }
    }
    
    /**
     * Check if the word is a stop word
     * @param strRawSentence
     * @return Boolean true if there is a stop word, false if not.
     */
    
    private Boolean isStopword(
            String          strWord) throws IOException
    {
        // Initialize the boolean value to false
        
        Boolean isStopWord = false;
        
        // If the set of stop words is not empty, remove the stop words

        if ((m_stopWordsHashSet != null) && !m_stopWordsHashSet.isEmpty())
        {
            // If the token is a stop word, remove

            if(m_stopWordsHashSet.contains(strWord))
            {
                isStopWord = true;
            }
        }
        
        // Return the stop words
        
        return isStopWord;
    }
    
    /**
     * This function loads the Metamap Lite instance before executing the queries.
     */
    
    private void loadMetamapLite() throws ClassNotFoundException, 
                                    InstantiationException, NoSuchMethodException, 
                                    IllegalAccessException, IOException
    {
        // Initialization Section
        
        Properties myProperties = new Properties();
        
        // Select the 2018AB database
        
        // myProperties.setProperty("metamaplite.index.directory", "../public_mm_lite/data/ivf/2018ABascii/USAbase/");
        myProperties.setProperty("metamaplite.index.directory", "../public_mm_lite/data/ivf/2020AA/USAbase/");
        myProperties.setProperty("opennlp.models.directory", "../public_mm_lite/data/models/");
        myProperties.setProperty("opennlp.en-pos.bin.path", "../public_mm_lite/data/models/en-pos-maxent.bin");
        myProperties.setProperty("opennlp.en-sent.bin.path", "../public_mm_lite/data/models/en-sent.bin");
        myProperties.setProperty("opennlp.en-token.bin.path", "../public_mm_lite/data/models/en-token.bin");
        
        myProperties.setProperty("metamaplite.sourceset", "MSH");

        // We create the METAMAP maname
        
        m_metaMapLiteInst = new MetaMapLite(myProperties);
        
    }
    
    /**
     * This function annotates a sentence with CUI codes replacing 
     * keywords with codes in the same sentence.
     * @return 
     */
    
    private String annotateSentence(
            String sentence) throws InvocationTargetException, IOException, Exception
    {
        // Initialize the result
        
        String annotatedSentence = sentence;
        
        // Processing Section

        // Each document must be instantiated as a BioC document before processing
        
        BioCDocument document = FreeText.instantiateBioCDocument(sentence);
        
        // Proccess the document with Metamap
        
        List<Entity> entityList = m_metaMapLiteInst.processDocument(document);

        // For each keyphrase, select the first CUI candidate and replace in text.
        
        for (Entity entity: entityList) 
        {
            for (Ev ev: entity.getEvSet()) 
            {
                // Replace in text
                
                annotatedSentence = annotatedSentence.replaceAll(entity.getMatchedText(), ev.getConceptInfo().getCUI());
            }
        }
        
        // Return the result
        
        return (annotatedSentence);
    }
}