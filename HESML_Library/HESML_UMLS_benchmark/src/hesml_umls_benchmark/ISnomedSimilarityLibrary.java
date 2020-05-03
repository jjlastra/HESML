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

package hesml_umls_benchmark;

import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;

/**
 * This interface sets the abstract interface provided by all semantic
 * measure libraries based on SNOMED-CT
 * @author j.lastra
 */

public interface ISnomedSimilarityLibrary
{
    /**
     * This function returns the library type
     * @return 
     */
    
    SnomedBasedLibraryType getLibraryType();
    
    /**
     * Thois function sets the active semantic measure used by the library
     * to compute the semantic similarity between concepts.
     * @param icModel
     * @param measureType 
     */
    
    void setSimilarityMeasure(
            IntrinsicICModelType    icModel,
            SimilarityMeasureType   measureType) throws Exception;
    
    /**
     * Load the SNOMED database
     * @throws java.lang.Exception
     */
    
    void loadSnomed() throws Exception;
    
    /**
     * Unload the SNOMED database
     */
    
    void unloadSnomed();
    
    /**
     * This function returns the degree of similarity between two
     * UMLS concepts.
     * @param strfirstUmlsCUI
     * @param strSecondUmlsCUI
     * @return 
     */
    
    double getSimilarity(
            String  strfirstUmlsCUI,
            String  strSecondUmlsCUI) throws Exception;
    
    /**
     * We release the resources associated to this object
     */
    
    void clear();
}
