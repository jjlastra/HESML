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
 * This functions implements the feature-based measure introduced by
 * Sánchez et al. (2012) in the paper below.
 * 
 * Sánchez, D., Batet, M., Isern, D., and Valls, A. (2012).
 * Ontology-based semantic similarity: A new feature-based approach.
 * Expert Systems with Applications, 39, 7718–7728.
 * 
 * @author Juan Lastra-Díaz
 */

class MeasureSanchez2012 extends SimilaritySemanticMeasure
{
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureSanchez2012(
        ITaxonomy   taxonomy)
    {
        super(taxonomy);
    }

    /**
     * This function returns the class of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureClass getMeasureClass()
    {
        return (SimilarityMeasureClass.Dissimilarity);
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.Sanchez2012);
    }

    /**
     * This function returns the comparison between nodes.
     * @param left
     * @param right
     * @return Dissimilarity value.
     */
    
    @Override
    public double compare(
            IVertex left,
            IVertex right) throws InterruptedException, Exception
    {
        double  dissimilarity;   // Returned value

        IVertexList leftAncestors;  // Ancestors
        IVertexList rightAncestors;
       
        double    leftDif, rightDif;  // CARDINAL OF THE DIFFERENCE SETS
        double    intersection;       // cardinal of the intersexction set
        
        double  featuresRatio;    // Argfument
        
        // We get the ancestro sets
        
        leftAncestors = left.getAncestors(true);
        rightAncestors = right.getAncestors(true);
        
        // We measure the difference sets and the intersection
        
        leftDif = leftAncestors.getDifferenceSetCount(rightAncestors);
        rightDif = rightAncestors.getDifferenceSetCount(leftAncestors);
        intersection = leftAncestors.getIntersectionSetCount(rightAncestors);
        
        // We clear the sets
        
        leftAncestors.clear();
        rightAncestors.clear();
        
        // We compute the dissimilarity ratio
        
        featuresRatio = (leftDif + rightDif) / (leftDif + rightDif + intersection);
        
        // We compute the dissimilarity measure
        
        dissimilarity = Math.log(1.0 + featuresRatio) / Math.log(2.0);
        
        // We return the result
        
        return (dissimilarity);
    }
}
