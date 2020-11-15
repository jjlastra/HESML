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

import hesml.sts.measures.IStringBasedSentenceSimMeasure;
import hesml.sts.measures.SentenceSimilarityFamily;
import hesml.sts.measures.SentenceSimilarityMethod;
import hesml.sts.measures.StringBasedSentenceSimilarityMethod;
import hesml.sts.preprocess.IWordProcessing;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  This class implements the Block Distance Measure.
 * 
 * Also called rectilinear distance, taxicab norm, and Manhattan distance.
 *  
 *  Krause, Eugene F. 1986. Taxicab Geometry: 
 *  An Adventure in Non-Euclidean Geometry. Courier Corporation.
 * 
 * @author alicia
 */

class BlockDistanceMeasure extends SentenceSimilarityMeasure
    implements IStringBasedSentenceSimMeasure
{
    /**
     * label shown in all raw matrix results
     */
    
    private final String m_strLabel;
    
    /**
     * Constructor with parameters.
     * @param preprocesser 
     */
    
    BlockDistanceMeasure(
            String          strLabel,
            IWordProcessing preprocesser)
    {
        
        // We intialize the base class
        
        super(preprocesser);
        
        // We initialize the object
        
        m_strLabel = strLabel;
    }
    
    /**
     * This function returns the label used to identify the measure in
     * a raw matrix results. This string attribute is set by the users
     * to provide the column header name included in all results generated
     * by this measure. This attribute was especially defined to
     * provide a meaningful name to distinguish the measures based on
     * pre-trained model files.
     * @return label
     */
    
    @Override
    public String getLabel()
    {
        return (m_strLabel);
    }
    
    /**
     * This function returns the string-based sentence similarity method
     * which is implemented by any derived class.
     * @return 
     */
    
    @Override
    public StringBasedSentenceSimilarityMethod getStringBasedMethodType()
    {
        return (StringBasedSentenceSimilarityMethod.BlockDistance);
    }
    
    /**
     * This function returns the current similarity method
     * @return SentenceSimilarityMethod
     */
    
    @Override
    public SentenceSimilarityMethod getMethod()
    {
        return (SentenceSimilarityMethod.BlockDistance);
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
     * This function calculates the similarity value between two sentences.
     * 
     * @param strRawSentence1
     * @param strRawSentence2
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     * @throws InterruptedException 
     */
    
    @Override
    public double getSimilarityValue(
            String  strRawSentence1, 
            String  strRawSentence2) 
            throws IOException, FileNotFoundException, InterruptedException, Exception
    {
        // We initialize the output

        double similarity = 0.0;
        
        // We first calculate the distance between the sentences
        
        float distance = 0;
        
        // Get the tokens for each sentence

        String[] lstWordsSentence1 = m_preprocesser.getWordTokens(strRawSentence1);
        String[] lstWordsSentence2 = m_preprocesser.getWordTokens(strRawSentence2);
        
        // Hashmap to store the frequency that an element occurs in each sentence
        
        Map<String, Integer> mapOccurrencesSentence1 = getStringOccurrences(lstWordsSentence1);
        Map<String, Integer> mapOccurrencesSentence2 = getStringOccurrences(lstWordsSentence2);
        
        // Count the total of items in each sentence (sum of the map values)
        
        int countTotalWordsS1 = mapOccurrencesSentence1.values().stream().mapToInt(Integer::intValue).sum();
        int countTotalWordsS2 = mapOccurrencesSentence2.values().stream().mapToInt(Integer::intValue).sum();
       
        //Calculate the distance
        
        // Get the union set of both sentences (a dictionary)
                
        Set<String> unionSentencesSet = new HashSet<>();
        
        unionSentencesSet.addAll(mapOccurrencesSentence1.keySet());
        unionSentencesSet.addAll(mapOccurrencesSentence2.keySet());
        
        // for each word get the abs(v1 - v2) being:
        // v1 the number of times the actual word occurs in the sentence 1
        // v2 the number of times the actual word occurs in the sentence 2
        
        for (String word : unionSentencesSet) 
        {
            // Initialize the frequency a word appears in the sentences to 0
            
            int frequencyofWordInSentence1 = 0;
            int frequencyofWordInSentence2 = 0;
            
            // Get the number of occurences of the word in the first sentence
            
            Integer numberOfOccurences = mapOccurrencesSentence1.get(word);
            
            // If there is one or more occurrences, add to the frequency
            
            if (numberOfOccurences != null) 
            {
                frequencyofWordInSentence1 = numberOfOccurences;
            }
            
            numberOfOccurences = mapOccurrencesSentence2.get(word);
            
            if (numberOfOccurences != null) 
            {
                frequencyofWordInSentence2 = numberOfOccurences;
            }
            
            // Add the local distance to the total distance.
            
            distance += Math.abs(frequencyofWordInSentence1 - frequencyofWordInSentence2);
        }
        
        // We release the auxiliary sets
        
        unionSentencesSet.clear();
        mapOccurrencesSentence1.clear();
        mapOccurrencesSentence2.clear();

        // The similarity is calculated as 1 minus the normalized distance
        
        similarity = 1.0f - distance / (countTotalWordsS1 + countTotalWordsS2);
        
        // Return the similarity
        
        return (similarity);
    }
    
    /**
     * Get a HashMap with the number of occurrences for each word in the sentence.
     * 
     * For example: "this words this ..."
     * 
     * {{"this", 2}, {"word", 1}, ...}
     * 
     * @param lstWordsSentence list of words in the sentence
     * @return Map<String, Integer> string occurrences.
     */
    
    private Map<String, Integer> getStringOccurrences(
            String[]    lstWordsSentence)
    {
        // Initialize the output
        
        Map<String, Integer> occurrences = new HashMap<>(); 
  
        // For each word in the sentence, 
        // count the time the word appears in the sentence and store it.
        
        for (String word : lstWordsSentence) { 
            Integer numberOcurrencesofWordInSentence = occurrences.get(word); 
            occurrences.put(word, (numberOcurrencesofWordInSentence == null) ? 1 : numberOcurrencesofWordInSentence + 1); 
        } 
        
        // Return the result
        
        return (occurrences);
    }
}