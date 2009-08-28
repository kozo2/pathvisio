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

import org.pathvisio.debug.Logger;

import java.util.*;

/**
 * @author Mark Woon
 */
public class ObjectProperties {
    /** Unique id */
    private String id;
    /** Display name */
    private String name;
    private ObjectType type;
    private List<Property> properties = new ArrayList<Property>();
    private List<PropertyType> staticProperties = new ArrayList<PropertyType>();
    private Set<Property> modes;
    private Map<Property, Map<String, List<Property>>> subPropertiesMap;


    public ObjectProperties(String newId, String newName, ObjectType newType) {
        id = newId;
        name = newName;
        type = newType;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ObjectType getType() {
        return type;
    }


    public List<Property> getProperties() {
        return properties;
    }

    /**
     * returns all properties with a given mode
     * @param mode
     * @return
     */
    public List<Property> getProperties(Property mode) {
        List<Property> props = new ArrayList<Property>();
        for (Property p: properties){
            if (p.getModes().contains(mode)){
                props.add(p);
            }
        }
        return props;
    }

    public void addProperty(Property prop) {
        if (properties == null) {
            properties = new ArrayList<Property>();
        }
        properties.add(prop);
    }

    public void addPropertyList(String id) {
        List<Property> props = PropertyManager.getPropertyList(id);
        if (props != null) {
            properties.addAll(props);
        } else {
            Logger.log.warn("Invalid property list id: " + id);
        }
    }


    public List<PropertyType> getStaticProperties() {
        return staticProperties;
    }

    public void addStaticProperty(PropertyType prop) {
        staticProperties.add(prop);
    }

    public void addStaticPropertyList(String id) {
        List<PropertyType> props = PropertyManager.getStaticPropertyList(id);
        if (props != null) {
            staticProperties.addAll(props);
        } else {
            Logger.log.warn("Invalid static property list id: " + id);
        }
    }


    /**
     * Gets the modes this property is visible in.
     *
     * @return the modes this is visible in, or null if it is always visible
     */
    public Set<Property> getModes() {
        return modes;
    }

    /**
     * Sets the modes this property is visible in.  A mode must be of type {@link PropertyClass#MODE}.
     */
    public void setModes(Set<Property> modes) {

        for (Property mode : modes) {
            addMode(mode);
        }
    }

    /**
     * Adds a mode this property is visible in.  A mode must be of type {@link PropertyClass#MODE}.
     */
    public void addMode(Property mode) {

        if (mode != null) {
            if (mode.getType() != PropertyClass.MODE) {
                Logger.log.error("Mode property must be of type MODE");
            } else {
                if (modes == null) {
                    modes = new HashSet<Property>();
                }
                modes.add(mode);
            }
        }
    }


    public Set<Property> getSubPropertyKeys() {
        return subPropertiesMap.keySet();
    }

    public List<Property> getSubProperties(Property prop, String value) {
        return subPropertiesMap.get(prop).get(value);
    }

    /**
     * Adds a sub-property that is dependent on the value of another property.
     *
     * @param propKey the property to check
     * @param condition the value of property under which the sub-property is visible
     * @param subProp the sub-property
     */
    public void addSubProperty(Property propKey, String condition, Property subProp) {

        if (subPropertiesMap == null) {
            subPropertiesMap = new HashMap<Property, Map<String, List<Property>>>();
        }
        Map<String, List<Property>> subProperties = subPropertiesMap.get(propKey);
        if (subProperties == null) {
            subProperties = new HashMap<String, List<Property>>();
            subPropertiesMap.put(propKey, subProperties);
        }
        if (propKey.isValidValue(condition)) {
            List<Property> props = subProperties.get(condition);
            if (props == null) {
                props = new ArrayList<Property>();
                subProperties.put(condition, props);
            }
            props.add(subProp);
        }
    }
}
