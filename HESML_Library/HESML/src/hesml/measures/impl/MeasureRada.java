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

package hesml.measures.impl;

// HESML references

import hesml.measures.*;
import hesml.taxonomy.*;

/**
 * This class implements the semantic distance introduced by Rada et al. in
 * the following paper. We note that the Rada et al. distance is
 * linearly converted into a similarity measure when the client code calls the
 * ISemanticMeasure.Similarity() method.
 * 
 * Rada, R., Mili, H., Bicknell, E., and Blettner, M. (1989).
 * Development and application of a metric on semantic nets.
 * IEEE Transactions on Systems, Man, and Cybernetics, 19(1), 17–30.
 * 
 * @author j.lastra
 */

class MeasureRada extends SimilaritySemanticMeasure
{
    /**
     * This flag forces the use of the fast approximantion of Djikstra
     * algortihm instead of the exact method (false),
     */
    
    private boolean m_useFastShortestPathAlgorithm;
    
    /**
     * Maxium distanbce value used to set the null similarity value
     */
    
    private double  m_maxDistance;
    
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureRada(
            ITaxonomy   taxonomy,
            boolean     useFastMethod) throws Exception
    {
        super(taxonomy);
        m_useFastShortestPathAlgorithm = useFastMethod;
        m_maxDistance = 2.0 * taxonomy.getVertexes().getGreatestDepthMax();
    }
    
    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (!m_useFastShortestPathAlgorithm ?
                SimilarityMeasureType.Rada :
                SimilarityMeasureType.AncSPLRada);
    }
    
    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureClass getMeasureClass()
    {
        return (SimilarityMeasureClass.Distance);
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
        // We compute the shortest path length
        
        double distance = !m_useFastShortestPathAlgorithm ?
                left.getShortestPathDistanceTo(right, false) :
                left.getFastShortestPathDistanceTo(right, false);
        
        // We return the result
        
        return (distance);
    }    
    
    /**
     * This function returns the value returned by the similarity measure when
     * there is none similarity between both input concepts, or the concept
     * is not contained in the taxonomy.
     * @return 
     */
    
    @Override
    public double getNullSimilarityValue()
    {
        return (1.0 - m_maxDistance / 2.0);
    }
}
