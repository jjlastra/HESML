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

import bioc.BioCDocument;
import gov.nih.nlm.nls.metamap.document.FreeText;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import gov.nih.nlm.nls.ner.MetaMapLite;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a pair of CUI concepts
 * @author alicia
 */

public class AnnotateDataset
{
    /**
     * Metamap Lite instance
     */
    
    protected MetaMapLite m_metaMapLiteInst;
    
    /**
     * HashMap with the path of each dataset and the non-annotated pair of sentences
     */
    
    HashMap<String, ArrayList<ArrayList<String>>> m_datasets;
    
    /**
     * HashMap with the path of each dataset and the annotated pair of sentences
     */
    
    HashMap<String, ArrayList<ArrayList<String>>> m_annotatedDatasets;
    
    /**
     * Constructor
     * @param strDatasetPaths
     * @throws java.lang.Exception
     */
    
    public AnnotateDataset(String[]  strDatasetPaths) throws Exception 
    {
        // Initialize the variables
        
        m_datasets = new HashMap<>();
        m_metaMapLiteInst = null;
        m_annotatedDatasets = new HashMap<>();
        
        // We load the Metamap Lite instance
                
        try {
            // We load METAMAP Lite

            loadMetamapLite();
        } catch (ClassNotFoundException | InstantiationException 
                | NoSuchMethodException | IllegalAccessException 
                | IOException ex) {
            Logger.getLogger(AnnotateDataset.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // We load the dataset
        
        loadDatasetBenchmark(strDatasetPaths);
    }
    
    /**
     * Datasets getter
     * @param strDatasetPath
     * @return m_datasets
     */
    
    public ArrayList<ArrayList<String>> getDatasets(String strDatasetPath)
    {
        return m_datasets.get(strDatasetPath);
    }
    
    /**
     * Datasets getter
     * @param strDatasetPath
     * @return m_datasets
     */
    
    public ArrayList<ArrayList<String>> getAnnotatedDatasets(String strDatasetPath)
    {
        return m_annotatedDatasets.get(strDatasetPath);
    }
    
    
    /**
     * This function loads the dataset matrix with the sentence pairs for the evaluation.
     * @param strDatasetFilename 
     * @throws Exception 
     */
    
    private void loadDatasetBenchmark(
        String[]  strDatasetFilenames) throws Exception
    {
        System.out.println("Loading the sentence similarity datasets");
        
        for(String strDatasetPath : strDatasetFilenames)
        {
            // Initialize the sentences
        
            ArrayList<String> first_sentences = new ArrayList<>();
            ArrayList<String> second_sentences = new ArrayList<>();
            
            // Read the benchmark CSV 
        
            BufferedReader csvReader = new BufferedReader(new FileReader(strDatasetPath));

            String strRowLine;

            while ((strRowLine = csvReader.readLine()) != null) 
            {
                // Split the line by tab

                String[] sentencePairs = strRowLine.split("\t");

                // Fill the matrix with the sentences

                first_sentences.add(sentencePairs[0]);
                second_sentences.add(sentencePairs[1]);
            }
            
            // We initialize the array with the pairs of sentences
            
            ArrayList<ArrayList<String>> pairs = new ArrayList<>();
            
            pairs.add(first_sentences);
            pairs.add(second_sentences);
            
            // We close the file

            csvReader.close();
            
            m_datasets.put(strDatasetPath, pairs);
            m_annotatedDatasets.put(strDatasetPath, new ArrayList<>());
        }
    }
    
    /**
     * This function annotates all the sentences with CUI instances.
     * @param strDatasetPath
     * @throws java.io.IOException
     */
    
    public void annotate(String strDatasetPath) throws IOException, Exception
    {
        // If the annotation has not been performed yet, annotate
        
        if(m_annotatedDatasets.get(strDatasetPath).isEmpty())
        { 
            // Warning message
        
            System.out.println("Annotating the dataset with Metamap Lite...");
        
            // We get the dataset path

            ArrayList<ArrayList<String>> sentences = m_datasets.get(strDatasetPath);

            // We create an auxiliary array with the sentence pairs

            ArrayList<ArrayList<String>> sentencesAnnotated = new ArrayList<>();

            // We create two auxiliary arrays 

            ArrayList<String> firstSentencesAnnotated = new ArrayList<>();
            ArrayList<String> secondSentencesAnnotated = new ArrayList<>();

            // We annotate all the sentences

            for (int i = 0; i < sentences.get(0).size(); i++) 
            { 	
                //System.out.println("Annotating sentence: " + i + " from dataset: " + strDatasetPath);
                
                // Annotate the sentences and add to the array

                firstSentencesAnnotated.add(annotateSentence(sentences.get(0).get(i)));

                // Annotate the sentences and add to the array

                secondSentencesAnnotated.add(annotateSentence(sentences.get(1).get(i)));
            } 

            // Add the pairs of sentences to the array

            sentencesAnnotated.add(firstSentencesAnnotated);
            sentencesAnnotated.add(secondSentencesAnnotated);

            m_annotatedDatasets.put(strDatasetPath, sentencesAnnotated);
        }
        else
        {
            // Warning message
        
            System.out.println("The dataset has been previously annotated.");
        }
    }
    
    /**
     * This function annotates a sentence with CUI codes replacing 
     * keywords with codes in the same sentence.
     * @return 
     */
    
    private String annotateSentence(
            String sentence) throws InvocationTargetException, IOException, Exception
    {
        // Initialize the result
        
        String annotatedSentence = sentence;
        
        // Processing Section

        // Each document must be instantiated as a BioC document before processing
        
        // The sentence should have almost an alphanumeric character
        
        if(sentence.matches("^.*[a-zA-Z0-9]+.*$"))
        {
            BioCDocument document = FreeText.instantiateBioCDocument(sentence);
        
            // Proccess the document with Metamap

            List<Entity> entityList = m_metaMapLiteInst.processDocument(document);

            // For each keyphrase, select the first CUI candidate and replace in text.

            for (Entity entity: entityList) 
            {
                for (Ev ev: entity.getEvSet()) 
                {
                    // Replace in text

                    annotatedSentence = annotatedSentence.replaceAll(entity.getMatchedText(), ev.getConceptInfo().getCUI());
                }
            }
        }
        
        // Return the result
        
        return (annotatedSentence);
    }
    
    /**
     * This function loads the Metamap Lite instance before executing the queries.
     */
    
    private void loadMetamapLite() throws ClassNotFoundException, 
                                    InstantiationException, NoSuchMethodException, 
                                    IllegalAccessException, IOException
    {
        // Warning message
        
        System.out.println("Loading the Metamap Lite Instance...");

        // Initialization Section
        
        Properties myProperties = new Properties();
        
        // Select the 2018AB database
        
        myProperties.setProperty("metamaplite.index.directory", "../public_mm_lite/data/ivf/2018ABascii/USAbase/");
        myProperties.setProperty("opennlp.models.directory", "../public_mm_lite/data/models/");
        myProperties.setProperty("opennlp.en-pos.bin.path", "../public_mm_lite/data/models/en-pos-maxent.bin");
        myProperties.setProperty("opennlp.en-sent.bin.path", "../public_mm_lite/data/models/en-sent.bin");
        myProperties.setProperty("opennlp.en-token.bin.path", "../public_mm_lite/data/models/en-token.bin");
        
        myProperties.setProperty("metamaplite.sourceset", "MSH");

        // We create the METAMAP maname
        
        m_metaMapLiteInst = new MetaMapLite(myProperties);
    }
}
