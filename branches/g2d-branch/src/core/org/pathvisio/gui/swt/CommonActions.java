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
package org.pathvisio.gui.swt;

import java.awt.Dimension;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.pathvisio.Engine;
import org.pathvisio.Globals;
import org.pathvisio.biopax.gui.BiopaxDialog;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayExporter;
import org.pathvisio.preferences.swt.PreferenceDlg;
import org.pathvisio.preferences.swt.SwtPreferences;
import org.pathvisio.preferences.swt.SwtPreferences.SwtPreference;
import org.pathvisio.util.SwtUtils.SimpleRunnableWithProgress;
import org.pathvisio.view.VPathway;
import org.pathvisio.view.swt.VPathwaySWT;

/**
   This class contains a large number of JFace Actions that are both in V1 and V2.
*/   
public class CommonActions 
{
	static class UndoAction extends Action
	{
		MainWindow window;
		public UndoAction (MainWindow w)
		{
			window = w;
			setText ("&Undo@Ctrl+Z");
			setToolTipText ("Undo last action");
		}
		public void run() 
		{
			if (Engine.getActivePathway() != null)
			{
				Engine.getActivePathway().undo();
			}
		}
	}
	
	/**
	 * {@link Action} to create a new gpml pathway
	 */
	static class NewAction extends Action 
	{
		MainWindow window;
		public NewAction (MainWindow w)
		{
			window = w;
			setText ("&New pathway@Ctrl+N");
			setToolTipText ("Create new pathway");
			setImageDescriptor(ImageDescriptor.createFromURL(
					Engine.getResourceURL("icons/new.gif")));
		}
		public void run () {
			if (Engine.getActivePathway() == null ||
				MessageDialog.openQuestion(window.getShell(), "Discard changes?",
						"Warning: This will discard any changes to " +
						"the current pathway. Are you sure?"))
			{
				SwtEngine.newPathway();
			}
		}
	}
	
	/**
	 * {@link Action} to create a new gpml pathway
	 */
	static class SvgExportAction extends Action 
	{
		MainWindow window;
		public SvgExportAction (MainWindow w)
		{
			window = w;
			setText ("Export to SVG");
			setToolTipText ("Export to Scalable Vector Graphics (SVG) " +
					"for publication-quality images");
		}
		public void run () 
		{
			VPathway drawing = Engine.getActiveVPathway();
			Pathway gmmlData = Engine.getActivePathway();
			// Check if a gpml pathway is loaded
			if (drawing != null)
			{
				FileDialog fd = new FileDialog(window.getShell(), SWT.SAVE);
				fd.setText("Save");
				fd.setFilterExtensions(new String[] {"*." + Engine.SVG_FILE_EXTENSION, "*.*"});
				fd.setFilterNames(new String[] {Engine.SVG_FILTER_NAME, "All files (*.*)"});
				
				File xmlFile = gmmlData.getSourceFile();
				if(xmlFile != null) {
					String name = xmlFile.getName();
					if (name.endsWith("." + Engine.PATHWAY_FILE_EXTENSION))
					{
						name = name.substring(0, name.length() - 
							Engine.PATHWAY_FILE_EXTENSION.length()) +
							Engine.SVG_FILE_EXTENSION;
					}
					fd.setFileName(name);
					fd.setFilterPath(xmlFile.getPath());
				} else {
					fd.setFileName(SwtPreference.SWT_DIR_PWFILES.getValue());
				}
				String fileName = fd.open();
				// Only proceed if user selected a file
				
				if(fileName == null) return;
				
				// Append .svg extension if not already present
				if(!fileName.endsWith("." + Engine.SVG_FILE_EXTENSION)) 
					fileName += "." + Engine.SVG_FILE_EXTENSION;
				
				File checkFile = new File(fileName);
				boolean confirmed = true;
				// If file exists, ask overwrite permission
				if(checkFile.exists())
				{
					confirmed = MessageDialog.openQuestion(window.getShell(),"",
					"File already exists, overwrite?");
				}
				if(confirmed)
				{
					try
					{
						gmmlData.writeToSvg(checkFile);
					}
					catch (ConverterException e)
					{
						String msg = "While writing svg to " 
							+ checkFile.getAbsolutePath();					
						MessageDialog.openError (window.getShell(), "Error", 
								"Error: " + msg + "\n\n" + 
								"See the error log for details.");
						Engine.log.error(msg, e);
					}
				}
			}
			else
			{
				MessageDialog.openError (window.getShell(), "Error", 
					"No pathway to save! Open or create a new pathway first");
			}			
		}
	}

	/**
	 * {@link Action} to open an gpml pathway
	 */
	static class OpenAction extends Action 
	{
		MainWindow window;
		public OpenAction (MainWindow w)
		{
			window = w;
			setText ("&Open pathway@Ctrl+O");
			setToolTipText ("Open pathway");
			setImageDescriptor(ImageDescriptor.createFromURL(Engine.getResourceURL("icons/open.gif")));
		}
		public void run () 
		{
			FileDialog fd = new FileDialog(window.getShell(), SWT.OPEN);
			fd.setText("Open");
			String pwpath = SwtPreference.SWT_DIR_PWFILES.getValue();
			fd.setFilterPath(pwpath);
			fd.setFilterExtensions(new String[] {"*." + Engine.PATHWAY_FILE_EXTENSION, "*.*"});
			fd.setFilterNames(new String[] {Engine.PATHWAY_FILTER_NAME, "All files (*.*)"});
	        String fnMapp = fd.open();
	        // Only open pathway if user selected a file
	        
	        if(fnMapp != null) { 
	        	SwtEngine.openPathway(fnMapp); 
	        }
		}
	}

	/**
	 * {@link Action} to open an gpml pathway
	 */
	static class ImportAction extends Action 
	{
		MainWindow window;
		public ImportAction (MainWindow w)
		{
			window = w;
			setText ("&Import");
			setToolTipText ("Import Pathway in GenMAPP format");
		}
		public void run () 
		{
			FileDialog fd = new FileDialog(window.getShell(), SWT.OPEN);
			fd.setText("Open");
			fd.setFilterPath(SwtPreference.SWT_DIR_PWFILES.getValue());
			fd.setFilterExtensions(new String[] {"*." + Engine.GENMAPP_FILE_EXTENSION, "*.*"});
			fd.setFilterNames(new String[] {Engine.GENMAPP_FILTER_NAME, "All files (*.*)"});
	        String fnMapp = fd.open();
	        // Only open pathway if user selected a file
	        
	        if(fnMapp != null) { 
	        	SwtEngine.openPathway(fnMapp); 
	        }
		}
	}

	/**
	 * {@link Action} to save a gpml pathway to a file specified by the user
	 */
	static class SaveAsAction extends Action 
	{
		MainWindow window;
		public SaveAsAction (MainWindow w)
		{
			window = w;
			setText ("Save pathway &As");
			setToolTipText ("Save pathway with new file name");
		}
		
		static public void do_run(MainWindow window)
		{
			VPathway drawing = Engine.getActiveVPathway();
			Pathway gmmlData = Engine.getActivePathway();
			// Check if a gpml pathway is loaded
			if (drawing != null)
			{
				FileDialog fd = new FileDialog(window.getShell(), SWT.SAVE);
				fd.setText("Save");
				fd.setFilterExtensions(new String[] {"*." + Engine.PATHWAY_FILE_EXTENSION, "*.*"});
				fd.setFilterNames(new String[] {Engine.PATHWAY_FILTER_NAME, "All files (*.*)"});
				
				File xmlFile = gmmlData.getSourceFile();
				if(xmlFile != null) {
					fd.setFileName(xmlFile.getName());
					fd.setFilterPath(xmlFile.getPath());
				} else {
					fd.setFilterPath(SwtPreference.SWT_DIR_PWFILES.getValue());
				}
				String fileName = fd.open();
				// Only proceed if user selected a file
				
				if(fileName == null) return;
				
				// Append .gpml extension if not already present
				if(!fileName.endsWith("." + Engine.PATHWAY_FILE_EXTENSION)) 
					fileName += "." + Engine.PATHWAY_FILE_EXTENSION;
				
				File checkFile = new File(fileName);
				boolean confirmed = true;
				// If file exists, ask overwrite permission
				if(checkFile.exists())
				{
					confirmed = MessageDialog.openQuestion(window.getShell(),"",
					"File already exists, overwrite?");
				}
				if(confirmed)
				{
					double usedZoom = drawing.getPctZoom();
					// Set zoom to 100%
					drawing.setPctZoom(100);					
					// Overwrite the existing xml file
					try
					{
						gmmlData.writeToXml(checkFile, true);
						// Set zoom back
						drawing.setPctZoom(usedZoom);
					}
					catch (ConverterException e)
					{
						String msg = "While writing xml to " 
							+ checkFile.getAbsolutePath();					
						MessageDialog.openError (window.getShell(), "Error", 
								"Error: " + msg + "\n\n" + 
								"See the error log for details.");
						Engine.log.error(msg, e);
					}
				}
			}
			else
			{
				MessageDialog.openError (window.getShell(), "Error", 
					"No gpml file loaded! Open or create a new gpml file first");
			}			
		}
		public void run () 
		{
			do_run(window);
		}
	}

	
	/**
	 * {@link Action} to save a gpml pathway to a file specified by the user
	 */
	static class ExportAction extends Action 
	{
		MainWindow window;
		public ExportAction (MainWindow w)
		{
			window = w;
			setText ("&Export");
			setToolTipText ("Export Pathway to GenMAPP format");
		}
		public void run () {
			VPathway drawing = Engine.getActiveVPathway();
			Pathway gmmlData = Engine.getActivePathway();
			// Check if a gpml pathway is loaded
			if (drawing != null)
			{
				FileDialog fd = new FileDialog(window.getShell(), SWT.SAVE);
				fd.setText("Export");
				
				class FileType implements Comparable<FileType> {
					final String name;
					final String ext;
					public FileType(String n, String e) { name = n; ext = e; }
					public int compareTo(FileType o) {
						return name.compareTo(o.name);
					}
				}
				
				ArrayList<FileType> fts = new ArrayList<FileType>();
				HashMap<String, PathwayExporter> exporters = Engine.getPathwayExporters();
								
				for(String ext : exporters.keySet()) {
					fts.add(new FileType(
								exporters.get(ext).getName() + " (*." + ext + ")",
								"*." + ext));
				}
				Collections.sort(fts);
				String[] exts = new String[fts.size()];
				String[] nms = new String[fts.size()];
				for(int i = 0; i < fts.size(); i++) {
					FileType ft = fts.get(i);
					exts[i] = ft.ext;
					nms[i] = ft.name;
				}
				fd.setFilterExtensions(exts);
				fd.setFilterNames(nms);
								
				File xmlFile = gmmlData.getSourceFile();
				if(xmlFile != null) {
					String name = xmlFile.getName();
					if (name.endsWith("." + Engine.PATHWAY_FILE_EXTENSION))
					{
						name = name.substring(0, name.length() - 
							Engine.PATHWAY_FILE_EXTENSION.length() - 1);
					}
					fd.setFileName(name);
					fd.setFilterPath(xmlFile.getPath());
				} else {
					fd.setFileName(SwtPreference.SWT_DIR_PWFILES.getValue());
				}
				String fileName = fd.open();
				// Only proceed if user selected a file
				if(fileName == null) return;
				
				int dot = fileName.lastIndexOf('.');
				String ext = Engine.GENMAPP_FILE_EXTENSION;
				if(dot >= 0) {
					ext = fileName.substring(dot + 1, fileName.length());
				}
				PathwayExporter exporter = Engine.getPathwayExporter(ext);
				
				if(exporter == null) 
					MessageDialog.openError (window.getShell(), "Error", 
					"No exporter for '" + ext +  "' files");
								
				File checkFile = new File(fileName);
				boolean confirmed = true;
				// If file exists, ask overwrite permission
				if(checkFile.exists())
				{
					confirmed = MessageDialog.openQuestion(window.getShell(),"",
					"File already exists, overwrite?");
				}
				if(confirmed)
				{
					try
					{
						//gmmlData.writeToMapp(checkFile);
						exporter.doExport(checkFile, gmmlData);
					}
					catch (ConverterException e)
					{
						String msg = "While exporting to " 
							+ checkFile.getAbsolutePath();					
						MessageDialog.openError (window.getShell(), "Error", 
								"Error: " + msg + "\n\n" + 
								"See the error log for details.");
						Engine.log.error(msg, e);
					}
				}
			}
			else
			{
				MessageDialog.openError (window.getShell(), "Error", 
					"No pathway to save! Open or create a new pathway first");
			}			
		}
	}

	/**
	 * {@link Action} to close the gpml pathway (does nothing yet)
	 */
	static class CloseAction extends Action 
	{
		MainWindow window;
		public CloseAction (MainWindow w)
		{
			window = w;
			setText ("&Close pathway@Ctrl+W");
			setToolTipText ("Close this pathway");
		}
		public void run () {
			//TODO: unload drawing, ask to save
		}
	}

	/**
	 * {@link Action} to exit the application
	 */
	static class ExitAction extends Action 
	{
		MainWindow window;
		public ExitAction (MainWindow w)
		{
			window = w;
			setText ("E&xit@Ctrl+X");
			setToolTipText ("Exit Application");
		}
		public void run () {
			window.close();
			//TODO: ask to save pathway if content is changed
		}
	}
	
	static class PreferencesAction extends Action
	{
		MainWindow window;
		public PreferencesAction (MainWindow w)
		{
			window = w;
			setText("&Preferences");
			setToolTipText("Edit preferences");
		}
		public void run () {
			PreferenceManager pg = new PreferenceDlg();
			PreferenceDialog pd = new PreferenceDialog(window.getShell(), pg);
			pd.setPreferenceStore((SwtPreferences)SwtEngine.getPreferences());
			pd.open();
		}
	}

	/**
	 * {@link Action} that zooms a mapp to the specified zoomfactor
	 */
	static class ZoomAction extends Action 
	{
		MainWindow window;
		int pctZoomFactor;
		
		/**
		 * Constructor for this class
		 * @param w {@link MainWindow} window this action belongs to
		 * @param newPctZoomFactor the zoom factor as percentage of original
		 */
		public ZoomAction (MainWindow w, int newPctZoomFactor)
		{
			window = w;
			pctZoomFactor = newPctZoomFactor;
			if(pctZoomFactor == MainWindow.ZOOM_TO_FIT) 
			{
				setText ("Zoom to fit");
				setToolTipText("Zoom mapp to fit window");
			}
			else
			{
				setText (pctZoomFactor + " %");
				setToolTipText ("Zoom mapp to " + pctZoomFactor + " %");
			}
		}
		public void run () {
			VPathway drawing = Engine.getActiveVPathway();
			if (drawing != null)
			{
				drawing.setPctZoom(pctZoomFactor);
			}
			else
			{
				MessageDialog.openError (window.getShell(), "Error", 
					"No gpml file loaded! Open or create a new gpml file first");
			}
		}
	}

	/**
	 * {@link Action} to open a {@link AboutDlg} window
	 */
	static class AboutAction extends Action 
	{
		MainWindow window;
		public AboutAction (MainWindow w)
		{
			window = w;
			setText ("&About");
			setToolTipText ("About " + Globals.APPLICATION_VERSION_NAME);
		}
		public void run () {
			AboutDlg gmmlAboutBox = new AboutDlg(window.getShell(), SWT.NONE);
			gmmlAboutBox.open();
		}
	}
	
	/**
	 * {@link Action} to open a {@link AboutDlg} window
	 */
	static class HelpAction extends Action 
	{
		MainWindow window;
		public HelpAction (MainWindow w)
		{
			window = w;
			setText ("&Help@F1");
			setToolTipText ("Opens " + Globals.APPLICATION_VERSION_NAME + " help in your web browser");
		}
		public void run () {
			SimpleRunnableWithProgress rwp = new SimpleRunnableWithProgress(
					window.getClass(), "openHelp", new Class[] {}, new Object[] {}, null);
			SimpleRunnableWithProgress.setMonitorInfo("Opening help", IProgressMonitor.UNKNOWN);
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(window.getShell());
			try {
				dialog.run(true, true, rwp);
			} catch (InvocationTargetException e) {
				Throwable cause = e.getCause();
				String msg = cause == null ? null : cause.getMessage();
				MessageDialog.openError(window.getShell(), "Unable to open help",
				"Unable to open web browser" +
				(msg == null ? "" : ": " + msg) +
				"\nYou can open the help page manually:\n" +
				Globals.HELP_URL);
			} catch (InterruptedException ignore) {}
			

		}
	}

	static class CopyAction extends Action
	{
		MainWindow window;
		public CopyAction (MainWindow w)
		{
			window = w;
			setText ("Copy@Ctrl+C");
			setToolTipText ("Copy selected objects to clipboard");
		}
		public void run()
		{
			Engine.getActiveVPathway().copyToClipboard();
		}
	}

	static class PasteAction extends Action
	{
		MainWindow window;
		public PasteAction (MainWindow w)
		{
			window = w;
			setText ("Paste@Ctrl+V");
			setToolTipText ("Paste contents of clipboard");
		}
		public void run()
		{
			Engine.getActiveVPathway().pasteFromClipboad();
		}
	}
	
	/**
	 * {@link Action} to save a gpml pathway
	 */
	static class SaveAction extends Action 
	{
		MainWindow window;
		public SaveAction (MainWindow w)
		{
			window = w;
			setText ("&Save pathway@Ctrl+S");
			setToolTipText ("Save pathway");
			setImageDescriptor(ImageDescriptor.createFromURL(Engine.getResourceURL("icons/save.gif")));
		}
		
		public void run () {
			Pathway gmmlData = Engine.getActivePathway();
			VPathway drawing = Engine.getActiveVPathway();
			
			double usedZoom = drawing.getPctZoom();
			// Set zoom to 100%
			drawing.setPctZoom(100);			
			// Overwrite the existing xml file
			if (gmmlData.getSourceFile() != null)
			{
				try
				{
					gmmlData.writeToXml(gmmlData.getSourceFile(), true);
				}
				catch (ConverterException e)
				{
					String msg = "While writing xml to " 
							+ gmmlData.getSourceFile().getAbsolutePath();					
					MessageDialog.openError (window.getShell(), "Error", 
							"Error: " + msg + "\n\n" + 
							"See the error log for details.");
					Engine.log.error(msg, e);
				}
			}
			else
			{
				SaveAsAction.do_run(window);
			}
			// Set zoom back
			drawing.setPctZoom(usedZoom);
		}
	}
	
	static class BiopaxAction extends Action 
	{
		MainWindow window;
		public BiopaxAction (MainWindow w)
		{
			window = w;
			setText ("Edit &BioPAX code");
			setToolTipText ("Edit BioPAX code");
		}
		
		public void run () {
			BiopaxDialog d = new BiopaxDialog(window.getShell());
			d.setPathway(Engine.getActivePathway());
			d.open();
		}
	}

}
