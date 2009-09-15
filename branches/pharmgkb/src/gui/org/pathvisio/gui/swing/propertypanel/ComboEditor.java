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

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.awt.*;

/**
 * @author Rebecca Fieldman
*/ //TODO: merge with ComboRenderer
class ComboEditor extends DefaultCellEditor {

	Map<Object, Object> label2value;
	Map<Object, Object> value2label;
	boolean useIndex;
	JComboBox combo;

	public ComboEditor(boolean editable, Object[] labels, boolean useIndex) {
		this(labels, useIndex);
		combo.setEditable(editable);
	}

	public ComboEditor(Object[] labels, boolean useIndex) {
		super(new JComboBox(labels));
		combo = (JComboBox)getComponent();
		this.useIndex = useIndex;
	}

	public ComboEditor(Object[] labels, Object[] values) {
		this(labels, false);
		if(values != null) {
			updateData(labels, values);
		}
	}

	public int getItemCount() {
		return label2value.size();
	}

	public void updateData(Object[] labels, Object[] values) {
		combo.setModel(new DefaultComboBoxModel(labels));
		if(values != null) {
			if(labels.length != values.length) {
				throw new IllegalArgumentException("Number of labels doesn't equal number of values");
			}
			if(label2value == null) label2value = new HashMap<Object, Object>();
			else label2value.clear();
			if(value2label == null) value2label = new HashMap<Object, Object>();
			else value2label.clear();
			for(int i = 0; i < labels.length; i++) {
				label2value.put(labels[i], values[i]);
				value2label.put(values[i], labels[i]);
			}
		}
	}

	public Object getCellEditorValue() {
		if(label2value == null) { //Use index
			JComboBox cb = (JComboBox)getComponent();
			return useIndex ? cb.getSelectedIndex() : cb.getSelectedItem();
		} else {
			Object label = super.getCellEditorValue();
			return label2value.get(label);
		}
	}

	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {
		if(value2label != null) {
			value = value2label.get(value);
		}
		if(useIndex) {
			combo.setSelectedIndex((Integer)value);
		} else {
			combo.setSelectedItem(value);
		}
		return combo;
	}
}
