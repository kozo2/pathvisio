package visualization.plugins;

import gmmlVision.GmmlVision;
import graphics.GmmlGeneProduct;
import graphics.GmmlGraphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.jdom.Element;

import util.SwtUtils;
import visualization.Visualization;
import data.GmmlDataObject;

/**
 * Provides label for Gene Product
 * @author thomas
 *
 */
public class LabelPlugin extends VisualizationPlugin {
	static final String NAME = "Gene product label";
	static final String XML_ATTR_STYLE = "style";
		
	final static int STYLE_ID = 0;
	final static int STYLE_SYMBOL = 1;
	
	Label labelSidePanel;
	
	int style;
	
	String font = "Arial narrow";
	int fontSize = 10;
	
	public LabelPlugin(Visualization v) {
		super(v);		
	    setIsConfigurable(true);
		setDisplayOptions(DRAWING | SIDEPANEL | TOOLTIP);
		setIsGeneric(true);
		setUseReservedArea(true);
	}

	public String getName() { return NAME; }
	
	public void createSidePanelComposite(Composite parent) {
		Composite comp = new Composite(parent, SWT.NULL);
		labelSidePanel = new Label(comp, SWT.CENTER);
	}

	public void draw(GmmlGraphics g, PaintEvent e, GC buffer) {
		if(g instanceof GmmlGeneProduct) {
			Font f = null;
			
			GmmlDataObject gd = g.getGmmlData();
			Rectangle area = getVisualization().getReservedRegion(this, g).getBounds();
			
			buffer.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
			buffer.fillRectangle(area);
			
			f = SwtUtils.changeFont(f, new FontData(font, getFontSize(), SWT.NONE), e.display);
			String label = getLabelText((GmmlGeneProduct) g);
			Point textSize = buffer.textExtent (label);
			buffer.drawString (label, 
					area.x + (int)(area.width / 2) - (int)(textSize.x / 2),
					area.y + (int)(area.height / 2) - (int)(textSize.y / 2), true);
			
			f.dispose();
		}
	}
	
	private int getFontSize() {
		return (int)(fontSize * GmmlVision.getDrawing().getZoomFactor());
	}
	
	public Composite getToolTipComposite(Composite parent, GmmlGraphics g) {
		if(g instanceof GmmlGeneProduct) {
			Composite comp = new Composite(parent, SWT.NULL);
			Label label = new Label(comp, SWT.CENTER);
			label.setText(getLabelText((GmmlGeneProduct) g));
			return comp;
		}
		return null;
	}

	protected Composite createConfigComposite(Composite parent) {
		Composite comp = new Composite(parent, SWT.NULL);
		comp.setLayout(new FillLayout());
		
		Group typeGroup = new Group(comp, SWT.NULL);
		typeGroup.setLayout(new RowLayout(SWT.VERTICAL));
		typeGroup.setText("Label text");
		final Button id = new Button(typeGroup, SWT.RADIO);
		id.setText("Geneproduct ID");
		final Button symbol = new Button(typeGroup, SWT.RADIO);
		symbol.setText("Geneproduct name");
		
		SelectionAdapter radioAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if		(e.widget == id) 		style = STYLE_ID;
				else if (e.widget == symbol) 	style = STYLE_SYMBOL;
			}
		};
		
		symbol.setSelection(true);
		
		id.addSelectionListener(radioAdapter);
		symbol.addSelectionListener(radioAdapter);
		
		return comp;
	}
		
	public void updateSidePanel(GmmlGraphics g) {
		if(g instanceof GmmlGeneProduct) 
			labelSidePanel.setText(getLabelText((GmmlGeneProduct)g));
		
	}
	
	private String getLabelText(GmmlGeneProduct g) {
		switch(style) {
		case STYLE_ID: 		return g.getID();
		case STYLE_SYMBOL:
		default:			return g.getName();
		}
	}

	public Element toXML() {
		Element elm = super.toXML();
		elm.setAttribute(XML_ATTR_STYLE, Integer.toString(style));
		return elm;
	}
	
	public void loadXML(Element xml) {
		super.loadXML(xml);
		
		String styleStr = xml.getAttributeValue(XML_ATTR_STYLE);
		
		try {
			style = Integer.parseInt(styleStr);
		} catch(NumberFormatException e) {
			GmmlVision.log.error("Unable to get style for " + NAME, e);
		}
	}
}
