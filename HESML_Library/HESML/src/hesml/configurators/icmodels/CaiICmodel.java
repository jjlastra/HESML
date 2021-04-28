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
 *
 */

package hesml.configurators.icmodels;

// HESML references

import hesml.taxonomy.*;
import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;

/**
 * This class implements the Cai et al. (2017) IC model introduced in the paper below.
 * Cai, Y., Zhang, Q., Lu, W., & Che, X. (2017)
 * A hybrid approach for measuring semantic similarity based on IC-weighted
 * path distance in WordNet. Journal of Intelligent Information Systems, 1–25.
 * @author Juan Lastra-Díaz
 */

class CaiICmodel extends AbstractICmodel
    implements ITaxonomyInfoConfigurator
{
    /**
     * Free parameter used in the IC model as proposed in formula (3)
     * of the main paper cited above.
     */
    
    private double  m_lambda;
    
    /**
     * Constructor
     * @param lambda free parameter
     */
    
    CaiICmodel(
        double  lambda)
    {
        m_lambda = lambda;
    }
    
    /**
     * This functions computes and sets the IC values of the concepts.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        // We compute the logarithm of the overall number of nodes in WordNet
        
        double logMaxNodes = Math.log(taxonomy.getVertexes().getCount());
        
        // We set the IC values for the nodes
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We compute the seco IC value corresponding to the first factor
            // of the IC value in formula (3)
            
            double secoICvalue = 1.0 - Math.log(vertex.getNonInclusiveHyponymSetCount() + 1) / logMaxNodes;
            
            // Finally, we compute the final IC value
            
            double icValue = secoICvalue * Math.tanh(m_lambda * vertex.getDepthMax());
            
            // We set the IC value
            
            vertex.setICValue(icValue);
        }
        
        // We set the delta IC weights for the IC-based weighted Measures
        
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
        return (IntrinsicICModelType.Cai.toString());
    }        
}
