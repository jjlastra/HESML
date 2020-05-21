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

import hesml_umls_benchmark.SnomedBasedLibraryType;
import hesml_umls_benchmark.ISemanticLibrary;

/**
 * This class implements a factory of SNOMED provider objects which
 * encapsulates a SNOMED-based semantic similarity measure.
 * @author j.lastra
 */

public class SnomedLibraryFactory
{
    /**
     * This fucntion creates a specific library wrapper.
     * @param library
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @return
     * @throws Exception 
     */
    
    public static ISemanticLibrary getLibrary(
            SnomedBasedLibraryType  libraryType,
            String                  strSnomedDir,
            String                  strSnomedDBconceptFileName,
            String                  strSnomedDBRelationshipsFileName,
            String                  strSnomedDBdescriptionFileName,
            String                  strUmlsDir,
            String                  strSNOMED_CUI_mappingfilename) throws Exception
    {
        // We initialize the output
        
        ISemanticLibrary library = null;
        
        // We cretae the warpper for each library being evaliated
        
        switch (libraryType)
        {
            case HESML:
                
                library = new HESMLSemanticLibraryWrapper(strSnomedDir,
                            strSnomedDBconceptFileName,
                            strSnomedDBRelationshipsFileName,
                            strSnomedDBdescriptionFileName,
                            strUmlsDir, strSNOMED_CUI_mappingfilename);
                
                break;
                
            case SML:
                
                library = new SMLSemanticLibraryWrapper(strSnomedDir,
                            strSnomedDBconceptFileName,
                            strSnomedDBRelationshipsFileName,
                            strSnomedDBdescriptionFileName,
                            strUmlsDir, strSNOMED_CUI_mappingfilename);
                
                break;
                
            case UMLS_SIMILARITY:
                
                library = new UMLSSemanticLibraryWrapper(strSnomedDir,
                            strSnomedDBconceptFileName,
                            strSnomedDBRelationshipsFileName,
                            strSnomedDBdescriptionFileName,
                            strUmlsDir, strSNOMED_CUI_mappingfilename);
                
                break;
        }
        
        // We return the result
        
        return (library);
    }
}
