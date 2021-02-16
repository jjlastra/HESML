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
import java.util.Random;

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
     * This function generate a sample list of random concept pairs.
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
        
        // We fill the randomConceptPairs concept pairs 
        
        for (int i = 0; i < overallSamples; i++)
        {
            // We obtain a pair of random vertexes
            
            IVertex source = vertexes.getAt(rand.nextInt(vertexes.getCount()));
            IVertex target = vertexes.getAt(rand.nextInt(vertexes.getCount()));
            
            // We fill the randomConceptPairs concepts
            
            group.add(new SnomedConceptPair(0.0, source, target));
        }
        
        // We return the result
        
        return (group);
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
        // We define the number of concept pairs that will be evaluated in the experiment
        
        int overallSamples = 1000;
                
        // We generate the renadom concept pairs
        
        ArrayList<SnomedConceptPair> randomConceptPairs = generateRandomPairs(overallSamples);
        
        // We create the output file wit the following format
        // Id source | Id target | Exact distance | AncSPL distance
        
        String[][] strOutputMatrix = new String[1 + randomConceptPairs.size()][4];
        
        // We insert the headers
        
        strOutputMatrix[0][0] = "Id source";
        strOutputMatrix[0][1] = "Id target";
        strOutputMatrix[0][2] = "Exact distance";
        strOutputMatrix[0][3] = "AncSPL distance";
        
        // We initialize the file row counter
        
        int iPair = 1;
        
        // We compute the exact and ancSPL distance for all vertex pairs in the same randomConceptPairs
        
        for (SnomedConceptPair pair : randomConceptPairs)
        {
            // We output the progress - debug message
            
            System.out.println("Computing the distance-based group " + iPair + " of " + overallSamples);
            
            // We get the source and target Ids
            
            IVertex source = pair.getSourceConceptVertex();
            IVertex target = pair.getTargetConceptVertex();
            
            // We compute the distances
            
            double exactDistance = source.getShortestPathDistanceTo(target, false);
            double ancSPLDistance = source.getAncSPLdistanceTo(target, false);
            
            // We fill the output matrix
            
            strOutputMatrix[iPair][0] = Long.toString(source.getID());
            strOutputMatrix[iPair][1] = Long.toString(target.getID());
            strOutputMatrix[iPair][2] = Double.toString(exactDistance);
            strOutputMatrix[iPair][3] = Double.toString(ancSPLDistance);
            
            // We increment the matrix row
            
            iPair++;
        }
        
        // We release the auxiliary resources
        
        randomConceptPairs.clear();
        
        // We write the output file
        
        SemanticLibraryBenchmark.writeCSVfile(strOutputMatrix, strOutputRawDataFilename);
    }
}
