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

package hesml.taxonomyreaders.obo.impl;

import hesml.taxonomyreaders.obo.IOboOntology;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * Reader of OBO ontology files
 * @author j.lastra
 */

class OboReader
{
    /**
     * This function loads an OBO ontology.
     * @param strOboFilename
     * @return 
     */
    
    static IOboOntology loadOntology(
            String  strOboFilename) throws FileNotFoundException, IOException, Exception
    {
        // Warning message
        
        System.out.println("Loading OBO ontology file = " + strOboFilename);
        
        // We cretae an OBO ontology in blank
        
        OboOntology ontology = null;
        
        // We create the OBO reader
        
        BufferedReader reader = new BufferedReader(new FileReader(strOboFilename));
        
        // We set the tag for the ontology name
        
        String tagOntologyName = "ontology:";
        String tagDefaultNamespace = "default-namespace:";
        
        // Global names
        
        String strDefaultNamespace = "";
        
        // We parse the concepts contained in the file
        
        String strLine;
        
        while ((strLine = reader.readLine()) != null)
        {
            // We detect a concept block
            
            if (strLine.startsWith(tagOntologyName))
            {
                // We extract the ontology name and cretae the ontology
                
                String strOntologyName = strLine.substring(tagOntologyName.length()).trim();
                ontology = new OboOntology(strOntologyName);
            }
            else if (strLine.startsWith(tagDefaultNamespace))
            {
                strDefaultNamespace = strLine.substring(tagDefaultNamespace.length()).trim();
            }
            else if (strLine.startsWith("[Term]"))
            {
                parseTerm(reader, ontology, strDefaultNamespace);
            }
        }
        
        // We close the file
        
        reader.close();
        
        // We build the taxonomies
        
        ontology.buildTaxonomies();
        ontology.checkTopology();
        
        // We return the result
        
        return (ontology);
    }
    
    /**
     * This function parses the input OBO file to extract the concept data.
     * @param reader 
     */
    
    private static void parseTerm(
            BufferedReader  reader,
            OboOntology     ontology,
            String          strDefaultNamespace) throws IOException, Exception
    {
        // We initilize the attributes to be parsed
        
        String  strId = "";
        String  strName = "";
        String  strNamespace = strDefaultNamespace;
        
        HashSet<String> parentsId = new HashSet<>();
        
        // We set the tag names
        
        String  tagNamespace = "namespace:";
        String  tagId = "id:";
        String  tagName = "name:";
        String  tagIsA = "is_a:";
        String  tagObsolete = "is_obsolete:";
        boolean isObsolete = false;
        
        // We parse all term fields
        
        String strLine;
        
        while (!(strLine = reader.readLine()).equals(""))
        {
            if (strLine.startsWith(tagId))
            {
                strId = strLine.substring(tagId.length()).trim();
            }
            else if (strLine.startsWith(tagName))
            {
                strName = strLine.substring(tagName.length()).trim();
            }
            else if (strLine.startsWith(tagNamespace))
            {
                strNamespace = strLine.substring(tagNamespace.length()).trim();
            }
            else if (strLine.startsWith(tagIsA))
            {
                String strGoId = strLine.substring(tagIsA.length()).trim();
                
                String[] strFields = strGoId.split("\\s+");
                
                parentsId.add(strFields[0]);
            }
            else if (strLine.startsWith(tagObsolete))
            {
                String value = strLine.substring(tagObsolete.length()).trim();
                
                isObsolete = value.equals("true");
            }
        }
        
        // We check that the concept is no obsolete
        
        if (!isObsolete)
        {
            // We create the parents vector

            String[] strParentIds = new String[parentsId.size()];

            parentsId.toArray(strParentIds);
            parentsId.clear();

            // We create a new concept

            ontology.addConcept(strId, strNamespace, strName, strParentIds);
        }
    }
}
