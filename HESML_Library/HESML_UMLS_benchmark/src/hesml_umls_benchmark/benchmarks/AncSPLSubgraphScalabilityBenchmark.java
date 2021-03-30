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

import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import hesml.taxonomyreaders.mesh.impl.MeSHFactory;
import hesml.taxonomyreaders.obo.IOboOntology;
import hesml.taxonomyreaders.obo.impl.OboFactory;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import hesml.taxonomyreaders.wordnet.impl.WordNetFactory;
import hesml_umls_benchmark.IBioLibraryExperiment;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

/**
 * This class implements an experiment to evaluate the scalability of the AncSPL algortihm
 * with regards to the number of ancestor nodes used by the AncSPL algorithm
 * @author Juan J. Lastra-Díaz (jlastra@invi.uned.es)
 */

class AncSPLSubgraphScalabilityBenchmark implements IBioLibraryExperiment
{
    /**
     * SNOMED-CT ontology
     */
    
    private ISnomedCtOntology   m_snomedOntology;
    
    /**
     * Go ontology
     */
    
    private IOboOntology    m_goOntology;
    
    /**
     * MeSH ontology
     */
    
    private IMeSHOntology   m_meshOntology;
    
    /**
     * WordNet DB
     */
    
    private IWordNetDB  m_wordnet;  
    
    /**
     * HESML taxonomy representing the ontology
     */
    
    private ITaxonomy   m_taxonomy;
    
    /**
     * Constructor for SNOMED-CT
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename 
     */
    
    AncSPLSubgraphScalabilityBenchmark(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName,
            String  strUmlsDir,
            String  strSNOMED_CUI_mappingfilename) throws Exception
    {
        // We init the unused ontologies
        
        m_meshOntology = null;
        m_goOntology = null;
        m_wordnet = null;
        
        // We load the SNOMED-CT ontology
        
        m_snomedOntology = SnomedCtFactory.loadSnomedDatabase(strSnomedDir, strSnomedDBconceptFileName,
                            strSnomedDBRelationshipsFileName, strSnomedDBdescriptionFileName,
                            strUmlsDir, strSNOMED_CUI_mappingfilename);
        
        // We retrieve the taxonomy
        
        m_taxonomy = m_snomedOntology.getTaxonomy();
    }
    
    /**
     * Constructor for SNOMED-CT
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename 
     */
    
    AncSPLSubgraphScalabilityBenchmark(
            String  strMeSHXmlFilename,
            String  strUmlsDir,
            String  strSNOMED_CUI_mappingfilename) throws Exception
    {
        // We init the unused ontologies
        
        m_goOntology = null;
        m_snomedOntology = null;
        m_wordnet = null;
        
        // We load the SNOMED-CT ontology
        
        m_meshOntology = MeSHFactory.loadMeSHOntology(strMeSHXmlFilename,
                            strUmlsDir+"/"+strSNOMED_CUI_mappingfilename);
        
        // We retrieve the taxonomy
        
        m_taxonomy = m_meshOntology.getTaxonomy();
    }
    
    /**
     * Constructor for SNOMED-CT
     * @param strGoOntologyFilename 
     */
    
    AncSPLSubgraphScalabilityBenchmark(
            String  strGoOntologyFilename) throws Exception
    {
        // We init the unused ontologies
        
        m_meshOntology = null;
        m_snomedOntology = null;
        m_wordnet = null;
        
        // We load the SNOMED-CT ontology
        
        m_goOntology = OboFactory.loadOntology(strGoOntologyFilename);
        
        // We retrieve the taxonomy
        
        m_taxonomy = m_goOntology.getTaxonomy();
    }
    
    /**
     * Constructor for SNOMED-CT
     * @param strGoOntologyFilename 
     */
    
    AncSPLSubgraphScalabilityBenchmark(
            String  strBaseDir, 
            String  strWordNet3_0_Dir) throws Exception
    {
        // We init the unused ontologies
        
        m_meshOntology = null;
        m_snomedOntology = null;
        m_goOntology = null;
        
        // We load the SNOMED-CT ontology
        
        m_wordnet = WordNetFactory.loadWordNetDatabase(strBaseDir+strWordNet3_0_Dir, "data.noun");
        
        // We set the taxonomy
        
        m_taxonomy = WordNetFactory.buildTaxonomy(m_wordnet, true);
    }
    
    /**
     * This function releases all resources used in the experiment
     */
    
    @Override
    public void clear()
    {
        if (m_snomedOntology != null) m_snomedOntology.clear();
        if (m_goOntology != null) m_goOntology.clear();
        if (m_meshOntology != null) m_meshOntology.clear();
        if (m_wordnet != null)
        {
            m_wordnet.clear();
            m_taxonomy.clear();
        }
    }
    
    /**
     * This function generates a large collection of vertex pairs grouped b the number
     * of ancestor nodes used by the AncSPL algorithm.
     */
  
    private TreeMap<Integer, ArrayList<VertexPair>> computeConceptGroups(
        int overallSamples) throws Exception
    {
        // We initialize the collections of groups of concept pairs indexed by their
        // AncSPL subgraphDimension
        
        TreeMap<Integer, ArrayList<VertexPair>>  groupedConceptPairs = new TreeMap<>();
        
        // We create a random number
        
        Random rand = new Random(500);
        
        // We get the vertex list
        
        IVertexList vertexes = m_taxonomy.getVertexes();
        
        // We get the overall number of vertexes in the taxonomy
        
        int nVertexes = vertexes.getCount();
        
        // We generate random concept pairs to populate the collection of groups
        // In order to generate a more uniform distribution regarding to the
        // subgraphDimension between vertex pairs (SNOMED-CT concePTS), first we group
        // all ontology vertxes into collections with the same depth-max value,
        // and then, we randomly select two vertexes from two randomly
        // selected depth-based groups.
        
        for (int i = 0; i < overallSamples; i++)
        {
            // We obtain a pair of random vertexes
            
            IVertex source = vertexes.getAt(rand.nextInt(nVertexes));
            IVertex target = vertexes.getAt(rand.nextInt(nVertexes));
            
            // We evaluate the dimension of the subgraph used by the AncSPL algorithm
            
            int ancSplSubgraphDimension = (int) source.getAncSPLSubgraphDimension(target);
            
            // We retrieve the group for this subgraphDimension
            
            if ((ancSplSubgraphDimension > 0) && (ancSplSubgraphDimension <= 100))
            {
                if (!groupedConceptPairs.containsKey(ancSplSubgraphDimension))
                {
                    groupedConceptPairs.put(ancSplSubgraphDimension, new ArrayList<>());
                }

                ArrayList<VertexPair> group = groupedConceptPairs.get(ancSplSubgraphDimension);

                group.add(new VertexPair(ancSplSubgraphDimension, source, target));
            }
        }
        
        // We return the result
        
        return (groupedConceptPairs);
    }
    
    /**
     * This function runs the experiment.
     * @param strOutputRawDataFilename 
     */
    
    @Override
    public void run(
        String  strOutputRawDataFilename) throws Exception
    {
        // We compute the groups of concepts
        
        TreeMap<Integer, ArrayList<VertexPair>>  groupedConceptPairs = computeConceptGroups(10000000);
        
        // We create the output file wit the following format
        // Pair subgraphDimension | # pairs | Overall time (secs) | Avg. speed (#pairs/secs)
        
        String[][] strOutputMatrix = new String[1 + groupedConceptPairs.size()][4];
        
        // We insert the headers
        
        strOutputMatrix[0][0] = "# Subgraph dimension";
        strOutputMatrix[0][1] = "# concept pairs";
        strOutputMatrix[0][2] = "Overall time (secs)";
        strOutputMatrix[0][3] = "Avg time (time/concept pair)";
        
        // We evaluate the AncSPL subgraphDimension for all vertex pairs with the same subgraphDimension
        
        int iGroup = 1;
        
        for (Integer subgraphDimension : groupedConceptPairs.keySet())
        {
            // We get the group of vertex pairs for the current subgraphDimension
            
            ArrayList<VertexPair> group = groupedConceptPairs.get(subgraphDimension);
            
            // Debug message
            
            System.out.println(getOntologyName() + " evaluating the subgraph-based group "
                    + iGroup + " of " + groupedConceptPairs.size());
            
            // In order to deal with the large differences in time measurements,
            // we adjust the number of pairs to be evaluated to the expexcted performance.
            // Thus, we define the minumum number of evaluations regarding to
            // the dimension of the subgraph
            
            double minSamples = getMinSamplesPerOntology();
            
            // We compute the minimum number of repetitions to obtain at least
            // the number of pair evaluation samples 
            
            int reps = Math.max(1, (int)Math.ceil(minSamples / group.size()));
            
            // We start the stop watch
            
            long stopWatch = System.currentTimeMillis();
            
            // We evaluate the subgraphDimension for all vertex pairs in the same group
            
            for (int i = 0; i < reps; i++)
            {
                for (VertexPair pair : group)
                {
                    pair.getSourceConceptVertex().getFastShortestPathDistanceTo(
                            pair.getTargetConceptVertex(), false);
                }
            }
            
            // We measure the ellapsed time
            
            double timeEllapsedSecs = (System.currentTimeMillis() - stopWatch) / 1000.0;
            
            // We register the ellapsed time
            
            long overallpairEvaluations = reps * group.size();
            
            strOutputMatrix[iGroup][0] = Integer.toString(subgraphDimension);
            strOutputMatrix[iGroup][1] = Long.toString(overallpairEvaluations);
            strOutputMatrix[iGroup][2] = Double.toString(timeEllapsedSecs);
            strOutputMatrix[iGroup++][3] = Double.toString(timeEllapsedSecs / overallpairEvaluations);
        }
        
        // We release all groups
        
        for (ArrayList<VertexPair> group : groupedConceptPairs.values())
        {
            group.clear();
        }
        
        // We release the object 
        
        groupedConceptPairs.clear();
        
        // We write the output file
        
        SemanticLibraryBenchmark.writeCSVfile(strOutputMatrix, strOutputRawDataFilename);
    }
    
    /**
     * This function computes the minimum number of samples regarding the ontology
     * being evaluated to set a proper time span for each time measurement.
     * @return 
     */
    
    private double getMinSamplesPerOntology()
    {
        double minSamples = 1e06;   // Output value
        
        // We set the minimum samples count
        
        if (m_meshOntology != null) minSamples = 1e08;
        else if (m_snomedOntology != null) minSamples = 500000;
//        else if (m_wordnet != null) minSamples = 1e07;
        else minSamples = 500000;
        
        // We return the result
        
        return (minSamples);
    }
    
    /**
     * This function returns the name of the ontology being evaluated
     * @return 
     */
    
    private String getOntologyName()
    {
        // We initializae the output
        
        String strOntologyName = "";
        
        // We set the minimum samples count
        
        if (m_meshOntology != null) strOntologyName = "MeSH";
        else if (m_snomedOntology != null) strOntologyName = "SNOMED-CT";
        else if (m_wordnet != null) strOntologyName = "WordNet";
        else strOntologyName = "GO";
        
        // We return the result
        
        return (strOntologyName);
    }
}