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

package hesml.sts.preprocess.impl;


import gov.nih.nlm.nls.ner.MetaMapLite;
import hesml.sts.preprocess.CharFilteringType;
import hesml.sts.preprocess.INER;
import hesml.sts.preprocess.ITokenizer;
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.NERType;
import hesml.sts.preprocess.TokenizerType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 *  This class configures the preprocess method and preprocess the sentences.
 * @author alicia
 */

class WordProcessing implements IWordProcessing
{
    // Configure if the text would be lowercased.
    
    protected final boolean m_lowercaseNormalization; 
    
    // Configure the concept annotation
    
    protected NERType m_nerType;
    protected final INER m_ner;
    
    // Metamap Lite instance
    
    protected MetaMapLite m_metaMapLiteInst;

    // Set the tokenization method
    
    protected final TokenizerType m_tokenizerType; 
    
    // Set the filtering method
    
    protected final CharsFiltering m_charFilter; 

    // Stopwords filename and hashset. 
    // If empty, there's not a stopwords preprocessing.
    
    protected final String m_strStopWordsFileName;
    
    // The Temp and Python directories are used in some 
    // tokenizer methods that uses the Python wrapper.
    // Path to the temp directory.
    
    protected final String m_tempDir;
    
    // Python executable using the virtual environment.
    
    protected final String m_pythonVenvDir;
    
    // Python script wrapper
    
    protected final String m_pythonScriptDir;
    
    // Path to the pretrained model embedding 
    
    protected String m_modelDirPath;
    
    // Set with all the stop words
    
    protected HashSet<String> m_stopWordsHashSet;
    
    // Define an internal Pattern parameter for checking if the input sentence 
    // has at least one alphanumeric character.
    
    protected final Pattern m_pattern;

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
            NERType             nerType,
            String              strStopWordsFileName,
            CharFilteringType   charFilteringType) throws IOException
    {
        // We save the preprocessing parameters
        
        m_tokenizerType = tokenizerType;
        m_lowercaseNormalization = lowercaseNormalization;
        m_strStopWordsFileName = strStopWordsFileName;
        m_charFilter = new CharsFiltering(charFilteringType);
        m_nerType = nerType;
        
        // Initialize the Concept annotation method
        
        m_ner = new NER(m_nerType);
        
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
            NERType             nerType,
            String              strStopWordsFileName,
            CharFilteringType   charFilteringType,
            String              tempDir,
            String              pythonVenvDir,
            String              pythonScriptDir,
            String              modelDirPath) throws IOException 
    {
        // We save the tokeniztion parameters
        
        m_tokenizerType = tokenizerType;
        m_lowercaseNormalization = lowercaseNormalization;
        m_strStopWordsFileName = strStopWordsFileName;
        m_charFilter = new CharsFiltering(charFilteringType);
        m_nerType = nerType;
        
        // Initialize the Concept annotation method
        
        m_ner = new NER(m_nerType);
        
        // Initialize the temporal dirs to null.
        
        m_tempDir = tempDir;
        m_pythonVenvDir = pythonVenvDir;
        m_pythonScriptDir = pythonScriptDir;
        m_modelDirPath = modelDirPath;
        
        // load the stop words
        
        getStopWords();
        
        // Compile the pattern in the constructor for efficiency reasons.
        
        m_pattern = Pattern.compile("^.*[a-zA-Z0-9]+.*$");
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
        m_ner.clear();
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
            // In the Spanish version, the annotation process occurs at the end
            
            if(m_nerType != NERType.MetamapSNOMEDCT_SP &&
                    m_nerType != NERType.MetamapMESH_SP &&
                    m_nerType != NERType.None)
            {
                // Annotate the text (if set)
            
                strFilteredSentence = m_ner.annotate(strRawSentence);
            }
            
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
            
            // In the Spanish version, the annotation process occurs at the end
            
            if(m_nerType == NERType.MetamapSNOMEDCT_SP ||
                    m_nerType == NERType.MetamapMESH_SP)
            {
                // Create an auxiliary string
                
                String preprocessedSentNotAnn = String.join(" ", lstWordsPreprocessed);
                
                // Annotate the text (if set)
            
                preprocessedSentNotAnn = m_ner.annotate(preprocessedSentNotAnn);
                
                // Convert to list
                
                tokens = preprocessedSentNotAnn.split(" ");
            }
            else
            {
                // Convert the arraylist to a string array

                tokens = lstWordsPreprocessed.toArray(new String[0]);
            }
            
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
     * Dynamically assign a label or name for a Wordprocessing object 
     * using the parameters configuration.
     * 
     * @return label
     */
    
    @Override
    public String getLabel() 
    {
        // Initialize the result filename
        
        String label = "";
        
        // Add the bert vocabulary if exists
        
        if(m_modelDirPath != null)
        {
            String[] modelDirList = m_modelDirPath.split("/");
            String modelDir = modelDirList[modelDirList.length-1];
            label = modelDir;
        }
        
        // Add the tokenizer method
        
        if(label == "")
            label = "TOK-" + m_tokenizerType.toString();
        else
            label = label + "_TOK-" + m_tokenizerType.toString();
        
        // Add the lowercase info
        
        if(m_lowercaseNormalization)
            label = label + "_lc";
        else
            label = label + "_notlc";
        
        // Stop words filename
        
        String[] stopwords = m_strStopWordsFileName.split("/");
        String cleanedStopWordsFilename = stopwords[stopwords.length-1].replace(".txt","");
        label = label + "_SW-" + cleanedStopWordsFilename;
        
        // Add the char filtering method
        
        label = label + "_CF-" + m_charFilter.to_string();
        
        // Add the conceptAnnotationInfo
        
        label = label + "_NER-" + m_nerType;
        
        // lowercase the result
        
        label = label.toLowerCase();
        
        // Return the result
        
        return (label);
    }

    @Override
    public void setNERType(NERType nerType) 
    {
        m_nerType = nerType;
    }

    @Override
    public void setBERTModel(String bertmodel) 
    {
        m_modelDirPath = bertmodel;
    }
}