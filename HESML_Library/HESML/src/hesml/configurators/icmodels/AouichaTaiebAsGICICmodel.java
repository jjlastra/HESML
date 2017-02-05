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

import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;

/**
 * This class implements the AsGIC IC model introduced by Aouicha and
 * Hadj Taieb in the paper below:
 * Aouicha, M. B., and Taieb, M. A. H. (2015).
 * Computing semantic similarity between biomedical concepts using new
 * information content approach. Journal of Biomedical Informatics.
 * http://doi.org/10.1016/j.jbi.2015.12.007
 * 
 * @author j.lastra
 */

class AouichaTaiebAsGICICmodel extends AbstractICmodel
    implements ITaxonomyInfoConfigurator
{
    /**
     * This function computes the IC model proposed by Aouicha and Hadj Taieb.
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
        
        double  ancestorSpec;   // Ancestor specificity (eq.21 in the paper)
        double  hypoOverlap;    // Hyponym graph overlapping factor (eq. 22)

        IVertexList ancestors;              // Ancestor set
        IVertexList descendants;            // Descendant set (hyponyms)
        IVertexList hypernymDescendants;    // Descendants of a hypernym node
        IVertexList parents;                // Direct parents of an ancestor
        
        int i = 1;  // Counter
        
        // We compute the IC value for each node. 
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // This IC model spend a lot of hours to be computed.
            // For this reason, we show a console message in
            // order to report the progress of the method.
            
            System.out.println("Computing IC value of the vertex #" + i
                    + " of " + taxonomy.getVertexes().getCount());
            
            // We get the ancestor set including the base vertex
            
            ancestors = vertex.getAncestors(true);
            
            // We compute and accumulate the ancestor specificity
            // for each ancestor node
            
            icValue = 0.0;
            
            for (IVertex ancestor: ancestors)
            {
                // We get the descendants and parent (hypernyms)
                // of the ancestor node
                
                descendants = ancestor.getHyponyms(true);
                parents = ancestor.getParents();
                
                // We compute the ancestor specificity
                
                ancestorSpec = 0.0;
                
                for (IVertex hypernym: parents)
                {
                    // We get the descendants of the parent node
                    
                    hypernymDescendants = hypernym.getHyponyms(true);
                    
                    // We compute the hyponym overlapping factor
                    
                    hypoOverlap = ((double)descendants.getCount()) /
                                ((double)hypernymDescendants.getCount());
                    
                    // We release the hyperhym descendants
                    
                    hypernymDescendants.clear();
                    
                    // We accumulate the ancestor specificity
                    
                    ancestorSpec += hypernym.getDepthMax() * hypoOverlap;
                }
                
                // We accumulate the ancestor specificity
                
                icValue += ancestorSpec;
                
                // We clear the vertexes lists
                
                descendants.clear();
                parents.clear();
            }
            
            // We set the IC value
            
            vertex.setICValue(icValue);
            
            // We clear the ancestor list
            
            ancestors.clear();
            
            // Increment the vertex counter
            
            i++;
        }
        
        // We set the delta IC weights for the weighted Measures
        
        setICDeltaWeights(taxonomy);
    }
    
    /**
     * This function returns a String representing the IC model type of the
     * current object.
     * @return A string representing the IC model type of the current instance
     */
    
    @Override
    public String toString()
    {
        return (IntrinsicICModelType.AouichaTaiebAsGIC.toString());
    }
}
