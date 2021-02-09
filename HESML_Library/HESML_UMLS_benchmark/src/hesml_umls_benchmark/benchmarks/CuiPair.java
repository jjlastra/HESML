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

package hesml_umls_benchmark.benchmarks;

/**
 * This class represents a pair of CUI concepts
 * @author j.lastra
 */

public class CuiPair
{
    /**
     * CUI codes
     */
    
    private String  m_CuiCode1;
    private String  m_CuiCode2;

    /**
     * Constructor
     * @param cuiCode1
     * @param cuiCode2 
     */
    
    public CuiPair(
            String  cuiCode1,
            String  cuiCode2)
    {
        m_CuiCode1 = cuiCode1;
        m_CuiCode2 = cuiCode2;
    }
    
    /**
     * CUI code 1
     * @return 
     */
    
    public String getCuiCode1()
    {
        return (m_CuiCode1);
    }

    /**
     * CUI code 1
     * @return 
     */
    
    public String getCuiCode2()
    {
        return (m_CuiCode2);
    }
    
    /**
     * This fucntion checks if both objects are equal
     * @param obj
     * @return 
     */
    
    @Override
    public boolean equals(Object obj)
    {
        // We initialize the result
        
        boolean result = false;
        
        // Wecheck the object class
        
        if (obj.getClass() == this.getClass())
        {
            CuiPair other = (CuiPair) obj;
            
            // We check the identity case
            
            result = ((other.getCuiCode1() == m_CuiCode1)
                    && (other.getCuiCode2() == m_CuiCode2))
                    || ((other.getCuiCode1() == m_CuiCode2)
                    && (other.getCuiCode2() == m_CuiCode1));
        }
        
        // We return the result
        
        return (result);
    }
}
