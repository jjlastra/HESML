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

// References to Java objects

import java.io.FileWriter;
import java.io.IOException;

// HESML objects

import hesml.benchmarks.*;
import hesml.taxonomy.*;
import hesml.configurators.*;
import hesml.measures.*;

/**
 * This class defines a collection of common functions for all the similarity benchmarks.
 * @author Juan Lastra-Díaz
 */

abstract class AbstractBenchmark implements ISimilarityBenchmark
{
    /**
     * Taxonomy commonly associated to WordNet
     */
    
    protected ITaxonomy   m_Taxonomy;

    /**
     * Default output filename
     */
    
    protected String    m_strDefaulOutputFileName;
    
    /**
     * Set of metrics included in the output matrix.
     */
    
    protected CorrelationOutputMetrics   m_OutputMetrics;
    
    /**
     * Constructor
     * @param taxonomy Taxonomy used in the benchmark 
     * @param metrics Type of correlation metrics that will be computed
     */
    
    AbstractBenchmark(
            ITaxonomy                   taxonomy,
            CorrelationOutputMetrics    metrics) throws Exception
    {
        m_Taxonomy = taxonomy;
        m_OutputMetrics = metrics;
        m_strDefaulOutputFileName = "Output.csv";
    }
    
    /**
     * This function releases all the resources used by the object.
     */
    
    @Override
    public void clear()
    {
        m_Taxonomy.clear();
    }
    
    /**
     * This function returns the output filename of the benchmark.
     * @return Output filename
     */
    
    @Override
    public String  getDefaultOutputFilename()
    {
        return (m_strDefaulOutputFileName);
    }
    
    /**
     * This function sets the default output filename.
     * @param strDefaultOutputFilename 
     */
    
    @Override
    public void setDefaultOutputFilename(
        String  strDefaultOutputFilename)
    {
        m_strDefaulOutputFileName = strDefaultOutputFilename;
    }

    
    /**
     * This function returns the associated taxonomy.
     * @return The base taxonomy used by the test.
     */
    
    @Override
    public ITaxonomy getTaxonomy()
    {
        return (m_Taxonomy);
    }
       
    /**
     * This function ss aves the results in a CSV file
     * @param strMatrix 
     */
    
    protected static void saveCSVfile(
            String[][]  strMatrix,
            String      strOutputFile) throws IOException
    {
        FileWriter  writer;     // Output file
        
        int i, j;   // Counters
        
        // We open for writing the file
        
        writer = new FileWriter(strOutputFile, false);

        // We write the matrix
        
        for (i = 0; i < strMatrix.length; i++)
        {
            for (j = 0; j < strMatrix[0].length; j++)
            {
                if (j > 0)
                {
                    writer.write(";");
                }
                
                writer.write(strMatrix[i][j]);
            }
            
            // Werite the end of line
            
            writer.write("\n");
        }
        
        // We close the file
        
        writer.close();
    }

    /**
     * This function create a blank experiment matrix in order to record the
     * correlation values reported for the cross-validation of multiple
     * measures versus multiple IC-based measures. The number
     * of columns depends on the number of output metrics specified by the
     * client code.
     * @param measureTypes Vector with the types of similarity measures to be evaluated.
     * @param strICModels Names of the IC models to be evaluated.
     * @return A blank matrix to record the results of a similarity benchmark.
     */
    
    protected String[][] getOutputMetricsMatrix(
            SimilarityMeasureType[] measureTypes,
            String[]                strICModels)
    {
        String[][]  strMatrix; // Returne value
        
        int step = getMetricFillingStep();  // Columns for the metrics
        
        // We create the matrix
        
        strMatrix = new String[1 + strICModels.length][1 + step * measureTypes.length];
        
        // We fill the titles for the rows (IC-computation methods)
        
        strMatrix[0][0] = "Methods";
        
        for (int i = 0; i < strICModels.length; i++)
        {
            strMatrix[1 + i][0] = strICModels[i];
        }

        // We fill the column titles for the IC-based measures
        
        for (int i = 0, k = 0; i < measureTypes.length; i++, k += step)
        {
            // We define the titles according to the output metrics
            
            switch (m_OutputMetrics)
            {
                case Pearson:
                    
                    strMatrix[0][1 + k] = measureTypes[i].toString() + "-Pearson";
                    
                    break;
                    
                case Spearman:
                    
                    strMatrix[0][1 + k] = measureTypes[i].toString() + "-Spearman";
                    
                    break;
                    
                case PearsonAndSpearman:
                    
                    strMatrix[0][1 + k] = measureTypes[i].toString() + "-Pearson";
                    strMatrix[0][1 + k + 1] = measureTypes[i].toString() + "-Spearman";
                    
                    break;
            }
        }
        
        // We return the result
        
        return (strMatrix);
    }   
    
    /**
     * This function creates the experiment matrix to save the results
     * for multiple measures versus multiple IC-based measures. The number
     * of columns depends on the number of output metrics specified by the
     * client code.
     * @param measureTypes Vector with the types of similarity measures to be evaluated.
     * @param icModels Vector of IC models to be evaluated.
     * @return 
     */
    
    protected String[][] getOutputMetricsMatrix(
            SimilarityMeasureType[]     measureTypes,
            ITaxonomyInfoConfigurator[] icModels)
    {
        String[][]  strMatrix; // Returne value
        
        int step = getMetricFillingStep();  // Columns for the metrics
        
        // We create the matrix
        
        strMatrix = new String[1 + icModels.length][1 + step * measureTypes.length];
        
        // We fill the titles for the rows (IC-computation methods)
        
        strMatrix[0][0] = "Methods";
        
        for (int i = 0; i < icModels.length; i++)
        {
            strMatrix[1 + i][0] = icModels[i].toString();
        }

        // We fill the column titles for the IC-based measures
        
        for (int i = 0, k = 0; i < measureTypes.length; i++, k += step)
        {
            // We define the titles according to the output metrics
            
            switch (m_OutputMetrics)
            {
                case Pearson:
                    
                    strMatrix[0][1 + k] = measureTypes[i].toString() + "-Pearson";
                    
                    break;
                    
                case Spearman:
                    
                    strMatrix[0][1 + k] = measureTypes[i].toString() + "-Spearman";
                    
                    break;
                    
                case PearsonAndSpearman:
                    
                    strMatrix[0][1 + k] = measureTypes[i].toString() + "-Pearson";
                    strMatrix[0][1 + k + 1] = measureTypes[i].toString() + "-Spearman";
                    
                    break;
            }
        }
        
        // We return the result
        
        return (strMatrix);
    }   

    /**
     * This function create the experiment matrix to save the results
     * for multiple measures versus multiple IC-based measures. The number
     * of columns depends on the number of output metrics specified by the
     * client code.
     * @param measureTypes Vector with the types of similarity measures to be evaluated.
     * @param icModels Type vector with the intrinsic IC models to be evaluated.
     * @return 
     */
    
    protected String[][] getOutputMetricsMatrix(
            SimilarityMeasureType[] measureTypes,
            IntrinsicICModelType[]  icModels)
    {
        String[][]  strMatrix; // Returne value
        
        int step = getMetricFillingStep();  // Columns for the metrics
        
        // We create the matrix
        
        strMatrix = new String[1 + icModels.length][1 + step * measureTypes.length];
        
        // We fill the titles for the rows (IC-computation methods)
        
        strMatrix[0][0] = "Methods";
        
        for (int i = 0; i < icModels.length; i++)
        {
            strMatrix[1 + i][0] = icModels[i].toString();
        }

        // We fill the column titles for the IC-based measures
        
        for (int i = 0, k = 0; i < measureTypes.length; i++, k += step)
        {
            // We define the titles according to the output metrics
            
            switch (m_OutputMetrics)
            {
                case Pearson:
                    
                    strMatrix[0][1 + k] = measureTypes[i].toString() + "-Pearson";
                    
                    break;
                    
                case Spearman:
                    
                    strMatrix[0][1 + k] = measureTypes[i].toString() + "-Spearman";
                    
                    break;
                    
                case PearsonAndSpearman:
                    
                    strMatrix[0][1 + k] = measureTypes[i].toString() + "-Pearson";
                    strMatrix[0][1 + k + 1] = measureTypes[i].toString() + "-Spearman";
                    
                    break;
            }
        }
        
        // We return the result
        
        return (strMatrix);
    }   
    
    /**
     * This function create the experiment matrix to save the results
     * for multiple non IC-based measures.
     * @param measures Vector with the measures to be evaluated
     * @return Blank experiment matrix
     */
    
    protected String[][] getOutputMetricsMatrix(
            ISimilarityMeasure[] measures)
    {
        String[][]  strMatrix; // Returne value
        
        int step = getMetricFillingStep();  // Columns for the metrics
        
        // We create the matrix
        
        strMatrix = new String[1 + measures.length][1 + step];
        
        // We fill the titles for the rows (IC-computation methods)
        
        strMatrix[0][0] = "Measures";
        
        for (int i = 0; i < measures.length; i++)
        {
            strMatrix[1 + i][0] = measures[i].getMeasureType().toString();
        }

        // We define the column titles according to the output metrics

        switch (m_OutputMetrics)
        {
            case Pearson:

                strMatrix[0][1] = "Pearson";

                break;

            case Spearman:

                strMatrix[0][1] = "Spearman";

                break;

            case PearsonAndSpearman:

                strMatrix[0][1] = "Pearson";
                strMatrix[0][2] = "Spearman";

                break;
        }
        
        // We return the result
        
        return (strMatrix);
    }   
    
    /**
     * This function returns the step used to fill the output matrix
     * according to the number of metrics reported.
     * @return 
     */
    
    protected int getMetricFillingStep()
    {
        int step = 1;   // Returned value
        
        // We compute the step
        
        if (m_OutputMetrics == CorrelationOutputMetrics.PearsonAndSpearman)
        {
            step = 2;
        }
        
        // We return the result
        
        return (step);
    }
}
