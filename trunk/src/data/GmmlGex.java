package data;

import graphics.GmmlGeneProduct;
import graphics.GmmlGpColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.graphics.RGB;

import colorSet.*;

import data.GmmlDb.ConvertThread;

public class GmmlGex {
	public Connection con;
	Connection conGmGex;
	public File gexFile;
	public File gmGexFile;
	public GmmlGdb gmmlGdb;
	public Vector colorSets;
	
	ConvertThread convertThread;
	
	public GmmlGex(GmmlGdb gmmlGdb) {
		this.gmmlGdb = gmmlGdb;
		colorSets = new Vector();
	}
	
	public void setColorSets(Vector colorSets)
	{
		this.colorSets = colorSets;
	}
	
	public String[] getColorSetNames()
	{
		String[] colorSetNames = new String[colorSets.size()];
		for(int i = 0; i < colorSetNames.length; i++)
		{
			colorSetNames[i] = ((GmmlColorSet)colorSets.get(i)).name;
		}
		return colorSetNames;
	}
	
	public void setGexReadOnly(boolean readonly)
	{
		boolean reconnect = false;
		
		long t = System.currentTimeMillis();
		
		if(con != null)
		{
			System.out.println("reconnecting");
			reconnect = true;
			close(false);
		}
		
		System.out.println(System.currentTimeMillis() - t);
		
		t = System.currentTimeMillis();
		
		Properties gexProp = new Properties();
		try {
		gexProp.load(new FileInputStream(gexFile));
		gexProp.setProperty("readonly", Boolean.toString(readonly));
		gexProp.store(new FileOutputStream(gexFile), "HSQL Database Engine");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		t = System.currentTimeMillis();
		System.out.println(System.currentTimeMillis() - t);
		
		t = System.currentTimeMillis();
		
		if(reconnect)
		{
			connect();
		}
		System.out.println(System.currentTimeMillis() - t);
	}
	
	public void saveColorSets()
	{
		try
		{
			setGexReadOnly(false);
			Statement s = con.createStatement();
			s.execute("DELETE FROM colorSets");
			s.execute("DELETE FROM colorSetObjects");
			
			PreparedStatement sCs = con.prepareStatement(
					"INSERT INTO colorSets	" +
					"( colorSetId, name ) VALUES	" +
					"( ?, ? )"	);
			PreparedStatement sCso = con.prepareStatement(
					"INSERT INTO colorSetObjects 	" +
					"( 	name, colorSetId,		" +
					"	criterion	) VALUES		" +
					"(	?, ?, ?	)"	);
			
			for(int i = 0; i < colorSets.size(); i++)
			{
				GmmlColorSet cs = (GmmlColorSet)colorSets.get(i);
				sCs.setInt(1, i);
				sCs.setString(2, cs.name);
				sCs.execute();
				Vector colorSetObjects = cs.getColorSetObjects();
				for(int j = 0; j < colorSetObjects.size(); j++)
				{
					GmmlColorSetObject cso = (GmmlColorSetObject)colorSetObjects.get(j);
					sCso.setString(1, cso.name);
					sCso.setInt(2, i);
					sCso.setString(3, cso.getCriterionString());
					sCso.execute();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		setGexReadOnly(true);
		
	}
	
	public void loadColorSets()
	{
		try
		{
			colorSets = new Vector();
			Statement sCso = con.createStatement();
			ResultSet r = con.createStatement().executeQuery(
				"SELECT * FROM colorSets ORDER BY colorSetId" );
			while(r.next())
			{
				GmmlColorSet cs = new GmmlColorSet(r.getString(2));
				colorSets.add(cs);
				ResultSet rCso = sCso.executeQuery(
						"SELECT * FROM colorSetObjects" +
						" WHERE colorSetId = " + r.getInt(1) +
						" ORDER BY id");
				while(rCso.next())
				{
					String name = rCso.getString(2);
					String criterion = rCso.getString(4);
					if(criterion.contains("GRADIENT"))
					{
						GmmlColorSetObject co = new GmmlColorGradient(cs, name, criterion);
						cs.addObject(co);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public HashMap<Integer, Sample> samples;
	public void setSamples()
	{
		samples = new HashMap<Integer, Sample>();
		try {
			ResultSet r = con.createStatement().executeQuery(
					"SELECT idSample, name, dataType FROM samples"
					);
			
			while(r.next())
			{
				int id = r.getInt(1);
				samples.put(id, new Sample(id, r.getString(2), r.getInt(3)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public class Sample implements Comparable
	{
		public int idSample;
		public String name;
		public int dataType;
		
		public Sample(int idSample, String name, int dataType)
		{
			this.idSample = idSample;
			this.name = name;
			this.dataType = dataType;
		}
		
		public int compareTo(Object o) throws ClassCastException
		{
			if(o instanceof Sample)
			{
				return idSample - ((Sample)o).idSample;
			} else {
				throw new ClassCastException("Object is not of type Sample");
			}
		}
	}
	
	public HashMap<String, RefData> data;
	public class RefData
	{
		String mappId;
		public HashMap<Integer, ArrayList> sampleData;
		HashMap<Integer, Sample> samples;
		
		public RefData(String mappId)
		{
			this.mappId = mappId;
			sampleData = new HashMap<Integer, ArrayList>();
		}
		
		public HashMap<Integer, Object> getAvgSampleData()
		{
			HashMap<Integer, Object> avgSampleData = new HashMap<Integer, Object>();
			for(int i : sampleData.keySet())
			{
				if(samples.get(i).dataType == Types.REAL)
				{
					avgSampleData.put(i, getAvgDouble(sampleData.get(i)));
				} else 
				{
					avgSampleData.put(i, getAvgString(sampleData.get(i)));
				}
			}
			return avgSampleData;
		}
		
		public double getAvgDouble(ArrayList<String[]> data)
		{
			double avg = 0;
			for(String[] d : data)
			{
				avg += Double.parseDouble(d[2]);
			}
			return avg / data.size();
		}
		
		public String getAvgString(ArrayList<String[]> data)
		{
			StringBuilder str = new StringBuilder("Multiple values: ");
			for(String[] d : data)
			{
				str.append(d[2] + ", ");
			}
			return str.toString();
		}
		
		public double getWeightedStd()
		{
			double stdWg = 0;
			int nDouble = 0;
			for(int i : sampleData.keySet())
			{
				if(samples.get(i).dataType == Types.REAL)
				{
					nDouble++;
					ArrayList<String[]> data = sampleData.get(i);
					double avg = getAvgDouble(data);
					double std = 0;
					for(String[] d : data)
					{
						std += Math.abs(avg - Double.parseDouble(d[2]));
					}
					stdWg += std / data.size();
				}
			}
			return stdWg / nDouble;
		}
		
		public boolean isAveraged()
		{
			if(sampleData.size() > 0)
			{
				return sampleData.get(sampleData.keySet().toArray()[0]).size() > 1;
			}
			return false;
		}
	}	
	
	public HashMap getId2EnsHash(ArrayList<String> ids)
	{
		HashMap<String, ArrayList> ensIds = new HashMap<String, ArrayList>();
		for(String id : ids)
		{
			ensIds.put(id, gmmlGdb.ref2EnsIds(id));
		}
		return ensIds;
	}
	
	public String getDataString(String id) {
		String exprInfo = "<P><B>" + id + "</B><TABLE border='1'>";
		if(con != null && gmmlGdb.con != null) {
			try 
			{				
//				More complicated query, slower (~150 seconds)
//				ArrayList refs = getCrossRefs(id);
				ArrayList ensIds = gmmlGdb.ref2EnsIds(id);
				for(int j = 0; j < ensIds.size(); j++)
				{
					ArrayList refs = gmmlGdb.ensId2Refs((String)ensIds.get(j));
					
//					StringBuilder ensString = new StringBuilder();
//					for(int i = 0; i < refs.size(); i++) {
//					ensString.append("'" + refs.get(i) + "', ");
//					}
					
					for(int i = 0; i < refs.size(); i++) {
//						More complicated query, slower (~10 seconds)
//						ResultSet r = conGex.createStatement().executeQuery(
//						"SELECT id, data, idSample FROM expression " +
//						"WHERE id IN " +
//						"( " + ensString.substring(0,ensString.lastIndexOf(", ")) + " )"
//						);
						ResultSet r = con.createStatement().executeQuery(
								"SELECT id, data, idSample FROM expression " +
								"WHERE id = '" + (String)refs.get(i) + "'"
						);
						
						while(r.next())
						{
							String data = r.getString(2);
							ResultSet rsn = con.createStatement().executeQuery(
									"SELECT name FROM samples" +
									" WHERE idSample = " + r.getInt(3));
							rsn.next();
							String sampleName = rsn.getString(1);
							exprInfo += "<TR><TH>" + sampleName +
							"<TH>" + data;	
						}
					}
					exprInfo += "</TABLE>";
					return exprInfo;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	
	public void cacheData(ArrayList<String> ids)
	{	
		if(samples == null)
		{
			setSamples();
		}
		
		data = new HashMap<String, RefData>();
		for(String id : ids)
		{			 
			RefData refData = new RefData(id);
			refData.samples = samples;
			data.put(id, refData);
			
			ArrayList<String> ensIds = gmmlGdb.ref2EnsIds(id);
			for(String ensId : ensIds)
			{				
				try {					
					ResultSet r = con.createStatement().executeQuery(
							"SELECT id, data, idSample FROM expression " +
							" WHERE ensId = '" + ensId + "'");

					while(r.next())
					{						 
						String[] data = new String[3];
						int idSample = r.getInt(3);
						if(refData.sampleData.containsKey(idSample))
						{
							data[0] = r.getString(1);
							data[1] = ensId;
							data[2] = r.getString(2);
							refData.sampleData.get(idSample).add(data);
						}
						else
						{
							ArrayList<String[]> d = new ArrayList<String[]>();
							data[0] = r.getString(1);
							data[1] = ensId;
							data[2] = r.getString(2);
							d.add(data);
							refData.sampleData.put(idSample, d);
						}						
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			if(cacheThread.isInterrupted)
			{
				return;
			}
			cacheThread.progress += 100.0 / ids.size();
		}
		cacheThread.progress = 100;
	}

	CacheThread cacheThread;
	public class CacheThread extends Thread
	{
		volatile double progress;
		volatile boolean isInterrupted;
		ArrayList<String> ids;
		
		public CacheThread(ArrayList<String> ids) 
		{
			this.ids = ids;
		}
		
		public void run()
		{
			progress = 0;
			isInterrupted = false;
			cacheData(ids);
		}
		
		public void interrupt()
		{
			isInterrupted = true;
		}
	}
	
	public ArrayList<String> mappIds;
	public IRunnableWithProgress cacheRunnable = new IRunnableWithProgress() {
		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Caching expression data",100);
			cacheThread = new CacheThread(mappIds);
			cacheThread.start();
			int prevProgress = 0;
			while(cacheThread.progress < 100) {
				if(monitor.isCanceled()) {
					cacheThread.interrupt();
					break;
				}
				if(prevProgress < (int)cacheThread.progress) {
					monitor.worked((int)cacheThread.progress - prevProgress);
					prevProgress = (int)cacheThread.progress;
				}
			}
			monitor.done();
		}
	};
	
	public class ConvertThread extends Thread
	{
		volatile double progress;
		volatile boolean isInterrupted;
		public ConvertThread() 
		{
			isInterrupted = false;
		}
		
		public void run()
		{
			progress = 0;
			convertGex();
		}
		
		public void interrupt()
		{
			isInterrupted = true;
		}
	}
	
	public IRunnableWithProgress convertRunnable = new IRunnableWithProgress() {
		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Converting Gene Expression Dataset",100);
			convertThread = new ConvertThread();
			convertThread.start();
			int prevProgress = 0;
			while(convertThread.progress < 100) {
				if(monitor.isCanceled()) {
					convertThread.interrupt();
					break;
				}
				if(prevProgress < (int)convertThread.progress) {
					monitor.worked((int)convertThread.progress - prevProgress);
					prevProgress = (int)convertThread.progress;
				}
			}
			monitor.done();
		}
	};
	
	public void convertGex()
	{
    	PrintWriter error = null;
	    try {
	        error = new PrintWriter(new FileWriter("convert_gex_error.txt"));
	    } catch(IOException ex) {
	        ex.printStackTrace();
	    }
	    
		connect(true);
		connectGmGex(gmGexFile);
		createTables();
		try {
			con.setAutoCommit(false);
			Statement s = conGmGex.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			PreparedStatement pstmtExpr = con.prepareStatement(
					"INSERT INTO expression			" +
					"	(id, code, ensId,			" + 
					"	 idSample, data)			" +
			"VALUES	(?, ?, ?, ?, ?)			");
			
			ResultSet r = s.executeQuery("SELECT * FROM Expression");
			r.last();
			int nrRows = r.getRow();
			r.beforeFirst();
			
			ResultSetMetaData rsmd = r.getMetaData();
			// Column 4 to 2 before last contain expression data
			int nCols = rsmd.getColumnCount();
			ArrayList dataTypes = new ArrayList();
			for(int i = 4; i < nCols - 1; i++) 
			{
				int dataType = rsmd.getColumnType(i);
				dataTypes.add(dataType);
				String sampleName = rsmd.getColumnName(i);
				// Add new sample
				con.createStatement().execute("INSERT INTO SAMPLES" +
						"	(idSample, name, dataType)" + 
						"VALUES ( " + (i - 4) + ",'" + sampleName + "', " + dataType + " )");
			}

			int nq = 0;
			String id = "";
			String code = "";
			while(r.next()) {
				if(convertThread.isInterrupted) 
				{
					closeGmGex();
					close();
					return;
				}
				
				id = r.getString(2);
				code = r.getString(3);
				ArrayList<String> ensIds = gmmlGdb.ref2EnsIds(id);
				
				if(ensIds.size() == 0)
				{
					error.println(id + "\tGene not found in gene database");
				}
				else
				{				
					ArrayList<String> data = new ArrayList<String>();
					for(int i = 4; i < nCols - 1; i++) {
							data.add(r.getString(i));
					}
					for( String ensId : ensIds)
					{
						int i = 0;
						for(String str : data)
						{
							try {
									pstmtExpr.setString(1,id);
									pstmtExpr.setString(2,code);
									pstmtExpr.setString(3, ensId);
									pstmtExpr.setInt(4,i);
									pstmtExpr.setString(5,str);
									pstmtExpr.execute();
							} catch (Exception e) {
								error.println(id + ", " + code + ", " + i + "\t" + e.getMessage());
							}
							i++;
						}
					}
				}
				nq++;
				if(nq % 1000 == 0)
					con.commit();
				convertThread.progress += 100.0/nrRows;
			}
			con.commit();	
		} catch(Exception e) {
			error.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		closeGmGex();
		close();
		
		setGexReadOnly(true);
		
		convertThread.progress = 100;
	}
	
	public void connect(boolean clean)
	{
		if(clean)
		{
			//remove old property file
			File gexPropFile = gexFile;
			gexPropFile.delete();
			connect();
		}
		else
		{
			connect();
		}
	}
	public void connect()
	{
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			Properties prop = new Properties();
			prop.setProperty("user","sa");
			prop.setProperty("password","");
			//prop.setProperty("hsqldb.default_table_type","cached");
			String file = gexFile.getAbsolutePath().toString();
			con = DriverManager.getConnection("jdbc:hsqldb:file:" + 
					file.substring(0,file.lastIndexOf(".")) + ";shutdown=true", prop);
		} catch(Exception e) {
			System.out.println ("Error: " +e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void close(boolean shutdown)
	{
		if(con != null)
		{
			try
			{
				Statement sh = con.createStatement();
				if(shutdown) {
					sh.executeQuery("SHUTDOWN"); // required, to write last changes
				}
				sh.close();
				con = null;
			} catch (Exception e) {
				System.out.println ("Error: " +e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void close()
	{
		close(true);
	}
	
	public void connectGmGex(File gmGexFile) {
		String database_after = ";DriverID=22;READONLY=true";
		String database_before =
			"jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			conGmGex = DriverManager.getConnection(
					database_before + gmGexFile.toString() + database_after, "", "");
		} catch (Exception e) {
			System.out.println ("Error: " +e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void closeGmGex() {
		if(conGmGex != null)
		{
			try {
				conGmGex.close();
				conGmGex = null;
			} catch (Exception e) {
				System.out.println ("Error: " +e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void createTables() {	
		try {
			con.setReadOnly(false);
			Statement sh = con.createStatement();
			sh.execute("DROP TABLE samples IF EXISTS");
			sh.execute("DROP TABLE expression IF EXISTS");
			sh.execute("DROP TABLE colorSets IF EXISTS");
			sh.execute("DROP TABLE colorSetObjects IF EXISTS");
			sh.execute("DROP TABLE textdata IF EXISTS");
		} catch(Exception e) {
			System.out.println("Error: "+e.getMessage());
		}
		try
		{
			Statement sh = con.createStatement();
			sh.execute(
					"CREATE CACHED TABLE                    " +
					"		samples							" +
					" (   idSample INTEGER PRIMARY KEY,		" +
					"     name VARCHAR(50),					" +
					"	  dataType INTEGER					" +
			" )										");
			
			sh.execute(
					"CREATE CACHED TABLE					" +
					"		expression						" +
					" (   id VARCHAR(50),					" +
					"     code VARCHAR(50),					" +
					"	  ensId VARCHAR(50),				" +
					"     idSample INTEGER,					" +
					"     data REAL							" +
//					"     PRIMARY KEY (id, code, idSample, data)	" +
			" )										");
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
//			sh.execute(
//					"CREATE CACHED TABLE		" +
//					"		textdata			" +
//					"(	id VARCHAR(50),			" +
//					"	code VARCHAR(50),		" +
//					"   ensId VARCHAR(50),		" +	
//					"	idSample INTEGER,		" +
//					"	data VARCHAR(100)	)");
//			sh.execute(
//					"CREATE INDEX i_textdata_id " +
//					"ON textdata(id)			 ");
//			sh.execute(
//					"CREATE INDEX i_textdata_ensId " +
//					"ON textdata(ensId)			 ");
//			sh.execute(
//					"CREATE INDEX i_textdata_idSample " +
//					"ON textdata(idSample)	 ");
//			sh.execute(
//					"CREATE INDEX i_textdata_data " +
//					"ON textdata(data)	     ");
			sh.execute(
					"CREATE CACHED TABLE				" +
					"		colorSets					" +
					"(	colorSetId INTEGER PRIMARY KEY,	" +
					"	name VARCHAR(50)	)");
			sh.execute(
					"CREATE CACHED TABLE				" +
					"		colorSetObjects				" +
					"(	id INTEGER IDENTITY,			" +
					"	name VARCHAR(50),				" +
					"	colorSetId INTEGER,				" +
					"	criterion VARCHAR(100)			" +
					" )							");
		} catch (Exception e)
		{
			System.out.println ("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
