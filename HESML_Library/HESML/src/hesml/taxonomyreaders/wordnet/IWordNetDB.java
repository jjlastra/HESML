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

/**
 * This interfaces represents an abstract WordNetDB which manages
 * the WordNet databases.
 * @author Juan Lastra-Díaz
 */

public interface IWordNetDB extends Iterable<IWordNetSynset>
{
    /**
     * This function returns the WordNet version assigned to this database.
     * @return 
     */
    
    String getVersion();
    
    /**
     * This function sets the version name of the WordNet database.
     * @param strVersion 
     */
    
    void setVersion(String strVersion);
    
    /**
     * This functions determines if the input word is present in the DB.
     * 
     * @param strWord
     * @return True if the word is contained in the DB
     */
    
    boolean contains(String strWord);

    /**
     * This function returns the number of synsets in the database.
     * @return 
     */
    
    int getSynsetCount();
    
    /**
     * This function returns the unique ID for all the synsets
     * associated to the input word.
     * @param strWord The word whose synsets will be retrieved
     * @return A set of synsets ID
     * @throws java.lang.Exception Unexpected error
     */

    IWordNetSynset[] getWordSynsets(
        String  strWord) throws Exception;
    
    /**
     * This function returns a vector with the synset ID values of the
     * concepts evoked by the input word.
     * @param strWord Input word
     * @return A sequence of synset ID evoked by the input word
     * @throws Exception Unexpected error
     */
    
    Integer[] getWordSynsetsID(
        String  strWord) throws Exception;
    
    /**
     * This function returns the maximum number of Synset for a word
     * in WordNet.
     * @return 
     */
    
    int getMaxSynsetsNumberPerWord();
    
    /**
     * This function returns the synset associated to the input ID
     * @param synsetID
     * @return The synset for this ID
     */
    
    IWordNetSynset getSynset(Integer synsetID);
    
    /**
     * Clear the database
     */
    
    void clear();
}
