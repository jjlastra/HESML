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

package hesml.taxonomy.impl;

// HESML references

import hesml.taxonomy.ITaxonomy;

/**
 * This class implements a set of functions to create an in-memory taxonomy
 * from scratch or from some testing file, as well as other functions
 * to save taxonomy information into a CSV file.
 * @author Juan Lastra-Díaz
 */

public class TaxonomyFactory
{
    /**
     * This function creates a blank taxonomy.
     * @return A blank taxonomy
     */
    
    public static ITaxonomy createBlankTaxonomy()
    {
        return (new Taxonomy());
    }

    /**
     * This function creates a blank taxonomy with pre-reserved memory
     * for an expected number of vertexes in order to warrant
     * that at least this number of vertexes could be loaded.
     * @param expectedVertexCount Initial capacity of the taxonomy. 
     * @return A blank taxonomy
     */
    
    public static ITaxonomy createBlankTaxonomy(
        int expectedVertexCount)
    {
        return (new Taxonomy(expectedVertexCount));
    }
    
    /**
     * This function saves the vertexes info into an Excel (*.csv) file.
     * @param taxonomy Input taxonomy
     * @param strfilename Output filename
     * @param includeProb
     * @throws java.lang.Exception
     */
    
    public static void saveVertexesInfoToCSV(
            ITaxonomy   taxonomy,
            String      strfilename,
            boolean     includeProb) throws Exception
    {
        TaxonomyCSVWriter.saveVertexesInfoToCSV(taxonomy,
                strfilename, includeProb);
    }

    /**
     * This function saves the edge-based information into an Excel (*.csv)file
     * @param taxonomy Input taxonomy
     * @param strfilename Output filename
     * @throws Exception 
     */
    
    public static void saveEdgesInfoToCSV(
            ITaxonomy   taxonomy,
            String      strfilename) throws Exception
    {
        TaxonomyCSVWriter.saveEdgesInfoToCSV(taxonomy, strfilename);
    }
    
    /**
     * This function loads a taxonomy from HESML taxonomy file.
     * @param strFileTaxonomy
     * @return 
     * @throws java.lang.Exception 
     */
    
    public static ITaxonomy loadFromHESMLfile(
            String  strFileTaxonomy) throws Exception
    {
        ITaxonomy   taxonomy;   // Returned value
        
        TaxonomyReader  reader; // Reader
        
        // We create thje reader
        
        reader = new TaxonomyReader();
        
        // We read the taxonomy
        
        taxonomy = reader.loadHESMLfile(strFileTaxonomy);
        
        // We return the result
        
        return (taxonomy);
    }
}
