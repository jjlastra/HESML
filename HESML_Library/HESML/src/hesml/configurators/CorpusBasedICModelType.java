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
 * This enumeration defines the types of corpus-based IC models that can be
 * instantiated in current HESML version.
 * @author Juan Lastra-Díaz
 */

public enum CorpusBasedICModelType
{
    /**
     * It asks for the creation of a Resnik corpus-based IC model using the
     * Pedersen WordNet-based frequency files.
     *//**
     * It asks for the creation of a Resnik corpus-based IC model using the
     * Pedersen WordNet-based frequency files.
     */
    
    Resnik,
    
    /**
     * It asks for the creation of a CondProbCorpus IC model using  the
     * Pedersen WordNet-based frequency files.
     */
    
    CondProbCorpus,
    
    /**
     * It asks for the creation of a CondProbRefCorpus IC model using the
     * Pedersen WordNet-based frequency files, as detailed in the paper below.
     * Lastra-Díaz, J. J. and García-Serrano, A. (2016).
     * A refinement of the well-founded Information Content models with
     * a very detailed experimental survey on WordNet. Tech. Rep. TR-2016-01
     * Department of Computer Languages and Systems. NLP and IR Research Group.
     * Universidad Nacional de Educación a Distancia (UNED).
     * http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement
     */
    
    CondProbRefCorpus
}
