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
 * This class implements the IC model called CondProbRefCorpus
 * that is introduced in the paper below.
 * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
 * A refinement of the well-founded Information Content models with
 * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
 * Department of Computer Languages and Systems. NLP and IR Research Group.
 * Universidad Nacional de Educación a Distancia (UNED).
 * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
 * 
 * @author j.lastra
 */

class CondProbRefinedCorpusICmodel extends CondProbCorpusICmodel
{
    /**
     * Constructor
     */
    
    CondProbRefinedCorpusICmodel(
        String  strPedersenFile) throws Exception
    {
        super(strPedersenFile);
    }
    
    /**
     * This function loads the IC values contained in the Pedersen
     * files for each concept within a particular WordNet version.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws java.lang.Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        // We call the superclass function
        
        super.setTaxonomyData(taxonomy);
        
        // We call the refined computation of the IC values in order
        // to set the node probability as the sum of the probability
        // of the subsumed leaf nodes.
        
        setNodesProbICvaluesBySubsumedLeaves(taxonomy, false);
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
        
        String  strICmodel = CorpusBasedICModelType.CondProbRefCorpus.toString()
                            + "," + strFilename;
        
        // We return the result
        
        return (strICmodel);
    }    
}
