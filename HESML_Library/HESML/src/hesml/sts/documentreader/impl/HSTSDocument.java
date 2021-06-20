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

package hesml.sts.documentreader.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import hesml.sts.documentreader.HSTSIDocument;
import hesml.sts.documentreader.HSTSIParagraph;
import hesml.sts.documentreader.HSTSIParagraphList;
import hesml.sts.documentreader.HSTSISentence;
import hesml.sts.documentreader.HSTSISentenceList;
import hesml.sts.preprocess.IWordProcessing;
import java.io.FileNotFoundException;

/**
 *  This class implements a HESMLSTS Document.
 *  A document is an object with a list of paragraphs.
 * 
 *  @author Alicia Lara-Clares
 */

class HSTSDocument implements HSTSIDocument
{
    // Id of the document
    
    private final int m_idDocument;
    
    // Document path
    
    private final String m_strDocumentPath;
    
    // List of paragraphs of the document
    
    private HSTSIParagraphList m_paragraphList;
    
    // Preprocesser object
    
    private final IWordProcessing m_preprocessing;
    
    /**
     * Creates an object document and extract the sentences.
     * @param strDocumentPath Path to the document.
     */
    
    public HSTSDocument(
            int             idDocument,
            String          strDocumentPath,
            IWordProcessing wordPreprocessing) 
    {
        // Set the variables
        
        m_idDocument = idDocument;
        m_strDocumentPath = strDocumentPath;
        m_preprocessing = wordPreprocessing;
        
        // Initialize the paragraph list.
        
        m_paragraphList = new HSTSParagraphList();
    }

    /**
     * Read the paragraphs from the document
     * @return ArrayList<IParagraphList> the list of paragraphs in the document.
     */
    
    @Override
    public HSTSIParagraphList getParagraphList()
    {
        return (m_paragraphList);
    }
    
    /**
     * Add a new Paragraph in the document
     * @return HSTSIParagraphList the list of paragraphs in the document.
     */
    
    @Override
    public void setParagraphList(
            HSTSIParagraphList paragraphList)
    {
        //Iterate paragraphs and add them to the list
        
        m_paragraphList = paragraphList;
    }
    
    /**
     * Append the sentences into a file.
     * 
     * @param fileOutput
     * @throws IOException 
     */
    
    @Override
    public void saveSentencesToFile(
        File fileOutput) throws IOException
    {
        // Create a StringBuilder object and fill with the sentences
        
        StringBuilder sb = new StringBuilder();
        
        // Iterate the paragraphs.  
        
        for (HSTSIParagraph paragraph : m_paragraphList) {
            HSTSISentenceList sentenceList = paragraph.getSentenceList();
            
            // iterate the sentences of each paragraph.
            
            for (HSTSISentence sentence : sentenceList) {
                
                // Get the sentence text and append it.
                
                String strSentence = sentence.getText();
                if(strSentence.length() > 0)
                {
                    sb.append(strSentence);
                    sb.append("\n");
                }
            }
        }
        
        // Get the output that will be written to the file
        
        String strFinalText = sb.toString();
        
        // Write the file
        FileWriter fileWriter = new FileWriter(fileOutput, true);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        
        // Append the text to the document
        
        writer.write(strFinalText);
        
        // Close the file
        
        writer.close();
        fileWriter.close();
    }

    /**
     * Get the document and preprocess it using IWordProcesser
     */
    
    @Override
    public void preprocessDocument() 
            throws FileNotFoundException, IOException, InterruptedException, Exception
    {
        // For each paragraph iterate the sentences
        
        for (HSTSIParagraph paragraph : m_paragraphList) 
        {
            // For each sentence get word tokens if it's not empty and has alphanumeric characters.
            
            HSTSISentenceList sentenceList = paragraph.getSentenceList();
            for (HSTSISentence sentence : sentenceList) 
            {
                // Get the sentence
                
                String strSentence = sentence.getText();
                if(strSentence.length() > 0)
                {
                    // Get the word tokens using the HESML STS preprocessed object
                    
                    String[] tokens = m_preprocessing.getWordTokens(strSentence);
                    if(tokens.length > 0)
                    {
                        // Join the tokens and create a new preprocessed sentence
                        
                        String newSentence = String.join(" ", tokens);
                        sentence.setText(newSentence); 
                    }
                }
                else
                {
                    sentence.setText(""); 
                }
            }
        }
    }
}
