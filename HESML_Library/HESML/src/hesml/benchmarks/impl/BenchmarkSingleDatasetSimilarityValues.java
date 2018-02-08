/*
 * Copyright (C) 2016-2018 Universidad Nacional de Educación a Distancia (UNED)
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

import hesml.benchmarks.CorrelationOutputMetrics;
import static hesml.benchmarks.impl.AbstractBenchmark.saveCSVfile;
import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.measures.IWordNetWordSimilarityMeasure;
import hesml.measures.IWordSimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import java.security.InvalidParameterException;
import java.util.HashSet;

/**
 * This class evaluates a specific word similarity benchmarks (single dataset)
 * using the noun database of a specific WordNet version on a collection
 * of similarity measures. Main output is a table with the raw similarity values
 * returned by each similarity value for each word pair. First column
 * of the output matrix contains the word pairs, whilst subsequent columns
 * contain the raw similarity values returned by each measure.
 * @author j.lastra
 */

public class BenchmarkSingleDatasetSimilarityValues extends WordNetSimBenchmark
{
    /**
     * Collection of IC models to be evaluated
     */
    
    private ITaxonomyInfoConfigurator[]    m_ICmodels;
    
    /**
     * Measure types evaluated
     */
    
    private final SimilarityMeasureType[]   m_MeasureTypes;
    
    /**
     * Filenames of the pre-trained embedding models in EMB file format
     */
    
    private final String[]  m_strRawEmbeddingFilenames;
    
    /**
     * Filenames of the pre.trained embedding models in Nasari file format
     */
    
    private final String[][] m_strNasariModelFilenames;
    
    /**
     * Filenames of the pre-trained emebdding files in UKB file format
     */
    
    private final String[]  m_strUKBModelFilenames;
    
    /**
     * Constructor
     */
    
    BenchmarkSingleDatasetSimilarityValues(
            IWordNetDB                  wordnetDb,
            ITaxonomy                   taxonomy,
            String                      strWordPairsFile,
            ITaxonomyInfoConfigurator[] icModels,
            SimilarityMeasureType[]     measureTypes,
            String[]                    strEmbModelFilenames,
            String[]                    strUKBModelFilenames,
            String[][]                  strNasariModelFilenames) throws Exception
    {   
        // We initialize the base class
        
        super(wordnetDb, taxonomy, CorrelationOutputMetrics.Pearson, strWordPairsFile);
        
        // We check the validity of the input parameters. We expect one
        // IC model per similarity measure. The IC models for non IC-based similarity
        // measures are defined to null.
        
        if (measureTypes.length != icModels.length)
        {
            throw (new InvalidParameterException());
        }
        
        // We store the data to carry-out the benchmark
        
        m_MeasureTypes = measureTypes;
        m_ICmodels = icModels;
        m_strRawEmbeddingFilenames = strEmbModelFilenames;
        m_strNasariModelFilenames = strNasariModelFilenames;
        m_strUKBModelFilenames = strUKBModelFilenames;
    }

    /**
     * This function runs the experiments and saves the results.
     * Each specific similarity measure defines a row in the output matrix,
     * and it is defined by the combination of any similarity measure with
     * any IC model. The IC models can be null when the measure is a non
     * IC-based similarity measure.
     * @param strMatrixResultsFile 
     * @param showDebugInfo 
     * @throws java.lang.Exception 
     */
    
    @Override
    public void executeTests(
            String  strMatrixResultsFile,
            boolean showDebugInfo) throws Exception
    {
        String[][]  strOutputMatrix;  // Output matrix
        
        // User message reporting the output file to be computed
        
        System.out.println("/**");
        System.out.println(" * Benchmark: similarity measures vs (Dataset and WordNet version)");
        System.out.println(" * Computing the output file: " + strMatrixResultsFile);
        System.out.println(" **/");
        
        // We get the formatted output matrix for the current experiment.
        
        strOutputMatrix = getOurOutputMatrix();
        
        // We iterate over the similarity measures

        for (int iMeasure = 0;
                iMeasure < m_MeasureTypes.length + m_strRawEmbeddingFilenames.length
                        + m_strUKBModelFilenames.length + m_strNasariModelFilenames.length;
                iMeasure++)
        {
            // The measure needs to be created every time in this place,
            // in order to allow the upgrade of the internal parameters
            // depending of the base taxonomy. It is necessary to use
            // the version of MeasureFactory.getMeasure() that accepts
            // WordNet as input paprameter, because of the TaiebSim2
            // only can be created through this function.

            IWordSimilarityMeasure measure = getMeasure(iMeasure);
            
            if (showDebugInfo)
            {
                System.out.println("Loading " + measure.toString());
            }
            
            // We evaluate the similarity for each word pair

            for (int iWord = 0; iWord < m_WordPairs.length; iWord++)
            {
                // We compute the highest similarity value
                
                double similarity = measure.getSimilarity(m_WordPairs[iWord].getWord1(),
                                    m_WordPairs[iWord].getWord2());
                
                // We save the similarity value in the output matrix
                
                strOutputMatrix[iWord + 1][2 + iMeasure] = Double.toString(similarity);
            }
            
            // We release the measure
            
            measure.clear();
        }
        
        // We save the file in CSV format
        
        saveCSVfile(strOutputMatrix, strMatrixResultsFile);
    }
    
    /**
     * This function instances a word similarity measure according to
     * the selected index. This benchmark evaluates four types of
     * measures as follows:
     * (1) ontology-based measures based on WOrdNet,
     * (2) embedding models in EMB file format,
     * (3) embedding models in UKB (ppv) file format,
     * (4) embedding models in EMB file format,
     * @param index
     * @return 
     */
    
    private IWordSimilarityMeasure getMeasure(
            int iMeasure) throws Exception
    {
        IWordSimilarityMeasure measure = null;
        
        // We create the intervals vector and fill it with the starting
        // positions of each family of measures
        
        int[] spans = new int[5];
                    
        spans[0] = 0;
        spans[1] = m_MeasureTypes.length;
        spans[2] = spans[1] + m_strRawEmbeddingFilenames.length;
        spans[3] = spans[2] + m_strUKBModelFilenames.length;
        spans[4] = spans[3] + m_strNasariModelFilenames.length;
        
        // We find the span of the input index
        
        int iSpan = 0;
        
        for (int i = 0; i < spans.length - 1; i++)
        {
            if ((iMeasure >= spans[i]) && (iMeasure < spans[i + 1]))
            {
                iSpan = i;
                break;
            }
        }
        
        // We creates the measure defined by the input index
        
        switch (iSpan)
        {
            case 0:
                
                measure = MeasureFactory.getWordNetWordSimilarityMeasure(
                            m_Wordnet, m_Taxonomy, m_MeasureTypes[iMeasure],
                            m_ICmodels[iMeasure]);
                
                break;

            case 1:
                
                measure = MeasureFactory.getEMBWordEmbeddingModel(
                            m_strRawEmbeddingFilenames[iMeasure - spans[iSpan]],
                        getDatasetWords());
                
                break;
         
            case 2:

                measure = MeasureFactory.getUKBppvEmbeddingModel(
                            m_strUKBModelFilenames[iMeasure - spans[iSpan]]);
                
                break;
                
            case 3:

                // We retrieve the filanems of the two Nasari files
                
                measure = MeasureFactory.getNasariEmbeddingModel(
                            m_strNasariModelFilenames[iMeasure - spans[iSpan]][0],
                            m_strNasariModelFilenames[iMeasure - spans[iSpan]][1],
                            getDatasetWords());
                
                break;
        }
        
        // We return the measure
        
        return (measure);
    }
    
    /**
     * This function retrieves the words to be evaluated
     * @return 
     */
    
    private String[] getDatasetWords()
    {
        // We create the set of words in the benchmark
        
        HashSet<String> words = new HashSet<>();
        
        // We retrieve all words
        
        for (WordPairSimilarity pair: m_WordPairs)
        {
            if (!words.contains(pair.getWord1()))
            {
                words.add(pair.getWord1());
            }
            
            if (!words.contains(pair.getWord2()))
            {
                words.add(pair.getWord2());
            }            
        }
        
        // We convert the set to array
        
        String[] strWords = new String[words.size()];
        
        words.toArray(strWords);
        
        // We reset the set
        
        words.clear();
        
        // We return the result
        
        return (strWords);
    }
    
    /**
     * This function returns the name of the measure in the iMeasure position.
     * @param iMeasure
     * @return
     * @throws Exception 
     */
    
    private String getMeasureName(
            int iMeasure)
    {
        String strName = "";    // Returned value
        
        // We create the intervals vector and fill it with the starting
        // positions of each family of measures
        
        int[] spans = new int[5];
                    
        spans[0] = 0;
        spans[1] = m_MeasureTypes.length;
        spans[2] = spans[1] + m_strRawEmbeddingFilenames.length;
        spans[3] = spans[2] + m_strUKBModelFilenames.length;
        spans[4] = spans[3] + m_strNasariModelFilenames.length;
        
        // We find the span of the input index
        
        int iSpan = 0;
        
        for (int i = 0; i < spans.length - 1; i++)
        {
            if ((iMeasure >= spans[i]) && (iMeasure < spans[i + 1]))
            {
                iSpan = i;
                break;
            }
        }
        
        // We creates the measure defined by the input index
        
        switch (iSpan)
        {
            case 0:
                
                strName = m_MeasureTypes[iMeasure].toString();
                
                if (m_ICmodels[iMeasure] != null)
                {
                    strName += "+" + m_ICmodels[iMeasure].toString();
                }
                
                break;

            case 1:
                
                strName = m_strRawEmbeddingFilenames[iMeasure - spans[iSpan]];
                
                break;
         
            case 2:

                strName = m_strUKBModelFilenames[iMeasure - spans[iSpan]];
                
                break;
                
            case 3:

                strName = m_strNasariModelFilenames[iMeasure - spans[iSpan]][0]
                        + m_strNasariModelFilenames[iMeasure - spans[iSpan]][1];
                
                break;
        }
        
        // We return the measure
        
        return (strName);
    }
    
    /**
     * This function creates the experiment matrix to record the raw
     * similarity values returned by each measure for each word pair.
     * The number of rows is set to 1 (human judgements) plus the number
     * of similarity measures which are evaluated, whilst the number of rows
     * is set to 1 (headers) plus the word pair count.
     * @return Blank experiment matrix
     */
    
    private String[][] getOurOutputMatrix()
    {
        String[][]  strMatrix; // Returne value
        
        int step = getMetricFillingStep();  // Columns for the metrics
        
        // We compute the number of measures evaluated
        
        int nMeasures = m_MeasureTypes.length + m_strRawEmbeddingFilenames.length
                    + m_strUKBModelFilenames.length + m_strNasariModelFilenames.length;
        
        // We create the output matrix of raw similarity values.
        // The matrix will have one row per word pair.
        
        strMatrix = new String[1 + m_WordPairs.length][2 + nMeasures];
        
        // We fill the titles of the first two columns
        
        strMatrix[0][0] = "Word pair";
        strMatrix[0][1] = "Human jugdement";
        
        // We fill the titles for the remaining columns
        
        for (int iMeasure = 0; iMeasure < nMeasures; iMeasure++)
        {
            strMatrix[0][iMeasure + 2]= getMeasureName(iMeasure);
        }
        
        // We fill the first two columns
        
        for (int iWordPair = 0; iWordPair < m_WordPairs.length; iWordPair++)
        {
            strMatrix[iWordPair + 1][0] = m_WordPairs[iWordPair].getWord1() + ","
                                            + m_WordPairs[iWordPair].getWord2();
            
            strMatrix[iWordPair + 1][1] = Double.toString(m_WordPairs[iWordPair].getHumanJudgement());
        }
        
        // We return the result
        
        return (strMatrix);
    }   
}
