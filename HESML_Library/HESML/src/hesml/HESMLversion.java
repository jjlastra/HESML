/*
 * Copyright (C) 2016-2022 Universidad Nacional de Educación a Distancia (UNED)
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

package hesml;

/**
 * This class is included only to report the HESML version info.
 * @author j.lastra
 */

public class HESMLversion
{
    /**
     * This function reports the version of the current HESML software library.
     * @author j.lastra
     * @param args Input arguments
     */
    
    public static void main(String[] args)
    {
        // We print the version information
    
        System.out.println("\n" + getReleaseName() + " Java software library of ontology-based");
        System.out.println("semantic similarity measures and information content models");
        System.out.println("\nRelease name: " + getReleaseName());    
        System.out.println("Version code: " + getVersionCode());
        System.out.println("Version date: July 2020");
        System.out.println("Copyright (C) 2020 Universidad Nacional de Educación a Distancia (UNED)");
        
        System.out.println("\nMain HESML publications:\n");
        System.out.println("[1] Lastra-Díaz, J. J. & García-Serrano, A. & Batet, M. & Fernández, M. & Chirigati, F. (2017).");
        System.out.println("HESML: a scalable ontology-based semantic similarity measures library");
        System.out.println("with a set of reproducible experiments and a replication dataset.");
        System.out.println("Information Systems 66, pp. 97-118.");
        System.out.println("http://dx.doi.org/10.1016/j.is.2017.02.002\n");
        
        System.out.println("[2] J.J. Lastra-Díaz, A. Lara-Clares, A. García-Serrano,");
        System.out.println("HESML: a real-time semantic measures library for the biomedical");
        System.out.println("domain with a reproducible survey, Submitted for Publication. (2020)\n");
    }
    
    /**
     * This function returns the name of the HESML version.
     * @return The name of the version.
     */
    
    public static String getReleaseName()
    {
        return ("HESML V1R5");
    }
    
    /**
     * This function returns the version code of the current HESML version.
     * @return The version code of the current library.
     */
    
    public static String getVersionCode()
    {
        return ("(1.5.0.1)");
    }
}
