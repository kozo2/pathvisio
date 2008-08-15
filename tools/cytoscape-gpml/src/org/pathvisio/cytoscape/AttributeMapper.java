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

import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PropertyType;

import cytoscape.data.CyAttributes;


public interface AttributeMapper {
	/**
	 * Set a default value that will be used when no mapping is available
	 */
	public void setDefaultValue(PropertyType prop, String value);
	public void setMapping(String attr, PropertyType prop);
	public void attributesToProperties(String id, PathwayElement elm, CyAttributes attr);
	public void propertiesToAttributes(String id, PathwayElement elm, CyAttributes attr);
}
