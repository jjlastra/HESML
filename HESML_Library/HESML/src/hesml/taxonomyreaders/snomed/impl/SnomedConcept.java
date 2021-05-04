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
    
    private final ArrayList<String>   m_termsOfConcept;
    
    /**
     * Parents of the current concept
     */
    
    private final ArrayList<Long>  m_parentSnomedIds;
    
    /**
     * Concept unique CUID
     */
    
    private final Long m_conceptSnomedCuid;
    
    /**
     * Traversing flag
     */
    
    private boolean m_Visited;
    
    /**
     * Owner SNOMED-CT DB
     */
    
    private SnomedCtOntology   m_ownerDB;
    
    /**
     * ID of the vertex corresponding to the current SNOMED-CT
     * concept within the SNOMED taxonomy.
     */
    
    private Long m_taxonomyVertexId;
    
    /**
     * Constructor
     * @param ownerDB
     * @param snomedId
     */
    
    SnomedConcept(
            SnomedCtOntology    ownerDB,
            Long                snomedId)
    {
        m_ownerDB = ownerDB;
        m_conceptSnomedCuid = snomedId;
        m_termsOfConcept = new ArrayList<>();
        m_parentSnomedIds = new ArrayList<>();
        m_Visited = false;
        m_taxonomyVertexId = -1L;
    }

    /**
     * This function returns the ID of the taxonomy vertex
     * corresponding to this concept in the SNOMED-CT taxonomy.
     * @return 
     */
    
    @Override
    public Long getTaxonomyNodeId()
    {
        return (m_taxonomyVertexId);
    }
    
    /**
     * This function sets the taxonomy node Id associated to this object
     * @param nodeId 
     */
    
    void setTaxonomyNodeId(Long nodeId)
    {
        m_taxonomyVertexId = nodeId;
    }
    
    /**
     * This function  returns a string representing the object
     * @return 
     */
    
    @Override
    public String toString()
    {
        return (m_conceptSnomedCuid.toString());
    }
    
    /**
     * This function sets the owner database.
     * @param database 
     */
    
    void setDatabase(
            SnomedCtOntology database)
    {
        m_ownerDB = database;        
    }
    
    /**
     * This function releases the object
     */
    
    void clear()
    {
        m_termsOfConcept.clear();
        m_parentSnomedIds.clear();
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
        
        // We copy the parent IDs
        
        for (Long parentSnomedId: m_parentSnomedIds)
        {
            parents.add(m_ownerDB.getConcept(parentSnomedId));
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
        return (m_ownerDB);
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
                && (this.m_conceptSnomedCuid == ((SnomedConcept)obj).m_conceptSnomedCuid);
        
        // We return the result
        
        return (result);
    }
    
    /**
     * This function checks if the input concept is a parent concept
     * for the current Synset
     * @param snomedCuid
     * @return 
     */
    
    @Override
    public boolean isParent(Long snomedCuid)
    {
        // We initialize the result
        
        boolean parent = false;
        
        // We check if the synset is a parent of the current synset
        
        for (Long parentCuid: m_parentSnomedIds)
        {
            if (Objects.equals(parentCuid, snomedCuid))
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
     * @param snomedCuid
     * @return 
     */
    
    @Override
    public boolean isChild(Long snomedCuid)
    {
        // We initialize the output
        
        boolean isChild = false;
        
        // We retrieve the synset from DB
        
        ISnomedConcept child = m_ownerDB.getConcept(snomedCuid);
        
        // We check the query
        
        if (child != null) isChild = child.isParent(m_conceptSnomedCuid);
        
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
        
        String[] strTerms = new String[m_termsOfConcept.size()];
        
        // We copy the terms to the array
        
        strTerms = m_termsOfConcept.toArray(strTerms);
        
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
        return (m_conceptSnomedCuid);
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
     * @param parentSnomedCuid 
     */
    
    void AddParent(
           Long    parentSnomedCuid)
    {
        m_parentSnomedIds.add(parentSnomedCuid);
    }
    
    /**
     * This function adds one term to the concept.
     * @param strTerm 
     */
    
    void addTerm(
            String  strTerm)
    {
        m_termsOfConcept.add(strTerm);
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
        // We create the output vector
        
        Long[] parentSnomedIds = new Long[m_parentSnomedIds.size()];
                
        // We copy the IDs
        
        parentSnomedIds = m_parentSnomedIds.toArray(parentSnomedIds);
        
        // We return the result
        
        return (parentSnomedIds);
    }
    
    /**
     * This function returns the taxonomy ID values from the
     * parent concepts of the current concept.
     * @return The taxonomy ID of the parent SNOMED concepts
     */

    @Override
    public Long[] getParentTaxonomyNodesId() throws Exception
    {
        // We define the output variable
        
        Long[] parentTaxonomyIds = new Long[m_parentSnomedIds.size()];
            
        // We retrieve the taxonomy node ID from the parent concepts. However,
        // we must check that this object is linked to the ontology (m_ownerDB),
        // other wise we retuirn the unassigned node ID (-1).
        
        int i = 0;

        for (Long snomedParentId: m_parentSnomedIds)
        {
            parentTaxonomyIds[i++] = (m_ownerDB != null) ? m_ownerDB.getConcept(snomedParentId).getTaxonomyNodeId() : -1;
        }
        
        // We return the result
        
        return (parentTaxonomyIds);
    }
}
