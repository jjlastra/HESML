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

import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.snomed.ISnomedConcept;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import hesml.taxonomyreaders.snomed.ISnomedCtOntology;
import java.util.Arrays;

/**
 * This class implements the SNOMED-CT database.
 * @author j.lastra
 */

class SnomedCtOntology implements ISnomedCtOntology
{
    /**
     * Taxonomy enconding the SNOMED-CT 'is-a' graph.
     */
    
    private ITaxonomy   m_Taxonomy;
    
    /**
     * Concepts indexed by their unique SNOMED ID
     */
    
    private final HashMap<Long, ISnomedConcept>    m_ConceptsIndexedById;
    
    /**
     * SNOMED-CT concepts collection
     */
    
    private final SnomedConcept[]    m_SnomedConcepts;
    
    /**
     * Table with the pairs (CUI,SNOMEd_Id)
     */
    
    private final HashMap<String, ISnomedConcept[]> m_ConceptsIndexedByCUI;
    
    /**
     * SNOMED-CT concepts for each term. This is the inverted map from
     * term to SNOMED-CT concepts.
     */
    
    private final HashMap<String, ArrayList<Long>>  m_ConceptsIndexedByTerm;

    /**
     * Constructor. The concepts are sortted from the root with a total order
     * relation.
     * @param concepts Sorted list of concepts
     */
    
    SnomedCtOntology(
            ArrayList<SnomedConcept>                    concepts,
            HashMap<String, HashSet<ISnomedConcept>>    mapCuiToSnomedConcepts,
            boolean                                     useAncestorsCaching) throws Exception
    {
        // We initialize the collections
        
        m_ConceptsIndexedById = new HashMap<>(concepts.size());
        m_SnomedConcepts = new SnomedConcept[concepts.size()];
        m_ConceptsIndexedByTerm = new HashMap<>(concepts.size());
        m_ConceptsIndexedByCUI = new HashMap<>(mapCuiToSnomedConcepts.size());
        
        // We copy the concepts
        
        concepts.toArray(m_SnomedConcepts);
        
        // We build the table of CUI->ISnomedConcept[] isntead of keeping
        // the ArrayList for the concepts which demands more mmeory
        
        for (String strCUI: mapCuiToSnomedConcepts.keySet())
        {
            // We obtain the SNOMED concepts associated to the UMLS CUI
            
            HashSet<ISnomedConcept> conceptsOfCUI = mapCuiToSnomedConcepts.get(strCUI);
            
            // We only register CUIs with at least 1 SNOMed CONCEPT.
            // During the reading of the CUI file could appear CUIs
            // with inactive SNOMED concepts.
            
            if (conceptsOfCUI.size() > 0)
            {
                // We copy the concepts and index them

                ISnomedConcept[] cuiConcepts = new ISnomedConcept[conceptsOfCUI.size()];

                conceptsOfCUI.toArray(cuiConcepts);
                m_ConceptsIndexedByCUI.put(strCUI, cuiConcepts);
            }
            
            // We release the input list
            
            conceptsOfCUI.clear();
        }
        
        // We release the input CUI mapping
        
        mapCuiToSnomedConcepts.clear();
        
        // We connect all the concepts to this instance

        indexingConcepts();
        
        // We cretae the taxonomy
        
        buildTaxonomy(useAncestorsCaching);
    }
    
    /**
     * This function returns the SNOMED concept associated to the CUI
     * or null if it is not found in the SNOMED database.
     * @param umlsConcetCUI
     * @return 
     */
    
    @Override
    public ISnomedConcept[] getConceptsForUmlsCUI(String umlsConceptCUI)
    {
        // We initialize the output
        
        ISnomedConcept[] concepts = m_ConceptsIndexedByCUI.get(umlsConceptCUI);
        
        // We filter the case in which the CUI is not contained in SMOD
        
        if (concepts == null) concepts = new ISnomedConcept[0];
        
        // We return the result
        
        return (concepts);
    }
    
    /**
     * This function returns all vertexes associated to the SNOMED-CT
     * concepts evoked by a specific UMLS CUI.
     * @param umlsConceptCUI
     * @return 
     */
    
    @Override
    public IVertex[] getTaxonomyVertexesForUmlsCUI(String umlsConceptCUI)
    {
        // We initialize the output
        
        ISnomedConcept[] concepts = m_ConceptsIndexedByCUI.get(umlsConceptCUI);
        
        // We initialize the output
        
        IVertex[] vertexes = (concepts != null) ?
                            new IVertex[concepts.length] :
                            new IVertex[0];
        
        // We retrieve all vertex
        
        if (vertexes.length > 0)
        {
            // We get the taxonomy vertexes
            
            IVertexList taxonomyVertexes = m_Taxonomy.getVertexes();
            
            // We get the vertexes
            
            for (int i = 0; i < concepts.length; i++)
            {
                vertexes[i] = taxonomyVertexes.getById(concepts[i].getSnomedId());
            }
        }

        // We return the result
        
        return (vertexes);
    }
    
    /**
     * This function returns the SNOMED concept IDs associated to the CUI
     * or an empty array if it is not found in the SNOMED database.
     * @param umlsConceptCUI
     * @return 
     */
    
    @Override
    public Long[] getConceptIdsForUmlsCUI(String umlsConceptCUI)
    {
        // We initialize the output
        
        ISnomedConcept[] concepts = m_ConceptsIndexedByCUI.containsKey(umlsConceptCUI) ?
                                    m_ConceptsIndexedByCUI.get(umlsConceptCUI) :
                                    new ISnomedConcept[0];
        
        // We get all SNOMED ids of the first set

        Long[] snomedIds = new Long[concepts.length];

        for (int i = 0; i < concepts.length; i++)
        {
            snomedIds[i] = concepts[i].getSnomedId();
        }
        
        // We return the result
        
        return (snomedIds);
    }
    
    /**
     * This function returns the SNOMED concepts associated to the CUIs
     * or null if they are not found in the SNOMED database.
     * @param umlsConcetCUI
     * @return 
     */
    
    @Override
    public ISnomedConcept[] getConceptsForUmlsCUI(String[] umlsConceptCUIs)
    {
        // We create an auxiliary collection
        
        HashSet<ISnomedConcept> conceptSet = new HashSet<>();
        
        // We retirve all concepts
        
        for (int i = 0; i < umlsConceptCUIs.length; i++)
        {
            ISnomedConcept[] currentIdConcepts = getConceptsForUmlsCUI(umlsConceptCUIs[i]);
            
            for (ISnomedConcept concept: currentIdConcepts)
            {
                if (!conceptSet.contains(concept)) conceptSet.add(concept);
            }
        }
        
        // We copy the concepts into a vector and destroy the axuliary set
        
        ISnomedConcept[] concepts = (ISnomedConcept[]) conceptSet.toArray();
        conceptSet.clear();
        
        // We return the result
        
        return (concepts);
    }
    
    /**
     * This function builds all the indexing maps.
     */
    
    private void indexingConcepts()
    {
        // We connect all the concepts to this instance
        
        for (SnomedConcept concept : m_SnomedConcepts)
        {
            // Index the concepts by its ID
            
            m_ConceptsIndexedById.put(concept.getSnomedId(), concept);
            
            // We buld the indexed collection of concepts by terms.
            
            for (String strTerm: concept.getTerms())
            {
                // We get the list of concept IDs of the term
                
                ArrayList<Long> snomedIds = m_ConceptsIndexedByTerm.containsKey(strTerm) ?
                                            m_ConceptsIndexedByTerm.get(strTerm) :
                                            new ArrayList<>();
                
                // We insert the id array fir the first time
                
                if (snomedIds.size() == 0) m_ConceptsIndexedByTerm.put(strTerm, snomedIds);
                
                // We insert the conept ID into the indexd list of the term
                
                snomedIds.add(concept.getSnomedId());
            }
        }
    }
    
    /**
     * This function buils the SNOMED-CT taxonomy.
     * @param useAncestorsCaching 
     * @return 
     */
    
    private void buildTaxonomy(boolean useAncestorsCaching) throws Exception
    {
        // Debugging message
        
        System.out.println("Building the SNOMED-CT taxonomy ("
                + getConceptCount() + ") nodes");
        
        // We create the graph
        
        m_Taxonomy = hesml.taxonomy.impl.TaxonomyFactory.createBlankTaxonomy(getConceptCount());
        
        // We create a vertex into the taxonomy for each comcept.
        
        for (SnomedConcept concept: m_SnomedConcepts)
        {
            // We add the vertex to the taxonomy with the same SNOMED-CT ID
            // than its qasaociated ISnomedConcept
            
            m_Taxonomy.addVertex(concept.getSnomedId(), concept.getParentsSnomedId());
            
            // We connect the concept to this database. Omce it is done, the
            // concept clears its collection of parent IDs because this information
            // can be retrieved from the taxonomy.
            
            concept.setDatabase(this);
        }
        
        // We compute all cached information
        
        m_Taxonomy.computesCachedAttributes();
        
        // From HESML V1R5, we support the caching of the ancestor sets in
        // each vertex of the taxonomy with the aim of speeding up the 
        // computation of MICA vertex in high-complexity ontologies as
        // SNOMED-CT.
        
        if (useAncestorsCaching) m_Taxonomy.computeCachedAncestorSet(false);
    }
    
    /**
     * This fucntion returns the HESML taxonomy encoding the SNOMED-CT 'is-a'
     * ontology.
     * @return In-memory HESML taxonomy encoding the 'is-a' SNOMED-CT graph
     */
    
    @Override
    public ITaxonomy getTaxonomy()
    {
        return (m_Taxonomy);
    }
    
    /**
     * This function implements the Iterable interface.
     * @return Iterator
     */
    
    public Iterator<ISnomedConcept> iterator()
    {
        return (new ConceptArrayIterator(m_SnomedConcepts));
    }
    
    /**
     * This functions determines if the input term is present in the DB.
     * @param strTerm
     * @return True if the word is contained in the DB
     */
    
    @Override
    public boolean contains(String strTerm)
    {
        return (m_ConceptsIndexedByTerm.containsKey(strTerm));
    }

    /**
     * This function returns the number of concepts in the database.
     * @return 
     */
    
    @Override
    public int getConceptCount()
    {
        return (m_ConceptsIndexedById.size());
    }
    
    /**
     * This function returns the unique CUID for all the concepts
     * associated to the input term.
     * @param strTerm The term whose concepts will be retrieved
     * @return A set of concepts CUID
     * @throws java.lang.Exception Unexpected error
     */

    @Override
    public ISnomedConcept[] getTermConcepts(
        String  strTerm) throws Exception
    {
        // We declare the outpuy
        
        ISnomedConcept[] concepts;
        
        // We check that the input word is contained in WordNet
        
        if (contains(strTerm))
        {
            // We get the synsets for the word
            
            ArrayList<Long> termConcepts = m_ConceptsIndexedByTerm.get(strTerm);

            // We create the array to copy the synsets

            concepts = new ISnomedConcept[termConcepts.size()];

            // We copy the synsets

            for (int i = 0; i < termConcepts.size(); i++)
            {
                concepts[i] = m_ConceptsIndexedById.get(termConcepts.get(i));
            }
        }
        else
        {
            concepts = new ISnomedConcept[0];
        }
        
        // We return the result
        
        return (concepts);        
    }
    
    /**
     * This function returns a vector with the CUID values of the
     * concepts evoked by the input term.
     * @param strTerm Input term
     * @return A sequence of CUID values corresponding to the concepts
     * evoked by the input term
     * @throws Exception Unexpected error
     */
    
    @Override
    public Long[] getSnomedConceptIdsEvokedByTerm(
        String  strTerm) throws Exception
    {
        // We initialize the output
        
        Long[] snomedIds = new Long[0];
        
        // We check that the input word is contained in WordNet
        
        if (contains(strTerm))
        {
            // We get the synsets for the word

            ArrayList<Long> termConcepts = m_ConceptsIndexedByTerm.get(strTerm);

            // We create the arraya to copy the synsets

            snomedIds = new Long[termConcepts.size()];
            termConcepts.toArray(snomedIds);
        }
        
        // We return the result
        
        return (snomedIds);
    }
       
    /**
     * This function returns an ordered array with all concepts in the
     * database. Concepts are totally ordered from the root in a BFS mode.
     * @return 
     */
    
    @Override
    public ISnomedConcept[] getAllConcepts()
    {        
        return (m_SnomedConcepts);
    }
    
    /**
     * This function returns the concept associated to the input CUID
     * @param snomedId
     * @return The concept for this CUID
     */
    
    @Override
    public ISnomedConcept getConcept(Long snomedId)
    {
        return (m_ConceptsIndexedById.get(snomedId));
    }
    
    /**
     * Clear the database
     */
    
    @Override
    public void clear()
    {
        // We clear the mapping of synsets per word
        
        for (ArrayList<Long> concepts: m_ConceptsIndexedByTerm.values())
        {
            concepts.clear();
        }

        // We release the resources used by the concepts
        
        for (SnomedConcept concept: m_SnomedConcepts)
        {
            concept.clear();
        }
        
        // We clear the synset collections
        
        m_ConceptsIndexedByTerm.clear();
        m_ConceptsIndexedById.clear();
        m_Taxonomy.clear();
        m_ConceptsIndexedByCUI.clear();
    }
}

/**
 * This class implements an iterator on an aeeay of concepts
 * @author j.lastra
 */

class ConceptArrayIterator implements Iterator<ISnomedConcept>
{
    /**
     * SNOMED concept array
     */
    
    private final ISnomedConcept[] m_SnomedConcepts;
    
    /**
     * Reading cursor
     */
    
    private int m_ReadingPosition;
    
    /**
     * Constructor
     * @param concepts 
     */
    
    ConceptArrayIterator(ISnomedConcept[] concepts)
    {
        m_SnomedConcepts = concepts;
        m_ReadingPosition = 0;
    }
    
    /**
     * This function checks wheter there is pending concepts for reading.
     * @return 
     */
    
    @Override
    public boolean hasNext()
    {
        return (m_ReadingPosition < m_SnomedConcepts.length);
    }

    /**
     * This function returns the next concept in tue collection
     * @return 
     */
    
    @Override
    public ISnomedConcept next()
    {
        return (m_SnomedConcepts[m_ReadingPosition++]);
    }
}