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

package hesml.sts.documentreader.impl;

import java.util.ArrayList;
import java.util.Iterator;
import hesml.sts.documentreader.HSTSISentence;
import hesml.sts.documentreader.HSTSISentenceList;

/**
 * A HSTSSentenceList is an object for iterating between sentences.
 * @author Alicia Lara-Clares
 */

public class HSTSSentenceList implements HSTSISentenceList
{
    // We store a list of sentences.
    
    private final ArrayList<HSTSISentence> m_sentences;

    /**
     * Constructor by default
     */
    public HSTSSentenceList() 
    {
        // Initialize the sentences.
        
        this.m_sentences = new ArrayList<>();
    }
    
    /**
     * Add a new sentence to the list.
     * @param sentence 
     */
    
    @Override
    public void addSentence(
            HSTSISentence sentence)
    {
        m_sentences.add(sentence);
    }
    
    /**
     * Get the sentence by id
     * @param idSentence
     * @return 
     */
    
    @Override
    public HSTSISentence getSentence(
            int idSentence)
    {
        return (this.m_sentences.get(idSentence));
    }
    
    /**
     * Get the total of sentences.
     * @return 
     */
    
    @Override
    public int getCount()
    {
        return (this.m_sentences.size());
    }
    
    /**
     * Return the sentence iterator
     * @return Iterator<HSTSISentence>
     */
    
    @Override
    public Iterator<HSTSISentence> iterator() 
    {
        return (m_sentences.iterator());
    }
}
