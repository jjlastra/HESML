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

    private static final String m_strUMLSdir = "../UMLS/UMLS2019AB";
    private static final String m_strSnomedDir = "../UMLS/SNOMED_Nov2019";
    private static final String m_strSNOMED_conceptFilename = "sct2_Concept_Snapshot_US1000124_20190901.txt";
    private static final String m_strSNOMED_relationshipsFilename = "sct2_Relationship_Snapshot_US1000124_20190901.txt";
    private static final String m_strSNOMED_descriptionFilename = "sct2_Description_Snapshot-en_US1000124_20190901.txt";
    private static final String m_strUmlsCuiMappingFilename = "MRCONSO.RRF";
    
    /**
     * Main function. This fucntion executes all experiments reported in
     * the HEMSL-UMLS introductory paper [1].
     * 
     * [1] J.J. Lastra-Díaz, A. Lara-Clares, A. García-Serrano,
     * HESML: an efficient semantic measures library for the biomedical
     * domain with a reproducible benchmark, Submitted for Publication. (2020).
     * S
     * @param args the command line arguments
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
        System.out.println("This program reproduces the experiments reported in the paperbelow when");
        System.out.println("it is called with the 'MiniMayoSRS_physicians.csv' file as first argument.\n");
        System.out.println("\tJ.J. Lastra-Díaz, A. Lara-Clares, A. García-Serrano,");
        System.out.println("\tHESML: an efficient semantic measures library for the biomedical domain,");
        System.out.println("\tSubmitted for Publication. (2020).");
        
        // We initialize the input paraemters
        
        String strOutputDir = "./";
        String strConceptBiomedicalDataset = "";
        
        // We check the input arguments
        
        boolean errorMessage = (args.length < 1) || (args.length > 2);

        if (!errorMessage)
        {
            strConceptBiomedicalDataset = args[0];
            errorMessage = !Files.exists(Paths.get(strConceptBiomedicalDataset));
            if (errorMessage) System.out.println(strConceptBiomedicalDataset + " file does not exist");
        }
        
        if (!errorMessage && (args.length == 2))
        {
            strOutputDir = args[1];
            errorMessage = !Files.exists(Paths.get(strOutputDir));
            if (errorMessage) System.out.println(strOutputDir + " directory does not exist");
        }
        
        // We exit showing the error message
        
        if (errorMessage)
        {
            System.out.println("\nCall this program as detailed below:\n");
            System.out.println("HESML_UMLS_benchmark <CUI_pairs_file.csv> [outputdir]");
            System.exit(0);
        }
        
        // We shopw the input arguments
        
        System.out.println("\nInput biomedical concept similarity dataset = " + strConceptBiomedicalDataset);
        System.out.println("Output director for raw experimental data = " + strOutputDir);
        System.out.println("---------------------------------------------\n");
        
        // We check if the UMLS database is correctly installed.
        
        testDbConnection();
        
        // We intialize the stopwatch
        
        long startTime = System.currentTimeMillis();
        
        /**
         * Experiment 1: we compare the performance of the HEMSL, SML and
         * UMLS::Similarity by evaluating the similarity of a sequence
         * of randomly generated UMLS concept pairs using the SNOMED-CT US ontology.
         */
        
        RunRandomConceptsExperiment(strOutputDir, BiomedicalOntologyType.SNOMED_CT);

        /**
         * Experiment 2: we compare the performance of the HEMSL, SML and
         * UMLS::Similarity by evaluating the similarity of a sequence
         * of randomly generated UMLS concept pairs using the MeSH ontology.
         */
        
        //RunExperiment(strOutputDir, BiomedicalOntologyType.SNOMED_CT);

        /**
         * Experiment 3: we compare the performance of the HEMSL, SML and
         * UMLS::Similarity os a sequence of randomly generated UMLS
         * concept pairs on MeSH ontology.
         */
        
        //RunExperiment(strOutputDir, BiomedicalOntologyType.SNOMED_CT);
        
        /**
         * Experiment 2.2: we evaluate the approximation quality of the novel
         * Ancestor-based Shortest Path Length (AncSPL) algorithm in the
         * weighted-edge case by comparing the similarity scores returned
         * by the coswJ&C [1] similarity measure using either exact Dijkstra
         * shortest-path method ir the new AncSPL approximated shortest-path
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
     * This function executes the benchamrk which evaluates the similarity fo
     * a random sequence of concept pairs.
     * @param strRawOutputDir
     * @param ontologyType
     * @throws Exception 
     */
    
    private static void RunRandomConceptsExperiment(
            String                  strRawOutputDir, 
            BiomedicalOntologyType  ontologyType) throws Exception
    {
        /**
         * We set the vector of libraries to be compared
         */
        
        SnomedBasedLibraryType[] libraries = new SnomedBasedLibraryType[]{
                                                    SnomedBasedLibraryType.HESML,
                                                    SnomedBasedLibraryType.SML};//,
                                                    //SnomedBasedLibraryType.UMLS_SIMILARITY};

        // We set the measures being evaluated
                                                    
        SimilarityMeasureType[] measureTypes = new SimilarityMeasureType[]{
                                                    SimilarityMeasureType.Rada,
                                                    SimilarityMeasureType.AncSPLRada,
                                                    SimilarityMeasureType.Lin,
                                                    SimilarityMeasureType.LeacockChodorow,
                                                    SimilarityMeasureType.WuPalmerFast};
                
        /**
         * Output filenames.
         */
           
        String[] strOutputFilenames = new String[]{"raw_output_Rada_SNOMED_exp1.csv",
                                            "raw_output_AncSPL-Rada_SNOMED_exp1.csv",
                                            "raw_output_Lin-Seco_SNOMED_exp1.csv",
                                            "raw_output_Leacock_eSNOMED_xp1.csv",
                                            "raw_output_Wu-Palmer_SNOMED_exp1.csv"};
        
        /**
         * We set the number of random concept pairs evaluated by each library
         * with the aim of computing the average running times. Because of the
         * running times could span different orders of magnitude the number
         * of concept pairs need to be different to provide reasonable
         * experimentation times.
         */

         int[] nRandomSamplesPerLibrary = new int[]{1000000, 1000000, 10};
        
        /**
         * We compare the performance of HESML, SML and UMLS::Similarity by evaluating
         * different similarity measures on a random sequence of concept pairs.
         */
        
        int nRuns = 1;
        
        for (int i = 0; i < measureTypes.length; i++)
        {
            IUMLSBenchmark icBasedBenchmark = UMLSBenchmarkFactory.createConceptBenchmark(
                                            libraries, SimilarityMeasureType.Lin,
                                            IntrinsicICModelType.Seco, nRandomSamplesPerLibrary,
                                            nRuns, m_strSnomedDir, m_strSNOMED_conceptFilename,
                                            m_strSNOMED_relationshipsFilename,
                                            m_strSNOMED_descriptionFilename,
                                            m_strUMLSdir, m_strUmlsCuiMappingFilename);
        
            icBasedBenchmark.run(strRawOutputDir + "/" + strOutputFilenames[i]);
            icBasedBenchmark.clear();
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

