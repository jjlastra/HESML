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

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This interface sets the abstract methods which must be implemented by
 * any specific word pre-processing method.
 * 
 * @author alicia
 */

public interface IWordProcessing
{
    /**
     * This function releases all resources used by the word pre-processing method.
     */
    
    void clear();
    
    /**
     * This function returns the collection of word tokens extracted from
     * the raw input sentence.
     * 
     * @param strRawSentence
     * @return String[]
     * @throws java.io.FileNotFoundException 
     * @throws java.lang.InterruptedException 
     */
    
    String[] getWordTokens(
            String strRawSentence)  
            throws FileNotFoundException, IOException, InterruptedException, Exception;
    
    /**
     * Dynamically assign a label for a Wordprocessing object using the parameters configuration.
     * @return label
     */
    
    String getLabel();
    
    /**
     * Set the NER type
     */
    
    void setNERType(NERType nerType);
}
