/*
 * Copyright (C) 2016-2018 Universidad Nacional de Educación a Distancia (UNED)
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
 *
 */

package hesml.taxonomy.impl;

/**
 * This class represents a node record in the HESML taxonomy file format.
 * @author Juan Lastra-Díaz
 */

class TaxonomyNodeRecord
{
    /**
     * Node record unique ID
     */
    
    private final Long m_IdNode;
    
    /**
     * Parents ID
     */
    
    private final Long[]   m_ParentsID;
    
    /**
     * Visiting flag
     */
    
    private boolean m_Visited;
    
    /**
     * Taxonomy node record 
     * @param strLine 
     */
    
    TaxonomyNodeRecord(String   strLine)
    {
        String[]   strFields;  // Line decomposition
        
        // We set the visiting field
        
        m_Visited = false;
        
        // We get all the fields for this node
        
        strFields = strLine.split(";");
        
        // The first field is the ID for the node
        
        m_IdNode = Long.parseUnsignedLong(strFields[0]);
        
        // We read the parents
        
        m_ParentsID = new Long[strFields.length - 1];
        
        // We read the parents
        
        for (int i = 0; i < strFields.length - 1; i++)
        {
            m_ParentsID[i] = Long.parseUnsignedLong(strFields[i + 1]);
        }
    }
    
    /**
     * This function returns the value of the visiting
     * flag used to traverse the WordNet DB graph.
     * @return The value of the visiting flag.
     */
    
    boolean getVisited()
    {
        return (m_Visited);
    }
    
    /**
     * This function sets the values of the visited flag.
     * @param visited 
     */
    
    void setVisited(boolean visited)
    {
        m_Visited = visited;
    }
    
    /**
     * ID of the current node
     * @return 
     */
    
    Long getID()
    {
        return (m_IdNode);
    }
    
    /**
     * We return the parents ID
     * @return 
     */
    
    Long[] getParentIDs()
    {
        return (m_ParentsID);
    }
}
