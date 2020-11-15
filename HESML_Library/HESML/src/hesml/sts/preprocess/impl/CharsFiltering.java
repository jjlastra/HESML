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

package hesml.sts.preprocess.impl;

import hesml.sts.preprocess.CharFilteringType;
import hesml.sts.preprocess.ICharsFiltering;
import java.util.HashMap;

/**
 *  This class implements the punctuation marks 
 *  filtering methods used in the papers.
 *  @author alicia
 */

class CharsFiltering implements ICharsFiltering
{
    // Mapping holding the pairs (input chain, output chain) for all
    // detailed string replacements.
    
    private HashMap<String, String> m_replacingMap;
    
    /**
     *  Constructor with parameters.
     *  @param charFilteringType
     */
    
    CharsFiltering(
            CharFilteringType charFilteringType)
    {
        // We create the replacement mapping
        
        m_replacingMap = new HashMap<>();
        
        // We set the replcament patterns for each pre-defined method.
        
        switch (charFilteringType)
        {
            case Default:
                
                // Remove all the punctuation marks using the Java preexisting regex.
                // 	Punctuation: One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
                
                m_replacingMap.put("\\p{Punct}"," ");
                
                break;
                
            case BIOSSES:
                
                setBiosssesFilteringPatterns();
                
                break;
                
            case Blagec2019:
                
                setBlagecFilteringPatterns();
                
                break;
        }
        
        // We remove the special symbols from \x00 to \x7f
        
        m_replacingMap.put("[^\\x00-\\x7F]+","");
        
        // We register the last extra words applied as last filtering
        
        m_replacingMap.put("\\s{2,}", " ");
    }
    
    /**
     * This function releases all resources used by the object.
     */
    
    public void clear()
    {
        m_replacingMap.clear();
    }
    
    /**
     * Filter the sentence punctuation marks
     * @param strRawSentence
     * @return String
     */
    
    @Override
    public String filter(
            String strRawSentence)
    {
        // We apply the first triming
        
        String strFilteredSentence = strRawSentence.trim(); 
        
        // We apply all replacements by substituting all search patterns
        // registered in the global mapping.
        
        for (String strToBeReplaced : m_replacingMap.keySet())
        {
            strFilteredSentence = strFilteredSentence.replaceAll(strToBeReplaced,
                                    m_replacingMap.get(strToBeReplaced));
        }
        
        // We apply the last triming. Extra words are removed by the last replcament pattern.
                
        strFilteredSentence = strFilteredSentence.trim();
        
        // Return the result
        
        return (strFilteredSentence);
    }
    
    /**
     * Replace the punctuation marks as the BIOSSES2017 original code does.
     */
    
    private void setBiosssesFilteringPatterns()
    {
        // We register all string replcaments
        
        m_replacingMap.put("\\.","");
        m_replacingMap.put(";","");
        m_replacingMap.put("-","");
        m_replacingMap.put(":","");
        m_replacingMap.put(",","");
        m_replacingMap.put("_","");
        m_replacingMap.put("!", "");
        m_replacingMap.put("\\(", "");
        m_replacingMap.put("\\)", "");
        m_replacingMap.put("\\[", "");
        m_replacingMap.put("\\]", "");
        m_replacingMap.put("\\*", "");
        m_replacingMap.put("/", "");
        m_replacingMap.put("\\?", "");
    }
    
    /**
     * Replace the punctuation marks for string measures described in Blagec2019.
     */
    
    private void setBlagecFilteringPatterns()
    {
        // We set the filtering chain
        
        m_replacingMap.put("\\.","");
        m_replacingMap.put(",","");
        m_replacingMap.put(":","");
        m_replacingMap.put(";","");
        m_replacingMap.put("\\?", "");
        m_replacingMap.put("!", "");
        m_replacingMap.put("/", "");     
        m_replacingMap.put("-","");
    }
}
