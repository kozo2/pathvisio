package visualization;

import gmmlVision.GmmlVision;

import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import util.TableColumnResizer;
import util.Utils;
import visualization.colorset.ColorSetComposite;
import visualization.plugins.VisualizationPlugin;
import data.GmmlGex;

/**
 * Dialog to configure visualizations
 * @author thomas
 *
 */
public class VisualizationDialog extends ApplicationWindow {
	VisualizationSettings settingsComp;
	Composite noneSelectedComp;
	
	StackLayout settingsStack;
	ListViewer visList;
	
	Color nonGenericColor;
	Color genericColor;
	
	int tabItemOnOpen = 0;
	
	public static final int TABITEM_VISUALIZATIONS = 0;
	public static final int TABITEM_COLORSETS = 1;
	final String[] tabItemNames = new String[] {
		"Visualizations", "Color sets"	
	};
	final String[] columnNames = new String[] {
			"Name", "Active", "Drawing", "Side panel", "Tooltip"
	};
	final String[] columnTips = new String[] {
			"Plugin name", "Choose whether to activate this plugin or not",
			"Show this plugin on drawing", "Show this plugin in side panel", 
			"Show this plugin in tooltip when hovering over Gene Product"
	};
	
	public VisualizationDialog(Shell shell) {
		super(shell);
		setBlockOnOpen(true);
	}
	
	public boolean close() {
		GmmlGex.saveXML();
		return super.close();
	}
	
	public void setTabItemOnOpen(int index) {
		tabItemOnOpen = index;
	}
	
	public Control createContents(Composite parent) {
		Shell shell = getShell();
		shell.setSize(700, 500);
		
		Composite content = new Composite(parent, SWT.NULL);
		content.setLayout(new GridLayout());
		
		CTabFolder tabs = new CTabFolder(content, SWT.BORDER);
		tabs.setSimple(false);
		tabs.setSelectionBackground(new Color[] {
				tabs.getSelectionBackground(),
				tabs.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND) }, 
				new int[] { 100 }, true);
		CTabItem visTab = new CTabItem(tabs, SWT.NULL);
		visTab.setControl(createVisualizationComp(tabs));
		visTab.setText(tabItemNames[0]);

		if(GmmlGex.isConnected()) {
			CTabItem colorTab = new CTabItem(tabs, SWT.NULL);
			colorTab.setControl(new ColorSetComposite(tabs, SWT.NULL));
			colorTab.setText(tabItemNames[1]);
		}

		tabs.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final Button ok = new Button(content, SWT.NULL);
		ok.setText("  Ok  ");
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});
		ok.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Visualization v = VisualizationManager.getCurrent();
		if(v != null) visList.setSelection(new StructuredSelection(v));
		
		tabs.setSelection(tabItemOnOpen);
		content.setFocus();
		return tabs;
	}

	Composite createVisualizationComp(Composite parent) {
		Composite content = new Composite(parent, SWT.NULL);
		content.setLayout(new GridLayout(2, false));
		
		createVisualizationsList(content);
		Composite rightComp = new Composite(content, SWT.NULL);
		rightComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		settingsStack = new StackLayout();
		rightComp.setLayout(settingsStack);
		createNoneSelectedComp(rightComp);
		settingsComp = new VisualizationSettings(rightComp, SWT.NULL);
		settingsStack.topControl = noneSelectedComp;
		
		return content;
	}
	
	private void createNoneSelectedComp(Composite parent) {
		noneSelectedComp = new Composite(parent, SWT.NULL);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.justify = true;
		layout.marginLeft = 30;
		noneSelectedComp.setLayout(layout);
		Label l = new Label(noneSelectedComp, SWT.CENTER | SWT.WRAP);
		l.setText("No visualization selected, click 'Add' to add a visualization or select " +
				"one from the list to configure");
	}
	
	private void createVisualizationsList(Composite parent) {
		Composite comp = new Composite(parent, SWT.NULL);
		comp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		comp.setLayout(new GridLayout());
		
		Label label = new Label(comp, SWT.CENTER);
		label.setText("Visualizations");
		
		visList = new ListViewer(comp, SWT.SINGLE | SWT.BORDER);
		visList.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		setListProviders();
		setListListeners();
				
		Composite bComp = new Composite(comp, SWT.NULL);
		bComp.setLayout(new RowLayout(SWT.HORIZONTAL));
		final Button add = new Button(bComp, SWT.PUSH);
		add.setText("Add");
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				newVisualization();
			}
		});
		final Button remove = new Button(bComp, SWT.PUSH);
		remove.setText("Remove");
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeVisualization(visList.getList().getSelectionIndex());
			}
		});
		bComp.pack();
	}
	
	private void setListProviders() {
		visList.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((Visualization)element).getName();
			}
		});
		visList.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object input) {
				return ((List)input).toArray();
			}
			public void dispose() {}
			public void inputChanged(Viewer v, Object oldInput, Object newInput) {}
			
		});
		visList.setInput(VisualizationManager.getVisualizations()); 
	}
	
	private void setListListeners() {
		visList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				Visualization v = getSelectedVisualization();
				VisualizationManager.setCurrent(v);
				showVisualizationSettings(v);
			}
		});
	}
	
	private void showVisualizationSettings(Visualization v) {
		if(v == null) hideVisualizationSettings();
		settingsStack.topControl = settingsComp;
		((Composite)settingsComp.getParent()).layout();
		settingsComp.setInput(v);
	}
	
	private void hideVisualizationSettings() {
		settingsStack.topControl = noneSelectedComp;
		((Composite)noneSelectedComp.getParent()).layout();
	}
	
	private void newVisualization() {
		InputDialog d = new InputDialog(getShell(), 
				"New visualization", "Name: ",
				VisualizationManager.getNewName(), new IInputValidator() {
					public String isValid(String name) {
						return VisualizationManager.nameExists(name) ? "Name already exists" : null;
					}
		});
		if(d.open() == InputDialog.OK) {
			Visualization v = new Visualization(d.getValue());
			VisualizationManager.addVisualization(v);
			visList.refresh();
			visList.setSelection(new StructuredSelection(v));
		}
	}
	
	private void removeVisualization(int index) {
		VisualizationManager.removeVisualization(index);
		visList.refresh();
	}
		
	private Visualization getSelectedVisualization() {
		return (Visualization)
		((IStructuredSelection)visList.getSelection()).getFirstElement();
	}
	
	TableViewer pluginTable;
	class VisualizationSettings extends Composite {		
		Visualization input;
		Text nameText;
		Label description;
		Button pluginConfigButton, firstButton, upButton, downButton, lastButton;
		
		VisualizationSettings(Composite parent, int style) {
			super(parent, style);
			createContents();
		}
		
		void createContents() {
			setLayoutData(new GridData(GridData.FILL_BOTH));
			setLayout(new GridLayout());
			
			createNameComposite(this);
			Group pluginGroup = new Group(this, SWT.NULL);
			pluginGroup.setLayout(new FillLayout());
			pluginGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			pluginGroup.setText("Visualization plugins");
			createPluginComp(pluginGroup);
		}
		
		void createNameComposite(Composite parent) {
			Composite comp = new Composite(parent, SWT.NULL);
			comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			comp.setLayout(new GridLayout(2, false));
			Label l = new Label(comp, SWT.NULL);
			l.setText("Name: ");
			nameText = new Text(comp, SWT.SINGLE | SWT.BORDER);
			nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			nameText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if(input != null) {
						input.setName(nameText.getText());
						visList.update(input, null);
					}
				}
			});
		}
		
		void createPluginComp(Composite parent) {
			Composite comp = new Composite(parent, SWT.NULL);
			comp.setLayout(new GridLayout(2, false));
			
			pluginTable = new TableViewer(createPluginTable(comp));
			pluginTable.setContentProvider(new IStructuredContentProvider() {
				public Object[] getElements(Object input) {
					return ((Visualization)input).getPluginsSorted().toArray();
				}
				public void dispose() {}
				public void inputChanged(Viewer arg0, Object arg1, Object arg2) {}
			});
			pluginTable.setLabelProvider(new PluginTableLabelProvider());
			pluginTable.setCellModifier(new PluginTableModifier());
			CellEditor[] editors = new CellEditor[columnNames.length];
			editors[0] = null;
			editors[1] = editors[2] = editors[3] = editors[4] = new CheckboxCellEditor();
			pluginTable.setCellEditors(editors);
			pluginTable.setColumnProperties(columnNames);
			
			pluginTable.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					VisualizationPlugin p = getSelectedPlugin();
					description.setText(p == null ? "" : "Description:\n" + p.getDescription());
					layout();
					setPluginButtonsEnabled(true);
				}
			});
			
			createPluginButtonComp(comp);
			
			Composite descrComp = createDescriptionComp(comp);
			GridData span = new GridData(GridData.FILL_HORIZONTAL);
			span.horizontalSpan= 2;
			descrComp.setLayoutData(span);
		}
		
		Composite createDescriptionComp(Composite parent) {
			Composite comp = new Composite(parent, SWT.NULL);
			comp.setLayout(new FillLayout());
			description = new Label(comp, SWT.NULL);
			return comp;
		}
		
		Composite createPluginButtonComp(Composite parent) {
			Composite comp = new Composite(parent, SWT.NULL);
			comp.setLayout(new RowLayout(SWT.VERTICAL));
			
			pluginConfigButton = new Button(comp, SWT.PUSH);
			pluginConfigButton.setText("Configure plugin");
			pluginConfigButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					openPluginConfiguration();
				}
			});
			
			Group group = new Group(comp, SWT.NULL);
			group.setText("Drawing order");
			RowLayout rl = new RowLayout(SWT.VERTICAL);
			rl.fill = true;
			rl.pack = false;
			rl.justify = true;
			group.setLayout(rl);
			firstButton = new Button(group, SWT.PUSH);
			firstButton.setText("First");
			firstButton.addSelectionListener(getDrawingOrderListener());
			upButton = new Button(group, SWT.PUSH);
			upButton.setText("Up");
			upButton.addSelectionListener(getDrawingOrderListener());
			downButton = new Button(group, SWT.PUSH);
			downButton.setText("Down");
			downButton.addSelectionListener(getDrawingOrderListener());
			lastButton = new Button(group, SWT.PUSH);
			lastButton.setText("Last");
			lastButton.addSelectionListener(getDrawingOrderListener());
			
			setPluginButtonsEnabled(false);
			return comp;
		}
		
		SelectionListener getDrawingOrderListener() {
			return new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Visualization v = getSelectedVisualization();
					VisualizationPlugin p = getSelectedPlugin();
					if(p == null || v == null) return; //Shouldn't happen
					
					int order = 0;
					if		(e.widget == firstButton)
						order = Utils.ORDER_FIRST;
					else if (e.widget == lastButton)
						order = Utils.ORDER_LAST;
					else if (e.widget == upButton) 
						order = Utils.ORDER_UP;
					else if (e.widget == downButton) 
						order = Utils.ORDER_DOWN;
					v.setDrawingOrder(p, order);
					pluginTable.refresh();
					colorPluginTable();
				}
			};
		}
		void setPluginButtonsEnabled(boolean enable) {
			
			VisualizationPlugin p = getSelectedPlugin();
			boolean doEnable = false;
			boolean config = false;
			if(p != null) {
				doEnable = enable ? p.isActive() : false;
				config = p.isConfigurable();
			}
			pluginConfigButton.setEnabled(doEnable && config);
			setOrderButtonsEnabled(doEnable);
		}
		
		void setOrderButtonsEnabled(boolean enable) {
			firstButton.setEnabled(enable);
			upButton.setEnabled(enable);
			downButton.setEnabled(enable);
			lastButton.setEnabled(enable);
		}
		
		VisualizationPlugin getSelectedPlugin() {
			return (VisualizationPlugin)
				((IStructuredSelection)pluginTable.getSelection()).getFirstElement();
		}
		
		void openPluginConfiguration() {
			VisualizationPlugin p = getSelectedPlugin();
			if(p.isActive()) p.openConfigDialog(getShell());
		}
		
		Table createPluginTable(Composite parent) {
			Composite tableComp = new Composite(parent, SWT.NULL);
			tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			tableComp.setLayout(new FillLayout());
			Table t = new Table(tableComp, SWT.BORDER | SWT.FULL_SELECTION);
			t.setHeaderVisible(true);
				
			int[] alignment = new int[] { 
					SWT.LEFT, SWT.CENTER,
					SWT.CENTER, SWT.CENTER, SWT.CENTER };
			boolean[] resizable = new boolean[] { 
					true, false,
					false, false, false };
			int[] width = new int[] {
					50, 60, 60, 60, 60
			};
			for(int i = 0; i < columnNames.length; i++) {
				TableColumn tc = new TableColumn(t, alignment[i]);
				tc.setText(columnNames[i]);
				tc.setToolTipText(columnTips[i]);
				tc.setWidth(width[i]);
				tc.setResizable(resizable[i]);
			}
				
			t.addControlListener(new TableColumnResizer(t, tableComp));
			return t;
		}
		
		void setInput(Visualization v) { 
			input = v;
			refresh();
		}
		
		void refresh() {
			if(input != null) {
				nameText.setText(input.getName());
				pluginTable.setInput(input);
				colorPluginTable();
			} else {
				nameText.setText("");
				pluginTable.setInput(null);
				hideVisualizationSettings();
			}
		}
		
	}
	
	void colorPluginTable() {
		if(genericColor == null) 
			genericColor = pluginTable.getTable().getBackground();
		if(nonGenericColor == null) 
			nonGenericColor = pluginTable.getTable().getDisplay().getSystemColor(
					SWT.COLOR_INFO_BACKGROUND);
		for(TableItem ti : pluginTable.getTable().getItems()) {			
			VisualizationPlugin p = (VisualizationPlugin)ti.getData();
			ti.setBackground(p.isGeneric() ? genericColor : nonGenericColor);
		}
	}
	
	class PluginTableLabelProvider implements ITableLabelProvider {
		final Image checkTrue = GmmlVision.getImageRegistry().get("checkbox.checked");
		final Image checkFalse = GmmlVision.getImageRegistry().get("checkbox.unchecked");
		final Image checkUnavailable = GmmlVision.getImageRegistry().get("checkbox.unavailable");
		
		public String getColumnText(Object element, int columnIndex) {
			VisualizationPlugin p = (VisualizationPlugin)element;
			
			if(columnIndex == 0) return p.getName();
			return null;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			VisualizationPlugin p = (VisualizationPlugin) element;
			
			if(columnIndex == 1) return p.isActive() ? checkTrue : checkFalse;
			if(p.isActive()) {
				switch(columnIndex) {
				case 2: 
					if(p.canDrawingObject())
						return p.isUseDrawingObject() ? checkTrue : checkFalse;
					else return checkUnavailable;
				case 3: 
					if(p.canSidePanel()) 
						return p.isUseSidePanel() ? checkTrue : checkFalse;
					else return checkUnavailable;
				case 4: 
					if(p.canToolTip())
						return p.isUseToolTip() ? checkTrue : checkFalse;
					else return checkUnavailable;
				default: return null; 
				}
			} else {
				switch(columnIndex) {
				case 2:
				case 3:
				case 4: return checkUnavailable;
				default: return null;
				}
			}
		}
		
		public void addListener(ILabelProviderListener arg0) { }
		
		public void dispose() {	}
		public boolean isLabelProperty(Object arg0, String arg1) { return false; }
		public void removeListener(ILabelProviderListener arg0) { }		
	}
	
	class PluginTableModifier implements ICellModifier {
		public boolean canModify(Object element, String property) {
			VisualizationPlugin p = (VisualizationPlugin) element;
			int index = getColumnIndex(property);
			switch(index) {
			case 0: return false;
			case 1: return true;
			case 2:
			case 3:
			case 4: return p.isActive();
			default: return false;
			}
		}

		public Object getValue(Object element, String property) {
			VisualizationPlugin p = (VisualizationPlugin) element;
			int index = getColumnIndex(property);
			switch(index) {
			case 0: return p.getName();
			case 1: return p.isActive();
			case 2: return p.isUseDrawingObject();
			case 3: return p.isUseSidePanel();
			case 4: return p.isUseToolTip();
			default: return null; //Shouldn't happen
			}
		}

		public void modify(Object element, String property, Object value) {
			int index = getColumnIndex(property);
			TableItem ti = (TableItem) element;
			VisualizationPlugin p = (VisualizationPlugin) ti.getData();

			switch(index) {
			case 1: {
				p.setActive((Boolean) value);
				break;
			}
			case 2: 
				p.setUseDrawingObject((Boolean)value);
				settingsComp.setOrderButtonsEnabled((Boolean)value);
				break;
			case 3: p.setUseSidePanel((Boolean)value); break;
			case 4: p.setUseToolTip((Boolean)value); break;
			}
			
			pluginTable.refresh();
		}
	}
	
	int getColumnIndex(String property) {
		for(int i = 0; i < columnNames.length; i++) 
			if(columnNames[i].equals(property)) return i;
		return -1;
	}

}
