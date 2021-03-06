/*
 * Copyright (C) 2016-2021 Universidad Nacional de Educación a Distancia (UNED)
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

import hesml.measures.GroupwiseSimilarityMeasureType;
import hesml.measures.IGroupwiseSimilarityMeasure;
import hesml.taxonomy.IVertex;
import java.util.HashSet;
import java.util.Set;

/**
 * THis class implements the simLP measure introduced by Gentleman [1, page 10].
 * 
 * [1] R. Gentleman, Visualizing and distances using GO,
 * URL Http://www.Bioconductor. Org/docs/vignettes. Html. 38 (2005).
 * http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.592.2206&rep=rep1&type=pdf.
 * 
 * @author Juan J. Lastra-Díaz (jlastra@invi.uned.es)
 */

class GroupwiseSimLPMeasure implements IGroupwiseSimilarityMeasure
{
    /**
     * This function returns the type of the measure.
     * @return 
     */

    @Override
    public GroupwiseSimilarityMeasureType getMeasureType()
    {
        return (GroupwiseSimilarityMeasureType.SimLP);
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
        // According to Gentleman [1]: "For simLP the similarity measure is the
        // depth of the longest shared path from the root node". Thus, we compute
        // the depthest common ancestor in the common ancestor set.
        
        // We compute the full ancestor set for the left set
        
        HashSet<IVertex> leftAncestors = new HashSet<>(left.size());
        
        for (IVertex vertex : left)
        {
            for (IVertex ancestor : vertex.getAncestors(true))
            {
                leftAncestors.add(ancestor);
            }
        }

        // We compute the full ancestor set for the left set
        
        HashSet<IVertex> rightAncestors = new HashSet<>(right.size());
        
        for (IVertex vertex : right)
        {
            for (IVertex ancestor : vertex.getAncestors(true))
            {
                rightAncestors.add(ancestor);
            }
        }
        
        // We compute the intersection set
        
        leftAncestors.retainAll(rightAncestors);
        
        // We search for the deepest vertexes
        
        double similarity = 0.0;
        
        for (IVertex vertex : leftAncestors)
        {
            similarity = Math.max(similarity, vertex.getDepthMax());
        }
        
        // We clear the auxiliary vertexes
        
        leftAncestors.clear();
        rightAncestors.clear();
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function returns the type of groupwise measure
     * @return 
     */
    
    @Override
    public String toString()
    {
        return (GroupwiseSimilarityMeasureType.SimLP.toString());
    }
}

