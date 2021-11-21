/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hesml.sts.measures;

/**
 * COM Mixed Vectors measure type
 * @author root
 */
public enum ComMixedVectorsMeasureType 
{
    PooledMin,
    
    PooledAVG,
    
    PooledMax,
    
    /* Our proposed method, an adaptation from Li et. al
     * 
     * Li Y, McLean D, Bandar ZA, James DO, 
     * Crockett K. Sentence Similarity Based on Semantic Nets and 
     * Corpus Statistics. IEEE Trans Knowl Data Eng. 2006; 
     * 1138–1150. doi:10.1109/TKDE.2006.130
     */
    
    NoneOntology,
    
    Mixed
    
}
