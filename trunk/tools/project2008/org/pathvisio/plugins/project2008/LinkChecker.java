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
// import the things needed to run this java file.
package org.pathvisio.plugins.project2008;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.apache.xmlrpc.XmlRpcException;
import org.pathvisio.data.SimpleGdb;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.Xref;
import org.pathvisio.util.PathwayParser;
import org.pathvisio.util.PathwayParser.ParseException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * In the link checker for each pathway is is checked if the links in that pathway exist in the 
 * database. An output file is created that shows a table. On the right column of that table the 
 * filenames are shown and on the left column of the table the percentage is shown of the links
 * that exist in the database.
 */

public class LinkChecker 
{
	
	public static void printUsage(String error)
	{
		System.out.println ("LinkChecker\n" +
				"	Checks references in pathways and prints a html table with the result\n"+
				"\n"+
				"Usage:\n"+
				"	java LinkChecker online <dbDir> <cacheDir> <outFile>\n" +
				"	java LinkChecker local <dbDir> <pwDir> <outFile>\n" +
				"\n" + 
				"Where dbDir is a directory containing gene databases,\n"+
				"cachedir is a directory containing wikipahtways pathways,\n"+
				"and pwDir is the root of a directory tree containing pathways in GPML format.\n"+
				"\n" +
				"Error: " + error);
	}

	/**
	 * If connected to the internet, the boolean online has to be set to true. 
	 */
	boolean isOnline = true; // set to true if connected to the internet
	
	/** 
	 * Check if the String[] args is given, and make Files containing the directories to
	 * the pathways and databases 
	 */ 
	File dbDir = null;
	File pwDir = null;
	PrintWriter out;

	/**
	 * sets online, out, dbDir and pwDir.
	 * Exits the program if there was an error.
	 */
	private void parseArgs (String[] args)
	{
		String errorMessage = null;

		int i = 0;
		if (args.length != 4)
		{
			errorMessage = "Expected 4 arguments";
		}
		if (errorMessage == null)
		{
			if (args[i].equals("local"))
			{
				isOnline = false;
			}
			else if (args[i].equals("online"))
			{
				isOnline = true;
			}
			else
			{
				errorMessage = "Expected 'local' or 'online'";
			}
			i++;
		}
		if (errorMessage == null)
		{
			dbDir = new File(args[i]);
			if (!(dbDir.exists() && dbDir.isDirectory()))
			{
				errorMessage = "Could not find database directory " + dbDir;
			}
			i++;
		}
		if (errorMessage == null)
		{
			pwDir = new File(args[i]);
			if (!(pwDir.exists() && pwDir.isDirectory()))
			{
				errorMessage = "Could not find pathway directory " + pwDir;
			}
			i++;
		}
		if (errorMessage == null)
		{
			// DO this for standard output:
			// out = new PrintWriter (System.out);
			try
			{
				out = new PrintWriter (new FileWriter (args[i]));
			}
			catch (IOException e)
			{
				errorMessage = "Couldn't open file for output: " + args[i];
			}
		}
		if (errorMessage != null)
		{
			printUsage(errorMessage);
			System.exit(1);
		}
	}
	
	private void run()
	{
		/**
		 * Get a list of files of databases and pathways. Here the method 'getFileListing' is 
		 * executed.
		 */
		String pwExtension = ".gpml";
		List<File> pwFilenames = FileUtils.getFileListing(pwDir, pwExtension);
		
		if (isOnline)
		{
			/**
			 * If the boolean online is true, first the data is loaded from the last changed 
			 * pathway. With the date of this last change, the data of the other recently 
			 * changed pathways can also be loaded.
			 */		
			long localdate = dateLastModified(pwFilenames);
			Date d = new Date(localdate); 
			DateFormat df = DateFormat.getDateTimeInstance();
			System.out.println("Date last modified: "+df.format(d)); 
			try
			{
				System.out.println("---[Get Recently Changed Files]---");
				WikiPathwaysCache wp = new WikiPathwaysCache(pwDir);
				wp.downloadNew(d);
			
				System.out.println("---[Get All Other Files]---");
				// download all pathways to the pathway folder
				wp.downloadAll();
				System.out.println("---[Ready]---");
				System.out.println("---[Start Checking Links]---");
			}
			catch (XmlRpcException e)
			{
				System.out.println("ERROR: Couldn't update cache");
			}
			catch (IOException e)
			{
				System.out.println("ERROR: Couldn't update cache");
			}
		}
		
		// initialize local Gdb manager
		LocalGdbManager localGdbManager = new LocalGdbManager(dbDir);

		/**
		 * With the try/catch the output file is created.
		 * Then in the for-loop all pathway files are loaded. And the percentage of found Xrefs
		 * in de database is given. 
		 */
		String titleOfHTMLPage = "LinkChecker.java results";
		out.print("<HTML><HEAD><TITLE>"+titleOfHTMLPage+"</TITLE></HEAD><BODY><center><h1>"+titleOfHTMLPage+"</h1><TABLE border=\"1\"><TR><TD><B>Filename</B></TD><TD><B>Percentage found in Gdb</B></TD></B></TR>");

		XMLReader xmlReader = null;
		
		try
		{
			xmlReader = XMLReaderFactory.createXMLReader();
		}
		catch (SAXException e)
		{
			Logger.log.error ("Couldn't create XML reader");
			return; // abort
		}
		
		for (File filename:pwFilenames)
		{
//			Pathway pway = new Pathway();
//				
//			 /** 
//			  * The pathway file can be validated. For this end, the boolean validate has to be set 
//			  * to true.
//			  */ 
//			boolean validate = false; //Set to true if you want to validate the pathway file.
//			try
//			{
//				pway.readFromXml(filename, validate);
//			}
//			catch(ConverterException e)
//			{
//				System.out.println("empty file is found");
//			}
			
			SimpleGdb currentGdb = localGdbManager.getDatabaseForPathway(filename);

			/**
			 * First a list is made that contains the Xref's.
			 * In the if statement:
			 * If the database is found, add a row to the table of the html file, containing the
			 * name of the pathway and the percentage of found Xref's in the databse.
			 * In the else statement:
			 * Is the database is not found, add a row to the table of the html file, containing
			 * the name of the pathway and the text "Database not found".
			 */
//			List<Xref> xrefList = makeXrefList(pway);
			List<Xref> xrefList = new ArrayList<Xref>();
			try
			{
				PathwayParser pwy = new PathwayParser (filename, xmlReader);
				xrefList.addAll (pwy.getGenes());
			}
			catch (ParseException e)
			{
				// ignore parse errors
				Logger.log.error ("Couldn't parse " + filename);
			}
			
			if (currentGdb != null)
			{
				out.print("<TR><TD>"+filename.getName()+"</TD>");
				String percentage = calculatePercentage(xrefList, currentGdb);
				out.println("<TD>"+percentage+")</TD></TR>");
			}
			else
			{
				out.print("<TR><TD>"+filename.getName()+"</TD>");
				out.println("<TD> Database not found </TD></TR>");				
			}
		
		}
		
		/**
		 * All pathway rows are added to the table. Now the HTML file has to be closed properly.
		 */
		out.print("</TABLE></center></BODY></HTML>");
		out.close();
	}
	
	/**
	* in the String[] args, 3 arguments are given:
	* in example:
	* "C:\\databases\\"
	* "C:\pathways"
	* 
	* The first one is the directory that contains the databases.
	* The second one is the directory that contains the pathway cache.
	*/
	public static void main(String[] args)
	{
		LinkChecker linkChecker = new LinkChecker();
		
		linkChecker.parseArgs (args);
		linkChecker.run();
	}
	
	/** 
	 * In this method, the percentage of Xref's found in the database is calculated.
	 * The properties you have to enter are:
	 * 'xrefList' (a list of all the xrefs from a pathway) and 
	 * 'database' (a SimpleGdb database that has to be checked if it contains the Xrefs).
	 */
	public static String calculatePercentage(List<Xref> xrefList, SimpleGdb database){
		
		int countTrue = 0;       // counter for the true outcome (a xref is found)
        int countTotal = 0;      // counter for the total of xrefs
		String percentage;       // string for the outcome
		int percentageint; // int for the actual percentage
		// Check each Xref from the xrefList if it is found in the database.
		for (Xref xref:xrefList){
			if (database.xrefExists(xref) == true){
				countTrue++;
			}
			countTotal++;
		}		
		// Calculate the percentage of found references.
		if (countTotal != 0){
			percentageint = Math.round(100*countTrue/countTotal);
			percentage = (percentageint+"% (of total: "+countTotal+" in ");
		}
		else{
			percentage = ("<font color=\"red\"><b>total: 0</b></font> (divide by zero) in ");
		}
		// Return the percentage.
		return percentage;
	}
	
	/**
	 * In this method the date is returned when the last change is made in a pathway. The
	 * property that has to be given is:
	 * 'pathways' (a list of pathways you want to have the most recent date from). 
	 */
	public static long dateLastModified(List<File> pathways)
	{
		// Set initial value.
		long lastModified = 0;
		// Walk through all the pathways.
		for (File pathway:pathways)
		{
			// If pathway is more recent, use this date.
			if (lastModified < pathway.lastModified()){
				lastModified = pathway.lastModified();
			}
		}
		return lastModified;
	}
	
}
