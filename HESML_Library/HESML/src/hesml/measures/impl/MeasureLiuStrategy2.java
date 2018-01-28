/*
 * Copyright (C) 2017 Universidad Nacional de Educación a Distancia (UNED)
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

import hesml.measures.SimilarityMeasureClass;
import hesml.measures.SimilarityMeasureType;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;

/**
 * This class implements the similarity measures defined in equation (5)
 * of the paper introduced by Liu et al. (2007) cited below:
 * 
 * Liu, X. Y., Zhou, Y. M., and Zheng, R. S. (2007).
 * Measuring Semantic Similarity in Wordnet.
 * In Proc. of the 2007 International Conference on Machine Learning
 * and Cybernetics (Vol. 6, pp. 3431–3435). IEEE.
 * 
 * @author j.lastra
 */

class MeasureLiuStrategy2 extends SimilaritySemanticMeasure
{
    /**
     * Tuning parameters defined in equation (5) of the Liu et al. (2007) paper.
     * The authors say that they obtain the highest correlation with alfa = 0.25
     * and beta = 0.25.
     */
    
    private final double  m_Alfa;
    private final double  m_Beta;
    
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     * @param alfa Alfa factor defined in equation (5) of the paper.
     * @param beta Beta factor defined in equation (5) of the paper.
     */
    
    MeasureLiuStrategy2(
            ITaxonomy   taxonomy,
            double      alfa,
            double      beta)
    {
        super(taxonomy);
        
        // We save the tuning parameters
        
        m_Alfa = alfa;
        m_Beta = beta;
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.LiuStrategy2);
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
     * @return Similarity value.
     */
    
    @Override
    public double compare(
            IVertex left,
            IVertex right) throws InterruptedException, Exception
    {
        double  similarity = 0.0;   // Returned value

        // We filter the equal case
        
        if (left == right)
        {
            similarity = 1.0;
        }
        else
        {
            // We get the Lowest Common Subsummer (LCS) vertex

            IVertex lcsVertex = m_Taxonomy.getLCS(left, right, false);

            // We compute the distance

            if (lcsVertex != null)
            {
                // We obtain the shortest path distance (edge weight = 1.0)
                // between the input vertexes

                double length = left.getShortestPathDistanceTo(right, false);

                // We obtain the depth of the LCS vertex defiend as the the
                // length of shortest path from the vertex to the root

                double depth = lcsVertex.getDepthMin();

                // We compute the similarity as defined in equation (5)
                // of the paper

                similarity = (Math.exp(m_Alfa * depth) - 1.0)/
                            (Math.exp(m_Alfa * depth) + Math.exp(m_Beta * length) - 2.0);
            }
        }
        
        // We return the result
        
        return (similarity);
    }
}
