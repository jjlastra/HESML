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
 * This class implements the benchmark application used to compare
 * the performance of th HESML-UMLS library with the UMLS::Similairty [2]
 * and SML [3] Libraries.
 * 
 * [1] HESML-UMLS paper.
 * 
 * [2] B.T. McInnes, T. Pedersen, S.V.S. Pakhomov,
 * UMLS-Interface and UMLS-Similarity : open source software for measuring
 * paths and semantic similarity, in: Proc. of the Annual Symposium of the
 * American Medical Informatics Association, ncbi.nlm.nih.gov,
 * San Francisco, CA, 2009: pp. 431–435.
 * 
 * [3] S. Harispe, S. Ranwez, S. Janaqi, J. Montmain, The semantic measures
 * library and toolkit: fast computation of semantic similarity and relatedness
 * using biomedical ontologies, Bioinformatics. 30 (2014) 740–742.
 * 
 * @author j.lastra and alicia lc
 */

/**
 * Multithreading version of the HESML UMLS benchmarks.
 * 
 * @author alicia
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
    public void run() {
        try {
            m_benchmark.run(m_outputPath);
        } catch (Exception ex) {
            Logger.getLogger(BioBenchmarkThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        m_benchmark.clear();
    }
}