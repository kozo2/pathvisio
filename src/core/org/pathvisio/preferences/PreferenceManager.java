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
package org.pathvisio.preferences;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Properties;

import org.pathvisio.debug.Logger;
import org.pathvisio.util.ColorConverter;
import org.pathvisio.util.Utils;
import org.pathvisio.model.Property;

/**
 * Loads & saves application preferences
 */
public class PreferenceManager 
{
	private Properties properties;
	private File propFile = null;
	
	boolean dirty;
	
	/**
	 * Stores preferences back to preference file, if necessary.
	 * Only writes to disk if the properties have changed.
	 */
	public void store()
	{
		if (dirty)
		{
			Logger.log.info ("Preferences have changed. Writing preferences");
			try
			{
				properties.store(new FileOutputStream(propFile), "");
				dirty = false;
			}
			catch (IOException e)
			{
				Logger.log.error ("Could not write properties");
			}
		}
	}
		
	/**
	 * Load preferences from file
	 */
	public void load()
	{
		properties = new Properties();
		propFile = new File(System.getProperty("user.home") + File.separator + 
				".PathVisio" + File.separator + ".PathVisio");
		
		try
		{
			properties.load(new FileInputStream(propFile));
			compatUpdate();
		}
		catch (IOException e)
		{
			Logger.log.warn ("Could not read properties", e);
		}
		dirty = false;
	}
	
	/**
	 * Convert old / obsolete properties to new values.
	 * Old properties are left in place for backwards compatibility.
	 */
	private void compatUpdate()
	{
		if (properties.containsKey(GlobalPreference.DB_GDB_CURRENT.name()) && 
			!properties.containsKey(GlobalPreference.DB_CONNECTSTRING_GDB.name()))
			set(GlobalPreference.DB_CONNECTSTRING_GDB, "idmapper-pgdb:" + get(GlobalPreference.DB_GDB_CURRENT));
		if (properties.containsKey(GlobalPreference.DB_METABDB_CURRENT.name()) &&
			!properties.containsKey(GlobalPreference.DB_CONNECTSTRING_METADB.name()))
			set(GlobalPreference.DB_CONNECTSTRING_METADB, "idmapper-pgdb:" + get(GlobalPreference.DB_METABDB_CURRENT));
	}
	
	/**
	 * Get a preference as String
	 */
	public String get(Object p) {
		if (p instanceof Preference) {
			String key = ((Preference)p).name();
			if (properties.containsKey(key))
			{
				return properties.getProperty(key);
			}
			else
			{
				return ((Preference)p).getDefault();
			}
		} else if (p instanceof Property) {
			return properties.getProperty(((Property)p).getId());
		} else {
			throw new IllegalArgumentException("Key must be a property or preference");
		}
	}

	public void set (Object p, String newVal)
	{
		String key;
		if (p instanceof Preference) {
			key = ((Preference)p).name();
		} else if (p instanceof Property) {
			key = ((Property)p).getId();
		} else {
			throw new IllegalArgumentException("Key must be a property or preference");
		}
		String oldVal = properties.getProperty(key);
		if (!Utils.stringEquals(oldVal, newVal)) {
			properties.setProperty(key, newVal);
			dirty = true;
		}
	}

	public int getInt (Preference p)
	{
		return Integer.parseInt (get(p));
	}
	
	public void setInt (Preference p, int val)
	{
		set (p, "" + val);
	}
	
	public File getFile (Preference p)
	{
		return new File (get (p));
	}
	
	public void setFile (Preference p, File val)
	{
		set (p, "" + val);
	}
	
	public Color getColor (Preference p)
	{
		return ColorConverter.parseColorString(get (p));
	}

	public void setColor (Preference p, Color c)
	{
		set (p, ColorConverter.getRgbString(c));
	}
	
	public void setBoolean (Object p, Boolean val)
	{
		if (!(p instanceof Preference || p instanceof Property)) {
			throw new IllegalArgumentException("Key must be a property or preference");
		}
		set (p, "" + val);
	}

	public boolean getBoolean (Object p)
	{
		if (!(p instanceof Preference || p instanceof Property)) {
			throw new IllegalArgumentException("Key must be a property or preference");
		}
		return Boolean.parseBoolean(get(p));
	}

	/**
	 * Returns true if the current value of Preference p equals the default value.
	 */
	public boolean isDefault (Preference p)
	{
		return !properties.containsKey(p.name());
	}	

	
	static PreferenceManager preferences = null;
	
	public static PreferenceManager getCurrent()
	{
		return preferences;
	}
	
	public static void init()
	{
		if (preferences == null)
		{
			preferences = new PreferenceManager();
			preferences.load();
		}
		else
		{
			Logger.log.warn ("PreferenceManager was initialized twice");
		}
	}
}
