import java.awt.Color;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

/**
 * This class implements a brace and provides 
 * methods to resize and draw it
 */
public class GmmlBrace extends GmmlGraphics
{
	private static final long serialVersionUID = 1L;
	
	private List attributes;

	double centerx;
	double centery;
	double width;
	double ppo;
	
	int orientation; //orientation: 0=top, 1=right, 2=bottom, 3=left
	Color color;
	
	GmmlDrawing canvas;
	Element jdomElement;
	
	// Some mappings to Gmml
	private final List orientationMapping = Arrays.asList(new String[] {
			"top", "right", "bottom", "left"
	});
	
	/**
	 * Constructor for this class
	 * <BR>
	 * <DL><B>Parameters<B>
	 * <DD>GmmlDrawing canvas	- this GmmlDrawing this brace will be part of
	 * <DL>
	 */
	public GmmlBrace(GmmlDrawing canvas)
	{
		this.canvas = canvas;
	}
	
	/**
	 * Constructor for this class
	 * <BR>
	 * <DL><B>Parameters</B>
	 * <DD>	Double centerX		- the braces center x coordinate 
	 * <DD>	Double centerY		- the braces center ys coordinate 
	 * <DD>	Double width		- the braces widht
	 * <DD>	Double height		- the braces height
	 * <DD> Double ppo			-
	 * <DD> Int orientation		- the braces orientation; 0 for top, 1 for right, 2 for bottom, 3 for left
	 * <DD>	Color color			- the color the brace will be painted
	 * <DD> GmmlDrawig canvas	- the GmmlDrawing this arc will be part of
	 * </DL> 
	 */
	public GmmlBrace(double centerX, double centerY, double width, double ppo, int orientation, Color color, GmmlDrawing canvas)
	{
		this.centerx = centerX;
		this.centerx = centerY;
		this.width = width;
		this.ppo = ppo;
		this.orientation = orientation;
		this.color = color;
		this.canvas = canvas;
		
	} //end constructor GmmlBrace

	/**
	 * Constructor for mapping a JDOM Element.
	 * @param e	- the GMML element which will be loaded as a GmmlBrace
	 * @param canvas - the GmmlDrawing this GmmlBrace will be part of
	 */
	public GmmlBrace(Element e, GmmlDrawing canvas) {
		this.jdomElement = e;
		// List the attributes
		attributes = Arrays.asList(new String[] {
				"CenterX", "CenterY", "Width",
				"PicPointOffset","Orientation","Color"
		});
		mapAttributes(e);
		
		this.canvas = canvas;
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
		
		updateJdomGraphics(); // TODO: implement visualization of brace (constructBrace()) and update the element from there
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
				jdomGraphics.setAttribute("Orientation", (String)orientationMapping.get(orientation));
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#draw(java.awt.Graphics)
	 */
	protected void draw(Graphics g)
	{
		//Graphics2D g2D = (Graphics2D)g;

	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#isContain(java.awt.geom.Point2D)
	 */
	protected boolean isContain(Point2D p)
	{
		return false;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#intersects(java.awt.geom.Rectangle2D.Double)
	 */
	protected boolean intersects(Rectangle2D.Double r)
	{
		return false;
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
	protected void resizeX(double dx){}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#resizeY(double)
	 */
	protected void resizeY(double dy){}

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
						if(orientationMapping.indexOf(value) > -1)
							this.orientation = orientationMapping.indexOf(value);
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
	
	
} //end of GmmlBrace