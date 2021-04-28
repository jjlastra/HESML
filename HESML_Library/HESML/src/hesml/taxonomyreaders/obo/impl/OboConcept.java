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

package hesml.taxonomyreaders.obo.impl;

import hesml.taxonomyreaders.obo.IOboConcept;
import hesml.taxonomyreaders.obo.IOboOntology;

/**
 * This class represents an OBO concept, a node of any OBO namespace (taxonomy)
 * @author j.lastra
 */

class OboConcept implements IOboConcept
{
    /**
     * Preferred name for the concept
     */
    
    private String  m_strName;
    
    /**
     * Namespace
     */
    
    private String  m_strNamespace;
    
    /**
     * Unique ID of the concept
     */
    
    private String  m_strId;
    
    /**
     * Node ID
     */
    
    private Long    m_taxonomyNodeId;
    
    /**
     * Parents ID
     */
    
    private String[]  m_parentsId;
    
    /**
     * Alias for this concept
     */
    
    private String[]  m_alternativeIds;
    
    /**
     * Traversing flag
     */
    
    private boolean m_visited;
    
    /**
     * Parent ontology
     */
    
    private OboOntology m_ontology;

    /**
     * Constructor of OBO concepts
     * @param ontology
     * @param strId
     * @param strNamespace
     * @param strName
     * @param strParentIds
     * @param strAlternativeIds 
     */
    
    OboConcept(
            OboOntology ontology,
            String      strId,
            String      strNamespace,
            String      strName,
            String[]    strParentIds,
            String[]    strAlternativeIds)
    {
        m_strId = strId;
        m_ontology = ontology;
        m_strNamespace = strNamespace;
        m_strName = strName;
        m_parentsId = strParentIds;
        m_taxonomyNodeId = -1L;
        m_visited = false;
    }
    
    /**
     * This fucntion reurns the ID of the concept
     * @return 
     */
    
    @Override
    public String toString()
    {
        return (m_strId);
    }
    
    /**
     * This function sets the taxonomy node Id associated to this object
     * @param nodeId 
     */
    
    void setTaxonomyNodeId(Long nodeId)
    {
        m_taxonomyNodeId = nodeId;
    }
    
    /**
     * This function retuns a vector with the parent concepts.
     * @return 
     */
    
    @Override
    public IOboConcept[] getParentConcepts()
    {
        // We create the output vector
        
        IOboConcept[] parents = new IOboConcept[m_parentsId.length];
        
        // We retrieve the parents from the taxonomy
        
        for (int i = 0; i < m_parentsId.length; i++)
        {
            parents[i] = m_ontology.getConceptById(m_parentsId[i]);
        }
        
        // We return the result
        
        return (parents);
    }
    
    /**
     * This function returns the status of the traversing flag
     * @return 
     */
    
    boolean getVisited()
    {
        return (m_visited);
    }
    
    /**
     * This function sets the traversing value.
     * @param visited 
     */
    
    void setVisited(boolean visited)
    {
        m_visited = visited;
    }
    
    /**
     * This fucntion returns the parent Ids for the concept
     * @return 
     */
    
    @Override
    public String[] getParentsId()
    {
        return ((String[]) m_parentsId.clone());
    }

    /**
     * This fucntion returns the parent Ids for the concept
     * @return 
     */
    
    @Override
    public String[] getAlternativeIds()
    {
        return ((String[]) m_alternativeIds.clone());
    }
    
    /**
     * This function returns the container ontology
     * @return 
     */
    
    @Override
    public IOboOntology getOntology()
    {
        return (m_ontology);
    }
    
    /**
     * This function returns the unique ID of an OBO concept within
     * an OBO ontology.
     * @return 
     */
    
    @Override
    public String getId()
    {
        return (m_strId);
    }
    
    /**
     * This function returns the namespace which belongs this concept.
     * @return 
     */
    
    @Override
    public String getNamespace()
    {
        return (m_strNamespace);
    }
    
    /**
     * This function return the taxonomy node ID associated to this concept.
     * @return 
     */
    
    @Override
    public Long getTaxonomyNodeId()
    {
        return (m_taxonomyNodeId);
    }
    
    /**
     * Preferrerd name for the concept.
     * @return 
     */
    
    @Override
    public String getName()
    {
        return (m_strName);
    }
}
