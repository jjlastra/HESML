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
 * This class implements the semantic distance introduced by Al-Mubaid+
 * and Nguyen in the paper below:
 * Al-Mubaid, H., and Nguyen, H. A. (2009).
 * Measuring Semantic Similarity Between Biomedical Concepts Within
 * Multiple Ontologies. IEEE Transactions on Systems, Man and Cybernetics.
 * Part C, Applications and Reviews: A Publication of the IEEE Systems,
 * Man, and Cybernetics Society, 39(4), 389–398.
 * 
 * @author j.lastra
 */

class MeasureAlMubaidNguyen2009  extends SimilaritySemanticMeasure
{
    /**
     * Constructor
     * @param taxonomy The taxonomy used to compute the measurements.
     */
    
    MeasureAlMubaidNguyen2009(
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
        return (SimilarityMeasureType.Mubaid);
    }
    
    /**
     * This function returns the type of measure.
     * @return The type of semantic measure.
     */

    @Override
    public SimilarityMeasureClass getMeasureClass()
    {
        return (SimilarityMeasureClass.Distance);
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
        double  distance;   // Returned value

        double  path;       // Length of the shortest path depth
        double  maxDepth;   // Maximum depth of the taxonomy
        double  lcsDepth;   // Depth of the LCS vertex
        
        IVertex lcsVertex = left.getTaxonomy().getLCS(left, right, false);
        
        // We get the maximum depth in the taxonomy
        
        maxDepth = left.getTaxonomy().getVertexes().getGreatestDepthMin();
        
        // We obtain the factors of the distance formula for the
        // normal case (LCS exists) and for the potentially pathological
        // case (non-single root taxonomy, WN verbs).
        
        if (lcsVertex != null)
        {
            path = left.getShortestPathDistanceTo(right, false);
            lcsDepth = lcsVertex.getDepthMin();
        }
        else
        {
            // We consider the maximum distance according to the
            // Mubaid-Nguyen formula
            
            path = 2.0 * maxDepth;
            lcsDepth = 0.0;
        }

        // We note that AlMubaid and Nguyen define the shortest path
        // length like the node-counting distance while we use
        // the edge-based definition, thus, we omit the substraction
        // of 1 to the path term. Finally, we compute the distance
        // using the equation (3) in the aforementioned paper below,
        // with the factors provided by default: k = 1, beta = 1, alfa=1
        // The authors reports a Pearson correlation value of 0.815
        // in the RG65 dataset with these default values.
        
        distance = Math.log(1 + path * (maxDepth - lcsDepth));
        
        // We return the result
        
        return (distance);
    }    
}
