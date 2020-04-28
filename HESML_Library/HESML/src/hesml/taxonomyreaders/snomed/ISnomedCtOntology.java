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

package hesml.taxonomyreaders.snomed;

import hesml.taxonomy.ITaxonomy;

/**
 * This interface encapsulates a SNOMED-CT terminology database.
 * @author j.lastra
 */

public interface ISnomedCtOntology extends Iterable<ISnomedConcept>
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
     * This function returns the SNOMED concept associated to the CUI
     * or null if it is not found in the SNOMED database.
     * @param umlsConceptCUI
     * @return 
     */
    
    ISnomedConcept[] getConceptsForUmlsCUI(String umlsConceptCUI);

    /**
     * This function returns the SNOMED concepts associated to the CUIs
     * or null if they are not found in the SNOMED database.
     * @param umlsConcetCUI
     * @return 
     */
    
    ISnomedConcept[] getConceptsForUmlsCUI(String[] umlsConceptCUIs);

    /**
     * This function returns an ordered array with all concepts in the
     * database. Concepts are totally ordered from the root in a BFS mode.
     * @return 
     */
    
    ISnomedConcept[] getAllConcepts();
    
    /**
     * This function returns the unique CUID for all the concepts
     * associated to the input term.
     * @param strTerm The term whose concepts will be retrieved
     * @return A set of SNOMED ids
     * @throws java.lang.Exception Unexpected error
     */

    ISnomedConcept[] getTermConcepts(
        String  strTerm) throws Exception;
    
    /**
     * This function returns a vector with the CUID values of the
     * concepts evoked by the input term.
     * @param strTerm Input term
     * @return A sequence of CUID values corresponding to the concepts
     * evoked by the input term
     * @throws Exception Unexpected error
     */
    
    Long[] getSnomedConceptIdsEvokedByTerm(
        String  strTerm) throws Exception;
       
    /**
     * This function returns the concept associated to the input CUID
     * @param cuid
     * @return The concept for this snomedId
     */
    
    ISnomedConcept getConcept(Long snomedId);
    
    /**
     * This function returns the HESML taxonomy encoding the SNOMED-CT 'is-a'
     * ontology.
     * @return In-memory HESML taxonomy encoding the 'is-a' SNOMED-CT graph
     */
    
    ITaxonomy getTaxonomy();
    
    /**
     * Clear the database
     */
    
    void clear();
}
