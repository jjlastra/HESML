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
import hesml.measures.SimilarityMeasureType;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import org.w3c.dom.Element;

/**
 * This class implements the reader of 'MixedICmodelsExperiment' experiments
 * declared into a Xml file. The class reads the 'NonICbasedSimMeasuresExperiment'
 * XML node and instances a benchmark.
 * @author j.lastra
 */

class NonICbasedSimMeasuresExperimentReader extends XmlBenchmarkReader
{
    /**
     * Constructor.
     * @param wordnetDbVersions Databases in different WordNet versions
     * @param wordnetDbTaxonomies Taxonomies of different WordNet version
     */

    NonICbasedSimMeasuresExperimentReader(
            HashMap<String, IWordNetDB> wordnetDbVersions,
            HashMap<String, ITaxonomy> wordnetDbTaxonomies)
    {
        super(wordnetDbVersions, wordnetDbTaxonomies);
    }
    
    /**
     * This function loads the benchmark detailed into a Xml node.
     * @param experimentRootNode
     * @return 
     */
    
    @Override
    ISimilarityBenchmark readBenchmark(
        Element experimentRoot)  throws FileNotFoundException, Exception
    {
        // We read the dataset filename and path, as well as the output filename
        
        String  strDatasetFilename = readStringField(experimentRoot, "DatasetFileName");
        String  strDatasetDir = readStringField(experimentRoot, "DatasetDirectory");
        String  strDatasetFullPath = strDatasetDir + "/" + strDatasetFilename;
        
        // We check the existence of the dataset
        
        File datasetFile = new File(strDatasetFullPath);
        
        if (!datasetFile.exists())
        {
            throw (new FileNotFoundException(strDatasetFullPath));
        }

        // We read the input list of similarity measures
        
        SimilarityMeasureType[] measureTypes = readSimilarityMeasureList(experimentRoot);
        
        // We load the WN database and its taxonomy from the cache
        
        IWordNetDB  wordnet = getExperimentWordNetDB(experimentRoot);
        ITaxonomy   taxonomy = getExpWordNetTaxonomy(experimentRoot);
        
        // We get a novel test

        ISimilarityBenchmark experiment = BenchmarkFactory.getMultipleNonICBasedMeasureTest(
                                            taxonomy, wordnet, strDatasetFullPath,
                                            readCorrelationMetricField(experimentRoot),
                                            measureTypes);

        // We return the result
        
        return (experiment);
    }
}
