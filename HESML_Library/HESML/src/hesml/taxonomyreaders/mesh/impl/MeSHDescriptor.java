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

package hesml.taxonomyreaders.mesh.impl;

import hesml.taxonomyreaders.mesh.IMeSHDescriptor;

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
     * Constructor
     * @param strDescriptorId
     * @param strPreferredName
     * @param strTreeNodeIds 
     */
    
    MeSHDescriptor(
            String      strDescriptorId,
            String      strPreferredName,
            String[]    strTreeNodeIds)
    {
        m_strDescriptorId = strDescriptorId;
        m_strPreferredName = strPreferredName;
        m_strTreeNodeIds = strTreeNodeIds;
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
