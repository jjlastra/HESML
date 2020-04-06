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
 * This class implements the reader of 'MultipleDatasetsExperiment' experiments
 * declared into a Xml file. The class reads the 'MultipleDatasetsExperiment'
 * XML node and instances a benchmark.
 * @author j.lastra
 */

class MultipleDatasetsExperimentReader extends XmlBenchmarkReader
{
    /**
     * Constructor.
     * @param wordnetDbVersions Databases in different WordNet versions
     * @param wordnetDbTaxonomies Taxonomies of different WordNet version
     */

    MultipleDatasetsExperimentReader(
            HashMap<String, IWordNetDB> wordnetDbVersions,
            HashMap<String, ITaxonomy> wordnetDbTaxonomies)
    {
        super(wordnetDbVersions, wordnetDbTaxonomies);
    }
    
    /**
     * This function loads the benchmark detailed into a Xml node called
     * 'MultipleDatasetsExperiment'.
     * @param experimentRootNode
     * @return 
     */
    
    @Override
    ISimilarityBenchmark readBenchmark(
        Element experimentRoot)  throws FileNotFoundException, Exception
    {
        // We read the dataset filename and path
        
        String  strDatasetDir = readStringField(experimentRoot, "DatasetDirectory");
        String  strConceptFrqFileDir = readStringField(experimentRoot, "ConceptFrequencyFilesDir");
        String  strWNdbFilename = readStringField(experimentRoot, "WordNetDatabaseFileName");
        
        String[]    strWordNetDirectories = readStringFields(experimentRoot, "WordNetVersions");
        String[]    strDatasetFileNames = readStringFields(experimentRoot, "WNDatasets");
        
        // We build the fullnames of the dataset files
        
        for (int i = 0; i < strDatasetFileNames.length; i++)
        {
            strDatasetFileNames[i] = strDatasetDir + "/" + strDatasetFileNames[i];
        }
        
        // We read the specific similarity measures
        
        ArrayList<SimilarityMeasureType> measureTypeList = new ArrayList<>();
        ArrayList<ITaxonomyInfoConfigurator> icModelsList = new ArrayList<>();
        
        Element xmlSimMeasures = getFirstChildWithTagName(experimentRoot, "SimilarityMeasures");
        
        NodeList xmlSpecificMeasures = xmlSimMeasures.getChildNodes();
        
        for (int i = 0, nChild = xmlSpecificMeasures.getLength();
                i < nChild;
                i++)
        {
            // We get the current i-esim node
            
            Node child = xmlSpecificMeasures.item(i);
            
            // We filter the Xmlelements
            
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                readSpecificSimilarityMeasure(strConceptFrqFileDir,
                       (Element) child, measureTypeList, icModelsList);
            }
        }
        
        // We copy the IC models and measures to the arrays and clear them.
        
        ITaxonomyInfoConfigurator[] icModels = new ITaxonomyInfoConfigurator[icModelsList.size()];
        icModelsList.toArray(icModels);
        icModelsList.clear();
        
        SimilarityMeasureType[] measureTypes = new SimilarityMeasureType[measureTypeList.size()];
        measureTypeList.toArray(measureTypes);
        measureTypeList.clear();
    
        // We create the array to store the WordNet versions and their taxonomies
        
        IWordNetDB[] wordnetVersions = new IWordNetDB[strWordNetDirectories.length];
        ITaxonomy[] taxonomies = new ITaxonomy[strWordNetDirectories.length];

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
        
        ISimilarityBenchmark experiment = BenchmarkFactory.getMultipleDatasetsBenchmark(
                                            wordnetVersions, taxonomies,
                                            readCorrelationMetricField(experimentRoot),
                                            strDatasetFileNames, icModels, measureTypes);
        
        // We return the result
        
        return (experiment);
    }
}
