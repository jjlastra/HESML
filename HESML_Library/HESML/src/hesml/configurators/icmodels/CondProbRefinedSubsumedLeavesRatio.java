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

import hesml.configurators.IntrinsicICModelType;
import hesml.taxonomy.IEdge;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;

/**
 * This class implements the IC model called CondProbRefSubsumedLeavesRatio 
 * that is introduced in the paper below.
 * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
 * A refinement of the well-founded Information Content models with
 * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
 * Department of Computer Languages and Systems. NLP and IR Research Group.
 * Universidad Nacional de Educación a Distancia (UNED).
 * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
 * 
 * @author j.lastra
 */

public class CondProbRefinedSubsumedLeavesRatio  extends AbstractCondProbICmodel
{
    /**
     * This function computes the data.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws java.lang.Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        double  totalProb;  // Total probability for the children
        
        IEdge   incidentEdge;   // Incident endge
        
        IVertexList children;   // Children nodes
        
        IVertexList parentSubsumedLeaves;   // Subsumed leaves
        IVertexList childSubsumedLeaves;
        
        double  weight;     // Weight for the direct oriented edge
        double  condProb;   // Normalized conditional probability
        
        double  twoLog = Math.log(2.0);
        
        // NOTE: this function sets the edge weigths (IC(P(c|p)) when
        // the method is applied on the edges, otherwise, the
        // function saves the conditional probabilities inyo the edge weigths,
        // and in a second traversal computes the IC-node values.
        
        // We compute the weights of every edge in the taxonomy
        
        for (IVertex parent: taxonomy.getVertexes())
        {
            // We get the children vertexes
            
            children = parent.getChildren();
            
            // We get the subsumed leaves of the parent node
            
            parentSubsumedLeaves = parent.getSubsumedLeaves(true);
            
            // We compute the overall probability for the children
            
            totalProb = 0.0;

            // We compute the estimation for the probability of the node
            // using the Sánchez et al. (2011) factor.
            
            for (IVertex child: children)
            {
                // We get the subsumed leaves of the child
                
                childSubsumedLeaves = child.getSubsumedLeaves(true);
                
                // We computes the subsumed leaves ratio between
                // the child and parent node
                
                condProb = childSubsumedLeaves.getIntersectionSetCount(parentSubsumedLeaves);
                condProb /= (double) parentSubsumedLeaves.getCount();
                
                // We accumulate the overall conditional probability
                
                totalProb += condProb;
                
                // We release the auxiliary list
                
                childSubsumedLeaves.clear();
            }
            
            // We compute the edge weight for each edge
            
            for (IVertex child: children)
            {
                // We get the subsumed leaves of the child node
                
                childSubsumedLeaves = child.getSubsumedLeaves(true);
                
                // We computes the subsumed leaves ratio between
                // the child and parent node
                
                condProb = childSubsumedLeaves.getIntersectionSetCount(parentSubsumedLeaves);
                condProb /= (double) parentSubsumedLeaves.getCount();
                
                // We release the subsumed leaves of the child node
                
                childSubsumedLeaves.clear();
                
                // We normalize the conditional probability
                
                condProb /= totalProb;
                
                // We get the incident edge joining the vertexes
                
                incidentEdge = parent.getIncidentEdge(child).getEdge();
                
                // We set the conditional probability for the edge
                
                incidentEdge.setCondProbability(condProb);
                
                // We compute the IC of the conditional probability as the
                // negative of its binary logarithm

                weight = -Math.log(condProb) / twoLog;
                
                // We set the weight for the non-oriented edge
                
                incidentEdge.setWeight(weight);
            }
            
            // We remove the list of children and the subsumed leaves
            
            children.clear();
            parentSubsumedLeaves.clear();
        }
        
        // Now, if the target object is the node, we computed the
        // exact probabilities for the nodes according to the conditional
        // probabilities stored in the edge weights. Later, we computes
        // the IC values for each node.
        
        setNodesProbabilityAndICValue(taxonomy);
        
        // We compute the final probability and IC values
        // using the refined algorithm
        
        setNodesProbICvaluesBySubsumedLeaves(taxonomy, false);
    }
    
    /**
     * This function returns a String representing IC model type of the
     * current object.
     * @return A string representing the IC model type of the current instance
     */
    
    @Override
    public String toString()
    {
        return (IntrinsicICModelType.CondProbRefSubsumedLeavesRatio.toString());
    }        
}