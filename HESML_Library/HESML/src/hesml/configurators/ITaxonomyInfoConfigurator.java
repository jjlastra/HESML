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

// HESML references

import hesml.taxonomy.ITaxonomy;

/**
 * This interface defines the objects in charge of initializing
 * a taxonomy to be used by any IC-based semantic measure.
 * All the semantic measures use the IC-node and edge-weights
 * stored in the fields of the vertexes or edges. Therefore,
 * any object that implements this interface can set these
 * values to control the operation of any dependent measure.
 * @author Juan Lastra-Díaz
 */

public interface ITaxonomyInfoConfigurator
{
    /**
     * This function is invoked to compute and to store the data
     * in the taxonomy objects.
     * @param taxonomy Taxonomy whose IC model will be computed
     * @throws java.lang.Exception Unexpected error
     */
    
    void setTaxonomyData(ITaxonomy taxonomy) throws Exception;
}
