/*
 * * Copyright (C) 2020-2021 Universidad Complutense de Madrid (UCM)
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
 */

package hesml_umls_benchmark.benchmarks;

import hesml.taxonomy.IVertex;

/**
 * This inteface represents a pair of SNOMED-CT concepts which are
 * separated a specific distance value obtaned by the AncSPL algorithm
 * @author Juan J. Lastra-Díaz (jlastra@invi.uned.es)
 */

class SnomedConceptPair
{
    /**
     * Distance between source and target concept
     */
    
    private double    m_ancSplDistance;
    
    /**
     * Source and target vertexes for the SNOMED-CT concepts
     */
    
    private IVertex    m_sourceConceptVertex;
    private IVertex    m_targetConceptVertex;   
    
    /**
     * Constrcutor
     * @param ancSplDistance
     * @param sourceConceptVertex
     * @param targteConceptId 
     */
    
    SnomedConceptPair(
            double  ancSplDistance,
            IVertex sourceConceptVertex,
            IVertex targetConceptVertex)
    {
        m_ancSplDistance = ancSplDistance;
        m_sourceConceptVertex = sourceConceptVertex;
        m_targetConceptVertex = targetConceptVertex;
    }
    
    /**
     * Distance value between bpth concepts using the AncSPL algorithm
     * @return 
     */
    
    public double getAncSPLDistance()
    {
        return (m_ancSplDistance);
    }
    
    /**
     * Source concept ID
     * @return 
     */
    
    public IVertex getSourceConceptVertex()
    {
        return (m_sourceConceptVertex);
    }
    
    /**
     * Target concept ID
     * @return 
     */
    
    public IVertex getTargetConceptVertex()
    {
        return (m_targetConceptVertex);
    }
}
