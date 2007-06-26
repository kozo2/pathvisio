package org.pathvisio.gui.swing.propertypanel;

import java.awt.Color;

import org.pathvisio.model.PropertyType;

public class TypedProperty {
	public static final Object DIFFERENT = new Object();
	Object value = DIFFERENT;
	PropertyType type;
	
	public TypedProperty(Object value, PropertyType type) {
		this.value = value;
	}
		
	public Object getViewValue() {
		return value;
	}
	
	public String stringValue() {
		return value.toString();
	}
	
	public PropertyType getType() {
		return type;
	}
	
	public static TypedProperty getInstance(Object value, PropertyType type) {
		switch(type.type()) {
		case STRING:
			return new StringProperty(value, type);
		case COLOR:
			return new ColorProperty(value, type);
		default:
			return new TypedProperty(value, type);
		}
	}
	
	public static class StringProperty extends TypedProperty {
		public StringProperty(Object value, PropertyType type) {
			super(value, type);
		}
		public String getViewValue() {
			return (String)value;
		}
	}

	public static class ColorProperty extends TypedProperty {
		public ColorProperty(Object value, PropertyType type) {
			super(value, type);
		}
		public Color getViewValue() {
			return (Color)value;
		}
	}
}
