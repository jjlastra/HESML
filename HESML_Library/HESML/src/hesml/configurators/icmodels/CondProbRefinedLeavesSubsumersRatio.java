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
 * This class implements the IC model called CondProbRefLeavesSubsumersRatio
 * that is introduced in the paper below.
 * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
 * A refinement of the well-founded Information Content models with
 * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
 * Department of Computer Languages and Systems. NLP and IR Research Group.
 * Universidad Nacional de Educación a Distancia (UNED).
 * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
 * 
 * In addition, the commercial use of this IC model is protected
 * by the following US patent application:
 * Lastra Díaz, J. J. and García Serrano, A. (2016).
 * System and method for the indexing and retrieval of semantically annotated
 * data using an ontology-based information retrieval model. United States
 * Patent and Trademark Office (USPTO) Application, US2016/0179945 A1.
 * 
 * @author j.lastra
 */

class CondProbRefinedLeavesSubsumersRatio  extends AbstractCondProbICmodel
{
    /**
     * This function computes the data.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws java.lang.Exception Sánchez-Batet-Isern factor
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        double totalProb;  // Total probability for the children
        
        IEdge   incidentEdge;   // Incident endge
        
        IVertexList children;   // Children nodes
        
        double  weight;     // Weight for the non-oriented edge
        double  condProb;   // Normalized conditional probability
        
        double  childCondProb;  // Child probability
        double  parentProb;
        
        double  twoLog = Math.log(2.0);
        
        // NOTE: this function sets the edge weigths (IC(P(c|p)) when
        // the method is applied on the edges, otherwise, the
        // function saves the conditional probabilities in the edge weigths,
        // and in a second traversal computes the IC-node values.
        
        // We compute the weights of every edge in the taxonomy
        
        for (IVertex parent: taxonomy.getVertexes())
        {
            // We compute the parent factor
            
            parentProb = getSanchez2011Factor(parent);
            
            // We get the children vertexes
            
            children = parent.getChildren();
            
            // We compute the overall probability for the children
            
            totalProb = 0.0;
                    
            for (IVertex child: children)
            {
                // We compute the estimation for the probability of the node

                childCondProb = getSanchez2011Factor(child)/ parentProb;
                
                // We accummulate the conditional probability estimation
                
                totalProb += childCondProb;
            }
            
            // We compute the edge weight for each edge
            
            for (IVertex child: children)
            {
                // We compute the estimation for the probability of the node

                childCondProb = getSanchez2011Factor(child) / parentProb;
                
                // We compute the normalized conditional probability
                
                condProb = childCondProb / totalProb;
                
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
            
            // We remove the list of children
            
            children.clear();
        }
        
        // Now, if the target object is the node, we computed the
        // exact probabilities for the nodes according to the conditional
        // probabilities stored into the edge weights. Later, we computes
        // the IC values for each node.
        
        setNodesProbabilityAndICValue(taxonomy);
        
        // We compute the final probability and IC values
        // using the refoined algorithm
        
        setNodesProbICvaluesBySubsumedLeaves(taxonomy, false);
    }
    
    /**
     * This function computes the Leaves/Subsumers ratio defined by
     * the Sánchez et al.(2011) IC model cited below.
     * Sánchez, D., Batet, M., and Isern, D. (2011).
     * Ontology-based information content computation.
     * Knowledge-Based Systems, 24(2), 297–303.
     * 
     * @param vertex Base vector for the computation of the Sánchez-Batet-Isern factor
     * @return Sánchez-Batet-Isern factor for the input vertex
     * @throws Exception Unexpected error
     */
    
    private double getSanchez2011Factor(
        IVertex vertex) throws Exception
    {
        double  sanchezFactor;  // Returned value
        
        double  leavesCount;    // Non-inclusive subsumed leaves count
        double  subsumersCount; // Inclusive subsumerscount
        
        // We get the leaves and subsumer count of the node

        leavesCount = vertex.getNonInclusiveSubsumedLeafSetCount();
        subsumersCount = vertex.getNonInclusiveAncestorSetCount() + 1;

        // We compute the estimation for the probability of the node

        sanchezFactor = 1.0 + (leavesCount / subsumersCount);
        
        // We return the result
        
        return (sanchezFactor);
    }
    
    /**
     * This function returns a String representing IC model type of the
     * current object.
     * @return A string representing the IC model type of the current instance
     */
    
    @Override
    public String toString()
    {
        return (IntrinsicICModelType.CondProbRefLeavesSubsumersRatio.toString());
    }        
}