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

package hesml.measures;

// HESML references

import hesml.taxonomy.*;

/**
 * This interface represents an abstract ontology-based semantic measure
 * @author Juan Lastra-Díaz
 */

public interface ISimilarityMeasure
{
    /**
     * This function returns the taxonomy
     * @return The taxonomy used to measure
     */
    
    ITaxonomy getTaxonomy();
    
    /**
     * This function returns the class of the measure
     * @return The type of the current semantic measure
     */
    
    SimilarityMeasureClass getMeasureClass();
    
    /**
     * This function returns the type of the measure.
     * @return 
     */
    
    SimilarityMeasureType getMeasureType();
    
    /**
     * This function returns the semantic measure between two
     * concepts associated to the input vertexes.
     * @param left The first vertex associated to the first concept
     * @param right The second vertex associated to the second concept.
     * @return 
     * @throws java.lang.InterruptedException 
     */
    
    double compare(
            IVertex left,
            IVertex right)
            throws InterruptedException, Exception;
    
    /**
     * This function returns the semantic measure between two
     * concepts associated to the input vertexes.
     * @param left The first vertex associated to the first concept
     * @param right The second vertex associated to the second concept.
     * @return 
     * @throws java.lang.InterruptedException 
     */
    
    double getSimilarity(
            IVertex left,
            IVertex right)
            throws InterruptedException, Exception;
    
    /**
     * This function returns the best similarity value for the Cartesian
     * product of both vertexes set.
     * @param left
     * @param right
     * @return
     * @throws InterruptedException
     * @throws Exception 
     */
    
    double getHighestPairwiseSimilarity(
            IVertexList left,
            IVertexList right)
            throws InterruptedException, Exception;
}
