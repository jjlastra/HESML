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

import hesml.configurators.IntrinsicICModelType;
import hesml.taxonomy.*;

/**
 * This class implements the HypoValue function introduced in the paper below.
 * We considered this function as a class of IC model, thus, we have implemented
 * it herein. However, this function is not formally considered as an IC model by
 * their authors. This IC model is used as preliminary step in the
 * computation of the similarity measures introduced in the paper below.
 * 
 * Hadj Taieb, M. A., Ben Aouicha, M., and Ben Hamadou, A. (2014).
 * Ontology-based approach for measuring semantic similarity.
 * Engineering Applications of Artificial Intelligence, 36(0), 238–261.
 * 
 * @author Juan Lastra-Díaz
 */

class HadjTaiebHypoValueICmodel extends AbstractCondProbICmodel
{
    /**
     * This function computes the data.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws java.lang.Exception      * 

     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        int[]   depthFreqCounters;  // Frequence counters
        
        int depth;  // Depth of a vertex
        
        double  prob;        // Probability
        double  nConcepts;   // Maximum depth
        double  hypoValue;  // Acummulated probability
        double  hypoMax;
        double  specHypo;   // Specificityvalue
        
        IVertexList hyponyms;   // Hyponyms
        
        // We get hte maximum depth
        
        nConcepts = taxonomy.getVertexes().getCount();
        
        // We create the vector to count the depths
        
        depthFreqCounters = new int[taxonomy.getVertexes().getGreatestDepthMax()+ 1];
        
        // We estimate the depth probability function
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            depthFreqCounters[vertex.getDepthMax()]++;
        }
        
        // We define the probability of each concept as the probability
        // of its depth
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We get the depth of the concept
            
            depth = vertex.getDepthMax();
            
            // We compute the probability
            
            prob = ((double)depthFreqCounters[depth] )/ nConcepts;
            
            // We set the probability value
            
            vertex.setProbability(prob);
        }
        
        // We compute the hyponym-based IC value for each vertex
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We get the hyponyms of the vertexes
            
            hyponyms = vertex.getHyponyms(false);
            hypoValue = vertex.getProbability();
            
            // We sum the hyponyms probabilities
            
            for (IVertex hyponym: hyponyms)
            {
                hypoValue += hyponym.getProbability();
            }
            
            // We clear the vertex list
            
            hyponyms.clear();
            
            // We save the hypoValue in the IC field
            
            vertex.setICValue(hypoValue);
        }
        
        // We get the maximum hypoValue
        
        hypoMax = taxonomy.getVertexes().getAt(0).getICvalue();
        
        // We define the final IC value per noce
        
        for (IVertex vertex: taxonomy.getVertexes())
        {
            // We compute the specificity value
            
            hypoValue = vertex.getICvalue();
            
            specHypo = 1.0 - Math.log(hypoValue) / Math.log(hypoMax);
            
            // We save the IC value
            
            vertex.setICValue(specHypo);
            vertex.setProbability(hypoValue);
        }
    }
    
    /**
     * This function returns a String representing IC model type of the
     * current object.
     * @return A string representing the IC model type of the current instance
     */
    
    @Override
    public String toString()
    {
        return (IntrinsicICModelType.HadjTaiebHypoValue.toString());
    }        
}

