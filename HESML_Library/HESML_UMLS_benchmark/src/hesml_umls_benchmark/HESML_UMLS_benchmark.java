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

import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml_umls_benchmark.benchmarks.UMLSBenchmarkFactory;

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
     * Main function. This fucntion executes all experiments reported in
     * the HEMSL-UMLS introductory paper [1].
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws Exception
    {
        // We set the UMLS directory
        
        String strUMLSdir = "../UMLS/SNOMED-CT_March_09_2020";
        String strSNOMED_conceptFilename = "sct2_Concept_Snapshot_US1000124_20200301.txt";
        String strSNOMED_relationshipsFilename = "sct2_Relationship_Snapshot_US1000124_20200301.txt";
        String strSNOMED_descriptionFilename = "sct2_Description_Snapshot-en_US1000124_20200301.txt";
        String strSNOMED_CUI_mappingfilename = "MRCONSO.RRF";
        
        /**
         * Experiment 1: we compare the performance of the HEMSL, SML and
         * UMLS::Similarity libraries in the evaluation of the IC-based
         * Lib [1] semantic similarity libray by evaluating the degree of
         * similarity between one million of random concept pairs.
         * [1] D. Lin, An information-theoretic definition of similarity,
         * in: Proceedings of the 15th International Conference on Machine
         * Learning, Madison, WI, 1998: pp. 296–304.
         */
        
        SnomedBasedLibraryType[] librariesExp1 = new SnomedBasedLibraryType[]{
                                                    SnomedBasedLibraryType.SML,
                                                    SnomedBasedLibraryType.HESML};
        
        IUMLSBenchmark benchmark1 = UMLSBenchmarkFactory.createConceptBenchmark(
                                    librariesExp1, SimilarityMeasureType.Lin,
                                    IntrinsicICModelType.Seco, 1000000, 10, strUMLSdir,
                                    strSNOMED_conceptFilename, strSNOMED_relationshipsFilename,
                                    strSNOMED_descriptionFilename, strSNOMED_CUI_mappingfilename);
        
        benchmark1.run("IC_based_Concept_Similarity_exp.csv");
        benchmark1.clear();
    }
}


