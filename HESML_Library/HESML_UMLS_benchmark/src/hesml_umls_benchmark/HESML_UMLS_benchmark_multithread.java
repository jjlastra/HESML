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

import hesml.HESMLversion;
import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import static hesml_umls_benchmark.HESML_UMLS_benchmark.testDbConnection;
import hesml_umls_benchmark.benchmarks.BenchmarkFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
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
 * 
 * 
 * @author root
 */
class HESML_UMLS_benchmark_multithread implements Runnable 
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
    
    HESML_UMLS_benchmark_multithread(IBioLibraryExperiment benchmark, String outputPath)
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
            Logger.getLogger(HESML_UMLS_benchmark_multithread.class.getName()).log(Level.SEVERE, null, ex);
        }
        m_benchmark.clear();
    }
}

/**
 * This class define the threads of the experiments
 * 
 * @author alicia
 */

class Multithread 
{ 
    /**
    * Filenames and directories of the SNOMD-CT files and UMLS CUI file
    */

    private static final String m_strMeSHdir = "../UMLS/MeSH_Nov2019";
    private static final String m_strMeSHXmlFilename = "desc2019.xml";
    private static final String m_strUMLSdir = "../UMLS/UMLS2019AB";
    private static final String m_strSnomedDir = "../UMLS/SNOMED_Nov2019";
    private static final String m_strSNOMED_conceptFilename = "sct2_Concept_Snapshot_US1000124_20190901.txt";
    private static final String m_strSNOMED_relationshipsFilename = "sct2_Relationship_Snapshot_US1000124_20190901.txt";
    private static final String m_strSNOMED_descriptionFilename = "sct2_Description_Snapshot-en_US1000124_20190901.txt";
    private static final String m_strUmlsCuiMappingFilename = "MRCONSO.RRF";
    private static final String m_strMedSTSfilename = "../SentenceSimDatasets/MedSTS_subset30_normalized.tsv"; 
       
    public static void main(String[] args) throws ClassNotFoundException, SQLException, Exception 
    { 
        // We set the measures being evaluated
                                                    
        SimilarityMeasureType[][] measureTypes = new SimilarityMeasureType[4][2];
        
        measureTypes[0][0] = SimilarityMeasureType.Rada;
        measureTypes[0][1] = SimilarityMeasureType.AncSPLRada;
        measureTypes[1][0] = SimilarityMeasureType.LeacockChodorow;
        measureTypes[1][1] = SimilarityMeasureType.AncSPLLeacockChodorow;
        measureTypes[2][0] = SimilarityMeasureType.CosineNormWeightedJiangConrath;
        measureTypes[2][1] = SimilarityMeasureType.AncSPLCosineNormWeightedJiangConrath;
        measureTypes[3][0] = SimilarityMeasureType.CaiStrategy1;
        measureTypes[3][1] = SimilarityMeasureType.AncSPLCaiStrategy1;
        
        // We build the vector of raw output filenames
        
        String[] strOutputFilenames = new String[measureTypes.length];
        
        // We initialize the input parameters
        
        String strRawOutputDir = ".";
        
        for (int i = 0; i < strOutputFilenames.length; i++)
        {
            strOutputFilenames[i] = "raw_output_" + measureTypes[i][1] + "_exp4.csv";
        }

        // We compare the correlation between two measures
        
        for (int i = 0; i < measureTypes.length; i++)
        {
            IBioLibraryExperiment benchmark = BenchmarkFactory.createAncSPLBenchmark(
                                                IntrinsicICModelType.Seco,
                                                measureTypes[i][0],
                                                measureTypes[i][1],
                                                50, m_strSnomedDir, m_strSNOMED_conceptFilename,
                                                m_strSNOMED_relationshipsFilename,
                                                m_strSNOMED_descriptionFilename,
                                                m_strUMLSdir, m_strUmlsCuiMappingFilename);
            
            String outputPath = strRawOutputDir + "/" + strOutputFilenames[i];
            
            // Each instance of a benchmark is executed by one thread.
            
            Thread object = new Thread(new HESML_UMLS_benchmark_multithread(benchmark, outputPath)); 
            object.start(); 
        }
    } 
}