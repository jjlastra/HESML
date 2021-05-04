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

package hesml.sts.documentreader.impl;

import hesml.sts.documentreader.HSTSIDocument;
import hesml.sts.documentreader.HSTSIParagraph;
import hesml.sts.preprocess.IWordProcessing;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLStreamException;

// Used bioc pipeline for reading the documents.

import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCPassage;
import bioc.io.BioCCollectionReader;
import bioc.io.BioCFactory;
import bioc.preprocessing.pipeline.PreprocessingPipeline;

// Used Stanford CoreNLP for extracting sentences.

import edu.stanford.nlp.ling.CoreLabel; 

/**
 * The BioCReader class gets the directory path of the document and the 
 * type of document and create an HSTSIDocument object.
 * @author Alicia Lara-Clares
 * 
 * @todo STATIC FUNCTIONS!
 */

class BioCReader 
{ 
    // ID of the document
    
    private final int m_idDocument;
    
    // File input with BioC format
    
    private final File m_biocFile;
    
    // Word preprocesser
    
    private final IWordProcessing m_preprocessing;
    
    /**
     * Constructor.
     * 
     * @param idDocument
     * @param biocFile
     * @param preprocessing 
     */
    
    BioCReader(
            int idDocument,
            File biocFile,
            IWordProcessing preprocessing)
    {
        // Initialize the variables
        
        m_idDocument = idDocument;
        m_biocFile = biocFile;
        m_preprocessing = preprocessing;
    }
    
    /**
     * Loads the BioC file, extract the documents and paragraphs.
     * -> A BioCColection has one or more BioCDocuments.
     * -> Each BioCDocument has one or more BioCPassages.
     * -> Each BioCPassage has one text (similar to paragraph).
     * @param strDocumentsPath
     * @param documentType
     * @return HSTSIDocument
     * @throws IOException
     * @throws XMLStreamException 
     */
    
    public HSTSIDocument readFile() 
            throws FileNotFoundException, XMLStreamException
    {
        // read BioC XML collection
        
        BioCCollection collection = this.readBioCCollection();
        
        // Extract the documents from the BioC collection
        
        List<BioCDocument> biocDocuments = collection.getDocuments();

        // Create an IDocument
        
        String str_documentPath = m_biocFile.getAbsolutePath();
        HSTSDocument document = new HSTSDocument(m_idDocument, str_documentPath, m_preprocessing);

        // Create a ParagraphList
        
        HSTSParagraphList paragraphList = new HSTSParagraphList();

        // Iterate the BioCCollection: a list of documents
         
        for (Iterator<BioCDocument> iterator = biocDocuments.iterator(); iterator.hasNext();) 
        {
            BioCDocument nextBioCDocument = iterator.next();

            // Get the passages of the document (similar to paragraphs)
            
            List<BioCPassage> bioCPassages = nextBioCDocument.getPassages();

            // Iterate over passages and extract the texts
            
            int idParagraph = 0;
            for (Iterator<BioCPassage> iterator1 = bioCPassages.iterator(); iterator1.hasNext();) 
            {
                BioCPassage nextBioCPassage = iterator1.next();

                // Create an IParagraph
                
                HSTSParagraph paragraph = new HSTSParagraph(idParagraph, m_idDocument);
                
                // Get and set the text in the paragraph.
                
                String strParagraph = nextBioCPassage.getText();
                paragraph.setText(strParagraph);
                
                // In BioC, the sentences are collected using the BioC library.
                
                paragraph.setSentenceList(this.extractBioCSentences(paragraph));
                
                // Add the text to the paragraph
                
                paragraphList.addParagraph(paragraph);
                
                idParagraph++;
            }
        }
        
        // Clear the list of documents
        
        biocDocuments.clear();
        
        // Set the collection to null 
        
        collection = null;
        
        // Add the paragraphs to the document
        
        document.setParagraphList(paragraphList);
        
        // Return the result
        
        return (document);
    }
    
    /**
     * Call to the BioC Java Pipeline library to perform the sentence splitting.
     * 
     * "Sentence segmenter.
     * An efficient sentence segmenter, DocumentPreprocessor, is used to produce a list of sentences from a plain text. 
     * It is a creation of the Stanford NLP group using a heuristic finite-state machine that assumes the sentence 
     * ending is always signaled by a fixed set of characters. Tokenization is performed by the default rule-based 
     * tokenizer of the sentence segmenter, PTBTokenizer, before the segmenting process to divide text into a sequence of tokens. 
     * The ‘invertible’ option of the tokenizer is invoked to ensure that multiple whitespaces 
     * are reflected in token offsets so that the resulting tokens can be faithfully converted back to the original text. 
     * Sentence segmentation is then a deterministic consequence of tokenization."
     * Cited from:
     * Comeau, D. C., Liu, H., Islamaj Doğan, R., & Wilbur, W. J. (2014). 
     * Natural language processing pipelines to annotate BioC collections with 
     * an application to the NCBI disease corpus. Database : the journal of 
     * biological databases and curation, 2014, bau056. doi:10.1093/database/bau056
     * 
     * @param HSTSIParagraph
     * @return HSTSSentenceList
     */
    
    private HSTSSentenceList extractBioCSentences(
            HSTSIParagraph paragraph)
    {
        // Initialize a bioc preprocessing pipeline
        
        PreprocessingPipeline preprocessingPipeline = new PreprocessingPipeline("sentence");
        
        // Get the text from the bioc paragraph
        
        String strParagraph = paragraph.getText();
        preprocessingPipeline.setParseText(strParagraph);
        
        // Create an internal bioc object for storing the sentences
        
        List<List<CoreLabel>> sentencesCoreLabels = preprocessingPipeline.performSentenceSplit();
        
        int idParagraph = paragraph.getId();
        int idDocument = paragraph.getIdDocument();
    
        //Create a new HSTSSentenceList object
        
        HSTSSentenceList sentenceList = new HSTSSentenceList();
        
        // Iterate the sentences and extract the text using the positions.
        
        int idSentence = 0;
        for(List<CoreLabel> sentenceCoreLabel : sentencesCoreLabels) 
        {
            // Get the text from the sentence
            
            String text = strParagraph.substring(sentenceCoreLabel.get(0).beginPosition(), sentenceCoreLabel.get(sentenceCoreLabel.size()-1).endPosition());
            
            // Create a new sentence object to store the extracted sentence.
            
            HSTSSentence sentence = new HSTSSentence(idSentence, idParagraph, idDocument, text);
                    
            //add the sentence to the sentences list
        
            sentenceList.addSentence(sentence);
            idSentence++;
        }
        
        // Clear the list of sentences
        
        sentencesCoreLabels.clear();
        
        // Return the result
        
        return (sentenceList);
    }
            
    /**
     * Read a bioc document and create a bioc collection.
     * A bioc collection is an internal object from the Bioc library.
     * 
     * @param biocFile
     * @return
     * @throws FileNotFoundException
     * @throws XMLStreamException 
     */
    
    private BioCCollection readBioCCollection() 
            throws FileNotFoundException, XMLStreamException
    {
        // read BioC XML collection
        
        Reader inputReader = new FileReader(m_biocFile);
        BioCFactory bioCFactory = BioCFactory.newFactory("STANDARD");
        
        // Now I create a BioCCollection item. 
        
        BioCCollectionReader collectionReader =
        bioCFactory.createBioCCollectionReader(inputReader);
        BioCCollection collection = collectionReader.readCollection();
        
        // Return the collection
        
        return (collection);
    }
        
    /**
     * Once the proccess has finished, clean the variables from memory.
     */
    
    public void clean()
    {
        // Clean the preprocessing object
        
        m_preprocessing.clear();
    }
}
