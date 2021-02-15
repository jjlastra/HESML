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
import hesml_umls_benchmark.IAncSPLScalabilityBenchmark;
import hesml_umls_benchmark.UMLSOntologyType;
import hesml_umls_benchmark.SemanticLibraryType;
import hesml_umls_benchmark.ISemanticLibBenchmark;

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

    public static ISemanticLibBenchmark createSnomedConceptBenchmark(
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

    public static ISemanticLibBenchmark createMeSHConceptBenchmark(
            SemanticLibraryType[]    libraries,
            UMLSOntologyType      ontology,
            SimilarityMeasureType       similarityMeasure,
            IntrinsicICModelType        icModel,
            int[]                       nRandomSamplesPerLibrary,
            int                         nRuns,
            String                      strMeShDir,
            String                      strMeSHXmlDescriptionFileName,
            String                      strUmlsDir,
            String                      strSNOMED_CUI_mappingfilename) throws Exception
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
    
    public static ISemanticLibBenchmark createGOConceptBenchmark(
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
     * This fucntion creates a benchmark to evaluate the approximation quality
     * of the AncSPL algorithm.
     * @param icModel
     * @param measureType1
     * @param measureType2
     * @param nRandomSamples
     * @param useEdgeWeights
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strSNOMED_CUI_mappingfilename
     * @param strUmlsDir
     * @return
     * @throws Exception 
     */
    
    public static ISemanticLibBenchmark createAncSPLBenchmark(
            IntrinsicICModelType    icModel,
            SimilarityMeasureType   measureType1,
            SimilarityMeasureType   measureType2,
            int                     nRandomSamples,
            String                  strSnomedDir,
            String                  strSnomedDBconceptFileName,
            String                  strSnomedDBRelationshipsFileName,
            String                  strSnomedDBdescriptionFileName,
            String                  strUmlsDir,
            String                  strSNOMED_CUI_mappingfilename) throws Exception
    {
        return (new AncSPLBenchmark(strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName, strSnomedDBdescriptionFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename, icModel, 
                measureType1, measureType2, nRandomSamples));
    }   
    
    
        /**
     * This function creates a random concept evaluation.
     * @param libraries
     * @param vocabulary
     * @param similarityMeasure
     * @param icModel
     * @param strDatasetPath
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @return
     * @throws Exception 
     */

    public static ISemanticLibBenchmark createMeSHSentenceBenchmark(
            SemanticLibraryType[]   libraries,
            SimilarityMeasureType   similarityMeasure,
            IntrinsicICModelType    icModel,
            String                  strDatasetPath,
            String                  strMeSHdir,
            String                  strMeSHXmlConceptFileName,
            String                  strUmlsDir,
            String                  strSNOMED_CUI_mappingfilename) throws Exception
    {
        return (new MeSHSentencesEvalBenchmark(libraries,
                similarityMeasure, icModel, strDatasetPath, 
                strMeSHdir, strMeSHXmlConceptFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename));
    }
    
    /**
     * This fucntion creates an istance of the AncSPL scalability benchmark
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @return
     * @throws Exception 
     */
    
    public static IAncSPLScalabilityBenchmark createAncSPLScalabilityTest(
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
}