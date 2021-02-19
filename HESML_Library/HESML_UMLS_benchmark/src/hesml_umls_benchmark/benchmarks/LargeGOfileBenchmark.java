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

import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.GroupwiseMetricType;
import hesml.measures.GroupwiseSimilarityMeasureType;
import hesml.measures.IGroupwiseSimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.obo.IOboConcept;
import hesml.taxonomyreaders.obo.IOboOntology;
import hesml.taxonomyreaders.obo.impl.OboFactory;
import hesml_umls_benchmark.IBioLibraryExperiment;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements a large GO-based experiment in which two
 * large GO annotated files in GO Annotation File (GAF) format
 * are compared. The experiment compares the GO-based protein
 * annotations of two large files.
 * @author j.lastra
 */

class LargeGOfileBenchmark implements IBioLibraryExperiment
{ 
    /**
     * In-memory GO representation
     */
    
    private IOboOntology    m_GOontology;
    
    /**
     * Sets of proteins annotated with GO concepts. Both
     * collections are indexed by the protein keyname as
     * read from the GO Annotated Files (GAF). The loading
     * function reads the GO annotation and retrieves
     * their corresponding Go concept node (vertex).
     */
    
    private HashMap<String, Set<IVertex>>   m_firstProteinsSet;
    private HashMap<String, Set<IVertex>>   m_secondProteinsSet;
    
    /**
     * Groupwise similarity measures
     */
    
    private IGroupwiseSimilarityMeasure m_groupwiseSimMeasure;
    
    /**
     * Constructor for the GO-based benchmark with no-parameter groupwise measures
     * @param groupwiseType 
     * @param strGoOboFilename File containing the GO ontology
     * @param strGoAnnotatedFile1 
     * @param strGoAnnotatedFile2 
     */
    
    LargeGOfileBenchmark(
            GroupwiseSimilarityMeasureType  groupwiseType,
            String                          strGoOboFilename,
            String                          strGoAnnotatedFile1,
            String                          strGoAnnotatedFile2) throws Exception
    {
        // We load the GO ontology
        
        m_GOontology = OboFactory.loadOntology(strGoOboFilename);
        
        // We create the collection of groupwise measures to be evaluated
        
        m_groupwiseSimMeasure = MeasureFactory.getGroupwiseNoParameterMeasure(groupwiseType);
        
        // We load both input files
        
        m_firstProteinsSet = loadGoAnnotatedFile(strGoAnnotatedFile1);
        m_secondProteinsSet = loadGoAnnotatedFile(strGoAnnotatedFile2);
    }
   
    /**
     * Constructor for the GO-based benchmark with the SimGIC groupwise measure
     * @param icModelType 
     * @param strGoOboFilename File containing the GO ontology
     * @param strGoAnnotatedFile1 
     * @param strGoAnnotatedFile2 
     */
    
    LargeGOfileBenchmark(
            IntrinsicICModelType    icModelType,
            String                  strGoOboFilename,
            String                  strGoAnnotatedFile1,
            String                  strGoAnnotatedFile2) throws Exception
    {
        // We load the GO ontology
        
        m_GOontology = OboFactory.loadOntology(strGoOboFilename);
        
        // We set the Seco IC model
        
        ITaxonomyInfoConfigurator icModel = ICModelsFactory.getIntrinsicICmodel(icModelType);
        
        icModel.setTaxonomyData(m_GOontology.getTaxonomy());
        
        // We create the collection of groupwise measures to be evaluated
        
        m_groupwiseSimMeasure = MeasureFactory.getGroupwiseNoParameterMeasure(GroupwiseSimilarityMeasureType.SimGIC);
        
        // We load both input files
        
        m_firstProteinsSet = loadGoAnnotatedFile(strGoAnnotatedFile1);
        m_secondProteinsSet = loadGoAnnotatedFile(strGoAnnotatedFile2);
    }
    
    /**
     * Constructor for the GO-based benchmark with the BMA groupwise measure
     * using an IN-based measure
     * @param icModelType 
     * @param strGoOboFilename File containing the GO ontology
     * @param strGoAnnotatedFile1 
     * @param strGoAnnotatedFile2 
     */
    
    LargeGOfileBenchmark(
            GroupwiseMetricType     groupMetricType,
            SimilarityMeasureType   nodeSimilarityMeasureType,
            IntrinsicICModelType    icModelType,
            String                  strGoOboFilename,
            String                  strGoAnnotatedFile1,
            String                  strGoAnnotatedFile2) throws Exception
    {
        // We load the GO ontology
        
        m_GOontology = OboFactory.loadOntology(strGoOboFilename);
        
        // We set the Seco IC model
        
        ITaxonomyInfoConfigurator icModel = ICModelsFactory.getIntrinsicICmodel(icModelType);
        
        icModel.setTaxonomyData(m_GOontology.getTaxonomy());
        
        // We create the collection of groupwise measures to be evaluated
        
        m_groupwiseSimMeasure = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(m_GOontology.getTaxonomy(),
                                        nodeSimilarityMeasureType, groupMetricType);
        
        // We load both input files
        
        m_firstProteinsSet = loadGoAnnotatedFile(strGoAnnotatedFile1);
        m_secondProteinsSet = loadGoAnnotatedFile(strGoAnnotatedFile2);
    }
    
    
    /**
     * This functions loads all proteins defiend in the input file and returns
     * an indexed collection of GO-based annotations, one per protein as defined
     * in the input file.
     * @param strGoAnnotatedFilename
     * @return 
     */
    
    private HashMap<String, Set<IVertex>> loadGoAnnotatedFile(
            String  strGoAnnotatedFilename) throws IOException
    {
        // We initialize the output
        
        HashMap<String, Set<IVertex>> indexedProteins = new HashMap<>();
         
        // Warning message
        
        System.out.println("Loading GO Annotated File = " + strGoAnnotatedFilename);
        
        // We create the OBO reader
        
        BufferedReader reader = new BufferedReader(new FileReader(strGoAnnotatedFilename));
        
        // We get the Go taxonomy
        
        IVertexList goVertexes = m_GOontology.getTaxonomy().getVertexes();
        
        // We parse the concepts contained in the file
        
        String strLine;
        
        while ((strLine = reader.readLine()) != null)
        {
            // We skip the commented lines
            
            if (!strLine.startsWith("!"))
            {
                // We recover the tag-separated fields
                
                String[] strFields = strLine.split("\t");
                
                // We recover the name of the protein and GO annotation
                
                String strProteinName = strFields[1].trim();
                String strGoAnnotation = strFields[4].trim();
                
                // We retrieve the collection of annotations of the protein
                
                if (!indexedProteins.containsKey(strProteinName))
                {
                    indexedProteins.put(strProteinName, new HashSet<>());                            
                }
                
                HashSet<IVertex> goAnnotations = (HashSet) indexedProteins.get(strProteinName);
                
                // We save the new Go annotation
                
                IOboConcept goConcept = m_GOontology.getConceptById(strGoAnnotation);
                
                if (goConcept != null)
                {
                    goAnnotations.add(goVertexes.getById(goConcept.getTaxonomyNodeId()));
                }
            }
        }
        
        // We close the file
        
        reader.close();
        
        // We return the result
         
        return (indexedProteins);
    }
    
    /**
     * This computes the overall count of GO-based annotations which will be
     * evaluated by this experiment.
     * @return 
     */

    private long getOverallGoAnnotations()
    {
        // We compute the overall number of GO-based comparisons
        
        long firstGoAnnotations = 0;
        
        for (Set<IVertex> annotationSet : m_firstProteinsSet.values())
        {
            firstGoAnnotations += annotationSet.size();
        }
        
        long secondGoAnnotations = 0;
        
        for (Set<IVertex> annotationSet : m_secondProteinsSet.values())
        {
            secondGoAnnotations += annotationSet.size();
        }
        
        // We compute the overall annotation count
        
        long overallAnnotations = firstGoAnnotations * secondGoAnnotations;
        
        // We return the result
        
        return (overallAnnotations);
    }
    
    /**
     * This function runs the benchmark and writes the results to the
     * output file.
     */
    
    @Override
    public void run(String strOutputFilename) throws Exception
    {
        // We create the output data matrix
        
        String[][] strOutputMatrix = new String[2][4];
        
        // We fill the headers
        
        strOutputMatrix[0][0] = "Groupwise measure";
        strOutputMatrix[0][1] = "# protein pairs";
        strOutputMatrix[0][2] = "# GO similarity evaluations";
        strOutputMatrix[0][3] = "Overall time (secs)";
        
        // Warning message

        System.out.println("Evaluating GO files with " + m_groupwiseSimMeasure.toString());

        // We start the stopwatch

        long startWatch = System.currentTimeMillis();

        // We evaluate all protein pairs

        for (Set<IVertex> protein1 : m_firstProteinsSet.values())
        {
            for (Set<IVertex> protein2 : m_secondProteinsSet.values())
            {
                m_groupwiseSimMeasure.getSimilarity(protein1, protein2);
            }
        }

        // We compute the ellapsed time

        double ellapedTimeSecs = (System.currentTimeMillis() - startWatch) / 1000.0;

        // We save the result

        strOutputMatrix[1][0] = m_groupwiseSimMeasure.toString();
        strOutputMatrix[1][1] = Long.toString(m_firstProteinsSet.size() * m_secondProteinsSet.size());
        strOutputMatrix[1][2] = Long.toString(getOverallGoAnnotations());
        strOutputMatrix[1][3] = Double.toString(ellapedTimeSecs);
        
        // We write the results
        
        SemanticLibraryBenchmark.writeCSVfile(strOutputMatrix, strOutputFilename);
    }   
    
    /**
     * This function releases the resouurces used by the benchmark.
     */
    
    @Override
    public void clear()
    {
        // We release all GO-based annotations
        
        for (Set<IVertex> annotationSet : m_firstProteinsSet.values())
        {
            annotationSet.clear();
        }

        for (Set<IVertex> annotationSet : m_secondProteinsSet.values())
        {
            annotationSet.clear();
        }
        
        // We relrease all resources
        
        m_GOontology.clear();
        m_firstProteinsSet.clear();
        m_secondProteinsSet.clear();
    }
}
