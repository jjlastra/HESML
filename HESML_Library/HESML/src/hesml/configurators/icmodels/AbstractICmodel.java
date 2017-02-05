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

package hesml.configurators.icmodels;

// HESML references

import hesml.taxonomy.*;

/**
 * This class represents an abstract Information Content model which is
 * defined as any ITaxonomyConfigurator that sets the IC values of the
 * vertexes (nodes) in the taxonomy.
 * @author Juan Lastra-Díaz
 */

abstract class AbstractICmodel
{
    /**
     * This function sets the weigths for the edges as the difference
     * between the IC values between both incident nodes. This function
     * allows that the computation of the weighted similarity measures
     * introduced in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A novel family of IC-based similarity measures with a detailed
     * experimental survey on WordNet. Engineering Applications of
     * Artificial Intelligence Journal, 46, 140–153.
     * 
     * @param taxonomy Taxonomy whose IC modle is being computed
     * @throws Exception Unexpected error
     */

    protected void setICDeltaWeights(ITaxonomy taxonomy) throws Exception
    {
        IVertex child;  // Extremes of an edge
        IVertex parent;
        
        double  icChild;    // Ic values on the extremes
        double  icParent;
        
        // We computed the IC-delta for each edge
        
        for (IEdge edge: taxonomy.getEdges())
        {
            // We get the ic value for the child
            
            if (edge.getDirect().getEdgeType() == OrientedEdgeType.SubClassOf)
            {
                parent = edge.getDirect().getTarget();
                child = edge.getInverse().getTarget();
            }
            else
            {
                child = edge.getDirect().getTarget();
                parent = edge.getInverse().getTarget();
            }
            
            // We get the IC-values
            
            icChild = child.getICvalue();
            icParent = parent.getICvalue();
            
            // We set the edge weight value
            
            edge.setWeight(Math.abs(icChild - icParent));
        }
    }
}
