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

import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.GroupwiseMetricType;
import hesml.measures.IGroupwiseSimilarityMeasure;
import hesml.measures.ISimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.sts.measures.ComMixedVectorsMeasureType;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class implements the BIOSSES2017 Measure for WBSM methods (WordNet-based methods)
 * Given two sentences, the method works as follows:
 * 
 * 1. Construct the joint set of distinct words from S1 and S2 (dictionary)
 * 2. Initialize the semantic vectors.
 * 3. Use WordNet to construct the semantic vector
 * 4. Calculate the cosine similarity of the semantic vectors
 * 
 * @author alicia
 */

class ComMixedVectorsMeasure extends SentenceSimilarityMeasure
{
    // Word Similarity measure used for calculating similarity between words.
    
    private ISimilarityMeasure m_wordSimilarityMeasureTypeWordnet;
    private ISimilarityMeasure m_wordSimilarityMeasureTypeUMLS;
    
    // SNOMED-CT ontology
    
    private final ISnomedCtOntology   m_SnomedOntology;
    
    // Taxonomy and vertexes contained in the HESML taxonomy encoding SNOMED
    
    private final ITaxonomy   m_Snomedtaxonomy;
    
    // MESH ontology
    
    private final IMeSHOntology m_MeshOntology;   
    
    // Taxonomy and vertexes contained in the HESML taxonomy encoding MESH
    
    private final ITaxonomy   m_MeSHtaxonomy;
    
    // WordNetDB and taxonomy for computing the WordNet-based word similarity measures.
    
    private final IWordNetDB  m_wordnet;            // WordNet DB
    private final ITaxonomy   m_wordnetTaxonomy;    // WordNet taxonomy
    
    // User label which is shown in all raw matrix results
    
    private final String m_strLabel;
    
    // IC model used to evaluate the associated IC-based word similarity measure
    
    private final ITaxonomyInfoConfigurator m_ICmodelWordNet;
    private final ITaxonomyInfoConfigurator m_ICmodelUMLS;
    
    // We initialize the String measure
    
    private final ISentenceSimilarityMeasure  m_stringMeasure;
    
    // Lambda value
    
    private final Double m_lambda;
    
    // COM Mixed vector measure type
    
    private final ComMixedVectorsMeasureType  m_poolingMethod;
    
    // UBSM and WBSM preprocess configurations
    
    private final IWordProcessing m_preprocesserWBSM;
    private final IWordProcessing m_preprocesserUBSM;
    
    private final IGroupwiseSimilarityMeasure m_UBSMMeasure;
    private final IGroupwiseSimilarityMeasure m_WBSMMeasure;
    
    /**
     * Constructor for SNOMED ontology
     * @param preprocesser 
     * @param strWordNet_Dir Path to WordNet directory
     */
    
    ComMixedVectorsMeasure(
            String                      strLabel,
            IWordProcessing             preprocesser,
            ISnomedCtOntology           snomedCtOntology,
            ITaxonomy                   taxonomy,
            SimilarityMeasureType       wordSimilarityMeasureTypeUMLS,
            IntrinsicICModelType        icModelType,
            ISentenceSimilarityMeasure  stringMeasure,
            Double                      lambda,
            ComMixedVectorsMeasureType  comMixedVectorsMeasureType) throws Exception
    {
        // We intialize the base class
        
        super(preprocesser);
        
        m_preprocesserWBSM = null;
        m_preprocesserUBSM = null;
        
        // Initialize the WordNetDB, taxonomy and IC model
        
        m_ICmodelUMLS = ICModelsFactory.getIntrinsicICmodel(icModelType);
        m_ICmodelWordNet = null;
        
        m_SnomedOntology = snomedCtOntology;
        m_Snomedtaxonomy = taxonomy;
        
        m_MeshOntology = null;
        m_MeSHtaxonomy = null;
        
        m_wordnet = null;
        m_wordnetTaxonomy = null;
        
        // Set the COM Mixed method
        
        m_poolingMethod = comMixedVectorsMeasureType;
        
        m_UBSMMeasure = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(
                                                    m_Snomedtaxonomy,
                                                    wordSimilarityMeasureTypeUMLS,
                                                    GroupwiseMetricType.BestMatchAverage);
        m_WBSMMeasure = null;
        
        // Initialize the string measure
        
        m_stringMeasure = stringMeasure;
        
        // Initialize the lambda value
        
        m_lambda = lambda;
        
        // Initialize the word similarity measures 
        
        setSimilarityMeasureUMLS(wordSimilarityMeasureTypeUMLS);
        
        // We save the label

        m_strLabel = strLabel;
    }
    
    /**
     * Constructor for none ontology
     * @param preprocesser 
     * @param strWordNet_Dir Path to WordNet directory
     */
    
    ComMixedVectorsMeasure(
            String                      strLabel,
            IWordProcessing             preprocesser,
            SimilarityMeasureType       wordSimilarityMeasureTypeUMLS,
            ISentenceSimilarityMeasure  stringMeasure,
            Double                      lambda,
            ComMixedVectorsMeasureType  comMixedVectorsMeasureType) throws Exception
    {
        // We intialize the base class
        
        super(preprocesser);
        
        m_preprocesserWBSM = null;
        m_preprocesserUBSM = null;
        
        // Initialize the WordNetDB, taxonomy and IC model
        
        m_ICmodelUMLS = null;
        m_ICmodelWordNet = null;
        
        m_SnomedOntology = null;
        m_Snomedtaxonomy = null;
        
        m_MeshOntology = null;
        m_MeSHtaxonomy = null;
        
        m_wordnet = null;
        m_wordnetTaxonomy = null;
        
        // Set the COM Mixed method
        
        m_poolingMethod = comMixedVectorsMeasureType;
        
        // Initialize the string measure
        
        m_stringMeasure = stringMeasure;
        
        // Initialize the lambda value
        
        m_lambda = lambda;
        
        // We save the label

        m_strLabel = strLabel;
        
        m_UBSMMeasure = null;
        m_WBSMMeasure = null;
    }
    
    /**
     * Constructor for COM WordNet + Snomed pooled ontology
     * @param preprocesser 
     * @param strWordNet_Dir Path to WordNet directory
     */
    
    ComMixedVectorsMeasure(
            String                      strLabel,
            IWordProcessing             preprocesserWBSM,
            IWordProcessing             preprocesserUBSM,
            ISnomedCtOntology           snomedCtOntology,
            ITaxonomy                   taxonomy,
            IWordNetDB                  wordnet,
            ITaxonomy                   wordnetTaxonomy,
            SimilarityMeasureType       wordSimilarityMeasureTypeWordnet,
            SimilarityMeasureType       wordSimilarityMeasureTypeUMLS,
            IntrinsicICModelType        icModelType,
            ISentenceSimilarityMeasure  stringMeasure,
            Double                      lambda,
            ComMixedVectorsMeasureType  comMixedVectorsMeasureType) throws Exception
    {
        // We intialize the base class
        
        super(null);
        
        m_preprocesserWBSM = preprocesserWBSM;
        m_preprocesserUBSM = preprocesserUBSM;
        
        // Initialize the WordNetDB, taxonomy and IC model
        
        m_ICmodelUMLS = ICModelsFactory.getIntrinsicICmodel(icModelType);
        m_ICmodelWordNet = ICModelsFactory.getIntrinsicICmodel(icModelType);
        
        m_SnomedOntology = snomedCtOntology;
        m_Snomedtaxonomy = taxonomy;
        
        m_MeshOntology = null;
        m_MeSHtaxonomy = null;
        
        m_wordnet = wordnet;
        m_wordnetTaxonomy = wordnetTaxonomy;
        
        // Set the COM Mixed method
        
        m_poolingMethod = comMixedVectorsMeasureType;
        
        // Initialize the string measure
        
        m_stringMeasure = stringMeasure;
        
        // Initialize the lambda value
        
        m_lambda = lambda;
        
        // Initialize the word similarity measures 
        
        setSimilarityMeasureUMLS(wordSimilarityMeasureTypeUMLS);
        setSimilarityMeasureWordNet(wordSimilarityMeasureTypeWordnet);
        
        // We save the label

        m_strLabel = strLabel;
        
        m_UBSMMeasure = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(
                                                    m_Snomedtaxonomy,
                                                    wordSimilarityMeasureTypeUMLS,
                                                    GroupwiseMetricType.BestMatchAverage);
        
        m_WBSMMeasure = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(
                                                    wordnetTaxonomy,
                                                    wordSimilarityMeasureTypeWordnet,
                                                    GroupwiseMetricType.BestMatchAverage);
    }
    
    /**
     * Constructor for WordNet ontology
     * @param preprocesser 
     * @param strWordNet_Dir Path to WordNet directory
     */
    
    ComMixedVectorsMeasure(
            String                      strLabel,
            IWordProcessing             preprocesser,
            IWordNetDB                  wordnet,
            ITaxonomy                   wordnetTaxonomy,
            SimilarityMeasureType       wordSimilarityMeasureTypeWordnet,
            IntrinsicICModelType        icModelType,
            ISentenceSimilarityMeasure  stringMeasure,
            Double                      lambda,
            ComMixedVectorsMeasureType  comMixedVectorsMeasureType) throws Exception
    {
        // We intialize the base class
        
        super(preprocesser);
        
        m_preprocesserWBSM = null;
        m_preprocesserUBSM = null;
        
        // Initialize the WordNetDB, taxonomy and IC model
        
        m_ICmodelUMLS = ICModelsFactory.getIntrinsicICmodel(icModelType);
        m_ICmodelWordNet = ICModelsFactory.getIntrinsicICmodel(icModelType);
        
        m_SnomedOntology = null;
        m_Snomedtaxonomy = null;
        
        m_MeshOntology = null;
        m_MeSHtaxonomy = null;
        
        m_wordnet = wordnet;
        m_wordnetTaxonomy = wordnetTaxonomy;
        
        // Set the COM Mixed method
        
        m_poolingMethod = comMixedVectorsMeasureType;
        
        // Initialize the string measure
        
        m_stringMeasure = stringMeasure;
        
        // Initialize the lambda value
        
        m_lambda = lambda;
        
        // Initialize the word similarity measures 
        
        setSimilarityMeasureWordNet(wordSimilarityMeasureTypeWordnet);
        
        // We save the label

        m_strLabel = strLabel;
        
        m_UBSMMeasure = null;
                
        m_WBSMMeasure = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(
                                                    wordnetTaxonomy,
                                                    wordSimilarityMeasureTypeWordnet,
                                                    GroupwiseMetricType.BestMatchAverage);
    }
    
    /**
     * Constructor for MESH ontology
     * @param preprocesser 
     * @param strWordNet_Dir Path to WordNet directory
     */
    
    ComMixedVectorsMeasure(
            String                      strLabel,
            IWordProcessing             preprocesser,
            IMeSHOntology               meshOntology,
            ITaxonomy                   taxonomy,
            SimilarityMeasureType       wordSimilarityMeasureTypeUMLS,
            IntrinsicICModelType        icModelType,
            ISentenceSimilarityMeasure  stringMeasure,
            Double                      lambda,
            ComMixedVectorsMeasureType  comMixedVectorsMeasureType) throws Exception
    {
        // We intialize the base class
        
        super(preprocesser);
        
        m_preprocesserWBSM = null;
        m_preprocesserUBSM = null;
        
        // Initialize the WordNetDB, taxonomy and IC model
        
        m_ICmodelUMLS = ICModelsFactory.getIntrinsicICmodel(icModelType);
        m_ICmodelWordNet = null;
        
        m_SnomedOntology = null;
        m_Snomedtaxonomy = null;
        
        m_MeshOntology = meshOntology;
        m_MeSHtaxonomy = taxonomy;
        
        m_wordnet = null;
        m_wordnetTaxonomy = null;
        
        // Set the COM Mixed method
        
        m_poolingMethod = comMixedVectorsMeasureType;
        
        // Initialize the string measure
        
        m_stringMeasure = stringMeasure;
        
        // Initialize the lambda value
        
        m_lambda = lambda;
        
        // Initialize the word similarity measures 
        
        setSimilarityMeasureUMLS(wordSimilarityMeasureTypeUMLS);
        
        // We save the label

        m_strLabel = strLabel;
        
        m_UBSMMeasure = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(
                                                    m_MeSHtaxonomy,
                                                    wordSimilarityMeasureTypeUMLS,
                                                    GroupwiseMetricType.BestMatchAverage);
        
        m_WBSMMeasure = null;
    }
    
    /**
     * Constructor for COM MESH + WordNet pooled ontology
     * @param preprocesser 
     * @param strWordNet_Dir Path to WordNet directory
     */
    
    ComMixedVectorsMeasure(
            String                      strLabel,
            IWordProcessing             preprocesserWBSM,
            IWordProcessing             preprocesserUBSM,
            IMeSHOntology               meshOntology,
            ITaxonomy                   taxonomy,
            IWordNetDB                  wordnet,
            ITaxonomy                   wordnetTaxonomy,
            SimilarityMeasureType       wordSimilarityMeasureTypeWordnet,
            SimilarityMeasureType       wordSimilarityMeasureTypeUMLS,
            IntrinsicICModelType        icModelType,
            ISentenceSimilarityMeasure  stringMeasure,
            Double                      lambda,
            ComMixedVectorsMeasureType  comMixedVectorsMeasureType) throws Exception
    {
        // We intialize the base class
        
        super(null);
        
        m_preprocesserWBSM = preprocesserWBSM;
        m_preprocesserUBSM = preprocesserUBSM;
        
        // Initialize the WordNetDB, taxonomy and IC model
        
        m_ICmodelUMLS = ICModelsFactory.getIntrinsicICmodel(icModelType);
        m_ICmodelWordNet = ICModelsFactory.getIntrinsicICmodel(icModelType);
        
        m_SnomedOntology = null;
        m_Snomedtaxonomy = null;
        
        m_MeshOntology = meshOntology;
        m_MeSHtaxonomy = taxonomy;
        
        m_wordnet = wordnet;
        m_wordnetTaxonomy = wordnetTaxonomy;
        
        // Set the COM Mixed method
        
        m_poolingMethod = comMixedVectorsMeasureType;
        
        // Initialize the string measure
        
        m_stringMeasure = stringMeasure;
        
        // Initialize the lambda value
        
        m_lambda = lambda;
        
        // Initialize the word similarity measures 
        
        setSimilarityMeasureUMLS(wordSimilarityMeasureTypeUMLS);
        setSimilarityMeasureWordNet(wordSimilarityMeasureTypeWordnet);
        
        // We save the label

        m_strLabel = strLabel;
        
        m_UBSMMeasure = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(
                                                    m_MeSHtaxonomy,
                                                    wordSimilarityMeasureTypeUMLS,
                                                    GroupwiseMetricType.BestMatchAverage);
        
        m_WBSMMeasure = MeasureFactory.getGroupwiseBasedOnPairwiseMeasure(
                                                    wordnetTaxonomy,
                                                    wordSimilarityMeasureTypeWordnet,
                                                    GroupwiseMetricType.BestMatchAverage);
    }
    
    /**
     * This function returns the label used to identify the measure in
     * a raw matrix results. This string attribute is set by the users
     * to provide the column header name included in all results generated
     * by this measure. This attribute was especially defined to
     * provide a meaningful name to distinguish the measures based on
     * pre-trained model files.
     * @return 
     */
    
    @Override
    public String getLabel()
    {
        return (m_strLabel);
    }
    
    /**
     * This function sets the active semantic measure used by the library
     * to compute the semantic similarity between concepts.
     * @param icModel
     * @param measureType 
     * @return true if the measure is allowed
     */

    private void setSimilarityMeasureWordNet(
            SimilarityMeasureType   measureType) throws Exception
    {      
        // We set the IC model in the taxonomy
        
        if (measureType != SimilarityMeasureType.Rada)
        {
            m_ICmodelWordNet.setTaxonomyData(m_wordnetTaxonomy);
            m_wordnetTaxonomy.computeCachedAncestorSet(true);
        }
        
        // We get the similarity measure
        
        m_wordSimilarityMeasureTypeWordnet = MeasureFactory.getMeasure(m_wordnetTaxonomy, measureType);
    }
    
    /**
     * This function sets the active semantic measure used by the library
     * to compute the semantic similarity between concepts.
     * @param icModel
     * @param measureType 
     * @return true if the measure is allowed
     */

    private void setSimilarityMeasureUMLS(
            SimilarityMeasureType   measureType) throws Exception
    {      
        // We set the IC model in the taxonomy
        
        if (measureType != SimilarityMeasureType.Rada)
        {
            if(m_Snomedtaxonomy != null)
            {
                m_ICmodelUMLS.setTaxonomyData(m_Snomedtaxonomy);
//                m_Snomedtaxonomy.computeCachedAncestorSet(true);
            }
            if(m_MeSHtaxonomy != null)
            {
                m_ICmodelUMLS.setTaxonomyData(m_MeSHtaxonomy);
//                m_MeSHtaxonomy.computeCachedAncestorSet(true);
            }
        }
        
        // We get the similarity measure
        
        if(m_Snomedtaxonomy != null)
        {
            m_wordSimilarityMeasureTypeUMLS = MeasureFactory.getMeasure(m_Snomedtaxonomy, measureType);
        }
        if(m_MeSHtaxonomy != null)
        {
            m_wordSimilarityMeasureTypeUMLS = MeasureFactory.getMeasure(m_MeSHtaxonomy, measureType);
        }
    }
    
    /**
     * This function is called by any client function before to evaluate
     * the current sentence similarity measure.
     */
    
    @Override
    public void prepareForEvaluation() throws Exception
    {
        // This funcion is called by the sentence similairty benchmark prior
        // to evaluate the similairty between a collection of sentence pairs.
        // This opportunity is used to compute and set the IC values of the
        // WordNet taxonomy in the case that IC model not being null.
        
//        if (m_ICmodel != null) m_ICmodel.setTaxonomyData(m_MeshTaxonomy);
    }
    
    /**
     * Return the current method.
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return (SentenceSimilarityMethod.ComMixedVectorsMeasure);
    }

    /**
     * This function returns the family of the current sentence similarity method.
     * @return SentenceSimilarityFamily
     */
    
    @Override
    public SentenceSimilarityFamily getFamily()
    {
        return (SentenceSimilarityFamily.OntologyBased);
    }
    
    /**
     * The method returns the similarity value between two sentences 
     * using the WBSM measure.
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return double similarity value
     * @throws IOException 
     */
    
    @Override
    public double getSimilarityValue(
            String  strRawSentence1, 
            String  strRawSentence2) 
            throws IOException, FileNotFoundException, 
            InterruptedException, Exception
    {
        // We initialize the output score
        
        double similarity = 0.0;
        
        // We initialize the semantic vectors
        
        double[] semanticVector1 = null;
        double[] semanticVector2 = null;
        
        double[] semanticVector1_umls = null;
        double[] semanticVector2_umls = null;
        
        double[] semanticVector1_wordnet = null;
        double[] semanticVector2_wordnet = null;
        
        // We initialize the dictionary vector
        
        ArrayList<String> dictionary = null;
        
        ArrayList<String> dictionaryWBSM = null;
        
        ArrayList<String> dictionaryUBSM = null;
        
        // Preprocess the sentences and get the tokens for each sentence
        
        String[] lstWordsSentence1 = null;
        String[] lstWordsSentence2 = null;
        
        String[] lstWordsSentence1WBSM = null;
        String[] lstWordsSentence2WBSM = null;
        
        String[] lstWordsSentence1UBSM = null;
        String[] lstWordsSentence2UBSM = null;
        
        switch (m_poolingMethod) 
        {
            case PooledAVG:
            case PooledMin:
            case PooledMax:
            case Mixed:
                
                // We get the word tokens
                
                lstWordsSentence1WBSM = m_preprocesserWBSM.getWordTokens(strRawSentence1);
                lstWordsSentence2WBSM = m_preprocesserWBSM.getWordTokens(strRawSentence2);
                
                lstWordsSentence1UBSM = m_preprocesserUBSM.getWordTokens(strRawSentence1);
                lstWordsSentence2UBSM = m_preprocesserUBSM.getWordTokens(strRawSentence2);
                
                // 1. Construct the joint set of distinct words from S1 and S2 (dictionary)
                
                dictionaryWBSM = constructDictionaryList(lstWordsSentence1WBSM, lstWordsSentence2WBSM);
                dictionaryUBSM = constructDictionaryList(lstWordsSentence1UBSM, lstWordsSentence2UBSM);
                
                // We combine the two dictionaries
                
                Set<String> set = new LinkedHashSet<>(dictionaryWBSM);
                set.addAll(dictionaryUBSM);
                dictionary = new ArrayList<>(set);
                
                break;
                
            default:
                
                // We get the word tokens
                
                lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
                lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
                
                dictionary = constructDictionaryList(lstWordsSentence1, lstWordsSentence2);
                
                break;
        }
        
        // 2. Initialize the semantic vectors.
        
        // Initialize the similarity value 
        
        double ontologySimilarity = 0.0;
            
        switch (m_poolingMethod) 
        {
            case PooledAVG:
            case PooledMin:
            case PooledMax:
                
                // With a pooling method, we calculate the similarity using UMLS and WordNet
                
                semanticVector1_umls = constructSemanticVector(dictionary, lstWordsSentence1UBSM, "umls");
                semanticVector2_umls = constructSemanticVector(dictionary, lstWordsSentence2UBSM, "umls");
                
                semanticVector1_wordnet = constructSemanticVector(dictionary, lstWordsSentence1WBSM, "wordnet");
                semanticVector2_wordnet = constructSemanticVector(dictionary, lstWordsSentence2WBSM, "wordnet"); 
                
                semanticVector1 = poolVectors(semanticVector1_umls,semanticVector1_wordnet);
                semanticVector2 = poolVectors(semanticVector2_umls,semanticVector2_wordnet);
                
                // 3. Compute the cosine similarity between the semantic vectors
                
                ontologySimilarity = computeCosineSimilarity(semanticVector1, semanticVector2);
                
                break;
                
            case Mixed:
                
                semanticVector1_umls = constructSemanticVector(dictionary, lstWordsSentence1UBSM, "umls");
                semanticVector2_umls = constructSemanticVector(dictionary, lstWordsSentence2UBSM, "umls");
                
                semanticVector1_wordnet = constructSemanticVector(dictionary, lstWordsSentence1WBSM, "wordnet");
                semanticVector2_wordnet = constructSemanticVector(dictionary, lstWordsSentence2WBSM, "wordnet");
                
                
                // 3. Compute the cosine similarity between the semantic vectors
                
                double ontologySimilarityUMLS = computeCosineSimilarity(semanticVector1_umls, semanticVector2_umls);
                double ontologySimilarityWordNet = computeCosineSimilarity(semanticVector1_wordnet, semanticVector2_wordnet);
                
                // We get the higher value for each similarity measure
                
                ontologySimilarity = (ontologySimilarityUMLS + ontologySimilarityWordNet) / 2;
                
                break;
                
            default:
                
                // With a single ontology, we construct the vector using only the active ontology
                
                if(m_Snomedtaxonomy != null || m_MeSHtaxonomy != null)
                {
                    semanticVector1 = constructSemanticVector(dictionary, lstWordsSentence1, "umls");
                    semanticVector2 = constructSemanticVector(dictionary, lstWordsSentence2, "umls");
                }
                else if(m_wordnet != null)
                {
                    semanticVector1 = constructSemanticVector(dictionary, lstWordsSentence1, "wordnet");
                    semanticVector2 = constructSemanticVector(dictionary, lstWordsSentence2, "wordnet");
                }
                else
                {
                    semanticVector1 = constructSemanticVector(dictionary, lstWordsSentence1, "none");
                    semanticVector2 = constructSemanticVector(dictionary, lstWordsSentence2, "none");
                }
                
                // 3. Compute the cosine similarity between the semantic vectors
                
                ontologySimilarity = computeCosineSimilarity(semanticVector1, semanticVector2);
                
                break;
        }
            
        // Compute the string-based similarity value
        
        double stringSimilarity = m_stringMeasure.getSimilarityValue(strRawSentence1, strRawSentence2);
        
        // Compute the value
        
        similarity = (ontologySimilarity * m_lambda) + (stringSimilarity * (1.0 - m_lambda));
        
        // Return the similarity value
        
        return (ontologySimilarity);
    }
    
    /**
     * This function compute the pooling of two vectors
     * 
     * @param v1
     * @param v2
     * @return 
     */
    private double[] poolVectors(
            double[] v1, 
            double[] v2)
    {
        // We initialize the accumulated word vector
        
        double[] sentenceVector = v1;
        
        // We obtain the vectors for each word
        
        int vectorCount = 0;
        
        for (int i = 0; i < v1.length; i++)
        {
            // We get the next vlue
            
            switch (m_poolingMethod)
            {
                case PooledAVG:

                    sentenceVector[i] += v2[i];

                    break;
                
                case PooledMax:

                    sentenceVector[i] = Math.max(sentenceVector[i], v2[i]);

                    break;

                case PooledMin:

                    sentenceVector[i] = Math.min(sentenceVector[i], v2[i]);

                    break;
                    
                default:

                    sentenceVector[i] = Math.max(sentenceVector[i], v2[i]);

                    break;
            }
            
            // We increment the non-null vector count

            vectorCount++;
            
        }
        
        // For the case of avergae pooling, we divde by the vector count @alicia: if (m_poolingMethod == Average)¿?
        
        if (m_poolingMethod == ComMixedVectorsMeasureType.PooledAVG)
        {
            for (int i = 0; i < sentenceVector.length; i++)
            {
                sentenceVector[i] /= (double)vectorCount;
            }
        }
        
        // We return the result
        
        return (sentenceVector);
    }
    
    /**
     * This method compute the cosine similarity of the HashMap values.
     * 
     * @param sentence1Map
     * @param sentence2Map
     * @return 
     */
    
    private double computeCosineSimilarity(
            double[] sentence1Vector,
            double[] sentence2Vector)
    {
        // Initialize the result
        
        double similarity = 0.0;

        // We check the validity of the word vectors. They could be null if
        // any word is not contained in the vocabulary of the embedding.
        
        if ((sentence1Vector != null) && (sentence2Vector != null))
        {
            // We compute the cosine similarity function (dot product)
            
            for (int i = 0; i < sentence1Vector.length; i++)
            {
                similarity += sentence1Vector[i] * sentence2Vector[i];
            }
            
            // We divide by the vector norms
            
            similarity /= (getVectorNorm(sentence1Vector)
                        * getVectorNorm(sentence2Vector));
        }
        
        // Return the result
        
        return (similarity);
    }
    
    /**
     * This function computes the Euclidean norm of the input vector
     * @param vector
     * @return 
     */
    
    public static double getVectorNorm(
        double[]    vector)
    {
        double norm = 0.0;  // Returned value
        
        // We compute the acumulated square-coordinates
        
        for (int i = 0; i < vector.length; i++)
        {
            norm += vector[i] * vector[i];
        }
        
        // Finally, we compute the square root
        
        norm = Math.sqrt(norm);
        
        // We return the result
        
        return (norm);
    }
    
    /**
     * This method initializes the semantic vectors.
     * Each vector has the words from the dictionary vector.
     * If the word exists in the sentence of the semantic vector, the value is 1.
     * 
     * @param lstWordsSentence1
     * @param lstWordsSentence2
     * @throws FileNotFoundException 
     */
    
    private double[] constructSemanticVector(
            ArrayList<String>   dictionary,
            String[]            lstWordsSentence,
            String              ontology) throws FileNotFoundException, Exception
    {
        // Initialize the semantic vector
        
        double[] semanticVector = new double[dictionary.size()];
        
        // Convert arrays to set to facilitate the operations 
        // (this method do not preserve the word order)
        
        Set<String> setWordsSentence1 = new HashSet<>(Arrays.asList(lstWordsSentence));

        // For each list of words of a sentence
        // If the value is in the sentence, the value of the semantic vector will be 1.
        
        int count = 0;
        for (String word : dictionary)
        {
            // We check if the first sentence contains the word
            double wordVectorComponent;
            
            if(setWordsSentence1.contains(word))
            {
                wordVectorComponent = 1.0;
            }
            else
            {
                wordVectorComponent = getWordSimilarityScore(word, lstWordsSentence, ontology);
            }
            
            semanticVector[count] = wordVectorComponent;
            count++;
        } 
        
        // Return the result
        
        return (semanticVector);
    }
    
    /**
     * Get the maximum similarity value comparing a word with a list of words.
     * 
     * @param word
     * @param lstWordsSentence
     * @return double
     */
    
    private double getWordSimilarityScore(
            String              word,
            String[]            lstWordsSentence,
            String              ontology) throws Exception
    {
        // Initialize the result
        
        double maxValue = 0.0;
        
        // Iterate the dictionary and compare the similarity 
        // between the pivot word and the dictionary words to get the maximum value.
        
        for (String wordDict : lstWordsSentence)
        {
            // Get the similarity between the words
            
            double similarityScore = getSimilarityWordPairs(word, wordDict, ontology);
            
            // If the returned value is greater, set the new similarity value
            
            maxValue = maxValue < similarityScore ? similarityScore : maxValue;
        }
        
        // Return the result
        
        return (maxValue);
    }
    
    /**
     * This function returns the degree of similarity between two CUI concepts or words.
     * @param strFirstConceptId
     * @param strSecondConceptId
     * @return 
     */

    private double getSimilarityWordPairs(
            String  strFirstConceptId,
            String  strSecondConceptId,
            String  ontology) throws Exception
    {
        // We initialize the output
        
        double similarity = 0.0;
        
        // We compute the similarity by its ontology
        
        switch (ontology) {
            
            case "umls":
                
                // We get the similarity measure
        
                if(m_Snomedtaxonomy != null)
                {
                    // We compute the similarity using the active ontology
                
                    if(isCuiCode(strFirstConceptId) && isCuiCode(strSecondConceptId))
                        similarity = getSnomedSimilarity(strFirstConceptId, strSecondConceptId);
                }
                else if(m_MeSHtaxonomy != null)
                {
                    // We compute the similarity using the active ontology
                
                    if(isCuiCode(strFirstConceptId) && isCuiCode(strSecondConceptId))
                        similarity = getMeSHSimilarity(strFirstConceptId, strSecondConceptId);
                }
                
                break;
                
            case "wordnet":
                
                // We compute the similarity using the active ontology
                
                if(m_wordnet.contains(strFirstConceptId) & m_wordnet.contains(strSecondConceptId))
                {
                    similarity = getWordNetSimilarity(strFirstConceptId, strSecondConceptId);
                }
                
                break;
                
            case "none":
                
                // In this case, if the concept is in the array, the similarity value is 1, else the similarity value is 0
                
                if(strFirstConceptId.equals(strSecondConceptId))
                    similarity = 1.0;
                else
                    similarity = 0.0;
                
                break; 
            
            case "mixed":
            default:
                
                // If both concepts are CUIs codes, compute similarity values
        
                if(isCuiCode(strFirstConceptId) && isCuiCode(strSecondConceptId))
                {
                    // We get the similarity measure
        
                    if(m_Snomedtaxonomy != null)
                    {
                        // We compute the similarity using the active ontology

                        similarity = getSnomedSimilarity(strFirstConceptId, strSecondConceptId);
                    }
                    else if(m_MeSHtaxonomy != null)
                    {
                        // We compute the similarity using the active ontology

                        similarity = getMeSHSimilarity(strFirstConceptId, strSecondConceptId);
                    }
                }
                else if(m_wordnet.contains(strFirstConceptId) & m_wordnet.contains(strSecondConceptId))
                {
                    // If the concepts exists in WordNet, compute the similiarity
                    // We compute the similarity using the active ontology

                    similarity = getWordNetSimilarity(strFirstConceptId, strSecondConceptId);
                }
                
                break;
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * Constructs the dictionary set with all the distinct words from the sentences.
     * 
     * @param lstWordsSentence1
     * @param lstWordsSentence2
     * @throws FileNotFoundException 
     */
    
    private ArrayList<String> constructDictionaryList(
            String[] lstWordsSentence1, 
            String[] lstWordsSentence2) throws FileNotFoundException
    {
        // Initialize the set
        
        ArrayList<String> dictionary = null;
        
        // Create a linked set with the ordered union of the two sentences
        
        Set<String> setOrderedWords = new LinkedHashSet<>();
        setOrderedWords.addAll(Arrays.asList(lstWordsSentence1));
        setOrderedWords.addAll(Arrays.asList(lstWordsSentence2));
        
        // Copy the linked set to the arraylist
        
        dictionary = new ArrayList<>(setOrderedWords);
        
        // Return the result
        
        return (dictionary);
    }
    
   /**
     * This function returns the degree of similarity between two CUI concepts
     * evaluated on MeSH.
     * @param strFirstUmlsCUI
     * @param strSecondUmlsCUI
     * @return 
     */

    private double getMeSHSimilarity(
            String  strFirstUmlsCUI,
            String  strSecondUmlsCUI) throws Exception
    {
        // We initilizae the output
        
        double similarity = 0.0;
        
        // If both concepts are CUIs codes, compute similarity values
        
        if(isCuiCode(strFirstUmlsCUI) && isCuiCode(strSecondUmlsCUI))
        {
            // We get the SNOMED concepts evoked by each CUI

            IVertex[] firstVertexes = m_MeshOntology.getTaxonomyNodesForUmlsCUI(strFirstUmlsCUI.toUpperCase());
            IVertex[] secondVertexes = m_MeshOntology.getTaxonomyNodesForUmlsCUI(strSecondUmlsCUI.toUpperCase());

            // We check the existence oif SNOMED concepts associated to the CUIS

            if ((firstVertexes.length > 0)
                    && (secondVertexes.length > 0))
            {
                // We initialize the maximum similarity

                double maxSimilarity = Double.NEGATIVE_INFINITY;

                // We compare all pairs of evoked SNOMED concepts

                for (IVertex vertex1: firstVertexes)
                {
                    for (IVertex vertex2: secondVertexes)
                    {
                        double ontoSimilarity = m_wordSimilarityMeasureTypeUMLS.getSimilarity(
                                                    vertex1, vertex2);

                        // We update the maximum similarity

                        if (ontoSimilarity > maxSimilarity) maxSimilarity = ontoSimilarity;
                    }
                }

                // We assign the output similarity value

                similarity = maxSimilarity;
            }
        }
        
        // We return the result
        
        return (similarity);
    }

    /**
     * This function returns the degree of similarity between two CUI concepts
     * evaluated on MeSH.
     * @param strFirstUmlsCUI
     * @param strSecondUmlsCUI
     * @return 
     */

    private double getSnomedSimilarity(
            String  strFirstUmlsCUI,
            String  strSecondUmlsCUI) throws Exception
    {
        // We initilizae the output
        
        double similarity = 0.0;
        
        // If both concepts are CUIs codes, compute similarity values
        
        if(isCuiCode(strFirstUmlsCUI) && isCuiCode(strSecondUmlsCUI))
        {
            // We get the SNOMED concept nodes in thetaxonbomy which are evokedby the CUIs

            Set<IVertex> cuiVertexes1 = m_SnomedOntology.getTaxonomyVertexSetForUmlsCUI(strFirstUmlsCUI.toUpperCase());
            Set<IVertex> cuiVertexes2 = m_SnomedOntology.getTaxonomyVertexSetForUmlsCUI(strSecondUmlsCUI.toUpperCase());

            // We get the similarity between both CUIs

            similarity = m_UBSMMeasure.getSimilarity(cuiVertexes1, cuiVertexes2);
            
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * Get the similarity of two words using a Wordnet-based similarity measure
     * @param measure
     * @param word1
     * @param word2
     * @return
     * @throws Exception 
     */
    
    private double getWordNetSimilarity(
            String              word1, 
            String              word2) throws Exception
    {
        // Initialize the similarity values

        double maxSimilarity = 0.0;

        // If the concepts exists in WordNet, compute the similiarity
        
        if(m_wordnet.contains(word1) & m_wordnet.contains(word2))
        {
            // Vertexes corresponding to the concepts evoked by the word1
            
            IVertexList word1Concepts;  

            // Vertexes corresponding to the concepts evoked by the word2
            
            IVertexList word2Concepts;  
                                    
            // We obtain the concepts evoked by the words 
            
            word1Concepts = m_wordnetTaxonomy.getVertexes().getByIds(
                                m_wordnet.getWordSynsetsID(word1));

            word2Concepts = m_wordnetTaxonomy.getVertexes().getByIds(
                                m_wordnet.getWordSynsetsID(word2));

            // We compute the similarity among all the pairwise
            // combinations of Synsets (cartesian product)

//            simValue = m_wordSimilarityMeasureTypeWordnet.getHighestPairwiseSimilarity(
//                                word1Concepts, word2Concepts);

            // We compute the similarity for each pair of tree nodes

            for (IVertex vertex1: word1Concepts)
            {
                for (IVertex vertex2: word2Concepts)
                {
                    double simValue = m_wordSimilarityMeasureTypeWordnet.getSimilarity(
                                                vertex1, vertex2);

                    // We update the maximum similarity

                    if (simValue > maxSimilarity) maxSimilarity = simValue;
                }
            }

            // We clear the vertex lists

            word1Concepts.clear();
            word2Concepts.clear();
        }
        
        // Return the value
        
        return (maxSimilarity);
    }
    
    /**
     * Filter if a String is or not a CUI code
     */
    
    private boolean isCuiCode(String word)
    {
        return (word.matches("C\\d\\d\\d\\d\\d\\d\\d") || word.matches("c\\d\\d\\d\\d\\d\\d\\d"));
    }
    
    /**
     * Unload the ontology
     */
    
    @Override
    public void clear()
    {
        // We release the resources of the base class
        
        super.clear();
    }
}