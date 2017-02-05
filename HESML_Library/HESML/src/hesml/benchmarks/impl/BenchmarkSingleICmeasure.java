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
import hesml.configurators.*;
import hesml.configurators.icmodels.ICModelsFactory;
import static hesml.benchmarks.impl.AbstractBenchmark.saveCSVfile;

/**
 * This class evaluates one single IC-based similarity measure with a
 * collection of intrinsic IC models and returns the matrix of Pearson
 * correlation values.
 * @author Juan Lastra-Díaz
 */

class BenchmarkSingleICmeasure  extends WordNetSimBenchmark
{
    /**
     * Collection of IC models to be evaluated
     */
    
    private final IntrinsicICModelType[]    m_ICmodels;
    
    /**
     * measure type
     */
    
    private final SimilarityMeasureType m_MeasureType;
    
    /**
     * Constructor with multiple IC models
     */
    
    BenchmarkSingleICmeasure(
            IWordNetDB                  wordnet,
            ITaxonomy                   taxonomy,
            CorrelationOutputMetrics    metrics,
            String                      strWordPairsFile,
            IntrinsicICModelType[]      icModels,
            SimilarityMeasureType       measureType) throws Exception
    {
        // We initialize the base clase
        
        super(wordnet, taxonomy, metrics, strWordPairsFile);
        
        // We store the data to carry-out the becnhmark
        
        m_ICmodels = icModels;
        m_MeasureType = measureType;
    }

    /**
     * Constructor with a single IC model
     */
    
    BenchmarkSingleICmeasure(
            IWordNetDB                  wordnet,
            ITaxonomy                   taxonomy,
            CorrelationOutputMetrics    metrics,
            String                      strWordPairsFile,
            IntrinsicICModelType        icModel,
            SimilarityMeasureType       measureType) throws Exception
    {
        // We initialize the base clase
        
        super(wordnet, taxonomy, metrics, strWordPairsFile);
        
        // We store the data to carry-out the becnhmark
        
        m_ICmodels = new IntrinsicICModelType[1];
        m_ICmodels[0] = icModel;
        m_MeasureType = measureType;
    }    
    
    /**
     * This function runs the tests
     * @param strMatrixResultsFile 
     */
    
    @Override
    public void executeTests(
            String  strMatrixResultsFile,
            boolean showDebugInfo) throws Exception            
    {
        ITaxonomyInfoConfigurator    intrinsicMethod;    // IC-computation method
        
        String[][]  strMatrix;  // Results matrix
        
        double[]  pearsonSpearman;    // Correlation values

        SimilarityMeasureType[] types = {m_MeasureType};

        // User message reporting the output file to be computed
        
        System.out.println("/**");
        System.out.println(" * Benchmark: intrinsic IC models vs "
            + m_MeasureType.toString() + " similarity measure");
        System.out.println(" * Computing the output file: " + strMatrixResultsFile);
        System.out.println(" **/");
        
        // We get the matrix
        
        strMatrix = getOutputMetricsMatrix(types, m_ICmodels);
        
        // We compute all the IC-node methods
        
        for (int i = 0; i < m_ICmodels.length; i++)
        {
            // We get the IC-node cheme
            
            intrinsicMethod = ICModelsFactory.getIntrinsicICmodel(m_ICmodels[i]);
        
            // We compute and saves the IC values per node
            
            System.out.println("*************************************");
            System.out.println("Computing intrinsic IC model " + m_ICmodels[i].toString());
            
            intrinsicMethod.setTaxonomyData(m_Taxonomy);

            // The measure needs to be cretaed every time in this place,
            // to allow the upgrade of the internal parameters depending
            // on the IC models. It is the case of our cosine-normalized
            // Jiang-Conrath measures, which require to compute the
            // maximum IC value in order to normalize the distance measure.
            
            m_Measure = MeasureFactory.getMeasure(m_Taxonomy, m_MeasureType);
            
            // We evaluate different intrinsic measures
        
            System.out.println("---------------------------------");
            System.out.println("Evaluating " + m_MeasureType.toString());

            // We evaluate the measure
            
            pearsonSpearman = evalPearsonSpearmanMeasure(showDebugInfo);

            // We show thee results
            
            System.out.println("Intrinsic IC-node " +
                    m_ICmodels[i].toString() + " Measure "
                    + m_MeasureType.toString()
                    + " -> Pearson/Spearman = " + pearsonSpearman[0]
                    + "/" + pearsonSpearman[1]);

            // We save the result in the matrix

            setOutputMetrics(strMatrix, i + 1, 1,
                    pearsonSpearman[0], pearsonSpearman[1]);
        }
        
        // We save the file in CSV format
        
        saveCSVfile(strMatrix, strMatrixResultsFile);
    }
}
