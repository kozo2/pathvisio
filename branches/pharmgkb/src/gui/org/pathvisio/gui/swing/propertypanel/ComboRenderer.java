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
import javax.swing.table.TableCellRenderer;
import java.util.Map;
import java.util.HashMap;
import java.awt.*;

/**
 * @author Rebecca Fieldman
*/ //TODO: merge with ComboEditor
class ComboRenderer extends JComboBox implements TableCellRenderer {

	Map<Object, Object> value2label;
	public ComboRenderer(Object[] values) {
		super(values);
	}

	public ComboRenderer(Object[] labels, Object[] values) {
		this(labels);
		if(labels.length != values.length) {
			throw new IllegalArgumentException("Number of labels doesn't equal number of values");
		}
		updateData(labels, values);
	}

	public void updateData(Object[] labels, Object[] values) {
		setModel(new DefaultComboBoxModel(labels));
		if(values != null) {
			if(labels.length != values.length) {
				throw new IllegalArgumentException("Number of labels doesn't equal number of values");
			}
			value2label = new HashMap<Object, Object>();
			for(int i = 0; i < labels.length; i++) {
				value2label.put(values[i], labels[i]);
			}
		}
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if(value2label != null) {
			value = value2label.get(value);
		}
		if(value instanceof Integer) {
			setSelectedIndex((Integer)value);
		} else {
			setSelectedItem(value);
		}
		return this;
	}
}
