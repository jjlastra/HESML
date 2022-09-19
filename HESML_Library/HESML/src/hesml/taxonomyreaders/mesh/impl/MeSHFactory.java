/*
 * Copyright (C) 2016-2022 Universidad Nacional de Educación a Distancia (UNED)
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
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

/**
 * This class loads a MeSH ontology in memory.
 * @author Juan J. Lastra-Díaz <jlastra@invi.uned.es>
 */

public class MeSHFactory
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
        return (MeSHreader.loadMeSHOntology(strMeSHXmlDescriptorFilename,
                strUmlsCUImappingFilename));
    }
}
