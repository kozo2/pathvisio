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
package org.pathvisio.gui.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.pathvisio.Engine;
import org.pathvisio.Globals;
import org.pathvisio.data.DataException;
import org.pathvisio.data.GdbManager;
import org.pathvisio.data.GexManager;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.BatikImageExporter;
import org.pathvisio.model.DataNodeListExporter;
import org.pathvisio.model.EUGeneExporter;
import org.pathvisio.model.GpmlFormat;
import org.pathvisio.model.ImageExporter;
import org.pathvisio.model.MappFormat;
import org.pathvisio.plugin.PluginManager;
import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.view.MIMShapes;

/**
 * Main class for the Swing GUI. This class creates and shows the GUI.
 * Subclasses may override {@link #createAndShowGUI(MainPanel)} to perform custom
 * actions before showing the GUI.
 * @author thomas
 *
 */
public class GuiMain
{
	protected MainPanelStandalone mainPanel;
	
	private static void initLog()
	{
		String logDest = Engine.getCurrent().getPreferences().get(GlobalPreference.FILE_LOG);
		Logger.log.setDest (logDest);		
		Logger.log.setLogLevel(true, true, true, true, true, true);//Modify this to adjust log level
	}

	// plugin files specified at command line
	private List<String> pluginLocations = new ArrayList<String>();
	
	// pathway specified at command line
	private URL pathwayUrl = null;
	private String pgexFile = null;

	public void parseArgs(String [] args)
	{
		
		for(int i = 0; i < args.length - 1; i++) 
		{
			if("-p".equals(args[i])) 
			{
				pluginLocations.add(args[i + 1]);
				i++;
			}
			else if ("-d".equals(args[i]))
			{
				pgexFile = args[i + 1];
				if (!new File(pgexFile).exists())
				{
					System.out.println ("Data file '" + pgexFile + "' not found"); 
					printHelp();
					System.exit(-1);
				}
				i++;
			}
			else if ("-o".equals(args[i])) 
			{
				String pws = args[i + 1];
				try {
					File f = new File(pws);
					//Assume the argument is a file
					if(f.exists()) {
						pathwayUrl = f.toURI().toURL();
					//If it doesn't exist, assume it's an url
					} else {
						pathwayUrl = new URL(pws);
					}
				} catch(MalformedURLException e) 
				{
					System.out.println ("Pathway '" + args[i] + "' not a valid file or URL"); 
					printHelp();
					System.exit(-1);
				}
				i++;
			}
			else
			{
				Logger.log.warn("Unable to parse argument: " + args[i]);
			}
		}
	}
	
	/**
	 * Act upon the command line arguments
	 */
	public void processOptions()
	{
		SwingEngine swingEngine = SwingEngine.getCurrent();
		
		//Create a plugin manager that loads the plugins
		if(pluginLocations.size() > 0) {
			PluginManager pluginManager = new PluginManager(
					pluginLocations.toArray(new String[0])
			);
		}
		
		if(pathwayUrl != null) {
			swingEngine.openPathway(pathwayUrl);
		}
	
		if (pgexFile != null)
		{
			try
			{
				
				GexManager.getCurrent().setCurrentGex(pgexFile, false);
				swingEngine.loadGexCache();
				Logger.log.info ("Loaded pgex " + pgexFile);
			}
			catch (DataException e)
			{
				Logger.log.error ("Couldn't open pgex " + pgexFile, e);
			}
		}
	}
	
	/**
	 * Creates and shows the GUI. Creates and shows the Frame, sets the size, title and menubar.
	 * @param mainPanel The main panel to show in the frame
	 */
	protected JFrame createAndShowGUI(MainPanelStandalone mainPanel) 
	{
		//Create and set up the window.
		JFrame frame = new JFrame(Globals.APPLICATION_NAME);
		// dispose on close, otherwise windowClosed event is not called.
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.add(mainPanel);
		frame.setJMenuBar(mainPanel.getMenuBar());
		try {
		    UIManager.setLookAndFeel(
		        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			Logger.log.error("Unable to load native look and feel", ex);
		}
		frame.pack();
		PreferenceManager preferences = Engine.getCurrent().getPreferences();
		frame.setSize(preferences.getInt(GlobalPreference.WIN_W), preferences.getInt(GlobalPreference.WIN_H));
		frame.setLocation(preferences.getInt(GlobalPreference.WIN_X), preferences.getInt(GlobalPreference.WIN_Y));
		
		frame.addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent we)
			{
				PreferenceManager prefs = Engine.getCurrent().getPreferences();
				JFrame frame = SwingEngine.getCurrent().getFrame();
				Dimension size = frame.getSize();
				Point p = frame.getLocationOnScreen();
				prefs.setInt(GlobalPreference.WIN_W, size.width);
				prefs.setInt(GlobalPreference.WIN_H, size.height);
				prefs.setInt(GlobalPreference.WIN_X, p.x);
				prefs.setInt(GlobalPreference.WIN_Y, p.y);				
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) 
			{
				GuiMain.this.shutdown();
			}
		});
		
		//Display the window.
		frame.setVisible(true);

		int spPercent = Engine.getCurrent().getPreferences().getInt (GlobalPreference.GUI_SIDEPANEL_SIZE);
		double spSize = (100 - spPercent) / 100.0;
		mainPanel.getSplitPane().setDividerLocation(spSize);
		
		return frame;
	}

	private void shutdown() 
	{
		PreferenceManager prefs = Engine.getCurrent().getPreferences();
		prefs.store();
	}
	
	public MainPanel getMainPanel() { return mainPanel; }
	
	
	static void printHelp() {
		System.out.println(
				"Command line parameters:\n" +
				"-o: A GPML file to open\n" +
				"-p: A plugin file/directory to load\n" +
				"-d: A pgex data file to load\n"
		);
	}
	
	
	public static void main(String[] args) {
		final GuiMain gui = new GuiMain();
		gui.parseArgs (args);
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() 
		{
			
			public void run() {
				Engine.init();
				initLog();
				Engine engine = Engine.getCurrent();
				engine.setApplicationName("PathVisio (experimental)");
				SwingEngine.init(engine);
				SwingEngine swingEngine = SwingEngine.getCurrent();
				swingEngine.getGdbManager().initPreferred();
				MainPanelStandalone mps = new MainPanelStandalone();
				JFrame frame = gui.createAndShowGUI(mps);
				initImporters(engine);
				initExporters(engine, swingEngine.getGdbManager());
				MIMShapes.registerShapes();
				swingEngine.setFrame(frame);
				swingEngine.setApplicationPanel(mps);
				gui.processOptions();

			}
		});
	}
	
	private static void initImporters(Engine engine) 
	{
		engine.addPathwayImporter(new MappFormat());
		engine.addPathwayImporter(new GpmlFormat());
	}
	
	private static void initExporters(Engine engine, GdbManager gdbManager) 
	{
		engine.addPathwayExporter(new MappFormat());
		engine.addPathwayExporter(new GpmlFormat());
		engine.addPathwayExporter(new BatikImageExporter(ImageExporter.TYPE_SVG));
		engine.addPathwayExporter(new BatikImageExporter(ImageExporter.TYPE_PNG));
		engine.addPathwayExporter(new BatikImageExporter(ImageExporter.TYPE_TIFF));
		engine.addPathwayExporter(new BatikImageExporter(ImageExporter.TYPE_PDF));	
		engine.addPathwayExporter(new DataNodeListExporter(gdbManager));
		engine.addPathwayExporter(new EUGeneExporter());
	}
	
}
