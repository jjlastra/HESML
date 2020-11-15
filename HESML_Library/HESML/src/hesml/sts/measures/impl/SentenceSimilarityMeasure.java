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

import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class implements the general methods for calculating measures scores.
 * It is the abstract base class for all sentence similarity measures.
 * @author alicia
 */

abstract class SentenceSimilarityMeasure implements ISentenceSimilarityMeasure
{
    // Word preprocessing object
    
    protected IWordProcessing m_preprocesser;
    
    /**
     * Constructor with parameters.
     * @param preprocesser 
     */
    
    SentenceSimilarityMeasure(
            IWordProcessing preprocesser)
    {
        // Initialize the preprocesser object.
        
        m_preprocesser = preprocesser;
    }
    
    /**
     * This function returns the label used to identify the measure in
     * a raw matrix results. This string attribute is set by the users
     * to provide the column header name included in all results generated
     * by this measure. This attribute was especially defined to
     * provide a meaningful name to distinguish the measures based on
     * pre-trained model files.
     * @return 
     */
    
    @Override
    public String getLabel()
    {
        return (getMethod().toString());
    }
    
    /**
     * This function is called by any client function before to evaluate
     * the current sentence similarity measure.
     */
    
    @Override
    public void prepareForEvaluation() throws Exception {}
    
    /**
     * This function releases all resources used by the measure. Once this
     * function is called the measure is completely disabled.
     */
    
    @Override
    public void clear()
    {
        m_preprocesser.clear();
    }
    
    /**
     * This function returns the current method.
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public abstract SentenceSimilarityMethod getMethod();

    /**
     * This function returns the current family of STS method
     * @return SentenceSimilarityFamily
     */
    
    @Override
    public abstract SentenceSimilarityFamily getFamily();
    
    /**
     * Get the similarity value of two sentences.
     * Each measure implements its own method.
     * BERTEmbeddingModelMeasure does not implement this method.
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return
     * @throws IOException 
     */
    
    @Override
    public abstract double getSimilarityValue(
            String strRawSentence1, 
            String strRawSentence2) throws IOException,
            FileNotFoundException, InterruptedException, Exception;

    /**
     * Get the similarity value between a list of pairs of sentences.
     * 
     * @param firstSentencesVector
     * @param secondSentencesVector
     * @return
     * @throws IOException
     * @throws InterruptedException 
     */
    
    @Override
    public double[] getSimilarityValues(
            String[] firstSentencesVector,
            String[] secondSentencesVector) throws IOException,
            FileNotFoundException, InterruptedException, Exception
    {
        // We check that the length of the lists has to be equal
        
        if(firstSentencesVector.length != secondSentencesVector.length)
        {
            String strerror = "The size of the input arrays are different!";
            throw new IllegalArgumentException(strerror);
        }
        
        // Initialize the scores
        
        double[] similarityScores = new double[firstSentencesVector.length];
        
        // Iterate the sentences and get the similarity scores.
        
        for (int i = 0; i < firstSentencesVector.length; i++)
        {
            similarityScores[i] = this.getSimilarityValue(firstSentencesVector[i], secondSentencesVector[i]);
        }
        
        // Return the result
        
        return (similarityScores);
    }
}