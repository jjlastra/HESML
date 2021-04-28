/*
 * Copyright (C) 2016-2021 Universidad Nacional de Educación a Distancia (UNED)
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

/**
 * This interface represents an abstract similarity measure between words.
 * @author j.lastra
 */

public interface IWordSimilarityMeasure
{
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
     * This function returns the semantic measure between two words.
     * @param strWord1 The first word
     * @param strWord2 The second word
     * @return 
     * @throws java.lang.InterruptedException 
     */
    
    double getSimilarity(
            String strWord1,
            String strWord2)
            throws InterruptedException, Exception;
    
    /**
     * This function returns the value returned by the similarity measure when
     * there is none similarity between both input concepts, or the concept
     * is not contained in the taxonomy.
     * @return 
     */
    
    double getNullSimilarityValue();
    
    /**
     * This function is called with the aim of releasing all resources used
     * by the measure.
     */
    
    void clear();
}
