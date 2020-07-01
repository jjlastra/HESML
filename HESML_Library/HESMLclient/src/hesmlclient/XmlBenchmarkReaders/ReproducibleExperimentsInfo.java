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

package hesmlclient.XmlBenchmarkReaders;

import hesml.benchmarks.ISimilarityBenchmark;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import hesml.taxonomyreaders.wordnet.impl.WordNetFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class encapsulates the input data provided in a XML mfile in order
 * to define a collection of reproducible experiments.
 * @author j.lastra
 */

public class ReproducibleExperimentsInfo
{    
    /**
     * Reproducible experiment definitions.
     */
    
    private final  ArrayList<ISimilarityBenchmark>    m_Experiments;
    
    /**
     * Output directory
     */
    
    private final String    m_strOutputDir;

    /**
     * We keep in memory all the versions used by the experiments.
     */
    
    private final HashMap<String, IWordNetDB>   m_WordNetDbVersions;
    private final HashMap<String, ITaxonomy>    m_WordNetDbTaxonomies;
    
    /**
     * Directory containing the WordNet database file
     */
    
    private String    m_strWordNetDBdir;
    
    /**
     * Full path of the WordNet database file
     */    
    
    private String    m_strWordNetDBfilename;
        
    /**
     * Constructor from an input XML file
     * @param inputXmlExpFile
     * @param strSchemaFilename
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    
    public ReproducibleExperimentsInfo(
        File    inputXmlExpFile,
        String  strSchemaFilename) throws IOException, SAXException, ParserConfigurationException, Exception
    {
        // We define the output directory as the directory of the input file
        
        m_strOutputDir = inputXmlExpFile.getCanonicalFile().getParent();
        m_Experiments = new ArrayList<>();
        
        // We define the collection of WordNet and taxonomy versions
        
        m_WordNetDbVersions = new HashMap<>();
        m_WordNetDbTaxonomies = new HashMap<>();
        
        // We create the schema file
        
        File schemaFile = new File(m_strOutputDir + "/" + strSchemaFilename);        
        
        // We validate the file using the schema
        
        if (schemaFile.exists())
        {
            validateXMLSchema(inputXmlExpFile, schemaFile);
        }
        
        // We configure the Xml parser in order to validate the Xml file
        // by using the schema that describes the Xml file format
        // for the reproducible experiments.
        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
        
        // We parse the input document
        
        Document xmlDocument = docBuilder.parse(inputXmlExpFile);
        
        // We get the root node
        
        Element rootNode = xmlDocument.getDocumentElement();
        
        // We load the experiment definitions
        
        parseExperiments(rootNode);
        
        // We clear the document
        
        xmlDocument.removeChild(rootNode);
    }
    
    /**
     * This function reads the collection of experiments.
     * @param rootNode 
     */
    
    private void parseExperiments(
        Element rootNode) throws Exception
    {
        // We get the node with collection of experiments 
        
        NodeList experimentNodes = rootNode.getChildNodes();
        
        // We traverse the collection of experiments parsing them
        
        for (int i = 0; i < experimentNodes.getLength(); i++)
        {
            // We get the next experiment in the Xml node collection
            
            if (experimentNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                // We get the experiment root node
                
                Element experimentRoot = (Element) experimentNodes.item(i);

                // We declare the abstract reader and loaded experiment
                
                XmlBenchmarkReader reader = null;
                
                // We parse the experiment node according to its type

                switch (experimentRoot.getTagName())
                {
                    case "MixedICmodelsExperiment":
                        
                        reader = new MixedICmodelsExperimentReader(
                                        m_WordNetDbVersions,
                                        m_WordNetDbTaxonomies);
                        
                        break;
                        
                    case "SingleICmodelExperiment":

                        reader = new SingleICmodelExperimentReader(
                                        m_WordNetDbVersions,
                                        m_WordNetDbTaxonomies);

                        break;

                    case "MultipleICmodelsExperiment":

                        reader = new MultipleICmodelsExperimentReader(
                                        m_WordNetDbVersions,
                                        m_WordNetDbTaxonomies);

                        break;

                    case "NonICbasedSimMeasuresExperiment":

                        reader = new NonICbasedSimMeasuresExperimentReader(
                                        m_WordNetDbVersions,
                                        m_WordNetDbTaxonomies);

                        break;

                    case "CorpusICmodelsExperiment":

                        reader = new CorpusICmodelsExperimentReader(
                                        m_WordNetDbVersions,
                                        m_WordNetDbTaxonomies);

                        break;
                        
                    case "MultipleDatasetsExperiment":
                        
                        reader = new MultipleDatasetsExperimentReader(
                                        m_WordNetDbVersions,
                                        m_WordNetDbTaxonomies);
                        
                        break;
                        
                    case "SingleDatasetSimilarityValuesExperiment":
                        
                        reader = new SingleDatasetSimilarityValuesExperimentReader(
                                        m_WordNetDbVersions,
                                        m_WordNetDbTaxonomies);
                        
                        break;
                }
                
                // We check that the node is valid
                
                if (reader != null)
                {
                    // We load the experiment
                
                    ISimilarityBenchmark experiment = reader.readBenchmark(experimentRoot);
                
                    // We read the output filename
                
                    String strOutputFilename = readStringField(experimentRoot, "OutputFileName");                
                
                    // We set the output full filename

                    experiment.setDefaultOutputFilename(m_strOutputDir + "/" + strOutputFilename);

                    // We store the experiment

                    m_Experiments.add(experiment);
                }
            }
        }
    }
    
    /**
     * This function reads the WordNet reference from the XML node of the
     * experiment and gets the WordNet DB from the cache.
     * @param strWNfullpath
     * @return 
     */
    
    private IWordNetDB getExpWordNetDB(
            String  strWNfullpath) throws FileNotFoundException, Exception
    {
        IWordNetDB  wordnet;    // Returned value
        
        // We check the existence of the WordNet DB in the cache of WN versions
        
        if (m_WordNetDbVersions.containsKey(strWNfullpath))
        {
            wordnet = m_WordNetDbVersions.get(strWNfullpath);
        }
        else
        {
            // We check and load the WordNet database file

            File    wordNetFile = new File(strWNfullpath);

            if (!wordNetFile.exists())
            {
                throw (new FileNotFoundException(wordNetFile.getCanonicalPath()));
            }
            
            // We load the WN database
            
            wordnet = WordNetFactory.loadWordNetDatabase(strWNfullpath);
            
            // We store the WN database instance in the cache
            
            m_WordNetDbVersions.put(strWNfullpath, wordnet);
            
            // We build the taxonomy

            System.out.println("Building the WordNet taxonomy ...");

            ITaxonomy   taxonomy = WordNetFactory.buildTaxonomy(wordnet);

            // We pre-process the taxonomy to compute all the parameters
            // used by the intrinsic IC-computation methods

            System.out.println("Pre-processing the WordNet taxonomy");

            taxonomy.computesCachedAttributes();
            taxonomy.computeCachedAncestorSet(false);
            
            // We store the taxonomy
            
            m_WordNetDbTaxonomies.put(strWNfullpath, taxonomy);
        }
        
        // We return the result
        
        return (wordnet);
    }
    
    /**
     * This function reads the text value of a child element.
     * @param parent
     * @param strFieldName
     * @return 
     */
    
    private String readStringField(
            Element parent,
            String  strFieldName)
    {
        String  strText;    // Returned value
        
        Element child;  // Child node
        
        // We get the child node matching the input name

        child = getFirstChildWithTagName(parent, strFieldName);
        
        if (child == null)
        {
            throw (new IllegalArgumentException(strFieldName));
        }
        
        // We get the text field
        
        strText = child.getFirstChild().getNodeValue();
        
        // We return the result
        
        return (strText);
    }
    
    /**
     * This function returns the first child element whose tag name matches
     * the input tag name.
     * @param parent
     * @param strChildTagName
     * @return 
     */
    
    private Element getFirstChildWithTagName(
            Element parent,
            String  strChildTagName)
    {
        Element selectedChild = null;   // Returned value
        
        NodeList    children = parent.getChildNodes();
        
        // We traverse the direct child nodes
        
        for (int i = 0, nCount = children.getLength(); i < nCount; i++)
        {
            // We get the next child node
            
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                // We get the element
                
                Element child = (Element)children.item(i);
                
                // We look for the required child node

                if (child.getTagName().equals(strChildTagName))
                {
                    selectedChild = child;
                    break;
                }
            }
        }
        
        // We return the result
        
        return (selectedChild);
    }
    
    /**
     * This function validates an input file
     * @param xmlInputFile XML input file to be validated
     * @param xsdFile XML scheme used to validate the input file
     */
    
    private static void validateXMLSchema(
            File    xmlInputFile,
            File    xsdFile) throws SAXException, IOException
   {
       SchemaFactory    factory;    // Schema objects
       Schema           schema;
       
        // We create the schema factory and the schema instance

        factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schema = factory.newSchema(xsdFile);

        // We create the validator, then the input file is validated on-the-fly

        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xmlInputFile));
    }    
    
    /**
     * This function executes all experiments.
     * @throws java.lang.Exception
     */
    
    public void ExecuteAllExperiments() throws Exception
    {
        // We execute all experiments and release their resources
        
        for (ISimilarityBenchmark benchmark: m_Experiments)
        {
            benchmark.executeTests(benchmark.getDefaultOutputFilename(), true);
        }
    }
        
    /**
     * This function release the resources allocated by the object.
     * Once this function is called, the object cannot be used anymore.
     */
    
    public void clear()
    {
        // We release all the taxonomies and WordNet DB versions loaded
        // in order to run the experiments.
        
        for (ITaxonomy taxonomy: m_WordNetDbTaxonomies.values())
        {
            taxonomy.clear();
        }
        
        for (IWordNetDB wordnet: m_WordNetDbVersions.values())
        {
            wordnet.clear();
        }
        
        // We release all the experiments and their resources
        
        for (ISimilarityBenchmark benchmark: m_Experiments)
        {
            benchmark.clear();
        }
        
        // We release the cache of WN versions and experiments
        
        m_WordNetDbTaxonomies.clear();
        m_WordNetDbVersions.clear();
        m_Experiments.clear();
    }
}
