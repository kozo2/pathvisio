import java.awt.Shape;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Polygon;

import org.jdom.Attribute;
import org.jdom.Element;

import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
 
/**
 * This class implements and handles a line
 */
public class GmmlLine extends GmmlGraphics
{
	private static final long serialVersionUID = 1L;
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
	
	/**
	 * Constructor for this class
	 * @param canvas - the GmmlDrawing this line will be part of
	 */
	public GmmlLine(GmmlDrawing canvas)
	{
		this.canvas = canvas;
		
		canvas.addElement(handlecenter);
		canvas.addElement(handleStart);
		canvas.addElement(handleEnd);
	}
	
	/**
	 * Constructor for this class
	 * @param startx - start x coordinate
	 * @param starty - start y coordinate
	 * @param endx - end x coordinate
	 * @param endy - end y coordinate
	 * @param color - color this line will be painted
	 * @param canvas - the GmmlDrawing this line will be part of
	 */
	public GmmlLine(double startx, double starty, double endx, double endy, Color color, GmmlDrawing canvas)
	{
		this.startx = startx;
		this.starty = starty;
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
	 * @param e	- the GMML element which will be loaded as a GmmlLine
	 * @param canvas - the GmmlDrawing this GmmlLine will be part of
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
	 * Constructs the internal line in this class
	 */
	public void constructLine()
	{
		line = new Line2D.Double(startx, starty, endx, endy);
		// Update JDOM Graphics element
		updateJdomGraphics();
	}
	
	/**
	 * Sets the line start and end to the coordinates specified
	 * <DL><B>Parameters</B>
	 * <DD>Double x1	- new startx 
	 * <DD>Double y1	- new starty
	 * <DD>Double x2	- new endx
	 * <DD>Double y2	- new endy
	 */
	public void setLine(double x1, double y1, double x2, double y2)
	{
		startx = x1;
		starty = y1;
		endx   = x2;
		endy   = y2;
		
		constructLine();
	}

	/**
	 * Sets the line start and en to the points specified
	 * <DL><B>Parameters</B>
	 * <DD>Point2D start	- new start point 
	 * <DD>Point2D end		- new end point
	 * <DL>
	 */
	public void setLine(Point2D start, Point2D end)
	{
		startx = start.getX();
		starty = start.getY();
		endx   = end.getX();
		endy   = end.getY();
		
		constructLine();		
	}

	/**
	 * Updates the JDom representation of this label
	 */	
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

	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#draw(java.awt.Graphics)
	 */
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
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#isContain(java.awt.geom.Point2D)
	 */
	protected boolean isContain(Point2D point)
	{
		BasicStroke stroke = new BasicStroke(10);
		Shape outline = stroke.createStrokedShape(line);
		isSelected = outline.contains(point);
		return isSelected;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#intersects(java.awt.geom.Rectangle2D.Double)
	 */
	protected boolean intersects(Rectangle2D.Double r)
	{
		BasicStroke stroke = new BasicStroke(10);
		Shape outline = stroke.createStrokedShape(line);
		
		isSelected = outline.intersects(r.x, r.y, r.width, r.height);
		return isSelected;
	}

	/*
 	 *  (non-Javadoc)
 	 * @see GmmlGraphics#moveBy(double, double)
 	 */
	protected void moveBy(double dx, double dy)
	{
		setLine(startx + dx, starty + dy, endx + dx, endy + dy);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#moveLineStart(double, double)
	 */
	protected void moveLineStart(double dx, double dy)
	{
		startx += dx;
		starty += dy;
		constructLine();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#moveLineEnd(double, double)
	 */
	protected void moveLineEnd(double dx, double dy)
	{
		endx += dx;
		endy += dy;
		constructLine();
	}
	
	/**
	 * If the line type is arrow, this method draws the arrowhead
	 */
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

	/**
	 * Maps attributes to internal variables.
	 * @param e - the element to map to a GmmlArc
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
						this.color = GmmlColorConvertor.string2Color(value); break;
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

	/**
	 * Sets the handles in this class at the correct location
	 */
	private void setHandleLocation()
	{
		handlecenter.setLocation((startx + endx)/2, (starty + endy)/2);
		handleStart.setLocation(startx, starty);
		handleEnd.setLocation(endx, endy);
	}

} // end of classdsw
