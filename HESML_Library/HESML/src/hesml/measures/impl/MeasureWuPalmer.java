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
 * Wu, Z., and Palmer, M. (1994).
 * Verbs Semantics and Lexical Selection.
 * In Proceedings of the 32Nd Annual Meeting on Association for
 * Computational Linguistics (pp. 133–138).
 * Stroudsburg, PA, USA: Association for Computational Linguistics.
 * 
 * @author Juan Lastra-Díaz
 */

class MeasureWuPalmer extends SimilaritySemanticMeasure
{
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureWuPalmer(
        ITaxonomy   taxonomy)
    {
        super(taxonomy);
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.WuPalmer);
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
        double  similarity = 0.0;   // Returned value

        IVertex lcsVertex;  // Common lowest ancestor
       
        // We check the equality case
        
        if (left == right)
        {
            similarity = 1.0;
        }
        else
        {
            // We get the LCS vertex

            lcsVertex = m_Taxonomy.getLCS(left, right, false);

            // We compute the distance field from the LCS vertex
            // and finally the similarity value
            
            if (lcsVertex != null)
            {
                // In order to save computation time, we compute
                // the distance field from the LCS vertex to the
                // remainder vertexes within the taxonomy before
                // to recover the two required distances from
                // the minimum distance attribute of each vertex.
                
                lcsVertex.computeDistanceField(false);
                
                // We compute the similarity
                
                similarity = 2.0 * lcsVertex.getDepthMin() /
                        (2.0 * lcsVertex.getDepthMin() + left.getMinDistance()
                        + right.getMinDistance());
            }
        }
        
        // We return the result
        
        return (similarity);
    }
}
