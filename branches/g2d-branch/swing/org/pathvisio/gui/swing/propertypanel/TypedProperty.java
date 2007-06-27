package org.pathvisio.gui.swing.propertypanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.pathvisio.model.LineStyle;
import org.pathvisio.model.LineType;
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

	public T getValue() {
		return value;
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
		if(hasDifferentValues()) return differentRenderer;
		switch(type) {
		case COLOR:
			return colorRenderer;
		case LINETYPE:
			return lineTypeRenderer;
		case LINESTYLE:
			return lineStyleRenderer;
		default: return null;
		}
	}

	public TableCellEditor getCellEditor() {
		switch(type) {
		case COLOR:
			return colorEditor;
		default: return null;
		}
	}
	
	public Component getEditComponent() {
		return (Component)getCellRenderer();
	}

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
		case LINETYPE:
			return new TypedProperty<LineType>(elements, (LineType)value, type, different);
		case LINESTYLE:
			return new TypedProperty<Integer>(elements, (Integer)value, type, different);	
		default:
			return new TypedProperty<Object>(elements, value, type, different);
		}
	}

	private ColorEditor colorEditor = new ColorEditor();
	
	private class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
		Color currentColor;
		JButton button;
		JColorChooser colorChooser;
		JDialog dialog;
		protected static final String EDIT = "edit";

		public ColorEditor() {
			button = new JButton();
			button.setActionCommand("edit");
			button.addActionListener(this);
			button.setBorderPainted(false);

			colorChooser = new JColorChooser();
			dialog = JColorChooser.createDialog(button,
					"Pick a Color",
					true,  //modal
					colorChooser,
					this,  //OK button handler
					null); //no CANCEL button handler
		}

		public void actionPerformed(ActionEvent e) {
			if (EDIT.equals(e.getActionCommand())) {
				button.setBackground(currentColor);
				colorChooser.setColor(currentColor);
				dialog.setVisible(true);

				fireEditingStopped(); //Make the renderer reappear.

			} else {
				currentColor = colorChooser.getColor();
			}
		}

		public Object getCellEditorValue() {
			return currentColor;
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value,
				boolean isSelected,
				int row,
				int column) {
			currentColor = (Color)value;
			return button;
		}
	}

	private ColorRenderer colorRenderer = new ColorRenderer();
	private ComboRenderer lineTypeRenderer = new ComboRenderer(LineType.getNames());
	private ComboRenderer lineStyleRenderer = new ComboRenderer(LineStyle.getNames());

	private DefaultTableCellRenderer differentRenderer = new DefaultTableCellRenderer() {
		protected void setValue(Object value) {
			value = "Different values";
			super.setValue(value);
		}
	};

	private class ComboRenderer extends JComboBox implements TableCellRenderer {
		public ComboRenderer(Object[] items) {
			super(items);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			setSelectedItem(value);
			return this;
		}
	}

	private class ColorRenderer extends JLabel implements TableCellRenderer {
		Border unselectedBorder = null;
		Border selectedBorder = null;
		boolean isBordered = true;

		public ColorRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(
				JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
			Color newColor = (Color)color;
			setBackground(newColor);
			if (isBordered) {
				if (isSelected) {
					if (selectedBorder == null) {
						selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
								table.getSelectionBackground());
					}
					setBorder(selectedBorder);
				} else {
					if (unselectedBorder == null) {
						unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
								table.getBackground());
					}
					setBorder(unselectedBorder);
				}
			}

			setToolTipText("RGB value: " + newColor.getRed() + ", "
					+ newColor.getGreen() + ", "
					+ newColor.getBlue());
			return this;
		}
	}
}
