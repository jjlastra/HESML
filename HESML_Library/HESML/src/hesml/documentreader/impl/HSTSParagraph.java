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

import hesml.sts.documentreader.HSTSIParagraph;
import hesml.sts.documentreader.HSTSISentenceList;

/**
 * This functions implements a paragraph object.
 * A document has a list of paragraph.
 * Each paragraph has a list of sentences.
 * 
 * @author Alicia Lara-Clares
 */

class HSTSParagraph implements HSTSIParagraph
{
    // Each paragraph is identified by an id
    
    private final int m_idParagraph;
    
    // Each paragraph belongs to a document
    
    private final int m_idDocument;
    
    // Each paragraph has a list of sentences.
    
    private HSTSISentenceList m_sentenceList;
    
    // The text of a paragraph
    
    private String m_text;
   
    /**
     * Creates an object document and extract the sentences.
     * @param strDocumentPath Path to the document.
     * @param documentType Type of input document.
     */
    
    HSTSParagraph(
            int idParagraph, 
            int idDocument) 
    {
        // Initialize the variables.
        
        m_idParagraph = idParagraph;
        m_idDocument = idDocument;
        m_sentenceList = new HSTSSentenceList();
        m_text = "";
    }
    
    /**
     * Read the paragraphs from the document
     * @return ArrayList<IParagraphList> the list of paragraphs in the document.
     */
    
    @Override
    public HSTSISentenceList getSentenceList()
    {
        return (m_sentenceList);
    }
    
    /**
     * Add sentences in the paragraph
     * @return ArrayList<ISentenceList> the list of sentences in the paragraph.
     * @todo IMPLEMENTAR - ojo, controlar que se intente insertar un sent que ya existia.
     */
    
    @Override
    public void setSentenceList(
            HSTSISentenceList sentenceList)
    {
        m_sentenceList = sentenceList;
    }
    
    /**
     * Add a text to the paragraph
     * @param text 
     */
    
    @Override
    public void setText(String text)
    {
        m_text = text;
    }
    
    /**
     * Get the paragraph text
     * @param text 
     */
    
    @Override
    public String getText()
    {
        return (m_text);
    }
    
    /**
     * Get the document id
     * @param id document 
     */
    
    @Override
    public int getIdDocument()
    {
        return (m_idDocument);
    }
    
    /**
     * Get the paragraph id
     * @return int
     */

    @Override
    public int getId() {
        return (m_idParagraph);
    }
}
