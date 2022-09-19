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

package hesml.taxonomyreaders.mesh.impl;

import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.mesh.IMeSHDescriptor;
import hesml.taxonomyreaders.mesh.IMeSHOntology;

/**
 * This interface implements a MeSH Descriptor Data record (MeSH concept).
 * A MeSH descriptor (concept) could be present in multiple MeSH trees.
 * @author j.lastra
 */

class MeSHDescriptor implements IMeSHDescriptor
{
    /**
     * Unique key ID for the MeSH descriptor
     */
    
    private String  m_strDescriptorId;
    
    /**
     * Main name used to refer the MeSh concept
     */
    
    private String  m_strPreferredName;
    
    /**
     * Identitiers of the tree nodes associated to this MeSH concept.
     * All identifiers use a hierarchical coding.
     */
    
    private String[]    m_strTreeNodeIds;
    
    /**
     * Ontology which belongs the current conept.
     */
    
    private MeSHOntology    m_ownerOntology;
    
    /**
     * Cached taxonomy nodes
     */
    
    private IVertex[] m_Vertexes;

    /**
     * Constructor
     * @param ownerOntology
     * @param strDescriptorId
     * @param strPreferredName
     * @param strTreeNodeIds 
     */
    
    MeSHDescriptor(
            MeSHOntology    ownerOntology,
            String          strDescriptorId,
            String          strPreferredName,
            String[]        strTreeNodeIds)
    {
        m_ownerOntology = ownerOntology;
        m_strDescriptorId = strDescriptorId;
        m_strPreferredName = strPreferredName;
        m_strTreeNodeIds = strTreeNodeIds;
        m_Vertexes = null;
    }
    
    /**
     * This function returns the taxonomy nodes
     * @return 
     */
    
    @Override
    public IVertex[] getTaxonomyNodes()
    {
        return (m_Vertexes);
    }
    
    /**
     * This function returns a representative string of the object
     * @return 
     */
    
    @Override
    public String toString()
    {
        return (m_strDescriptorId);
    }
    
    /**
     * This funtion returns the ontology containing this concept.
     * @return 
     */
    
    @Override
    public IMeSHOntology getOntology()
    {
        return (m_ownerOntology);
    }
    
    /**
     * This function returns the 
     * @return 
     */
    
    @Override
    public Long[] getTaxonomyNodesId()
    {
        // We create the output vector with vertexes ID
        
        Long[] vertexesId = new Long[m_strTreeNodeIds.length];
        
        // We retrieve all vertexes ID
        
        for (int i = 0; i < m_strTreeNodeIds.length; i++)
        {
            vertexesId[i] = m_ownerOntology.getVertexIdForTreeNodeId(m_strTreeNodeIds[i]);
        }
        
        // We return the result
        
        return (vertexesId);
    }
    
    /**
     * This fucntion stores the vertexes of the taxonomy
     */
    
    void setCachedVertexes()
    {
        // We create the output vector with vertexes ID
        
        m_Vertexes = new IVertex[m_strTreeNodeIds.length];
        
        // We get the taxonomy
        
        IVertexList taxonomyVertexes = m_ownerOntology.getTaxonomy().getVertexes();
        
        // We retrieve all vertexes ID
        
        for (int i = 0; i < m_strTreeNodeIds.length; i++)
        {
            m_Vertexes[i] = taxonomyVertexes.getById(m_ownerOntology.getVertexIdForTreeNodeId(m_strTreeNodeIds[i]));
        }
    }
    
    /**
     * This function returns the unique MeSH descriptor ID
     * @return 
     */
    
    @Override
    public String getMeSHDescriptorId()
    {
        return (m_strDescriptorId);
    }
    
    /**
     * This function states the equality only based on the vertex ID,
     * not the attributes.
     * @param obj
     * @return 
     */
    
    @Override
    public boolean equals(Object obj)
    {
        // We initialize the output
        
        boolean result = (obj != null)
                && (getClass() == obj.getClass())
                && (m_strDescriptorId.equals(((MeSHDescriptor)obj).m_strDescriptorId));
        
        // We return the result
        
        return (result);
    }
    
    /**
     * Every MeSH descriptor (concept) could be rerpesented in multiple
     * tree-like taxonomies. Thus, this fuction returns all taxonomy nodes
     * associated to the current MeSH descriptor (concept)
     * @return 
     */
    
    @Override
    public String[] getTreeNodeIds()
    {
        return (m_strTreeNodeIds);
    }
    
    /**
     * This fucntion returns the preferred name for this concept.
     * @return 
     */
    
    @Override
    public String getPreferredName()
    {
       return (m_strPreferredName); 
    }
}
