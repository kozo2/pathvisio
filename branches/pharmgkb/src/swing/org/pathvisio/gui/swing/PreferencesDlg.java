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
package org.pathvisio.gui.swing;

import javax.swing.JPanel;

import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.model.PropertyManager;
import org.pathvisio.model.Property;

import java.util.Set;

/**
 * Global dialog for setting the user preferences.
 */
public class PreferencesDlg extends AbstractPreferenceDlg
{
	PreferencesDlg ()
	{
		super (PreferenceManager.getCurrent());
	}
	
	public void initPanels()
	{		
		PreferencePanelBuilder builder;

		// config display panel
		builder = createBuilder();
		builder.addIntegerField (
				GlobalPreference.GUI_SIDEPANEL_SIZE,
				"Initial side panel size (percent of window size):", 0, 100);
		builder.addBooleanField (
			GlobalPreference.DATANODES_ROUNDED,
			"Use rounded rectangles for data nodes");
		builder.addIntegerField(
				GlobalPreference.MAX_NR_CITATIONS,
				"Maximum citations to show (use -1 to show all)",
				-1, 1000
		);
		builder.addBooleanField(
				GlobalPreference.SNAP_TO_ANGLE, 
				"Snap to angle when moving line and rotation handles");
		builder.addIntegerField (
				GlobalPreference.SNAP_TO_ANGLE_STEP,
				"Distance between snap-steps in degrees:", 1, 90);
		builder.addBooleanField (
				GlobalPreference.MIM_SUPPORT,
				"Load support for molecular interaction maps (MIM) at program start");
		builder.addBooleanField (
				GlobalPreference.SHOW_ADVANCED_PROPERTIES,									   
				"Show advanced properties (e.g. references)");
		builder.addBooleanField (
				GlobalPreference.USE_SYSTEM_LOOK_AND_FEEL,									   
				"Use Java System look-and-feel at program start");
		builder.addBooleanField(
				GlobalPreference.ENABLE_DOUBLE_BUFFERING, 
				"Enable double-buffering (pathway is drawn slower, but flickerless)");
		// create display panel
		JPanel displayPanel = builder.getPanel();


		// config color panel
		builder = createBuilder();
		builder.addColorField(
				GlobalPreference.COLOR_NO_CRIT_MET, 
				"Default color for 'no criteria met':");
		builder.addColorField(
				GlobalPreference.COLOR_NO_GENE_FOUND, 
				"Default color for 'gene not found':");
		builder.addColorField(
				GlobalPreference.COLOR_NO_DATA_FOUND, 
				"Default color for 'no data found':");
		builder.addColorField(
				GlobalPreference.COLOR_SELECTED, 
				"Line color for selected objects:");
		builder.addColorField(
				GlobalPreference.COLOR_HIGHLIGHTED,
				"Highlight color");
		// create color panel
		JPanel colorPanel = builder.getPanel();
		

		// config file panel
		builder = createBuilder();
		builder.addFileField(
				GlobalPreference.FILE_LOG, 
				"Log file:", false);
        builder.addFileField(
                GlobalPreference.FILE_PROPERTIES,
                "Custom Properties file:", false);
		// create file panel
		JPanel filePanel = builder.getPanel();


		// config dir panel
		builder = createBuilder();
		builder.addFileField (GlobalPreference.DIR_PWFILES,
				"Gpml pathways:", true);
		builder.addFileField (GlobalPreference.DIR_GDB,
				"Gene databases:", true);
		builder.addFileField (GlobalPreference.DIR_EXPR,
				"Expression datasets:", true);		
		// create dir panel
		JPanel dirPanel = builder.getPanel();


		// config db panel
		builder = createBuilder();
		builder.addStringField (GlobalPreference.DB_ENGINE_GDB,
				"Database connector class for gene database:");
		builder.addStringField (GlobalPreference.DB_ENGINE_GEX,
				"Database connector class for expression dataset:");
		// create db panel
		JPanel dbPanel = builder.getPanel();


        // config modes panel
        builder = createBuilder();
        for (Property mode : PropertyManager.getModes()) {
			builder.addBooleanField(mode, mode.getName() + " mode");
		}
		// create modes panel
        JPanel mdPanel = builder.getPanel();

		addPanel ("Display", displayPanel);
		addPanel ("Display.Colors", colorPanel);
		addPanel ("Directories", dirPanel);
		addPanel ("Files", filePanel);
		addPanel ("Database", dbPanel);
        addPanel ("Display Modes", mdPanel);
	}
}
