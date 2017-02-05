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

import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;

/**
 * This class implements the intrinsic IC model introduced by Adhakari et al.
 * in the following paper:
 * 
 * Adhikari, A., Singh, S., Dutta, A., and Dutta, B. (2015).
 * A novel information theoretic approach for finding semantic similarity in
 * WordNet. In Proc. of IEEE International Technical Conference
 * (TENCON-2015) (pp. 1–4). Macau, China: IEEE.
 * 
 * @author j.lastra
 */

class AdhikariICmodel  extends AbstractICmodel
    implements ITaxonomyInfoConfigurator
{
    /**
     * This function implements the Adhakari et al. (2015) IC model.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        double  depth;      // Depth of each vertex
        double  leaves;     // Non-inclusive subsumed leaves count
        double  hypoFactor; // Hyponym factor
        double  icValue;    // IC value of the vertex
        double  subsumers;  // Subsumer set count including the vertex
        
        double  logDmax;    // Logarithm of (Depth-max + 1)
        double  leavesMax;  // Leaf vertexes count
        double  logNodeMax; // Logartihm of the concept count
        
        IVertexList hyponymSet;   // Hyponym set
        
        // We compute the constant terms
        
        logDmax = Math.log(1.0 + taxonomy.getVertexes().getGreatestDepthMin());
        leavesMax = taxonomy.getVertexes().getLeavesCount();
        logNodeMax = Math.log(taxonomy.getVertexes().getCount());
        
        // We computes the IC value of the vertexes
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We get the vertex depth, leaves count and hyponym set
            
            depth = vertex.getDepthMin();
            leaves = vertex.getNonInclusiveSubsumedLeafSetCount();
            hyponymSet = vertex.getHyponyms(false);
            subsumers = vertex.getNonInclusiveAncestorSetCount() + 1;
            
            // We compute the hypoFactor
            
            hypoFactor = 0.0;
            
            for (IVertex hyponym: hyponymSet)
            {
                hypoFactor += (1.0 / hyponym.getDepthMin());
            }
            
            // We compute the IC value
            
            icValue = (Math.log(depth + 1.0) / logDmax)
                    * (1.0 - Math.log(1.0 + (leaves * vertex.getParentsCount() / leavesMax) / subsumers))
                    * (1.0 - Math.log(1.0 + hypoFactor) / logNodeMax);
            
            // We set the IC value
            
            vertex.setICValue(icValue);
            
            // We release the hyponym set which is computed on-the-fly
            
            hyponymSet.clear();
        }
        
        // We set the delta IC weights for the weighted Measures
        
        setICDeltaWeights(taxonomy);
    }
    
    /**
     * This function returns a String representing IC model type of the
     * current object.
     * @return A name representing the type of the current IC model instance
     */
    
    @Override
    public String toString()
    {
        return (IntrinsicICModelType.Adhikari.toString());
    }
    
}
