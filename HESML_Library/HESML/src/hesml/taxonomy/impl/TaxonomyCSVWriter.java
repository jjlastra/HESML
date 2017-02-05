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

// Java referenes

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// HESML references

import hesml.taxonomy.*;

/**
 * This class writes the taxonomy information into a CSV file. The class
 * is used to generate all the files in the WNSimRep replications dataset
 * detailed in the paper below.
 * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
 * A new evaluation of Information Content models and ontology-based similarity
 * measures in WordNet with a framework for their exact replication.
 * Knowledge-Based Systems. Submitted for Publication.
 * 
 * @author Juan Lastra-Díaz
 */

class TaxonomyCSVWriter
{
    /**
     * This function saves the vertexes information into a CSV file.
     * @param strCSVFilename 
     */
    
    static void saveVertexesInfoToCSV(
            ITaxonomy   taxonomy,
            String      strCSVFilename,
            boolean     includeProb) throws IOException, Exception
    {
        BufferedWriter  writer; // Writer
        
        String  strLine;    // Line written
        
        char    sep = ';';  // Separator dield
        
        // We create a writer for the text file
        
        writer = new BufferedWriter(new FileWriter(strCSVFilename, false));
        
        // We write the titles
        
        strLine = "Synset ID" + sep
                + "Words" + sep
                + "ParentsID" + sep
                + "IC value" + sep;
        
        if (includeProb)
        {
            strLine += "Probability" + sep;
        }
        
        strLine += "Depth" + sep
                + "Longest depth" + sep
                + "# Children" + sep
                + "# Parents" + sep
                + "# Subsumers" + sep
                + "# Hyponyms" + sep
                + "# Leaves" + "\n";
        
        writer.write(strLine);
        
        // We write the info for each taxonomy node
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We get the vertx info into a linte
            
            strLine = getVertexLine(vertex, sep, includeProb);
            
            // We write the vertx line
            
            writer.write(strLine);
        }
        
        // We close the file
        
        writer.close();
    }
    
    /**
     * This function saves the edge information into a CSV file.
     * @param strCSVFilename 
     */
    
    static void saveEdgesInfoToCSV(
            ITaxonomy   taxonomy,
            String      strCSVFilename) throws IOException, Exception
    {
        BufferedWriter  writer; // Writer
        
        String  strLine;    // Line written
        
        char    sep = ';';  // Separator dield
        
        // We create a writer for the text file
        
        writer = new BufferedWriter(new FileWriter(strCSVFilename, false));
        
        // We write the titles
        
        strLine = "Source ID" + sep
                + "Target ID" + sep
                + "CondProbability" + sep
                + "IC weight" + "\n";
        
        writer.write(strLine);
        
        // We write the info for each taxonomy node
        
        for (IEdge edge: taxonomy.getEdges())
        {
            // We get the vertx info into a linte
            
            strLine = getEdgeLine(edge, sep);
            
            // We write the vertx line
            
            writer.write(strLine);
        }
        
        // We close the file
        
        writer.close();
    }
    
    /**
     * This function returns the fields of the vertex
     * as defined in a CSV line format.
     * @param vertex
     * @return 
     */
    
    private static String getVertexLine(
            IVertex vertex,
            char    sep,
            boolean includeProb) throws Exception
    {
        String  strInfo;    // Returned value
        
        // We write the titles
        
        strInfo = Integer.toString(vertex.getID()) + sep
                + vertex.getStringTag() + sep
                + convertToCsvField(vertex.getParents().getIDs()) + sep
                + Double.toString(vertex.getICvalue()) + sep;
        
        // We check whether we must include the probability attribute
        
        if (includeProb)
        {
            strInfo += Double.toString(vertex.getProbability()) + sep;
        }
                
        // We build the final line information
        
        strInfo += Integer.toString(vertex.getDepthMin()) + sep
                + Integer.toString(vertex.getDepthMax()) + sep
                + Integer.toString(vertex.getChildrenCount()) + sep
                + Integer.toString(vertex.getParentsCount()) + sep
                + Integer.toString(vertex.getNonInclusiveAncestorSetCount()) + sep
                + Integer.toString(vertex.getNonInclusiveHyponymSetCount()) + sep
                + Integer.toString(vertex.getNonInclusiveSubsumedLeafSetCount()) + "\n";
        
        // We return the result
        
        return (strInfo);
    }

    /**
     * This function returns the attribute of the input edge in a text line
     * with CSV format.
     * @param edge Edge whose attributes
     * @param sep Character used as separator (typically ';')
     * @return Text line in CSv format containing the attributes of the input edge
     */
    
    private static String getEdgeLine(
            IEdge   edge,
            char    sep) throws Exception
    {
        String  strInfo;    // Returned value

        IHalfEdge   orientEdge; // Oriented edge
        
        // We cretae the info fields for the edge,
        // using the top-down orientation
        
        if (edge.getDirect().getEdgeType() == OrientedEdgeType.SuperClassOf)
        {
            orientEdge = edge.getDirect();
        }
        else
        {
            orientEdge = edge.getInverse();
        }
        
        // We creaste the LINE
        
        strInfo = Integer.toString(orientEdge.getOpposite().getTarget().getID())
                + sep
                + Integer.toString(orientEdge.getTarget().getID())
                + sep + Double.toString(edge.getCondProbability())
                + sep + Double.toString(edge.getWeight()) + "\n";
        
        // We write the titles
        
        return (strInfo);
    }
    
    /**
     * This function serializes the arry to string using the comma
     * separator.
     * @param intFields
     * @return 
     */
    
    private static String convertToCsvField(
        int[]    intFields)
    {
        String  strUnion = "";   // Returned value
        
        int i;  // Counter
        
        // We build the union of the fields
        
        for (i = 0; i < intFields.length; i++)
        {
            // We insert the comma as separator
            
            if (i > 0)
            {
                strUnion += ',';
            }
            
            // We add the i-field
            
            strUnion = strUnion.concat(String.valueOf(intFields[i]));
        }
        
        // We return the result
        
        return (strUnion);
    }
}
