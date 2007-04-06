// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2007 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package util;

/*
 * Converter.java
 * Command Line GenMAPP to GPML Converter
 * Created on 15 augustus 2005, 20:28
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import data.ConverterException;
import data.GmmlData;
import debug.Logger;

/**
 * @author Thomas Kelder (t.a.j.kelder@student.tue.nl)
 */
public class Converter {  
    public enum FileType {
    	GPML (new String[] { "gpml", "xml" }),
    	MAPP (new String[] { "mapp" }),
    	SVG (new String[] { "svg" });
    	
    	private String[] ext;
       	private static List<FileType> types;
       	
    	FileType(String[] _extensions) {
    		ext = _extensions;
    		if(FileType.types == null) FileType.types = new ArrayList<FileType>();
    		FileType.types.add(this);
    	}
    	public String[] getExtensions() { return ext; }
    	
    	public static FileType fromExtension(String extension) {
    		for(FileType type : FileType.types) {
    			for(String ext : type.getExtensions()) {
    				if(ext.equalsIgnoreCase(extension)) return type;
    			}
    		}
    		return null;
    	}
    	public String toString() {
    		return getExtensions()[0];
    	}
    }
    
	public static void printUsage()
	{
		System.out.println ("GPML Converter\n" +
				"Usage:\n" +
				"\tjava Converter <input filename><.extension> [<output filename>]<.extension>\n" +
				"\n" +
				"Converts between PathVisio GPML format and GenMAPP MAPP or SVG.\n" +
				"The conversion direction is determined from the extensions of the in/output file.\n" +
				"Valid extensions are:\n" +
				"\t.mapp for GenMAPP mapp format,\n" +
				"\t.xml or .gpml for PathVisio GPML format\n." +
				"\t.svg for SVG format\n" +
				"\n" +
				"Return codes:\n" +
				"\t 0: OK\n" +
				"\t-1: Parameter or file error\n" +
				"\t-2: Conversion error\n" +
				"\t-3: Validation error\n");
	}
	
	
	/**
     * Command line arguments:
     *
     */ 
    public static void main(String[] args) 
    {
    	String outputString = "";
        String inputString = "";
        File inputFile = null;
        File outputFile = null;
        FileType from = null;
        FileType to = null;
        
        // Handle command line arguments
        // Check for custom output path
        Logger log = new Logger();
		log.setStream (System.out);		
						//debug, trace, info, warn, error, fatal
		log.setLogLevel (false, false, true, true, true, true);
		
		boolean error = false;
		if (args.length == 0)
		{
			log.error ("Need at least one command line argument");
			error = true;			
		}
		else if (args.length > 2)
		{
			log.error ("Too many arguments");
			error = true;
		}
		else
		{
			inputFile = new File(args[0]);
			inputString = args[0];
			outputString = args[1];
		}		
		
		if (!error) {
			//Find input filetype
			int pos = inputString.lastIndexOf('.');
			if (pos >= 0) {
				from = FileType.fromExtension(inputString.substring(pos + 1));
				inputFile = new File(inputString);
			}
			//Find output filetype
			pos = outputString.lastIndexOf('.');
			if (pos >= 0) {
				to = FileType.fromExtension(outputString.substring(pos + 1));
			}
			if(from == null) {
				log.error("Wrong extension for input file");
				error = true;
			}
			if(to == null) {
				log.error("Wrong extension for output file");
				error = true;
			}
		}

		if (!error)
		{
			if(outputString.lastIndexOf('.') == 0) { //No output filename specified, take input name
				int pos = inputString.lastIndexOf('.');
				outputString = inputString.substring(0, pos) + outputString;
			}
			outputFile = new File (outputString);
		}
		

		if (!error)
		{
			if (inputFile.exists() && inputFile.canRead())
				;			
			else
			{
				log.error("Can't read from file " + args[0]);
				error = true;
			}			
		}
		
		if (!error)
		{
			log.info("Source: " + inputString);
			log.info("Dest:   " + outputString);
			log.info("Going from " + from + " to " + to);

			boolean valid = true;
			
			try
			{
				if (from == FileType.MAPP && to == FileType.GPML)
				{
					GmmlData gmmlData = new GmmlData();
					gmmlData.readFromMapp(inputFile);
					gmmlData.writeToXml(outputFile, true);					
				}
				else if (from == FileType.GPML && to == FileType.MAPP)
				{
					GmmlData gmmlData = new GmmlData();
					gmmlData.readFromXml(inputFile, true);					
					gmmlData.writeToMapp(outputFile);
				}
				else if (from == FileType.GPML && to == FileType.SVG) {
					GmmlData gmmlData = new GmmlData();
					gmmlData.readFromXml(inputFile, true);
					gmmlData.writeToSvg(outputFile);
				}
				else {
					log.error("Conversion from " + from + " to " + to + " not supported");
					System.exit(-1);
				}
			}
			catch (ConverterException e)
			{
				log.error(e.getMessage(), e);
				System.exit(-2);			
			}
			System.exit(valid ? 0 : -3);
		}
		else
		{
			printUsage();
			System.exit(-1);
		}        
                
    }
}
