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

package hesml.taxonomy;

/**
 * This interface represents an abstract ordered collection of taxonomy
 * vertexes as defined in the HESML paper below.
 * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * HESML: a scalable ontology-based semantic similarity measures
 * library with a set of reproducible experiments and a replication dataset.
 * Submitted for publication to the Information Systems Journal.
 * 
 * @author Juan Lastra-Díaz
 */

public interface IVertexList extends Iterable<IVertex>
{
    /**
     * This function returns an ordered list of vertexes matching the
     * input ID sequence.
     * @param vertexIds Vector of vertex Id values for the vertex to be retrieved.
     * @return Ordered list of vertexes
     * @throws java.lang.Exception Unexpected error
     */
    
    IVertexList getByIds(Integer[] vertexIds) throws Exception;
    
    /**
     * This function computes the difference set as regards the input set.
     * @param others Input vertex list to be compared with the current one.
     * @return The difference set
     * @throws java.lang.Exception Unexpected error
     */
    
    IVertexList getDifferenceSet(
            IVertexList others) throws Exception;

    /**
     * This function computes element count in the difference set.
     * @param others Input vertex list to be compared with the current one.
     * @return Number of elements in the difference set
     * @throws java.lang.Exception Unexpected error
     */
    
    long getDifferenceSetCount(
            IVertexList others) throws Exception;
    
    /**
     * This function computes the intersection set between the current
     * and input lists.
     * @param others Input set to be compared
     * @return Intersection set
     * @throws java.lang.Exception Unexpected error
     */
    
    IVertexList getIntersectionSet(
            IVertexList others) throws Exception;

    /**
     * This function computes the number of elements in the intersection set
     * between the current vertex list and the input set.
     * @param others Input set to be compared
     * @return Number of elements in the intersection set
     * @throws java.lang.Exception Unexpected error
     */
    
    long getIntersectionSetCount(
            IVertexList others) throws Exception;
    
    /**
     * This function computes the element count for the intersection set
     * between the current and input lists.
     * @return Element count in the intersection set.
     */
    
    public double[] getICValues();
    
    /**
     * This function returns the greatest IC value for any vertex
     * in the collection.
     * @return Highest IC value
     */
    
    double getGreatestICValue();
    
    /**
     * This function returns the number of leaf vertexes in the current list.
     * @return The number of Leaf nodes in the list.
     * @throws java.lang.Exception Unexpected error
     */
    
    int getLeavesCount() throws Exception;
    
    /**
     * This function returns the greatest depth value among all the vertexes
     * contained in the list, where the depth is defined as the length of
     * the shortest ascending path from any vertex to the root,
     * so called DepthMin in HESML library.
     * @return Highest depth value
     * @throws java.lang.Exception
     */
    
    int getGreatestDepthMin() throws Exception;
    
    /**
     * This function returns the greatest depth value among all the vertexes
     * contained in the list, where the depth is defined as the length of
     * the longest ascending path from any vertex to the root, so called
     * DepthMax in HESML library.
     * @return Highest depth value
     * @throws java.lang.Exception
     */
    
    int getGreatestDepthMax() throws Exception;

    /**
     * This function returns the greatest depth value in base 1.
     * @return Highest depthMIn value plus 1
     * @throws java.lang.Exception
     */
    
    int getGreatestDepthMinBase1() throws Exception;
    
    /**
     * This function returns an ordered sequence (vector) with the DepthMin
     * values of the sequence of vertexes contained in the list.
     * @return The depth of the vertexes in the list
     * @throws java.lang.Exception
     */
    
    int[] getDepthMinValues() throws Exception;
    
    /**
     * This function returns the set of root vertexes contained in the list.
     * @return A set of roots nodes included in the list
     * @throws java.lang.Exception Unexpected error
     */
    
    IVertexList getRoots() throws Exception;
    
    /**
     * This function returns the leaf vertexes contained in the list.
     * @return The leaves in the list
     * @throws java.lang.Exception Unexpected error
     */
    
    IVertexList getLeaves() throws Exception;
    
    /**
     * This function returns the position of the input vertex in the list,
     * or -1 if it is not present.
     * @param vertex Input vertex
     * @return The position of the input vertex in the list
     */
    
    int indexOf(IVertex vertex);
    
    /**
     * This function returns one vector containing the ordered IDs of
     * the vertexes in the list.
     * @return Vertex ID sequence
     */
    
    int[] getIDs();
    
    /**
     * This function sets the value of the traversal flag (visited) used
     * by part of the client code to mark the visited vertexes
     * during a traversal of the vertex set.
     * @param visited New traversal value
     */
    
    void setVisited(boolean visited);
    
    /**
     * This function checks whether a vertex with the input ID is contained
     * in the list.
     * @param vertexID ID of the vertex whose existence in the list will be checked
     * @return True if the list contains the vertex with input ID
     */
    
    boolean contains(Integer vertexID);
    
    /**
     * This function returns the count of vertexes in the list.
     * @return The number of vertexes in the list
     */
    
    int getCount();
 
    /**
     * This function returns the vertex at the required position.
     * @param index
     * @return The vertex in the input position
     */
    
    IVertex getAt(int index);
    
    /**
     * This function returns a vertex by ID whether it is contained
     * in the list.
     * @param vertexId
     * @return The vertex with the id required
     */
    
    IVertex getById(Integer vertexId);
    
    /**
     * This functions returns the lock state of the list.
     * @return 
     */
    
    boolean isLocked();
    
    /**
     * This function removes all the vertexes in the list, releasing
     * the memory space. This function only can be invoked for non-locked
     * lists as all the vertex list computed on-the-fly. The only
     * locked list is the global vertexes list of the taxonomy.
     * @throws java.lang.Exception Unexpected error
     */
    
    void clear() throws Exception;
}
