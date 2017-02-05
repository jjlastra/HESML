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
 * Similarity measure class. This enumeration indicates the type of value
 * returned by each similarity measures implementing the ISimilarityMeasure
 * interface. Most similarity measures returns a 'similarity value', but other
 * ones as the Jiang-Conrath returns a distance or a dissimilarity value as
 * the Sánchez et al. (2012) measure. Thus, this value is used by the
 * getSimilarity() function in the SimilaritySemanticMeasure abstract class
 * in order to decide the application of any proper conversion.
 * 
 * @author Juan Lastra-Díaz
 */

public enum SimilarityMeasureClass
{
    /**
     * The similarity measure returns a pure similarity value, not
     * necessarily normalized.
     */
    Similarity,

    /**
     * The similarity measure returns a distance value.
     */
    Distance,

    /**
     * The similarity measure returns a pure dissimilarity value.
     */
    Dissimilarity
}
