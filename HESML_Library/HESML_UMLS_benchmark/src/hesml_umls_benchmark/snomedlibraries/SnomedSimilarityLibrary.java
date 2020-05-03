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

package hesml_umls_benchmark.snomedlibraries;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * This class sets the base class for all SNOMED providers.
 * @author j.lastra
 */

public abstract class SnomedSimilarityLibrary
{
    /**
     * SNOMED-CT RF2 files
     */
    
    protected String  m_strSnomedDir;
    protected String  m_strUmlsDir;
    protected String  m_strSnomedDBconceptFileName;
    protected String  m_strSnomedDBRelationshipsFileName;
    protected String  m_strSnomedDBdescriptionFileName;
    protected String  m_strUmlsCuiMappingFilename;
    
    /**
     * Column offset for the main attributes EXTRACTED
     * from concept and
     * relationship files.
     */
    
    private static final int CONCEPT_ID = 0;
    private static final int ACTIVE_ID = 2;
       
    /**
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsDir
     * @param strUmlsCuiMappingFilename
     * @throws Exception 
     */

    SnomedSimilarityLibrary(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName,
            String  strUmlsDir,
            String  strUmlsCuiMappingFilename) throws Exception
    {
        // We save the SNOMED filenames
        
        m_strSnomedDir = strSnomedDir;
        m_strUmlsDir = strUmlsDir;
        m_strSnomedDBconceptFileName = strSnomedDBconceptFileName;
        m_strSnomedDBRelationshipsFileName = strSnomedDBRelationshipsFileName;
        m_strSnomedDBdescriptionFileName = strSnomedDBdescriptionFileName;
        m_strUmlsCuiMappingFilename = strUmlsCuiMappingFilename;
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
     * @param strConceptIdfile
     * @param strUmlsDir
     * @param strCUIfile
     * @return
     * @throws FileNotFoundException 
     */
    
    public static HashMap<String, HashSet<Long>> readConceptsUmlsCUIs(
            String  strSnomedDir,
            String  strConceptIdfile,
            String  strUmlsDir,
            String  strCUIfile) throws FileNotFoundException
    {
        // We create the output mapping table (CUI, SNOMED_id)
        
        HashMap<String, HashSet<Long>> outputCuiToSnomedConcepts = new HashMap<>();
        
        // We get all SNOMED concept IDs
        
        HashSet<Long> snomedIdSet = getSnomedIdSet(strSnomedDir, strConceptIdfile);
        
        // We open the file for reading
        
        File cuiConceptsFile = new File(strUmlsDir + "/" + strCUIfile);
        Scanner reader = new Scanner(cuiConceptsFile);
        System.out.println("Loading " + cuiConceptsFile);
                
        // We read the relationship lines
        
        do
        {
            // We read the next relationship entry
            
            String strLine = reader.nextLine();
            
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
            
        } while (reader.hasNextLine());
        
        // We close the database an release the axuiliary resoruces
        
        reader.close();
        snomedIdSet.clear();
        
        // We return the result
        
        return (outputCuiToSnomedConcepts);
    }
}

