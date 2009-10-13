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
package org.pathvisio.gui.swing.dialogs;

import org.pathvisio.debug.Logger;
import org.pathvisio.gui.swing.propertypanel.TypedProperty;
import org.pathvisio.model.DictionaryEntry;
import org.pathvisio.model.DictionaryManager;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.Property;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

/**
 * @author Rebecca Tang
 */
public class DictionaryDialog extends OkCancelDialog {
	private static final String ADD = "Select Values";
	private PathwayElement pathwayElement;
	private TypedProperty property;
	DictionaryTableModel dictTableModel;
	boolean readonly = false;
	JButton addBtn;


	public DictionaryDialog(PathwayElement curElem, TypedProperty curProp, Frame frame, String title,
							Component locationComp) {
		super(frame, title, locationComp, true, true);
		pathwayElement = curElem;
		property = curProp;
		setSize(450, 300);

		// build panel
		JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
		// table
		List<DictionaryEntry> selectedEntries = (List<DictionaryEntry>)pathwayElement.getDynamicProperty((Property)property.getType());
		if (selectedEntries != null && !selectedEntries.isEmpty()){
			Map<String, String> masterMap = DictionaryManager.getValuesMap((Property)property.getType());
			for (DictionaryEntry e : selectedEntries){
				e.setValid(false);
				if (masterMap.containsKey(e.getId())){
					String name = masterMap.get(e.getId());
					if (name.equals(e.getName())){
						e.setValid(true);
					}
				}
			}
		}
		dictTableModel = new DictionaryTableModel(selectedEntries);
		JTable dictTable = new JTable(dictTableModel) {
			private TableCellRenderer renderer = new DeleteableTextCellRenderer();
			public TableCellRenderer getCellRenderer(int row, int column) {
				return renderer;
			}
		};
		dictTable.setTableHeader(null);
		JScrollPane tablePane = new JScrollPane(dictTable);
		mainPanel.add(tablePane, BorderLayout.CENTER);
		
        // buttons
		JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		addBtn = new JButton(ADD);
		addBtn.setActionCommand(ADD);
		addBtn.addActionListener(this);
		btnPane.add(addBtn);
		mainPanel.add(btnPane, BorderLayout.SOUTH);

		setDialogComponent(mainPanel);
	}

	public void setReadOnly(boolean readonly) {
		this.readonly = readonly;
		addBtn.setEnabled(!readonly);
	}


	private void refresh() {
		validate();
	}

	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand().equals(ADD)) {
			final DictValuesDialog d = new DictValuesDialog(dictTableModel, null, this, property);
			if(!SwingUtilities.isEventDispatchThread()) {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							d.setVisible(true);
						}
					});
				} catch (Exception ex) {
					Logger.log.error("Unable to open dialog", ex);
				}
			} else {
				d.setVisible(true);
			}
			if(d.getExitCode().equals(DictValuesDialog.OK)) {
				refresh();
			}
		} else {
			super.actionPerformed(e);
		}
	}

	public void okPressed(){
		pathwayElement.setDynamicProperty((Property)property.getType(), dictTableModel.getData());
		super.okPressed();
	}

}
