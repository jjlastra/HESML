/*
 * Copyright (C) 2016-2018 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml.taxonomyreaders.wordnet.impl;

import hesml.taxonomyreaders.wordnet.IWordNetDB;
import hesml.taxonomyreaders.wordnet.IWordNetSynset;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class implements a WordNet DB which is defined as a collection of
 * synsets.
 * @author Juan Lastra-Díaz
 */

class WordNetDB implements IWordNetDB
{
    /**
     * Synsets indexed by their unique Id
     */
    
    private HashMap<Long, IWordNetSynset>    m_IndexedSynsets;
    
    /**
     * Synset collection
     */
    
    private ArrayList<IWordNetSynset>    m_Synsets;
    
    /**
     * Synsets for each word. This is the inverted map from
     * word to synsets.
     */
    
    private HashMap<String, ArrayList<IWordNetSynset>>  m_IndexedByWord;
    
    /**
     * This attribute is assigned by the client code to store
     * the OWrdNet database version.
     */
    
    private String  m_strDBVersion;
    
    /**
     * Constructor
     */
    
    public WordNetDB()
    {
        // We create the mapping table in blank
        
        m_IndexedSynsets = new HashMap<>(120000);
        m_Synsets = new ArrayList<>(120000);
        m_IndexedByWord = new HashMap<>(120000);
        
        // We initialize the version to the 3.0 by default
        
        m_strDBVersion = "WN3.0";
    }

    /**
     * This function returns the WordNet version assigned to this database.
     * @return 
     */
    
    @Override
    public String getVersion()
    {
        return (m_strDBVersion);
    }
    
    /**
     * This function sets the version name of the WordNet database.
     * @param strVersion
     */
    
    @Override
    public void setVersion(String strVersion)
    {
        m_strDBVersion = strVersion;
    }
    
    /**
     * This function returns the maximum number of Synsets for a word
     * in WordNet.
     * @return 
     */
    
    @Override
    public int getMaxSynsetsNumberPerWord()
    {
        int maxSynsets = 0; // Returned value
        
        // We search the maximum number of synsets per word
        
        for (ArrayList<IWordNetSynset>  synsets: m_IndexedByWord.values())
        {
            maxSynsets = Math.max(synsets.size(), maxSynsets);
        }
        
        // We return the result
        
        return (maxSynsets);
    }
    
    /**
     * This function returns the number of synsets in the database.
     * @return 
     */
    
    @Override
    public int getSynsetCount()
    {
        return (m_Synsets.size());
    }
    
    /**
     * This function returns the Synset matching the required ID or null.
     * @param synsetID Unique ID of the synset
     * @return Synset whose ID matches the input ID
     */
    
    @Override
    public IWordNetSynset getSynset(
            Long synsetID)
    {
        return (m_IndexedSynsets.get(synsetID));
    }
    
    /**
     * This function builds a total ordering for the synset collection
     * regardless their original insertion order. The synsets define
     * a partially ordered set (poset), being the taxonomy of WordNet.
     * We recover the poset structure by building the total ordering,
     * which is achieved if each synset is subsequent to all its
     * parent synsets.
     * @throws java.lang.InterruptedException Unexpected error
     */    

    void sortSynsets() throws InterruptedException
    {
        LinkedList<IWordNetSynset>    pending;    // Processing queue
        
        IWordNetSynset   pendingSynset;  // Synset to read
        
        boolean allParentsVisited; // Flag
        
        // We cretae the pending queue
        
        pending = new LinkedList<>();
        
        for (IWordNetSynset synset: m_Synsets)
        {
            synset.setVisited(false);
            pending.add(synset);
        }
        
        // We clear the synset
        
        m_Synsets.clear();
        
        // We process the pending nodes
        
        while (!pending.isEmpty())
        {
            // We get the next synset to load
            
            pendingSynset = pending.remove();
            
            // We check if all its parents have been visited
            
            allParentsVisited = true;
            
            for (Long parentId: pendingSynset.getParentsId())
            {
                if (!m_IndexedSynsets.get(parentId).getVisited())
                {
                    allParentsVisited = false;
                    break;
                }
            }
            
            // We put the current synset in the list (totally order) or we
            // enqueue it again when any parent has not been visited yet.
            
            if (allParentsVisited)
            {
                pendingSynset.setVisited(true);
                m_Synsets.add(pendingSynset);
            }
            else
            {
                pending.add(pendingSynset);
            }
        }
    }
    
    /**
     * This function creates a novel synset and inserts it on the DB.
     * @param synset Synset inserted in the WordNet database
     * @throws java.lang.Exception Unexpected error
     */
    
    void addSynset(
            WordNetSynset   synset) throws Exception
    {
        Exception   error;      // Thrown error
        String      strError;   // Error message
        
        // We check that the id is not already used
        
        if (m_IndexedSynsets.containsKey(synset.getID()))
        {
            strError = "There are other synset with the same id";
            error = new Exception(strError);
            throw (error);
        }
        
        // We inserts the synset in the DB
        
        m_IndexedSynsets.put(synset.getID(), synset);
        m_Synsets.add(synset);
        
        // We build the idnex for the words of the synset
        
        addIntoWordsToSynsetMapping(synset);
    }
    
    /**
     * Add the synset to the wprd-to-synsets inverted mapping.
     * @param synset 
     */
    
    private void addIntoWordsToSynsetMapping(
            WordNetSynset   synset)
    {
        ArrayList<IWordNetSynset>   wordSynsets;    // Synsets by word
                
        // We get the synsets associated to each word
        
        for (String strWord: synset.getWords())
        {
            // We get the set of synsets for the word
            
            wordSynsets = m_IndexedByWord.get(strWord);
            
            if (wordSynsets == null)
            {
                wordSynsets = new ArrayList<>();
                m_IndexedByWord.put(strWord, wordSynsets);
            }
            
            // We add the synset to this word
            
            wordSynsets.add(synset);
        }
    }
    
    /**
     * This function destroy the current DB
     */
    
    @Override
    public void clear()
    {
        // We clear the ampping of synsets per word
        
        for (ArrayList<IWordNetSynset> synsets: m_IndexedByWord.values())
        {
            synsets.clear();
        }
        
        m_IndexedByWord.clear();
        
        // We clear the synset collections
        
        m_IndexedSynsets.clear();
        m_Synsets.clear();
    }

    /**
     * 
     * @return Iterator
     */
    
    @Override
    public Iterator<IWordNetSynset> iterator()
    {
        return (m_Synsets.iterator());
    }

    /**
     * This functions determines if the input word is present in the DB.
     * 
     * @param strWord
     * @return True if the word is contained in the DB
     */
    
    @Override
    public boolean contains(
            String  strWord)
    {
        return (m_IndexedByWord.containsKey(strWord));
    }
    
    /**
     * This function retrieves the synsets ID associated to the input word.
     * @param strWord
     * @return 
     */
    
    @Override
    public IWordNetSynset[] getWordSynsets(String strWord) throws Exception
    {
        Exception   error;  // Thrown exception
        
        IWordNetSynset[]    synsets;    // Returned value
        
        ArrayList<IWordNetSynset>   wordSynsets;    // Synsets for the word
        
        // We get the synsets for the word
        
        wordSynsets = m_IndexedByWord.get(strWord);
        
        if (wordSynsets == null)
        {
            error = new Exception("The word is not in WordNet " + strWord);
            throw (error);
        }
        
        // We create the arrya to copy the synsets
        
        synsets = new IWordNetSynset[wordSynsets.size()];
        
        // We copy the synsets
        
        wordSynsets.toArray(synsets);
        
        // We return the result
        
        return (synsets);
    }

    /**
     * This function returns a vector with the synset ID values of the
     * concepts evoked by the input word.
     * @param strWord Input word
     * @return A sequence of synset ID evoctaed by the input word
     * @throws Exception Unexpected error
     */
    
    @Override
    public Long[] getWordSynsetsID(String strWord) throws Exception
    {
        Exception   error;  // Thrown exception
        
        Long[]    synsets;    // Returned value
        
        ArrayList<IWordNetSynset>   wordSynsets;    // Synsets for the word
        
        int i = 0;  // Counter
        
        // We get the synsets for the word
        
        wordSynsets = m_IndexedByWord.get(strWord);
        
        if (wordSynsets == null)
        {
            error = new Exception("The word is not in WordNet " + strWord);
            throw (error);
        }
        
        // We create the arrya to copy the synsets
        
        synsets = new Long[wordSynsets.size()];
        
        // We copy the synset ID values
        
        for (IWordNetSynset synset: wordSynsets)
        {
            synsets[i++] = synset.getID();
        }
        
        // We return the result
        
        return (synsets);
    }
}
