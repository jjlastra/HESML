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

package hesml.measures;

import java.io.IOException;

/**
 * This interface represents a pre-trained word embedding model which
 * can be interrogated to retrieve word vectors encoded by the model.
 * @author j.lastra
 */

public interface IPretrainedWordEmbedding extends IWordSimilarityMeasure
{
    /**
     * This function returns the type of pre-trained file managed by the
     * object.
     * @return 
     */
    
    WordEmbeddingFileType getWordEmbeddingFileType();
    
    /**
     * This function checks the existence of the word in the model.
     * @param strWord
     * @return 
     */
    
    boolean ContainsWord(String strWord);
    
    /**
     * This function returns the vector corresponding to the input word,
     * or a zero-value vector if the word is not in the model.
     * @param strWord
     * @return 
     */
    
    double[] getWordVector(String strWord)  throws IOException, Exception;
    
    /**
     * This function returns the dimensions of the vectors in the model.
     * @return 
     */
    
    int getVectorDimension();
}
