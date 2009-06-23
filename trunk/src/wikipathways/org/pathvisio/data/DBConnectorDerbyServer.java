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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.OperationNotSupportedException;

import org.bridgedb.IDMapperException;
import org.bridgedb.rdb.DBConnector;
import org.pathvisio.debug.Logger;
import org.pathvisio.debug.StopWatch;

/**
 * Database connector that connects to a Derby database server
 * @author thomas
 *
 */
public class DBConnectorDerbyServer extends DBConnector {
	
	static private String host;
	static private int port;
	static boolean inited = false;
	
	/**
	 * Initialize global settings that will be shared by all instances of DBConnectorDerbyServer
	 * @param host	The host on which the database resides
	 * @param port	The port to create the connection on
	 */
	static public void init (String aHost, int aPort)
	{
		host = aHost;
		port = aPort;
		inited = true;
	}
	
	/**
	 * Create a new database connector for the hostname and port set at initialization time.
	 * Will raise an exception if you forgot to call init() before.
	 */
	public DBConnectorDerbyServer() 
	{
		if (!inited) throw new IllegalArgumentException("Not yet initialized!");
	}
	
	public Connection createConnection(String dbName) throws IDMapperException 
	{
		Properties sysprop = System.getProperties();
		sysprop.setProperty("derby.storage.tempDirectory", System.getProperty("java.io.tmpdir"));
		
		try
		{
			sysprop.setProperty("derby.stream.error.file", File.createTempFile("derby",".log").toString());
			Class.forName("org.apache.derby.jdbc.ClientDriver");
		}
		catch (ClassNotFoundException e)
		{
			throw new IDMapperException (e);
		}
		catch (IOException f)
		{
			throw new IDMapperException (f);
		}
		StopWatch timer = new StopWatch();
		timer.start();
		
		String url = "jdbc:derby://" + host + ":" + port + "/" + dbName;
		Logger.log.trace("Connecting to database: " + url);
		Connection con;
		try
		{
			con = DriverManager.getConnection(url);
		}
		catch (SQLException f)
		{
			throw new IDMapperException (f);
		}
		Logger.log.trace("Connected");
		return con;
	}

	public Connection createConnection(String dbName, int props) throws IDMapperException 
	{
		return createConnection(dbName);
	}

	public String finalizeNewDatabase(String dbName) throws IDMapperException 
	{
		//Creating database not supported
		throw new IDMapperException (new OperationNotSupportedException("Can't create new database on server"));
	}

}
