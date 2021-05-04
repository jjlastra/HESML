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

import bioc.BioCDocument;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;
import gov.nih.nlm.nls.metamap.document.FreeText;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import gov.nih.nlm.nls.ner.MetaMapLite;
import hesml.sts.preprocess.INER;
import hesml.sts.preprocess.NERType;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Implementation of the NER methods
 * @author alicia
 */

class NER implements INER
{
    // Type of tokenization method
    
    private final NERType m_NERType;
    
    // Metamap Lite instance
    
    protected MetaMapLite m_metaMapLiteInst;
    
    // Metamap instance 
    
    protected MetaMapApi m_metaMapInst;
    
    /**
     * Constructor with parameters.
     * @param NERType 
     */
    
    NER(NERType nerType)
    {
        // Set the ner type
            
        m_NERType = nerType;
        
        // load the object instance
         
        try {
            load();
        } catch (Exception ex) {
            Logger.getLogger(NER.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This function returns the NER method used in the object
     * @return m_NERType
     */
    
    @Override
    public NERType getNERType() {
        return (m_NERType);
    }
    
    /**
     * This function annotates a sentence using a NER tool
     * 
     * @param strRawSentence
     * @return
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws Exception 
     */
    
    private void load() 
            throws InvocationTargetException, ClassNotFoundException, 
            InstantiationException, IOException, NoSuchMethodException, Exception
    {
        // We create the NER

        switch (m_NERType)
        {
            case MetamapLite:

                // Load the Metamap Lite instance
                
                loadMetamapLite();

                break;

            case MetamapSNOMEDCT:
                
                // Loads Metamap using word sense disambiguation
                
//                loadMetamap("-R SNOMEDCT"); 
                loadMetamap(""); 

                break;
                
            case MetamapMESH:
                
                // Loads Metamap using word sense disambiguation
                
                loadMetamap("-R MSH"); 

                break;
                
            case MetamapSNOMEDCT_SP:
                
                // Loads Metamap using word sense disambiguation
                
                loadMetamap("-R SNOMEDCT_US,SCTSPA"); 

                break;
                
            case MetamapMESH_SP:
                
                // Loads Metamap using word sense disambiguation
                
                loadMetamap("-R MSH,MSHSPA"); 

                break;
        }
    }

    /**
     * This function annotates a sentence using a NER tool
     * 
     * @param strRawSentence
     * @return
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws Exception 
     */
    
    @Override
    public String annotate(String strRawSentence) 
            throws InvocationTargetException, ClassNotFoundException, 
            InstantiationException, IOException, NoSuchMethodException, Exception
    {
        // Initialize the output
        
        String annotatedSentence = ""; 
        
        // We create the NER

        switch (m_NERType)
        {
            case None:

                // No modification

                annotatedSentence = strRawSentence; 

                break;

            case MetamapLite:
                
                // Annotate the sentence
                
                annotatedSentence = annotateSentenceMetamapLite(strRawSentence);

                break;

            case MetamapSNOMEDCT:
            case MetamapSNOMEDCT_SP:
            case MetamapMESH:
            case MetamapMESH_SP:
                                
                // Annotate the sentence
                
                annotatedSentence = annotateSentenceMetamap(strRawSentence);

                break;
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
        // Initialization Section
        
        Properties myProperties = new Properties();
        
        // Select the 2018AB database
        
        myProperties.setProperty("metamaplite.index.directory", "../public_mm_lite/data/ivf/2020AA/USAbase/");
        myProperties.setProperty("opennlp.models.directory", "../public_mm_lite/data/models/");
        myProperties.setProperty("opennlp.en-pos.bin.path", "../public_mm_lite/data/models/en-pos-maxent.bin");
        myProperties.setProperty("opennlp.en-sent.bin.path", "../public_mm_lite/data/models/en-sent.bin");
        myProperties.setProperty("opennlp.en-token.bin.path", "../public_mm_lite/data/models/en-token.bin");
        
        myProperties.setProperty("metamaplite.sourceset", "MSH");

        // We create the METAMAP maname
        
        m_metaMapLiteInst = new MetaMapLite(myProperties);
        
    }
     
    /**
     * This function annotates a sentence with CUI codes replacing 
     * keywords with codes in the same sentence.
     * @return 
     */
    
    private String annotateSentenceMetamapLite(
            String sentence) throws InvocationTargetException, IOException, Exception
    {
        // Initialize the result
        
        String annotatedSentence = sentence;
        
        // Processing Section

        // Each document must be instantiated as a BioC document before processing
        
        BioCDocument document = FreeText.instantiateBioCDocument(sentence);
        
        // Proccess the document with Metamap
        
        List<Entity> entityList = m_metaMapLiteInst.processDocument(document);

        // For each keyphrase, select the first CUI candidate and replace in text.
        
        for (Entity entity: entityList) 
        {
            for (Ev ev: entity.getEvSet()) 
            {
                // Replace in text
                
                annotatedSentence = annotatedSentence.replaceAll(entity.getMatchedText(), " " + ev.getConceptInfo().getCUI() + " ");
            }
        }
        
        // Return the result
        
        return (annotatedSentence);
    }
    
    /**
     * This function loads the Metamap instance before executing the queries.
     */
    
    private void loadMetamap(String options) throws ClassNotFoundException, 
                                    InstantiationException, NoSuchMethodException, 
                                    IllegalAccessException, IOException
    {
        // Create a new Metamap instance using the selected options 
        
        m_metaMapInst = new MetaMapApiImpl();
        m_metaMapInst.setHost("127.0.0.1");
        m_metaMapInst.setPort(8066);
        List<String> theOptions = new ArrayList<>();
        theOptions.add("-y"); // turn on Word Sense Disambiguation
        theOptions.add(options);
           
        
        theOptions.forEach(opt -> {
            m_metaMapInst.setOptions(opt);
        });
       
    }
    
    /**
     * This function annotates a sentence with CUI codes replacing 
     * keywords with codes in the same sentence.
     * @return 
     */
    
    private String annotateSentenceMetamap(
            String sentence) throws InvocationTargetException, IOException, Exception
    {
        // Initialize the result
        
        String annotatedSentence = sentence;
        
        // Process the sentence using the Metamap objects
        
        List<Result> resultList = m_metaMapInst.processCitationsFromString(sentence);
        for (Result result: resultList) 
        {
            for (Utterance utterance: result.getUtteranceList()) 
            {
                for (PCM pcm: utterance.getPCMList()) 
                {
                  for (Mapping map: pcm.getMappingList()) 
                  {
                    for (gov.nih.nlm.nls.metamap.Ev mapEv: map.getEvList()) 
                    {
                        for(String matchedWord : mapEv.getMatchedWords())
                        {
                            annotatedSentence = annotatedSentence.replaceAll(matchedWord, " " + mapEv.getConceptId() + " ");
                        }
                    }
                }
            }
          }
        }
        
        // Return the result
        
        return (annotatedSentence);
    }
}