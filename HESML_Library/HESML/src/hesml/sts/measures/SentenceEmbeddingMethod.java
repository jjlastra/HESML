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
 * This enumeration groups all sentence embedding methods.
 * @author j.lastra
 */

public enum SentenceEmbeddingMethod
{
    /**
     * This method evaluates all the pre-trained BERT-based models.
     */
    
    BERTEmbeddingModel,
    
    /**
     * Cer, Daniel, Yinfei Yang, Sheng-Yi Kong, Nan Hua, Nicole Limtiaco, 
     * Rhomni St. John, Noah Constant, et al. 2018. 
     * “Universal Sentence Encoder.” arXiv [cs.CL]. 
     * arXiv. http://arxiv.org/abs/1803.11175.
     */
    
    USEModel,
    
    /**
     * Le, Quoc, and Tomas Mikolov. 2014. 
     * “Distributed Representations of Sentences and Documents.” 
     * In International Conference on Machine Learning, 1188–96. jmlr.org.
     */
    
    ParagraphVector
}
