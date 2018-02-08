/*
 * Copyright (C) 2016-2018 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml.measures.impl;

import hesml.measures.IWordSimilarityMeasure;
import hesml.measures.SimilarityMeasureClass;
import hesml.measures.SimilarityMeasureType;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class implements a cosine-similarity function based on the word vectors
 * provided by the pre-trained word embedding model contained in the
 * companion vector file. This class implements the reader and evaluator
 * of the EMB file format
 * 
 * @author j.lastra
 */

class EMBWordEmbeddingModel implements IWordSimilarityMeasure
{
    /**
     * This file contains the word vectors provided by the pre-trained
     * embedding model defining this measure.
     */
    
    private final String  m_strRawPretrainedEmbeddingFilename;
    
    /**
     * Buffer saving the word vectors to be used in a similarity benchmark.
     */
    
    private HashMap<String, Double[]>  m_bufferedWordVectors;
    
    /**
     * Constructor
     * @param strVectorFilename 
     */
    
    EMBWordEmbeddingModel(
        String      strVectorFilename,
        String[]    words) throws IOException, ParseException
    {
        m_strRawPretrainedEmbeddingFilename = strVectorFilename;
        m_bufferedWordVectors = new HashMap<>();
        
        // We loand only those word vectors to be evaluated
        
        loadBufferedWordVectors(words);
    }
    
    /**
     * This function retrieves all sense vectors corresponding to the
     * senses of the input words.
     * @param words 
     */
    
    private void loadBufferedWordVectors(
        String[]    words) throws IOException, ParseException
    {
        // We create the list with all input words
        
        HashSet<String> pendingWords = new HashSet<>();
        
        for (String strWord: words)
        {
            pendingWords.add(strWord);
        }
        
        // Debug message
        
        System.out.println("Loading words vectors to be evaluated");
        
        // We scan the sense vector file to retrieve all senses at the same time
        
        BufferedReader reader = new BufferedReader(new FileReader(m_strRawPretrainedEmbeddingFilename), 1000000);
        
        String strLine = reader.readLine();
        
        while ((strLine != null) && (pendingWords.size() > 0))
        {
            // We get the synset of the line
            
            String strWord = strLine.substring(0, strLine.indexOf(" "));
            
            // We check if the sense is in the list
            
            if (pendingWords.contains(strWord))
            {
                // We save the sense vector
                
                m_bufferedWordVectors.put(strWord, parseWordVector(strLine));
                
                // We remove the sense from list
                
                pendingWords.remove(strWord);
            }
            
            // We read the next line
            
            strLine = reader.readLine();
        }
               
        // We close the file
        
        reader.close();
    }
    
    /**
     * This function is called with the aim of releasing all resources used
     * by the measure.
     */
    
    @Override
    public void clear()
    {
    }
    
    /**
     * This function returns the name of the vectors file.
     * @return 
     */
    
    @Override
    public String toString()
    {
        return (m_strRawPretrainedEmbeddingFilename);
    }
    
    /**
     * This function returns the similarity measure class.
     * @return 
     */
    
    @Override
    public SimilarityMeasureClass getMeasureClass()
    {
        return (SimilarityMeasureClass.Similarity);
    }

    /**
     * This function returns the measure type.
     * @return 
     */
    
    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.EMBWordEmbedding);
    }
    
    /**
     * This function returns the semantic measure between two words.
     * @param strWord1 The first word
     * @param strWord2 The second word
     * @return 
     * @throws java.lang.InterruptedException 
     */

    @Override
    public double getSimilarity(
            String strWord1,
            String strWord2) throws InterruptedException, Exception
    {
        double similarity = 0.0;    // Returned value
        
        // We get vectors representing both words
        
        Double[] word1 = m_bufferedWordVectors.get(strWord1);
        Double[] word2 = m_bufferedWordVectors.get(strWord2);
        
        // We check the validity of the word vectors. They could be null if
        // any word is not contained in the vocabulary of the embedding.
        
        if ((word1 != null)
                && (word2 != null)
                && (word1.length == word2.length))
        {
            // We compute the cosine similarity function (dot product)
            
            for (int i = 0; i < word1.length; i++)
            {
                similarity += word1[i] * word2[i];
            }
            
            // We divide by the vector norms
            
            similarity /= (getVectorNorm(word1) * getVectorNorm(word2));
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function computes the Euclidean norm of the input vector
     * @param vector
     * @return 
     */
    
    private double getVectorNorm(
        Double[]    vector)
    {
        double norm = 0.0;  // Returned value
        
        // We compute the acumulated square-coordinates
        
        for (int i = 0; i < vector.length; i++)
        {
            norm += vector[i] * vector[i];
        }
        
        // Finally, we compute the square root
        
        norm = Math.sqrt(norm);
        
        // We return the result
        
        return (norm);
    }
    
    /**
     * This function retrieves the word vector from the vectors file
     * @param strWord
     * @return 
     */
    
    private Double[] parseWordVector(
            String strLine) throws FileNotFoundException, IOException
    {
        // We initialize the output
        
        String[] strFields = strLine.split("\\t| |,|;");

        // We create the vector

        Double[] vector = new Double[strFields.length - 1];

        // We copy the coordinates

        for (int i = 0; i < vector.length; i++)
        {
            vector[i] = Double.parseDouble(strFields[i + 1]);
        }
        
        // We return the result
        
        return (vector);
    }

    /**
     * This function returns the value returned by the similarity measure when
     * there is none similarity between both input concepts, or the concept
     * is not contained in the taxonomy.
     * @return 
     */
    
    @Override
    public double getNullSimilarityValue()
    {
        return (0.0);
    }
}
