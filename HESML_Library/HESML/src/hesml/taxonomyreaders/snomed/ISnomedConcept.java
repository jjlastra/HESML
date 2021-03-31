/*
 * Copyright (C) 2016-2020 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml.taxonomyreaders.snomed;

import java.util.Set;

/**
 * This interface encapsulates a SNOMED-CT concept
 * @author j.lastra
 */

public interface ISnomedConcept
{
    /**
     * This function returns the owener ontology
     * @return 
     */
    
    ISnomedCtOntology getOntology();
    
    /**
     * This function returns the ID of the taxonomy vertex
     * corresponding to this concept in the SNOMED-CT taxonomy.
     * @return taxonomy node ID
     */
    
    Long getTaxonomyNodeId();
    
    /**
     * This function returns the inmmediate parent concepts.
     * @return The set of parent concepts.
     */
    
    Set<ISnomedConcept> getParents() throws Exception;
    
    /**
     * This function checks if the input concept is a parent concept
     * for the current Synset
     * @param cuid
     * @return 
     */
    
    boolean isParent(Long childSnomedId);
    
    /**
     * This function checks if the concept is a direct child of the current one.
     * @param cuid
     * @return 
     */
    
    boolean isChild(Long parentSnomedId);
    
    /**
     * This function returns the terms evocating the concept
     * defined by the current SNOMED-CT concept.
     * @return The words associated to this synset.
     */
    
    String[] getTerms();

    /**
     * Unique SNOMED-CT ID for the current concept
     * @return Unique Identifier for the concept in SNOMED-CT.
     */
    
    Long getSnomedId();
    
    /**
     * This function returns the value of the traversing
     * flag used to traverse the SNOMED-CT DB graph.
     * @return The value of the visiting flag.
     */
    
    boolean getVisited();
    
    /**
     * This function sets the values of the traversing flag.
     * @param visited 
     */
    
    void setVisited(boolean visited);

    /**
     * This function returns the CUID values from the
     * parent concepts of the current concept.
     * @return The parents of the synset.
     */
    
    Long[] getParentsSnomedId() throws Exception;
    
    /**
     * This function returns the taxonomy ID values from the
     * parent concepts of the current concept.
     * @return The taxonomy ID of the parent SNOMED concepts
     */
    
    Long[] getParentTaxonomyNodesId() throws Exception;
}
