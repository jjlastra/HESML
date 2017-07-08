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

package hesmlclient;

// Java references

import hesml.HESMLversion;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import hesml.benchmarks.CorrelationOutputMetrics;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

// HESML references

import hesml.benchmarks.ISimilarityBenchmark;
import hesml.benchmarks.impl.BenchmarkFactory;
import hesml.taxonomyreaders.wordnet.impl.WordNetFactory;
import hesml.configurators.*;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.*;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.*;
import hesml.taxonomy.impl.TaxonomyFactory;
import java.io.File;
import java.util.LinkedList;
import java.util.Random;

/**
 * This class implements a basic client application of the HESML similarity
 * measures library introduced in the paper below.
 * 
 * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * HESML: a scalable ontology-based semantic similarity measures
 * library with a set of reproducible experiments and a replication dataset.
 * Submitted for publication in Information Systems Journal.
 * 
 * @author j.lastra
 */

public class HESMLclient
{
    /**
     * Dataset names for the five most significant datasets distributed with
     * HESML. These datasets have been included in HESML as CSV files. If you
     * use them in your experiments, you should cite the origional works
     * where these datasets are introduced.
     */
    
    /**
     * Rubenstein, H., and Goodenough, J. B. (1965).
     * Contextual Correlates of Synonymy. Communications of the ACM, 8(10), 627–633.
     */
    
    private static final String   RG65 = "Rubenstein_Goodenough_dataset";
    
    /**
     * Miller, G. A., and Charles, W. G. (1991).
     * Contextual correlates of semantic similarity. Language and
     * Cognitive Processes, 6(1), 1–28.
     */
    
    private static final String   MC28 = "Miller_Charles_28_dataset";

    /**
     * Pirró, G. (2009). A semantic similarity metric combining features and
     * intrinsic information content. Data and Knowledge Engineering, 68(11),
     * pp.1289–1308.
     */
    
    private static final String   PIRRO_SECO = "PirroSeco_full_dataset";
    
    /**
     * Agirre, E., Alfonseca, E., Hall, K., Kravalova, J., Paşca, M., and Soroa, A. (2009).
     * A Study on Similarity and Relatedness Using Distributional and
     * WordNet-based Approaches. In Proceedings of Human Language Technologies:
     * The 2009 Annual Conference of the North American Chapter of the
     * Association for Computational Linguistics (pp. 19–27). Stroudsburg,
     * PA, USA: Association for Computational Linguistics.
     */
    
    private static final String   AGIRRE203 = "Agirre201_dataset";
    
    /**
     * Hill, F., Reichart, R., and Korhonen, A. (2014). SimLex-999:
     * Evaluating Semantic Models with (Genuine) Similarity Estimation.
     * arXiv:1408.3456. Retrieved from http://arxiv.org/abs/1408.3456
     */
    
    private static final String   SIMLEX665 = "SimLex665_dataset";
    
    /**
     * WordNet database path, corpus-based Pedersen IC files, and results
     * directory in the current PC. You should change the base HESML
     * directory according to your code installation.
     */
     
    private static final String  m_strBaseDir = "..";
    private static final String  m_strWordNetDatasetsDir = m_strBaseDir + "/WN_Datasets/";
    private static final String  m_strWordNet3_0_Dir = m_strBaseDir + "/Wordnet-3.0/dict";
    private static final String  m_strResultsDir = m_strBaseDir + "/ReproducibleExperiments/";
    private static final String  m_strCorpusFrqFilesDir = m_strBaseDir + "/PedersenICmodels/WordNet-InfoContent-3.0/";
    
    /**
     * Xml schema file describing the experiment (*.exp) file format.
     */
    
    private static final String m_SchemaFilename = "WordNetBasedExperiments.xsd";
    
    /**
     * This is the main function where we show different use examples
     * of HESML. The function processes an input XML file defining a
     * set of reproducible experiments, or call a function implementing
     * a set of examples in order to show the HESML functionality.
     * @param args the command line arguments
     * @throws java.lang.Exception
     */

    public static void main(String[] args) throws Exception
    {
        boolean   showUsage = false;  // Show usage
        
        // We print the HESML version
        
        System.out.println("Running HESMLClient V1R3 (v1.3.0.1) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        // We read the incoming parameters and load the reproducible
        // experiments defined by the user in a XML-based file.
        
        if (args.length > 0)
        {
            // We parse the input arguments in order to know the invoked command
            
            if (args[0].equals("-WNSimRepV1") && (args.length == 2))
            {
                buildWNSimRepFiles(args[1]);
            }
            else if (args[0].endsWith(".exp"))
            {
                // Running of a reproducible experiment file

                File    inputFile = new File(args[0]);  // Get the file path

                // We check the existence of the file

                if (inputFile.exists())
                {
                    // We get the start time

                    long    startFileProcessingTime = System.currentTimeMillis();

                    // Loading the experiment file

                    System.out.println("Loading and running the experiments defined in "
                            + inputFile.getName());

                    // We parse the input file in order to recover the
                    // experiments definition.

                    ReproducibleExperimentsInfo reproInfo;

                    reproInfo = new ReproducibleExperimentsInfo(inputFile, m_SchemaFilename);

                    // We execute all the experiments defined in the input file

                    reproInfo.ExecuteAllExperiments();
                    reproInfo.clear();

                    // We measure the elapsed time to run the experiments

                    long    endTime = System.currentTimeMillis();
                    long    minutes = (endTime - startFileProcessingTime) / 60000;
                    long    seconds = (endTime - startFileProcessingTime) / 1000;

                    System.out.println("Overall elapsed loading and computation time (minutes) = " + minutes);
                    System.out.println("Overall elapsed loading and computation time (seconds) = " + seconds);
                }
            }
            else
            {
                showUsage = true;
            }
        }
        else
        {
            showUsage = true;
        }
        
        // For a wrong calling to the program, we show the usage.
        
        if (showUsage)
        {
            System.err.println("\nIn order to properly use the HESMLclient program");
            System.err.println("you should call it using any of the two methods shown below:\n");
            System.err.println("(1) C:> java -jar dist\\HESMLclient.jar <reproexperiment.exp>");
            System.err.println("(2) C:> java -jar dist\\HESMLclient.jar -WNSimRepV1 <outputdir>");
        }
        
        // Finally, we show below the SampleExperiments() function which contains
        // the collection of hand-coded sample experiments that you can use
        // with the aim of learning to use HESML.

        //SampleExperiments();        
    }
    
    /**
     * Each function call within this function is invoking
     * some specific test example with the aim to show the
     * HESML functionality.
     */
    
    private static void SampleExperiments() throws Exception
    {
        // (1) AllSimilarityBenchmarks() runs the full set of
        // cross-validation benchmarks for all the IC models and measures
        // on the five most significant datasets, whose files are defined
        // by the static attributes above. The paths above match the
        // project structure.
        
        //testAllSimilarityBenchmarks();
        
        // (2) The following test function evaluates the set of selected
        // intrinsic IC models versus another set of IC-based similarity
        // measures on the five most significant word similarity
        // benchmarks.
        
        //testMultipleICmodelsMultipleICmeasuresBenchmarks();
        
        // (3) The following test evaluates a single non IC-based similarity
        // measure in the RG65 dataset. It shows the use of the automatized
        // benchmark for single non IC-based similarity measures.
        
        //testSingleNonICbasedMeasure();
        
        // (4) The following test evaluates a single IC-similarity measures
        // with multiple intrinsic IC models.
        
        //testSingleICSimMeasureMultipleICmodels();
        
        // (5) The following test evaluates a single IC-similarity measure
        // with a single intrinsic IC miodel.
        
        testSingleICSimMeasureSingleICmodel();
        
        // (6) The following test shows how to directly compute the
        // similarity between two words using two different similarity measures.
        
        //testWordPairSimilarity();
        
        // (7) the following test evaluates a single IC model with multiple
        // IC-based similarity measures
        
        //testSingleICmodelMultipleICbasedMeasures();
        
        // (8) This function runs the full collection of cross-validation
        // tests for all the corpus-based IC models. The function runs the
        // experiments on the five most significant similarity datasets.
        
        //testCorpusBasedSimilarityBenchmarks();
        
        // (9) the following test creates all the data files included in the
        // WNSimRep dataset provided as complementary material of one
        // of our papers,
        
        //testBuildWNSimRepFiles();
    }
    
    /**
     * This function runs the full collection of cross-validation
     * tests for all the corpus-based IC models. The function runs the
     * experiments on the five most significant similarity datasets.
     * 
     * The function loads WordNet and creates a HESML taxonomy to represent it.
     * The benchmarks loads the dataset files with the word pairs and
     * creates a experiment matrix to evaluate each selected
     * similarity measures and IC model.
     * @throws Exception 
     */
    
    private static void testCorpusBasedSimilarityBenchmarks() throws Exception
    {
        IWordNetDB  wordnet;            // WordNet DB
        ITaxonomy   wordnetTaxonomy;    // WordNet taxonomy
        
        // We load the WordNet database
        
        wordnet = WordNetFactory.loadWordNetDatabase(m_strWordNet3_0_Dir, "data.noun");
        
        // We build the taxonomy
        
        System.out.println("Building the WordNet taxonomy ...");
        
        wordnetTaxonomy = WordNetFactory.buildTaxonomy(wordnet);
               
        // We pre-process the taxonomy to compute all the parameters
        // used by the intrinsic IC-computation methods
        
        System.out.println("Pre-processing the WordNet taxonomy");
        
        wordnetTaxonomy.computesCachedAttributes();
  
        // We define the dataset to be evaluated
        
        String[] strDatasetNames = {RG65};//, MC28, AGIRRE203, PIRRO_SECO, SIMLEX665};

        // We define the corpus-based IC models to be evaluated. Each
        // corpus-based IC model is defined by a corpus IC model computing method
        // plus an specific Wordnet-based frequency file used to compute the
        // IC model values. Thus, in the example below, each corpus-based IC method
        // is evaluated with the two sample files 'ic-treebank-add1.dat' and
        // 'ic-bnc-resnik-add1.dat'.
        
        CorpusBasedICModelType[]    icModels = {CorpusBasedICModelType.Resnik,
                                                CorpusBasedICModelType.CondProbCorpus,
                                                CorpusBasedICModelType.CondProbRefCorpus,
                                                CorpusBasedICModelType.Resnik,
                                                CorpusBasedICModelType.CondProbCorpus,
                                                CorpusBasedICModelType.CondProbRefCorpus};
        
        String[]    strFrequencyFiles = {"ic-treebank-add1.dat",
                                        "ic-treebank-add1.dat",
                                        "ic-treebank-add1.dat",
                                        "ic-bnc-resnik-add1.dat",
                                        "ic-bnc-resnik-add1.dat",
                                        "ic-bnc-resnik-add1.dat"};
        
        // We define the similarity measures to be evaluated
        
        SimilarityMeasureType[] measures = {SimilarityMeasureType.Resnik,
                                            SimilarityMeasureType.Lin,
                                            SimilarityMeasureType.JiangConrath};
        
        // We runs the benchmarks for each dataset.
        // The IC-based similarity measures evaluated in the IC-based tests
        // are defined by the function getICmeasureTypesToEvaluate().

        for (String strDatasetName: strDatasetNames)
        {
            // We create the similarity benchmark to run the experiments
        
            ISimilarityBenchmark    benchmark;
        
            benchmark = BenchmarkFactory.getCorpusICSimilarityMeasuresTest(
                            wordnetTaxonomy, wordnet,
                            m_strWordNetDatasetsDir + strDatasetName + ".csv",
                            CorrelationOutputMetrics.PearsonAndSpearman,
                            m_strCorpusFrqFilesDir,
                            icModels, strFrequencyFiles, measures);
            
            // We execute the cross-validation benchmark for the current dataset
            
            benchmark.executeTests(m_strResultsDir + "Test_results_Corpus.csv", true);
        }
        
        // We release the resources
        
        wordnet.clear();
        wordnetTaxonomy.clear();        
    }
    
    /**
     * This function runs the full collection of cross-validation
     * tests for all the IC models and similarity measures, including
     * the corpus-based and intrinsic IC models, as well as the
     * non IC-based similarity measures. The function runs the
     * experiments on the five most significant similarity datasets.
     * 
     * The function loads WordNet and creates a HESML taxonomy to represent it.
     * The benchmarks loads the dataset files with the word pairs and
     * creates a experiment matrix to evaluate each selected
     * similarity measures and IC model.
     * @throws Exception 
     */
    
    private static void testAllSimilarityBenchmarks() throws Exception
    {
        IWordNetDB  wordnet;            // WordNet DB
        ITaxonomy   wordnetTaxonomy;    // WordNet taxonomy
        
        // We load the WordNet database
        
        wordnet = WordNetFactory.loadWordNetDatabase(m_strWordNet3_0_Dir, "data.noun");
        
        // We build the taxonomy
        
        System.out.println("Building the WordNet taxonomy ...");
        
        wordnetTaxonomy = WordNetFactory.buildTaxonomy(wordnet);
               
        // We pre-process the taxonomy to compute all the parameters
        // used by the intrinsic IC-computation methods
        
        System.out.println("Pre-processing the WordNet taxonomy");
        
        wordnetTaxonomy.computesCachedAttributes();
  
        // We define the dataset to be evaluated
        
        String[] strDatasetNames = {RG65, MC28, AGIRRE203, PIRRO_SECO, SIMLEX665};

        // We runs the benchmarks for each dataset.
        // The IC-based similarity measures evaluated in the IC-based tests
        // are defined by the function getICmeasureTypesToEvaluate().

        for (String strDatasetName: strDatasetNames)
        {
            CrossICmeasuresTest("WN3_0",
                    wordnetTaxonomy, wordnet, m_strResultsDir,
                    m_strWordNetDatasetsDir, strDatasetName);
            
            CrossCorpusICmeasuresTest(CorpusBasedICModelType.Resnik,
                    "WN3_0", wordnetTaxonomy,
                    wordnet, m_strResultsDir, m_strWordNetDatasetsDir,
                    strDatasetName, m_strCorpusFrqFilesDir);
            
            CrossCorpusICmeasuresTest(CorpusBasedICModelType.CondProbCorpus,
                    "WN3_0", wordnetTaxonomy,
                    wordnet, m_strResultsDir, m_strWordNetDatasetsDir,
                    strDatasetName, m_strCorpusFrqFilesDir);
        
            CrossNonICmeasures("WN30", wordnetTaxonomy,
                    wordnet, m_strResultsDir, m_strWordNetDatasetsDir,
                    strDatasetName);
        }
        
        // We release the resources
        
        wordnet.clear();
        wordnetTaxonomy.clear();        
    }

    /**
     * This function runs the tests for all the intrinsic IC models
     * and IC-based similarity measures. The function runs the
     * experiments on the five most significant similarity datasets.
     * 
     * The function loads WordNet and creates a HESML taxonomy to represent it.
     * The benchmarks loads the dataset files with the word pairs and
     * creates a experiment matrix to evaluate each selected
     * similarity measures and IC model.
     * @throws Exception 
     */
    
    private static void testMultipleICmodelsMultipleICmeasuresBenchmarks() throws Exception
    {
        IWordNetDB  wordnet;            // WordNet DB
        ITaxonomy   wordnetTaxonomy;    // WordNet taxonomy
        
        // We load the WordNet database
        
        wordnet = WordNetFactory.loadWordNetDatabase(m_strWordNet3_0_Dir, "data.noun");
        
        // We build the taxonomy
        
        System.out.println("Building the WordNet taxonomy ...");
        
        wordnetTaxonomy = WordNetFactory.buildTaxonomy(wordnet);
               
        // We pre-process the taxonomy to compute all the parameters
        // used by the intrinsic IC-computation methods
        
        System.out.println("Pre-processing the WordNet taxonomy");
        
        wordnetTaxonomy.computesCachedAttributes();
  
        // We define the dataset to be evaluated
        
        String[] strDatasetNames = {RG65, MC28, PIRRO_SECO, AGIRRE203, SIMLEX665};

        // We runs the benchmarks for each dataset.
        // The IC-based similarity measures evaluated in the IC-based tests
        // are defined by the function getICmeasureTypesToEvaluate().

        for (String strDatasetName: strDatasetNames)
        {
            CrossICmeasuresTest("Test_all_ICmodels",
                    wordnetTaxonomy, wordnet, m_strResultsDir,
                    m_strWordNetDatasetsDir, strDatasetName);
        }
        
        // We release the resources
        
        wordnet.clear();
        wordnetTaxonomy.clear();        
    }
    
    /**
     * This function evaluates a single intrinsic IC model with multiple
     * IC-based similarity measures.
     * @throws Exception 
     */
    
    private static void testSingleICmodelMultipleICbasedMeasures() throws Exception
    {
        IWordNetDB  wordnet;            // WordNet DB
        ITaxonomy   wordnetTaxonomy;    // WordNet taxonomy
        
        String  strOutputFile;  // Results file
        String  strDataset;     // Dataset filename
        
        Date    time;   // registro de tiempo
        
        IntrinsicICModelType[]  icModels = {IntrinsicICModelType.Sanchez2011};
        
        // We load the WordNet database
        
        wordnet = WordNetFactory.loadWordNetDatabase(m_strWordNet3_0_Dir, "data.noun");
        
        // We build the taxonomy
        
        System.out.println("Building the WordNet taxonomy ...");
        
        wordnetTaxonomy = WordNetFactory.buildTaxonomy(wordnet);
               
        // We pre-process the taxonomy to compute all the parameters
        // used by the intrinsic IC-computation methods
        
        System.out.println("Pre-processing the WordNet taxonomy");
        
        wordnetTaxonomy.computesCachedAttributes();
  
        // We define the dataset to be evaluated
        
        String[] strDatasetNames = {RG65, MC28, AGIRRE203, PIRRO_SECO, SIMLEX665};
       
        // We runs the benchmarks for each dataset.
        // The IC-based similarity measures evaluated in the IC-based tests
        // are defined by the function getICmeasureTypesToEvaluate().

        for (String strDatasetName: strDatasetNames)
        {
            // We define the filepath of the output

            time = new Date();

            strOutputFile = m_strResultsDir + icModels[0].toString() + "_" +
                            strDatasetName + "_" + time.getTime() + ".csv";

            // We build the dataset name

            strDataset = m_strWordNetDatasetsDir + strDatasetName + ".csv";

            // We execute the tests

            ISimilarityBenchmark    test;   // Single measure test

            // We get a novel test

            test = BenchmarkFactory.getMultipleICSimilarityMeasuresTest(
                    wordnetTaxonomy, wordnet, strDataset,
                    CorrelationOutputMetrics.PearsonAndSpearman,
                    icModels, getICbasedMeasuresToEvaluate());
            
            // We run the benchmarks
            
            test.executeTests(strOutputFile, false);
        }
        
        // We release the resources
        
        wordnet.clear();
        wordnetTaxonomy.clear();
    }
    
    /**
     * This function evaluates a single non IC-based similarity measure
     * on a word similarity benchmark using an automatized benchmark object.
     * You only need to load WordNet and select any benchmark. The benchmark
     * computes the Pearson correlation value.
     * @throws Exception 
     */
    
    private static void testSingleNonICbasedMeasure() throws Exception
    {
        IWordNetDB  wordnet;            // WordNet DB
        ITaxonomy   wordnetTaxonomy;    // WordNet taxonomy
        
        ISimilarityBenchmark    benchmark;          // Benchmark for a single measure
        SimilarityMeasureType   measureToEvaluate;  // Measure to be evaluated
        
        // We set the similarity measure to be evaluated
        
        measureToEvaluate = SimilarityMeasureType.Mubaid;
        
        // We load the WordNet database
        
        wordnet = WordNetFactory.loadWordNetDatabase(m_strWordNet3_0_Dir, "data.noun");
        
        // We build the taxonomy
        
        System.out.println("Building the WordNet taxonomy ...");
        
        wordnetTaxonomy = WordNetFactory.buildTaxonomy(wordnet);
               
        // We pre-process the taxonomy to compute all the parameters
        // used by the intrinsic IC-computation methods as depths,
        // and hyponym and leaf concept count. However, we do not
        // cache the ancestor set o descendant set, only integer values.
        
        System.out.println("Pre-processing the WordNet taxonomy");
        
        wordnetTaxonomy.computesCachedAttributes();
        
        // We define the dataset to be evaluated
        
        String[] strDatasetNames = {RG65, MC28, AGIRRE203, PIRRO_SECO, SIMLEX665};
        
        // We compute the benchmark and save the output Pearson correlation
        // value in the output file
        
        for (String strDataset: strDatasetNames)
        {
            // We get the benchmark for a single non IC-based similarity measure

            benchmark = BenchmarkFactory.getSingleNonICSimilarityMeasureTest(
                        wordnetTaxonomy, wordnet,
                        m_strWordNetDatasetsDir + strDataset + ".csv",
                        CorrelationOutputMetrics.PearsonAndSpearman,
                        measureToEvaluate);

            // We evaluate the benchmark
            
            benchmark.executeTests(m_strResultsDir + measureToEvaluate
                + "_" + strDataset + ".csv", true);
        }
        
        // We release the resources
        
        wordnet.clear();
        wordnetTaxonomy.clear();        
    }
    
    /**
     * This function corresponds to the cross-validation of a single
     * IC-based similarity measure with multiple intrinsic IC models
     * on a word similarity benchmark (dataset) using an
     * automatized benchmark object. The benchmark computes the Pearson
     * correlation values and save them into a csv file. 
     * @throws Exception 
     */
    
    private static void testSingleICSimMeasureMultipleICmodels() throws Exception
    {
        IWordNetDB  wordnet;            // WordNet DB
        ITaxonomy   wordnetTaxonomy;    // WordNet taxonomy
        
        ISimilarityBenchmark    benchmark;  // Benchmark for a single measure
        
        IntrinsicICModelType[]  icModels = getIntrinsicICmodelsToEvaluate();
        
        // We load the WordNet database
        
        wordnet = WordNetFactory.loadWordNetDatabase(m_strWordNet3_0_Dir, "data.noun");
        
        // We build the taxonomy
        
        System.out.println("Building the WordNet taxonomy ...");
        
        wordnetTaxonomy = WordNetFactory.buildTaxonomy(wordnet);
               
        // We pre-process the taxonomy to compute all the parameters
        // used by the intrinsic IC-computation methods
        
        System.out.println("Pre-processing the WordNet taxonomy");
        
        wordnetTaxonomy.computesCachedAttributes();
        
        // We get the benchmark for a single non IC-based similarity measure
        
        benchmark = BenchmarkFactory.getSingleICSimilarityMeasureTest(
                    wordnetTaxonomy, wordnet,
                    m_strWordNetDatasetsDir + RG65 + ".csv",
                    CorrelationOutputMetrics.PearsonAndSpearman,
                    icModels, SimilarityMeasureType.CosineNormJiangConrath);
        
        // We compute the benchmark and save the output Pearson correlation
        // value in the output file
        
        benchmark.executeTests(m_strResultsDir +
                SimilarityMeasureType.CosineNormJiangConrath + "_RG65.csv",
                true);
        
        // We release the resources
        
        wordnet.clear();
        wordnetTaxonomy.clear();        
    }
    
    /**
     * This function corresponds to the cross-validation of a single
     * IC-based similarity measure with multiple intrinsic IC models
     * on a word similarity benchmark (dataset) using an
     * automatized benchmark object. The benchmark computes the Pearson
     * correlation values and save them into a csv file. 
     * @throws Exception 
     */
    
    private static void testSingleICSimMeasureSingleICmodel() throws Exception
    {
        IWordNetDB  wordnet;            // WordNet DB
        ITaxonomy   wordnetTaxonomy;    // WordNet taxonomy
        
        ISimilarityBenchmark    benchmark;  // Benchmark for a single measure
        
        // We load the WordNet database
        
        wordnet = WordNetFactory.loadWordNetDatabase(m_strWordNet3_0_Dir, "data.noun");
        
        // We build the taxonomy
        
        System.out.println("Building the WordNet taxonomy ...");
        
        wordnetTaxonomy = WordNetFactory.buildTaxonomy(wordnet);
               
        // We pre-process the taxonomy to compute all the parameters
        // used by the intrinsic IC-computation methods
        
        System.out.println("Pre-processing the WordNet taxonomy");
        
        wordnetTaxonomy.computesCachedAttributes();
        
        // We get the benchmark for a single non IC-based similarity measure
        
        benchmark = BenchmarkFactory.getSingleICSimilarityMeasureTest(
                    wordnetTaxonomy, wordnet,
                    m_strWordNetDatasetsDir + MC28 + ".csv",
                    CorrelationOutputMetrics.PearsonAndSpearman,
                    IntrinsicICModelType.Seco,
                    SimilarityMeasureType.CosineNormWeightedJiangConrath);
        
        // We compute the benchmark and save the output Pearson correlation
        // value in the output file
        
        benchmark.executeTests(m_strResultsDir +
                "CosineNormWeightedJiangConrath_RG65.csv", true);
        
        // We release the resources
        
        wordnet.clear();
        wordnetTaxonomy.clear();        
    }

    /**
     * This function shows how to directly compute the similarity between
     * words without any automatized benchmark. We show the use of two
     * IC-based similarity model with an IC model, as well as the
     * use of two non IC-based similarity measure.
     * @throws Exception 
     */
    
    private static void testWordPairSimilarity() throws Exception
    {
        IWordNetDB  wordnet;            // WordNet DB
        ITaxonomy   wordnetTaxonomy;    // WordNet taxonomy
        
        ISimilarityBenchmark    benchmark;  // Benchmark for a single measure
        
        IVertexList word1Concepts;  // Vertexes corresponding to the
                                    // concepts evoked by the word1
                                    
        IVertexList word2Concepts;  // Vertexes corresponding to the
                                    // concepts evoked by the word2                                   

        ITaxonomyInfoConfigurator   secoICmodel;        // IC model used
        
        ISimilarityMeasure  simJiangConrath;    // Jiang-Conrath Similarity
        ISimilarityMeasure  simCosJiangConrath; // Lastra-Díaz and García-Serrano (2015)
        ISimilarityMeasure  simMubaid;          // Mubaid 2009
        ISimilarityMeasure  simSanchez2012;     // Sánchez et al (2012)
        
        double[]    simValues = new double[4];  // Similarity values
        
        // We load the WordNet database
        
        wordnet = WordNetFactory.loadWordNetDatabase(m_strWordNet3_0_Dir, "data.noun", true);
        
        // We build the taxonomy
        
        System.out.println("Building the WordNet taxonomy ...");
        
        wordnetTaxonomy = WordNetFactory.buildTaxonomy(wordnet);
               
        // We pre-process the taxonomy to compute all the parameters
        // used by the intrinsic IC-computation methods
        
        System.out.println("Pre-processing the WordNet taxonomy");
        
        wordnetTaxonomy.computesCachedAttributes();
        
        // We obtain the concepts evoked by the words "shore" and "forest"
        
        word1Concepts = wordnetTaxonomy.getVertexes().getByIds(
                            wordnet.getWordSynsetsID("shore"));

        word2Concepts = wordnetTaxonomy.getVertexes().getByIds(
                            wordnet.getWordSynsetsID("forest"));
                
        // We obtain an instance of the Seco et al. (2004) IC model,
        // then the model is computed on the taxonomy. All the IC models
        // computes and store the IC values in the own taxonomy vertexes.
        
        secoICmodel = ICModelsFactory.getIntrinsicICmodel(IntrinsicICModelType.Seco);
        
        secoICmodel.setTaxonomyData(wordnetTaxonomy);
        
        // We create all the similarity measures
        
        simJiangConrath = MeasureFactory.getMeasure(wordnetTaxonomy, SimilarityMeasureType.JiangConrath);
        simCosJiangConrath = MeasureFactory.getMeasure(wordnetTaxonomy, SimilarityMeasureType.CosineNormJiangConrath);
        simMubaid = MeasureFactory.getMeasure(wordnetTaxonomy, SimilarityMeasureType.Mubaid);
        simSanchez2012 = MeasureFactory.getMeasure(wordnetTaxonomy, SimilarityMeasureType.Sanchez2012);
        
        // We compute the four similarity values
        
        simValues[0] = simJiangConrath.getHighestPairwiseSimilarity(word1Concepts, word2Concepts);
        simValues[1] = simCosJiangConrath.getHighestPairwiseSimilarity(word1Concepts, word2Concepts);
        simValues[2] = simMubaid.getHighestPairwiseSimilarity(word1Concepts, word2Concepts);
        simValues[3] = simSanchez2012.getHighestPairwiseSimilarity(word1Concepts, word2Concepts);
        
        // We destroy all resources
        
        word1Concepts.clear();
        word2Concepts.clear();
        wordnet.clear();
        wordnetTaxonomy.clear();
    }
    
    /**
     * This function builds all the data files in the WNSimRep v1 dataset, which
     * is introduced in the following paper.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
     * WNSimRep: a framework and replication dataset for ontology-based
     * semantic similarity measures and information content models.
     * Mendeley Data v1. https://doi.org/10.17632/mpr2m8pycs.1
     */
    
    private static void buildWNSimRepFiles(
            String  strDatasetRootDir) throws Exception
    {
        IWordNetDB  wordnet;            // WordNet DB
        ITaxonomy   wordnetTaxonomy;    // Asspcoyated taxonomy
        
        IntrinsicICModelType[]  icModels;   // IC models to be exported
        
        String  strPreffix = "WNSimRep"; // Preffix for the files
        
        // We define the directories for each type of file
        
        File    rootInfo = new File(strDatasetRootDir);
        File    intrinsicICInfo = new File(strDatasetRootDir + "/Intrinsic_IC_model_files/");
        File    corpusICInfo = new File(strDatasetRootDir + "/Corpus_IC_model_files/");
        File    edgevaluedInfo = new File(strDatasetRootDir + "/Edge_valued_files/");
        File    synsetsInfo = new File(strDatasetRootDir + "/Synsets_valued_files/");
        
        // We check the existente of the directories
        
        if (!rootInfo.exists())
        {
            rootInfo.mkdir();
        }
        
        if (!intrinsicICInfo.exists())
        {
            intrinsicICInfo.mkdir();
        }
        
        if (!corpusICInfo.exists())
        {
            corpusICInfo.mkdir();
        }
        
        if (!edgevaluedInfo.exists())
        {
            edgevaluedInfo.mkdir();
        }
        
        if (!synsetsInfo.exists())
        {
            synsetsInfo.mkdir();
        }
        
        // We load the WordNet database
        
        wordnet = WordNetFactory.loadWordNetDatabase(m_strWordNet3_0_Dir, "data.noun");
        
        // We build and preprocess the taxonomy
        
        System.out.println("Building the taxonomy ...");
        wordnetTaxonomy = WordNetFactory.buildTaxonomy(wordnet);
        
        System.out.println("Pre-processing the taxonomy");
        wordnetTaxonomy.computesCachedAttributes();
        
        // We copy the words into the vertexes in order to allow
        // their use during the generation of the dataset files.
        
        CopyWordsIntoTaxonomy(wordnetTaxonomy, wordnet);
          
        // We get the collection of intrinsic IC models
        
        icModels = getIntrinsicICmodelsToEvaluate();
        
        // We computes all the node-valued data files containing
        // IC models included in the WNSimRep dataset. The first
        // function call is invoking the construction on-the-fly
        // of the set of intrinsic IC models, while the second
        // function call computes the Resnik IC models and
        // the third one the CondProbCorpus IC models.
        
        WNSimRepDatasetBuilder.buildNodeBasedIntrinsicICmodelFiles(
                icModels, strPreffix, wordnetTaxonomy,
                intrinsicICInfo.getAbsolutePath());
        
        WNSimRepDatasetBuilder.buildCorpusICmodels(CorpusBasedICModelType.Resnik, strPreffix,
                wordnetTaxonomy,
                corpusICInfo.getAbsolutePath(),
                m_strCorpusFrqFilesDir);

        WNSimRepDatasetBuilder.buildCorpusICmodels(
                CorpusBasedICModelType.CondProbCorpus, strPreffix,
                wordnetTaxonomy,
                corpusICInfo.getAbsolutePath(),
                m_strCorpusFrqFilesDir);

        WNSimRepDatasetBuilder.buildCorpusICmodels(
                CorpusBasedICModelType.CondProbRefCorpus, strPreffix,
                wordnetTaxonomy,
                corpusICInfo.getAbsolutePath(),
                m_strCorpusFrqFilesDir);
        
        // We compute the edge-valued data file for the
        // CondProbCorpus and CondProbRefCorpus IC models
        
        WNSimRepDatasetBuilder.buildEdgeBasedCondProbCorpusICmodelFiles(
                CorpusBasedICModelType.CondProbCorpus, strPreffix,
                wordnetTaxonomy,
                edgevaluedInfo.getAbsolutePath(),
                m_strCorpusFrqFilesDir);

        WNSimRepDatasetBuilder.buildEdgeBasedCondProbCorpusICmodelFiles(
                CorpusBasedICModelType.CondProbRefCorpus, strPreffix,
                wordnetTaxonomy,
                edgevaluedInfo.getAbsolutePath(),
                m_strCorpusFrqFilesDir);
        
        // We compute the edge-valued data file for the intrinsic CondProb IC models

        WNSimRepDatasetBuilder.buildEdgeInfoCondProbICmodelFiles(
                strPreffix, wordnetTaxonomy,
                edgevaluedInfo.getAbsolutePath());
        
        // We create the synset-pair-valued data files included in WNSimRep dataset
        
        WNSimRepDatasetBuilder.buildSynsetsBasedFiles(
                icModels, getICbasedMeasuresToEvaluate(),
                strPreffix, m_strWordNetDatasetsDir,
                RG65, wordnet, wordnetTaxonomy,
                synsetsInfo.getAbsolutePath());
        
        WNSimRepDatasetBuilder.buildNonICbasedMeasuresSynsetsBasedFile(
                getNonICmeasureTypesToEvaluate(), strPreffix,
                m_strWordNetDatasetsDir, RG65, wordnet, wordnetTaxonomy,
                synsetsInfo.getAbsolutePath());
        
        // We release the resources
        
        wordnet.clear();
        wordnetTaxonomy.clear();        
    }
    
    /**
     * This function copies the Synset words into the string tag field.
     * It is used by the WNSimRep functions in order to export the 
     * words evocating each synset to the data files.
     * @param taxonomy  Taxonomy
     * @param wordnet   WordNet database
     */
    
    private static void CopyWordsIntoTaxonomy(
            ITaxonomy   taxonomy,
            IWordNetDB  wordnet)
    {
        String  strUnion;   // Union of words
        
        // We copy the words
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We convertg the words of the synset
            
            strUnion = ConvertToCsvField(wordnet.getSynset(vertex.getID()).getWords());
            
            
            // We save the sequence of words associated to the synset
            
            vertex.setStringTag(strUnion);
        }
    }
    
    /**
     * This functions serialize the string fields with commas
     * @param strFields
     * @return 
     */
    
    private static String ConvertToCsvField(
        String[]    strFields)
    {
        String  strUnion = "";   // Returned value
        
        int i;  // Counter
        
        // We build the union of the fields
        
        for (i = 0; i < strFields.length; i++)
        {
            // We insert the comma as separator
            
            if (i > 0)
            {
                strUnion += ',';
            }
            
            // We ad the i-field
            
            strUnion += strFields[i];
        }

        // We retgrn the result
        
        return (strUnion);
    }
    /**
     * This function evaluates a set of intrinsic IC models and IC-based
     * similarity measures.
     * @param wordnetTaxonomy       Taxonomy
     * @param wordnet               WordNet database
     * @param strResultsDir         Out put directory
     * @param strWordnetDatasetsDir Input directory for the word similarity benchmarks
     * @param strDataSetName        Dataset file name without the *.csv extension.
     */
    
    private static void CrossICmeasuresTest(
            String      strWordnetPrefix,
            ITaxonomy   wordnetTaxonomy,
            IWordNetDB  wordnet,
            String      strResultsDir,
            String      strWordnetDatasetsDir,
            String      strDataSetName) throws Exception
    {
        String  strOutputFile;  // Filename of the output file
        
        Date    time;   // time
        
        String  strDataset; // Datatset
        
        // We compute all the IC-node methods

        SimilarityMeasureType[] evalMeasures = getICbasedMeasuresToEvaluate();
        IntrinsicICModelType[]  icModels = getIntrinsicICmodelsToEvaluate();

        // We define the filepath of the output

        time = new Date();

        strOutputFile = strResultsDir + strWordnetPrefix
            + "_" + strDataSetName + "_" + time.getTime() + ".csv";
        
        // We build the dataset name
        
        strDataset = strWordnetDatasetsDir + strDataSetName + ".csv";
        
        // We execute the tests
        
        ISimilarityBenchmark    test;   // Single measure test
        
        // We get a novel test

        test = BenchmarkFactory.getMultipleICSimilarityMeasuresTest(
                wordnetTaxonomy, wordnet, strDataset,
                CorrelationOutputMetrics.PearsonAndSpearman,
                icModels, evalMeasures);

        // Execute the test
        
        test.executeTests(strOutputFile, false);
    }
    
    /**
     * This functions returns the sequence of intrinsic IC models that will
     * be evaluated in the benchmarks.
     * @return 
     */
    
    private static IntrinsicICModelType[] getIntrinsicICmodelsToEvaluate()
    {
        IntrinsicICModelType[]    icModels; // Returned value
        
        // We create the vector of IC model types
        
        icModels = new IntrinsicICModelType[22];
        
        // We select the IC models to evaluate in the benchmarks
        
        icModels[0] = IntrinsicICModelType.Seco;
        icModels[1] = IntrinsicICModelType.Blanchard;
        icModels[2] = IntrinsicICModelType.Zhou;
        icModels[3] = IntrinsicICModelType.Sanchez2011;
        icModels[4] = IntrinsicICModelType.Sanchez2012;
        icModels[5] = IntrinsicICModelType.Meng;
        icModels[6] = IntrinsicICModelType.Yuan;
        icModels[7] = IntrinsicICModelType.HadjTaieb;
        icModels[8] = IntrinsicICModelType.Adhikari;

        // Family of well-founded intrinsic IC models
        
        icModels[9] = IntrinsicICModelType.CondProbHyponyms;
        icModels[10] = IntrinsicICModelType.CondProbUniform;
        icModels[11] = IntrinsicICModelType.CondProbLeaves;
        icModels[12] = IntrinsicICModelType.CondProbCosine;
        icModels[13] = IntrinsicICModelType.CondProbLogistic;
        
        // Family of refined well-founded intrinsic IC models
        
        icModels[14] = IntrinsicICModelType.CondProbRefHyponyms;
        icModels[15] = IntrinsicICModelType.CondProbRefUniform;
        icModels[16] = IntrinsicICModelType.CondProbRefLeaves;
        icModels[17] = IntrinsicICModelType.CondProbRefCosine;
        icModels[18] = IntrinsicICModelType.CondProbRefCosineLeaves;
        icModels[19] = IntrinsicICModelType.CondProbRefLogistic;
        icModels[20] = IntrinsicICModelType.CondProbRefLogisticLeaves;
        icModels[21] = IntrinsicICModelType.CondProbRefLeavesSubsumersRatio;
        
        // We return the result
        
        return (icModels);
    }
    
    /**
     * This function returns the measure types to be evaluated into the
     * IC-based benchmarks. You can set another measures according
     * to the experiments that you need to carry-out.
     * @return Vector of IC-based measure types.
     */
    
    private static SimilarityMeasureType[] getICbasedMeasuresToEvaluate()
    {
        SimilarityMeasureType[] evalMeasures;  // Returned value
        
        // We create the vector of measure types to be evaluated
        
        evalMeasures = new SimilarityMeasureType[13];
        
        // We set the measures to be evaluated
        
        evalMeasures[0] = SimilarityMeasureType.Resnik;
        evalMeasures[1] = SimilarityMeasureType.Lin;
        evalMeasures[2] = SimilarityMeasureType.JiangConrath;
        evalMeasures[3] = SimilarityMeasureType.PirroSeco;
        evalMeasures[4] = SimilarityMeasureType.FaITH;
        evalMeasures[5] = SimilarityMeasureType.Meng2012;
        evalMeasures[6] = SimilarityMeasureType.Garla;
        evalMeasures[7] = SimilarityMeasureType.CosineNormJiangConrath;
        evalMeasures[8] = SimilarityMeasureType.Li2003Strategy9;
        evalMeasures[9] = SimilarityMeasureType.Zhou;
        evalMeasures[10] = SimilarityMeasureType.Meng2014;
        evalMeasures[11] = SimilarityMeasureType.Gao2015Strategy3;
        evalMeasures[12] = SimilarityMeasureType.CosineNormWeightedJiangConrath;
        
        // We return the result
        
        return (evalMeasures);
    }
    
    /**
     * This function returns the measure types to be evaluated into the
     * non IC-based benchmarks. You can set another measures according
     * to the experiments that you need to carry-out.
     * @return Vector of IC-based measure types.
     */
    
    private static SimilarityMeasureType[] getNonICmeasureTypesToEvaluate()
    {
        SimilarityMeasureType[]    evalMeasures;  // Returned value
        
        // We create the vector of measure types to be evaluated
        
        evalMeasures = new SimilarityMeasureType[9];
        
        // We set the measures to be evaluated
        
        evalMeasures[0] = SimilarityMeasureType.Rada;
        evalMeasures[1] = SimilarityMeasureType.WuPalmer;
        evalMeasures[2] = SimilarityMeasureType.LeacockChodorow;
        evalMeasures[3] = SimilarityMeasureType.Li2003Strategy3;
        evalMeasures[4] = SimilarityMeasureType.Li2003Strategy4;
        evalMeasures[5] = SimilarityMeasureType.PedersenPath;
        evalMeasures[6] = SimilarityMeasureType.Sanchez2012;
        evalMeasures[7] = SimilarityMeasureType.Taieb2014;
        evalMeasures[8] = SimilarityMeasureType.Mubaid;
        
        // We return the result
        
        return (evalMeasures);
    }
    
    
    /**
     * Cross validation
     * @param wordnetTaxonomy
     * @param wordnet
     * @param strResultsDir
     * @param strWordnetDatasetsDir
     * @param strDataSetName 
     */
    
    private static void CrossCorpusICmeasuresTest(
            CorpusBasedICModelType  corpusModel,
            String                  strWordnetPrefix,
            ITaxonomy               wordnetTaxonomy,
            IWordNetDB              wordnet,
            String                  strResultsDir,
            String                  strWordnetDatasetsDir,
            String                  strDataSetName,
            String                  strICdataFilesDir) throws Exception
    {
        ISimilarityBenchmark    test;   // Single measure test
        
        String  strOutputFile;  // Filename of the output file
        
        Date    time;   // time
        
        String  strDataset; // Datatset
        
        // We compute all the IC-node methods

        SimilarityMeasureType[] evalMeasures = getICbasedMeasuresToEvaluate();
        
        String[]    icModelFiles = {"ic-treebank-add1.dat"};

        // We define the filepath of the output

        time = new Date();

        strOutputFile = strResultsDir + strWordnetPrefix
            + "_" + corpusModel.toString() + "_"
            + strDataSetName + "_" + time.getTime() + ".csv";
        
        // We build the datatset name
        
        strDataset = strWordnetDatasetsDir + strDataSetName + ".csv";
        
        // We get a novel test

        test = BenchmarkFactory.getMultipleCorpusICSimilarityMeasuresTest(
                wordnetTaxonomy, wordnet, strDataset, 
                CorrelationOutputMetrics.PearsonAndSpearman,
                icModelFiles, strICdataFilesDir, corpusModel, evalMeasures);

        // Execute the test
        
        test.executeTests(strOutputFile, true);
    }
    
    /**
     * This function runs a benchmark on a set of non IC-based measures
     * @param strWordnetPrefix Prefix for the output file.
     * @param wordnetTaxonomy Base taxonomy for the similarity measures
     * @param wordnet WordNet database
     * @param strResultsDir Directory containing the output file
     * @param strWordnetDatasetsDir Directory containing the word similarity benchmarks
     * @param strDatasetName Name of the file containing the benchmark dataset
     * @throws Exception 
     */
    
    private static void CrossNonICmeasures(
            String      strWordnetPrefix,
            ITaxonomy   wordnetTaxonomy,
            IWordNetDB  wordnet,
            String      strResultsDir,
            String      strWordnetDatasetsDir,
            String      strDatasetName) throws Exception
    {
        String  strOutputFile;  // Filename of the output file
        
        Date    time;       // time
        String  strDataset; // Datatset
        
        SimilarityMeasureType[]  measures;   // Measures to be evaluated
        
        String[]    strCorpusName = {strDatasetName};
        
        // We cretae the vector of measures
        
        measures = getNonICmeasureTypesToEvaluate();
        
        // We veluate each datatset
        
        for (String strDataSetName: strCorpusName)
        {
            // We define the filepath of the output

            time = new Date();

            strOutputFile = strResultsDir + strWordnetPrefix
                    + "_NonIC_based" + "_" + strDataSetName
                    + "_" + time.getTime() + ".csv";

            // We builds the datatset name

            strDataset = strWordnetDatasetsDir + strDataSetName + ".csv";

            // We execute the tests

            ISimilarityBenchmark    test;   // Single measure test

            // We get a novel test

            test = BenchmarkFactory.getMultipleNonICBasedMeasureTest(
                    wordnetTaxonomy, wordnet, strDataset,
                    CorrelationOutputMetrics.PearsonAndSpearman,
                    measures);

            // Execute the test

            test.executeTests(strOutputFile, true);
        }
    }
    
    /**
     * This function saves the dato to file
     * @param taxonomy 
     */
    
    private static void ExportTaxonomyInfo(
            ITaxonomy   taxonomy) throws IOException, Exception
    {
        String[][]  strData;    // Taxonomy data
        
        int i = 0;  // Counter
        
        // We creaete the matrix
        
        strData = new String[taxonomy.getVertexes().getCount()][3];
        
        // We copy the depth values
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            strData[i][0] = Long.toString(vertex.getID());
            strData[i][1] = Long.toString(vertex.getDepthMax());
            strData[i++][2] = Double.toString(vertex.getProbability());
        }
        
        // Guardamosl os daots a figero
        
        SaveCSVfile(strData, "depthWordnetTaieb.csv");
    }
    
    /**
     * This function saves the results in a CSV file
     * @param strMatrix 
     */
    
    private static void SaveCSVfile(
            String[][]  strMatrix,
            String      strOutputFile) throws IOException
    {
        FileWriter  writer;     // Output file
        
        // We open for writing the file
        
        writer = new  FileWriter(strOutputFile, false);

        // We write the matrix
        
        for (int i = 0; i < strMatrix.length; i++)
        {
            for (int j = 0; j < strMatrix[0].length; j++)
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
     * This function creates a tree-like taxonomy with the number
     * of vertexes defined by the input parameter. For each vertex
     * (node) of the tree, the function inserts a random number of
     * children between two and maximal value. The seed
     * of the generator of random numbers is always the same in
     * order to warrant the reproducibility of the results.
     * @param overallVertexes target size of the returned taxonomy
     * @return A tree-like taxonomy with the number of vertexes required
     * @throws Exception 
     */
    
    private static ITaxonomy createTestTaxonomy(
            int overallVertexes,
            int maxChildren) throws Exception
    {
        System.out.println("Creating the random taxonomy ... #vertexes = " + overallVertexes);
        
        // We create a blank taxonomy with pre-reserved memory for the vertexes

        ITaxonomy taxonomy = TaxonomyFactory.createBlankTaxonomy(overallVertexes);
        
        // We create a queue in order to store the vertexes to be expanded

        LinkedList<IVertex> pending = new LinkedList<>();

        // We create an array for a single parent.
        
        Long[] parents = new Long[1];
        
        // We create the root with Id = 0 and enqueue it in order to
        // make a top-down expansion

        IVertex root = taxonomy.addVertex((long)0, new Long[0]);

        // We enqueue the root

        pending.add(root);

        // We create a random number for the children
        
        Random  childrenRandomCount = new Random(2016);

        // We expand the tree in a top-down process

        while ((pending.size() > 0)
                && (taxonomy.getVertexes().getCount() < overallVertexes))
        {
            // We get the current vertex to expand

            parents[0] = pending.remove().getID();

            // We get the number of children for the current node.
            // The random gnerator returns a value in the range from
            // 0 to maxChildren - 2. Thus, adding 2 to the latter random
            // number we get a random variable in the range (2, maxChildren)
            
            int childrenCount = 2 + childrenRandomCount.nextInt(maxChildren - 1);
            
            // We create a random number of child nodes

            for (int i = 0; i < childrenCount; i++)
            {
                // We get the current vertexes count
                        
                long vertexCount = taxonomy.getVertexes().getCount();
                
                // We create and enqueue the vertex to be expanded
                // only if the taxonomy has not reached the target size
                
                if (vertexCount < overallVertexes)
                {
                    pending.add(taxonomy.addVertex(vertexCount, parents));
                }
            }
        }
        
        // We return the result
        
        return (taxonomy);
    }
}
