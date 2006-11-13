package visualization.plugins;

import gmmlVision.GmmlVision;
import graphics.GmmlGraphics;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
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
import org.jdom.Element;

import util.ColorConverter;
import util.SwtUtils;
import visualization.Visualization;
import visualization.colorset.ColorSet;
import data.GmmlGex.Sample;
import data.GmmlGex.CachedData.Data;

public class ExpressionColorPlugin extends PluginWithColoredSamples {
	final String NAME = "Color by expression";
	static final String DESCRIPTION = 
		"This plugin colors gene product objects in the pathway by their expression data.";
			
	RGB lineColor;
	boolean drawLine = false;
	
	public ExpressionColorPlugin(Visualization v) {
		super(v);
		setDisplayOptions(DRAWING);
	}

	public String getName() { return NAME; }
	public String getDescription() { return DESCRIPTION; }
		
	void drawNoDataFound(ConfiguredSample s, Rectangle area, PaintEvent e, GC buffer) {
		ColorSet cs = s.getColorSet();
		drawColoredRectangle(area, cs.getColor(ColorSet.ID_COLOR_NO_DATA_FOUND), e, buffer);
	}

	protected void drawSample(ConfiguredSample s, Data data, Rectangle area, PaintEvent e, GC buffer) {
		ColorSample smp = (ColorSample)s;
		if(data.hasMultipleData()) {
			switch(smp.getAmbigiousType()) {
			case ColorSample.AMBIGIOUS_AVG:
				drawSampleAvg(smp, data, area, e, buffer);
				break;
			case ColorSample.AMBIGIOUS_BARS:
				drawSampleBar(smp, data, area, e, buffer);
				break;
			}
		} else {
			ColorSet cs = smp.getColorSet();
			RGB rgb = cs.getColor(data.getSampleData(), smp.getId());
			drawColoredRectangle(area, rgb, e, buffer);
		}
	}

	void drawSampleAvg(ConfiguredSample s, Data data, Rectangle area, PaintEvent e, GC buffer) {
		ColorSet cs = s.getColorSet();
		RGB rgb = cs.getColor(data.getAverageSampleData(), s.getId());
		drawColoredRectangle(area, rgb, e, buffer);
	}
	
	void drawSampleBar(ConfiguredSample s, Data data, Rectangle area, PaintEvent e, GC buffer) {
		ColorSet cs = s.getColorSet();
		List<Data> refdata = data.getRefData();
		int n = refdata.size();
		int left = area.height % n;
		int h = area.height / n;
		for(int i = 0; i < n; i++) {
			RGB rgb = cs.getColor(refdata.get(i).getSampleData(), s.getId());
			Rectangle r = new Rectangle(
					area.x, area.y + i*h,
					area.width, h + (i == n-1 ? left : 0));
			drawColoredRectangle(r, rgb, e, buffer);
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
	
	static final String XML_ATTR_DRAWLINE = "drawLine";
	static final String XML_ELM_LINECOLOR = "lineColor";
	
	protected void saveAttributes(Element xml) {
		xml.setAttribute(XML_ATTR_DRAWLINE, Boolean.toString(drawLine));
		xml.addContent(ColorConverter.createColorElement(XML_ELM_LINECOLOR, getLineColor()));
	}
		
	protected void loadAttributes(Element xml) {
		try {
			lineColor = ColorConverter.parseColorElement(xml.getChild(XML_ELM_LINECOLOR));
			drawLine = Boolean.parseBoolean(xml.getAttributeValue(XML_ATTR_DRAWLINE));
		} catch(Exception e) {
			GmmlVision.log.error("Unable to parse settings for plugin " + NAME, e);
		}
	}
	
	SampleConfigComposite sampleConfigComp;
	Button checkLine;
	Color labelColor;
	Composite createOptionsComp(Composite parent) {
		Group lineGroup = new Group(parent, SWT.NULL);
		lineGroup.setLayout(new GridLayout());
		lineGroup.setText("General options");
		
		checkLine = new Button(lineGroup, SWT.CHECK);
		checkLine.setText("Draw line around sample boxes");
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
	
	protected SampleConfigComposite createSampleConfigComp(Composite parent) {
		return new ColorSampleConfigComposite(parent, SWT.NULL);
	}
	
	protected class ColorSampleConfigComposite extends SampleConfigComposite {
		ConfiguredSample input;
		Button radioBar, radioAvg;
		
		public ColorSampleConfigComposite(Composite parent, int style) {
			super(parent, style);
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
		
		ColorSample getInput() { return (ColorSample)input; }
		
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
					if(e.widget == radioAvg) type = ColorSample.AMBIGIOUS_AVG;
					else type = ColorSample.AMBIGIOUS_BARS;
					if(input != null) getInput().setAmbigiousType(type);
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
				boolean avg = getInput().getAmbigiousType() == ColorSample.AMBIGIOUS_AVG;
				radioAvg.setSelection(avg);
				radioBar.setSelection(!avg);
			}
		}
		
		public void setAllEnabled(boolean enable) {
			SwtUtils.setCompositeAndChildrenEnabled(this, enable);
		}
	}
	
	protected ConfiguredSample createConfiguredSample(Sample s) {
		return new ColorSample(s);
	}
	
	protected ConfiguredSample createConfiguredSample(Element xml) throws Exception {
		return new ColorSample(xml);
	}
	
	protected class ColorSample extends ConfiguredSample {
		public static final int AMBIGIOUS_AVG = 0;
		public static final int AMBIGIOUS_BARS = 1;
		
		int ambigious = AMBIGIOUS_BARS;
		
		public ColorSample(int idSample, String name, int dataType) {
			super(idSample, name, dataType);
		}
		
		public ColorSample(Sample s) {
			super(s.getId(), s.getName(), s.getDataType());
		}
		
		public ColorSample(Element xml) throws Exception {
			super(xml);
		}
		
		int getAmbigiousType() { return ambigious; }
		
		void setAmbigiousType(int type) { 
			ambigious = type;
			fireModifiedEvent();
		}
		
		static final String XML_ATTR_AMBIGIOUS = "ambigious";
				
		protected void saveAttributes(Element xml) {
			xml.setAttribute(XML_ATTR_AMBIGIOUS, Integer.toString(ambigious));
		}
		
		protected void loadAttributes(Element xml) {
			int amb = Integer.parseInt(xml.getAttributeValue(XML_ATTR_AMBIGIOUS));
			setAmbigiousType(amb);
		}
	}
	
	public Composite getToolTipComposite(Composite parent, GmmlGraphics g) { return null; }
	public void createSidePanelComposite(Composite parent) { }
	public void updateSidePanel(GmmlGraphics g) { }

}
