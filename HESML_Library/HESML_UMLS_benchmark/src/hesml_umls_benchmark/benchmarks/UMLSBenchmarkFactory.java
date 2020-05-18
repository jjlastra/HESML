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
import hesml_umls_benchmark.IUMLSBenchmark;
import hesml_umls_benchmark.SnomedBasedLibraryType;
import hesml_umls_benchmark.LibraryType;

/**
 * This function creates all UMLS benchmarks
 * @author j.lastra
 */

public class UMLSBenchmarkFactory
{
    /**
     * This function creates a random concept evaluation.
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

    public static IUMLSBenchmark createConceptBenchmark(
            SnomedBasedLibraryType[]    libraries,
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
        return (new RandomConceptsEvalBenchmark(libraries,
                similarityMeasure, icModel, nRandomSamplesPerLibrary,
                nRuns, strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename));
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
    
    public static IUMLSBenchmark createAncSPLBenchmark(
            IntrinsicICModelType    icModel,
            SimilarityMeasureType   measureType1,
            SimilarityMeasureType   measureType2,
            int                     nRandomSamples,
            boolean                 useEdgeWeights,
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
                measureType1, measureType2, nRandomSamples, useEdgeWeights));
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

    public static IUMLSBenchmark createSentenceBenchmark(
            SnomedBasedLibraryType[]    libraries,
            LibraryType                  vocabulary,
            SimilarityMeasureType       similarityMeasure,
            IntrinsicICModelType        icModel,
            String                      strDatasetPath,
            String                      strSnomedDir,
            String                      strSnomedDBconceptFileName,
            String                      strSnomedDBRelationshipsFileName,
            String                      strSnomedDBdescriptionFileName,
            String                      strUmlsDir,
            String                      strSNOMED_CUI_mappingfilename) throws Exception
    {
        return (new SentencesEvalBenchmark(libraries, vocabulary,
                similarityMeasure, icModel, strDatasetPath, 
                strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName,
                strUmlsDir, strSNOMED_CUI_mappingfilename));
    }
}
