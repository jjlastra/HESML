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

import hesml.sts.measures.ComMixedVectorsMeasureType;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class implements the LiMixed method
 * 
 * @author alicia
 */

class ComMixedVectorsMeasure extends SentenceSimilarityMeasure
{
    // User label which is shown in all raw matrix results
    
    private final String m_strLabel;
    
    // We initialize the String measure
    
    private final ISentenceSimilarityMeasure  m_stringMeasure;
    
    // Lambda value
    
    private final Double m_lambda;
    
    /**
     * Constructor for none ontology (LiMixed measure)
     * @param preprocesser 
     * @param strWordNet_Dir Path to WordNet directory
     */
    
    ComMixedVectorsMeasure(
            String                      strLabel,
            IWordProcessing             preprocesser,
            ISentenceSimilarityMeasure  stringMeasure,
            Double                      lambda,
            ComMixedVectorsMeasureType  comMixedVectorsMeasureType) throws Exception
    {
        // We intialize the base class
        
        super(preprocesser);
        
        // Initialize the string measure
        
        m_stringMeasure = stringMeasure;
        
        // Initialize the lambda value
        
        m_lambda = lambda;
        
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
        return (SentenceSimilarityFamily.String);
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
        
        String[] lstWordsSentence1 = null;
        String[] lstWordsSentence2 = null;

        // We get the word tokens
                
        lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);

        dictionary = constructDictionaryList(lstWordsSentence1, lstWordsSentence2);
        
        // 2. Initialize the semantic vectors.
        
        semanticVector1 = constructSemanticVector(dictionary, lstWordsSentence1);
        semanticVector2 = constructSemanticVector(dictionary, lstWordsSentence2);
        
        // 3. Compute the cosine similarity between the semantic vectors

        double liSimilarity = computeCosineSimilarity(semanticVector1, semanticVector2);
            
        // 4. Compute the string-based similarity value
        
        double stringSimilarity = m_stringMeasure.getSimilarityValue(strRawSentence1, strRawSentence2);

        // if the 
        
        if(liSimilarity == 0.0)
        {
            // Compute the value
        
            similarity = stringSimilarity;
        }
        else
        {
            // Compute the value
        
//            similarity = (liSimilarity * m_lambda) + (stringSimilarity * (1.0 - m_lambda));
            similarity = (liSimilarity + stringSimilarity) / 2;
        }
        
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
            double wordVectorComponent;
            
            if(setWordsSentence1.contains(word))
            {
                wordVectorComponent = 1.0;
            }
            else
            {
                wordVectorComponent = getWordSimilarityScore(word, lstWordsSentence);
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
     * This function returns the degree of similarity between two CUI concepts or words.
     * @param strFirstConceptId
     * @param strSecondConceptId
     * @return 
     */

    private double getSimilarityWordPairs(
            String  strFirstConceptId,
            String  strSecondConceptId) throws Exception
    {
        // We initialize the output
        
        double similarity = 0.0;
        
        // We compute the similarity by its ontology
        
        // In this case, if the concept is in the array, the similarity value is 1, else the similarity value is 0
                
        if(strFirstConceptId.equals(strSecondConceptId))
            similarity = 1.0;
        else
            similarity = 0.0;
        
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
     * Unload the ontology
     */
    
    @Override
    public void clear()
    {
        // We release the resources of the base class
        
        super.clear();
    }
}