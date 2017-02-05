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

package hesml.taxonomy.impl;

// Java references

import java.util.ArrayList;

// HESML references

import hesml.taxonomy.*;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * This class implements the ITaxonomy interface which represents an
 * abstract taxonomy based on a half-edge representation as detailed in
 * the paper below.
 * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * HESML: a scalable ontology-based semantic similarity measures
 * library with a set of reproducible experiments and a replication dataset.
 * To appear in the Information Systems Journal.
 * 
 * @author Juan Lastra-Díaz
 */

class Taxonomy implements ITaxonomy
{
    /**
     * Collection of non-oriented edges of the taxonomy
     */
    
    private final EdgeList    m_Edges;
    
    /**
     * Full indexed collection of the vertexes within the taxonomy.
     */
    
    private final VertexList  m_Vertexes;
    
    /**
     * Constructor
     */
    
    Taxonomy()
    {
        m_Edges = new EdgeList();
        m_Vertexes = new VertexList(true);
    }
    
    /**
     * Constructor with pre-reserved memory for a number of vertexes
     * defined by the input parameter.
     * @param initialCapacity Expected quantity of vertexes
     */
    
    Taxonomy(int initialCapacity)
    {
        m_Edges = new EdgeList(initialCapacity);
        m_Vertexes = new VertexList(true, initialCapacity);
    }
    
    /**
     * This function returns the overall sum of the leaf node probabilities.
     * @return Overall probability in the leaf nodes.
     */
    
    @Override
    public double getSumLeafProbability()
    {
        double  totalProb = 0.0;    // Retuirned value
        
        // We sum the probability of the leaf nodes
        
        for (IVertex vertex: m_Vertexes)
        {
            if (vertex.isLeaf())
            {
                totalProb += vertex.getProbability();
            }
        }
        
        // We return the result
        
        return (totalProb);
    }
    
    /**
     * This function computes the lowest common subsumer (ancestor), defined
     * as the common ancestor with highest depth. The function
     * returns the first vertex in the LCA set, although could exist
     * more than one on multiple inheritance taxonomies. As the node depth
     * is defined in two different ways, the last parameter defines what
     * depth is used to recover the LCS vertex.
     * @param begin
     * @param end
     * @param useLongestDepth Use the softest ascending path depth attribute (false)
     * or the depth attribute defined as the longest ascending path to the root.
     * @return LCS vertex
     */
    
    @Override
    public IVertex getLCS(
            IVertex begin,
            IVertex end,
            boolean useLongestDepth) throws Exception
    {
        Exception   error;      // Error thrown
        String      strError;   // Error message
        
        IVertex lcaVertex = null;    // Returned value

        int  maxDepth = -1;    // Maximum
        
        // We retrieve the inclusive and unordered ancestor sets
        // of the input vertexes
        
        HashSet<IVertex> beginAncestors = getUnorderedAncestorSet(begin);
        HashSet<IVertex> endAncestors = getUnorderedAncestorSet(end);
        
        // We traverse all the vertexes looking for the common ancestor
        // which satisfies the LCS criterium.
        // In order to speed up the intersection of both sets,
        // we check first the condition to select the vertex than
        // its membership to the opposite ancestor set.
        
        for (IVertex vertex: beginAncestors)
        {
            if (!useLongestDepth
                    && (vertex.getDepthMin() > maxDepth)
                    && (endAncestors.contains(vertex)))
            {
                maxDepth = vertex.getDepthMin();
                lcaVertex = vertex;
            }
            else if (useLongestDepth
                    && (vertex.getDepthMax() > maxDepth)
                    && (endAncestors.contains(vertex)))
            {
                maxDepth = vertex.getDepthMax();
                lcaVertex = vertex;
            }
        }
        
        // We reset the visited sets
        
        beginAncestors.clear();
        endAncestors.clear();
        
        // We check the mica
        
        if (lcaVertex == null)
        {
            strError = "The vertexes do not share a common ancestor";
            error = new Exception(strError);
            throw (error);
        }
               
        // We return the result
        
        return (lcaVertex);
    }
    
    /**
     * Remove all the objects and destroys the taxonomy.
     */
    
    @Override
    public void clear()
    {
        // We catch the exceptions
        
        try
        {
            // We destroy all the vertexes
            
            for (IVertex vertex: m_Vertexes)
            {
                ((Vertex)vertex).destroy();
            }
            
            // We destroy all the edges
            
            for (IEdge edge: m_Edges)
            {
                ((Edge)edge).destroy();
            }
            
            // We reset all the object list

            m_Edges.clear();
            m_Vertexes.clear();
        }
        
        // We include the catch to filter the compiler error
        // of the unexpected exception thrown by the VertexList object.
        // We note that m_Vertexes is the only locked VertexList
        // object in HESML, whose aim is to avoid any user modification.
        
        catch (Exception e)
        {
            
        }
    }
    
    /**
     * This function returns the most informative common ancestor (MICA) vertex.
     * The method marks all the ancestor vertexes from both input vertexes,
     * then it looks for the intersection set which is defined as the
     * marked vertexes in both ancestor sets.
     * @param begin First input vertex
     * @param end Second input vertex
     * @return The most informative common ancestor (MICA) vertex.
     */

    @Override
    public IVertex getMICA(
            IVertex begin,
            IVertex end) throws InterruptedException, Exception
    {
        IVertex micaVertex = null;    // Returned value

        double  maxIC = Double.NEGATIVE_INFINITY;    // Maximum
        
        // We retrieves the inclusive ancestor sets of the input vertexes
        
        HashSet<IVertex> beginAncestors = getUnorderedAncestorSet(begin);
        HashSet<IVertex> endAncestors = getUnorderedAncestorSet(end);
        
        // We traverse all the vertexes looking for the common ancestor
        // which satisfies the MICA criterium.
        // In order to speed up the intersection of both sets,
        // we check first the condition to select the vertex than
        // its membership to the opposite ancestor set.
        
        for (IVertex vertex: beginAncestors)
        {
            if ((vertex.getICvalue() > maxIC)
                    && endAncestors.contains(vertex))
            {
                maxIC = vertex.getICvalue();
                micaVertex = vertex;
            }
        }
        
        // We release the visiting sets
        
        beginAncestors.clear();
        endAncestors.clear();
        
        // We check the mica
        
        if (micaVertex == null)
        {
            throw (new Exception("A MICA vertex was not found"));
        }
               
        // We return the result
        
        return (micaVertex);
    }
    
    /**
     * This function retrieves the ancestor set of the seed vertex,
     * including this later one, and returns the set as an unordered
     * HashSet, instead of the heavier and ordered IVertexList.
     * @param seed Seed vertex whose ancestors set will be visited.
     * @return The ancestor set.
     * @throws InterruptedException 
     */
    
    private HashSet<IVertex>  getUnorderedAncestorSet(
            IVertex seed) throws InterruptedException
    {
        // We create the output ancestor set
        
        HashSet<IVertex>    ancestorSet = new HashSet<>();
        
        // We create the traversal queue
        
        LinkedList<IVertex>  pending = new LinkedList<>();
        
        // We enqueue this vertes to mark all the ancestors
        
        pending.add(seed);
        ancestorSet.add(seed);
        
        // We traverse the ancestors of the current vertex
        
        while (!pending.isEmpty())
        {
            // We get the next vertex to explore
            
            IVertex current = pending.remove();
                    
            // We enqueue all its parents. We use a direct loop traversal
            // of the vertex to retrieve its parents, with the aim to
            // reduce the time spent in the creation of a list whenever
            // the getParents() emthod is invoked.
            
            IHalfEdge firstEdge = current.getFirstOutcomingEdge();
            IHalfEdge loop = firstEdge;
            
            do
            {
                // We check if the adjacent vertex is a parent
                
                if (loop.getEdgeType() == OrientedEdgeType.SubClassOf)
                {
                    IVertex parent = loop.getTarget();
                    
                    if (!ancestorSet.contains(parent))
                    {
                        pending.add(parent);
                        ancestorSet.add(parent);
                    }
                }
                
                // We move to the next outcoming edge
                
                loop = loop.getOpposite().getNext();
                
            } while (loop != firstEdge);
        }
        
        // We return the result
        
        return (ancestorSet);
    }
    
    /**
     * This function forces the computation and caching of the hyponym set
     * count for each concept in the taxonomy. It is made to accelerate the
     * computation of several IC models using the Hyponym count feature.
     */
    
    private void computeHyponymCount() throws Exception
    {
        for (IVertex vertex: m_Vertexes)
        {
            vertex.getNonInclusiveHyponymSetCount();
        }
    }
    
    /**
     * This function forces the computation of several taxonomical features
     * required by IC models and ontology-based similarity measures. All the
     * cached features correspond to integer values as the node depths, and
     * cardinals of sets as the leaves count and hyponym count. The taxonomy
     * does not store any cache of object collections which are always computed
     * on-the-fly through the adjacency relationships encoded by using the
     * posetHERep adjacency model.
     * This function is especially helpful for the speedup of cross-validation
     * benchmarks of similarity measures and IC models.
     * @throws Exception 
     */
    
    @Override
    public void computesCachedAttributes() throws Exception
    {
        // Computes the hyponyms and leaves count
        
        this.computeHyponymCount();
        this.computeAllDepths();
        this.computeLeavesCount();        
    }
    
    /**
     * This function computes the minimum and maximum depth
     * for all vertexes within the taxonomy. The minimum depth
     * is measured as the shortest ascending path from the vertex
     * to the root, whilst the maximum depth is defined as the
     * length of the longest ascending path from the vertex
     * to the root.
     */
    
    private void computeAllDepths() throws Exception
    {
        // We request all the minimum depths of the vertexes
        // in order to force their computation on-the-fly and caching
        
        for (IVertex vertex: m_Vertexes)
        {
            vertex.getDepthMin();
            vertex.getDepthMax();
        }
    }
    
    /**
     * This function forces the computation and caching of the leaves set
     * count for each concept in the taxonomy. It is made to accelerate the
     * computation of several IC models using the leaves count feature.
     */
    
    private void computeLeavesCount() throws Exception
    {
        for (IVertex vertex: m_Vertexes)
        {
            vertex.getNonInclusiveSubsumedLeafSetCount();
        }
    }
    
    /**
     * This function inserts a novel vertex in the graph by connecting it
     * to its parents. We note that the parent vertexes must be already
     * present within the taxonomy before the creation and insertion of
     * a new vertex.
     * @param vertexId Integer unique key of the new vertex.
     * @param parentVertexes Integer IDs of the parent vertexes.
     * @return The new vertex inserted into the taxonomy
     */

    @Override
    public IVertex  addVertex(
            Integer     vertexId,
            Integer[]   parentVertexes) throws Exception
    {
        Exception   error;      // Error thrown
        String      strError;   // Error message
                
        Vertex  newVertexInserted;      // Novel vertex
        Vertex  targetAdjacentVertex;   // Target adjacent vertex
        
        ArrayList<Edge>  edges;  // Edges of the node
        
        Edge    parentEdge; // Arista padre
        IVertex parent;     // Parent vertex

        HalfEdge   outArc; // Arcs from the vertex
        HalfEdge   inArc;
        
        // We check the new ID
        
        if (m_Vertexes.contains(vertexId))
        {
            strError = "The taxonomy alreaday contains a vertex with this ID";
            error = new Exception(strError);
            throw (error);
        }
        
        // We create the novel vertex
        
        newVertexInserted = new Vertex(vertexId, this);
        
        // We create the vector of parents
        
        edges = new ArrayList<>();
        
        // We get the parent vertexes
        
        for (Integer parentId: parentVertexes)
        {
            // We get the parent vertex
            
            parent = m_Vertexes.getById(parentId);
            
            assert (parent != null);
            
            // We create the whole edge
            
            parentEdge = new Edge(newVertexInserted, parent);
            
            // We save the edge in the global list and the parent edges
            
            m_Edges.add(parentEdge);
            edges.add(parentEdge);
        }
               
        // We connect the edges
        
        if (edges.size() > 0)
        {
            // We set the first outcoming arc
            
            newVertexInserted.setFirstOutEdge(edges.get(0).getDirect());
            
            // We connect the edges around the vertexes
            
            for (IEdge edge: edges)
            {
                // We get the arcs from the vertex
                
                outArc = (HalfEdge) edge.getDirect();
                inArc = (HalfEdge) outArc.getOpposite();
                
                // We connect the arcs around the vertexes
                
                targetAdjacentVertex = (Vertex) outArc.getTarget();
                targetAdjacentVertex.addIncomingArc(outArc);
                newVertexInserted.addIncomingArc(inArc);
            }
        }
        
        // We store the vertex
        
        m_Vertexes.add(newVertexInserted);
        
        // We clear the edges set
        
        edges.clear();
        
        // We return the new vertex
        
        return (newVertexInserted);
    }

    /**
     * This function returns the global collection of vertexes contained
     * in the taxonomy.
     * @return Concepts in the taxonomy
     */
    
    @Override
    public IVertexList getVertexes()
    {
        return (m_Vertexes);
    }

    /**
     * This function returns the global collection of non-oriented edges
     * contained in the taxonomy.
     * @return Edges of the concepts in the taxonomy
     */
    
    @Override
    public IEdgeList getEdges()
    {
        return (m_Edges);
    }    
}
