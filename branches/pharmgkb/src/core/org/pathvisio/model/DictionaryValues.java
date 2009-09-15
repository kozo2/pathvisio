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

import java.util.Set;
import java.util.List;

/**
 * @author Rebecca Tang
 * Holds selected Dictionary Values
 * Used to pass values to and from
 */
public class DictionaryValues {
	List<String> m_values;

	public void setValue(String value){
		if (!m_values.contains(value)){
		  m_values.add(value);
		}
	}
	public List<String> getValues(){
		return m_values;
	}

}
