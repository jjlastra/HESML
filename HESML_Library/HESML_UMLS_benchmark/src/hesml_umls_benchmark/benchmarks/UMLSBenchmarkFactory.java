/*
 * Copyright (C) 2020 Universidad Complutense de Madrid (UCM)
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
 */

package hesml_umls_benchmark.benchmarks;

import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml_umls_benchmark.IUMLSBenchmark;
import hesml_umls_benchmark.SnomedBasedLibrary;

/**
 * This function creates all UMLS benchmarks
 * @author j.lastra
 */

public class UMLSBenchmarkFactory
{
    /**
     * This function creates a random concept-pair benchmark
     * @param libraries
     * @param similarityMeasure
     * @param icModel
     * @param nRandomSamples
     * @param nRuns
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @return
     * @throws Exception 
     */

    public static IUMLSBenchmark createConceptBenchmark(
            SnomedBasedLibrary[]    libraries,
            SimilarityMeasureType   similarityMeasure,
            IntrinsicICModelType    icModel,
            int                     nRandomSamples,
            int                     nRuns,
            String                  strSnomedDir,
            String                  strSnomedDBconceptFileName,
            String                  strSnomedDBRelationshipsFileName,
            String                  strSnomedDBdescriptionFileName) throws Exception
    {
        return (new ConceptEvaluationBenchmark(libraries,
                similarityMeasure, icModel, nRandomSamples, nRuns, 
                strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName, strSnomedDBdescriptionFileName));
    }   
}
