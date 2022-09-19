/*
 * Copyright (C) 2016-2022 Universidad Nacional de Educación a Distancia (UNED)
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
 */

package hesml.measures;

import hesml.taxonomy.IVertex;
import java.util.Set;

/**
 * This interface sets the abstract definition for all groupwise similairty
 * measures.
 * @author Juan J. Lastra-Díaz (jlastra@invi.uned.es)
 */

public interface IGroupwiseSimilarityMeasure
{
    /**
     * This function returns the type of the measure.
     * @return 
     */
    
    GroupwiseSimilarityMeasureType getMeasureType();
    
    /**
     * This function returns the semantic measure between two set of
     * concepts associated to the input vertex sets.
     * @param left The first set of vertexes (concepts) 
     * @param right The second set of vertexes (concepts) 
     * @return 
     * @throws java.lang.InterruptedException 
     */
    
    double getSimilarity(
            Set<IVertex> left,
            Set<IVertex> right)
            throws InterruptedException, Exception;
}
