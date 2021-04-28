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

package hesml.measures.impl;

import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.measures.ISimilarityMeasure;
import hesml.measures.IWordNetWordSimilarityMeasure;
import hesml.measures.SimilarityMeasureClass;
import hesml.measures.SimilarityMeasureType;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import hesml.taxonomyreaders.wordnet.IWordNetSynset;

/**
 * This class implements a word similarity measure based on WordNet.
 * @author j.lastra
 */

class WordNetWordSimilarityMeasure implements IWordNetWordSimilarityMeasure
{
    /**
     * Base taxonomy
     */
    
    private ITaxonomy   m_WordnetTaxonomy;
    
    /**
     * WordNet database
     */
    
    private IWordNetDB  m_wordnetDB;
    
    /**
     * Measure
     */
    
    private ISimilarityMeasure  m_Measure;
    
    /**
     * IC model associated to the measure
     */
    
    private ITaxonomyInfoConfigurator   m_icModel;
    
    /**
     * Constructor
     * @param wordNetDb
     * @param taxonomy
     * @param measureType
     * @param icModel 
     */
    
    WordNetWordSimilarityMeasure(
            IWordNetDB                  wordNetDb,
            ITaxonomy                   wordnetTaxonomy,
            SimilarityMeasureType       measureType,
            ITaxonomyInfoConfigurator   icModel) throws Exception
    {
        m_WordnetTaxonomy = wordnetTaxonomy;
        m_wordnetDB = wordNetDb;
        m_icModel = icModel;
        
        // We apply the IC model to the Wordnet taxonomy
        
        if (icModel != null)
        {
            icModel.setTaxonomyData(m_WordnetTaxonomy);
        }
        
        // We create a new measure
        
        m_Measure = MeasureFactory.getMeasure(wordnetTaxonomy, measureType);
    }
    
    /**
     * This function is called with the aim of releasing all resources used
     * by the measure.
     */
    
    @Override
    public void clear()
    {
    }
    
    /**
     * This function returns the WordNet database.
     * @return 
     */

    @Override
    public IWordNetDB getWordNetDB()
    {
        return (m_wordnetDB);
    }
    
    /**
     * This function returns the concept similarity measure
     * @return 
     */

    @Override
    public ISimilarityMeasure getConceptSimilarityMeasure()
    {
        return (m_Measure);
    }
    
    /**
     * This function returns a descriptive name of the measure
     * @return 
     */
    
    @Override
    public String toString()
    {
        String strName = m_Measure.getMeasureType().toString();
        
        // We check that the IC model being non-null
        
        if (m_icModel != null)
        {
            strName += "+" + m_icModel.toString();
        }
        
        // We return the fullname of the measure
        
        return (strName);
    }
    
    /**
     * This function returns the similarity measure class.
     * @return 
     */

    @Override
    public SimilarityMeasureClass getMeasureClass()
    {
        return (m_Measure.getMeasureClass());
    }

    /**
     * This function returns the measure type.
     * @return 
     */
    
    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (m_Measure.getMeasureType());
    }

    /**
     * This function returns the semantic measure between two words.
     * @param strWord1 The first word
     * @param strWord2 The second word
     * @return 
     * @throws java.lang.InterruptedException 
     */
    
    @Override
    public double getSimilarity(
            String  strWord1,
            String  strWord2) throws InterruptedException, Exception
    {
        // We initialize the returned value
        
        double  highestSimilarity = m_Measure.getNullSimilarityValue();
        
        // We check the existence of the words in WordNet
        
        if (m_wordnetDB.contains(strWord1) && m_wordnetDB.contains(strWord2))
        {
            // We get the synsets asscoiated to the input words

            IWordNetSynset[] urisWord1 = m_wordnetDB.getWordSynsets(strWord1);
            IWordNetSynset[] urisWord2 = m_wordnetDB.getWordSynsets(strWord2);

            // We get the vertexes synsets

            IVertexList word1Vertexes = getWordSynsetVertexes(urisWord1);
            IVertexList word2Vertexes = getWordSynsetVertexes(urisWord2);

            // We compute the similarity among all the pairwise
            // combinations of Synsets (cartesian product)

            highestSimilarity = m_Measure.getHighestPairwiseSimilarity(word1Vertexes, word2Vertexes);

            // We clear the vertxes list

            word1Vertexes.clear();
            word2Vertexes.clear();
        }
        
        // We return the result
        
        return (highestSimilarity);        
    }

    /**
     * This function returns the value returned by the similarity measure when
     * there is none similarity between both input concepts, or the concept
     * is not contained in the taxonomy.
     * @return 
     */
    
    @Override
    public double getNullSimilarityValue()
    {
        return (m_Measure.getNullSimilarityValue());
    }
    
    /**
     * This function recovers the list of vertexes in the taxonomy,ç
     * which corresponds to the synsets of the word.
     * @param synsets
     * @return 
     */
    
    private IVertexList getWordSynsetVertexes(
            IWordNetSynset[]    synsets) throws Exception
    {
        // We build the vector of vertxes ID (synsets)
        
        Long[] ids = new Long[synsets.length];
        
        // We get the ids of the synsets
        
        for (int i = 0; i < synsets.length; i++)
        {
            ids[i] = synsets[i].getID();
        }
        
        // We get the vertexes in the taxonomy
        
        IVertexList vertexes = m_WordnetTaxonomy.getVertexes().getByIds(ids);
        
        // We returnr the result
        
        return (vertexes);
    }    
}
