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
package org.pathvisio.data;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBConnector {
	public static final int PROP_NONE = 0;
	public static final int PROP_RECREATE = 4;
	public static final int PROP_FINALIZE = 8;
	
	/**
	 * Type for gene database
	 */
	public static final int TYPE_GDB = 0;
	/**
	 * Type for expression database
	 */
	public static final int TYPE_GEX = 1;

	public abstract Connection createConnection(String dbName) throws Exception;

	public abstract Connection createConnection(String dbName, int props) throws Exception;	
	
	/**
	 * Close the given connection
	 * @param con The connection to be closed
	 * @throws Exception
	 */
	public void closeConnection(Connection con) throws Exception;
	/**
	 * Close the given connection, and optionally finalize it after creation (using {@link #PROP_FINALIZE})
	 * @param con The connection to be closed
	 * @param props Close properties (one of {@link #PROP_NONE}, {@link #PROP_FINALIZE} or {@link #PROP_RECREATE})
	 * @throws Exception
	 */
	public void closeConnection(Connection con, int props) throws Exception;
	
	/**
	 * Set the database type (one of {@link #TYPE_GDB} or {@link #TYPE_GEX})
	 * @param type The type of the database that will be used for this class
	 */
	public void setDbType(int type);
	
	/**
	 * Get the database type (one of {@link #TYPE_GDB} or {@link #TYPE_GEX})
	 * return The type of the database that is used for this class
	 */
	public int getDbType();
	
	/**
	 * Create a new database with the given name. This includes creating tables.
	 * @param dbName The name of the database to create
	 * @return A connection to the newly created database
	 * @throws Exception 
	 * @throws Exception
	 */
	public Connection createNewDatabase(String dbName) throws Exception;
	
	/**
	 * This method is called to finalize the given database after creation
	 * (e.g. set read-only, archive files). The database name needs to returned, this
	 * may change when finalizing the database modifies the storage type (e.g. from directory
	 * to single file).
	 * @param dbName The name of the database to finalize	
	 * @throws Exception
	 * @return The name of the finalized database
	 */
	public String finalizeNewDatabase(String dbName) throws Exception;
	
	/**
	 * Excecutes several SQL statements to create the tables and indexes for storing 
	 * the expression data
	 */
	public void createTables(Connection con) throws Exception;
	
	/**
	 * Creates indices for a newly created expression database.
	 * @param con The connection to the expression database
	 * @throws SQLException
	 */
	public void createIndices(Connection con) throws SQLException;
	
	/**
	 * This method may be implemented when the database files need to be
	 * compacted or defragmented after creation of a new database. It will be called
	 * after all data is added to the database.
	 * @param con A connection to the database
	 * @throws SQLException
	 */
	public void compact(Connection con) throws SQLException;
}
