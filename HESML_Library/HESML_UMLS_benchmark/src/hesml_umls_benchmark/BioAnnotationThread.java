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

import hesml_umls_benchmark.benchmarks.AnnotateDataset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Multithreading version of the HESML UMLS benchmarks.
 * @author j.lastra and alicia lc
 */


class BioAnnotationThread implements Runnable 
{
    //  Benchmark object
    
    AnnotateDataset m_annotateDataset;
    
    // Dataset path
    
    String m_datasetPath;
    
    // Position of the sentence [firstSentence|secondSentence|joinSentences]
    
    String m_posSentence;

    /**
     * Constructor with parameters
     * @param benchmark
     * @param outputPath 
     */
    
    BioAnnotationThread(AnnotateDataset annotateDataset, 
                        String datasetPath,
                        String posSentence)
    {
        // We initialize the objects
        
        m_annotateDataset = annotateDataset;
        m_datasetPath = datasetPath;
        m_posSentence = posSentence;
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
            // Check type of annotation process and execute the annotation or join sentences
            
            if("firstSentence".equals(m_posSentence) 
               || "secondSentence".equals(m_posSentence))
            {
                // Annotate
                
                m_annotateDataset.annotate(m_datasetPath, m_posSentence);
            }
            else
            {
                // Throw exception if the position value is not valid
                
                throw new Exception("ERROR: Invalid position value");
            }
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(BioAnnotationThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}