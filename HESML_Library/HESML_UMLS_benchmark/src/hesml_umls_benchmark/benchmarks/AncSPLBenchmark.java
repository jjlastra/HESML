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
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.ISimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml_umls_benchmark.SemanticLibraryType;
import hesml_umls_benchmark.semantclibrarywrappers.HESMLSemanticLibraryWrapper;
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

class AncSPLBenchmark extends SemanticLibraryBenchmark
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
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strSNOMED_CUI_mappingfilename
     * @param strUmlsDir
     * @param nRandomSamples
     * @throws Exception 
     */
    
    AncSPLBenchmark(
            String                  strSnomedDir,
            String                  strSnomedDBconceptFileName,
            String                  strSnomedDBRelationshipsFileName,
            String                  strSnomedDBdescriptionFileName,
            String                  strUmlsDir,
            String                  strSNOMED_CUI_mappingfilename,
            IntrinsicICModelType    icModelMetric,
            SimilarityMeasureType   measureType1,
            SimilarityMeasureType   measureType2,
            int                     nRandomSamples) throws Exception
    {
        // We initialize the base class to load the HESML library
        
        super(new SemanticLibraryType[]{SemanticLibraryType.HESML},
                strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName, strSnomedDBdescriptionFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename);    
        
        // We save the number of random concept pairs to be evaluated
        
        m_nRandomSamples = nRandomSamples;
        m_icModelMetric = icModelMetric;
        m_measureType1 = measureType1;
        m_measureType2 = measureType2;
    }
    
    /**
     * Constructor for the GO ontology
     * @param strOboOntologyFile
     * @param icModelMetric
     * @param measureType1
     * @param measureType2
     * @param nRandomSamples
     * @throws Exception 
     */
    
    AncSPLBenchmark(
            String                  strOboOntologyFile,
            IntrinsicICModelType    icModelMetric,
            SimilarityMeasureType   measureType1,
            SimilarityMeasureType   measureType2,
            int                     nRandomSamples) throws Exception
    {
        // We initialize the base class to load the HESML library
        
        super(new SemanticLibraryType[]{SemanticLibraryType.HESML},
                strOboOntologyFile);
                
        // We save the number of random concept pairs to be evaluated
        
        m_nRandomSamples = nRandomSamples;
        m_icModelMetric = icModelMetric;
        m_measureType1 = measureType1;
        m_measureType2 = measureType2;
    }
            
    /**
     * Constructor for the GO ontology
     * @param strOboOntologyFile
     * @param icModelMetric
     * @param measureType1
     * @param measureType2
     * @param nRandomSamples
     * @throws Exception 
     */
    
    AncSPLBenchmark(
            String                  strBaseDir,
            String                  strWordNet3_0_Dir,
            IntrinsicICModelType    icModelMetric,
            SimilarityMeasureType   measureType1,
            SimilarityMeasureType   measureType2,
            int                     nRandomSamples) throws Exception
    {
        // We initialize the base class to load the HESML library
        
        super(new SemanticLibraryType[]{SemanticLibraryType.HESML},
                strBaseDir, strWordNet3_0_Dir);
                
        // We save the number of random concept pairs to be evaluated
        
        m_nRandomSamples = nRandomSamples;
        m_icModelMetric = icModelMetric;
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
        System.out.println("\t" + m_measureType1.toString() + " vs " +  m_measureType2.toString());
        System.out.println("-----------------------------------------------");
        
        // We load SNOMED taxonomy
        
        m_Libraries[0].loadOntology();
                   
        // We get the SNOMED taxonomy instanced by HESML
        
        ITaxonomy taxonomy = ((HESMLSemanticLibraryWrapper) m_Libraries[0]).getTaxonomy();
        
        // We set the intrinsic IC model for the weighted case
        
        ICModelsFactory.getIntrinsicICmodel(m_icModelMetric).setTaxonomyData(taxonomy);
        
        // We get the vector of random concept pairs
        
        int nontreeNodes = taxonomy.getNumberOfVertexesWithMulitpleParents();
        int overallNodes = taxonomy.getVertexes().getCount();
        
        System.out.println("NON TREE NODES: " + nontreeNodes);
        System.out.println("OVERALL NODES: " + overallNodes);

        IVertex[][] randomVertexPairs = getRandomNodePairs(taxonomy, m_nRandomSamples);
        
        // We get the two similairty measures
        
        ISimilarityMeasure measure1 = MeasureFactory.getMeasure(taxonomy, m_measureType1);
        ISimilarityMeasure measure2 = MeasureFactory.getMeasure(taxonomy, m_measureType2);
        
        // We create the output data matrix and fill the row headers
        // SNOMED ID1 | SNOMED ID2 | Exact Dijkstra distance | AncSPL distance
        
        String[][] strOutputDataMatrix = new String[randomVertexPairs.length + 1][4];
        
        // We fill the first row headers
        
        if(!m_strSnomedDBconceptFileName.equals(""))
        {
            strOutputDataMatrix[0][0] = "SNOMED Id1";
            strOutputDataMatrix[0][1] = "SNOMED Id1";
        }
        else if(!m_strOboFilename.equals(""))
        {
            strOutputDataMatrix[0][0] = "GO Id1";
            strOutputDataMatrix[0][1] = "GO Id1";
        }
        else
        {
            strOutputDataMatrix[0][0] = "WordNet Id1";
            strOutputDataMatrix[0][1] = "WordNet Id1";
        }
        
        strOutputDataMatrix[0][2] = m_measureType1.toString();
        strOutputDataMatrix[0][3] = m_measureType2.toString();
        
        // We evaluate the performance of the HESML library on the SNOMED-CT or GO ontologies
        
        for (int iPair = 0; iPair < randomVertexPairs.length; iPair++)
        {
            // We fill the values for the current concept pair
            
            if(!m_strSnomedDBconceptFileName.equals(""))
            {
                strOutputDataMatrix[iPair + 1][0] = Long.toString(randomVertexPairs[iPair][0].getID());
                strOutputDataMatrix[iPair + 1][1] = Long.toString(randomVertexPairs[iPair][1].getID());
            }
            else if(!m_strOboFilename.equals(""))
            {
                strOutputDataMatrix[iPair + 1][0] = randomVertexPairs[iPair][0].getStringTag();
                strOutputDataMatrix[iPair + 1][1] = randomVertexPairs[iPair][1].getStringTag();
            }
            else
            {
                strOutputDataMatrix[iPair + 1][0] = Long.toString(randomVertexPairs[iPair][0].getID());
                strOutputDataMatrix[iPair + 1][1] = Long.toString(randomVertexPairs[iPair][1].getID());
            }
            
            // We get the SNOMED taxonomy vertexes
            
            IVertex vertex1 = randomVertexPairs[iPair][0];
            IVertex vertex2 = randomVertexPairs[iPair][1];
            
            // We evaluate the Dijsktra distance
            
            strOutputDataMatrix[iPair + 1][2] = Double.toString(measure1.getSimilarity(vertex1, vertex2));
            strOutputDataMatrix[iPair + 1][3] = Double.toString(measure2.getSimilarity(vertex1, vertex2));
            
            // We show the progress
            
            System.out.println((iPair + 1) + " of " + Integer.toString(randomVertexPairs.length) + " pairs.");
        }
        
        // We unload SNOMED taxonomy
        
        m_Libraries[0].unloadOntology();
        
        // We write the output raw data
        
        writeCSVfile(strOutputDataMatrix, strOutputFilename);
    }    
    
    /**
     * This function generates a vector of random concept pairs which
     * will be used to evaluate the performance of the libraries.
     * @param taxonomy
     * @param nPairs
     * @return 
     */
    
    private IVertex[][] getRandomNodePairs(
            ITaxonomy   taxonomy,
            int         nPairs)
    {
        // We create the random SNOMED pairs
        
        IVertex[][] snomedNodePairs = new IVertex[nPairs][2];
        
        // We create a ranodm number
        
//        Random rand = new Random(500);
        Random rand = new Random(600);
        
        // We get the number of concepts in the SNOMED taxonomy
        
        double nSnomedConcepts = taxonomy.getVertexes().getCount();
        
        // We generate the ranomdon node pairs
        
        for (int i = 0; i < nPairs; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                // We generate a random index in the overall vertex coleccion
                
                int randomIndex = (int)(rand.nextDouble() * (nSnomedConcepts - 1));
                
                // We retrieve the vertexes at the index position
                
                snomedNodePairs[i][j] = taxonomy.getVertexes().getAt(randomIndex);
            }
        }
        
        // We return the output
        
        return (snomedNodePairs);
    }
}
