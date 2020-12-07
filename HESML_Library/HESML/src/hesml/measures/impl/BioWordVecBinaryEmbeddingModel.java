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

package hesml.measures.impl;

import hesml.measures.IPretrainedWordEmbedding;
import hesml.measures.SimilarityMeasureClass;
import hesml.measures.SimilarityMeasureType;
import hesml.measures.WordEmbeddingFileType;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.util.HashMap;

/**
 * This class implements an evaluator of pre-trained BioWordVec
 * embedding models trained with the FastText embedding method [2].
 * BioWordvec embeddings are introduced in the papers [1,2] below,
 * whilst fastText is introduced by Bojanowksi wt al. [3]
 * 
 * [1] Y. Zhang, Q. Chen, Z. Yang, H. Lin, Z. Lu, BioWordVec, improving biomedical
 *     word embeddings with subword information and MeSH, Sci Data. 6 (2019) 52.
 *     http://dx.doi.org/10.1038/s41597-019-0055-0
 * 
 * [2] Y. Zhang, Q. Chen, Z. Yang, H. Lin, Z. Lu, BioWordVec: Improving Biomedical
 *     Word Embeddings with Subword Information and MeSH Ontology, Figshare. (2018).
 *     http://dx.doi.org/10.6084/m9.figshare.6882647.v2
 * 
 * [3] P. Bojanowski, E. Grave, A. Joulin, T. Mikolov,
 *     Enriching Word Vectors with Subword Information, arXiv [cs.CL]. (2016).
 * 
 * @author j.lastra
 */

class BioWordVecBinaryEmbeddingModel implements IPretrainedWordEmbedding
{
    /**
     * This file contains the word vectors provided by the pre-trained
     * embedding model defining this measure.
     */
    
    private final String  m_strRawPretrainedEmbeddingFilename;
    
    /**
     * Offsets of the word vectors in the pre-trained model file.
     */
    
    private HashMap<String, Long>  m_WordOffsetsInFile;
    
    /**
     * Dimension of the pre-trained worc vectors
     */
    
    private int m_VectorDimension;
    
    /**
     * Reading cursor position during the loading process
     */
    
    private Long    m_currentReadingPosition;
    
    /**
     * Constructor
     * @param strVectorFilename 
     */
    
    BioWordVecBinaryEmbeddingModel(
        String  strVectorFilename) throws IOException, ParseException
    {
        // We save the filename and cretae the indexed buffer of word vectors
        
        m_strRawPretrainedEmbeddingFilename = strVectorFilename;

        // We only load those word vectors to be evaluated
        
        loadWordVectorOffsets();
    }
    
    /**
     * This function returns the type of pre-trained file managed by the
     * object.
     * @return 
     */
    
    @Override
    public WordEmbeddingFileType getWordEmbeddingFileType()
    {
        return (WordEmbeddingFileType.BioWordVecBinaryWordEmbedding);
    }
    
    /**
     * This function retrieves the offsets of all word vectors..
     * @param words Word set to be evaluated
     */
    
    private void loadWordVectorOffsets() throws IOException, ParseException
    {
        // Debug message
        
        System.out.println("Loading BioWordVec vectors from " + m_strRawPretrainedEmbeddingFilename);
        
        // We scan the sense vector file to retrieve all senses at the same time
        
        DataInputStream  reader = new DataInputStream(new FileInputStream(m_strRawPretrainedEmbeddingFilename));
        
        // We read the file header tpo get the word count and vector dimension
        
        m_currentReadingPosition = 0L;
        String strWordCount = readString(reader, 32);
        String strDimension = readString(reader, 10);
        
        m_VectorDimension = Integer.parseInt(strDimension);
        
        int wordCount = Integer.parseInt(strWordCount);
        long wordVectorByteLength = 4 * m_VectorDimension;
        
        // We create the offset buffer
        
        m_WordOffsetsInFile = new HashMap<>(wordCount);
        
        // We start the stopwatch to evaluate the loading time
        
        long startFileReadingTime = System.currentTimeMillis();
        
        // We extract all word vectors
        
        for (int iWord = 0; iWord < wordCount; iWord++)
        {
            // We get the word of the line
            
            String strWord = readString(reader, 32);
            
            // Clean the word from line ends
            
            strWord = strWord.replace("\n", "");
                       
            // We register the word vectro offset
            
            m_WordOffsetsInFile.put(strWord, m_currentReadingPosition);
            
            // We read all words and skip the word vectors
            
            reader.skip(wordVectorByteLength);
            m_currentReadingPosition += wordVectorByteLength;
        }
        
        // We close the file
        
        reader.close();
        
        // We measure the elapsed time to run the experiments

        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startFileReadingTime) / 1000;

        System.out.println("Overall BioWordVec file loading time (seconds) = " + seconds);
    }
    
    /**
     * This function reads a single vector from the pre-trained model file.
     * If the word is not in the embedding then a zero-values vector is returned.
     * @param reader
     * @return
     * @throws IOException 
     */
    
    @Override
    public double[] getWordVector(
            String  strWord) throws IOException
    {
        // We scan the sense vector file to retrieve all senses at the same time
        
        DataInputStream  reader = new DataInputStream(new FileInputStream(m_strRawPretrainedEmbeddingFilename));
        
        // We create the output vector
        
        double[] vector = new double[m_VectorDimension];
        
        // We get te word vector offset
        
        if (m_WordOffsetsInFile.containsKey(strWord))
        {
            // We skip the reading cursor until the vector position
            
            reader.skip(m_WordOffsetsInFile.get(strWord));
            
            // We read all components into byte buffer
        
            byte[] valueBuffer = new byte[4 * m_VectorDimension];
            reader.read(valueBuffer);
            
            // We build the buffer to order the data in LITTLE ENDIAN
            
            ByteBuffer buffer = ByteBuffer.allocate(valueBuffer.length);
            
            buffer.put(valueBuffer);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            for (int i = 0, j = 0; i < m_VectorDimension; i++, j += 4)
            {
                vector[i] = buffer.getFloat(j);
            }
        }
        
        // We clsoe the file
        
        reader.close();
        
        // We return the result
        
        return (vector);
    }
    
    /**
     * This function returns the next text string recorded in the file
     * being scanned.
     * @param reader
     * @param eosCode ASCII code of the END OF STRING symbol
     * @return 
     */
    
    private String readString(
        DataInputStream reader,
        int             eosCode) throws IOException
    {
        // We initialize le buffer de strings
        
        byte[] buffer = new byte[120];
        
        // We read until finding a zero value
        
        int iByte = 0;
        int byteRead;
        
        while (((byteRead = reader.read()) != -1)
                && (byteRead != eosCode))
        {
            buffer[iByte++] = (byte)byteRead;
        }
        
        // We increase the reading cursor position
        
        m_currentReadingPosition += (iByte + 1);
        
        // We create a buffer with the exact 
        
        String strOutput = new String(buffer, 0, iByte);
        
        // We reurn the result
        
        return (strOutput);
    }
    
    /**
     * This function is called with the aim of releasing all resources used
     * by the measure.
     */
    
    @Override
    public void clear()
    {
        m_WordOffsetsInFile.clear();
    }
    
    /**
     * This function returns the name of the vectors file.
     * @return 
     */
    
    @Override
    public String toString()
    {
        // We get the path of the files
        
        File path1 = new File(m_strRawPretrainedEmbeddingFilename);
        
        // We return the result
        
        return (path1.getName());
    }
    
    /**
     * This function returns the similarity measure class.
     * @return 
     */
    
    @Override
    public SimilarityMeasureClass getMeasureClass()
    {
        return (SimilarityMeasureClass.Similarity);
    }

    /**
     * This function returns the measure type.
     * @return 
     */
    
    @Override
    public SimilarityMeasureType getMeasureType()
    {
        return (SimilarityMeasureType.FastTextBinaryWordEmbedding);
    }
    
    /**
     * This function returns the semantic measure between two words.
     * @param strWord1 The first word
     * @param strWord2 The second word
     * @return 
     * @throws java.lang.InterruptedException 
     */

    @Override
    public double getSimilarity(
            String strWord1,
            String strWord2) throws InterruptedException, Exception
    {
        double similarity = 0.0;    // Returned value
        
        // We get vectors representing both words
        
        double[] word1 = getWordVector(strWord1);
        double[] word2 = getWordVector(strWord2);
        
        // We check the validity of the word vectors. They could be null if
        // any word is not contained in the vocabulary of the embedding.
        
        if ((word1 != null) && (word2 != null)
                && (word1.length == word2.length))
        {
            // We compute the cosine similarity function (dot product)
            
            for (int i = 0; i < word1.length; i++)
            {
                similarity += word1[i] * word2[i];
            }
            
            // We divide by the vector norms
            
            similarity /= (MeasureFactory.getVectorNorm(word1) * MeasureFactory.getVectorNorm(word2));
        }
        
        // We return the result
        
        return (similarity);
    }
    
    /**
     * This function returns the value returned by the similarity measure when
     * there is none similarity between both input concepts, or the concept
     * is not contained in the taxonomy.
     * @return 
     */
    
    @Override
    public double getNullSimilarityValue()
    {
        return (0.0);
    }
    
    /**
     * This function checks the existence of the word in the model.
     * @param strWord
     * @return 
     */
    
    @Override
    public boolean ContainsWord(String strWord)
    {
        return (m_WordOffsetsInFile.containsKey(strWord));
    }
    
    /**
     * This function returns the dimensions of the vectors in the model.
     * @return 
     */
    
    @Override
    public int getVectorDimension()
    {
        return (m_VectorDimension);
    }
}
