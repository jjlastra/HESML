/*
<<<<<<< HEAD
 * Copyright (C) 2016-2022 Universidad Nacional de Educación a Distancia (UNED)
=======
 * Copyright (C) 2016-2021 Universidad Nacional de Educación a Distancia (UNED)
>>>>>>> master
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
 */

package hesml.measures.impl;

import hesml.measures.GroupwiseMetricType;
import hesml.measures.GroupwiseSimilarityMeasureType;
import hesml.measures.IGroupwiseSimilarityMeasure;
import hesml.measures.ISimilarityMeasure;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import java.util.Set;

/**
 * This class implements the basic groupwise similarity measure between
 * sets of concepts which is based on some metric between sets and a
 * any pairwise similairty measure.
 * @author Juan J. Lastra-Díaz (jlastra@invi.uned.es)
 */

class GroupwiseBasedOnPairwiseMeasure implements IGroupwiseSimilarityMeasure
{
    /**
     * Taxonomy containng all concepts
     */
    
    private ITaxonomy   m_taxonomy;
    
    /**
     * Pairwise similarity measure
     */
    
    private ISimilarityMeasure  m_pairwiseMeasure;
    
    /**
     * Metric used to produce the similarity value from the pairwise scores
     */
    
    GroupwiseMetricType m_groupwiseMetric;
    
    /**
     * Constructor
     * @param taxonomy
     * @param pairwiseMeasure
     * @param metricType 
     */
    
    GroupwiseBasedOnPairwiseMeasure(
            ITaxonomy           taxonomy,
            ISimilarityMeasure  pairwiseMeasure,
            GroupwiseMetricType metricType)
    {
        m_groupwiseMetric = metricType;
        m_taxonomy = taxonomy;
        m_pairwiseMeasure = pairwiseMeasure;
    }
    
    /**
     * This function returns the type of the measure.
     * @return 
     */

    @Override
    public GroupwiseSimilarityMeasureType getMeasureType()
    {
        return (GroupwiseSimilarityMeasureType.BasedOnPairwiseMeasure);
    }

    /**
     * This function returns the semantic measure between two set of
     * concepts associated to the input vertex sets.
     * @param left The first set of vertexes (concepts) 
     * @param right The second set of vertexes (concepts) 
     * @return 
     * @throws java.lang.InterruptedException 
     */
    
    @Override
    public double getSimilarity(
            Set<IVertex> left,
            Set<IVertex> right)
            throws InterruptedException, Exception
    {
        // We initialize the output value
        
        double similarity = 0.0;
        
        // We compute the similarity value according to the groupwise metric
        
        switch (m_groupwiseMetric)
        {
            case Maximum:
                
                similarity = maximumSimilarity(left, right);
                
                break;
                
            case Average:
                
                similarity = averageSimilarity(left, right);
                
                break;
                
            case BestMatchAverage:
                
                similarity = bestMatchAverageSimilarity(left, right);
                
                break;
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function computes the maximum pairwise similarity value as proposed
     * by Sevilla et al. [1] in formula 2.
     * [1] J.L. Sevilla, V. Segura, A. Podhorski, E. Guruceaga, J.M. Mato, 
     * L.A. Martínez-Cruz, F.J. Corrales, A. Rubio,
     * Correlation between gene expression and GO semantic similarity,
     * IEEE/ACM Trans. Comput. Biol. Bioinform. 2 (2005) 330–338.
     * 
     * @param left The first set of vertexes (concepts) 
     * @param right The second set of vertexes (concepts) 
     * @return 
     * @throws java.lang.InterruptedException 
     */
    
    private double maximumSimilarity(
            Set<IVertex> left,
            Set<IVertex> right) throws Exception
    {
        // We initialize the output value
        
        double similarity = m_pairwiseMeasure.getNullSimilarityValue();
        
        // We compute all pairwise similarity valies
        
        for (IVertex vertex1: left)
        {
            for (IVertex vertex2: right)
            {
                similarity = Math.max(similarity, m_pairwiseMeasure.getSimilarity(vertex1, vertex2));
            }
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function computes the average pairwise simialirty value as proposed
     * in the pioneering paper by Lord et al.[1] and detailed by
     * Azuaje et al. [2, formula 1].
     * 
     * [1] P.W. Lord, R.D. Stevens, A. Brass, C.A.
     * Goble, Semantic similarity measures as tools for exploring the gene ontology,
     * in: Pacific Symposium on Biocomputing., homepages.cs.ncl.ac.uk, 2003: pp. 601–612.
     * 
     * [2] F. Azuaje, H. Wang, O. Bodenreider,
     * Ontology-driven similarity approaches to supporting gene functional
     * assessment, in: Proceedings of the ISMB’2005 SIG Meeting on
     * Bio-Ontologies, academia.edu, 2005: pp. 9–10.
     * 
     * @param left The first set of vertexes (concepts) 
     * @param right The second set of vertexes (concepts) 
     * @return 
     * @throws java.lang.InterruptedException 
     */
    
    private double averageSimilarity(
            Set<IVertex> left,
            Set<IVertex> right) throws Exception
    {
        // We initialize the output value
        
        double similarity = m_pairwiseMeasure.getNullSimilarityValue();
        
        // We compute all pairwise similarity values
        
        if ((left.size() > 0) && (right.size() > 0))
        {
            // We compute the average
            
            similarity = 0.0;
            
            for (IVertex vertex1: left)
            {
                for (IVertex vertex2: right)
                {
                    similarity += m_pairwiseMeasure.getSimilarity(vertex1, vertex2);
                }
            }
        
            // We compute the average value
        
            similarity /= ((double)(left.size() * right.size()));
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function computes the Best-Match avergae (BMA) similarity between
     * GO concept sets as introduced by Azuaje et al. [1, formula 2].
     * 
     * [2] F. Azuaje, H. Wang, O. Bodenreider,
     * Ontology-driven similarity approaches to supporting gene functional
     * assessment, in: Proceedings of the ISMB’2005 SIG Meeting on
     * Bio-Ontologies, academia.edu, 2005: pp. 9–10.
     * 
     * @param leftVertexes The first set of vertexes (concepts) 
     * @param rightVertexes The second set of vertexes (concepts) 
     * @return 
     * @throws java.lang.InterruptedException 
     */
    
    private double bestMatchAverageSimilarity(
            Set<IVertex> leftVertexes,
            Set<IVertex> rightVertexes) throws Exception
    {
        // We initialize the output value
        
        double similarity = m_pairwiseMeasure.getNullSimilarityValue();
        
        // We compute the maximum similarity for each left vertex
        // as regards the right set
        
        if ((leftVertexes.size() > 0) && (rightVertexes.size() > 0))
        {
            similarity = 0.0;
            
            for (IVertex leftVertex: leftVertexes)
            {
                double bestMatch = m_pairwiseMeasure.getNullSimilarityValue();

                for (IVertex rightVertex: rightVertexes)
                {
                    bestMatch = Math.max(bestMatch, m_pairwiseMeasure.getSimilarity(leftVertex, rightVertex));
                }

                // We acumulate the best match for every vertex

                similarity += bestMatch;
            }

            // We compute the maximum similarity for each right vertex
            // as regards the left set

            for (IVertex rightVertex: rightVertexes)
            {
                double bestMatch = m_pairwiseMeasure.getNullSimilarityValue();

                for (IVertex leftVertex: leftVertexes)
                {
                    bestMatch = Math.max(bestMatch, m_pairwiseMeasure.getSimilarity(leftVertex, rightVertex));
                }

                // We acumulate the best match for every vertex

                similarity += bestMatch;
            }

            // We compute the average value

            similarity /= ((double)(leftVertexes.size() + rightVertexes.size()));
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This fucntion returns the type of groupwise measure
     * @return 
     */
    
    @Override
    public String toString()
    {
        return (m_groupwiseMetric.toString()
                + "/" + m_pairwiseMeasure.getMeasureType().toString());
    }
}
