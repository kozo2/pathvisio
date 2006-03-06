import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;

import org.jdom.Attribute;
import org.jdom.Element;

/**
 * This class implements a brace and provides 
 * methods to resize and draw it
 */
public class GmmlBrace extends GmmlGraphics
{
	private static final long serialVersionUID = 1L;
	
	public static final int ORIENTATION_TOP		= 0;
	public static final int ORIENTATION_RIGHT	= 1;
	public static final int ORIENTATION_BOTTOM	= 2;
	public static final int ORIENTATION_LEFT	= 3;
	
	public final List attributes = Arrays.asList(new String[] {
			"CenterX", "CenterY", "Width", "PicPointOffset","Orientation","Color"
	});

	double centerx;
	double centery;
	double width;
	double ppo;
	
	int orientation; //orientation: 0=top, 1=right, 2=bottom, 3=left
	Color color;
	
	GmmlDrawing canvas;
	Element jdomElement;
	
	GmmlHandle handlecenter = new GmmlHandle(GmmlHandle.HANDLETYPE_CENTER, this);
	GmmlHandle handlewidth	= new GmmlHandle(GmmlHandle.HANDLETYPE_WIDTH, this);
	
	// Some mappings to Gmml
	private final List orientationMappings = Arrays.asList(new String[] {
			"top", "right", "bottom", "left"
	});
		
	/**
	 * Constructor for this class
	 * @param canvas - the GmmlDrawing this brace will be part of
	 */
	public GmmlBrace(GmmlDrawing canvas)
	{
		this.canvas = canvas;
		
		canvas.addElement(handlecenter);
		canvas.addElement(handlewidth);
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
	public GmmlBrace(double centerX, double centerY, double width, double ppo, int orientation, Color color, GmmlDrawing canvas)
	{
		this.centerx = centerX;
		this.centery = centerY;
		this.width = width;
		this.ppo = ppo;
		this.orientation = orientation;
		this.color = color;
		this.canvas = canvas;
		
		setHandleLocation();
		
		canvas.addElement(handlecenter);
		canvas.addElement(handlewidth);
	} //end constructor GmmlBrace

	/**
	 * Constructor for mapping a JDOM Element.
	 * @param e	- the GMML element which will be loaded as a GmmlBrace
	 * @param canvas - the GmmlDrawing this GmmlBrace will be part of
	 */
	public GmmlBrace(Element e, GmmlDrawing canvas) {
		this.jdomElement = e;
		// List the attributes
		mapAttributes(e);
		
		this.canvas = canvas;
		
		canvas.addElement(handlecenter);
		canvas.addElement(handlewidth);
	}

	/**
	 * Sets the brace at the location specified
	 * @param centerX - the x coordinate
	 * @param centerY - the y coordinate
	 */
	public void setLocation(double centerX, double centerY)
	{
		this.centerx = centerX;
		this.centery = centerY;
		
		updateJdomGraphics();
	}
	
	/**
	 * Updates the JDom representation of this arc
	 */
	public void updateJdomGraphics() {
		if(jdomElement != null) {
			Element jdomGraphics = jdomElement.getChild("Graphics");
			if(jdomGraphics !=null) {
				jdomGraphics.setAttribute("CenterX", Integer.toString((int)centerx * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("CenterY", Integer.toString((int)centery * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("Width", Integer.toString((int)width * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("PicPointOffset", Double.toString(ppo));
				jdomGraphics.setAttribute("Orientation", (String)orientationMappings.get(orientation));
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#adjustToZoom()
	 */
	protected void adjustToZoom(double factor)
	{
		centerx	*= factor;
		centery	*= factor;
		width	*= factor;
		ppo		*= factor;
	}

	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#draw(java.awt.Graphics)
	 */
	protected void draw(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
		
		Arc2D.Double[]	arcsOfBrace = new Arc2D.Double[4]; //4 Arcs are used to create a brace
		Line2D.Double[] linesOfBrace = new Line2D.Double[2];; //2 Lines are used to creata a brace
				
		linesOfBrace[0] = new Line2D.Double();
		linesOfBrace[1] = new Line2D.Double();
	
		for (int i=0; i<4; i++){
			arcsOfBrace[i] = new Arc2D.Double();
		}
		
		if (orientation == ORIENTATION_TOP)
		{
			linesOfBrace[0].setLine(centerx + ppo/2, centery, centerx + width/2 - ppo/2, centery); //line on the right
			linesOfBrace[1].setLine(centerx - ppo/2, centery, centerx - width/2 + ppo/2, centery); //line on the left
			
			arcsOfBrace[0].setArc(centerx - width/2, centery, ppo, ppo, -180, -90, 0); //arc on the left
			arcsOfBrace[1].setArc(centerx - ppo, centery - ppo,	ppo, ppo, -90, 90, 0); //left arc in the middle
			arcsOfBrace[2].setArc(centerx, centery - ppo, ppo, ppo, -90, -90, 0); //right arc in the middle
			arcsOfBrace[3].setArc(centerx + width/2 - ppo, centery, ppo, ppo, 0, 90, 0); //arc on the right
		}
		
		else if (orientation == ORIENTATION_RIGHT)
		{
			linesOfBrace[0].setLine(centerx, centery + ppo/2, centerx, centery + width/2 - ppo/2); //line on the bottom
			linesOfBrace[1].setLine(centerx, centery - ppo/2, centerx, centery - width/2 + ppo/2); //line on the top
			
			arcsOfBrace[0].setArc(centerx - ppo,centery - width/2, ppo, ppo, 0, 90, 0); //arc on the top
			arcsOfBrace[1].setArc(centerx, centery - ppo, ppo, ppo, -90, -90, 0); //upper arc in the middle
			arcsOfBrace[2].setArc(centerx, centery, ppo, ppo, 90, 90, 0); //lowidther arc in the middle
			arcsOfBrace[3].setArc(centerx - ppo, centery + width/2 - ppo, ppo, ppo, 0, -90, 0); //arc on the bottom

		}
		
		else if (orientation == ORIENTATION_BOTTOM)
		{ 
			linesOfBrace[0].setLine(centerx + ppo/2, centery, centerx + width/2 - ppo/2, centery); //line on the right
			linesOfBrace[1].setLine(centerx - ppo/2, centery, centerx - width/2 + ppo/2, centery); //line on the left
			
			arcsOfBrace[0].setArc(centerx - width/2, centery - ppo, ppo, ppo, -180, 90, 0); //arc on the left
			arcsOfBrace[1].setArc(centerx - ppo, centery, ppo, ppo, 90, -90, 0); //left arc in the middle
			arcsOfBrace[2].setArc(centerx, centery, ppo, ppo, 90, 90, 0); //right arc in the middle
			arcsOfBrace[3].setArc(centerx + width/2 - ppo, centery - ppo, ppo, ppo, 0, -90, 0); //arc on the right

		}
		
		else if (orientation == ORIENTATION_LEFT)
		{
			linesOfBrace[0].setLine(centerx, centery + ppo/2, centerx, centery + width/2 - ppo/2); //line on the bottom
			linesOfBrace[1].setLine(centerx, centery - ppo/2, centerx, centery - width/2 + ppo/2); //line on the top
			
			arcsOfBrace[0].setArc(centerx, centery - width/2, ppo, ppo, -180, -90, 0); //arc on the top
			arcsOfBrace[1].setArc(centerx - ppo, centery - ppo, ppo, ppo, -90, 90, 0); //upper arc in the middle
			arcsOfBrace[2].setArc(centerx - ppo, centery, ppo, ppo, 90, -90, 0); //lowidther arc in the middle
			arcsOfBrace[3].setArc(centerx, centery + width/2 - ppo, ppo, ppo, -90, -90, 0); //arc on the bottom

		} 
		
		g2D.setColor(color);
		g2D.setStroke(new BasicStroke(2.0f));
		
		g2D.draw(linesOfBrace[0]);
		g2D.draw(linesOfBrace[1]);
		g2D.draw(arcsOfBrace[0]);
		g2D.draw(arcsOfBrace[1]);
		g2D.draw(arcsOfBrace[2]);
		g2D.draw(arcsOfBrace[3]);
		
		setHandleLocation();
	}
			
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#isContain(java.awt.geom.Point2D)
	 */
	protected boolean isContain(Point2D p)
	{
		Line2D l = new Line2D.Double();
		if (orientation == ORIENTATION_TOP)
		{
			l = new Line2D.Double(centerx - width/2, centery, centerx + width/2, centery);
			
		}
		else if (orientation == ORIENTATION_RIGHT)
		{
			l = new Line2D.Double(centerx, centery - width/2, centerx, centery + width/2);
		}
		else if (orientation == ORIENTATION_BOTTOM)
		{
			l = new Line2D.Double(centerx - width/2, centery, centerx + width/2, centery);
		}
		else if (orientation == ORIENTATION_LEFT)
		{
			l = new Line2D.Double(centerx, centery - width/2, centerx, centery + width/2);
		}
		
		BasicStroke stroke = new BasicStroke(10);
		Shape outline = stroke.createStrokedShape(l);
		isSelected = outline.contains(p);
		return isSelected;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#intersects(java.awt.geom.Rectangle2D.Double)
	 */
	protected boolean intersects(Rectangle2D.Double r)
	{
		Line2D l = new Line2D.Double();
		if (orientation == ORIENTATION_TOP)
		{
			l = new Line2D.Double(centerx - width/2, centery, centerx + width/2, centery);
			
		}
		else if (orientation == ORIENTATION_RIGHT)
		{
			l = new Line2D.Double(centerx, centery - width/2, centerx, centery + width/2);
		}
		else if (orientation == ORIENTATION_BOTTOM)
		{
			l = new Line2D.Double(centerx - width/2, centery, centerx + width/2, centery);
		}
		else if (orientation == ORIENTATION_LEFT)
		{
			l = new Line2D.Double(centerx, centery - width/2, centerx, centery + width/2);
		}
		BasicStroke stroke = new BasicStroke(10);
		Shape outline = stroke.createStrokedShape(l);
		
		isSelected = outline.intersects(r.x, r.y, r.width, r.height);
		return isSelected;
	}

	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#getPropertyTable()
	 */
	protected JTable getPropertyTable()
	{
		Object[][] data = new Object[][] {{new Double(centerx), new Double(centery),
			new Double(width), new Double(ppo), new Integer(orientation), color}};
		
		Object[] cols = new Object[]{"CenterX", "CenterY", 
				"Width", "PicPoint Offset", "Orientation", "Color"}; 
		
		return new JTable(data, cols);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#moveBy(double, double)
	 */
	protected void moveBy(double dx, double dy)
	{
		setLocation(centerx + dx, centery + dy);
	}

	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#resizeX(double)
	 */
	protected void resizeX(double dx)
	{
		width += dx;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#resizeY(double)
	 */
	protected void resizeY(double dy){}

	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#updateFromPropertyTable(javax.swing.JTable)
	 */
	protected void updateFromPropertyTable(JTable t)
	{
		centerx		= Double.parseDouble(t.getValueAt(0, 0).toString());
		centery		= Double.parseDouble(t.getValueAt(0, 1).toString());
		width		= Double.parseDouble(t.getValueAt(0, 2).toString());
		ppo			= Double.parseDouble(t.getValueAt(0, 3).toString());
		orientation	= (int)Double.parseDouble(t.getValueAt(0, 4).toString());
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
					case 0: // CenterX
						this.centerx = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 1: // CenterY
						this.centery = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 2: // Width
						this.width = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 3: // PicPointOffset
						this.ppo = Double.parseDouble(value) / GmmlData.GMMLZOOM; break;
					case 4: // Orientation
						if(orientationMappings.indexOf(value) > -1)
							this.orientation = orientationMappings.indexOf(value);
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
	
	private void setHandleLocation()
	{
		handlecenter.setLocation(centerx, centery);
		if (orientation == ORIENTATION_TOP)
		{
			handlewidth.setLocation(centerx + width/2, centery);
		}
		else if (orientation == ORIENTATION_RIGHT)
		{
			handlewidth.setLocation(centerx, centery + width/2);
		}
		else if (orientation == ORIENTATION_BOTTOM)
		{
			handlewidth.setLocation(centerx + width/2, centery);	
		}
		else if (orientation == ORIENTATION_LEFT)
		{
			handlewidth.setLocation(centerx, centery + width/2);
		}
	}
	
	
} //end of GmmlBrace