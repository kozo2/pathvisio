package graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Transform;
import org.jdom.Document;
import org.jdom.Element;

import util.SwtUtils;
import data.*;

/**
 * This class implements a brace and provides 
 * methods to resize and draw it
 */
public class GmmlBrace extends GmmlGraphicsShape
{
	private static final long serialVersionUID = 1L;
	
	public static final int INITIAL_PPO = 10;
				
	/**
	 * Constructor for this class
	 * @param canvas - the GmmlDrawing this brace will be part of
	 */
	public GmmlBrace(GmmlDrawing canvas)
	{
		super(canvas);
		drawingOrder = GmmlDrawing.DRAW_ORDER_BRACE;
		gdata.setObjectType(ObjectType.BRACE);
	}
	
	/**
	 * Constructor for this class
	 * @param centerX - center x coordinate
	 * @param centerY - center y coordinate
	 * @param width - width
	 * @param ppo - picpoint ofset
	 * @param orientation - orientation (0 for top, 1 for right, 2 for bottom, 3 for left)
	 * @param color - the color this brace will be painted
	 * @param canvas - the GmmlDrawing this brace will be part of
	 */
	public GmmlBrace(double centerX, double centerY, double width, double ppo, int orientation, RGB color, GmmlDrawing canvas, Document doc)
	{
		this(canvas);
		
		gdata.setCenterX(centerX);
		gdata.setCenterY(centerY);
		gdata.setWidth(width);
		gdata.setHeight(ppo);
		gdata.setOrientation(orientation);
		gdata.setColor(color);

		setHandleLocation();
		createJdomElement(doc);
	}

	/**
	 * Constructor for mapping a JDOM Element.
	 * @param e	- the GMML element which will be loaded as a GmmlBrace
	 * @param canvas - the GmmlDrawing this GmmlBrace will be part of
	 */
	public GmmlBrace(Element e, GmmlDrawing canvas) {
		this(canvas);
		
		gdata.jdomElement = e;
		
		gdata.mapNotesAndComment();
		gdata.mapBraceData();
		gdata.mapColor();		
		setHandleLocation();
	}
	
	/**
	 * Updates the JDom representation of this arc
	 */
	public void updateJdomElement() {
		if(gdata.jdomElement != null) {
			gdata.updateNotesAndComment();
			gdata.updateColor();
			gdata.updateBraceData();
		}
	}
	
	protected void createJdomElement(Document doc) {
		if(gdata.jdomElement == null) {
			gdata.jdomElement = new Element("Brace");
			gdata.jdomElement.addContent(new Element("Graphics"));
			
			doc.getRootElement().addContent(gdata.jdomElement);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#adjustToZoom()
	 */
	protected void adjustToZoom(double factor)
	{
		gdata.setLeft(gdata.getLeft() * factor);
		gdata.setTop(gdata.getTop() * factor);
		gdata.setWidth(gdata.getWidth() * factor);
		gdata.setHeight(gdata.getHeight() * factor);	}

	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#draw(java.awt.Graphics)
	 */
	protected void draw(PaintEvent e, GC buffer)
	{		
		Color c = null;
		if (isSelected())
		{
			c = SwtUtils.changeColor(c, selectColor, e.display);
		}
		else 
		{
			c = SwtUtils.changeColor(c, gdata.getColor(), e.display);
		}
		buffer.setForeground (c);
		buffer.setLineStyle (SWT.LINE_SOLID);
		buffer.setLineWidth (2);
		
		Transform tr = new Transform(e.display);
		rotateGC(buffer, tr);
		
		int cx = getCenterX();
		int cy = getCenterY();
		int w = (int)gdata.getWidth();
		int d = (int)gdata.getHeight();
		
		buffer.drawLine (cx + d/2, cy, cx + w/2 - d/2, cy); //line on the right
		buffer.drawLine (cx - d/2, cy, cx - w/2 + d/2, cy); //line on the left
		buffer.drawArc (cx - w/2, cy, d, d, -180, -90); //arc on the left
		buffer.drawArc (cx - d, cy - d,	d, d, -90, 90); //left arc in the middle
		buffer.drawArc (cx, cy - d, d, d, -90, -90); //right arc in the middle
		buffer.drawArc (cx + w/2 - d, cy, d, d, 0, 90); //arc on the right
		
		buffer.setTransform(null);
		
		c.dispose();
	}
	
	protected void draw(PaintEvent e)
	{
		draw(e, e.gc);
	}
}