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

package hesml_umls_benchmark.benchmarks;

import hesml_umls_benchmark.IUMLSBenchmark;
import hesml_umls_benchmark.SemanticLibraryType;
import hesml_umls_benchmark.semantclibrarywrappers.SimilarityLibraryFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import hesml_umls_benchmark.ISemanticLibrary;

/**
 * This class implements the abstract base class for all types of benchmarks
 * implemented by this program to compare the HESML, SML and UMLS::Similarity
 * semantic measures libraries.
 * @author j.lastra
 */

abstract class SemanticLibraryBenchmark implements IUMLSBenchmark
{
    /**
     * Wrappers of the semantic measure libraries being evaluated
     */
    
    protected ISemanticLibrary[]    m_Libraries;

    /**
     * UMLS and SNOMED-CT RF2 files
     */
    
    protected String    m_strSnomedDir;
    protected String    m_strMeShDir;
    protected String    m_strUmlsDir;
    protected String    m_strSnomedDBconceptFileName;
    protected String    m_strSnomedDBRelationshipsFileName;
    protected String    m_strSnomedDBdescriptionFileName;   
    protected String    m_strMeSHdescriptionFileName;   
    protected String    m_strSNOMED_CUI_mappingfilename;
   
    /**
     * Constructor to build the Snomed HESML database
     * @param libraries
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strSNOMED_CUI_mappingfilename
     * @throws Exception 
     */
    
    SemanticLibraryBenchmark(
            SemanticLibraryType[]    libraries,
            String                      strSnomedDir,
            String                      strSnomedDBconceptFileName,
            String                      strSnomedDBRelationshipsFileName,
            String                      strSnomedDBdescriptionFileName,
            String                      strUmlsDir,
            String                      strSNOMED_CUI_mappingfilename) throws Exception
    {
        // We save the SNOMED filenames
        
        m_strSnomedDir = strSnomedDir;
        m_strUmlsDir = strUmlsDir;
        m_strSnomedDBconceptFileName = strSnomedDBconceptFileName;
        m_strSnomedDBRelationshipsFileName = strSnomedDBRelationshipsFileName;
        m_strSnomedDBdescriptionFileName = strSnomedDBdescriptionFileName;
        m_strSNOMED_CUI_mappingfilename = strSNOMED_CUI_mappingfilename;
        
        // We load the SNOMED database and build its HESML taxonomy

        m_Libraries = new ISemanticLibrary[libraries.length];
        
        for (int i = 0; i < libraries.length; i++)
        {
            m_Libraries[i] = SimilarityLibraryFactory.getLibraryForSNOMED(
                                    libraries[i], m_strSnomedDir,
                                    m_strSnomedDBconceptFileName,
                                    m_strSnomedDBRelationshipsFileName,
                                    m_strSnomedDBdescriptionFileName,
                                    m_strUmlsDir, m_strSNOMED_CUI_mappingfilename);
        }
    }
    
    /**
     * Constructor to build the Snomed HESML database
     * @param libraries
     * @param strMeSHDir
     * @param strMeSHXmlFileName
     * @param strUmlsDir
     * @param strSNOMED_CUI_mappingfilename
     * @throws Exception 
     */
    
    SemanticLibraryBenchmark(
            SemanticLibraryType[]   libraries,
            String                  strMeSHDir,
            String                  strMeSHXmlFileName,
            String                  strUmlsDir,
            String                  strSNOMED_CUI_mappingfilename) throws Exception
    {
        // We save the SNOMED filenames
        
        m_strMeShDir = strMeSHDir;
        m_strUmlsDir = strUmlsDir;
        m_strMeSHdescriptionFileName = strMeSHXmlFileName;
        m_strSNOMED_CUI_mappingfilename = strSNOMED_CUI_mappingfilename;
        
        // We load the SNOMED database and build its HESML taxonomy

        m_Libraries = new ISemanticLibrary[libraries.length];
        
        for (int i = 0; i < libraries.length; i++)
        {
            m_Libraries[i] = SimilarityLibraryFactory.getLibraryForMeSH(
                                    libraries[i], m_strMeShDir,
                                    m_strMeSHdescriptionFileName,
                                    m_strUmlsDir, m_strSNOMED_CUI_mappingfilename);
        }
    }
    
    /**
     * This fucntion runs the benchmark and writes the results to the
     * output file.
     */
    
    @Override
    public abstract void run(String strOutputFilename) throws Exception;
    
    /**
     * This function releases the resouurces used by the benchmark.
     */
    
    @Override
    public void clear()
    {
        // We release all libraries
        
        for (ISemanticLibrary library: m_Libraries)
        {
            library.clear();
        }
    }
       
    /**
     * This function saves an output data matrix int oa CSV file
     * @param strDataMatrix 
     * @param strOutputFilename 
     */
    
    protected void WriteCSVfile(
            String[][]  strDataMatrix,
            String      strOutputFilename) throws IOException
    {
        // We create a writer for the text file
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(strOutputFilename, false));
        
        // We write the info for each taxonomy node
        
        char sep = ';';  // Separator dield
        
        for (int iRow = 0; iRow < strDataMatrix.length; iRow++)
        {
            // We initialize the line
            
            String strLine = "\n" + strDataMatrix[iRow][0];
            
            // We build the row
            
            for (int iCol = 1; iCol < strDataMatrix[0].length; iCol++)
            {
                strLine += (sep + strDataMatrix[iRow][iCol]);
            }
            
            // We write the line
            
            writer.write(strLine);
        }
        
        // We close the file
        
        writer.close();
    }
}
