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
 * This class implements the IEdge object. The Edge class represents
 * a non-oriented edge in our representation model for taxonomies, called
 * PosetHERep. Each Edge instance contains two half-edges which are
 * oriented edges.
 * 
 * @author Juan Lastra-Díaz
 */

class Edge implements IEdge
{
    /**
     * Oriented edge in the direct direction of the parent non-oriented edge
     */
    
    private HalfEdge   m_Direct;
    
    /**
     * Paired oriented edge in the inverse direction
     */
    
    private HalfEdge   m_Inverse;
    
    /**
     * Weight for the whole edge (symmetric weight)
     */
    
    private double  m_Weight;
    
    /**
     * Conditional probability associated to the whole edge
     */
    
    private double  m_CondProbability;
    
    /**
     * Constructor
     * @param source for the Direct arc
     * @param target for the Inverse arc
     */
    
    Edge(IVertex source, IVertex target)
    {
        // We init the object
        
        m_Weight = 0.0;
        m_CondProbability = 0.0;
        
        // We create both half edges
        
        m_Direct = new HalfEdge(this, target, OrientedEdgeType.SubClassOf);
        m_Inverse = new HalfEdge(this, source, OrientedEdgeType.SuperClassOf);
        
        // We link both roeiented edges
        
        m_Direct.setOpposite(m_Inverse);
        m_Inverse.setOpposite(m_Direct);
    }

    /**
     * This function is invoked to disconnect the edge object during a
     * taxonomy clear call.
     */
    
    void destroy()
    {
        // We destroy the oriented egdes
        
        m_Direct.destroy();
        m_Inverse.destroy();
        
        // We disconnect the oriented children edges.
        
        m_Direct = null;
        m_Inverse = null;
    }
    
    /**
     * This function returns the direct oriented edge.
     * @return Direct edge
     */
    
    @Override
    public IHalfEdge getDirect()
    {
        return (m_Direct);
    }
    
    /**
     * This function returns the inverse oriented edge.
     * @return The inverse oriented edge
     */
    
    @Override
    public IHalfEdge getInverse()
    {
        return (m_Inverse);
    }

    /**
     * This function returns the weight associated to the edge.
     * @return Weight for his edge
     */
    
    @Override
    public double getWeight()
    {
        return (m_Weight);
    }

    /**
     * This function returns the conditional probability attribute.
     * @return Conditional probability value
     */
    
    @Override
    public double getCondProbability()
    {
        return (m_CondProbability);
    }
    
    /**
     * This function sets the value for the conditional probability attribute.
     * @param value 
     */
    
    @Override
    public void setCondProbability(
        double  value)
    {
        m_CondProbability = value;
    }
    
    /**
     * This function sets the weight value for this edge.
     * @param weight New weight value
     */
    
    @Override
    public void setWeight(
        double  weight)
    {
        m_Weight = weight;
    }
}
