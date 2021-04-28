/*
 * Copyright (C) 2016-2021 Universidad Nacional de Educación a Distancia (UNED)
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

// HESML references

import hesml.measures.*;
import hesml.taxonomy.*;

/**
 * This class implements the similarity measure introduced in the paper below.
 * 
 * Meng, L., Huang, R., and Gu, J. (2014).
 * Measuring Semantic Similarity of Word Pairs Using Path and
 * Information Content. International Journal of Future Generation
 * Communication and Networking, 7(3).
 * 
 * @author Juan Lastra-Díaz
 */

class MeasureMeng2014 extends SimilaritySemanticMeasure
{
    /**
     * This flag forces the use of the fast approximantion of Djikstra
     * algortihm instead of the exact method (false),
     */
    
    private boolean m_useFastShortestPathAlgorithm;
    
    /**
     * Constant in the exponential
     */
    
    private double  m_K;
    
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureMeng2014(
            ITaxonomy   taxonomy,
            boolean     useFastMethod)
    {
        // We call the base constructor
        
        super(taxonomy);
        m_useFastShortestPathAlgorithm = useFastMethod;
        
        // Default value in the paper
        
        m_K = 0.08;
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (!m_useFastShortestPathAlgorithm ?
                SimilarityMeasureType.Meng2014 :
                SimilarityMeasureType.AncSPLMeng2014);
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureClass getMeasureClass()
    {
        return (SimilarityMeasureClass.Similarity);
    }

    /**
     * This function returns the comparison between nodes.
     * @param left
     * @param right
     * @return Similarity value
     */
    
    @Override
    public double compare(
            IVertex left,
            IVertex right) throws InterruptedException, Exception
    {
        double  similarity = 0.0;   // Returned value

        // We obtain the MICA vertex
        
        IVertex micaVertex = m_Taxonomy.getMICA(left, right);
       
        // We check the MCIA vertex
        
        if (micaVertex != null)
        {
            // We get the length among concepts

            double length = !m_useFastShortestPathAlgorithm ? 
                    left.getShortestPathDistanceTo(right, false) :
                    left.getFastShortestPathDistanceTo(right, false);

            // We measure the power factor

            double power = (Math.exp(m_K * length) - 1.0);

            // We compute the distance

            similarity = 2.0 * micaVertex.getICvalue()
                        / (left.getICvalue() + right.getICvalue());

            // We apply the Meng's factor

            similarity = Math.pow(similarity, power);
        }
        
        // We return the result
        
        return (similarity);
    }
}

