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

package hesml.measures.impl;

// HESML references

import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.measures.*;
import hesml.taxonomy.*;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.HashSet;

/**
 * The aim of this class is to instantiate all the similarity measures in
 * order to hide the implementation classes to the client code.
 * @author Juan Lastra-Díaz
 */

public class MeasureFactory
{
    /**
     * This function creates an instance of the Taieb constrained similarity
     * measure, which corresponds to the second measure introduced
     * in the following paper.
     * 
     * Hadj Taieb, M. A., Ben Aouicha, M., and Ben Hamadou, A. (2014).
     * Ontology-based approach for measuring semantic similarity.
     * Engineering Applications of Artificial Intelligence, 36, 238–261.
     * 
     * @param taxonomy
     * @param maxSynsetsNumberPerWord
     * @return Hadj Taieb ert al (2014) similarity measure 2
     * @throws java.lang.Exception 
     */
    
    public static ISimilarityMeasure getConstrainedTaiebMeasure(
            ITaxonomy   taxonomy,
            int         maxSynsetsNumberPerWord) throws Exception
    {
        return (new MeasureTaieb2014(taxonomy, maxSynsetsNumberPerWord));
    }
    
    /**
     * This function returns the SimilarityMeasureType corresponding to the input string.
     * @param strInputMeasureType 
     * @return The type of pairwise similarity measure
     */
    
    public static SimilarityMeasureType convertToSimilarityMeasureType(
            String strInputMeasureType) throws Exception
    {
        // We retrieve the enum type
        
        SimilarityMeasureType inputMeasureType = SimilarityMeasureType.CosineLin;
        
        // We look for the matching value

        boolean found = false;
        
        for (SimilarityMeasureType measureType: SimilarityMeasureType.values())
        {
            if (measureType.toString().equals(strInputMeasureType))
            {
                inputMeasureType = measureType;
                found = true;
                break;
            }
        }
        
        // We check that input string could be correctly parsed
        
        if (!found) throw (new Exception("Unknown SimilarityMeasureType = " + strInputMeasureType));
        
        // We return the result
        
        return (inputMeasureType);
    }

    /**
     * This function returns the SimilarityMeasureType corresponding to the input string.
     * @param strInputMeasureType 
     * @return The type of pairwise similarity measure
     */
    
    public static GroupwiseSimilarityMeasureType convertToGroupwiseSimilarityMeasureType(
            String strInputMeasureType) throws Exception
    {
        // We retrieve the enum type
        
        GroupwiseSimilarityMeasureType inputMeasureType = GroupwiseSimilarityMeasureType.SimLP;
        
        // We look for the matching value

        boolean found = false;
        
        for (GroupwiseSimilarityMeasureType measureType: GroupwiseSimilarityMeasureType.values())
        {
            if (measureType.toString().equals(strInputMeasureType))
            {
                inputMeasureType = measureType;
                found = true;
                break;
            }
        }
        
        // We check that input string could be correctly parsed
        
        if (!found) throw (new Exception("Unknown GroupwiseSimilarityMeasureType = " + strInputMeasureType));
        
        // We return the result
        
        return (inputMeasureType);
    }
    
    /**
     * This function converts the input string to its correspnding
     * groupwise metric type.
     * @param strGroupwiseMetricType
     * @return The  equivalent groupwise metric type
     */
    
    public static GroupwiseMetricType convertToGroupwiseMetric(
            String  strGroupwiseMetricType) throws Exception
    {
        // We retrieve the enum type
        
        GroupwiseMetricType metricType = GroupwiseMetricType.Average;
        
        // We look for the matching value
        
        boolean found = false;
        
        for (GroupwiseMetricType groupwiseMetric: GroupwiseMetricType.values())
        {
            if (groupwiseMetric.toString().equals(strGroupwiseMetricType))
            {
                metricType = groupwiseMetric;
                found = true;
                break;
            }
        }
        
        // We check that input string could be correctly parsed
        
        if (!found) throw (new Exception("Unknown GroupwiseMetricType = " + strGroupwiseMetricType));
        
        // We return the result
        
        return (metricType);
    }
    
    /**
     * This function creates a groupwise measure based on a groupwise
     * metric and any pairwise similarity measure.
     * @param taxonomy
     * @param pairwiseMeasureType
     * @param groupwiseMetricType
     * @return 
     */
    
    public static IGroupwiseSimilarityMeasure getGroupwiseBasedOnPairwiseMeasure(
            ITaxonomy               taxonomy,
            SimilarityMeasureType   pairwiseMeasureType,
            GroupwiseMetricType     groupwiseMetricType) throws Exception
    {
        return (new GroupwiseBasedOnPairwiseMeasure(taxonomy,
                getMeasure(taxonomy, pairwiseMeasureType), groupwiseMetricType));
    }

    /**
     * This function creates a groupwise measure which noi require parameters,
     * such as SimLP, SimGIC and SimLP.
     * @param groupwiseMeasureType
     * @return 
     */
    
    public static IGroupwiseSimilarityMeasure getGroupwiseNoParameterMeasure(
            GroupwiseSimilarityMeasureType  groupwiseMeasureType) throws Exception
    {
        // We initialize the output
        
        IGroupwiseSimilarityMeasure groupwiseMeasure = null;
        
        // We create the specific groupwise similairty measure
        
        switch (groupwiseMeasureType)
        {
            case SimGIC:
                
                groupwiseMeasure = new GroupwiseSimGICMeasure();
                
                break;

            case SimLP:
                
                groupwiseMeasure = new GroupwiseSimLPMeasure();
                
                break;

            case SimUI:
                
                groupwiseMeasure = new GroupwiseSimUIMeasure();
                
                break;
                
            default:
                
                throw (new Exception("Unssoported type in this function -> "
                        + groupwiseMeasureType.toString()));
        }
        
        // We return the result
        
        return (groupwiseMeasure);
    }
    
    /**
     * This function creates an instance of an specific similarity measure.
     * @param taxonomy
     * @param measureType
     * @return A similarity measure.
     * @throws java.lang.Exception 
     */
    
    public static ISimilarityMeasure getMeasure(
            ITaxonomy               taxonomy,
            SimilarityMeasureType   measureType) throws Exception
    {
        ISimilarityMeasure    measure = null; // Returned value
        
        // We create the measure
        
        switch (measureType)
        {
            case Rada:
            case AncSPLRada:    
                
                measure = new MeasureRada(taxonomy, (measureType == SimilarityMeasureType.AncSPLRada));
                
                break;
                
            case Mubaid:
            case AncSPLMubaid:
                
                measure = new MeasureAlMubaidNguyen2009(taxonomy, (measureType == SimilarityMeasureType.AncSPLMubaid));
                
                break;
                
            case LeacockChodorow:
            case AncSPLLeacockChodorow:
                
                measure = new MeasureLeacockChodorow(taxonomy, (measureType == SimilarityMeasureType.AncSPLLeacockChodorow));
                
                break;
                
            case PedersenPath:
            case AncSPLPedersenPath:
                
                measure = new MeasurePedersenPath(taxonomy, (measureType == SimilarityMeasureType.AncSPLPedersenPath));
                
                break;
                                
            case Garla:
                
                measure = new MeasureGarla(taxonomy);
                
                break;
                               
            case WuPalmer:
                
                measure = new MeasureWuPalmer(taxonomy);
                
                break;

            case WuPalmerFast:
                
                measure = new MeasureWuPalmerFast(taxonomy);
                
                break;
                
            case Li2003Strategy3:
            case AncSPLLi2003Strategy3:
                
                measure = new MeasureLi2003Strategy3(taxonomy,
                        (measureType == SimilarityMeasureType.AncSPLLi2003Strategy3));
                
                break;

            case Li2003Strategy4:
            case AncSPLLi2003Strategy4:
                
                measure = new MeasureLi2003Strategy4(taxonomy,
                        (measureType == SimilarityMeasureType.AncSPLLi2003Strategy4));
                
                break;
                
            case Li2003Strategy9:
            case AncSPLLi2003Strategy9:
                
                measure = new MeasureLi2003Strategy9(taxonomy,
                        (measureType == SimilarityMeasureType.AncSPLLi2003Strategy9));
                
                break;
                
            case Sanchez2012:
                
                measure = new MeasureSanchez2012(taxonomy);
                
                break;
                
            case Zhou:
            case AncSPLZhou:
                
                measure = new MeasureZhou(taxonomy, (measureType == SimilarityMeasureType.AncSPLZhou));
                
                break;
                
            case CosineNormWeightedJiangConrath:
            case AncSPLCosineNormWeightedJiangConrath:
                
                measure = new MeasureCosineNormWeightedJiangConrath(taxonomy,
                        (measureType == SimilarityMeasureType.AncSPLCosineNormWeightedJiangConrath));              

                
                break;
                
            case WeightedJiangConrath:
            case AncSPLWeightedJiangConrath:
                
                measure = new MeasureWeightedJiangConrath(taxonomy,
                             (measureType == SimilarityMeasureType.AncSPLWeightedJiangConrath));
                
                break;
                
            case JiangConrath:
                
                measure = new MeasureJiangConrath(taxonomy);
                
                break;
                
            case LogisticLin:
                
                measure = new MeasureLogisticLin(taxonomy);
                
                break;
                
            case CosineLin:
                
                measure = new MeasureCosineLin(taxonomy);
                
                break;
                
            case ExpNormJiangConrath:
                
                measure = new MeasureExpNormJiangConrath(taxonomy);
                
                break;
                
            case CosineNormJiangConrath:
                
                measure = new MeasureCosineNormJiangConrath(taxonomy);
                
                break;
                
            case LogisticNormJiangConrath:
                
                measure = new MeasureLogisticNormJiangConrath(taxonomy);
                
                break;
                               
            case PirroSeco:
                
                measure = new MeasurePirroSeco(taxonomy);
                
                break;
                
            case FaITH:
                
                measure = new MeasureFaith(taxonomy);
                
                break;
                
            case Lin:
                
                measure = new MeasureLin(taxonomy);
                
                break;
                
            case Resnik:
                
                measure = new MeasureResnik(taxonomy);
                
                break;
                
            case Meng2012:
                
                measure = new MeasureMeng2012(taxonomy);
                        
                break;
                
            case Meng2014:
            case AncSPLMeng2014:
                
                measure = new MeasureMeng2014(taxonomy, (measureType == SimilarityMeasureType.AncSPLMeng2014));
                         
                break;
                
            case Taieb2014:
                
                measure = new MeasureTaieb2014(taxonomy);
                        
                break;
                
            case Gao2015Strategy3:
            case AncSPLGao2015Strategy3:

                measure = new MeasureGao2015Method3(taxonomy, (measureType == SimilarityMeasureType.AncSPLGao2015Strategy3));
                
                break;
                
            case Hao:
            case AncSPLHao:
                
                // We use the default values provided in the paper
                
                measure = new MeasureHao(taxonomy, 0.0, 1.0,
                        (measureType == SimilarityMeasureType.AncSPLHao));
                
                break;
                
            case LiuStrategy1:
            case AncSPLLiuStrategy1:
                
                // We use the default values provided in the paper
                
                measure = new MeasureLiuStrategy1(taxonomy, 0.5, 0.55,
                            (measureType == SimilarityMeasureType.AncSPLLiuStrategy1));
                
                break;
                
            case LiuStrategy2:
            case AncSPLLiuStrategy2:
                
                // We use the default values provided in the paper
                
                measure = new MeasureLiuStrategy2(taxonomy, 0.25, 0.25,
                            (measureType == SimilarityMeasureType.AncSPLLiuStrategy2));
                
                break;
                
            case Stojanovic:
                
                measure = new MeasureStojanovic(taxonomy);
                
                break;
                
            case PekarStaab:
            case AncSPLPekarStaab:
                
                measure = new MeasurePekarStaab(taxonomy,
                            (measureType == SimilarityMeasureType.AncSPLPekarStaab));
                
                break;
                
            case CaiStrategy1:
            case AncSPLCaiStrategy1:
                
                measure = new MeasureCaiStrategy1(taxonomy, 0.5, 0.1,
                            (measureType == SimilarityMeasureType.AncSPLCaiStrategy1));
                
                break;

            case CaiStrategy2:
                
                measure = new MeasureCaiStrategy2(taxonomy, 0.5, 0.1);
                
                break;
                
            case Taieb2014sim2:
                
                throw (new InvalidParameterException("Use the other getMeasure() function with a WordNetDB"));
        }
        
        // We return the result
        
        return (measure);
    }
    
    /**
     * This function creates an instance of an specific similarity measure.
     * The only aim of this function is to allow the unified creation
     * of the Taieb2014sim2 similarity measure with the remaining measures
     * during the loading process of the MultipleDatasetsExperiment.
     * Unlike the remaining similarity measures, the Taieb2014sim2 similarity
     * measure requires the maximum number of synsets per word in addition
     * to the base taxonomy. For this reason, this function defines an instance
     * of the WordNetDB as input parameter. Currently, this function is only invoked
     * by the MultipleDatasetsExperiment in order to reproduce the results
     * reported in table 4 of our paper below.
     * 
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A novel family of IC-based similarity measures with a detailed
     * experimental survey on WordNet.
     * Engineering Applications of Artificial Intelligence Journal, 46, 140–153.
     * 
     * @param wordnet
     * @param taxonomy
     * @param measureType
     * @return A similarity measure.
     * @throws java.lang.Exception 
     */
    
    public static ISimilarityMeasure getMeasure(
            IWordNetDB              wordnet,
            ITaxonomy               taxonomy,
            SimilarityMeasureType   measureType) throws Exception
    {
        ISimilarityMeasure    measure; // Returned value
        
        // We create the measure
        
        if (measureType != SimilarityMeasureType.Taieb2014sim2)
        {
            measure = getMeasure(taxonomy, measureType);
        }
        else
        {
            measure = new MeasureTaieb2014(taxonomy, wordnet.getMaxSynsetsNumberPerWord());
        }
        
        // We return the result
        
        return (measure);
    }   
    
    /**
     * This function creates a new similarity measure based on WordNet.
     * @param wordnetDB WordNet database
     * @param wordnetTaxonomy Base taxonomy. It will be updated by the IC model
     * @param measureType Type of measure to be created
     * @param icModel ICmodel used which can be null for non IC_based measures
     * @return The new measure
     * @throws Exception 
     */
    
    public static IWordSimilarityMeasure getWordNetWordSimilarityMeasure(
            IWordNetDB                  wordnetDB,
            ITaxonomy                   wordnetTaxonomy,
            SimilarityMeasureType       measureType,
            ITaxonomyInfoConfigurator   icModel) throws Exception
    {
        return (new WordNetWordSimilarityMeasure(wordnetDB, wordnetTaxonomy, measureType, icModel));
    }
    
    /**
     * This function loads a EMB word embedding model implementing
     * a word similarity measure.
     * @param strRawVectorFile
     * @param words
     * @return 
     */
    
    public static IWordSimilarityMeasure getEMBWordEmbeddingModel(
            String      strRawVectorFile,
            String[]    strWords) throws IOException, ParseException
    {
        return (new EMBWordEmbeddingModel(strRawVectorFile, strWords));
    }
    
    /**
     * This function loads a UKB (ppv) word embedding model implementing
     * a word similarity measure.
     * @param strUKBppvVectorFile
     * @return 
     */
    
    public static IWordSimilarityMeasure getUKBppvEmbeddingModel(
            String      strUKBppvVectorFile,
            String[]    strWords) throws IOException, ParseException
    {
        return (new UKBppvWordEmbeddingModel(strUKBppvVectorFile, strWords));
    }
    
    /**
     * This function loads a UKB (ppv) word embedding model implementing
     * a word similarity measure.
     * @param strSensesFilename
     * @param strVectorFilename
     * @param strWords Words which will be evaluated later
     * @return 
     * @throws java.io.IOException 
     * @throws java.text.ParseException 
     */
    
    public static IWordSimilarityMeasure getNasariEmbeddingModel(
            String      strSensesFilename,
            String      strVectorFilename,
            String[]    strWords) throws IOException, ParseException
    {
        return (new NasariWordEmbeddingModel(strSensesFilename, strVectorFilename, strWords));
    }    

    /**
     * This function returns the list of measures which use any path-based feature
     * into their computation, and thus are slower than the remaining ones.
     * @return 
     */
    
    public static HashSet<SimilarityMeasureType> getPathBasedMeasureTypes()
    {
        HashSet<SimilarityMeasureType>  pathBasedMeasures = new HashSet<>();
        
        pathBasedMeasures.add(SimilarityMeasureType.CosineNormWeightedJiangConrath);
        pathBasedMeasures.add(SimilarityMeasureType.Rada);
        pathBasedMeasures.add(SimilarityMeasureType.Zhou);
        pathBasedMeasures.add(SimilarityMeasureType.WuPalmer);
        pathBasedMeasures.add(SimilarityMeasureType.Mubaid);
        pathBasedMeasures.add(SimilarityMeasureType.CaiStrategy1);
        pathBasedMeasures.add(SimilarityMeasureType.Gao2015Strategy3);
        pathBasedMeasures.add(SimilarityMeasureType.Hao);
        pathBasedMeasures.add(SimilarityMeasureType.LeacockChodorow);
        pathBasedMeasures.add(SimilarityMeasureType.Li2003Strategy3);
        pathBasedMeasures.add(SimilarityMeasureType.LiuStrategy1);
        pathBasedMeasures.add(SimilarityMeasureType.LiuStrategy2);
        pathBasedMeasures.add(SimilarityMeasureType.Meng2014);
        pathBasedMeasures.add(SimilarityMeasureType.PedersenPath);
        pathBasedMeasures.add(SimilarityMeasureType.PekarStaab);
        pathBasedMeasures.add(SimilarityMeasureType.WeightedJiangConrath);
        
        // We return the result
        
        return (pathBasedMeasures);
    }
    
    /**
     * This function computes the Euclidean norm of the input vector
     * @param vector
     * @return 
     */
    
    public static double getVectorNorm(
        double[]    vector)
    {
        double norm = 0.0;  // Returned value
        
        // We compute the acumulated square-coordinates
        
        for (int i = 0; i < vector.length; i++)
        {
            norm += vector[i] * vector[i];
        }
        
        // Finally, we compute the square root
        
        norm = Math.sqrt(norm);
        
        // We return the result
        
        return (norm);
    }
    
    /**
     * This function loads a word embedding model implementing
     * a word similarity measure.
     * @param embeddingType
     * @param strRawVectorFile
     * @return 
     * @throws java.io.IOException 
     * @throws java.text.ParseException 
     */
    
    public static IPretrainedWordEmbedding getWordEmbeddingModel(
            WordEmbeddingFileType   embeddingType,
            String                  strRawVectorFile) throws IOException, ParseException, Exception
    {
        // We initialize the output
        
        IPretrainedWordEmbedding model = null;
        
        // We create the word mebedding model
        
        switch(embeddingType)
        {
            case BioWordVecBinaryWordEmbedding:
             
                model = new BioWordVecBinaryEmbeddingModel(strRawVectorFile);
                
                break;
                
            case FastTextVecWordEmbedding:
             
                model = new FastTextVecWordEmbeddingModel(strRawVectorFile);
                
                break;
        }
        
        // We return the result
        
        return (model);
    }
}
