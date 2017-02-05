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

package hesml.taxonomyreaders.wordnet.impl;

import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.wordnet.IWordNetDB;
import hesml.taxonomyreaders.wordnet.IWordNetSynset;

/**
 * This class is the responsible to create the WordNet readers and loads
 * a WordNet database.
 * @author Juan Lastra-Díaz
 */

public class WordNetFactory
{
    /**
     * This function loads a WordNet database without to read the glosses.
     * @param strWordnetDir Directory for the WordNet database files
     * @param strWordNetDBFileName Database filename
     * @return The loaded WordnNetDB
     * @throws java.lang.Exception Unexpected error
     */
    
    public static IWordNetDB loadWordNetDatabase(
            String  strWordnetDir,
            String  strWordNetDBFileName) throws Exception
    {
        return (WordNetReader.loadDatabase(strWordnetDir, strWordNetDBFileName, false));
    }
    
    /**
     * This function loads a WordNet database without to read the glosses.
     * @param strWordNetDBFullpath
     * @return The loaded WordnNetDB
     * @throws java.lang.Exception Unexpected error
     */
    
    public static IWordNetDB loadWordNetDatabase(
            String  strWordNetDBFullpath) throws Exception
    {
        return (WordNetReader.loadDatabase(strWordNetDBFullpath, false));
    }
    
    /**
     * This function loads a WordNet database and reads the glosses
     * @param strWordnetDir Directory for the WordNet database files
     * @param strWordNetDBFileName  Database filename
     * @param loadGloss Indicates if the gloss will be loaded
     * @return The loaded WordnNetDB
     * @throws java.lang.Exception Unexpected error
     */
    
    public static IWordNetDB loadWordNetDatabase(
            String  strWordnetDir,
            String  strWordNetDBFileName,
            boolean loadGloss) throws Exception
    {
        return (WordNetReader.loadDatabase(strWordnetDir,
                strWordNetDBFileName, loadGloss));
    }
       
    /**
     * This function builds the taxonomy associated to a WordNetDB
     * @param wordnet WordNet database
     * @return The taxonomy representing the WordNet database
     * @throws java.lang.InterruptedException Unexpected error
     */
    
    public static ITaxonomy buildTaxonomy(
            IWordNetDB  wordnet) throws InterruptedException, Exception
    {
        ITaxonomy   taxonomy;   // Returned value
       
        // We create the graph
        
        taxonomy = hesml.taxonomy.impl.TaxonomyFactory.createBlankTaxonomy(
                    wordnet.getSynsetCount());
        
        // We create a vertex into the taxonomy for each synset.
        // Each vertex shares the same ID that its synset parent.
        
        for (IWordNetSynset synset: wordnet)
        {
            taxonomy.addVertex(synset.getID(), synset.getParentsId());
        }
        
        // We return the result
        
        return (taxonomy);
    }
}
