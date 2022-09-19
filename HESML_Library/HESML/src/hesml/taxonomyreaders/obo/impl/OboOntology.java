/*
 * Copyright (C) 2016-2022 Universidad Nacional de Educación a Distancia (UNED)
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
import hesml.taxonomy.IVertex;
import hesml.taxonomy.impl.TaxonomyFactory;
import hesml.taxonomyreaders.obo.IOboConcept;
import hesml.taxonomyreaders.obo.IOboOntology;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
    
    private ITaxonomy   m_taxonomy;
    
    /**
     * Name of the ontology
     */
    
    private String  m_strName;
    
    /**
     * Constructor
     */
    
    OboOntology(String strName)
    {
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
     * This function returns a vector with all GO concept identifiers
     * @return 
     */
    
    @Override
    public String[] getConceptIds()
    {
        // We create the output vector
        
        String[] strConceptsId = new String[m_taxonomy.getVertexes().getCount() - 1];
        
        // We retrieve all GO ids from the taxonomy nodes
        
        int i = 0;
        
        for (IVertex vertex: m_taxonomy.getVertexes())
        {
            if (!vertex.getStringTag().equals(""))
            {
                strConceptsId[i++] = vertex.getStringTag();
            }
        }
        
        // We return the result
        
        return (strConceptsId);
    }
    
    /**
     * This function checks if the concept is conatined in the ontology
     * @param strId
     * @return 
     */
    
    @Override
    public boolean containsConceptId(String strId)
    {
        return (m_conceptsdIndexedById.containsKey(strId));
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
            String[]    strParentIds,
            String[]    strAlternativeIds) throws Exception
    {
        // We check that there is no other concept with the same ID
        
        if (m_conceptsdIndexedById.containsKey(strId))
        {
            String strError = "There is other concept with the ID = " + strId;
            throw (new Exception(strError));
        }
        
        // We create the concept and isnert in the ontology
        
        OboConcept concept = new OboConcept(this, strId, strNamespace,
                                    strName, strParentIds, strAlternativeIds);
        
        // We add the conepot to the overall collection
        
        m_conceptsdIndexedById.put(strId, concept);
        
        // We register all alternative 'alias' IDs
        
        for (int i = 0; i < strAlternativeIds.length; i++)
        {
            m_conceptsdIndexedById.put(strAlternativeIds[i], concept);
        }
        
        // We return the result
        
        return (concept);
    }
    
    /**
     * This function returns the taxonomy associated to a given namespace.
     * @return 
     */
    
    public ITaxonomy getTaxonomy()
    {
        return (m_taxonomy);
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
     * This function retrieves the set of taxonomy vertexes corresponding
     * to the OBO concept set.
     * @param strOBOconceptIds
     * @return 
     */
    
    @Override
    public Set<IVertex> getTaxonomyNodesForOBOterms(String[] strOBOconceptIds)
    {
        // We initialize the output
        
        HashSet<IVertex> vertexes = new HashSet<>();
        
        // We retrieve the vertexes for each OBO concept term
        
        for (int i = 0; i < strOBOconceptIds.length; i++)
        {
            // We try to retrieve the associated OBO concept
            
            IOboConcept concept = getConceptById(strOBOconceptIds[i]);
            
            // If the OBO concept exists, we retrieve the taxonomy vertex
            
            if (concept != null) vertexes.add(m_taxonomy.getVertexes().getById(concept.getTaxonomyNodeId()));
        }
        
        // We return the result
        
        return (vertexes);
    }
    
    /**
     * This fucntion builds the taxonomies assocyaed to the ontology
     */
    
    public void buildTaxonomy(
        boolean useAncestorsCaching) throws Exception
    {
        // We create the collection of namespaces
        
        HashSet<String> namespaces = new HashSet<>();
        
        // We search for all namespaces
        
        for (OboConcept concept: m_conceptsdIndexedById.values())
        {
            namespaces.add(concept.getNamespace());
        }
        
        // We create a taxonomy for each namespace
        
        m_taxonomy = TaxonomyFactory.createBlankTaxonomy();
        
        // We createt he virtual root
        
        m_taxonomy.addVertex(0L, new Long[0]);
        
        Long[] rootParent = {0L};
        
        // We set the traversing flag and enqueue the concepts
        
        LinkedList<OboConcept> pending = new LinkedList<>(m_conceptsdIndexedById.values());
        
        for (OboConcept concept: m_conceptsdIndexedById.values())
        {
            concept.setVisited(false);
            pending.add(concept);
        }
        
        // We insert all concepts into their corresponding taxonomies
        
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
            
            if (parentsVisited && !concept.getVisited())
            {
                // We get the parents ID
                
                String[] parents = concept.getParentsId();
                
                Long[] parentsId = new Long[parents.length];
                
                for (int i = 0; i < parents.length; i++)
                {
                    parentsId[i] = m_conceptsdIndexedById.get(parents[i]).getTaxonomyNodeId();
                }
                
                // We insert the vertex in the taxonomy
                
                Long conceptNodeId = Integer.toUnsignedLong(m_taxonomy.getVertexes().getCount());
                
                // We check if the vertex is a root of an independent taxonomy
                
                if (parentsId.length == 0) parentsId = rootParent;
                
                // We insert the vertex
                
                IVertex vertex = m_taxonomy.addVertex(conceptNodeId, parentsId);
                
                // We set the Tag name to the OBO node ID
                
                vertex.setStringTag(concept.getId());
                
                // We set the node ID
                
                concept.setTaxonomyNodeId(conceptNodeId);
                concept.setVisited(true);
            }
        }
        
        // We compute all cached information
        
        m_taxonomy.computesCachedAttributes();
        if (useAncestorsCaching) m_taxonomy.computeCachedAncestorSet(false);
    }
    
    /**
     * This function checks the topology of all imported taxonomies
     */
    
    public void checkTopology() throws Exception
    {
        // We print the name of the ontology
        
        System.out.println("--- HESML OBO ontology ----");
        System.out.println("Ontology name = " + m_strName);
        
        // Information message

        System.out.println("Unified taxonomy, #nodes = "
                + m_taxonomy.getVertexes().getCount());

        // We close the information block
        
        System.out.println("Overall indexed #concepts = " + m_conceptsdIndexedById.size());
        System.out.println("----------------------------");
    }
    
    /**
     * This function releases the ontology resources
     */
    
    public void clear()
    {
        m_conceptsdIndexedById.clear();
        m_taxonomy.clear();
    }
}
