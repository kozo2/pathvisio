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
package org.pathvisio.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Extensible enum for Anchor types
 */
public class AnchorType implements Comparable<AnchorType> {
	private static Map<String, AnchorType> nameMappings = new HashMap<String, AnchorType>();
	private static Set<AnchorType> values = new TreeSet<AnchorType>();

	public static final AnchorType NONE = new AnchorType("None");
	public static final AnchorType CIRCLE = new AnchorType("Circle");

	private String name;

	private AnchorType (String name)
	{
		if (name == null) { throw new NullPointerException(); }

		this.name  = name;
		values.add(this);
		nameMappings.put (name, this);
	}

	/**
	   Create an object and add it to the list.

	   For extending the enum.
	 */
	public static AnchorType create (String name)
	{
		return new AnchorType(name);
	}

	/**
	   looks up the AnchorType corresponding to that name.
	 */
	public static AnchorType fromName (String value)
	{
		return nameMappings.get(value);
	}

	/**
	   Stable identifier for this AnchorType.
	 */
	public String getName ()
	{
		return name;
	}

	static public AnchorType[] getValues()
	{
		return values.toArray(new AnchorType[0]);
	}

	public String toString()
	{
		return name;
	}

	public int compareTo(AnchorType o) {
		return toString().compareTo(o.toString());
	}
}