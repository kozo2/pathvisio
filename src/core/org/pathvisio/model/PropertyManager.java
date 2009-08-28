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
import java.io.InputStream;
import java.io.IOException;
import java.util.*;

/**
 * This class handles dynamic properties configured via the properties.xml file.
 *
 * @author Mark Woon
 * @author Rebecca Tang
 */
public class PropertyManager {
    private static final Map<String, Property> PROPERTY_MAP = new HashMap<String, Property>();
    private static final EnumMap<ObjectType, ObjectProperties> OBJECT_MAP =
            new EnumMap<ObjectType, ObjectProperties>(ObjectType.class);
    private static final Map<String, List<Property>> PROPERTY_LIST_MAP =
            new HashMap<String, List<Property>>();
    private static final Map<String, String> PROPERTY_LIST_NAMES = new HashMap<String, String>();
    private static final Map<String, List<PropertyType>> STATIC_PROPERTY_LIST_MAP =
            new HashMap<String, List<PropertyType>>();
    private static final Map<String, String> STATIC_PROPERTY_LIST_NAMES = new HashMap<String, String>();
    private static final Set<Property> MODES = new HashSet<Property>();


    /**
     * Initialize the PropertyManager with standard defaults.
     */
    public static void init() {

        InputStream in = null;
        try {
           in = PropertyManager.class.getResourceAsStream("properties.xml");
            processPropertiesXML(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }


    /**
     * Given an XML file, parse out the properties.
     */
    static void processPropertiesXML(InputStream xmlStream) throws Exception {

        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlStream);
        NodeList roots = doc.getElementsByTagName("pathvisio");
        for (int i=0; i<roots.getLength(); i++) {
            Element rootElement = (Element)roots.item(i);
            processModes(rootElement.getElementsByTagName("modes"));
            processProperties(rootElement.getElementsByTagName("properties"));
            processObjectProperties(rootElement.getElementsByTagName("objects"));
        }
    }



    /**
     * Parse modes.  Should be called first.
     */
    static void processModes(NodeList modesNL) throws Exception {
        for (int i=0; i<modesNL.getLength(); i++) {
            Element modesElem = (Element)modesNL.item(i);
            NodeList modes = modesElem.getElementsByTagName("mode");
            for (int j=0; j<modes.getLength(); j++) {
                Element modeElem = (Element)modes.item(j);
                registerProperty(new Property(modeElem.getAttribute("id"), modeElem.getAttribute("name"), PropertyClass.MODE));
            }
        }
    }

    /**
     * Parse properties.  Should be called after parsing modes.
     */
    static void processProperties(NodeList propsNL) throws Exception{
        for (int i=0; i<propsNL.getLength(); i++){
            Element propsElem = (Element)propsNL.item(i);
            NodeList defNL = propsElem.getElementsByTagName("property");
            for (int j=0; j<defNL.getLength(); j++) {
                Element propElem = (Element)defNL.item(j);
                PropertyClass typeProp = PropertyClass.valueOf(propElem.getAttribute("type"));
                Property prop = new Property(propElem.getAttribute("id"), propElem.getAttribute("name"), typeProp);
                PropertyManager.registerProperty(prop); //register

                String multiSelect = propElem.getAttribute("multiSelect");
                if (multiSelect != null && multiSelect.equalsIgnoreCase("true")){
                    prop.setMultiSelect(true);
                }
                //property modes
                NodeList modesNL = propElem.getElementsByTagName("propertyMode");
                for (int n=0; n<modesNL.getLength(); n++){
                    Element modeElem = (Element)modesNL.item(n);
                    String modeId = modeElem.getTextContent();
                    prop.addMode(PropertyManager.getProperty(modeId));
                }
                if (typeProp == PropertyClass.ENUM){
                    NodeList optionNL = propElem.getElementsByTagName("option");
                    for (int m=0; m<optionNL.getLength(); m++){
                        Element optionElem = (Element)optionNL.item(m);
                        String optionName = optionElem.getAttribute("name");
                        PropertyEnum propEnum = new PropertyEnum(optionName);
                        NodeList optionModeNL = optionElem.getElementsByTagName("optionMode"); //option modes
                        for (int p=0; p<optionModeNL.getLength(); p++){
                            Element optionModeElem = (Element)optionModeNL.item(p);
                            Property optionModeProp = PropertyManager.getProperty(optionModeElem.getTextContent());
                            propEnum.addMode(optionModeProp);
                        }
                        prop.addValue(propEnum);
                    }
                }
            }
        }
    }


    /**
     * Parse object properties.  Should be called after parsing properties.
     */
    static void processObjectProperties(NodeList objsNL) throws Exception {
        // objects

        for (int i=0; i<objsNL.getLength(); i++) {
            Element rootElement = (Element)objsNL.item(i);
            //static lists
            processStaticPropList(rootElement);
            //objects
            NodeList objNL = rootElement.getElementsByTagName("object");
            for (int j=0; j<objNL.getLength(); j++) {
                Element objElem = (Element)objNL.item(j);
                ObjectProperties objProps = new ObjectProperties(objElem.getAttribute("id"),
                        objElem.getAttribute("name"), ObjectType.getTagMapping(objElem.getAttribute("type")));
                PropertyManager.registerObject(objProps);
                // modes, add each mode to objProperties
                NodeList modeNL = objElem.getElementsByTagName("mode");
                for (int k=0; k<modeNL.getLength(); k++){
                    Element modeElem = (Element)modeNL.item(k);
                    if (modeElem != null){
                        String modeId = modeElem.getTextContent();
                        if (modeId != null){
                            Property modeProp = PropertyManager.getProperty(modeId);
                            if (modeProp != null){
                                objProps.addMode(modeProp);
                            }
                        }
                    }
                }
                // properties, add each property to objProperties
                NodeList propNL = objElem.getElementsByTagName("property");
                for (int m=0; m<propNL.getLength(); m++){
                    Element propElem = (Element)propNL.item(m);
                    String propertyId = propElem.getTextContent();
                    Property prop = PropertyManager.getProperty(propertyId);
                    objProps.addProperty(prop);
                }
                //subtypes
                NodeList subTypeNL = objElem.getElementsByTagName("subType");
                for (int n=0; n<subTypeNL.getLength(); n++){
                    Element subTypeElem = (Element)subTypeNL.item(n);
                    String propId = subTypeElem.getAttribute("prop");
                    String optionId = subTypeElem.getAttribute("value");
                    Property mainProp = PropertyManager.getProperty(propId);
                    // sub properties
                    NodeList subPropNL = subTypeElem.getElementsByTagName("subProperty");
                    for (int p=0; p<subPropNL.getLength(); p++){
                        Element subPropElem = (Element)subPropNL.item(p);
                        String subPropId = subPropElem.getTextContent();
                        Property subProp = PropertyManager.getProperty(subPropId);
                        objProps.addSubProperty(mainProp, optionId, subProp);
                    }
                }

                //static properties
                NodeList staticPropNL = objElem.getElementsByTagName("staticProperty");
                for (int q=0; q<staticPropNL.getLength(); q++){
                    Element staticPropElem = (Element)staticPropNL.item(q);
                    String staticPropTypeTag = staticPropElem.getTextContent();
                    PropertyType staticPropType = PropertyType.getByTag(staticPropTypeTag);
                    objProps.addStaticProperty(staticPropType);
                }

                //static property lists
                //static properties
                NodeList staticPropListNL = objElem.getElementsByTagName("staticPropertyListId");
                for (int q=0; q<staticPropListNL.getLength(); q++){
                    Element staticPropListElem = (Element)staticPropListNL.item(q);
                    String staticPropTypeId = staticPropListElem.getTextContent();
                    objProps.addStaticPropertyList(staticPropTypeId);
                }
            }
        }
    }

    private static void processStaticPropList(Element rootElement){
        NodeList staticPropListNL = rootElement.getElementsByTagName("staticPropertyList");
        for (int i=0; i<staticPropListNL.getLength(); i++) {
            Element staticPropListElem = (Element)staticPropListNL.item(i);
            String id = staticPropListElem.getAttribute("id");
            String name = staticPropListElem.getAttribute("name");

            NodeList staticPropNL = staticPropListElem.getElementsByTagName("staticProperty");
            List<PropertyType> props = new ArrayList<PropertyType>();
            for (int j=0; j<staticPropNL.getLength(); j++){
                Element staticPropElem = (Element)staticPropNL.item(j);
                String propTypeTag = staticPropElem.getTextContent();
                PropertyType staticPropType = PropertyType.getByTag(propTypeTag);
                props.add(staticPropType);
            }
            PropertyManager.registerStaticPropertyList(id, name, props);
        }
    }


    /**
     * Register property.
     */
    public static void registerProperty(Property property) {

        if (PROPERTY_MAP.containsKey(property.getId())) {
            throw new IllegalArgumentException("Property Id's for given type must be unique: '" + property.getId() + "'");
        }
        PROPERTY_MAP.put(property.getId(), property);
        if (property.getType() == PropertyClass.MODE) {
            MODES.add(property);
        }
    }

    public static Property getProperty(String id) {

        return PROPERTY_MAP.get(id);
    }


    /**
     * Register object properties.
     */
    public static void registerObject(ObjectProperties object) {
        if (OBJECT_MAP.containsKey(object.getType())) {
            throw new IllegalArgumentException("Cannot register more than one object of type " + object.getType());
        }
        OBJECT_MAP.put(object.getType(), object);
    }

    public static ObjectProperties getObjectProperties(ObjectType type) {
        return OBJECT_MAP.get(type);
    }


    /**
     * Register property list.
     */
    public static void registerPropertyList(String id, String name, List<Property> props) {
        if (PROPERTY_LIST_MAP.containsKey(id)) {
            throw new IllegalArgumentException("Cannot register duplicate id '" + id + "'");
        }
        PROPERTY_LIST_NAMES.put(id, name);
        PROPERTY_LIST_MAP.put(id, props);
    }

    public static List<Property> getPropertyList(String id) {
        return PROPERTY_LIST_MAP.get(id);
    }

    public static String getPropertyListName(String id) {
        return PROPERTY_LIST_NAMES.get(id);
    }


    /**
     * Register static property list.
     */
    public static void registerStaticPropertyList(String id, String name, List<PropertyType> props) {
        if (STATIC_PROPERTY_LIST_MAP.containsKey(id)) {
            throw new IllegalArgumentException("Cannot register duplicate id '" + id + "'");
        }
        STATIC_PROPERTY_LIST_NAMES.put(id, name);
        STATIC_PROPERTY_LIST_MAP.put(id, props);
    }

    public static List<PropertyType> getStaticPropertyList(String id) {
        return STATIC_PROPERTY_LIST_MAP.get(id);
    }

    public static String getStaticPropertyListName(String id) {
        return STATIC_PROPERTY_LIST_NAMES.get(id);
    }


    /**
     * Gets all modes.
     */
    public static Set<Property> getModes() {
        return MODES;
    }
}
