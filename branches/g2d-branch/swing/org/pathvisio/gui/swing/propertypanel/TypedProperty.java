package org.pathvisio.gui.swing.propertypanel;

import java.awt.Color;
import java.awt.Component;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PropertyType;

public class TypedProperty<T> {	
	Collection<PathwayElement> elements;
	T value;
	PropertyType type;
	boolean different;
	
	private TypedProperty(Collection<PathwayElement> elements, T value, PropertyType type, boolean different) {
		this.elements = elements;
		this.value = value;
		this.type = type;
		this.different = different;
	}
	
	public void setValue(T value) {
		//TODO: validate
		this.value = value;
		if(value != null) {
			for(PathwayElement e : elements) {
				e.setProperty(type, value);
			}
		}
	}
	
	public T getViewValue() {
		return value;
	}
	
	public PropertyType getType() {
		return type;
	}
		
	public boolean hasDifferentValues() { return different; }
	public void setHasDifferentValues(boolean diff) { different = diff; }
	
	public TableCellRenderer getCellRenderer() {
		return hasDifferentValues() ? differentRenderer : null;
	}
	
//	public TableEditor getCellEditor() {
//		
//	}
	
	public static TypedProperty getInstance(Collection<PathwayElement> elements, Object value, PropertyType type) {
		return getInstance(elements, value, type, false);
	}
	
	public static TypedProperty getInstance(Collection<PathwayElement> elements, PropertyType type) {
		return getInstance(elements, null, type, true);
	}
	
	private static TypedProperty getInstance(Collection<PathwayElement> elements, Object value, PropertyType type, boolean different) {
		switch(type.type()) {
		case STRING:
			return new TypedProperty<String>(elements, (String)value, type, different);
		case COLOR:
			return new TypedProperty<Color>(elements, (Color)value, type, different);
		default:
			return new TypedProperty<Object>(elements, value, type, different);
		}
	}
	
	private DefaultTableCellRenderer differentRenderer = new DefaultTableCellRenderer() {
		protected void setValue(Object value) {
			value = "Different values";
			super.setValue(value);
		}
	};
}
