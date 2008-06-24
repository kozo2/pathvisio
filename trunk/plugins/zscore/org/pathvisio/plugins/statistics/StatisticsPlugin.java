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
package org.pathvisio.plugins.statistics;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingworker.SwingWorker;
import org.pathvisio.Engine;
import org.pathvisio.data.Gdb;
import org.pathvisio.data.GexManager;
import org.pathvisio.data.SimpleGex;
import org.pathvisio.data.CachedData.Data;
import org.pathvisio.debug.Logger;
import org.pathvisio.gui.swing.SwingEngine;
import org.pathvisio.model.Xref;
import org.pathvisio.plugin.Plugin;
import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.util.FileUtils;
import org.pathvisio.util.PathwayParser;
import org.pathvisio.util.ProgressKeeper;
import org.pathvisio.util.Stats;
import org.pathvisio.util.PathwayParser.ParseException;
import org.pathvisio.util.swing.ListWithPropertiesTableModel;
import org.pathvisio.util.swing.MatchResult;
import org.pathvisio.util.swing.PropertyColumn;
import org.pathvisio.util.swing.RowWithProperties;
import org.pathvisio.visualization.colorset.Criterion;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StatisticsPlugin implements Plugin 
{
	/**
	 * Plugin initialization method, registers statistics action in the Data menu
	 */
	public void init() 
	{
		StatisticsAction statisticsAction = new StatisticsAction();

		Logger.log.info ("Initializing statistics plugin");
		SwingEngine.getCurrent().registerMenuAction ("Data", statisticsAction);
	}
	
	/**
	 * Statistics action
	 */
	private static class StatisticsAction extends AbstractAction 
	{
		private static final long serialVersionUID = 1L;

		public StatisticsAction() 
		{
			super();
			putValue(NAME, "Statistics...");
			putValue(SHORT_DESCRIPTION, "Do simple pathway statistics");
		}

		public void actionPerformed(ActionEvent e) 
		{
			SimpleGex gex = GexManager.getCurrent().getCurrentGex();
			if (gex == null)
			{
				JOptionPane.showMessageDialog(SwingEngine.getCurrent().getFrame(), "Select an expression dataset first");
			}
			else
			{
				StatisticsDlg dlg = new StatisticsDlg();
				dlg.createAndShowDlg();
			}
		}
	}

	/**
	 * Dialog to let the user set parameters, start calculation and view results.
	 */
	private static class StatisticsDlg
	{
		Criterion myCriterion = new Criterion();
		JTextField txtExpr;
		JLabel lblError;
		
		private void updateCriterion()
		{
			boolean ok = myCriterion.setExpression(txtExpr.getText());
			if (!ok)
			{
				lblError.setText(myCriterion.getParseException().getMessage());
			}
			else
			{
				lblError.setText ("OK");
			}
		}
		
		/**
		 * Pop up the statistics dialog
		 */
		private void createAndShowDlg()
		{
			
			final JFrame frame = new JFrame ("Pathway statistics");
			
			FormLayout layout = new FormLayout (
					"4dlu, pref:grow, 4dlu, pref, 4dlu", 
					"4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, fill:pref:grow");
			frame.setLayout(layout);
			
			CellConstraints cc = new CellConstraints();
			
			frame.add (new JLabel ("Expression: "), cc.xy(2,2));
			txtExpr = new JTextField(40);
			txtExpr.getDocument().addDocumentListener(new DocumentListener() 
			{

				public void changedUpdate(DocumentEvent e) 
				{
					updateCriterion();
				}

				public void insertUpdate(DocumentEvent e) 
				{
					updateCriterion();
				}

				public void removeUpdate(DocumentEvent e) 
				{
					updateCriterion();					
				}
			});
			
			frame.add (txtExpr, cc.xyw(2,4,3));
			
	
			final JList lstOperators = new JList(Criterion.tokens);
			frame.add (new JScrollPane (lstOperators), cc.xy (2,6));
			
			lstOperators.addMouseListener(new MouseAdapter() 
			{
				public void mouseClicked(MouseEvent me) 
				{
					int selectedIndex = lstOperators.getSelectedIndex();
					if (selectedIndex >= 0)
					{
						String expr = txtExpr.getText();
						txtExpr.setText (expr + " " + Criterion.tokens[selectedIndex]);
					}
					txtExpr.requestFocus();
				}
			} );
			
			SimpleGex gex = GexManager.getCurrent().getCurrentGex();
			final List<String> sampleNames = gex.getSampleNames();
			final JList lstSamples = new JList(sampleNames.toArray());
	
			lstSamples.addMouseListener(new MouseAdapter() 
			{
				public void mouseClicked(MouseEvent me) 
				{
					int selectedIndex = lstSamples.getSelectedIndex();
					if (selectedIndex >= 0)
					{
						String expr = txtExpr.getText();
						txtExpr.setText (expr + " [" + sampleNames.get(selectedIndex) + "]");
					}
					txtExpr.requestFocus();
				}
			} );
	
			frame.add (new JScrollPane (lstSamples), cc.xy (4,6));
			lblError = new JLabel("OK");
			frame.add (lblError, cc.xyw (2,8,3));
			frame.add (new JLabel ("Pathway Directory: "), cc.xy (2,10));
			final JTextField txtDir = new JTextField(40);
			txtDir.setText(Engine.getCurrent().getPreferences().get(GlobalPreference.DIR_PWFILES));
			frame.add (txtDir, cc.xy(2,12));
			JButton btnDir = new JButton("Browse");
			btnDir.addActionListener(new ActionListener ()
			{
	
				public void actionPerformed(ActionEvent ae) 
				{
					JFileChooser jfc = new JFileChooser();
					jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					jfc.setCurrentDirectory(new File (txtDir.getText()));
					if (jfc.showDialog(null, "Choose") == JFileChooser.APPROVE_OPTION)
					{
						txtDir.setText("" + jfc.getSelectedFile());
					}
				}
			});
			frame.add (btnDir, cc.xy (4,12));
			JButton btnCalc = new JButton ("Calculate");
			frame.add (btnCalc, cc.xyw (2,14,3, "center, top"));
			
			final JTable tblResult = new JTable ();
			tblResult.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent me) 
				{
					int row = tblResult.getSelectedRow();
					final StatisticsResult sr = ((StatisticsTableModel)(tblResult.getModel())).getRow(row);
						
						//TODO: here I want to use SwingEngine.openPathway, but I need to 
						// be able to wait until the process is finished!
					SwingEngine.getCurrent().openPathway(sr.getFile());
				}

			});

			frame.add (new JScrollPane (tblResult), cc.xyw (2,16,3));
			
			btnCalc.addActionListener(new ActionListener (){
	
				public void actionPerformed(ActionEvent ae) 
				{
					File pwDir = new File (txtDir.getText());
					String expr = txtExpr.getText();
					
					//TODO: run in separate thread
					doCalculate (pwDir, myCriterion, tblResult, frame);
					
				}
			});
	
			txtExpr.requestFocus();
			frame.pack();
			frame.setLocationRelativeTo(SwingEngine.getCurrent().getFrame());
			frame.setVisible(true);
		}

	}
	
	/**
	 * asynchronous statistics calculation function
	 */
	private static void doCalculate(final File pwDir, final Criterion crit, final JTable resultTable, JFrame parentFrame)
	{
		final int TOTALWORK = 1000;

		final ProgressMonitor pmon = new ProgressMonitor(
				parentFrame, "Pathway search", "searching pathways...",
				0, TOTALWORK);
		
		final StatisticsTableModel stm = new StatisticsTableModel();
		stm.setColumns(new Column[] {Column.PATHWAY_NAME, Column.R, Column.N, Column.TOTAL, Column.PCT, Column.ZSCORE});
		resultTable.setModel (stm);

		SwingWorker<Boolean, StatisticsResult> worker = new SwingWorker<Boolean, StatisticsResult>() 
		{
			@Override
			protected Boolean doInBackground()
			{
				pmon.setProgress (0);
				pmon.setNote("Analyzing data");
				SimpleGex gex = GexManager.getCurrent().getCurrentGex();
				
				// first we calculate N and R
				
				int N = 0;
				int R = 0;
				try
				{
					int maxRow = gex.getMaxRow();
					for (int i = 0; i < maxRow; ++i)
					{
						if (pmon.isCanceled()) return false;
						Data d = gex.getRow(i);
						N++;
						boolean result = crit.evaluate(d.getSampleData());
						if (result)
						{
							R++;
						}		
//						Logger.log.trace ("Row " + i +  " (" + d.getXref() + ") = " + result);
						pmon.setProgress ((int)(0.2 * (double)i / (double)maxRow * (double)TOTALWORK));
					}
				}
				catch (Exception e)
				{
					Logger.log.error ("Problem during calculation of R/N ", e);
					//TODO: better error handling
				}

				Logger.log.info ("N: " + N + ", R: " + R);
				
				// now we calculate n and r for each pwy				
				List<File> files = FileUtils.getFiles(pwDir, "gpml", true);
				
				XMLReader xmlReader = null;
				
				try
				{
					xmlReader = XMLReaderFactory.createXMLReader();
				}
				catch (SAXException e)
				{
					Logger.log.error("Problem while searching pathways", e);
					return false;
				}
		
				int i = 0;

				pmon.setNote ("Analyzing pathways");

				for (File file : files)
				{
					try
					{
						PathwayParser pwyParser = new PathwayParser(file, xmlReader);
						
						// Step 1: map the genes in the pathway to a common system.
						// This common system can be Ensembl, but it doesn't have to be.
						// as long as the same single common system is used for all pathways
						// Make the list unique. This is our "n".
						
						Gdb gdb = SwingEngine.getCurrent().getGdbManager().getCurrentGdb();
						
						Logger.log.info ("Calculating statistics for " + pwyParser.getName());
						
						List <Xref> genes = new ArrayList<Xref>();
						genes.addAll (pwyParser.getGenes());
						Map <String, List<Data>> ensGenes = new HashMap <String, List<Data>> ();
						
						gex.cacheData(genes, new ProgressKeeper(1000), gdb);

						for (Xref ref : genes)
						{
							List<Data> datas  = gex.getCachedData().getData(ref);
							
							for (String ensId : gdb.ref2EnsIds(ref))
							{
								if (pmon.isCanceled()) return false;
								Logger.log.info ("Mapping: " + ensId);
								ensGenes.put (ensId, datas);								
							}
						}
						
						int cPwyTotal = ensGenes.size();
						int cPwyMeasured = 0;
						// Step 2: find the corresponding rows in the Gex. There could be more than one row per Ensembl gene, this is ok.
						
						
						double cPwyPositive = 0;
						
						for (String ensGene : ensGenes.keySet())
						{
							if (pmon.isCanceled()) return false;
							List<Data> datas = ensGenes.get (ensGene);
							
							if (datas != null)
							{
								int cGeneTotal = datas.size();
								if (cGeneTotal > 0) { cPwyMeasured++; }
								int cGenePositive = 0;
								
								for (Data data : datas)
								{
									if (pmon.isCanceled()) return false;
									Logger.log.info ("Data found: " + data.getXref() + ", for sample 1: " + data.getSampleData(1));
									try
									{	
										boolean result = crit.evaluate(data.getSampleData());
										if (result) cGenePositive++;
									}
									catch (Exception e)
									{
										Logger.log.error ("Unknown error during statistics", e);
									}
								}
							
								// Step 4: Map the rows back to the corresponding genes. "yes" is counted, weighed by the # of rows per gene. This is our "r".
								
//								cPwyPositive += (double)cGenePositive / (double)cGeneTotal;
								if (cGenePositive > 0) cPwyPositive += 1;
							}
						}
						
						double z = Stats.zscore (cPwyMeasured, cPwyPositive, N, R);						
						
						StatisticsResult sr = new StatisticsResult (file, pwyParser.getName(), cPwyMeasured, (int)Math.round (cPwyPositive), cPwyTotal, z);
						publish (sr);
					}
					catch (ParseException pe)
					{
						Logger.log.warn ("Could not parse " + file + ", ignoring");
					}
					i++;
					pmon.setProgress((int)((0.2 + (0.8 * (double)i / (double)files.size())) * (double)TOTALWORK));				
				}
				stm.saveData();
				pmon.close();
				return true;
			}
			
			@Override
			protected void process (List<StatisticsResult> srs)
			{
				for (StatisticsResult sr : srs)
				{
					stm.addRow (sr);
					stm.sort();
					stm.packRows(resultTable, 2);
				}
			}
		};
		
		worker.execute();
		
	}
	
	/**
	 * Statistics calculation for a single pathway,
	 * to be shown as a row in the statistics result table
	 */
	private static class StatisticsResult implements RowWithProperties<Column> 
	{
		private int r = 0;
		private int n = 0;
		private int total = 0;
		private String name;
		private double z = 0;
		private File f;

		File getFile() { return f; }
		
		StatisticsResult (File f, String name, int n, int r, int total, double z)
		{
			this.f = f;
			this.r = r;
			this.n = n;
			this.total = total;
			this.name = name;
			this.z = z;
		}

		public String getProperty(Column prop) 
		{
			switch (prop)
			{
			case N: return "" + n;
			case R: return "" + r;
			case TOTAL: return "" + total;
			case PATHWAY_NAME: return name;
			case PVAL: return "0.01"; //TODO
			case ZSCORE: return String.format ("%3.2f", (float)z);
			case PCT: return String.format("%3.2f%%", (n == 0 ? Float.NaN : 100.0 * (float)r / (float)n));
			default : throw new IllegalArgumentException("Unknown property");
			}
		}
	}

	/**
	 * Table Model for showing statistics results 
	 */
	private static class StatisticsTableModel extends ListWithPropertiesTableModel<StatisticsPlugin.Column, StatisticsResult>
	{
		private static final long serialVersionUID = 1L;
		
		public void saveData()
		{
			for (StatisticsResult sr : rows)
			{
				System.out.println (sr.name + "\t" + sr.r + "\t" + sr.n + "\t" + sr.z);  
			}
		}
		
		/**
		 * Sort results on z-score
		 */
		public void sort()
		{
			Collections.sort (rows, new Comparator<StatisticsResult>()
			{

				public int compare(StatisticsResult arg0, StatisticsResult arg1) 
				{
					if (arg0.z > arg1.z)
					{
						return -1;
					}
					else if (arg0.z < arg1.z)
					{
						return 1;
					}
					else
					{
						return 0;
					}
				}
			});
			fireTableDataChanged();
		}
		
		/**
	    // Returns the preferred height of a row.
	    // The result is equal to the tallest cell in the row.
	     */
	    public int getPreferredRowHeight(JTable table, int rowIndex, int margin) {
	        // Get the current default height for all rows
	        int height = table.getRowHeight();
	    
	        // Determine highest cell in the row
	        for (int c=0; c<table.getColumnCount(); c++) {
	            TableCellRenderer renderer = table.getCellRenderer(rowIndex, c);
	            Component comp = table.prepareRenderer(renderer, rowIndex, c);
	            int h = comp.getPreferredSize().height + 2*margin;
	            height = Math.max(height, h);
	        }
	        return height;
	    }
	    
	    /**
	     * The height of each row is set to the preferred height of the
	     * tallest cell in that row.
	     */
	    public void packRows(JTable table, int margin) 
	    {
	        packRows(table, 0, table.getRowCount(), margin);
	    }
	    
	    /**
	    // For each row >= start and < end, the height of a
	    // row is set to the preferred height of the tallest cell
	    // in that row.
	     */
	    public void packRows(JTable table, int start, int end, int margin) {
	        for (int r=0; r<table.getRowCount(); r++) {
	            // Get the preferred height
	            int h = getPreferredRowHeight(table, r, margin);
	    
	            // Now set the row height using the preferred height
	            if (table.getRowHeight(r) != h) {
	                table.setRowHeight(r, h);
	            }
	        }
	    }

	}

	/**
	 * Enum for possible columns in the statistics result table
	 */
	private static enum Column implements PropertyColumn
	{
		N("measured (n)"),
		R("positive (r)"),
		TOTAL("total"),
		PATHWAY_NAME("Pathway"),
		PCT("%"),
		PVAL("pval"),
		ZSCORE ("Z Score");
		
		private String title;
		
		private Column(String title)
		{
			this.title = title;
		}
		
		public String getTitle() 
		{
			return title;
		}
	}
	
}