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

import hesml.benchmarks.impl.BenchmarkFactory;
import hesml.configurators.CorpusBasedICModelType;
import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.SimilarityMeasureType;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.impl.TaxonomyFactory;
import hesml.taxonomyreaders.wordnet.IWordNetDB;

/**
 * This class creates the collection of files included in the WNSimRep
 * dataset introduced in the paper [1], which is a complementary paper
 * of [2].
 * 
 * [1] Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * WNSimRep: a framework and replication dataset for ontology-based
 * semantic similarity measures and information content models.
 * Mendeley Data v1. https://doi.org/10.17632/mpr2m8pycs.1
 * 
 * [2] Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * HESML: a scalable ontology-based semantic similarity measures
 * library with a set of reproducible experiments and a replication
 * dataset. Submitted for publication in Information Systems Journal.
 * 
 * @author j.lastra
 */

public class WNSimRepDatasetBuilder
{
    /**
     * This function creates a collection of node-valued data files containing
     * a structured representation of a set of corpus-based IC models and
     * taxonomical features as described in the paper below:
     * 
     * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
     * WNSimRep: a framework and replication dataset for ontology-based
     * semantic similarity measures and information content models.
     * Mendeley Data v1. https://doi.org/10.17632/mpr2m8pycs.1
     * 
     * We use the set of WordNet-based frequency files provided
     * by Ted Pedersen in the following repository.
     * 
     * Pedersen, T. (2008). WordNet-InfoContent-3.0.tar dataset repository
     * https://www.researchgate.net/publication/273885902_WordNet-InfoContent-3.0.tar
     * 
     * Please, read the papers [1] and [2] in order to know what papers
     * from Ted Pedersen you should cite in the case you use these
     * files for academic purposes.
     * 
     * The Pedersen dataset is made up a set of *.dat files distributed
     * with HESML software librARY in the directory BELOW:
     * ..\HESML_Library\PedersenICmodels\WordNet-InfoContent-3.0
     * 
     * @param corpusModel       Type of corpus-based IC model to be created  
     * @param strWordnetPrefix  Prefix added to all the output files
     * @param wordnetTaxonomy   Input taxonomy
     * @param strResultsDir     Output directory where the files are created.  
     * @param strICdataFilesDir Input directory containing the Pederser WN-based frequency files
     * @throws Exception        An execption is thrown whether any input file is missing.
     */
    
    public static void buildCorpusICmodels(
            CorpusBasedICModelType  corpusModel,
            String                  strWordnetPrefix,
            ITaxonomy               wordnetTaxonomy,
            String                  strResultsDir,
            String                  strICdataFilesDir) throws Exception
    {
        String  strOutputFile;  // Filename of the output file
        
        String[]    icModelFiles = getCorpusICmodelFiles();

        ITaxonomyInfoConfigurator   infoICmodel;    // IC model
        
        int i;  // Counter
        
        // We define the filepath of the output

        for (i = 0; i < icModelFiles.length; i++)
        {
            // We define the output file for the IC model
            
            strOutputFile = strResultsDir + "/" + strWordnetPrefix
                + "_ICmodel_" + corpusModel.toString()
                + "_" + icModelFiles[i] + ".csv";

            System.out.println("Building " + icModelFiles[i]);
            
            // We cretae the IC model
            
            infoICmodel = ICModelsFactory.getCorpusICmodel(corpusModel,
                            strICdataFilesDir + icModelFiles[i]);
            
            // We set the IC models values
            
            infoICmodel.setTaxonomyData(wordnetTaxonomy);
            
            // We save the IC model to CSV file
            
            System.out.println("Writing " + strOutputFile);
            
            TaxonomyFactory.saveVertexesInfoToCSV(wordnetTaxonomy, strOutputFile, true);
        }
    }
    
    /**
     * This function returns a String array with the names of the WordNet-based
     * frequency files used for building WNSimRep v1.
     * @return 
     */

    private static String[] getCorpusICmodelFiles()
    {
        String[]    icModelFiles;   // Returned value
        
        // We build the  returned array
        
        icModelFiles = new String[8];
        
        // We fill the array
        
        icModelFiles[0] = "ic-bnc-resnik-add1.dat";
        icModelFiles[1] = "ic-brown-resnik-add1.dat";
        icModelFiles[2] = "ic-semcor-add1.dat";
        icModelFiles[3] = "ic-semcorraw-add1.dat";
        icModelFiles[4] = "ic-semcorraw-resnik-add1.dat";
        icModelFiles[5] = "ic-shaks-resnink-add1.dat";
        icModelFiles[6] = "ic-treebank-add1.dat";
        icModelFiles[7] = "ic-treebank-resnik-add1.dat";
        
        // We return the result
        
        return (icModelFiles);
    }
    /**
     * This function creates a collection of edge-valued data files containing
     * a structured representation of the CondProbCorpus IC model with
     * different WordNet-based frequency files, as described in the paper below:
     * 
     * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
     * WNSimRep: a framework and replication dataset for ontology-based
     * semantic similarity measures and information content models.
     * Mendeley Data v1. https://doi.org/10.17632/mpr2m8pycs.1
     * 
     * We define the set of WordNet-based frequency files provided
     * by Ted Pedersen in the following repository.
     * 
     * Pedersen, T. (2008). WordNet-InfoContent-3.0.tar dataset repository
     * https://www.researchgate.net/publication/273885902_WordNet-InfoContent-3.0.tar
     * 
     * Please, read the HESML readme file to know what papers from Pedersen
     * you should cite in the case you use these files for academic purposes.
     * 
     * The Pedersen dataset is made up a set of *.dat files distributed
     * with HESML in the following directory:
     * ..\HESML_Library\PedersenICmodels\WordNet-InfoContent-3.0
     * 
     * @param corpusICmodel     Corpus IC model type
     * @param strWordnetPrefix  Prefix for the output files
     * @param wordnetTaxonomy   WordNet taxonomy
     * @param strResultsDir     Output directory
     * @param strICdataFilesDir Input directory that contains the Pedersen dataset
     * @throws Exception 
     */
    
    public static void buildEdgeBasedCondProbCorpusICmodelFiles(
            CorpusBasedICModelType  corpusICmodel,
            String                  strWordnetPrefix,
            ITaxonomy               wordnetTaxonomy,
            String                  strResultsDir,
            String                  strICdataFilesDir) throws Exception
    {
        String  strOutputFile;  // Filename of the output file
        
        String[]    icModelFiles = getCorpusICmodelFiles();

        ITaxonomyInfoConfigurator   infoICmodel;    // IC model
        
        int i;  // Counter
        
        // We define the filepath of the output

        for (i = 0; i < icModelFiles.length; i++)
        {
            // We define the output file for the IC model
            
            strOutputFile = strResultsDir + "/" + strWordnetPrefix
                + "_EdgeInfo_" + corpusICmodel.toString() + "_"
                + icModelFiles[i] + ".csv";

            System.out.println("Building " + icModelFiles[i]);
            
            // We cretae the IC model
            
            infoICmodel = ICModelsFactory.getCorpusICmodel(corpusICmodel,
                            strICdataFilesDir + icModelFiles[i]);
            
            // We set the IC models values
            
            infoICmodel.setTaxonomyData(wordnetTaxonomy);
            
            // We save the IC model to CSV file
            
            System.out.println("Writing " + strOutputFile);
            
            TaxonomyFactory.saveEdgesInfoToCSV(wordnetTaxonomy, strOutputFile);
        }
    }
    
    /**
     * This function creates a collection of node-valued data files containing
     * a structured representation of a set of intrinsic IC models and
     * taxonomical features as described in the paper below:
     * 
     * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
     * WNSimRep: a framework and replication dataset for ontology-based
     * semantic similarity measures and information content models.
     * Mendeley Data v1. https://doi.org/10.17632/mpr2m8pycs.1
     * 
     * @param icModels          Sequence of intrinsic IC models to be exported
     * @param strWordnetPrefix  Prefix of the output files
     * @param wordnetTaxonomy   Taxonomy
     * @param strResultsDir     Output directory
     * @throws Exception 
     */

    public static void buildNodeBasedIntrinsicICmodelFiles(
            IntrinsicICModelType[]  icModels,
            String                  strWordnetPrefix,
            ITaxonomy               wordnetTaxonomy,
            String                  strResultsDir) throws Exception
    {
        String  strOutputFile;  // Filename of the output file
        
        ITaxonomyInfoConfigurator   infoICmodel;    // IC model
        
        boolean exportProbability;  // Export the probability to file
        
        int i;  // Counter
        
        // We define the filepath of the output

        for (i = 0; i < icModels.length; i++)
        {
            // We define the output file for the IC model
            
            strOutputFile = strResultsDir + "/" + strWordnetPrefix
                + "_ICmodel_" + icModels[i].toString() + ".csv";

            System.out.println("Building " + icModels[i]);
            
            // We cretae the IC model
            
            infoICmodel = ICModelsFactory.getIntrinsicICmodel(icModels[i]);
            
            // We set the IC models values
            
            infoICmodel.setTaxonomyData(wordnetTaxonomy);
            
            // We export the node-valued probability for the CondProbModels
            
            exportProbability = icModels[i].toString().startsWith("CondProb");
            
            // We save the IC model to CSV file
            
            System.out.println("Writing " + strOutputFile);
            
            TaxonomyFactory.saveVertexesInfoToCSV(wordnetTaxonomy,
                    strOutputFile, exportProbability);
        }
    }

    /**
     * This function creates a collection of synset-valued data files containing
     * the evaluation of a set of measures with the input IC models on
     * the input dataset. The output file contains all the taxonomical
     * features and similarity values for each synset pair
     * evoked by the word pairs in the input dataset, as described in the
     * paper below:
     * 
     * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
     * WNSimRep: a framework and replication dataset for ontology-based
     * semantic similarity measures and information content models.
     * Mendeley Data v1. https://doi.org/10.17632/mpr2m8pycs.1
     * 
     * The paper above is a companion paper and dataset of the HEMSL paper below.
     * 
     * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
     * HESML: a scalable ontology-based semantic similarity measures
     * library with a set of reproducible experiments and a replication
     * dataset. Submitted for publication in Information Systems Journal.
     * 
     * @param icModels              Sequence of IC models to be exported
     * @param measureTypes          Measures to be evaluated
     * @param strWordnetPrefix      Prefix added to the output files
     * @param strWordnetDatasetDir  Input directory for the word similarity benchmarks
     * @param strDatasetFile        Full name of the benchmark file
     * @param wordnet               WordNet database
     * @param wordnetTaxonomy       Taxonomy
     * @param strResultsDir         Output directory
     * @throws Exception 
     */
    
    public static void buildSynsetsBasedFiles(
            IntrinsicICModelType[] icModels,
            SimilarityMeasureType[] measureTypes,
            String                  strWordnetPrefix,
            String                  strWordnetDatasetDir,
            String                  strDatasetFile,
            IWordNetDB              wordnet,
            ITaxonomy               wordnetTaxonomy,
            String                  strResultsDir) throws Exception
    {
        String  strOutputFile;  // Filename of the output file
        
        ITaxonomyInfoConfigurator   infoICmodel;    // IC model
        
        int i;  // Counter
        
        // We define the filepath of the output

        for (i = 0; i < icModels.length; i++)
        {
            // We define the output file for the IC model
            
            strOutputFile = strResultsDir + "/" + strWordnetPrefix
                + "_SynsetPairs_" + icModels[i].toString() + ".csv";

            System.out.println("Building " + icModels[i]);
            
            // We cretae the IC model
            
            infoICmodel = ICModelsFactory.getIntrinsicICmodel(icModels[i]);
            
            // We set the IC models values
            
            infoICmodel.setTaxonomyData(wordnetTaxonomy);

            // We compute the synset data
            
            BenchmarkFactory.buildSynsetsDataMatrixEvaluation(
                    true, wordnet, wordnetTaxonomy,
                    strWordnetDatasetDir + strDatasetFile + ".csv",
                    strOutputFile, measureTypes);
        }
    }

    /**
     * This function creates a collection of synset-valued data files containing
     * the evaluation of a set of non IC-based similarity measures on
     * the input dataset. The output file contains all the taxonomical
     * features and similarity values for each synset pair
     * evokated by the word pairs in the input dataset, as described in the
     * paper below:
     * 
     * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
     * WNSimRep: a framework and replication dataset for ontology-based
     * semantic similarity measures and information content models.
     * Mendeley Data v1. https://doi.org/10.17632/mpr2m8pycs.1
     * 
     * @param measureTypes          Non IC-based similarity measures to be evaluated
     * @param strWordnetPreffix      Prefix added to the output files
     * @param strWordnetDatasetDir  Input directory for the word similarity benchmarks
     * @param strDatasetFile        Full name of the benchmark file
     * @param wordnet               WordNet database
     * @param wordnetTaxonomy       Taxonomy
     * @param strResultsDir         Output directory
     * @throws Exception 
     */
    
    public static void buildNonICbasedMeasuresSynsetsBasedFile(
            SimilarityMeasureType[] measureTypes,
            String                  strWordnetPreffix,
            String                  strWordnetDatasetDir,
            String                  strDatasetFile,
            IWordNetDB              wordnet,
            ITaxonomy               wordnetTaxonomy,
            String                  strResultsDir) throws Exception
    {
        String  strOutputFile;  // Filename of the output file
        
        // We define the output file for the IC model

        strOutputFile = strResultsDir + "/" + strWordnetPreffix
            + "_SynsetPairs_nonIC_based_measures.csv";

        System.out.println("Building " + strOutputFile);

        // We compute the synset data

        BenchmarkFactory.buildSynsetsDataMatrixEvaluation(
                false, wordnet, wordnetTaxonomy,
                strWordnetDatasetDir + strDatasetFile + ".csv",
                strOutputFile, measureTypes);
    }
    
    /**
     * This function creates the edge-based data file for the well-founded
     * CondProb IC models.
     * @param strWordnetPrefix  Prefix for the output files
     * @param wordnetTaxonomy   Taxonomy
     * @param strResultsDir     Output resulting path.
     * @throws Exception 
     */
    
    public static void buildEdgeInfoCondProbICmodelFiles(
            String      strWordnetPrefix,
            ITaxonomy   wordnetTaxonomy,
            String      strResultsDir) throws Exception
    {
        String  strOutputFile;  // Filename of the output file
        
        IntrinsicICModelType[]    icModels = {IntrinsicICModelType.CondProbHyponyms,
                                            IntrinsicICModelType.CondProbUniform,
                                            IntrinsicICModelType.CondProbLeaves,
                                            IntrinsicICModelType.CondProbLogistic,
                                            IntrinsicICModelType.CondProbCosine,
                                            IntrinsicICModelType.CondProbRefHyponyms,
                                            IntrinsicICModelType.CondProbRefUniform,
                                            IntrinsicICModelType.CondProbRefLeaves,
                                            IntrinsicICModelType.CondProbRefCosine,
                                            IntrinsicICModelType.CondProbRefLogistic,
                                            IntrinsicICModelType.CondProbRefCosineLeaves,
                                            IntrinsicICModelType.CondProbRefLogisticLeaves,
                                            IntrinsicICModelType.CondProbRefLeavesSubsumersRatio};
        
        ITaxonomyInfoConfigurator   infoICmodel;    // IC model
        
        int i;  // Counter
        
        // We define the filepath of the output

        for (i = 0; i < icModels.length; i++)
        {
            // We define the output file for the IC model
            
            strOutputFile = strResultsDir + "/" + strWordnetPrefix
                + "_EdgeInfo_" + icModels[i].toString() + ".csv";

            System.out.println("Building " + icModels[i]);
            
            // We cretae the IC model
            
            infoICmodel = ICModelsFactory.getIntrinsicICmodel(icModels[i]);
            
            // We set the IC models values
            
            infoICmodel.setTaxonomyData(wordnetTaxonomy);
            
            // We save the IC model to CSV file
            
            System.out.println("Writing " + strOutputFile);
            
            TaxonomyFactory.saveEdgesInfoToCSV(wordnetTaxonomy, strOutputFile);
        }
    }
}
