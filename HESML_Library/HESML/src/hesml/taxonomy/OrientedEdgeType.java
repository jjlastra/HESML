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

package hesml.taxonomy;

/**
 * Type of oriented edge for the half-edge representation.
 * In future HESML versions, this enumeration could include other
 * types of relationships, such as 'part of', or any other specific
 * type.
 * @author Juan Lastra-Díaz
 */

public enum OrientedEdgeType
{

    /**
     * It means that the base vertex (concept) of the oriented edge (half-edge) is
     * a subclass of the target vertex (parent concept).
     */

    SubClassOf,

    /**
     * It means that the base vertex (concept) of the oriented edge (half-edge) is
     * a superclass of the target vertex (child concept).
     */
     
    SuperClassOf
}
