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
package org.pathvisio.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.pathvisio.biopax.gui.BiopaxCellEditor;
import org.pathvisio.data.DataSources;
import org.pathvisio.model.Color;
import org.pathvisio.model.DataNodeType;
import org.pathvisio.model.LineType;
import org.pathvisio.model.MappFormat;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayEvent;
import org.pathvisio.model.PathwayListener;
import org.pathvisio.model.PropertyClass;
import org.pathvisio.model.PropertyType;
import org.pathvisio.model.ShapeType;
import org.pathvisio.util.ColorConverter;
import org.pathvisio.util.SuggestCellEditor;
import org.pathvisio.util.TableColumnResizer;
import org.pathvisio.view.Graphics;
import org.pathvisio.view.SelectionBox;
import org.pathvisio.view.SelectionBox.SelectionEvent;
import org.pathvisio.view.SelectionBox.SelectionListener;
import org.pathvisio.preferences.Preferences;

/**
 * This class implements the sidepanel where you can edit graphical properties
 * of each object on the pathway.
 */
public class PropertyPanel extends Composite implements PathwayListener, SelectionListener {
	public TableViewer tableViewer;
	CellEditor[] cellEditors = new CellEditor[2];
	TextCellEditor textEditor;
	ColorCellEditor colorEditor;
	ComboBoxCellEditor comboBoxEditor;
	SuggestCellEditor identifierSuggestEditor;
	SuggestCellEditor symbolSuggestEditor;
	BiopaxCellEditor biopaxEditor;
	
	private List<PathwayElement> dataObjects;
	
	private List<PropertyType> attributes;
	
	final static int TYPES_DIFF = ObjectType.MIN_VALID -1;
	final static Object VALUE_DIFF = new Object() {
		public boolean equals(Object o) { return false; }
		public String toString() { return "different values"; }
	};

	/**
	 * Add a {@link PathwayElement} to the list of objects of which 
	 * the properties are displayed
	 * @param o
	 */
	public void addGmmlDataObject(PathwayElement o) {
		if(!dataObjects.contains(o)) {
			if(dataObjects.add(o)) {
				o.addListener(this);
				refresh();
			}
		}
	}
	
	/**
	 * Remove a {@link PathwayElement} from the list of objects of which 
	 * the properties are displayed
	 * @param o
	 */
	public void removeGmmlDataObject(PathwayElement o) {
		if(dataObjects.remove(o)) {
			o.removeListener(this);
			refresh();
		}
	}
	
	/**
	 * Clear the list of objects of which the properties are displayed
	 */
	public void clearGmmlDataObjects() {
		for(PathwayElement o : dataObjects) o.removeListener(this);
		dataObjects.clear();
		refresh();
	}
	
	/**
	 * Refresh the table and attributes to display
	 */
	void refresh() {
		setAttributes();
		tableViewer.refresh();
	}
	
	int getAggregateType() {
		int type = TYPES_DIFF;
		for(int i = 0; i < dataObjects.size(); i++) {
			PathwayElement g = dataObjects.get(i);
			
			if(i != 0 && type != g.getObjectType()) return TYPES_DIFF;
			
			type = g.getObjectType();
		}
		return type;
	}
	
	Object getAggregateValue(PropertyType key) {
		Object value = VALUE_DIFF;
		for(int i = 0; i < dataObjects.size(); i++) {
			PathwayElement g = dataObjects.get(i);
			Object o = g.getProperty(key);
			if(i != 0 && (o == null || !o.equals(value))) return VALUE_DIFF;

			value = o;
		}
		return value;
	}
		
	/**
	 * Sets the attributes for the selected objects
	 * Only attributes that are present in all objects in the selection will be
	 * added to the attributes list and shown in the property table
	 */
	public void setAttributes ()
	{
		HashMap<PropertyType, Integer> master = new HashMap<PropertyType, Integer>();
		for (PathwayElement o : dataObjects)
		{
			// get attributes. Only get advanced attributes if the preferences say so.
			for (PropertyType attr : o.getAttributes(
					 Engine.getPreferences().getBoolean(Preferences.PREF_SHOW_ADVANCED_ATTR)))
			{
				if (master.containsKey(attr))
				{
					// increment
					master.put(attr, master.get(attr) + 1);
				}
				else
				{
					// set to 1
					master.put(attr, 1);
				}
			}
		}
		attributes.clear();
		for (PropertyType attr : master.keySet())
		{
			if (master.get(attr) == dataObjects.size())
			{
				attributes.add(attr);
			}
		}
		// sortAttributes();
		Collections.sort (attributes);		
	}
	
//	void sortAttributes() {
//		Collections.sort(attributes, new Comparator() {
//			public int compare(Object o1, Object o2) {
//				return o1.ordinal() - o2.ordinal();
//			}
//		});
//	}

	final static String[] colNames = new String[] {"Property", "Value"};
				
	PropertyPanel(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new FillLayout());
		Table t = new Table(this, style);
		TableColumn tcName = new TableColumn(t, SWT.LEFT);
		TableColumn tcValue = new TableColumn(t, SWT.LEFT);
		tcName.setText(colNames[0]);
		tcValue.setText(colNames[1]);
		tcName.setWidth(80);
		tcValue.setWidth(70);
		tableViewer = new TableViewer(t);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setContentProvider(tableContentProvider);
		tableViewer.setLabelProvider(tableLabelProvider);
		
		cellEditors[1] = cellEditors[0] = textEditor = new TextCellEditor(tableViewer.getTable());
		colorEditor = new ColorCellEditor(tableViewer.getTable());
		comboBoxEditor = new ComboBoxCellEditor(tableViewer.getTable(), new String[] {""});
		identifierSuggestEditor = new GdbCellEditor(tableViewer.getTable(), GdbCellEditor.TYPE_IDENTIFIER);
		symbolSuggestEditor = new GdbCellEditor(tableViewer.getTable(), GdbCellEditor.TYPE_SYMBOL);
		biopaxEditor = new BiopaxCellEditor(tableViewer.getTable(), "...");
		
		tableViewer.setCellEditors(cellEditors);
		tableViewer.setColumnProperties(colNames);
		tableViewer.setCellModifier(cellModifier);
		
		t.addControlListener(new TableColumnResizer(t, t.getParent()));
		
		dataObjects = new ArrayList<PathwayElement>();
		attributes = new ArrayList<PropertyType>();
		tableViewer.setInput(attributes);
		
		SelectionBox.addListener(this);
	}
	
	/**
	 * return the right cell editor for a certain object. Will return
	 * one of existing editors. In the case of a list of possible values, 
	 * a comboboxeditor will be set up with the proper values for
	 * the drop down list.
	 */
	final static String[] orientation_names = {"Top", "Right", "Bottom", "Left"};
	final static String[] linestyle_names = {"Solid", "Dashed"};
	final static String[] boolean_names = {"false", "true"};
	final static String[] shape_names = ShapeType.getNames();
	final static String[] linetype_names = {
			"Line", "Arrow", "TBar", "Receptor", "LigandSquare", 
			"ReceptorSquare", "LigandRound", "ReceptorRound"}; 
	final static String[] genetype_names = DataNodeType.getNames();
	
	private CellEditor getCellEditor(Object element)
	{
		PropertyType key = (PropertyType)element;
		PropertyClass type = key.type();
		switch(type)
		{
			case FONT:				
			case STRING:
			case DOUBLE:
			case ANGLE:
			case INTEGER: 	return textEditor;
			case COLOR: 	return colorEditor;
			case LINETYPE:
				comboBoxEditor.setItems(linetype_names);
				return comboBoxEditor;
			case SHAPETYPE:
				comboBoxEditor.setItems(shape_names);
				return comboBoxEditor;
			case DATASOURCE:			
				comboBoxEditor.setItems(DataSources.dataSources);
				return comboBoxEditor;
			case ORIENTATION:
				comboBoxEditor.setItems(orientation_names);
				return comboBoxEditor;
			case LINESTYLE:
				comboBoxEditor.setItems(linestyle_names);
				return comboBoxEditor;
			case BOOLEAN:
				comboBoxEditor.setItems(boolean_names);
				return comboBoxEditor;
			case ORGANISM:
				comboBoxEditor.setItems(MappFormat.organism_latin_name);
				return comboBoxEditor;
			case GENETYPE:
				comboBoxEditor.setItems(genetype_names);
				return comboBoxEditor;
			case DB_ID:
				return identifierSuggestEditor;
			case DB_SYMBOL:
				return textEditor;
			case BIOPAXREF:
				return biopaxEditor;
				
		}
		return textEditor;
	}
	
	private ICellModifier cellModifier = new ICellModifier()
	{
		public boolean canModify(Object element, String property) {
			if (!colNames[1].equals(property))
			{
				return false;
			}
			
			cellEditors[1] = getCellEditor(element);
			return true;
		}

		/**
		 * Getvalue is the value that is passed to the Cell Editor when it is 
		 * activated.
		 * It should return an Integer object for ComboboxCellEditors.
		 */
		public Object getValue(Object element, String property) 
		{
			PropertyType key = (PropertyType)element;
			Object value = getAggregateValue(key);
			if(value == VALUE_DIFF) {
				return VALUE_DIFF.toString();
			}
			switch(key.type())
			{
				case ANGLE:
				{
					Double x = Math.round((Double)(value) * 1800.0 / Math.PI) / 10.0;
					return x.toString();
				}
				case DOUBLE:
				{
					Double x = Math.round((Double)(value) * 100.0) / 100.0;
					return x.toString();
				}
				case INTEGER: 
					return value.toString();
				case ORGANISM:
					return Arrays.asList(MappFormat.organism_latin_name).indexOf((String)value);
				case GENETYPE:
					return Arrays.asList(genetype_names).indexOf((String)value);
				case STRING:
				case FONT:
					return value == null ? "" : (String)value;
				case DATASOURCE:
					return DataSources.lDataSources.indexOf((String)value);				
				// for all combobox types:
				case BOOLEAN:
					return ((Boolean)value) ? 1 : 0;
				case SHAPETYPE:
					return (((ShapeType)value).ordinal());
				case LINETYPE:
					return (((LineType)value).ordinal());
				case COLOR:
					if(value instanceof Color)
						value = ColorConverter.toRGB((Color)value);
					return (RGB)value;
				case ORIENTATION:
				case LINESTYLE:
				{
//					try 
//					{
						return (Integer)value;
//					}
//					catch (ClassCastException e)
//					{
//						MessageDialog.openWarning(getShell(), "warning", "Can't cast " + value + " to Integer!");
//					}
				}
				case DB_ID:
				case DB_SYMBOL:
					if(value instanceof String) return (String)value;
					if(value instanceof PropertyPanel.AutoFillData) 
						return ((PropertyPanel.AutoFillData)value).getMainValue();
				case BIOPAXREF:
					return value;
					
			}
			return null;
		}
		
		public void modify(Object element, String property, Object value) {
			PropertyType key = (PropertyType)((TableItem)element).getData();
			
			if(value == VALUE_DIFF || value == VALUE_DIFF.toString()) {
				return;
			}
			/*
			 * Here, we transform the output of the cell editor
			 * to a value understood by PathwayElement.SetProperty().
			 * 
			 * The output of a comboboxCellEditor is Integer.
			 * The output of a textCellEditor is String.
			 * 
			 * For linetype and shapetype we go from Integer to Integer. easy
			 * For boolean, we go from Integer to Boolean
			 * For Double / Integer, we go from String to Double
			 * For Datasource, we go from Integer to String.
			 */
			switch(key.type())
			{
			case ANGLE: 	
				try 
				{ 
					// convert degrees (property editor) to radians (model)
					value = Double.parseDouble((String)value) * Math.PI / 180;					
					break;
				} 
				catch(Exception e) 
				{
					// invalid input, ignore
					return; 
				}
			case DOUBLE: 	
				try 
				{ 
					value = Double.parseDouble((String)value); 
					break; 
				} 
				catch(Exception e) 
				{
					// invalid input, ignore
					return; 
				}
			case INTEGER: 	
				try 
				{ 
					value = Integer.parseInt((String)value); 
					break; 
				}
				catch(Exception e) 
				{ 
					// invalid input, ignore 
					return; 
				}
			case DATASOURCE:
				if((Integer)value == -1) return; //Nothing selected
				value = DataSources.lDataSources.get((Integer)value);
				break;
			case BOOLEAN:
				if ((Integer)value == 0)
				{
					value = new Boolean (false);
				}
				else
				{
					value = new Boolean (true);
				}
				break;
			case ORGANISM:
				if((Integer)value == -1) return; //Nothing selected
				value = MappFormat.organism_latin_name[(Integer)value];
				break;
			case GENETYPE:
				if((Integer)value == -1) return; //Nothing selected
				value = genetype_names[(Integer)value];
				break;
			case COLOR:
				value = ColorConverter.fromRGB((RGB)value);
			case DB_SYMBOL:
			case DB_ID:
				if(value instanceof PropertyPanel.AutoFillData) {
					PropertyPanel.AutoFillData adf = (PropertyPanel.AutoFillData)value;
					for(PathwayElement o : dataObjects) {
						if(o.getObjectType() == ObjectType.DATANODE) {
							adf.fillData(o);
						}
					}
					value = adf.getMainValue();
				}
				break;
			}
			for(PathwayElement o : dataObjects) {
				o.setProperty(key, value);
			}
			tableViewer.refresh();
			Engine.getVPathway().redrawDirtyRect();
		}
	};
	
	private IStructuredContentProvider tableContentProvider = new ArrayContentProvider();
	
	private ITableLabelProvider tableLabelProvider = new ITableLabelProvider() {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			PropertyType key = (PropertyType)element;
			switch(columnIndex) {
				case 0:
					return key.desc();					
				case 1:
					//TODO: prettier labels for different value types
					if(attributes.contains(key))
					{
						Object value = getAggregateValue(key);
						if (value == null)
						{
							return null;
						}
						else 
						{
							switch (key.type())
							{
								case ANGLE:
								{
									if (value instanceof Double)
									{
										Double x = Math.round((Double)(value) * 1800.0 / Math.PI) / 10.0;
										return x.toString();
									}
									else
										return value.toString();
								}
								case DOUBLE:								
									if (value instanceof Double)
									{
										Double x = Math.round((Double)(value) * 10.0) / 10.0;
										return x.toString();
									}
									else
										return value.toString();
										
								case BOOLEAN:
								{
									if (value instanceof Boolean)
									{
										return (Boolean)(value) ? "true" : "false";
									}
									else
										return value.toString();
								}
								case LINETYPE:
								{
									if (value instanceof Integer)
										return linetype_names[(Integer)(value)];
									else
										return value.toString();
								}
								case LINESTYLE:
								{
									if (value instanceof Integer)
										return linestyle_names[(Integer)(value)];
									else
										return value.toString();
								}
								case ORIENTATION:
								{
									if (value instanceof Integer)
										return orientation_names[(Integer)(value)];
									else
										return value.toString();									
								}
								case SHAPETYPE:
								{
									if (value instanceof Integer)
										return shape_names[(Integer)(value)];
									else
										return value.toString();
								}
								case COLOR:
									if(value instanceof Color) {
										return ColorConverter.toRGB((Color)value).toString();
									}
								default:
									return value.toString();
							}
						}
					}
			}
			return null;
			}
		
		public void addListener(ILabelProviderListener listener) { }
		public void dispose() {}
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}
		public void removeListener(ILabelProviderListener listener) { }
	};

	public void gmmlObjectModified(PathwayEvent e) {
		tableViewer.refresh();
	}

	//TODO: implement all attribute types as subclasses of MyType.
//	class MyType {
//		abstract String getColumnText(Object value);
//		abstract Object adjustedValue(Object value);
//		abstract CellEditor getCellEditor()
//	}
	
	public void drawingEvent(SelectionEvent e) {
		switch(e.type) {
		case SelectionEvent.OBJECT_ADDED:
			if(e.affectedObject instanceof Graphics)
				addGmmlDataObject(((Graphics)e.affectedObject).getGmmlData());
			break;
		case SelectionEvent.OBJECT_REMOVED:
			if(e.affectedObject instanceof Graphics)
				removeGmmlDataObject(((Graphics)e.affectedObject).getGmmlData());
			break;
		case SelectionEvent.SELECTION_CLEARED:
			 clearGmmlDataObjects();
			break;
		}
		
	}

	static class AutoFillData {
		PropertyType mProp;
		Object mValue;
		HashMap<PropertyType, String> values;
		
		private boolean doGuess = false;
		
		public AutoFillData(PropertyType mainProperty, String mainValue) {
			values = new HashMap<PropertyType, String>();
			mProp = mainProperty;
			mValue = mainValue;
			setProperty(mainProperty, mainValue);
		}
		
		public void setProperty(PropertyType property, String value) {
			values.put(property, value);
		}
		
		public PropertyType getMainProperty() { return mProp; }
		public Object getMainValue() { return mValue; }
		
		public String getProperty(PropertyType property) { return values.get(property); }
		
		public Set<PropertyType> getProperties() { return values.keySet(); }
		
		public void fillData(PathwayElement o) {
			if(doGuess) guessData(o);
			for(PropertyType p : getProperties()) {
				Object vNew = getProperty(p);
				o.setProperty(p, vNew);
			}
		}
		
		public void setDoGuessData(boolean doGuessData) {
			doGuess = doGuessData;
		}
		
		protected void guessData(PathwayElement o) {
		}
	}
}
