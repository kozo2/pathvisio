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
package org.pathvisio.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This class is responsible for resizing a table's column width to fit its parents width
 * Columns that are unresizable ({@link TableColumn#getResizable()} = false) are skipped
 * NOTE: Be sure the table's parent {@link Composite} contains only the table (no
 * other controls) and uses {@link FillLayout}.
 */
public class TableColumnResizer extends ControlAdapter {
	Table table;
	Composite parent;
	TableColumn[] cols;
	double[] weights;
	int[] widths;
	
	/**
	 * Constructor for this class
	 * @param table		The tables of which the columns have to be resized
	 * @param parent	The parent composite to fill the column width
	 * @param weights	The relative size of each column (when a column is unresizable, set this to 0)
	 */
	public TableColumnResizer(Table table, Composite parent, int[] weights) {
		this.table = table;
		this.parent = parent == null ? table.getParent() : parent;
		cols = table.getColumns();
		setWeights(weights);
		initWidths();
	}
	
	void initWidths() {
		widths = new int[cols.length];
		for(int i = 0; i < cols.length; i++)
			widths[i] = cols[i].getWidth();
	}
	
	public void setWeights(int[] intWeights) {
		if(intWeights == null) {
			weights = new double[cols.length];
			int resizable = 0;
			for(TableColumn c : cols) resizable += c.getResizable() ? 1 : 0;
			for(int i = 0; i < weights.length; i++) weights[i] = 1.0 / resizable;
		} else {
			int sum = 0;
			for(int i : intWeights) sum += i;
			weights = new double[intWeights.length];
			for(int i = 0; i < intWeights.length; i++) weights[i] = (double)intWeights[i] / sum;
		}
	}
	public TableColumnResizer(Table table, Composite parent) {
		this(table, parent, null);
	}
	
	public void controlResized(ControlEvent e) {
		doResize();
	}
	
	public void doResize() {
		//Check if number of columns is the same
		if(table.getColumns().length != cols.length) {
			cols = table.getColumns();
			setWeights(null);
		}
		cols = table.getColumns();
		
		Rectangle area = parent.getClientArea();
		Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int width = area.width - 2 * table.getBorderWidth();
		if (preferredSize.y > area.height + table.getHeaderHeight()) {
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			Point vBarSize = table.getVerticalBar().getSize();
			width -= vBarSize.x * 2;
		}
		
		//Subtract width of columns with fixed size from available width
		for(int i = 0; i < cols.length; i++) width -= cols[i].getResizable() ? 0 : widths[i];
		
		Point oldSize = table.getSize();
		if (oldSize.x > area.width) {
			// table is getting smaller so make the columns
			// smaller first and then resize the table to
			// match the client area width
			for (int i = 0; i < cols.length; i++) {
				if(cols[i].getResizable()) {
					cols[i].setWidth((int)(width * weights[i]));
				}
			}
			
			table.setSize(area.width, area.height);
		} else {
			// table is getting bigger so make the table
			// bigger first and then make the columns wider
			// to match the client area width
			table.setSize(area.width, area.height);
			
			for (int i = 0; i < cols.length; i++) {
				if(cols[i].getResizable())	{
					cols[i].setWidth((int)(width * weights[i]));
				}
			}
		}
	}
}