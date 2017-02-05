/*
 * Copyright (C) 2016 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml.benchmarks.impl;

// HESML references

import hesml.taxonomyreaders.wordnet.IWordNetSynset;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import hesml.benchmarks.CorrelationOutputMetrics;
import hesml.measures.*;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.*;
import hesml.configurators.*;
import static hesml.benchmarks.impl.AbstractBenchmark.saveCSVfile;

/**
 * The aim of this class is to generate the Synset pair info files
 * included in our WNSimRep dataset. Each row of these files
 * contains a set of synset pair-based attributes as follows:
 * (1) shortest path length, (2) lowest common subsumer (LCS)
 * using the basic depth definition, (3) lowest common subsumer
 * for the longest depth.
 * @author Juan Lastra-Díaz
 */

class BenchmarkSynsetsInfo extends WordNetSimBenchmark
{
    /**
     * Sequence of measure types that will be evaluated
     */
    
    private SimilarityMeasureType[] m_MeasureTypes;
    
    /**
     * This flag indicates whether the IC-based features should
     * be saved into the output file. It is only used with
     * the IC-based similarity measures.
     */
    
    private boolean m_SaveICfeatures;
       
    /**
     * Constructor
     */
    
    BenchmarkSynsetsInfo(
            IWordNetDB                  wordnet,
            ITaxonomy                   taxonomy,
            CorrelationOutputMetrics    metrics,
            String                      strWordPairsFile,
            boolean                     exportICfeatures,
            SimilarityMeasureType[]     measureTypes) throws Exception
    {
        super(wordnet, taxonomy, metrics, strWordPairsFile);
        
        // We save the collection of measure types
        
        m_MeasureTypes = measureTypes;
        m_SaveICfeatures = exportICfeatures;
    }

    /**
     * This function executes the tests
     * @param strMatrixResultsFile 
     */
    
    @Override
    public void executeTests(
            String  strMatrixResultsFile,
            boolean showDebugInfo) throws Exception
    {
        String[][]  strResultsMatrix;  // Results matrix
        
        IWordNetSynset[]    synsetsWord1;  // Synsets of word 1
        IWordNetSynset[]    synsetsWord2;  // Synsets of word 2
        
        IVertexList word1Vertexes;   // Synsets for each word
        IVertexList word2Vertexes;
        
        WordPairSimilarity[]    wordPairs;  // Dataset
        
        int nRows;  // Number of rows for the resulting matrix
        
        ISimilarityMeasure[]  measures;   // Measures to be evaluated
        
        // We get the wordpairs
        
        wordPairs = getWordPairs();
        
        // We compute the numnber of rows
        
        nRows = 1 + getRowsCountDataset(wordPairs);
       
        // We create the measures
        
        measures = getEvaluatedSimilarityMeasures(m_Taxonomy);
        
        // We create the matrix
        
        strResultsMatrix = getResultsBlankMatrix(nRows, measures.length);
                
        // We get the word pairs
        
        wordPairs = getWordPairs();
        
        // We compute all the IC-node methods
        
        int iRow = 1;
        
        for (WordPairSimilarity pair: wordPairs)
        {
            // We get the synsets for each word
            
            synsetsWord1 = m_Wordnet.getWordSynsets(pair.getWord1());
            synsetsWord2 = m_Wordnet.getWordSynsets(pair.getWord2());
            
            // We get the vertexes mathcing the concepts in the taxonomy
            
            word1Vertexes = getWordSynsetVertexes(synsetsWord1);
            word2Vertexes = getWordSynsetVertexes(synsetsWord2);
            
            // We insert the title row for the words pair
            
            insertWordPairHeader(pair, strResultsMatrix, iRow++);
            
            // We insert the results for the synset pair
            
            for (IVertex left: word1Vertexes)
            {
                for (IVertex right: word2Vertexes)
                {
                    insertSynsetPairResults(left, right,
                            strResultsMatrix, iRow++, measures);
                }
            }            
        }
        
        // We save the file in CSV format
        
        saveCSVfile(strResultsMatrix, strMatrixResultsFile);
    }
    
    /**
     * This function creates a blank matrix to save the results.
     * @param nRows     Rows count
     * @param nMeasures Similarity measures count
     * @return 
     */
    
    private String[][] getResultsBlankMatrix(
            int nRows,
            int nMeasures)
    {
        String[][]  strResultsMatrix;   // Returned value
        
        int iDataOffset;    // Offset position for the measure values
        
        // We create the matrix including the IC-based features
        
        if (m_SaveICfeatures)
        {
            iDataOffset = 7;
            strResultsMatrix = new String[nRows][iDataOffset + nMeasures];

            // We fill the last column headers for the IC-based ffeatures
            
            strResultsMatrix[0][5] = "Most Informative Common Ancenstor (MICA)";
            strResultsMatrix[0][6] = "MICA IC value";
        }
        else
        {
            iDataOffset = 5;
            strResultsMatrix = new String[nRows][iDataOffset + nMeasures];
        }
     
        // We fill the header of the result matrix

        strResultsMatrix[0][0] = "Synset ID1";
        strResultsMatrix[0][1] = "Synset ID2";
        strResultsMatrix[0][2] = "Shortest path length";
        strResultsMatrix[0][3] = "Lowest common subsumer (LCS)";
        strResultsMatrix[0][4] = "Lowest common subsumer (longest depth)";
        
        // We copy the measure names in the column headers
        
        for (int i = 0; i < m_MeasureTypes.length; i++)
        {
            strResultsMatrix[0][iDataOffset + i] = m_MeasureTypes[i].toString();
        }
        
        // We return the result
        
        return (strResultsMatrix);
    }
    
    /**
     * This function creates the similarity measures to be evaluated.
     * @param taxonomy Taxonomy of the benchmark
     * @return Vector of similarity measures.
    */
    
    private ISimilarityMeasure[] getEvaluatedSimilarityMeasures(
            ITaxonomy   taxonomy) throws Exception
    {
        ISimilarityMeasure[]  measures;   // Returned valueçç// We return the result
        
        int i;  // Counter
        
        // We create the blank vector
        
        measures = new ISimilarityMeasure[m_MeasureTypes.length];
        
        // We create the measures

        for (i = 0; i < m_MeasureTypes.length; i++)
        {
            measures[i] = MeasureFactory.getMeasure(taxonomy, m_MeasureTypes[i]);
        }
        
        // We return the result
        
        return (measures);
    } 
    
    /**
     * This function computes the number of rows for the
     * data matrix. The number of rows is the sum of
     * synsets combinations plus the number of pairs.
     * @param wordpairs
     * @return 
     */
    
    private int getRowsCountDataset(
            WordPairSimilarity[]    wordpairs) throws Exception
    {
        int nRows = wordpairs.length;  // Returned value
        
        // We computes the number of combinations
        
        for (WordPairSimilarity pair: wordpairs)
        {
            nRows += (m_Wordnet.getWordSynsets(pair.getWord1()).length
                    * m_Wordnet.getWordSynsets(pair.getWord2()).length);
        }
        
        // We return the result
        
        return (nRows);
    }
    
    /**
     * This function inserts the word pair in the current row
     * @param pair
     * @param strResultsMatrix
     * @param iRow 
     */
    
    private void insertWordPairHeader(
            WordPairSimilarity  pair,
            String[][]          strResultsMatrix,
            int                 iRow)
    {
        int i;  // Couner
        
        // We copy the both words
        
        strResultsMatrix[iRow][0] = pair.getWord1();
        strResultsMatrix[iRow][1] = pair.getWord2();
        
        System.out.println("Processing " + pair.getWord1() + "-" + pair.getWord2());
        
        // We fill the row with blank spaces
        
        for (i = 2; i < strResultsMatrix[iRow].length; i++)
        {
            strResultsMatrix[iRow][i] = "";
        }
    }
    
    /**
     * This function inserts the results for a synset pair.
     * @param strResultMatrix
     * @param iRow 
     */
    
    private void insertSynsetPairResults(
            IVertex             left,
            IVertex             right,
            String[][]          strResultMatrix,
            int                 iRow,
            ISimilarityMeasure[]  measures) throws Exception
    {
        IVertex micaVertex;         // MICA vertex
        IVertex lcsVertex;          // LCS vertex
        IVertex longestLCSvertex;   // LCS based on the longest path depth
        
        double  similarity;      // Similarities
        
        int shortestPathLength;
        
        int iColumnOffset;  // Offset to insert the similarity values
        
        int i;  // Counter
        
        // We compute the shortest path length
        
        shortestPathLength = (int) left.getShortestPathDistanceTo(right, false);
        
        // We compute the MICA and LCS vertexes
        
        lcsVertex = m_Taxonomy.getLCS(left, right, false);
        longestLCSvertex = m_Taxonomy.getLCS(left, right, true);
               
        // We save the results in the matrix
        
        strResultMatrix[iRow][0] = Integer.toString(left.getID());
        strResultMatrix[iRow][1] = Integer.toString(right.getID());
        strResultMatrix[iRow][2] = Integer.toString(shortestPathLength);
        strResultMatrix[iRow][3] = Integer.toString(lcsVertex.getID());
        strResultMatrix[iRow][4] = Integer.toString(longestLCSvertex.getID());
        
        // We copy the IC-based features
        
        if (m_SaveICfeatures)
        {
            iColumnOffset = 7;
            micaVertex = m_Taxonomy.getMICA(left, right);            
            strResultMatrix[iRow][5] = Integer.toString(micaVertex.getID());
            strResultMatrix[iRow][6] = Double.toString(micaVertex.getICvalue());
        }
        else
        {
            iColumnOffset = 5;
        }
        
        // We compute all the measures
        
        for (i = 0; i < measures.length; i++)
        {
            similarity = measures[i].getSimilarity(left, right);
            strResultMatrix[iRow][iColumnOffset + i] = Double.toString(similarity);
        }
    }
}

