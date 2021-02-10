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
 *
 */

package hesml_umlsclient;

import hesml.HESMLversion;
import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.GroupwiseMetricType;
import hesml.measures.IGroupwiseSimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.IVertex;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import hesml.taxonomyreaders.mesh.impl.MeSHFactory;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * This class implements a client program to use the SNOMED-CT taxonomy
 * an the collection of semantic measures defined in HESML.
 * This class loads and executes automatic reproducible experiments
 * on biomedicla word similarity.
 * @author j.lastra
 */

public class HESML_UMLSclient
{
    /**
     * Filenames and directory for the SNOMED-CT files
     */

    private static final String m_strSnomedDir = "../UMLS/SNOMED_Nov2019";
    private static final String m_strSnomedConceptFilename = "sct2_Concept_Snapshot_US1000124_20190901.txt";
    private static final String m_strSnomedRelationshipsFilename = "sct2_Relationship_Snapshot_US1000124_20190901.txt";
    private static final String m_strSnomedDescriptionFilename = "sct2_Description_Snapshot-en_US1000124_20190901.txt";

    /**
     * Filename and directory for the UMLS CUI mapping file
     */
    
    private static final String m_strUmlsCuiMappingFilename = "MRCONSO.RRF";
    private static final String m_strUMLSdir = "../UMLS/UMLS2019AB";    
    
    /**
     * Filenames and directory for the MeSH ontology
     */
    
    private static final String m_strMeSHdir = "../UMLS/MeSH_Nov2019";
    private static final String m_strMeSHdescriptorFilename = "desc2020.xml";
    
    /**
     * Path tof MayoSRS dataset
     */
    
    private static String m_UMNSRS_datasetFilename = "../UMLS_Datasets/UMNSRS_similarity.csv";
    
    /**
     * This function admits the execution of reproducible word similarity
     * experiments based on SNOMED-CT defined in the (*.snexp) XML-based
     * file format.
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws Exception
    {
        // We initialize the flag to show the usage message
        
        boolean showUsage = false;
        
        // We print the HESML version
        
        System.out.println("Running HESML_UMLSclient V1R5 (1.5.0.1, July 2020) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        // We read the incoming parameters and load the reproducible
        // experiments defined by the user in a XML-based file with
        // extension *.umlsexp
        
        if ((args.length == 1) && (args[0].endsWith(".umlsexp")))
        {
            // We load a reproducible experiment file in XML file format

            File inputFile = new File(args[0]);  // Get the file path

            // We check the existence of the file

            if (inputFile.exists())
            {
                // We get the start time

                long startFileProcessingTime = System.currentTimeMillis();

                // Loading the experiment file

                System.out.println("Loading and running the UMLS-based experiments defined in "
                        + inputFile.getName());

                // We parse the input file in order to recover the
                // experiments definition.

                UMLSReproducibleExperimentsInfo reproInfo =
                        new UMLSReproducibleExperimentsInfo(inputFile.getPath());

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
            System.err.println("\nIn order to properly use the HESML_UMLSclient program");
            System.err.println("you should call it using the method below:\n");
            System.err.println("(1) C:> java -jar dist\\HESML_UMLSclient.jar <reproexperiment.exp>");
        }
    }
    
    /**
     * This function loads SNOMED-CT ontology in memory and computes the
     * degree of similarity between a collection of UMLS CUI pairs.
     * @throws Exception 
     */

    private static void exampleMeSHsimilarity() throws Exception
    {
        // We load the MayoSRS dataset
        
        String[][] UMNSRSdataset = loadUMLSconceptSimilairtyCsvDataset(m_UMNSRS_datasetFilename);
        
        // We load the MeSH taxonomy
        
        IMeSHOntology meshOntology = MeSHFactory.loadMeSHOntology(
                                        m_strMeSHdir + "/" + m_strMeSHdescriptorFilename,
                                        m_strUMLSdir + "/" + m_strUmlsCuiMappingFilename);
        
        // We set the Sanchez et al. (2011) IC model
        
        ITaxonomyInfoConfigurator icModel = ICModelsFactory.getIntrinsicICmodel(IntrinsicICModelType.Sanchez2011);
        
        icModel.setTaxonomyData(meshOntology.getTaxonomy());
        
        // We create Lin and AncSPLRada similarity measures
        
        IGroupwiseSimilarityMeasure simMeasure1 = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(
                                                    meshOntology.getTaxonomy(),
                                                    SimilarityMeasureType.Lin,
                                                    GroupwiseMetricType.Maximum);

        IGroupwiseSimilarityMeasure simMeasure2 = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(
                                                    meshOntology.getTaxonomy(),
                                                    SimilarityMeasureType.AncSPLRada,
                                                    GroupwiseMetricType.Maximum);
        
        // We compute the similairty measure between all concept pairs defined below.
        // You can also use the UMNSRS datset loaded above.

        String[][] data = new String[2][2];
        
        data[0][0] = "C0002871";
        data[0][1] = "C0002888";
        data[1][0] = "C0018670";
        data[1][1] = "C0016504";
        
        evaluateSimilarityWithMeSH(meshOntology, simMeasure1, data, "prueba_Lin_MeSH.csv");
        evaluateSimilarityWithMeSH(meshOntology, simMeasure2, data, "prueba_AncSPLRada_MeSH.csv");

        // We unload SNOMED-CT
        
        meshOntology.clear();
    }
    
    /**
     * This function evaluates an UMLS-based concept similaritu benchmark
     * using MeSH ontology
     * @param snomedDB
     * @param measure
     * @param strDataset
     * @param strOutputCsvFilename
     * @throws Exception 
     */
    
    private static void evaluateSimilarityWithMeSH(
            IMeSHOntology               meshOntology,
            IGroupwiseSimilarityMeasure measure,
            String[][]                  strDataset,
            String                      strOutputCsvFilename) throws Exception
    {
        // We create the output matrix
        
        String[][]  strOutputMatrix = new String[strDataset.length][3];
        
        // We evaluate the similarity between all concept pairs
        
        for (int i = 0; i < strDataset.length; i++)
        {
            // We get the next pair of CUIs

            String strFirstCui = strDataset[i][0];
            String strSecondCui = strDataset[i][1];
            
            // We copy the concept CUIs
            
            strOutputMatrix[i][0] = strFirstCui;
            strOutputMatrix[i][1] = strSecondCui;

            // We get the SNOMED concept IDs evoked by each CUI, which sets their
            // corresponding vertexes in the taxonomy.

            Set<IVertex> cuiVertexes1 = meshOntology.getTaxonomyVertexSetForUmlsCUI(strFirstCui);
            Set<IVertex> cuiVertexes2 = meshOntology.getTaxonomyVertexSetForUmlsCUI(strSecondCui);

            // We get the similarity between both CUIs as the hjighest similarity
            // score between both sets of taxonomy nodes.

            strOutputMatrix[i][2] = Double.toString(measure.getSimilarity(cuiVertexes1, cuiVertexes2));
            
            // We release the vertex sets
            
            cuiVertexes1.clear();
            cuiVertexes2.clear();
        }
        
        // We writye the results
        
        WriteCSVfile(strOutputMatrix, strOutputCsvFilename);
    }
    
    
    /**
     * This function loads SNOMED-CT ontology in memory and computes the
     * degree of similarity between a collection of UMLS CUI pairs.
     * @throws Exception 
     */

    private static void exampleSnomedSimilarity() throws Exception
    {
        // We load the MayoSRS dataset
        
        String[][] UMNSRSdataset = loadUMLSconceptSimilairtyCsvDataset(m_UMNSRS_datasetFilename);
        
        // We load the SNOMED-CT taxonomy
        
        ISnomedCtOntology snomedOntology = SnomedCtFactory.loadSnomedDatabase(m_strUMLSdir, m_strSnomedConceptFilename,
                                            m_strSnomedRelationshipsFilename,
                                            m_strSnomedDescriptionFilename,
                                            m_strUMLSdir, m_strUmlsCuiMappingFilename);
        
        // We set the Sanchez et al. (2011) IC model
        
        ITaxonomyInfoConfigurator icModel = ICModelsFactory.getIntrinsicICmodel(IntrinsicICModelType.Sanchez2011);
        
        icModel.setTaxonomyData(snomedOntology.getTaxonomy());
        
        // We create Lin and AncSPLRada similarity measures
        
        IGroupwiseSimilarityMeasure simMeasure1 = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(
                                                    snomedOntology.getTaxonomy(),
                                                    SimilarityMeasureType.Lin,
                                                    GroupwiseMetricType.Maximum);

        IGroupwiseSimilarityMeasure simMeasure2 = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(
                                                    snomedOntology.getTaxonomy(),
                                                    SimilarityMeasureType.AncSPLRada,
                                                    GroupwiseMetricType.Maximum);
        
        // We compute the similarIty measure between all concept pairs in MayoSRS dataset

        String[][] data = new String[2][2];
        
        data[0][0] = "C0002871";
        data[0][1] = "C0002888";
        data[1][0] = "C0018670";
        data[1][1] = "C0016504";
        
        evaluateSimilarityWithSNOMED(snomedOntology, simMeasure1, data, "prueba_Lin_SNOMED.csv");
        evaluateSimilarityWithSNOMED(snomedOntology, simMeasure2, data, "prueba_AncSPLRada_SNOMED.csv");

        // We unload SNOMED-CT
        
        snomedOntology.clear();
    }
    
    /**
     * This function evaluates an UMLS-based concept similaritu benchmark
     * * using SNOMED ontology
     * @param snomedDB
     * @param measure
     * @param strDataset
     * @param strOutputCsvFilename
     * @throws Exception 
     */
    
    private static void evaluateSimilarityWithSNOMED(
            ISnomedCtOntology           snomedDB,
            IGroupwiseSimilarityMeasure measure,
            String[][]                  strDataset,
            String                      strOutputCsvFilename) throws Exception
    {
        // We create the output matrix
        
        String[][]  strOutputMatrix = new String[strDataset.length][3];
        
        // We evaluate the similarity between all concept pairs
        
        for (int i = 0; i < strDataset.length; i++)
        {
            // We get the next pair of CUIs

            String strFirstCui = strDataset[i][0];
            String strSecondCui = strDataset[i][1];
            
            // We copy the concept CUIs
            
            strOutputMatrix[i][0] = strFirstCui;
            strOutputMatrix[i][1] = strSecondCui;

            // We get the SNOMED concept nodes in thetaxonbomy which are evokedby the CUIs

            Set<IVertex> cuiVertexes1 = snomedDB.getTaxonomyVertexSetForUmlsCUI(strFirstCui);
            Set<IVertex> cuiVertexes2 = snomedDB.getTaxonomyVertexSetForUmlsCUI(strSecondCui);

            // We get the similarity between both CUIs

            strOutputMatrix[i][2] = Double.toString(measure.getSimilarity(cuiVertexes1, cuiVertexes2));
            
            // We release the vertex sets
            
            cuiVertexes1.clear();
            cuiVertexes2.clear();
        }
        
        // We writye the results
        
        WriteCSVfile(strOutputMatrix, strOutputCsvFilename);
    }
    
    /**
     * Thus function loads a CSV file containing a concept similarity
     * benchmark of CUI pairs.
     * @param strCsvDatsetFilename
     * @return 
     */
    
    private static String[][] loadUMLSconceptSimilairtyCsvDataset(
            String  strCsvDatsetFilename) throws FileNotFoundException, IOException
    {
        // Create the temporal list to read the file
        
        ArrayList<String[]> loadedPairs = new ArrayList<>();
        
        // We create the reader of the file
        
        BufferedReader reader = new BufferedReader(new FileReader(strCsvDatsetFilename));
        
        // We read the content of the file in row mode
        
        String strLine = reader.readLine();
        
        while (strLine != null)
        {
            // We retrieve the 3 fields
            
            String[] strFields = strLine.split(";|,");
            
            // We create a new word pair
            
            if (strFields.length == 3)
            {
                loadedPairs.add(new String[]{strFields[0], strFields[1]});
            }
            
            // We get the next line
            
            strLine = reader.readLine();
        }
        
        // We close the file
        
        reader.close();
        
        // We create the output matrix and copy all concept pairs
        
        String[][] similarityDataset = new String[loadedPairs.size()][];
        
        loadedPairs.toArray(similarityDataset);
        loadedPairs.clear();
        
        // We return the result
        
        return (similarityDataset);
    }
    
    /**
     * This function saves an output data matrix int oa CSV file
     * @param strDataMatrix 
     * @param strOutputFilename 
     */
    
    private static void WriteCSVfile(
            String[][]  strDataMatrix,
            String      strOutputFilename) throws IOException
    {
        // We create a writer for the text file
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(strOutputFilename, false));
        
        // We write the info for each taxonomy node
        
        char sep = ';';  // Separator dield
        
        for (int iRow = 0; iRow < strDataMatrix.length; iRow++)
        {
            // We initialize the line
            
            String strLine = "\n" + strDataMatrix[iRow][0];
            
            // We build the row
            
            for (int iCol = 1; iCol < strDataMatrix[0].length; iCol++)
            {
                strLine += (sep + strDataMatrix[iRow][iCol]);
            }
            
            // We write the line
            
            writer.write(strLine);
        }
        
        // We close the file
        
        writer.close();
    }
}
