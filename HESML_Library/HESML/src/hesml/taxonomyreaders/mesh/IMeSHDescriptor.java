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

package hesml.taxonomyreaders.mesh;

import hesml.taxonomy.IVertex;

/**
 * This interface represents a MeSH Descriptor Data (MeSH concepts). We use
 * the same nomenclature used by MeSH because MeSH uses the concept term to
 * name the terms associated to a MeSH descriptor.
 * @author j.lastra
 */

public interface IMeSHDescriptor
{
    /**
     * This function retunrs the unique descriptor ID
     * @return 
     */
    
    String getMeSHDescriptorId();
    
    /**
     * Every MeSH descriptor (concept) could be rerpesented in multiple
     * tree-like taxonomies. Thus, this fuction returns all taxonomy nodes
     * associated to the current MeSH descriptor (concept)
     * @return 
     */
    
    String[] getTreeNodeIds();
    
    /**
     * This function returns the preferred name for this concept.
     * @return 
     */
    
    String getPreferredName();
    
    /**
     * This function returns the 
     * @return 
     */
    
    Long[] getTaxonomyNodesId();
    
    /**
     * This function returns the taxonomy nodes
     * @return 
     */
    
    IVertex[] getTaxonomyNodes();
    
    /**
     * This funtion returns the ontology containing this concept.
     * @return 
     */
    
    IMeSHOntology getOntology();
}
