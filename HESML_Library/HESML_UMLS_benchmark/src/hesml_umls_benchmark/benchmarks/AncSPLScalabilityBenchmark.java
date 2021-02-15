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
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import hesml_umls_benchmark.IAncSPLScalabilityBenchmark;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

/**
 * Thisclass implements the wcalability benchmark for the AncSPL algorithm.
 * @author Juan J. Lastra-Díaz (jlastra@invi.uned.es)
 */

class AncSPLScalabilityBenchmark implements IAncSPLScalabilityBenchmark
{
    /**
     * SNOMED-CT ontology
     */
    
    private ISnomedCtOntology   m_snomedOntology;
    
    /**
     * Groups of concept pairs indexed by their AncSPL distance
     */
    
    private TreeMap<Integer, ArrayList<SnomedConceptPair>>  m_groupedConceptPairs;
    
    /**
     * This function loads 
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename 
     */
    
    AncSPLScalabilityBenchmark(
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
        
        // We initialize the collections of groups of concept pairs indexed by their
        // AncSPL distance
        
        m_groupedConceptPairs = new TreeMap<>();
    }
    
    /**
     * This function releases all resources used in the experiment
     */
    
    @Override
    public void clear()
    {
        // We release all groups
        
        for (ArrayList<SnomedConceptPair> group : m_groupedConceptPairs.values())
        {
            group.clear();
        }
        
        // We release all objects
        
        m_groupedConceptPairs.clear();
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
     * This function computes a collections of groups of Snomedpairs
     */
    
    @Override
    public void computeConceptGroups() throws Exception
    {
        // We create a random number
        
        Random rand = new Random(1500);
        
        // We get hte matrix of vertexes grouped by depth-max value
        
        IVertex[][] vertexesGroupedByDepthValue = getVertexesByDepthMax();
        
        // We generate random concept pairs to populate the collection of groups
        // In order to generate a more uniform distribution regarding to the
        // distance between vertex pairs (SNOMED-CT concePTS), first we group
        // all ontology vertxes into collections with the same depth-max value,
        // and then, we randomly select two vertexes from two randomly
        // selected depth-based groups.
        
        int overallSamples = 10000;
        
        for (int i = 0; i < overallSamples; i++)
        {
            // We obtain a random source vertex
            
            int sourceDepth = rand.nextInt(vertexesGroupedByDepthValue.length);
            
            IVertex[]  sourceVertexes = vertexesGroupedByDepthValue[sourceDepth];
            
            IVertex source = sourceVertexes[rand.nextInt(sourceVertexes.length)];
            
            // We obtain a random target vertex
            
            int targetDepth = rand.nextInt(vertexesGroupedByDepthValue.length);
            
            IVertex[]  targetVertexes = vertexesGroupedByDepthValue[targetDepth];
            
            IVertex target = targetVertexes[rand.nextInt(targetVertexes.length)];
            
            // We evaluate thie AncSPL distance
            
            int ancSplDistance = (int) source.getFast2ShortestPathDistanceTo(target, false);
            
            // We retrieve the group for this distance
            
            if (!m_groupedConceptPairs.containsKey(ancSplDistance))
            {
                m_groupedConceptPairs.put(ancSplDistance, new ArrayList<>());
            }
            
            ArrayList<SnomedConceptPair> group = m_groupedConceptPairs.get(ancSplDistance);
            
            group.add(new SnomedConceptPair(ancSplDistance, source, target));
            
            if (i % 100 == 0) System.out.println("Samples = "+ (i + 1) + " / " + overallSamples);
        }
        
        // We read the quantity of data
        
        for (ArrayList<SnomedConceptPair> group : m_groupedConceptPairs.values())
        {
            System.out.println("Distance = " + group.get(0).getAncSPLDistance()
                + " -> count = " + group.size());
        }
    }
    
    /**
     * This function evaluates the AncSPL for each group of concet pairs
     * and generates the output raw data file containing the overall
     * running time for each distance group.
     * @param strOutputRawDataFilename 
     */
    
    @Override
    public void evaluatePairwiseDistanceForAllGroups(
        String  strOutputRawDataFilename) throws Exception
    {
        // We create the output file wit the following format
        // Pair distance | # pairs | Overall time (secs) | Avg. speed (#pairs/secs)
        
        int maxPairDistance = 30;
        
        String[][] strOutputMatrix = new String[1 + maxPairDistance][4];
        
        // We insert the headers
        
        strOutputMatrix[0][0] = "Distance";
        strOutputMatrix[0][1] = "# pairs";
        strOutputMatrix[0][2] = "Overall time (secs)";
        strOutputMatrix[0][3] = "Avg speed (concept pairs/secs)";
        
        // We evaluate the AncSPL distance for all vertex pairs with the same distance
        
        for (int distance = 1; distance <= maxPairDistance; distance++)
        {
            // We get the group of vertex pairs for the current distance
            
            ArrayList<SnomedConceptPair> group = m_groupedConceptPairs.get(distance);
            
            // Debug message
            
            System.out.println("Evaluating the distance-based group " + distance + "of " + maxPairDistance);
            
            // In order to deal with the large differences in time measurements,
            // we adjust the number of pairs to be evaluated to the expexcted performance.
            // Thus, we define the minimum number of evaluations in decreasing
            // order regading to the pair distance.
            
            double s = (distance - 1) / ((double) (maxPairDistance - 1));
            
            double minSamples = 1000;
            
            // We compute the minimum number of repetitions to obtain at least
            // the number of pair evaluation samples 
            
            int reps = Math.max(1, (int)Math.ceil(minSamples / group.size()));
            
            // We start the stop watch
            
            long stopWatch = System.currentTimeMillis();
            
            // We evaluate the distance for all vertex pairs in the sam group
            
            for (int i = 0; i < reps; i++)
            {
                for (SnomedConceptPair pair : group)
                {
                    pair.getSourceConceptVertex().getFast2ShortestPathDistanceTo(
                            pair.getTargetConceptVertex(), false);
                }
            }
            
            // We measure the ellapsed time
            
            double timeEllapsedSecs = (System.currentTimeMillis() - stopWatch) / 1000.0;
            
            // We register the ellapsed time
            
            long overallpairEvaluations = reps * group.size();
            
            strOutputMatrix[distance][0] = Integer.toString(distance);
            strOutputMatrix[distance][1] = Long.toString(overallpairEvaluations);
            strOutputMatrix[distance][2] = Double.toString(timeEllapsedSecs);
            strOutputMatrix[distance][3] = Double.toString(overallpairEvaluations / timeEllapsedSecs);
        }
        
        // We write the output file
        
        SemanticLibraryBenchmark.writeCSVfile(strOutputMatrix, strOutputRawDataFilename);
    }
}
