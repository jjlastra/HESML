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

package hesml_goclient;

import hesml.HESMLversion;
import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.GroupwiseMetricType;
import hesml.measures.GroupwiseSimilarityMeasureType;
import hesml.measures.IGroupwiseSimilarityMeasure;
import hesml.measures.ISimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.IVertex;
import hesml.taxonomyreaders.obo.IOboConcept;
import hesml.taxonomyreaders.obo.IOboOntology;
import hesml.taxonomyreaders.obo.impl.OboFactory;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;

/**
 **
 * @author Juan J. Lastra-Díaz (jlastra@invi.uned.es)
 */

public class HESML_GOclient {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws IOException, ParserConfigurationException, Exception
    {
    
        // We initialize the flag to show the usage message
        
        boolean showUsage = false;
        
        // We print the HESML version
        
        System.out.println("Running HESML_GOclient V1R5 (1.5.0.1, July 2020) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        // We read the incoming parameters and load the reproducible
        // experiments defined by the user in a XML-based file with
        // extension *.umlsexp
        
        if ((args.length == 1) && (args[0].endsWith(".oboexp")))
        {
            // We load a reproducible experiment file in XML file format

            File inputFile = new File(args[0]);  // Get the file path

            // We check the existence of the file

            if (inputFile.exists())
            {
                // We get the start time

                long startFileProcessingTime = System.currentTimeMillis();

                // Loading the experiment file

                System.out.println("Loading and running the GO-based experiments defined in "
                        + inputFile.getName());

                // We parse the input file in order to recover the
                // experiments definition.

                GOReproducibleExperimentsInfo reproInfo =
                        new GOReproducibleExperimentsInfo(inputFile.getPath());

                // We execute all the experiments defined in the input file

                reproInfo.RunAllExperiments();
                reproInfo.clear();

                // We measure the elapsed time to run the experiments

                long    endTime = System.currentTimeMillis();
                long    minutes = (endTime - startFileProcessingTime) / 60000;
                long    seconds = (endTime - startFileProcessingTime) / 1000;

                System.out.println("Overall elapsed loading and computation time (minutes) = " + minutes);
                System.out.println("Overall elapsed loading and computation time (seconds) = " + seconds);
            }
        }
        else
        {
            showUsage = true;
        }
        
        // For a wrong calling to the program, we show the usage.
        
        if (showUsage)
        {
            System.err.println("\nIn order to properly use the HESML_GOclient program");
            System.err.println("you should call it using the method below:\n");
            System.err.println("(1) C:> java -jar dist\\HESML_GOclient.jar <reproexperiment.exp>");
        }
    }
    
    /**
     * This function shows how to load the GO ontology and evaluate a pairwise similarity measure.
     */
    
    private static void exampleGOpairwiseSimilarity() throws Exception
    {
        String  strGoOBOfilename = "../GeneOntology/go.obo";
        
        // We load the GO ontology
        
        IOboOntology goOntology = OboFactory.loadOntology(strGoOBOfilename);
        
        // We set the Sanchez2011 IC model
        
        ITaxonomyInfoConfigurator icModel = ICModelsFactory.getIntrinsicICmodel(IntrinsicICModelType.Sanchez2011);
        
        icModel.setTaxonomyData(goOntology.getTaxonomy());
        
        // We create an instance of the Lin measure
        
        ISimilarityMeasure linMeasure = MeasureFactory.getMeasure(goOntology.getTaxonomy(),
                                        SimilarityMeasureType.Lin);
        
        // We get the OBO concepts associated to the GOtemrs
        
        IOboConcept concept1 = goOntology.getConceptById("GO:0060139");
        IOboConcept concept2 = goOntology.getConceptById("GO:0071839");
        
        if ((concept1 != null) && (concept2 != null))
        {
            // We get the taxonomy nodes associated to the GO terms
            
            IVertex v1 = goOntology.getTaxonomy().getVertexes().getById(concept1.getTaxonomyNodeId());
            IVertex v2 = goOntology.getTaxonomy().getVertexes().getById(concept2.getTaxonomyNodeId());
            
            // We compute the pairwise similarity
            
            double similarity = linMeasure.getSimilarity(v1, v2);
        }
        
        // We relrease the ontology
        
        goOntology.clear();
    }
    
    /**
     * This function shows how to load the GO ontology and evaluate a groupwise similarity measure.
     */
    
    @SuppressWarnings("empty-statement")
    private static void exampleGOgroupwiseSimilarity() throws Exception
    {
        String  strGoOBOfilename = "../GeneOntology/go.obo";
        
        // We load the GO ontology
        
        IOboOntology goOntology = OboFactory.loadOntology(strGoOBOfilename);
        
        // We set the Sanchez2011 IC model
        
        ITaxonomyInfoConfigurator icModel = ICModelsFactory.getIntrinsicICmodel(IntrinsicICModelType.Sanchez2011);
        
        icModel.setTaxonomyData(goOntology.getTaxonomy());
        
        // We create an instance of the groupwise BMA measure based on pairwise Lin
        
        IGroupwiseSimilarityMeasure bmaLinMeasure = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(
                                                        goOntology.getTaxonomy(),
                                                        SimilarityMeasureType.Lin,
                                                        GroupwiseMetricType.BestMatchAverage);

        IGroupwiseSimilarityMeasure simGICmeasure = MeasureFactory.getGroupwiseNoParameterMeasure(
                                                    GroupwiseSimilarityMeasureType.SimGIC);
        
        // We define two sets of GO concepts
        
        String[] strGOterms1 = new String[] {"GO:0060139", "GO:0071839", "GO:0044346"};
        String[] strGOterms2 = new String[] {"GO:1902489", "GO:0016505", "GO:0043066"};

        // We get the sets of vertexes associated to both GO term list
        
        Set<IVertex> v1 = goOntology.getTaxonomyNodesForOBOterms(strGOterms1);
        Set<IVertex> v2 = goOntology.getTaxonomyNodesForOBOterms(strGOterms2);
        
        // We compute the groupwise similarity using two different groupwise measures
        
        if (!v1.isEmpty() && !v2.isEmpty())
        {
            double similarityBMA_Lin_Sanchez2011 = bmaLinMeasure.getSimilarity(v1, v2);
            double similaritySimGIC = simGICmeasure.getSimilarity(v1, v2);
        }
        
        // We relrese all resources
        
        v2.clear();
        v2.clear();
        goOntology.clear();
    }
}
