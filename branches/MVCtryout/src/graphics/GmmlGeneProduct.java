package graphics;

import gmmlVision.GmmlVision;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import util.ColorConverter;
import util.SwtUtils;
import data.*;

/**
 * This class implements a geneproduct and 
 * provides methods to resize and draw it.
 */
public class GmmlGeneProduct extends GmmlGraphicsShape
{
	private static final long serialVersionUID = 1L;
	private static final int INITIAL_FONTSIZE = 10;
	public static final int INITIAL_WIDTH = 80;
	public static final int INITIAL_HEIGHT = 20;
	public static final RGB INITIAL_FILL_COLOR = new RGB(255, 255, 255);
	
	double fontSizeDouble;
	int fontSize;

	// note: not the same as color!
	RGB fillColor = INITIAL_FILL_COLOR;
	GmmlGpColor gpColor;
	
	/**
	 * Constructor for this class
	 * @param canvas - the GmmlDrawing this geneproduct will be part of
	 */
	public GmmlGeneProduct(GmmlDrawing canvas)
	{
		super(canvas);
		
		drawingOrder = GmmlDrawing.DRAW_ORDER_GENEPRODUCT;
				
		gpColor = new GmmlGpColor(this);
		
		this.fontSizeDouble = INITIAL_FONTSIZE / canvas.getZoomFactor();
		this.fontSize = (int)this.fontSizeDouble;
		gdata.setObjectType(ObjectType.GENEPRODUCT);
	}
	
	/**
	 * Constructor for this class
	 * @param x - the upper left corner x coordinate
	 * @param y - the upper left corner y coordinate
	 * @param width - the width
	 * @param height - the height
	 * @param geneLabel - the gene label as it will be shown as a label
	 * @param xref - 
	 * @param color - the color this geneproduct will be painted
	 * @param canvas - the GmmlDrawing this geneproduct will be part of
	 */
	public GmmlGeneProduct(double x, double y, double width, double height, String geneLabel, String xref, RGB color, GmmlDrawing canvas){
		this(canvas);
		
		gdata.setCenterX(x);
		gdata.setCenterY(y);
		gdata.setWidth(width);
		gdata.setHeight(height);
		gdata.setGeneID(geneLabel);
		gdata.setXref(xref);
		gdata.setColor(color);

		setHandleLocation();
	}
	
	/**
	 * Constructor for mapping a JDOM Element.
	 * @param e	- the GMML element which will be loaded as a GmmlGeneProduct
	 * @param canvas - the GmmlDrawing this GmmlAGmmlGeneProductrc will be part of
	 */
	public GmmlGeneProduct(Element e, GmmlDrawing canvas) {
		this(canvas);
		
		gdata.mapShapeData(e);
		gdata.mapColor(e);
		gdata.mapNotesAndComment(e);
		gdata.mapGeneProductData(e);
		
		setHandleLocation();
	}
	
	public String getName()
	{
		return gdata.getGeneProductName();
	}
	
	/**
	 * Fetches the gene identifier from the Jdom representation
	 */
	public String getId() {
		return gdata.getGeneID();
	}
	
	public void setFontSize(double size) {
		fontSizeDouble = size;
		fontSize = (int)size;
	}
	
	/**
	 * Looks up the systemcode for this gene in {@link GmmlData#sysName2Code}
	 * @param systemName	The system name (as in gmml)
	 * @return	The system code or an empty string if the system is not found
	 */
	public String getSystemCode()
	{
		String systemCode = "";
		if(GmmlData.sysName2Code.containsKey(gdata.getDataSource())) 
			systemCode = GmmlData.sysName2Code.get(gdata.getDataSource());
		return systemCode;
	}
	
	private Text t;
	public void createTextControl()
	{		
		Color background = canvas.getShell().getDisplay()
		.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
		
		Composite textComposite = new Composite(canvas, SWT.NONE);
		textComposite.setLayout(new GridLayout());
		textComposite.setLocation(getCenterX(), getCenterY() - 10);
		textComposite.setBackground(background);
		
		Label label = new Label(textComposite, SWT.CENTER);
		label.setText("Specify gene name:");
		label.setBackground(background);
		t = new Text(textComposite, SWT.SINGLE | SWT.BORDER);
				
		t.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				disposeTextControl();
			}
		});
				
		t.setFocus();
		
		Button b = new Button(textComposite, SWT.PUSH);
		b.setText("OK");
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				disposeTextControl();
			}
		});
		
		textComposite.pack();
	}
	
	protected void disposeTextControl()
	{	
		markDirty();
		gdata.setGeneID (t.getText());
		markDirty();
		//TODO: implement listener. 
		//canvas.updatePropertyTable(this);
		Composite c = t.getParent();
		c.setVisible(false);
		c.dispose();
		
		canvas.redrawDirtyRect();
	}
	
	public void createJdomElement(Document doc) 
	{		
		Element e = new Element("GeneProduct");
		e.addContent(new Element("Graphics"));			
		gdata.updateGeneProductData(e);
		gdata.updateNotesAndComment(e);
		gdata.updateColor(e);
		gdata.updateShapeData(e);
		doc.getRootElement().addContent(e);
	}
		
	public void adjustToZoom(double factor)
	{
		gdata.setLeft(gdata.getLeft() * factor);
		gdata.setTop(gdata.getTop() * factor);
		gdata.setWidth(gdata.getWidth() * factor);
		gdata.setHeight(gdata.getHeight() * factor);
		fontSizeDouble *= factor;
		fontSize = (int)fontSizeDouble;
		setHandleLocation();
	}

	protected void draw(PaintEvent e, GC buffer)
	{
		Color c = null;
		if(isSelected())
		{
			c = SwtUtils.changeColor(c, selectColor, e.display);
		}
		else 
		{
			c = SwtUtils.changeColor(c, gdata.getColor(), e.display);
		}
		
		buffer.setForeground(c);
		buffer.setLineStyle (SWT.LINE_SOLID);
		buffer.setLineWidth (1);		
		
		buffer.drawRectangle (
			(int)(gdata.getLeft()),
			(int)(gdata.getTop()),
			(int)gdata.getWidth(),
			(int)gdata.getHeight()
		);
		
		buffer.setClipping (
				(int)(gdata.getLeft()) + 1,
				(int)(gdata.getTop()) + 1,
				(int)gdata.getWidth() - 1,
				(int)gdata.getHeight() - 1
			);
		
		gpColor.draw(e, buffer);
		
		Region r = null;
		buffer.setClipping(r);
		
		drawHighlight(e, buffer);
		
		c.dispose();
	}
	
	protected void draw(PaintEvent e)
	{
		draw(e, e.gc);
	}
	
	public void drawHighlight(PaintEvent e, GC buffer)
	{
		if(isHighlighted())
		{
			Color c = null;
			c = SwtUtils.changeColor(c, highlightColor, e.display);
			buffer.setForeground(c);
			buffer.setLineWidth(2);
			buffer.drawRectangle (
					(int)(gdata.getStartX()) - 1,
					(int)(gdata.getStartY()) - 1,
					(int)gdata.getWidth() + 3,
					(int)gdata.getHeight() + 3
				);
			if(c != null) c.dispose();
		}
	}
	
}