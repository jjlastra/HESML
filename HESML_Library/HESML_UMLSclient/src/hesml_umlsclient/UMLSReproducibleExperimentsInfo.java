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
import hesml.measures.ISimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import hesml.taxonomyreaders.mesh.impl.MeSHFactory;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import java.io.IOException;
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
        String strUmlsCuiFilename = umlsOntologyNode.getElementsByTagName("UMLSCuiFilename").item(0).getTextContent();
        
        // We chek which ontology will be used: MeSH or SNOMED
        
        if (umlsOntologyNode.getElementsByTagName("MeSH").getLength() > 0)
        {
            Element meshNode = (Element) umlsOntologyNode.getElementsByTagName("MeSH").item(0);
            
            String strMeshFilename = meshNode.getElementsByTagName("XmlMeSHOntologyFilename").item(0).getTextContent();
            
            // We load the MeSH ontology
            
            m_MeshOntology = MeSHFactory.loadMeSHOntology(strMeshFilename, strUmlsCuiFilename);
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
                                strSnomedDescriptionFilename, strOntologyFileDir,
                                strUmlsCuiFilename);
        }
    }
    
    /**
     * This function parses and loads the similarity measure which will be
     * evaluated in this experiments.
     * @param taxonomy
     * @param similarityMeasureDefinitions 
     */
    
    private ISimilarityMeasure loadSimilarityMeasure(
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
        
        // We load the pairwise similarity measure
        
        ISimilarityMeasure measure = MeasureFactory.getMeasure(taxonomy, inputMeasureType);
        
        // We return the result
        
        return (measure);
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
