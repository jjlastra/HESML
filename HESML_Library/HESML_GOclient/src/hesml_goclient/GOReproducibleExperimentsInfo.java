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

import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.GroupwiseMetricType;
import hesml.measures.IGroupwiseSimilarityMeasure;
import hesml.measures.ISimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import hesml.taxonomyreaders.mesh.impl.MeSHFactory;
import hesml.taxonomyreaders.obo.IOboOntology;
import hesml.taxonomyreaders.obo.impl.OboFactory;
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
 * This class implements the parsing of any GO-based semantic similarity
 * experiments encoding the evaluation of the degree of similarity between
 * GO concept pairs defined in a complementary dataset file in CSV file format.
 * 
 * This class parses (*.umlsexp) files in XML file format as defined by
 * the following schema file included in the HESML distribution. This schema
 * file was created with XML Spy 5.
 * ../ReproducibleExperiments/GOBasedExperiments.xsd
 * 
 * @author j.lastra
 */

class GOReproducibleExperimentsInfo
{
    /**
     * SNOMED-CT ontology
     */
    
    private IOboOntology   m_OboOntology;
    
    /**
     * Experiments file
     */
    
    private String  m_strXmlExperimentsFilename;

    /**
     * This constructor loads the XML-based experiemtns into this instance
     * @param strXmlExperimentsFilename 
     */
    
    GOReproducibleExperimentsInfo (
            String  strXmlExperimentsFilename)
    {
        m_strXmlExperimentsFilename = strXmlExperimentsFilename;
        m_OboOntology = null;
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
        
        NodeList experimentNodes = xmlDocument.getElementsByTagName("OBOExperiment");
        
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
    
    public void clear()
    {
        // We releaso both ontolgies
        
        if (m_OboOntology != null)
        {
            m_OboOntology.clear();
            m_OboOntology = null;
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
        
        String strInputFilesDir = experimentNode.getElementsByTagName("InputFilesDirectory").item(0).getTextContent();
        String strOutputFilename = experimentNode.getElementsByTagName("OutputFilename").item(0).getTextContent();
        
        // We get the basic information of the UMLS ontology including the filename
        // containing the CUI codes (MRCONSO.RRF)
        
        Element umlsOntologyNode = (Element) experimentNode.getElementsByTagName("OBOOntology").item(0);
        
        String strOntologyFileDir = umlsOntologyNode.getElementsByTagName("OntologyDir").item(0).getTextContent();
        String strOBOFilename = umlsOntologyNode.getElementsByTagName("OBOfilename").item(0).getTextContent();
        
        // We load the SNOMED-Ct ontology

        m_OboOntology = OboFactory.loadOntology(strOntologyFileDir + "/" + strOBOFilename);
        
        // We defibne the raw output matrix
        
        String[][] strRawOutputMatrix = null;
        
        // There are two types of experiments as follows: (1) the pairwise comparison of GO pairs,
        // and (2) the comparison of sets of GO terms
        
        if (experimentNode.getElementsByTagName("PairwiseEvaluation").getLength() > 0)
        {
            // We get the node for the pairwise evaluation
            
            Element pairwiseEvalNode = (Element) experimentNode.getElementsByTagName("PairwiseEvaluation").item(0);
            String strInputGOpairsFilename = pairwiseEvalNode.getElementsByTagName("InputGOpairsFilename").item(0).getTextContent();
            
            strRawOutputMatrix = loadAndEvaluatePairwiseSimMeasures(experimentNode,
                            loadInputGOPairs(strInputFilesDir + "/" + strInputGOpairsFilename));
        }
        else if (experimentNode.getElementsByTagName("GroupwiseEvaluation").getLength() > 0)
        {
            // We get the node for the pairwise evaluation
            
            Element groupwisewiseEvalNode = (Element) experimentNode.getElementsByTagName("GroupwiseEvaluation").item(0);
            
            String strInputGOtermsFilename1 = groupwisewiseEvalNode.getElementsByTagName("InputGOtermsFilename1").item(0).getTextContent();
            String strInputGOtermsFilename2 = groupwisewiseEvalNode.getElementsByTagName("InputGOtermsFilename2").item(0).getTextContent();
            
            // We evaluate all groupwise measures
            
            strRawOutputMatrix = loadAndEvaluateGroupwiseSimMeasures(m_OboOntology,
                                    groupwisewiseEvalNode,
                                    loadGOtermList(strInputFilesDir + "/" + strInputGOtermsFilename1),
                                    loadGOtermList(strInputFilesDir + "/" + strInputGOtermsFilename2));
        }
        
        // We write the output file for the experiment into the directory
        // containg the input epxeriemnt file
        
        writeOutputCSVfile(strRawOutputMatrix,
                getInputExperimentFileDir() + "/" + strOutputFilename);
    }
    
    /**
     * This functions reads a list of GO terms from a plain text file which contains
     * one GO term per line.
     * @param strGOtermsFilename
     * @return 
     */
    
    private String[] loadGOtermList(
            String  strGOtermsFilename) throws IOException
    {
        // We create the reader of the file
        
        BufferedReader reader = new BufferedReader(new FileReader(strGOtermsFilename));

        // We create the temporary list to parse the GO terms
        
        ArrayList<String> auxTerms = new ArrayList<>();
        
        // We read the content of the file in row mode
        
        String strLine;
        
        while ((strLine = reader.readLine()) != null)
        {
            auxTerms.add(strLine);
        }
        
        // We close the file
        
        reader.close();
        
        // We create the output term vector

        String[] strOutputList = new String[auxTerms.size()];
        
        auxTerms.toArray(strOutputList);
        auxTerms.clear();
        
        // We return the result
        
        return (strOutputList);
    }
    
    /**
     * This function computes the GO-based semantic similarity between both
     * sets of GO terms using a groupwise measure
     * @param groupwiseNode
     * @param goTerms1
     * @param goTerms2
     * @return 
     */
    
    private String[][] loadAndEvaluateGroupwiseSimMeasures(
            IOboOntology    goOntology,
            Element         groupwiseNode,
            String[]        goTerms1,
            String[]        goTerms2) throws Exception
    {
        // We get the collection of groupwise measures
        
        NodeList groupwiseMeasures = groupwiseNode.getElementsByTagName("GroupwiseSimilarityMeasure");
        
        // We create the output matrix
        
        String[][] strOutputRawMatrix = new String[1 + groupwiseMeasures.getLength()][2];
        
        strOutputRawMatrix[0][0] = "Measure";
        strOutputRawMatrix[0][1] = "Similarity value";
        
        // We get the taxonomy vertexes for both GO sets
        
        Set<IVertex> goNodes1 = goOntology.getTaxonomyNodesForOBOterms(goTerms1);
        Set<IVertex> goNodes2 = goOntology.getTaxonomyNodesForOBOterms(goTerms2);
        
        // We load and evaluate each groupwise measure
        
        for (int i = 0; i < groupwiseMeasures.getLength(); i++)
        {
            // We get the next groupwise measure
            
            IGroupwiseSimilarityMeasure measure = readGroupwiseMeasure((Element) groupwiseMeasures.item(i),
                                                    goOntology.getTaxonomy());

            // We evaluate the measure
            
            strOutputRawMatrix[i + 1][0] = measure.toString();
            strOutputRawMatrix[i + 1][1] = Double.toString(measure.getSimilarity(goNodes1, goNodes2));
        }
        
        // We return the output
        
        return (strOutputRawMatrix);
    }
    
    /**
     * This function parses the groupwise XML node and creates an instance
     * of a groupwise similarity measure.
     * @param groupwiseMeasureNode
     * @param goTaxonomy
     * @return 
     */
    
    private IGroupwiseSimilarityMeasure readGroupwiseMeasure(
            Element     groupwiseMeasureNode,
            ITaxonomy   goTaxonomy) throws Exception
    {
        // We initialize the ouptut value
        
        IGroupwiseSimilarityMeasure groupwiseMeasure = null;
        
        // We check the type of groupwise measure to know which type of measure
        // should be parsed.
        
        if (groupwiseMeasureNode.getElementsByTagName("GroupwiseSimilarityType").getLength() > 0)
        {
            String strGroupwiseSimMeasure = groupwiseMeasureNode.getElementsByTagName("GroupwiseSimilarityType").item(0).getTextContent();
            
            groupwiseMeasure = MeasureFactory.getGroupwiseNoParameterMeasure(
                                MeasureFactory.convertToGroupwiseSimilarityMeasureType(strGroupwiseSimMeasure));
        }
        else if (groupwiseMeasureNode.getElementsByTagName("BasedOnPairwiseMeasure").getLength() > 0)
        {
            // We get the root node of the groupwise measure based on a pairwise similairity measure
            
            Element groupwiseDefNode = (Element)groupwiseMeasureNode.getElementsByTagName("BasedOnPairwiseMeasure").item(0);
            
            // IMPORTANT: IC model shpould be set vefore the creation of IC-based measures
            // We load the IC model whenever it has been defined
            
            if (groupwiseDefNode.getElementsByTagName("IntrinsicICModel").getLength() > 0)
            {
                String strIntrinsicICModel = groupwiseDefNode.getElementsByTagName("IntrinsicICModel").item(0).getTextContent();
                
                ITaxonomyInfoConfigurator icModel = ICModelsFactory.getIntrinsicICmodel(
                            ICModelsFactory.convertToIntrinsicICModelType(strIntrinsicICModel));
                
                // We set the IC values in the taxpnomy nodes
                
                icModel.setTaxonomyData(goTaxonomy);
            }
            
            // We parse the measure parameters
            
            String strSimilarityMeasureType = groupwiseDefNode.getElementsByTagName("SimilarityMeasureType").item(0).getTextContent();
            String strGroupwiseMetricType = groupwiseDefNode.getElementsByTagName("GroupwiseMetricType").item(0).getTextContent();
            
            groupwiseMeasure = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(goTaxonomy,
                                MeasureFactory.convertToSimilarityMeasureType(strSimilarityMeasureType),
                                MeasureFactory.convertToGroupwiseMetric(strGroupwiseMetricType));
        }
        
        // We return the result
        
        return (groupwiseMeasure);
    }
    
    /**
     * This funcion parses and inmmediately evaluates the similairty measures
     * defined in the XML-based experimetn file.
     * @param measuresNode XML node containing the similarity measures
     * @param strInputOboTermPairs Matrix containg all OBO concept pairs
     * @return A matrix with the CUI pairs and one column with the similarity values for each measure
     */
    
    private String[][] loadAndEvaluatePairwiseSimMeasures(
            Element     measuresNode,
            String[][]  strInputOboTermPairs) throws Exception
    {
        // We get the current taxonomy
        
        ITaxonomy taxonomy = m_OboOntology.getTaxonomy();
        
        // We retrieve the list of similarity measure nodes
        
        NodeList similarityMeasures = measuresNode.getElementsByTagName("PairwiseSimilarityMeasure");
        
        // We initialize the output matrix
        
        String[][] strOutputmatrix = new String[1 + strInputOboTermPairs.length][strInputOboTermPairs[0].length + similarityMeasures.getLength()];
        
        strOutputmatrix[0][0] = "OBO term 1";
        strOutputmatrix[0][1] = "OBO term 2";
        
        // We load and evaluates the measures. IMPORTANT: the optional IC models
        // are parsed and applied to the taxonomy in function loadSimilarityMeasure()
        
        for (int iMeasure = 0, iMeasureCol = strInputOboTermPairs[0].length;
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
            
            String strPairwiseMeasureType = measureNode.getElementsByTagName("SimilarityMeasureType").item(0).getTextContent();
            
            ISimilarityMeasure pairwiseMeasure = MeasureFactory.getMeasure(taxonomy,
                                                MeasureFactory.convertToSimilarityMeasureType(strPairwiseMeasureType));
            
            // We set the column title
            
            strOutputmatrix[0][iMeasureCol] = (icModel == null) ? pairwiseMeasure.toString()
                    : pairwiseMeasure.toString() + "-" + icModel.toString();
            
            // We evaluate the similarity of the input dataset
            
            for (int iPair = 0; iPair < strInputOboTermPairs.length; iPair++)
            {
                // We get the CUI pair
                
                String strOboConcept1 = strInputOboTermPairs[iPair][0];
                String strOboConcept2 = strInputOboTermPairs[iPair][1];
                
                // We copy the CUI pair values in the first columns
                
                for (int i = 0; i < strInputOboTermPairs[iPair].length; i++)
                {
                    strOutputmatrix[1 + iPair][i] = strInputOboTermPairs[iPair][i];
                }
                
                // We get the the vertexes for both OCO concepts

                IVertex vertex1 = m_OboOntology.containsConceptId(strOboConcept1) ?
                                    taxonomy.getVertexes().getById(m_OboOntology.getConceptById(strOboConcept1).getTaxonomyNodeId())
                                    : null;
                
                IVertex vertex2 = m_OboOntology.containsConceptId(strOboConcept2) ?
                                    taxonomy.getVertexes().getById(m_OboOntology.getConceptById(strOboConcept2).getTaxonomyNodeId())
                                    : null;
                
                // We evaluate the similarity between the sets of ontoogy concepts
                
                double similarity = ((vertex1 != null) && (vertex2 != null)) ?
                                    pairwiseMeasure.getSimilarity(vertex1, vertex2)
                                    : pairwiseMeasure.getNullSimilarityValue();
                
                // We save the similarity value
                
                strOutputmatrix[1 + iPair][iMeasureCol] = Double.toString(similarity);
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
     * This function loads the GO pairs defined in a Comma Separated Values (CSV) file.
     * @param strInputCuiPairsCSVfilename
     * @return 
     */
    
    private String[][] loadInputGOPairs(
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
            
            if (strFields.length > 0) strTempPairs.add(strFields);
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
        
            // We load the pairwise similarity measure
        
            icModel = ICModelsFactory.getIntrinsicICmodel(ICModelsFactory.convertToIntrinsicICModelType(strInputIcModelType));
        }
        
        // We return the result
        
        return (icModel);
    }
}
