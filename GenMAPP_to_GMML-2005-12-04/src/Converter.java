/*
 * Converter.java
 * Command Line GenMAPP to GMML Converter
 * Created on 15 augustus 2005, 20:28
 */

import converter.MappToGmml;
import fileInOut.*;
import gmml.ObjectFactory;
import gmml.Pathway;
import javax.xml.bind.JAXBException;
import java.io.*;

/**
 * @author Thomas Kelder (t.a.j.kelder@student.tue.nl)
 */
public class Converter {  
    /**
     * Command line arguments:
     *
     */
    public static void main(String[] args) {
        String outputString = "";
        String inputString = "";
        File inputFile = null;
        File outputDir = null;
        String usage = "-> Usage: java -jar GenMAPPtoGMML.jar ['output directory'] 'input file'\narguments between [] are optional \n";
        String example = "-> Example: java -jar GenMAPPtoGMML.jar output\\ input\\Hs_Fatty_Acid_Degradation.mapp \n";
        // Handle command line arguments
        // Check for custom output path
        if(args.length == 1) {
            if(args[0].equals("-h") || args[0].equals( "-?") || args[0].equals("/?")) { 
                System.out.println(">> GenMAPP to GMML Converter");
                System.out.println(usage+example);
                System.exit(0);
            } else {
                inputFile = new File(args[0]);
                inputString = inputFile.getAbsolutePath();
                outputString = inputString;
                System.out.println(">>> Outstr:" + outputString);
            }
                     
        } else if(args.length == 2) {
            outputDir = new File(args[0]);
            inputFile = new File(args[1]);       
            inputString = inputFile.getAbsolutePath();
            outputString = outputDir.getAbsolutePath() + "\\" + inputFile.getName();
        // If no custom output path is given, use input path
        } else {
            System.out.println("-> Invalid arguments");
            System.out.println(usage+example);
            System.exit(0);
        }
        
        // Change the extension of the output file from .mapp to .xml
        if(outputString.endsWith(".mapp")) {
            outputString = outputString.replaceAll(".mapp",".xml");
        } else {
            outputString = outputString + ".xml";
        }
        
        // Load the GenMAPP mapp data into two String arrays
        String[][] mappObjects = MappFile.importMAPPObjects(inputString);
        String[][] mappInfo = MappFile.importMAPPInfo(inputString);
        
        // Create a new gmml pathway "p"
        Pathway p = null;
        try {
            ObjectFactory o = new ObjectFactory();
            p = o.createPathway();
        } catch (JAXBException ex) {
        }
        // Copy the info table to the new gmml pathway
        MappToGmml.copyMappInfo(mappInfo, p);
        
        // Copy the objects table to the new gmml pahtway
        MappToGmml.copyMappObjects(mappObjects,p);
        
        // Write pathway p to file
        GmmlFile.writeToXML(p, outputString);
        
        System.out.println(">> CONVERSION COMPLETED");
    }
    
}
