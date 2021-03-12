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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Multithreading version of the HESML UMLS benchmarks.
 * @author j.lastra and alicia lc
 */


class BioBenchmarkThread implements Runnable 
{
    //  Benchmark object
    
    IBioLibraryExperiment m_benchmark;
    
    // Output path to the directory
    
    String m_outputPath;

    /**
     * Constructor with parameters
     * @param benchmark
     * @param outputPath 
     */
    
    BioBenchmarkThread(IBioLibraryExperiment benchmark, String outputPath)
    {
        // We initialize the objects
        
        m_benchmark = benchmark;
        m_outputPath = outputPath;
    }
    
    /**
     * Runnable object
     */
    
    @Override
    public void run() 
    {
        // Execute the experiment

        try 
        {
            // Execute 

            m_benchmark.run(m_outputPath);
            
            // Clear the benchmark

            m_benchmark.clear();
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(BioBenchmarkThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}