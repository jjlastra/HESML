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

package hesml_umls_benchmark.semantclibrarywrappers;

import hesml.configurators.IntrinsicICModelType;
import hesml.measures.SimilarityMeasureType;
import hesml_umls_benchmark.SemanticLibraryType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import hesml_umls_benchmark.ISemanticLibrary;
import hesml_umls_benchmark.UMLSOntologyType;
import java.util.UUID;

/**
 * * This class implements the SNOMED similarity library based on UMLS::Similarity.
 * @author alicia
 */

public class UMLSSemanticLibraryWrapper extends SimilarityLibraryWrapper
        implements ISemanticLibrary
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
    
    // Random string for naming the temporal filenames
    
    String m_uuid;
    
    /**
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @param strUmlsCuiMappingFilename
     * @throws Exception 
     */
    
    UMLSSemanticLibraryWrapper(
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
        
        // Generate a random string for defining the filenames
        
        m_uuid = UUID.randomUUID().toString();
    }
    
    /**
     * Constructor to build the Snomed HESML database
     * @param strMeSHDir
     * @param strMeSHXmlFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strUmlsCuiFilename
     * @throws Exception 
     */
    
    UMLSSemanticLibraryWrapper(
            String  strMeSHDir,
            String  strMeSHXmlFileName,
            String  strUmlsDir,
            String  strUmlsCuiFilename) throws Exception
    {
        // Inicializamos la clase base
        
        super(strMeSHDir, strMeSHXmlFileName,
                strUmlsDir, strUmlsCuiFilename);
        
        // We obtain the temporary directory used for the evaluation script
        
        m_PerlScriptDir = new File("./UMLS_Similarity_Perl").getCanonicalPath();
        
        // We obtain the directory for temporary files
        
        m_PerlTempDir = System.getProperty("java.io.tmpdir");
        
        // Generate a random string for defining the filenames
        
        m_uuid = UUID.randomUUID().toString();
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
            String[][]          umlsCuiPairs,
            UMLSOntologyType    ontology) throws FileNotFoundException, IOException,
                                        InterruptedException, Exception 
    {
        // Initialize the result
        
        double[][] similarity = new double[umlsCuiPairs.length][2];
        
        // We write the temporal file with all the CUI pairs
        
        String tempFile = m_PerlTempDir + "/tempFile"+m_uuid+".csv";
        
        // We write the input file for the Perl script
        
        writeCuiPairsCsvFile(umlsCuiPairs, tempFile);
        
        // Get the measure as Perl script input format

        String measure = convertHesmlMeasureTypeToUMLS_Sim(m_measureType);
        
        // Warning message
        
        String strTempOutputFilename = m_PerlTempDir + "/tempFileOutput"+m_uuid+".csv";

        // We execute the Perl script
        
        executePerlScript(measure, ontology, tempFile, strTempOutputFilename);
        
        System.out.println("Reading UMLS__Similarity output file -> " + strTempOutputFilename);
        
        // We read the output from the Perl script.
        // Each row has the following format: CUI1 | CUI2 | similarity | time
        
        BufferedReader csvReader = new BufferedReader(new FileReader(strTempOutputFilename));
        
        // We retrieve the similarity score and running time per concept pair
        
        for (int i = 0; i < umlsCuiPairs.length; i++)
        {
            // We read the next output line and retrieve
            
            String[] data = csvReader.readLine().split(",");
            
            // We read the degree of similarity and running time
            
            similarity[i][0] = Double.valueOf(data[2]);
            similarity[i][1] = Double.valueOf(data[3]);
        }
        
        // We close the output script file and remove the files
        
        csvReader.close();
        this.clear();
        
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

        // We get the output measure tyoe
        
        String strUMLSimMeasureType = conversionMap.containsKey(hesmlMeasureType) ?
                                        conversionMap.get(hesmlMeasureType) : "";
        
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
            String              measureType,
            UMLSOntologyType    ontology,
            String              inputTempFile,
            String              outputTempFile) throws InterruptedException, IOException
    {
        // Create the command line for Perl
        
        String perl_path = "perl "; // default to linux

        // We set the ontology used by UMLS::Similarity library
        
        String strOntology = (ontology == UMLSOntologyType.MeSH) ? "msh" : "snomedct_us";
        
        // We build the command to call the evaluation Perl script
        
        String cmd = perl_path + m_PerlScriptDir + "/umls_similarity_from_cuis.t "
                    + measureType + " " + strOntology + " " + inputTempFile + " " + outputTempFile;
        
        System.out.println("Executing the Perl script for calculating UMLS::Similarity with " + ontology);
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
        // Remove temporary files
        
        this.removeFile(m_PerlTempDir + "/tempFile"+m_uuid+".csv");
        this.removeFile(m_PerlTempDir + "/tempFileOutput"+m_uuid+".csv");
    }

    /**
     * This function returns the library type
     * @return 
     */
    
    @Override
    public SemanticLibraryType getLibraryType()
    {
        return (SemanticLibraryType.UMLS_SIMILARITY);
    }
    
    /**
     * This function sets the active semantic measure used by the library
     * to compute the semantic similarity between concepts.
     * @param icModel
     * @param measureType 
     * @return true if the measure is allowed
     */
    
    @Override
    public boolean setSimilarityMeasure(
            IntrinsicICModelType    icModel,
            SimilarityMeasureType   measureType) throws Exception
    {
        // We save the new configuration
        
        boolean result = !convertHesmlMeasureTypeToUMLS_Sim(measureType).equals("");
        
        // We set the new measure type
        
        if (result)
        {
            m_icModel = icModel;
            m_measureType = measureType;
        }
        
        // We return the result
        
        return (result);
    }
    
    /**
     * Load the ontology. This functions does nothing because UMLS::Similarity
     * is invoked through a Perl script.
     */
    
    @Override
    public void loadOntology() throws Exception
    {

    }
    
    /**
     * Unload the ontology
     */
    
    @Override
    public void unloadOntology()
    {

    }
    
    /**
     * This function saves an output data matrix int oa CSV file
     * @param strDataMatrix 
     * @param strOutputFilename 
     */
    
    private void writeCuiPairsCsvFile(
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
    
    private void removeFile(
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
        throw new UnsupportedOperationException("getSimilarity");
    }
}