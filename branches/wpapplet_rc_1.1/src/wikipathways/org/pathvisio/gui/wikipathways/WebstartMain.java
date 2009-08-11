//PathVisio,
//a tool for data visualization and analysis using Biological Pathways
//Copyright 2006-2007 BiGCaT Bioinformatics

//Licensed under the Apache License, Version 2.0 (the "License"); 
//you may not use this file except in compliance with the License. 
//You may obtain a copy of the License at 

//http://www.apache.org/licenses/LICENSE-2.0 

//Unless required by applicable law or agreed to in writing, software 
//distributed under the License is distributed on an "AS IS" BASIS, 
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
//See the License for the specific language governing permissions and 
//limitations under the License.

package org.pathvisio.gui.wikipathways;

import java.net.URL;

import javax.jnlp.UnavailableServiceException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.pathvisio.Engine;
import org.pathvisio.debug.Logger;
import org.pathvisio.gui.swing.GuiMain;
import org.pathvisio.gui.swing.SwingEngine;
import org.pathvisio.util.ProgressKeeper;
import org.pathvisio.util.RunnableWithProgress;
import org.pathvisio.wikipathways.Parameter;
import org.pathvisio.wikipathways.UserInterfaceHandler;
import org.pathvisio.wikipathways.WikiPathways;

import com.sun.media.sound.Toolkit;

public class WebstartMain extends GuiMain {
	WikiPathways wiki;
	UserInterfaceHandler uiHandler;

	protected void createAndShowGUI() {
		Engine engine = new Engine();
		Engine.setCurrent(engine);
		SwingEngine.setCurrent(new SwingEngine(engine));


		try {
			initWiki();
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Unable to launch editor:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			Logger.log.error("Unable to launch editor", e);
		}
		super.createAndShowGUI(mainPanel);
	}

	private void initWiki() throws UnavailableServiceException {
		uiHandler = new WebstartUserInterfaceHandler(getFrame());
		wiki = new WikiPathways(uiHandler);
		mainPanel = wiki.prepareMainPanel();


		final RunnableWithProgress r = new RunnableWithProgress() {
			public Object excecuteCode() {
				parseCommandLine(getArgs());

				try {
					Engine.getCurrent().setWrapper(SwingEngine.getCurrent().createWrapper());
					wiki.init(getProgressKeeper(), new URL("http://www.wikipathways.org"));
				} catch(Exception e) {
					Logger.log.error("Error while starting editor", e);
					JOptionPane.showMessageDialog(
							getMainPanel(), e.getClass() + ": " + e.getMessage(), "Error while initializing editor", JOptionPane.ERROR_MESSAGE);
				};
				return null;
			}
		};
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {					
				uiHandler.runWithProgress(r, "", ProgressKeeper.PROGRESS_UNKNOWN, false, true);
			}
		});
	}

	void parseCommandLine(String[] args) {
		for(int i = 0; i < args.length -1; i++) {
			String a = args[i];
			if(a.startsWith("-")) {
				//Try to find the corresponding parameter
				String parName = a.substring(1);
				Parameter par = Parameter.valueOf(parName);
				if(par == null) {
					Logger.log.error("Invalid commandline parameter '" + parName + "'");
				} else {
					par.setValue(args[i+1]);
					i++; //Skip value
				}
			}
		}
	}

	public static void main(String[] args) {
		final WebstartMain gui = new WebstartMain();
		gui.setArgs(args);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.createAndShowGUI();
			}
		});
	}
}