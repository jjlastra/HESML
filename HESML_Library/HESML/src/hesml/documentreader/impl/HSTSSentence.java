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

import hesml.sts.documentreader.HSTSISentence;

/**
 * This class implements a HSTS sentence object.
 * 
 * @author Alicia Lara-Clares
 */

class HSTSSentence implements HSTSISentence
{  
    // A sentence belongs to a document
    
    private final int m_idDocument;
    
    // A sentence belongs to a paragraph
    
    private final int m_idParagraph;
    
    // The sentence has an id sentence.
    
    private final int m_idSentence;
    
    // Text of the sentence.
    
    private String m_text;

    /**
     * HSTS sentence constructor
     * This class initializes the internal variables.
     * @param idSentence
     * @param idParagraph
     * @param idDocument
     * @param text 
     */
    
    HSTSSentence(
            int     idSentence, 
            int     idParagraph, 
            int     idDocument, 
            String  text) 
    {
        // Initialize the internal variables
        
        this.m_idSentence = idSentence;
        this.m_idParagraph = idParagraph;
        this.m_idDocument = idDocument;
        this.m_text = text;
    }
    
    /**
     * Set the text to the sentence
     * @param text 
     */
    
    @Override
    public void setText(
            String text)
    {
        m_text = text;
    }
    
    /**
     * Get the sentence text
     * @param text 
     */
    
    @Override
    public String getText()
    {
        return (m_text);
    }

    /**
     * Return the id sentence.
     * 
     * @return int id sentence
     */
    
    @Override
    public int getIdSentence() {
        return (m_idSentence);
    }

    /**
     * Return the id paragraph
     * @return 
     */
    
    @Override
    public int getIdParagraph() {
        return (m_idParagraph);
    }

    /**
     * Return the id document
     * @return 
     */
    
    @Override
    public int getIdDocument() {
        return (m_idDocument);
    }
}
