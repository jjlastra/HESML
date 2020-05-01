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

import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.ISimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import hesml.taxonomyreaders.mesh.impl.MeSHFactory;
import hesml.taxonomyreaders.snomed.ISnomedConcept;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
        // We execute an example on evaluating the similarity between CUI
        // concepts using MeSh ontology

        exampleMeSHsimilarity();
        
        // We execute an example on evaluating the similarity between CUI
        // concepts using SNOMED-CT ontology
        
        //exampleSnomedSimilarity();
    }
    
    /**
     * This function loads SNOMED-CT ontology in memory and computes the
     * degree of similarity between a collection of UMLS CUI pairs.
     * @throws Exception 
     */

    private static void exampleMeSHsimilarity() throws Exception
    {
        // We load the MayoSRS dataset
        
        String[][] mayoSRSdataset = loadUMLSconceptSimilairtyCsvDataset(m_UMNSRS_datasetFilename);
        
        // We load the MeSH taxonomy
        
        IMeSHOntology meshOntology = MeSHFactory.loadMeSHOntology(
                                        m_strMeSHdir + "/" + m_strMeSHdescriptorFilename);
        
        // We set the Sanchez et al. (2011) IC model
        
        ITaxonomyInfoConfigurator icModel = ICModelsFactory.getIntrinsicICmodel(IntrinsicICModelType.Seco);
        
        icModel.setTaxonomyData(meshOntology.getTaxonomy());
        
        // We obtain our cosJ&C similairty measure
        
        ISimilarityMeasure simMeasure1 = MeasureFactory.getMeasure(meshOntology.getTaxonomy(),
                                            SimilarityMeasureType.Lin);

        ISimilarityMeasure simMeasure2 = MeasureFactory.getMeasure(meshOntology.getTaxonomy(),
                                            SimilarityMeasureType.AncSPLPedersenPath);
        
        // We compute the similairty measure between all concept pairs in MayoSRS dataset

        String[][] data = new String[2][2];
        
        data[0][0] = "C0002871";
        data[0][1] = "C0002888";
        data[1][0] = "C0018670";
        data[1][1] = "C0016504";
        
        //evaluateMeasureInDatset(snomedDatabase, simMeasure1, data, "prueba_Lin.csv");
        //evaluateMeasureInDatset(snomedDatabase, simMeasure2, data, "prueba_Rada.csv");

        // We unload SNOMED-CT
        
        meshOntology.clear();
    }
    
    /**
     * This function loads SNOMED-CT ontology in memory and computes the
     * degree of similarity between a collection of UMLS CUI pairs.
     * @throws Exception 
     */

    private static void exampleSnomedSimilarity() throws Exception
    {
        // We load the MayoSRS dataset
        
        String[][] mayoSRSdataset = loadUMLSconceptSimilairtyCsvDataset(m_UMNSRS_datasetFilename);
        
        // We load the SNOMED-CT taxonomy
        
        ISnomedCtOntology snomedOntology = SnomedCtFactory.loadSnomedDatabase(m_strUMLSdir, m_strSnomedConceptFilename,
                                            m_strSnomedRelationshipsFilename,
                                            m_strSnomedDescriptionFilename,
                                            m_strUMLSdir, m_strUmlsCuiMappingFilename);
        
        // We set the Sanchez et al. (2011) IC model
        
        ITaxonomyInfoConfigurator icModel = ICModelsFactory.getIntrinsicICmodel(IntrinsicICModelType.Seco);
        
        icModel.setTaxonomyData(snomedOntology.getTaxonomy());
        
        // We obtain our cosJ&C similairty measure
        
        ISimilarityMeasure simMeasure1 = MeasureFactory.getMeasure(snomedOntology.getTaxonomy(),
                                            SimilarityMeasureType.Lin);

        ISimilarityMeasure simMeasure2 = MeasureFactory.getMeasure(snomedOntology.getTaxonomy(),
                                            SimilarityMeasureType.AncSPLPedersenPath);
        
        // We compute the similairty measure between all concept pairs in MayoSRS dataset

        String[][] data = new String[2][2];
        
        data[0][0] = "C0002871";
        data[0][1] = "C0002888";
        data[1][0] = "C0018670";
        data[1][1] = "C0016504";
        
        evaluateMeasureInDatset(snomedOntology, simMeasure1, data, "prueba_Lin.csv");
        evaluateMeasureInDatset(snomedOntology, simMeasure2, data, "prueba_Rada.csv");

        // We unload SNOMED-CT
        
        snomedOntology.clear();
    }
    
    /**
     * This function evaluates an UMLS-based concept similaritu benchmark
     * @param snomedDB
     * @param measure
     * @param strDataset
     * @param strOutputCsvFilename
     * @throws Exception 
     */
    
    private static void evaluateMeasureInDatset(
            ISnomedCtOntology   snomedDB,
            ISimilarityMeasure  measure,
            String[][]          strDataset,
            String              strOutputCsvFilename) throws Exception
    {
        // We create the output matrix
        
        String[][]  strOutputMatrix = new String[strDataset.length][3];
        
        // We evaluate the similarity between all concept pairs
        
        for (int i = 0; i < strDataset.length; i++)
        {
            // We copy the concept CUIs
            
            strOutputMatrix[i][0] = strDataset[i][0];
            strOutputMatrix[i][1] = strDataset[i][1];
            
            // We compute the similarity score

            double similarity = getSnomedSimilarity(snomedDB, measure,
                                strDataset[i][0], strDataset[i][1]);
            
            // We register the result
            
            strOutputMatrix[i][2] = Double.toString(similarity);
        }
        
        // We writye the results
        
        WriteCSVfile(strOutputMatrix, strOutputCsvFilename);
    }
    
    /**
     * This fucntion returns the degree of similarity between two
     * SNOMED-CT concepts.
     * @param snomedDB
     * @param firstConceptSnomedID
     * @param secondConceptSnomedID
     * @return 
     */

    private static double getSnomedSimilarity(
            ISnomedCtOntology   snomedDB,
            ISimilarityMeasure  measure,
            String              strFirstUmlsCUI,
            String              strSecondUmlsCUI) throws Exception
    {
        // We initilizae the output
        
        double similarity = 0.0;
        
        // We get the SNOMED concepts evoked by each CUI
        
        ISnomedConcept[] firstSnomedConcepts = snomedDB.getConceptsForUmlsCUI(strFirstUmlsCUI);
        ISnomedConcept[] secondSnomedConcepts = snomedDB.getConceptsForUmlsCUI(strSecondUmlsCUI);
        
        // We check the existence oif SNOMED concepts associated to the CUIS
        
        if ((firstSnomedConcepts.length > 0)
                && (secondSnomedConcepts.length > 0))
        {
            // We get the vertexes of the SNOMED taxonomy
            
            IVertexList vertexes = snomedDB.getTaxonomy().getVertexes();
            
            // We initialize the maximum similarity
            
            double maxSimilarity = Double.NEGATIVE_INFINITY;
            
            // We compare all pairs of evoked SNOMED concepts
            
            for (int i = 0; i < firstSnomedConcepts.length; i++)
            {
                Long snomedId1 = firstSnomedConcepts[i].getSnomedId();
                
                for (int j = 0; j < secondSnomedConcepts.length; j++)
                {
                    Long snomedId2 = secondSnomedConcepts[j].getSnomedId();
                    
                    // We evaluate the similarity measure
        
                    double snomedSimilarity = measure.getSimilarity(
                                                vertexes.getById(snomedId1),
                                                vertexes.getById(snomedId2));
                    
                    // We update the maximum similarity
                    
                    if (snomedSimilarity > maxSimilarity) maxSimilarity = snomedSimilarity;
                }
            }
            
            // We assign the output similarity value
            
            similarity = maxSimilarity;
        }
        
        // We return the result
        
        return (similarity);
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
