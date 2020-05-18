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

import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml_umls_benchmark.ISnomedSimilarityLibrary;
import hesml_umls_benchmark.SnomedBasedLibraryType;
import hesml_umls_benchmark.LibraryType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * * This class implements the SNOMED similarity library based on UMLS::Similarity.
 * @author alicia
 */

public class UMLSSimilarityLibrary extends SnomedSimilarityLibrary
        implements ISnomedSimilarityLibrary
{
    /**
     * IC model and measure type evaluated by the library
     */
    
    private IntrinsicICModelType    m_icModel;
    private SimilarityMeasureType   m_measureType;
    
    /**
     * Directory used to create the Perl script which calls the 
     * UMLS:Similarity libray
     */
    
    private final String    m_PerlScriptDir;
    private final String    m_PerlTempDir;
    
    /**
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsCuiMappingFilename
     * @throws Exception 
     */
    
    UMLSSimilarityLibrary(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName,
            String  strUmlsDir,
            String  strUmlsCuiMappingFilename) throws Exception
    {
        // Inicializamos la clase base
        
        super(strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName,
                strUmlsDir, strUmlsCuiMappingFilename);
        
        // We obtain the temporary directory used for the evaluation script
        
        m_PerlScriptDir = new File("./UMLS_Similarity_Perl").getCanonicalPath();
        
        // We obtain the directory for temporary files
        
        m_PerlTempDir = System.getProperty("java.io.tmpdir");
    }
    
    /**
     * This function calculates the degree of similairity for each concept pairs.
     * In addition, this function returns the running time in seconds for each
     * independent evaluation.
     * @param umlsCuiPairs
     * @param libraryType
     * @return
     * @throws java.io.FileNotFoundException
     * @throws Exception 
     * @throws java.lang.InterruptedException 
     */
    
    public ArrayList<String> getCUIsSimilaritiesAndRunningTimes(
            String[][]  umlsCuiPairs,
            LibraryType libraryType) throws FileNotFoundException, IOException,
                                        InterruptedException, Exception 
    {
        // Initialize the result
        
        ArrayList<String> result = new ArrayList<>();
        
        // We write the temporal file with all the CUI pairs
        
        String tempFile = m_PerlTempDir + "/tempFile.csv";
        
        // We write the input file for the Perl script
        
        writeCSVfile(umlsCuiPairs, tempFile);
        
        // Get the measure as Perl script input format

        String measure = convertHesmlMeasureTypeToUMLS_Sim(m_measureType);

        // We execute the Perl script
        
        executePerlScript(measure, libraryType);
        
        // We read the output from the Perl script.
        // Each row has the following format: CUI1 | CUI2 | similarity | time
        
        BufferedReader csvReader = new BufferedReader(new FileReader(m_PerlTempDir + "/tempFileOutput.csv"));
        
        for (int i = 0; i < umlsCuiPairs.length; i++)
        {
            // We read the next output line and retrieve
            
            String data = csvReader.readLine();
            result.add(data);
        }
        
        csvReader.close();
        
        // Return the result
        
        return (result);
    }
    
    /**
     * This function calculates the degre of similairity for each concept pairs.
     * In addition, this function returns the running time in seconds for each
     * independent evaluation.
     * @param umlsCuiPairs
     * @return
     * @throws java.io.FileNotFoundException
     * @throws Exception 
     * @throws java.lang.InterruptedException 
     */
    
    public double[][] getSimilaritiesAndRunningTimes(
            String[][]  umlsCuiPairs,
            LibraryType libraryType) throws FileNotFoundException, IOException,
                                        InterruptedException, Exception 
    {
        // Initialize the result
        
        double[][] similarity = new double[umlsCuiPairs.length][2];
        
        // We write the temporal file with all the CUI pairs
        
        String tempFile = m_PerlTempDir + "/tempFile.csv";
        
        // We write the input file for the Perl script
        
        writeCSVfile(umlsCuiPairs, tempFile);
        
        // Get the measure as Perl script input format

        String measure = convertHesmlMeasureTypeToUMLS_Sim(m_measureType);

        // We execute the Perl script
        
        executePerlScript(measure, libraryType);
        
        // We read the output from the Perl script.
        // Each row has the following format: CUI1 | CUI2 | similarity | time
        
        BufferedReader csvReader = new BufferedReader(new FileReader(m_PerlTempDir + "/tempFileOutput.csv"));
        
        for (int i = 0; i < umlsCuiPairs.length; i++)
        {
            // We read the next output line and retrieve
            
            String[] data = csvReader.readLine().split(",");
            
            // We read the degree of similarity and running time
            
            similarity[i][0] = Double.valueOf(data[2]);
            similarity[i][1] = Double.valueOf(data[3]);
        }
        
        csvReader.close();
        
        // Return the result
        
        return (similarity);
    }
    
    /**
     * This function converts a HESML similarity measure type to the
     * closest measure type in UMLS::Similarity.
     * @param hesmlMeasureType
     * @return
     * @throws Exception 
     */
    
    private String convertHesmlMeasureTypeToUMLS_Sim(
            SimilarityMeasureType   hesmlMeasureType) throws Exception
    {
        // We fill a hasmMap with the conversion
        
        HashMap<SimilarityMeasureType, String> conversionMap = new HashMap<>();
        
        // We fill the conversion map
        
        conversionMap.put(SimilarityMeasureType.Lin, "lin");
        conversionMap.put(SimilarityMeasureType.Resnik, "res");
        conversionMap.put(SimilarityMeasureType.JiangConrath, "jcn");
        conversionMap.put(SimilarityMeasureType.CosineNormJiangConrath, "jcn");
        conversionMap.put(SimilarityMeasureType.CosineNormWeightedJiangConrath, "jcn");
        conversionMap.put(SimilarityMeasureType.WeightedJiangConrath, "jcn");
        conversionMap.put(SimilarityMeasureType.FaITH, "faith");
        conversionMap.put(SimilarityMeasureType.WuPalmer, "wup");
        conversionMap.put(SimilarityMeasureType.WuPalmerFast, "wup");
        conversionMap.put(SimilarityMeasureType.Rada, "cdist");
        conversionMap.put(SimilarityMeasureType.AncSPLRada, "cdist");
        conversionMap.put(SimilarityMeasureType.LeacockChodorow, "lch");
        conversionMap.put(SimilarityMeasureType.PedersenPath, "path");
        conversionMap.put(SimilarityMeasureType.PekarStaab, "pks");
        conversionMap.put(SimilarityMeasureType.Sanchez2012, "sanchez");

        // We check that the measure is implemented by thius library
        
        if (!conversionMap.containsKey(hesmlMeasureType))
        {
            throw (new Exception(hesmlMeasureType.toString() +
                    " is not implemented by UMLS::Similarity"));
        }
        
        // We get the output measure tyoe
        
        String strUMLSimMeasureType = conversionMap.get(hesmlMeasureType);
        
        // We release the conversion table
        
        conversionMap.clear();
        
        // We return the result
        
        return (strUMLSimMeasureType);
    }
   
    /**
     * Execute the Perl script.
     * This function executes the script that call the UMLS::Similarity library 
     * and writes in an output file the result.
     * @param measureType
     */
    
    private void executePerlScript(
            String measureType,
            LibraryType libraryType) throws InterruptedException, IOException
    {
        // Create the command line for Perl
        
        String perl_path = "perl "; // default to linux

        // We build the command to call the evaluation Perl script
        
        String cmd = perl_path + m_PerlScriptDir + "/umls_similarity_from_cuis.t " + measureType + " " + libraryType.toString().toLowerCase();
        
        System.out.println("Executing the Perl script for calculating UMLS::Similarity");
        System.out.println(cmd);
        
        // Execute the script
        
        Process process = Runtime.getRuntime().exec(cmd);
       
        process.waitFor();
        
        InputStream stdin = process.getErrorStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        System.out.println("<OUTPUT>");
        while ( (line = br.readLine()) != null)
            System.out.println(line);
        System.out.println("</OUTPUT>");
        int exitVal = process.waitFor();            
        System.out.println("Process exitValue: " + exitVal);
        
        InputStream stdin2 = process.getInputStream();
        InputStreamReader isr2 = new InputStreamReader(stdin2);
        BufferedReader br2 = new BufferedReader(isr2);
        String line2 = null;
        System.out.println("<OUTPUT>");
        while ( (line2 = br2.readLine()) != null)
            System.out.println(line2);
        System.out.println("</OUTPUT>");
        int exitVal2 = process.waitFor();            
        System.out.println("Process exitValue: " + exitVal2);
        
        // Wait for the Perl process result
        

        
        System.out.println("Finished the execution of the Perl script");
    }
    
    /**
     * We release the resources associated to this object
     */
    
    @Override
    public void clear()
    {
        unloadSnomed();
        
        // Remove temporal files
        
        this.removeFile(m_PerlTempDir + "/tempFile.csv");
        this.removeFile(m_PerlTempDir + "/tempFileOutput.csv");
    }

    /**
     * This function returns the library type
     * @return 
     */
    
    @Override
    public SnomedBasedLibraryType getLibraryType()
    {
        return (SnomedBasedLibraryType.UMLS_SIMILARITY);
    }
    
    /**
     * This function sets the active semantic measure used by the library
     * to compute the semantic similarity between concepts.
     * @param icModel
     * @param measureType 
     */
    
    @Override
    public void setSimilarityMeasure(
            IntrinsicICModelType    icModel,
            SimilarityMeasureType   measureType) throws Exception
    {
        m_icModel = icModel;
        m_measureType = measureType;
    }
    
    /**
     * Load the SNOMED database
     */
    
    @Override
    public void loadSnomed() throws Exception
    {

    }
    
    /**
     * Unload the SNOMED databse
     */
    
    @Override
    public void unloadSnomed()
    {

    }
    
    /**
     * This function saves an output data matrix int oa CSV file
     * @param strDataMatrix 
     * @param strOutputFilename 
     */
    
    private void writeCSVfile(
            String[][]  strDataMatrix,
            String      strOutputFilename) throws IOException
    {
        // We create a writer for the text file
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(strOutputFilename, false));
        
        // We write the info for each taxonomy node
        
        char sep = ';';  // Separator dield
        
        for (int iRow = 0; iRow < strDataMatrix.length; iRow++)
        {
            // Avoid blank in first line
            
            String strLine = "\n" + strDataMatrix[iRow][0];
            
            if(iRow == 0) strLine = strDataMatrix[iRow][0];
                
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
    
    /**
     * Remove temporal files 
     * 
     * @param filePath 
     */
    protected void removeFile(
            String filePath)
    {
        File file = new File(filePath); 
        file.delete();
    }

    /**
     * This function evaluates the degreee of similarity between a pair od
     * UMLS concepts.
     * @param strfirstUmlsCUI
     * @param strSecondUmlsCUI
     * @return
     * @throws Exception 
     */    
    
    @Override
    public double getSimilarity(
            String  strfirstUmlsCUI,
            String  strSecondUmlsCUI) throws Exception
    {
        throw new UnsupportedOperationException("This function is not supported.");
    }
}