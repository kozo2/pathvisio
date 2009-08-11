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
package org.pathvisio.cytoscape.actions;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import org.pathvisio.cytoscape.GpmlPlugin;
import org.pathvisio.view.swing.PathwayTransferable;

import cytoscape.util.CytoscapeAction;

public class PasteAction extends CytoscapeAction implements FlavorListener {
	GpmlPlugin importer;
	
	public PasteAction(GpmlPlugin importer) {
		super();
		this.importer = importer;		
	}
	
	protected void initialize() {
		super.initialize();
		putValue(NAME, "Paste GPML");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl V"));
	}
	
	public String getPreferredMenu() {
		return "Edit";
	}

	public boolean isInMenuBar() {
		return true;
	}
	
	public void actionPerformed(ActionEvent e) {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		importer.drop(c.getContents(this));
	}
	
	public void flavorsChanged(FlavorEvent e) {
		//Listene for supported clipboard contents
		Clipboard clip = (Clipboard)e.getSource();
		setEnabled(clip.isDataFlavorAvailable(PathwayTransferable.gpmlDataFlavor));
	}
}