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

import java.util.Set;

/**
 * This interface encapsulates a SNOMED-CT concept
 * @author j.lastra
 */

public interface ISnomedConcept
{
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
    
    boolean isParent(Long cuid);
    
    /**
     * This function checks if the concept is a direct child of the current one.
     * @param cuid
     * @return 
     */
    
    boolean isChild(Long cuid);
    
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
}
