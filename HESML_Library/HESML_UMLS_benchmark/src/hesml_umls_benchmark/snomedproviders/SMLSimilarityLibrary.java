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
import hesml.measures.SimilarityMeasureType;
import hesml_umls_benchmark.ISnomedSimilarityLibrary;
import hesml_umls_benchmark.SnomedBasedLibrary;
import org.openrdf.model.URI;
import slib.graph.io.conf.GDataConf;
import slib.graph.io.loader.GraphLoaderGeneric;
import slib.graph.io.loader.bio.snomedct.GraphLoaderSnomedCT_RF2;
import slib.graph.io.util.GFormat;
import slib.graph.model.graph.G;
import slib.graph.model.impl.graph.memory.GraphMemory;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;
import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;

/**
 * * This class implementes the SNOMED similarity library based on SML.
 * @author j.lastra
 */

class SMLSimilarityLibrary extends SnomedSimilarityLibrary
        implements ISnomedSimilarityLibrary
{
    /**
     * SML factory object
     */
    
    private URIFactory m_factory;
    
    /**
     * Graph structure encoding the SNOMED taxonomy
     */
    
    private G m_graph;
    
    /**
     * Object responsible of computing the similarity measures
     */
    
    private SM_Engine m_engine;
    
    /**
     * SNOMED URI
     */
    
    private URI m_snomedctURI;

    /**
     * SML IC model and similairy measure
     */
    
    private ICconf m_icConf;
    private SMconf m_smConf;

    /**
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @throws Exception 
     */
    
    SMLSimilarityLibrary(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName) throws Exception
    {
        // Inicializamos la clase base
        
        super(strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName);
        
        // We initialize the object
        
        m_graph = null;
        m_factory = null;
        m_engine = null;
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
        // We get the URI for both concepts

        URI concept1 = m_factory.getURI(m_snomedctURI.stringValue() + firstConceptSnomedID);
        URI concept2 = m_factory.getURI(m_snomedctURI.stringValue() + secondConceptSnomedID);

        // We evaluate the similarity measure
        
        double similarity = m_engine.compare(m_smConf, concept1, concept2);
        
        // We return the result
        
        return (similarity);
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
     * Load the SNOMED database
     */
    
    @Override
    public void loadSnomed() throws Exception
    {
        // We create an in-memory graph in which we will load Snomed-CT.
        // Notice that Snomed-CT is quite large (e.g. version 20120731 contains 296433 concepts and872318 relationships ).
        // You will need to allocate extra memory to the JVM e.g add -Xmx3000m parameter to allocate 3Go.
        
        m_factory = URIFactoryMemory.getSingleton();
        m_snomedctURI = m_factory.getURI("http://snomedct/");
        m_graph = new GraphMemory(m_snomedctURI);

        GDataConf dataConf = new GDataConf(GFormat.SNOMED_CT_RF2);
        
        dataConf.addParameter(GraphLoaderSnomedCT_RF2.ARG_CONCEPT_FILE,
                m_strSnomedDir + "/" + m_strSnomedDBconceptFileName);
        
        dataConf.addParameter(GraphLoaderSnomedCT_RF2.ARG_RELATIONSHIP_FILE,
                m_strSnomedDir + "/" + m_strSnomedDBRelationshipsFileName);

        GraphLoaderGeneric.populate(dataConf, m_graph);
        System.out.println(m_graph.toString());
        
        // We define the engine used to compute the similarity
        
        m_engine = new SM_Engine(m_graph);
    }
    
    /**
     * This function returns the library type
     * @return 
     */
    
    @Override
    public SnomedBasedLibrary getLibraryType()
    {
        return (SnomedBasedLibrary.SML);
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
        // We set the measure and IC model types
        
        String  strIcModel = (icModel == IntrinsicICModelType.Seco) ?
                SMConstants.FLAG_ICI_SECO_2004 : SMConstants.FLAG_ICI_SANCHEZ_2011;
        
        String strMeasure = (measureType == SimilarityMeasureType.Lin) ?
                SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998
                : SMConstants.FLAG_SIM_PAIRWISE_DAG_EDGE_RADA_1989;
        
        // First we configure an intrincic IC 

        m_icConf = new IC_Conf_Topo(strIcModel);

        // Then we configure the pairwise measure to use, we here choose to use Lin formula

        m_smConf = new SMconf(strMeasure, m_icConf);
    }
    
    /**
     * Unload the SNOMED databse
     */
    
    @Override
    public void unloadSnomed()
    {
        // We only disconnect this objects because SML does not provide
        // functios to clear its objects
        
        m_graph = null;
        m_factory = null;
        m_engine = null;
        
        // We force the release of memory
        System.gc();
    }
}
