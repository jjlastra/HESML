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
import java.util.Iterator;

// HESML references

import hesml.taxonomy.*;

/**
 * This class implements the IEdge list interface and it represents
 * a collection of Edge instances. Each ITaxonomy instance uses an instance
 * of this class in order to store the full set of non-oriented edges.
 * 
 * @author Juan Lastra-Díaz
 */

class EdgeList implements IEdgeList
{
    /**
     * Ordered edge set.
     */
    
    private final ArrayList<IEdge>    m_Edges;

    /**
     * Constructor
     */
    
    EdgeList()
    {
        m_Edges = new ArrayList<>();
    }
    
    /**
     * Constructor with pre-reserved memory for a number of edges
     * defined by the input parameter.
     * @param initialCapacity Expected quantity of vertexes
     */
    
    EdgeList(int initialCapacity)
    {
        m_Edges = new ArrayList<>(initialCapacity);
    }
    
    /**
     * Remove all the edges
     */
    
    void clear()
    {
        m_Edges.clear();
    }
    
    /**
     * This function inserts an edge in the list
     * @param edge A novel edge
     */
    
    void add(IEdge  edge)
    {
        m_Edges.add(edge);
    }
    
    /**
     * This function returns the edge count in the list.
     * @return The number of edges in the list
     */
    
    @Override
    public int getCount()
    {
        return (m_Edges.size());
    }

    /**
     * This function implements the default iterator of the list.
     * @return The iterator over the collection.
     */
    
    @Override
    public Iterator<IEdge> iterator()
    {
        return (m_Edges.iterator());
    }
}
