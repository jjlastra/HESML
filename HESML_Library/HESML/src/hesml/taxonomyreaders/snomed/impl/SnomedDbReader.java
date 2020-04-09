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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import hesml.taxonomyreaders.snomed.ISnomedCtDatabase;

/**
 * This class is mainly esponsible to parse the SNOMED-CT databse files
 * and loading the taxonomy of concepts.
 * @author j.lastra
 */

class SnomedDbReader
{
    /**
     * Column offset for the main attributes extratec from concept and
     * relationsship files.
     */
    
    private static final int CONCEPT_ID = 0;
    private static final int CHILD_ID = 4;
    private static final int PARENT_ID = 5;
    private static final int ACTIVE_ID = 2;
    private static final int RELATIONSHIP_GROUP_ID = 6;
    
    /**
     * This fucntion loads a SNOMED-CT taxonomy into memory.
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @return 
     */
    
    static ISnomedCtDatabase loadDatabase(
            String strSnomedDir,
            String strSnomedDBconceptFileName,
            String strSnomedDBRelationshipsFileName) throws Exception
    {
        // We load the SNOMED RF2 file
        
        File snomedConceptFile = new File(strSnomedDir + "/" + strSnomedDBconceptFileName);
        File snomedRelationshipFile = new File(strSnomedDir + "/" + strSnomedDBRelationshipsFileName);
        
        // We chechk the existence of the path
        
        if (!snomedConceptFile.exists())
        {
            String strError = "This file doesn´t exist -> " + snomedConceptFile.getPath();
            throw (new Exception(strError));
        }

        if (!snomedRelationshipFile.exists())
        {
            String strError = "This file doesn´t exist -> " + snomedRelationshipFile.getPath();
            throw (new Exception(strError));
        }

        // We read the SNOMED-CT concepts
        
        HashMap<Long, SnomedConcept> concepts = readConcepts(snomedConceptFile);
        
        // We read the child/parent relationships
        
        readParentConcepts(snomedRelationshipFile, concepts);
        
        // We check the consistency of the data
        
        checkTaxonomyConsistency(concepts);
        
        // Wesort all concepts from the root to the leaves
        
        ArrayList<SnomedConcept> sortedConcepts = sortOntologyConcepts(concepts);
        
        // We insert the sorted concepts into the database
        
        SnomedCtDatabase snomedDatabase = new SnomedCtDatabase(sortedConcepts);

        // We release the auxiliary collections
        
        concepts.clear();
        sortedConcepts.clear();

        // We return the database
        
        return (snomedDatabase);
    }
    
    /**
     * This fucntion checks the existence of  a single root node.
     * @param indexedConcepts 
     */
    
    private static void checkTaxonomyConsistency(
        HashMap<Long, SnomedConcept>    indexedConcepts) throws Exception
    {
        // Debugging message
        
        System.out.println("Checking the topological information");
        
        // We compute the number of root nodes
        
        int rootConcepts = 0;
        
        for (SnomedConcept concept: indexedConcepts.values())
        {
            if (concept.getParentsSnomedId().length == 0) rootConcepts++;
        }
        
        // We check that there is only one root concept
        
        if (rootConcepts != 1)
        {
            String strError = "The taxonomy has more than one root node";
            throw (new Exception(strError));
        }
    }
    
    /**
     * This function sorts all concepts from the root
     * @param indexedConcepts
     * @return 
     */
    
    private static ArrayList<SnomedConcept> sortOntologyConcepts(
            HashMap<Long, SnomedConcept>    indexedConcepts)
    {
        // We create the sorted list of concepts
        
        ArrayList<SnomedConcept> sortedConcepts = new ArrayList<>(indexedConcepts.size());
        
        // We cretae the pending queue
        
        LinkedList<SnomedConcept> pending = new LinkedList<>();
        
        for (SnomedConcept concept: indexedConcepts.values())
        {
            concept.setVisited(false);
            pending.add(concept);
        }
        
        // We process the pending nodes
        
        while (!pending.isEmpty())
        {
            // We get the next synset to load
            
            SnomedConcept pendingConcept = pending.remove();
            
            // We check if all its parents have been visited
            
            boolean allParentsVisited = true;
            
            for (Long parentId: pendingConcept.getParentsSnomedId())
            {
                if (!indexedConcepts.get(parentId).getVisited())
                {
                    allParentsVisited = false;
                    break;
                }
            }
            
            // We put the current concept in the list (totally order) or we
            // enqueue it again when any parent has not been visited yet.
            
            if (allParentsVisited)
            {
                pendingConcept.setVisited(true);
                sortedConcepts.add(pendingConcept);
            }
            else
            {
                pending.add(pendingConcept);
            }
        }
        
        // We return the result
        
        return (sortedConcepts);
    }
    
    /**
     * This function read the parents of all conepts.
     * @param concepts 
     */
    
    private static void readParentConcepts(
            File                            snomedRelationshipFile,
            HashMap<Long, SnomedConcept>    concepts) throws FileNotFoundException
    {
        // We open the file for reading
        
        Scanner reader = new Scanner(snomedRelationshipFile);
        System.out.println("Loading " + snomedRelationshipFile);
                
        // We skip the header line
        
        String strHeaderLine = reader.nextLine();
        
        // We read the relationship lines
        
        do
        {
            // We read the next relationship entry
            
            String strLine = reader.nextLine();
            
            // We extract the attributes of the relationship
            
            String[] strAttributes = strLine.split("\t");
            
            boolean active = strAttributes[2].equals("1");
            Long childSnomedId = Long.parseLong(strAttributes[CHILD_ID]);
            Long parentSnomedId = Long.parseLong(strAttributes[PARENT_ID]);
            int relationshipGroup = Integer.parseInt(strAttributes[RELATIONSHIP_GROUP_ID]);
            
            // We retrieve the child concept and link it with its parent.
            // We check that the previous reading of the parent concept.
            
            if (active && (relationshipGroup == 0) && concepts.containsKey(childSnomedId))
            {
                concepts.get(childSnomedId).AddParent(parentSnomedId);
            }
            
        } while (reader.hasNextLine());
        
        // We close the database
        
        reader.close();
    }
    
    /**
     * This function parses the SNOMED-CT concept database
     * @param fileInfo
     * @return 
     */
    
    private static HashMap<Long, SnomedConcept> readConcepts(
            File    snomedFile) throws FileNotFoundException
    {
        // We open the file for reading
        
        Scanner reader = new Scanner(snomedFile);
        System.out.println("Loading " + snomedFile);
                
        // We skip the first line containing the headers.
        // We focus only on thereading of concept ID and term, because it
        // is the only information that we need. Thus, we reject
        // to read the full record for each concept.
        
        String strHeaderLine = reader.nextLine();
        
        // We create the temporary set of concepts
        
        HashMap<Long, SnomedConcept> concepts = new HashMap<>();
                
        // We read the concept lines
        
        do
        {
            // We extract the attribites of the concept

            String[] strAttributes = reader.nextLine().split("\t");

            // We get the needed attributes

            Long snomedId = Long.parseLong(strAttributes[CONCEPT_ID]);
            //String strTerm = strAttributes[7];
            boolean active = strAttributes[ACTIVE_ID].equals("1");

            // We create a new concept if it is active

            if (active)
            {
                // We insert the novel entity in the database
                
                SnomedConcept concept = new SnomedConcept(null, snomedId);
            
                // We insert the novel entity in the database
            
                if (!concepts.containsKey(concept.getSnomedId()))
                {
                    concepts.put(concept.getSnomedId(), concept);
                }
            }
            
        } while (reader.hasNextLine());
        
        // We close the database
        
        reader.close();

        // We sort the synsets for the creation of the graph
        
        return (concepts);
    }
}
