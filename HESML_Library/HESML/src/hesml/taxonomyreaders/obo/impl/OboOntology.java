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
 */

package hesml.taxonomyreaders.obo.impl;

import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.impl.TaxonomyFactory;
import hesml.taxonomyreaders.obo.IOboConcept;
import hesml.taxonomyreaders.obo.IOboOntology;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * This class represents an OBO ontology
 * @author j.lastra
 */

public class OboOntology implements IOboOntology
{
    /**
     * Collection of conceopts indexed by Id
     */
    
    private HashMap<String, OboConcept> m_conceptsdIndexedById;
    
    /**
     * Taxonomies associated to each namespace
     */
    
    private HashMap<String, ITaxonomy>  m_taxonomiesByNamespace;
    
    /**
     * Name of the ontology
     */
    
    private String  m_strName;
    
    /**
     * Constructor
     */
    
    OboOntology(String strName)
    {
        m_taxonomiesByNamespace = new HashMap<>();
        m_conceptsdIndexedById = new HashMap<>();
        m_strName = strName;
    }

    /**
     * This function returns the ontology name
     * @return 
     */
    
    @Override
    public String getName()
    {
        return (m_strName);        
    }
    
    /**
     * This fucntion adds a new concept to the ontology
     * @param strId
     * @param strNamespace
     * @param strName
     * @param strSynomyms
     * @param strParentIds
     * @return 
     */
    
    public IOboConcept addConcept(
            String      strId,
            String      strNamespace,
            String      strName,
            String[]    strParentIds) throws Exception
    {
        // We check that there is no other concept with the same ID
        
        if (m_conceptsdIndexedById.containsKey(strId))
        {
            String strError = "There is other concept with the ID = " + strId;
            throw (new Exception(strError));
        }
        
        // We create the concept and isnert in the ontology
        
        OboConcept concept = new OboConcept(this, strId, strNamespace,
                                    strName, strParentIds);
        
        // We add the conepot to the overall collection
        
        m_conceptsdIndexedById.put(strId, concept);
        
        // We return the result
        
        return (concept);
    }
    
    /**
     * This function returns the taxonomy associated to a given namespace.
     * @return 
     */
    
    public ITaxonomy getTaxonomyByNamespace(String strNamespace)
    {
        return (m_taxonomiesByNamespace.get(strNamespace));
    }
    
    /**
     * This function retrieves a concept by its ID
     * @param strId
     * @return 
     */
    
    @Override
    public IOboConcept getConceptById(String strId)
    {
        return (m_conceptsdIndexedById.get(strId));
    }
    
    /**
     * This fucntion builds the taxonomies assocyaed to the ontology
     */
    
    public void buildTaxonomies() throws Exception
    {
        // We create the collection of namespaces
        
        HashSet<String> namespaces = new HashSet<>();
        
        // We search for all namespaces
        
        for (OboConcept concept: m_conceptsdIndexedById.values())
        {
            namespaces.add(concept.getNamespace());
        }
        
        // We create a taxonomy for each namespace
        
        for (String strNamespace: namespaces)
        {
            m_taxonomiesByNamespace.put(strNamespace, TaxonomyFactory.createBlankTaxonomy());
        }
        
        // We traverse all OBO concepts
        
        LinkedList<OboConcept> pending = new LinkedList<>(m_conceptsdIndexedById.values());
        
        while (pending.size() > 0)
        {
            // We get the following concept
            
            OboConcept concept = pending.remove();
            
            // We check if all its parents have benn already visited
            
            boolean parentsVisited = true;
            
            for (String strParentId: concept.getParentsId())
            {
                if (!m_conceptsdIndexedById.get(strParentId).getVisited())
                {
                    parentsVisited = false;
                    pending.add(concept);
                    break;
                }
            }
            
            // We check if the concept can be added to the taxonmy
            
            if (parentsVisited)
            {
                // Obtengo la taxonomía asociada a su dominio
                
                ITaxonomy taxonomy = m_taxonomiesByNamespace.get(concept.getNamespace());
                
                // We get the parents ID
                
                String[] parents = concept.getParentsId();
                
                Long[] parentsId = new Long[parents.length];
                
                for (int i = 0; i < parents.length; i++)
                {
                    parentsId[i] = m_conceptsdIndexedById.get(parents[i]).getTaxonomyNodeId();
                }
                
                // We insert the vertex in the taxonomy
                
                Long conceptNodeId = Integer.toUnsignedLong(taxonomy.getVertexes().getCount());
                
                taxonomy.addVertex(conceptNodeId, parentsId);
                
                // We set the node ID
                
                concept.setTaxonomyNodeId(conceptNodeId);
                concept.setVisited(true);
            }
            else
            {
                pending.add(concept);
            }
        }
    }
    
    /**
     * This function releases the ontology resources
     */
    
    public void clear()
    {
        // We destry all taxonomies
        
        for (ITaxonomy taxonomy: m_taxonomiesByNamespace.values())
        {
            taxonomy.clear();
        }
        
        // We release the mappings
        
        m_conceptsdIndexedById.clear();
        m_taxonomiesByNamespace.clear();
    }
}
