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
package org.pathvisio.data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.rdb.DBConnector;
import org.bridgedb.rdb.SimpleGdb;
import org.bridgedb.rdb.SimpleGdbFactory;
import org.pathvisio.debug.Logger;
import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.preferences.PreferenceManager;

/**
 * GdbManager is responsible for maintaining a single
 * static Gene database for use in the GUI application
 * 
 * This gene database could be a SimpleGdb, 
 * DoubleGdb or aggregateGdb or otherwise.
 * 
 * This class is not needed in headless mode. 
 */
public class GdbManager extends AbstractListModel 
{
	private final IDMapperStack currentGdb = new IDMapperStack();
	private IDMapper metabolites;
	private IDMapper genes;
	
	public GdbManager()
	{
		try
		{
			Class.forName ("org.bridgedb.file.IDMapperText");
			Class.forName ("org.bridgedb.rdb.IDMapperRdb");
		}
		catch (ClassNotFoundException ex)
		{
			Logger.log.error("Could not initilize GDB Manager", ex);
			//TODO: propagate exception???
		}
	}
	
	public IDMapperStack getCurrentGdb ()
	{
		return currentGdb;
	}
	
	/**
	 * Returns true if there is currently a non-null
	 * gdb and it isConnected()
	 */
	public boolean isConnected()
	{
		return 
			currentGdb != null &&
			currentGdb.isConnected();
	}

	/**
	 * Set the global metabolite database 
	 * with the given file- or
	 * directory name.
	 * The database type used for the connection
	 * depends on the value of the DB_ENGINE_GDB value
	 * 
	 * use null to disconnect the current db
	 */
	public void setMetaboliteDb(String dbName) throws IDMapperException
	{
		if (dbName == null)
		{
			currentGdb.removeIDMapper(metabolites);
			metabolites = null;
		}
		else
		{
			String connectString = dbName;
			metabolites = currentGdb.addIDMapper(connectString);
			if (metabolites != null)
			{
				PreferenceManager.getCurrent().set(GlobalPreference.DB_CONNECTSTRING_METADB, (connectString));
			}
		}
		
		GdbEvent e = new GdbEvent (this, GdbEvent.GDB_CONNECTED, dbName);
		fireGdbEvent (e);
		Logger.log.trace("Current Gene Database: " + dbName);
	
	}
	

	/**
	 * Implement this interface if you want to listen to Gdb Events.
	 */
	public interface GdbEventListener 
	{
		public void gdbEvent(GdbEvent e);
	}

	/**
	 * Use this method if you want to respond to the connection of Gdb databases 
	 */
	public void addGdbEventListener(GdbEventListener l) 
	{
		if (l == null) throw new NullPointerException();
		gdbEventListeners.add(l);
	}
	
	public void removeGdbEventListener(GdbEventListener l) {
		gdbEventListeners.remove(l);
	}
	
	private void fireGdbEvent (GdbEvent e)
	{
		for(GdbEventListener l : gdbEventListeners) l.gdbEvent(e);
	}
		
	private List<GdbEventListener> gdbEventListeners  = new ArrayList<GdbEventListener>();
	
	/**
	 * Set the global gene database
	 * with the given file- or
	 * directory name.
	 * The database type used for the connection
	 * depends on the value of the DB_ENGINE_GDB value
	 * 
	 * use null to disconnect the current db.
	 */
	public void setGeneDb(String dbName) throws IDMapperException
	{
		if (dbName == null)
		{
			if (genes != null) currentGdb.removeIDMapper(genes);
			genes = null;
		}
		else
		{
			String connectString = dbName;
			genes = currentGdb.addIDMapper(connectString);
			if (genes != null)
			{
				PreferenceManager.getCurrent().set(GlobalPreference.DB_CONNECTSTRING_GDB, connectString);
			}
		}
		GdbEvent e = new GdbEvent (this, GdbEvent.GDB_CONNECTED, dbName);
		fireGdbEvent (e);
		Logger.log.trace("Current Gene Database: " + dbName);
	}
	
	/**
	 * Helper method
	 * Connect to a database using the 
	 * DBConnector set in the global preferences.
	 */
	private SimpleGdb connect(String gdbName) throws IDMapperException
	{
		if (dbConnector == null) throw new NullPointerException();
		SimpleGdb gdb = SimpleGdbFactory.createInstance(gdbName, dbConnector, DBConnector.PROP_NONE);
		return gdb;
	}
	
	/**
	 * Initiates this class. Checks the preferences for a previously
	 * used Gene Database and tries to open a connection if found.
	 * If that doesn't work, reverts attempts to use the default value for
	 * that property.
	 * 
	 * Idem for the metabolite database.
	 * TODO: move to src/swing (only used standalone)
	 */
	public void initPreferred()
	{
		try
		{
			dbConnector = getDBConnector();
		}
		catch (ClassNotFoundException e)
		{
			Logger.log.error ("Could not initialize gene databases", e);
		}
		catch (IllegalAccessException e)
		{
			Logger.log.error ("Could not initialize gene databases", e);
		}
		catch (InstantiationException e)
		{
			Logger.log.error ("Could not initialize gene databases", e);
		}		
		
		PreferenceManager prefs = PreferenceManager.getCurrent();
		// first do the Gene database
		String gdbName = prefs.get (GlobalPreference.DB_CONNECTSTRING_GDB);
		if(!gdbName.equals("") && !prefs.isDefault (GlobalPreference.DB_CONNECTSTRING_GDB))
		{
			try 
			{
				setGeneDb(gdbName);
			} 
			catch(IDMapperException e) 
			{
				Logger.log.error("Setting previous Gdb failed.", e);
			}
		}
		// then do the Metabolite database
		gdbName = prefs.get(GlobalPreference.DB_CONNECTSTRING_METADB);
		if(!gdbName.equals("") && !prefs.isDefault (GlobalPreference.DB_CONNECTSTRING_METADB))
		{
			try 
			{
				setMetaboliteDb(gdbName);
			} 
			catch(Exception e) 
			{
				Logger.log.error("Setting previous Metabolite db failed.", e);
			}
		}
	}

	
	private DBConnector dbConnector = null;
	
	@Deprecated
	public void setDBConnector(DBConnector value)
	{
		dbConnector = value;
	}
	
	@Deprecated
	public DBConnector getDBConnector() throws 
		ClassNotFoundException, 
		InstantiationException, 
		IllegalAccessException 
	{
		if (dbConnector == null)
		{
			String className = null;
			className = PreferenceManager.getCurrent().get(GlobalPreference.DB_ENGINE_GEX);
			
			if(className == null) return null;
				
			Class<?> dbc = Class.forName(className);
			Object o = dbc.newInstance();
			if(o instanceof DBConnector) 
			{
				dbConnector = (DBConnector)dbc.newInstance();
				dbConnector.setDbType(DBConnector.TYPE_GDB);
			}
		}
		return dbConnector;
	}

	public Object getElementAt(int arg0) 
	{
		return currentGdb.getIDMapperAt(arg0);
	}

	public int getSize() 
	{
		return currentGdb.getSize();
	}
}
