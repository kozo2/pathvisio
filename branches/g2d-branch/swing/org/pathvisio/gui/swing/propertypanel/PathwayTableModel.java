package org.pathvisio.gui.swing.propertypanel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PropertyType;

public class PathwayTableModel extends AbstractTableModel {
	List<PathwayElement> input;
	List<TypedProperty> properties;
	
	public void setInput(PathwayElement pwElm) {
		input.clear();
		input.add(pwElm);
		fireTableDataChanged();
	}
	
	public void setInput(Collection<PathwayElement> input) {
		this.input.clear();
		this.input.addAll(input);
		fireTableDataChanged();
	}
	
	protected List<TypedProperty> generateProperties(List<PathwayElement> elements) {
		List<TypedProperty> properties = new ArrayList<TypedProperty>();
		List<PropertyType> propTypes = getProperties(elements);
		for(PropertyType pt : propTypes) {
			Object value = getAggregateValue(pt, elements);
			properties.add(TypedProperty.getInstance(value, pt));
		}
		return properties;
	}
	
	protected List<PropertyType> getProperties(List<PathwayElement> elements) {
		List<PropertyType> properties = null;
		List<PropertyType> remove = new ArrayList<PropertyType>();
		for(PathwayElement e : elements) {
			if(properties == null) {
				properties = e.getAttributes();
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
		return properties;
	}
	
	Object getAggregateValue(PropertyType key, List<PathwayElement> elements) {
		Object value = TypedProperty.DIFFERENT;
		for(int i = 0; i < elements.size(); i++) {
			PathwayElement e = elements.get(i);
			Object o = e.getProperty(key);
			if(i != 0 && (o == null || !o.equals(value))) {
				return TypedProperty.DIFFERENT;
			}

			value = o;
		}
		return value;
	}
		
	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return properties.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		TypedProperty p = properties.get(rowIndex);
		if(columnIndex == 0) return p.getType().desc();
		else return p.getViewValue();
	}
}
