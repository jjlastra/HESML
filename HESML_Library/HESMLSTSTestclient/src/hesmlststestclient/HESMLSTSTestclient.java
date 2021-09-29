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

package hesmlststestclient;

import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Position;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

import hesml.HESMLversion;
import java.io.IOException;
import java.util.List;


/**
 * This class implements a basic client application of the HESML for sentence similarity
 * 
 * @author alicia and j.lastra
 */

public class HESMLSTSTestclient
{
    /**
     * This function loads an input XML file detailing a
     * set of reproducible experiments on sentence similarity.
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    
    public static void main(String[] args) throws IOException, InterruptedException, Exception
    {
        // We print the HESML version
        
        System.out.println("Running HESMLSTSClient V2R1 (2.1.0.0, February 2020) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
                + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        System.out.println("");
     
        // We get the start time

        long startFileProcessingTime = System.currentTimeMillis();
        
        long endTime = 0;
        long minutes = 0;
        long seconds = 0;
        
        // Initialize the result
        
        String sentence = "anorexia";
        
        String annotatedSentence = sentence;

        // Process the sentence using the Metamap objects


        MetaMapApi api = new MetaMapApiImpl();
        
        api.setOptions("-y");
        api.setOptions("-R SNOMEDCT_US");  
        List<Result> resultList = api.processCitationsFromString(sentence);
        
        
        Result result = resultList.get(0);

        int diff_lenght = 0;
        
        for (Utterance utterance: result.getUtteranceList()) {
            System.out.println("Utterance:");
            System.out.println(" Id: " + utterance.getId());
            System.out.println(" Utterance text: " + utterance.getString());
            System.out.println(" Position: " + utterance.getPosition());
            
            for (PCM pcm: utterance.getPCMList()) {
                System.out.println("Phrase:");
                System.out.println(" text: " + pcm.getPhrase().getPhraseText());
                
                System.out.println("Candidates:");
                
                
                System.out.println("Mappings:");
                for (Mapping map: pcm.getMappingList()) {
                  System.out.println(" Map Score: " + map.getScore());
                  for (Ev mapEv: map.getEvList()) {
                    System.out.println("   Score: " + mapEv.getScore());
                    System.out.println("   Concept Id: " + mapEv.getConceptId());
                    System.out.println("   Concept Name: " + mapEv.getConceptName());
                    System.out.println("   Preferred Name: " + mapEv.getPreferredName());
                    System.out.println("   Matched Words: " + mapEv.getMatchedWords());
                    System.out.println("   Semantic Types: " + mapEv.getSemanticTypes());
                    System.out.println("   MatchMap: " + mapEv.getMatchMap());
                    System.out.println("   MatchMap alt. repr.: " + mapEv.getMatchMapList());
                    System.out.println("   is Head?: " + mapEv.isHead());
                    System.out.println("   is Overmatch?: " + mapEv.isOvermatch());
                    System.out.println("   Sources: " + mapEv.getSources());
                    System.out.println("   Positional Info: " + mapEv.getPositionalInfo());
                    
                    List<Position> pos = mapEv.getPositionalInfo();
                    
                    for(Position p : mapEv.getPositionalInfo())
                    {
                        long pos_ini = p.getX() - diff_lenght;
                        long num_chars = p.getY();
                        
                        int total_lenght = annotatedSentence.length();
                        
                        int pos_end = (int) (pos_ini + num_chars);
                        
                        int cuiLenght = mapEv.getConceptId().length();
                        
                        diff_lenght += (int) (num_chars - cuiLenght);
                        
                        annotatedSentence = annotatedSentence.substring(0, (int) pos_ini) + mapEv.getConceptId() + annotatedSentence.substring(pos_end, total_lenght);
                    }

                  }
                }
            }
        }
        

        // We measure the elapsed time to run the experiments

        endTime = System.currentTimeMillis();
        minutes = (endTime - startFileProcessingTime) / 60000;
        seconds = (endTime - startFileProcessingTime) / 1000;

        System.out.println("Overall elapsed loading and computation time (minutes) = " + minutes);
        System.out.println("Overall elapsed loading and computation time (seconds) = " + seconds);
    }
}