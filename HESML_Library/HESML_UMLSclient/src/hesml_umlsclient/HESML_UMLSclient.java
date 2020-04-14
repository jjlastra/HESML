/*
 * Copyright (C) 2020 Universidad Complutense de Madrid (UCM)
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

package hesml_umlsclient;

import hesml.taxonomy.ITaxonomy;
import hesml.taxonomy.IVertex;
import hesml.taxonomy.IVertexList;
import hesml.taxonomyreaders.snomed.ISnomedConcept;
import hesml.taxonomyreaders.snomed.ISnomedCtDatabase;
import hesml.taxonomyreaders.snomed.impl.SnomedCtFactory;

/**
 * This class implements a client program to use the SNOMED-CT taxonomy
 * an the collection of semantic measures defined in HESML.
 * This class loads and executes automatic reproducible experiments
 * on biomedicla word similarity.
 * @author j.lastra
 */

public class HESML_UMLSclient
{
    /**
     * This function admits the execution of reproducible word similarity
     * experiments based on SNOMED-CT defined in the (*.snexp) XML-based
     * file format.
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws Exception
    {
        // We set the UMLS directory
        
        String strUMLSdir = "../UMLS/SNOMED-CT_March_09_2020/Terminology";
        String strSNOMED_conceptFilename = "sct2_Concept_Snapshot_US1000124_20200301.txt";
        String strSNOMED_relationshipsFilename = "sct2_Relationship_Snapshot_US1000124_20200301.txt";
        String strSNOMED_descriptionFilename = "sct2_Description_Snapshot-en_US1000124_20200301.txt";
        
        // We load the SNOMED-CT databasa
        
        ISnomedCtDatabase snomedDatabase = SnomedCtFactory.loadSnomedDatabase(strUMLSdir,
                                        strSNOMED_conceptFilename,
                                        strSNOMED_relationshipsFilename,
                                        strSNOMED_descriptionFilename, true);
        
        // We build the SNOMED-CT taxonomy
        
        ITaxonomy snomedTaxonomy = snomedDatabase.getTaxonomy();
        
        // We retrieve the vertex node for Myocardium (SNOMED ID = 74281007)
        
        long myocardioum = 74281007L;
                
        ISnomedConcept concept = snomedDatabase.getConcept(74281007L);
        
        String[] strTerms = concept.getTerms();
        
        IVertex myocardiumNode = snomedTaxonomy.getVertexes().getById(myocardioum);
        
        long[] parents = myocardiumNode.getParents().getIDs();
        long[] children = myocardiumNode.getChildren().getIDs();
        
        Long[] conceptsIdForTerm = snomedDatabase.getSnomedConceptIdsEvokedByTerm("Cardiac muscle");
        
        IVertexList leaves = snomedTaxonomy.getVertexes().getLeaves();
        
        System.out.println("Number of leaves = " + leaves.getCount());
        
        System.out.println("Maximum depthMax = " + leaves.getGreatestDepthMax());
        
        
    }
}
