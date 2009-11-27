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
package org.pathvisio.gui.swing.dnd;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.pathvisio.debug.Logger;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.GpmlFormat;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.view.swing.PathwayTransferable;
import org.pathvisio.view.swing.VPathwaySwing;

public class PathwayImportHandler extends TransferHandler implements ClipboardOwner {
	
	static final int NOT_OWNER = -1;
	int timesPasted; //Keeps track of how many times the same data is pasted
	
	Set<DataFlavor> supportedFlavors;
	
	public PathwayImportHandler() {
		supportedFlavors = new HashSet<DataFlavor>();
		supportedFlavors.add(PathwayTransferable.GPML_DATA_FLAVOR);
		supportedFlavors.add(DataFlavor.stringFlavor);
	}
	
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		for(DataFlavor d : transferFlavors) {
			if(supportedFlavors.contains(d)) return true;
		}
		return false;
	}

	public boolean importData(JComponent comp, Transferable t) {
		try {			
			String xml = PathwayTransferable.getText(t);
			if(xml != null) {
				Logger.log.trace("Importing from xml: " + xml);
				importGpml(comp, xml);
			}

		} catch(Exception e) {
			Logger.log.error("Unable to paste pathway data", e);
		}
		return false;
	}

	private boolean importGpml(JComponent comp, String xml) throws UnsupportedFlavorException, IOException, ConverterException {
		Pathway pnew = new Pathway();
		GpmlFormat.readFromXml(pnew, new StringReader(xml), true);
		
		List<PathwayElement> elements = new ArrayList<PathwayElement>();
		for(PathwayElement elm : pnew.getDataObjects()) {
			if(elm.getObjectType() != ObjectType.MAPPINFO) {
				elements.add(elm);
			} else {
				//Only add mappinfo if it's not generated by the transferable
				String source = elm.getMapInfoDataSource();
				if(!PathwayTransferable.INFO_DATASOURCE.equals(source)) {
					elements.add(elm);
				}
			}
		}
		int shift = 0;
		if(timesPasted != NOT_OWNER) shift = ++timesPasted;
		
		((VPathwaySwing)comp).getChild().paste(elements, shift);
		return false;
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		timesPasted = NOT_OWNER;
	}
	
	public void obtainedOwnership() {
		timesPasted = 0;
	}
}
