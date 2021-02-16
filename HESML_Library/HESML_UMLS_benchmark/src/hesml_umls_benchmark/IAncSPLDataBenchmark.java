/*
 * * Copyright (C) 2020-2021 Universidad Complutense de Madrid (UCM)
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

package hesml_umls_benchmark;

/**
 * This interface defines the API for the benchmark evaluating
 * the scalability of the AncSPL algorithm with regards to
 * the distance between SNOMED-CT concepts. 
 * @author Juan J. Lastra-Díaz (jlastra@invi.uned.es)
 */

public interface IAncSPLDataBenchmark
{
    /**
     * This function releases all resources used in the experiment
     */
    
    void clear();
    

    /**
     * This function evaluates the AncSPL for each group of concept pairs
     * and generates the output raw data file.
     * @param strOutputRawDataFilename 
     */
    
    void runExperiment(
        String  strOutputRawDataFilename) throws Exception;
}
