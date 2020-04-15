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
package hesml_umls_benchmark.benchmarks;

import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.icmodels.ICModelsFactory;
import hesml.measures.ISimilarityMeasure;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.impl.MeasureFactory;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertexList;
import java.util.Random;
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
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.ex.SLIB_Exception;

/**
 * This class implements a benchmark to comapre the performance
 * of the UMLS similarity libraries in the evaluation of the degree
 * of similairty between randdom concept pairs in SNOMED-CT.
 * @author j.lastra
 */

class ConceptEvaluationBenchmark extends UMLSLibBenchmark
{
    /**
     * SNOMED-CT RF2 files
     */
    
    private String  m_strSnomedDir;
    private String  m_strSnomedDBconceptFileName;
    private String  m_strSnomedDBRelationshipsFileName;
    private String  m_strSnomedDBdescriptionFileName;
    
    /**
     * Constructor to build the Snomed HESML database
     * @param strSnomedDir
     * @param strSnomedDBconceptFileName
     * @param strSnomedDBRelationshipsFileName
     * @param strSnomedDBdescriptionFileName
     * @throws Exception 
     */

    ConceptEvaluationBenchmark(
            String  strSnomedDir,
            String  strSnomedDBconceptFileName,
            String  strSnomedDBRelationshipsFileName,
            String  strSnomedDBdescriptionFileName) throws Exception
    {
        // Inicializamos la clase base
        
        super(strSnomedDir, strSnomedDBconceptFileName,
                strSnomedDBRelationshipsFileName,
                strSnomedDBdescriptionFileName);
        
        // We save the SNOMED filenames
        
        m_strSnomedDir = strSnomedDir;
        m_strSnomedDBconceptFileName = strSnomedDBconceptFileName;
        m_strSnomedDBRelationshipsFileName = strSnomedDBRelationshipsFileName;
        m_strSnomedDBdescriptionFileName = strSnomedDBdescriptionFileName;
    }
    
    /**
     * This function generates a vector of random SNOMED-CT concept pairs which
     * will be used to evaluate the performance of the libraeries.
     * @param snomedTaxonomy
     * @param nPairs
     * @return 
     */
    
    private Long[][] getRandomNodePairs(
            ITaxonomy   snomedTaxonomy,
            int         nPairs) 
    {
        // We cretae the random paris
        
        Long[][] snomedPairs = new Long[nPairs][2];
        
        // We create a ranodm number
        
        Random rand = new Random(500);
        
        // We get the number of nodes in the SNOMED-CT graph
        
        long nConcepts = snomedTaxonomy.getVertexes().getCount();
        
        // We generate the ranomdon node pairs
        
        for (int i = 0; i < nPairs; i++)
        {
            // We randomly select two nodex of the SNOMED-CT taxonomy
            
            int snomedConceptIndex1 = (int)(rand.nextDouble() * (nConcepts - 1));
            int snomedConceptIndex2 = (int)(rand.nextDouble() * (nConcepts - 1));
            
            // We copy their concept IDs
            
            snomedPairs[i][0] = snomedTaxonomy.getVertexes().getAt(snomedConceptIndex1).getID();
            snomedPairs[i][1] = snomedTaxonomy.getVertexes().getAt(snomedConceptIndex2).getID();
        }
        
        // We return the output
        
        return (snomedPairs);
    }
    
    /**
     * This function executes the benchmark.
     */
    
    @Override
    public void run(String strOutputFilename) throws Exception
    {
        // We set the setup parameters
        
        int nRuns = 10;
        int nSamples = 10;
        
        // set the number of runs
        
        Long[][] snomedIDpairs = getRandomNodePairs(m_hesmlSnomedDatabase.getTaxonomy(), nSamples);
        
        // We create the output data matrix and fill the row headers
        
        String[][] strOutputDataMatrix = new String[2][nRuns + 1];
        
        strOutputDataMatrix[0][0] = "HESML";
        strOutputDataMatrix[1][0] = "SML";
        
        // We evaluate the performance of the HESML library
        
        CopyRunningTimesToMatrix(strOutputDataMatrix,
                EvaluateLinMeasureUsingHESML(snomedIDpairs, nRuns), 0);
        
        // We relñease the SNOMED-CT database
        
        m_hesmlSnomedDatabase.clear();
        
        // Evaluate the SML library
        
        CopyRunningTimesToMatrix(strOutputDataMatrix,        
            EvaluateLinMeasureUsingSML(snomedIDpairs, nRuns), 1);
        
        // We write the output raw data
        
        WriteCSVfile(strOutputDataMatrix, strOutputFilename);
    }
    
    /**
     * This function sets the Seco et al.(2004)[1] and the Lin similarity
     * measure [2] using HESML library [3]. Then, the function evaluates the
     * Lin similairty of the set of random concept pairs.
     * 
     * [1] N. Seco, T. Veale, J. Hayes,
     * An intrinsic information content metric for semantic similarity
     * in WordNet, in: R. López de Mántaras, L. Saitta (Eds.), Proceedings
     * of the 16th European Conference on Artificial Intelligence (ECAI),
     * IOS Press, Valencia, Spain, 2004: pp. 1089–1094.
     * 
     * [2] D. Lin, An information-theoretic definition of similarity,
     * in: Proceedings of the 15th International Conference on Machine
     * Learning, Madison, WI, 1998: pp. 296–304.
     * 
     * [3] J.J. Lastra-Díaz, A. García-Serrano, M. Batet, M. Fernández, F. Chirigati,
     * HESML: a scalable ontology-based semantic similarity measures library
     * with a set of reproducible experiments and a replication dataset,
     * Information Systems. 66 (2017) 97–118.
     * 
     * @param snomedPairs
     */
    
    private double[] EvaluateLinMeasureUsingHESML(
            Long[][]    snomedPairs,
            int         nRuns) throws Exception
    {
        // We get the taxonomy
        
        ITaxonomy taxonomy = m_hesmlSnomedDatabase.getTaxonomy();
        IVertexList vertexes = taxonomy.getVertexes();
        
        // We set the Seco IC model in the taxonomy
        
        System.out.println("Computing the Seco IC model into the SNOMED-CT  taxonomy");
        
        ICModelsFactory.getIntrinsicICmodel(IntrinsicICModelType.Seco).setTaxonomyData(taxonomy);
        
        // We get the Lin similarity measure
        
        ISimilarityMeasure measureLin = MeasureFactory.getMeasure(taxonomy, SimilarityMeasureType.Lin);
        
        // We initialize the output vector
        
        double[] runningTimes = new double[nRuns];
        
        double accumulatedTime = 0.0;
        
        // We exucte multiple times the benchmark to compute a stable running time
        
        for (int iRun = 0; iRun < nRuns; iRun++)
        {
            // We initializa the stopwatch

            long startTime = System.currentTimeMillis();

            // We evaluate the random concept pairs

            for (int i = 0; i < snomedPairs.length; i++)
            {
                measureLin.getSimilarity(vertexes.getById(snomedPairs[i][0]),
                        vertexes.getById(snomedPairs[i][1]));
            }

            // We compute the elapsed time in seconds

            runningTimes[iRun] = (System.currentTimeMillis() - startTime) / 1000.0;
            
            accumulatedTime += runningTimes[iRun];
        }
        
        // We compute the averga running time
        
        double averageRuntime = accumulatedTime / nRuns;
        
        // We print the average results
        
        System.out.println("# concept pairs evaluated = " + snomedPairs.length);
        System.out.println("Average time elapsed evaluating Lin measure with HESML (secs) = "
                + averageRuntime);
        
        System.out.println("Average HESML evaluation speed (#evaluation/second) = "
                + ((double)snomedPairs.length) / averageRuntime);
        
        // We return the results
        
        return (runningTimes);
    }
    
    /**
     * This function sets the Seco et al.(2004)[1] and the Lin similarity
     * measure [2] using SML library [3]. Then, the function evaluates the
     * Lin similairty of the set of random concept pairs.
     * 
     * [1] N. Seco, T. Veale, J. Hayes,
     * An intrinsic information content metric for semantic similarity
     * in WordNet, in: R. López de Mántaras, L. Saitta (Eds.), Proceedings
     * of the 16th European Conference on Artificial Intelligence (ECAI),
     * IOS Press, Valencia, Spain, 2004: pp. 1089–1094.
     * 
     * [2] D. Lin, An information-theoretic definition of similarity,
     * in: Proceedings of the 15th International Conference on Machine
     * Learning, Madison, WI, 1998: pp. 296–304.
     * 
     * [3] S. Harispe, S. Ranwez, S. Janaqi, J. Montmain,
     * The semantic measures library and toolkit: fast computation of semantic
     * similarity and relatedness using biomedical ontologies,
     * Bioinformatics. 30 (2014) 740–742.
     * 
     * @param snomedPairs 
     */
    
    private double[] EvaluateLinMeasureUsingSML(
            Long[][]    snomedPairs,
            int         nRuns) throws SLIB_Ex_Critic, SLIB_Exception
    {
        // We create an in-memory graph in which we will load Snomed-CT.
        // Notice that Snomed-CT is quite large (e.g. version 20120731 contains 296433 concepts and872318 relationships ).
        // You will need to allocate extra memory to the JVM e.g add -Xmx3000m parameter to allocate 3Go.
        
        URIFactory factory = URIFactoryMemory.getSingleton();
        URI snomedctURI = factory.getURI("http://snomedct/");
        G g = new GraphMemory(snomedctURI);

        GDataConf conf = new GDataConf(GFormat.SNOMED_CT_RF2);
        
        conf.addParameter(GraphLoaderSnomedCT_RF2.ARG_CONCEPT_FILE,
                m_strSnomedDir + "/" + m_strSnomedDBconceptFileName);
        
        conf.addParameter(GraphLoaderSnomedCT_RF2.ARG_RELATIONSHIP_FILE,
                m_strSnomedDir + "/" + m_strSnomedDBRelationshipsFileName);

        GraphLoaderGeneric.populate(conf, g);

        System.out.println(g.toString());

        // We compute the similarity between the concepts  
        // associated to Heart	and Myocardium, i.e. 80891009 and 74281007 respectively
        // We first build URIs correspondind to those concepts

        // First we configure an intrincic IC 

        ICconf icConf = new IC_Conf_Topo(SMConstants.FLAG_ICI_SECO_2004);

        // Then we configure the pairwise measure to use, we here choose to use Lin formula

        SMconf smConf = new SMconf(SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998, icConf);
        
        // We define the engine used to compute the similarity
        
        SM_Engine engine = new SM_Engine(g);

        // We initialize the output vector
        
        double[] runningTimes = new double[nRuns];
        
        double accumulatedTime = 0.0;
        
        // We exucte multiple times the benchmark to compute a stable running time
        
        for (int iRun = 0; iRun < nRuns; iRun++)
        {
            // We initializa the stopwatch

            long startTime = System.currentTimeMillis();

            // We evaluate the random concept pairs

            for (int i = 0; i < snomedPairs.length; i++)
            {
                // We get the URI for both concepts

                URI concept1 = factory.getURI(snomedctURI.stringValue() + snomedPairs[i][0].toString()); // i.e http://snomedct/230690007
                URI concept2 = factory.getURI(snomedctURI.stringValue() + snomedPairs[i][1].toString());

                // We evaluate the Lin measure

                engine.compare(smConf, concept1, concept2);
            }
            
            // We compute the elapsed time in seconds

            runningTimes[iRun] = (System.currentTimeMillis() - startTime) / 1000.0;
            
            accumulatedTime += runningTimes[iRun];
        }
        
        // We compute the averga running time
        
        double averageRuntime = accumulatedTime / nRuns;

        // We print the average running time
        
        System.out.println("# concept pairs evaluated = " + snomedPairs.length);
        System.out.println("Average time elapsed evaluating Lin measure with SML (secs) = " + averageRuntime);
        System.out.println("Average SML evaluation speed (#evaluation/second) = "
                + ((double)snomedPairs.length) / averageRuntime);
        
        // We return the results
        
        return (runningTimes);
    }
}
