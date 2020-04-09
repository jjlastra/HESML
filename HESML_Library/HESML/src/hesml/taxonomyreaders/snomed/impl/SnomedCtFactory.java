/*
 * Copyright (C) 2020 j.lastra
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hesml.taxonomyreaders.snomed.impl;

import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.snomed.ISnomedConcept;
import hesml.taxonomyreaders.snomed.ISnomedCtDatabase;

/**
 * This class implements the fucntions to load a SNOMED-CT database
 * and building its taxonomy.
 * @author j.lastra
 */
public class SnomedCtFactory
{
    /**
     * This function loads a SNOMED-CT database
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBconceptFileName RF2 file containing the SNOMED concepts
     * @param strSnomedDBRelationshipsFileName Relationships between concepts
     * @return The loaded WordnNetDB
     * @throws java.lang.Exception Unexpected error
     */
    
    public static ISnomedCtDatabase loadSnomedDatabase(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName) throws Exception
    {
        return (SnomedDbReader.loadDatabase(strSnomedDir,
                strSnomedDBconceptFileName, strSnomedDBRelationshipsFileName));
    }
    
    /**
     * This function buils the SNOMED-CT taxonomy
     * @return 
     */
    
    public static ITaxonomy buildTaxonomy(
            ISnomedCtDatabase snomedDatabase) throws Exception
    {
        // Debugging message
        
        System.out.println("Building the SNOMED-CT taxonomy ("
                + snomedDatabase.getConceptCount() + ") nodes");
        
        // We create the graph
        
        ITaxonomy taxonomy = hesml.taxonomy.impl.TaxonomyFactory.createBlankTaxonomy(
                            snomedDatabase.getConceptCount());
        
        // We create a vertex into the taxonomy for each comcept.
        // Each vertex shares the same CUID that its associated concept
        
        for (ISnomedConcept concept: snomedDatabase)
        {
            taxonomy.addVertex(concept.getCUID(), concept.getParentsCuid());
        }
        
        // We return the result
        
        return (taxonomy);
    }
}
