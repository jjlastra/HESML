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

package hesml.sts.measures;

/**
 * This enumeration groups all string-based sentence similarity methods.
 * @author j.lastra
 */

public enum StringBasedSentenceSimilarityMethod
{
    /**
     * JACCARD, and P. 1908. “Nouvelles Recherches Sur La Distribution Florale.” 
     * Bulletin de La SociÃ©tÃ© Vaudoise Des Sciences Naturelles 44: 223–70.
     */
    
    Jaccard,
    
    /**
     * Ukkonen, Esko. 1992. “Approximate String-Matching with Q-Grams 
     * and Maximal Matches.” Theoretical Computer Science 92 (1): 191–211.
     */
    
    Qgram,
    
    /**
     * Krause, Eugene F. 1986. Taxicab Geometry: 
     * An Adventure in Non-Euclidean Geometry. Courier Corporation.
     */
    
    BlockDistance,
    
    /**
     * Lawlor, Lawrence R. 1980. “Overlap, Similarity, 
     * and Competition Coefficients.” Ecology 61 (2): 245–51.
     */
    
    OverlapCoefficient,
    
    /**
     * Levenshtein, Vladimir I. 1966. “Binary Codes Capable of 
     * Correcting Deletions, Insertions, and Reversals.” 
     * In Soviet Physics Doklady, 10:707–10. nymity.ch.
     */
    
    Levenshtein
}
