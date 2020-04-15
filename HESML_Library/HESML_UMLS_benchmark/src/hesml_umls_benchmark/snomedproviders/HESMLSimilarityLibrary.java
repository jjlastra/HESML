/*
 * Copyright (C) 2020 Universidad Complutense de Madrid (UCM)
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

package hesml_umls_benchmark.snomedproviders;

import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.ISimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.snomed.ISnomedCtDatabase;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;
import hesml_umls_benchmark.ISnomedSimilarityLibrary;
import hesml_umls_benchmark.SnomedBasedLibrary;

/**
 * This class implementes the SNOMED similarity library based on HESML.
 * @author j.lastra
 */

class HESMLSimilarityLibrary extends SnomedSimilarityLibrary
        implements ISnomedSimilarityLibrary
{
    /**
     * SNOMED databse implemented by HESML
     */
    
    private ISnomedCtDatabase   m_hesmlSnomedDatabase;
    
    /**
     * Active semantic similarity measure
     */
    
    private ISimilarityMeasure  m_hesmlSimilarityMeasure; 
    
    /**
     * Vertexes contained in the HESML taxonomy encoding SNOMED
     */
    
    private IVertexList m_hesmlVertexes;
    
    /**
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @throws Exception 
     */
    
    HESMLSimilarityLibrary(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName) throws Exception
    {
        // Inicializamos la clase base
        
        super(strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName);
        
        // We initialize the class
        
        m_hesmlSimilarityMeasure = null;
        m_hesmlSnomedDatabase = null;
    }

    /**
     * This function returns the library type
     * @return 
     */
    
    @Override
    public SnomedBasedLibrary getLibraryType()
    {
        return (SnomedBasedLibrary.HESML);
    }
    
    /**
     * We release the resources associated to this object
     */
    
    @Override
    public void clear()
    {
        unloadSnomed();
    }
    
    /**
     * This fucntion returns the degree of similarity between two
     * SNOMED-CT concepts.
     * @param firstConceptSnomedID
     * @param secondConceptSnomedID
     * @return 
     */

    @Override
    public double getSimilarity(
            long    firstConceptSnomedID,
            long    secondConceptSnomedID)  throws Exception
    {
        // We compute the semantic similarity between concepts
        
        double similarity = m_hesmlSimilarityMeasure.getSimilarity(
                            m_hesmlVertexes.getById(firstConceptSnomedID),
                            m_hesmlVertexes.getById(secondConceptSnomedID));
        
        // We return the result
        
        return (similarity);
        
    }
    
    /**
     * This function sets the active semantic measure used by the library
     * to compute the semantic similarity between concepts.
     * @param icModel
     * @param measureType 
     */
    
    @Override
    public void setSimilarityMeasure(
            IntrinsicICModelType    icModel,
            SimilarityMeasureType   measureType) throws Exception
    {
        // We force the loading of the SNOMED database
        
        loadSnomed();
        
        // We set the Seco IC model in the taxonomy
        
        System.out.println("Computing the Seco IC model into the SNOMED-CT  taxonomy");
        
        ICModelsFactory.getIntrinsicICmodel(icModel).setTaxonomyData(m_hesmlSnomedDatabase.getTaxonomy());
        
        // We get the Lin similarity measure
        
        m_hesmlSimilarityMeasure = MeasureFactory.getMeasure(m_hesmlSnomedDatabase.getTaxonomy(), measureType);
    }
    
    /**
     * Load the SNOMED database
     */
    
    @Override
    public void loadSnomed() throws Exception
    {
        // We load the SNOMED database and get the vertex list of its taxonomy
     
        if (m_hesmlSnomedDatabase == null)
        {
            m_hesmlSnomedDatabase = SnomedCtFactory.loadSnomedDatabase(m_strSnomedDir,
                                    m_strSnomedDBconceptFileName,
                                    m_strSnomedDBRelationshipsFileName,
                                    m_strSnomedDBdescriptionFileName, true);
            
            m_hesmlVertexes = m_hesmlSnomedDatabase.getTaxonomy().getVertexes();
        }
    }
    
    /**
     * Unload the SNOMED databse
     */
    
    @Override
    public void unloadSnomed()
    {
        if (m_hesmlSnomedDatabase != null) m_hesmlSnomedDatabase.clear();
    }
}
