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
 * We generate a file containing the exact and app
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
        m_snomedOntology.clear();
    }
    
    /**
     * This function generate a sample list of random concept pairs.
     * 
     * @param overallSamples
     * @return
     * @throws Exception 
     */
    
    private IVertex[][] generateRandomPairs(int overallSamples) throws Exception
    {
        // We initialize the result
        
        IVertex[][] randomPairs = new IVertex[overallSamples][2];
        
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
            
            randomPairs[i][0] = source;
            randomPairs[i][1] = target;
        }
        
        // We return the result
        
        return (randomPairs);
    }
    
    /**
     * This function generates a file containing the exact and approximated
     * distance values between random SNOMED-CT concept pairs returned by
     * the exact Djikstra and AncSPL algorithms, respectively.
     * @param strOutputRawDataFilename 
     */
    
    @Override
    public void runExperiment(
            String  strOutputRawDataFilename) throws Exception
    {
        // We define the number of concept pairs that will be evaluated in the experiment
        
        int overallSamples = 1000;
                
        // We generate the renadom concept pairs
        
        IVertex[][] randomConceptPairs = generateRandomPairs(overallSamples);
        
        // We create the output file wit the following format
        // Id source | Id target | Exact distance | AncSPL distance
        
        String[][] strOutputMatrix = new String[1 + overallSamples][4];
        
        // We insert the headers
        
        strOutputMatrix[0][0] = "Id source";
        strOutputMatrix[0][1] = "Id target";
        strOutputMatrix[0][2] = "Exact distance";
        strOutputMatrix[0][3] = "AncSPL distance";
        
        // We generate random concept pairs to populate the collection of groups
        
        IVertexList vertexes = m_snomedOntology.getTaxonomy().getVertexes();
        
        // We compute the exact and ancSPL distance for all vertex pairs in the same randomConceptPairs
        
        for (int i = 0; i < overallSamples; i++)
        {
            // We output the progress - debug message
            
            System.out.println("Computing the random pair " + (i+1) + " of " + overallSamples);
            
            // We get the source and target Ids
            
            IVertex source = randomConceptPairs[i][0];
            IVertex target = randomConceptPairs[i][1];
            
            // We compute the distances
            
            double exactDistance = source.getShortestPathDistanceTo(target, false);
            double ancSPLDistance = source.getAncSPLdistanceTo(target, false);
            
            // We fill the output matrix
            
            strOutputMatrix[i+1][0] = Long.toString(source.getID());
            strOutputMatrix[i+1][1] = Long.toString(target.getID());
            strOutputMatrix[i+1][2] = Double.toString(exactDistance);
            strOutputMatrix[i+1][3] = Double.toString(ancSPLDistance);
        }
        
        // We write the output file
        
        SemanticLibraryBenchmark.writeCSVfile(strOutputMatrix, strOutputRawDataFilename);
    }
}
