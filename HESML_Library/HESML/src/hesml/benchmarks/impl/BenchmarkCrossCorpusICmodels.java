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
 * This class implements a set of cross-validation benchmarks by computing
 * the correlation values for the experimental matrix defined by the
 * Cartesian product of a set of corpus-based IC models versus a set
 * of IC-based similarity measures.
 * The benchmark computes the Pearson and Spearman correlation values
 * for each pair (IC model, IC measure).
 * @author Juan Lastra-Díaz
 */

class BenchmarkCrossCorpusICModels extends WordNetSimBenchmark
{
    /**
     * Collection of WordNet-based frequency files used to compute
     * each IC model. The files are in the file format provided by
     * Ted Perdersen.
     */
    
    private String[]    m_strWordNetFrequencyFiles;
    
    /**
     * Directory containing the WordNet-based frequency files
     */
    
    private String  m_strWordNetFreqFilesDir;
    
    /**
     * Measure types evaluated
     */
    
    private SimilarityMeasureType[]   m_MeasureTypes;
    
    /**
     * Corpus-based IC models to be evaluated
     */
    
    private CorpusBasedICModelType[]    m_CorpusICmodels;
    
    /**
     * Constructor
     */
    
    BenchmarkCrossCorpusICModels(
            IWordNetDB                  wordnet,
            ITaxonomy                   taxonomy,
            CorrelationOutputMetrics    metrics,
            String                      strWordPairsFile,
            String[]                    strICPedersenFiles,
            String                      strPedersenFilesDir,
            CorpusBasedICModelType[]    corpusICmodels,
            SimilarityMeasureType[]     measureTypes) throws Exception
    {   
        // We initialize the base clase
        
        super(wordnet, taxonomy, metrics, strWordPairsFile);
        
        // We check the input parameters
        
        if (corpusICmodels.length != strICPedersenFiles.length)
        {
            throw (new Exception("The number of IC files must match the number of IC models"));
        }
        
        // We store the data to carry-out the becnhmark
        
        m_strWordNetFrequencyFiles = strICPedersenFiles;
        m_strWordNetFreqFilesDir = strPedersenFilesDir;
        m_MeasureTypes = measureTypes;
        m_CorpusICmodels = corpusICmodels;
    }

    /**
     * This function runs the experiments matrix ICModel vs IC-based measure.
     * For each IC model the function evaluates all the IC-based measures.
     * @param strMatrixResultsFile 
     */
    
    @Override
    public void executeTests(
            String  strMatrixResultsFile,
            boolean showDebugInfo) throws Exception
    {
        ITaxonomyInfoConfigurator    intrinsicMethod;    // IC-computation method
        
        String[][]  strMatrix;  // Results matrix
        
        double[]  pearsonSpearman;    // Correlation value

        int step = getMetricFillingStep();  // Step to fill the matrix
        
        // User message reporting the output file to be computed
        
        System.out.println("/**");
        System.out.println(" * Benchmark: corpus IC models vs IC-based measures");
        System.out.println(" * Computing the output file: " + strMatrixResultsFile);
        System.out.println(" **/");
        
        // We get the matrix
        
        strMatrix = getOutputMetricsMatrix(m_MeasureTypes, buildICmodelFileNames());
        
        // We compute all the IC-node methods
        
        for (int i = 0; i < m_strWordNetFrequencyFiles.length; i++)
        {
            // We get the corpus-based IC model
            
            intrinsicMethod = ICModelsFactory.getCorpusICmodel(m_CorpusICmodels[i],
                                m_strWordNetFreqFilesDir + "/"
                                + m_strWordNetFrequencyFiles[i]);
        
            // We compute and saves the IC values per node

            intrinsicMethod.setTaxonomyData(m_Taxonomy);

            // We show a log message
            
            String  strICmodelDescription = "Corpus-based IC model "
                        + getICmodelDescriptor(m_CorpusICmodels[i], m_strWordNetFrequencyFiles[i]);
            
            System.out.println("*************************************");
            System.out.println(strICmodelDescription);
            
            // We iterate over the measures
            
            for (int j = 0, k = 0; j < m_MeasureTypes.length; j++, k += step)
            {
                // The measure needs to be cretaed every time in this place,
                // to allow the upgrade of the internal parameters

                m_Measure = MeasureFactory.getMeasure(m_Taxonomy, m_MeasureTypes[j]);

                // We evaluate different intrinsic measures

                System.out.println("---------------------------------");
                System.out.println("Evaluating " + m_MeasureTypes[j].toString());

                pearsonSpearman = evalPearsonSpearmanMeasure(showDebugInfo);

                System.out.println(strICmodelDescription
                        + " Measure " + m_MeasureTypes[j].toString()
                        + " -> Pearson/ Spearman = " + pearsonSpearman[0]
                        + " / " + pearsonSpearman[1]);

                // We save the result in the matrix

                setOutputMetrics(strMatrix, i + 1, k + 1,
                        pearsonSpearman[0], pearsonSpearman[1]);
            }
        }
        
        // We save the file in CSV format
        
        saveCSVfile(strMatrix, strMatrixResultsFile);
    }
    
    /**
     * This function computes a descriptor for the IC model
     * @param icModel
     * @param strFrequencyFile
     * @return 
     */
    
    private String getICmodelDescriptor(
            CorpusBasedICModelType  icModel,
            String                  strFrequencyFile)
    {
        String  strDescriptor;  // returned value
        
        // We build the descriptor
        
        strDescriptor = "(" + icModel.toString() + "," + strFrequencyFile + ")";
        
        // We return the result
        
        return (strDescriptor);
    }
    
    /**
     * This function builds a vector of strings with the names of the
     * IC models plus the WordNet-based frquency file used in their computation.
     * @return 
     */
    
    private String[] buildICmodelFileNames()
    {
        String[]    strAssembledICmodelNames;   // Returned value
        
        // We create the balnk vector
        
        strAssembledICmodelNames = new String[m_CorpusICmodels.length];
        
        // We fill the names vector of the IC models
        
        for (int i = 0; i < m_CorpusICmodels.length; i++)
        {
            strAssembledICmodelNames[i] = getICmodelDescriptor(m_CorpusICmodels[i],
                                               m_strWordNetFrequencyFiles[i]);
        }
        
        // We return the result
        
        return (strAssembledICmodelNames);
    }
}

