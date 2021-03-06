/*
 * Copyright (C) 2016-2021 Universidad Nacional de Educación a Distancia (UNED)
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
import hesml.measures.GroupwiseMetricType;
import hesml.measures.GroupwiseSimilarityMeasureType;
import hesml.measures.SimilarityMeasureType;
import hesml_umls_benchmark.UMLSOntologyType;
import hesml_umls_benchmark.SemanticLibraryType;
import hesml_umls_benchmark.IBioLibraryExperiment;

/**
 * This function creates all UMLS benchmarks
 * @author j.lastra
 */

public class BenchmarkFactory
{
    /**
     * This function creates a random concept evaluation on SNOMED-CT ontology.
     * @param libraries
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
     * @return
     * @throws Exception 
     */

    public static IBioLibraryExperiment createSnomedConceptBenchmark(
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
        return (new RandomConceptsEvalBenchmark(libraries, ontology,
                similarityMeasure, icModel, nRandomSamplesPerLibrary,
                nRuns, strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename));
    }   
    
    /**
     * This function creates a random concept evaluation on MeSH ontology.
     * @param libraries
     * @param ontology
     * @param similarityMeasure
     * @param icModel
     * @param nRandomSamplesPerLibrary
     * @param nRuns
     * @param strMeShDir
     * @param strMeSHXmlDescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @return
     * @throws Exception 
     */

    public static IBioLibraryExperiment createMeSHConceptBenchmark(
            SemanticLibraryType[]   libraries,
            UMLSOntologyType        ontology,
            SimilarityMeasureType   similarityMeasure,
            IntrinsicICModelType    icModel,
            int[]                   nRandomSamplesPerLibrary,
            int                     nRuns,
            String                  strMeShDir,
            String                  strMeSHXmlDescriptionFileName,
            String                  strUmlsDir,
            String                  strSNOMED_CUI_mappingfilename) throws Exception
    {
        return (new RandomConceptsEvalBenchmark(libraries, ontology,
                similarityMeasure, icModel, nRandomSamplesPerLibrary,
                nRuns, strMeShDir, strMeSHXmlDescriptionFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename));
    }   
    
    /**
     * This function creates a random concept evaluation.
     * @param libraries
     * @param similarityMeasure
     * @param icModel
     * @param nRandomSamplesPerLibrary
     * @param nRuns
     * @param strGoOboFilename
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createGOConceptBenchmark(
            SemanticLibraryType[]   libraries,
            SimilarityMeasureType   similarityMeasure,
            IntrinsicICModelType    icModel,
            int[]                   nRandomSamplesPerLibrary,
            int                     nRuns,
            String                  strGoOboFilename) throws Exception
    {
        return (new RandomConceptsEvalBenchmark(libraries,
                similarityMeasure, icModel, nRandomSamplesPerLibrary,
                nRuns, strGoOboFilename));
    }

    /**
     * This function creates an experiment to compare all proteins
     * defined in both input files.
     * @param strGoAnnotatedFile1 
     * @param groupwiseSimilarityMeasure 
     * @param gOontology 
     * @param strGoAnnotatedFile2 
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createLargeGOConceptBenchmark(
            GroupwiseSimilarityMeasureType  groupwiseType,
            String                          strGoOntologyFilename,
            String                          strGoAnnotatedFile1,
            String                          strGoAnnotatedFile2) throws Exception
    {
        return (new LargeGOfileBenchmark(groupwiseType, strGoOntologyFilename,
                strGoAnnotatedFile1, strGoAnnotatedFile2));
    }       
    
    /**
     * Constructor for the GO-based benchmark with the SimGIC groupwise measure
     * @param icModelType 
     * @param strGoOboFilename File containing the GO ontology
     * @param strGoAnnotatedFile1 
     * @param strGoAnnotatedFile2 
     */
    
    public static IBioLibraryExperiment createLargeGOConceptBenchmark(
            IntrinsicICModelType    icModelType,
            String                  strGoOboFilename,
            String                  strGoAnnotatedFile1,
            String                  strGoAnnotatedFile2) throws Exception
    {
        return (new LargeGOfileBenchmark(icModelType, strGoOboFilename,
                strGoAnnotatedFile1, strGoAnnotatedFile2));
    }
    
    /**
     * Constructor for the GO-based benchmark with the BMA groupwise measure
     * using an IN-based measure
     * @param groupMetricType 
     * @param icModelType 
     * @param strGoOboFilename File containing the GO ontology
     * @param strGoAnnotatedFile1 
     * @param strGoAnnotatedFile2 
     */
    
    public static IBioLibraryExperiment createLargeGOConceptBenchmark(
            GroupwiseMetricType     groupMetricType,
            SimilarityMeasureType   nodeSimilarityMeasureType,
            IntrinsicICModelType    icModelType,
            String                  strGoOboFilename,
            String                  strGoAnnotatedFile1,
            String                  strGoAnnotatedFile2) throws Exception
    {
        return (new LargeGOfileBenchmark(groupMetricType, nodeSimilarityMeasureType, icModelType,
                strGoOboFilename,strGoAnnotatedFile1, strGoAnnotatedFile2));
    }
    
    /**
     * This fucntion creates a benchmark to evaluate the approximation quality
     * of the AncSPL algorithm in the SNOMED-CT ontology.
     * @param icModel
     * @param measureType1
     * @param measureType2
     * @param nRandomSamples
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strSNOMED_CUI_mappingfilename
     * @param seed
     * @param strUmlsDir
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createSnomedAncSPLBenchmark(
            IntrinsicICModelType    icModel,
            SimilarityMeasureType   measureType1,
            SimilarityMeasureType   measureType2,
            int                     nRandomSamples,
            String                  strSnomedDir,
            String                  strSnomedDBconceptFileName,
            String                  strSnomedDBRelationshipsFileName,
            String                  strSnomedDBdescriptionFileName,
            String                  strUmlsDir,
            String                  strSNOMED_CUI_mappingfilename,
            int                     seed) throws Exception
    {
        return (new AncSPLBenchmark(strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName, strSnomedDBdescriptionFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename, icModel, 
                measureType1, measureType2, nRandomSamples, seed));
    }   
    
    /**
     * This function creates a benchmark to evaluate the approximation quality
     * of the AncSPL algorithm in the GO ontology.
     * @param icModel
     * @param measureType1
     * @param measureType2
     * @param nRandomSamples
     * @param strOboOntologyFilename
     * @param seed
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createGoAncSPLBenchmark(
            IntrinsicICModelType    icModel,
            SimilarityMeasureType   measureType1,
            SimilarityMeasureType   measureType2,
            int                     nRandomSamples,
            String                  strOboOntologyFilename,
            int                     seed) throws Exception
    {
        return (new AncSPLBenchmark(strOboOntologyFilename, icModel, 
                measureType1, measureType2, nRandomSamples, seed));
    }   
    
    /**
     * This function creates a benchmark to evaluate the approximation quality
     * of the AncSPL algorithm in the GO ontology.
     * @param icModel
     * @param measureType1
     * @param measureType2
     * @param nRandomSamples
     * @param strBaseDir
     * @param strWordNet3_0_Dir
     * @param seed
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createWordNetAncSPLBenchmark(
            IntrinsicICModelType    icModel,
            SimilarityMeasureType   measureType1,
            SimilarityMeasureType   measureType2,
            int                     nRandomSamples,
            String                  strBaseDir,
            String                  strWordNet3_0_Dir,
            int                     seed) throws Exception
    {
        return (new AncSPLBenchmark(strBaseDir, strWordNet3_0_Dir, icModel, 
                measureType1, measureType2, nRandomSamples, seed));
    }
    
    /**
     * This function creates a random concept evaluation.
     * @param libraries
     * @param similarityMeasure
     * @param icModel
     * @param strDatasetPath
     * @param strMeSHdir
     * @param strMeSHXmlConceptFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @param annotatedDataset
     * @return
     * @throws Exception 
     */

    public static IBioLibraryExperiment createMeSHSentenceBenchmark(
            SemanticLibraryType[]   libraries,
            SimilarityMeasureType   similarityMeasure,
            IntrinsicICModelType    icModel,
            String[]                strDatasetPath,
            String                  strMeSHdir,
            String                  strMeSHXmlConceptFileName,
            String                  strUmlsDir,
            String                  strSNOMED_CUI_mappingfilename,
            AnnotateDataset         annotatedDataset) 
            throws Exception
    {
        return (new MeSHSentencesEvalBenchmark(libraries,
                similarityMeasure, icModel, strDatasetPath, 
                strMeSHdir, strMeSHXmlConceptFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename, annotatedDataset));
    }
    
    /**
     * This fucntion creates an instance of the AncSPL scalability benchmark for SNOMED-CT
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createSnomedAncSPLScalabilityTest(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName,
            String  strUmlsDir,
            String  strSNOMED_CUI_mappingfilename) throws Exception
    {
        return (new AncSPLScalabilityBenchmark(strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName, strSnomedDBdescriptionFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename));
    }

    /**
     * This function creates an instance of the AncSPL subgraph scalability benchmark for SNOMED-CT
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createSnomedAncSPLSubgraphScalabilityBenchmark(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName,
            String  strUmlsDir,
            String  strSNOMED_CUI_mappingfilename) throws Exception
    {
        return (new AncSPLSubgraphScalabilityBenchmark(strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName, strSnomedDBdescriptionFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename));
    }
    
    /**
     * This function creates an instance of the AncSPL subgraph scalability benchmark for MeSH
     * @param strXmlMSHfilename 
     * @param strUmlsDir 
     * @param strSNOMED_CUI_mappingfilename
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createMeSHAncSPLSubgraphScalabilityBenchmark(
            String  strXmlMSHfilename,
            String  strUmlsDir,
            String  strSNOMED_CUI_mappingfilename) throws Exception
    {
        return (new AncSPLSubgraphScalabilityBenchmark(strXmlMSHfilename, strUmlsDir, strSNOMED_CUI_mappingfilename));
    }
    
    /**
     * This function creates an instance of the AncSPL subgraph scalability benchmark for GO
     * @param strGoOntologyFilename
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createGoAncSPLSubgraphScalabilityTest(
            String  strGoOntologyFilename) throws Exception
    {
        return (new AncSPLSubgraphScalabilityBenchmark(strGoOntologyFilename));
    }
    
    /**
     * This function creates an instance of the AncSPL subgraph scalability benchmark for GO
     * @param strGoOntologyFilename
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createWordNetAncSPLSubgraphScalabilityTest(
            String  strBaseDir, 
            String  strWordNet3_0_Dir) throws Exception
    {
        return (new AncSPLSubgraphScalabilityBenchmark(strBaseDir, strWordNet3_0_Dir));
    }
    
    /**
     * This funcion creates an istance of the AncSPL scalability benchmark for MeSH
     * @param strXmlMSHfilename 
     * @param strUmlsDir 
     * @param strSNOMED_CUI_mappingfilename
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createMeSHAncSPLScalabilityTest(
            String  strXmlMSHfilename,
            String  strUmlsDir,
            String  strSNOMED_CUI_mappingfilename) throws Exception
    {
        return (new AncSPLScalabilityBenchmark(strXmlMSHfilename, strUmlsDir, strSNOMED_CUI_mappingfilename));
    }
    
    /**
     * This function an istance of the AncSPL scalability benchmark for GO
     * @param strGoOntologyFilename
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createGoAncSPLScalabilityTest(
            String  strGoOntologyFilename) throws Exception
    {
        return (new AncSPLScalabilityBenchmark(strGoOntologyFilename));
    }
    
    /**
     * This function creates an instance of the AncSPL statistical benchmark
     * for the SNOMED-CT ontology
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createAncSPLStatisticalBenchmark(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName,
            String  strUmlsDir,
            String  strSNOMED_CUI_mappingfilename) throws Exception
    {
        return (new AncSPLStatisticalBenchmark(strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName, strSnomedDBdescriptionFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename));
    }

    /**
     * This function creates an instance of the AncSPL benchmark for the
     * GO ontology.
     * @param strOboOntology
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createAncSPLStatisticalBenchmark(
            String  strOboOntology) throws Exception
    {
        return (new AncSPLStatisticalBenchmark(strOboOntology));
    }
    
    /**
     * This function creates an instance of the AncSPL benchmark for the
     * WordNet ontology.
     * @param strOboOntology
     * @return
     * @throws Exception 
     */
    
    public static IBioLibraryExperiment createAncSPLStatisticalBenchmark(
            String  strWordNetDatasetsDir,
            String  strWordNet3_0_Dir) throws Exception
    {
        return (new AncSPLStatisticalBenchmark(strWordNetDatasetsDir, strWordNet3_0_Dir));
    }
}