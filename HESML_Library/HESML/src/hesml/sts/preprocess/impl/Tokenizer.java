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

import bioc.preprocessing.pipeline.PreprocessingPipeline;
import edu.stanford.nlp.ling.CoreLabel;
import hesml.sts.preprocess.ITokenizer;
import hesml.sts.preprocess.TokenizerType;

import edu.stanford.nlp.simple.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *  Implementation of the tokenization methods
 * @author alicia
 */

class Tokenizer implements ITokenizer
{
    // Type of tokenization method
    
    private final TokenizerType m_tokenizerType;
    
    // Python executable using the virtual environment.
    
    private final String m_pythonVenvDir;
    
    // Python script wrapper
    
    private final String m_pythonScriptDir;
    
    // Path to the model dir path
    
    private final String m_modelDirPath;
    
    /**
     * Constructor with parameters.
     * @param tokenizerType 
     */
    
    Tokenizer(
            TokenizerType tokenizerType)
    {
        // Set the tokenizer type 
        
        m_tokenizerType = tokenizerType;
        
        // If the Python wrapper is not used, set the variables null.
        
        m_pythonVenvDir = null;
        m_pythonScriptDir = null;
        m_modelDirPath = null;
    }
    
    /**
     * Constructor with parameters using the python wrapper.
     * 
     * @param TempDir
     * @param PythonVenvDir
     * @param PythonScriptDir
     * @throws IOException 
     */
    
    Tokenizer(
            TokenizerType   tokenizerType,
            String          PythonVenvDir,
            String          PythonScriptDir,
            String          modelDirPath) throws IOException
    {
        // Set the variables by constructor
        
        m_tokenizerType = tokenizerType;
        m_pythonScriptDir = PythonScriptDir;
        m_pythonVenvDir = PythonVenvDir;
        m_modelDirPath = modelDirPath;
    }
    
    /**
     * This function returns the current tokenizer method.
     * @return TokenizerType
     */
    
    @Override
    public TokenizerType getTokenizerType()
    {
        return m_tokenizerType;
    }

    /**
     * Get the tokens from a sentence
     * @param strRawSentence
     * @return String[] list of tokens of the current sentence
     */
    
    @Override
    public String[] getTokens(
            String  strRawSentence) throws InterruptedException, IOException
    {
        // Initialize the output
        
        String[] tokens = {}; 
        
        // We create the tokenizer

        switch (m_tokenizerType)
        {
            case WhiteSpace:

                // Split words by whitespace.

                tokens = strRawSentence.split("\\s+"); 

                break;

            case StanfordCoreNLPv3_9_1:

                // Convert to a Stanford CoreNLP Sentence object and get the tokens.

                Sentence sent = new Sentence(strRawSentence);

                // Get the list of tokenized words and convert to array.

                tokens = sent.words().toArray(new String[0]); 

                break;

            case WordPieceTokenizer:

                tokens = this.getTokensUsingWordPiecePythonWrapper(strRawSentence);

                break;

            case BioCNLPTokenizer:

                // Use the BioC NLP library to tokenize the sentence.

                tokens = this.getTokensBioCNLPLibrary(strRawSentence);

                break;
        }
        
        // Return the tokens
            
        return tokens;
    }

    /**
     * Execute the wrapper for get the tokenized texts.
     * 
     * @throws InterruptedException
     * @throws IOException 
     * @return String[] array of tokens
     */
    
    private String[] getTokensUsingWordPiecePythonWrapper(
            String  strSentence) 
            throws InterruptedException, IOException
    {
        //Initialize the output
        
        String[] tokenizedText = {};
                
        // Fill the command params and execute the script
        // Create the command 

        Process proc = new ProcessBuilder(
                m_pythonVenvDir,
                "-W",
                "ignore",
                m_pythonScriptDir,
                m_modelDirPath + "/vocab.txt",
                strSentence).start();
        
        // Read the output 
        
        InputStreamReader inputStreamReader = new InputStreamReader(proc.getInputStream());
        BufferedReader readerTerminal = new BufferedReader(inputStreamReader);

        // Read the sentence and split by whitespaces
        
        String sentence = readerTerminal.readLine();
        sentence = sentence.trim();
        tokenizedText = sentence.split(" ");
        
        // Close the reader
        readerTerminal.close();
        inputStreamReader.close();
        
        // Destroy the process
        
        proc.waitFor();  
        proc.destroy();
        
        // Return the result
        
        return tokenizedText;
    }
    
    /**
     * Use the BioC NLP library to tokenize the text.
     * 
     * Comeau, Donald C., Rezarta Islamaj Doğan, Paolo Ciccarese, 
     * Kevin Bretonnel Cohen, Martin Krallinger, Florian Leitner, 
     * Zhiyong Lu, et al. 2013. “BioC: A Minimalist Approach to Interoperability 
     * for Biomedical Text Processing.” Database: The Journal of Biological 
     * Databases and Curation 2013 (September): bat064.
     * 
     * @param strSentence
     * @return String[] array with the tokens of the sentence
     */
    
    private String[] getTokensBioCNLPLibrary(
            String strSentence)
    {
        // Initialize the output

        String[] tokens = {};

        // Create a BioC Processing Pipeline object and set the sentence text.

        PreprocessingPipeline preprocessingPipeline = new PreprocessingPipeline("sentence");
        preprocessingPipeline.setParseText(strSentence);

        // Perform the sentence splitting (which firstly performs a tokenization process)

        List<List<CoreLabel>> sentencesCoreLabels = preprocessingPipeline.performSentenceSplit();

        // Iterate the tokens in the sentence and write the token in the list

        ArrayList<String> tokenizedTokens = new ArrayList();

        List<CoreLabel> sentenceCoreLabel = sentencesCoreLabels.get(0);

        for(CoreLabel token: sentenceCoreLabel)
        {
            // Extract the token from the sentence

            String word = strSentence.substring(token.beginPosition(), token.endPosition());

            // Add the token to the list

            tokenizedTokens.add(word);
        }

        // Convert the arraylist to an array

        tokens = tokenizedTokens.toArray(new String[0]);

        // Clear the arraylist

        tokenizedTokens.clear();

        // Return the results

        return tokens;
    }
}