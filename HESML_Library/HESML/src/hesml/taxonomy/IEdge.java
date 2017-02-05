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
 * This interface represents a non-oriented edge joining two vertexes within
 * a taxonomy object (ITaxonomy) as detailed in the paper below:
 * Lastra-Díaz, J. J., and García-Serrano, A. (2016).
 * HESML: a scalable ontology-based semantic similarity measures
 * library with a set of reproducible experiments and a replication dataset.
 * Submitted for publication to the Information Systems Journal.
 * 
 * @author Juan Lastra-Díaz
 */

public interface IEdge
{
    /**
     * 
     * @return Direct arc
     */
    
    IHalfEdge getDirect();
    
    /**
     * 
     * @return The inverse arc
     */
    
    IHalfEdge getInverse();
    
    /**
     * 
     * @return The weight associated to this edge
     */
    
    double getWeight();
    
    /**
     * This function sets the weight associated to this edge
     * @param weight New weight value
     */
    
    void setWeight(double weight);
    
    /**
     * This function sets the conditional probability field value.
     * @param value New probability value
     */
    
    void setCondProbability(double value);

    /**
     * This function returns the conditional probability field value.
     * @return Conditional probability value stored by the edge
     */
    
    double getCondProbability();
}
