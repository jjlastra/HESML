/*
 * Copyright (C) 2016-2020 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml_umls_benchmark;

import hesml.HESMLversion;
import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml_umls_benchmark.benchmarks.UMLSBenchmarkFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;  
import java.util.HashSet;

/**
 * This class implements the benchmark application used to compare
 * the performance of th HESML-UMLS library with the UMLS::Similairty [2]
 * and SML [3] Libraries.
 * 
 * [1] HESML-UMLS paper.
 * 
 * [2] B.T. McInnes, T. Pedersen, S.V.S. Pakhomov,
 * UMLS-Interface and UMLS-Similarity : open source software for measuring
 * paths and semantic similarity, in: Proc. of the Annual Symposium of the
 * American Medical Informatics Association, ncbi.nlm.nih.gov,
 * San Francisco, CA, 2009: pp. 431–435.
 * 
 * [3] S. Harispe, S. Ranwez, S. Janaqi, J. Montmain, The semantic measures
 * library and toolkit: fast computation of semantic similarity and relatedness
 * using biomedical ontologies, Bioinformatics. 30 (2014) 740–742.
 * 
 * @author j.lastra
 */

public class HESML_UMLS_benchmark
{
    /**
     * Filenames and directories of the SNOMD-CT files and UMLS CUI file
     */

    private static final String m_strMeSHdir = "../UMLS/MeSH_Nov2019";
    private static final String m_strMeSHXmlFilename = "desc2019.xml";
    private static final String m_strUMLSdir = "../UMLS/UMLS2019AB";
    private static final String m_strSnomedDir = "../UMLS/SNOMED_Nov2019";
    private static final String m_strSNOMED_conceptFilename = "sct2_Concept_Snapshot_US1000124_20190901.txt";
    private static final String m_strSNOMED_relationshipsFilename = "sct2_Relationship_Snapshot_US1000124_20190901.txt";
    private static final String m_strSNOMED_descriptionFilename = "sct2_Description_Snapshot-en_US1000124_20190901.txt";
    private static final String m_strUmlsCuiMappingFilename = "MRCONSO.RRF";
    private static final String m_strMedSTSfilename = "../SentenceSimDatasets/MedStsFullNormalized.tsv";
    private static final String m_strUMNSRSfilename = "../UMLS_Datasets/UMNSRS_similarity.csv";
    
    /**
     * Main function. This fucntion executes all experiments reported in
     * the HEMSL-UMLS introductory paper [1].
     * 
     * [1] J.J. Lastra-Díaz, A. Lara-Clares, A. García-Serrano,
     * HESML: an efficient semantic measures library for the biomedical
     * domain with a reproducible benchmark, Submitted for Publication. (2020).
     * S
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    
    public static void main(String[] args) throws Exception
    {
        // We print the HESML version
        
        System.out.println("Running HESML_UMLS_benchmark, May 2020) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        // User message
        
        System.out.println("\n---------------------------------------------");
        System.out.println("This program reproduces the experiments reported in the paper below:");
        System.out.println("\tJ.J. Lastra-Díaz, A. Lara-Clares, A. García-Serrano,");
        System.out.println("\tHESML: a real-time semantic measures library for the biomedical domain with a reproducible survey,");
        System.out.println("\tSubmitted for Publication. (2020).");
        
        // We initialize the input parameters
        
        String strOutputDir = ".";
        
        // We check the input arguments
        
        boolean errorMessage = (args.length > 1);

        if (!errorMessage && (args.length == 21))
        {
            strOutputDir = args[1];
            errorMessage = !Files.exists(Paths.get(strOutputDir));
            if (errorMessage) System.out.println(strOutputDir + " directory does not exist");
        }
        
        // We exit showing the error message
        
        if (errorMessage)
        {
            System.out.println("\nCall this program as detailed below:\n");
            System.out.println("HESML_UMLS_benchmark [outputdir]");
            System.exit(0);
        }
        
        // We show the input arguments
        
        System.out.println("\nOutput directory for raw experimental data = \"" + strOutputDir + "\"");
        System.out.println("---------------------------------------------\n");
        
        // We check if the UMLS database is correctly installed.
        
        testDbConnection();
        
        // We intialize the stopwatch
        
        long startTime = System.currentTimeMillis();
        
        /**
         * Experiment 1: we compare the performance of the `HESML, SML and
         * UMLS::Similarity by evaluating the similarity of a sequence
         * of randomly generated UMLS concept pairs using the SNOMED-CT US ontology.
         */
        
        //RunRandomConceptsExperiment(strOutputDir, UMLSOntologyType.SNOMEDCT_US);

        /**
         * Experiment 2: we compare the performance of the HEMSL, SML and
         * UMLS::Similarity by evaluating the similarity of a sequence
         * of randomly generated UMLS concept pairs using the MeSH ontology.
         */
        
        //RunRandomConceptsExperiment(strOutputDir, UMLSOntologyType.MeSH);

        /**
         * Experiment 3: we compare the performance of the HEMSL, SML and
         * UMLS::Similarity by evaluating the MedSTS sentence similarity
         * dataset.
         */
        
        RunSentenceSimilarityExperiment(strOutputDir, m_strMedSTSfilename);
        
        /**
         * Experiment 4: we evaluate the approximation quality of the novel
         * Ancestor-based Shortest Path Length (AncSPL) algorithm in the
         * weighted-edge case by comparing the similarity scores returned
         * by the coswJ&C [1] similarity measure using either exact Dijkstra
         * shortest-path method or the new AncSPL approximated shortest-path
         * method on the edge-weighted SNOMED taxonomy.
         * The coswJ&C [1] similarity measure requires the computation
         * of the shortest-path length on an IC-based weigthed taxonomy. Thus,
         * this measure allows to evaluatethe new AnxSPL method on a weighted
         * taxonomy.
         * 
         * [1] J.J. Lastra-Díaz, A. García-Serrano, A novel family of IC-based
         * similarity measures with a detailed experimental survey on WordNet,
         * Engineering Applications of Artificial Intelligence Journal. 46 (2015) 140–153.
         */
        
        /*IUMLSBenchmark weightedBasedBenchmark = UMLSBenchmarkFactory.createAncSPLBenchmark(
                                                IntrinsicICModelType.Seco,
                                                SimilarityMeasureType.LeacockChodorow,
                                                SimilarityMeasureType.AncSPLLeacockChodorow,
                                                30, true, m_strSnomedDir, m_strSNOMED_conceptFilename,
                                                m_strSNOMED_relationshipsFilename,
                                                m_strSNOMED_descriptionFilename,
                                                m_strUMLSdir, m_strUmlsCuiMappingFilename);
        
        weightedBasedBenchmark.run("raw_output_AncSPLLeacockChodorow_quality_exp.csv");
        weightedBasedBenchmark.clear();*/
        
        
        // We show the overalll running time
        
        long stoptime = System.currentTimeMillis();
        
        System.out.println("Overall running time (secons) = "
            + ((stoptime - startTime) / 1000.0));
    }

    /**
     * This funtion returns the number of random samples used to evaluate
     * a library on a specific ontology a similairty measure wit the aim
     * of setting reasonable running times. It is needed becasue the large
     * difference in performance of the libraries being evañuated.
     * @param library
     * @param measureType
     * @param ontology
     * @return 
     */
    
    private static int getRandonSamplesCountPerLibrary(
            SemanticLibraryType     library,
            SimilarityMeasureType   measureType,
            UMLSOntologyType        ontology)
    {
        // We initialize the output
        
        int randomSamples = 100;
        
        HashSet<SimilarityMeasureType> pathMeasures = new HashSet<>();
        
        pathMeasures.add(SimilarityMeasureType.Rada);
                        
        // We set the value according to the library
        
        switch (library)
        {
            case HESML:
                
                randomSamples = ((ontology == UMLSOntologyType.SNOMEDCT_US)
                                && pathMeasures.contains(measureType)) ?
                                10 : 1000000;
                
                break;
                
            case SML:
                
                if (measureType == SimilarityMeasureType.WuPalmerFast)
                {
                    randomSamples = 0;
                }
                else if (!pathMeasures.contains(measureType))
                {
                    randomSamples = 1000000;
                }
                else
                {
                    randomSamples = (ontology == UMLSOntologyType.MeSH) ? 10 : 0;
                }
                
                break;
                
            case UMLS_SIMILARITY:
                
                randomSamples = (ontology == UMLSOntologyType.MeSH) ? 100 : 10;
                
                break;
        }
        
        // We return the result
        
        return (randomSamples);
    }
    
    /**
     * This function executes the benchamrk which evaluates the similarity fo
     * a random sequence of concept pairs.
     * @param strRawOutputDir
     * @param ontologyType
     * @throws Exception 
     */
    
    private static void RunRandomConceptsExperiment(
            String            strRawOutputDir, 
            UMLSOntologyType  ontologyType) throws Exception
    {
        /**
         * We set the vector of libraries to be compared
         */
        
        SemanticLibraryType[] libraries = new SemanticLibraryType[]{
                                                    SemanticLibraryType.HESML,
                                                    SemanticLibraryType.SML};
                                                    //SnomedBasedLibraryType.UMLS_SIMILARITY};

        // We set the measures being evaluated
                                                    
        SimilarityMeasureType[] measureTypes = new SimilarityMeasureType[]{
                                                    SimilarityMeasureType.Rada,
                                                    SimilarityMeasureType.Lin,
                                                    SimilarityMeasureType.WuPalmerFast};
                
        // We build the vector of raw output filenames
        
        String[] strOutputFilenames = new String[measureTypes.length];
        
        for (int i = 0; i < strOutputFilenames.length; i++)
        {
            strOutputFilenames[i] = "raw_output_" + measureTypes[i] + "_" + ontologyType + ".csv";
        }

        // We create the running-time vector

        int[] nRandomSamplesPerLibrary = new int[libraries.length];
        
        /**
         * We compare the performance of HESML, SML and UMLS::Similarity by evaluating
         * different similarity measures on a random sequence of concept pairs.
         */
        
        int nRuns = 5;
        
        for (int i = 0; i < measureTypes.length; i++)
        {
            /**
             * We set the number of random concept pairs evaluated by each library
             * with the aim of computing the average running times. Because of the
             * running times could span different orders of magnitude the number
             * of concept pairs need to be different to provide reasonable
             * experimentation times.
             */

            for (int j = 0; j < libraries.length; j++)
            {
                nRandomSamplesPerLibrary[j] = getRandonSamplesCountPerLibrary(libraries[j],
                                                measureTypes[i], ontologyType);
            }
        
            // We set the benchmark
            
            IUMLSBenchmark benchmark = null;
            
            // We build the benchmark according tor the underlying ontology
            
            switch (ontologyType)
            {
                case SNOMEDCT_US:
                
                    benchmark = UMLSBenchmarkFactory.createSnomedConceptBenchmark(
                                    libraries, ontologyType, measureTypes[i],
                                    IntrinsicICModelType.Seco, nRandomSamplesPerLibrary,
                                    nRuns, m_strSnomedDir, m_strSNOMED_conceptFilename,
                                    m_strSNOMED_relationshipsFilename,
                                    m_strSNOMED_descriptionFilename,
                                    m_strUMLSdir, m_strUmlsCuiMappingFilename);
                    
                    break;
                    
                case MeSH:
                    
                    benchmark = UMLSBenchmarkFactory.createMeSHConceptBenchmark(
                                    libraries, ontologyType, measureTypes[i],
                                    IntrinsicICModelType.Seco, nRandomSamplesPerLibrary,
                                    nRuns, m_strMeSHdir, m_strMeSHXmlFilename,
                                    m_strUMLSdir, m_strUmlsCuiMappingFilename);
                    
                    break;
            }
        
            // We run and destroy the benchmark
            
            benchmark.run(strRawOutputDir + "/" + strOutputFilenames[i]);
            benchmark.clear();
        }
    }
    
    /**
     * This function executes the benchamrk which evaluates the similarity of
     * the sentence pairs in the MedSTS dataset.
     * @param strRawOutputDir
     * @param ontologyType
     * @throws Exception 
     */
    
    private static void RunSentenceSimilarityExperiment(
            String  strRawOutputDir,
            String  strMedSTSfilename) throws Exception
    {
        /**
         * We set the vector of libraries to be compared
         */
        
        SemanticLibraryType[] libraries = new SemanticLibraryType[]{
                                                    SemanticLibraryType.HESML};//,
                                                    //SemanticLibraryType.SML};
                                                    //SemanticLibraryType.UMLS_SIMILARITY};

        // We set the measures being evaluated
                                                    
        SimilarityMeasureType[] measureTypes = new SimilarityMeasureType[]{
                                                    SimilarityMeasureType.Rada,
                                                    SimilarityMeasureType.Lin,
                                                    SimilarityMeasureType.WuPalmerFast};
                
        // We build the vector of raw output filenames
        
        String[] strOutputFilenames = new String[measureTypes.length];
        
        for (int i = 0; i < strOutputFilenames.length; i++)
        {
            strOutputFilenames[i] = "raw_output_" + measureTypes[i] + "_MedSTS.csv";
        }
        
        /**
         * Experiment 3: we compare the performance of the HEMSL, SML and
         * UMLS::Similarity libraries by evaluating the MedSTS dataset [|].
         * [1] Wang, Yanshan, Naveed Afzal, Sunyang Fu, Liwei Wang, 
         * Feichen Shen, Majid Rastegar-Mojarad, and Hongfang Liu. 2018. 
         * “MedSTS: A Resource for Clinical Semantic Textual Similarity.” 
         * Language Resources and Evaluation, October.
         */
        
        for (int i = 0; i < measureTypes.length; i++)
        {
            IUMLSBenchmark sentenceBenchmark = UMLSBenchmarkFactory.createMeSHSentenceBenchmark(
                                        libraries, measureTypes[i],
                                        IntrinsicICModelType.Seco, strMedSTSfilename, 
                                        m_strMeSHdir, m_strMeSHXmlFilename,
                                        m_strUMLSdir, m_strUmlsCuiMappingFilename);

            sentenceBenchmark.run(strRawOutputDir + "/" + strOutputFilenames[i]);
            sentenceBenchmark.clear();
        }
    }
    
    /**
     * Function for testing if the UMLS database is correctly installed.
     * 
     * This function check which sources (vocabularies) are indexed in the UMLS database.
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    
    public static void testDbConnection() throws ClassNotFoundException, SQLException
    {
        try{
            System.out.println("Initializing UMLS database test...");
            
            System.out.println("Checking MySQL driver for Java...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Ok");
            
            System.out.println("Checking MySQL connection...");
            Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/umls","root","");  
            System.out.println("Ok");

            /*System.out.println("Checking MySQL connection...");
            Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/umls","xrdpuser","root");  
            System.out.println("Ok");*/
            
            System.out.println("Execute testing query...");
            Statement stmt=con.createStatement();  
            ResultSet rs=stmt.executeQuery("select distinct(sab) from MRREL;");  
            System.out.println("Ok");
            
            System.out.println("List of available vocabularies:");
            while(rs.next())  
                System.out.println("   " + rs.getString(1));  
            
            // Closing connection
            
            con.close();  
        } 
        catch(ClassNotFoundException | SQLException e)
        { 
            System.out.println(e);
        }  
        
    }
}


