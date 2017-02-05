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
import hesml.benchmarks.*;
import hesml.measures.*;
import hesml.taxonomy.*;
import hesml.configurators.*;

/**
 * The main aim of this class is to instantiate all the benchmarks in order to
 * hide them to the client code.
 * @author Juan Lastra-Díaz
 */

public class BenchmarkFactory
{
    /**
     * This function returns the test for a single IC-based similarity
     * measure evaluated with multiple intrinsic IC models.
     * @param taxonomy          WordNet taxonomy
     * @param wordnet           WordNet database
     * @param strWordPairsFile  Word similarity benchmark
     * @param outputMetrics
     * @param icModels          IC models to be evaluated
     * @param measureType       IC-based simialrity measure
     * @return                  Benchmark
     * @throws Exception 
     */
    
    public static ISimilarityBenchmark getSingleICSimilarityMeasureTest(
            ITaxonomy                   taxonomy,
            IWordNetDB                  wordnet,
            String                      strWordPairsFile,
            CorrelationOutputMetrics    outputMetrics,
            IntrinsicICModelType[]      icModels,
            SimilarityMeasureType       measureType) throws Exception
    {
        ISimilarityBenchmark    test;   // Returned value
        
        // We create the test
        
        test = new BenchmarkSingleICmeasure(wordnet, taxonomy,
                    outputMetrics, strWordPairsFile, icModels, measureType);
        
        // We return the value
        
        return (test);
    }

    /**
     * This function returns the test for a single IC-based similarity
     * measure evaluated with a single intrinsic IC model.
     * @param taxonomy          WordNet taxonomy
     * @param wordnet           WordNet database
     * @param strWordPairsFile  Word similarity benchmark
     * @param outputMetrics
     * @param icModel           IC model to be evaluated
     * @param measureType       IC-based simialrity measure
     * @return                  Benchmark
     * @throws Exception 
     */
    
    public static ISimilarityBenchmark getSingleICSimilarityMeasureTest(
            ITaxonomy                   taxonomy,
            IWordNetDB                  wordnet,
            String                      strWordPairsFile,
            CorrelationOutputMetrics    outputMetrics,
            IntrinsicICModelType        icModel,
            SimilarityMeasureType       measureType) throws Exception
    {
        ISimilarityBenchmark    test;   // Returned value
        
        // We create the test
        
        test = new BenchmarkSingleICmeasure(wordnet, taxonomy,
                    outputMetrics, strWordPairsFile, icModel, measureType);
        
        // We return the value
        
        return (test);
    }
    
    /**
     * This function computes the data matrix for a taxonomy
     * and IC model. The function assumes that the IC model
     * has been previously associated to the taxonomy. The
     * test evaluates the dataset for each measure and
     * builds a data matrix wit the synset pairs and the
     * similarity values reported by each measure.
     * @param exportICfeatures      Save IC-based features into the output file.
     * @param wordnet               WordNet database
     * @param wordnetTaxonomy       WordNetTaxonomy
     * @param strSimilarityTestFile Word similarity benchmark filename
     * @param strOutputCSVfile      Output *.csv file
     * @param measureTypes          Similarity measures to be evaluated
     * @throws java.lang.Exception 
     */
    
    public static void buildSynsetsDataMatrixEvaluation(
            boolean                 exportICfeatures,
            IWordNetDB              wordnet,
            ITaxonomy               wordnetTaxonomy,
            String                  strSimilarityTestFile,
            String                  strOutputCSVfile,
            SimilarityMeasureType[] measureTypes) throws Exception
    {
        BenchmarkSynsetsInfo    benchmark;  // Benchmark
        
        // We createh the benchmark
        
        benchmark = new BenchmarkSynsetsInfo(wordnet, wordnetTaxonomy,
                        CorrelationOutputMetrics.PearsonAndSpearman,
                        strSimilarityTestFile, exportICfeatures, measureTypes);
        
        // We save the results matrix
        
        benchmark.executeTests(strOutputCSVfile, false);
    }
    
    /**
     * This function returns the test for multiple IC-based similarity measures
     * versus a collection of intrinsic IC models.
     * @param taxonomy          WordNet taxonomy
     * @param wordnet           WordNet database
     * @param strWordPairsFile  Filename of the word similarity benchmark (dataset)
     * @param outputMetrics
     * @param icModels          Type of IC models to be evaluated
     * @param measureTypes      Type of the measures to be evaluated
     * @return                  The benchmark
     * @throws Exception 
     */
    
    public static ISimilarityBenchmark getMultipleICSimilarityMeasuresTest(
            ITaxonomy                   taxonomy,
            IWordNetDB                  wordnet,
            String                      strWordPairsFile,
            CorrelationOutputMetrics    outputMetrics,
            IntrinsicICModelType[]      icModels,
            SimilarityMeasureType[]     measureTypes) throws Exception
    {
        ISimilarityBenchmark    test;   // Returned value
        
        // We create the test
        
        test = new BenchmarkCrossICModelMeasures(wordnet, taxonomy,
                    outputMetrics, strWordPairsFile, icModels, measureTypes);
        
        // We return the value
        
        return (test);
    }
    
    /**
     * This function returns the test for multiple IC-based similarity measures
     * versus a collection of intrinsic IC models.
     * @param taxonomy          WordNet taxonomy
     * @param wordnet           WordNet database
     * @param strWordPairsFile  Filename of the word similarity benchmark (dataset)
     * @param outputMetrics
     * @param icModels          Type of IC models to be evaluated
     * @param measureTypes      Type of the measures to be evaluated
     * @return                  The benchmark
     * @throws Exception 
     */
    
    public static ISimilarityBenchmark getMixedICModelsSimilarityTest(
            ITaxonomy                   taxonomy,
            IWordNetDB                  wordnet,
            String                      strWordPairsFile,
            CorrelationOutputMetrics    outputMetrics,
            ITaxonomyInfoConfigurator[] icModels,
            SimilarityMeasureType[]     measureTypes) throws Exception
    {
        ISimilarityBenchmark    test;   // Returned value
        
        // We create the test
        
        test = new BenchmarkCrossICModelMeasures(wordnet, taxonomy,
                    outputMetrics, strWordPairsFile, icModels, measureTypes);
        
        // We return the value
        
        return (test);
    }
    
    /**
     * This function creates benchmark for multiple non IC-based measures.
     * @param taxonomy WordNet taxonomy
     * @param wordnet WordNet database
     * @param strWordPairsFile Word similarity benchmark filename
     * @param outputMetrics
     * @param measures Set of measures to be evaluated
     * @return The benchmark
     * @throws Exception 
     */
    
    public static ISimilarityBenchmark getMultipleNonICBasedMeasureTest(
            ITaxonomy                   taxonomy,
            IWordNetDB                  wordnet,
            String                      strWordPairsFile,
            CorrelationOutputMetrics    outputMetrics,
            SimilarityMeasureType[]     measures) throws Exception
    {
        ISimilarityBenchmark    test;   // Returned value
        
        // We create the test
        
        test = new BenchmarkNonICMultiMeasures(wordnet, taxonomy,
                    outputMetrics, strWordPairsFile,  measures);
        
        // We return the value
        
        return (test);
    }
    
    /**
     * This function returns a benchmark to evaluate a single corpus-based IC model
     * with a collection of Wordnet-based frequency files created by Ted Pedersen.
     * @param taxonomy WordNet taxonomy
     * @param wordnet WprdNet database
     * @param strWordPairsFile Word similarity benchmark filename
     * @param outputMetrics
     * @param strICPedersenModels Collection of WordNet-based frequency files
     * @param strICfilesDir Directory containing the frequency files
     * @param corpusICmodel Type of corpus-based IC model to be created
     * @param measureTypes IC-based similarity measures to be evaluated
     * @return The benchmark
     * @throws Exception 
     */
    
    public static ISimilarityBenchmark getMultipleCorpusICSimilarityMeasuresTest(
            ITaxonomy                   taxonomy,
            IWordNetDB                  wordnet,
            String                      strWordPairsFile,
            CorrelationOutputMetrics    outputMetrics,
            String[]                    strICPedersenModels,
            String                      strICfilesDir,
            CorpusBasedICModelType      corpusICmodel,
            SimilarityMeasureType[]     measureTypes) throws Exception
    {
        ISimilarityBenchmark    test;   // Returned value
        
        // We create the test
        
        test = new BenchmarkCrossCorpusICModelMeasures(wordnet, taxonomy,
                    outputMetrics, strWordPairsFile, strICPedersenModels,
                    strICfilesDir, corpusICmodel, measureTypes);
        
        // We return the value
        
        return (test);
    }
    
    /**
     * This function returns a benchmark to evaluate a set of corpus-based IC models
     * defined with their corresponding Wordnet-based frequency files.
     * @param taxonomy WordNet taxonomy
     * @param wordnet  WordNet database
     * @param strWordPairsFile File with the word similarity benchmark
     * @param outputMetrics
     * @param strICfilesDir Directory containig the WordNet-based frequency files
     * @param corpusICmodels Vector of corpus-based IC models
     * @param strICPedersenModels Vector of frequency files
     * @param measureTypes Similarity measures to be evaluated
     * @return The benchmark
     * @throws Exception 
     */
    
    public static ISimilarityBenchmark getCorpusICSimilarityMeasuresTest(
            ITaxonomy                   taxonomy,
            IWordNetDB                  wordnet,
            String                      strWordPairsFile,
            CorrelationOutputMetrics    outputMetrics,
            String                      strICfilesDir,
            CorpusBasedICModelType[]    corpusICmodels,
            String[]                    strICPedersenModels,            
            SimilarityMeasureType[]     measureTypes) throws Exception
    {
        ISimilarityBenchmark    test;   // Returned value
        
        // We create the test
        
        test = new BenchmarkCrossCorpusICModels(wordnet, taxonomy,
                    outputMetrics, strWordPairsFile, strICPedersenModels,
                    strICfilesDir, corpusICmodels, measureTypes);
        
        // We return the value
        
        return (test);
    }
    
    /**
     * This function creates a benchmark for non IC-based measures.
     * The benchmark instantiates a measure, but an IC mdoel is not
     * created before the evaluation of the measure.
     * @param taxonomy WordNet taxonomy
     * @param wordnet WordNet database
     * @param strWordPairsFile Word similarity benchmark
     * @param outputMetrics
     * @param measureType Measure type to be evaluated
     * @return The benchmark
     * @throws Exception 
     */
    
    public static ISimilarityBenchmark getSingleNonICSimilarityMeasureTest(
            ITaxonomy                   taxonomy,
            IWordNetDB                  wordnet,
            String                      strWordPairsFile,
            CorrelationOutputMetrics    outputMetrics,
            SimilarityMeasureType       measureType) throws Exception
    {
        ISimilarityBenchmark    test;   // Returned value
        
        // We create the test
        
        test = new BenchmarkSingleNonICMeasure(wordnet, taxonomy,
                    outputMetrics, strWordPairsFile, measureType);
        
        // We return the value
        
        return (test);
    }
    
    /**
     * This function creates an instance of a multiple dataset experiment.
     * @param wordnetVersions
     * @param taxonomies
     * @param metrics
     * @param strWordPairsFiles
     * @param icModels
     * @param measureTypes
     * @return
     * @throws Exception 
     */
    
    public static ISimilarityBenchmark getMultipleDatasetsBenchmark(
            IWordNetDB[]                wordnetVersions,
            ITaxonomy[]                 taxonomies,
            CorrelationOutputMetrics    metrics,
            String[]                    strWordPairsFiles,
            ITaxonomyInfoConfigurator[] icModels,
            SimilarityMeasureType[]     measureTypes) throws Exception
    {
        BenchmarkMultipleDatasets   benchmark;  // Returned value
        
        // We create the benchmark
        
        benchmark = new BenchmarkMultipleDatasets(wordnetVersions,
                        taxonomies, metrics, strWordPairsFiles,
                        icModels, measureTypes);
        
        // We return the result
        
        return (benchmark);
    }
}
