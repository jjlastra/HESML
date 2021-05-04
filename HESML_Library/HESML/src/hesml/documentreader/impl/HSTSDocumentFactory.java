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

import hesml.sts.documentreader.HSTSDocumentType;
import hesml.sts.documentreader.HSTSIDocument;
import hesml.sts.preprocess.IWordProcessing;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;

/**
 * This class is responsible of creating the input Documents readers and writers
 * @author Alicia Lara-Clares
 */

public class HSTSDocumentFactory 
{
    /**
     * This function load a file and creates an HSTSIDocument object 
     * filling the paragraphs and sentences.
     * 
     * @param iDocument
     * @param fileInput
     * @param documentType
     * @param preprocesser
     * @return
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    
    public static HSTSIDocument loadDocument(
            int                 iDocument,
            File                fileInput,
            HSTSDocumentType    documentType,
            IWordProcessing     preprocesser) 
            throws FileNotFoundException, XMLStreamException
    {
        // We initialize the output document
        HSTSIDocument document = null;
        
        if(documentType == HSTSDocumentType.BioCXMLUnicode)
        {
            // Initialize the reader
            
            BioCReader biocReader = new BioCReader(iDocument, fileInput, preprocesser);
            
            // Read the bioc file
            
            document = biocReader.readFile();
            
            // Clean the internal variables.
            
            biocReader.clean();
        }

        // We return the result
        
        return (document);
    }
    
    
    /**
     * This function encapsulates the writing of 
     * sentences from an HSTSIDocument object to a file.
     * 
     * @param document
     * @param fileOutput
     * @throws IOException
     */
    
    public static void writeSentencesToFile(
            HSTSIDocument document,
            File fileOutput) throws IOException
    {
        document.saveSentencesToFile(fileOutput);
    }
}