/*
 * Copyright (C) 2016 Universidad Nacional de Educación a Distancia (UNED)
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

package hesmlclient;

import hesml.benchmarks.CorrelationOutputMetrics;
import hesml.benchmarks.ISimilarityBenchmark;
import hesml.benchmarks.impl.BenchmarkFactory;
import hesml.configurators.CorpusBasedICModelType;
import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.SimilarityMeasureType;
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
     */
    
    ReproducibleExperimentsInfo(
        File    inputXmlExpFile,
        String  strSchemaFilename) throws IOException, SAXException, ParserConfigurationException, Exception
    {
        DocumentBuilder         docBuilder;
        DocumentBuilderFactory  builderFactory;
        
        Document    xmlDocument;    // Reproducible experiments document
        
        Element rootNode;   // Root node
        File    schemaFile; // Xml schema file
                
        // We define the output directory as the directory of the input file
        
        m_strOutputDir = inputXmlExpFile.getCanonicalFile().getParent();
        m_Experiments = new ArrayList<>();
        
        // We define the collection of WordNet and taxonomy versions
        
        m_WordNetDbVersions = new HashMap<>();
        m_WordNetDbTaxonomies = new HashMap<>();
        
        // We create the schema file
        
        schemaFile = new File(m_strOutputDir + "/" + strSchemaFilename);        
        
        // We validate the file using the schema
        
        if (schemaFile.exists())
        {
            validateXMLSchema(inputXmlExpFile, schemaFile);
        }
        
        // We configure the Xml parser in order to validate the Xml file
        // by using the schema that describes the Xml file format
        // for the reproducible experiments.
        
        builderFactory = DocumentBuilderFactory.newInstance();
        docBuilder = builderFactory.newDocumentBuilder();
        
        // We parse the input document
        
        xmlDocument = docBuilder.parse(inputXmlExpFile);
        
        // We get the root node
        
        rootNode = xmlDocument.getDocumentElement();
        
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
        NodeList    experimentNodes;    // Collectionof experiments
        
        // We get the node with collection of experiments 
        
        experimentNodes = rootNode.getChildNodes();
        
        // We traverse the collection of experiments parsing them
        
        for (int i = 0, nExperiments = experimentNodes.getLength();
                i < nExperiments;
                i++)
        {
            // We get the next experiment in the Xml node collection
            
            if (experimentNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                Element experiment = (Element) experimentNodes.item(i);

                // We parse the experiment node according to its type

                switch (experiment.getTagName())
                {
                    case "MixedICmodelsExperiment":
                        
                        readMixedICmodelsExperiment(experiment);
                        
                        break;
                        
                    case "SingleICmodelExperiment":

                        readSingleICmodelExperiment(experiment);

                        break;

                    case "MultipleICmodelsExperiment":

                        readMultipleICmodelsExperiment(experiment);

                        break;

                    case "NonICbasedSimMeasuresExperiment":

                        readNonICbasedSimMeasuresExperiment(experiment);

                        break;

                    case "CorpusICmodelsExperiment":

                        readCorpusICmodelsExperiment(experiment);

                        break;
                        
                    case "MultipleDatasetsExperiment":
                        
                        readMultipleDatasetsExperiment(experiment);
                        
                        break;
                }
            }
        }
    }
    
    /**
     * This function recovers a multiple dataset experiments from its
     * Xml definition.
     * @param experimentRoot 
     */
    
    private void readMultipleDatasetsExperiment(
            Element experimentRoot) throws Exception
    {
        ISimilarityBenchmark    experiment; // Loaded experiment
        
        CorrelationOutputMetrics    metric;     // Métricas
        
        ITaxonomyInfoConfigurator[] icModels;       // IC model of each measure
        SimilarityMeasureType[]     measureTypes;   // Specific measures
            
        IWordNetDB[]    wordnetVersions;    // WordNet DB versions
        ITaxonomy[]     taxonomies;

        // We read the dataset filename and path
        
        String  strDatasetDir = readStringField(experimentRoot, "DatasetDirectory");
        String  strOutputFilename = readStringField(experimentRoot, "OutputFileName");
        String  strConceptFrqFileDir = readStringField(experimentRoot, "ConceptFrequencyFilesDir");
        String  strWNdbFilename = readStringField(experimentRoot, "WordNetDatabaseFileName");
        
        String[]    strWordNetDirectories = readStringFields(experimentRoot, "WordNetVersions");
        String[]    strDatasetFileNames = readStringFields(experimentRoot, "WNDatasets");
        
        // We build the fullnames of the dataset files
        
        for (int i = 0; i < strDatasetFileNames.length; i++)
        {
            strDatasetFileNames[i] = strDatasetDir + "/" + strDatasetFileNames[i];
        }
        
        // We read the output metrics
        
        metric = readCorrelationMetricField(experimentRoot);
        
        // We read the specific similarity measures
        
        ArrayList<SimilarityMeasureType>        measureTypeList = new ArrayList<>();
        ArrayList<ITaxonomyInfoConfigurator>    icModelsList;
        
        icModelsList = new ArrayList<>();
        
        Element xmlSimMeasures = getFirstChildWithTagName(experimentRoot, "SimilarityMeasures");
        
        NodeList    xmlSpecificMeasures = xmlSimMeasures.getChildNodes();
        
        for (int i = 0, nChild = xmlSpecificMeasures.getLength();
                i < nChild;
                i++)
        {
            // We get the current i-esim node
            
            Node    child = xmlSpecificMeasures.item(i);
            
            // We filter the Xmlelements
            
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                readSpecificSimilarityMeasure(strConceptFrqFileDir,
                       (Element) child, measureTypeList, icModelsList);
            }
        }
        
        // We copy the IC models and measures to the arrays and clear them.
        
        icModels = new ITaxonomyInfoConfigurator[icModelsList.size()];
        icModelsList.toArray(icModels);
        icModelsList.clear();
        
        measureTypes = new SimilarityMeasureType[measureTypeList.size()];
        measureTypeList.toArray(measureTypes);
        measureTypeList.clear();
    
        // We create the array to store the WordNet versions and their taxonomies
        
        wordnetVersions = new IWordNetDB[strWordNetDirectories.length];
        taxonomies = new ITaxonomy[strWordNetDirectories.length];

        // We load all the Wordnet versions and their taxonomies
        
        for (int iWordNet = 0; iWordNet < wordnetVersions.length; iWordNet++)
        {
            // We load the WordNet version and set the WordNet version name.
            // The Wordnet version is recovered from its base directory

            String  strWNfullname = strWordNetDirectories[iWordNet] + "/"
                                    + strWNdbFilename;
            
            wordnetVersions[iWordNet] = getExpWordNetDB(strWNfullname);
            wordnetVersions[iWordNet].setVersion(
                    recoverWordNetVersion(strWordNetDirectories[iWordNet]));

            // We recover the taxonomy of each WordNet version
            
            taxonomies[iWordNet] = getExpWordNetTaxonomy(strWNfullname);
        }
        
        // We create an instance from a multiple dataset experiment
        
        experiment = BenchmarkFactory.getMultipleDatasetsBenchmark(
                        wordnetVersions, taxonomies, metric,
                        strDatasetFileNames, icModels, measureTypes);
        
        // We set the output full filename
        
        experiment.setDefaultOutputFilename(m_strOutputDir + "/" + strOutputFilename);
        
        // We store the experiment
        
        m_Experiments.add(experiment);
    }
    
    /**
     * This functionrecovers the Wordnet version from the directory name
     * containing the WordNet database.
     * @param strWordNetDirectory
     * @return 
     */
    
    private String recoverWordNetVersion(
            String  strWordNetDirectory)
    {
        String  strWordNetVersion;  // Returned value
        
        File    fileInfo = new File(strWordNetDirectory);

        // We get the name of the WordNet folder
        
        strWordNetVersion = fileInfo.getParentFile().getName();
        
        // We return the result
        
        return (strWordNetVersion);
    }
    
    /**
     * This function reads the data of a specific similarity measure, which
     * is defined by a non IC-based similarity measure, or an IC-based
     * similarity measure plus an IC model. This latter IC model could be
     * intrinsic or corpus-based.
     * @param strConceptFreqFilesDir
     * @param xmlSpecificMeasure
     * @param measureTypes
     * @param icModels
     * @throws Exception 
     */
    
    private void readSpecificSimilarityMeasure(
            String                                  strConceptFreqFilesDir,
            Element                                 xmlSpecificMeasure,
            ArrayList<SimilarityMeasureType>        measureTypes,
            ArrayList<ITaxonomyInfoConfigurator>    icModels) throws Exception
    {
        ITaxonomyInfoConfigurator   icModel = null; // IC model
        
        // We read the measure type
        
        String  strMeasureType = readStringField(xmlSpecificMeasure, "MeasureType");
        
        measureTypes.add(ConvertToSimilarityMeasureType(strMeasureType));
        
        // We check the existence of an intrinsic IC model and recover it
        
        Element xmlIntrinsicICmodel = getFirstChildWithTagName(
                                        xmlSpecificMeasure, "IntrinsicICModel");
        
        if (xmlIntrinsicICmodel != null)
        {
            // We read the IC model name
            
            String  strICmodel = xmlIntrinsicICmodel.getFirstChild().getNodeValue();
            
            // We convert the name of the IC model into a qualified type
            
            IntrinsicICModelType    icModelType = ConvertToIntrinsicICmodelType(strICmodel);
            
            // We create an instance of the IC model
            
            icModel = ICModelsFactory.getIntrinsicICmodel(icModelType);
        }
        else
        {
            // We check the existence of a corpus-based IC model
            
            Element xmlCorpusICmodel = getFirstChildWithTagName(
                                        xmlSpecificMeasure, "CorpusBasedICModel");
            
            if (xmlCorpusICmodel != null)
            {
                String  strCorpusModel = readStringField(xmlCorpusICmodel, "Method");
                String  strConceptFreqFile = readStringField(xmlCorpusICmodel,
                                                "ConceptFrequencyFilename");
                
                icModel = ICModelsFactory.getCorpusICmodel(
                            ConvertToCorpusBasedICModelType(strCorpusModel),
                            strConceptFreqFilesDir + "/" + strConceptFreqFile);
            }
        }
        
        // We save the recovered IC model or null
        
        icModels.add(icModel);
    }
    
    /**
     * This function recovers an experiment from its Xml definition.
     * @param experimentRoot 
     */
    
    private void readSingleICmodelExperiment(
            Element experimentRoot) throws Exception, FileNotFoundException
    {
        ISimilarityBenchmark    experiment; // Loaded experiment
        
        IntrinsicICModelType[]  icModels = {IntrinsicICModelType.Seco};
        
        SimilarityMeasureType[] measureTypes;   // 
        
        // We read the dataset filename and path, as well as the output filename
        
        String  strDatasetFilename = readStringField(experimentRoot, "DatasetFileName");
        String  strDatasetDir = readStringField(experimentRoot, "DatasetDirectory");
        String  strDatasetFullPath = strDatasetDir + "/" + strDatasetFilename;
        String  strOutputFilename = readStringField(experimentRoot, "OutputFileName");
               
        // We check the existence of the dataset
        
        File    datasetFile = new File(strDatasetFullPath);
        
        if (!datasetFile.exists())
        {
            throw (new FileNotFoundException(strDatasetFullPath));
        }

        // We read the single IC model
        
        icModels[0] = ConvertToIntrinsicICmodelType(
                        readStringField(experimentRoot, "ICmodel"));
        
        // We read the input list of similarity measures
        
        measureTypes = readSimilarityMeasureList(experimentRoot);
        
        // We load the WN database and its taxonomy from the cache
        
        IWordNetDB  wordnet = getExperimentWordNetDB(experimentRoot);
        ITaxonomy   taxonomy = getExpWordNetTaxonomy(experimentRoot);
        
        // We get a novel test

        experiment = BenchmarkFactory.getMultipleICSimilarityMeasuresTest(
                        taxonomy, wordnet, strDatasetFullPath,
                        readCorrelationMetricField(experimentRoot),
                        icModels, measureTypes);
        
        // We set the output full filename
        
        experiment.setDefaultOutputFilename(m_strOutputDir + "/" + strOutputFilename);
        
        // We store the experiment
        
        m_Experiments.add(experiment);
    }
    
    /**
     * This function reads the WordNet reference from the XML node of the
     * experiment and gets the WordNet DB from the cache.
     * @param experimentRootNode
     * @return 
     */
    
    private IWordNetDB getExperimentWordNetDB(
            Element experimentRootNode) throws FileNotFoundException, Exception
    {
        IWordNetDB  wordnet;    // Returned value
        
        // We read the WN database filename and its directory
                
        String  strWordNetDBfilename = readStringField(experimentRootNode, "WordNetDatabaseFileName");
        String  strWordNetDBdir = readStringField(experimentRootNode, "WordNetDatabaseDirectory");
        
        String  strWNfullpath = strWordNetDBdir + "/" + strWordNetDBfilename;
        
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
            
            // We store the taxonomy
            
            m_WordNetDbTaxonomies.put(strWNfullpath, taxonomy);
        }
        
        // We return the result
        
        return (wordnet);
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
            
            // We store the taxonomy
            
            m_WordNetDbTaxonomies.put(strWNfullpath, taxonomy);
        }
        
        // We return the result
        
        return (wordnet);
    }
    
    /**
     * This function reads the WordNet reference in the XML node of the
     * experiment and gets the WordNet taxonomy from the cache.
     * @param experimentRootNode
     * @return 
     */
    
    private ITaxonomy getExpWordNetTaxonomy(
            Element experimentRootNode) throws FileNotFoundException, Exception
    {
        ITaxonomy  taxonomy;    // Returned value
        
        // We read the WN database filename and its directory
                
        String  strWordNetDBfilename = readStringField(experimentRootNode, "WordNetDatabaseFileName");
        String  strWordNetDBdir = readStringField(experimentRootNode, "WordNetDatabaseDirectory");
        
        String  strWNfullpath = strWordNetDBdir + "/" + strWordNetDBfilename;
        
        // We check the existence of the WordNet DB in the cache of WN versions
        
        if (m_WordNetDbTaxonomies.containsKey(strWNfullpath))
        {
            taxonomy = m_WordNetDbTaxonomies.get(strWNfullpath);
        }
        else
        {
            // If there is no a taxonomy associated to the WordNet DB
            // is because of the WN DB has not been loaded. Thus, we force
            // here the loading of the WN DB.
            
            getExperimentWordNetDB(experimentRootNode);
            
            // We get the taxonomy associated to the WN database
            
            taxonomy = m_WordNetDbTaxonomies.get(strWNfullpath);
        }
        
        // We return the result
        
        return (taxonomy);
    }

    /**
     * This function reads the WordNet reference in the XML node of the
     * experiment and gets the WordNet taxonomy from the cache.
     * @param strWNfullpath
     * @return 
     */
    
    private ITaxonomy getExpWordNetTaxonomy(
            String  strWNfullpath) throws FileNotFoundException, Exception
    {
        ITaxonomy  taxonomy;    // Returned value
        
        // We check the existence of the WordNet DB in the cache of WN versions
        
        if (m_WordNetDbTaxonomies.containsKey(strWNfullpath))
        {
            taxonomy = m_WordNetDbTaxonomies.get(strWNfullpath);
        }
        else
        {
            // If there is no a taxonomy associated to the WordNet DB
            // is because of the WN DB has not been loaded. Thus, we force
            // here the loading of the WN DB.
            
            getExpWordNetDB(strWNfullpath);
            
            // We get the taxonomy associated to the WN database
            
            taxonomy = m_WordNetDbTaxonomies.get(strWNfullpath);
        }
        
        // We return the result
        
        return (taxonomy);
    }
    
    /**
     * This function returns the correlation metric programmed as output
     * of the experiment.
     * @param experimentRoot
     * @return 
     */
    
    private CorrelationOutputMetrics readCorrelationMetricField(
            Element experimentRoot)
    {
        CorrelationOutputMetrics    metric = CorrelationOutputMetrics.Pearson;
        
        // We read the field
        
        String  strMetric = readStringField(experimentRoot, "CorrelationOutputMetrics");
        
        // We look for the matching value
        
        for (CorrelationOutputMetrics outputMetric: CorrelationOutputMetrics.values())
        {
            if (outputMetric.toString().equals(strMetric))
            {
                metric = outputMetric;
                break;
            }
        }
        
        // We return the result

        return (metric);
    }
    
    /**
     * This function reads the collection of similarity measures types
     * @param experimentRoot
     * @return 
     */
    
    private SimilarityMeasureType[] readSimilarityMeasureList(
            Element experimentRoot)
    {
        SimilarityMeasureType[] measures;   // Returned value
        
        ArrayList<SimilarityMeasureType>    temp;   // Temporary list
        
        Element measuresNode = getFirstChildWithTagName(experimentRoot, "SimilarityMeasures");
        
        // We create the temporary list
        
        temp = new ArrayList<>();
        
        // We recover all the measure types in the node list
        
        NodeList    measureList = measuresNode.getChildNodes();
        
        for (int i = 0, nMeasures = measureList.getLength(); i < nMeasures; i++)
        {
            if (measureList.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                // We get the current measure type element
                
                Element measureTypeNode = (Element) measureList.item(i);
                
                // We read the measure type
                
                String  strMeasureType = measureTypeNode.getFirstChild().getNodeValue();
                
                // We obtain the corresponding Enumeration type
                
                temp.add(ConvertToSimilarityMeasureType(strMeasureType));
            }
        }
        
        // We copy the measure types to the returned array
        
        measures = new SimilarityMeasureType[temp.size()];
        temp.toArray(measures);
        temp.clear();
        
        // We return the result
        
        return (measures);
    }
    
    /**
     * This function reads the collection of similarity measures types
     * @param experimentRoot
     * @return 
     */
    
    private IntrinsicICModelType[] readIntrinsicICModelTypeList(
            Element experimentRoot)
    {
        IntrinsicICModelType[] icModels;   // Returned value
        
        ArrayList<IntrinsicICModelType>    temp;   // Temporary list
        
        Element icModelsNode = getFirstChildWithTagName(experimentRoot, "ICmodels");
        
        // We create the temporary list
        
        temp = new ArrayList<>();
        
        // We recover all the measure types in the node list
        
        NodeList    icModelsNodeList = icModelsNode.getChildNodes();
        
        for (int i = 0, nICmodels = icModelsNodeList.getLength(); i < nICmodels; i++)
        {
            if (icModelsNodeList.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                // We get the current measure type element
                
                Element icModelTypeNode = (Element) icModelsNodeList.item(i);
                
                // We read the measure type
                
                String  strICmodelType = icModelTypeNode.getFirstChild().getNodeValue();
                
                // We obtain the corresponding Enumeration type
                
                temp.add(ConvertToIntrinsicICmodelType(strICmodelType));
            }
        }
        
        // We copy the measure types to the returned array
        
        icModels = new IntrinsicICModelType[temp.size()];
        temp.toArray(icModels);
        temp.clear();
        
        // We return the result
        
        return (icModels);
    }
    
    /**
     * This function reads the collection of similarity measures types
     * @param experimentRoot
     * @return 
     */
    
    private CorpusBasedICModelType[] readCorpusBasedICModelTypeList(
            Element experimentRoot)
    {
        CorpusBasedICModelType[] icModels;   // Returned value
        
        ArrayList<CorpusBasedICModelType>    temp;   // Temporary list
        
        Element icModelsNode = getFirstChildWithTagName(experimentRoot, "ICmodels");
        
        // We create the temporary list
        
        temp = new ArrayList<>();
        
        // We recover all the measure types in the node list
        
        NodeList    icModelsNodeList = icModelsNode.getChildNodes();
        
        for (int i = 0, nICmodels = icModelsNodeList.getLength(); i < nICmodels; i++)
        {
            if (icModelsNodeList.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                // We get the current measure type element
                
                Element icModelTypeNode = (Element) icModelsNodeList.item(i);
                
                // We read the measure type
                
                String  strICmodelType = icModelTypeNode.getFirstChild().getNodeValue();
                
                // We obtain the corresponding Enumeration type
                
                temp.add(ConvertToCorpusBasedICModelType(strICmodelType));
            }
        }
        
        // We copy the measure types to the returned array
        
        icModels = new CorpusBasedICModelType[temp.size()];
        temp.toArray(icModels);
        temp.clear();
        
        // We return the result
        
        return (icModels);
    }
    
    /**
     * This function converts the input string into a SimilarityMeasureType value.
     * @param strMeasureType
     * @return 
     */
    
    private SimilarityMeasureType ConvertToSimilarityMeasureType(
            String  strMeasureType)
    {
        SimilarityMeasureType   measure = SimilarityMeasureType.CosineLin;
        
        // We look for the matching value
        
        for (SimilarityMeasureType measureType: SimilarityMeasureType.values())
        {
            if (measureType.toString().equals(strMeasureType))
            {
                measure = measureType;
                break;
            }
        }
        
        // We return the result
        
        return (measure);
    }

    /**
     * This function converts the input string into a IntrinsicICModelType value.
     * @param strICmodelType
     * @return 
     */
    
    private IntrinsicICModelType ConvertToIntrinsicICmodelType(
            String  strICmodelType)
    {
        IntrinsicICModelType   icModel = IntrinsicICModelType.Seco;
        
        // We look for the matching value
        
        for (IntrinsicICModelType icModelType: IntrinsicICModelType.values())
        {
            if (icModelType.toString().equals(strICmodelType))
            {
                icModel = icModelType;
                break;
            }
        }
        
        // We return the result
        
        return (icModel);
    }
    
    /**
     * This function converts the input string into a CorpusBasedICModelType value.
     * @param strICmodelType
     * @return 
     */
    
    private CorpusBasedICModelType ConvertToCorpusBasedICModelType(
            String  strICmodelType)
    {
        CorpusBasedICModelType   icModel = CorpusBasedICModelType.Resnik;
        
        // We look for the matching value
        
        for (CorpusBasedICModelType icModelType: CorpusBasedICModelType.values())
        {
            if (icModelType.toString().equals(strICmodelType))
            {
                icModel = icModelType;
                break;
            }
        }
        
        // We return the result
        
        return (icModel);
    }
    
    /**
     * This function recovers an experiment from its Xml definition.
     * @param experimentRoot 
     */
    
    private void readMultipleICmodelsExperiment(
            Element experimentRoot) throws FileNotFoundException, Exception
    {
        ISimilarityBenchmark    experiment; // Loaded experiment
        
        IntrinsicICModelType[]  icModels;       // Intrinsic IC models
        SimilarityMeasureType[] measureTypes;   // Measures
        
        // We read the dataset filename and path
        
        String  strDatasetFilename = readStringField(experimentRoot, "DatasetFileName");
        String  strDatasetDir = readStringField(experimentRoot, "DatasetDirectory");
        String  strDatasetFullPath = strDatasetDir + "/" + strDatasetFilename;
        String  strOutputFilename = readStringField(experimentRoot, "OutputFileName");
        
        // We check the existence of the dataset
        
        File    datasetFile = new File(strDatasetFullPath);
        
        if (!datasetFile.exists())
        {
            throw (new FileNotFoundException(strDatasetFullPath));
        }

        // We read the IC models and similarity measures
        
        icModels = readIntrinsicICModelTypeList(experimentRoot);
        measureTypes = readSimilarityMeasureList(experimentRoot);
        
        // We load the WN database and its taxonomy from the cache
        
        IWordNetDB  wordnet = getExperimentWordNetDB(experimentRoot);
        ITaxonomy   taxonomy = getExpWordNetTaxonomy(experimentRoot);
        
        // We get a novel test

        experiment = BenchmarkFactory.getMultipleICSimilarityMeasuresTest(
                        taxonomy, wordnet, strDatasetFullPath,
                        readCorrelationMetricField(experimentRoot),
                        icModels, measureTypes);
        
        // We set the output full filename
        
        experiment.setDefaultOutputFilename(m_strOutputDir + "/" + strOutputFilename);
        
        // We store the experiment
        
        m_Experiments.add(experiment);
    }

    /**
     * This function recovers an experiment from its Xml definition.
     * @param experimentRoot 
     */
    
    private void readNonICbasedSimMeasuresExperiment(
            Element experimentRoot) throws Exception, FileNotFoundException
    {
        ISimilarityBenchmark    experiment; // Loaded experiment
        
        SimilarityMeasureType[] measureTypes;   // 
        
        // We read the dataset filename and path, as well as the output filename
        
        String  strDatasetFilename = readStringField(experimentRoot, "DatasetFileName");
        String  strDatasetDir = readStringField(experimentRoot, "DatasetDirectory");
        String  strDatasetFullPath = strDatasetDir + "/" + strDatasetFilename;
        String  strOutputFilename = readStringField(experimentRoot, "OutputFileName");
        
        // We check the existence of the dataset
        
        File    datasetFile = new File(strDatasetFullPath);
        
        if (!datasetFile.exists())
        {
            throw (new FileNotFoundException(strDatasetFullPath));
        }

        // We read the input list of similarity measures
        
        measureTypes = readSimilarityMeasureList(experimentRoot);
        
        // We load the WN database and its taxonomy from the cache
        
        IWordNetDB  wordnet = getExperimentWordNetDB(experimentRoot);
        ITaxonomy   taxonomy = getExpWordNetTaxonomy(experimentRoot);
        
        // We get a novel test

        experiment = BenchmarkFactory.getMultipleNonICBasedMeasureTest(
                        taxonomy, wordnet, strDatasetFullPath,
                        readCorrelationMetricField(experimentRoot),
                        measureTypes);
        
        // We set the output full filename
        
        experiment.setDefaultOutputFilename(m_strOutputDir + "/" + strOutputFilename);
        
        // We store the experiment
        
        m_Experiments.add(experiment);        
    }

    /**
     * This function recovers an experiment from its Xml definition.
     * @param experimentRoot 
     */
    
    private void readCorpusICmodelsExperiment(
            Element experimentRoot) throws FileNotFoundException, Exception
    {
        ISimilarityBenchmark    experiment; // Loaded experiment
        
        CorpusBasedICModelType[]    corpusICmodels; // Corpus-based IC models
        SimilarityMeasureType[]     measureTypes;   // Measures
        
        // We read the dataset filename and path
        
        String strDatasetFilename = readStringField(experimentRoot, "DatasetFileName");
        String strDatasetDir = readStringField(experimentRoot, "DatasetDirectory");
        String strDatasetFullPath = strDatasetDir + "/" + strDatasetFilename;
        String strOutputFilename = readStringField(experimentRoot, "OutputFileName");
        
        // We read the frequency files and directory
        
        String strFrequencyFilesDir = readStringField(experimentRoot, "ConceptFrequencyFilesDir");
        
        String[] strICPedersenModels = readStringFields(experimentRoot, "ConceptFrequencyFiles");
                
        // We check the existence of the dataset
        
        File    datasetFile = new File(strDatasetFullPath);
        
        if (!datasetFile.exists())
        {
            throw (new FileNotFoundException(strDatasetFullPath));
        }

        // We read the IC models and similarity measures
        
        corpusICmodels = readCorpusBasedICModelTypeList(experimentRoot);
        measureTypes = readSimilarityMeasureList(experimentRoot);
        
        // We load the WN database and its taxonomy from the cache
        
        IWordNetDB  wordnet = getExperimentWordNetDB(experimentRoot);
        ITaxonomy   taxonomy = getExpWordNetTaxonomy(experimentRoot);
        
        // We get a novel test

        experiment = BenchmarkFactory.getCorpusICSimilarityMeasuresTest(
                        taxonomy, wordnet, strDatasetFullPath,
                        readCorrelationMetricField(experimentRoot),
                        strFrequencyFilesDir, corpusICmodels, strICPedersenModels,
                        measureTypes);
        
        // We set the output full filename
        
        experiment.setDefaultOutputFilename(m_strOutputDir + "/" + strOutputFilename);
        
        // We store the experiment
        
        m_Experiments.add(experiment);
    }

    /**
     * This function recovers an experiment from its XML definition.
     * @param experimentRoot 
     */
    
    private void readMixedICmodelsExperiment(
            Element experimentRoot) throws FileNotFoundException, Exception
    {
        ISimilarityBenchmark    experiment; // Loaded experiment
        
        ITaxonomyInfoConfigurator[]    icModels;        // All IC models
        SimilarityMeasureType[]         measureTypes;   // Measures
        
        // We read the dataset filename and path
        
        String strDatasetFilename = readStringField(experimentRoot, "DatasetFileName");
        String strDatasetDir = readStringField(experimentRoot, "DatasetDirectory");
        String strDatasetFullPath = strDatasetDir + "/" + strDatasetFilename;
        String strOutputFilename = readStringField(experimentRoot, "OutputFileName");
        
        // We read the frequency files and directory
        
        String strFrequencyFilesDir = readStringField(experimentRoot, "ConceptFrequencyFilesDir");
        
        // We check the existence of the dataset
        
        File    datasetFile = new File(strDatasetFullPath);
        
        if (!datasetFile.exists())
        {
            throw (new FileNotFoundException(strDatasetFullPath));
        }

        // We read the IC models and similarity measures
        
        icModels = readMixedICmodels(
                    getFirstChildWithTagName(experimentRoot, "ICmodels"),
                    strFrequencyFilesDir);
        
        measureTypes = readSimilarityMeasureList(experimentRoot);
        
        // We load the WN database and its taxonomy from the cache
        
        IWordNetDB  wordnet = getExperimentWordNetDB(experimentRoot);
        ITaxonomy   taxonomy = getExpWordNetTaxonomy(experimentRoot);
        
        // We get a novel test

        experiment = BenchmarkFactory.getMixedICModelsSimilarityTest(
                        taxonomy, wordnet, strDatasetFullPath,
                        readCorrelationMetricField(experimentRoot),
                        icModels, measureTypes);
        
        // We set the output full filename
        
        experiment.setDefaultOutputFilename(m_strOutputDir + "/" + strOutputFilename);
        
        // We store the experiment
        
        m_Experiments.add(experiment);
    }
    
    /**
     * This function reads a collection of IC models of any type.
     * @param mixedICModelList
     * @return 
     */
    
    private ITaxonomyInfoConfigurator[] readMixedICmodels(
        Element mixedICModelList,
        String  strFrequencyFilesDir) throws Exception
    {
        ITaxonomyInfoConfigurator[] outputICmodels; // Returned IC models
        
        ArrayList<ITaxonomyInfoConfigurator>    parsingList;    // Auxiliary list
        
        NodeList    icModelNodes;   // IC models
        
        ITaxonomyInfoConfigurator   icModel;    // Recovered IC model

        // Wecreate the temporary list for parsing
        
        parsingList = new ArrayList<>();
        
        // We get the nodes of the root 'ICmodels' element
        
        icModelNodes = mixedICModelList.getChildNodes();
        
        // We parse the nodes
        
        for (int i = 0, nChild = icModelNodes.getLength();
                i < nChild;
                i++)
        {
            // We get the current i-esim node
            
            Node    child = icModelNodes.item(i);
            
            // We filter the Xmlelements
            
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                Element icModelNode = (Element)child;
                
                switch (icModelNode.getTagName())
                {
                    case "IntrinsicICModel":
                        
                        IntrinsicICModelType    intrinsicType;
                        
                        // We read the intrinsic IC model type in the Xml node
                        
                        String  strICmodelType = icModelNode.getFirstChild().getNodeValue();
                                
                        intrinsicType = ConvertToIntrinsicICmodelType(strICmodelType);
                        
                        // We build and store a new intrinsic IC model
                        
                        icModel = ICModelsFactory.getIntrinsicICmodel(intrinsicType);
                        
                        parsingList.add(icModel);
                        
                        break;
                        
                    case "CorpusBasedICModel":
                        
                        CorpusBasedICModelType    corpusICmodelType;
                        
                        // We read the intrinsic IC model type in the Xml node
                        
                        String  strCorpusICmodelType = readStringField(icModelNode, "Method");
                        String  strFrequencyFile = readStringField(icModelNode, "ConceptFrequencyFilename");
                                
                        corpusICmodelType = ConvertToCorpusBasedICModelType(strCorpusICmodelType);
                        
                        // We build and store a new intrinsic IC model
                        
                        icModel = ICModelsFactory.getCorpusICmodel(corpusICmodelType,
                                    strFrequencyFilesDir + "/" + strFrequencyFile);
                        
                        parsingList.add(icModel);
                        
                        break;
                }
            }
        }
        
        // We copy the IC models to the output vector
        
        outputICmodels = new ITaxonomyInfoConfigurator[parsingList.size()];
        
        parsingList.toArray(outputICmodels);
        
        // We return the IC models
        
        return (outputICmodels);
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
     * This function reads a vector of string fields defined as children of
     * the parent node.
     * @param parent
     * @param strFieldCollectionName
     * @return 
     */
    
    private String[] readStringFields(
            Element parent,
            String  strFieldCollectionName)
    {
        String[]  strTexts;    // Returned value
        
        Element childCollectionNode;  // Child node
        
        ArrayList<String>   temp;   // Auxiliary list
        
        // We get the child node matching the input name

        childCollectionNode = getFirstChildWithTagName(parent, strFieldCollectionName);
        
        if (childCollectionNode == null)
        {
            throw (new IllegalArgumentException(strFieldCollectionName));
        }
        
        // We get the text field
        
        NodeList    children = childCollectionNode.getChildNodes();
        
        // We create the temporary list because of the children list could
        // contain non element nodes.  Thus, we must filter these nodes.
        
        temp = new ArrayList<>(children.getLength());
        
        // We traverse the children collection
        
        for (int i = 0, nChild = children.getLength(); i < nChild; i++)
        {
            // We get the next child node
            
            Node    childNode = children.item(i);
            
            // We check that it is a Xml element
            
            if (childNode.getNodeType() == Node.ELEMENT_NODE)
            {
                temp.add(childNode.getFirstChild().getNodeValue());
            }
        }
        
        // We build the output vector
        
        strTexts = new String[temp.size()];

        temp.toArray(strTexts);
        temp.clear();
        
        // We return the result
        
        return (strTexts);
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
            benchmark.executeTests(benchmark.getDefaultOutputFilename(), false);
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
