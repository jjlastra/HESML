/*
 * Copyright (C) 2016-2018 Universidad Nacional de Educación a Distancia (UNED)
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
import hesml.benchmarks.impl.BenchmarkFactory;
import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.measures.SimilarityMeasureType;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class implements the XML reader of a new benchmark type created in HESML
 * V1R4 version. This new experiment type is called SingleDatasetSimilarityValuesExperiment
 * and it allows the evaluation of ontology-based semantic similarity measures
 * based on WordNet and pre-trained word embedding models into a same
 * benchmark in a single dataset (word pairs). The benchmark evaluates all methods
 * in a single dataset and produces an output matrix in CSV file format
 * with the raw similarity values.
 * XML file format of all experiments is defined in the XMLschema file
 * HESML_Library\ReproducibleExperiments\WordNetBasedExperiments.xsd
 * 
 * @author j.lastra
 */

class SingleDatasetSimilarityValuesExperimentReader extends XmlBenchmarkReader
{
    /**
     * Constructor.
     * @param wordnetDbVersions Databases in different WordNet versions
     * @param wordnetDbTaxonomies Taxonomies of different WordNet version
     */

    SingleDatasetSimilarityValuesExperimentReader(
            HashMap<String, IWordNetDB> wordnetDbVersions,
            HashMap<String, ITaxonomy> wordnetDbTaxonomies)
    {
        super(wordnetDbVersions, wordnetDbTaxonomies);
    }
    
    /**
     * This function loads the benchmark detailed into a Xml node called
     * 'SingleDatasetSimilarityValuesExperiment'.
     * @param experimentRootNode
     * @return 
     */
    
    @Override
    ISimilarityBenchmark readBenchmark(
        Element experimentRoot)  throws FileNotFoundException, Exception
    {
        // We read the dataset filename and path
        
        String strDatasetDir = readStringField(experimentRoot, "DatasetDirectory");
        String strDatasetFileName = readStringField(experimentRoot, "DatasetFileName");

        System.out.println("Loading benchmark on dataset = " + strDatasetFileName);
        
        // We assembly the filename of the dataset
        
        strDatasetFileName = strDatasetDir + "/" + strDatasetFileName;
        
        // We read the specific similarity measures
        
        ArrayList<SimilarityMeasureType> measureTypeList = new ArrayList<>();
        ArrayList<ITaxonomyInfoConfigurator> icModelsList = new ArrayList<>();
        
        // Inicializamos los objetos relacionados con WordNet
        
        IWordNetDB  wordnet = null;
        ITaxonomy   taxonomy = null;
        ITaxonomyInfoConfigurator[] icModels = {};
        SimilarityMeasureType[] measureTypes = {};
        
        // We search for the WordNet-based measures
        
        Element wordNetMeasuresRoot = getFirstChildWithTagName(experimentRoot, "WordNetMeasures");
        
        if (wordNetMeasuresRoot != null)
        {
            String strWNdbFilename = readStringField(wordNetMeasuresRoot, "WordNetDatabaseFileName");
            String strWordNetDirectory = readStringField(wordNetMeasuresRoot, "WordNetDatabaseDirectory");
        
            Element xmlSimMeasures = getFirstChildWithTagName(wordNetMeasuresRoot, "SimilarityMeasures");
        
            NodeList xmlSpecificMeasures = xmlSimMeasures.getChildNodes();
        
            for (int i = 0, nChild = xmlSpecificMeasures.getLength(); i < nChild; i++)
            {
                // We get the current i-esim node

                Node child = xmlSpecificMeasures.item(i);

                // We filter the Xmlelements

                if (child.getNodeType() == Node.ELEMENT_NODE)
                {
                    readSpecificSimilarityMeasure("", (Element) child, 
                            measureTypeList, icModelsList);
                }
            }
        
            // We copy the IC models and measures to the arrays and clear them.

            icModels = new ITaxonomyInfoConfigurator[icModelsList.size()];
            icModelsList.toArray(icModels);
            icModelsList.clear();

            measureTypes = new SimilarityMeasureType[measureTypeList.size()];
            measureTypeList.toArray(measureTypes);
            measureTypeList.clear();

            // We load the WN database and its taxonomy from the cache

            String  strWNfullpath = strWordNetDirectory + "/" + strWNdbFilename;
            
            wordnet = getExpWordNetDB(strWNfullpath);
            taxonomy = getExpWordNetTaxonomy(strWNfullpath);
        }

        // We load the (*.emb) word embedding models
    
        String[] strEmbVectorFilenames = {};
        String[] strUKBVectorFilenames = {};
        String[][] strNasariVectorFilenames = {};
        
        // We load the embeddings
        
        Element wordVectorsRoot = getFirstChildWithTagName(experimentRoot, "RawWordVectorFiles");
        
        if (wordVectorsRoot != null)
        {
            strEmbVectorFilenames = readStringFields(wordVectorsRoot, "EmbVectorFiles");
            strUKBVectorFilenames = readStringFields(wordVectorsRoot, "UKBVectorFiles");
            strNasariVectorFilenames = readNasariVectorFiles(wordVectorsRoot);
        }
        
        // We create an instance from a multiple dataset experiment
        
        ISimilarityBenchmark experiment = BenchmarkFactory.getSingleDatasetSimilarityValuesTest(
                                            taxonomy, wordnet, strDatasetFileName,
                                            icModels, measureTypes,
                                            strEmbVectorFilenames,
                                            strUKBVectorFilenames,
                                            strNasariVectorFilenames);
        
        // We return the result
        
        return (experiment);
    }
    
    /**
     * This function reads the Nasari pre-trained models
     * @param wordVectorRoot
     * @return 
     */
    
    private String[][] readNasariVectorFiles(
        Element wordVectorRoot)
    {
        // We initialzie the output
        
        String[][] strNasariModels = {};
        
        // We get the root node of the Nasari pre-trained models
        
        Element xmlNasariModels = getFirstChildWithTagName(wordVectorRoot, "NasariVectorFiles");
        
        // We get the pre-traiend models
        
        if (xmlNasariModels != null)
        {
            ArrayList<Element>   pretrainedModels = new ArrayList<>();

            NodeList xmlModels = xmlNasariModels.getChildNodes();

            for (int i = 0, nChild = xmlModels.getLength(); i < nChild; i++)
            {
                // We get the current i-esim node

                Node child = xmlModels.item(i);

                // We filter the Xmlelements

                if ((child.getNodeType() == Node.ELEMENT_NODE)
                        && (child.getNodeName() == "NasariVectorFile"))
                {
                    pretrainedModels.add((Element) child);
                }
            }

            // Creamos la matriz de pares de ficheros Nasari

            strNasariModels = new String[pretrainedModels.size()][2];

            int i = 0;

            for (Element model: pretrainedModels)
            {
                strNasariModels[i][0] = readStringField((Element) model, "SensesFile");
                strNasariModels[i][1] = readStringField((Element) model, "VectorFile");
            }

            // Destroy the modell list

            pretrainedModels.clear();
        }
        
        // We return the result
        
        return (strNasariModels);
    }
}
