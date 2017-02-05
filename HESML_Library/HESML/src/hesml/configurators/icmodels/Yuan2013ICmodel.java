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
 * This function implements the Yuan et al. IC model introduced in
 * the paper below.
 * Yuan, Q., Yu, Z., and Wang, K. (2013).
 * A New Model of Information Content for Measuring the Semantic Similarity
 * between Concepts. In Proc. of the International Conference on Cloud
 * Computing and Big Data (CloudCom-Asia 2013) (pp. 141–146).
 * IEEE Computer Society.
 * 
 * @author Juan Lastra-Díaz
 */

class Yuan2013ICmodel extends AbstractICmodel
    implements ITaxonomyInfoConfigurator
{
    /**
     * This function implements the Yuan et al (2013) IC model.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        double  icValue;    // Ic value
        
        double  fDepth;     // Depth factor
        double  fLeaves;    // leaves factor
        double  fHyper;     // Hypernyms factor
        
        double  logDepthMax;    // Depth max
        double  logLeavesMax;   // leaves max + 1
        double  logMaxNodes;
        
        IVertexList vertexes = taxonomy.getVertexes();
        
        // We compute the static values
        
        logLeavesMax = Math.log(vertexes.getLeavesCount()+ 1);
        logDepthMax = Math.log(vertexes.getGreatestDepthMinBase1());
        logMaxNodes = Math.log(vertexes.getCount());
        
        // We set the Ci values for each node
        
        for (IVertex vertex: vertexes)
        {
            // Wecompute the three factors
            
            fDepth = Math.log(vertex.getDepthMinBase1()) / logDepthMax;
            
            fLeaves = Math.log(1.0 + vertex.getNonInclusiveSubsumedLeafSetCount())
                       / logLeavesMax;
            
            fHyper = Math.log(vertex.getNonInclusiveAncestorSetCount() + 1)
                    / logMaxNodes;
            
            // We compute the ic-value
            
            icValue = fDepth * (1.0 - fLeaves) + fHyper;
            
            // We set hte ic-value
            
            vertex.setICValue(icValue);
        }
        
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
        return (IntrinsicICModelType.Yuan.toString());
    }        
}
