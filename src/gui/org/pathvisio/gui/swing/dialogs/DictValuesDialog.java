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

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.pathvisio.gui.swing.propertypanel.TypedProperty;
import org.pathvisio.model.DictionaryEntry;
import org.pathvisio.model.DictionaryManager;
import org.pathvisio.model.Property;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * @author Rebecca Tang
 */
public class DictValuesDialog extends OkCancelDialog {
	private DictionaryTableModel dictTableModel;
	private JTextField filterText;
	private TableRowSorter<MyTableModel> sorter;


	public DictValuesDialog(DictionaryTableModel curDictTableModel, Frame frame, Component locationComp,
							TypedProperty property){
		super(frame, "Dictionary Entries", locationComp, true, false);
		dictTableModel = curDictTableModel;

		setDialogComponent(createDialogTablePane(property));
		setSize(400, 300);
	}


	/**
	 * table implementation
	 */
	private JPanel createDialogTablePane(TypedProperty property) {

		JPanel mainForm = new JPanel(new BorderLayout());

		List<DictionaryEntry> dictValues;
		try {
			dictValues = DictionaryManager.getValues((Property)property.getType());
		} catch (Exception ex) {
			StringWriter writer = new StringWriter();
			PrintWriter pw = new PrintWriter(writer);
			ex.printStackTrace(pw);
			JLabel label = new JLabel(writer.toString());
			mainForm.add(label, BorderLayout.CENTER);
			return mainForm;
		}

		MyTableModel model = new MyTableModel(dictValues);	
		JTable table = new JTable(model);
		sorter = new TableRowSorter<MyTableModel>(model);
		table.setRowSorter(sorter);
		table.setFillsViewportHeight(true);
		table.setTableHeader(null);
		JScrollPane scrollPane = new JScrollPane(table);

		//Create a separate form for filterText
		JPanel filterPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel filterTextLabel = new JLabel("Filter Text:", SwingConstants.TRAILING);
		filterPane.add(filterTextLabel);
		filterText = new JTextField(30);
		//Whenever filterText changes, invoke newFilter.
		filterText.getDocument().addDocumentListener(
				new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						newFilter();
					}
					public void insertUpdate(DocumentEvent e) {
						newFilter();
					}
					public void removeUpdate(DocumentEvent e) {
						newFilter();
					}
				});
		filterTextLabel.setLabelFor(filterText);
		filterPane.add(filterText);
		mainForm.add(scrollPane, BorderLayout.CENTER);
		mainForm.add(filterPane, BorderLayout.SOUTH);
		return mainForm;
	}
	/**
	  * Update the row filter regular expression from the expression in
	  * the text box.
	  */
	 private void newFilter() {
		 RowFilter<MyTableModel, Object> rf = null;
		 //If current expression doesn't parse, don't update.
		 try {
			 rf = RowFilter.regexFilter(filterText.getText());
		 } catch (java.util.regex.PatternSyntaxException e) {
			 return;
		 }
		 sorter.setRowFilter(rf);
	 }


	class MyTableModel extends AbstractTableModel {
		private static final int NAME_COL = 1;
		private static final int CHECKBOX_COL = 0;
		private String[] columnNames = {"",""};
		private Object[][] data;
		public MyTableModel(java.util.List<DictionaryEntry> dictValues){
			data = new Object[dictValues.size()-dictTableModel.getValidCount()][2];
			int i = 0;
			for (DictionaryEntry entry : dictValues){
				if (!dictTableModel.contains(entry)){ // don't display if selected
					data[i][CHECKBOX_COL] = Boolean.FALSE;
					data[i][NAME_COL] = entry;
					i++;
				}
			}

		}
		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/*
		 * JTable uses this method to determine the default renderer/
		 * editor for each cell.  If we didn't implement this method,
		 * then the last column would contain text ("true"/"false"),
		 * rather than a check box.
		 */
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's
		 * editable.
		 */
		public boolean isCellEditable(int row, int col) {
			//Note that the data/cell address is constant,
			//no matter where the cell appears onscreen.
			if (col == CHECKBOX_COL) {
				return true;
			} else {
				return false;
			}
		}

		/*
		 * Don't need to implement this method unless your table's
		 * data can change.
		 */
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			if (data[row][col].equals(true)){
				dictTableModel.setValue(data[row][NAME_COL]);
			}else{
				dictTableModel.removeValue(data[row][NAME_COL]);
			}
			fireTableCellUpdated(row, col);
		}
	}


	/**
	 * Forms implementation
	 */
	private Component createDialogPane(TypedProperty property){


		List<DictionaryEntry> dictValues;
		try {
			dictValues = DictionaryManager.getValues((Property)property.getType());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		DictValuesDialogBuilder builder = new DictValuesDialogBuilder();
		for (DictionaryEntry entry : dictValues){
//			if (!selectedEntry.contains(entry)){ // don't display if selected
//				builder.addBooleanField(entry);
//			}
		}

		JPanel contents = builder.getPanel();

		return contents;
	}

	/**
	 * used to build the dialog for Forms implementation
	 */
	protected static class DictValuesDialogBuilder
	{
		private DefaultFormBuilder m_builder;
		FormLayout m_layout;

		DictValuesDialogBuilder()
		{
			m_layout = new FormLayout("left:pref", "m");
			m_builder = new DefaultFormBuilder(m_layout);
		}

		JPanel getPanel()
		{
			return m_builder.getPanel();
		}

		private class BooleanFieldEditor implements ActionListener
		{
			private DictionaryEntry m_entry;

			BooleanFieldEditor(DictionaryEntry d)
			{
				m_entry = d;
			}

			public void actionPerformed(ActionEvent ae)
			{
//				selectedEntries.add(m_entry);
			}
		}

		void addBooleanField (DictionaryEntry d)
		{
			JCheckBox cb = new JCheckBox (d.getName());
			BooleanFieldEditor editor = new BooleanFieldEditor (d);
			cb.addActionListener(editor);

			m_builder.append(cb);
			m_builder.nextLine();			
		}

	}
}