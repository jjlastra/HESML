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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.SortedMap;

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
     * Word senses
     */
    
    private final HashMap<String, HashSet<String>>    m_WordSenses;
    
    /**
     * Buffered Sense vectors
     */
    
    private final HashMap<String, HashMap<String, Integer>>  m_BufferedSenseVectors;
    
    /**
     * Constructor
     * @param strVectorFilename 
     */
    
    NasariWordEmbeddingModel(
            String      strWordSenseFilename,
            String      strSenseVectorFilename,
            String[]    words) throws IOException, ParseException
    {
        // We save the two filenames
        
        m_strSenseVectorsFilename = strSenseVectorFilename;
        
        // We initialize the overall tables
                        
        m_WordSenses = new HashMap<>();
        m_BufferedSenseVectors = new HashMap<>();
        
        // We load the word senses and coords
        
        loadWordSenses(strWordSenseFilename);
        loadBufferedSenseVectors(words);
    }
    
    /**
     * This function retrieves all sense vectors corresponding to the
     * senses of the input words.
     * @param words 
     */
    
    private void loadBufferedSenseVectors(
        String[]    words) throws IOException, ParseException
    {
        // We create the list of all senses of the input words
        
        HashSet<String> pendingSenses = new HashSet<>();
        
        // We iterate on words to get their senses
        
        for (int i = 0; i < words.length; i++)
        {
            if (m_WordSenses.containsKey(words[i]))
            {
                pendingSenses.addAll(m_WordSenses.get(words[i]));
            }
        }
        
        // Debug message
        
        System.out.println("Loading sense vectors of words to be evaluated");
        
        // We scan the sense vector file to retrieve all senses at the same time
        
        BufferedReader reader = new BufferedReader(new FileReader(m_strSenseVectorsFilename), 1000000);
        
        String strLine = reader.readLine();
        
        while ((strLine != null) && (pendingSenses.size() > 0))
        {
            // We get the synset of the line
            
            String strSense = strLine.substring(0, strLine.indexOf("\t"));
            
            // We check if the sense is in the list
            
            if (pendingSenses.contains(strSense))
            {
                // We save the sense vector
                
                m_BufferedSenseVectors.put(strSense, parseSenseVector(strLine));
                
                // We remove the sense from list
                
                pendingSenses.remove(strSense);
            }
            
            // We read the next line
            
            strLine = reader.readLine();
        }
        
        // Debug message
        
        System.out.println("Finished the the sense vector buffering, senses = ");
        
        // We close the file
        
        reader.close();
    }
    
    /**
     * This function loads the word senses.
     * @param strWordSensesFilename 
     */
    
    private void loadWordSenses(
        String  strWordSensesFilename) throws FileNotFoundException, IOException
    {
        // Debugging message
        
        System.out.println("Loading word senses from " + strWordSensesFilename);
        
        // We open the file to read all word senses with a buffer of 10 Mbytes
        
        BufferedReader reader = new BufferedReader(new FileReader(strWordSensesFilename), 1000000);
        
        // We read all word senses
        
        String strLine  = reader.readLine();
        
        while (strLine != null)
        {
            // We split the line into fields
            
            String[] strFields = strLine.split("\t");
            
            if (strFields.length == 2)
            {
                // We get the word
                
                String strWord = strFields[0];
                
                // We check if the word is already in the table
                
                if (!m_WordSenses.containsKey(strWord))
                {
                    m_WordSenses.put(strWord, new HashSet<>());
                }
                
                // We add the sense
                
                m_WordSenses.get(strWord).add(strFields[1]);
            }
            
            // We read the next line
            
            strLine = reader.readLine();
        }
        
        // We close the file
        
        reader.close();
    }

    /**
     * This function parses a text line in order to retrieve the synset
     * vector of an input sense.
     * @param strSenseLine
     * @return 
     */
    
    private HashMap<String, Integer> parseSenseVector(
            String strSenseLine) throws ParseException
    {
        // We initialize the output
        
        HashMap<String, Integer> ranksVector = new HashMap<>();
        
        // We split into fields

        String[] strFields = strSenseLine.split("\t");

        // We read all vector components which are ranked in accordance
        // with their weigths. We are assuming that the vectors are already
        // sorted by their weights.

        for (int i = 2, rank = 1; i < strFields.length; i++, rank++)
        {
            String[] strSenseWeight = strFields[i].split("_");

            if (strSenseWeight.length == 2)
            {
                ranksVector.put(strSenseWeight[0], rank);
            }
        }

        // We return the result
        
        return (ranksVector);
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
        
        if (!senses1.isEmpty() && !senses2.isEmpty())
        {
            // We search for the highest similarity value between both sense sets

            boolean synonyms = false;
            
            for (String strSense1 : senses1)
            {
                if (senses2.contains(strSense1))
                {
                    similarity = 1.0;
                    synonyms = true;
                    break;
                }
            }
            
            // We evaluate the Cartesian product lookig for the
            // highest similarity value

            if (!synonyms)
            {
                similarity = 0.0;
            
                for (String strSense1: senses1)
                {
                    for (String strSense2: senses2)
                    {
                        // We check that both sense vectors exist

                        if (m_BufferedSenseVectors.containsKey(strSense1)
                            && m_BufferedSenseVectors.containsKey(strSense2))
                        {
                            similarity = Math.max(similarity, getWeightedOverlap(strSense1, strSense2));
                        }
                    }
                }
            }
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function computes the weighted overlap as detailed in the papers
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
        // We retieve the sense vectors
        
        HashMap<String, Integer> vector1 = m_BufferedSenseVectors.get(strSense1);
        HashMap<String, Integer> vector2 = m_BufferedSenseVectors.get(strSense2);
        
        // We initialize the score and counters
        
        double score_prov = 0.0;
        double normalization = 0.0;
        
        int cont = 0;
        
        // We compute the weighted overlap score
        
        for (String word: vector1.keySet())
        {
            if (vector2.containsKey(word))
            {
                cont += 1;
                normalization += 1.0 / (2.0 * cont);
                score_prov += 1.0 / (vector1.get(word) + vector2.get(word));
            }
        }
    
        // We compute the final weighted overlap
        
        double weightedOverlap = (cont > 0) ? Math.sqrt(score_prov/normalization) : 0.0;
        
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
