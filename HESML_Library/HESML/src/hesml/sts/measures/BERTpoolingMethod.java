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
 * This enumeration sets the pooling methods for BERT experiments.
 * 
 * The pooling strategies are defined by the bert-as-a-service library.
 * 
 * Xiao, Han. n.d. Bert-as-Service. Github. Accessed November 22, 2019. 
 * https://github.com/hanxiao/bert-as-service.
 * 
 * https://github.com/hanxiao/bert-as-service#q-what-are-the-available-pooling-strategies
 * 
 * 
 * @author j.lastra
 */

public enum BERTpoolingMethod
{
    /**
     * No pooling at all, useful when you want to use word embedding instead of sentence embedding. 
     * This will results in a [max_seq_len, 768] encode matrix for a sequence.
     */
    
    NONE,

    /**
     *  Take the average of the hidden state of encoding layer on the time axis.
     */
    
    REDUCE_MEAN,

    /**
     *  Take the maximum of the hidden state of encoding layer on the time axis.
     */
    
    REDUCE_MAX,

    /**
     *  Do REDUCE_MEAN and REDUCE_MAX separately and then concat them 
     * together on the last axis, resulting in 1536-dim sentence encodes.
     */
    
    REDUCE_MEAN_MAX,
    
    /**
     *  Get the hidden state corresponding to [CLS], i.e. the first token
     */
    
    FIRST_TOKEN,
    
    /**
     *  Get the hidden state corresponding to [SEP], i.e. the last token
     */
    
    LAST_TOKEN
}
