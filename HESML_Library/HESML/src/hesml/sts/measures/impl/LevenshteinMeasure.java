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

package hesml.sts.measures.impl;

import hesml.sts.measures.IStringBasedSentenceSimMeasure;
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.measures.StringBasedSentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *  This class implements the Levenshtein Measure.
 * 
 *  * In this implementation, the cost of insert and deletions are 1 by default.
 * 
 *  Levenshtein, Vladimir I. 1966. “Binary Codes Capable of 
 *  Correcting Deletions, Insertions, and Reversals.” 
 *  In Soviet Physics Doklady, 10:707–10. nymity.ch.
 * 
 *  @author alicia
 */

class LevenshteinMeasure extends SentenceSimilarityMeasure
                         implements IStringBasedSentenceSimMeasure
{
    // Internal variables used in the method.
    // @param insertDeleteCost: positive non-zero cost of an insert or deletion operation
    // @param substituteCost: positive cost of a substitute operation
    // @param maxCost: max(insertDelete, substitute)
    
    private final double m_insertDeleteCost;
    private final double m_substituteCost;
    private double m_maxCost;
    
    /**
     * label shown in all raw matrix results
     */
    
    private final String m_strLabel;
    
    /**
     * Constructor with parameters.
     * @param preprocesser 
     */
    
    LevenshteinMeasure(
            String          strLabel,
            IWordProcessing preprocesser,
            int             insertDeleteCost,
            int             substituteCost)
    {
        // We intialize the base class
        
        super(preprocesser);
        
        // Initialize the default internal variables
        
        m_insertDeleteCost = insertDeleteCost;
        m_substituteCost = substituteCost;
        m_maxCost = Math.max(m_insertDeleteCost, m_substituteCost);
        m_strLabel = strLabel;
    }
    
    /**
     * This function returns the label used to identify the measure in
     * a raw matrix results. This string attribute is set by the users
     * to provide the column header name included in all results generated
     * by this measure. This attribute was especially defined to
     * provide a meaningful name to distinguish the measures based on
     * pre-trained model files.
     * @return label
     */
    
    @Override
    public String getLabel()
    {
        return (m_strLabel);
    }
    
    /**
     * This function returns the sentence method used.
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return (SentenceSimilarityMethod.Levenshtein);
    }

    /**
     * This function returns the family of the current sentence similarity method.
     * @return SentenceSimilarityFamily
     */
    
    @Override
    public SentenceSimilarityFamily getFamily()
    {
        return (SentenceSimilarityFamily.String);
    }
    
    /**
     * This function returns the String method.
     * @return StringBasedSentenceSimilarityMethod
     */
    
    @Override
    public StringBasedSentenceSimilarityMethod getStringBasedMethodType()
    {
        return (StringBasedSentenceSimilarityMethod.Levenshtein);
    }
    
    /**
     * Get the similarity value for the Levenshtein distance.
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return
     * @throws IOException 
     */
    
    @Override
    public double getSimilarityValue(
            String strRawSentence1, 
            String strRawSentence2) 
            throws IOException, FileNotFoundException, FileNotFoundException, InterruptedException, Exception
    {
        // We initialize the output

        double similarity = 0.0;
        
        // We first calculate the Levenshtein distance
        
        double distance = 0.0;
        
        // Get the tokens for each sentence

        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
        
        // Join the words and get a preprocessed sentence
        
        String sentence1 = String.join(" ", lstWordsSentence1);
        String sentence2 = String.join(" ", lstWordsSentence2);
        
        // The similarity is 1 if the two lists are empty
        
        if (lstWordsSentence1.length == 0 && lstWordsSentence2.length == 0) 
            return 1.0f;
        
        // Calculate the distance between the sentences
        
        distance = distance(sentence1, sentence2);

        // The max cost is the maximum number between 
        // the costs of insert and substitution
        // By default is 1
        
        m_maxCost = Math.max(m_insertDeleteCost, m_substituteCost);
        
        // Calculate the similarity
        
	similarity = 1.0 - (distance / (this.m_maxCost * 
                        Math.max(sentence1.length(), sentence2.length())));
        
        // Return the result
        
        return (similarity);
    }
    
    /**
     *  This function calculates the distance between two strings 
     *  using the Levenshtein distance.
     * 
     * @param s
     * @param t
     * @return
     */
    
    private double distance(
            final String strSentence1, 
            final String strSentence2)
    {
        // We initialize the result
        
        double distanceValue = 0.0;
                
        // if there is an empty sentence, the total cost will be the other sentence lenght.
        
        if (strSentence1.isEmpty())
        {
            distanceValue = strSentence2.length();
        }
        else if (strSentence2.isEmpty())
        {
            distanceValue = strSentence1.length();
        }
        else if (strSentence1.equals(strSentence2))
        {
            distanceValue = 0.0;
        }
        else
        {
            // Initialize the vectors cost

            double[] swap;
            double[] costVectorSentence1 = new double[strSentence2.length() + 1];
            double[] costVectorSentence2 = new double[strSentence2.length() + 1];

            // initialize costVectorSentence1 (the previous row of distances)
            // this row is A[0][i]: edit distance for an empty s
            // the distance is just the number of characters to delete from t

            for (int i = 0; i < costVectorSentence1.length; i++) 
            {
                costVectorSentence1[i] = i * m_insertDeleteCost;
            }
            
            // We compute the cost of insert, delete and substitute the characters
            // of wrod2 to become word1

            for (int i = 0; i < strSentence1.length(); i++) 
            {
                // first element of v1 is A[i+1][0]
                // edit distance is delete (i+1) chars from s to match empty t

                costVectorSentence2[0] = (i + 1) * m_insertDeleteCost;

                for (int j = 0; j < strSentence2.length(); j++) 
                {
                    double substitutionCost = (strSentence1.charAt(i) 
                            == strSentence2.charAt(j)) ? 0.0 : m_substituteCost;

                    costVectorSentence2[j + 1] = Math.min(costVectorSentence2[j] 
                            + m_insertDeleteCost, costVectorSentence1[j + 1] + m_insertDeleteCost);
                    costVectorSentence2[j + 1] = Math.min(costVectorSentence2[j + 1], 
                            costVectorSentence1[j] + substitutionCost);
                }

                // We swap both cost vectors
                
                swap = costVectorSentence1;
                costVectorSentence1 = costVectorSentence2;
                costVectorSentence2 = swap;
            }

            // latest results was in v1 which was swapped with v0
        
            distanceValue = costVectorSentence1[strSentence2.length()];
        }
        
        // We return the result
        
        return (distanceValue);
    }
}