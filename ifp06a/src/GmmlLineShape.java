import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;

import org.jdom.Attribute;
import org.jdom.Element;

/**
 * This class implements a Gmml lineshape and provides 
 * methods to resize and draw it.
 */
public class GmmlLineShape extends GmmlGraphics
{

	private static final long serialVersionUID = 1L;

	public final List attributes = Arrays.asList(new String[] {
			"StartX", "StartY", "EndX", "EndY",	"Type", "Color"
	});
	
	double startx;
	double starty;
	double endx;
	double endy;
	
	int type; 

	GmmlDrawing canvas;
	Color color;
	
	private final List typeMappings = Arrays.asList(new String[] {
			"Tbar", "ReceptorRound", "LigandRound", 
			"ReceptorSquare", "LigandSquare"
	});	
	Element jdomElement;

	GmmlHandle handlecenter = new GmmlHandle(0, this);
	GmmlHandle handleStart	= new GmmlHandle(3, this);
	GmmlHandle handleEnd	= new GmmlHandle(4, this);
	
	/**
	 * Constructor for this class
	 * @param canvas - the GmmlDrawing this lineshape will be part of
	 */
	public GmmlLineShape(GmmlDrawing canvas)
	{
		this.canvas = canvas;

		canvas.addElement(handlecenter);
		canvas.addElement(handleStart);
		canvas.addElement(handleEnd);
	}
	
	/**
	 * Constructor for this class
	 * @param startx - x coordinate of the starting point
	 * @param starty - x coordinate of the starting point
	 * @param end x - x coordinate of the end point 
	 * @param end y - y coordinate of the end point
	 * @param type - this lineshapes type (0 for tbar, 1 for receptor round, 
	 * 2 for ligand round, 3 for receptro square, 4 for ligandsquare)
	 * @param color - the color this lineshape will be painted
	 * @param canvas - the GmmlDrawing this geneproduct will be part of
	 */	
	public GmmlLineShape(double startx, double starty, double endx, double endy, int type, Color color, GmmlDrawing canvas)
	{
		this.startx = startx;
		this.starty = starty;
		this.endx 	= endx;
		this.endy 	= endy;
		this.type 	= type;
		this.color 	= color;
		this.canvas = canvas;
		
		canvas.addElement(handlecenter);
		canvas.addElement(handleStart);
		canvas.addElement(handleEnd);		
	}
	
	/**
	 * Constructor for mapping a JDOM Element.
	 * @param e	- the GMML element which will be loaded as a GmmLineShape
	 * @param canvas - the GmmlDrawing this GmmlLineShape will be part of
	 */
	public GmmlLineShape(Element e, GmmlDrawing canvas) {
		this.jdomElement = e;
		// List the attributes
		mapAttributes(e);
				
		this.canvas = canvas;
		
		canvas.addElement(handlecenter);
		canvas.addElement(handleStart);
		canvas.addElement(handleEnd);
	}

	/**
	 * Sets lineshape at the location specified
	 * <BR>
	 * <DL><B>Parameters</B>
	 * <DD>Double x1	- the new start x position
	 * <DD>Double y1	- the new start y position
	 * <DD>Double x2	- the new end x position
	 * <DD>Double y2	- the new end y position
	 * <DL>
	 */	
	public void setLocation(double x1, double y1, double x2, double y2)
	{
		startx = x1;
		starty = y1;
		endx	 = x2;
		endy	 = y2;
		
		// Update JDOM Graphics element
		updateJdomGraphics();
	}
	
	/**
	 * Updates the JDom representation of this lineshape
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
	 *  (non-Javadoc)
	 * @see GmmlGraphics#adjustToZoom()
	 */
	protected void adjustToZoom(double factor)
	{
		startx	*= factor;
		starty	*= factor;
		endx	*= factor;
		endy	*= factor;
	}

	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#draw(java.awt.Graphics)
	 */
	protected void draw(Graphics g)
	{
		//Types:
		// 0 - Tbar
		// 1 - Receptor round
		// 2 - Ligand round
		// 3 - Receptor square
		// 4 - Ligand square

		Graphics2D g2D = (Graphics2D)g;
		g2D.setColor(color);
		g2D.setStroke(new BasicStroke(1.0f));		

		double s = Math.sqrt(((endx-startx)*(endx-startx)) + ((endy - starty)*(endy - starty)));
		
		if (type == 0)
		{
			s /= 8;
			
			double capx1 = ((-endy + starty)/s) + endx;
			double capy1 = (( endx - startx)/s) + endy;
			double capx2 = (( endy - starty)/s) + endx;
			double capy2 = ((-endx + startx)/s) + endy;

			Line2D.Double l1 = new Line2D.Double(startx, starty, endx, endy);
			Line2D.Double l2 = new Line2D.Double(capx1, capy1, capx2, capy2);

			g2D.draw(l1);
			g2D.draw(l2);
		}
		
		else if (type == 1)
		{
			double dx = (endx - startx)/s;
			double dy = (endy - starty)/s;
			
			Line2D.Double l 					= new Line2D.Double(startx, starty, endx - (6*dx), endy - (6*dy));
			Ellipse2D.Double ligandround 		= new Ellipse2D.Double(endx - 5, endy - 5, 10, 10);
			
			g2D.draw(l);
			g2D.draw(ligandround);
			g2D.fill(ligandround);			
		}
		
		else if (type == 2)
		{
			double theta 	= Math.toDegrees(Math.atan((endx - startx)/(endy - starty)));
			double dx 		= (endx - startx)/s;
			double dy 		= (endy - starty)/s;	
			
			Line2D.Double l = new Line2D.Double(startx, starty, endx - (8*dx), endy - (8*dy));
			Arc2D.Double  a = new Arc2D.Double(startx - 8, endy - 8, 16, 16, theta + 180, -180, Arc2D.OPEN);
			
			g2D.draw(l);
			g2D.draw(a);
		}
		
		else if (type == 3)
		{
			s /= 8;
			
			double x3 		= endx - ((endx - startx)/s);
			double y3 		= endy - ((endy - starty)/s);
			double capx1 	= ((-endy + starty)/s) + x3;
			double capy1 	= (( endx - startx)/s) + y3;
			double capx2 	= (( endy - starty)/s) + x3;
			double capy2 	= ((-endx + startx)/s) + y3;			
			double rx1		= capx1 + 1.5*(endx - startx)/s;
			double ry1 		= capy1 + 1.5*(endy - starty)/s;
			double rx2 		= capx2 + 1.5*(endx - startx)/s;
			double ry2 		= capy2 + 1.5*(endy - starty)/s;
			
			
			Line2D.Double l 	= new Line2D.Double(startx, starty, x3, y3);		
			Line2D.Double cap = new Line2D.Double(capx1, capy1, capx2, capy2);
			Line2D.Double r1	= new Line2D.Double(capx1, capy1, rx1, ry1);
			Line2D.Double r2	= new Line2D.Double(capx2, capy2, rx2, ry2);

			g2D.draw(l);
			g2D.draw(cap);
			g2D.draw(r1);
			g2D.draw(r2);
		}
		else if (type == 4)
		{
			s /= 6;
			double x3 		= endx - ((endx - startx)/s);
			double y3 		= endy - ((endy - starty)/s);

			int[] polyx = new int[4];
			int[] polyy = new int[4];
			
			polyx[0] = (int) (((-endy + starty)/s) + x3);
			polyy[0] = (int) ((( endx - startx)/s) + y3);
			polyx[1] = (int) ((( endy - starty)/s) + x3);
			polyy[1] = (int) (((-endx + startx)/s) + y3);

			polyx[2] = (int) (polyx[1] + 1.5*(endx - startx)/s);
			polyy[2] = (int) (polyy[1] + 1.5*(endy - starty)/s);
			polyx[3] = (int) (polyx[0] + 1.5*(endx - startx)/s);
			polyy[3] = (int) (polyy[0] + 1.5*(endy - starty)/s);

			Line2D.Double l 	= new Line2D.Double(startx, starty, x3, y3);
			Polygon p 			= new Polygon(polyx, polyy, 4);
			
			g2D.draw(l);
			g2D.draw(p);
			g2D.fill(p);
		}
		setHandleLocation();
	}
	
	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#isContain(java.awt.geom.Point2D)
	 */
	protected boolean isContain(Point2D point)
	{
		double s  = Math.sqrt(((endx-startx)*(endx-startx)) + ((endy-starty)*(endy-starty))) / 60;
		
		int[] x = new int[4];
		int[] y = new int[4];
			
		x[0] = (int)(((-endy + starty)/s) + endx);
		y[0] = (int)((( endx - startx)/s) + endy);
		x[1] = (int)((( endy - starty)/s) + endx);
		y[1] = (int)(((-endx + startx)/s) + endy);
		x[2] = (int)((( endy - starty)/s) + startx);
		y[2] = (int)(((-endx + startx)/s) + starty);
		x[3] = (int)(((-endy + starty)/s) + startx);
		y[3] = (int)((( endx - startx)/s) + starty);
			
		Polygon p = new Polygon(x, y, 4);
				
		isSelected = p.contains(point);
		return isSelected;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#intersects(java.awt.geom.Rectangle2D.Double)
	 */
	protected boolean intersects(Rectangle2D.Double r)
	{
		double s  = Math.sqrt(((endx-startx)*(endx-startx)) + ((endy-starty)*(endy-starty))) / 60;
		
		int[] x = new int[4];
		int[] y = new int[4];
			
		x[0] = (int)(((-endy + starty)/s) + endx);
		y[0] = (int)((( endx - startx)/s) + endy);
		x[1] = (int)((( endy - starty)/s) + endx);
		y[1] = (int)(((-endx + startx)/s) + endy);
		x[2] = (int)((( endy - starty)/s) + startx);
		y[2] = (int)(((-endx + startx)/s) + starty);
		x[3] = (int)(((-endy + starty)/s) + startx);
		y[3] = (int)((( endx - startx)/s) + starty);
			
		Polygon p = new Polygon(x, y, 4);
				
		isSelected = p.intersects(r.x, r.y, r.width, r.height);
		return isSelected;
	}

	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#getPropertyTable()
	 */
	protected JTable getPropertyTable()
	{
		Object[][] data = new Object[][] {{new Double(startx), new Double(starty), 
			 new Double(endx), new Double(endy), new Integer(type), color}};
		
		Object[] cols = new Object[] {"Start X", "Start Y",
				"EndX", "EndY",	"Type", "Color"};
		
		return new JTable(data, cols);
	}
	
	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#moveBy(double, double)
	 */
	protected void moveBy(double dx, double dy)
	{
		setLocation(startx + dx, starty + dy, endx + dx, endy + dy);
	}
	
	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#moveLineStart(double, double)
	 */
	protected void moveLineStart(double dx, double dy)
	{
		startx += dx;
		starty += dy;
		
		// Update JDOM Graphics element
		updateJdomGraphics();
		
//		constructLine();
	}
	
	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#moveLineEnd(double, double)
	 */
	protected void moveLineEnd(double dx, double dy)
	{
		endx += dx;
		endy += dy;
		
		// Update JDOM Graphics element
		updateJdomGraphics();
		
//		constructLine();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#updateFromPropertyTable(javax.swing.JTable)
	 */
	protected void updateFromPropertyTable(JTable t)
	{
		startx		= Double.parseDouble(t.getValueAt(0, 0).toString());
		starty		= Double.parseDouble(t.getValueAt(0, 1).toString());
		endx		= Double.parseDouble(t.getValueAt(0, 2).toString());
		endy		= Double.parseDouble(t.getValueAt(0, 3).toString());
		type		= (int)Double.parseDouble(t.getValueAt(0, 4).toString());
		color 		= GmmlColorConvertor.string2Color(t.getValueAt(0, 5).toString());
		
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
					case 4: // Type
						if(typeMappings.indexOf(value) > -1)
							this.type = typeMappings.indexOf(value);
						break;
					case 5: // Color
						this.color = GmmlColorConvertor.string2Color(value); break;
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
	 * Sets this class handles at the correct position 
	 */
	private void setHandleLocation()
	{
		handlecenter.setLocation((startx + endx)/2, (starty + endy)/2);
		handleStart.setLocation(startx, starty);
		handleEnd.setLocation(endx, endy);
	}
}