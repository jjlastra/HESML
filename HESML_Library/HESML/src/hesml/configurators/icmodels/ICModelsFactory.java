/*
 * Copyright (C) 2016 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml.configurators.icmodels;

// HESML references

import hesml.configurators.CorpusBasedICModelType;
import hesml.configurators.IntrinsicICModelType;
import hesml.configurators.ITaxonomyInfoConfigurator;

/**
 * This class creates the instances of all the IC models, either intrinsic or
 * corpus-based.
 * @author Juan Lastra-Díaz
 */

public class ICModelsFactory
{
    /**
     * This function creates a well-founded corpus-based IC model for
     * Pedersen WN-based frequency files.
     * @param modelType
     * @param strPedersenFile Corpus-based concept frequency file
     * @return Corpus-based IC model
     * @throws java.lang.Exception      * 

     */
    
    public static ITaxonomyInfoConfigurator getCorpusICmodel(
            CorpusBasedICModelType  modelType,
            String                  strPedersenFile) throws Exception
    {
        ITaxonomyInfoConfigurator   icModel = null;    // Returned value
        
        // We create the IC model
        
        switch (modelType)
        {
            case Resnik:

                icModel = new CorpusResnikICmodel(strPedersenFile);
                
                break;
                
            case CondProbCorpus:
                
                icModel = new CondProbCorpusICmodel(strPedersenFile);
                
                break;
                
            case CondProbRefCorpus:
                
                icModel = new CondProbRefinedCorpusICmodel(strPedersenFile);
                
                break;
        }
        
        // We return the results
        
        return (icModel);
    }
    
    /**
     * This function returns an instance of an intrinsic IC model.
     * @param method Intrinsic IC model type to be created
     * @return IC model
     * @throws java.lang.Exception      * 

     */
    
    public static ITaxonomyInfoConfigurator getIntrinsicICmodel(
            IntrinsicICModelType method) throws Exception
    {
        ITaxonomyInfoConfigurator    icModel = null; // Returned value
        
        // We creates an instance of the required preprocessor
        
        switch (method)
        {               
            case Blanchard:
                
                icModel = new BlanchardICmodel();
                
                break;
                
            case Adhikari:
                
                icModel = new AdhikariICmodel();
                
                break;
                
            case AouichaTaiebAsGIC:
                
                icModel = new AouichaTaiebAsGICICmodel();
                
                break;
                
            case CondProbLogistic:
                
                icModel = new CondProbLogisticICmodel(8.0);
                
                break;
                
            case CondProbLogisticK10:
                
                icModel = new CondProbLogisticICmodel(10.0);
                
                break;

            case CondProbLogisticK12:
                
                icModel = new CondProbLogisticICmodel(12.0);
                
                break;
                
            case CondProbCosine:
                
                icModel = new CondProbCosineICmodel();
                
                break;
            
            case CondProbHyponyms:
                
                icModel = new CondProbHyponymsICmodel();
                
                break;
                
            case CondProbLeaves:

                icModel = new CondProbLeavesICmodel();
                
                break;
                               
            case CondProbUniform:
                
                icModel = new CondProbUniformICmodel();
                
                break;
                
            case Seco:
                
                icModel = new SecoICmodel();
                
                break;
                
            case Zhou:
                
                icModel = new ZhouICmodel();
                
                break;
                
            case Sanchez2011:
                
                icModel = new Sanchez2011ICmodel();
                
                break;
                               
            case Meng:
                
                icModel = new Meng2012ICmodel();
                
                break;
                
            case Yuan:

                icModel = new Yuan2013ICmodel();
                
                break;
                                
            case Harispe:
                
                icModel = new Harispe2012ICmodel();
                
                break;
                
            case HadjTaieb:
                
                icModel = new HadjTaieb2013ICmodel();
                
                break;
                
            case HadjTaiebHypoValue:
                
                icModel = new HadjTaiebHypoValueICmodel();
                
                break;
                       
            case Sanchez2012:
                
                icModel = new Sanchez2012ICmodel();
                
                break;
                
            case CondProbRefCosine:
                
                icModel = new CondProbRefinedCosineICmodel();
                
                break;
                
            case CondProbRefCosineLeaves:
                
                icModel = new CondProbRefinedCosineLeavesICmodel();
                
                break;
            
            case CondProbRefHyponyms:
                
                icModel = new CondProbRefinedHypoICmodel();
                
                break;
                
            case CondProbRefLeaves:
                
                icModel = new  CondProbRefinedLeavesICmodel();
                
                break;
                
            case CondProbRefLogistic:
                
                icModel = new CondProbRefinedLogisticICmodel(8.0);
                
                break;
                
            case CondProbRefLogisticLeaves:
                
                icModel = new CondProbRefinedLogisticLeavesICmodel();
                
                break;
                
            case CondProbRefUniform:
                
                icModel = new CondProbRefinedUniformICmodel();
                
                break;
                
            case CondProbRefLeavesSubsumers:
                
                icModel = new CondProbRefinedLeavesSubsumers();
                
                break;
                
            case CondProbRefLeavesSubsumersRatio:
                
                icModel = new CondProbRefinedLeavesSubsumersRatio();
                
                break;   
                
            case CondProbRefSubsumedLeavesRatio:
                
                icModel = new CondProbRefinedSubsumedLeavesRatio();
                
                break;
                
            case AICAouichaTaiebHamadu2016:
                
                String  strError = "This IC model is not reproducible and usable\n"
                        + "until the details described in the AICAouichaTaiebHamaduICmodel2016\n"
                        + " source file remarks be clarified by Ben Aouicha et al.\n"
                        + " We refer the reader to our remarks and the authors of \n"
                        + "the IC model in order to complete the replication of this method.";
                
                throw (new Exception(strError));
                
                //icModel = new AICAouichaTaiebHamaduICmodel2016();
        }
        
        // We return the result
        
        return (icModel);
    }
}
