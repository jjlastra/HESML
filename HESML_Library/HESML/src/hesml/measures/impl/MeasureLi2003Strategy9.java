/*
 * Copyright (C) 2016-2022 Universidad Nacional de Educación a Distancia (UNED)
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
 * This class implements the similarity measure called strategy 9 introduced
 * in the paper below.
 * Li, Y., Bandar, Z. A., and McLean, D. (2003).
 * An approach for measuring semantic similarity between words using
 * multiple information sources.
 * IEEE Transactions on Knowledge and Data Engineering, 15(4), 871–882.
 * 
 * @author Juan Lastra-Díaz
 */

class MeasureLi2003Strategy9 extends SimilaritySemanticMeasure
{
    /**
     * This flag forces the use of the fast approximantion of Djikstra
     * algortihm instead of the exact method (false),
     */
    
    private boolean m_useFastShortestPathAlgorithm;
    
    /**
     * Exponential factor initializaed to the best
     * default value for this measure in the paper.
     */
    
    private double  m_Alpha;
    
    /**
     * Factor used in the exponential of depth
     */
    
    private double  m_Beta;
    
    /**
     * Factor used in the IC term
     */
    
    private double  m_Gamma;
    
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureLi2003Strategy9(
            ITaxonomy   taxonomy,
            boolean     useFastMethod) throws Exception
    {
        super(taxonomy);
        m_useFastShortestPathAlgorithm = useFastMethod;
        
        // We initialize the best default value on the RG65 dataset
        
        m_Alpha = 0.2;
        m_Beta = 0.6;
        m_Gamma = 0.4;
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.Li2003Strategy9);
    }
    
    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureClass getMeasureClass()
    {
        return (SimilarityMeasureClass.Similarity);
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
        double  similarity = 0.0;   // Returned value

        // We obtain the MICA vertex
        
        IVertex micavertex = m_Taxonomy.getMICA(left, right);
        
        // We check the existence of the MICA vertex
        
        if (micavertex != null)
        {
            // We get the ic vlaue of the mica

            double icMica = micavertex.getICvalue();

            // We get the f1 and f2 terms

            double f1 = MeasureLi2003Strategy3.simStrategyFun1(left,
                        right, m_Alpha, m_useFastShortestPathAlgorithm);
            
            double f2 = MeasureLi2003Strategy4.simStrategyFun2(left, right, m_Beta);

            // We compute the F3 function

            double expIC = Math.exp(m_Gamma * icMica);
            double expNegIC = Math.exp(-m_Gamma * icMica);

            double f3 = (expIC - expNegIC) / (expIC + expNegIC);

            // We compute the similarity

            similarity = f1 * f2 * f3;
        }
        
        // We return the result
        
        return (similarity);
    }
}

