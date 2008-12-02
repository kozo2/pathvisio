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
package org.pathvisio.gui.wikipathways;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.pathvisio.Globals;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.Pathway.StatusFlagEvent;
import org.pathvisio.model.Pathway.StatusFlagListener;
import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.util.Resources;
import org.pathvisio.view.VPathway;
import org.pathvisio.wikipathways.UserInterfaceHandler;
import org.pathvisio.wikipathways.WikiPathways;

/**
 * A collection of actions related to WikiPathways
 * @author thomas
 */
public class Actions {
	public static abstract class WikiAction extends AbstractAction {
		UserInterfaceHandler uiHandler;
		WikiPathways wiki;
		public WikiAction(UserInterfaceHandler uih, WikiPathways w, String name, ImageIcon icon) {
			super(name, icon);
			uiHandler = uih;
			wiki = w;
		}
	}

	public static class ImportAction extends WikiAction {

		public ImportAction(UserInterfaceHandler h, WikiPathways w) {
			super(h, w, "Import", new ImageIcon(Resources.getResourceURL("import.gif")));
			putValue(Action.SHORT_DESCRIPTION, "Import pathway from a file on your computer");
			putValue(Action.LONG_DESCRIPTION, "Import a pathway from various file formats on your computer");;
		}

		public void actionPerformed(ActionEvent e) 
		{
			if(wiki.getSwingEngine().importPathway()) {
				VPathway vp = wiki.getEngine().getActiveVPathway();
				wiki.setPathwayView(vp);
			}
		}
	}

	/**
	 * Exits the editor, either with or without saving the current pathway to WikiPathways
	 * @author thomas
	 */
	public static class ExitAction extends WikiAction {

		boolean doSave;
		String description;

		public ExitAction(UserInterfaceHandler h, WikiPathways w, boolean save, String description) {
			super(h, w, "Finish", new ImageIcon(save ? Resources.getResourceURL("apply.gif") : Resources.getResourceURL("cancel.gif")));
			this.description = description;
			doSave = save;
			String descr = doSave ? "Save pathway and close editor" : "Close the editor";
			putValue(Action.SHORT_DESCRIPTION, descr);
		}
		public void actionPerformed(ActionEvent e) {
			wiki.exit(doSave, description);
		}
	}

	public static class SaveToServerAction extends WikiAction implements StatusFlagListener {
		String description;

		public SaveToServerAction(UserInterfaceHandler h, WikiPathways w, String description) {
			super(h, w, "Save to ", new ImageIcon(Resources.getResourceURL("savetoweb.gif")));
			this.description = description;
			putValue(Action.SHORT_DESCRIPTION, "Save the pathway to " + Globals.SERVER_NAME);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, 
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			wiki.addStatusFlagListener(this);
			setEnabled(wiki.hasChanged());
		}

		public void actionPerformed(ActionEvent e) {
			try {
				wiki.saveUI(description);
			} catch(Exception ex) {
				Logger.log.error("Unable to save pathway", ex);
				JOptionPane.showMessageDialog(null, "Unable to save pathway:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		public void statusFlagChanged(StatusFlagEvent e) {
			setEnabled(e.getNewStatus());
		}
	}

		/**
		 * Action that switches an applet between fullscreen and embedded mode
		 * @author thomas
		 *
		 */
		public static class FullScreenAction extends WikiAction {
			final ImageIcon imgFull = new ImageIcon(
					Resources.getResourceURL("fullscreen.gif"));
			final ImageIcon imgRestore = new ImageIcon(
					Resources.getResourceURL("restorescreen.gif"));
			final String tooltip_full = "Switch to fullscreen mode";
			final String tooltip_restore = "Switch to embedded mode";

			PathwayPageApplet applet;

			public FullScreenAction(UserInterfaceHandler uiHandler, WikiPathways wiki, PathwayPageApplet applet) {
				super(uiHandler, wiki, "Fullscreen", null);
				this.applet = applet;
				putValue(WikiAction.SMALL_ICON, imgFull);
				putValue(WikiAction.SHORT_DESCRIPTION, tooltip_full);
			}

			public void actionPerformed(ActionEvent e) {
				if(applet.isFullScreen()) {
					toApplet();
				} else {
					toFrame();
				}
			}

			/**
			 * Make the applet go to fullscreen mode
			 */
			private void toFrame() {
				applet.toFullScreen();
				
				putValue(WikiAction.SMALL_ICON, imgRestore);
				putValue(WikiAction.SHORT_DESCRIPTION, tooltip_restore);

				resetDividerLocation();
			}
			
			private void resetDividerLocation() {
				int spPercent = PreferenceManager.getCurrent().getInt(GlobalPreference.GUI_SIDEPANEL_SIZE);
				wiki.getMainPanel().getSplitPane().setDividerLocation((100 - spPercent) / 100.0);
			}

			/**
			 * Disposes the frame and transfers the mainPanel to the
			 * applet
			 */
			private void toApplet() {
				applet.toEmbedded();
				
				putValue(WikiAction.SMALL_ICON, imgFull);
				putValue(WikiAction.SHORT_DESCRIPTION, tooltip_full);

				resetDividerLocation();
			}
		}
	}
