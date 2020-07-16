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

import hesml.measures.*;
import hesml.taxonomy.*;

/**
 * This class implements the measure introduced by Pirró and Seco in
 * the paper below.
 * 
 * Pirró, G., and Seco, N. (2008). Design, Implementation and Evaluation
 * of a New Semantic Similarity Metric Combining Features and Intrinsic
 * Information Content. In R. Meersman and Z. Tari (Eds.),
 * On the Move to Meaningful Internet Systems: OTM 2008 (Vol. 5332,
 * pp. 1271–1288). Springer Berlin Heidelberg.
 * 
 * @author Juan Lastra-Díaz
 */

class MeasurePirroSeco extends SimilaritySemanticMeasure
{
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasurePirroSeco(
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
        return (SimilarityMeasureType.PirroSeco);
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

        // We filter the equal values
        
        if (left == right)
        {
            similarity = 1.0;                    
        }
        else
        {
            // We get the MICA vertex

            IVertex micaVertex = m_Taxonomy.getMICA(left, right);

            // We compute the distance

            if (micaVertex != null)
            {
                similarity = 3.0 * micaVertex.getICvalue()
                        - left.getICvalue() - right.getICvalue();
            }
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function returns the value returned by the similarity measure when
     * there is none similarity between both input concepts, or the concept
     * is not contained in the taxonomy.
     * @return 
     */
    
    @Override
    public double getNullSimilarityValue()
    {
        return (-2.0 * m_Taxonomy.getVertexes().getGreatestICValue());
        //return (Double.NEGATIVE_INFINITY);
    }
}
