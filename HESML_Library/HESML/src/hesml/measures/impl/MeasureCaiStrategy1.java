/*
 * Copyright (C) 2016-2018 Universidad Nacional de Educación a Distancia (UNED)
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
 * This class implements the semantic similarity measure called strategy1 in
 * the paper by Cai et  al. (2017) cited below.
 * Cai, Y., Zhang, Q., Lu, W., & Che, X. (2017).
 * A hybrid approach for measuring semantic similarity based on IC-weighted path
 * distance in WordNet. Journal of Intelligent Information Systems, 1–25.
 * 
 * @author Juan Lastra-Díaz
 */

class MeasureCaiStrategy1 extends BaseJiangConrathMeasure
{
    /**
     * Free parameters in formula (7) of the paper cited above
     */
    
    private double  m_alplha;
    private double  m_beta;
    
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureCaiStrategy1(
            ITaxonomy   taxonomy,
            double      alpha,
            double      beta) throws Exception
    {
        super(taxonomy);
        
        // We store the free parameters
        
        m_alplha = alpha;
        m_beta = beta;
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.CaiStrategy1);
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
        // We obtain the MICA vertex
        
        IVertex micaVertex = left.getTaxonomy().getMICA(left, right);

        // We compute splW (Jiang-Coraanh distance)
        
        double splW = left.getICvalue() + right.getICvalue() - 2 * micaVertex.getICvalue();
        
        // We compute the normalized shortest path (formula 5 in the paper)
        
        double splN = 0.5 * (double) left.getShortestPathDistanceTo(right, false)
                        / (double) left.getTaxonomy().getVertexes().getGreatestDepthMax();
        
        // Now we compute the similarity value
        
        double sim1 = Math.exp(-(m_alplha * splW + m_beta * splN));
        
        // We return the result
        
        return (sim1);
    }
}
