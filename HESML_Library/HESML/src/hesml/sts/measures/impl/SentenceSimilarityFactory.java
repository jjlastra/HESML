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

package hesml.sts.measures.impl;

import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml.sts.measures.ICombinedSentenceSimilarityMeasure;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.StringBasedSentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import hesml.taxonomyreaders.obo.IOboOntology;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.wordnet.IWordNetDB;

/**
 * This class builds the sentence similarity measures.
 * @author j.lastra
 */

public class SentenceSimilarityFactory
{
    /**
     * This function creates a string-based sentence similarity measure.
     * 
     * @param strLabel
     * @param method
     * @param wordPreprocessing
     * @return 
     */
    
    public static ISentenceSimilarityMeasure getStringBasedMeasure(
            String                  strLabel,
            StringBasedSentenceSimilarityMethod method,
            IWordProcessing                     wordPreprocessing)
    {
        // We initialize the output
        
        ISentenceSimilarityMeasure measure = null;
        
        // We creates an instance of the required method
        
        switch (method)
        {
            case BlockDistance:
                
                measure = new BlockDistanceMeasure(strLabel, wordPreprocessing);
                
                break;
                
            case Jaccard:
                
                measure = new JaccardMeasure(strLabel, wordPreprocessing);
                
                break;
                
            case Levenshtein:
                
                // We use the default values provided in other state-of-the-art implementations.
                
                measure = new LevenshteinMeasure(strLabel, wordPreprocessing, 1, 1);
                
                break;
                
            case OverlapCoefficient:
                
                measure = new OverlapCoefficientMeasure(strLabel, wordPreprocessing);
                
                break;
                
            case Qgram:
                
                // We use the default values provided in other state-of-the-art implementations.
                
                measure = new QgramMeasure(strLabel, wordPreprocessing, 3);
                
                break;
        }
        
        // We return the result
        
        return (measure);
    }
    
    /**
     * This function creates a WBSM measure.
     * 
     * @param strLabel
     * @param preprocesser
     * @param wordnetTaxonomy
     * @param wordSimilarityMeasureType
     * @param icModelType
     * @param wordnet
     * @return ISentenceSimilarityMeasure
     * @throws java.lang.Exception
     */
    
    public static ISentenceSimilarityMeasure getWBSMMeasure(
            String                  strLabel,
            IWordProcessing         preprocesser,
            IWordNetDB              wordnet,
            ITaxonomy               wordnetTaxonomy,
            SimilarityMeasureType   wordSimilarityMeasureType,
            IntrinsicICModelType    icModelType) throws Exception
    {
        return (new WBSMMeasure(strLabel, preprocesser, wordnet,
                wordnetTaxonomy, wordSimilarityMeasureType, icModelType));
    }
    
    /**
     * This function creates a UBSM measure.
     * 
     * @param strLabel
     * @param preprocesser
     * @param SnomedOntology
     * @param wordSimilarityMeasureType
     * @param icModelType
     * @return ISentenceSimilarityMeasure
     * @throws java.lang.Exception
     */
    
    public static ISentenceSimilarityMeasure getUBSMMeasureSnomed(
            String                  strLabel,
            IWordProcessing         preprocesser,
            ISnomedCtOntology       SnomedOntology,
            SimilarityMeasureType   wordSimilarityMeasureType,
            IntrinsicICModelType    icModelType) throws Exception
    {
        return (new UBSMMeasure(strLabel, preprocesser, SnomedOntology, wordSimilarityMeasureType, icModelType));
    }
    
    /**
     * This function creates a COM measure.
     * 
     * @param strLabel
     * @param lambda
     * @param measures
     * @return ISentenceSimilarityMeasure
     * @throws java.lang.Exception
     */
    
    public static ICombinedSentenceSimilarityMeasure getCOMMeasure(
            String                          strLabel,
            Double                          lambda,
            ISentenceSimilarityMeasure[]    measures) throws Exception
    {
        return (new COMMeasure(strLabel, lambda, measures));
    }
    
    
    /**
     * This function creates a UBSM measure.
     * 
     * @param strLabel
     * @param preprocesser
     * @param MeshOntology
     * @param wordSimilarityMeasureType
     * @param icModelType
     * @return ISentenceSimilarityMeasure
     * @throws java.lang.Exception
     */
    
    public static ISentenceSimilarityMeasure getUBSMMeasureMeSH(
            String                  strLabel,
            IWordProcessing         preprocesser,
            IMeSHOntology           MeshOntology,
            SimilarityMeasureType   wordSimilarityMeasureType,
            IntrinsicICModelType    icModelType) throws Exception
    {
        return (new UBSMMeasure(strLabel, preprocesser, MeshOntology, wordSimilarityMeasureType, icModelType));
    }
    
    /**
     * This function creates a UBSM measure.
     * 
     * @param strLabel
     * @param preprocesser
     * @param OboOntology
     * @param wordSimilarityMeasureType
     * @param icModelType
     * @return ISentenceSimilarityMeasure
     * @throws java.lang.Exception
     */
    
    public static ISentenceSimilarityMeasure getUBSMMeasureObo(
            String                  strLabel,
            IWordProcessing         preprocesser,
            IOboOntology            OboOntology,
            SimilarityMeasureType   wordSimilarityMeasureType,
            IntrinsicICModelType    icModelType) throws Exception
    {
        return (new UBSMMeasure(strLabel, preprocesser, OboOntology, wordSimilarityMeasureType, icModelType));
    }
}