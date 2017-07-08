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
 * This class implements the similarity measures defined in equation (2)
 * of the paper introduced below.
 * 
 * Pekar, V., and Staab, S. (2002).
 * Taxonomy Learning: Factoring the Structure of a Taxonomy into a
 * Semantic Classification Decision.
 * In Proceedings of the 19th International Conference on Computational Linguistics
 * (Vol. 1, pp. 1–7). Stroudsburg, PA, USA: Association for Computational Linguistics.
 * 
 * @author j.lastra
 */

class MeasurePekarStaab extends SimilaritySemanticMeasure
{
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasurePekarStaab(
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
        return (SimilarityMeasureType.PekarStaab);
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

        // We obtain the Lowest Common Subsumer
        
        IVertex lcsVertex = left.getTaxonomy().getLCS(left, right, false);

        // We check that there is a LCS vertex
        
        if (lcsVertex != null)
        {
            // We get the root vertex
            
            IVertex root = left.getTaxonomy().getVertexes().getRoots().getAt(0);
            
            // We compute the distance between the input vertexes, LCS and root
            
            double distLcsRoot = lcsVertex.getShortestPathDistanceTo(root, false);
            double distLeftLcs = left.getShortestPathDistanceTo(lcsVertex, false);
            double distRightLcs = right.getShortestPathDistanceTo(lcsVertex, false);
                    
            // We compute the similarity as defined in equation (2) of the paper
            
            similarity = distLcsRoot / (distLeftLcs + distRightLcs + distLcsRoot);
        }
        
        // We return the result
        
        return (similarity);
    }
}
