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
 * This class implements a set of word similarity benchmarks for a single
 * non IC-based similarity measure.
 * @author Juan Lastra-Díaz
 */

class BenchmarkSingleNonICMeasure extends WordNetSimBenchmark
{
    /**
     * Constructor
     * @param wordnet
     * @param taxonomy
     * @param strWordPairsFile
     * @throws Exception 
     */

    BenchmarkSingleNonICMeasure(
            IWordNetDB                  wordnet,
            ITaxonomy                   taxonomy,
            CorrelationOutputMetrics    metrics,
            String                      strWordPairsFile,
            SimilarityMeasureType       measureType) throws Exception
    {
        super(wordnet, taxonomy, metrics, strWordPairsFile);
        
        // We crete the novel measure
        
        m_Measure = MeasureFactory.getMeasure(taxonomy, measureType);
    }

    /**
     * This functions executes the tests and saves the Pearson and
     * Spearman correlation values into a CSV file.
     * @param strMatrixResultsFile
     * @throws Exception 
     */
    
    @Override
    public void executeTests(
            String  strMatrixResultsFile,
            boolean showDebugInfo) throws Exception
    {
        String[][]  strMatrix;  // Results matrix
        
        double[]    pearsonSpearman;    // Correlation values
        
        // User message reporting the output file to be computed
        
        System.out.println("/**");
        System.out.println(" * Evaluating " +
                m_Measure.getMeasureType().toString() + " similarity measure");
        System.out.println(" * Computing the output file: " + strMatrixResultsFile);
        System.out.println(" **/");
        
        // We create the matrix
        
        strMatrix = new String[1][3];
        
        // We evaluate the measure
        
        pearsonSpearman = evalPearsonSpearmanMeasure(showDebugInfo);
        
        // We evaluate the measure

        strMatrix[0][0] = m_Measure.getMeasureType().toString();
        strMatrix[0][1] = Double.toString(pearsonSpearman[0]);
        strMatrix[0][2] = Double.toString(pearsonSpearman[1]);

        // We print the result

        System.out.println(m_Measure.getMeasureType().toString()
            + " Pearson/Spearman values = " + strMatrix[0][1]
            + "/" + strMatrix[0][2]);
        
        // We save the results
        
        saveCSVfile(strMatrix, strMatrixResultsFile);
    }
}
