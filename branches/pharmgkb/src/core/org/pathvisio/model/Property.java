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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mark Woon
 */
public class Property implements Comparable<Property> {
    /** Unique id */
    private String id;
    /** Display name */
    private String name;
    private PropertyClass type;
    private List<PropertyEnum> values;
    private Set<Property> modes;
    private boolean isInternal;
    private boolean isMultiSelect;


    public Property(String newId, String newName, PropertyClass newType) {
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

    public PropertyClass getType() {
        return type;
    }


    /**
     * Gets allowed values if this is an enum.
     *
     * @return list of allowed values or null if this is not an enum
     */
    public List<PropertyEnum> getValues() {
        return values;
    }

    /**
     * Sets allowed values if this is an enum.
     */
    public void setValues(List<PropertyEnum> values) {
        this.values = values;
    }

    public void addValue(PropertyEnum value) {
        if (value != null) {
            if (values == null) {
                values = new ArrayList<PropertyEnum>();
            }
            values.add(value);
        }
    }

    public boolean isValidValue(String value) {
        for (PropertyEnum e : values) {
            if (e.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidValue(String value, Set<Property> modes) {

        if (modes == null) {
            return isValidValue(value);
        }
        for (PropertyEnum e : values) {
            if (!Collections.disjoint(e.getModes(), modes) && e.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the modes this property is visible in.  If none are specified, then this property is always visible.
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


    /**
     * Property is hidden if it is only visible in specific modes and we're not running in that mode right now.
     */
    public boolean isHidden(Set<Property> currentModes) {

        if (currentModes != null && currentModes.size() > 0 && 
                modes != null && modes.size() > 0) {
            for (Property mode : currentModes) {
                if (modes.contains(mode)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    /**
     * Gets whether this property is internal to PathVisio.
     */
    public boolean isInternal() {
        return isInternal;
    }

    /**
     * Sets whether this property is internal to PathVisio.
     */
    public void setInternal(boolean internal) {
        this.isInternal = internal;
    }


    /**
     * Gets whether this property can have multiple values.
     */
    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    /**
     * Sets whether this property can have multiple values.
     */
    public void setMultiSelect(boolean multiSelect) {
        this.isMultiSelect = multiSelect;
    }


    public int compareTo(Property o) {
        int rez = name.compareTo(o.getName());
        if (rez == 0) {
            rez = id.compareTo(o.getId());
        }
        return rez;
    }

    @Override
    public String toString() {
        return name;
    }
}
