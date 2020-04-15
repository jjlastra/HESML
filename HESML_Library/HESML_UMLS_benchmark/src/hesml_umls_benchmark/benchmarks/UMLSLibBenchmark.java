/*
 * Copyright (C) 2020 Universidad Complutense de Madrid (UCM)
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

package hesml_umls_benchmark.benchmarks;

import hesml.taxonomyreaders.snomed.ISnomedCtDatabase;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import hesml_umls_benchmark.IUMLSBenchmark;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class implements the abstract base class for all types of benchmarks
 * implemented by this program.
 * @author j.lastra
 */

abstract class UMLSLibBenchmark implements IUMLSBenchmark
{
    /**
     * SNOMED databse implemented by HESML
     */
    
    protected ISnomedCtDatabase   m_hesmlSnomedDatabase;
    
    /**
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @throws Exception 
     */
    
    UMLSLibBenchmark(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName) throws Exception
    {
        // We load the SNOMED database and build its HESML taxonomy
        
        m_hesmlSnomedDatabase = SnomedCtFactory.loadSnomedDatabase(strSnomedDir,
                                    strSnomedDBconceptFileName,
                                    strSnomedDBRelationshipsFileName,
                                    strSnomedDBdescriptionFileName, true);
    }
    
    /**
     * This fucntion runs the benchmark and writes the results to the
     * output file.
     */
    
    @Override
    public abstract void run(String strOutputFilename) throws Exception;
    
    /**
     * This fucntion releases the resouurces used by the benchmark.
     */
    
    @Override
    public void clear()
    {
        m_hesmlSnomedDatabase.clear();
    }
    
    /**
     * This function fills the data matrix with the running times reported
     * in the experiments.
     * @param strOutputDataMatrix
     * @param runningTimesInSecs
     * @param iRow 
     */
    
    protected void CopyRunningTimesToMatrix(
            String[][]  strOutputDataMatrix,
            double[]    runningTimesInSecs,
            int         iRow)
    {
        // We copy the values
        
        for (int i = 0; i < runningTimesInSecs.length; i++)
        {
            strOutputDataMatrix[iRow][i + 1] = Double.toString(runningTimesInSecs[i]);
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
