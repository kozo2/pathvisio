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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.pathvisio.data.CachedData.Data;
import org.pathvisio.debug.Logger;
import org.pathvisio.debug.StopWatch;
import org.pathvisio.model.DataSource;
import org.pathvisio.model.Xref;
import org.pathvisio.util.ProgressKeeper;

/**
 * Responsible for creating and querying a pgex database.
 * SimpleGex wraps SQL statements in methods, 
 * so the rest of the apps don't need to know the
 * details of the Database schema.
 * For this, SimpleGex uses the generic JDBC interface.
 * 
 * It delegates dealing with the differences between Derby, Hsqldb etc.
 * to a DBConnector instance. 
 * You need to pass a correct DBConnector instance at creation of
 * SimpleGex. 
 * 
 * In the PathVisio GUI environment, use GexManager
 * to create and connect to a centralized Gex. 
 * This will also automatically
 * find the right DBConnector from the preferences.
 *  
 * In a head-less or test environment, you can bypass GexManager
 * to create or connect to one or more databases of any type.
 */
//TODO: add function to query # of samples
public class SimpleGex
{
	private static final int GEX_COMPAT_VERSION = 1; //Preferred schema version
	private static final int SAMPLE_NAME_LEN = 50; // max length of sample names
	
	private Connection con;
	private DBConnector dbConnector;
			
	private static CachedData cachedData;

	/**
	 * Get the {@link Connection} to the Expression-data database
	 * @deprecated Shouldn't be exposed
	 */
	public Connection getCon() { return con; }
	/**
	 * Check whether a connection to the database exists
	 * @return	true is a connection exists, false if not
	 */
	public boolean isConnected() { return con != null; }
	
	private String dbName;
	/**
	 * Get the database name of the expression data currently loaded
	 */
	public String getDbName() { return dbName; }
	
	/**
	 * Set the database name of the expression data currently loaded
	 * (Connection is not reset)
	 */
	private void setDbName(String name) { dbName = name; }
						    
	private HashMap<Integer, Sample> samples;
	/**
	 * Loads the samples used in the expression data (Sample table) in memory
	 */
	public void setSamples()
	{		
		try {
			ResultSet r = con.createStatement().executeQuery(
					"SELECT idSample, name, dataType FROM samples"
			);
			samples = new HashMap<Integer, Sample>();
			while(r.next())
			{
				int id = r.getInt(1);
				samples.put(id, new Sample(id, r.getString(2), r.getInt(3)));					
			}
		} catch (Exception e) {
			Logger.log.error("while loading data from the 'samples' table: " + e.getMessage(), e);
		}
	}
	
	PreparedStatement pstSample = null;
	PreparedStatement pstExpr = null;
	
	public void prepare() throws SQLException
	{
		pstSample = con.prepareStatement(
				" INSERT INTO SAMPLES " +
				"	(idSample, name, dataType)  " +
		" VALUES (?, ?, ?)		  ");
		pstExpr = con.prepareStatement(
				"INSERT INTO expression			" +
				"	(id, code, ensId,			" + 
				"	 idSample, data, groupId)	" +
		"VALUES	(?, ?, ?, ?, ?, ?)			");
	}


	/**
	 * add a Sample to the db.
	 * Must call preprare() before.
	 */
	public void addSample(int sampleId, String value, int type) throws SQLException
	{
		assert (pstSample != null);
		if (value.length() >= SAMPLE_NAME_LEN) 
			throw new IllegalArgumentException ("Sample name can't be longer than " + SAMPLE_NAME_LEN + " chars");
		pstSample.setInt(1, sampleId);
		pstSample.setString(2, value);
		pstSample.setInt(3, type);
		pstSample.execute();
	}
	
	/**
	 * Add an expression row to the db. Must call prepare() before.
	 */
	public void addExpr(Xref ref, String link, String idSample, String value, int group)
		throws SQLException
	{
		assert (pstExpr != null);
		pstExpr.setString(1, ref.getId());
		pstExpr.setString(2, ref.getDataSource().getSystemCode());
		pstExpr.setString(3, link);
		pstExpr.setString(4, idSample);
		//TODO: this is a hack.
		// Proper solution: ask user which columns contain data
		// don't even try to import annotation and other stuff
		// give an exception if a data value is longer than 50
		String truncValue = value;
		if (value.length() > 50) truncValue = value.substring(0, 50);		
		pstExpr.setString(5, truncValue);
		pstExpr.setInt(6, group);
		pstExpr.execute();
	}
	
	public Sample getSample(int id) {
		return getSamples().get(id);
	}
	
	public HashMap<Integer, Sample> getSamples()
	{
		if(samples == null) setSamples();
		return samples;
	}
	
	public List<String> getSampleNames() {
		return getSampleNames(-1);
	}
	
	public List<String> getSampleNames(int dataType) {
		List<String> names = new ArrayList<String>();
		List<Sample> sorted = new ArrayList<Sample>(samples.values());
		Collections.sort(sorted);
		for(Sample s : sorted) {
			if(dataType == s.dataType || dataType == -1)
				names.add(s.getName());
		}
		return names;
	}
	
	public List<Sample> getSamples(int dataType) {
		List<Sample> smps = new ArrayList<Sample>();
		List<Sample> sorted = new ArrayList<Sample>(samples.values());
		Collections.sort(sorted);
		for(Sample s : sorted) {
			if(dataType == s.dataType || dataType == -1)
				smps.add(s);
		}
		return smps;
	}
	
	public List<Data> getCachedData(Xref idc)
	{
		if(cachedData != null) {
			return cachedData.getData(idc);
		} else {
			return null;
		}
	}
	
	public CachedData getCachedData() {
		return cachedData;
	}


	
	/**
	 * Loads expression data for all the given gene ids into memory
	 * @param refs	Genes to cache the expression data for
	 * (typically all genes in a pathway)
	 */
	public void cacheData(Collection<Xref> refs, ProgressKeeper p, Gdb gdb)
	{	
		cachedData = new CachedData();
		StopWatch timer = new StopWatch();
		timer.start();
			
		for(Xref pwIdc : refs)
		{
			String id = pwIdc.getId();			
			String code = pwIdc.getDataSource().getSystemCode();
			
			if(cachedData.hasData(pwIdc)) continue;
			
			List<String> ensIds = gdb.ref2EnsIds(pwIdc); //Get all Ensembl genes for this id
			
			HashMap<Integer, Data> groupData = new HashMap<Integer, Data>();
			
			if(ensIds.size() > 0) //Only create a Data object if the id maps to an Ensembl gene
			{				
				StopWatch tt = new StopWatch();
				StopWatch ts = new StopWatch();
				
				tt.start();
				
				groupData.clear();
				
				for(String ensId : ensIds)
				{	
					try {
						ts.start();
						
						ResultSet r = con.createStatement().executeQuery(
								"SELECT id, code, data, idSample, groupId FROM expression " +
								" WHERE ensId = '" + ensId + "'");
						//r contains all genes and data mapping to the Ensembl id
						while(r.next())
						{
							int group = r.getInt("groupId");
							Xref ref = new Xref(
									r.getString("id"), 
									DataSource.getBySystemCode(r.getString("code"))
									);
							Data data = groupData.get(group);
							if(data == null) {
								groupData.put(group, data = new Data(ref, group));
								cachedData.addData(pwIdc, data);
							}
							
							int idSample = r.getInt("idSample");					
							data.setSampleData(idSample, r.getString("data"));
						}
						
						ts.stopToLog("Fetching data for ens id: " + ensId + "\t");
					} catch (Exception e)
					{
						Logger.log.error("while caching expression data: " + e.getMessage(), e);
					}
				}
				
				tt.stopToLog(id + ", " + code + ": adding data to cache\t\t");
			}			
			if(p.isCancelled()) //Check if the process is interrupted
			{
				return;
			}
			p.worked(p.getTotalWork() / refs.size()); //Update the progress
		}
		p.finished();
		timer.stopToLog("Caching expression data\t\t\t");
		Logger.log.trace("> Nr of ids queried:\t" + refs.size());
	}
				
	/**
	 * Connects to the Expression database with
	 * option to remove the old database
	 * Will use the passed connector type (of which a new instance is created)
	 * @param 	create true if the old database has to be removed, false for just connecting
	 */
	public SimpleGex(String dbName, boolean create, DBConnector connector) throws DataException
	{
		this.dbName = dbName;
		try
		{
			dbConnector = connector.getClass().newInstance();
		}
		catch (InstantiationException e)
		{
			throw new DataException (e);
		} 
		catch (IllegalAccessException e) 
		{
			throw new DataException (e);
		}
		
		dbConnector.setDbType(DBConnector.TYPE_GEX);
		if(create)
		{
			createNewGex(dbName);
		} 
		else 
		{
			con = dbConnector.createConnection(dbName, DBConnector.PROP_NONE);
			setSamples();
		}
//		try
//		{
//			con.setReadOnly( !create );
//		}
//		catch (SQLException e)
//		{
//			throw new DataException (e);
//		}
	}
	
	/**
	 * Connects to the Expression database 
	 */
//	public static void connect() throws Exception
//	{
//		connect(null, false, true);
//	}
//	
//	public static void connect(String dbName) throws Exception
//	{
//		connect(dbName, false, true);
//	}
		
	/**
	 * Close the connection to the Expression database, with option to execute the 'SHUTDOWN COMPACT'
	 * statement before calling {@link Connection#close()}
	 */
	public void close() throws DataException
	{
		if(con != null)
		{
			dbConnector.closeConnection(con);
			con = null;
		}
	}

	/**
	 * Create a new database with the given name. This includes creating tables.
	 * @param dbName The name of the database to create
	 * @return A connection to the newly created database
	 * @throws Exception 
	 * @throws Exception
	 */
	public final void createNewGex(String dbName) throws DataException 
	{
		con = dbConnector.createConnection(dbName, DBConnector.PROP_RECREATE);
		this.dbName = dbName;
		createGexTables();
	}

	/**
	 * Excecutes several SQL statements to create the tables and indexes for storing 
	 * the expression data
	 */
	protected void createGexTables() throws DataException 
	{	
		try
		{
			con.setReadOnly(false);
			Statement sh = con.createStatement();
			try { sh.execute("DROP TABLE info"); } catch(SQLException e) { Logger.log.warn("Warning: unable to drop expression data tables: "+e.getMessage()); }
			try { sh.execute("DROP TABLE samples"); } catch(SQLException e) { Logger.log.warn("Warning: unable to drop expression data tables: "+e.getMessage()); }
			try { sh.execute("DROP TABLE expression"); } catch(SQLException e) { Logger.log.warn("Warning: unable to drop expression data tables: "+e.getMessage()); }
			
			sh.execute(
					"CREATE TABLE					" +
					"		info							" +
					"(	  version INTEGER PRIMARY KEY		" +
					")");
			sh.execute( //Add compatibility version of GEX
					"INSERT INTO info VALUES ( " + GEX_COMPAT_VERSION + ")");
			sh.execute(
					"CREATE TABLE                    " +
					"		samples							" +
					" (   idSample INTEGER PRIMARY KEY,		" +
					"     name VARCHAR(" + SAMPLE_NAME_LEN + "),					" +
					"	  dataType INTEGER					" +
			" )										");
			
			sh.execute(
					"CREATE TABLE					" +
					"		expression						" +
					" (   id VARCHAR(50),					" +
					"     code VARCHAR(50),					" +
					"	  ensId VARCHAR(50),				" +
					"     idSample INTEGER,					" +
					"     data VARCHAR(50),					" +
					"	  groupId INTEGER 					" +
	//				"     PRIMARY KEY (id, code, idSample, data)	" +
			")										");
		}
		catch (SQLException e)
		{
			throw new DataException (e);
		}
	}
	
	/**
	 * Creates indices for a newly created expression database.
	 * @param con The connection to the expression database
	 * @throws SQLException
	 */
	public void createGexIndices() throws DataException 
	{
		try
		{
			con.setReadOnly(false);
			Statement sh = con.createStatement();
			sh.execute(
					"CREATE INDEX i_expression_id " +
			"ON expression(id)			 ");
			sh.execute(
					"CREATE INDEX i_expression_ensId " +
			"ON expression(ensId)			 ");
			sh.execute(
					"CREATE INDEX i_expression_idSample " +
			"ON expression(idSample)	 ");
			sh.execute(
					"CREATE INDEX i_expression_data " +
			"ON expression(data)	     ");
			sh.execute(
					"CREATE INDEX i_expression_code " +
			"ON expression(code)	 ");
			sh.execute(
					"CREATE INDEX i_expression_groupId" +
			" ON expression(groupId)	");
		}
		catch (SQLException e)
		{
			// wrap up the sql exception
			throw new DataException (e);
		}
	}

	/**
	 * Run this after insterting all sample / expression data 
	 * once, to defragment the db and create indices.
	 * This method closes the current database connection in order
	 * for the {@link DBConnector} to clean up.
	 */
	public void finalize() throws DataException
	{
		dbConnector.compact(con);
		createGexIndices();
		dbConnector.closeConnection(con, DBConnector.PROP_FINALIZE);
		//The dbConnector may change the database file after cleaning up,
		//for example, the derby connector first creates the database as directory
		//and then adds the database to a zip file and removes the directory.
		//The database name needs to be changed to the zip file in this case.
		String newDb = dbConnector.finalizeNewDatabase(dbName);
		setDbName(newDb);
	}
	
	/**
	   commit inserted data
	 */
	public void commit() throws DataException
	{
		try
		{
			con.commit();
		}
		catch (SQLException e)
		{
			throw new DataException (e);
		}
	}

	
	PreparedStatement pstRow = null;
	
	// lazy instantiation of pstRow
	private PreparedStatement getPstRow() throws SQLException
	{
		if (pstRow == null)
		{
			pstRow = con.prepareStatement (
					"SELECT id, code, ensId, idSample, data, groupId " +
					"FROM expression " +
					"WHERE groupId = ?");
		}
		return pstRow;
	}
	
	public Data getRow(int rowId) throws DataException
	{
		try
		{
			Data result;
			PreparedStatement ps = getPstRow();
			ps.setInt(1, rowId);
			ResultSet rs = ps.executeQuery();
			
			Xref ref = null;			
			result = new Data (null, rowId);
			
			while (rs.next())
			{
				if (ref == null)
				{
					//TODO: this redundancy in ref is not normalized
					ref = new Xref (rs.getString(1), DataSource.getBySystemCode(rs.getString(2)));
					result.setXref(ref);
				}
				
				int sample = rs.getInt(4);
				String value = rs.getString (5);
				result.setSampleData(sample, value);
			}
			
			return result;
		}
		catch (SQLException e)
		{
			throw new DataException (e);
		}
	}
	
	public int getMaxRow() throws DataException
	{
		try
		{
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT MAX(groupId) FROM expression");
			rs.next();
			return rs.getInt(1);
		}
		catch (SQLException e)
		{
			throw new DataException (e);
		}		
	}
}