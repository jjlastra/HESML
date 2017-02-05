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
 * This class implements the CondProbLeaves IC model introduced in the
 * following paper.
 * 
 * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
 * A new family of information content models with an experimental survey
 * on WordNet. Knowledge-Based Systems, 89, 509–526.
 * 
 * In addition, the commercial use of this IC model is protected
 * by the following US patent application:
 * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
 * System and method for the indexing and retrieval of semantically annotated
 * data using an ontology-based information retrieval model. United States
 * Patent and Trademark Office (USPTO) Application, US2016/0179945 A1.
 *
 * @author Juan Lastra-Díaz
 */

class CondProbLeavesICmodel extends AbstractCondProbICmodel
{
    /**
     * This function computes the data.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws java.lang.Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        double total;  // Total hyponyms for the children
        
        IEdge       incidentEdge;   // Incident endge
        IVertexList children;       // Children nodes
        
        double  weight;     // Weight for the edge
        double  condProb;  // Normalized conditional probability
        
        double  twoLog = Math.log(2.0);
        
        // NOTE: this function sets the edge weigths (IC(P(c|p)) when
        // the method is applied on the edges, otherwise, the
        // function saves the conditional probabilities in the edge weigths,
        // and in a second traversal computes the IC-node values.
        
        // We compute the weights of every edge ion the taxonomy
        
        for (IVertex parent: taxonomy.getVertexes())
        {
            // We get the children vertexes
            
            children = parent.getChildren();
            
            // We compute the total of hyponyms for the children
            
            total = 0.0;
                    
            for (IVertex child: children)
            {
                condProb = child.getNonInclusiveSubsumedLeafSetCount()+ 1.0;
                total += condProb;
            }
            
            // We compute the edge wieght for each edge
            
            for (IVertex child: children)
            {
                // We compute the normalized conditional probability
                
                condProb = (child.getNonInclusiveSubsumedLeafSetCount()+ 1.0) / total;
                
                // We get the incident edge joining the vertexes
                
                incidentEdge = parent.getIncidentEdge(child).getEdge();
                
                // We set the conditional probability
                
                incidentEdge.setCondProbability(condProb);
                
                // We compute the IC of the conditional probability as the
                // negative of its binary logarithm

                weight = -Math.log(condProb) / twoLog;
                incidentEdge.setWeight(weight);
            }
            
            // We remove the list of children
            
            children.clear();
        }
        
        // Now, if the target object is the node, we computed the
        // exact probabilities for the nodes according to the conditional
        // probabilities stored in the edge weights. Later, we computes
        // the IC values for each node.
        
        setNodesProbabilityAndICValue(taxonomy);
    }
    
    /**
     * This function returns a String representing IC model type of the
     * current object.
     * @return A string representing the IC model type of the current instance
     */
    
    @Override
    public String toString()
    {
        return (IntrinsicICModelType.CondProbLeaves.toString());
    }        
}
