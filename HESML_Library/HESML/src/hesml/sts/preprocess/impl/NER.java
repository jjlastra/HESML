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



// CTAKES dependencies

import org.apache.ctakes.typesystem.type.textsem.*;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;

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
    
    // Ctakes pipeline
    
    protected AnalysisEngine m_pipelineIncludingUmlsDictionaries;
    
    /**
     * Constructor with parameters.
     * @param NERType 
     */
    
    NER(NERType nerType)
    {
        // Set the ner type
            
        m_NERType = nerType;
        
        m_metaMapLiteInst = null;
        m_metaMapInst = null;
        m_pipelineIncludingUmlsDictionaries = null;
        
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
                
                loadMetamap("-R SNOMEDCT");

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
            
            case Ctakes:
                
                // Loads Ctakes
                
                loadCtakes();
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
            
            case Ctakes:
                
                // Annotate the sentence
                
                annotatedSentence = annotateSentenceCtakes(strRawSentence);

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
        
        // Select the 2020AA database
        
        myProperties.setProperty("metamaplite.index.directory", "/home/user/HESML_DATA/public_mm_lite/data/ivf/2020AA/USAbase/");
        myProperties.setProperty("opennlp.models.directory", "/home/user/HESML_DATA/public_mm_lite/data/models/");
        myProperties.setProperty("opennlp.en-pos.bin.path", "/home/user/HESML_DATA/public_mm_lite/data/models/en-pos-maxent.bin");
        myProperties.setProperty("opennlp.en-sent.bin.path", "/home/user/HESML_DATA/public_mm_lite/data/models/en-sent.bin");
        myProperties.setProperty("opennlp.en-token.bin.path", "/home/user/HESML_DATA/public_mm_lite/data/models/en-token.bin");
        
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
    
    /**
     * This function loads the Ctakes instance before executing the queries.
     */
    
    private void loadCtakes() throws ClassNotFoundException, 
                                    InstantiationException, NoSuchMethodException, 
                                    IllegalAccessException, IOException, ResourceInitializationException, InvalidXMLException
    {
        // Create a new ctakes instance using the selected options 

        m_pipelineIncludingUmlsDictionaries = AnalysisEngineFactory.createEngineFromPath(
                "/home/user/HESML_DATA/apache-ctakes-4.0.0.1-src/desc/ctakes-clinical-pipeline/desc/analysis_engine/CuisOnlyPlaintextUMLSProcessor.xml");
       
    }
    
    /**
     * This function annotates a sentence with CUI codes replacing 
     * keywords with codes in the same sentence.
     * @return 
     */
    
    private String annotateSentenceCtakes(
            String sentence) throws InvocationTargetException, IOException, Exception
    {
        // Initialize the result

        String annotatedSentence = sentence;

        //Create cTakes Pipeline
         
        JCas jCas = m_pipelineIncludingUmlsDictionaries.newJCas();
        
        //Load text to pipeline
        
        jCas.setDocumentText(sentence);
        
        //Process document
        
        m_pipelineIncludingUmlsDictionaries.process(jCas);

        //Create cTakes Pipeline
        
        List<Class<? extends Annotation>> semClasses = new ArrayList<>();
        
        // CUI types: ALL

        semClasses.add(IdentifiedAnnotation.class);

        //Parse the results
        
        //Iterate over relevant Annotation Types
        
        for(Class<? extends Annotation> semClass : semClasses){

            //iterate over annotations
            
            for(Annotation annot : JCasUtil.select(jCas, semClass)){
                
                //Create Response object
          
                if(annot instanceof IdentifiedAnnotation) 
                {
                    IdentifiedAnnotation ia = (IdentifiedAnnotation) annot;
                    
                    if(ia.getOntologyConceptArr() != null) 
                    {
                        //Extract all umls Concepts found for annotation
                        
                        for (UmlsConcept concept : JCasUtil.select(ia.getOntologyConceptArr(), UmlsConcept.class)) 
                        {
                            //Save each found match for UMLS concept
                            
//                            Map<String, String> atts = new HashMap<>();
//                            atts.put("codingScheme", concept.getCodingScheme());
//                            atts.put("cui", concept.getCui());
//                            atts.put("text", ia.getCoveredText());
//                            atts.put("begin", String.valueOf(ia.getBegin()));
//                            atts.put("end", String.valueOf(ia.getEnd()));
//                            
//                            System.out.println(atts.toString());

                            // replace substring from indexes
                            
                            annotatedSentence = annotatedSentence.replaceAll(ia.getCoveredText(), " " + String.valueOf(concept.getCui()) + " ");
//                            System.out.println(annotatedSentence);
                        }
                    }
                }
            }
        }
        
        // Return the result
        
        return (annotatedSentence);
    }

    /**
     * Clear the data
     */
    
    @Override
    public void clear() 
    {
        if(m_metaMapLiteInst != null)
            m_metaMapLiteInst = null;
        
        if(m_metaMapInst != null)
            m_metaMapInst.disconnect();
        
//        if(m_pipelineIncludingUmlsDictionaries != null)
//            m_pipelineIncludingUmlsDictionaries.destroy();
    }
}