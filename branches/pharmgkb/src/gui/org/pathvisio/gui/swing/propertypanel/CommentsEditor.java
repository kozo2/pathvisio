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

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Rebecca Fieldman
*/
class CommentsEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

	static final String BUTTON_LABEL = "View/edit comments";
	JButton button;
	PathwayElement currentElement;
	TypedProperty property;

	protected static final String EDIT = "edit";

	private SwingEngine swingEngine;

	public CommentsEditor(SwingEngine swingEngine) {
		this.swingEngine = swingEngine;
		button = new JButton();
		button.setText(BUTTON_LABEL);
		button.setActionCommand("edit");
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
				PathwayElementDialog d = PathwayElementDialog.getInstance(swingEngine, currentElement, false, null, this.button);
				d.selectPathwayElementPanel(PathwayElementDialog.TAB_COMMENTS);
				d.setVisible(true);
				fireEditingCanceled(); //Value is directly saved in dialog
			}
		}
	}

	public Object getCellEditorValue() { // this method is not called because of fireEditingCanceled();
		return currentElement.getComments();
	}

	public Component getTableCellEditorComponent(JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
		return button;
	}
}