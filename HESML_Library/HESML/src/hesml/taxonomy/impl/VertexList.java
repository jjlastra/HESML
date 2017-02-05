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
import java.util.HashMap;
import java.util.Iterator;

// HESML references

import hesml.taxonomy.*;

/**
 * This class implements an ordered and indexed collection of IVertex objects.
 * @author Juan Lastra-Díaz
 */

class VertexList implements IVertexList
{
    /**
     * Vertexes indexed by the unique key URI
     */
    
    private HashMap<Integer, IVertex>    m_IndexedVertexes;
    
    /**
     * Ordered vertexes
     */
    
    private ArrayList<IVertex>  m_Vertexes;

    /**
     * Lock flag to avoid the cleaning of the list.
     */
    
    private final boolean m_Locked;
    
    /**
     * Constructor
     * @param locked It indicates that the list cannot be cleared.
     */
    
    VertexList(
        boolean locked)
    {
        m_IndexedVertexes = new HashMap<>();
        m_Vertexes = new ArrayList<>();
        m_Locked = locked;
    }
    
    /**
     * Constructor with pre-reserved memory for an expected number
     * of vertexes defined by the initialCapacity parameter.
     * @param locked
     * @param initialCapacity Expected quantity of vertexes
     */

    public VertexList(
            boolean     locked,
            int         initialCapacity)
    {
        m_IndexedVertexes = new HashMap<>(initialCapacity + 1, 1.0f);
        m_Vertexes = new ArrayList<>(initialCapacity);
        m_Locked = locked;
    }
    
    /**
     * This functions returns the lock state of the list.
     * @return 
     */
    
    @Override
    public boolean isLocked()
    {
        return (m_Locked);
    }
    
    /**
     * This function removes a vertex from the list it is included.
     * @param vertex The vertex to be removed
     * @return 
     */
    
    public boolean remove(
        IVertex vertex)
    {
        boolean removed;    // Returned value
        
        // We get the vertex Id
        
        int idVertex = vertex.getID();
        
        // We check for the existence of the vertex in the list
        
        removed = m_IndexedVertexes.containsKey(idVertex);
        
        // We remove the vertex
        
        if (removed)
        {
            m_IndexedVertexes.remove(idVertex);
            m_Vertexes.remove(vertex);
        }
        
        // We return the result
        
        return (removed);
    }
    
    /**
     * This function computes the difference set = {this \ other}
     * @param others
     * @return 
     * @throws java.lang.Exception 
     */
    
    @Override
    public IVertexList getDifferenceSet(
            IVertexList others) throws Exception
    {
        VertexList  dif; // Returned value
        
        // We create the list of leaves
        
        dif = new VertexList(false);
        
        // We comptue the number of leaves
        
        for (IVertex vertex: m_Vertexes)
        {
            if (!others.contains(vertex.getID()))
            {
                dif.add(vertex);
            }
        }
        
        // We return the value
        
        return (dif);
    }
    
    /**
     * This function computes the difference set count = | this \ other |
     * @param others
     * @return The cardinality of the difference set
     * @throws java.lang.Exception
     */
    
    @Override
    public long getDifferenceSetCount(
            IVertexList others) throws Exception
    {
        long difCount = 0; // Returned value
        
        // We comptue the number of leaves
        
        for (IVertex vertex: m_Vertexes)
        {
            if (!others.contains(vertex.getID()))
            {
                difCount++;
            }
        }
        
        // We return the value
        
        return (difCount);
    }
    
    /**
     * This function computes the intersection set = {this and other}
     * @param others
     * @return 
     * @throws java.lang.Exception 
     */
    
    @Override
    public IVertexList getIntersectionSet(
            IVertexList others) throws Exception
    {
        VertexList  dif; // Returned value
        
        // We create the list of leaves
        
        dif = new VertexList(false);
        
        // We comptue the number of leaves
        
        for (IVertex vertex: m_Vertexes)
        {
            if (others.contains(vertex.getID()))
            {
                dif.add(vertex);
            }
        }
        
        // We return the value
        
        return (dif);
    }
    
    /**
     * This function computes the intersection set count = |{this and other}|
     * @param others
     * @return Cardinality of the intersection set
     * @throws java.lang.Exception
     */
    
    @Override
    public long getIntersectionSetCount(
            IVertexList others) throws Exception
    {
        long  intersection = 0; // Returned value
        
        // We comptue the number of leaves
        
        for (IVertex vertex: m_Vertexes)
        {
            if (others.contains(vertex.getID()))
            {
                intersection++;
            }
        }
        
        // We return the value
        
        return (intersection);
    }
    
    /**
     * This function returns the highest IC value for any vertex
     * in the collection.
     * @return Highest IC value
     */
    
    @Override
    public double getGreatestICValue()
    {
        double  highestIC = 0.0;    // Returned value
        
        double  icValue;    // IC vertex value
        
        // We search the highest IC value
        
        for (IVertex vertex: m_Vertexes)
        {
            // We get the IC vlaue of the current vertex
            
            icValue = vertex.getICvalue();
            
            // We chekc for the maximum
            
            highestIC = Math.max(icValue, highestIC);
        }
        
        // We return the result
        
        return (highestIC);
    }
    
    /**
     * This function returns a vector with the IC values of the vertexes
     * contained in the list.
     * @return 
     */
    
    @Override
    public double[] getICValues()
    {
        double[]   icValues; // Returbed value
        
        int i = 0;  // Counter
        
        // We create the result
        
        icValues = new double[m_Vertexes.size()];
        
        // We gete the depth values
        
        for (IVertex vertex: m_Vertexes)
        {
            icValues[i++] = vertex.getICvalue();
        }
        
        // We return the result
        
        return (icValues);
    }
    
    /**
     * This function returns a vector with the depth values of the vertexes
     * contained in the list.
     * @return The depth of the vertexes in the list
     */
    
    @Override
    public int[] getDepthMinValues() throws Exception
    {
        int[]   depths; // Returbed value
        
        int i = 0;  // Counter
        
        // We create the result
        
        depths = new int[m_Vertexes.size()];
        
        // We gete the depth values
        
        for (IVertex vertex: m_Vertexes)
        {
            depths[i++] = vertex.getDepthMin();
        }
        
        // We return the result
        
        return (depths);
    }
    
    /**
     * This function returns the maximum depth of any vertex in the list.
     * @return The max Depth value
     */
    
    @Override
    public int getGreatestDepthMin() throws Exception
    {
        int depthMax = 0;   // Returned value
        
        // We search for the maximum value
        
        for (IVertex vertex: m_Vertexes)
        {
            if (vertex.getDepthMin() > depthMax)
            {
                depthMax = vertex.getDepthMin();
            }
        }
        
        // We return the value
        
        return (depthMax);
    }
    
    /**
     * This function returns the highest longest depth of any vertex in the list.
     * @return The max Depth value
     */
    
    @Override
    public int getGreatestDepthMax() throws Exception
    {
        int depthMax = 1;   // Returned value
        
        // We search for the maximum value
        
        for (IVertex vertex: m_Vertexes)
        {
            if (vertex.getDepthMax() > depthMax)
            {
                depthMax = vertex.getDepthMax();
            }
        }
        
        // We return the value
        
        return (depthMax);
    }
    
    /**
     * This function returns the maximum depth + 1.
     * @return The max Depth value
     */
    
    @Override
    public int getGreatestDepthMinBase1() throws Exception
    {
        return (1 + getGreatestDepthMin());
    }
    
    /**
     * We clear the list when it is not locked.
     * @throws java.lang.Exception
     */
    
    @Override
    public void clear() throws Exception
    {
        // We check that the list is not locked
        
        if (!m_Locked)
        {
            // We clear the lists

            m_IndexedVertexes.clear();
            m_Vertexes.clear();

            // We disconnect the objetcs

            m_IndexedVertexes = null;
            m_Vertexes = null;
        }
        else
        {
            throw (new Exception("You cannot clear the list because of it is locked"));
        }
    }
    
    /**
     * This function gets all the root ndoes in the list
     * @return A set of roots nodes included in the list
     * @throws java.lang.Exception
     */
    
    @Override
    public IVertexList getRoots() throws Exception
    {
        VertexList  roots;  // Returned result
        
        // We cretae the list of roots
        
        roots = new VertexList(false);
        
        // We search for the roots in the list
        
        for (IVertex vertex: m_Vertexes)
        {
            if (vertex.isRoot())
            {
                roots.add(vertex);
            }
        }
        
        // We return the result
        
        return (roots);
    }
    
    /**
     * This function retrieves the leaf set defined by those
     * vertexes without any descendant.
     * @return The leaves in the list
     * @throws java.lang.Exception
     */
    
    @Override
    public IVertexList getLeaves() throws Exception
    {
        // We create the list of leaves to be retuned
        
        VertexList  leaves = new VertexList(false);
        
        // We retrieve the leaf veretxes
        
        for (IVertex vertex: m_Vertexes)
        {
            if (vertex.isLeaf())
            {
                leaves.add(vertex);
            }
        }
        
        // We return the value
        
        return (leaves);
    }

    /**
     * This function computes the number of Leaf nodes in the list.
     * @return The number of Leaf vertexes in the list.
     */
    
    @Override
    public int getLeavesCount()
    {
        int leaves = 0; // Returned value
        
        // We comptue the number of leaves
        
        for (IVertex vertex: m_Vertexes)
        {
            if (vertex.isLeaf())
            {
                leaves++;
            }
        }
        
        // We return the value
        
        return (leaves);
    }
    
    /**
     * This function sets the value of the visited flag.
     * @param visited 
     */
    
    @Override
    public void setVisited(
            boolean visited)
    {
        // We set the valu of the flag

        for (IVertex vertex: m_Vertexes)
        {
            vertex.setVisited(visited);
        }
    }
    
    /**
     * This function returns the position of the input vertex in the list,
     * or -1.
     * @param vertex
     * @return 
     */
    
    @Override
    public int indexOf(IVertex vertex)
    {
        int index = -1; // Returned value
        
        // We check that the vertex is in the list
        
        if (m_IndexedVertexes.containsKey(vertex.getID()))
        {
            index = m_Vertexes.indexOf(vertex);
        }
        
        // We r eturn the value
        
        return (index);
    }
    
    /**
     * This function inserts a new vertex in the list
     * @param vertex 
     */
    
    void add(IVertex vertex) throws Exception
    {
        Exception   error;      // Error thrown
        String      strError;   // Error message
                
        // We check the new ID
        
        if (m_IndexedVertexes.containsKey(vertex.getID()))
        {
            strError = "The taxonomy already contains a vertex with this ID";
            error = new Exception(strError);
            throw (error);
        }
        
        // We save the vertex in the list
        
        m_IndexedVertexes.put(vertex.getID(), vertex);
        m_Vertexes.add(vertex);
    }
    
    /**
     * This function returns a sequence with the ID values of the
     * vertexes in the list.
     * @return A vector with the ID of the vertexes.
     */
    
    @Override
    public String toString()
    {
        String  strVertexes = "[";    // Returned value
        
        // We copy the vertexes ID
        
        for (IVertex vertex: m_Vertexes)
        {
            strVertexes += vertex.getID() + ",";
        }
        
        // We remove the last comma
        
        strVertexes += "]";
        
        // We return the result
        
        return (strVertexes);
    }
    
    /**
     * 
     * @param vertexID
     * @return True if the list contains onve vertex with the inpuit ID
     */
    
    @Override
    public boolean contains(
            Integer vertexID)
    {
        return (m_IndexedVertexes.containsKey(vertexID));
    }
    
    /**
     * 
     * @return Number of vertexes in the list
     */
    
    @Override
    public int getCount()
    {
        return (m_Vertexes.size());
    }

    /**
     * 
     * @param index
     * @return The vertex in the required position
     */
    
    @Override
    public IVertex getAt(int index)
    {
        return (m_Vertexes.get(index));
    }
    
    /**
     * This function returns the vertex by ID.
     * @param vertexId
     * @return The vertexes with the required Id
     */
    
    @Override
    public IVertex getById(Integer vertexId)
    {
        IVertex query = null;   // Returned value
        
        // We check that the vertex is contained in the list
        
        if (m_IndexedVertexes.containsKey(vertexId))
        {
            query = m_IndexedVertexes.get(vertexId);
        }
        
        // We return the result
        
        return (query);
    }
    
    /**
     * This function returns a set of vertexes by Ids.
     * @param vertexIds
     * @return 
     * @throws java.lang.Exception 
     */
    
    @Override
    public IVertexList getByIds(
            Integer[]   vertexIds) throws Exception
    {
        VertexList  query;  // Returned value
        
        // We create the output list
        
        query = new VertexList(false);
        
        // We recover all the vertexes required
        
        for (Integer vertexID: vertexIds)
        {
            if (m_IndexedVertexes.containsKey(vertexID))
            {
                query.add(m_IndexedVertexes.get(vertexID));
            }
        }
        
        // We return the result
        
        return (query);
    }
    
    /**
     * 
     * @return One vector containing the IDs of all the vertexes contained
     * in the list.
     */
    
    @Override
    public int[] getIDs()
    {
        int[]   idVertexes; // returned value
        
        int i = 0;  // Counter
        
        // We cretae the output vector
        
        idVertexes = new int[m_Vertexes.size()];
        
        // We copy the iDs
        
        for (IVertex vertex: m_Vertexes)
        {
            idVertexes[i++] = vertex.getID();
        }
        // We return the result
        
        return (idVertexes);
    }
    
    /**
     * Iterator
     * @return 
     */

    @Override
    public Iterator<IVertex> iterator()
    {
        return (m_Vertexes.iterator());
    }
}
