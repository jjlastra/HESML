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

import java.util.PriorityQueue;

// HESML references

import hesml.taxonomy.*;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * This class implements a IVertex object in the half-edge representation
 * detailed in the paper below.
 * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * HESML: a scalable ontology-based semantic similarity measures
 * library with a set of reproducible experiments and a replication dataset.
 * To appear in the Information Systems Journal.
 * 
 * @author Juan Lastra-Díaz
 */

class Vertex implements IVertex
{
    /**
     * Unique key for the node. This field is used to link
     * to the WordNet graph.
     */
    
    private final Integer  m_Id;
    
    /**
     * First half edge outcoming from the vertex.
     */
    
    private IHalfEdge   m_FirstOutArc;
    
    /**
     * Owner graph
     */
    
    private final Taxonomy  m_Taxonomy;
    
    /**
     * Visiting flag
     */
    
    private boolean m_Visited;

    /**
     * Information Content value for the node.
     */
    
    private double  m_ICvalue;
    
    /**
     * Client code field
     */
    
    private Object  m_Tag;
    
    /**
     * This field stores a user-defined Tag
     */
    
    private String  m_strTag;
    
    /**
     * Special field to implement efficiently the Dijkstra's algorithm
     */
    
    private double  m_minDistance;
    
    /**
     * Cached count of the hyponyms.
     */
    
    private int m_CachedHyponymsCount;
    
    /**
     * Cached number of leaves.
     */
    
    private int m_CachedLeavesCount;
    
    /**
     * Cached number of ancestors.
     */
    
    private int m_CachedAncestorsCount;
    
    /**
     * Minimal depth of the vertex defined as the length of
     * the shortest path from the vertex to the root.
     */
    
    private int m_CachedDepthMin;
    
    /**
     * Maximal depth of the vertex defined as the length of
     * the longest path from the vertex to the root.
     */
    
    private int m_CachedDepthMax;
    
    /**
     * Probability of the node.
     */
    
    private double  m_Probability;
    
    /**
     * Constructor
     * @param id Integer unique key of a Graph node (WordNet nodes)
     * @param taxonomy Container taxonomy of the new vertex
     */
    
    Vertex(
            Integer     id,
            ITaxonomy   taxonomy)
    {
        // We save the key
        
        m_Id = id;
        m_Taxonomy = (Taxonomy) taxonomy;
        m_Visited = false;
        m_FirstOutArc = null;
        m_Tag = null;
        m_strTag = "";
        m_CachedHyponymsCount = -1;
        m_CachedLeavesCount = -1;
        m_CachedAncestorsCount = -1;
        m_CachedDepthMin = -1;
        m_CachedDepthMax = -1;
        m_minDistance = 0.0;
        m_ICvalue = 0.0;
        m_Probability = 0.0;
    }
    
    /**
     * This function returns the hashCode of the vertex.
     * @return 
     */
    
    @Override
    public int hashCode()
    {    
        return (m_Id.hashCode());
    }

    /**
     * This function states the equality only based on the vertex ID,
     * not the attributes.
     * @param obj
     * @return 
     */
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vertex other = (Vertex) obj;
        if (this != other)
        {
            return false;
        }
        return true;
    }
    
    /**
     * This function disconnects the vertex from all the related objects
     * in order to release all resources and to destroy the taxonomy.
     */
    
    void destroy()
    {
        m_FirstOutArc = null;
        m_Tag = null;
        m_strTag = null;
    }
    
    /**
     * This function returns the content of the string Tag field
     * @return A user-defined string value.
     */
    
    @Override
    public String getStringTag()
    {
        return (m_strTag);
    }
    
    /**
     * This function sets the user-defined string field.
     * @param strTag 
     */
    
    @Override
    public void setStringTag(String strTag)
    {
        m_strTag = strTag;
    }
    
    /**
     * This function returns the probability stored in the node.
     * @return The probability field value.
     */
    
    @Override
    public double getProbability()
    {
        return (m_Probability);
    }
    
    /**
     * This function returns the probability stored in the node.
     * @param probability The new probability value assigned to the vertex.
     */
    
    @Override
    public void setProbability(
        double  probability)
    {
        m_Probability = probability;
    }
    
    /**
     * This function returns the depth of the vertex defined as the
     * shortest ascending path from the vertex to the root.
     * @return Depth
     */
    
    @Override
    public int getDepthMin() throws Exception
    {
        // We force the computation of all depths
        
        if (m_CachedDepthMin < 0)
        {
            m_CachedDepthMin = computeMinDepth();
        }
        
        // We return the result
        
        return (m_CachedDepthMin);
    }
    
    /**
     * This function returns the depth of the vertex defined as the
     * longest ascending path from the vertex to the root.
     * @return Longest depth
     */
    
    @Override
    public int getDepthMax() throws Exception
    {
        // We force the computation of all depths
        
        if (m_CachedDepthMax < 0)
        {
            m_CachedDepthMax = computeMaxDepth();
        }
        
        // We return the result
    
        return (m_CachedDepthMax);
    }
    
    /**
     * This function returns depth + 1
     * @return The depth
     */
    
    @Override
    public int getDepthMinBase1() throws Exception
    {
        return (1 + getDepthMin());
    }
    
    /**
     * This function returns longest depth + 1
     * @return The depth
     */
    
    @Override
    public int getDepthMaxBase1() throws Exception
    {
        return (1 + getDepthMax());
    }
    
    /**
     * This function checks whether the current vertex does not have parents,
     * thus, it is a root node.
     * @return True if the vertex is a root
     */
    
    @Override
    public boolean isRoot()
    {
        boolean root = true;    // returned value
        
        IHalfEdge   loop = m_FirstOutArc;   // Cursor
        
        // We iterate around the vertex
        
        if (loop != null)
        {
            do
            {
                // We insert the children vertexes

                if (loop.getEdgeType() == OrientedEdgeType.SubClassOf)
                {
                    root = false;
                    break;
                }

                // We get the next outcoming arc

                loop = loop.getOpposite().getNext();

                assert(loop != null);

            } while (loop != m_FirstOutArc);
        }
        
        // We return the result
        
        return (root);
    }
    
    /**
     * Checks if the current vertex is a Leaf node, it means that
     * the vertex does not have other descendant vertexes.
     * @return true when the vertex is a leaf node.
     */
    
    @Override
    public boolean isLeaf()
    {
        boolean leaf = false;   // returned value
        
        // We check if the node is leaf
        
        if (getChildrenCount() == 0)
        {
            leaf = true;
        }
        
        // We retgurn the result
        
        return (leaf);
    }
    
    /**
     * This function gets all the ancestors of the input
     * vertex, including itself.
     * @param includeVertex This parameter indicates if the own vertex will included in the output list
     * @throws InterruptedException 
     */
    
    @Override
    public IVertexList getAncestors(
        boolean includeVertex) throws InterruptedException, Exception
    {
        VertexList  ancestors = new VertexList(false);  // Returned value
        
        // We obtain the global visited1 set to maintain the
        // set of visited vertexes
        
        HashSet<IVertex> visited = new HashSet<>();

        // We create the traversal queue
        
        LinkedList<IVertex> pending = new LinkedList<>();
        
        // We enqueue this vertex to mark all the ancestors
        
        pending.add(this);
        visited.add(this);
        
        // We traverse the ancestors of the current vertex
        
        while (!pending.isEmpty())
        {
            // We get the next ancestor to expand
            
            IVertex ancestor = pending.remove();
            
            // We add the vertex to the ancestor set
            
            ancestors.add(ancestor);
                    
            // We enqueue all its parents. We use a direct loop traversal
            // of the vertex to retrieve its parents, with the aim to
            // reduce the time spent in the creation of a list whenever
            // the getParents() method is invoked.
            
            IHalfEdge   ancestorFirstOutcomingArc = ancestor.getFirstOutcomingEdge();
            IHalfEdge   loop = ancestorFirstOutcomingArc;
            
            do
            {
                // We enqueue the parent vertexes

                if (loop.getEdgeType() == OrientedEdgeType.SubClassOf)
                {
                    IVertex parent = loop.getTarget();
                    
                    if (!visited.contains(parent))
                    {
                        pending.add(parent);
                        visited.add(parent);
                    }
                }

                // We get the next outcoming arc

                loop = loop.getOpposite().getNext();

            } while (loop != ancestorFirstOutcomingArc);
        }
        
        // We remove the current vertex when the function
        // call does not include it
        
        if (!includeVertex)
        {
            ancestors.remove(this);
        }
        
        // We reset the visited set
        
        visited.clear();
        
        // We return the result
        
        return (ancestors);
    }
    
    /**
     * This function returns the value of the minimum distance field.
     * It is used by all the Djikstra-like algorithms.
     * @return The minimum accumulated distance in a Djikstra method
     */
    
    @Override
    public double getMinDistance()
    {
        return (m_minDistance);
    }
    
    /**
     * This function sets the value of the minimum distance field.
     * It is used by all the Djikstra-like algorithms.
     * @param minDistance 
     */
    
    @Override
    public void setMinDistance(double  minDistance)
    {
        m_minDistance = minDistance;
    }
    
    /**
     * This function returns the first outcoming oriented edge from the
     * vertex.
     * @return The first outcoming oriented edge.
     */
    
    @Override
    public IHalfEdge getFirstOutcomingEdge()
    {
        return (m_FirstOutArc);
    }
    
    /**
     * This function computes the distance field from the current vertex
     * using the Dijkstra algorithm and the edge weights
     * assigned to the taxonomy. When the parameter 'weighted' is false, the
     * function uses the edge length (weight = 1), otherwise
     * it computes the weighted shortest path distance.
     * Once the function is executed, the distance from the vertex
     * to each vertex in the taxonomy can be recovered by calling
     * the getMinDistance() function on each vertex.
     * @param weighted Flag indicating if the edge weights will be used
     */
    
    @Override
    public void computeDistanceField(
            boolean     weighted)
    {
        PriorityQueue<IVertex> pending; // Processing queue

        IHalfEdge   firstOutEdge;  // Firts outcoming edge
        IHalfEdge   loop;          // Iterator
        
        IVertex     seed;       // Seed vertex in each iteration
        IVertex     adjacent;   // Adjacent vertex
        
        double  novelDistance;  // Novel distance from current seed vertex
        double  weight;         // Edge wweight
        
        // We reset all the minimum distances before to start the method

        for (IVertex vertex: m_Taxonomy.getVertexes())
        {
            vertex.setMinDistance(Double.POSITIVE_INFINITY);
        }

        // We set to 0 the distance in the source vertex

        m_minDistance = 0.0;

        // We create the priority queue

        pending = new PriorityQueue<>();

        // We insert the current vertex as source

        pending.add(this);

        // We make a BFS traversal of the taxonomy

        while (!pending.isEmpty())
        {
            // We get the current vertex to expolore

            seed = pending.poll();
            firstOutEdge = seed.getFirstOutcomingEdge();

            // Visit each edge exiting u

            loop = firstOutEdge;

            do
            {
                // We get the adjacent vertex and the weight

                adjacent = loop.getTarget();

                if (weighted)
                {
                    weight = loop.getEdge().getWeight();
                }
                else
                {
                    weight = 1.0;
                }

                // We compute the novel distance

                novelDistance = seed.getMinDistance() + weight;

                // We check if the novel didstance is lower

                if (novelDistance < adjacent.getMinDistance())
                {
                    // We update the shortest distance until
                    // the adjacent vertex

                    adjacent.setMinDistance(novelDistance);

                    // We remove the adjacent from the queue and insert
                    // it at the end

                    pending.remove(adjacent);
                    pending.add(adjacent);
                }

                // We iterate aroung the vertex

                loop = loop.getOpposite().getNext();

            } while (loop != firstOutEdge);
        }        
    }
        
    /**
     * This function computes the Dijkstra algorithm using the edge weights
     * assigned to the taxonomy, or a uniform weight = 1 when it is invoked
     * to count the edges between the current and target vertexes.
     * @param target
     * @return 
     */
    
    @Override
    public double getShortestPathDistanceTo(
            IVertex     target,
            boolean     weighted)
    {
        double  distance;    // Returned value
        
        // We check for identical target
        
        if (target == this)
        {
            distance = 0.0;
        }
        else
        {
            // We compute the distance filed from the vertex
            
            computeDistanceField(weighted);

            // We get the shortest distance until the target vertex

            distance = target.getMinDistance();
        }
        
        // We return the result
        
        return (distance);
    }
    
    /**
     * This function computes the common subgraph of the input vertexes,
     * which is defined as the union of the ancestor and descendant sets
     * of both input vertexes. The common subgraph is the set of
     * vertexes within the taxonomy which can be part of any path
     * linking both vertexes.
     * @param source
     * @param target
     * @return 
     */
    
    private static HashSet<IVertex> getCommonSubgraph(
            IVertex source,
            IVertex target)
    {
        HashSet<IVertex>    subgraph = new HashSet<>(); // Returned value
        
        // We create the pending queue
        
        LinkedList<IVertex> pending = new LinkedList<>();

        // We define the two exploring directions
        
        OrientedEdgeType[]  exploreDirs = {OrientedEdgeType.SubClassOf,
                                           OrientedEdgeType.SuperClassOf};

        // We traverse the taxonomy two times in order to obtain
        // both the ancestor and descendant sets of the vertexes.
        
        for (int i = 0; i < exploreDirs.length; i++)
        {
            // We enqueue the seed vertexes

            pending.add(source);
            subgraph.add(source);
            
            if (!subgraph.contains(target))
            {
                pending.add(target);
                subgraph.add(target);
            }
            
            // We expand the vertexes
            
            while (!pending.isEmpty())
            {
                // We get the next vertex
                
                IVertex vertex = pending.remove();
                
                // We enqueue its children or parents
                
                IHalfEdge firstArc = vertex.getFirstOutcomingEdge();
                IHalfEdge loop = firstArc;
                
                do
                {
                    // We check the type of the current oriented edge
                    
                    if (loop.getEdgeType() == exploreDirs[i])
                    {
                        IVertex adjacent = loop.getTarget();
                        
                        if (!subgraph.contains(adjacent))
                        {
                            pending.add(adjacent);
                            subgraph.add(adjacent);
                        }
                    }
                    
                    // We get the next outcoming arc
                    
                    loop = loop.getOpposite().getNext();
                    
                } while (loop != firstArc);
            }
        }        
        
        // We return the result
        
        return (subgraph);
    }
    
    /**
     * This function computes the length of the shortest path between the
     * current vertex and the target vertex using the weights of the oriented
     * edges, instead of the weight of the non-oriented edges. Thus, this
     * method allows to define an asymmetric distance between vertexes
     * in the taxonomy.
     * @param target Target vertex
     * @return The length of the shortest path
     */
    
    @Override
    public double getAsymmetricShortestPathDist(
            IVertex target)
    {
        double  distance;    // Returned value
        
        PriorityQueue<IVertex> pending; // Processing queue

        IHalfEdge   firstOutEdge;  // Firtst outcoming edge
        IHalfEdge   loop;          // Iterator
        
        IVertex     seed;       // Seed vertex in each iteration
        IVertex     adjacent;   // Adjacent vertex
        
        double  novelDistance;  // Novel distance from current seed vertex
        double  weight;         // Oriented edge (half-edge) wweight
        
        // We check for identical target
        
        if (target == this)
        {
            distance = 0.0;
        }
        else
        {
            // We reset all the minimum distances before to start the method

            for (IVertex vertex: m_Taxonomy.getVertexes())
            {
                vertex.setMinDistance(Double.POSITIVE_INFINITY);
            }

            // We set to 0 the distance in the source vertex

            m_minDistance = 0.0;

            // We create the priority queue
            
            pending = new PriorityQueue<>();
             
            // We insert the current vertex as source

            pending.add(this);

            // We make a BFS traversal of the taxonomy

            while (!pending.isEmpty())
            {
                // We get the current vertex to expolore

                seed = pending.poll();
                firstOutEdge = seed.getFirstOutcomingEdge();

                // Visit each edge exiting u

                loop = firstOutEdge;

                do
                {
                    // We get the adjacent vertex and the weight

                    adjacent = loop.getTarget();
                    weight = loop.getWeight();

                    // We compute the novel distance

                    novelDistance = seed.getMinDistance() + weight;

                    // We check if the novel didstance is lower

                    if (novelDistance < adjacent.getMinDistance())
                    {
                        // We update the shortest distance until
                        // the adjacent vertex

                        adjacent.setMinDistance(novelDistance);

                        // We remove the adjacent from the queue and insert
                        // it at the end

                        pending.remove(adjacent);
                        pending.add(adjacent);
                    }

                    // We iterate aroung the vertex

                    loop = loop.getOpposite().getNext();

                } while (loop != firstOutEdge);
            }        

            // We get the shortest distance until the target vertex

            distance = target.getMinDistance();
        }
        
        // We return the result
        
        return (distance);
    }
    
    /**
     * This function returns the IC-value assigned to this vertex.
     * @return The IC value for this node
     */
    
    @Override
    public double getICvalue()
    {
        return (m_ICvalue);
    }
    
    /**
     * This function returns and ordered collection with the 1-ring adjacent vertexes.
     * @return An ordered collection of adjacent vertexes.
     */
    
    @Override
    public IVertexList getNeighbours() throws Exception
    {
        VertexList  neighbours;    // Returned value
        
        IHalfEdge   loop = m_FirstOutArc;   // Cursor
        
        // We cretat the adjacent list
        
        neighbours = new VertexList(false);
        
        // We iterate around the vertex
        
        do
        {
            // We add the neighbour to the list
            
            neighbours.add(loop.getTarget());
            
            // We go to the next neighbour
            
            loop = loop.getOpposite().getNext();
            
        } while (loop != m_FirstOutArc);
        
        // We return the result
        
        return (neighbours);
    }
    
    /**
     * This function returns the count of children vertexes (direct child nodes).
     * @return Children vertex count.
     */
    
    @Override
    public int getChildrenCount()
    {
        int children = 0;   // Returned result
        
        IHalfEdge   loop = m_FirstOutArc;   // Cursor
        
        // We iterate around the vertex
        
        do
        {
            // We insert the children vertexes
            
            if (loop.getEdgeType() == OrientedEdgeType.SuperClassOf)
            {
                children++;
            }
            
            // We get the next outcoming arc
            
            loop = loop.getOpposite().getNext();
            
            assert(loop != null);
            
        } while (loop != m_FirstOutArc);
        
        // We return the result
        
        return (children);
    }
    
    /**
     * This function sets the IC value for this vertex.
     * @param valueIC New IC value assigned to the vertex.
     */
    
    @Override
    public void setICValue(double valueIC)
    {
        m_ICvalue = valueIC;
    }
    
    /**
     * This function returns the unique ID assigned to the current vertex
     * within a taxonomy.
     * @return The unique ID of the vertex.
     */
    
    @Override
    public int getID()
    {
        return (m_Id);
    }
    
    /**
     * This function sets the value of the first visiting flag.
     * @param visited 
     */
    
    @Override
    public void setVisited(
            boolean visited)
    {
        m_Visited = visited;                
    }
    
    /**
     * This function returns the value of the first visiting flag.
     * @return the value of the visiting flag
     */
    
    @Override
    public boolean getVisited()
    {
        return (m_Visited);
    }
    
    /**
     * This function returns a collection with the children vertexes of the
     * current vertex. The children collection is computed on-the-fly using
     * the half-edge representation, thus, it is responsibility of the
     * client code to clear the collection in order to release the
     * used memory.
     * @return Direct child vertexes
     * @throws java.lang.Exception
     */
    
    @Override
    public IVertexList getChildren() throws Exception
    {
        // We create the set of children vertexes to be returned
        
        VertexList  children = new VertexList(false);
        
        // We initialize the local vertex iterator
        
        IHalfEdge   loop = m_FirstOutArc;   // Cursor
        
        // We iterate around the vertex
        
        do
        {
            // We insert the children vertexes
            
            if (loop.getEdgeType() == OrientedEdgeType.SuperClassOf)
            {
                children.add(loop.getTarget());
            }
            
            // We get the next outcoming arc
            
            loop = loop.getOpposite().getNext();
            
            //assert(loop != null);
            
        } while (loop != m_FirstOutArc);
        
        // We return the result
        
        return (children);
    }
    
    /**
     * This function returns the oriented edge pointing to the input vertex
     * or null if there is no any linking edge.
     * @param target
     * @return Outcoming incident edge pointing to the target vertex.
     */
    
    @Override
    public IHalfEdge getIncidentEdge(
            IVertex target)
    {
        IHalfEdge   targetEdge = null;  // rerturned value
        
        IHalfEdge   loop = m_FirstOutArc;   // Cursor
        
        // We iterate around the vertex
        
        do
        {
            // We insert the children vertexes
            
            if (loop.getTarget() == target)
            {
                targetEdge = loop;
                break;
            }
            
            // We get the next outcoming arc
            
            loop = loop.getOpposite().getNext();
            
        } while (loop != m_FirstOutArc);
        
        // We reutrn the result
        
        return (targetEdge);
    }
    
    /**
     * This function returns the collection of parent vertexes. Like the
     * getChildren() function, the parent collection is computed on-the-fly.
     * @return The parent vertexes in the taxonomy
     * @throws Exception Unexpected error
     */
    
    @Override
    public IVertexList getParents() throws Exception
    {
        VertexList   parents;   // Returned result
        
        IHalfEdge   loop = m_FirstOutArc;   // Cursor
        
        // We cretae the set of children vertexes
        
        parents = new VertexList(false);
        
        // We iterate around the vertex
        
        do
        {
            // We insert the children vertexes
            
            if (loop.getEdgeType() == OrientedEdgeType.SubClassOf)
            {
                parents.add(loop.getTarget());
            }
            
            // We get the next outcoming arc
            
            loop = loop.getOpposite().getNext();
            
            //assert(loop != null);
            
        } while (loop != m_FirstOutArc);
        
        // We return the result
        
        return (parents);
    }
    
    /**
     * This function returns a string vector with the string tags stored
     * by the parent vertexes.
     * @return The string tags stored by the parent vertexes
     */
    
    @Override
    public String[] getParentStringTags()
    {
        String[]   strTagFields;   // Returned result
        
        IHalfEdge   loop ;   // Cursor
        
        int nParents = 0;   // Parents count
        int i = 0;          // Counter
        
        // We iterate around the vertex to count the parents
        
        loop = m_FirstOutArc;
        
        do
        {
            // We insert the children vertexes
            
            if (loop.getEdgeType() == OrientedEdgeType.SubClassOf)
            {
                nParents++;
            }
            
            // We get the next outcoming arc
            
            loop = loop.getOpposite().getNext();
            
        } while (loop != m_FirstOutArc);

        // We iterate around the vertex to count the parents
        
        strTagFields = new String[nParents];
        loop = m_FirstOutArc;
        
        do
        {
            // We insert the children vertexes
            
            if (loop.getEdgeType() == OrientedEdgeType.SubClassOf)
            {
                strTagFields[i++] = loop.getTarget().getStringTag();
            }
            
            // We get the next outcoming arc
            
            loop = loop.getOpposite().getNext();
            
        } while (loop != m_FirstOutArc);
        
        // We return the result
        
        return (strTagFields);
    }
    
    /**
     * This function returns the number of parents of the vertex.
     * @return Parents count
     */
    
    @Override
    public int getParentsCount()
    {
        int nParents = 0;   // Returned value
        
        IHalfEdge   loop = m_FirstOutArc;   // Cursor
        
        // We iterate around the vertex to count the parents
        
        do
        {
            // We insert the children vertexes
            
            if (loop.getEdgeType() == OrientedEdgeType.SubClassOf)
            {
                nParents++;
            }
            
            // We get the next outcoming arc
            
            loop = loop.getOpposite().getNext();
            
        } while (loop != m_FirstOutArc);

        // We return the result
        
        return (nParents);
    }

    /**
     * This function returns the number of subsumed leaves
     * including the current vertex
     * @return Overall subsumed leaves
     * @throws java.lang.Exception 
     */
    
    @Override
    public int getInclusiveSubsumedLeafSetCount() throws Exception
    {
        // We get the subsumed leaves count without to include the current vertex
        
        int subsumedLeaves = getNonInclusiveSubsumedLeafSetCount();
        
        // We add 1 if the vertex is a leaf node
        
        if (this.isLeaf())
        {
            subsumedLeaves++;
        }
        
        // We return the result
        
        return (subsumedLeaves);
    }
    
    /**
     * This function returns the cached value of subsumed leaf vertexes
     * without to include the current vertex. If the current value
     * is invalid then the function forces its coomputation.
     * @return 
     */
    
    @Override
    public int getNonInclusiveSubsumedLeafSetCount() throws Exception
    {
        // We check that the value has been already computed
        
        if (m_CachedLeavesCount < 0)
        {
            // We initialize the cached value
            
            m_CachedLeavesCount = 0;

            // We obtain the global visited1 set to maintain the
            // set of visited vertexes
        
            HashSet<IVertex> visited = new HashSet<>();

            // We create the seed pending queue

            LinkedList<IVertex> pending = new LinkedList<>();

            pending.add(this);
            visited.add(this);

            // We process the pending nodes

            while (!pending.isEmpty())
            {
                // We get the next descendant

                IVertex descendant = pending.remove();

                // We increase  the counter and label it as visited

                if ((descendant != this) && descendant.isLeaf())
                {
                    m_CachedLeavesCount++;
                }

                // We enqueue the children of the descendant
                // in order to visit the descendant vertexes

                IHalfEdge   descendantFirstOurArc = descendant.getFirstOutcomingEdge();
                IHalfEdge   loop = descendantFirstOurArc;

                do
                {
                    // We obtain the next child vertex

                    if (loop.getEdgeType() == OrientedEdgeType.SuperClassOf)
                    {
                        IVertex child = loop.getTarget();

                        if (!visited.contains(child))
                        {
                            pending.add(child);
                            visited.add(child);
                        }
                    }

                    // We get the next outcoming arc

                    loop = loop.getOpposite().getNext();

                } while (loop != descendantFirstOurArc);                
            }
            
            // We reset the visited set
                    
            visited.clear();
        }
        
        // We return the result
        
        return (m_CachedLeavesCount);
    }
    
    /**
     * This function returns the pre-computed hyponyms count
     * without to include the own base vertex.
     * @return 
     * @throws java.lang.Exception 
     */
    
    @Override
    public int getNonInclusiveHyponymSetCount() throws Exception
    {
        // We check whether the value has been already computed
        
        if (m_CachedHyponymsCount  < 0)
        {
            // We initialize the hyponyms counter
            
            m_CachedHyponymsCount = 0;
            
            // We obtain the global visited1 set to maintain the
            // set of visited vertexes

            HashSet<IVertex> visited = new HashSet<>();

            // We create the queue of pending vertexes and add the vertex as seed.

            LinkedList<IVertex> pending = new LinkedList<>();

            pending.add(this);
            visited.add(this);

            // We process the pending nodes

            while (!pending.isEmpty())
            {
                // We get the next descendant and count it

                IVertex descendant = pending.remove();

                if (descendant != this)
                {
                    m_CachedHyponymsCount++;
                }

                // We enqueue the children of the descendant.

                IHalfEdge   descendantFirstOurArc = descendant.getFirstOutcomingEdge();
                IHalfEdge   loop = descendantFirstOurArc;

                do
                {
                    // We obtain the next child vertex

                    if (loop.getEdgeType() == OrientedEdgeType.SuperClassOf)
                    {
                        IVertex child = loop.getTarget();

                        if (!visited.contains(child))
                        {
                            pending.add(child);
                            visited.add(child);
                        }
                    }

                    // We get the next outcoming arc

                    loop = loop.getOpposite().getNext();

                } while (loop != descendantFirstOurArc);
            }

            // We reset the visited set

            visited.clear();
        }
        
        // We return the result
        
        return (m_CachedHyponymsCount);
    }
    
    /**
     * This function return the pre-computed ancestors count.
     * @return Ancestors count
     * @throws java.lang.Exception 
     */
    
    @Override
    public int getNonInclusiveAncestorSetCount() throws InterruptedException, Exception
    {
        // We check that the value has been already computed
        
        if (m_CachedAncestorsCount  < 0)
        {
            m_CachedAncestorsCount = getAncestorsCount();
        }
        
        // We return the result
        
        return (m_CachedAncestorsCount);
    }
    
    /**
     * This function computes on-the-fly the ancestors count
     * for this vertex without to include the own base vertex.
     * @return 
     */
    
    private int getAncestorsCount() throws InterruptedException, Exception
    {
        int ancestorsCount;  // Returned value
        
        IVertexList ancestors;  // Ancestors

        // We get all the ancestors, inclusive the vertex
        
        ancestors = getAncestors(false);
        
        // We get the number of ancestors
        
        ancestorsCount = ancestors.getCount();
        
        // We destroy the list
        
        ancestors.clear();
        
        // We return the results
        
        return (ancestorsCount);
    }   
    
    /**
     * This function computes on-the-fly the hyponym set of the vertex
     * @return Hyponym count
     * @throws java.lang.Exception
     */
    
    @Override
    public IVertexList getHyponyms(
        boolean vertexInclusive) throws Exception
    {
        VertexList  hyponyms = new VertexList(false);   // Returned value
        
        // We obtain the global visited1 set to maintain the
        // set of visited vertexes
        
        HashSet<IVertex> visited = new HashSet<>();
        
        // We create the seed pending queue
        
        LinkedList<IVertex> pending = new LinkedList<>();
        
        // We enqueue the current vertex
        
        pending.add(this);
        visited.add(this);
        
        // We process the pending nodes
        
        while (!pending.isEmpty())
        {
            // We get the next descendant
            
            IVertex descendant = pending.remove();
            
            // We add the current descendant to the list
            
            hyponyms.add(descendant);
            
            // EXPANSION OF THE CHILDREN VERTEXES
            // We enqueue the children of the descendant. in order
            // to avoid the retrieval and removal of the children
            // list, with its corresponding overhead,
            // we explictly implement here the main
            // iteration loop of our PosetHERep.

            IHalfEdge   descendantFirstOurArc = descendant.getFirstOutcomingEdge();
            IHalfEdge   loop = descendantFirstOurArc;

            do
            {
                // We obtain the next child vertex
                
                if (loop.getEdgeType() == OrientedEdgeType.SuperClassOf)
                {
                    IVertex child = loop.getTarget();
                    
                    if (!visited.contains(child))
                    {
                        pending.add(child);
                        visited.add(child);
                    }
                }

                // We get the next outcoming arc

                loop = loop.getOpposite().getNext();

            } while (loop != descendantFirstOurArc);
        }
            
        // We remove the current vertex when the function
        // call does not include it
        
        if (!vertexInclusive)
        {
            hyponyms.remove(this);
        }
        
        // We reset the visited set
        
        visited.clear();
        
        // We return the result
        
        return (hyponyms);
    }
    
    /**
     * This function returns the set of subsumed leaves
     * except the vertex itself.
     * * @param inclusive The vertex will be included if it is a leaf node.
     * @return The number of subsumed leaves distinct from the vertex
     * @throws java.lang.Exception
     */
    
    @Override
    public IVertexList getSubsumedLeaves(
        boolean    inclusive) throws Exception
    {
        // We create the list of leaf vertexes to be returned
        
        VertexList  leaves = new VertexList(false);
               
        // We obtain the global visited1 set to maintain the
        // set of visited vertexes
        
        HashSet<IVertex> visited = new HashSet<>();
        
        // We create the seed pending queue
        
        LinkedList<IVertex> pending = new LinkedList<>();
        
        pending.add(this);
        visited.add(this);
        
        // We process the pending nodes
        
        while (!pending.isEmpty())
        {
            // We get the next descendant
            
            IVertex descendant = pending.remove();
            
            // We add the current descendant to the list
            
            if (descendant.isLeaf() && (inclusive || (descendant != this)))
            {
                leaves.add(descendant);
            }
            
            // We enqueue the children of the descendant.

            IHalfEdge   descendantFirstOurArc = descendant.getFirstOutcomingEdge();
            IHalfEdge   loop = descendantFirstOurArc;

            do
            {
                // We obtain the next child vertex
                
                if (loop.getEdgeType() == OrientedEdgeType.SuperClassOf)
                {
                    IVertex child = loop.getTarget();
                    
                    if (!visited.contains(child))
                    {
                        pending.add(child);
                        visited.add(child);
                    }
                }

                // We get the next outcoming arc

                loop = loop.getOpposite().getNext();

            } while (loop != descendantFirstOurArc);
        }
        
        // We reset the visited set
        
        visited.clear();
        
        // We return the result
        
        return (leaves);
    }
    
    /**
     * This function returns the tag field associated to the vertex.
     * @return Tag field used by any client code
     */
    
    @Override
    public Object getTag()
    {
        return (m_Tag);
    }
    
    /**
     * This function sets the tag field associated to the current vertex.
     * @param tag 
     */
    
    @Override
    public void setTag(Object tag)
    {
        m_Tag = tag;
    }
    
    /**
     * This function returns the ID of the vertex.
     * @return A string defining the object
     */
    
    @Override
    public String toString()
    {
        return (m_Id.toString());
    }
    
    /**
     * This function sets the first outcoming oriented edge for the
     * current vertex.
     * @param outArc 
     */
    
    void setFirstOutEdge(
            IHalfEdge   outArc)
    {
        // Check the connectivity of the arc
        
        assert (outArc.getOpposite().getTarget() == this);
        
        // We save the arc
        
        m_FirstOutArc = outArc;
    }
    
    /**
     * This function adds an incoming arc to the vertex
     * @param incomingArc 
     */
    
    void addIncomingArc(
            HalfEdge   incomingArc)
    {
        IHalfEdge   lastOutArc;     // last outcoming arc
        HalfEdge    oppLastOutArc;
        
        // We chehck that the vertex has been already connected
        
        if (m_FirstOutArc != null)
        {
            // We connect the incoming arc to the first outcoming
            
            incomingArc.setNext(m_FirstOutArc);
            
            // We get the last arc
            
            lastOutArc = getLastOutcomingArc();
            
            // We connect the las arc to the opposite arc
            
            oppLastOutArc = (HalfEdge) lastOutArc.getOpposite();
            oppLastOutArc.setNext(incomingArc.getOpposite());
        }
        else
        {
            m_FirstOutArc = incomingArc.getOpposite();
            assert (m_FirstOutArc != null);
            incomingArc.setNext(m_FirstOutArc);
        }
    }
    
    /**
     * This function returns the last outcoming oriented edge from the vertex.
     * @return Last outcoming arc
     */
    
    private IHalfEdge getLastOutcomingArc()
    {
        IHalfEdge   outArc = null;  // Returned value
        
        IHalfEdge   loop = m_FirstOutArc;   // Cursor
        IHalfEdge   next;
        
        // We iterate to get the last arc
        
        if (loop != null)
        {
            do
            {
                // We get the next arc
                
                next = loop.getOpposite().getNext();
                
                // We check the next is not the first one
                
                if (next == m_FirstOutArc)
                {
                    outArc = loop;
                }
                else
                {
                    loop = next;
                }

            } while (outArc == null);
        }
        
        // We return the result
        
        return (outArc);
    }

    /**
     * This function returns the contained taxonomy of the current vertex.
     * @return The owner taxonomy
     */
    
    @Override
    public ITaxonomy getTaxonomy()
    {
        return (m_Taxonomy);
    }
    
    /**
     * This function compares the minimum distance field of the current vertex
     * with the value associated to other vertex. This function is required
     * by the MinPriorityQueue used implement the Djikstra algorithm.
     * @param other
     * @return 
     */

    @Override
    public int compareTo(IVertex other)
    {
        return (Double.compare(m_minDistance, other.getMinDistance()));
    }
    
    /**
     * This function computes the depth measured from the root to the vertex.
     * The algorithm starts by computing the ancestor set of the vertex
     * in order to reduce the search-space for the constrained
     * top-down Djikstra algorithm that we use to measure the length.
     */
    
    private int computeMinDepth() throws Exception
    {
        int minDepth = 0;   // Returned value
        
        // We filter that root case
        
        if (!this.isRoot())
        {
            // We obtain the ancestor set of the vertex, which is made
            // in linear time in a very efficient way.

            IVertexList ancestors = this.getAncestors(true);
            
            // We obtain the root defined as the latest vertex in the ancestor set

            IVertex root = ancestors.getRoots().getAt(0);
            
            assert(root != null);

            // We reset all the minimum distances before to start the method

            for (IVertex vertex: ancestors)
            {
                vertex.setMinDistance(Double.POSITIVE_INFINITY);
            }

            // We set to 0 the distance in the source vertex

            root.setMinDistance(0.0);

            // We use a min-priority queue for our top-down Djikstra algorithm
            
            PriorityQueue<IVertex> pending = new PriorityQueue<>();
            
            // We insert the root as source

            pending.add(root);

            // We make a BFS traversal of the taxonomy

            while (!pending.isEmpty())
            {
                // We get the current vertex to explore

                IVertex seed = pending.poll();
                IHalfEdge firstOutEdge = seed.getFirstOutcomingEdge();

                // Visit each edge exiting from the current vertex

                IHalfEdge loop = firstOutEdge;

                do
                {
                    // We get the adjacent vertex and the weight

                    IVertex adjacent = loop.getTarget();

                    // We check that the edge is pointing down and the target
                    // vertex is included in the ancestor set (search space)

                    if ((loop.getEdgeType() == OrientedEdgeType.SuperClassOf)
                            && ancestors.contains(adjacent.getID()))
                    {
                        // We compute the novel distance

                        double novelDistance = seed.getMinDistance() + 1.0;

                        // We check if the novel distance is lower
                        // than the current one.

                        if (novelDistance < adjacent.getMinDistance())
                        {
                            // We update the shortest distance until
                            // the adjacent vertex

                            adjacent.setMinDistance(novelDistance);

                            // We remove the adjacent from the queue and insert
                            // it at the end

                            pending.remove(adjacent);
                            pending.add(adjacent);
                        }
                    }

                    // We iterate aroung the vertex

                    loop = loop.getOpposite().getNext();

                } while (loop != firstOutEdge);
            }        

            // We get the depth for the current vertexes

            minDepth = (int)m_minDistance;

            // We release the ancestor set

            ancestors.clear();
        }
        
        // We return the result
        
        return (minDepth);
    }    
    
    /**
     * This function computes the longest depth measured from the root.
     * The algorithm starts by computing the ancestor set of the vertex
     * in order to reduce the search-space for the constrained
     * top-down Djikstra algorithm that we use to measure the length.
     */
    
    private int computeMaxDepth() throws Exception
    {
        int maxDepth = 0;   // Returned value
        
        // We check if the vertex is a root
        
        if (!this.isRoot())
        {
            // We compute the ancestor set on-the-fly
            
            IVertexList ancestors = this.getAncestors(true);
            
            // We reset all the minimum distances before to start the method

            for (IVertex vertex: ancestors)
            {
                vertex.setMinDistance(0.0);
            }

            // We obtain the root defined as the latest vertex in the ancestor set

            IVertex root = ancestors.getRoots().getAt(0);
            
            assert(root != null);
            
            // We create a min-prioty queue for our top-down Djikstra algorithm
            
            PriorityQueue<IVertex> pending = new PriorityQueue<>();
            
            // We insert the current vertex as seed vertex

            pending.add(root);

            // We make a BFS traversal of the taxonomy

            while (!pending.isEmpty())
            {
                // We get the current vertex to expolore

                IVertex     seed = pending.poll();
                IHalfEdge   firstOutEdge = seed.getFirstOutcomingEdge();

                // Visit each edge exiting u

                IHalfEdge   loop = firstOutEdge;

                do
                {
                    // We get the adjacent vertex and the weight

                    IVertex adjacent = loop.getTarget();

                    // We chekc that the edge is pointing down
                    
                    if ((loop.getEdgeType() == OrientedEdgeType.SuperClassOf)
                            && ancestors.contains(adjacent.getID()))
                    {
                        // We compute the novel distance

                        double novelDistance = seed.getMinDistance() + 1.0;

                        // We check if the novel distance is higher than
                        // the current one in order to update it. We note
                        // that we are using the minDistance attribute
                        // as register, despite we are computing the maximum
                        // distance. This vertex attribute is used in all
                        // our shortest path algorithms.

                        if (novelDistance > adjacent.getMinDistance())
                        {
                            // We update the shortest distance until
                            // the adjacent vertex

                            adjacent.setMinDistance(novelDistance);

                            // We remove the adjacent from the queue and insert
                            // it at the end

                            pending.remove(adjacent);
                            pending.add(adjacent);
                        }
                    }

                    // We iterate aroung the vertex

                    loop = loop.getOpposite().getNext();

                } while (loop != firstOutEdge);
            }     
            
            // We get the depth for all the vertexes

            maxDepth = (int)m_minDistance;
            
            // We release the ancestor set
            
            ancestors.clear();            
        }
        
        // We return the result
        
        return (maxDepth);
    }
}
