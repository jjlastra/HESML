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

package hesml_umls_benchmark.benchmarks;

import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml_umls_benchmark.ISnomedSimilarityLibrary;
import hesml_umls_benchmark.SnomedBasedLibraryType;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * This class implements a benchmark to compare the performance
 * of the UMLS similarity libraries in the evaluation of the degree
 * of similairty between randdom concept pairs in SNOMED-CT.
 * @author j.lastra
 */

class RandomConceptsEvalBenchmark extends UMLSLibBenchmark
{
    /**
     * Column offset for the main attributes extratec from concept and
     * relationsship files.
     */
    
    private static final int CONCEPT_ID = 0;
    private static final int ACTIVE_ID = 2;
    
    /**
     * Setup parameters of the semantic similarity measure
     */
    
    private SimilarityMeasureType   m_MeasureType;
    private IntrinsicICModelType    m_icModel;

    /**
     * Number of random concept pairs and runs for averaging the time
     */
    
    protected int m_nSamples;
    protected int m_nRuns;

    /**
     * Constructor of the random concept pairs benchmark
     * @param libraries
     * @param similarityMeasure
     * @param icModel
     * @param nRandomSamples
     * @param nRuns
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strSNOMED_CUI_mappingfilename
     * @throws Exception 
     */

    RandomConceptsEvalBenchmark(
            SnomedBasedLibraryType[]    libraries,
            SimilarityMeasureType   similarityMeasure,
            IntrinsicICModelType    icModel,
            int                     nRandomSamples,
            int                     nRuns,
            String                  strSnomedDir,
            String                  strSnomedDBconceptFileName,
            String                  strSnomedDBRelationshipsFileName,
            String                  strSnomedDBdescriptionFileName,
            String                  strSNOMED_CUI_mappingfilename) throws Exception
    {
        // We initialize the base class
        
        super(libraries, strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName,
                strSNOMED_CUI_mappingfilename);    
        
        // We initialize the attributes of the object
        
        m_MeasureType = similarityMeasure;
        m_icModel = icModel;
        m_nSamples = nRandomSamples;
        m_nRuns = nRuns;
    }
    
    /**
     * This function executes the benchmark.
     */
    
    @Override
    public void run(String strOutputFilename) throws Exception
    {
        // set the number of runs
        
        Long[][] snomedIDpairs = getRandomNodePairs(getAllSnomedConceptsId(), m_nSamples);
        
        // We create the output data matrix and fill the row headers
        
        String[][] strOutputDataMatrix = new String[2][m_nRuns + 1];
        
        strOutputDataMatrix[0][0] = "HESML";
        strOutputDataMatrix[1][0] = "SML";
        
        // We evaluate the performance of the HESML library
        
        for (int i = 0; i < m_Libraries.length; i++)
        {
            // Debugginf message
            
            System.out.println("---------------------------------");
            System.out.println("\t" + m_Libraries[i].toString()
                    + " library: evaluating the similarity between " + m_nSamples
                    + " random concept pairs in " + m_nRuns + " runs");
            
            // We set the row header
            
            strOutputDataMatrix[i][0] = m_Libraries[i].getLibraryType().toString()
                                        + "-" + m_MeasureType.toString();
                                            
            // We load SNOMED and the resources of the library
            
            m_Libraries[i].loadSnomed();
            
            // We set the similarity measure to be used
            
            m_Libraries[i].setSimilarityMeasure(m_icModel, m_MeasureType);
            
            // We evaluate the library
            
            CopyRunningTimesToMatrix(strOutputDataMatrix,
                EvaluateLibrary(m_Libraries[i], snomedIDpairs, m_nRuns), i);
            
            // We release the database and resources used by the library
            
            m_Libraries[i].unloadSnomed();
        }

        
        // We write the output raw data
        
        WriteCSVfile(strOutputDataMatrix, strOutputFilename);
    }
    
    /**
     * This function sets the Seco et al.(2004)[1] and the Lin similarity
     * measure [2] using HESML [3] and SML [4] libraries. Then, the function
     * evaluates the Lin similarity [2] of the set of random concept pairs.
     * 
     * [1] N. Seco, T. Veale, J. Hayes,
     * An intrinsic information content metric for semantic similarity
     * in WordNet, in: R. López de Mántaras, L. Saitta (Eds.), Proceedings
     * of the 16th European Conference on Artificial Intelligence (ECAI),
     * IOS Press, Valencia, Spain, 2004: pp. 1089–1094.
     * 
     * [2] D. Lin, An information-theoretic definition of similarity,
     * in: Proceedings of the 15th International Conference on Machine
     * Learning, Madison, WI, 1998: pp. 296–304.
     * 
     * [3] J.J. Lastra-Díaz, A. García-Serrano, M. Batet, M. Fernández, F. Chirigati,
     * HESML: a scalable ontology-based semantic similarity measures library
     * with a set of reproducible experiments and a replication dataset,
     * Information Systems. 66 (2017) 97–118.
     * 
     * [4] S. Harispe, S. Ranwez, S. Janaqi, J. Montmain,
     * The semantic measures library and toolkit: fast computation of semantic
     * similarity and relatedness using biomedical ontologies,
     * Bioinformatics. 30 (2014) 740–742.
     * 
     * @param snomedPairs
     */
    
    private double[] EvaluateLibrary(
            ISnomedSimilarityLibrary    library,
            Long[][]                    snomedPairs,
            int                         nRuns) throws Exception
    {
        // We initialize the output vector
        
        double[] runningTimes = new double[nRuns];
        double accumulatedTime = 0.0;
        
        // We exucte multiple times the benchmark to compute a stable running time
        
        for (int iRun = 0; iRun < nRuns; iRun++)
        {
            // We initializa the stopwatch

            long startTime = System.currentTimeMillis();

            // We evaluate the random concept pairs

            for (int i = 0; i < snomedPairs.length; i++)
            {
                library.getSimilarity(snomedPairs[i][0], snomedPairs[i][1]);
            }

            // We compute the elapsed time in seconds

            runningTimes[iRun] = (System.currentTimeMillis() - startTime) / 1000.0;
            
            accumulatedTime += runningTimes[iRun];
        }
        
        // We compute the averga running time
        
        double averageRuntime = accumulatedTime / nRuns;
        
        // We print the average results
        
        System.out.println("# concept pairs evaluated = " + snomedPairs.length);
        System.out.println(library.getLibraryType() + " Average time (secs) = "
                + averageRuntime);
        
        System.out.println(library.getLibraryType() + " Average evaluation speed (#evaluation/second) = "
                + ((double)snomedPairs.length) / averageRuntime);
        
        // We return the results
        
        return (runningTimes);
    }
    
    /**
     * This function retrieves all the SNOMED concepts ID from the SNOMED
     * concept file.
     * @return 
     */
    
    private Long[] getAllSnomedConceptsId() throws FileNotFoundException
    {
        // We initialize the concept ID list
        
        ArrayList<Long> concepts = new ArrayList<>(360000);
        
        // We open the file for reading
        
        Scanner reader = new Scanner(new File(m_strSnomedDir + "/" + m_strSnomedDBconceptFileName));
        System.out.println("Reading SNOMED concept IDs " + m_strSnomedDBconceptFileName);
                
        // We skip the first line containing the headers.
        // We focus only on thereading of concept ID and term, because it
        // is the only information that we need. Thus, we reject
        // to read the full record for each concept.
        
        String strHeaderLine = reader.nextLine();
        
        // We read the concept lines
        
        do
        {
            // We extract the attribites of the concept

            String[] strAttributes = reader.nextLine().split("\t");

            // We get the needed attributes

            Long snomedId = Long.parseLong(strAttributes[CONCEPT_ID]);
            boolean active = strAttributes[ACTIVE_ID].equals("1");

            // We create a new concept if it is active

            if (active) concepts.add(snomedId);
            
        } while (reader.hasNextLine());
        
        // We close the database
        
        reader.close();
        
        // We create the output vector and fill it with the concept IDs
        
        Long[] conceptsId = new Long[concepts.size()];
        
        concepts.toArray(conceptsId);
        
        // Werelease the auxiliary list
        
        concepts.clear();
        
        // We return the result
        
        return (conceptsId);
    }
    
    /**
     * This function generates a vector of random SNOMED-CT concept pairs which
     * will be used to evaluate the performance of the libraeries.
     * @param snomedTaxonomy
     * @param nPairs
     * @return 
     */
    
    private Long[][] getRandomNodePairs(
            Long[]   snomedConceptIDs,
            int      nPairs) 
    {
        // We cretae the random paris
        
        Long[][] snomedPairs = new Long[nPairs][2];
        
        // We create a ranodm number
        
        Random rand = new Random(500);
        
        // We get the number of nodes in the SNOMED-CT graph
        
        long nConcepts = snomedConceptIDs.length;
        
        // We generate the ranomdon node pairs
        
        for (int i = 0; i < nPairs; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                int snomedConceptIndex = (int)(rand.nextDouble() * (nConcepts - 1));
                snomedPairs[i][j] = snomedConceptIDs[snomedConceptIndex];
            }
        }
        
        // We return the output
        
        return (snomedPairs);
    }
}
