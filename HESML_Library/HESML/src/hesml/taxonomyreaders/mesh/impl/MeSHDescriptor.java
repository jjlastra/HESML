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
 */

package hesml.taxonomyreaders.mesh.impl;

import hesml.taxonomyreaders.mesh.IMeSHDescriptor;

/**
 * This interface implements a MeSH Descriptor Data (MeSH concept).
 * @author j.lastra
 */

public class MeSHDescriptor implements IMeSHDescriptor
{
    /**
     * This function retunrs the unique descriptor ID
     * @return 
     */
    
    @Override
    public String getMeSHDescriptorId()
    {
        return ("");
    }
    
    /**
     * Every MeSH descriptor (concept) could be rerpesented in multiple
     * tree-like taxonomies. Thus, this fuction returns all taxonomy nodes
     * associated to the current MeSH descriptor (concept)
     * @return 
     */
    
    @Override
    public String[] getTreeNodeDescriptors()
    {
        return (new String[0]);
    }
    
    /**
     * This fucntion returns the preferred name for this concept.
     * @return 
     */
    
    @Override
    public String getPreferredName()
    {
       return (""); 
    }
}
