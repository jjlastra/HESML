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
import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.mesh.IMeSHOntology;
import hesml.taxonomyreaders.obo.IOboConcept;
import hesml.taxonomyreaders.obo.IOboOntology;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
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

class UBSMMeasure extends SentenceSimilarityMeasure
{
    // Word Similarity measure used for calculating similarity between words.
    
    private ISimilarityMeasure m_wordSimilarityMeasure;
    
    /**
     * Taxonomy and vertexes contained in the HESML taxonomy encoding SNOMED
     */
    
    private IVertexList m_Vertexes;
    private ITaxonomy   m_taxonomy;
    
    /**
     * User label which is shown in all raw matrix results
     */
    
    private final String m_strLabel;
    
    /**
     * IC model used to evaluate the associated IC-based word similarity measure
     */
    
    private final ITaxonomyInfoConfigurator m_ICmodel;
    
    /**
     * MeSH ontology loaded in memory
     */
    
    private IMeSHOntology   m_MeshOntology;
    
    /**
     * SNOMED-CT ontology
     */
    
    private ISnomedCtOntology   m_SnomedOntology;
    
    /**
     * OBO ontology
     */
    
    private IOboOntology    m_OboOntology;
    
    /**
     * Constructor for SNOMED-CT ontology
     * @param preprocesser 
     * @param strWordNet_Dir Path to WordNet directory
     */
    
    UBSMMeasure(
            String                      strLabel,
            IWordProcessing             preprocesser,
            ISnomedCtOntology           snomedOntology,
            SimilarityMeasureType       wordSimilarityType,
            IntrinsicICModelType        icModelType) throws Exception
    {
        // We intialize the base class
        
        super(preprocesser);
        
        // Initialize the WordNetDB, taxonomy and IC model
        
        m_SnomedOntology = snomedOntology;
        
        m_ICmodel = ICModelsFactory.getIntrinsicICmodel(icModelType);
        
        m_taxonomy = m_SnomedOntology.getTaxonomy();

        m_Vertexes = m_taxonomy.getVertexes();
        
        // Initialize the word similarity measure 
        
        setSimilarityMeasure(wordSimilarityType);

        // We save the label

        m_strLabel = strLabel;
    }
    
    /**
     * Constructor for MESH ontology
     * @param preprocesser 
     * @param strWordNet_Dir Path to WordNet directory
     */
    
    UBSMMeasure(
            String                      strLabel,
            IWordProcessing             preprocesser,
            IMeSHOntology               meshOntology,
            SimilarityMeasureType       wordSimilarityType,
            IntrinsicICModelType        icModelType) throws Exception
    {
        // We intialize the base class
        
        super(preprocesser);
        
        // Initialize the WordNetDB, taxonomy and IC model
        
        m_MeshOntology = meshOntology;
        
        m_ICmodel = ICModelsFactory.getIntrinsicICmodel(icModelType);
        
        m_taxonomy = m_MeshOntology.getTaxonomy();
        m_Vertexes = m_taxonomy.getVertexes();
        
        // Initialize the word similarity measure 
        
        setSimilarityMeasure(wordSimilarityType);
        
        // We save the label

        m_strLabel = strLabel;
    }
    
    /**
     * Constructor for Obo-based ontology
     * @param preprocesser 
     * @param strWordNet_Dir Path to WordNet directory
     */
    
    UBSMMeasure(
            String                      strLabel,
            IWordProcessing             preprocesser,
            IOboOntology                oboOntology,
            SimilarityMeasureType       wordSimilarityType,
            IntrinsicICModelType        icModelType) throws Exception
    {
        // We intialize the base class
        
        super(preprocesser);
        
        // Initialize the WordNetDB, taxonomy and IC model
        
        m_OboOntology = oboOntology;
        
        // We load the IC model 
        
        m_ICmodel = ICModelsFactory.getIntrinsicICmodel(icModelType);
        
        m_taxonomy = m_OboOntology.getTaxonomy();
        m_Vertexes = m_taxonomy.getVertexes();
        
        // Initialize the word similarity measure 
        
        setSimilarityMeasure(wordSimilarityType);
        
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
     * This function sets the active semantic measure used by the library
     * to compute the semantic similarity between concepts.
     * @param icModel
     * @param measureType 
     * @return true if the measure is allowed
     */

    private void setSimilarityMeasure(
            SimilarityMeasureType   measureType) throws Exception
    {      
        // We set the IC model in the taxonomy
        
        if (measureType != SimilarityMeasureType.Rada)
        {
            System.out.println("Setting the " + m_ICmodel.toString() + " IC model into the taxonomy");
            
            m_ICmodel.setTaxonomyData(m_taxonomy);
            // m_taxonomy.computeCachedAncestorSet(true);
        }
        
        // We get the similarity measure
        
        m_wordSimilarityMeasure = MeasureFactory.getMeasure(m_taxonomy, measureType);
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
        
        if (m_ICmodel != null) m_ICmodel.setTaxonomyData(m_taxonomy);
    }
    
    /**
     * Return the current method.
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return (SentenceSimilarityMethod.UBSMMeasure);
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
        
        // 2. Initialize the semantic vectors.
        
        semanticVector1 = constructSemanticVector(dictionary, lstWordsSentence1);
        semanticVector2 = constructSemanticVector(dictionary, lstWordsSentence2);

        // 3. Use WordNet to construct the semantic vector
        
        semanticVector1 = computeSemanticVector(semanticVector1, dictionary);
        semanticVector2 = computeSemanticVector(semanticVector2, dictionary);
        
        // 4. Compute the cosine similarity between the semantic vectors
        
        similarity = computeCosineSimilarity(semanticVector1, semanticVector2);
        
        // Return the similarity value
        
        return (similarity);
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
            String[]            lstWordsSentence) throws FileNotFoundException
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
            
            double wordVectorComponent = setWordsSentence1.contains(word) ? 1.0 : 0.0;

            semanticVector[count] = wordVectorComponent;
            count++;
        } 
        
        // Return the result
        
        return (semanticVector);
    }
    
    /**
     * Compute the values from the semantic vector in the positions with zeros.
     * 
     * For each vector position, check if the value is zero.
     * If the value is zero, compute the word similarity with the dictionary
     * using word similarity measures and get the maximum value.
     * 
     * @param semanticVector
     * @return 
     */
    
    private double[] computeSemanticVector(
            double[]            semanticVector,
            ArrayList<String>   dictionary) throws Exception
    {
        // Initialize the result
        
        double[] semanticVectorComputed = new double[semanticVector.length];
        
        // Compute the semantic vector value in each position
        
        for (int i = 0; i < semanticVector.length; i++)
        {
            // If the value is zero, get the word similarity
            
            double wordVectorComponent = semanticVector[i] == 1.0 ? 1.0 : 
                        getWordSimilarityScore(dictionary.get(i), dictionary);
  
            semanticVectorComputed[i] = wordVectorComponent;
        }

        // Return the result
        
        return (semanticVectorComputed);
    }
    
    /**
     * Get the maximum similarity value comparing a word with a list of words.
     * 
     * @param word
     * @param dictionary
     * @return double
     */
    
    private double getWordSimilarityScore(
            String              word,
            ArrayList<String>   dictionary) throws Exception
    {
        // Initialize the result
        
        double maxValue = 0.0;
        
        // Iterate the dictionary and compare the similarity 
        // between the pivot word and the dictionary words to get the maximum value.
        
        for (String wordDict : dictionary)
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
     * This function returns the degree of similarity between two CUI concepts.
     * @param strFirstConceptId
     * @param strSecondConceptId
     * @return 
     */

    private double getSimilarityWordPairs(
            String  strFirstConceptId,
            String  strSecondConceptId) throws Exception
    {
        // We initialize the output
        
        double similarity = Double.NaN;
        
        // If both concepts are CUIs codes, compute similarity values
        
        if(isCuiCode(strFirstConceptId) && isCuiCode(strSecondConceptId))
        {
            // We compute the similarity using the active ontology
        
            if (m_MeshOntology != null)
            {
                similarity = getMeSHSimilarity(strFirstConceptId, strSecondConceptId);
            }
            else if (m_SnomedOntology != null)
            {
                similarity = getSnomedSimilarity(strFirstConceptId, strSecondConceptId);
            }
            else if (m_OboOntology != null)
            {
                // We get the OBO concepts

                IOboConcept concept1 = m_OboOntology.getConceptById(strFirstConceptId);
                IOboConcept concept2 = m_OboOntology.getConceptById(strSecondConceptId);

                // We check the existence of both concepts

                if ((concept1 != null) && (concept2 != null))
                {
                    similarity = m_wordSimilarityMeasure.getSimilarity(
                                    m_Vertexes.getById(concept1.getTaxonomyNodeId()),
                                    m_Vertexes.getById(concept2.getTaxonomyNodeId()));
                }
            }
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
     * evaluated on SNOMED.
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
        
        // We get the SNOMED concepts evoked by each CUI
        
        IVertex[] firstVertexes = m_SnomedOntology.getTaxonomyVertexesForUmlsCUI(strFirstUmlsCUI);
        IVertex[] secondVertexes = m_SnomedOntology.getTaxonomyVertexesForUmlsCUI(strSecondUmlsCUI);
        
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
                    double snomedSimilarity = m_wordSimilarityMeasure.getSimilarity(
                                                vertex1, vertex2);
                
                    // We update the maximum similarity

                    if (snomedSimilarity > maxSimilarity) maxSimilarity = snomedSimilarity;
                }
            }
            
            // We assign the output similarity value
            
            similarity = maxSimilarity;
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

    private double getMeSHSimilarity(
            String  strFirstUmlsCUI,
            String  strSecondUmlsCUI) throws Exception
    {
        // We initilizae the output
        
        double similarity = 0.0;
        
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
                    double meshSimilarity = m_wordSimilarityMeasure.getSimilarity(
                                                vertex1, vertex2);
                
                    // We update the maximum similarity

                    if (meshSimilarity > maxSimilarity) maxSimilarity = meshSimilarity;
                }
            }
            
            // We assign the output similarity value
            
            similarity = maxSimilarity;
        }
        
        // We return the result
        
        return (similarity);
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
        
        // We unload the ontologies
        
        if (m_SnomedOntology != null) m_SnomedOntology.clear();
        if (m_MeshOntology != null) m_MeshOntology.clear();
        if (m_OboOntology != null) m_OboOntology.clear();
        
        m_taxonomy = null;
        m_Vertexes = null;
    }
}