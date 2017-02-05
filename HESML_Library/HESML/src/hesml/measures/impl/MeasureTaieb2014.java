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
import hesml.configurators.*;
import hesml.configurators.icmodels.ICModelsFactory;

/**
 * This function implements the similarity measures introduced by
 * Hadj Taieb et al. in the paper below.
 * 
 * Hadj Taieb, M. A., Ben Aouicha, M., and Ben Hamadou, A. (2014).
 * Ontology-based approach for measuring semantic similarity-
 * Engineering Applications of Artificial Intelligence, 36(0), 238–261.
 * 
 * @author Juan Lastra-Díaz
 */

class MeasureTaieb2014 extends SimilaritySemanticMeasure
{
    /**
     * Alpha value: maximum number of synsets per word in WordNet
     */
    
    private double m_Alpha;
    
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureTaieb2014(
        ITaxonomy   taxonomy) throws Exception
    {
        super(taxonomy);

        // We initialize the data
        
        initSpecValues(taxonomy);
        m_Alpha = 0;
    }

    /**
     * This functions builds the measure in constrained mode
     * @param taxonomy
     * @param alpha 
     */
    
    MeasureTaieb2014(ITaxonomy taxonomy, int alpha) throws Exception
    {
        super(taxonomy);

        // We initialize the data
        
        initSpecValues(taxonomy);
        m_Alpha = alpha;
    }

    /**
     * This function returns the best similarity value for the cartesian
     * product of both vertexes set. This function overrides the default
     * implementation for all the similariry measures in order to implement
     * the method proposed by Hadj Taieb et al. in their aforementioned paper.
     * @param left
     * @param right
     * @return Similarity measure
     * @throws InterruptedException
     * @throws Exception 
     */
    
    @Override
    public double getHighestPairwiseSimilarity(
            IVertexList left,
            IVertexList right)
            throws InterruptedException, Exception
    {
        double  bestSimilarityValue = Double.NEGATIVE_INFINITY;  // Returned value
        
        double  similarity;     // Similarity value
        double  factor = 0.0;   // Adjustment factor
        
        // We compute the factor
        
        if (m_Alpha > 0)
        {
            factor = (double) Math.max(left.getCount(), right.getCount()) / m_Alpha;
        }
        
        // We search for the best similarity value
        
        for (IVertex leftVertex: left)
        {
            for (IVertex rightVertex: right)
            {
                // We compoute the similarity value between left and right vertexes
                
                similarity = similarityWithFactor(leftVertex, rightVertex, factor);
                
                // We save the best value
                
                bestSimilarityValue = Math.max(similarity, bestSimilarityValue);
            }
        }
        
        // We return the result
        
        return (bestSimilarityValue);
    }
    
    /**
     * This function implements similarity function with factor
     * introduced by HAdj Taieb et al. in their paper.
     * @param left
     * @param right
     * @return The semantic distance between the nodes.
     */
    
    private double similarityWithFactor(
            IVertex left,
            IVertex right,
            double  factor) throws InterruptedException, Exception
    {
        double  similarity = 0.0;   // Returned value

        IVertex lcsVertex;  // Least common subsumer
        
        double  termHypo;   // We compute the termHypo
        double  termDepth;
        
        // We compute the termHYpo
        // We get the LCS vertex

        lcsVertex = m_Taxonomy.getLCS(left, right, true);

        // We evaluate the terms in the similarity measure. We recall that
        // the IC values contains the node-valued specificity function
        // defined by Hadj Taieb et al. in their paper.

        if (lcsVertex != null)
        {
            termHypo = 2.0 * lcsVertex.getICvalue()/
                        (left.getICvalue() + right.getICvalue());

            termDepth = 2.0 * lcsVertex.getDepthMax()/
                        (left.getDepthMax()+ right.getDepthMax());

            similarity = Math.abs(termDepth - factor) * termHypo;
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function initializes the data
     * @param taxonomy 
     */
    
    private void initSpecValues(
            ITaxonomy   taxonomy) throws Exception
    {
        ITaxonomyInfoConfigurator   icModel;    // IC model of Taieb
        
        // We create the IC model of Taieb
        
        icModel = ICModelsFactory.getIntrinsicICmodel(
                    IntrinsicICModelType.HadjTaiebHypoValue);
        
        // We set the IC values
        
        icModel.setTaxonomyData(taxonomy);
    }
    
    /**
     * This function returns the class of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureClass getMeasureClass()
    {
        return (SimilarityMeasureClass.Similarity);
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        SimilarityMeasureType   type = SimilarityMeasureType.Taieb2014;
        
        // We check if the measure mathces the second type introdcued
        // by Hadj Taieb et al. in their paper.
        
        if (m_Alpha > 0)
        {
            type = SimilarityMeasureType.Taieb2014sim2;
        }
        
        // We return the result
        
        return (type);
    }

    /**
     * This function returns the comparison between nodes.
     * @param left
     * @param right
     * @return The semantic distance between the nodes.
     */
    
    @Override
    public double compare(
            IVertex left,
            IVertex right) throws InterruptedException, Exception
    {
        double  similarity; // Returned value
        
        // We get the similarity value
        
        similarity = similarityWithFactor(left, right, 0.0);
        
        // We return the result
        
        return (similarity);
    }
}
