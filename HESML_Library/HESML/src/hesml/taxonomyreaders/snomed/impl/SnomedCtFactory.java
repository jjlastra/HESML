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
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName) throws Exception
    {
        return (SnomedDbReader.loadDatabase(strSnomedDir,
                strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName));
    }   
}
