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
package org.pathvisio.cytoscape;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pathvisio.debug.Logger;
import org.pathvisio.model.DataSource;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PropertyType;

import cytoscape.data.CyAttributes;

public class DefaultAttributeMapper implements AttributeMapper {
	public static final String CY_COMMENT_SOURCE = "cytoscape-attribute: ";
	private Map<PropertyType, String> defaultValues;
	private Map<PropertyType, String> prop2attr;
	private Map<String, PropertyType> attr2prop;
	
	private Set<PropertyType> protectedProps;
	
	public DefaultAttributeMapper() {
		prop2attr = new HashMap<PropertyType, String>();
		attr2prop = new HashMap<String, PropertyType>();
		defaultValues = new HashMap<PropertyType, String>();
		
		setInitialMappings();
	}
	
	/**
	 * Sets a two way mapping
	 */
	public void setMapping(String attr, PropertyType prop) {
		setAttributeToPropertyMapping(attr, prop);
		setPropertyToAttributeMapping(prop, attr);
	}
	
	public void setDefaultValue(PropertyType prop, String value) {
		defaultValues.put(prop, value);
	}
	
	/**
	 * Set a mapping from attribute to property
	 * @param attr
	 * @param prop
	 */
	public void setAttributeToPropertyMapping(String attr, PropertyType prop) {
		attr2prop.put(attr, prop);		
	}
	
	/**
	 * Set a mapping from property to attribute
	 * @param prop
	 * @param attr
	 */
	public void setPropertyToAttributeMapping(PropertyType prop, String attr) {
		prop2attr.put(prop, attr);		
	}
	
	protected Set<PropertyType> getProtectedProps() {
		if(protectedProps == null) {
			protectedProps = new HashSet<PropertyType>();
			protectedProps.add(PropertyType.CENTERX);
			protectedProps.add(PropertyType.CENTERY);
			protectedProps.add(PropertyType.STARTX);
			protectedProps.add(PropertyType.STARTY);
		}
		return protectedProps;
	}
	
	protected void setInitialMappings() {
		setMapping("canonicalName", PropertyType.TEXTLABEL);
		setDefaultValue(PropertyType.DATASOURCE, "UniProt");
	}
	
	protected boolean isProtected(PropertyType prop) {
		return getProtectedProps().contains(prop);
	}
	
	public void attributesToProperties(String id, PathwayElement elm, CyAttributes attr) {
		//Process defaults
		for(PropertyType prop : defaultValues.keySet()) {
			elm.setProperty(prop, defaultValues.get(prop));
		}
		
		//Process mappings
		for(String aname : attr.getAttributeNames()) {
			PropertyType prop = getProperty(aname);
			
			//No mapping for this attribute, store as comment
			if(prop == null) {
				String value = null;
				try { //We don't know what type of attribute
					  //Throws an IllegalArgumentException if it's a Map
					  //TODO: Find out if there is a generic method that returns an object
					value = attr.getStringAttribute(id, aname);					
				} catch(Exception e) {
					Logger.log.error("Unable to transfer attribute " + aname, e);
				}
				if(value != null && !(value.length() == 0)) {
					elm.addComment(elm.new Comment(attr.getStringAttribute(id, aname), CY_COMMENT_SOURCE + aname));					
				}
				continue;
			}
			
			//Protected property, don't set from attributes
			if(isProtected(prop)) {
				continue;
			}
			
			//Found a property, try to set it
			Object value = null;
			switch(prop.type()) {
			case BOOLEAN:
				value = attr.getBooleanAttribute(id, aname);
				break;
			case INTEGER:
				value = attr.getIntegerAttribute(id, aname);
				break;
			case DOUBLE:
				value = attr.getDoubleAttribute(id, aname);
				break;
			case STRING:
			case DB_ID:
			case DB_SYMBOL:
			case DATASOURCE:
				value = attr.getStringAttribute(id, aname);
				break;
			default:
				Logger.log.trace("Unsupported type: attribute " + aname + " to property " + prop);
				//Don't transfer the attribute, if it's not a supported type
			}
			if(value != null) {
				elm.setProperty(prop, value);
			}
		}
	}

	private PropertyType getProperty(String attributeName) {
		//First check if a mapping is explicitely set
		PropertyType prop = attr2prop.get(attributeName);
		if(prop == null) { //If not, find out if it's a GPML attribute
			prop = PropertyType.getByTag(attributeName);
		}
		return prop;
	}
	
	private String getAttributeName(PropertyType property) {
		//First check if a mapping is explicitely set
		String name = prop2attr.get(property);
		if(name == null) { //If not, use the property tag name
			name = property.tag();
		}
		return name;
	}
	
	public void propertiesToAttributes(String id, PathwayElement elm,
			CyAttributes attr) {
		for(PropertyType prop : elm.getAttributes(true)) {
			Object value = elm.getProperty(prop);
			if(value != null) {
				String aname = getAttributeName(prop);
				switch(prop.type()) {
				case BOOLEAN:
					attr.setAttribute(id, aname, (Boolean)value);
					break;
				case INTEGER:
					attr.setAttribute(id, aname, (Integer)value);
					break;
				case DOUBLE:
					attr.setAttribute(id, aname, (Double)value);
					break;
				case STRING:
				case DB_ID:
				case DB_SYMBOL:
				default:
					attr.setAttribute(id, aname, value.toString());
				}
			}
		}
	}
}
