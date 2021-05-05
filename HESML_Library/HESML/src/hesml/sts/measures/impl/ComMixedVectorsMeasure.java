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
import hesml.measures.ISimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.sts.measures.ComMixedVectorsMeasureType;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SWEMpoolingMethod;
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
    
    private final IVertexList m_Snomedvertexes;
    private final ITaxonomy   m_Snomedtaxonomy;
    
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
    
    // Specific method implemented

    private final SWEMpoolingMethod    m_poolingMethod;
    
    // COM Mixed vector measure type
    
    private final ComMixedVectorsMeasureType  m_comMixedVectorsMeasureType;
    
    
    /**
     * Constructor for MESH ontology
     * @param preprocesser 
     * @param strWordNet_Dir Path to WordNet directory
     */
    
    ComMixedVectorsMeasure(
            String                      strLabel,
            IWordProcessing             preprocesser,
            ISnomedCtOntology           snomedCtOntology,
            IVertexList                 vertexes,
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
        
        super(preprocesser);
        
        // Initialize the WordNetDB, taxonomy and IC model
        
        m_ICmodelUMLS = ICModelsFactory.getIntrinsicICmodel(icModelType);
        m_ICmodelWordNet = ICModelsFactory.getIntrinsicICmodel(icModelType);
        
        m_SnomedOntology = snomedCtOntology;
                
        m_Snomedtaxonomy = taxonomy;
        m_Snomedvertexes = vertexes;
        
        m_wordnet = wordnet;
        m_wordnetTaxonomy = wordnetTaxonomy;
        
        // Set the COM Mixed method
        
        m_comMixedVectorsMeasureType = comMixedVectorsMeasureType;
        
        // Initialize the string measure
        
        m_stringMeasure = stringMeasure;
        
        // Initialize the lambda value
        
        m_lambda = lambda;
        
        // Initialize the word similarity measures 
        
        setSimilarityMeasureWordNet(wordSimilarityMeasureTypeWordnet);
        setSimilarityMeasureUMLS(wordSimilarityMeasureTypeUMLS);
        
        // We save the label

        m_strLabel = strLabel;
        
        m_poolingMethod = SWEMpoolingMethod.Min;
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
            m_ICmodelUMLS.setTaxonomyData(m_Snomedtaxonomy);
            m_Snomedtaxonomy.computeCachedAncestorSet(true);
        }
        
        // We get the similarity measure
        
        m_wordSimilarityMeasureTypeUMLS = MeasureFactory.getMeasure(m_Snomedtaxonomy, measureType);
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
        
        // Preprocess the sentences and get the tokens for each sentence
        
        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
        
        // 1. Construct the joint set of distinct words from S1 and S2 (dictionary)
                
        dictionary = constructDictionaryList(lstWordsSentence1, lstWordsSentence2);
        
        // 2. Initialize the semantic vectors.
        
        if(m_comMixedVectorsMeasureType == ComMixedVectorsMeasureType.PooledAVG ||
                m_comMixedVectorsMeasureType == ComMixedVectorsMeasureType.PooledMin)
        {
            semanticVector1_umls = constructSemanticVector(dictionary, lstWordsSentence1, "umls");
            semanticVector2_umls = constructSemanticVector(dictionary, lstWordsSentence2, "umls");

            semanticVector1_wordnet = constructSemanticVector(dictionary, lstWordsSentence1, "wordnet");
            semanticVector2_wordnet = constructSemanticVector(dictionary, lstWordsSentence2, "wordnet");

            semanticVector1 = poolVectors(semanticVector1_umls,semanticVector1_wordnet);
            semanticVector2 = poolVectors(semanticVector2_umls,semanticVector2_wordnet);
            
        }
        else if(m_comMixedVectorsMeasureType == ComMixedVectorsMeasureType.WordNet)
        {
            semanticVector1 = constructSemanticVector(dictionary, lstWordsSentence1, "wordnet");
            semanticVector2 = constructSemanticVector(dictionary, lstWordsSentence2, "wordnet");

        }
        else if(m_comMixedVectorsMeasureType == ComMixedVectorsMeasureType.UMLS)
        {
            semanticVector1 = constructSemanticVector(dictionary, lstWordsSentence1, "umls");
            semanticVector2 = constructSemanticVector(dictionary, lstWordsSentence2, "umls");
        }
            
        // 3. Compute the cosine similarity between the semantic vectors
        
        double ontologySimilarity = computeCosineSimilarity(semanticVector1, semanticVector2);
        
        // Compute the string-based similarity value
        
        double stringSimilarity = m_stringMeasure.getSimilarityValue(strRawSentence1, strRawSentence2);
        
        // Compute the COM value
        
        similarity = (ontologySimilarity*m_lambda) + (stringSimilarity*(1-m_lambda));
        
        // Return the similarity value
        
        return (similarity);
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
                case Average:
                case Sum:

                    sentenceVector[i] += v2[i];

                    break;

                case Max:

                    sentenceVector[i] = Math.max(sentenceVector[i], v2[i]);

                    break;

                case Min:

                    sentenceVector[i] = Math.min(sentenceVector[i], v2[i]);

                    break;
            }
            
            // We increment the non-null vector count

            vectorCount++;
            
        }
        
        // For the case of avergae pooling, we divde by the vector count @alicia: if (m_poolingMethod == Average)¿?
        
        if (m_poolingMethod == SWEMpoolingMethod.Average)
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
            
            double wordVectorComponent = setWordsSentence1.contains(word) ? 1.0 : 
                        getWordSimilarityScore(word, lstWordsSentence, ontology);
            
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
                
                // We compute the similarity using the active ontology
                
                if(isCuiCode(strFirstConceptId) && isCuiCode(strSecondConceptId))
                    similarity = getMeSHSimilarity(strFirstConceptId, strSecondConceptId);
                
                break;
                
            case "wordnet":
                
                // We compute the similarity using the active ontology
                
                if(m_wordnet.contains(strFirstConceptId) & m_wordnet.contains(strSecondConceptId))
                    similarity = getWordNetSimilarity(strFirstConceptId, strSecondConceptId);
                
                break;
                
            default:
                
                // If both concepts are CUIs codes, compute similarity values
        
                if(isCuiCode(strFirstConceptId) && isCuiCode(strSecondConceptId))
                {
                    // We compute the similarity using the active ontology

                    similarity = getMeSHSimilarity(strFirstConceptId, strSecondConceptId);
                }
                else 
                    if(m_wordnet.contains(strFirstConceptId) & m_wordnet.contains(strSecondConceptId))
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

            IVertex[] firstVertexes = m_SnomedOntology.getTaxonomyVertexesForUmlsCUI(strFirstUmlsCUI.toUpperCase());
            IVertex[] secondVertexes = m_SnomedOntology.getTaxonomyVertexesForUmlsCUI(strSecondUmlsCUI.toUpperCase());

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
        
        double simValue = 0.0;  

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

            simValue = m_wordSimilarityMeasureTypeWordnet.getHighestPairwiseSimilarity(
                                word1Concepts, word2Concepts);

            // We clear the vertex lists

            word1Concepts.clear();
            word2Concepts.clear();
        }
        
        // Return the value
        
        return (simValue);
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