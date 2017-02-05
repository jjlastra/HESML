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
 * This class implements the IC model introduced in the paper below.
 * Meng, L., Gu, J., and Zhou, Z. (2012).
 * A new model of information content based on concept’s topology for
 * measuring semantic similarity in WordNet.
 * International Journal of Grid and Distributed Computing, 5(3), 81–93.
 * 
 * @author Juan Lastra-Díaz
 */

class Meng2012ICmodel extends AbstractICmodel
    implements ITaxonomyInfoConfigurator
{
    /**
     * This function implements the IC-value introduced in the paper below.
     * Meng, L., Gu, J., and Zhou, Z. (2012). A new model of information
     * content based on concept’s topology for measuring semantic similarity
     * in WordNet. International Journal of Grid and Distributed, 5(3), 81–93.
     * 
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        double  icValue;    // IC value for one node
        
        double  logDepthMax;    // Depth max factor
        double  logMaxNodes;
        double  sumFactor;  
        
        // We get the depth max factor
        
        logDepthMax = Math.log(taxonomy.getVertexes().getGreatestDepthMinBase1());
        logMaxNodes = Math.log(taxonomy.getVertexes().getCount());
        
        // We compute the IC values for the nodes
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We compute the right facrtor over the hyponums
            
            sumFactor = 1.0;
            
            for (IVertex hyponym: vertex.getHyponyms(false))
            {
                sumFactor += (1.0 / hyponym.getDepthMinBase1());
            }
            
            // We get the logarithm value
            
            sumFactor = (1.0 - Math.log(sumFactor) / logMaxNodes);
            
            // We compute the value
            
            icValue = (Math.log(vertex.getDepthMinBase1()) / logDepthMax) * sumFactor;

            // We set the ic-value
            
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
        return (IntrinsicICModelType.Meng.toString());
    }        
}
