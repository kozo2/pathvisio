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

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pathvisio.Engine;
import org.pathvisio.Globals;
import org.pathvisio.debug.Sleak;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.Pathway;
import org.pathvisio.preferences.PreferenceCollection;
import org.pathvisio.preferences.swt.SwtPreferences;
import org.pathvisio.view.VPathwayWrapper;
import org.pathvisio.view.swt.VPathwaySWT;

/**
 * This class contains the essential parts of the program: the window, drawing and gpml data
 */
public class SwtEngine {
	/**
	 * {@link Pathway} object containing JDOM representation of the gpml pathway 
	 * and handle gpml related actions
	 */
	
	private static MainWindow window;
	
	private static ImageRegistry imageRegistry;
	private static PreferenceCollection preferences;
	
	private static File DIR_APPLICATION;
	private static File DIR_DATA;
	static boolean USE_R;
		
	/**
	 * Get the {@link ApplicationWindow}, the UI of the program
	 */
	public static MainWindow getWindow() {
		if(window == null) window = new MainWindow();
		return window;
	}
	
	/**
	 * Initiates an instance of {@link MainWindow} that is monitored by Sleak.java,
	 * to monitor what handles (to OS device context) are in use. For debug purposes only 
	 * (to check for undisposed widgets)
	 * @return The {@link MainWindow} monitored by Sleak.java
	 */
	public static MainWindow getSleakWindow() {
		//<DEBUG to find undisposed system resources>
		DeviceData data = new DeviceData();
		data.tracking = true;
		Display display = new Display(data);
		Sleak sleak = new Sleak();
		sleak.open();
		
		Shell shell = new Shell(display);
		window = new MainWindow(shell);
		return window;
		//</DEBUG>
	}
	
	public static void openPathway(String fileName) {
		try {
			VPathwayWrapper pswt = null;
			if(window != null) {
				pswt = new VPathwaySWT(window.sc, SWT.NO_BACKGROUND);
			}
			Engine.openPathway(fileName);
		} catch(ConverterException e) {		
			if (e.getMessage().contains("Cannot find the declaration of element 'Pathway'"))
			{
				MessageDialog.openError(getWindow().getShell(), 
						"Unable to open Gpml file", 
						"Unable to open Gpml file.\n\n" +
						"The most likely cause for this error is that you are trying to open an old Gpml file. " +
						"Please note that the Gpml format has changed as of March 2007. " +
						"The standard pathway set can be re-downloaded from http://pathvisio.org " +
						"Non-standard pathways need to be recreated or upgraded. " +
						"Please contact the authors at martijn.vaniersel@bigcat.unimaas.nl if you need help with this.\n" +
						"\nSee error log for details");
				Engine.log.error("Unable to open Gpml file", e);
			}
			else
			{
				MessageDialog.openError(getWindow().getShell(), 
						"Unable to open Gpml file", e.getClass() + e.getMessage());
				Engine.log.error("Unable to open Gpml file", e);
			}
		}
	}
	
	/**
	 * Get the {@link SwtPreferences} containing the user preferences
	 */
	public static PreferenceCollection getPreferences() { 
		if(preferences == null) preferences = new SwtPreferences();
		return preferences; 
	}
	
	/**
	 * Get the {@link ImageRegistry} containing commonly used images
	 */
	public static ImageRegistry getImageRegistry() { 
		if(imageRegistry == null) imageRegistry = new ImageRegistry();
		return imageRegistry; 
	}
	
	/**
	 * Set the {@link ImageRegistry} containing commonly used images
	 */
	public static void setImageRegistry(ImageRegistry _imageRegistry) {
		imageRegistry = _imageRegistry;
	}
			
	/**
	 * Get the working directory of this application
	 */
	public static File getApplicationDir() {
		if(DIR_APPLICATION == null) {
			DIR_APPLICATION = new File(System.getProperty("user.home"), "." + Globals.APPLICATION_NAME);
			if(!DIR_APPLICATION.exists()) DIR_APPLICATION.mkdir();
		}
		return DIR_APPLICATION;
	}
		
	public static File getDataDir() {
		if(DIR_DATA == null) {
			DIR_DATA = new File(System.getProperty("user.home"), Globals.APPLICATION_NAME + "-Data");
			if(!DIR_DATA.exists()) DIR_DATA.mkdir();
		}
		return DIR_DATA;
	}
			
	public static boolean isUseR() { return USE_R; }
}