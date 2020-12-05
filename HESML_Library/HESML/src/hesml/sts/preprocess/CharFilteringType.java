/* 
 * Copyright (C) 2016-2020 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml.sts.preprocess;

/**
 *  Enumeration class for group all filtering punctuation marks.
 *  @author alicia
 */

public enum CharFilteringType
{
    /**
     * Use no filter method.
     */
    
    None,
    
    /**
     * Filter all the punctuation marks using a standard Java regex.
     */
    
    Default,
    
    /**
     * Filter punctuation marks used by the authors of BIOSSES[1] paper
     * in their experiments.
     * 
     * Filtered symbols: .;-:,_!()[]*?, whitespaces and "/"
     * 
     * [1] G. Sogancioglu, H. Öztürk, A. Özgür,
     * BIOSSES: a semantic sentence similarity estimation system for
     * the biomedical domain, Bioinformatics. 33 (2017) i49–i58.
     */
    
    BIOSSES,
    
    /**
     * Filter punctuation marks used by Blagec et al. [1] in their
     * experiments.
     * Filtered symbols: full stop, comma, colon, semicolon, question mark, 
     * exclamation mark slash, dash.
     * 
     * [1] K. Blagec, H. Xu, A. Agibetov, M.
     * Samwald, Neural sentence embedding models for semantic similarity
     * estimation in the biomedical domain, BMC Bioinformatics. 20 (2019) 178.
     */
    
    Blagec2019
}
