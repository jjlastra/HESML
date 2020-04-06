/*
 * Copyright (C) 2020 j.lastra
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package hesml.taxonomyreaders.snomed;

/**
 * This interface encapsulates a SNOMED-CT terminology database.
 * @author j.lastra
 */

public interface ISnomedCtDb extends Iterable<ISnomedConcept>
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
     * This function returns the unique CUID for all the concepts
     * associated to the input term.
     * @param strTerm The term whose concepts will be retrieved
     * @return A set of concepts CUID
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
    
    Long[] getTermConceptsCUID(
        String  strTerm) throws Exception;
       
    /**
     * This function returns the concept associated to the input CUID
     * @param cuid
     * @return The concept for this CUID
     */
    
    ISnomedConcept getConcept(Long cuid);
    
    /**
     * Clear the database
     */
    
    void clear();
}
