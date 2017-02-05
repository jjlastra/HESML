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
        
/**
 * This class is the base class for all the IC models in
 * the family of well-founded IC models introduced in the paper below.
 * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
 * A new family of information content models with an experimental survey
 * on WordNet. Knowledge-Based Systems, 89, 509–526.
 * 
 * @author Juan Lastra-Díaz
 */

abstract class AbstractCondProbICmodel extends AbstractICmodel
    implements ITaxonomyInfoConfigurator
{
    /**
     * This function sets the probabilities values fore each node.
     * The values are saved in the IC field.
     * @param taxonomy Target taxonomy 
     * @throws Exception Unexpected error
     */
    
    protected void setNodesProbabilityAndICValue(
            ITaxonomy   taxonomy) throws Exception
    {
        double  childProb;  // Probability
        
        IVertex root;   // Root node
        
        IHalfEdge   loop;   // Cursor
        IHalfEdge   first;
        
        double  log2 = Math.log(2.0);
        
        // We reset the visiting flags for all the vertexes

        taxonomy.getVertexes().setVisited(false);
        
        // We set to 1 the probability of the root node
        
        root = taxonomy.getVertexes().getAt(0);
        root.setProbability(1.0);
        root.setICValue(0.0);
        root.setVisited(true);
        
        // We compute the probabilities for each vertex
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We check that the vertex has not been visited
            
            if (!vertex.getVisited())
            {
                // We initialize  the probability of the vertex

                childProb = 0.0;

                // We iterate on the pàrents

                first = vertex.getFirstOutcomingEdge();
                loop = first;
                
                do
                {
                    // We check if the target is a parent
                    
                    if (loop.getEdgeType() == OrientedEdgeType.SubClassOf)
                    {
                        // We accumulate the probability
                    
                        childProb += loop.getEdge().getCondProbability() *
                                    loop.getTarget().getProbability();
                    }
                    
                    // We move the sursor
                    
                    loop = loop.getOpposite().getNext();
                    
                } while (loop != first);
                
                // We set the probability of the node
                // By numerical tolerances, and the cognitive method,
                // could occur that probability being greater than 1
                
                vertex.setProbability(Math.min(childProb, 1.0));
                
                // We set the IC value of the node
                
                vertex.setICValue(-Math.log(vertex.getProbability()) / log2);
                
                // We set the traverse flag
                
                vertex.setVisited(true);
            }
        }
        
        // We set the asymmetric weights in order to allow the evaluation
        // of the asymmetric weighted Jiang-Conrath distance
        
        setAsymmetricWeights(taxonomy);        
    }
    
    /**
     * This function sets the asymmetric weights based on the conditional
     * probabilities. 
     * @param taxonomy Taxonomy whose IC model will be computed
     */
    
    protected void setAsymmetricWeights(
        ITaxonomy   taxonomy)
    {
        IHalfEdge   loop;           // Cursor
        IHalfEdge   firstOutEdge;   // First outcoming edge
        
        double  weight;                 // Weight of the ascending edges
        double  log2 = Math.log(2.0);   // Log2
        
        // We traverse all the vertexes in order to set the asymmetric
        // weights for each oriented edge between the vertex and its parents
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We get the first outcoming edge of the vertex
            
            firstOutEdge = vertex.getFirstOutcomingEdge();
            loop = firstOutEdge;
            
            // We iterates around the vertex
            
            do
            {
                // We check if the outcoming edge is pointing to a parent vertex
                
                if (loop.getEdgeType() == OrientedEdgeType.SubClassOf)
                {
                    // We set the weight of the descending edge like the
                    // current IC-based weight derived from the conditional
                    // probability
                    
                    loop.getOpposite().setWeight(loop.getEdge().getWeight());
                    
                    // We set the weight of the ascending edge as
                    // weight = -log(p(parent | child)/log(2), but it 
                    // is equal to w = log(p(child)) / log(2) because of
                    // p(parent | child) = 1 / p(child)
                    
                    weight = -Math.log(1.0 / vertex.getProbability()) / log2;
                    loop.setWeight(weight);
                }
                
                // We go to the next vertex
                
                loop = loop.getOpposite().getNext();
                
            } while (loop != firstOutEdge);
        }
    }
    
    /**
     * This function normalizes the probability of the leaf nodes,
     * such that the overall probability for the leaves be 1.
     * @param taxonomy 
     */
    
    protected void setNormalizedLeavesProbability(
        ITaxonomy   taxonomy)
    {
        double  leavesProb = 0.0;   // Sum of the leaf node probability
        
        // We compute the overall probability of the leaf nodes
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            if (vertex.isLeaf())
            {
                leavesProb += vertex.getProbability();
            }
        }
        
        // We traverse the leaf nodes again and set their probability
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            if (vertex.isLeaf())
            {
                vertex.setProbability(vertex.getProbability() / leavesProb);
            }
        }
    }
    
    /**
     * This function sets the node probabilities and IC values to the
     * sum of the probabilities of the subsumed leaf nodes. This
     * function is invoked by all the CondProb IC models, thus,
     * the function assumes that the sum of the probability over
     * the leaf vertexes is equal to 1.
     * @param taxonomy Taxonomy whose IC model is being computed
     * @param setWeightsToICdiff Edge weights will be defined as |IC(a) - IC(b)|
     * @throws Exception Unexpected error
     */
    
    protected void setNodesProbICvaluesBySubsumedLeaves(
        ITaxonomy   taxonomy,
        boolean     setWeightsToICdiff) throws Exception
    {
        IVertexList  leaves; // Subsumed leaves
        
        double  probability;            // Node probability
        double  log2 = Math.log(2.0);
        
        // We set the probability and IC value of each node
        // using the subsumed leaves.
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We skip the root because its probbaility is exactly 1
            // and none modification is required.
            
            if (!vertex.isRoot())
            {
                // We get the subsumed leaves of the vertex
                
                leaves = vertex.getSubsumedLeaves(true);
                
                // We set the overall node probability to 0
                
                probability = 0.0;
                
                // We compute the accumulated probability
                
                for (IVertex leaf: leaves)
                {
                    probability += leaf.getProbability();
                }
                
                // We set the overall probability and IC vlaue
                // of the vertex
                
                vertex.setProbability(probability);
                vertex.setICValue(-Math.log(probability) / log2);
                
                // We destroy the list of subsumed leaves
                
                leaves.clear();
            }
        }
        
        // We set the delta IC weights for the weighted edge-based
        // similarity measures

        if (setWeightsToICdiff)
        {
            setICDeltaWeights(taxonomy);
        }
    }
}
