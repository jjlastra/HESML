/*
 * Copyright (C) 2016 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml.benchmarks.impl;

/**
 * This class encapsulates two words and their baseline similarity.
 * The class is used to store one word pair in order to run the
 * word similarity benchmarks.
 * @author j.lastra
 */

class WordPairSimilarity implements Comparable<WordPairSimilarity>
{
    /**
     * Word 1 of the pair
     */
    
    private final String  m_Word1;
    
    /**
     * Word 2 of the pair
     */
        
    private final String  m_Word2;
    
    /**
     * Similarity defined by a human judgement
     */
    
    private final double  m_HumanJudgementSimilarity;
    
    /*
     * Unique ID indicating the word pair position in the collection
     * of word pairs within a dataset.
    */
    
    private final int   m_IndexId;
    
    /**
     * Constructor
     * @param indexId
     * @param word1
     * @param word2
     * @param similarity 
     */
    
    public WordPairSimilarity(
            int     indexId,
            String  word1,
            String  word2,
            double  similarity)
    {
        // We save the values
        
        m_IndexId = indexId;
        m_Word1 = word1;
        m_Word2 = word2;
        m_HumanJudgementSimilarity = similarity;
    }
    
    /**
     * This function returns the ID of the word pair.
     * @return Unique ID
     */

    public int getID()
    {
        return (m_IndexId);
    }
    
    /**
     * This functions returns the first word compared 1
     * @return First word compared
     */
    
    public String getWord1()
    {
        return (m_Word1);
    }
    
    /**
     * This function returns the second word compared
     * @return Second word compared
     */
    
    public String getWord2()
    {
        return (m_Word2);
    }    
    
    /**
     * Similarity defined for the word pair.
     * @return Human judgement of similarity between words
     */
    
    public double getHumanJudgement()
    {
        return (m_HumanJudgementSimilarity);
    }

    /**
     * This function compares two instances according to the similarity value.
     * @param other
     * @return Sign of the difference in similarity values
     */
    
    @Override
    public int compareTo(WordPairSimilarity other)
    {
        return ((int)Math.signum(m_HumanJudgementSimilarity - other.getHumanJudgement()));
    }
}
