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
package org.pathvisio.util.swing;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataEvent;

/**
 * A JComboBox implementation that allows setting
 * an item that is not in the listModel, even
 * when it's not editable.
 * 
 * Workaround for java bug 4291374
 */
public class PermissiveComboBox extends JComboBox {

	public PermissiveComboBox(ComboBoxModel m) {
		super(m);
	}

	public PermissiveComboBox() {
		super();
	}
	
	public PermissiveComboBox(Object[] o) {
		super (o);
	}

	public void contentsChanged(ListDataEvent lde) {
		ComboBoxModel mod = getModel();
		Object newSelectedItem = mod.getSelectedItem();
		if (selectedItemReminder == null) {
			if (newSelectedItem != null)
				selectedItemChanged();
		} else {
			if (!selectedItemReminder.equals(newSelectedItem)) {
				selectedItemChanged();
			}
		}
	}
	
}
