import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Component;
import java.awt.Polygon;
import javax.swing.JPanel;
import javax.swing.JComponent;

import org.jdom.Attribute;
import org.jdom.Element;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
 
public class GmmlLine extends GmmlGraphics
{
	private List attributes;
	int ID;
	
	double startx;
	double starty;
	double endx;
	double endy;
	double mx;
	double my;
	
	int style;
	int type;
	
	Color color;
	
	GmmlDrawing canvas;
	Line2D line;
	
	Element jdomElement;
	
	GmmlHandle handlecenter = new GmmlHandle(0, this);
	GmmlHandle handleStart	= new GmmlHandle(3, this);
	GmmlHandle handleEnd		= new GmmlHandle(4, this);
	
	public GmmlLine(GmmlDrawing canvas)
	{
		this.canvas = canvas;
		
		canvas.addElement(handlecenter);
		canvas.addElement(handleStart);
		canvas.addElement(handleEnd);
	}

	/**
	 * Constructor for this class
	 * <BR>
	 * <DL><B>Parameters<B>
	 * <DD>Double startx		- the lineshapes start x coordinate
	 * <DD>Double starty		- the lineshapes start y coordinate
	 * <DD>Double endx			- the lineshapes end x coordinate
	 * <DD>Double endy			- the lineshapes end y coordinate
	 * <DD>Color color			- the color this lineshape will be painted
	 * <DD>GmmlDrawing canvas	- the GmmlDrawing this lineshape will be part of
	 * <DL>
	 */		
	public GmmlLine(int x1, int y1, int x2, int y2, Color color, GmmlDrawing canvas)
	{
		this.startx 	= startx;
		this.starty 	= starty;
		this.endx 	= endx;
		this.endy 	= endy;
		
		this. color = color;
		
		line = new Line2D.Double(startx, starty, endx, endy);
		
		this.canvas = canvas;

		setHandleLocation();
		canvas.addElement(handlecenter);
		canvas.addElement(handleStart);
		canvas.addElement(handleEnd);	
	}

	/**
	 * Constructor for mapping a JDOM Element.
	 * <BR>
	 * <DL><B>Parameters</B>
	 * <DD> Element e			- the GMML element which will be loaded as a GmmlShape
	 * <DD> GmmlDrawing canvas	- the GmmlDrawing this GmmlShape will be part of
	 * <DL>
	 */
	public GmmlLine (Element e, GmmlDrawing canvas) {
		this.jdomElement = e;
		// List the attributes
		attributes = Arrays.asList(new String[] {
				"StartX", "StartY", "EndX", "EndY", 
				"Color", "Style", "Type"
		});
		mapAttributes(e);
		
		line = new Line2D.Double(startx, starty, endx, endy);
		
		this.canvas = canvas;
		
		setHandleLocation();
		canvas.addElement(handlecenter);
		canvas.addElement(handleStart);
		canvas.addElement(handleEnd);
	}

	/**
	 * Maps attributes to internal variables.
	 * <BR>
	 * <DL><B>Parameters</B>
	 * <DD> Element e	- the element that will be loaded as a GmmlShape
	 * <DL>
	 */
	private void mapAttributes (Element e) {
		// Map attributes
		System.out.println("> Mapping element '" + e.getName()+ "'");
		Iterator it = e.getAttributes().iterator();
		while(it.hasNext()) {
			Attribute at = (Attribute)it.next();
			int index = attributes.indexOf(at.getName());
			String value = at.getValue();
			switch(index) {
					case 0: // StartX
						this.startx = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 1: // StartY
						this.starty = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 2: // EndX
						this.endx = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 3: // EndY
						this.endy = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 4: // Color
						this.color = GmmlColor.convertStringToColor(value); break;
					case 5: // Style
						List styleMappings = Arrays.asList(new String[] {
								"Solid", "Broken"
						});
						if(styleMappings.indexOf(value) > -1)
							this.type = styleMappings.indexOf(value);
						break;
					case 6: // Type
						List typeMappings = Arrays.asList(new String[] {
								"Line", "Arrow"
						});
						if(typeMappings.indexOf(value) > -1)
							this.type = typeMappings.indexOf(value);
						break;
					case -1:
						System.out.println("\t> Attribute '" + at.getName() + "' is not recognized");
			}
		}
		// Map child's attributes
		it = e.getChildren().iterator();
		while(it.hasNext()) {
			mapAttributes((Element)it.next());
		}
	}
	
	public void constructLine()
	{
		line = new Line2D.Double(startx, starty, endx, endy);
		// Update JDOM Graphics element
		updateJdomGraphics();
	}
	
	public void updateJdomGraphics() {
		if(jdomElement != null) {
			Element jdomGraphics = jdomElement.getChild("Graphics");
			if(jdomGraphics !=null) {
				jdomGraphics.setAttribute("StartX", Integer.toString((int)startx * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("StartY", Integer.toString((int)starty * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("EndX", Integer.toString((int)endx * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("EndY", Integer.toString((int)endy * GmmlData.GMMLZOOM));
			}
		}
	}
	
	protected void draw(Graphics g)
	{
		if(line!=null)
		{	
			Graphics2D g2D = (Graphics2D)g;
			g2D.setColor(color);
			float[] dash = {3.0f};	
			if (style == 0)
			{
				g2D.setStroke(new BasicStroke(1.0f));
			}
			else if (style == 1)
			{ 
				g2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			}			
	 		g2D.draw(this.line);
			
			if (type == 1)
			{
				drawArrowhead(g2D);
			}
			setHandleLocation();
		}
	}
	
	private void drawArrowhead(Graphics2D g2D) //2Do! clean up this mess.....
	{
		g2D.setColor(color);
		double angle = 25.0;
		double theta = Math.toRadians(180 - angle);
		double[] rot = new double[2];
		double[] p = new double[2];
		double[] q = new double[2];
		double a, b, norm;
		
		rot[0] = Math.cos(theta);
		rot[1] = Math.sin(theta);
		
		g2D.setStroke(new BasicStroke(1.0f));

		a = endx-startx;
		b = endy-starty;
		norm = 8/(Math.sqrt((a*a)+(b*b)));				
		p[0] = ( a*rot[0] + b*rot[1] ) * norm + endx;
		p[1] = (-a*rot[1] + b*rot[0] ) * norm + endy;
		q[0] = ( a*rot[0] - b*rot[1] ) * norm + endx;
		q[1] = ( a*rot[1] + b*rot[0] ) * norm + endy;
		int[] x = {(int) (endx),(int) (p[0]),(int) (q[0])};
		int[] y = {(int) (endy),(int) (p[1]),(int) (q[1])};
		Polygon arrowhead = new Polygon(x,y,3);
		g2D.draw(arrowhead);
		g2D.fill(arrowhead);
	}
  
	/* Checks whether a Point cuts the Line */
	protected boolean isContain(Point2D point)
	{
		BasicStroke stroke = new BasicStroke(10);
		Shape outline = stroke.createStrokedShape(line);
		isSelected = outline.contains(point);
		return isSelected;
	}
	
	/* Methods for resizing Lines */
	public void setLine(double x1, double y1, double x2, double y2)
 	{
 		startx = x1;
		starty = y1;
		endx   = x2;
		endy   = y2;
		
		constructLine();
	}
 	
 	/* Methods for resizing Lines */
 	public void setLine(Point2D start, Point2D end)
 	{
 		startx = start.getX();
		starty = start.getY();
		endx   = end.getX();
		endy   = end.getY();
		
		constructLine();		
 	}
	
	protected void moveBy(double dx, double dy)
	{
		setLine(startx + dx, starty + dy, endx + dx, endy + dy);
	}
	
	protected void resizeX(double dx){}
	protected void resizeY(double dy){}

	private void setHandleLocation()
	{
		handlecenter.setLocation((startx + endx)/2, (starty + endy)/2);
		handleStart.setLocation(startx, starty);
		handleEnd.setLocation(endx, endy);
	}
	
	protected void moveLineStart(double dx, double dy)
	{
		startx += dx;
		starty += dy;
		constructLine();
	}
	
	protected void moveLineEnd(double dx, double dy)
	{
		endx += dx;
		endy += dy;
		constructLine();
	}
	
	protected boolean intersects(Rectangle2D.Double r)
	{
		BasicStroke stroke = new BasicStroke(10);
		Shape outline = stroke.createStrokedShape(line);
		
		isSelected = outline.intersects(r.x, r.y, r.width, r.height);
		return isSelected;
	}

} // end of classdsw
