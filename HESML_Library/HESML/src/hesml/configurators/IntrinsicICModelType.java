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

package hesml.configurators;

/**
 *
 * @author Juan Lastra-Díaz
 */

public enum IntrinsicICModelType
{
     /**
     * It asks for the creation of a Seco et al. (2004) IC model introduced in
     * the paper below.
     * Seco, N., Veale, T., and Hayes, J. (2004).
     * An intrinsic information content metric for semantic similarity in WordNet.
     * In R. López de Mántaras and L. Saitta (Eds.), Proceedings of the 16th
     * European Conference on Artificial Intelligence (ECAI) (Vol. 16,
     * pp. 1089–1094). Valencia, Spain: IOS Press.
     */
    
    Seco,
    
    /**
     * It asks for the creation of a Blanchard et al. (2008) ICg model
     * introduced in the paper below.
     * Blanchard, E., Harzallah, M., and Kuntz, P. (2008).
     * A generic framework for comparing semantic similarities on a subsumption
     * hierarchy. In M. Ghallab, C. D. Spyropoulos, N. Fakotakis, and N. Avouris (Eds.),
     * Proceedings of the ECAI (Vol. 178, pp. 20–24). IOS Press.
     */
    
    Blanchard,
    
    /**
     * It asks for the creation of a Zhou et al. (2008) IC model introduced in
     * the paper below.
     * Zhou, Z., Wang, Y., and Gu, J. (2008).
     * A new model of information content for semantic similarity in WordNet.
     * In Proc.of the Second International Conference on Future Generation
     * Communication and Networking Symposia (FGCNS’08) (Vol. 3, pp. 85–89). IEEE.
     */
    
    Zhou,
    
    /**
     * It asks for the creation of a Sánchez et al. (2011) IC model introduced
     * in the paper below.
     * Sánchez, D., Batet, M., and Isern, D. (2011).
     * Ontology-based information content computation.
     * Knowledge-Based Systems, 24(2), 297–303.
     */    

    Sanchez2011,
    
    /**
     * It asks for the creation of a Harispe (2012) IC model as defined
     * in the SML library by Harispe et al.
     * We recall that it is an unpublished IC model that we evaluated
     * in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A new family of information content models with an
     * experimental survey on WordNet. Knowledge-Based Systems, 89, 509–526.
     */
    
    Harispe,
    
    /**
     * It asks for the creation of a Sánchez et al. (2012) IC model
     * introduced in the paper below.
     * Sánchez, D., and Batet, M. (2012). A new model to compute the information
     * content of concepts from taxonomic knowledge. International Journal on
     * Semantic Web and Information Systems, 8(2), 34–50.
     */
    
    Sanchez2012,

    /**    
     * It asks for the creation of a Meng et al. (2012) IC model
     * introduced in the paper below.
     * Meng, L., Gu, J., and Zhou, Z. (2012).
     * A new model of information content based on concept’s topology for
     * measuring semantic similarity in WordNet.
     * International Journal of Grid and Distributed Computing, 5(3), 81–93.
     */
    
    Meng,
    
    /**
     * It asks for the creation of a Yuan et al. (2013) IC model introduced
     * i nthe paper below.
     * Yuan, Q., Yu, Z., and Wang, K. (2013).
     * A New Model of Information Content for Measuring the Semantic Similarity
     * between Concepts. In Proc. of the International Conference on Cloud
     * Computing and Big Data (CloudCom-Asia 2013) (pp. 141–146).
     * IEEE Computer Society.
     */
    
    Yuan,
    
    /**
     * It asks for the creation of a HadjTaieb et al. (2013) IC model
     * as defined in the paper below.
     * Hadj Taieb, M. A., Ben Aouicha, M., and Ben Hamadou, A. (2013).
     * Computing semantic relatedness using Wikipedia features.
     * International Journal of Uncertainty, Fuzziness and Knowledge-Based
     * Systems, 50(0), 260–278.
     */
    
    HadjTaieb,
    
    /**
     * It asks for the creation of the Spec node-valued function defined
     * in the paper below by Hadj Taieb et al (2014) paper,
     * which is internally used in order to compute the Hadj Taieb et al.
     * (2014) similarity measure, although it has not been formally introduced
     * as an IC model.
     */
    
    HadjTaiebHypoValue,
    
    /**
     * It asks for the creation of a CondProbHyponyms IC model as defined
     * in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A new family of information content models with an experimental
     * survey on WordNet. Knowledge-Based Systems, 89, 509–526.
     */
    
    CondProbHyponyms,

    /**
     * It asks for the creation of a CondProbUniform IC model as defined
     * in the paper below.
     * in Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A new family of information content models with an experimental
     * survey on WordNet. Knowledge-Based Systems, 89, 509–526.
     */
    
    CondProbUniform,
    
    /**
     * It asks for the creation of a CondProbLeaves IC model as defined
     * in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A new family of information content models with an experimental
     * survey on WordNet. Knowledge-Based Systems, 89, 509–526.
     */
    
    CondProbLeaves,
    
    /**
     * It asks for the creation of a CondProbCosine IC model as defined
     * in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A new family of information content models with an experimental
     * survey on WordNet. Knowledge-Based Systems, 89, 509–526.
     */
    
    CondProbCosine,
    
    /**
     * It asks for the creation of a CondProbLogistic IC model as defined
     * in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A new family of information content models with an experimental
     * survey on WordNet. Knowledge-Based Systems, 89, 509–526.
     */

    CondProbLogistic,
    
    /**
     * It asks for the creation of a CondProbLogistic (k=10) IC model
     * as defined in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A new family of information content models with an experimental
     * survey on WordNet. Knowledge-Based Systems, 89, 509–526.
     */
    
    CondProbLogisticK10,
    
    /**
     * It asks for the creation of a CondProbLogistic (k=12) IC model as
     * defined in the paper below.
     * Lastra-Díaz, J. J., and García-Serrano, A. (2015).
     * A new family of information content models with an experimental
     * survey on WordNet. Knowledge-Based Systems, 89, 509–526.
     */
    
    CondProbLogisticK12,
    
    /**
     * It asks for the creation of a CondProbRefHyponyms IC model as defined in
     * the paper below.
     * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
     * A refinement of the well-founded Information Content models with
     * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
     * Department of Computer Languages and Systems. NLP and IR Research Group.
     * Universidad Nacional de Educación a Distancia (UNED).
     * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
     */
    
    CondProbRefHyponyms,

    /**
     * It asks for the creation of a CondProbRefUniform IC model as defined in
     * the paper below.
     * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
     * A refinement of the well-founded Information Content models with
     * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
     * Department of Computer Languages and Systems. NLP and IR Research Group.
     * Universidad Nacional de Educación a Distancia (UNED).
     * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
     */
    
    CondProbRefUniform,

    /**
     * It asks for the creation of a CondProbRefLeaves IC model as defined in
     * the paper below.
     * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
     * A refinement of the well-founded Information Content models with
     * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
     * Department of Computer Languages and Systems. NLP and IR Research Group.
     * Universidad Nacional de Educación a Distancia (UNED).
     * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
     */

    CondProbRefLeaves,

    /**
     * It asks for the creation of a CondProbRefCosine IC model
     * as defined in the paper below.
     * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
     * A refinement of the well-founded Information Content models with
     * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
     * Department of Computer Languages and Systems. NLP and IR Research Group.
     * Universidad Nacional de Educación a Distancia (UNED).
     * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
     */

    CondProbRefCosine,

    /**
     * It asks for the creation of a CondProbRefLogistic IC model
     * as defined in the paper below.
     * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
     * A refinement of the well-founded Information Content models with
     * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
     * Department of Computer Languages and Systems. NLP and IR Research Group.
     * Universidad Nacional de Educación a Distancia (UNED).
     * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
     */

    CondProbRefLogistic,

    /**
     * It asks for the creation of a CondProbRefCosineLeaves IC model
     * as defined in the paper below.
     * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
     * A refinement of the well-founded Information Content models with
     * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
     * Department of Computer Languages and Systems. NLP and IR Research Group.
     * Universidad Nacional de Educación a Distancia (UNED).
     * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
     */

    CondProbRefCosineLeaves,

    /**
     * It asks for the creation of a CondProbRefLogisticLeaves IC model
     * as defined in the paper below.
     * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
     * A refinement of the well-founded Information Content models with
     * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
     * Department of Computer Languages and Systems. NLP and IR Research Group.
     * Universidad Nacional de Educación a Distancia (UNED).
     * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
     */

    CondProbRefLogisticLeaves,

    /**
     * It asks for the creation of a CondProbRefLeavesSubsumers IC model
     * as defined in the paper below.
     * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
     * A refinement of the well-founded Information Content models with
     * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
     * Department of Computer Languages and Systems. NLP and IR Research Group.
     * Universidad Nacional de Educación a Distancia (UNED).
     * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
     */

    CondProbRefLeavesSubsumers,

    /**
     * It asks for the creation of a CondProbRefLeavesSubsumersRatio IC model
     * as defined in the paper below.
     * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
     * A refinement of the well-founded Information Content models with
     * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
     * Department of Computer Languages and Systems. NLP and IR Research Group.
     * Universidad Nacional de Educación a Distancia (UNED).
     * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
     */

    CondProbRefLeavesSubsumersRatio,

    /**
     * It asks for the creation of a CondProbRefSubsumedLeavesRatio IC model as defined in
     * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
     * A refinement of the well-founded Information Content models with
     * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
     * Department of Computer Languages and Systems. NLP and IR Research Group.
     * Universidad Nacional de Educación a Distancia (UNED).
     * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
     */

    CondProbRefSubsumedLeavesRatio,
    
    /**
     * It asks for the creation of a Adhikari et al. (2015) IC model as defined
     * in the paper below.
     * 
     */
    
    Adhikari,
    
    /**
     * It asks for the creation of a AsGIC IC model as it is proposed
     * by Aouicha and Hadj Taieb (2015).
     * Aouicha, M. B., and Taieb, M. A. H. (2015).
     * Computing semantic similarity between biomedical concepts using new
     * information content approach. Journal of Biomedical Informatics.
     * http://doi.org/10.1016/j.jbi.2015.12.007
     */
    
    AouichaTaiebAsGIC,
    
    /**
     * It asks for the creation of the AIC IC model introduced by
     * Ben Aouicha, M., Taieb, M. A. H., and Ben Hamadou, A. (2016).
     * Taxonomy-based information content and wordnet-wiktionary-wikipedia
     * glosses for semantic relatedness. Applied Intelligence, 1–37.
     */
    
    AICAouichaTaiebHamadu2016
}
