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

package hesml.measures.impl;

// HESML references

import hesml.measures.*;
import hesml.taxonomy.*;

/**
 * This class implements the similarity measure called strategy 3 introduced
 * in the paper below.
 * 
 * Li, Y., Bandar, Z. A., and McLean, D. (2003).
 * An approach for measuring semantic similarity between words using
 * multiple information sources.
 * IEEE Transactions on Knowledge and Data Engineering, 15(4), 871–882.
 * 
 * @author Juan Lastra-Díaz
 */

class MeasureLi2003Strategy3 extends SimilaritySemanticMeasure
{
    /**
     * Exponential factor initializaed to the best
     * default value for this measure in the paper.
     */
    
    private double  m_Alpha;
    
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureLi2003Strategy3(
        ITaxonomy   taxonomy) throws Exception
    {
        super(taxonomy);
        
        // We initialize the best default value on the RG65 dataset
        
        m_Alpha = 0.25;
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.Li2003Strategy3);
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
     * @return The semantic distance between the nodes.
     */
    
    @Override
    public double compare(
            IVertex left,
            IVertex right) throws InterruptedException, Exception
    {
        return (simStrategyFun1(left, right, m_Alpha));
    }
    
    /**
     * This function computes the length-based similarity function.
     * We separate it in an static function to invoke it from the
     * other Li measures.
     * @param left
     * @param right
     * @param alpha
     * @return
     * @throws InterruptedException
     * @throws Exception 
     */
    
    static double simStrategyFun1(
            IVertex left,
            IVertex right,
            double  alpha) throws InterruptedException, Exception
    {
        double  similarity;   // Returned value

        double  length; // Shortest path length
        
        // We compute the shortest path length between nodes
        
        length = left.getShortestPathDistanceTo(right, false);
        
        // We compute the similarity
        
        similarity = Math.exp(-alpha * length);
        
        // We return the result
        
        return (similarity);
    }
}

