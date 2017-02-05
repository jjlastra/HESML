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
 * This class implements the Zhou et al. IC model introduced in the paper below.
 * Zhou, Z., Wang, Y., and Gu, J. (2008).
 * A new model of information content for semantic similarity in WordNet.
 * In Proc.of the Second International Conference on Future Generation
 * Communication and Networking Symposia (FGCNS’08) (Vol. 3, pp. 85–89). IEEE.
 * 
 * @author Juan Lastra-Díaz
 */

class ZhouICmodel extends AbstractICmodel
    implements ITaxonomyInfoConfigurator
{
    /**
     * This function implements the Zhou et al (2008) IC model.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        double  icSecovalue;    // Computed IC value for [Seco et al., 2004]
        double  icZhouFactor;      // Additional Zhou factor
        
        double  icZhou; // Total value
        
        double  logMaxNodes;    // Depth max
        double  logDepthMax;

        // We get hte depth max factor
        
        logDepthMax = Math.log(taxonomy.getVertexes().getGreatestDepthMinBase1());
        logMaxNodes = Math.log(taxonomy.getVertexes().getCount());
        
        // We set the IC values for the nodes
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We compute the value of [Seco, 2004]
            
            icSecovalue = 1.0 - Math.log(vertex.getNonInclusiveHyponymSetCount() + 1) /
                            logMaxNodes;
            
            // We compute the Zhou factor
            
            icZhouFactor = Math.log(vertex.getDepthMinBase1()) / logDepthMax;
            
            // We comptue the total value
            
            icZhou = 0.5 * icSecovalue + 0.5 * icZhouFactor;
            
            // We set the IC value
            
            vertex.setICValue(icZhou);
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
        return (IntrinsicICModelType.Zhou.toString());
    }        
}
