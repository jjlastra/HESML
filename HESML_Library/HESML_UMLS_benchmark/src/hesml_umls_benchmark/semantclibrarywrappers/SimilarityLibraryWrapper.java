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

package hesml_umls_benchmark.semantclibrarywrappers;

import hesml.taxonomyreaders.mesh.IMeSHDescriptor;
import hesml_umls_benchmark.BiomedicalOntologyType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * This class sets the base class for all SNOMED providers.
 * @author j.lastra
 */

public abstract class SimilarityLibraryWrapper
{
    /**
     * SNOMED-CT RF2 filenames
     */
    
    protected String  m_strSnomedDir;
    protected String  m_strUmlsDir;
    protected String  m_strSnomedDBconceptFileName;
    protected String  m_strSnomedDBRelationshipsFileName;
    protected String  m_strSnomedDBdescriptionFileName;
    protected String  m_strUmlsCuiMappingFilename;
    
    /**
     * MeSH XML-based filenames
     */
    
    protected String    m_strMeSHDir;
    protected String    m_strMeSHXmlFilename;
    
    /**
     * Column offset for the main attributes EXTRACTED
     * from concept and
     * relationship files.
     */
    
    private static final int CONCEPT_ID = 0;
    private static final int ACTIVE_ID = 2;
    
    /**
     * Ontology type. Same wrapper is sued to load all ontologies
     */
    
    private final BiomedicalOntologyType  m_ontologyType;
       
    /**
     * Constructor to load SNOMED
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strUmlsCuiMappingFilename
     * @throws Exception 
     */

    SimilarityLibraryWrapper(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName,
            String  strUmlsDir,
            String  strUmlsCuiMappingFilename) throws Exception
    {
        // We save the SNOMED filenames
        
        m_ontologyType = BiomedicalOntologyType.SNOMEDCT_US;
        m_strSnomedDir = strSnomedDir;
        m_strUmlsDir = strUmlsDir;
        m_strSnomedDBconceptFileName = strSnomedDBconceptFileName;
        m_strSnomedDBRelationshipsFileName = strSnomedDBRelationshipsFileName;
        m_strSnomedDBdescriptionFileName = strSnomedDBdescriptionFileName;
        m_strUmlsCuiMappingFilename = strUmlsCuiMappingFilename;
        
        // We set to null the files related to MeSH ontology
        
        m_strMeSHDir = "";
        m_strMeSHXmlFilename = "";
    }
    
    /**
     * Constructor to load MeSH ontology
     * @param strMeSHDir 
     * @param strMeSHXmlFileName 
     * @param strUmlsDir
     * @param strUmlsCuiFilename
     * @throws Exception 
     */
    
    SimilarityLibraryWrapper(
            String  strMeSHDir,
            String  strMeSHXmlFileName,
            String  strUmlsDir,
            String  strUmlsCuiFilename) throws Exception
    {
        // We save the MeSH related filenames
        
        m_ontologyType = BiomedicalOntologyType.MeSH;
        m_strMeSHDir = strMeSHDir;
        m_strMeSHXmlFilename = strMeSHXmlFileName;
        m_strUmlsDir = strUmlsDir;
        m_strUmlsCuiMappingFilename = strUmlsCuiFilename;

        // We set the SNOMED filenames to blank
        
        m_strSnomedDir = "";
        m_strSnomedDBconceptFileName = "";
        m_strSnomedDBRelationshipsFileName = "";
        m_strSnomedDBdescriptionFileName = "";
    }

    /**
     * This function checks if the text is a Long number
     * @param strText
     * @return 
     */
    
    private static boolean isLongNumber(
        String  strText)
    {
        // We initialize the result
        
        boolean result = true;
        
        // We try to parse the number
        
        try
        {
            Long number = Long.parseLong(strText);
        }
        
        catch (Exception error)
        {
            result = false;
        }
        
        // We return the result
        
        return (result);
    }
    
    /**
     * This function retrieves all the SNOMED concepts ID from the SNOMED
     * concept file.
     * @return 
     */
    
    private static HashSet<Long> getSnomedIdSet(
            String  strSnomedDir,
            String  strConceptFile) throws FileNotFoundException
    {
        // We initialize the concept ID list
        
        HashSet<Long> concepts = new HashSet<>(360000);
        
        // We open the file for reading
        
        File info= new File(strSnomedDir + "/" + strConceptFile);
        Scanner reader = new Scanner(info);
        System.out.println("Reading SNOMED concept IDs " + info);
                
        // We skip the first line containing the headers.
        // We focus only on thereading of concept ID and term, because it
        // is the only information that we need. Thus, we reject
        // to read the full record for each concept.
        
        String strHeaderLine = reader.nextLine();
        
        // We read the concept lines
        
        do
        {
            // We extract the attribites of the concept

            String[] strAttributes = reader.nextLine().split("\t");

            // We get the needed attributes

            Long snomedId = Long.parseLong(strAttributes[CONCEPT_ID]);
            boolean active = strAttributes[ACTIVE_ID].equals("1");

            // We create a new concept if it is active

            if (active) concepts.add(snomedId);
            
        } while (reader.hasNextLine());
        
        // We close the database
        
        reader.close();
        
        // We return the result
        
        return (concepts);
    }

    /**
     * This function reads the CUI file and imports the mapping of CUI
     * to SNOMED IDs
     * @param strSnomedDir
     * @param strSnomedConceptIdfile
     * @param strUmlsDir
     * @param strCUIfile
     * @return
     * @throws FileNotFoundException 
     */
    
    public static HashMap<String, HashSet<Long>> readMappingCuiToSnomedIds(
            String  strSnomedDir,
            String  strSnomedConceptIdfile,
            String  strUmlsDir,
            String  strCUIfile) throws FileNotFoundException, IOException
    {
        // We create the output mapping table (CUI, SNOMED_id)
        
        HashMap<String, HashSet<Long>> outputCuiToSnomedConcepts = new HashMap<>();
        
        // We get all SNOMED concept IDs
        
        HashSet<Long> snomedIdSet = getSnomedIdSet(strSnomedDir, strSnomedConceptIdfile);
        
        // We open the file for reading
        
        File cuiConceptsFile = new File(strUmlsDir + "/" + strCUIfile);
        BufferedReader reader = new BufferedReader(new FileReader(cuiConceptsFile));
        
        System.out.println("Loading " + cuiConceptsFile);
                
        // We read the relationship lines
        
        String strLine = "";
        
        while((strLine = reader.readLine()) != null)
        {
            
            // We extract the attributes of the relationship
            
            String[] strColumns = strLine.split("\\|");
            
            // We look for the SNOMED tag
            
            for (int iCol = 0; iCol < strColumns.length; iCol++)
            {
                // We filer the rows with SNOMED-Ct mappings
                
                if (strColumns[iCol].equals("SNOMEDCT_US")
                        && isLongNumber(strColumns[iCol + 2]))
                {
                    // We get the mapping CUI -> SNOMED Id
                    
                    Long snomedId = Long.parseLong(strColumns[iCol + 2]);
                    String strUmlsCUI = strColumns[0];
                    
                    // We register the snomed concept associated to a given CUI
                    // only when the SNOMED ID exists with the aim of filtering
                    // those cases in which the SNOMED-CT concept could be inactive.

                    if (snomedIdSet.contains(snomedId)) 
                    {
                        HashSet<Long> snomedConcepts;

                        if (!outputCuiToSnomedConcepts.containsKey(strUmlsCUI))
                        {
                            snomedConcepts = new HashSet<>(1);
                            outputCuiToSnomedConcepts.put(strUmlsCUI, snomedConcepts);
                        }
                        else
                        {
                            snomedConcepts = outputCuiToSnomedConcepts.get(strUmlsCUI);
                        }

                        // We register the new snomed concept ID associated to the CUI

                        snomedConcepts.add(snomedId);
                    }
                    
                    break;
                }
            }
        }
        
        // We close the database an release the axuiliary resoruces
        
        reader.close();
        snomedIdSet.clear();
        
        // We return the result
        
        return (outputCuiToSnomedConcepts);
    }
    
    /**
     * This function reads the Xml-based MeSH descriptor file
     * @param strMeshDir
     * @param strMeshXmlDescriptorFilename
     * @return 
     */
    
    private static HashSet<String> getMeshIdsFromFile(
            String  strMeshDir,
            String  strMeshXmlDescriptorFilename)
            throws FileNotFoundException, XMLStreamException
    {
        // We build the filename
        
        String strMeshFilename = strMeshDir + "/" + strMeshXmlDescriptorFilename;
        
        // Debugging message
        
        System.out.println("Loading the MeSH ontology file = " + strMeshFilename);
        
        // We create the output set of MeSH descriptor
        
        HashSet<String> meshDescriptors = new HashSet<>();
        
        // We open the XML-based file
        
        XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(
                                            new FileInputStream(strMeshFilename));        
        
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
                
                meshDescriptors.add(parseChildNode(xmlStreamReader, "DescriptorUI"));
            }
        }
        
        // We close the document
        
        xmlStreamReader.close();
        
        // We return the result
        
        return (meshDescriptors);
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
    
    /**
     * This function reads the CUI file and imports the mapping of CUI
     * to SNOMED IDs
     * @param strMeSHDir
     * @param strMeSHConceptIdfile
     * @param strUmlsDir
     * @param strCUIfile
     * @return
     * @throws FileNotFoundException 
     */
    
    public static HashMap<String, HashSet<String>> readMappingCuiToMeSHIds(
            String  strMeSHDir,
            String  strMeSHConceptIdfile,
            String  strUmlsDir,
            String  strCUIfile) throws FileNotFoundException, IOException, XMLStreamException
    {
        // We create the output mapping table (CUI, SNOMED_id)
        
        HashMap<String, HashSet<String>> outputCuiToSnomedConcepts = new HashMap<>();
        
        // We get all MeSH concept IDs
        
        HashSet<String> meshIdSet = getMeshIdsFromFile(strMeSHDir, strMeSHConceptIdfile);
        
        // We open the file for reading
        
        File cuiConceptsFile = new File(strUmlsDir + "/" + strCUIfile);
        BufferedReader reader = new BufferedReader(new FileReader(cuiConceptsFile));
        
        System.out.println("Loading " + cuiConceptsFile);
                
        // We read the relationship lines
        
        String strLine;
        
        while ((strLine = reader.readLine()) != null)
        {
            // We extract the attributes of the relationship
            
            String[] strColumns = strLine.split("\\|");
            
            // We look for the MeSH tag
            
            for (int iCol = 0; iCol < strColumns.length; iCol++)
            {
                if (strColumns[iCol].equals("MSH"))
                {
                    // We define the MeSh concept set
                    
                    HashSet<String> meshConcepts = null;
                    
                    // We get the mapping CUI -> MeSH Id
                    
                    String strUmlsCui = strColumns[0];
                    String meshDescriptorId = strColumns[iCol - 1];
                    
                    // We register the snomed concept associated to a given CUI
                    
                    if (!outputCuiToSnomedConcepts.containsKey(strUmlsCui))
                    {
                        meshConcepts = new HashSet<>(1);
                        outputCuiToSnomedConcepts.put(strUmlsCui, meshConcepts);
                    }
                    else
                    {
                        meshConcepts = outputCuiToSnomedConcepts.get(strUmlsCui);
                    }
                    
                    // We only register the SNOMED concept if it exists in the
                    // MeSH ontology. There could be some cases in which a
                    // MeSH concept associated to a CUI is not aalready active,
                    // and thus, it will not be in the database
                    
                    if (meshIdSet.contains(meshDescriptorId)) meshConcepts.add(meshDescriptorId);
                    
                    break;
                }
            }
        }
        
        // We close the database an release the axuiliary resoruces
        
        reader.close();
        meshIdSet.clear();
        
        // We return the result
        
        return (outputCuiToSnomedConcepts);
    }
}

