package visualization.plugins;

import gmmlVision.GmmlVision;
import graphics.GmmlGeneProduct;
import graphics.GmmlGraphics;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.jdom.Element;

import util.SwtUtils;
import visualization.Visualization;
import data.GmmlDataObject;
import data.GmmlGex;
import data.GmmlGdb.IdCodePair;
import data.GmmlGex.Sample;
import data.GmmlGex.CachedData.Data;

/**
 * Provides label for Gene Product
 * @author thomas
 *
 */
public class ExpressionAsTextPlugin extends VisualizationPlugin {
	static final String NAME = "Text by expression";
	static final String DESCRIPTION = 
		"This plugin displays expression data for a given set of samples as text";
	
	static final FontData DEFAULT_FONTDATA = new FontData("Arial narrow", 10, SWT.NORMAL);
			
	final static String SEP = ", ";	
	int roundTo = 2;
	boolean mean = false;
			
	FontData fontData;
	Set<Sample> useSamples = new LinkedHashSet<Sample>();
	
	public ExpressionAsTextPlugin(Visualization v) {
		super(v);		
	    setIsConfigurable(true);
		setDisplayOptions(DRAWING | TOOLTIP);
		setIsGeneric(false);
		setUseProvidedArea(false);
	}
	
	public String getName() { return NAME; }
	public String getDescription() { return DESCRIPTION; }
	
	public void draw(GmmlGraphics g, PaintEvent e, GC buffer) {
		if(g instanceof GmmlGeneProduct) {
			GmmlGeneProduct gp = (GmmlGeneProduct) g;
			Data data = GmmlGex.getCachedData(new IdCodePair(gp.getID(), gp.getSystemCode()));
			if(data == null || useSamples.size() == 0) return;
			
			Font f = new Font(e.display, getFontData());
			
			GmmlDataObject gd = g.getGmmlData();
			int startx = (int)(gd.getLeft() + gd.getWidth());
			int starty = (int)(gd.getTop() + gd.getHeight() / 2);
			

			buffer.setFont(f);
			int w = 0, i = 0;
			for(Sample s : useSamples) {
				String str = getDataString(s, data, SEP + "\n") + 
				(++i == useSamples.size() ? "" : SEP);
				Point size = buffer.textExtent(str);
				buffer.drawText(str, startx + w, starty - size.y / 2);
				w += size.x;
			}
				
			f.dispose();
		}
	}
	
	public Composite getToolTipComposite(Composite parent, GmmlGraphics g) {
		if(g instanceof GmmlGeneProduct) {
			GmmlGeneProduct gp = (GmmlGeneProduct)g;
			Data data = GmmlGex.getCachedData(new IdCodePair(gp.getID(), gp.getSystemCode()));
			if(data == null) return null;
			
			Group group = new Group(parent, SWT.NULL);
			group.setLayout(new GridLayout(2, false));
			group.setText("Expression data");
			
			for(Sample s : useSamples) {
				Label labelL = new Label(group, SWT.NULL);
				labelL.setText(getLabelLeftText(s));
				Label labelR = new Label(group, SWT.NULL);
				labelR.setText(getLabelRightText(s, data));
			}
			SwtUtils.setCompositeAndChildrenBackground(group, 
					group.getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			return group;
		} else return null;
	}
	
	String getLabelLeftText(Sample s) {
		return s.getName() + ":";
	}
	
	String getLabelRightText(Sample s, Data data) {
		return getDataString(s, data, SEP);
	}
	
	String getDataString(Sample s, Data data, String multSep) {	
		Object str = null;
		if(data.hasMultipleData())
			str = formatData(getSampleStringMult(s, data, multSep));
		else
			str =  formatData(getSampleData(s, data));
		return str == null ? "" : str.toString();
	}
	
	Object getSampleData(Sample s, Data data) {
		return data.getSampleData().get(s.getId());
	}
	
	Object getSampleStringMult(Sample s, Data data, String sep) {
		if(mean) return data.getAverageSampleData().get(s.getId());
		
		List<Data> refdata = data.getRefData();
		StringBuilder strb = new StringBuilder();
		for(Data d : refdata) {
			strb.append(formatData(d.getSampleData().get(s.getId())) + sep);
		}
		return strb.substring(0, strb.length() - (sep).length());
	}
	
	Object formatData(Object data) {
		if(data instanceof Double) {
			double d = (Double)data;
			int dec = (int)Math.pow(10, getRoundTo());
			double rounded = (double)(Math.round(d * dec)) / dec;
			data = dec == 1 ? Integer.toString((int)rounded) : Double.toString(rounded);
		}
		return data;
	}
	
	void setFontData(FontData fd) {
		if(fd != null) {
			fontData = fd;
			fireModifiedEvent();
		}
	}
	
	int getFontSize() {
		return getFontData().getHeight();
	}
	
	FontData getFontData() {
		return getFontData(false);
	}
	
	FontData getFontData(boolean adjustZoom) {
		FontData fd = fontData == null ? DEFAULT_FONTDATA : fontData;
		if(adjustZoom) {
			fd = new FontData(fd.name, fd.height, fd.style);
			fd.setHeight((int)Math.ceil(fd.getHeight() * GmmlVision.getDrawing().getZoomFactor()));
		}
		return fd;
	}
	
	void addUseSample(Sample s) {
		if(s != null) {
			useSamples.add(s);
			fireModifiedEvent();
		}
	}
	
	void addUseSamples(IStructuredSelection selection) {
		Iterator it = selection.iterator();
		while(it.hasNext()) {
			useSamples.add((Sample)it.next());
		}
		fireModifiedEvent();
	}
	
	void removeUseSamples(IStructuredSelection selection) {
		Iterator it = selection.iterator();
		while(it.hasNext()) {
			useSamples.remove((Sample)it.next());
		}
		fireModifiedEvent();
	}
	
	public int getRoundTo() { return roundTo; }
	
	public void setRoundTo(int dec) {
		if(dec >= 0 && dec < 10) {
			roundTo = dec;
			fireModifiedEvent();
		}
	}
	
	public void setCalcMean(boolean doCalcMean) {
		mean = doCalcMean;
		fireModifiedEvent();
	}
	
	protected Composite createConfigComposite(Composite parent) {
		Composite comp = new Composite(parent, SWT.NULL);
		comp.setLayout(new GridLayout());
		
		Composite sampleComp = createSampleComp(comp);
		sampleComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite options = createOptionsComp(comp);
		options.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		return comp;
	}
		
	Composite createSampleComp(Composite parent) {
		Composite sampleComp = new Composite(parent, SWT.NULL);
		sampleComp.setLayout(new GridLayout(2, false));

		Label expl = new Label(sampleComp, SWT.NULL);
		expl.setText("- Click on a sample in the left list to add to the samples that will" +
				"be shown as text\n" +
				"- Click on a sample on the right list to remove");
		GridData span = new GridData();
		span.horizontalSpan = 2;
		expl.setLayoutData(span);
		
		Label slabel = new Label(sampleComp, SWT.NULL);
		slabel.setText("All samples:");
		
		Label ulabel = new Label(sampleComp, SWT.NULL);
		ulabel.setText("Selected samples:");
		
		LabelProvider lprov = new LabelProvider() {
			public String getText(Object element) {
				return ((Sample)element).getName();
			}
		};
		
		final ListViewer samples = new ListViewer(sampleComp, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
		samples.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		samples.setContentProvider(new ArrayContentProvider());
		samples.setLabelProvider(lprov);
			
		final ListViewer use = new ListViewer(sampleComp, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
		use.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		use.setContentProvider(new ArrayContentProvider());
		use.setLabelProvider(lprov);
		
		ISelectionChangedListener slist = new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if(event.getSource() == use) 
					removeUseSamples((IStructuredSelection)event.getSelection());
				else
					addUseSamples((IStructuredSelection)event.getSelection());
				use.refresh();
			}
		};
		use.addSelectionChangedListener(slist);
		samples.addSelectionChangedListener(slist);
		
		samples.setInput(GmmlGex.getSamples(-1));
		use.setInput(useSamples);
		
		return sampleComp;
	}
	
	Composite createOptionsComp(Composite parent) {
		Group optionsComp = new Group(parent, SWT.NULL);
		optionsComp.setText("Options");
		optionsComp.setLayout(new RowLayout(SWT.VERTICAL));
		final Button font = new Button(optionsComp, SWT.PUSH);
		font.setText("Change font");
		font.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FontDialog fd = new FontDialog(font.getShell());
				fd.setFontList(new FontData[] { getFontData() });
				setFontData(fd.open());
			}
		});
		createRoundComp(optionsComp);
		final Button doAvg = new Button(optionsComp, SWT.CHECK);
		doAvg.setText("Show mean value of data with ambigious reporters");
		doAvg.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCalcMean(doAvg.getSelection());
			}
		});
		doAvg.setSelection(mean);
		return optionsComp;
	}
	
	Composite createRoundComp(Composite parent) {
		Composite roundComp = new Composite(parent, SWT.NULL);
		roundComp.setLayout(new RowLayout(SWT.VERTICAL));
		Label lb = new Label(roundComp, SWT.NULL);
		lb.setText("Number of decimals to round numeric data to:");
		final Spinner sp = new Spinner(roundComp, SWT.BORDER);
		sp.setMaximum(9);
		sp.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setRoundTo(sp.getSelection());
			}
		});
		sp.setSelection(getRoundTo());
		sp.pack();
		return roundComp;
	}
	
	
	public void updateSidePanel(Collection<GmmlGraphics> objects) { }
	public void createSidePanelComposite(Composite parent) { }

	static final String XML_ATTR_FONTDATA = "font";
	static final String XML_ATTR_AVG = "mean";
	static final String XML_ATTR_ROUND = "round-to";
	static final String XML_ELM_ID = "sample-id";
	public Element toXML() {
		Element elm = super.toXML();
		elm.setAttribute(XML_ATTR_FONTDATA, getFontData().toString());
		elm.setAttribute(XML_ATTR_ROUND, Integer.toString(getRoundTo()));
		elm.setAttribute(XML_ATTR_AVG, Boolean.toString(mean));
		for(Sample s : useSamples) {
			Element selm = new Element(XML_ELM_ID);
			selm.setText(Integer.toString(s.getId()));
			elm.addContent(selm);
		}
		return elm;
	}
	
	public void loadXML(Element xml) {
		super.loadXML(xml);
		for(Object o : xml.getChildren(XML_ELM_ID)) {
			try {
				int id = Integer.parseInt(((Element)o).getText());
				useSamples.add(GmmlGex.getSample(id));
			} catch(Exception e) { GmmlVision.log.error("Unable to add sample", e); }
		}
		roundTo = Integer.parseInt(xml.getAttributeValue(XML_ATTR_ROUND));
		fontData = new FontData(xml.getAttributeValue(XML_ATTR_FONTDATA));
		mean = Boolean.parseBoolean(xml.getAttributeValue(XML_ATTR_AVG));
	}
}

