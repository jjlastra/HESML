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

package hesml_umls_benchmark.benchmarks;

import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import hesml_umls_benchmark.IAncSPLDataBenchmark;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

/**
 * This class implements the statistical benchmark for the AncSPL algorithm.
 * @author Juan J. Lastra-Díaz (jlastra@invi.uned.es)
 */

class AncSPLStatisticalBenchmark implements IAncSPLDataBenchmark
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
    
    AncSPLStatisticalBenchmark(
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
        
        // We release all objects
        
        m_snomedOntology.clear();
    }
    
    /**
     * This function builds a matrix of vertexes ordered by their depth max value.
     * Each row contains all vertexes with the same depth-max value
     * @return 
     */
    
    private IVertex[][] getVertexesByDepthMax() throws Exception
    {
        // We create the collection sorted by depth-max value
        
        TreeMap<Integer, ArrayList<IVertex>>  vertexGroupsByDepthMaxValue = new TreeMap<>();
        
        // We traverse the SNOMED-CT taxonomy
        
        for (IVertex vertex : m_snomedOntology.getTaxonomy().getVertexes())
        {
            // We get the depth-max value
            
            int depthMax = vertex.getDepthMax();
            
            // We retrieve the collection of vertexes with the same depth-max value
            
            if (!vertexGroupsByDepthMaxValue.containsKey(depthMax))
            {
                vertexGroupsByDepthMaxValue.put(depthMax, new ArrayList<>());
            }
            
            ArrayList<IVertex> vertexes = vertexGroupsByDepthMaxValue.get(depthMax);
            
            // We register the vertex in the list of its corresponding group
            
            vertexes.add(vertex);
        }
        
        // We create the output matrix
        
        IVertex[][] depthVertexGroups = new IVertex[vertexGroupsByDepthMaxValue.size()][];
        
        // We copy the vertexes to the matrix
        
        int i = 0;
        
        for (Integer depthMaxvalue : vertexGroupsByDepthMaxValue.keySet())
        {
            // We get the vertex list for the current depth-max value
            
            ArrayList<IVertex> vertexes = vertexGroupsByDepthMaxValue.get(depthMaxvalue);
            
            // We copy the vertexes to the array
            
            depthVertexGroups[i++] = vertexes.toArray(new IVertex[vertexes.size()]);
            
            // We release the vertex list
            
            vertexes.clear();
        }
        
        // We release the sorted list
        
        vertexGroupsByDepthMaxValue.clear();
        
        // We return the result
        
        return (depthVertexGroups);
    }
    
    /**
     * This function generate a sample list of random pair of concepts.
     * 
     * @param overallSamples
     * @return
     * @throws Exception 
     */
    
    private ArrayList<SnomedConceptPair> generateRandomPairs(int overallSamples) throws Exception
    {
        // We initialize the result
        
        ArrayList<SnomedConceptPair> group = new ArrayList<>();
        
        // We create a random number
        
        Random rand = new Random(500);
        
        // We generate random concept pairs to populate the collection of groups
        
        IVertexList vertexes = m_snomedOntology.getTaxonomy().getVertexes();
        
        // We fill the group concept pairs 
        
        for (int i = 0; i < overallSamples; i++)
        {
            // We obtain a pair of random vertexes
            
            IVertex source = vertexes.getAt(rand.nextInt(vertexes.getCount()));
            IVertex target = vertexes.getAt(rand.nextInt(vertexes.getCount()));
            
            // We fill the group concepts
            
            group.add(new SnomedConceptPair(0.0, source, target));
        }
        
        // We return the result
        
        return (group);
    }
    
    /**
     * This function evaluates the AncSPL for each group of concet pairs
     * and generates the output raw data file containing the overall
     * running time for each distance group.
     * 
     * @param strOutputRawDataFilename 
     */
    
    @Override
    public void runExperiment(
            String  strOutputRawDataFilename) throws Exception
    {
        // We define the number of concept pairs that will be evaluated in the experiment
        
        int overallSamples = 1000;
                
        // We compute the groups of concepts
        
        ArrayList<SnomedConceptPair> group = generateRandomPairs(overallSamples);
        
        // We create the output file wit the following format
        // Id source | Id target | Exact distance | AncSPL distance
        
        String[][] strOutputMatrix = new String[1 + group.size()][4];
        
        // We insert the headers
        
        strOutputMatrix[0][0] = "Id source";
        strOutputMatrix[0][1] = "Id target";
        strOutputMatrix[0][2] = "Exact distance";
        strOutputMatrix[0][3] = "AncSPL distance";
        
        // We initialize the file row counter
        
        int iGroup = 1;
        
        // We compute the exact and ancSPL distance for all vertex pairs in the same group
        
        for (SnomedConceptPair pair : group)
        {
            // We output the progress - debug message
            
            System.out.println("Computing the distance-based group " + iGroup + " of " + overallSamples);
            
            // We get the source and target Ids
            
            IVertex source = pair.getSourceConceptVertex();
            IVertex target = pair.getTargetConceptVertex();
            
            // We compute the distances
            
            double exactDistance = source.getShortestPathDistanceTo(target, false);
            double ancSPLDistance = source.getFastShortestPathDistanceTo(target, false);
            
            // We fill the output matrix
            
            strOutputMatrix[iGroup][0] = Long.toString(source.getID());
            strOutputMatrix[iGroup][1] = Long.toString(target.getID());
            strOutputMatrix[iGroup][2] = Double.toString(exactDistance);
            strOutputMatrix[iGroup][3] = Double.toString(ancSPLDistance);
            
            // We increment the matrix row
            
            iGroup++;
        }
        
        // We write the output file
        
        SemanticLibraryBenchmark.writeCSVfile(strOutputMatrix, strOutputRawDataFilename);
    }
}
