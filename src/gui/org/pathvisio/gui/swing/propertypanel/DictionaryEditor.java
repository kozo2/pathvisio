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
package org.pathvisio.gui.swing.propertypanel;

import org.pathvisio.gui.swing.SwingEngine;
import org.pathvisio.gui.swing.dialogs.PathwayElementDialog;
import org.pathvisio.model.PathwayElement;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Rebecca Tang
*/ /*
dictionary editor, allows multiselect, loads from a file
editor and renderer are merged for dictionary, like comments
 */
class DictionaryEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

	static final String BUTTON_LABEL = "Select Data From Dictionary";
	JButton button;
	PathwayElement currentElement;
	TypedProperty property;

	protected static final String EDIT = "edit";

	private SwingEngine swingEngine;

	public DictionaryEditor(SwingEngine swingEngine) {
		this.swingEngine = swingEngine;
		button = new JButton();
		button.setText(BUTTON_LABEL);
		button.setActionCommand(EDIT);
		button.addActionListener(this);
	}

	public void setInput(TypedProperty p) {
		property = p;
		button.setText("");
		if(!mayEdit()) fireEditingCanceled();
		button.setText(BUTTON_LABEL);
	}

	boolean mayEdit() { return property.elements.size() == 1; }

	public void actionPerformed(ActionEvent e) {
		if(!mayEdit()) {
			fireEditingCanceled();
			return;
		}
		if (EDIT.equals(e.getActionCommand()) && property != null) {
			currentElement = property.getFirstElement();
			if(currentElement != null) {
				PathwayElementDialog d = PathwayElementDialog.getInstance(swingEngine, currentElement, false, null, this.button, PathwayElementDialog.DICTIONARY, property);
				d.selectPathwayElementPanel(PathwayElementDialog.TAB_VALUES);
				d.setVisible(true);
				fireEditingStopped();
			}
		}
	}

	public Object getCellEditorValue() {
		return currentElement.getDictionaryEntries(property);
	}

	public Component getTableCellEditorComponent(JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
		return button;
	}
}
