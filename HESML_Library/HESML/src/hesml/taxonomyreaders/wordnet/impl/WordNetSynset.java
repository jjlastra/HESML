/*
 * Copyright (C) 2016-2021 Universidad Nacional de Educación a Distancia (UNED)
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
        
import hesml.taxonomyreaders.wordnet.IWordNetSynset;
import hesml.taxonomyreaders.wordnet.PartOfSpeech;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class represents a Synset in WordNet
 * @author Juan Lastra-Díaz
 */

class WordNetSynset implements IWordNetSynset
{
    /**
     * Words associated to this Synsets
     */
    
    private final String[]   m_SynonymWords;
    
    /**
     * Parents of the current synset
     */
    
    private final Long[]  m_ParentsId;
    
    /**
     * Gloss associated to the Synsets
     */
    
    private final String  m_strGloss;
    
    /**
     * Synset unique Id
     */
    
    private final Long m_SynsetId;
    
    /**
     * Syntactical category
     */
    
    private PartOfSpeech    m_PartOfSpeech;

    /**
     * Traversing flag
     */
    
    private boolean m_Visited;
    
    /**
     * Owner WordNet DB
     */
    
    private final WordNetDB   m_OwnerDB;

    /**
     * Constructor
     * @param ownerDB
     * @param synsetId
     * @param partofSpeech
     * @param strWords
     * @param parentsId
     * @param strGloss 
     */
    
    public WordNetSynset(
            WordNetDB   ownerDB,
            Long        synsetId,
            char        partofSpeech,
            String[]    strWords,
            Long[]      parentsId,
            String      strGloss)
    {
        // We store the fields
        
        m_OwnerDB = ownerDB;
        m_SynsetId = synsetId;
        m_SynonymWords = strWords;
        m_ParentsId = parentsId;
        m_strGloss = strGloss;
        m_Visited = false;
        
        // Weread the POS type
        
        readPOS(partofSpeech);
        
        // We normalize the words
        
        normalizeWords();
    }
    
    /**
     * This function checks whether the input concept is a parent concept
     * for the current Synset
     * @param synsetID
     * @return 
     */
    
    @Override
    public boolean isParent(Long synsetID)
    {
        // We initialize the result
        
        boolean parent = false;
        
        // We check if the synset is a prent of the current synset
        
        for (Long parentID: m_ParentsId)
        {
            if (Objects.equals(parentID, synsetID))
            {
                parent = true;
                break;
            }
        }
        
        // We return the result
        
        return (parent);
    }
    
    /**
     * This function checks whether the synset is a direct child of the current one.
     * @param synsetID
     * @return 
     */
    
    @Override
    public boolean isChild(Long synsetID)
    {
        // We initialize the output
        
        boolean isChild = false;
        
        // We retrieve the synset from DB
        
        IWordNetSynset child = m_OwnerDB.getSynset(synsetID);
        
        // We check the query
        
        if (child != null) isChild = child.isParent(m_SynsetId);
        
        // We return the result
        
        return (isChild);
    }
    
    /**
     * This function returns the visiting field of the sysnset.
     * @return The visiting flag
     */
    
    @Override
    public boolean getVisited()
    {
        return (m_Visited);
    }
    
    /**
     * This function sets the visited flag
     * @param visited 
     */
    
    @Override
    public void setVisited(
            boolean visited)
    {
        m_Visited = visited;
    }
    
    /**
     * This function returns the parent IDs
     * @return The id of the parents
     */
    
    @Override
    public Long[] getParentsId()
    {
        return (m_ParentsId.clone());
    }
    
    /**
     * This function substitutes the underline by blank spaces
     * used by the WordNet file format.
     */
    
    private void normalizeWords()
    {
        int i;  // Counter
        
        // We normalize the words
        
        for (i = 0; i < m_SynonymWords.length; i++)
        {
            m_SynonymWords[i] = m_SynonymWords[i].replace('_', ' ');
        }
    }
    
    /**
     * This function returns the word set associated to this synset.
     * @return The words associated to this synset
     */
    
    @Override
    public String[] getWords()
    {
        return (m_SynonymWords.clone());
    }
    
    /**
     * This function loads the part of speech
     * @param posTag A char encoding the POSof the WordNet database 
     */
    
    private void readPOS(
            char  posTag)
    {
        // Convert the text in an enum defining the POS type
        
        switch (posTag)
        {
            case 'n':
                
                m_PartOfSpeech = PartOfSpeech.Noun;
                
                break;
                
            case 'v':
                
                m_PartOfSpeech = PartOfSpeech.Verb;
                
                break;

            case 'a':
                
                m_PartOfSpeech = PartOfSpeech.Adjective;
                
                break;

            case 's':
                
                m_PartOfSpeech = PartOfSpeech.AdjectiveSatellite;
                
                break;

            case 'r':
                
                m_PartOfSpeech = PartOfSpeech.Adverb;
                
                break;
                
            default:
                
                m_PartOfSpeech = PartOfSpeech.Noun;
                
            break;
        }
    }
    
    /**
     * This function returns the gloss of the synset.
     * @return The gloss for the synset
     */
    
    @Override
    public String getGloss()
    {
        return (m_strGloss);
    }
    
    /**
     * This function returns the POS of the synset.
     * @return The part of the speech for this synset.
     */
    
    public PartOfSpeech getPOS()
    {
        return (m_PartOfSpeech);
    }

    /**
     * This function return the collection of synset parents.
     * @return A set with the immediate parent synsets.
     */
    
    @Override
    public Set<IWordNetSynset> getParents()
    {
        // We create the set of parents
        
        HashSet<IWordNetSynset> parents = new HashSet<>();
        
        // We get the parents from the owner DB
        
        for (Long parentId: m_ParentsId)
        {
            parents.add(m_OwnerDB.getSynset(parentId));
        }
        
        // We return the results
        
        return (parents);
    }

    /**
     * This function returns the unique ID.
     * @return 
     */
    
    @Override
    public Long getID()
    {
        return (m_SynsetId);
    }
}
