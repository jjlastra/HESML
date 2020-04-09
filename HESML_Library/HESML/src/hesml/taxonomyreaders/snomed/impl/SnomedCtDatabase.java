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
     * Concepts indexed by their unique CUID
     */
    
    private HashMap<Long, ISnomedConcept>    m_ConceptsIndexedById;
    
    /**
     * SNOMED-CT concepts collection
     */
    
    private ISnomedConcept[]    m_SnomedConcepts;
    
    /**
     * SNOMED-CT concepts for each term. This is the inverted map from
     * term to SNOMED-CT concepts.
     */
    
    private HashMap<String, ArrayList<ISnomedConcept>>  m_ConceptsIndexedByTerm;

    /**
     * Constructor. The concepts are sortted from the root with a total order
     * relation.
     * @param concepts Sorted list of concepts
     */
    
    SnomedCtDatabase(ArrayList<SnomedConcept> concepts)
    {
        // We initialize the collections
        
        m_ConceptsIndexedById = new HashMap<>(concepts.size());
        m_SnomedConcepts = new ISnomedConcept[concepts.size()];
        m_ConceptsIndexedByTerm = new HashMap<>(concepts.size());
        
        // We copy the concepts
        
        concepts.toArray(m_SnomedConcepts);
        
        // We coonect all the concepts to this instance
        
        for (SnomedConcept concept : concepts)
        {
            concept.setDatabase(this);
            m_ConceptsIndexedById.put(concept.getSnomedId(), concept);
        }
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
            
            ArrayList<ISnomedConcept> termConcepts = m_ConceptsIndexedByTerm.get(strTerm);

            // We create the array to copy the synsets

            concepts = new ISnomedConcept[termConcepts.size()];

            // We copy the synsets

            termConcepts.toArray(concepts);
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
        Long[] cuids;    // Returned value
        
        // We check that the input word is contained in WordNet
        
        if (contains(strTerm))
        {
            // We get the synsets for the word

            ArrayList<ISnomedConcept> termConcepts = m_ConceptsIndexedByTerm.get(strTerm);

            // We create the arrya to copy the synsets

            cuids = new Long[termConcepts.size()];

            // We copy the synset ID values

            int i = 0;

            for (ISnomedConcept concept: termConcepts)
            {
                cuids[i++] = concept.getSnomedId();
            }
        }
        else
        {
            cuids = new Long[0];
        }
        
        // We return the result
        
        return (cuids);
    }
       
    /**
     * This function returns the concept associated to the input CUID
     * @param cuid
     * @return The concept for this CUID
     */
    
    @Override
    public ISnomedConcept getConcept(Long cuid)
    {
        return (m_ConceptsIndexedById.get(cuid));
    }
    
    /**
     * Clear the database
     */
    
    @Override
    public void clear()
    {
        // We clear the ampping of synsets per word
        
        for (ArrayList<ISnomedConcept> concepts: m_ConceptsIndexedByTerm.values())
        {
            concepts.clear();
        }
        
        // We clear the synset collections
        
        m_ConceptsIndexedByTerm.clear();
        m_ConceptsIndexedById.clear();
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