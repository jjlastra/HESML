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

import hesml.configurators.IntrinsicICModelType;
import hesml.taxonomy.*;

/**
 * This class implements the IC model called CondProbRefLogistic
 * that is introduced in the paper below.
 * 
 * Lastra-Díaz, Juan J., and Ana García-Serrano. 2016.
 * “A Very Detailed Experimental Survey of Ontology-Based Similarity
 * Measures with a Refinement of the Well-Founded Information Content Models.”
 * Submitted for Publication.
 * 
 * @author j.lastra
 */

class CondProbRefinedLogisticICmodel extends CondProbLogisticICmodel
{
    /**
     * Constructor.
     * @param kLogistic Constant for the logistic function in the IC model 
     */
    
    CondProbRefinedLogisticICmodel(
            double  kLogistic)
    {
        super(kLogistic);
    }
    
    /**
     * This function computes the data.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws java.lang.Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        // We compute the base IC model
        
        super.setTaxonomyData(taxonomy);
        
        // We set the overall leaf nodes probability to 1
        
        setNormalizedLeavesProbability(taxonomy);
        
        // We call the refined computation of the IC values in order
        // to set the node probability as the sum of the probability
        // of the subsumed leaf nodes.
        
        setNodesProbICvaluesBySubsumedLeaves(taxonomy, true);
    }    
    
    /**
     * This function returns a String representing IC model type of the
     * current object.
     * @return A string representing the IC model type of the current instance
     */
    
    @Override
    public String toString()
    {
        return (IntrinsicICModelType.CondProbRefLogistic.toString()
                + ", K=" + Double.toString(m_K));
    }        
}
