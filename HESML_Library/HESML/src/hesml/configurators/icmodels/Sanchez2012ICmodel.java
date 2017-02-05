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
import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;

/**
 * This class implements the IC-computation method introduced in the paper below.
 * Sánchez, D., and Batet, M. (2012). A new model to compute the information
 * content of concepts from taxonomic knowledge. International Journal on
 * Semantic Web and Information Systems, 8(2), 34–50.
 * 
 * @author Juan Lastra-Díaz
 */

class Sanchez2012ICmodel extends AbstractICmodel
    implements ITaxonomyInfoConfigurator
{
    /**
     * This function implements the IC-computation method introduced in the paper above.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws Exception Unexpected error
     */
    
    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        double  icValue;    // Returned value
        
        double  log2 = Math.log(2.0);   // Logarithm of 2
        
        double  subSummers;  // Set of ancestors including the node

        double  commonnessNode; // Node value
        double  commonnessRoot; // Root value
        
        IVertexList leaves; // Non-inclusive subsumed leaf set
        
        IVertex root;  // Root concept
        
        // We compute the IC value for each node in reverse order
        // because the commonness is bottom-up concept
        // (1) We initialize the leaves with its commonness value
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We check if the vertex is a Leaf Node
            
            if (vertex.isLeaf())
            {
                // We ompute the leaf value
                
                subSummers = 1.0 + vertex.getNonInclusiveAncestorSetCount();
                commonnessNode = 1.0 / subSummers;
                
                // We save the value in the IC field
                
                vertex.setICValue(commonnessNode);
                vertex.setVisited(true);
            }
        }
        
        // (2)  We compute the value for the inner nodes in bottom-up mode
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We check that vertex has been already visited (leaves)
            
            if (!vertex.isLeaf())
            {
                // We initializae the value

                commonnessNode = 0.0;

                // We get the set of non-inclusive subsumed leaves
                
                leaves = vertex.getSubsumedLeaves(false);
                
                // We sum all the Leaves values

                for (IVertex leaf: leaves)
                {
                    commonnessNode += leaf.getICvalue();
                }
                
                // We release the leaf set
                
                leaves.clear();
                
                // We set the commonness
                
                vertex.setICValue(commonnessNode);
            }
        }
        
        // We get hte commonness of the root
        
        root = taxonomy.getVertexes().getRoots().getAt(0);
        
        commonnessRoot = root.getICvalue();
        
        // We set the IC values for all nods
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We compute the IC-values for non-leaf concepts
            
            icValue = -Math.log(vertex.getICvalue() / commonnessRoot ) / log2;
            
            // We set the IC value
            
            vertex.setICValue(icValue);
        }
        
        // We set the root to 0
        
        root.setICValue(0.0);
        
        // We set the delta IC weights for the weighted Measures
        
        setICDeltaWeights(taxonomy);
    }
    
    /**
     * This function returns a String representing IC model type of the
     * current object.
     * @return A string representing the IC model type of the current instance
     */
    
    @Override
    public String toString()
    {
        return (IntrinsicICModelType.Sanchez2012.toString());
    }        
}
