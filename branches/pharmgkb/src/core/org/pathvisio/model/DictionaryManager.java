// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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
package org.pathvisio.model;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.SortedMap;

import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

/**
 * @author Rebecca Fieldman
 */
public class DictionaryManager {
	private static Map<Property, File> DICTIONARY_MAP;
	private static Map<Property, SortedMap<String, String>> DICTIONARY_VALUES;


	public static void setDictionaryFile(Property prop, File file) throws Exception{
		DICTIONARY_MAP.put(prop, file);
		processDictionaryXML(prop, file);
	}

	public static SortedMap<String, String> getValues(Property prop) {
		return DICTIONARY_VALUES.get(prop);
	}


	/**
	 * Given an XML file, parse out the properties.
	 */
	public static void processDictionaryXML(Property prop, File file) throws Exception {

		InputStream xmlStream = new FileInputStream(file);
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.parse(xmlStream);

		NodeList roots = doc.getElementsByTagName("dictionary");
		if (roots.getLength() == 0){
			throw new Exception("No dictionary root found");
		}
		if (roots.getLength() > 1){
			throw new Exception("More than one dictionary roots found");
		}
		if (DICTIONARY_VALUES.containsKey(prop)){
					DICTIONARY_VALUES.remove(prop);
		}
		SortedMap<String, String> values = new TreeMap<String, String>();
		DICTIONARY_VALUES.put(prop, values);
		Element rootElement = (Element)roots.item(0);
		NodeList entryNL = rootElement.getElementsByTagName("entry");
		for (int j=0; j<entryNL.getLength(); j++){
			Element entryElem = (Element)entryNL.item(j);
			String entryId = entryElem.getAttribute("id");
			String entryName = entryElem.getAttribute("name");
			if (entryName == null){
				entryName = entryId;
			}
			values.put(entryId, entryName);
		}
	}
}
