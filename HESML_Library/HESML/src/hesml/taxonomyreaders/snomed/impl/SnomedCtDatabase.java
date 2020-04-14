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

import hesml.taxonomy.ITaxonomy;
import hesml.taxonomyreaders.snomed.ISnomedConcept;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import hesml.taxonomyreaders.snomed.ISnomedCtDatabase;

/**
 * This class implements the SNOMED-CT database.
 * @author j.lastra
 */

class SnomedCtDatabase implements ISnomedCtDatabase
{
    /**
     * Taxonomy enconding the SNOMED-CT 'is-a' graph.
     */
    
    private ITaxonomy   m_Taxonomy;
    
    /**
     * Concepts indexed by their unique CUID
     */
    
    private final HashMap<Long, ISnomedConcept>    m_ConceptsIndexedById;
    
    /**
     * SNOMED-CT concepts collection
     */
    
    private final SnomedConcept[]    m_SnomedConcepts;
    
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
    
    SnomedCtDatabase(
            ArrayList<SnomedConcept>    concepts,
            boolean                     useAncestorsCaching) throws Exception
    {
        // We initialize the collections
        
        m_ConceptsIndexedById = new HashMap<>(concepts.size());
        m_SnomedConcepts = new SnomedConcept[concepts.size()];
        m_ConceptsIndexedByTerm = new HashMap<>(concepts.size());
        
        // We copy the concepts
        
        concepts.toArray(m_SnomedConcepts);
        
        // We connect all the concepts to this instance

        indexingConcepts();
        
        // We cretae the taxonomy
        
        buildTaxonomy(useAncestorsCaching);
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
        
        if (useAncestorsCaching) m_Taxonomy.computeCachedAncestorSet();
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
    
    private final ISnomedConcept[] m_Concepts;
    
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
        m_Concepts = concepts;
        m_ReadingPosition = 0;
    }
    
    /**
     * This function checks wheter there is pending concepts for reading.
     * @return 
     */
    
    @Override
    public boolean hasNext()
    {
        return (m_ReadingPosition < m_Concepts.length);
    }

    /**
     * This function returns the next concept in tue collection
     * @return 
     */
    
    @Override
    public ISnomedConcept next()
    {
        return (m_Concepts[m_ReadingPosition++]);
    }
}