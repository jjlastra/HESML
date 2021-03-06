/*
 * Copyright (C) 2016-2021 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml.taxonomyreaders.snomed.impl;

import hesml.taxonomyreaders.snomed.ISnomedCtOntology;

/**
 * This class implements the fucntions to load a SNOMED-CT database
 * and building its taxonomy.
 * @author j.lastra
 */

public class SnomedCtFactory
{
    /**
     * This function loads a SNOMED-CT database
     * @param strSnomedDir Direcory containg the SNOMED files
     * @param strSnomedDBconceptFileName RF2 file containing the SNOMED concepts
     * @param strSnomedDBRelationshipsFileName Relationships between concepts
     * @param strSnomedDBdescriptionFileName Terms of the concepts
     * @param strUmlsDir Direcyory containing the UMLS CUI mapping file
     * @param strUmlsCuiMappingfilename Mapping UMLS-CUI to SNOMED IDs
     * @return The loaded WordnNetDB
     * @throws java.lang.Exception Unexpected error
     */
    
    public static ISnomedCtOntology loadSnomedDatabase(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName,
            String  strUmlsDir,
            String  strUmlsCuiMappingfilename) throws Exception
    {
        return (SnomedDbReader.loadOntology(strSnomedDir,
                strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName,
                strUmlsDir, strUmlsCuiMappingfilename));
    }   
}
