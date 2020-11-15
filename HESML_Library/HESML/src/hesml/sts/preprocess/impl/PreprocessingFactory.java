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
import hesml.sts.preprocess.IWordProcessing;
import hesml.sts.preprocess.TokenizerType;
import java.io.IOException;

/**
 * The aim of this class is to instantiate the preprocessing pipeline
 * in order to hide the implementation classes to the client code.
 * 
 * @author alicia
 */

public class PreprocessingFactory
{
    /**
     *  Constructor of the preprocess pipeline factory
     * 
     * @param stopWordFileName
     * @param tokenizerType
     * @param lowercaseNormalization
     * @param charFilteringType
     * @return 
     * @throws java.io.IOException
     */
    
    public static IWordProcessing getWordProcessing(
            String              stopWordFileName,
            TokenizerType       tokenizerType,
            boolean             lowercaseNormalization,
            CharFilteringType   charFilteringType) throws IOException
    {   
        return (new WordProcessing(tokenizerType,lowercaseNormalization,
                stopWordFileName, charFilteringType));
    }
    
    
    /**
     *  Constructor of the preprocess pipeline factory with Python wrapper for BERT.
     * 
     * @param stopWordFileName
     * @param tokenizerType
     * @param lowercaseNormalization
     * @param charFilteringType
     * @param tempDir
     * @param modelDirPath
     * @param pythonVirtualEnvironmentDir
     * @param pythonScriptDir
     * @return 
     * @throws java.io.IOException
     */
    
    public static IWordProcessing getWordProcessing(
            String              stopWordFileName,
            TokenizerType       tokenizerType,
            boolean             lowercaseNormalization,
            CharFilteringType   charFilteringType,
            String              tempDir,
            String              pythonVirtualEnvironmentDir,
            String              pythonScriptDir,
            String              modelDirPath) throws IOException
    {   
        return (new WordProcessing(tokenizerType, lowercaseNormalization,
                stopWordFileName, charFilteringType, tempDir,
                pythonVirtualEnvironmentDir, pythonScriptDir, modelDirPath));
    }    
}