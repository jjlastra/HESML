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
 * This interface represents an abstract taxonomy as defined in the paper below.
 * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * HESML: a scalable ontology-based semantic similarity measures
 * library with a set of reproducible experiments and a replication dataset.
 * Submitted for publication to the Information Systems Journal.
 * 
 * @author Juan Lastra-Díaz
 */

public interface ITaxonomy
{
    /**
     * Remove all the objects and destroys the taxonomy.
     */
    
    void clear();
    
    /**
     * This function returns the sum of the probability for
     * the leaf nodes.
     * @return The overall probability for the leaf nodes.
     */
    
    double getSumLeafProbability();
    
    /**
     * This function inserts a novel vertex in the graph
     * @param vertexId Integer unique key for the new vertex
     * @param parentVertexes Key ID for the parent vertexes.
     * @return The new vertex inserted into the taxonomy
     * @throws java.lang.Exception The vertexID already exists in the taxonomy,
     * or any parent ID does not match any existent vertex
     */
    
    IVertex addVertex(
            Integer     vertexId,
            Integer[]   parentVertexes) throws Exception;

    /**
     * This functions computes several cached values such as
     * the node depth. This function assumes that the taxonomy has been already
     * defined.
     * @throws java.lang.Exception An unexpected exception
     */
    
    void computesCachedAttributes() throws Exception;
    
    /**
     * This functions returns the collection of ordered vertexes in the
     * taxonomy. The list is totally ordered from the root, what means
     * that each vertex is subsequent to all its parents.
     * @return Ordered collection of vertexes within the taxonomy
     */
    
    IVertexList getVertexes();
    
    /**
     * Collection of non-oriented edges in the taxonomy.
     * @return A edge list containig all the non-oriented edges in the taxonomy
     */
    
    IEdgeList getEdges();
    
    /**
     * This function returns the most informative common ancestor (MICA) vertex
     * between the two input vertexes. The computation is made by retrieving
     * on-the-fly the ancestor set from each vertex and searching for the
     * common ancestor with the highest IC value.
     * @param begin First input vertex
     * @param end Second input vertex
     * @return The most informative common ancestor (MICA) vertex
     * @throws java.lang.InterruptedException A MICA vertex was not found
     */

    IVertex getMICA(IVertex begin, IVertex end)
            throws InterruptedException, Exception;

    /**
     * This function computes the lowest common subsumer (ancestor),
     * which is defined as the common ancestor with highest depth.
     * The function returns the first vertex in the LCS set,
     * although could exist more than one on multiple inheritance
     * taxonomies.
     * @param begin First vertex
     * @param end Second vertex
     * @param useLongestDepth This parameter indicates whether the method
     * will use the length of the shortest ascending path value (FALSE)
     * as depth value for the search process, or the longest ascending
     * path (TRUE). We recall that depth attribute is ambiguous on
     * multiple-inheritance taxonomies as WordNet.
     * @return The first LCS vertex
     * @throws java.lang.Exception There is no any common subsumer
     */
    
    IVertex getLCS(
            IVertex begin,
            IVertex end,
            boolean useLongestDepth) throws Exception;
}
