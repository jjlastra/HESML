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
 * Hadj Taieb, M. A., Ben Aouicha, M., and Ben Hamadou, A. (2013).
 * Computing semantic relatedness using Wikipedia features.
 * International Journal of Uncertainty, Fuzziness and
 * Knowledge-Based Systems, 50(0), 260–278.
 * 
 * @author Juan Lastra-Díaz
 */

class HadjTaieb2013ICmodel extends AbstractICmodel
    implements ITaxonomyInfoConfigurator
{
    /**
     * This function implements the IC-computation method in
     * Hadj Taieb, M. A., Ben Aouicha, M., and Ben Hamadou, A. (2014).
     * A new semantic relatedness measurement using WordNet features.
     * Knowledge and Information Systems, 41(2), 467–497.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        double  icValue;    // IC-value for each vertex
        
        double  avgDepth;   // Average depth for the hypernyms
        double  score;
        
        double  hypoVertex; // Hyponyms inclusive of the vertex
        double  hypoParent; // Hyponyms inclusive of the parent
        
        IVertexList hyper;  // Set of hypernyms including the node
        
        // (1) We compute the scores for each node
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We get the hyponyms of the vertex according to the method
            
            hypoVertex = vertex.getNonInclusiveHyponymSetCount() + 1;
                    
            // We reset the score
            
            score = 0.0;
            
            for (IVertex parent: vertex.getParents())
            {
                // We get the Hypo value for the parent
                
                hypoParent = parent.getNonInclusiveHyponymSetCount() + 1;
                
                // We accumulate the score
                
                score += (double)parent.getDepthMin() * (hypoVertex / hypoParent);                                
            }
                    
            // We set he value
            
            vertex.setMinDistance(score);
        }
        
         // We compotue the ic-value for each value
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We get the hypernyms of the node
            
            hyper = vertex.getAncestors(true);
            
            // We compute the average depth
            
            avgDepth = 0.0;
            
            for (IVertex hypernym: hyper)
            {
                avgDepth += hypernym.getDepthMin();
            }
            
            avgDepth /= hyper.getCount();
            
            // We compute the ic-value
            
            score = 0.0;
            
            for (IVertex hypernym: hyper)
            {
                score += hypernym.getMinDistance();
            }
            
            icValue = score * avgDepth;
        
            // We set the IC value
            
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
        return (IntrinsicICModelType.HadjTaieb.toString());
    }        
}
