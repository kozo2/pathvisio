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
package org.pathvisio.gui.swing.propertypanel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.pathvisio.ApplicationEvent;
import org.pathvisio.Engine;
import org.pathvisio.Engine.ApplicationEventListener;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayEvent;
import org.pathvisio.model.PathwayListener;
import org.pathvisio.model.PropertyType;
import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.view.Graphics;
import org.pathvisio.view.VPathway;
import org.pathvisio.view.SelectionBox.SelectionEvent;
import org.pathvisio.view.SelectionBox.SelectionListener;

public class PathwayTableModel extends AbstractTableModel implements SelectionListener, 
									PathwayListener, 
									ApplicationEventListener {
	private static final long serialVersionUID = 1L;

	TableCellEditor defaultEditor = new DefaultCellEditor(new JTextField());
	
	Collection<PathwayElement> input;
	HashMap<PropertyType, TypedProperty> propertyValues;
	List<TypedProperty> shownProperties;
	
	public PathwayTableModel() {
		input = new HashSet<PathwayElement>();
		propertyValues = new HashMap<PropertyType, TypedProperty>();
		shownProperties = new ArrayList<TypedProperty>();
		
		Engine.getCurrent().addApplicationEventListener(this);
		VPathway vp = Engine.getCurrent().getActiveVPathway();
		if(vp != null) vp.addSelectionListener(this);
	}
	
	private void reset() {
		for(PathwayElement e : input) {
			//System.err.println("Removed " + e);
			e.removeListener(this);
		}
		propertyValues.clear();
		shownProperties.clear();
		input.clear();
		refresh(true);
	}
	
	private void removeInput(PathwayElement pwElm) {
		//System.err.println("Input removed");
		input.remove(pwElm);
		updatePropertyCounts(pwElm, true);
		pwElm.removeListener(this);
		refresh(true);
	}
	
	private void addInput(PathwayElement pwElm) {
		//System.err.println("Input added");
		input.add(pwElm);
		updatePropertyCounts(pwElm, false);
		pwElm.addListener(this);
		refresh(true);
	}
	
	protected void refresh() { refresh(false); }
	
	protected void refresh(boolean propertyCount) {
		if(propertyCount) {
			updateShownProperties();
		}
		refreshPropertyValues();
		fireTableDataChanged();
	}
		
	protected void updatePropertyCounts(PathwayElement e, boolean remove) {
		boolean advanced = GlobalPreference.getValueBoolean(GlobalPreference.SHOW_ADVANCED_ATTRIBUTES);
		for(PropertyType p : e.getAttributes(advanced)) {
			if(p.isHidden()) continue;
			
			TypedProperty tp = propertyValues.get(p);
			if(tp == null) {
				propertyValues.put(p, tp = new TypedProperty(p));
			}
			if(remove) {
				tp.removeElement(e);
			} else {
				tp.addElement(e);
			}
		}
	}
	
	protected void updateShownProperties() {
		for(TypedProperty tp : propertyValues.values()) {
			boolean shown = shownProperties.contains(tp);
			if(tp.elementCount() == input.size()) {
				//System.err.println("\tadding " + tp + " from shown");
				if(!shown) shownProperties.add(tp);
			} else {
				//System.err.println("\tremoveing " + tp + " from shown");
				shownProperties.remove(tp);
			}
		}
	}
	
	protected void refreshPropertyValues() {
		for(TypedProperty p : shownProperties) {
			p.refreshValue();
		}
	}
		
	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return shownProperties.size();
	}

	public TypedProperty getPropertyAt(int row) {
		return shownProperties.get(row);
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		TypedProperty p = getPropertyAt(rowIndex);
		if(columnIndex == 0) return p.getType().desc();
		else return p.getValue();
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(columnIndex != 0) {
			TypedProperty p = getPropertyAt(rowIndex);
			p.setValue(aValue);
		}
		Engine.getCurrent().getActiveVPathway().redrawDirtyRect();
	}
	
	public String getColumnName(int column) {
		if(column == 0) return "Property";
		return "Value";
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1 &&
				Engine.getCurrent().hasVPathway() && 
				Engine.getCurrent().getActiveVPathway().isEditMode();
	}
		
	public void selectionEvent(SelectionEvent e) {
		switch(e.type) {
		case SelectionEvent.OBJECT_ADDED:
			//System.err.println("OBJECT ADDED");
			if(e.affectedObject instanceof Graphics)
				addInput(((Graphics)e.affectedObject).getPathwayElement());
			break;
		case SelectionEvent.OBJECT_REMOVED:
			//System.err.println("OBJECT REMOVED");
			if(e.affectedObject instanceof Graphics)
				removeInput(((Graphics)e.affectedObject).getPathwayElement());
			break;
		case SelectionEvent.SELECTION_CLEARED:
			//System.err.println("CLEARED");
			 reset();
			break;
		}		
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		if(column != 0) {
			TypedProperty tp = getPropertyAt(row);
			if(tp != null) return tp.getCellRenderer();
		}
		return null;
	}

	public TableCellEditor getCellEditor(int row, int column) {
		if(column != 0) {
			TypedProperty tp = getPropertyAt(row);
			if(tp != null) return tp.getCellEditor();
		}
		return null;
	}
	
	public void gmmlObjectModified(PathwayEvent e) {
		refresh();
	}

	public void applicationEvent(ApplicationEvent e) {
		if(e.getType() == ApplicationEvent.VPATHWAY_CREATED) {
			((VPathway)e.getSource()).addSelectionListener(this);
		}
	}
}
