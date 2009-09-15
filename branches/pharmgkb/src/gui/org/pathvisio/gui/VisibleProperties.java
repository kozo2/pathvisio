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
//
package org.pathvisio.gui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PropertyType;
import org.pathvisio.model.Property;
import org.pathvisio.model.PropertyManager;
import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.preferences.PreferenceManager;

/**
 * The Properties side-panel shows a list of properties of the current PathwayElement,
 * but depending on the user preferences not all properties will be shown. By default,
 * some of the advanced properties are hidden. VisibleProperties is a Utility class that
 * knows how to filter advanced attributes.
 */
public class VisibleProperties 
{
	/**
	 * Get property keys for a certain pathway element, but take into 
	 * account the SHOW_ADVANCED_ATTRIBUTES preference.
	 * 
	 * if SHOW_ADVANCED_ATTRIBUTES is
	 * true:
	 * 	returns all static / dynamic property keys of e
	 * 
	 * false:
	 * 	returns only static properties, and excludes certain properties considered "advanced"
	 * 	such as graphId and graphRef.
	 */
	public static List<Object> getVisiblePropertyKeys (PathwayElement e)
	{
		List<Object> result = new ArrayList<Object>();
		// add dynamic properties
		List<Property> modes = new ArrayList<Property>();
		for (Property prop : PropertyManager.getModes()) {
			if (PreferenceManager.getCurrent().getBoolean(prop)) {
				modes.add(prop);
			}
		}
        result.addAll(e.getDynamicPropertyKeys(modes));

		result.addAll (e.getStaticPropertyKeys());
		boolean advanced = PreferenceManager.getCurrent().getBoolean(GlobalPreference.SHOW_ADVANCED_PROPERTIES);
		if (!advanced)
		{
			// filter out advanced properties
			result.removeAll (
					Arrays.asList(PropertyType.BOARDWIDTH,
							PropertyType.BOARDHEIGHT,
							PropertyType.WINDOWWIDTH,
							PropertyType.WINDOWHEIGHT,
							PropertyType.GRAPHID,
							PropertyType.BIOPAXREF,
							PropertyType.GRAPHREF,
							PropertyType.GROUPID,
							PropertyType.GROUPREF,
							PropertyType.STARTGRAPHREF,
							PropertyType.ENDGRAPHREF,
							PropertyType.ZORDER));
		}
		
		return result;
	}

}
