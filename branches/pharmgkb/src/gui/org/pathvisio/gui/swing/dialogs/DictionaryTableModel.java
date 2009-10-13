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

import org.pathvisio.model.DictionaryEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rebecca Fieldman
 */
class DictionaryTableModel extends AbstractTableModel {
	private static final int NAME_COL = 0;
	private static final int DELETE_COL = 0;
	private List<DictionaryEntry> data = new ArrayList<DictionaryEntry>();
	private int m_validCount = 0;

	public DictionaryTableModel(){
	}

	public DictionaryTableModel(List<DictionaryEntry> existingData){
		this();
		if (existingData != null){
			data = existingData;
		}
	}

	public int getValidCount(){
		m_validCount = 0;
		for (DictionaryEntry entry: data){
			if (entry.isValid()){
				m_validCount++;
			}
		}
		return m_validCount;
	}

	public boolean contains(DictionaryEntry e){
		return data.contains(e);
	}

	public List<DictionaryEntry> getData(){
		return data;
	}

	public void setValue(Object value){
		setValueAt(value, getRowCount(), NAME_COL);
	}

	public void removeValue(Object value){
		@SuppressWarnings({"SuspiciousMethodCalls"})
		int row = data.indexOf(value);
		removeValueAt(row);
	}

	public void removeValueAt(int row){
		if (row >= 0 && row <data.size()){
			data.remove(row);
			fireTableRowsDeleted(row, row);
		}
	}


	//-- TableModel methods --//

	public int getRowCount() {
		return data.size();
	}

	public int getColumnCount() {
		return 1;
	}

	public String getColumnName(int col) {
		return "";
	}

	public Class getColumnClass(int col) {
		return DictionaryEntry.class;
	}

	public boolean isCellEditable(int row, int col) {
		return col != NAME_COL;
	}

	public Object getValueAt(int row, int col) {
		if (col == NAME_COL) {
			DictionaryEntry entry = data.get(row);
			if (entry.isValid()){
				return entry.getName();
			}else{
				return "<html> <font color=\"red\">" + entry.getName() + "</font></html>";
			}
		}
		return null;
	}

	public void setValueAt(Object value, int row, int col) {
		if (col == NAME_COL) {
			if (row < data.size()) {
				throw new IllegalArgumentException("Cannot edit, can only add");
			}
			data.add((DictionaryEntry)value);
			fireTableRowsInserted(row, row);
		}
	}
}
