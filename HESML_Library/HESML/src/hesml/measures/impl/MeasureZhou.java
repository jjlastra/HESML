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
 * This class implements the measure introduced in the paper below.
 * 
 * Zhou, Z., Wang, Y., and Gu, J. (2008).
 * New model of semantic similarity measuring in wordnet. In Proc. of the
 * 3rd International Conference on Intelligent System and Knowledge
 * Engineering, 2008. ISKE 2008. (Vol. 1, pp. 256–261).
 * 
 * @author Juan Lastra-Díaz
 */

class MeasureZhou extends SimilaritySemanticMeasure
{
    /**
     * Maxikum depth of the base taxonomy
     */
    
    private int m_DepthMax;
    
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureZhou(
        ITaxonomy   taxonomy) throws Exception
    {
        super(taxonomy);
        
        // We get the maximum depth of the base taxonomy. We note
        // that Zhou et al. define the depth in base 1, it means that
        // the root depth is 1.

        m_DepthMax = taxonomy.getVertexes().getGreatestDepthMinBase1();
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.Zhou);
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
        double  similarity;   // Returned value

        double  distJC;         // Distancia de Jiang-Conrath
        double  depthFactor;    // Depth factor
        double  length;         // Length
        
        // We get the Jiang-Conrath distance
        
        distJC = BaseJiangConrathMeasure.getClassicJiangConrathDist(left, right);
        
        // We get the edge length between the nodes
        
        length = left.getShortestPathDistanceTo(right, false);
        
        // We compute the depth factor
        
        depthFactor = Math.log(length + 1.0) / Math.log(2.0 * m_DepthMax - 1.0);
        
        // We comptue the overall similarity
        
        similarity = 1.0 - 0.5 * (depthFactor + 0.5 * distJC);
        
        // We return the result
        
        return (similarity);
    }
}

