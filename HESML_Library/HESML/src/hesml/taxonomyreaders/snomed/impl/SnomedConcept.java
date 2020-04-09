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

import hesml.taxonomyreaders.snomed.ISnomedConcept;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class implements a SNOMED-CT concept.
 * @author j.lastra
 */

class SnomedConcept implements ISnomedConcept
{
    /**
     * Terms associated to this concept
     */
    
    private final ArrayList<String>   m_TermsOfConcept;
    
    /**
     * Parents of the current concept
     */
    
    private final ArrayList<Long>  m_ParentsCuid;
    
    /**
     * Concept unique CUID
     */
    
    private final Long m_ConceptCuid;
    
    /**
     * Traversing flag
     */
    
    private boolean m_Visited;
    
    /**
     * Owner SNOMED-CT DB
     */
    
    private SnomedCtDatabase   m_OwnerDB;
    
    /**
     * Constructor
     * @param ownerDB
     * @param snomedId
     */
    
    SnomedConcept(
            SnomedCtDatabase    ownerDB,
            Long                snomedId)
    {
        m_OwnerDB = ownerDB;
        m_ConceptCuid = snomedId;
        m_TermsOfConcept = new ArrayList<>();
        m_ParentsCuid = new ArrayList<>();
        m_Visited = false;
    }

    /**
     * This function sets the owner database.
     * @param database 
     */
    
    void setDatabase(
            SnomedCtDatabase database)
    {
        m_OwnerDB = database;
    }
    
    /**
     * This function releases the object
     */
    
    void clear()
    {
        m_TermsOfConcept.clear();
    }
    
    /**
     * This function returns the inmmediate parent concepts.
     * @return The set of parent concepts.
     */

    @Override
    public Set<ISnomedConcept> getParents()
    {
        // We create the set of parents
        
        HashSet<ISnomedConcept> parents = new HashSet<>();
        
        // We get the parents from the owner DB
        
        for (Long parentId: m_ParentsCuid)
        {
            parents.add(m_OwnerDB.getConcept(parentId));
        }
        
        // We return the results
        
        return (parents);
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
                && (this.m_ConceptCuid == ((SnomedConcept)obj).m_ConceptCuid);
        
        // We return the result
        
        return (result);
    }
    
    /**
     * This function checks if the input concept is a parent concept
     * for the current Synset
     * @param cuid
     * @return 
     */
    
    @Override
    public boolean isParent(Long cuid)
    {
        // We initialize the result
        
        boolean parent = false;
        
        // We check if the synset is a prent of the current synset
        
        for (Long parentCuid: m_ParentsCuid)
        {
            if (Objects.equals(parentCuid, cuid))
            {
                parent = true;
                break;
            }
        }
        
        // We return the result
        
        return (parent);
    }

    /**
     * This function checks if the concept is a direct child of the current one.
     * @param cuid
     * @return 
     */
    
    @Override
    public boolean isChild(Long cuid)
    {
        // We initialize the output
        
        boolean isChild = false;
        
        // We retrieve the synset from DB
        
        ISnomedConcept child = m_OwnerDB.getConcept(cuid);
        
        // We check the query
        
        if (child != null) isChild = child.isParent(m_ConceptCuid);
        
        // We return the result
        
        return (isChild);
    }

    /**
     * This function returns the terms evocating the concept
     * defined by the current SNOMED-CT concept.
     * @return The words associated to this synset.
     */
    
    @Override
    public String[] getTerms()
    {
        // We create a vector of terms
        
        String[] strTerms = new String[m_TermsOfConcept.size()];
        
        // We copy the terms to the array
        
        strTerms = m_TermsOfConcept.toArray(strTerms);
        
        // We return the result
        
        return (strTerms);
    }

    /**
     * Unique SNOMED-CT ID for the current concept
     * @return Unique Identifier for the concept in SNOMED-CT.
     */
    
    @Override
    public Long getCUID()
    {
        return (m_ConceptCuid);
    }

    /**
     * This function returns the value of the traversing
     * flag used to traverse the SNOMED-CT DB graph.
     * @return The value of the visiting flag.
     */
    
    @Override
    public boolean getVisited()
    {
        return (m_Visited);
    }

    /**
     * This function registers a new parent for the current concept
     * @param parentCuid 
     */
    
    void AddParent(
           Long    parentCuid)
    {
        m_ParentsCuid.add(parentCuid);
    }
    
    /**
     * This function adds one term to the concept.
     * @param strTerm 
     */
    
    void addTerm(
            String  strTerm)
    {
        m_TermsOfConcept.add(strTerm);
    }
    /**
     * This function sets the values of the traversing flag.
     * @param visited 
     */
    
    @Override
    public void setVisited(boolean visited)
    {
        m_Visited = visited;
    }

    /**
     * This function returns the CUID values from the
     * parent concepts of the current concept.
     * @return The parents of the synset.
     */
    
    @Override
    public Long[] getParentsCuid()
    {
        // We create the cuid array
        
        Long[] parentCuids = new Long[m_ParentsCuid.size()];
        
        // We return the result
        
        return (m_ParentsCuid.toArray(parentCuids));
    }
}
