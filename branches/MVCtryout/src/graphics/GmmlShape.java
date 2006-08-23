package graphics;

import gmmlVision.GmmlVision;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Transform;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import util.ColorConverter;
import util.SwtUtils;
import data.GmmlData;
import data.*;

/**
 * This class represents a GMMLShape, which can be a 
 * rectangle or ellips, depending of its type.
 */
public class GmmlShape extends GmmlGraphicsShape
{
	private static final long serialVersionUID = 1L;
			
	/**
	 * Constructor for this class
	 * @param canvas - the GmmlDrawing this GmmlShape will be part of
	 */
	public GmmlShape(GmmlDrawing canvas)
	{
		super(canvas);
		drawingOrder = GmmlDrawing.DRAW_ORDER_SHAPE;
		gdata.setObjectType(ObjectType.SHAPE);
	}
		
	/**
	 * Constructor for this class
	 * @param x - the upper left corner x coordinate
	 * @param y - the upper left corner y coordinate
	 * @param width - the width
	 * @param height - the height
	 * @param type - this shapes type (0 for rectangle, 1 for ellipse)
	 * @param color - the color this geneproduct will be painted
	 * @param canvas - the GmmlDrawing this geneproduct will be part of
	 */
	public GmmlShape(double x, double y, double width, double height, int type, RGB color, double rotation, GmmlDrawing canvas)
	{
		this(canvas);
		
		gdata.setCenterX (x);
		gdata.setCenterY (y);
		gdata.setWidth(width);
		gdata.setHeight(height);
		gdata.setColor(color);
		gdata.setShapeType(type);
		gdata.setRotation (rotation);

		setHandleLocation();
	}
	
	public GmmlShape (GmmlDrawing canvas, GmmlDataObject _gdata) {
		this(canvas);
		gdata = _gdata;		
		setHandleLocation();
	}
	
	protected void adjustToZoom(double factor)
	{
		gdata.setLeft(gdata.getLeft() * factor);
		gdata.setTop(gdata.getTop() * factor);
		gdata.setWidth(gdata.getWidth() * factor);
		gdata.setHeight(gdata.getHeight() * factor);
		setHandleLocation();
	}
	
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
		
		Transform tr = new Transform(e.display);
		rotateGC(buffer, tr);
		
		int startX = (int)gdata.getLeft();
		int startY = (int)gdata.getTop();
		int width = (int)gdata.getWidth();
		int height = (int)gdata.getHeight();
		
		switch (gdata.getShapeType())
		{
			case ShapeType.RECTANGLE: 
				buffer.setLineWidth (1);
				buffer.drawRectangle (
					startX,
					startY,
					width,
					height
				);
				break;
			case ShapeType.OVAL:
				
				buffer.setLineWidth (1);
				buffer.drawOval (
					startX, 
					startY,
					width, 
					height
				);
				break;
			case ShapeType.ARC:
				buffer.setLineWidth (2);
				buffer.drawArc(
						startX, 
						startY,
						width, 
						height,
					 0, -180
				);
				break;
		}

		buffer.setTransform(null);
		
		c.dispose();
		tr.dispose();
	}
	
	protected void draw(PaintEvent e)
	{
		draw(e, e.gc);
	}
	
}