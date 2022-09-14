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

package hesml.sts.measures.impl;

import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the BIOSSES2017 Measure for COM methods (WordNet-based and UMLS-based methods)
 * 
 * COM = ( WBSM * lambda ) + ( UBSM( 1-lambda ) )
 * 
 * @author alicia
 */

class COMMeasure extends CombinedSentenceSimilarityMeasure
{
    
    /**
     * label shown in all raw matrix results
     */
    
    private final String m_strLabel;
    
    // Lambda value
    
    private final double m_lambda;
    
    // WBSM measure
    
    private ISentenceSimilarityMeasure m_wbsmMeasure;
    
    // UBSM measure
    
    private ISentenceSimilarityMeasure m_ubsmMeasure;
    
    /**
     * Constructor with parameters.
     * @param preprocesser 
     */
    
    COMMeasure(
            String                          strLabel,
            Double                          lambda,
            ISentenceSimilarityMeasure[]    measures)
    {
        super(measures);
        
        // We initialize the attributes
        
        m_strLabel = strLabel;
        m_lambda = lambda;
        
        // We set the measures
        
        setMeasures();
    }
    
    /**
     * This function sets the WBSM and UBSM measures in the object
     */
    
    private void setMeasures()
    {
        // The first measure is WBSM and the second measure is UBSM
        
        m_wbsmMeasure = m_measures[0];
        m_ubsmMeasure = m_measures[1];
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
     * This function returns the family of the current sentence similarity method.
     * @return SentenceSimilarityFamily
     */
    
    @Override
    public SentenceSimilarityFamily getFamily()
    {
        return (SentenceSimilarityFamily.Combined);
    }
    
    /**
     * This function returns the type of method implemented by the current
     * sentence similarity measure.
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return (SentenceSimilarityMethod.COM);
    }
    
 

    /**
     * This function returns the similarity value (score) between two
     * raw sentences. 
     * 
     * 
     * COM = ( WBSM * lambda ) + ( UBSM( 1-lambda ) )
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
        
        // Get the score values for wbsm and ubsm

        double score_wbsm = m_wbsmMeasure.getSimilarityValue(strRawSentence1, strRawSentence2);
        double score_ubsm = m_ubsmMeasure.getSimilarityValue(strRawSentence1, strRawSentence2);
        
        // Compute the COM value
        
        similarity = (score_wbsm*m_lambda) + (score_ubsm*(1-m_lambda));
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function calculates the intersection between two sets 
     * to compute the Jaccard Similarity.
     * @param s1
     * @param s2
     * @return Set<String> intersection set
     */
    
    static Set<String> intersection(
            Set<String> s1, 
            Set<String> s2) 
    {
        // Initialize the set with the information of the first set
        
        Set<String> intersection = new HashSet<>(s1);
        
        // Intersect with the second set
        
        intersection.retainAll(s2);
        
        // Return the intersection
        
        return (intersection);
    }

    /**
     * This function is called by any client function before to evaluate
     * the current sentence similarity measure.
     * 
     * In this case, the method will be called by each combined measure.
     * 
     * @throws Exception 
     */
    @Override
    public void prepareForEvaluation() throws Exception 
    {
        m_wbsmMeasure.prepareForEvaluation();
        m_ubsmMeasure.prepareForEvaluation();
    }
    
    /**
     * This function is called by any client function before to evaluate
     * the current sentence similarity measure.
     * 
     * In this case, the method will be called by each combined measure.
     * 
     * @throws Exception 
     */
    
    @Override
    public void prepareForEvaluation(String dataset_info) throws Exception {
        m_wbsmMeasure.prepareForEvaluation(dataset_info);
        m_ubsmMeasure.prepareForEvaluation(dataset_info);
    }
}