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

package hesml.benchmarks;

// HESML references

import hesml.taxonomy.*;

/**
 * This is the common interface for all the similarity benchmarks.
 * @author Juan Lastra-Díaz
 */

public interface ISimilarityBenchmark
{
    /**
     * This function releases all the resources used by the object.
     */
    
    void clear();
    
    /**
     * This function returns the output filename of the benchmark.
     * @return Output filename
     */
    
    String  getDefaultOutputFilename();
    
    /**
     * This function sets the default output filename.
     * @param strDefaultOutputFilename 
     */
    
    void setDefaultOutputFilename(
        String  strDefaultOutputFilename);
    
    /**
     * This function returns the taxonomy associated to the benchmark.
     * @return The base taxonomy used by the test.
     */
    
    ITaxonomy getTaxonomy();
    
    /**
     * This function executes the test and save the result into the
     * output CSV file.
     * @param strMatrixResultsFile CSV file path containing the results
     * @param showDebugInfo The benchmark shows the count of the current word pair being evaluated.
     * @throws java.lang.Exception 
     */
    
    void executeTests(
            String  strMatrixResultsFile,
            boolean showDebugInfo) throws Exception;
}
