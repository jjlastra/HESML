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

// HESML references

import hesml.taxonomy.*;

/**
 * This class implements a non-oriented edge as defined by the IHalfEdge
 * interface. The IHalfEdge are the key object of our representation
 * model for taxonomies called PosetHERep. The instances of IHalfEdge objects
 * encode all adjacency relationships between vertexes within the taxonomy,
 * as detailed in the paper below.
 * 
 * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * HESML: a scalable ontology-based semantic similarity measures library
 * with a set of reproducible experiments and a replication dataset.
 * To Appear in Information Systems Journal.
 * 
 * @author Juan Lastra-Díaz
 */

class HalfEdge implements IHalfEdge
{
    /**
     * Parent edge
     */
    
    private IEdge   m_Edge;
    
    /**
     * Opposite edge
     */
    
    private IHalfEdge   m_OppositeEdge;
    
    /**
     * Target vertex pointed by the edge
     */
    
    private IVertex m_Target;
    
    /**
     * The next outcoming edge from the target vertex
     */
    
    private IHalfEdge   m_NextEdge;
    
    /**
     * Edge type
     */
    
    private final OrientedEdgeType    m_EdgeType;
    
    /**
     * Weight used to define asymmetric path.
     */
    
    private double  m_Weight;
    
    /**
     * Main constructor
     * @param edge The owner edge
     */
    
    HalfEdge(
            IEdge               edge,
            IVertex             target,
            OrientedEdgeType    type)
    {
        m_Edge = edge;
        m_OppositeEdge = null;
        m_Target = target;
        m_NextEdge = null;
        m_EdgeType = type;
        m_Weight = 0.0;
    }
    
    /**
     * This function returns the weight value of this half-edge
     * @return Weight value used in asymmetric shortest path computation.
     */
    
    @Override
    public double getWeight()
    {
        return (m_Weight);
    }
    
    /**
     * This function sets the weight value associated to the
     * current instance of the oriented edge.
     * @param weight New weight value
     */
    
    @Override
    public void setWeight(double weight)
    {
        m_Weight = weight;
    }
    
    /**
     * This function returns the type of edge
     * @return The type of edge
     */
    
    @Override
    public OrientedEdgeType getEdgeType()
    {
        return (m_EdgeType);
    }
    
    /**
     * This function sets the next outcoming edge from the target vertex.
     * This function is private being only invoked by the HESML code,
     * more specifically by the taxonomy in order to preserve the
     * consistency of the half-edge representation.
     * @param next Next outcoming edge from the target vertex.
     */
    
    void setNext(
            IHalfEdge   next)
    {
        m_NextEdge = next;
    }
    
    /**
     * This function returns the next outcoming edge from the target vertex.
     * @return Next outcoming edge from the target vertex.
     */
    
    @Override
    public IHalfEdge getNext()
    {
        return (m_NextEdge);
    }
    
    /**
     * This function returns the target vertex pointed by the current half-edge.
     * @return Target vertex
     */
    
    @Override
    public IVertex getTarget()
    {
        return (m_Target);
    }
    
    /**
     * This function returns the parent non-oriented edge.
     * @return Parent non-oriented edge.
     */
    
    @Override
    public IEdge getEdge()
    {
        return (m_Edge);
    }
    
    /**
     * This function sets the opposite edge, and it is only invoked
     * by the owner non-oriented edge in order to connect both
     * paired half-edges.
     * @param opposite 
     */
    
    void setOpposite(
            IHalfEdge   opposite)
    {
        m_OppositeEdge = opposite;
    }
    
    /**
     * This function returns the paired and opposite oriented edge.
     * @return The opposite oriented edge
     */
    
    @Override
    public IHalfEdge getOpposite()
    {
        return (m_OppositeEdge);
    }

    /**
     * This function disconnects the object during a clear call to
     * the owner taxonomy.
     */
    
    void destroy()
    {
        m_Target = null;
        m_Edge = null;
        m_OppositeEdge = null;
        m_NextEdge = null;
    }
}
