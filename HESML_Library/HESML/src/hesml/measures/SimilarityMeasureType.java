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

package hesml.measures;

/**
 * Enumeration type for all the similarity measures implemented in HESML.
 * @author Juan Lastra-Díaz
 */

public enum SimilarityMeasureType
{
    /**
     * It asks for the similarity measure introduced in the paper below.
     * Rada, R., Mili, H., Bicknell, E., and Blettner, M. (1989).
     * Development and application of a metric on semantic nets.
     * IEEE Transactions on Systems, Man, and Cybernetics, 19(1), 17–30.
     */
    
    Rada,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Leacock, C., and Chodorow, M. (1998).
     * Combining local context and WordNet similarity for word sense identification.
     * In C. Fellbaum (Ed.), WordNet: An electronic lexical database (pp. 265–283).
     * MIT Press.
     */
    
    LeacockChodorow,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Wu, Z., and Palmer, M. (1994). 
     * Verbs Semantics and Lexical Selection.
     * In Proceedings of the 32Nd Annual Meeting on Association for
     * Computational Linguistics (pp. 133–138).
     * Stroudsburg, PA, USA: Association for Computational Linguistics.
     */
    
    WuPalmer,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Pedersen, T., Pakhomov, S. V. S., Patwardhan, S., and Chute, C. G. (2007).
     * Measures of semantic similarity and relatedness in the biomedical domain.
     * Journal of Biomedical Informatics, 40(3), 288–299.
     */
    
    PedersenPath,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Al-Mubaid, H., and Nguyen, H. A. (2009).
     * Measuring Semantic Similarity Between Biomedical Concepts Within
     * Multiple Ontologies. IEEE Transactions on Systems, Man and Cybernetics.
     * Part C, Applications and Reviews: A Publication of the IEEE Systems,
     * Man, and Cybernetics Society, 39(4), 389–398.
     */
    
    Mubaid,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Resnik, P. (1995). Using information content to evaluate semantic similarity
     * in a taxonomy. In Proc. of the International Joint Conferences on
     * Artificial Intelligence (IJCAI 1995) (Vol. 1, pp. 448–453). Montreal, Canada.
     */
    
    Resnik,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Lin, D. (1998). An information-theoretic definition of similarity.
     * In Proceedings of the 15th International Conference on Machine
     * Learning (Vol. 98, pp. 296–304). Madison, WI.
     */
    
    Lin,

    /**
     * It asks for the similarity measure introduced in the paper below.v
     * Jiang, J. J., and Conrath, D. W. (1997).
     * Semantic similarity based on corpus statistics and lexical taxonomy.
     * In Proceedings of International Conference Research on Computational
     * Linguistics (ROCLING X) (pp. 19–33).
     */
    
    JiangConrath,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Pirró, G., and Seco, N. (2008). Design, Implementation and Evaluation
     * of a New Semantic Similarity Metric Combining Features and Intrinsic
     * Information Content. In R. Meersman and Z. Tari (Eds.),
     * On the Move to Meaningful Internet Systems: OTM 2008 (Vol. 5332,
     * pp. 1271–1288). Springer Berlin Heidelberg.
     */
    
    PirroSeco,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Pirró, G., and Euzenat, J. (2010).
     * A Feature and Information Theoretic Framework for Semantic Similarity
     * and Relatedness. In P. F. Patel-Schneider, Y. Pan, P. Hitzler, P. Mika,
     * L. Zhang, J. Z. Pan, … B. Glimm (Eds.), Proc. of the 9th International
     * Semantic Web Conference, ISWC 2010 (Vol. 6496, pp. 615–630).
     * Shangai, China: Springer.
     */
    
    FaITH,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Zhou, Z., Wang, Y., and Gu, J. (2008).
     * New model of semantic similarity measuring in wordnet. In Proc. of the
     * 3rd International Conference on Intelligent System and Knowledge
     * Engineering, 2008. ISKE 2008. (Vol. 1, pp. 256–261).
     */
    
    Zhou,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Meng, L., and Gu, J. (2012). A New Model for Measuring Word Sense Similarity
     * in WordNet1. In The 4th International Conference on Advanced Communication
     * and Networking, ASTL (Vol. 14, pp. 18–23).
     */
    
    Meng2012,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Meng, L., Huang, R., and Gu, J. (2014).
     * Measuring Semantic Similarity of Word Pairs Using Path and
     * Information Content. International Journal of Future Generation
     * Communication and Networking, 7(3).
     */
    
    Meng2014,

    /**
     * It asks for an unpublished logistic transformation of the
     * Lin (1998) similarity defined by Lastra-Díaz and García-Serrano in the
     * context of our research in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A novel family of IC-based similarity measures with a detailed experimental
     * survey on WordNet. Engineering Applications of Artificial Intelligence
     * Journal, 46, 140–153.
     */
    
    LogisticLin,

    /**
     * It asks for an unpublished similarity measure defined by
     * Lastra-Díaz and García-Serrano (2015) which is a cosine-normalized
     * version of the classic Lin (1997) similarity measure.
     * This measure is another monotone exponential-like scaling version
     * of the Lin measure, which is equivalent to the Meng et al (2012)
     * similarity measure.
     */
    
    CosineLin,

    /**
     * It asks for an an unpublished similarity measure defined
     * by Lastra-Díaz and García-Serrano that is a exponential-like
     * version of our cosine-normalized Jiang-Conrath similarity measure
     * introduced in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A novel family of IC-based similarity measures with a detailed experimental
     * survey on WordNet. Engineering Applications of Artificial Intelligence
     * Journal, 46, 140–153.
     */
    
    ExpNormJiangConrath,

    /**
     * It asks for an an unpublished similarity measure defined
     * by Lastra-Díaz and García-Serrano that is a exponential-like
     * version of our cosine-normalized Jiang-Conrath similarity measure
     * introduced in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A novel family of IC-based similarity measures with a detailed experimental
     * survey on WordNet. Engineering Applications of Artificial Intelligence
     * Journal, 46, 140–153.
     */
    
    LogisticNormJiangConrath,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).  
     * A novel family of IC-based similarity measures with a detailed experimental
     * survey on WordNet.Engineering Applications of Artificial Intelligence
     * Journal, 46, 140–153.
     */
    
    CosineNormJiangConrath,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).  
     * A novel family of IC-based similarity measures with a detailed experimental
     * survey on WordNet.Engineering Applications of Artificial Intelligence
     * Journal, 46, 140–153.
     */
    
    WeightedJiangConrath,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).  
     * A novel family of IC-based similarity measures with a detailed experimental
     * survey on WordNet.Engineering Applications of Artificial Intelligence
     * Journal, 46, 140–153.
     */
    
    CosineNormWeightedJiangConrath,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Sánchez, D., Batet, M., Isern, D., and Valls, A. (2012).
     * Ontology-based semantic similarity: A new feature-based approach.
     * Expert Systems with Applications, 39, 7718–7728.
     */
    
    Sanchez2012,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Hadj Taieb, M. A., Ben Aouicha, M., and Ben Hamadou, A. (2014).
     * Ontology-based approach for measuring semantic similarity-
     * Engineering Applications of Artificial Intelligence, 36(0), 238–261.
     */
    
    Taieb2014,

    /**
     * It asks for the similarity measure sim2 introduced in the paper below.
     * Hadj Taieb, M. A., Ben Aouicha, M., and Ben Hamadou, A. (2014).
     * Ontology-based approach for measuring semantic similarity-
     * Engineering Applications of Artificial Intelligence, 36(0), 238–261.
     */
    
    Taieb2014sim2,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Gao, J.-B., Zhang, B.-W., and Chen, X.-H. (2015).
     * A WordNet-based semantic similarity measurement combining
     * edge-counting and information content theory.
     * Engineering Applications of Artificial Intelligence, 39, 80–88.
     */
    
    Gao2015Strategy3,

    /**
     * It asks for the Strategy3 similarity measure introduced in the paper below.
     * Li, Y., Bandar, Z. A., and McLean, D. (2003).
     * An approach for measuring semantic similarity between words using
     * multiple information sources.
     * IEEE Transactions on Knowledge and Data Engineering, 15(4), 871–882.
     */
    
    Li2003Strategy3,

    /**
     * It asks for the Strategy4 similarity measure introduced in the paper below.
     * Li, Y., Bandar, Z. A., and McLean, D. (2003).
     * An approach for measuring semantic similarity between words using
     * multiple information sources.
     * IEEE Transactions on Knowledge and Data Engineering, 15(4), 871–882.
     */
    
    Li2003Strategy4,

    /**
     * It asks for the Strategy9 similarity measure introduced in the paper below.
     * Li, Y., Bandar, Z. A., and McLean, D. (2003).
     * An approach for measuring semantic similarity between words using
     * multiple information sources.
     * IEEE Transactions on Knowledge and Data Engineering, 15(4), 871–882.
     */
    
    Li2003Strategy9,

    /**
     * It asks for the similarity measure introduced in the paper below.
     * Garla, V. N., and Brandt, C. (2012).
     * Semantic similarity in the biomedical domain: an evaluation across
     * knowledge sources. BMC Bioinformatics, 13:261.
     */
    
    Garla
}
