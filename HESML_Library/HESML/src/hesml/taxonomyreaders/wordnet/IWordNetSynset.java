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

package hesml.taxonomyreaders.wordnet;

// Java references

import java.util.Set;

/**
 * This interface encapsulates a WordNet synset.
 * @author Juan Lastra-Díaz
 */

public interface IWordNetSynset 
{
    /**
     * This function returns the inmmediate parent synsets.
     * @return The set of parent synsets.
     */
    
    Set<IWordNetSynset> getParents();
    
    /**
     * This function checks if the input concept is a parent concept
     * for the current Synset
     * @param synsetID
     * @return 
     */
    
    boolean isParent(Integer synsetID);
    
    /**
     * This function checks if the synset is a direct child of the current one.
     * @param synsetID
     * @return 
     */
    
    boolean isChild(Integer synsetID);
    
    /**
     * This function returns the words evocating the concept
     * defined by the synset.
     * @return The words associated to this synset.
     */
    
    String[] getWords();

    /**
     * This function returns the gloss for the current synset if
     * it was loaded during the WordNet reading.
     * @return The gloss associated to the synset.
     */
    
    String getGloss();
    
    /**
     * Unique ID for the synset
     * @return Unique Identifier for the synset.
     */
    
    Integer getID();
    
    /**
     * This function returns the value of the visiting
     * flag used to traverse the WordNet DB graph.
     * @return The value of the visiting flag.
     */
    
    boolean getVisited();
    
    /**
     * This function sets the values of the visited flag.
     * @param visited 
     */
    
    void setVisited(boolean visited);

    /**
     * This function returns the synset ID values from the
     * parent synsets of the current synset.
     * @return The parents of the synset.
     */
    
    Integer[] getParentsId();
}
