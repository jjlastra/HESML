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

// Java references

import hesml.taxonomyreaders.wordnet.IWordNetSynset;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import hesml.benchmarks.CorrelationOutputMetrics;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

// HESML references

import hesml.measures.*;
import hesml.taxonomy.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * This class implements the common functions for all the word similarity
 * benchmarks based on WordNet.
 * @author Juan Lastra-Díaz
 */

abstract class WordNetSimBenchmark extends AbstractBenchmark
{ 
    /**
     * WordNet database
     */
    
    protected IWordNetDB  m_Wordnet;
    
    /**
     * Becnhmark dataset
     */
    
    protected WordPairSimilarity[]    m_WordPairs;
    
    /**
     * Sorting list with the pairs ordered by similarity value.
     */
    
    protected HashMap<Integer, Double>  m_WordPairsRanking;
       
    /**
     * Measure on the taxonomy
     */
    
    protected ISimilarityMeasure    m_Measure;
    
    /**
     * Constructor
     * @param wordnet
     * @param taxonomy
     * @param strWordPairsFile
     * @throws Exception 
     */
    
    WordNetSimBenchmark(
            IWordNetDB                  wordnet,
            ITaxonomy                   taxonomy,
            CorrelationOutputMetrics    metrics,
            String                      strWordPairsFile) throws Exception
    {
        // We load the apirs fiel
        
        super(taxonomy, metrics);
        
        // We save the objects
        
        m_Wordnet = wordnet;
        m_WordPairs = loadWordPairsFile(strWordPairsFile);
        
        // We create the ranked list of word pairs to allow the
        // computation of the Spearman correlation value.
        
        m_WordPairsRanking = getSpearmanRanking(m_WordPairs);
        
        // We check the existence of all the words
        
        checkWords();
    }
    
    /**
     * This function releases all the resources used by the object.
     */
    
    @Override
    public void clear()
    {
        // We release the resources stored by the base class
        
        super.clear();
        
        // We release our resources
                
        m_Wordnet.clear();
        m_WordPairsRanking.clear();
    }
    
    /**
     * This function sets the correlation values in the output matrix
     * according to the output metrics specified for the benchmark.
     * @param strOutputMatrix
     * @param pearson
     * @param spearman 
     */
    
    protected void setOutputMetrics(
            String[][]  strOutputMatrix,
            int         iBaseRow,
            int         iBaseColumn,
            double      pearson,
            double      spearman)
    {
        // We copy the values to the matrix according to the setup
        
        switch (m_OutputMetrics)
        {
            case Pearson:
                
                strOutputMatrix[iBaseRow][iBaseColumn] = Double.toString(pearson);
                
                break;
                
            case Spearman:
                
                strOutputMatrix[iBaseRow][iBaseColumn] = Double.toString(spearman);
                
                break;
                
            case PearsonAndSpearman:
                
                strOutputMatrix[iBaseRow][iBaseColumn] = Double.toString(pearson);
                strOutputMatrix[iBaseRow][iBaseColumn + 1] = Double.toString(spearman);
                
                break;
        }
    }
    
    /**
     * This function computes the ranking function for a vector
     * of word pair similarity values.
     * @param wordPairs Vector of word pairs whose degree of similarity will be evaluated.
     * @return The ranking function indexed by the pairs ID
     */
    
    protected HashMap<Integer, Double> getSpearmanRanking(
        WordPairSimilarity[]    wordPairs)
    {
        HashMap<Integer, Double>    pairsRanking;   // Returned value
        
        ArrayList<WordPairSimilarity>    sortedPairs;    // Ordered pairs
        
        HashMap<Double, int[]>    similaritySpans;    // Values frequencies
        
        int[]   minmax; // Minmax values for each similarity value
        
        int i = 1;  // Counter
        
        // We create, fill and sort the word pairs
        
        sortedPairs = new ArrayList<>();
        
        sortedPairs.addAll(Arrays.asList(wordPairs));
        Collections.sort(sortedPairs);
        
        // We create and fill the similarity frequecy count table
        
        similaritySpans = new HashMap<>();
        
        for (WordPairSimilarity pair: sortedPairs)
        {
            if (!similaritySpans.containsKey(pair.getHumanJudgement()))
            {
                minmax = new int[2];
                minmax[0] = i;
                minmax[1] = i;
                
                similaritySpans.put(pair.getHumanJudgement(), minmax);
            }
            else
            {
                minmax = similaritySpans.get(pair.getHumanJudgement());
                minmax[1] = i;
            }
            
            // We increase the counter
            
            i++;
        }
        
        // We create the mapping function
        
        pairsRanking = new HashMap<>();
        
        // We compute the spearman ranking values for each word pair.
        // The ranking is defined as the mean of the positions associated
        // to the same similarity value.
        
        for (WordPairSimilarity pair: sortedPairs)
        {
            // We get the positions covered by the similarity value
            
            minmax = similaritySpans.get(pair.getHumanJudgement());
            
            // We compute the average ranking value
            
            pairsRanking.put(pair.getID(), 0.5 * (minmax[0] + minmax[1]));
        }
        
        // We clear the auxiliary lists
        
        sortedPairs.clear();
        similaritySpans.clear();
        
        // We return the result
        
        return (pairsRanking);
    }
    
    /**
     * This function computes the Pearson's correlation factor
     * @param simVector
     * @return Pearson's correlation factor
     */
    
    protected double getPearsonCorrelation(
            double[]    simVector)
    {
        double  pearsonCorr;    // Returned value
        
        double  baselineMean;   // Mean of the baseline sample
        double  simVectorMean;  // Mena of the experiment
        
        double  baselineNorm;   // Norm of the centerd samples
        double  simVectorNorm;
        
        double  aux1, aux2; // Auxiliar variables
        
        int i;  // Counter
        
        // We compute the Pearson's correlation factor
        
        for (i = 0, baselineMean = 0, simVectorMean = 0;
                i < simVector.length;
                i++)
        {
            baselineMean += m_WordPairs[i].getHumanJudgement();
            simVectorMean += simVector[i];
        }
        
        // We compute the mean value of both samples
        
        baselineMean /= simVector.length;
        simVectorMean /= simVector.length;
        
        // We compute the pearson amd the norms
        
        for (i = 0, pearsonCorr = 0, baselineNorm = 0, simVectorNorm = 0;
                i < simVector.length;
                i++)
        {
            // We compute the coordinate translation of the i-sample
            // with regard to the mean
            
            aux1 = m_WordPairs[i].getHumanJudgement() - baselineMean;
            aux2 = simVector[i] - simVectorMean;
            
            // We acumulate the Pearson factors
            
            pearsonCorr += aux1 * aux2;
            
            // We compute the norms of the two sample vector
            
            baselineNorm += aux1 * aux1;
            simVectorNorm += aux2 * aux2;
        }
        
        // We get the euclidean norm of the deviation
        
        baselineNorm = Math.sqrt(baselineNorm);
        simVectorNorm = Math.sqrt(simVectorNorm);
        
        // We compute the final valu of the Pearson's correlation factor
        
        pearsonCorr /= (simVectorNorm * baselineNorm);
        
        // We return the result
        
        return (pearsonCorr);
    }

    /**
     * This function computes the Pearson's correlation factor
     * @param simVector
     * @return Pearson's correlation factor
     */
    
    protected double getSpearmanCorrelation(
            double[]    simVector) throws IOException
    {
        double  spearman;    // Returned value
        
        double  dif;    // Value difference
        double  difSum;
        
        double nSamples;  // nSamples
        
        int i;          // Counter
        
        HashMap<Integer, Double>   rankedResults; // Results to compare
        WordPairSimilarity[]       simResults;
        
        WordPairSimilarity  basePair;   // Baseline word pair
        
        double baseRank;   // Ranking of a base word pair
        double resultRank; // Ranking of the resulting word pair
        
        // We create the lists of auxiliary similarity results to rank
        // the results to be compared with the baseline.
        
        simResults = new WordPairSimilarity[simVector.length];
        
        // We fill the similarity result list to build the ranked list
        
        for (i = 0; i < simVector.length; i++)
        {
            // We get the baseline word pair
            
            basePair = m_WordPairs[i];
            
            // We create the result word pair
            
            simResults[i] = new WordPairSimilarity(i, basePair.getWord1(),
                                basePair.getWord2(), simVector[i]);
        }
        
        // We sort the similarity values within the ranked list
        
        rankedResults = getSpearmanRanking(simResults);
        
        // We compute the Pearson's correlation factor
        
        for (i = 0, difSum = 0.0; i < simVector.length; i++)
        {
            // We get the rank of the baseline pair and the result
            
            baseRank = m_WordPairsRanking.get(m_WordPairs[i].getID());
            resultRank = rankedResults.get(simResults[i].getID());
            
            // We compute the ranking difference norm
            
            dif = baseRank - resultRank;
            
            // We accummulate the difference
            
            difSum += dif * dif;
        }
        
        // We compute the final spearman score
        
        nSamples = (double) simVector.length;
        spearman = 1.0 - 6.0 * difSum / (nSamples * (nSamples * nSamples - 1));
        
        // We destroy the auxiliary lists
        
        rankedResults.clear();
       
        // We return the result
        
        return (spearman);
    }
    
    /**
     * This function saves to CSV file the human judgements Spearman ranking
     * and the ranking vector obtained from the similarity measure evaluated.
     * @param ranking 
     */
    
    private void SaveSpearmanRankingMatrix(
            String                      strOutputCSVfile,
            HashMap<Integer, Double>    ranking) throws IOException
    {
        String[][]  strDataMatrix;  // Ranking matrix
        
        int i = 0;  // Counter
        
        // We create the ranking blank matrix
        
        strDataMatrix = new String[1 + ranking.size()][3];
        
        // We fill the header info
        
        strDataMatrix[0][0] = "Concept ID";
        strDataMatrix[0][1] = "Golden ranking";
        strDataMatrix[0][2] = "Obtained ranking";
        
        // We fill the matrix with the ogtained Spearman ranking fators
        
        for (Integer conceptID: m_WordPairsRanking.keySet())
        {
            // We save the data for each row
            
            strDataMatrix[i + 1][0] = conceptID.toString();
            strDataMatrix[i + 1][1] = Double.toString(m_WordPairsRanking.get(conceptID));
            strDataMatrix[i + 1][2] = Double.toString(ranking.get(conceptID));
            
            // We increment the counter
            
            i++;
        }
                
        // We save the file
        
        saveCSVfile(strDataMatrix, strOutputCSVfile);
    }
    
    /**
     * This function returns the stored word pairs for the tests.
     * @return Test word pairs
     */
    
    protected WordPairSimilarity[] getWordPairs()
    {
        return (m_WordPairs);
    }
    
    /**
     * This function loads a word pairs file in CSV format in which
     * eac row is defined as follows: word1;word2;human judgement.
     * @param strFilename
     * @return
     * @throws Exception 
     */
    
    protected WordPairSimilarity[] loadWordPairsFile(
            String  strFilename) throws Exception
    {
        Exception   error;      // Thrown error
        String      strError;   // Error message
                
        WordPairSimilarity[]    allpairs;   // Returned value
        
        ArrayList<WordPairSimilarity>   readedPairs;    // Loading temp list
        
        File    csvFile;    // Data file
        Scanner reader;     // File reader
        
        String      strLine;    // Line with 3 fields per word pair
        String[]    strFields;  // Fields in the row
        
        int iPair = 0;  // Pairs counter
        
        // We create the file object to read the file
        
        csvFile = new File(strFilename);
        
        // We check the existence of the file
        
        if (!csvFile.exists())
        {
            strError = "The benchmark " + csvFile.getPath() + " file doesn´t exist";
            error = new Exception(strError);
            throw (error);
        }
        
        // Create the temporal list to read the file
        
        readedPairs = new ArrayList<>();
        
        // We create the reader of the file
        
        reader = new Scanner(csvFile);
        
        // We read the content of the file in row mode
        
        while (reader.hasNextLine())
        {
            // We retrieve the 3 fields
            
            strLine = reader.nextLine();
            strFields = strLine.split(";");
            
            // We create a new word pair
            
            if (strFields.length == 3)
            {
                readedPairs.add(new WordPairSimilarity(iPair++,
                    strFields[0], strFields[1],
                    Double.valueOf(strFields[2])));
            }
        }
        
        // We close the file
        
        reader.close();
        
        // We get the word pairs included in the file
        
        allpairs = new WordPairSimilarity[readedPairs.size()];
        readedPairs.toArray(allpairs);
        
        // Cleaning the temporary list
        
        readedPairs.clear();
        
        // We return the result
        
        return (allpairs);
    }
    
    /**
     * This function checks that all the words are contained in WordNet.
     */
    
    private void checkWords() throws Exception
    {
        Exception   error;      // Error thrown
        String      strError;   // Message error
                
        IVertex left, right;    // Vertexes
        IVertex micaVertex;
                
        IWordNetSynset[]    sets1, sets2;   // Synsets for the words
        
        // We check that all the words are contained in WordNet
        // aand there are a LCA for all pairs
        
        for (WordPairSimilarity pair: m_WordPairs)
        {
            // We check the existence of the words in wordnet
            
            if (!m_Wordnet.contains(pair.getWord1())
                || !m_Wordnet.contains(pair.getWord2()))
            {
                strError = "Any word of the pair is not contained in WordNet "
                        + pair.getWord1() + "-" + pair.getWord2();
                error = new Exception(strError);
                throw (error);
            }
            
            // We get all the synsets for each word
            
            sets1 = m_Wordnet.getWordSynsets(pair.getWord1());
            sets2 = m_Wordnet.getWordSynsets(pair.getWord2());
            
            // We check that all the word pairs have a LCA
            
            for (IWordNetSynset synset1: sets1)
            {
                left = m_Taxonomy.getVertexes().getById(synset1.getID());
                
                for (IWordNetSynset synset2: sets2)
                {
                    right = m_Taxonomy.getVertexes().getById(synset2.getID());
                    micaVertex = m_Taxonomy.getMICA(right, left);
                    
                    if (micaVertex == null)
                    {
                        throw (new Exception("There is no a MICA vertex for the synsets"
                            + synset1.getID() + " and " + synset2.getID()));
                    }
                }
            }
        }
    }

    /**
     * This function evaluates the input measure using the
     * current info within the taxonomy.
     * 
     * @return The Pearson's correlation factor.
     */
    
    protected double evalMeasure()
            throws Exception, InterruptedException
    {
        double  pearsonFactors;    // Returned value
        
        double[]    simVector;      // Similarity values

        // We configure a pairwise semantic similarity measure, 
        // i.e., a measure which will be used to assess the similarity 
        // of two nouns regarding their associated vertices in WordNet
        
        simVector = getSimilarityPairwiseVector(true);
        
        // We compute the correlation factor

        pearsonFactors = getPearsonCorrelation(simVector);
        
        // We return the result
        
        return (pearsonFactors);
    }

    /**
     * This function computes the Pearson and Spearman correlation
     * coefficients.
     * @return
     * @throws Exception
     * @throws InterruptedException 
     */
    
    protected double[] evalPearsonSpearmanMeasure(
        boolean showDebugInfo)
            throws Exception, InterruptedException
    {
        double[]  pearsonSpearman;    // Returned value
        
        double[]    simVector;      // Similarity values

        // We configure a pairwise semantic similarity measure, 
        // i.e., a measure which will be used to assess the similarity 
        // of two nouns regarding their associated vertices in WordNet
        
        simVector = getSimilarityPairwiseVector(showDebugInfo);
        
        // We compute the correlation factor

        pearsonSpearman = new double[2];
        
        pearsonSpearman[0] = getPearsonCorrelation(simVector);
        pearsonSpearman[1] = getSpearmanCorrelation(simVector);
        
        // We return the result
        
        return (pearsonSpearman);
    }
    
    /**
     * This function computes the similarity measure for each word pair
     * in the current benchmark and returns a vector with the
     * similarity values.
     * @return A vector with the similarity values.
     */

    private double[] getSimilarityPairwiseVector(
            boolean showDebugMsg)
            throws InterruptedException, Exception
    {
        double[]    simVector;  // Returned value
        
        int i = 0;  // Counter

        long    startTime;  // Stopwatch
        long    endTime;
        
        double  avgTimeMsec;    // Average time per word pair
        
        // We create the similarity vector to register the pairwise values
        
        simVector = new double[m_WordPairs.length];
        
        // We get the start time
        
        startTime = System.currentTimeMillis();
        
        // We compute the similarity value for each word pair
        
        for (WordPairSimilarity pair: m_WordPairs)
        {
            if (showDebugMsg)
            {
                System.out.println("Computing " + (i + 1) + " of " + simVector.length
                    + " " + pair.getWord1() + "-" + pair.getWord2());
            }
            
            simVector[i++] = highestSimilarityValue(m_Measure, pair.getWord1(), pair.getWord2());
        }
        
        // We get the final time
        
        endTime = System.currentTimeMillis();
        
        // We compute the time difference
        
        avgTimeMsec = (double)(endTime - startTime) / (double) simVector.length;
        
        // We print the average and total time
        
        System.out.println("Elapsed similarity computation time (msec) total = "
                + (endTime - startTime) + ", avg per pair = " + avgTimeMsec);
                
        // We return the result
        
        return (simVector);
    }
    
    /**
     * This function computes the similarity in WordNet among
     * two different words. The functions uses the maximum
     * similarity value among all the pairwise senses.
     * @param word1
     * @param word2
     * @return 
     */
    
    protected double highestSimilarityValue (
            ISimilarityMeasure  measure,
            String              word1,
            String              word2) throws InterruptedException, Exception
    {
        double  bestSimilarity;    // Returned value
        
        IWordNetSynset[]    urisWord1;  // Synsets of word 1
        IWordNetSynset[]    urisWord2;  // Synsets of word 2
        
        IVertexList word1Vertexes;   // Synsets for each word
        IVertexList word2Vertexes;
        
        // We get the synsets asscoiated to the input words

        urisWord1 = m_Wordnet.getWordSynsets(word1);
        urisWord2 = m_Wordnet.getWordSynsets(word2);

        assert(urisWord1.length > 0);
        assert(urisWord2.length > 0);
        
        // We get the vertexes synsets
        
        word1Vertexes = getWordSynsetVertexes(urisWord1);
        word2Vertexes = getWordSynsetVertexes(urisWord2);
        
        // We compute the similarity among all the pairwise
        // combinations of Synsets (cartesian product)
        
        bestSimilarity = measure.getHighestPairwiseSimilarity(word1Vertexes, word2Vertexes);
        
        // We clear the vertxes list
        
        word1Vertexes.clear();
        word2Vertexes.clear();
        
        // We return the result
        
        return (bestSimilarity);
    }
    
    /**
     * This function recovers the list of vertexes in the taxonomy,ç
     * which corresponds to the synsets of the word.
     * @param synsets
     * @return 
     */
    
    protected IVertexList getWordSynsetVertexes(
            IWordNetSynset[]    synsets) throws Exception
    {
        IVertexList vertexes;   // Returned value
        
        Integer[]   ids;    // Synset ids
        
        int i;  // Counter
        
        // We build the vector of vertxes ID (synsets)
        
        ids = new Integer[synsets.length];
        
        // We get the ids of the synsets
        
        for (i = 0; i < synsets.length; i++)
        {
            ids[i] = synsets[i].getID();
        }
        
        // We get the vertxes in the taxonomy
        
        vertexes = m_Taxonomy.getVertexes().getByIds(ids);
        
        // We returnr the result
        
        return (vertexes);
    }
}
