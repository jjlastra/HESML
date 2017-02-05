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

// HESML references

import hesml.taxonomyreaders.wordnet.IWordNetDB;
import hesml.benchmarks.CorrelationOutputMetrics;
import hesml.measures.*;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.*;
import static hesml.benchmarks.impl.AbstractBenchmark.saveCSVfile;

/**
 * This class implements a set of word similarity benchmarks for all
 * similarity measures which are not based on an IC model.
 * @author Juan Lastra-Díaz
 */

class BenchmarkNonICMultiMeasures extends WordNetSimBenchmark
{
    /**
     * Measures
     */
    
    private final ISimilarityMeasure[]   m_Measures;
    
    /**
     * Constructor
     * @param wordnet
     * @param taxonomy
     * @param strWordPairsFile
     * @throws Exception 
     */

    BenchmarkNonICMultiMeasures(
            IWordNetDB                  wordnet,
            ITaxonomy                   taxonomy,
            CorrelationOutputMetrics    metrics,
            String                      strWordPairsFile,
            SimilarityMeasureType[]     measures) throws Exception
    {
        super(wordnet, taxonomy, metrics, strWordPairsFile);
        
        int i =  0; // Counter
        
        // We crete the novel measure
        
        m_Measures = new ISimilarityMeasure[measures.length];
        
        // We create the set of measures to be evaluated
        
        for (SimilarityMeasureType measure: measures)
        {
            m_Measures[i++] = MeasureFactory.getMeasure(taxonomy, measure);
        }
    }

    /**
     * This function runs the experiments and saves the results in 
     * a CSV file
     * @param strMatrixResultsFile
     * @throws Exception 
     */
    
    @Override
    public void executeTests(
            String  strMatrixResultsFile,
            boolean showDebugInfo) throws Exception
    {
        String[][]  strMatrix;  // Results matrix
        
        double[]    pearsonSpearman;
        
        // User message reporting the output file to be computed
        
        System.out.println("/**");
        System.out.println(" * Benchmark: evaluating different non IC-based similarity measures");
        System.out.println(" * Computing the output file: " + strMatrixResultsFile);
        System.out.println(" **/");
        
        // We create the matrix
        
        strMatrix = getOutputMetricsMatrix(m_Measures);
        
        // We evaluate all the planned measures
        
        for (int i = 0; i < m_Measures.length; i++)
        {
            // We create the next measure
            
            m_Measure = m_Measures[i];
            
            // We evaluate the measure
            
            pearsonSpearman = evalPearsonSpearmanMeasure(showDebugInfo);
            
            // Wesave the results in the output matrix
            
            setOutputMetrics(strMatrix, i + 1, 1,
                    pearsonSpearman[0], pearsonSpearman[1]);
        
            // We print the result

            System.out.println(m_Measure.getMeasureType().toString()
                + " Pearson value = " + strMatrix[i + 1][1]);
        }
        
        // We save the results
        
        saveCSVfile(strMatrix, strMatrixResultsFile);
    }
}

