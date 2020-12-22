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
 * This enumeration sets the available Python libraries for evaluating BERT-based methods.
 * @author alicia
 */

public enum MLPythonLibrary
{
    /**
     *  BERT evaluation using Tensorflow format (folder with a .ckpt.index file)
     */
    
    Tensorflow,

    /**
     *  BERT evaluation using Pytorch format (folder with a pytorch_model.bin file)
     * 
     *  NOTE: Pytorch-format evaluatios allows neither pooling methods 
     * (uses average pooling by default) nor Pooling Layers (uses the last layer by default)
     */
    
    Pytorch
}
