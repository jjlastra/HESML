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
 * This class implements the Seco et al IC model introduced in the paper below.
 * Seco, N., Veale, T., and Hayes, J. (2004).
 * An intrinsic information content metric for semantic similarity in WordNet.
 * In R. López de Mántaras and L. Saitta (Eds.), Proceedings of the 16th
 * European Conference on Artificial Intelligence (ECAI) (Vol. 16,
 * pp. 1089–1094). Valencia, Spain: IOS Press.
 * 
 * @author Juan Lastra-Díaz
 */

class SecoICmodel extends AbstractICmodel
    implements ITaxonomyInfoConfigurator
{
    /**
     * This functions computes and sets the IC values of the concepts.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        double  icvalue;    // Computed IC value
        
        double  logMaxNodes;
        
        // We ghet the log of the number of nodes
        
        logMaxNodes = Math.log(taxonomy.getVertexes().getCount());
        
        // We set the IC values for the nodes
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We compute the value of [Seco, 2004]
            
            icvalue = 1.0 - Math.log(vertex.getNonInclusiveHyponymSetCount() + 1) /
                            logMaxNodes;
            
            // We set the IC value
            
            vertex.setICValue(icvalue);
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
        return (IntrinsicICModelType.Seco.toString());
    }        
}
