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

package hesml.configurators.icmodels;

// Java references

import java.io.File;
import java.util.Scanner;
import java.util.StringTokenizer;

// HESML references

import hesml.taxonomy.*;

/**
 * This class implements the reader of the WN-based frequency files used
 * by the corpus-based IC models in this library. The Pedersen dataset
 * is stored in the following repository.
 * 
 * Pedersen, T. (2008). WordNet-InfoContent-3.0.tar dataset repository.
 * https://www.researchgate.net/publication/273885902_WordNet-InfoContent-3.0.tar
 * 
 * If you use the corpus-based IC models deribed from the Pedersen dataset above,
 * you must cite the Pedersen papers shown below.
 * 
 * Patwardhan, S., and Pedersen, T. (2006).
 * Using WordNet-based context vectors to estimate the semantic relatedness
 * of concepts. In Proceedings of the EACL 2006 Workshop Making Sense of
 * Sense-Bringing Computational Linguistics and Psycholinguistics Together
 * (Vol. 1501, pp. 1–8). Trento, Italy.
 * 
 * Pedersen, T. (2010).
 * Information Content Measures of Semantic Similarity Perform Better Without
 * Sense-tagged Text. In Human Language Technologies: The 2010 Annual Conference
 * of the North American Chapter of the Association for Computational Linguistics
 * (pp. 329–332). Stroudsburg, PA, USA: Association for Computational Linguistics.
 * 
 * @author Juan Lastra-Díaz
 */

abstract class PedersenFilesICmodel extends AbstractCondProbICmodel
{
    /**
     * Path of the corpus-based IC data file
     */
    
    protected String  m_strPedersenFile;
    
    /**
     * Constructor
     */
    
    PedersenFilesICmodel(
        String  strPedersenFile) throws Exception
    {
        File    icDataFile = new File(strPedersenFile);
        
        // We check the existence of the file
        
        if (!icDataFile.exists())
        {
            String  strError = "The IC data file *.dat file doesn´t exist -> "
                    + icDataFile.getAbsolutePath();
            Exception   error = new Exception(strError);
            throw (error);
        }
        
        // We save the file path
        
        m_strPedersenFile = strPedersenFile;
    }

    /**
     * This function reads the concept frequencies in the
     * input WordNet-based frequency file associated to the IC
     * model. The input files are part of the dataset
     * created by Ted Pedersen.
     * @param taxonomy Taxonomy whose IC model is being computed
     * @throws Exception Unexpected error
     */
    
    protected void readConceptFrequency(
            ITaxonomy   taxonomy) throws Exception
    {
        Exception   error;      // Error thrpwn
        String      strError;   // Error message
        
        boolean isNoun = true; // Returned value
        
        StringTokenizer tokenizer;  // Tokenizer
        
        String  strConceptLine;
        String  strIdConcept;      // Id of the WordNet concept
        String  strNodeFreq;       // Node frequency
        
        IVertex vertex;     // XConcept vertex in the taxonomy
        
        int     idVertex;   // Id of the concepot vertex
        double  frequency;
        
        File    icDataFile; // Data file
        Scanner reader;     // File reader
        
        // We create the file object to read the file
        
        icDataFile = new File(m_strPedersenFile);
                
        // We create the reader of the file
        
        reader = new Scanner(icDataFile);
        
        // We check the header of the file
        
        if (!checkHeader(reader))
        {
            strError = "IC data file bad formatted";
            error = new Exception(strError);
            throw (error);
        }
        
        // We read the file
        
        while (reader.hasNextLine() && isNoun)
        {
            // We get the next line
            
            strConceptLine = reader.nextLine();
            
            // We create a tokenizer to process the concept line

            tokenizer = new StringTokenizer(strConceptLine);

            // We extract the two fields

            strIdConcept = tokenizer.nextToken();
            strNodeFreq = tokenizer.nextToken();

            // We check if the first ID is a noun ID

            if (strIdConcept.endsWith("n"))
            {
                // We indicate that is a noun and the reading muct continue

                isNoun = true;

                // We remove the last 'n' character

                strIdConcept = strIdConcept.replace("n", "");

                // We get the ID of the concept vertex

                idVertex = Integer.parseInt(strIdConcept);
                frequency = Double.parseDouble(strNodeFreq);

                // We get the vertex

                vertex = taxonomy.getVertexes().getById(idVertex);

                if (vertex == null)
                {
                    strError = "The vertex was not found Id = " + idVertex;
                    error = new Exception(strError);
                    throw (error);
                }

                // We save the data in the vertex

                vertex.setProbability(frequency);
            }
        }
        
        // We close the file
        
        reader.close();
    }
    
    /**
     * This function checks the header of the file.
     * @param reader
     * @return true if evrithing is OK
     */
    
    private boolean checkHeader(
            Scanner reader)
    {
        boolean fileOk = false; // Returned value
        
        String  strFirstLine;   // First line of the file
        
        // We reade the first line
        
        strFirstLine = reader.nextLine();
               
        // Wecheck the content of the line
        
        if (strFirstLine.startsWith("wnver::"))
        {
            fileOk = true;
        }
        
        // We return the result
        
        return (fileOk);
    }
}
