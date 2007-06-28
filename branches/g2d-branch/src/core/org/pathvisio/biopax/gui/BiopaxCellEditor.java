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
package org.pathvisio.biopax.gui;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.pathvisio.Engine;
import org.pathvisio.biopax.BiopaxManager;

public class BiopaxCellEditor extends DialogCellEditor {	
	public BiopaxCellEditor(Composite parent, String label) {
		super(parent);
	}
	
	protected void setSelectionListeners(Button b) {
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				BiopaxDialog d = new BiopaxDialog(e.display.getActiveShell());
				d.setPathway(Engine.getActivePathway());
				d.open();
			}
		});
	}

	protected Object openDialogBox(Control cellEditorWindow) {
		BiopaxManager bpm = new BiopaxManager(Engine.getActivePathway().getBiopax().getBiopax());
		BiopaxRefDialog d = new BiopaxRefDialog(cellEditorWindow.getShell(), bpm, (String)getValue());
		d.open();
		return d.getRef();
	}
}
