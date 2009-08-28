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

import java.util.Set;
import java.util.HashSet;

/**
 * @author Mark Woon
 */
public class PropertyEnum {
    private String value;
    private Set<Property> modes;


    public PropertyEnum(String val) {
        value = val;
    }

    public String getValue() {
        return value;
    }


    /**
     * Gets the modes this enum is visible in.  If none are specified, then this enum is always visible.
     *
     * @return the modes this is visible in, or null if it is always visible
     */
    public Set<Property> getModes() {
        return modes;
    }

    /**
     * Sets the modes this enum is visible in.  A mode must be of type {@link PropertyClass#MODE}.
     */
    public void setModes(Set<Property> modes) {

        for (Property mode : modes) {
            addMode(mode);
        }
    }

    /**
     * Adds a mode this enum is visible in.  A mode must be of type {@link PropertyClass#MODE}.
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
     * Enum is hidden if it is only visible in specific modes and we're not running in that mode right now.
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
}
