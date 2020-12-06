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
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertexList;
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

class WBSMMeasure extends SentenceSimilarityMeasure
{
    // Word Similarity measure used for calculating similarity between words.
    
    private final ISimilarityMeasure m_wordSimilarityMeasure;
    
    // WordNetDB and taxonomy for computing the WordNet-based word similarity measures.
    
    private final IWordNetDB  m_wordnet;            // WordNet DB
    private final ITaxonomy   m_wordnetTaxonomy;    // WordNet taxonomy
    
    /**
     * User label which is shown in all raw matrix results
     */
    
    private final String m_strLabel;
    
    /**
     * IC model used to evaluate the associated IC-based word similarity measure
     */
    
    private final ITaxonomyInfoConfigurator m_ICmodel;
    
    /**
     * Constructor
     * @param preprocesser 
     * @param strWordNet_Dir Path to WordNet directory
     */
    
    WBSMMeasure(
            String                      strLabel,
            IWordProcessing             preprocesser,
            IWordNetDB                  wordnet,
            ITaxonomy                   wordnetTaxonomy,
            SimilarityMeasureType       wordSimilarityType,
            IntrinsicICModelType        icModelType) throws Exception
    {
        // We intialize the base class
        
        super(preprocesser);
        
        // Initialize the WordNetDB, taxonomy and IC model
        
        m_wordnet = wordnet;
        m_wordnetTaxonomy = wordnetTaxonomy;
        m_ICmodel = ICModelsFactory.getIntrinsicICmodel(icModelType);
        
         // We set the IC model in the taxonomy
        
        if (wordSimilarityType != SimilarityMeasureType.Rada)
        {
            m_ICmodel.setTaxonomyData(m_wordnetTaxonomy);
            // m_wordnetTaxonomy.computeCachedAncestorSet(true);
        }
        
        // Initialize the word similarity measure 
        
        m_wordSimilarityMeasure = MeasureFactory.getMeasure(m_wordnetTaxonomy, wordSimilarityType);

        // We save the label

        m_strLabel = strLabel;
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
        
        if (m_ICmodel != null) m_ICmodel.setTaxonomyData(m_wordnetTaxonomy);
    }
    
    /**
     * Return the current method.
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return (SentenceSimilarityMethod.WBSMMeasure);
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
        
        // We initialize the dictionary vector
        
        ArrayList<String> dictionary = null;
        
        // Preprocess the sentences and get the tokens for each sentence
        
        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
        
        // 1. Construct the joint set of distinct words from S1 and S2 (dictionary)
                
        dictionary = constructDictionaryList(lstWordsSentence1, lstWordsSentence2);
        
        // 2. Initialize and compute the semantic vectors.
        
        semanticVector1 = constructSemanticVector(dictionary, lstWordsSentence1);
        semanticVector2 = constructSemanticVector(dictionary, lstWordsSentence2);

        
        // 3. Compute the cosine similarity between the semantic vectors
        
        similarity = computeCosineSimilarity(semanticVector1, semanticVector2);
        
        // Return the similarity value
        
        return (similarity);
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
            String[]            lstWordsSentence) throws Exception
    {
        // Initialize the result
        
        double maxValue = 0.0;
        
        // Iterate the dictionary and compare the similarity 
        // between the pivot word and the dictionary words to get the maximum value.
        
        for (String wordDict : lstWordsSentence)
        {
            // Get the similarity between the words
            
            double similarityScore = getSimilarityWordPairs(word, wordDict);
            
            // If the returned value is greater, set the new similarity value
            
            maxValue = maxValue < similarityScore ? similarityScore : maxValue;
        }
        
        // Return the result
        
        return (maxValue);
    }
    
    /**
     * Get the similarity of two words using a Wordnet-based similarity measure
     * @param measure
     * @param word1
     * @param word2
     * @return
     * @throws Exception 
     */
    
    private double getSimilarityWordPairs(
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

            simValue = m_wordSimilarityMeasure.getHighestPairwiseSimilarity(
                                word1Concepts, word2Concepts);

            // We clear the vertex lists

            word1Concepts.clear();
            word2Concepts.clear();
        }
        
        // Return the value
        
        return (simValue);
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
            
            similarity /= (MeasureFactory.getVectorNorm(sentence1Vector)
                        * MeasureFactory.getVectorNorm(sentence2Vector));
        }
        
        // Return the result
        
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
            String[]            lstWordsSentence) throws FileNotFoundException, Exception
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
                        getWordSimilarityScore(word, lstWordsSentence);
            
            semanticVector[count] = wordVectorComponent;
            count++;
        } 
        
        // Return the result
        
        return (semanticVector);
    }
    
    /**
     * This function releases all resources used by the measure. Once this
     * function is called the measure is completely disabled.
     */
    
    @Override
    public void clear()
    {     
       // We release the resources of the base class
       
       super.clear();
    }
}