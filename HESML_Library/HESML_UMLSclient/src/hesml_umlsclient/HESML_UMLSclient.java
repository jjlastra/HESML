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
import hesml.taxonomyreaders.snomed.ISnomedConcept;
import hesml.taxonomyreaders.snomed.ISnomedCtDatabase;
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
     * UMLS direcory and files
     */

    private static String m_strUMLSdir = "../UMLS/SNOMED-CT_March_09_2020";
    private static String m_strSNOMED_conceptFilename = "sct2_Concept_Snapshot_US1000124_20200301.txt";
    private static String m_strSNOMED_relationshipsFilename = "sct2_Relationship_Snapshot_US1000124_20200301.txt";
    private static String m_strSNOMED_descriptionFilename = "sct2_Description_Snapshot-en_US1000124_20200301.txt";
    private static String m_strSNOMED_CUI_mappingfilename = "MRCONSO.RRF";
    
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
        // We load the MayoSRS dataset
        
        String[][] mayoSRSdataset = loadUMLSconceptSimilairtyCsvDataset(m_UMNSRS_datasetFilename);
        
        // We load the SNOMED-CT taxonomy
        
        ISnomedCtDatabase snomedDatabase = SnomedCtFactory.loadSnomedDatabase(m_strUMLSdir, m_strSNOMED_conceptFilename,
                                            m_strSNOMED_relationshipsFilename,
                                            m_strSNOMED_descriptionFilename,
                                            m_strSNOMED_CUI_mappingfilename, true);
        
        // We set the Sanchez et al. (2011) IC model
        
        ITaxonomyInfoConfigurator icModel = ICModelsFactory.getIntrinsicICmodel(IntrinsicICModelType.Sanchez2011);
        
        icModel.setTaxonomyData(snomedDatabase.getTaxonomy());
        
        // We obtain our cosJ&C similairty measure
        
        ISimilarityMeasure simMeasure1 = MeasureFactory.getMeasure(snomedDatabase.getTaxonomy(),
                                            SimilarityMeasureType.CosineNormJiangConrath);

        ISimilarityMeasure simMeasure2 = MeasureFactory.getMeasure(snomedDatabase.getTaxonomy(),
                                            SimilarityMeasureType.AncSPLPedersenPath);
        
        // We compute the similairty measure between all concept pairs in MayoSRS dataset
        
        evaluateMeasureInDatset(snomedDatabase, simMeasure1, mayoSRSdataset,
                "UMNSRS_cosJC_Sanchez2011.csv");

        evaluateMeasureInDatset(snomedDatabase, simMeasure1, mayoSRSdataset,
                "UMNSRS_PedersenPath.csv");
        
        // We unload SNOMED-CT
        
        snomedDatabase.clear();
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
            ISnomedCtDatabase   snomedDB,
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
            ISnomedCtDatabase   snomedDB,
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
