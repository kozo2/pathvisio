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
package org.pathvisio.desktop.util;


/**
 * A row in a ListWithProperties table
 * A row defines a way to get to a number of properties that may or
 * may not be displayed in columns of this pathway
 *
 * @param <T> PropertyColumn type
 */
public interface RowWithProperties<T extends PropertyColumn>
{
	/**
	 * Get a property of this object
	 */
	public String getProperty (T prop);
}