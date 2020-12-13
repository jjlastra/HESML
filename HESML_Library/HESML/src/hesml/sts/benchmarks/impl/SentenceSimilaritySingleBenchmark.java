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

package hesml.sts.benchmarks.impl;

import hesml.sts.benchmarks.ISentenceSimilarityBenchmark;
import hesml.sts.measures.ISentenceSimilarityMeasure;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class implements a sentence similarity benchmark using a normalized
 * dataset with the following line-based file format:
 * first sentence \t second sentence \t human similarity judgment (score)
 * @author j.lastra
 */

class SentenceSimilaritySingleBenchmark implements ISentenceSimilarityBenchmark
{
    /**
     * Output filename.
     */
    
    private final String  m_strOutputFilename;
    
    /**
     * Dataset reader
     */
    
    private final SentenceSimilarityDataset   m_Dataset;
    
    /**
     * Collection of sentence similarity measures to being evaluated
     */
    
    private final ISentenceSimilarityMeasure[] m_Measures;
    
    /**
     * Constructor
     * @param strDatasetFilename
     * @param strOutputFilename 
     */
    
    SentenceSimilaritySingleBenchmark(
            ISentenceSimilarityMeasure[]    measures,
            String                          strDatasetDirectory,
            String                          strDatasetFilename,
            String                          strOutputFilename) throws Exception
    {
        // We store the setup objects
        
        m_Measures = measures;
        m_strOutputFilename = strOutputFilename;
        
        // We create the reader and manager of the dataset
        
        m_Dataset = new SentenceSimilarityDataset(strDatasetDirectory
                        + "/" + strDatasetFilename);
    }
    
    /**
     * This function releases all the resources used by the object.
     */
    
    @Override
    public void clear()
    {
        // We release the resoruces used by the measures
        
        for (ISentenceSimilarityMeasure measure : m_Measures)
        {
            measure.clear();
        }
    }
    
    /**
     * This function returns the output filename of the benchmark.
     * @return Output filename
     */
    
    @Override
    public String  getOutputFilename()
    {
        return (m_strOutputFilename);
    }
    
    /**
     * This function executes the test and save the raw similarity values into
     * the output CSV file.
     * @param strMatrixResultsFile CSV file path containing the results
     * @param showDebugInfo The benchmark shows the count of the current sentence pair being evaluated.
     * @throws java.lang.Exception 
     */
    
    @Override
    public void evaluateBenchmark(
            boolean showDebugInfo) throws Exception
    {
        // We create the similarity matrix containing the similiarity
        // values returned by each measure for each sentence pair.
        
        double[][] similarityMatrix = new double[m_Dataset.getPairsCount()][m_Measures.length + 1];
        
        // We create the vector of column headers
        
        String[] strColumnHeaders = new String[m_Measures.length + 1];
        
        strColumnHeaders[0] = "Human";
        
        // We copy the human similarity judgement in the first column
        
        for (int iPair = 0; iPair < m_Dataset.getPairsCount(); iPair++)
        {
            similarityMatrix[iPair][0] = m_Dataset.getHumanJudgementAt(iPair);
        }
        
        // We evaluate all similarity measures 

        for (int iMeasure = 0; iMeasure < m_Measures.length; iMeasure++)
        {
            if (showDebugInfo)
            {
                System.out.println("Computing measure " + m_Measures[iMeasure].getLabel());
            }

            // We set the coluimn header for the current measure

            strColumnHeaders[iMeasure + 1] = m_Measures[iMeasure].getLabel();
            
            // We prepare the measure for evaluation
            
            m_Measures[iMeasure].prepareForEvaluation(m_Dataset.getLabel());
            
            // We get an array with the similarity scores for each pair of sentences
            
            double[] similarityScores = m_Measures[iMeasure].getSimilarityValues(
                    m_Dataset.getFirstSentences(), m_Dataset.getSecondSentences());
            
            // We iterate the scores and fill the scores matrix
            
            for (int iScore = 0; iScore < similarityScores.length; iScore++)
            {
                similarityMatrix[iScore][iMeasure + 1] = similarityScores[iScore];
            }
        }
        
        // We save the raw similarity values into the output file
        
        writeCSVfile(strColumnHeaders, similarityMatrix, m_strOutputFilename);
    }
        
    /**
     * This function writes the raw similarity matrix into a CSV file
     * @param strMatrix 
     */
    
    private void writeCSVfile(
            String[]    strColumnHeaders,
            double[][]  strMatrix,
            String      strOutputFile) throws IOException
    {
        // We create the directory structure (if necessary)
        
        File file = new File(strOutputFile);
        file.getParentFile().mkdirs();
        
        // We open for writing the file
        
        FileWriter writer = new FileWriter(strOutputFile, false);
        System.out.println(strOutputFile);
        
        // We write the first row with the column headers
        
        for (int i = 0; i < strColumnHeaders.length; i++)
        {
            if (i > 0) writer.write(";");
            
            writer.write(strColumnHeaders[i]);
        }            
        
        writer.write("\n");
        
        // We write the matrix in row mode
        
        for (int i = 0; i < strMatrix.length; i++)
        {
            // We write a full row with all values separated by semicolon
            
            for (int j = 0; j < strMatrix[0].length; j++)
            {
                if (j > 0) writer.write(";");
                
                writer.write(Double.toString(strMatrix[i][j]));
            }
            
            // Write the end of line
            
            writer.write("\n");
        }
        
        // We close the file
        
        writer.close();
    }
}
