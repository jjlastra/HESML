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

package hesmlststestclient;

import hesml.sts.benchmarks.ISentenceSimilarityBenchmark;
import hesml.sts.benchmarks.impl.SentenceSimBenchmarkFactory;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Multi-threading version of the HESML STS benchmarks.
 * @author j.lastra and alicia lc
 */

public class BioSTSBenchmarkThread implements Runnable 
{
    //  Benchmark object
    
    ISentenceSimilarityBenchmark m_benchmark;
    
    // Measures
    
    ISentenceSimilarityMeasure[] m_measures;
    
    // Dataset directory and path
    
    String m_strDatasetDir;
    
    String m_strDatasetFileName;
    
    // Output path to the directory
    
    String m_outputPath;

    /**
     * Constructor with parameters
     * @param benchmark
     * @param outputPath 
     */
    
    BioSTSBenchmarkThread(
            ISentenceSimilarityMeasure[] measures,
            String strDatasetDir,
            String strDatasetFileName,
            String outputPath
            )
    {
        // We initialize the objects
        
        m_measures = measures;
        m_strDatasetDir = strDatasetDir;
        m_strDatasetFileName = strDatasetFileName;
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

            m_benchmark = SentenceSimBenchmarkFactory.getSingleDatasetBenchmark(
                                                    m_measures, m_strDatasetDir,
                                                    m_strDatasetFileName, m_outputPath);
            
            m_benchmark.evaluateBenchmark(true);
            
            // Clear the benchmark

            m_benchmark.clear();
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(BioSTSBenchmarkThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
