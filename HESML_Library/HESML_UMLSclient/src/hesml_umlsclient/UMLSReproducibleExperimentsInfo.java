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

package hesml_umlsclient;

import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.GroupwiseMetricType;
import hesml.measures.IGroupwiseSimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import hesml.taxonomyreaders.mesh.impl.MeSHFactory;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class implements the parsing of any UMLS-based semantic similarity
 * experiments encoding the evaluation of the degree of similarity between
 * CUI concept pairs defined in a compleemntary dataset file in CSV file format.
 * 
 * This class parses (*.umlsexp) files in XML file format as defined by
 * the following schema file included in the HESML distribution. This schema
 * file was created with XML Spy 5.
 * ../ReproducibleExperiments/UmlsBasedExperiments.xsd
 * 
 * @author j.lastra
 */

class UMLSReproducibleExperimentsInfo
{
    /**
     * MeSH ontology loaded in memory
     */
    
    private IMeSHOntology   m_MeshOntology;
    
    /**
     * SNOMED-CT ontology
     */
    
    private ISnomedCtOntology   m_SnomedOntology;
    
    /**
     * Experiments file
     */
    
    private String  m_strXmlExperimentsFilename;

    /**
     * This constructor loads the XML-based experiemtns into this instance
     * @param strXmlExperimentsFilename 
     */
    
    UMLSReproducibleExperimentsInfo (
            String  strXmlExperimentsFilename)
    {
        m_strXmlExperimentsFilename = strXmlExperimentsFilename;
        m_MeshOntology = null;
        m_SnomedOntology = null;
    }
    
    /**
     * This function runs all experiments detailed in the XML file format
     * previously loaded in an instance of this class.
     */
    
    public void RunAllExperiments() throws SAXException,
            IOException, ParserConfigurationException, Exception
    {
        // We configure the Xml parser in order to validate the Xml file
        // by using the schema that describes the Xml file format
        // for the reproducible experiments.
        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
        
        // We parse the input document
        
        Document xmlDocument = docBuilder.parse(m_strXmlExperimentsFilename);
        
        // We get the root node
        
        Element rootNode = xmlDocument.getDocumentElement();
        rootNode.normalize();
        
        // We get the node with collection of experiments 
        
        NodeList experimentNodes = xmlDocument.getElementsByTagName("UMLSExperiment");
        
        // We traverse the collection of experiments parsing them
        
        for (int i = 0; i < experimentNodes.getLength(); i++)
        {
            if (experimentNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                ParseAndRunExperiment((Element) experimentNodes.item(i));
            }
        }
        
        // We clear the document
        
        xmlDocument.removeChild(rootNode);
    }
    
    /**
     * This function returns the directory of the input experiment file.
     * @return 
     */
    
    private String getInputExperimentFileDir()
    {
        // We obtain the directory of the input file
        
        File fileInfo = new File(m_strXmlExperimentsFilename);
        
        // We return the result
        
        return (fileInfo.getParentFile().getPath());
    }
    
    /**
     * This function clear the ontologies used for each experiment
     */
    
    private void clear()
    {
        // We releaso both ontolgies
        
        if (m_MeshOntology != null)
        {
            m_MeshOntology.clear();
            m_MeshOntology = null;
        }
        
        if (m_SnomedOntology != null)
        {
            m_SnomedOntology.clear();
            m_SnomedOntology = null;
        }
    }
    /**
     * This function parses and run a single experiment
     * @param experimentNode 
     */
    
    private void ParseAndRunExperiment(
        Element experimentNode) throws Exception
    {
        // We release the ontologies
        
        clear();
        
        // We read the input and output files
        
        String strInputCuiPairsDir = experimentNode.getElementsByTagName("InputCuiPairsDir").item(0).getTextContent();
        String strInputCuiPairsFilename = experimentNode.getElementsByTagName("InputCuiPairsFilename").item(0).getTextContent();
        String strOutputFilename = experimentNode.getElementsByTagName("OutputFilename").item(0).getTextContent();
        
        // We get the basic information of the UMLS ontology including the filename
        // containing the CUI codes (MRCONSO.RRF)
        
        Element umlsOntologyNode = (Element) experimentNode.getElementsByTagName("UMLSOntology").item(0);
        
        String strOntologyFileDir = umlsOntologyNode.getElementsByTagName("OntologyFilesDir").item(0).getTextContent();
        String strUmlsCuiFileDir = umlsOntologyNode.getElementsByTagName("UMLSCuiFileDir").item(0).getTextContent();
        String strUmlsCuiFilename = umlsOntologyNode.getElementsByTagName("UMLSCuiFilename").item(0).getTextContent();
        
        // We check which ontology will be used: MeSH or SNOMED
        
        if (umlsOntologyNode.getElementsByTagName("MeSH").getLength() > 0)
        {
            // We get the main XML node of MeSH ontology
            
            Element meshNode = (Element) umlsOntologyNode.getElementsByTagName("MeSH").item(0);
            
            String strMeshFilename = strOntologyFileDir + "/" + meshNode.getElementsByTagName("XmlMeSHOntologyFilename").item(0).getTextContent();
            
            // We load the MeSH ontology
            
            m_MeshOntology = MeSHFactory.loadMeSHOntology(strMeshFilename,
                                strUmlsCuiFileDir + "/" + strUmlsCuiFilename);
        }
        else
        {
            // We load the SNOMED-CT ontology information
            
            Element snomedNode = (Element) umlsOntologyNode.getElementsByTagName("SNOMED-CT").item(0);
            
            String strSnomedConceptsFilename = snomedNode.getElementsByTagName("SnomedConceptsFilename").item(0).getTextContent();
            String strSnomedRelationshipsFilename = snomedNode.getElementsByTagName("SnomedRelationshipsFilename").item(0).getTextContent();
            String strSnomedDescriptionFilename = snomedNode.getElementsByTagName("SnomedDescriptionFilename").item(0).getTextContent();
            
            // We load the SNOMED-Ct ontology
            
            m_SnomedOntology = SnomedCtFactory.loadSnomedDatabase(strOntologyFileDir,
                                strSnomedConceptsFilename, strSnomedRelationshipsFilename,
                                strSnomedDescriptionFilename, strUmlsCuiFileDir,
                                strUmlsCuiFilename);
        }
        
        // We get the grouwise metric
        
        GroupwiseMetricType groupwiseMetric = readGroupwiseMetric(
                experimentNode.getElementsByTagName("GroupwiseMetricType").item(0).getTextContent());
        
        // We load the input CUI pair list, parse and EVALUATE the similarity measures
        
        String[][] strRawOutputMatrix = loadAndEvaluateSimilarityMeasures(experimentNode, groupwiseMetric,
                            loadInputCuiPairs(strInputCuiPairsDir + "/" + strInputCuiPairsFilename));
        
        // We write the output file for the experiment into the directory
        // containg the input epxeriemnt file
        
        writeOutputCSVfile(strRawOutputMatrix,
                getInputExperimentFileDir() + "/" + strOutputFilename);
    }
    
    /**
     * This function reads the groupwise metric type.
     * @param strGroupwiseMetricType
     * @return 
     */
    
    private GroupwiseMetricType readGroupwiseMetric(
            String  strGroupwiseMetricType)
    {
        // We retrieve the enum type
        
        GroupwiseMetricType metricType = GroupwiseMetricType.Average;
        
        // We look for the matching value
        
        for (GroupwiseMetricType groupwiseMetric: GroupwiseMetricType.values())
        {
            if (groupwiseMetric.toString().equals(strGroupwiseMetricType))
            {
                metricType = groupwiseMetric;
                break;
            }
        }
        
        // We return the result
        
        return (metricType);
    }
    
    /**
     * This funcion parses and inmmediately evaluates the similairty measures
     * defined in the XML-based experimetn file.
     * @param measuresNode XML node containing the similarity measures
     * @param strInputCuiPairs Matrix containg all CUI pairs
     * @return A matrix with the CUI pairs and one column with the similarity values for each measure
     */
    
    private String[][] loadAndEvaluateSimilarityMeasures(
            Element             measuresNode,
            GroupwiseMetricType groupwsieMetric,
            String[][]          strInputCuiPairs) throws Exception
    {
        // We get the current taxonomy
        
        ITaxonomy taxonomy = (m_MeshOntology != null) ? m_MeshOntology.getTaxonomy()
                            : m_SnomedOntology.getTaxonomy();
        
        // We retrieve the list of similarity measure nodes
        
        NodeList similarityMeasures = measuresNode.getElementsByTagName("SimilarityMeasure");
        
        // We initialize the output matrix
        
        String[][] strOutputmatrix = new String[1 + strInputCuiPairs.length][strInputCuiPairs[0].length + similarityMeasures.getLength()];
        
        strOutputmatrix[0][0] = "CUI 1";
        strOutputmatrix[0][1] = "CUI 2";
        
        // We load and evaluates the measures. IMPORTANT: the optional IC models
        // are parsed and applied to the taxonomy in function loadSimilarityMeasure()
        
        for (int iMeasure = 0, iMeasureCol = strInputCuiPairs[0].length;
                iMeasure < similarityMeasures.getLength();
                iMeasure++, iMeasureCol++)
        {
            // We get the measure node
            
            Element measureNode = (Element) similarityMeasures.item(iMeasure);
            
            // We read first the IC model because it should be applied before to
            // initialize the measure. Several IC-based measures computes a
            // normalization function which depends on the IC values.
            
            ITaxonomyInfoConfigurator icModel = loadIcModel(taxonomy, measureNode);
        
            if (icModel != null) icModel.setTaxonomyData(taxonomy);
            
            // We load the similarity measure and optional IC model
            
            SimilarityMeasureType pairwiseMeasureType = readSimilarityMeasureType(taxonomy, measureNode);
            
            IGroupwiseSimilarityMeasure groupwiseMeasure =
                    MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(taxonomy,
                        pairwiseMeasureType, groupwsieMetric);
            
            // We set the column title
            
            strOutputmatrix[0][iMeasureCol] = (icModel == null) ? groupwiseMeasure.toString()
                    : groupwiseMeasure.toString() + "-" + icModel.toString();
            
            // We evaluate the similarity of the input dataset
            
            for (int iPair = 0; iPair < strInputCuiPairs.length; iPair++)
            {
                // We get the CUI pair
                
                String strCui1 = strInputCuiPairs[iPair][0];
                String strCui2 = strInputCuiPairs[iPair][1];
                
                // We copy the CUI pair values in the first columns
                
                for (int i = 0; i < strInputCuiPairs[iPair].length; i++)
                {
                    strOutputmatrix[1 + iPair][i] = strInputCuiPairs[iPair][i];
                }
                
                // We get the the vertexes for both CUIs
                
                Set<IVertex> cuiNodes1 = (m_MeshOntology != null) ?
                                        m_MeshOntology.getTaxonomyVertexSetForUmlsCUI(strCui1) :
                                        m_SnomedOntology.getTaxonomyVertexSetForUmlsCUI(strCui1);

                Set<IVertex> cuiNodes2 = (m_MeshOntology != null) ?
                                        m_MeshOntology.getTaxonomyVertexSetForUmlsCUI(strCui2) :
                                        m_SnomedOntology.getTaxonomyVertexSetForUmlsCUI(strCui2);
                
                // We evaluate the similarity between the sets of ontoogy concepts
                
                double similarity = groupwiseMeasure.getSimilarity(cuiNodes1, cuiNodes2);
                
                // We save the similarity value
                
                strOutputmatrix[1 + iPair][iMeasureCol] = Double.toString(similarity);
                
                // We release the auxiliary sets
                
                cuiNodes1.clear();
                cuiNodes2.clear();
            }
        }
        
        // We return the result
        
        return (strOutputmatrix);
    }
    
    /**
     * This function saves an output data matrix int oa CSV file
     * @param strDataMatrix 
     * @param strOutputFilename 
     */
    
    private static void writeOutputCSVfile(
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
    
    /**
     * This function loads the CUI pairs defined in a Comma Separated Values (CSV) file.
     * @param strInputCuiPairsCSVfilename
     * @return 
     */
    
    private String[][] loadInputCuiPairs(
            String  strInputCuiPairsCSVfilename) throws IOException
    {
        // We create the reader of the file
        
        BufferedReader reader = new BufferedReader(new FileReader(strInputCuiPairsCSVfilename));
        
        // We create an auxiliary list
        
        ArrayList<String[]> strTempPairs = new ArrayList<>();
        
        // We read the content of the file in row mode
        
        String strLine;
        
        while ((strLine = reader.readLine()) != null)
        {
            // We retrieve the 3 fields
            
            String[] strFields = strLine.split(";|,");
            
            // We create a new word pair
            
            if (strFields.length > 2) strTempPairs.add(strFields);
        }
        
        // We close the file
        
        reader.close();
        
        // We cretae the output matrix
        
        String[][] strOutputCuiPairs = new String[strTempPairs.size()][strTempPairs.get(0).length];
        
        int iPair = 0;
        
        for (String[] strCuiPair : strTempPairs)
        {
            // We copy all attributes of each pair. Third column
            // would be commonly a human similairty judgement
            
            for (int iColumn = 0; iColumn < strCuiPair.length; iColumn++)
            {
                strOutputCuiPairs[iPair][iColumn] = strCuiPair[iColumn];
            }
            
            // We increase the pair counter
            
            iPair++;
        }

        // We release the temporary list
        
        strTempPairs.clear();
        
        // We return the result
        
        return (strOutputCuiPairs);
    }
    
    /**
     * This function parses and loads the similarity measure which will be
     * evaluated in this experiments.
     * @param taxonomy
     * @param similarityMeasureDefinitions 
     * @return The type of pairwise similarity measure
     */
    
    private SimilarityMeasureType readSimilarityMeasureType(
            ITaxonomy   taxonomy,
            Element     similarityMeasureNode) throws Exception
    {
        // We read the measure type
        
        String strInputMeasureType = similarityMeasureNode.getElementsByTagName("SimilarityMeasureType").item(0).getTextContent();
        
        // We retrieve the enum type
        
        SimilarityMeasureType inputMeasureType = SimilarityMeasureType.CosineLin;
        
        // We look for the matching value
        
        for (SimilarityMeasureType measureType: SimilarityMeasureType.values())
        {
            if (measureType.toString().equals(strInputMeasureType))
            {
                inputMeasureType = measureType;
                break;
            }
        }
        
        // We return the result
        
        return (inputMeasureType);
    }
    
    /**
     * This function parses and loads the similarity measure which will be
     * evaluated in this experiments.
     * @param taxonomy
     * @param similarityMeasureDefinitions 
     */
    
    private ITaxonomyInfoConfigurator loadIcModel(
            ITaxonomy   taxonomy,
            Element     icModelNode) throws Exception
    {
        // We initialize the output
        
        ITaxonomyInfoConfigurator icModel = null;
        
        // We read the IC model type
        
        if (icModelNode.getElementsByTagName("IntrinsicICModel").getLength() == 1)
        {
            // We read the IC model type
            
            String strInputIcModelType = icModelNode.getElementsByTagName("IntrinsicICModel").item(0).getTextContent();
        
            // We retrieve the enum type
        
            IntrinsicICModelType inputModelType = IntrinsicICModelType.Seco;
        
            // We look for the matching value

            for (IntrinsicICModelType icModelType: IntrinsicICModelType.values())
            {
                if (icModelType.toString().equals(strInputIcModelType))
                {
                    inputModelType = icModelType;
                    break;
                }
            }
        
            // We load the pairwise similarity measure
        
            icModel = ICModelsFactory.getIntrinsicICmodel(inputModelType);
        }
        
        // We return the result
        
        return (icModel);
    }
}
