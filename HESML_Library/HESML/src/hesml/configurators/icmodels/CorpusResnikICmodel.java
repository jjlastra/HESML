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

// HESML references

import hesml.configurators.CorpusBasedICModelType;
import hesml.taxonomy.*;
import java.io.File;

/**
 * This class implements the corpus-based IC model introduced by Resnik
 * in the paper below.
 * Resnik, P. (1999). Semantic Similarity in a Taxonomy:
 * An Information-Based Measure and its Application to Problems of
 * Ambiguity in Natural Language.
 * Journal of Artificial Intelligence Research, 11, 95–130.
 * @author Juan Lastra-Díaz
 */

class CorpusResnikICmodel extends PedersenFilesICmodel
{
    /**
     * Constructor
     * @param strPedersenFile Fullname of the input WordNet-based concept frequency file
     * @throws Exception Unexpected error
     */
    
    CorpusResnikICmodel(
        String  strPedersenFile) throws Exception
    {
        super(strPedersenFile);
    }
    
    /**
     * This function loads the IC values contained in the Pedersen
     * files for each concept within a particular WordNet version.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws java.lang.Exception 
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        // We read the content of the file in row mode. The functions
        // reads the frquency values and saves them in the probability
        // filed of the vertexes.
        
        readConceptFrequency(taxonomy);
        
        // We recovery the probability and IC values
        
        buildResnikICmodel(taxonomy);
    }

    /**
     * This function builds an IC model using the Resnik method.
     * @param taxonomy Taxonomy whose IC model is being computed
     * @throws Exception Unexpected error
     */
    
    private void buildResnikICmodel(
            ITaxonomy   taxonomy) throws Exception
    {
        double  rootFrq;    // Maxium frequency of concepts (root)
        double  prob;       // Probability
        
        double  twoLog = Math.log(2.0);

        // We get the maximum frequency saved in the root node
        
        rootFrq = taxonomy.getVertexes().getAt(0).getProbability();
        
        // We compute the weights of every edge ion the taxonomy
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We compute the probability of the node.
            // It is possible that the frequency value be zero.
            // It occurs on some Pedersen datasets, thus, we guarantee
            // that it doesn´t occur.
            
            prob = Math.max(1.0, vertex.getProbability()) / rootFrq;
            
            // We save the probability and IC value
            
            vertex.setProbability(prob);
            vertex.setICValue(-Math.log(prob) / twoLog);
        }
        
        // We set the delta IC weights for the weighted Measures
        
        setICDeltaWeights(taxonomy);       
    }
    
    /**
     * This function returns a String representing IC model type of the
     * current object.
     * @return A string representing the IC model type of the current instance
     */
    
    @Override
    public String toString()
    {
        File    fileInfo = new File(m_strPedersenFile); // WordNet-based frequency file
        
        String  strFilename = fileInfo.getName();   // Filename without path
        
        String  strICmodel = CorpusBasedICModelType.Resnik.toString()
                            + "," + strFilename;
        
        // We return the result
        
        return (strICmodel);
    }    
}
