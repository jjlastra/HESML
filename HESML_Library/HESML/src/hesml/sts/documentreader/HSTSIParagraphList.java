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

package hesml.sts.documentreader;

/**
 * This interface encapsulates the creation of a paragraphs iterable object.
 * @author Alicia Lara-Clares
 */

public interface HSTSIParagraphList extends Iterable<HSTSIParagraph> 
{
    /**
     * This function returns the number of paragraphs.
     * @return int number of documents.
     */
    
    int getCount();
    
    /**
     * This function returns the paragraph object by its id.
     * @param idParagraph id of the document.
     * @return idParagraph document object.
     */
    
    HSTSIParagraph getParagraph(
            int idParagraph);
    
    /**
     * Add a paragraph to the object.
     * @param paragraph
     */
    
    void addParagraph(
            HSTSIParagraph paragraph);
}
