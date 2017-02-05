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
 * This class implements the similarity measure called method 3 that is
 * introduced in the paper below.
 * Gao, J.-B., Zhang, B.-W., and Chen, X.-H. (2015).
 * A WordNet-based semantic similarity measurement combining
 * edge-counting and information content theory.
 * Engineering Applications of Artificial Intelligence, 39, 80–88.
 * 
 * @author Juan Lastra-Díaz
 */

class MeasureGao2015Method3 extends SimilaritySemanticMeasure
{
    /**
     * Alpha
     */
    
    private double  m_Alpha;
    
    /**
     * Beta
     */
    
    private double  m_Beta;
    
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureGao2015Method3(
        ITaxonomy   taxonomy)
    {
        super(taxonomy);
        
        // We set the default values as defined in the paper above
        
        m_Alpha = 0.15;
        m_Beta = 2.05;
    }

    /**
     * This function returns the class of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureClass getMeasureClass()
    {
        return (SimilarityMeasureClass.Similarity);
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.Gao2015Strategy3);
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
        
        double  length; // Shortest path length between concepts
        double  weight; // Weight    
        double  icMICAvalue;

        IVertex micaVertex = m_Taxonomy.getMICA(left, right);
        
        // We check the existence of the MICA vertex
        
        if (micaVertex != null)
        {
            // We get the shortest path length between the concepts

            length = left.getShortestPathDistanceTo(right, false);

            // We get the IC value of the lowest common ancestor

            icMICAvalue = micaVertex.getICvalue();

            // We comptue the IC-based weight

            if (icMICAvalue >= 1.0)
            {
                weight = Math.pow(1.0 + 1.0 / icMICAvalue, m_Beta);
            }
            else
            {
                weight = Math.pow(2.0, m_Beta);
            }

            // We comptue the similarity value

            similarity = Math.exp(-m_Alpha * length * weight);
        }
        
        // We return the result
        
        return (similarity);
    }
}
