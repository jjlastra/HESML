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
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class implements a similarity function based on the word vectors
 * provided by the pre-trained word embedding model contained in the
 * companion vector files. This class implements the reader and evaluator
 * of the Nasari file format made up by two files, one file containing the
 * word senses and other containing the vectors.
 * 
 * This class evaluates word embedding models introduced by 
 * Camacho-Collados, J., Pilehvar, M. T., & Navigli, R. (2015).
 * A Unified Multilingual Semantic Representation of Concepts.
 * In Proc. of the 53rd Annual Meeting of the Association for
 * Computational Linguistics and the 7th International Joint
 * Conference on Natural Language Processing (ACL-IJCNLP 2015).
 * Beijing, China: ACL.
 * @author j.lastra
 */

class NasariWordEmbeddingModel implements IWordSimilarityMeasure
{
    /**
     * This file contains the sense vectors provided by the pre-trained
     * embedding model defining this measure.
     */
    
    private final String  m_strSenseVectorsFilename;
    
    /**
     * This file contains the word senses
     */
    
    private final String    m_strWordSensesFilename;
    
    /**
     * Senses for each word
     */
    
    private final HashMap<String, HashSet<String>>  m_WordSenses;
    
    /**
     * Weight vector for each sense
     */
    
    private final HashMap<String, HashMap<String, Double>>  m_SensesCoords;
    
    /**
     * Constructor
     * @param strVectorFilename 
     */
    
    NasariWordEmbeddingModel(
            String  strWordSenseFilename,
            String  strSenseVectorFilename) throws IOException
    {
        // We save the two filenames
        
        m_strWordSensesFilename = strWordSenseFilename;
        m_strSenseVectorsFilename = strSenseVectorFilename;
        
        // We initialize the overall tables
                        
        m_WordSenses = new HashMap<>();
        m_SensesCoords = new HashMap<>();
        
        // We load the word senses and coords
        
        loadWordSenses();
        loadSenseVectors();
    }
    
    /**
     * This function loads the word senses
     */
    
    private void loadWordSenses() throws IOException
    {
        // Loading the word senses
                
        System.out.println("Loading the Nasari word senses");
        
        // We open for reading the vectors file
        
        BufferedReader reader = new BufferedReader(new FileReader(m_strWordSensesFilename));

        // We search for the word within the vectors file
        
        String strLine = reader.readLine();
        
        while (strLine != null)
        {
            // We extract the next word-sense pair

            String[] strFields = strLine.split("\\t");

            // We check the first field for the input word and
            // save the sense

            if (strFields.length == 2)
            {
                // We check for the existence of the word
                
                if (!m_WordSenses.containsKey(strFields[0]))
                {
                    HashSet<String> senses = new HashSet<>();
                    m_WordSenses.put(strFields[0], senses);
                }
                
                // We save the sense of the word
                
                m_WordSenses.get(strFields[0]).add(strFields[1]);
            }

            // We read the next line
            
            strLine = reader.readLine();
        }
        
        // We close the file
        
        reader.close();
    }

    /**
     * This function loads the sense vectors
     */
    
    private void loadSenseVectors() throws IOException
    {
        // Loading the sense vectors
                
        System.out.println("Loading the Nasari sense vectors");
        
        // We open for reading the vectors file
        
        BufferedReader reader = new BufferedReader(new FileReader(m_strSenseVectorsFilename));

        // We search for the sense within the vectors file
        
        String strLine = reader.readLine();
        
        while (strLine != null)
        {
            // We extract the next word-sense pair

            String[] strFields = strLine.split("\\t");

            // We initializae the output

            HashMap<String, Double> senseCoords = new HashMap<>();
            
            // We save the sense entry
            
            m_SensesCoords.put(strFields[0], senseCoords);
            
            // We check the first field for the input word

            for (int i = 2; i < strFields.length; i++)
            {
                // We split the vector into sense and weight

                String[] strSenseWeightPair = strFields[i].split("_");

                if (strSenseWeightPair.length == 2)
                {
                    // We retrieve the sense and weight value

                    String strCoordSense = strSenseWeightPair[0];

                    double weight = Double.parseDouble(strSenseWeightPair[1]);

                    // We add the new sense to the word

                    if (!senseCoords.containsKey(strCoordSense))
                    {
                        senseCoords.put(strCoordSense, weight);
                    }
                }
            }

            // We read the next line
            
            strLine = reader.readLine();
        }
        
        // We close the file
        
        reader.close();
    }
    
    /**
     * This function returns the name of the vectors file.
     * @return 
     */
    
    @Override
    public String toString()
    {
        return (m_strSenseVectorsFilename);
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
        // We initialize the null value
        
        double similarity = 0.5;
        
        // We obtain the word senses
        
        HashSet<String> senses1 = m_WordSenses.get(strWord1);
        HashSet<String> senses2 = m_WordSenses.get(strWord2);

        // We check the existence of the words in the dictionary
        
        if (senses1.isEmpty() || senses2.isEmpty())
        {
            similarity = 0.5;
        }
        else
        {
            // We search for the highest similarity value between both sense sets

            similarity = 0.0;
            
            for (String strSense1 : senses1)
            {
                // We check if both words are synomnims

                if (senses2.contains(strSense1))
                {
                    similarity = 1.0;
                    break;
                }

                // We compute the similarity between senses

                for (String strSense2: senses2)
                {
                    // We compute the weighted overlap
                    
                    double weightedOverlap = getWeightedOverlap(strSense1, strSense2);
                    
                    // We save the maximum value
                    
                    similarity = Math.max(similarity, weightedOverlap);
                }
            }
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function computes the weighted overlap as detaield in the papers
     * [1] and [2] below.
     * 
     * [1] Pilehvar, M. T., Jurgens, D., & Navigli, R. (2013).
     * Align, disambiguate and walk: A unified approach for measuring semantic
     * similarity. In Proceedings of the 51st Annual Meeting of the ACL
     * (Volume 1: Long Papers) (Vol. 1, pp. 1341–1351).
     * 
     * [2] Camacho-Collados, J., Pilehvar, M. T., & Navigli, R. (2015).
     * A Unified Multilingual Semantic Representation of Concepts.
     * In Proc. of the 53rd Annual Meeting of the ACL and the 7th International
     * Joint Conference on Natural Language Processing (ACL-IJCNLP 2015).
     * Beijing, China: ACL.
     * 
     */
    
    private double getWeightedOverlap(
            String  strSense1,
            String  strSense2) throws IOException
    {
        // We initiliaze the output
        
        double weightedOverlap = 0.0;
        
        // We get the sense vectors for each sense
        
        HashMap<String, Double> vector1 = m_SensesCoords.get(strSense1);
        HashMap<String, Double> vector2 = m_SensesCoords.get(strSense2);
        
        // We initialize the score
        
        double score_prov = 0.0;
        double normalization = 0.0;
        
        int cont = 0;
        
        for (String word: vector1.keySet())
        {
            if (vector2.containsKey(word))
            {
                cont++;
                normalization += 1.0 / (cont*2.0);
                score_prov += 1.0 / (vector1.get(word) + vector2.get(word));
            }
        }
    
        // We compute the final weighted overlap
        
        if (cont > 0)
        {
            weightedOverlap = Math.sqrt(score_prov/normalization);
        }

        // We return the result
        
        return (weightedOverlap);
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
        return (0.5);
    }
}
