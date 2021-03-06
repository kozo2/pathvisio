//PathVisio,
//a tool for data visualization and analysis using Biological Pathways
//Copyright 2006-2007 BiGCaT Bioinformatics

//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at

//http://www.apache.org/licenses/LICENSE-2.0

//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
package org.pathvisio.kegg;

import dtd.kegg.Pathway;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.bridgedb.IDMapperException;
import org.bridgedb.bio.Organism;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.GpmlFormat;
import org.pathvisio.core.model.ImageExporter;
import org.pathvisio.core.model.RasterImageExporter;
import org.pathvisio.core.preferences.PreferenceManager;
import org.xml.sax.SAXException;

/**
 * Command-line converter for KGML (KEGG pathway format) 
 */
public class Converter {
	private static final Pattern KGML_PATTERN =
		Pattern.compile("[a-z]{3}([0-9]{5}).(xml|kgml)$", Pattern.CASE_INSENSITIVE);

	@Option(name = "-useMap", required = false, usage = "Use the reference 'map' pathways to improve conversion" +
			" of the species specific pathways.")
	private boolean useMap;

	@Option(name = "-kgml", required = true, usage = "The path that contains the KGML files")
	private File keggPath;

	@Option(name = "-out", required = false, usage = "The output path to convert the files to")
	private File outPath;

	@Option(name = "-species", required = true, usage = "The species of the pathways (e.g. 'Homo sapiens')")
	private String species;

	@Option(name = "-overwrite", required = false, usage = "Overwrite existing files")
	private boolean overwrite;

	@Option(name = "-offline", required = false, usage = "Don't use the web service")
	private boolean offline;

	@Option(name = "-spacing", required = false, usage = "Multiplier for the coordinates to get more spacing between the elements (default = 2).")
	private double spacing = 2;
	
	@Option(name = "-symbolIndex", required = false, usage = "Which symbol to pick from bget results (default = 0 -> first symbol).")
	private int symbolIndex = 0;
	
	@Option(name = "-symbolByName", usage = "Query for symbols using element name instead of gene.")
	private boolean symbolByName = false;
	
	@Option(name = "-map", required = true, usage = "Prefix for map files, either ec or ko.")
	private String map;

	public static void main(String[] args) {
		PreferenceManager.init();
		Logger.log.setLogLevel(true, true, true, true, true, true);

		Converter converter = new Converter();
		CmdLineParser parser = new CmdLineParser(converter);
		try {
			parser.parseArgument(args);
			if(converter.outPath == null) converter.outPath = converter.keggPath;

		} catch(CmdLineException e) {
			e.printStackTrace();
			parser.printUsage(System.err);
			System.exit(-1);
		}

		try {
			converter.recursiveConversion();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(-2);
		}
	}

	public Converter() {
	}

	private String getOrganism() {
		//Try to parse Organism class to support short names for common species
		Organism organism = Organism.fromLatinName(species);
		if(organism == null) {
			//try by short name
			organism = Organism.fromShortName(species);
		}
		if(organism == null) {
			//finally, try by code
			organism = Organism.fromCode(species);
		}
		if(organism != null) species = organism.latinName();
		return species;
	}

	private void recursiveConversion() throws FileNotFoundException, RemoteException, JAXBException, ConverterException, ServiceException,
	ParserConfigurationException, SAXException, ClassNotFoundException, IDMapperException {
		recursiveConversion(keggPath);
	}

	private void recursiveConversion(File dir) throws FileNotFoundException, JAXBException, RemoteException, ConverterException, ServiceException,
	ParserConfigurationException, SAXException, ClassNotFoundException, IDMapperException {
		if(dir.isDirectory()) {
			for(File f : dir.listFiles()) {
				recursiveConversion(f);
			}
		} else {
			if(dir.getName().endsWith(".xml") ||
					dir.getName().endsWith(".kgml")) {
				convert(dir);
			}
		}
	}

	private void convert(File file) throws JAXBException, FileNotFoundException, RemoteException, ConverterException, ServiceException,
	ParserConfigurationException, ClassNotFoundException, SAXException, IDMapperException {
		//Check for overwrite
		Logger.log.trace("Processing " + file);

		//Define a string (suffix) to add to gpml filenames if user decided to use map files
		String fileAdd = useMap ? "_map" : "";
		File gpmlFile = new File(outPath, file.getName() + fileAdd + ".gpml");

		//Check for overwrite
		if(!overwrite && gpmlFile.exists()) {
			Logger.log.trace("Skipping " + file + ", " +
					gpmlFile + "already exists (use -overwrite to overwrite)."
			);
			return;
		}
		Logger.log.trace("Converting to " + gpmlFile.getAbsolutePath());

		Pathway pathway = (Pathway)Util.unmarshal(Pathway.class,
				new BufferedInputStream(new FileInputStream(file)));

		org.pathvisio.core.model.Pathway gpmlPathway = null;

		if(useMap && (!file.getName().startsWith(map))) {
			//Try to find the corresponding map file
			Matcher m = KGML_PATTERN.matcher(file.getName());
			if(m.matches()) {
				
				File mapFile = new File(
						file.getParentFile(), map + m.group(1) + "." + m.group(2)
				);
				if(!mapFile.exists()) {
					
						throw new FileNotFoundException("Unable to find reference map file for " + file.getName() +
								"\nExpected " + mapFile.getName());
					
					
				}
				Logger.log.trace("Using map " + mapFile);

				Pathway map = (Pathway)Util.unmarshal(Pathway.class,
						new BufferedInputStream(new FileInputStream(mapFile)));
				gpmlPathway = convert(map, pathway);
			} else {
				Logger.log.info("Couldn't parse map file for " + file.getAbsolutePath());
			}
		} else if(!useMap) {
			gpmlPathway = convert(pathway);
		} else {
			Logger.log.trace("Skipping " + file);
			return;
		}

		try {
			GpmlFormat.writeToXml(gpmlPathway, gpmlFile, true);
			ImageExporter imageExporter = new RasterImageExporter(ImageExporter.TYPE_PNG);
			imageExporter.doExport(new File(outPath, gpmlFile.getName() + ".png"), gpmlPathway);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void setKeggFormatOptions(KeggFormat kf) throws ServiceException {
		kf.setPrefSymbolIndex(symbolIndex);
		kf.setSymbolByGene(!symbolByName);
		kf.setUseWebservice(!offline);
		kf.setSpacing(spacing);
	}
	
	private org.pathvisio.core.model.Pathway convert(Pathway pathway) throws RemoteException, ConverterException, ServiceException, ClassNotFoundException, IDMapperException {
		KeggFormat kf = new KeggFormat(pathway, getOrganism());
		setKeggFormatOptions(kf);
		return kf.convert();
	}

	private org.pathvisio.core.model.Pathway convert(Pathway map, Pathway ko) throws RemoteException, ConverterException, ServiceException, ClassNotFoundException, IDMapperException {
		KeggFormat kf = new KeggFormat(map, ko, getOrganism());
		setKeggFormatOptions(kf);
		return kf.convert();
	}
}
