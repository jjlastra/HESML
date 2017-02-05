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
 * This class implements the ICg model introduced in the paper below.
 * Blanchard, E., Harzallah, M., and Kuntz, P. (2008).
 * A generic framework for comparing semantic similarities on a subsumption
 * hierarchy. In M. Ghallab, C. D. Spyropoulos, N. Fakotakis, and N. Avouris (Eds.),
 * Proceedings of the ECAI (Vol. 178, pp. 20–24). IOS Press.
 * 
 * @author j.lastra
 */

class BlanchardICmodel extends AbstractICmodel
    implements ITaxonomyInfoConfigurator
{
    /**
     * This function computes the Blanchard ICg model.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws java.lang.Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        double  twoLog = Math.log(2.0);
        
        double  prob;           // Node probbaility
        int     subsumedLeaves; // Leaves
        int     totalLeaves;    // Overall count of leaves

        // We get the total number of leaves
        
        totalLeaves = taxonomy.getVertexes().getLeavesCount();
        
        // We compute the weights of every edge ion the taxonomy
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We get the subsumed leaves
            
            subsumedLeaves = vertex.getInclusiveSubsumedLeafSetCount();
            
            // We computes the probability
            
            prob = (double) subsumedLeaves / (double)totalLeaves;
            
            // We set the probability
            
            vertex.setProbability(prob);
            vertex.setICValue(-Math.log(prob) / twoLog);
        }
        
        // We must set edge weights with the delta IC values
        
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
        return (IntrinsicICModelType.Blanchard.toString());
    }
}
