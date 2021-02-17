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

import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import hesml_umls_benchmark.IAncSPLDataBenchmark;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;

/**
 * This class evaluates the scalability of the AncSPL algorithm reagarding the
 * number of ancestor nodes of the source and target nodes, and the dimension
 * of the subgraph.
 * @author j.lastra
 */

class AncSPLComplexityBenchmark implements IAncSPLDataBenchmark
{
    /**
     * SNOMED-CT ontology
     */
    
    private ISnomedCtOntology   m_snomedOntology;
    
    /**
     * This function loads 
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename 
     */
    
    AncSPLComplexityBenchmark(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName,
            String  strUmlsDir,
            String  strSNOMED_CUI_mappingfilename) throws Exception
    {
        // We load the SNOMED-CT ontology
        
        m_snomedOntology = SnomedCtFactory.loadSnomedDatabase(strSnomedDir, strSnomedDBconceptFileName,
                            strSnomedDBRelationshipsFileName, strSnomedDBdescriptionFileName,
                            strUmlsDir, strSNOMED_CUI_mappingfilename);
    }
    
    /**
     * This function releases all resources used in the experiment
     */
    
    @Override
    public void clear()
    {
        m_snomedOntology.clear();
    }
    
    /**
     * This function computes a collections of groups of vertexes according to
     * their number of ancestors
     */
  
    private TreeMap<Integer, HashSet<IVertex>> computeVertexBins() throws Exception
    {
        // We initialize the collections of groups
        
        TreeMap<Integer, HashSet<IVertex>>  binsByAncestorsCount = new TreeMap<>();
        
        // We create a random number
        
        Random rand = new Random(500);
        
        // (1) We traverse the whole taxonomy excluding the root to obtain
        // the minimum and maxium number of ancestorsCount + overall adjacent nodes
        
        int maxValue = 0;
        int minValue = Integer.MAX_VALUE;
        
        for (IVertex vertex : m_snomedOntology.getTaxonomy().getVertexes())
        {
            // We get the ancestor and adjacent node counts
            
            if (!vertex.isRoot())
            {
                int[] ancestorsCount = vertex.getAncestorSubgraphCount();
                
                // We sum both values
                
                int overallValue = ancestorsCount[0] + ancestorsCount[1];
                
                // We compujte the minimum and maximum values
                
                minValue = Math.min(minValue, overallValue);
                maxValue = Math.max(maxValue, overallValue);
                
                // We save the vertex into its corresponding bin
                
                if (!binsByAncestorsCount.containsKey(overallValue))
                {
                    binsByAncestorsCount.put(overallValue, new HashSet<>());
                }
                
                binsByAncestorsCount.get(overallValue).add(vertex);
            }
        }
        
        // We return the result
        
        return (binsByAncestorsCount);
    }
    
    /**
     * This function generates a file containing the exact and approximated
     * distance values between random SNOMED-CT concept pairs returned by
     * the exact Djikstra and AncSPL algirthms, respectively.
     * @param strOutputRawDataFilename 
     */
    
    @Override
    public void runExperiment(
            String  strOutputRawDataFilename) throws Exception
    {
        // We generate the renadom concept pairs
        
        TreeMap<Integer, HashSet<IVertex>>  binsByAncestorsCount = computeVertexBins();
        
        // We create the output file wit the following format
        // Id source | Id target | Exact distance | AncSPL distance
        
        String[][] strOutputMatrix = new String[1 + binsByAncestorsCount.size()][4];
        
        // We insert the headers
        
        strOutputMatrix[0][0] = "#Overall nodes";
        strOutputMatrix[0][1] = "Overall time (secs)";
        strOutputMatrix[0][2] = "Overall distance evaluations";
        strOutputMatrix[0][3] = "Avg speed (evaluations nodes/secs)";
        
        // We initialize the file row counter and root vertex
        
        IVertex root = m_snomedOntology.getTaxonomy().getVertexes().getAt(0);
        
        int iEntry = 1;
        
        // We compute the exact and ancSPL distance for all vertex pairs in the same randomConceptPairs
        
        for (Integer overallAncestorCount : binsByAncestorsCount.keySet())
        {
            // We output the progress - debug message
            
            System.out.println("Computing running time of bin " + (iEntry + 1)
                    + " of " + binsByAncestorsCount.size());
            
            // We get the collection of vertexes for the current bin
            
            HashSet<IVertex> bin = binsByAncestorsCount.get(overallAncestorCount);
            
            // We compute the minimum number of repetitions to obtain at least
            // the number of pair evaluation samples 
            
            int minSamples = 10;
            int reps = Math.max(1, (int)Math.ceil(minSamples / bin.size()));

            // We start the stop watch
            
            long stopWatch = System.currentTimeMillis();
            
            // We evaluate the distance for all vertex pairs in the sam group
            
            for (int i = 0; i < reps; i++)
            {
                for (IVertex vertex : bin)
                {
                    vertex.getFastShortestPathDistanceTo(root, false);
                }
            }
            
            // We measure the ellapsed time
            
            double timeEllapsedSecs = (System.currentTimeMillis() - stopWatch) / 1000.0;
            
            // We register the ellapsed time
            
            long overallpairEvaluations = reps * bin.size();
            
            strOutputMatrix[iEntry][0] = Integer.toString(overallAncestorCount);
            strOutputMatrix[iEntry][1] = Double.toString(timeEllapsedSecs);
            strOutputMatrix[iEntry][1] = Long.toString(overallpairEvaluations);
            strOutputMatrix[iEntry][2] = Double.toString(overallpairEvaluations / timeEllapsedSecs);
            
            // We increment the matrix row
            
            iEntry++;
        }
        
        // We release the auxiliary resources
        
        for (HashSet<IVertex> bin : binsByAncestorsCount.values())
        {
            bin.clear();
        }
        
        binsByAncestorsCount.clear();
        
        // We write the output file
        
        SemanticLibraryBenchmark.writeCSVfile(strOutputMatrix, strOutputRawDataFilename);
    }
}
