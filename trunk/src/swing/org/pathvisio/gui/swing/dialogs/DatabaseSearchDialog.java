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
package org.pathvisio.gui.swing.dialogs;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.pathvisio.data.GdbManager;
import org.pathvisio.gui.swing.dialogs.DataNodeDialog.XrefWithSymbol;
import org.pathvisio.model.Xref;

public class DatabaseSearchDialog extends OkCancelDialog {
	List<XrefWithSymbol> xrefs = new ArrayList<XrefWithSymbol>();
	
	public DatabaseSearchDialog(String title, List<XrefWithSymbol> xrefs) {
		super(null, title, null, true);
		this.xrefs = xrefs;
		Collections.sort(xrefs, new Comparator<XrefWithSymbol>() {
			public int compare(XrefWithSymbol o1, XrefWithSymbol o2) 
			{
				if(o1.getSymbol() != null && o2.getSymbol() != null)
					return o1.getSymbol().compareTo(o2.getSymbol());
				if(o1.getId() != null && o2.getId() != null)
					return o1.getId().compareTo(o2.getId());
				if(o1.getDatabaseName() != null && o2.getDatabaseName() != null)
					return o1.getDatabaseName().compareTo(o2.getDatabaseName());
				return 0;
			}
		});
		((XRefTableModel)table.getModel()).refresh();
		validate();
	}

	JTable table;
	Xref selected;
	
	public Xref getSelected() {
		return selected;
	}
	
	protected Component createDialogPane() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		JLabel label = new JLabel("Please select one of the references and press Ok");
		table = new JTable(new XRefTableModel());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scroll = new JScrollPane(table);

		GridBagConstraints grid = new GridBagConstraints();
		grid.insets = new Insets(10, 5, 10, 5);
		grid.gridwidth = 1;
		grid.gridheight = 2;
		grid.gridx = 0;
		grid.weightx = 1;
		grid.gridy = GridBagConstraints.RELATIVE;
		grid.fill = GridBagConstraints.HORIZONTAL;
		panel.add(label, grid);
		grid.fill = GridBagConstraints.BOTH;
		grid.weighty = 1;
		panel.add(scroll, grid);

		//Apply on double click
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					okPressed();
				}
			}
		});

		//Apply on Enter
		//Disable default behavior of JTable
		table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"none"
		);
		table.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					okPressed();
				}
			}
		});
		
		return panel;
	}
	
	private class XRefTableModel extends AbstractTableModel {
		public String getColumnName(int column) {
			switch(column) {
			case 0: return "Name";
			case 1: return "Identifier";
			case 2: return "Datasource";
			default: return "";
			}
		}
		
		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return xrefs == null ? 0 : xrefs.size();
		}

		public Object getValueAt(int row, int col) {
			Xref xr = xrefs.get(row);
			switch(col) {
			case 0:
				return GdbManager.getCurrentGdb().getGeneSymbol(xr);
			case 1:
				return xr.getId();
			case 2:
				return xr.getDataSource();
			}
			return null;
		}
		
		public void refresh() {
			fireTableDataChanged();
		}
	}
	
	
	protected void okPressed() {
		//Store selected value
		int row = table.getSelectedRow();
		if(row > -1) {
			selected = xrefs.get(row);
		} else {
			selected = null;
		}
		super.okPressed();
	}
	
	protected void cancelPressed() {
		selected = null;
		super.cancelPressed();
	}
}
