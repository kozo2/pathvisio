package visualization.plugins;

import gmmlVision.GmmlVision;
import graphics.GmmlGeneProduct;
import graphics.GmmlGraphics;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jdom.Element;

import R.RFunctionLoader.ZScore.SetConfigDialog;

import util.ColorConverter;
import util.SwtUtils;
import util.TableColumnResizer;
import visualization.Visualization;
import visualization.colorset.ColorSet;
import visualization.colorset.ColorSetManager;
import data.GmmlGex;
import data.GmmlGdb.IdCodePair;
import data.GmmlGex.Sample;
import data.GmmlGex.CachedData.Data;

public class ExpressionColorPlugin extends VisualizationPlugin {
	static final String NAME = "Color by expression";
	static final String[] useSampleColumns = { "sample", "color set" };
	static final RGB LINE_COLOR_DEFAULT = new RGB(0, 0, 0);
	
	List<ConfiguredSample> useSamples = new ArrayList<ConfiguredSample>();
	
	RGB lineColor;
	boolean drawLine;
	
	public ExpressionColorPlugin(Visualization v) {
		super(v);
		setDisplayOptions(DRAWING);
		setIsConfigurable(true);
		setIsGeneric(false);
		setUseReservedArea(true);
	}

	public String getName() {
		return NAME;
	}
		
	public void draw(GmmlGraphics g, PaintEvent e, GC buffer) {
		if(!(g instanceof GmmlGeneProduct)) return;
		if(useSamples.size() == 0) return; //Nothing to draw
		
		GmmlGeneProduct gp = (GmmlGeneProduct) g;
		
		Region region = getVisualization().getReservedRegion(this, g);
		Rectangle area = region.getBounds();
		
		int nr = useSamples.size();
		int w = area.width / nr;
		for(int i = 0; i < nr; i++) {
			Rectangle r = new Rectangle(
					area.x + w * i,
					area.y,
					w, area.height);
			ConfiguredSample s = useSamples.get(i);
			Data data = GmmlGex.getCachedData(new IdCodePair(gp.getID(), gp.getSystemCode()));
			
			if(data == null) {
				drawNoDataFound(s, area, e, buffer);
			} else if(data.hasMultipleData()) {
				switch(s.getAmbigiousType()) {
				case ConfiguredSample.AMBIGIOUS_AVG:
					drawSampleAvg(s, data, r, e, buffer);
					break;
				case ConfiguredSample.AMBIGIOUS_BARS:
//					drawSampleBar(gp, s, e, buffer, r);
					break;
				}
			} else drawSample(s, data, r, e, buffer);
		}
		
		region.dispose();
	}
	
	public void drawNoDataFound(ConfiguredSample s, Rectangle area, PaintEvent e, GC buffer) {
		ColorSet cs = s.getColorSet();
		if(cs != null) {
			drawColoredRectangle(area, cs.getColor(ColorSet.ID_COLOR_NO_DATA_FOUND), e, buffer);
		}
	}
	
	public void drawSample(ConfiguredSample s, Data data, Rectangle area, PaintEvent e, GC buffer) {
		ColorSet cs = s.getColorSet();
		if(cs != null) {
			RGB rgb = cs.getColor(data.getSampleData(), s.getId());
			drawColoredRectangle(area, rgb, e, buffer);
		}
	}
	
	public void drawSampleAvg(ConfiguredSample s, Data data, Rectangle area, PaintEvent e, GC buffer) {
		ColorSet cs = s.getColorSet();
		if(cs != null) {
			RGB rgb = cs.getColor(data.getAverageSampleData(), s.getId());
			drawColoredRectangle(area, rgb, e, buffer);
		}
	}
	
	void drawColoredRectangle(Rectangle r, RGB rgb, PaintEvent e, GC buffer) {
		Color c = null;
		Color lc = null;
		
		c = SwtUtils.changeColor(c, rgb, e.display);
		
		buffer.setBackground(c);
		
		buffer.fillRectangle(r);
		if(drawLine) {
			lc = SwtUtils.changeColor(lc, getLineColor(), e.display);
			buffer.setForeground(lc);
			buffer.drawRectangle(r);
		}
		
		c.dispose();
		if(lc != null) lc.dispose();
	}
	
	void setLineColor(RGB rgb) {
		if(rgb != null)	{
			lineColor = rgb;
			fireModifiedEvent();
		}
	}
	
	RGB getLineColor() { return lineColor == null ? LINE_COLOR_DEFAULT : lineColor; }
	
	void setDrawLine(boolean draw) {
		drawLine = draw;
		fireModifiedEvent();
	}
		
	void addUseSample(Sample s) {
		if(s != null) {
			useSamples.add(new ConfiguredSample(s));
			fireModifiedEvent();
		}
	}
	
	void addUseSamples(IStructuredSelection selection) {
		Iterator it = selection.iterator();
		while(it.hasNext()) {
			useSamples.add(new ConfiguredSample((Sample)it.next()));
		}
		fireModifiedEvent();
	}
	
	void removeUseSamples(IStructuredSelection selection) {
		Iterator it = selection.iterator();
		while(it.hasNext()) {
			useSamples.remove((ConfiguredSample)it.next());
		}
		fireModifiedEvent();
	}
	
	void removeUseSample(ConfiguredSample s) {
		if(s != null) {
			useSamples.remove(s);
			fireModifiedEvent();
		}
	}
	
	static final String XML_ATTR_DRAWLINE = "drawLine";
	static final String XML_ELM_LINECOLOR = "lineColor";
	public Element toXML() {
		Element xml = super.toXML();
		xml.setAttribute(XML_ATTR_DRAWLINE, Boolean.toString(drawLine));
		xml.addContent(ColorConverter.createColorElement(XML_ELM_LINECOLOR, getLineColor()));
		for(ConfiguredSample s : useSamples) xml.addContent(s.toXML());
		return xml;
	}
	
	public void loadXML(Element xml) {
		super.loadXML(xml);
		try {
			lineColor = ColorConverter.parseColorElement(xml.getChild(XML_ELM_LINECOLOR));
			drawLine = Boolean.parseBoolean(xml.getAttributeValue(XML_ATTR_DRAWLINE));
		} catch(Exception e) {
			GmmlVision.log.error("Unable to parse settings for plugin " + NAME, e);
		}
		for(Object o : xml.getChildren(ConfiguredSample.XML_ELEMENT)) {
			try {
				useSamples.add(new ConfiguredSample((Element)o));
			} catch(Exception e) {
				GmmlVision.log.error("Unable to add sample to plugin " + NAME, e);
			}
		}
			
	}
	
	TableViewer useSampleTable;
	SampleConfigComposite sampleConfigComp;
	Button checkLine;
	protected Composite createConfigComposite(Composite parent) {
		Composite config = new Composite(parent, SWT.NULL);
		config.setLayout(new GridLayout());
		
		Composite optionsComp = createOptionsComp(config);
		optionsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite samplesComp = createSamplesComp(config);
		samplesComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		sampleConfigComp = new SampleConfigComposite(config, SWT.NULL);
		sampleConfigComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return config;
	}
	
	Composite createSamplesComp(Composite parent) {
		Group samplesGroup = new Group(parent, SWT.NULL);
		samplesGroup.setText("Samples to display");
		samplesGroup.setLayout(new GridLayout(3, false));
		
		Label sampleLabel = new Label(samplesGroup, SWT.NULL);
		sampleLabel.setText("Available samples:");
		GridData span = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		span.horizontalSpan = 2;
		sampleLabel.setLayoutData(span);
		
		Label useSampleLabel = new Label(samplesGroup, SWT.NULL);
		useSampleLabel.setText("Selected samples");
		useSampleLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
				
		final ListViewer sampleList = new ListViewer(samplesGroup, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		sampleList.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		sampleList.setContentProvider(new ArrayContentProvider());
		sampleList.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((Sample)element).getName();
			}
		});
		sampleList.setInput(GmmlGex.getSamples(Types.REAL));
		
		Composite buttons = new Composite(samplesGroup, SWT.NULL);
		buttons.setLayout(new RowLayout(SWT.VERTICAL));
		final Button add = new Button(buttons, SWT.PUSH);
		add.setText(">");
		final Button remove = new Button(buttons, SWT.PUSH);
		remove.setText("<");
		
		SelectionListener buttonListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(e.widget == add) {
					addUseSamples((IStructuredSelection)sampleList.getSelection());
				} else {
					removeUseSamples((IStructuredSelection)useSampleTable.getSelection());
				}
				useSampleTable.refresh();
			}
		};
		
		add.addSelectionListener(buttonListener);
		remove.addSelectionListener(buttonListener);
		
		Composite tableComp = new Composite(samplesGroup, SWT.NULL);
		tableComp.setLayout(new GridLayout());
		tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		Table t = new Table(tableComp, SWT.BORDER | SWT.FULL_SELECTION);
		t.setHeaderVisible(true);
		
		TableColumn tcnm = new TableColumn(t, SWT.LEFT);
		tcnm.setText(useSampleColumns[0]);
		tcnm.setToolTipText("The samples that will be displayed in the gene box");
		TableColumn tccs = new TableColumn(t, SWT.LEFT);
		tccs.setText(useSampleColumns[1]);
		tccs.setToolTipText("The color set to apply on this sample");
		t.addControlListener(new TableColumnResizer(t, tableComp));
		useSampleTable = new TableViewer(t);
		useSampleTable.setContentProvider(new ArrayContentProvider());
		useSampleTable.setLabelProvider(new ITableLabelProvider() {
			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex) {
				case 0: return ((Sample)element).getName();
				case 1: return ((ConfiguredSample)element).getColorSetName();
				default: return null;
				}
			}
			public Image getColumnImage(Object element, int columnIndex) { return null; }
			public void addListener(ILabelProviderListener listener) { }
			public void dispose() { }
			public boolean isLabelProperty(Object element, String property) { return false; }
			public void removeListener(ILabelProviderListener listener) { }
		});
		useSampleTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				sampleConfigComp.setInput(getSelectedUseSample());
			}
			
		});
		useSampleTable.setColumnProperties(useSampleColumns);
		final ComboBoxCellEditor editor = new ComboBoxCellEditor(useSampleTable.getTable(), ColorSetManager.getColorSetNames());
		useSampleTable.setCellEditors(new CellEditor[] { new TextCellEditor(), editor });
		useSampleTable.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return 
				property.equals(useSampleColumns[1]) &&
				editor.getItems().length > 0;
			}
			public Object getValue(Object element, String property) {
				if(property.equals(useSampleColumns[1]))
					return ((ConfiguredSample)element).getColorSetIndex();
				return null;
			}
			public void modify(Object element, String property, Object value) {
				if(property.equals(useSampleColumns[1])) {
					TableItem ti = (TableItem)element;
					((ConfiguredSample)ti.getData()).setColorSetIndex((Integer)value);
					useSampleTable.refresh();
				}
			}
			
		});
		
		useSampleTable.setInput(useSamples);
		
		return samplesGroup;
	}
	
	Color labelColor;
	Composite createOptionsComp(Composite parent) {
		Group lineGroup = new Group(parent, SWT.NULL);
		lineGroup.setLayout(new GridLayout());
		lineGroup.setText("General options");
		
		checkLine = new Button(lineGroup, SWT.CHECK);
		checkLine.setText("Draw line around color box");
		checkLine.setSelection(drawLine);
		
		final Composite colorComp = new Composite(lineGroup, SWT.NULL);
		colorComp.setLayout(new GridLayout(3, false));
		
		Label label = new Label(colorComp, SWT.NULL);
		label.setText("Line color: ");
		final CLabel colorLabel = new CLabel(colorComp, SWT.SHADOW_IN);
		colorLabel.setLayoutData(SwtUtils.getColorLabelGrid());
		labelColor = SwtUtils.changeColor(labelColor, getLineColor(), colorLabel.getDisplay());
		colorLabel.setBackground(labelColor);
		
		Button colorButton = new Button(colorComp, SWT.PUSH);
		colorButton.setText("...");
		colorButton.setLayoutData(SwtUtils.getColorLabelGrid());
		colorButton.addListener(SWT.Selection | SWT.Dispose, new Listener() {
			public void handleEvent(Event e) {
				switch(e.type) {
				case SWT.Selection:
					RGB rgb = new ColorDialog(colorLabel.getShell()).open();
					if(rgb != null) {
						labelColor = SwtUtils.changeColor(labelColor, rgb, e.display);
						colorLabel.setBackground(labelColor);
						setLineColor(rgb);
					}
				break;
				case SWT.Dispose:
					labelColor.dispose();
				break;
				}
			}
		});
		
		checkLine.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean doDraw = checkLine.getSelection();
				SwtUtils.setCompositeAndChildrenEnabled(colorComp, doDraw);
				setDrawLine(doDraw);
			}
		});
				
		return lineGroup;
	}
	
	ConfiguredSample getSelectedUseSample() {
		return (ConfiguredSample)
		((IStructuredSelection)useSampleTable.getSelection()).getFirstElement();
	}
	
	class SampleConfigComposite extends Composite {
		ConfiguredSample input;
		Button radioBar, radioAvg;
		
		public SampleConfigComposite(Composite parent, int style) {
			super(parent, style);
			createContents();
		}
		
		void createContents() {
			setLayout(new FillLayout());
			Group group = new Group(this, SWT.NULL);
			group.setText("Selected sample confguration");
			group.setLayout(new GridLayout());
			
			Composite ambComp = createAmbigiousComp(group);
			ambComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			setInput(null);
		}
		
		Composite createAmbigiousComp(Composite parent) {
			Group ambGroup = new Group(parent, SWT.NULL);
			ambGroup.setText("How to treat ambigious reporters?");
			
			ambGroup.setLayout(new RowLayout(SWT.VERTICAL));
			radioAvg = new Button(ambGroup, SWT.RADIO);
			radioAvg.setText("Use average value for color");
			radioBar = new Button(ambGroup, SWT.RADIO);
			radioBar.setText("Divide in horizontal bars");
			SelectionListener listener = new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int type;
					if(e.widget == radioAvg) type = ConfiguredSample.AMBIGIOUS_AVG;
					else type = ConfiguredSample.AMBIGIOUS_BARS;
					if(input != null) input.setAmbigiousType(type);
				}
			};
			radioAvg.addSelectionListener(listener);
			radioBar.addSelectionListener(listener);
			radioAvg.setSelection(true);
			
			return ambGroup;
		}
		
		void changeColorSet(int index) {
			if(input != null) 
				input.setColorSetIndex(index);
		}
		
		public void setInput(ConfiguredSample s) {
			input = s;
			refresh();
		}
		
		public void refresh() {
			if(input == null) setAllEnabled(false);
			else {
				setAllEnabled(true);
				boolean avg = input.getAmbigiousType() == ConfiguredSample.AMBIGIOUS_AVG;
				radioAvg.setSelection(avg);
				radioBar.setSelection(!avg);
			}
		}
		
		public void setAllEnabled(boolean enable) {
			SwtUtils.setCompositeAndChildrenEnabled(this, enable);
		}
	}
	
	class ConfiguredSample extends Sample {
		public static final int AMBIGIOUS_AVG = 0;
		public static final int AMBIGIOUS_BARS = 1;
		
		int ambigious;
		int colorSetIndex = 0;
		
		public ConfiguredSample(int idSample, String name, int dataType) {
			super(idSample, name, dataType);
		}
		
		public ConfiguredSample(Sample s) {
			super(s.getId(), s.getName(), s.getDataType());
		}
		
		public ConfiguredSample(Element xml) throws Exception {
			super(0, "", 0);
			loadXML(xml);
		}
		
		public void setColorSetIndex(int index) { colorSetIndex = index; }
		
		public ColorSet getColorSet() { return ColorSetManager.getColorSet(colorSetIndex); }
		
		public String getColorSetName() {
			ColorSet cs = getColorSet();
			return cs == null ? "no colorsets available" : cs.getName();
		}
		public int getColorSetIndex() { return colorSetIndex; }
		
		public int getAmbigiousType() { return ambigious; }
		
		public void setAmbigiousType(int type) { ambigious = type; }
		
		static final String XML_ELEMENT = "sample";
		static final String XML_ATTR_ID = "id";
		static final String XML_ATTR_AMBIGIOUS = "ambigious";
		static final String XML_ATTR_COLORSET = "colorset";
		
		public Element toXML() {
			Element xml = new Element(XML_ELEMENT);
			xml.setAttribute(XML_ATTR_ID, Integer.toString(getId()));
			xml.setAttribute(XML_ATTR_AMBIGIOUS, Integer.toString(ambigious));
			xml.setAttribute(XML_ATTR_COLORSET, Integer.toString(colorSetIndex));
			return xml;
		}
		
		public void loadXML(Element xml) throws Exception {
			int id = Integer.parseInt(xml.getAttributeValue(XML_ATTR_ID));
			int csi = Integer.parseInt(xml.getAttributeValue(XML_ATTR_COLORSET));
			int amb = Integer.parseInt(xml.getAttributeValue(XML_ATTR_AMBIGIOUS));
			Sample s = GmmlGex.getSamples().get(id);
			setId(id);
			setName(s.getName());
			setDataType(s.getDataType());
			setAmbigiousType(amb);
			setColorSetIndex(csi);
		}
	}
	
	public Composite getToolTipComposite(Composite parent, GmmlGraphics g) { return null; }
	public void createSidePanelComposite(Composite parent) { }
	public void updateSidePanel(GmmlGraphics g) { }

}
