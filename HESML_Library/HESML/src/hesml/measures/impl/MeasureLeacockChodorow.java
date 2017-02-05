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
 * This class implements the measure introduced by Leacock and Chodorow
 * in the paper below.
 * 
 * Leacock, C., and Chodorow, M. (1998).
 * Combining local context and WordNet similarity for word sense identification.
 * In C. Fellbaum (Ed.), WordNet: An electronic lexical database (pp. 265–283).
 * MIT Press.
 * 
 * @author j.lastra
 */

class MeasureLeacockChodorow extends SimilaritySemanticMeasure
{
    /**
     * Largest depth min value within the taxonomy cached in the
     * constructor.
     */
    
    private final int m_largestDepthMin;
    
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureLeacockChodorow(
        ITaxonomy   taxonomy) throws Exception
    {
        super(taxonomy);
        
        // We obtain the largest depth min value
        
        m_largestDepthMin = taxonomy.getVertexes().getGreatestDepthMin();
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.LeacockChodorow);
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

        // We compute the similarity

        similarity = -Math.log((1.0 + left.getShortestPathDistanceTo(right, false))
                            + (2.0 * m_largestDepthMin));
        
        // We return the result
        
        return (similarity);
    }
}
