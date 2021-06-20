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
import hesml.sts.documentreader.HSTSIParagraph;
import hesml.sts.documentreader.HSTSIParagraphList;

/**
 * Iterable object for storing a list of paragraph objects.
 * 
 * @author Alicia Lara-Clares
 */

public class HSTSParagraphList implements HSTSIParagraphList
{
    // This object has a list of paragraphs.
    
    private final ArrayList<HSTSIParagraph> m_paragraphs;
    
    /**
     * Paragraph list constructor
     */
    
    HSTSParagraphList()
    {
        // Initialize the mapping table in blank.
        
        m_paragraphs = new ArrayList<>();
    }
    
    /**
     * This function returns the number of documents existing.
     * @return int number of documents.
     */
    
    @Override
    public int getCount()
    {
        return (m_paragraphs.size());
    }
    
    /**
     * This function returns the Document object by its id.
     * @param idParagraph id of the document.
     * @return idParagraph document object.
     */
    
    @Override
    public HSTSIParagraph getParagraph(
            int idParagraph)
    {
        return (m_paragraphs.get(idParagraph));
    }
    
    /**
     * Add a paragraph to the list
     * @param paragraph 
     */
    
    @Override
    public void addParagraph(
            HSTSIParagraph paragraph)
    {
        m_paragraphs.add(paragraph);
    }
    
    /**
     * Return the iterator
     * @return Iterator<HSTSIParagraph>
     */
    
    @Override
    public Iterator<HSTSIParagraph> iterator() 
    {
        return (m_paragraphs.iterator());
    }
}
