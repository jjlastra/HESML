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
import hesml.taxonomy.IVertexList;

/**
 * This class implements the similarity measures defined in equation (9)
 * of the paper introduced by Stojanovic et al. (2001) cited below:
 * 
 * Stojanovic, N., Maedche, A., Staab, S., Studer, R., and Sure, Y. (2001).
 * SEAL: A Framework for Developing SEmantic PortALs.
 * In Proceedings of the 1st International Conference on Knowledge Capture
 * (pp. 155–162). New York, NY, USA: ACM.
 * 
 * @author j.lastra
 */

class MeasureStojanovic extends SimilaritySemanticMeasure
{
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureStojanovic(
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
        return (SimilarityMeasureType.Stojanovic);
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
        // We get the inclusive ancestors of the input vertexes
        // in accordance with the definition provided in equation (7)
        // of Stojanovic et al. (2001).
        
        IVertexList leftAncestors = left.getAncestors(true);
        IVertexList rightAncestors = right.getAncestors(true);
        
        // We get the count of the insersection set
        
        double intersectionCount = leftAncestors.getIntersectionSetCount(rightAncestors);
        double unionCount = leftAncestors.getUnionSetCount(rightAncestors);
        
        // We release the ancestor sets
        
        leftAncestors.clear();
        rightAncestors.clear();
        
        // We compute the similarity as the ratio between the intersection
        // and union sets, as defined in equation (9) of the paper.
        
        double similarity = intersectionCount / unionCount;
        
        // We return the result
        
        return (similarity);
    }
}
