/*
 * Copyright (C) 2016 Universidad Nacional de Educación a Distancia (UNED)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hesml.configurators.icmodels;

import hesml.configurators.ITaxonomyInfoConfigurator;
import hesml.configurators.IntrinsicICModelType;
import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;

/**
 * This class implements the IC model introduced in the paper below.
 * However, the score(c) function is ill-defined in equation (27) of
 * the paper below. In equation (27), the score(c) function includes
 * a term defined as -log(1/depth); however, the last term goes to
 * -infinity for any direct child of the root, because of the root depth
 * is 0. Thus, the IC model implemented herein has not been evaluated
 * and we suggest to any reader to ask the authors of this IC model
 * in order to clarify the aforementioned problems. Basically, the
 * authors should explain what is the exact definition of node depth
 * used by them in order to define the score function in equation (27),
 * because of this equation is ill-conditioned for a base-0 depth in which
 * the root depth takes a zero value. In the examples shown in the
 * paper below, the root depth is defined to 0; however, with this value
 * the equation (27) is ill-defined on the child concepts from the root.
 * 
 * Ben Aouicha, M., Taieb, M. A. H., and Ben Hamadou, A. (2016).
 * Taxonomy-based information content and wordnet-wiktionary-wikipedia
 * glosses for semantic relatedness. Applied Intelligence, 1–37.
 * @author j.lastra
 */

public class AICAouichaTaiebHamaduICmodel2016 implements ITaxonomyInfoConfigurator
{
    /**
     * Maximum value for the HypoInfo function on the input taxonomy
     */
    
    private double  m_MaxHypoInfo;
    
    /**
     * This function computes the AIC IC model and sets the IC values
     * of the input taxonomy.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws Exception Unexpected error
     */

    @Override
    public void setTaxonomyData(ITaxonomy taxonomy) throws Exception
    {
        IVertexList ancestorSubgraph;   // It includes the base concept
        
        // We compute and set the maximum value for the HypoInfo(c) function.
        
        SetMaxHypoInfoValue(taxonomy);
        
        // We traverse the taxonomy from the root to the leaf vertexes
        
        for (IVertex conceptVertex: taxonomy.getVertexes())
        {
            // We get the ancestor subgraph including the base concept
            
            ancestorSubgraph = conceptVertex.getAncestors(true);
            
            // We compute the sum of scores according as defined in
            // equation (26) in the Aouicha et al. (2016) paper.
            
            double  icValue = 0.0;
            
            for (IVertex ancestor: ancestorSubgraph)
            {
                icValue += Score(ancestor);
            }
            
            // We set the IC value of the vertex
            
            conceptVertex.setICValue(icValue);
            
            // We release the subgraph
            
            ancestorSubgraph.clear();
        }
    }
       
    /**
     * This function computes and sets the maximum value of the
     * HypoInfo8c) function on the input taxonomy.
     * @param taxonomy
     * @throws Exception Unexpected error
     */
    
     private void SetMaxHypoInfoValue(
        ITaxonomy   taxonomy) throws Exception
    {
        // We initialize the maximum value of the HypoInfo function.
        
        m_MaxHypoInfo = 0.0;
        
        // We traverse the input taxonomy computing the HypoInfo(c) function
        
        for (IVertex conceptVertex: taxonomy.getVertexes())
        {
            m_MaxHypoInfo = Math.max(m_MaxHypoInfo, HypoInfo(conceptVertex));
        }
    }
    
    /**
     * This function implements the Score function as defined in equation
     * (27) of the Aouicha et al. (2016) paper.
     * @param conceptVertex Input vertex (concept) for the Score(c) function
     * @return 
     * @throws Exception Unexpected error
     */
    
    private double Score(
        IVertex conceptVertex) throws Exception
    {
        double  score = 0.0;  // Returned value
        
        IVertexList parents;    // PArents of the input concept
        
        // We get the direct parents of the input concept
        
        parents = conceptVertex.getParents();
        
        // We compute the score function as defined in equation (27) of
        // Aouicha et al. (2016) paper. In the latter paper, the score
        // function is defined in equation (27) as shown below, with the
        // only difference that we express the term -log(1/depth(parent))
        // as log(depth). This term is ill-defined because the root concept
        // has a depth equal to 0, thus, this tewm will takes a -infinity value
        // for all the child concepts of the root.
        
        for (IVertex parent: parents)
        {
            score += Math.log((double)parent.getDepthMax()) * Term(parent);
        }
        
        // We compute the final value multiplying por Term(c)
        
        score *= Term(conceptVertex);
        
        // We release the parent list
        
        parents.clear();
        
        // We return the result
        
        return (score);
    }
    
    /**
     * This function implements the Term(c) function as defined in equation (29)
     * of the Aouicha et al. (2016) paper.
     * @param conceptVertex
     * @return 
     * @throws Exception Unexpected error
     */
    
    private double Term(
        IVertex conceptVertex) throws Exception
    {
        double  term;   // Returned value
        
        // We compute the term function
        
        term = 1.0 - Math.log(HypoInfo(conceptVertex) + 1.0) / 
                Math.log(m_MaxHypoInfo);
        
        // We return the result
        
        return (term);
    }
    
    /**
     * This function implements the HypoInfo(c) function as defined in
     * equation (28) of the Aouicha et al. (2016) paper.
     * @param conceptVertex
     * @return 
     * @throws Exception Unexpected error
     */
    
    private double HypoInfo(
        IVertex conceptVertex) throws Exception
    {
        double  hypoInfoValue = 0.0;    // Returned value
        
        IVertexList descendants;    // Descendants of the vertex
        
        // We get the non-inclusive descendant set of the base vertex.
        // However, this definition must be chekced with the authors
        // of the IC model becuase it is not detailed in their paper.
        
        descendants = conceptVertex.getHyponyms(false);
        
        // We compute the function value using the depth max value
        // which is defined as the length of the longest ascending
        // path from the vertex to the root.
        
        for (IVertex descendant: descendants)
        {
            hypoInfoValue += 1.0 / descendant.getDepthMax();
        }
        
        // We release the descendant list
        
        descendants.clear();
        
        // We return the result
        
        return (hypoInfoValue);
    }
    
    /**
     * This function returns a String representing IC model type of the
     * current object.
     * @return A name representing the type of the current IC model instance
     */
    
    @Override
    public String toString()
    {
        return (IntrinsicICModelType.AICAouichaTaiebHamadu2016.toString());
    }
}
