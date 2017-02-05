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
 * This class implements the semantic similarity measure called FaiTH,
 * which is introduced in the paper below.
 * 
 * Pirró, G., and Euzenat, J. (2010).
 * A Feature and Information Theoretic Framework for Semantic Similarity
 * and Relatedness. In P. F. Patel-Schneider, Y. Pan, P. Hitzler, P. Mika,
 * L. Zhang, J. Z. Pan, … B. Glimm (Eds.), Proc. of the 9th International
 * Semantic Web Conference, ISWC 2010 (Vol. 6496, pp. 615–630).
 * Shangai, China: Springer.
 * 
 * @author j.lastra
 */

class MeasureFaith extends SimilaritySemanticMeasure
{
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureFaith(
        ITaxonomy   taxonomy)
    {
        super(taxonomy);
    }

    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.FaITH);
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
     * @return Similaity value.
     */
    
    @Override
    public double compare(
            IVertex left,
            IVertex right) throws InterruptedException, Exception
    {
        double  similarity = 0.0;   // Returned value

        IVertex micaVertex; // LCA with the maximum IC value
        
        // We filter the equal values
        
        if (left == right)
        {
            similarity = 1.0;                    
        }
        else
        {
            // We get the MICA vertex

            micaVertex = m_Taxonomy.getMICA(left, right);

            // We compute the distance

            if (micaVertex != null)
            {
                similarity = micaVertex.getICvalue() /
                        (left.getICvalue() + right.getICvalue()
                        - micaVertex.getICvalue());
            }
        }
        
        // We return the result
        
        return (similarity);
    }
}
