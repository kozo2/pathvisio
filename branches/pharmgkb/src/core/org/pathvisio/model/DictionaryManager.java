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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Rebecca Tang
 * Associates dictionary files to properties
 */
public class DictionaryManager {
	private static SortedMap<Property, File> DICTIONARY_MAP = new TreeMap<Property, File>();
	private static Map<Property, List<DictionaryEntry>> DICTIONARY_VALUES = new HashMap<Property, List<DictionaryEntry>>();
	private static Map<Property, Map<String, String>> DICTIONARY_VALUES_MAP = new HashMap<Property, Map<String, String>>();


	public static void setDictionaryFile(Property prop, File file){
		DICTIONARY_MAP.put(prop, file);
		try{
			processDictionaryXML(prop, file);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static Map<Property, File> getDictionaryFiles(){
		return DICTIONARY_MAP;
	}


	public static File getDictionaryFile(Property prop){
		return DICTIONARY_MAP.get(prop);
	}

	public static List<DictionaryEntry> getValues(Property prop) {
		return DICTIONARY_VALUES.get(prop);
	}

	public static Map<String, String> getValuesMap(Property prop){
		return DICTIONARY_VALUES_MAP.get(prop);
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
		// clear keys
		DICTIONARY_VALUES.remove(prop);
		DICTIONARY_VALUES_MAP.remove(prop);
		//ID_MAP.remove(prop);

		List<DictionaryEntry> values = new ArrayList<DictionaryEntry>();
		DICTIONARY_VALUES.put(prop, values);
		Element rootElement = (Element)roots.item(0);
		NodeList entryNL = rootElement.getElementsByTagName("entry");
		for (int j=0; j<entryNL.getLength(); j++){
			Element entryElem = (Element)entryNL.item(j);
			String entryId = entryElem.getAttribute("id");
			String entryName = entryElem.getAttribute("name");
			if (entryName == null || entryName.isEmpty()){
				entryName = entryId;
			}
			Map<String, String> values_map = DICTIONARY_VALUES_MAP.get(prop);
			if (values_map == null){
				values_map = new HashMap<String,String>();
				DICTIONARY_VALUES_MAP.put(prop, values_map);
			}
			if (values_map.containsKey(entryId)){
				throw new Exception(" id already exists " + entryId + "in file " + file.getName());
			}
			values_map.put(entryId, entryName);
			values.add(new DictionaryEntry(entryId, entryName));
		}
	}
}
