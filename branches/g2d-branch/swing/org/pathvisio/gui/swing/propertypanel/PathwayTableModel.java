package org.pathvisio.gui.swing.propertypanel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayEvent;
import org.pathvisio.model.PathwayListener;
import org.pathvisio.model.PropertyType;
import org.pathvisio.view.Graphics;
import org.pathvisio.view.SelectionBox;
import org.pathvisio.view.SelectionBox.SelectionEvent;
import org.pathvisio.view.SelectionBox.SelectionListener;

public class PathwayTableModel extends AbstractTableModel implements SelectionListener, PathwayListener {
	Set<PathwayElement> input;
	List<TypedProperty> properties;
	
	public PathwayTableModel() {
		input = new HashSet<PathwayElement>();
		properties = new ArrayList<TypedProperty>();
		SelectionBox.addListener(this);
	}
	
	private void clearInput() {
		for(PathwayElement e : input) {
			e.removeListener(this);
		}
		input.clear();
		refresh();
	}
	
	private void removeInput(PathwayElement pwElm) {
		input.remove(pwElm);
		pwElm.removeListener(this);
		refresh();
	}
	
	private void addInput(PathwayElement pwElm) {
		input.add(pwElm);
		pwElm.addListener(this);
		refresh();
	}
	
	protected void refresh() {
		properties = generateProperties(input);
		fireTableDataChanged();
	}
	
	protected List<TypedProperty> generateProperties(Set<PathwayElement> elements) {
		List<TypedProperty> properties = new ArrayList<TypedProperty>();
		List<PropertyType> propTypes = getProperties(elements);
		for(PropertyType pt : propTypes) {
			TypedProperty value = getAggregateProperty(pt, elements);
			properties.add(value);
		}
		return properties;
	}
	
	protected List<PropertyType> getProperties(Set<PathwayElement> elements) {
		ArrayList<PropertyType> properties = null;
		ArrayList<PropertyType> remove = new ArrayList<PropertyType>();
		for(PathwayElement e : elements) {
			if(properties == null) {
				properties = new ArrayList<PropertyType>();
				properties.addAll(e.getAttributes());
				continue;
			}
			remove.clear();
			List<PropertyType> attributes = e.getAttributes();
			for(PropertyType p : properties) {
				if(!attributes.contains(p)) {
					remove.add(p);
				}
			}
			properties.removeAll(remove);
		}
		return properties == null ? new ArrayList<PropertyType>() : properties;
	}
	
	TypedProperty getAggregateProperty(PropertyType key, Set<PathwayElement> elements) {
		Object value = null;
		boolean first = true;
		for(PathwayElement e : elements) {
			Object o = e.getProperty(key);
			if(!first && (o == null || !o.equals(value))) {
				return TypedProperty.getInstance(elements, key);
			}
			value = o;
			first = false;
		}
		return TypedProperty.getInstance(elements, value, key);
	}
		
	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return properties.size();
	}

	public TypedProperty getPropertyAt(int row) {
		return properties.get(row);
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		TypedProperty p = properties.get(rowIndex);
		if(columnIndex == 0) return p.getType().desc();
		else return p.getViewValue();
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		super.setValueAt(aValue, rowIndex, columnIndex);
	}
	
	public String getColumnName(int column) {
		if(column == 0) return "Property";
		return "Value";
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}
	
	public void drawingEvent(SelectionEvent e) {
		switch(e.type) {
		case SelectionEvent.OBJECT_ADDED:
			if(e.affectedObject instanceof Graphics)
				addInput(((Graphics)e.affectedObject).getGmmlData());
			break;
		case SelectionEvent.OBJECT_REMOVED:
			if(e.affectedObject instanceof Graphics)
				removeInput(((Graphics)e.affectedObject).getGmmlData());
			break;
		case SelectionEvent.SELECTION_CLEARED:
			 clearInput();
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

	public void gmmlObjectModified(PathwayEvent e) {
		refresh();
	}
}
