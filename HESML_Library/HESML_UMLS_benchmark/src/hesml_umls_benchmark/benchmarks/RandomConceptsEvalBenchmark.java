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
import hesml.taxonomyreaders.obo.IOboOntology;
import hesml.taxonomyreaders.obo.impl.OboFactory;
import hesml_umls_benchmark.UMLSOntologyType;
import hesml_umls_benchmark.SemanticLibraryType;
import hesml_umls_benchmark.semantclibrarywrappers.SimilarityLibraryWrapper;
import hesml_umls_benchmark.semantclibrarywrappers.UMLSSemanticLibraryWrapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import hesml_umls_benchmark.ISemanticLibrary;
import java.util.LinkedList;
import javax.xml.stream.XMLStreamException;

/**
 * This class implements a benchmark to compare the performance
 * of the UMLS similarity libraries in the evaluation of the degree
 * of similairty between randdom concept pairs in SNOMED-CT.
 * @author j.lastra
 */

class RandomConceptsEvalBenchmark extends SemanticLibraryBenchmark
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
    
    protected int[] m_nRandomSamplesPerLibrary;
    protected int   m_nRuns;
    
    /**
     * Ontology type being evaluated
     */
    
    private UMLSOntologyType  m_ontologyType;
    
    /**
     * Constructor of the random concept pairs benchmark
     * @param libraries
     * @param ontology
     * @param similarityMeasure
     * @param icModel
     * @param nRandomSamplesPerLibrary
     * @param nRuns
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @throws Exception 
     */

    RandomConceptsEvalBenchmark(
            SemanticLibraryType[]    libraries,
            UMLSOntologyType      ontology,
            SimilarityMeasureType       similarityMeasure,
            IntrinsicICModelType        icModel,
            int[]                       nRandomSamplesPerLibrary,
            int                         nRuns,
            String                      strSnomedDir,
            String                      strSnomedDBconceptFileName,
            String                      strSnomedDBRelationshipsFileName,
            String                      strSnomedDBdescriptionFileName,
            String                      strUmlsDir,
            String                      strSNOMED_CUI_mappingfilename) throws Exception
    {
        // We initialize the base class
        
        super(libraries, strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename);    
        
        // We initialize the attributes of the object
        
        m_MeasureType = similarityMeasure;
        m_icModel = icModel;
        m_ontologyType = ontology;
        m_nRandomSamplesPerLibrary = nRandomSamplesPerLibrary.clone();
        m_nRuns = nRuns;
    }
    
    /**
     * Constructor of the random concept pairs benchmark
     * @param libraries
     * @param ontology
     * @param similarityMeasure
     * @param icModel
     * @param nRandomSamplesPerLibrary
     * @param nRuns
     * @param strMeSHDir
     * @param strMeSHXmlFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @throws Exception 
     */

    RandomConceptsEvalBenchmark(
            SemanticLibraryType[]   libraries,
            UMLSOntologyType        ontology,
            SimilarityMeasureType   similarityMeasure,
            IntrinsicICModelType    icModel,
            int[]                   nRandomSamplesPerLibrary,
            int                     nRuns,
            String                  strMeSHDir,
            String                  strMeSHXmlFileName,
            String                  strUmlsDir,
            String                  strSNOMED_CUI_mappingfilename) throws Exception
    {
        // We initialize the base class
        
        super(libraries, strMeSHDir, strMeSHXmlFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename);    
        
        // We initialize the attributes of the object
        
        m_MeasureType = similarityMeasure;
        m_icModel = icModel;
        m_ontologyType = ontology;
        m_nRandomSamplesPerLibrary = nRandomSamplesPerLibrary.clone();
        m_nRuns = nRuns;
    }
    
    /**
     * Constructor to evaluate the libraries with GO ontology
     * @param libraries
     * @param similarityMeasure
     * @param icModel
     * @param nRandomSamplesPerLibrary
     * @param nRuns
     * @param strOboFilename
     * @throws Exception 
     */
    
    RandomConceptsEvalBenchmark(
            SemanticLibraryType[]   libraries,
            SimilarityMeasureType   similarityMeasure,
            IntrinsicICModelType    icModel,
            int[]                   nRandomSamplesPerLibrary,
            int                     nRuns,
            String                  strOboFilename) throws Exception
    {
        // We initialize the base class
        
        super(libraries, strOboFilename);    
        
        // We initialize the attributes of the object
        
        m_MeasureType = similarityMeasure;
        m_icModel = icModel;
        //m_ontologyType = ontology;
        m_nRandomSamplesPerLibrary = nRandomSamplesPerLibrary.clone();
        m_nRuns = nRuns;
    }
    
    /**
     * This function executes the benchmark and saves the raw results into
     * the output file.
     */
    
    @Override
    public void run(String strOutputFilename) throws Exception
    {
        // We create the output data matrix and fill the row headers
        
        String[][] strOutputDataMatrix = buildOutputDataMatrix();
        
        // We evaluate the performance of the HESML library
        
        for (int iLib = 0, j = 1; iLib < m_Libraries.length; iLib++, j += 2)
        {
            // Debugginf message
            
            System.out.println("---------------------------------");
            System.out.println("\t" + m_Libraries[iLib].getLibraryType().toString()
                    + " library: evaluating the similarity between "
                    + m_nRandomSamplesPerLibrary[iLib]
                    + " random concept pairs in " + m_nRuns + " runs");
            
            // We get the collection of random UMLS CUI pairs

            LinkedList<String[][]> conceptIdpairsPerRun = m_strOboFilename.equals("") ?
                                    getRandomCUIpairs(m_nRandomSamplesPerLibrary[iLib], m_nRuns) :
                                    getRandomGOpairs(m_nRandomSamplesPerLibrary[iLib], m_nRuns);
            
            // We load SNOMED and the resources of the library
            
            m_Libraries[iLib].loadOntology();
            
            // We evaluate the library
            
            double[] runningTimes = EvaluateLibrary(m_Libraries[iLib], conceptIdpairsPerRun, m_nRuns);
            
            // We copy the results to the raw output matrix
            
            for (int iRun = 0; iRun < runningTimes.length; iRun++)
            {
                strOutputDataMatrix[iRun + 1][0] = Integer.toString(iRun + 1);
                strOutputDataMatrix[iRun + 1][j] = Integer.toString(m_nRandomSamplesPerLibrary[iLib]);
                strOutputDataMatrix[iRun + 1][j + 1] = Double.toString(runningTimes[iRun]);
            }
            
            // We release the database and resources used by the library
            
            m_Libraries[iLib].unloadOntology();
            conceptIdpairsPerRun.clear();
        }
        
        // We write the output raw data
        
        writeCSVfile(strOutputDataMatrix, strOutputFilename);
    }
    
    /**
     * This function builds the raw output matrix and fills the row headers.
     * @return 
     */
    
    private String[][] buildOutputDataMatrix()
    {
        // We create the output data matrix and fill the row headers
        
        String[][] strOutputDataMatrix = new String[m_nRuns + 1][2 * m_Libraries.length + 1];
        
        // We fill the row headers
        
        strOutputDataMatrix[0][0] = "#run";
        
        for (int i = 0, j = 1; i < m_Libraries.length; i++, j += 2)
        {
            strOutputDataMatrix[0][j] = "#pairs";
            strOutputDataMatrix[0][j + 1] = m_Libraries[i].getLibraryType().toString()
                                            + "-" + m_MeasureType.toString();
        }
        // We return the result
        
        return (strOutputDataMatrix);
    }
    
    /**
     * This function evaluates the HESML, SML and UMS::Similarity libraries
     * onto a same ontology.
     * @param library
     * @param conceptIdPairsPerRun
     * @param nRuns
     */
    
    private double[] EvaluateLibrary(
            ISemanticLibrary        library,
            LinkedList<String[][]>  conceptIdPairsPerRun,
            int                     nRuns) throws Exception
    {
        // We initialize the output vector
        
        double[] runningTimes;
        
        // We set the similarity measure to be used. SML library does not provide
        // practical running times for the Rada measure, thus we detect this case
        // and skip its evaluation.

        if (conceptIdPairsPerRun.get(0).length > 0) library.setSimilarityMeasure(m_icModel, m_MeasureType);
        
        // We set the NaN value for the cases in which the library
        // is not evaluated for a specific measure
        
        if (conceptIdPairsPerRun.get(0).length == 0)
        {
            runningTimes = getNullRunningTimes(nRuns);
        }
        else
        {
            // We creaet hte output vector
            
            runningTimes = new double[nRuns];
            
            // We initializa the time counter
            
            double overallAccumulatedTime = 0.0;
            
            // UMLS_SIMILARITY library gets all the iterations at one time
            // The rest of the libraries execute the benchmark n times

            if (library.getLibraryType() == SemanticLibraryType.UMLS_SIMILARITY)
            {
                // We make a casting to the UMLS::Similarity library

                UMLSSemanticLibraryWrapper pedersenLib = (UMLSSemanticLibraryWrapper) library;

                // We exucte multiple times the benchmark to compute a stable running time

                for (int iRun = 0; iRun < nRuns; iRun++)
                {
                    // We get the concept pairs for the current run
                    
                    String[][] conceptPairs = conceptIdPairsPerRun.get(iRun);
                    
                    // We evaluate the similarity of a list of pairs of concepts at once.
                    // The function also returns the running times for each run
                    // similarityWithRunningTimes[similarity_i][runningTime_ị]

                    double[][] similarityWithRunningTimes = pedersenLib.getSimilaritiesAndRunningTimes(
                                                                conceptPairs, m_ontologyType);
                    
                    // We initialize the running time for this run
                    
                    runningTimes[iRun] = 0.0;
                    
                    // Calculate the accumulated time for each iteration

                    for (int i = 0; i < similarityWithRunningTimes.length; i++)
                    {
                        runningTimes[iRun] += similarityWithRunningTimes[i][1];
                    }
                    
                    // We accumulate the overall running time for all runs
                    
                    overallAccumulatedTime += runningTimes[iRun];
                }
            }
            else
            {
                // We exucte multiple times the benchmark to compute a stable running time

                for (int iRun = 0; iRun < nRuns; iRun++)
                {
                    // We get the concept pairs for the current run
                    
                    String[][] conceptPairs = conceptIdPairsPerRun.get(iRun);
                    
                    // We initializa the stopwatch

                    long startTime = System.currentTimeMillis();

                    // We evaluate the random concept pairs

                    for (int i = 0; i < conceptPairs.length; i++)
                    {
                        library.getSimilarity(conceptPairs[i][0], conceptPairs[i][1]);
                    }

                    // We compute the elapsed time in seconds

                    runningTimes[iRun] = (System.currentTimeMillis() - startTime) / 1000.0;
                    overallAccumulatedTime += runningTimes[iRun];
                }
            }
        
            // We compute the averga running time

            double averageRuntime = overallAccumulatedTime / nRuns;

            // We print the average results

            System.out.println("# UMLS concept pairs evaluated = " + conceptIdPairsPerRun.get(0).length);
            System.out.println(library.getLibraryType() + " Average time (secs) = "
                    + averageRuntime);

            System.out.println(library.getLibraryType()
                    + " Average evaluation speed (#evaluation/second) = "
                    + ((double)conceptIdPairsPerRun.get(0).length) / averageRuntime);
            
            System.out.println("Similarity measure evaluated = " + m_MeasureType.toString());
        }
        
        // We return the results
        
        return (runningTimes);
    }
    
    /**
     * This function generates a vector of random UMLS concept pairs which
     * will be used to evaluate the performance of the libraries. The CUI pairs
     * always exist in the underlying ontology being evaluated.
     * @param snomedTaxonomy
     * @param nPairs
     * @param nRuns
     * @return 
     */
    
    private LinkedList<String[][]> getRandomCUIpairs(
            int nPairs,
            int nRuns) throws FileNotFoundException, IOException, XMLStreamException 
    {
        // We define the CUI keys vector
        
        String[] strAllValidCUIs;
        
        // We get the list of CUIs mapping to SNOMED or MeSH concepts
        
        if (m_ontologyType == UMLSOntologyType.SNOMEDCT_US)
        {
            HashMap<String, HashSet<Long>> cuiToUmlsOntologyId = 
                    SimilarityLibraryWrapper.readMappingCuiToSnomedIds(
                        m_strSnomedDir, m_strSnomedDBconceptFileName,
                        m_strUmlsDir, m_strSNOMED_CUI_mappingfilename);
            
            // We copy the CUI codes with valkid SNOMED concepts
            
            strAllValidCUIs = new String[cuiToUmlsOntologyId.size()];
            
            cuiToUmlsOntologyId.keySet().toArray(strAllValidCUIs);
            cuiToUmlsOntologyId.clear();
        }
        else
        {
            HashMap<String, HashSet<String>> cuiToMeshOntology =
                    SimilarityLibraryWrapper.readMappingCuiToMeSHIds(
                        m_strMeShDir, m_strMeSHdescriptionFileName,
                        m_strUmlsDir, m_strSNOMED_CUI_mappingfilename);
                 
            // We copy the CUI codes with valid MeSH concepts
                       
            strAllValidCUIs = new String[cuiToMeshOntology.size()];
            
            cuiToMeshOntology.keySet().toArray(strAllValidCUIs);
            cuiToMeshOntology.clear();
        }

        // We create a random number
        
        Random rand = new Random(500);
        
        // We create the output list containign nRuns matrix of concept pairs
        
        LinkedList<String[][]> pairsPerRun = new LinkedList<>();
        
        // We compute the random vectors of concept pairs per run
        
        for (int iRun = 0; iRun < nRuns; iRun++)
        {
            // We create a vector of random CUI pairs which are contained in SNOMED
            
            String[][] umlsCuiPairs = new String[nPairs][2];
            
            // We save the random vectors
            
            pairsPerRun.add(umlsCuiPairs);

            // We get the number of CUI nodes mapped to the ontology

            long nConcepts = strAllValidCUIs.length;

            // We generate the ranomdon node pairs

            for (int i = 0; i < nPairs; i++)
            {
                for (int j = 0; j < 2; j++)
                {
                    int conceptIndex = (int)(rand.nextDouble() * (nConcepts - 1));

                    umlsCuiPairs[i][j] = strAllValidCUIs[conceptIndex];
                }
            }
        }
        
        // We return the output
        
        return (pairsPerRun);
    }
    
    /**
     * This function returns a collection of pairs of GO concepts
     * @param nPairs
     * @param nRuns
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws XMLStreamException 
     */
    
    private LinkedList<String[][]> getRandomGOpairs(
            int nPairs,
            int nRuns) throws FileNotFoundException, IOException, XMLStreamException, Exception 
    {
        // We load the OBO ontology
        
        IOboOntology ontology = OboFactory.loadOntology(m_strOboFilename);

        // We get a vector with all Ids
        
        String[] strAllGoIds = ontology.getConceptIds();
        
        // We creat the output list
        
        LinkedList<String[][]> pairsPerRun = new LinkedList<>();
        
        // We create a random number
        
        Random rand = new Random(500);

        // We copmpùte the vector of random concept pairs for each run
        
        for (int iRun = 0; iRun < nRuns; iRun++)
        {
            // We create a vector of random CUI pairs which are contained in SNOMED
        
            String[][] goConceptPairs = new String[nPairs][2];
            
            // We save the random pairs per run
            
            pairsPerRun.add(goConceptPairs);

            // We get the number of GO nodes mapped to the ontology

            long nConcepts = strAllGoIds.length;

            // We generate the ranomdon node pairs

            for (int i = 0; i < nPairs; i++)
            {
                for (int j = 0; j < 2; j++)
                {
                    int conceptIndex = (int)(rand.nextDouble() * (nConcepts - 1));

                    goConceptPairs[i][j] = strAllGoIds[conceptIndex];
                }
            }
        }
        
        // We return the output
        
        return (pairsPerRun);
    }
}
