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
 */

package hesml.measures.impl;

import hesml.measures.GroupwiseSimilarityMeasureType;
import hesml.measures.IGroupwiseSimilarityMeasure;
import hesml.taxonomy.IVertex;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the groupsie GO-based semantic similairty measure
 * introduced by Pesquita et al. [1].
 * 
 * [1] C. Pesquita, D. Faria, H. Bastos, A. Falcão, F. Couto,
 * Evaluating GO-based semantic similarity measures, in:
 * Proc. 10th Annual Bio-Ontologies Meeting, 2007: p. 38.
 * 
 * @author Juan J. Lastra-Díaz (jlastra@invi.uned.es)
 */

class GroupwiseSimGICMeasure implements IGroupwiseSimilarityMeasure
{
    /**
     * This function returns the type of the measure.
     * @return 
     */

    @Override
    public GroupwiseSimilarityMeasureType getMeasureType()
    {
        return (GroupwiseSimilarityMeasureType.SimGIC);
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
        
        // We compute the union and intersection sets
        
        Set<IVertex> unionSet = new HashSet<>(leftAncestors);
        unionSet.addAll(rightAncestors);
        
        Set<IVertex> intersectionSet = new HashSet<>(leftAncestors);
        intersectionSet.retainAll(rightAncestors);

        // We compute the numerator of simGIC
        
        double numeratorICsum = 0.0;
        
        for (IVertex vertex : intersectionSet)
        {
            numeratorICsum += vertex.getICvalue();
        }
        
        // We compute the denominator of simGIC
        
        double denominatorICsum = 0.0;
        
        for (IVertex vertex : unionSet)
        {
            denominatorICsum += vertex.getICvalue();
        }
        
        // We clear both auxiliary sets
        
        unionSet.clear();
        intersectionSet.clear();
        leftAncestors.clear();
        rightAncestors.clear();
        
        // We compute the similarity
        
        double similarity = (denominatorICsum > 0.0) ?
                            numeratorICsum / denominatorICsum : 1.0;
        
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
        return (GroupwiseSimilarityMeasureType.SimGIC.toString());
    }
}

