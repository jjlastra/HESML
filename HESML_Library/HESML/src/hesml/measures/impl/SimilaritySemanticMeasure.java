/*
 * Copyright (C) 2016 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml.measures.impl;

// HESML references

import hesml.measures.*;
import hesml.taxonomy.*;

/**
 * This class implements a collection of common functions for all the
 * similarity measures.
 * @author Juan Lastra-Díaz
 */

abstract class SimilaritySemanticMeasure implements ISimilarityMeasure
{
    /**
     * Taxonomy
     */
    
    protected ITaxonomy   m_Taxonomy;

    /**
     * Constructor
     */
    
    SimilaritySemanticMeasure(
        ITaxonomy   taxonomy)
    {
        m_Taxonomy = taxonomy;
    }
    
    /**
     * This function returns the best similarity value for the Cartesian
     * product of both concept set. This function is overriden by the
     * Hadj Taieb et al (2014) measure in order to manage their second
     * measure which considers the number of synsets per word.
     * @param firstWordVertexes Vertexes associated to the concept set 1
     * @param secondWordVertexes Vertexes associated to the concept set 2
     * @return Best similarity value
     * @throws InterruptedException
     * @throws Exception 
     */
    
    @Override
    public double getHighestPairwiseSimilarity(
            IVertexList firstWordVertexes,
            IVertexList secondWordVertexes)
            throws InterruptedException, Exception
    {
        double  bestSimilarityValue = Double.NEGATIVE_INFINITY;  // Returned value
        
        double  similarity; // Similarity value
        
        // We search for the best similarity value
        
        for (IVertex leftVertex: firstWordVertexes)
        {
            for (IVertex rightVertex: secondWordVertexes)
            {
                // We compoute the similarity value between left and right vertexes
                
                similarity = getSimilarity(leftVertex, rightVertex);
                
                // We save the best value
                
                bestSimilarityValue = Math.max(similarity, bestSimilarityValue);
            }
        }
        
        // We return the result
        
        return (bestSimilarityValue);
    }
    
    /*
     * This function computes the logistic function of the
     * conditional probability models.
    */
    
    protected double logisticFun(
            double  kLogistic,
            double  argument)
    {
        double  output; // Returned value
        
        // We comptue the output of the function
        
        output = 1.0 / (1.0 + Math.exp(-kLogistic*(argument - 0.5)));
        
        // We return the value
        
        return (output);
    }
    
    /**
     * This function normalizes the similarity using the cosine-base
     * exponential mapping.
     * @param similarity
     * @return Transformed similarity
     */
    
    protected double getCosineNormSimilarity(
        double  similarity)
    {
        double  similarityNorm; // returned value
        
        // We normalize the similarity
        
        similarityNorm = 1.0 - Math.cos(0.5 * Math.PI * similarity);
        
        // We return the result
        
        return (similarityNorm);
    }
    
    /**
     * This function returns the associated taxonomy for the measure.
     * @return The taxonomy associated to the semantic measure
     */
    
    @Override
    public ITaxonomy getTaxonomy()
    {
        return (m_Taxonomy);
    }
    
    /**
     * This function returns the degree of similarity between the input vertexes.
     * @param left
     * @param right
     * @return The semantic distance between the nodes.
     */
    
    @Override
    public double getSimilarity(
            IVertex left,
            IVertex right) throws InterruptedException, Exception
    {
        double  similarity;   // Returned value

        SimilarityMeasureClass    funClass;   // Function class
        
        // We compute the distance
        
        similarity = compare(left, right);
        
        // We get the function class to convert it in a simkmilarity
        // if it was neeeded
        
        funClass = getMeasureClass();
        
        // We convert the distance to similarity when it is necessary
        
        if (funClass == SimilarityMeasureClass.Distance)
        {
            similarity = 1.0 - similarity / 2.0;
        }
        else if (funClass == SimilarityMeasureClass.Dissimilarity)
        {
            similarity = 1.0 - similarity;
        }
        
        // We return the result
        
        return (similarity);
    }
}
