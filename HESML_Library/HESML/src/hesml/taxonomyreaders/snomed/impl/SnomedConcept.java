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
 *
 */

package hesml.taxonomyreaders.snomed.impl;

import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.snomed.ISnomedConcept;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
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
    
    private final ArrayList<Long>  m_ParentSnomedIds;
    
    /**
     * Concept unique CUID
     */
    
    private final Long m_ConceptSnomedCuid;
    
    /**
     * Traversing flag
     */
    
    private boolean m_Visited;
    
    /**
     * Owner SNOMED-CT DB
     */
    
    private SnomedCtOntology   m_OwnerDB;
    
    /**
     * Constructor
     * @param ownerDB
     * @param snomedId
     */
    
    SnomedConcept(
            SnomedCtOntology    ownerDB,
            Long                snomedId)
    {
        m_OwnerDB = ownerDB;
        m_ConceptSnomedCuid = snomedId;
        m_TermsOfConcept = new ArrayList<>();
        m_ParentSnomedIds = new ArrayList<>();
        m_Visited = false;
    }

    /**
     * This function  returns a string representing the object
     * @return 
     */
    
    @Override
    public String toString()
    {
        return (m_ConceptSnomedCuid.toString());
    }
    
    /**
     * This function sets the owner database.
     * @param database 
     */
    
    void setDatabase(
            SnomedCtOntology database)
    {
        // We link the object to its owner database
        
        m_OwnerDB = database;
        
        // Omce it is done, the concept clears its collection of parent IDs
        // because this information can be directly retrieved from the taxonomy.
        // Thus, we can save a lot of memory in runtime.
           
        m_ParentSnomedIds.clear();
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
    public Set<ISnomedConcept> getParents() throws Exception
    {
        // We create the set of parents
        
        HashSet<ISnomedConcept> parents = new HashSet<>();
        
        // We check whether the concept is already linked to the owner DB
        // because in this case, the list of parents ID will be empty.
        // Once the taxonomy is cretaed, we use the relationships in the taxonomy
        // to hold the child/parent relationships. For this reason, we
        // release the parents ID lists to remove redundant information
        // and save memory.
        
        if (m_OwnerDB == null)
        {
            for (Long parentId: m_ParentSnomedIds)
            {
                parents.add(m_OwnerDB.getConcept(parentId));
            }
        }
        else
        {
            for (IVertex parentNode:
                    m_OwnerDB.getTaxonomy().getVertexes().getById(m_ConceptSnomedCuid).getParents())
            {
                parents.add(m_OwnerDB.getConcept(parentNode.getID()));
            }
        }
        
        // We return the results
        
        return (parents);
    }

    /**
     * This function returns the owener ontology
     * @return 
     */
    
    @Override
    public ISnomedCtOntology getOntology()
    {
        return (m_OwnerDB);
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
                && (this.m_ConceptSnomedCuid == ((SnomedConcept)obj).m_ConceptSnomedCuid);
        
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
        
        for (Long parentCuid: m_ParentSnomedIds)
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
        
        if (child != null) isChild = child.isParent(m_ConceptSnomedCuid);
        
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
    public Long getSnomedId()
    {
        return (m_ConceptSnomedCuid);
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
        m_ParentSnomedIds.add(parentCuid);
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
    public Long[] getParentsSnomedId() throws Exception
    {
        // We define the output variable
        
        Long[] parentSnomedIds;
                
        // We check whether the concept is already linked to the owner DB
        // because in this case, the list of parents ID will be empty.
        // Once the taxonomy is cretaed, we use the relationships in the taxonomy
        // to hold the child/parent relationships. For this reason, we
        // release the parents ID lists to remove redundant information
        // and save memory.
        
        if (m_OwnerDB == null)
        {
            parentSnomedIds = new Long[m_ParentSnomedIds.size()];
            m_ParentSnomedIds.toArray(parentSnomedIds);
        }
        else
        {
            // In this case, we rerieve the parent nodes from the taxonomy
            
            IVertex conceptNode = m_OwnerDB.getTaxonomy().getVertexes().getById(m_ConceptSnomedCuid);
            IVertexList parentNodes = conceptNode.getParents();

            // We copy the parent IDs
            
            parentSnomedIds = new Long[parentNodes.getCount()];
            
            int i = 0;
            
            for (IVertex parent: parentNodes)
            {
                parentSnomedIds[i++] = parent.getID();
            }
        }
        
        // We return the result
        
        return (parentSnomedIds);
    }
}
