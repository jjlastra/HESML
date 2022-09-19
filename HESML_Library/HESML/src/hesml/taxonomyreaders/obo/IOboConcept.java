/*
<<<<<<< HEAD
 * Copyright (C) 2016-2022 Universidad Nacional de Educación a Distancia (UNED)
=======
 * Copyright (C) 2016-2021 Universidad Nacional de Educación a Distancia (UNED)
>>>>>>> master
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

/**
 * This interface represents a OBO concept
 * @author j.lastra
 */

public interface IOboConcept
{
    /**
     * This function returns the container ontology
     * @return 
     */
    
    IOboOntology getOntology();
    
    /**
     * This function returns the unique ID of an OBO concept within
     * an OBO ontology.
     * @return 
     */
    
    String getId();
    
    /**
     * This function returns the namespace which belongs this concept.
     * @return 
     */
    
    String getNamespace();
    
    /**
     * This function return the taxonomy node ID associated to this concept.
     * @return 
     */
    
    Long getTaxonomyNodeId();
    
    /**
     * This function retuns a vector with the parent concepts.
     * @return 
     */
    
    IOboConcept[] getParentConcepts();
    
    /**
     * Preferrerd name for the concept.
     * @return 
     */
    
    String getName();
    
    /**
     * This fucntion returns the parent Ids for the concept
     * @return 
     */
    
    String[] getParentsId();
    
    /**
     * This fucntion returns the parent Ids for the concept
     * @return 
     */
    
    String[] getAlternativeIds();
}
