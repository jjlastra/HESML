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
import java.util.HashMap;

/**
 * This class implements a cosine-similarity function based on the word vectors
 * provided by the pre-trained word embedding model contained in the
 * companion vector file. This class implements the reader and evaluator
 * of the EMB file format
 * 
 * @author j.lastra
 */

class UKBppvWordEmbeddingModel implements IWordSimilarityMeasure
{
    /**
     * This file contains the word vectors provided by the pre-trained
     * embedding model defining this measure.
     */
    
    private final String  m_strRawPretrainedEmbeddingFilename;
    
    /**
     * Constructor
     * @param strVectorFilename 
     */
    
    UKBppvWordEmbeddingModel(
        String  strVectorFilename)
    {
        m_strRawPretrainedEmbeddingFilename = strVectorFilename;
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
        return (SimilarityMeasureType.UKBppvEmbedding);
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
        
        // We get vectors representing both words in the sparse
        // synset-based representation of UKB
        
        HashMap<String, Double> word1 = getWordVector(strWord1);
        HashMap<String, Double> word2 = getWordVector(strWord2);
        
        // We check the validity of the word vectors. They could be null if
        // any word is not contained in the vocabulary of the embedding.
        
        if ((word1 != null) && (word2 != null))
        {
            // We compute the cosine similarity function (dot product)

            for (String strSynset1: word1.keySet())
            {
                if (word2.containsKey(strSynset1))
                {
                    similarity += word1.get(strSynset1) * word2.get(strSynset1);
                }
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
        HashMap<String, Double> sparseVector)
    {
        double norm = 0.0;  // Returned value
        
        // We compute the acumulated square-coordinates
        
        for (Double coord: sparseVector.values())
        {
            norm += coord * coord;
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
    
    private HashMap<String, Double> getWordVector(
            String strWord) throws FileNotFoundException, IOException
    {
        // We initialize the output
        
        HashMap<String, Double> vector = new HashMap<>();
        
        // We open for reading the vectors file
        
        BufferedReader reader = new BufferedReader(new FileReader(m_strRawPretrainedEmbeddingFilename));
        
        // We search for the word within the vectors file
        
        String strLine;
        
        do
        {
            // We read the next line
            
            strLine = reader.readLine();
            
            // We extract the fields in line
            
            if (strLine != null)
            {
                String[] strFields = strLine.split("\\t| |,|;");
            
                // We check the first field for the input word
                
                //if ((strFields.length > 0) && (strFields[0].trim().equals(strWord)))
                {
                    // We create the vector

                    /*vector = new double[strFields.length - 1];

                    // We copy the coordinates

                    for (int i = 0; i < vector.length; i++)
                    {
                        vector[i] = Double.parseDouble(strFields[i + 1]);
                    }

                    break;*/
                }
            }
            
        } while (strLine != null);
         
        // We close the file
            
        reader.close();
        
        // We return the result
        
        return (null);
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
