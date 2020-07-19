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

package hesml_goclient;

import hesml.HESMLversion;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

/**
 **
 * @author Juan J. Lastra-Díaz (jlastra@invi.uned.es)
 */

public class HESML_GOclient {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws IOException, ParserConfigurationException, Exception
    {
    
        // We initialize the flag to show the usage message
        
        boolean showUsage = false;
        
        // We print the HESML version
        
        System.out.println("Running HESML_GOclient V1R5 (1.5.0.1, July 2020) based on "
                + HESMLversion.getReleaseName() + " " + HESMLversion.getVersionCode());
        
        System.out.println("Java heap size in Mb = "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        
        // We read the incoming parameters and load the reproducible
        // experiments defined by the user in a XML-based file with
        // extension *.umlsexp
        
        if ((args.length == 1) && (args[0].endsWith(".oboexp")))
        {
            // We load a reproducible experiment file in XML file format

            File inputFile = new File(args[0]);  // Get the file path

            // We check the existence of the file

            if (inputFile.exists())
            {
                // We get the start time

                long startFileProcessingTime = System.currentTimeMillis();

                // Loading the experiment file

                System.out.println("Loading and running the GO-based experiments defined in "
                        + inputFile.getName());

                // We parse the input file in order to recover the
                // experiments definition.

                GOReproducibleExperimentsInfo reproInfo =
                        new GOReproducibleExperimentsInfo(inputFile.getPath());

                // We execute all the experiments defined in the input file

                reproInfo.RunAllExperiments();
                reproInfo.clear();

                // We measure the elapsed time to run the experiments

                long    endTime = System.currentTimeMillis();
                long    minutes = (endTime - startFileProcessingTime) / 60000;
                long    seconds = (endTime - startFileProcessingTime) / 1000;

                System.out.println("Overall elapsed loading and computation time (minutes) = " + minutes);
                System.out.println("Overall elapsed loading and computation time (seconds) = " + seconds);
            }
        }
        else
        {
            showUsage = true;
        }
        
        // For a wrong calling to the program, we show the usage.
        
        if (showUsage)
        {
            System.err.println("\nIn order to properly use the HESML_GOclient program");
            System.err.println("you should call it using the method below:\n");
            System.err.println("(1) C:> java -jar dist\\HESML_GOclient.jar <reproexperiment.exp>");
        }
    }
}
