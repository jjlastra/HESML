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

package hesml.benchmarks.impl;

import hesml.benchmarks.CorrelationOutputMetrics;
import static hesml.benchmarks.impl.AbstractBenchmark.saveCSVfile;
import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.measures.IWordNetWordSimilarityMeasure;
import hesml.measures.IWordSimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import java.security.InvalidParameterException;

/**
 * This class evaluates a specific word similarity benchmarks (single dataset)
 * using the noun database of a specific WordNet version on a collection
 * of similarity measures. Main output is a table with the raw similarity values
 * returned by each similarity value for each word pair. First column
 * of the output matrix contains the word pairs, whilst subsequent columns
 * contain the raw similarity values returned by each measure.
 * @author j.lastra
 */

public class BenchmarkSingleDatasetSimilarityValues extends WordNetSimBenchmark
{
    /**
     * Collection of IC models to be evaluated
     */
    
    private ITaxonomyInfoConfigurator[]    m_ICmodels;
    
    /**
     * Measure types evaluated
     */
    
    private final SimilarityMeasureType[]   m_MeasureTypes;
    
    /**
     * Filenames of the pre-trained embedding models in EMB file format
     */
    
    private final String[]  m_strRawEmbeddingFilenames;
    
    /**
     * Filenames of the pre.trained embedding models in Nasari file format
     */
    
    private final String[] m_strNasariModelFilenames;
    
    /**
     * Filenames of the pre-trained emebdding files in UKB file format
     */
    
    private final String[]  m_strUKBModelFilenames;
    
    /**
     * Constructor
     */
    
    BenchmarkSingleDatasetSimilarityValues(
            IWordNetDB                  wordnetDb,
            ITaxonomy                   taxonomy,
            String                      strWordPairsFile,
            ITaxonomyInfoConfigurator[] icModels,
            SimilarityMeasureType[]     measureTypes,
            String[]                    strEmbModelFilenames,
            String[]                    strNasariModelFilenames,
            String[]                    strUKBModelFilenames) throws Exception
    {   
        // We initialize the base class
        
        super(wordnetDb, taxonomy, CorrelationOutputMetrics.Pearson, strWordPairsFile);
        
        // We check the validity of the input parameters. We expect one
        // IC model per similarity measure. The IC models for non IC-based similarity
        // measures are defined to null.
        
        if (measureTypes.length != icModels.length)
        {
            throw (new InvalidParameterException());
        }
        
        // We store the data to carry-out the benchmark
        
        m_MeasureTypes = measureTypes;
        m_ICmodels = icModels;
        m_strRawEmbeddingFilenames = strEmbModelFilenames;
        m_strNasariModelFilenames = strNasariModelFilenames;
        m_strUKBModelFilenames = strUKBModelFilenames;
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
        
        // User message reporting the output file to be computed
        
        System.out.println("/**");
        System.out.println(" * Benchmark: similarity measures vs (Dataset and WordNet version)");
        System.out.println(" * Computing the output file: " + strMatrixResultsFile);
        System.out.println(" **/");
        
        // We get the formatted output matrix for the current experiment.
        
        strOutputMatrix = getOurOutputMatrix();
        
        // We iterate over the similarity measures

        for (int iMeasure = 0;
                iMeasure < m_MeasureTypes.length + m_strRawEmbeddingFilenames.length;
                iMeasure++)
        {
            // The measure needs to be created every time in this place,
            // in order to allow the upgrade of the internal parameters
            // depending of the base taxonomy. It is necessary to use
            // the version of MeasureFactory.getMeasure() that accepts
            // WordNet as input paprameter, because of the TaiebSim2
            // only can be created through this function.

            IWordSimilarityMeasure measure;
            
            if (iMeasure < m_MeasureTypes.length)
            {
                measure = MeasureFactory.getWordNetWordSimilarityMeasure(
                            m_Wordnet, m_Taxonomy, m_MeasureTypes[iMeasure],
                            m_ICmodels[iMeasure]);
            }
            else
            {
                measure = MeasureFactory.getRawWordEmbeddingModel(
                            m_strRawEmbeddingFilenames[iMeasure - m_MeasureTypes.length]);
            }
                
            if (showDebugInfo)
            {
                System.out.println(measure.toString());
            }
            
            // We evaluate the similarity for each word pair

            for (int iWord = 0; iWord < m_WordPairs.length; iWord++)
            {
                // We evaluate the similarity between word 1 and word2
                
                if (showDebugInfo)
                {
                    String strDebugMsg = "Evaluating " + (iWord + 1) + " of "
                            + m_WordPairs.length + " word pairs";
                    
                    System.out.println(strDebugMsg);
                }
                
                // We compute the highest similarity value
                
                double similarity = measure.getSimilarity(m_WordPairs[iWord].getWord1(),
                                    m_WordPairs[iWord].getWord2());
                
                // We save the similarity value in the output matrix
                
                strOutputMatrix[iWord + 1][2 + iMeasure] = Double.toString(similarity);
            }
        }
        
        // We save the file in CSV format
        
        saveCSVfile(strOutputMatrix, strMatrixResultsFile);
    }
    
    /**
     * This function creates the experiment matrix to record the raw
     * similarity values returned by each measure for each word pair.
     * The number of rows is set to 1 (human judgements) plus the number
     * of similarity measures which are evaluated, whilst the number of rows
     * is set to 1 (headers) plus the word pair count.
     * @return Blank experiment matrix
     */
    
    private String[][] getOurOutputMatrix()
    {
        String[][]  strMatrix; // Returne value
        
        int step = getMetricFillingStep();  // Columns for the metrics
        
        // We create the output matrix of raw similarity values.
        // The matrix will have one row per word pair.
        
        strMatrix = new String[1 + m_WordPairs.length][2 + m_MeasureTypes.length
                        + m_strRawEmbeddingFilenames.length];
        
        // We fill the titles of the first two columns
        
        strMatrix[0][0] = "Word pair";
        strMatrix[0][1] = "Human jugdement";
        
        // We fill the titles for the remaining columns
        
        for (int iMeasure = 0; iMeasure < m_MeasureTypes.length; iMeasure++)
        {
            strMatrix[0][iMeasure + 2]= m_MeasureTypes[iMeasure].toString();
            
            // Second column shows the name of the IC model
            
            if (m_ICmodels[iMeasure] != null)
            {
                strMatrix[0][iMeasure + 2] += " + " + m_ICmodels[iMeasure].toString();
            }
        }
        
        // We fill the filenames of the raw vector files
        
        for (int i = 0; i < m_strRawEmbeddingFilenames.length; i++)
        {
            strMatrix[0][2 + m_MeasureTypes.length + i] = m_strRawEmbeddingFilenames[i];
        }
        
        // We fill the first two columns
        
        for (int iWordPair = 0; iWordPair < m_WordPairs.length; iWordPair++)
        {
            strMatrix[iWordPair + 1][0] = m_WordPairs[iWordPair].getWord1() + ","
                                            + m_WordPairs[iWordPair].getWord2();
            
            strMatrix[iWordPair + 1][1] = Double.toString(m_WordPairs[iWordPair].getHumanJudgement());
        }
        
        // We return the result
        
        return (strMatrix);
    }   
}
