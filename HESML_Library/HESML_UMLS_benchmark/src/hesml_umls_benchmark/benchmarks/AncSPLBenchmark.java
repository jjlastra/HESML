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
 */

package hesml_umls_benchmark.benchmarks;

import hesml.configurators.IntrinsicICModelType;
import hesml.measures.ISimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml_umls_benchmark.SnomedBasedLibraryType;
import hesml_umls_benchmark.snomedlibraries.HESMLSimilarityLibrary;
import java.io.FileNotFoundException;
import java.util.Random;
/**
 * This class implements a benchmark to evaluate the approximation quality
 * of the new Ancestor-based Shortest Path Length (AncSPL) algortihm to
 * approximate the exact Dijkstra algortihm on a taxonomy as SNOMED-CT.
 * [1] J.J. Lastra-Díaz, A. Lara-Clares, A. García-Serrano,
 * HESML: an efficient and scalable semantic measures library 
 * or the biomedical domain, Sobmitted for Publication. (2020).
 * @author j.lastra
 */

class AncSPLBenchmark extends UMLSLibBenchmark
{
    /**
     * Number of random concept pairs and runs for averaging the time
     */
    
    private int   m_nRandomSamples;
    
    /**
     * IC model used for the evaluation of the weigthed AcSPL algorithm
     */
    
    private IntrinsicICModelType  m_icModelMetric;
    
    /**
     * Measure types whose values will be comapared
     */
    
    private SimilarityMeasureType   m_measureType1;
    private SimilarityMeasureType   m_measureType2;
    
    /**
     * This flag sets if the benchmark will comapre the edge-counting
     * shortest path length of the weighted one.
     */
    
    protected boolean   m_useEdgeWeights;
    
    /**
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strSNOMED_CUI_mappingfilename
     * @param nRandomSamples
     * @throws Exception 
     */
    
    AncSPLBenchmark(
            String                  strSnomedDir,
            String                  strSnomedDBconceptFileName,
            String                  strSnomedDBRelationshipsFileName,
            String                  strSnomedDBdescriptionFileName,
            String                  strSNOMED_CUI_mappingfilename,
            IntrinsicICModelType    icModelMetric,
            SimilarityMeasureType   measureType1,
            SimilarityMeasureType   measureType2,
            int                     nRandomSamples,
            boolean                 useEdgeWeights) throws Exception
    {
        // We initialize the base class to load the HESML library
        
        super(new SnomedBasedLibraryType[]{SnomedBasedLibraryType.HESML},
                strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName, strSnomedDBdescriptionFileName,
                strSNOMED_CUI_mappingfilename);    
        
        // We save the number of random concept pairs to be evaluated
        
        m_nRandomSamples = nRandomSamples;
        m_icModelMetric = icModelMetric;
        m_useEdgeWeights = useEdgeWeights;
        m_measureType1 = measureType1;
        m_measureType2 = measureType2;
    }
    
    /**
     * This function executes the benchmark and saves the raw results into
     * the output file.
     */
    
    @Override
    public void run(String strOutputFilename) throws Exception
    {        
        // Debugging message

        System.out.println("-----------------------------------------------");
        System.out.println("\t Evaluating the approximation quality of the\n"
                + "\t Ancestor-based Shortest Path Length (AncSPL) algorithm");
        System.out.println("-----------------------------------------------");
        
        // We load SNOMED taxonomy
        
        m_Libraries[0].loadSnomed();
        
        // We set the intrinsic IC model for the weighted case
        
        if (m_useEdgeWeights)
        {
            m_Libraries[0].setSimilarityMeasure(m_icModelMetric, m_measureType1);
        }
            
        // We get the SNOMED taxonomy instanced by HESML
        
        ITaxonomy snomedTaxonomy = ((HESMLSimilarityLibrary) m_Libraries[0]).getSnomedTaxonomy();
        
        // We get the vector of random concept pairs

        IVertex[][] snomedNodepairs = getRandomSnomedNodePairs(snomedTaxonomy, m_nRandomSamples);
        
        // We get the two similairty measures
        
        ISimilarityMeasure measure1 = MeasureFactory.getMeasure(snomedTaxonomy, m_measureType1);
        ISimilarityMeasure measure2 = MeasureFactory.getMeasure(snomedTaxonomy, m_measureType2);
        
        // We create the output data matrix and fill the row headers
        // SNOMED ID1 | SNOMED ID2 | Exact Dijkstra distance | AncSPL distance
        
        String[][] strOutputDataMatrix = new String[snomedNodepairs.length + 1][4];
        
        // We fill the first row headers
        
        strOutputDataMatrix[0][0] = "SNOMED Id1";
        strOutputDataMatrix[0][1] = "SNOMED Id2";
        strOutputDataMatrix[0][2] = m_measureType1.toString();
        strOutputDataMatrix[0][3] = m_measureType2.toString();
        
        // We evaluate the performance of the HESML library
        
        for (int iPair = 0; iPair < snomedNodepairs.length; iPair++)
        {
            // We fill the values for the current concept pair
            
            strOutputDataMatrix[iPair + 1][0] = Long.toString(snomedNodepairs[iPair][0].getID());
            strOutputDataMatrix[iPair + 1][1] = Long.toString(snomedNodepairs[iPair][1].getID());

            // We get the SNOMED taxonomy vertexes
            
            IVertex snomed1 = snomedNodepairs[iPair][0];
            IVertex snomed2 = snomedNodepairs[iPair][1];
            
            // We evaluate the Dijsktra distance
            
            strOutputDataMatrix[iPair + 1][2] = Double.toString(measure1.getSimilarity(snomed1, snomed2));
            strOutputDataMatrix[iPair + 1][3] = Double.toString(measure2.getSimilarity(snomed1, snomed2));
            
            // We show the progress
            
            System.out.println(strOutputDataMatrix[iPair + 1][0] + " "
                + strOutputDataMatrix[iPair + 1][1] + " "
                + strOutputDataMatrix[iPair + 1][2] + " "
                + strOutputDataMatrix[iPair + 1][3] + " ("
                + iPair + " of " + Integer.toString(snomedNodepairs.length) + "pairs)");
        }
        
        // We unload SNOMED taxonomy
        
        m_Libraries[0].unloadSnomed();
        
        // We write the output raw data
        
        WriteCSVfile(strOutputDataMatrix, strOutputFilename);
    }    
    
    /**
     * This function generates a vector of random SNOMED-CT concept pairs which
     * will be used to evaluate the performance of the libraeries.
     * @param snomedTaxonomy
     * @param nPairs
     * @return 
     */
    
    private IVertex[][] getRandomSnomedNodePairs(
            ITaxonomy   snomedTaxonomy,
            int         nPairs) throws FileNotFoundException 
    {
        // We create the random SNOMED pairs
        
        IVertex[][] snomedNodePairs = new IVertex[nPairs][2];
        
        // We create a ranodm number
        
        Random rand = new Random(500);
        
        // We get the number of concepts in the SNOMED taxonomy
        
        double nSnomedConcepts = snomedTaxonomy.getVertexes().getCount();
        
        // We generate the ranomdon node pairs
        
        for (int i = 0; i < nPairs; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                // We generate a random index in the overall vertex coleccion
                
                int randomIndex = (int)(rand.nextDouble() * (nSnomedConcepts - 1));
                
                // We retrieve the vertexes at the index position
                
                snomedNodePairs[i][j] = snomedTaxonomy.getVertexes().getAt(randomIndex);
            }
        }
        
        // We return the output
        
        return (snomedNodePairs);
    }
}
