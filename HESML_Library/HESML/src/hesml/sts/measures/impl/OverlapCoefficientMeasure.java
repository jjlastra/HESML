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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *  This function implements the Overlap coefficient similarity between two sentences.
 * 
 *  Lawlor, Lawrence R. 1980. “Overlap, Similarity, 
 *  and Competition Coefficients.” Ecology 61 (2): 245–51.
 * 
 *  @author alicia
 */

class OverlapCoefficientMeasure extends SentenceSimilarityMeasure
                                implements IStringBasedSentenceSimMeasure
{
    /**
     * label shown in all raw matrix results
     */
    
    private final String m_strLabel;
    
    /**
     * Constructor
     * @param preprocesser 
     */
    
    OverlapCoefficientMeasure(
            String          strLabel,
            IWordProcessing preprocesser)
    {
        // We intialize the base class
        
        super(preprocesser);
        
        // We initialize the object
        
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
     * This function returns the type of method implemented by the current
     * sentence similarity measure.
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return (SentenceSimilarityMethod.OverlapCoefficient);
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
     * This function returns the String method
     * @return StringBasedSentenceSimilarityMethod
     */
    
    @Override
    public StringBasedSentenceSimilarityMethod getStringBasedMethodType()
    {
        return (StringBasedSentenceSimilarityMethod.OverlapCoefficient);
    }
    
    /**
     * This function returns the similarity value (score) between two
     * raw sentences.
     * 
     * similarity(a,b) = ∣a ∩ b∣ / ∣Min(|A|, |B|)∣
     * 
     * When ∣a ∪ b∣ is empty the sets have no elements in common.
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return similarity score
     * @throws java.io.IOException
     */
    
    @Override
    public double getSimilarityValue(
            String strRawSentence1, 
            String strRawSentence2) 
            throws IOException, FileNotFoundException, InterruptedException, Exception
    {
        // We initialize the output
        
        double similarity = 0.0; 
        
        // Get the tokens for each sentence

        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
        
        // Convert the lists to set objects
        // HashSet is a set where the elements are not sorted or ordered.
        
        Set<String> setWordsSentence1 = new HashSet<>(Arrays.asList(lstWordsSentence1)); 
        Set<String> setWordsSentence2 = new HashSet<>(Arrays.asList(lstWordsSentence2)); 

        // If both sets are empty, the similarity is 1
        
        if (setWordsSentence1.isEmpty() && setWordsSentence2.isEmpty())
        {
            similarity = 1.0;
        }
        else if (setWordsSentence1.isEmpty() || setWordsSentence2.isEmpty())
        {
            similarity = 0.0;
        }
        else
        {
            double intersection = intersection(setWordsSentence1, setWordsSentence2).size();

            // Get the smallest sentence

            int minimumSentenceSize = setWordsSentence1.size();

            if (setWordsSentence2.size() < minimumSentenceSize)
            {
                minimumSentenceSize = setWordsSentence2.size();
            }
        
            // ∣a ∩ b∣ / ∣Min(|A|, |B|)∣
        
            similarity = intersection / minimumSentenceSize;
        }
        
        // Return the result
        
        return (similarity);
    }
    
    /**
     * This function calculates the intersection between two sets 
     * to compute the Overlap Coefficient Similarity.
     * 
     * @param sentence1Set
     * @param sentence2Set
     * @return 
     */
    
    private Set<String> intersection(
            Set<String> sentence1Set, 
            Set<String> sentence2Set) 
    {
        // Initialize the intersection set with the first set.
        
        Set<String> intersection = new HashSet<>(sentence1Set);
        
        // Intersect with the second set
        
        intersection.retainAll(sentence2Set);
        
        // Return the intersected set.
        
        return (intersection);
    }
}