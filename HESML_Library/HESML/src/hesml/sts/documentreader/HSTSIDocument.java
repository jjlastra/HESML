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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Interface that encapsulates the creation of a Document.
 * @author Alicia Lara-Clares
 */

public interface HSTSIDocument 
{   
    /**
     * This function extracts the paragraphs from the document
     * @return HSTSIParagraphList
     */
    
    HSTSIParagraphList getParagraphList();
    
    /**
     * Set a ParagraphList to the HSTSIDocument object.
     * @param paragraphList
     */
    
    void setParagraphList(
            HSTSIParagraphList paragraphList);
    
    /**
     * Preprocess the document and store in the object.
     * @throws java.io.FileNotFoundException
     * @throws java.lang.InterruptedException
     */
    
    void preprocessDocument() 
            throws FileNotFoundException, IOException, InterruptedException, Exception;

    /**
     * Save sentences for each document to an output file.
     * @param fileOutput
     * @throws IOException
     */
    
    void saveSentencesToFile(
            File fileOutput) throws IOException;
}
