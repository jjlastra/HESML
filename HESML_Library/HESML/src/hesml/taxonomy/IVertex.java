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
 * This interface represents an abstract vertex within a taxonomy
 * as detailed in the paper below.
 * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * HESML: a scalable ontology-based semantic similarity measures
 * library with a set of reproducible experiments and a replication dataset.
 * Submitted for publication to the Information Systems Journal.
 * 
 * @author Juan Lastra-Díaz
 */

public interface IVertex extends Comparable<IVertex>
{
    /**
     * This function checks if the current vertex is a Leaf
     * @return A boolean indicating if the vertex is a leaf node or not.
     */
    
    boolean isLeaf();
    
    /**
     * This function returns a vector with the string tag fields of the parent
     * vertexes.
     * @return A vector with the string Tag fields of the parents nodes. 
     */
    
    String[] getParentStringTags();
    
    /**
     * This function returns the number of direct parents.
     * @return The number of parents
     */
    
    int getParentsCount();
    
    /**
     * This function returns the content of the string Tag field
     * @return A user-defined string field
     */
    
    String getStringTag();
    
    /**
     * This function sets the user-defined string field.
     * @param strTag New free-use string attribute attached to the vertex
     */
    
    void setStringTag(String strTag);
    
    /**
     * This function returns the length of the shortest ascending path from
     * the vertex to the root, so called DepthMin in HESMl library.
     * The depth takes a 0 value in the root.
     * @return Depth for the vertex.
     * @throws java.lang.Exception
     */
    
    int getDepthMin() throws Exception;
    
    /**
     * This function returns the length of the longest ascending path length
     * from the vertex to the root, so called DepthMax in HESML library.
     * The depth takes a 0 value in the root.
     * @return Longest depth value for the vertex
     * @throws java.lang.Exception
     */
    
    int getDepthMax() throws Exception;

    /**
     * This function returns DepthMax + 1 for the vertex.
     * @return The longest depth starting at 1, as defined by
     * @throws java.lang.Exception
     */
    
    int getDepthMaxBase1() throws Exception;;
    
    /**
     * This function returns Depth + 1
     * @return The depth starting at 1, as defined by Zhou et al. (2008)
     * @throws java.lang.Exception
     */
    
    int getDepthMinBase1() throws Exception;;
    
    /**
     * This function returns the hyponym set subsumed by the vertex.
     * The hyponym set is computed on-the-fly.
     * @param vertexInclusive Indicates whether the base vertex must be included.
     * @return Hyponym set of the vertex
     * @throws java.lang.Exception Unexpected error
     */
    
    IVertexList getHyponyms(boolean vertexInclusive) throws Exception;
    
    /**
     * This function returns the leaves set subsumed by the vertex,
     * being computed on-the-fly.
     * @param inclusive The vertex will be included if it is a leaf node.
     * @return The number of hyponyms
     * @throws java.lang.Exception An unexpected exception
     */
    
    IVertexList getSubsumedLeaves(boolean inclusive) throws Exception;
    
    /**
     * This function returns the number of subsumed leaves
     * including the vertex.
     * @return Overall subsumed leaves
     * @throws java.lang.Exception An unexpected exception
     */
    
    int getInclusiveSubsumedLeafSetCount() throws Exception;
    
    /**
     * This function returns the subsumer set (ancestor set) including
     * the vertex if it is required.
     * @param includeVertex Indicates that the current vertex will be included.
     * @return Ancestor sets including the own vertex.
     * @throws InterruptedException An unexpected exception
     */
    
    IVertexList getAncestors(
            boolean includeVertex) throws InterruptedException, Exception;
    
    /**
     * This function returns the pre-computed hyponym count without including the
     * vertex.
     * @return Number of hyponyms subsumed by the current vertex.
     * @throws java.lang.Exception Unexpected error
     */
    
    int getNonInclusiveHyponymSetCount() throws Exception;
    
    /**
     * This function returns the first outcoming oriented edge as defined
     * in the HESML paper.
     * @return The first outcoming oriented edge.
     */
    
    IHalfEdge getFirstOutcomingEdge();
    
    /**
     * This function returns the value of the minimum distance attribute
     * used by the shortest path algorithm.
     * @return The minimum accumulated distance in any Djikstra-like method
     */
    
    double getMinDistance();
    
    /**
     * This function sets the minimum distance value that is accumulated
     * by the vertex.
     * @param minDistance New Minimum distance value.
     */
    
    void setMinDistance(double  minDistance);
    
    /**
     * This function computes the Dijkstra algorithm using the edge weights
     * assigned to the taxonomy. When the parameter 'weighted' is false, the
     * function uses the edge length (weight = 1), otherwise
     * it computes the weighted shortest path distance.
     * @param target Final path vertex.
     * @param weighted Flag indicating the value for the weights
     * @return The minimum accumulated distance to the target vertex.
     */
    
    double getShortestPathDistanceTo(
            IVertex     target,
            boolean     weighted);

    /**
     * This function computes the distance field from the current vertex
     * using the Dijkstra algorithm and the edge weights
     * assigned to the taxonomy. When the parameter 'weighted' is false, the
     * function uses the edge length (weight = 1), otherwise
     * it computes the weighted shortest path distance.
     * Once the function is executed, the distance from the vertex
     * to each vertex in the taxonomy can be recovered by calling
     * the getMinDistance() function on each vertex.
     * @param weighted Flag indicating the value for the weights
     */
    
    void computeDistanceField(
            boolean     weighted);
    
    /**
     * This function computes the length of the shortest path between the
     * current vertex and the target vertex using the weights of the oriented
     * edges, instead of the weight of the non-oriented edges. Thus, this
     * method allows to define an asymmetric distance between vertexes
     * in the taxonomy.
     * @param target Target vertex
     * @return The length of the shortest path
     */
    
    double getAsymmetricShortestPathDist(
            IVertex target);
    
    /**
     * This function returns the IC-value assigned to this vertex.
     * @return The IC value for this vertex
     */
    
    double getICvalue();
       
    /**
     * This function returns the oriented outcoming edge pointing to the input vertex.
     * @param target Target vertex for the requested outcoming oriented-edge
     * @return Outcoming half-edge pointing to the target vertex.
     */
    
    IHalfEdge getIncidentEdge(IVertex target);

    /**
     * This function returns the content of the user-defined Tag field.
     * @return Tag field used by any client code
     */
    
    Object getTag();
    
    /**
     * This function sets the content of the user-defined Tag field.
     * @param tag New tag field content.
     */
    
    void setTag(Object tag);
    
    /**
     * This function returns the number of ancestors without
     * to include the own base vertex.
     * @return CachedAncestorsCount The number of ancestors without the own vertex
     * @throws java.lang.InterruptedException Unexpected error
     */
    
    int getNonInclusiveAncestorSetCount() throws InterruptedException, Exception;
    
    /**
     * This function returns the cached value for the number
     * of leaves subsumed by this vertex. This value does not
     * include the own vertex if it would be already a leaf node.
     * @return CachedLeavesCount The number of leaf nodes without the own vertex
     * @throws java.lang.Exception number of ancestors
     */
    
    int getNonInclusiveSubsumedLeafSetCount() throws Exception;
    
    /**
     * This function sets the IC value for this vertex.
     * @param newICvalue New IC value assigned to the vertex
     */
    
    void setICValue(double newICvalue);
    
    /**
     * This function returns the probability stored in the node.
     * @return The probability field value.
     */
    
    double getProbability();
    
    /**
     * This function sets the probability stored in the node.
     * @param newProbabilityValue New probability value assigned to the vertex
     */
    
    void setProbability(double newProbabilityValue);
    
    /**
     * This function return the key ID for the vertex.
     * @return The unique ID of the vertex.
     */
    
    int getID();
    
    /**
     * This function returns the taxonomy that contains the vertex.
     * @return Owner taxonomy
     */
    
    ITaxonomy  getTaxonomy();
    
    /**
     * This function sets the value of the first traversal flag
     * @param visited New value stored in the first traversal flag.
     */
    
    void setVisited(boolean visited);
    
    /**
     * This function returns the value of the first traversal flag.
     * @return Value of the first traversal flag
     */
    
    boolean getVisited();

    /**
     * This function returns the children set of the vertex.
     * @return Direct child vertexes
     * @throws java.lang.Exception number of ancestors
     */
    
    IVertexList getChildren() throws Exception;
    
    /**
     * This function returns the number of child vertexes.
     * @return Number of children nodes.
     */
    
    int getChildrenCount();
    
    /**
     * This function returns the parent list.
     * @return Parent vertex set
     * @throws Exception Unexpected error
     */
    
    IVertexList getParents() throws Exception;
    
    /**
     * This function gets the adjacent vertexes by computing them on-the-fly.
     * @return First-order adjacent vertex set
     * @throws java.lang.Exception Unexpected error
     */
    
    IVertexList getNeighbours() throws Exception;

    /**
     * This function checks whether the vertex is a root node.
     * @return True if the vertex is a root
     */
    
    boolean isRoot();
}
