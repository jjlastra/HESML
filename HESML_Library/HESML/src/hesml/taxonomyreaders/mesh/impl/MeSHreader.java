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
 */

package hesml.taxonomyreaders.mesh.impl;

import hesml.taxonomyreaders.mesh.IMeSHOntology;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.SAXException;

/**
 * This class parses the MeSH descriptor file in XML file format.
 * @author Juan J. Lastra-Díaz <jlastra@invi.uned.es>
 */

class MeSHreader
{
    /**
     * This function loads a MeSH ontology in memory.
     * @param strMeSHXmlDescriptorFilename
     * @return 
     */
    
    public static IMeSHOntology loadMeSHOntology(
            String  strMeSHXmlDescriptorFilename,
            String  strUmlsCUImappingFilename) throws IOException,
                                                SAXException,
                                                ParserConfigurationException,    
                                                XMLStreamException,    
                                                Exception    
    {
        // Debugging message
        
        System.out.println("Loading the MeSH ontology file = " + strMeSHXmlDescriptorFilename);
        
        // We create a blank MeSH ontology
        
        MeSHOntology meshOntology = new MeSHOntology();
        
        // We open the XML-based file
        
        XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(
                                            new FileInputStream(strMeSHXmlDescriptorFilename));        
        
        // We parse the document 
        
        while(xmlStreamReader.hasNext())
        {
            // We read to the next element
            
            xmlStreamReader.next();
            
            // We detect the staring position of the DescriptorRecord
            
            if((xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT)
                    && (xmlStreamReader.getLocalName().equals("DescriptorRecord")))
            {
                // We extract the three main attributes of the MeSH descriptor
                
                String strDescriptorId = parseChildNode(xmlStreamReader, "DescriptorUI");
                String[] strTreeNodes = parseTreeNodes(xmlStreamReader);
                String strPreferredName = parsePreferredName(xmlStreamReader);
                
                // We insert the concept into the MeSH ontology
                
                meshOntology.addDescriptor(strDescriptorId, strPreferredName, strTreeNodes);
            }
        }
        
        // We close the document
        
        xmlStreamReader.close();
        
        // We load the CUI mapping
        
        meshOntology.readConceptsUmlsCUIs(strUmlsCUImappingFilename);
        
        // We build the taxonomy
        
        meshOntology.buildOntology(true);
        
        // We return the result
        
        return (meshOntology);
    }
    
    /**
     * This function retrieves the descriptor keyname.
     * @param xmlStreamReader
     * @return 
     */
    
    private static String[] parseTreeNodes(
        XMLStreamReader xmlStreamReader) throws XMLStreamException
    {
        // We create an auxiliary set
        
        HashSet<String> parsingTreeNodes = new HashSet<>();
                
        // We parse the descriptor record 
        
        while(xmlStreamReader.hasNext())
        {
            // We read to the next element
            
            xmlStreamReader.next();
            
            // We detect the staring position of the Tree Number List
            
            if((xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT)
                    && (xmlStreamReader.getLocalName().equals("TreeNumberList")))
            {
                // We read all tree numbers
                
                do
                {
                    // We read next element
                    
                    xmlStreamReader.next();
                    
                    if((xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT)
                        && xmlStreamReader.getLocalName().equals("TreeNumber"))
                    {
                        parsingTreeNodes.add(xmlStreamReader.getElementText());
                    }
                    
                } while ((xmlStreamReader.getEventType() != XMLStreamReader.END_ELEMENT)
                    || !xmlStreamReader.getLocalName().equals("TreeNumberList"));
                
                // We exit from the loop once all tree nodes have been parsed
                
                break;
            }
        }
        
        // We create the output vector
        
        String[] strTreeNodes = new String[parsingTreeNodes.size()];
        
        parsingTreeNodes.toArray(strTreeNodes);
        parsingTreeNodes.clear();
        
        // We return the result
        
        return (strTreeNodes);
    }

    /**
     * This function retrieves the preferred name of a MeSH descriptor
     * @param xmlStreamReader
     * @return 
     */
    
    private static String parsePreferredName(
        XMLStreamReader xmlStreamReader) throws XMLStreamException
    {
        // We initialize the output
        
        String strPreferredName = "";
        
        // We parse the descriptor record 
        
        while(xmlStreamReader.hasNext())
        {
            // We read to the next element
            
            xmlStreamReader.next();
            
            // We detect the staring position of the Tree Number List
            
            if((xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT)
                    && xmlStreamReader.getLocalName().equals("Concept")
                    && (xmlStreamReader.getAttributeCount() == 1)
                    && xmlStreamReader.getAttributeLocalName(0).equals("PreferredConceptYN")
                    && xmlStreamReader.getAttributeValue(0).equals("Y"))
            {
                // We skip the cursor until the "ConceptName " element
                
                do
                {
                    // We read the following element

                    xmlStreamReader.next();

                    if ((xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT)
                            && xmlStreamReader.getLocalName().equals("ConceptName"))
                    {
                        strPreferredName = parseChildNode(xmlStreamReader, "String");
                        break;
                    }

                } while ((xmlStreamReader.getEventType() != XMLStreamReader.END_ELEMENT)
                    || !xmlStreamReader.getLocalName().equals("Concept"));
                
                // We exit from the parsing loop
                
                break;
            }
        }
        
        // We return the result
        
        return (strPreferredName);
    }
    
    /**
     * This function reads a child element of the current parsed element.
     * @param xmlStreamReader
     * @param strChildElementName
     * @return
     * @throws XMLStreamException 
     */
    
    private static String parseChildNode(
            XMLStreamReader xmlStreamReader,
            String          strChildElementName) throws XMLStreamException
    {
        // We initialize the output
        
        String strChildNodeValue = "";
        
        // We parse the descriptor record 
        
        while(xmlStreamReader.hasNext())
        {
            // We read to the next element
            
            xmlStreamReader.next();
            
            // We detect the staring position of the Tree Number List
            
            if((xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT)
                    && (xmlStreamReader.getLocalName().equals(strChildElementName)))
            {
                strChildNodeValue = xmlStreamReader.getElementText();
                break;
            }
        }
        
        // We return the result
        
        return (strChildNodeValue);
    }   
}
