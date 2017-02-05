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
 * This interface represents an abstract oriented edge (half-edge) within
 * a taxonomy as defined in the paper below.
 * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * HESML: a scalable ontology-based semantic similarity measures
 * library with a set of reproducible experiments and a replication dataset.
 * To appear in the Information Systems Journal.
 * 
 * @author Juan Lastra-Díaz
 */

public interface IHalfEdge
{
    /**
     * This functions returns the type of the edge within the taxonomy.
     * Currently, the oriented edge only supports two types in order to
     * distinguish between ascending (SubClassOf) or descending (SuperClassOF)
     * edges. The same scheme will allow in the future to support other
     * distinguished relationships between taxonomy nodes, called vertexes
     * in HESML..
     * @return The type of edge
     */
    
    OrientedEdgeType getEdgeType();
    
    /**
     * This function returns the next outcoming edges from the target vertex.
     * @return The next edge around the source vertex.
     */
    
    IHalfEdge getNext();
    
    /**
     * This function returns the target vertex pointed by the current edge.
     * @return the vertex pointed by this arc.
     */
    
    IVertex getTarget();
    
    /**
     * This function returns the non-oriented edge that contains the current
     * instance.
     * @return The owner full edge joining the paired oriented edges
     */
    
    IEdge getEdge();

    /**
     * This functions returns the opposite paired oriented-edge.
     * @return Opposite edge
     */
    
    IHalfEdge getOpposite();
    
    /**
     * This function returns the weight value of this half-edge
     * @return Weight value used in asymmetric shortest path computation.
     */
    
    double getWeight();
    
    /**
     * This function sets the weight value associated to the
     * current instance of the oriented edge.
     * @param weight New weight value
     */
    
    void setWeight(double weight);
}
