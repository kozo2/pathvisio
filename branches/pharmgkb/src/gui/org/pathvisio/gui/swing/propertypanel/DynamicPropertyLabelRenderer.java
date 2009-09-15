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
import java.awt.*;

/**
 * @author Rebecca Fieldman
*/
public class DynamicPropertyLabelRenderer extends JLabel implements TableCellRenderer {
	   public Component getTableCellRendererComponent(JTable table, Object value,
			   boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
		   // 'value' is value contained in the cell located at
		   // (rowIndex, vColIndex)
		   if (isSelected) {
			   // cell (and perhaps other cells) are selected
		   }
		   if (hasFocus) {
			   // this cell is the anchor and the table has the focus
		   }
		   // Configure the component with the specified value
		   setText(value.toString());

		   // Set tool tip if desired
		   setToolTipText((String)value);
		   setForeground(Color.BLUE);

		   return this;
	   }

	   // The following methods override the defaults for performance reasons
	   public void validate() {}
	   public void revalidate() {}
	   protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
	   public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
   }
