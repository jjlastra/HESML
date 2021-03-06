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
 */

package hesml.taxonomyreaders.obo;

import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import java.util.Set;

/**
 * This interface represents any ontology based on the OBO file format, such
 * as the Gene Ontology (GO) detailed in go.obo file.
 * @author j.lastra
 */

public interface IOboOntology
{
    /**
     * This function releases the ontology resources
     */
    
    void clear();
    
    /**
     * This function returns the unified taxonomy
     * @return 
     */
    
    ITaxonomy getTaxonomy();
    
    /**
     * This function retrieves a concept by its ID
     * @param strId
     * @return 
     */
    
    IOboConcept getConceptById(String strId);
    
    /**
     * This function retrieves the set of taxonomy vertexes corresponding
     * to the OBO concept set.
     * @param strOBOconceptIds
     * @return 
     */
    
    Set<IVertex> getTaxonomyNodesForOBOterms(String[] strOBOconceptIds);
    
    /**
     * This function checks if the concept is conatined in the ontology
     * @param strId
     * @return 
     */
    
    boolean containsConceptId(String strId);
    
    /**
     * This function returns a vector with all GO concept identifiers
     * @return 
     */
    
    String[] getConceptIds();
    
    /**
     * This function returns the ontology name
     * @return 
     */
    
    String getName();
}
