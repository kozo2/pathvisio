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
package org.pathvisio.preferences.swt;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.StringFieldEditor;
import org.pathvisio.gui.swt.SwtEngine;
import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.preferences.swt.SwtPreferences.SwtPreference;

public class PreferenceDlg extends PreferenceManager {
	
	public PreferenceDlg() {
		super();

		PreferenceNode display = new PreferenceNode("display", new DisplayPage());
	    PreferenceNode colors = new PreferenceNode("colors", new ColorsPage());
	    PreferenceNode directories = new PreferenceNode("directories", new DirectoriesPage());
	    PreferenceNode files = new PreferenceNode("files", new FilesPage());
	    PreferenceNode database = new PreferenceNode("database", new DatabasePage());
	    
	    addToRoot(display);
	    addTo("display", colors);
	    addToRoot(directories);
	    addToRoot(files);
	    addToRoot(database);
	}
	
	private class FilesPage extends FieldEditorPreferencePage {
		public FilesPage() {
			super("Files", GRID);
		}
		
		protected void createFieldEditors() {
			FileFieldEditor f1 = new FileFieldEditor(GlobalPreference.FILE_LOG.name(), "Log file:", getFieldEditorParent());
			addField(f1);
		}
	}
	
	private class DirectoriesPage extends FieldEditorPreferencePage {
		public DirectoriesPage() {
			super("Directories", GRID);
			noDefaultAndApplyButton();
		}
		
		protected void createFieldEditors() {
			DirectoryFieldEditor d1 = new DirectoryFieldEditor(SwtPreference.SWT_DIR_PWFILES.name(),
					"Gpml pathways:", getFieldEditorParent());
			addField(d1);
			
			DirectoryFieldEditor d2 = new DirectoryFieldEditor(SwtPreference.SWT_DIR_GDB.name(),
					"Gene databases:", getFieldEditorParent());
			addField(d2);
			
			DirectoryFieldEditor d3 = new DirectoryFieldEditor(SwtPreference.SWT_DIR_EXPR.name(),
					"Expression datasets:", getFieldEditorParent());
			addField(d3);

			if(SwtEngine.isUseR()) {
				DirectoryFieldEditor d4 = new DirectoryFieldEditor(SwtPreference.SWT_DIR_RDATA.name(),
						"Results from pathway statistics:", getFieldEditorParent());
				addField(d4);
			}
		}
	}
	
	private class DisplayPage extends FieldEditorPreferencePage {
		public DisplayPage() {
			super("Display", GRID);
		}
		
		protected void createFieldEditors() {
			IntegerFieldEditor f = new IntegerFieldEditor(SwtPreference.SWT_SIDEPANEL_SIZE.name(),
					"Initial side panel size (percent of window size):", getFieldEditorParent());
			f.setValidRange(0, 100);
			addField(f);
			
			BooleanFieldEditor f2 =	new BooleanFieldEditor (
					GlobalPreference.SHOW_ADVANCED_ATTRIBUTES.name(),									   
					"Show advanced attributes (e.g. references):", getFieldEditorParent());
				addField (f2);
		}
	}
	private class ColorsPage extends FieldEditorPreferencePage {
		public ColorsPage() {
			super("Colors", GRID);
		}
		
		protected void createFieldEditors() {
			ColorFieldEditor f1 = new ColorFieldEditor(GlobalPreference.COLOR_NO_CRIT_MET.name(), 
					"Default color for 'no criteria met':", getFieldEditorParent());
			addField(f1);
			ColorFieldEditor f2 = new ColorFieldEditor(GlobalPreference.COLOR_NO_GENE_FOUND.name(), 
					"Default color for 'gene not found':", getFieldEditorParent());
			addField(f2);
			ColorFieldEditor f3 = new ColorFieldEditor(GlobalPreference.COLOR_NO_DATA_FOUND.name(), 
					"Default color for 'no data found':", getFieldEditorParent());
			addField(f3);
			ColorFieldEditor f4 = new ColorFieldEditor(GlobalPreference.COLOR_SELECTED.name(), 
					"Line color for selected objects:", getFieldEditorParent());
			addField(f4);
			ColorFieldEditor f5 = new ColorFieldEditor(GlobalPreference.COLOR_HIGHLIGHTED.name(), 
					"Line color for highlighted objects:", getFieldEditorParent());
			addField(f5);
			
		}
	}
	
	private class DatabasePage extends FieldEditorPreferencePage {
		public DatabasePage() {
			super("Database", GRID);
		}
		
		protected void createFieldEditors() {
			StringFieldEditor f1 = new StringFieldEditor(GlobalPreference.DB_ENGINE_GDB.name(),
					"Database connector class for gene database:", getFieldEditorParent());
			addField(f1);
			StringFieldEditor f2 = new StringFieldEditor(GlobalPreference.DB_ENGINE_GEX.name(),
					"Database connector class for expression dataset:", getFieldEditorParent());
			addField(f2);
		}
	}
	
}