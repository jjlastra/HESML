/*
 * Copyright (C) 2016 Universidad Nacional de Educación a Distancia (UNED)
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

// Java references

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// HESML references

import hesml.taxonomy.*;
import java.util.LinkedList;

/**
 * This class implements the reader of the HESML taxonomy file format.
 * The file format is a simple row-based file format to load a blank
 * taxonomy from file with testing purposes.
 * The file format is the following:
 * ID vertex [space parentID1 space parentID2 ... lastParentID].
 * For instance, a line like "4 1 2 3" encodes the vertex 4 whose
 * parents are the vertexes 1,2 and 3.
 * @author Juan Lastra-Díaz
 */

class TaxonomyReader
{
    /**
     * Node records
     */
    
    private final HashMap<Integer, TaxonomyNodeRecord>    m_IndexedNodes;
    
    /**
     * Nodes sorted 
     */
    
    private final ArrayList<TaxonomyNodeRecord>   m_Nodes;

    /**
     * Constructor
     */
    
    TaxonomyReader()
    {
        m_IndexedNodes = new HashMap<>();
        m_Nodes = new ArrayList<>();
    }
    
    /**
     * This function loads the taxonomy from a HESML taxonomy file.
     * @param strFilename
     * @return 
     */
    
    ITaxonomy loadHESMLfile(
            String  strFilename) throws Exception
    {
        Exception   error;      // Thrown error
        String      strError;   // Error message
        
        ITaxonomy   taxonomy;   // Returned value
        
        File    taxFile;    // File in format ".tax";
        Scanner scanner;    // File reaading
        
        TaxonomyNodeRecord  record; // New node record
        
        String  strLine;    // Line to read
        
        // We create the wordnet file
        
        taxFile = new File(strFilename);
        
        // We chechk the existence of the path
        
        if (!taxFile.exists())
        {
            strError = "The file doesn´t exist";
            error = new Exception(strError);
            throw (error);
        }
        
        // We crate a new taxonomy
        
        taxonomy = hesml.taxonomy.impl.TaxonomyFactory.createBlankTaxonomy();
        
        // We get the file scanner
        
        scanner = new Scanner(taxFile);
        
        // We read the file
        
        while (scanner.hasNextLine())
        {
            // We get the next line
            
            strLine = scanner.nextLine();
            strLine = strLine.trim();
            
            // We filter the lines in blank
            
            if (!strLine.equals(""))
            {
                record = new TaxonomyNodeRecord(strLine);
                m_IndexedNodes.put(record.getID(), record);
            }
        }
        
        // We close the file
        
        scanner.close();
        
        // We sort the nodes
        
        sortNodeRecords();
        
        // We fill the taxonomy
        
        for (TaxonomyNodeRecord node: m_Nodes)
        {
            taxonomy.addVertex(node.getID(), node.getParentIDs());
        }
        
        // We destroy the auxiliar list
        
        m_Nodes.clear();
        m_IndexedNodes.clear();
        
        // We return the result
        
        return (taxonomy);
    }

    /**
     * This function builds a total ordering of the vertexes starting from
     * the root.
     * @throws java.lang.InterruptedException
     */    

    private void sortNodeRecords() throws InterruptedException
    {
        LinkedList<TaxonomyNodeRecord>    pending;    // Processing queue
        
        TaxonomyNodeRecord   pendingNode;  // Synset to read
        
        boolean allParentsVisited; // Flag
        
        // We cretae the pending queue
        
        pending = new LinkedList<>();
        
        for (TaxonomyNodeRecord node: m_IndexedNodes.values())
        {
            node.setVisited(false);
            pending.add(node);
        }
        
        // We process the pending nodes
        
        while (!pending.isEmpty())
        {
            // We get the next synset to load
            
            pendingNode = pending.remove();
            
            // We check if all its parents have been visited
            
            allParentsVisited = true;
            
            for (Integer parentId: pendingNode.getParentIDs())
            {
                if (!m_IndexedNodes.get(parentId).getVisited())
                {
                    allParentsVisited = false;
                    break;
                }
            }
            
            // We put the current node in the list or enqueue again
            // if its parents have not been visited yet.
            
            if (allParentsVisited)
            {
                pendingNode.setVisited(true);
                m_Nodes.add(pendingNode);
            }
            else
            {
                pending.add(pendingNode);
            }
        }
    }
}
