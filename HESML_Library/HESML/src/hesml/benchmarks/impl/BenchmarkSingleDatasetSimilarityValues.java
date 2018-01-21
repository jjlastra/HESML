/*
 * Copyright (C) 2018 Universidad Nacional de Educación a Distancia (UNED)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
     * Constructor
     */
    
    BenchmarkSingleDatasetSimilarityValues(
            IWordNetDB                  wordnetDb,
            ITaxonomy                   taxonomy,
            String                      strWordPairsFile,
            ITaxonomyInfoConfigurator[] icModels,
            SimilarityMeasureType[]     measureTypes) throws Exception
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
                        + " IC model on " + m_Wordnet.getVersion();
            }
                
            // The measure needs to be created every time in this place,
            // in order to allow the upgrade of the internal parameters
            // depending of the base taxonomy. It is necessary to use
            // the version of MeasureFactory.getMeasure() that accepts
            // WordNet as input paprameter, because of the TaiebSim2
            // only can be created through this function.

            m_Measure = MeasureFactory.getMeasure(m_Wordnet, m_Taxonomy,
                            m_MeasureTypes[iMeasure]);
                
            // We evaluate the similarity for each word pair

            for (int iWord = 0; iWord < m_WordPairs.length; iWord++)
            {
                // We compute the highest similarity value
                
                double similarity = highestSimilarityValue(m_Measure,
                                    m_WordPairs[iWord].getWord1(),
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
        
        strMatrix = new String[1 + m_WordPairs.length][2 + m_MeasureTypes.length];
        
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
        
        // We fill the first column
        
        for (int iWordPair = 0; iWordPair < m_WordPairs.length; iWordPair++)
        {
            strMatrix[iWordPair + 1][0] = m_WordPairs[iWordPair].getWord1() + ","
                                            + m_WordPairs[iWordPair].getWord2();
        }
        
        // We return the result
        
        return (strMatrix);
    }   
}
