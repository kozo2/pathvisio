package org.pathvisio.gui.swing.propertypanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.pathvisio.data.DataSources;
import org.pathvisio.model.LineStyle;
import org.pathvisio.model.LineType;
import org.pathvisio.model.MappFormat;
import org.pathvisio.model.OrientationType;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PropertyType;

public class TypedProperty {	
	Collection<PathwayElement> elements;
	Object value;
	PropertyType type;
	boolean different;

	public TypedProperty(Collection<PathwayElement> elements, PropertyType type) {
		this(elements, null, type, true);
	}
	
	public TypedProperty(Collection<PathwayElement> elements, Object value, PropertyType type) {
		this(elements, value, type, false);
	}
	
	private TypedProperty(Collection<PathwayElement> elements, Object value, PropertyType type, boolean different) {
		this.elements = elements;
		this.value = value;
		this.type = type;
		this.different = different;
	}

	public void setValue(Object value) {
		//TODO: validate
		this.value = value;
		if(value != null) {
			for(PathwayElement e : elements) {
				e.setProperty(type, value);
			}
		}
	}

	public Object getValue() {
		return value;
	}
	
	public PropertyType getType() {
		return type;
	}

	public boolean hasDifferentValues() { return different; }
	public void setHasDifferentValues(boolean diff) { different = diff; }

	public TableCellRenderer getCellRenderer() {
		if(hasDifferentValues()) return differentRenderer;
		switch(type.type()) {
		case COLOR:
			return colorRenderer;
		case LINETYPE:
			return lineTypeRenderer;
		case LINESTYLE:
			return lineStyleRenderer;
		case DATASOURCE:
			return datasourceRenderer;
		case BOOLEAN:
			return checkboxRenderer;
		case ORIENTATION:
			return orientationRenderer;
		case ORGANISM:
			return organismRenderer;
		case ANGLE:
			return angleRenderer;
		case FONT:
		case GENETYPE:
		}
		return null;
	}

	public TableCellEditor getCellEditor() {
		switch(type.type()) {
		case BOOLEAN:
			return checkboxEditor;
		case DATASOURCE:
			return datasourceEditor;
		case COLOR:
			return colorEditor;
		case LINETYPE:
			return lineTypeEditor;
		case LINESTYLE:
			return lineStyleEditor;
		case ORIENTATION:
			return orientationEditor;
		case ORGANISM:
			return organismEditor;
		case ANGLE:
			return angleEditor;
		default:
			return null;
		}
	}
	
	private static class AngleEditor extends DefaultCellEditor {
		public AngleEditor() {
			super(new JTextField());
		}
		public Object getCellEditorValue() {
			String value = ((JTextField)getComponent()).getText();
			Double d = new Double(0);
			try {
				d = Double.parseDouble(value) * Math.PI / 180;
			} catch(Exception e) {
				//ignore
			}
			return d;
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			value =  (Double)(value) * 180.0 / Math.PI;
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}
	}
	
	private static class ComboEditor extends DefaultCellEditor {
		HashMap label2value;
		boolean useIndex;
		
		public ComboEditor(Object[] labels, boolean useIndex) {
			super(new JComboBox(labels));
			this.useIndex = useIndex;
		}

		public ComboEditor(Object[] labels, Object[] values) {
			this(labels, false);
			if(values != null) {
				if(labels.length != values.length) {
					throw new IllegalArgumentException("Number of labels doesn't equal number of values");
				}
				label2value = new HashMap();
				for(int i = 0; i < labels.length; i++) {
					label2value.put(labels[i], values[i]);
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
	}
	
	private static class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
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

	private static ColorRenderer colorRenderer = new ColorRenderer();
	private static ComboRenderer lineTypeRenderer = new ComboRenderer(LineType.getNames(), LineType.values());
	private static ComboRenderer lineStyleRenderer = new ComboRenderer(LineStyle.getNames());
	private static ComboRenderer datasourceRenderer = new ComboRenderer(DataSources.dataSources);
	private static CheckBoxRenderer checkboxRenderer = new CheckBoxRenderer();
	private static ComboRenderer orientationRenderer = new ComboRenderer(OrientationType.getNames());
	private static ComboRenderer organismRenderer = new ComboRenderer(MappFormat.organism_latin_name);
	
	private static ColorEditor colorEditor = new ColorEditor();
	private static ComboEditor lineTypeEditor = new ComboEditor(LineType.getNames(), true);
	private static ComboEditor lineStyleEditor = new ComboEditor(LineStyle.getNames(), true);
	private static ComboEditor datasourceEditor = new ComboEditor(DataSources.dataSources, false);
	private static DefaultCellEditor checkboxEditor = new DefaultCellEditor(new JCheckBox());
	private static ComboEditor orientationEditor = new ComboEditor(OrientationType.getNames(), true);
	private static ComboEditor organismEditor = new ComboEditor(MappFormat.organism_latin_name, false);
	private static AngleEditor angleEditor = new AngleEditor();
	
	private DefaultTableCellRenderer angleRenderer = new DefaultTableCellRenderer() {
		protected void setValue(Object value) {
			super.setValue( (Double)(value) * 180.0 / Math.PI );
		}
	};
	
	private DefaultTableCellRenderer differentRenderer = new DefaultTableCellRenderer() {
		protected void setValue(Object value) {
			value = "Different values";
			super.setValue(value);
		}
	};

	private static class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			setSelected((Boolean)value);
			return this;
		}
	}
	
	private static class ComboRenderer extends JComboBox implements TableCellRenderer {
		HashMap value2label;
		public ComboRenderer(Object[] values) {
			super(values);
		}
		
		public ComboRenderer(Object[] labels, Object[] values) {
			this(labels);
			if(labels.length != values.length) {
				throw new IllegalArgumentException("Number of labels doesn't equal number of values");
			}
			value2label = new HashMap();
			for(int i = 0; i < labels.length; i++) {
				value2label.put(values[i], labels[i]);
			}
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if(value2label != null) {
				value = value2label.get(value);
			}
			setSelectedItem(value);
			return this;
		}
	}
	
	private static class ColorRenderer extends JLabel implements TableCellRenderer {
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
