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
 * This class implements the weighted Jiang-Conrath distance introduced by
 * Lastra-Díaz and García-Serrano in the next paper.
 * 
 * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
 * A novel family of IC-based similarity measures with a detailed experimental
 * survey on WordNet. Engineering Applications of Artificial Intelligence
 * Journal, 46, 140–153.
 * 
 * In addition, the commercial use of this similarity measure is protected
 * by the following US patent application:
 * Lastra Díaz, J. J. and García Serrano, A. (2016).
 * System and method for the indexing and retrieval of semantically annotated
 * data using an ontology-based information retrieval model. United States
 * Patent and Trademark Office (USPTO) Application, US2016/0179945 A1.
 * 
 * @author Juan Lastra-Díaz
 */

class MeasureWeightedJiangConrath extends BaseJiangConrathMeasure
{
    /**
     * This flag forces the use of the fast approximantion of Djikstra
     * algortihm instead of the exact method (false),
     */
    
    private boolean m_useFastShortestPathAlgorithm;
    
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureWeightedJiangConrath(
            ITaxonomy   taxonomy,
            boolean     usefastMethod) throws Exception
    {
        super(taxonomy);
        m_useFastShortestPathAlgorithm = usefastMethod;
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (!m_useFastShortestPathAlgorithm ?
                SimilarityMeasureType.WeightedJiangConrath :
                SimilarityMeasureType.AncSPLWeightedJiangConrath);
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
     * @return IC-based Weighted distance between the nodes.
     */
    
    @Override
    public double compare(IVertex left, IVertex right) throws Exception
    {
        // We compute the weighted distance between the nodes.
        // Before to evaluate this measure is necessary to
        // apply any IC model. All the IC models set the
        // IC-based weights used by the wieghted Jiang-Conrath
        // measures like this one.
        
        return (!m_useFastShortestPathAlgorithm ? 
                left.getShortestPathDistanceTo(right, true):
                left.getAncSPLdistanceTo(right, true));
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
        return (1.0 - m_Taxonomy.getVertexes().getGreatestICValue());
    }    
}
