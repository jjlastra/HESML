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
 */

package hesml.taxonomyreaders.mesh;

import hesml.taxonomy.ITaxonomy;

/**
 * This interface represents a MeSH Descriptor Data (MeSH concept).
 * @author j.lastra
 */

public interface IMeSHOntology extends Iterable<IMeSHDescriptor>
{
    /**
     * This functions determines if the input term is present in the DB.
     * @param strTerm
     * @return True if the word is contained in the DB
     */
    
    boolean contains(String strTerm);

    /**
     * This function returns the number of concepts in the database.
     * @return 
     */
    
    int getConceptCount();
    
    /**
     * This function returns the MeSH concept associated to the CUI
     * or null if it is not found in the MeSH database.
     * @param meshDescriptorId
     * @return 
     */
    
    IMeSHDescriptor[] getConceptsForUmlsCUI(String meshDescriptorId);

    /**
     * This function returns the MeSH concepts associated to the CUIs
     * or null if they are not found in the MeSH ontology.
     * @param meshDescriptorId
     * @return 
     */
    
    IMeSHDescriptor[] getConceptsForUmlsCUI(String[] meshDescriptorId);

    /**
     * This function returns an ordered array with all descriptor in the
     * MeSH ontology. Concepts are totally ordered from the root in a BFS mode.
     * @return 
     */
    
    IMeSHDescriptor[] getAllDescriptors();
    
    /**
     * This function returns all MeSH descriptors associated to the input term.
     * @param strTerm The term whose concepts will be retrieved
     * @return An array of MeSH descriptors
     * @throws java.lang.Exception Unexpected error
     */

    IMeSHDescriptor[] getTermDescriptor(
        String  strTerm) throws Exception;
    
    /**
     * This function returns a vector with the CUID values of the
     * concepts evoked by the input term.
     * @param strTerm Input term
     * @return A sequence of MeSH descriptor Ids values corresponding to
     * the MeSH descriptors (concepts) evoked by the input term
     * @throws Exception Unexpected error
     */
    
    String[] getMeSHDescriptoIdsEvokedByTerm(
        String  strTerm) throws Exception;
       
    /**
     * This function returns the concept associated to the input CUID
     * @param meshDescriptorId
     * @return The concept for this meshDescriptorId
     */
    
    IMeSHDescriptor getDescriptor(String meshDescriptorId);
    
    /**
     * This function returns the HESML taxonomy encoding the MeSH 'is-a' ontology.
     * @return In-memory HESML taxonomy encoding the 'is-a' SNOMED-CT graph
     */
    
    ITaxonomy getTaxonomy();
    
    /**
     * Clear the database
     */
    
    void clear();
}
