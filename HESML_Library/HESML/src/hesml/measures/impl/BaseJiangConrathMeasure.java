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

package hesml.measures.impl;

// HESML references

import hesml.taxonomy.*;

/**
 * This class implements some common functions used by all the measures
 * derived from the Jiang-Conrath distance.
 * @author Juan Lastra-Díaz
 */

abstract class BaseJiangConrathMeasure extends SimilaritySemanticMeasure
{
    /**
     * Maxiumm distance from  root to any leaf node.
     */
    
    private double    m_MaxDistance;
    
    /**
     * Standard constructor
     * @param taxonomy 
     */

    BaseJiangConrathMeasure(ITaxonomy taxonomy) throws Exception
    {
        super(taxonomy);
    }
    
    /**
     * This function computes the maximum distance and stores
     * the value in the m_MaxDiatance attribute. Maximum distance
     * is defined as the highest IC value on the leaf vertexes (concepts)
     * set. It matches the djcmax value defined in the following paper.
     * 
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A novel family of IC-based similarity measures with a detailed
     * experimental survey on WordNet. Engineering Applications of Artificial
     * Intelligence Journal, 46, 140–153.
     */
    
    protected void computeMaxDistance() throws Exception
    {
        // Debug message
        
        System.out.println("Computing the maximum distance");
        
        // We initilizae the maxDistance
        
        m_MaxDistance = 0.0;
        
        // We get the root node
        
        IVertex root = m_Taxonomy.getVertexes().getAt(0);
        
        // We compute the distance to any leaf node
        
        for (IVertex vertex: m_Taxonomy.getVertexes())
        {
            if (vertex.isLeaf())
            {
                // We compute the distance to the root
                
                double distance = vertex.getICvalue();
                
                // Chekc for the maxium
                
                m_MaxDistance = Math.max(distance, m_MaxDistance);
            }
        }
     }
    
    /**
     * This function computes the standard Jiang-Conrath distance
     * as defined in the following paper.
     * 
     * Jiang, J. J., and Conrath, D. W. (1997).
     * Semantic similarity based on corpus statistics and lexical taxonomy.
     * In Proceedings of International Conference Research on
     * Computational Linguistics (ROCLING X) (pp. 19–33).
     * 
     * @param left
     * @param right
     * @return Jiang-Conrath distance
     * @throws InterruptedException
     * @throws Exception 
     */
    
    static double getClassicJiangConrathDist(
            IVertex left,
            IVertex right) throws InterruptedException, Exception
    {
        double  distance = 1e10;   // Returned value

        // We compute the MICA vertex
        
        IVertex micaVertex = left.getTaxonomy().getMICA(left, right);
       
        // We check the existence of the MICA vertex. It only fails when
        // the taxonomy has more than one root node.
        
        if (micaVertex != null)
        {
            distance = left.getICvalue() + right.getICvalue()
                    - 2.0 * micaVertex.getICvalue();
        }
        
        // We return the result
        
        return (distance);
    }
    
    /**
     * This function applies the cosine function to normalize
     * the distance value returned by the Jiang-Conrath distance.
     * @param distance
     * @return Normalized similarity
     */
    
    protected double convertToCosineNormSimilarity(
        double  distance)
    {
        // We normalize the distance
        
        double similarity = distanceToSimilarity(distance);
        
        // Aplicamos la correción coseno
        
        similarity = getCosineNormSimilarity(similarity);
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function linearly converts the distance values between two
     * vertexes (concepts).
     * @param distance
     * @return 
     */
    
    private double distanceToSimilarity(
        double  distance)
    {
        // We normalize the distance
        
        double similarity = 1 - distance / (2.0 * m_MaxDistance);
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function applies the cosine function to normalize
     * the distance value returned by the Jinag-Conrath distance.
     * @param distance
     * @return Normalized similarity
     */
    
    protected double getExpNormSimilarity(
        double  distance)
    {
        // We normalize the distance
        
        double similarity = distanceToSimilarity(distance);
        
        // We apply the exponential scaling
        
        similarity = Math.exp(similarity) - 1.0;
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function computes the logistic normalized similarity.
     * @param distance
     * @return Similarity value
     */
    
    protected double getLogisticNormSimilarity(
        double  distance)
    {
        double  k = 8.0;    // Logistic constant
                
        // We normalize the distance
        
        double similarity = distanceToSimilarity(distance);
        
        // We compute the logistic param
        
        double arg = -k * (similarity - 0.5);
        
        // We apply the logistic tuning function
        
        similarity = 1.0 / (1.0 + Math.exp(arg));

        // We return the result
        
        return (similarity);
    }
}
