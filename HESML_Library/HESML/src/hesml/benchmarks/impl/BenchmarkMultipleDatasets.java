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

package hesml.benchmarks.impl;

import hesml.benchmarks.CorrelationOutputMetrics;
import static hesml.benchmarks.impl.AbstractBenchmark.saveCSVfile;
import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * This class implements a set of benchmarks of specific similarity measures (rows)
 * with different datasets and WordNet versions (columns).
 * Each row of the resulting experimental matrix is defined by a specific
 * similarity measure which is defined as any non IC-based similarity measure,
 * or a combination of any IC-based similarity measure with a specific intrinsic
 * or corpus-based IC model. On the other hand, each column of the experimental
 * matrix is defined by the evaluation of each similarity measure on a specific
 * WordNet version and dataset.
 * @author j.lastra
 */

public class BenchmarkMultipleDatasets extends WordNetSimBenchmark
{
    /**
     * WordNet versions to be evaluated in the experiments
     */
    
    private IWordNetDB[]    m_WordNetDbVersions;
    
    /**
     * Taxonomies associated to each WordNet version.
     */
    
    private ITaxonomy[] m_Taxonomies;
    
    /**
     * Word similarity benchmarks.
     */
    
    protected ArrayList<WordPairSimilarity[]> m_WordPairBenchmarks;
    
    /**
     * Collection of IC models to be evaluated
     */
    
    private ITaxonomyInfoConfigurator[]    m_ICmodels;
    
    /**
     * Measure types evaluated
     */
    
    private final SimilarityMeasureType[]   m_MeasureTypes;
    
    /**
     * Names  of he word similarity benchmarks
     */
    
    private String[]  m_strDatasetFiles;
    
    /**
     * Constructor
     */
    
    BenchmarkMultipleDatasets(
            IWordNetDB[]                wordnetVersions,
            ITaxonomy[]                 taxonomies,
            CorrelationOutputMetrics    metrics,
            String[]                    strWordPairsFiles,
            ITaxonomyInfoConfigurator[] icModels,
            SimilarityMeasureType[]     measureTypes) throws Exception
    {   
        // We initialize the base class
        
        super(wordnetVersions[0], taxonomies[0], metrics, strWordPairsFiles[0]);
        
        // We check the validity of the input parameters. We expect one
        // taxonomy per WordNet Db version, as well as one IC model per
        // similarity measure. The IC models for non IC-based similarity
        // measures are defiend to null.
        
        if ((wordnetVersions.length != taxonomies.length)
                || (measureTypes.length != icModels.length))
        {
            throw (new InvalidParameterException());
        }
        
        // We store the data to carry-out the benchmark
        
        m_WordNetDbVersions = wordnetVersions;
        m_Taxonomies = taxonomies;
        m_MeasureTypes = measureTypes;
        m_ICmodels = icModels;
        
        // We load all the datasets
        
        m_WordPairBenchmarks = new ArrayList<>();
        
        // We only load the dataset starting at the second position because
        // the first dataset is already loaded by the base class in
        // the m_WordPairs attribute.
        
        m_WordPairBenchmarks.add(m_WordPairs);
        
        for (int i = 1; i < strWordPairsFiles.length; i++)
        {
            m_WordPairBenchmarks.add(loadWordPairsFile(strWordPairsFiles[i]));
        }
        
        // We recover the names of the dataset files
        
        registerDatasetNames(strWordPairsFiles);
    }
    
    @Override
    public void clear()
    {
        // We release the resoruces of the base class
        
        super.clear();
        
        // We release the resoruces used by the class
        
        for (IWordNetDB wordnet: m_WordNetDbVersions)
        {
            wordnet.clear();
        }
        
        for (ITaxonomy taxonomy: m_Taxonomies)
        {
            taxonomy.clear();
        }
        
        m_WordPairBenchmarks.clear();
    }

    /**
     * This function recovers the names of the dataset files. This information
     * is used to define the titles of the columns in the output file.
     * @param strWordPairsFiles 
     */
    
    private void registerDatasetNames(
        String[]    strWordPairsFiles)
    {
        File    fileInfo;   // File information
        
        // We create the vector for the filenames
        
        m_strDatasetFiles = new String[strWordPairsFiles.length];
        
        // We recover the filename of each dataset.
        
        for (int iPair = 0; iPair < strWordPairsFiles.length; iPair++)
        {
            // We get the information of the word similarity benchmark file
            
            fileInfo = new File(strWordPairsFiles[iPair]);
            
            // We save the dataset filename
            
            m_strDatasetFiles[iPair] = fileInfo.getName();
        }
    }
    
    /**
     * This function runs the experiments and saves the results.
     * Each specific similarity measure defines a row in the output matrix,
     * and it is defined by the combination of any similarity measure with
     * any IC model. The IC models can be null when the measure is a non
     * IC-based similarity measure.
     * @param strMatrixResultsFile 
     * @param showDebugInfo 
     * @throws java.lang.Exception 
     */
    
    @Override
    public void executeTests(
            String  strMatrixResultsFile,
            boolean showDebugInfo) throws Exception
    {
        String[][]  strOutputMatrix;  // Output matrix
        
        double[]  pearsonSpearman;    // Correlation value

        int step = getMetricFillingStep();  // Filling step
        
        // User message reporting the output file to be computed
        
        System.out.println("/**");
        System.out.println(" * Benchmark: similarity measures vs (Dataset and WordNet version)");
        System.out.println(" * Computing the output file: " + strMatrixResultsFile);
        System.out.println(" **/");
        
        // We get the formatted output matrix for the current experiment.
        
        strOutputMatrix = getOurOutputMatrix();
        
        // We iterate over the similarity measures

        for (int iMeasure = 0; iMeasure < m_MeasureTypes.length; iMeasure++)
        {
            // We iterate over the WordNet versions and datasets
            
            for (int iWordNet = 0, iColumn = 2;
                    iWordNet < m_WordNetDbVersions.length;
                    iWordNet++)
            {
                // We assign the base taxonomy used in this iteration
                
                m_Taxonomy = m_Taxonomies[iWordNet];
                m_Wordnet = m_WordNetDbVersions[iWordNet];
                               
                // We initialize the running message
                
                String  strDebugMsg = "Evaluating " + m_MeasureTypes[iMeasure].toString();

                // We set the IC model associated to the similarity measure.
                // The IC model sets the IC values of the current taxonomy
                // associated to the current WordNet version. because of the
                // IC models depends on the underlying taxonomy, they must
                // be update for each Wordnet version.
                
                if (m_ICmodels[iMeasure] != null)
                {
                    m_ICmodels[iMeasure].setTaxonomyData(m_Taxonomy);
                    
                    strDebugMsg += " + " + m_ICmodels[iMeasure].toString()
                            + " IC model on " + m_WordNetDbVersions[iWordNet].getVersion();
                }
                
                // The measure needs to be created every time in this place,
                // in order to allow the upgrade of the internal parameters
                // depending of the base taxonomy. It is necessary to use
                // the version of MeasureFactory.getMeasure() that accepts
                // WordNet as input paprameter, because of the TaiebSim2
                // only can be created through this function.

                m_Measure = MeasureFactory.getMeasure(m_Wordnet, m_Taxonomy,
                                m_MeasureTypes[iMeasure]);
                
                // We evaluate the measure on each dataset
                
                for (int iDataset = 0;
                        iDataset < m_WordPairBenchmarks.size();
                        iDataset++, iColumn += step)
                {
                    System.out.println(strDebugMsg);
                    
                    // We set the dataset to be evaluated
                    
                    m_WordPairs = m_WordPairBenchmarks.get(iDataset);
                    
                    // We reset the ranking used for computing the Spearman metric
                    
                    m_WordPairsRanking.clear();
                    m_WordPairsRanking =  getSpearmanRanking(m_WordPairs);

                    // We evaluate the measure
                    
                    pearsonSpearman = evalPearsonSpearmanMeasure(showDebugInfo);

                    // We save the result in the output matrix

                    setOutputMetrics(strOutputMatrix, iMeasure + 2, iColumn,
                        pearsonSpearman[0], pearsonSpearman[1]);
                }
            }
        }
        
        // We save the file in CSV format
        
        saveCSVfile(strOutputMatrix, strMatrixResultsFile);
    }
    
    /**
     * This function creates the experiment matrix to record the results
     * for multiple measures versus multiple IC-based measures. The number
     * of columns depends on the number of output metrics specified by the
     * client code.
     * @return Blank experiment matrix
     */
    
    private String[][] getOurOutputMatrix()
    {
        String[][]  strMatrix; // Returne value
        
        int step = getMetricFillingStep();  // Columns for the metrics
        
        // We create the output matrix. The matrix will have one row per measure
        // and one column per (WN version, dataset) pair. However, we add two rows
        // in order to generate an output matrix with the format below.
        //                        | WN v1     | WN v1     | WN v2  ...
        // Measures | IC model    | Dataset 1 | Dataset 2 | Dataset 1 ...
        // ----------------------------------------------------------
        // Resnik   | Seco        | 0.8789    | 0.8789    | 0.8456
        
        strMatrix = new String[2 + m_MeasureTypes.length]
                    [2 + step * m_WordPairBenchmarks.size() * m_WordNetDbVersions.length];
        
        // We fill the titles of the first two columns
        
        strMatrix[0][0] = "";
        strMatrix[0][1] = "";
        strMatrix[1][0] = "Measure";
        strMatrix[1][1] = "IC model";
        
        // We fill the titles of the remaining columns
        
        for (int iWordNet = 0, iColumn = 2;
                iWordNet < m_WordNetDbVersions.length;
                iWordNet++)
        {
            for (int iDataset = 0;
                    iDataset < m_WordPairBenchmarks.size();
                    iDataset++, iColumn += step)
            {
                // We define the titles in second row according to the output metrics

                switch (m_OutputMetrics)
                {
                    case Pearson:

                        strMatrix[0][iColumn] = m_WordNetDbVersions[iWordNet].getVersion();
                        strMatrix[1][iColumn] = m_strDatasetFiles[iDataset] + "-Pearson";

                        break;

                    case Spearman:

                        strMatrix[0][iColumn] = m_WordNetDbVersions[iWordNet].getVersion();
                        strMatrix[1][iColumn] = m_strDatasetFiles[iDataset] + "-Spearman";

                        break;

                    case PearsonAndSpearman:

                        strMatrix[0][iColumn] = m_WordNetDbVersions[iWordNet].getVersion();
                        strMatrix[0][iColumn + 1] = m_WordNetDbVersions[iWordNet].getVersion();
                        
                        strMatrix[1][iColumn] = m_strDatasetFiles[iDataset] + "-Pearson";
                        strMatrix[1][iColumn + 1] = m_strDatasetFiles[iDataset] + "-Spearman";

                        break;
                }
            }
        }

        // We fill the titles for the rows
        
        for (int iMeasure = 0; iMeasure < m_MeasureTypes.length; iMeasure++)
        {
            strMatrix[iMeasure + 2][0] = m_MeasureTypes[iMeasure].toString();
            
            // Second column shows the name of the IC model
            
            if (m_ICmodels[iMeasure] != null)
            {
                strMatrix[iMeasure + 2][1] = m_ICmodels[iMeasure].toString();
            }
            else
            {
                strMatrix[iMeasure + 2][1] = "";
            }
        }
        
        // We return the result
        
        return (strMatrix);
    }   
}
