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

import hesml_umls_benchmark.SemanticLibraryType;
import hesml_umls_benchmark.ISemanticLibrary;

/**
 * This class implements a factory of similarity library wrappers.
 * @author j.lastra
 */

public class SimilarityLibraryFactory
{
    /**
     * This fucntion creates a specific library wrapper to load SNOMED-CT ontology
     * @param library
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @return
     * @throws Exception 
     */
    
    public static ISemanticLibrary getLibraryForSNOMED(
            SemanticLibraryType libraryType,
            String              strSnomedDir,
            String              strSnomedDBconceptFileName,
            String              strSnomedDBRelationshipsFileName,
            String              strSnomedDBdescriptionFileName,
            String              strUmlsDir,
            String              strUmlsCuiFilename) throws Exception
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
                            strUmlsDir, strUmlsCuiFilename);
                
                break;
                
            case SML:
                
                library = new SMLSemanticLibraryWrapper(strSnomedDir,
                            strSnomedDBconceptFileName,
                            strSnomedDBRelationshipsFileName,
                            strSnomedDBdescriptionFileName,
                            strUmlsDir, strUmlsCuiFilename);
                
                break;
                
            case UMLS_SIMILARITY:
                
                library = new UMLSSemanticLibraryWrapper(strSnomedDir,
                            strSnomedDBconceptFileName,
                            strSnomedDBRelationshipsFileName,
                            strSnomedDBdescriptionFileName,
                            strUmlsDir, strUmlsCuiFilename);
                
                break;
        }
        
        // We return the result
        
        return (library);
    }
    
    /**
     * This fucntion creates a specific library wrapper to load MeSH ontology
     * @param library
     * @param strMeSHDir
     * @param strMeSHXmlFileName
     * @param strUmlsDir
     * @param strUmlsCuiFilename
     * @return
     * @throws Exception 
     */
    
    public static ISemanticLibrary getLibraryForMeSH(
            SemanticLibraryType libraryType,
            String              strMeSHDir,
            String              strMeSHXmlFileName,
            String              strUmlsDir,
            String              strUmlsCuiFilename) throws Exception
    {
        // We initialize the output
        
        ISemanticLibrary library = null;
        
        // We cretae the warpper for each library being evaliated
        
        switch (libraryType)
        {
            case HESML:
                
                library = new HESMLSemanticLibraryWrapper(strMeSHDir,
                                strMeSHXmlFileName, strUmlsDir,
                                strUmlsCuiFilename);
                
                break;
                
            case SML:
                
                library = new SMLSemanticLibraryWrapper(strMeSHDir,
                                strMeSHXmlFileName, strUmlsDir,
                                strUmlsCuiFilename);
                
                break;
                
            case UMLS_SIMILARITY:
                
                library = new UMLSSemanticLibraryWrapper(strMeSHDir,
                                strMeSHXmlFileName, strUmlsDir,
                                strUmlsCuiFilename);
                
                break;
        }
        
        // We return the result
        
        return (library);
    }
    
    /**
     * Thius function creates an instance of a library wrapper to load
     * the Gene Ontology (GO).
     * @param libraryType
     * @param strOboGeneOntologyFilename
     * @return 
     */
    
    public static ISemanticLibrary getLibraryForGO(
            SemanticLibraryType libraryType,
            String              strOboGeneOntologyFilename) throws Exception
    {
        // We initialize the output
        
        ISemanticLibrary library = null;
        
        // We cretae the warpper for each library being evaliated
        
        switch (libraryType)
        {
            case HESML:
                
                library = new HESMLSemanticLibraryWrapper(strOboGeneOntologyFilename);
                
                break;
                
            case SML:
                
                library = new SMLSemanticLibraryWrapper(strOboGeneOntologyFilename);
                
                break;
                
            case UMLS_SIMILARITY:

                throw (new UnsupportedOperationException("UMLS_SIMILARITY does not implement GO ontology"));
        }
        
        // We return the result
        
        return (library);
    }
}
