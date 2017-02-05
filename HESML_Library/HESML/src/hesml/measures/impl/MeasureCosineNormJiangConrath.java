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
 * This class implements the cosine-normalized Jiang-Conrath similarity
 * which is introduced in the paper below.
 * 
 * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
 * A novel family of IC-based similarity measures with a detailed experimental
 * survey on WordNet.Engineering Applications of Artificial Intelligence
 * Journal, 46, 140–153.
 * 
 * @author Juan Lastra-Díaz
 */

class MeasureCosineNormJiangConrath extends BaseJiangConrathMeasure
{
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureCosineNormJiangConrath(
        ITaxonomy   taxonomy) throws Exception
    {
        super(taxonomy);
                
        // We compute the maximum Jiang-Conrath distance from the root to
        // any leaf vertexes. This measure should matches
        // the maximum IC value on the taxonomy.
        
        computeMaxDistance();
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.CosineNormJiangConrath);
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
     * 
     * @param left
     * @param right
     * @return The semantic distance between the nodes.
     */
    
    @Override
    public double compare(
            IVertex left,
            IVertex right) throws InterruptedException, Exception
    {
        double  similarity; // Returned value
        
        double  distance;   // Standard JC distance

        // We compute the distance
        
        distance = getClassicJiangConrathDist(left, right);
        
        // We normalize the distance
        
        similarity = convertToCosineNormSimilarity(distance);
        
        // We return the result
        
        return (similarity);
    }
}
