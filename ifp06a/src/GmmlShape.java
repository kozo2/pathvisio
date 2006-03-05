/*
Copyright 2005 H.C. Achterberg, R.M.H. Besseling, I.Kaashoek, 
M.M.Palm, E.D Pelgrim, BiGCaT (http://www.BiGCaT.unimaas.nl/)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and 
limitations under the License.
*/

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.BasicStroke;

import org.jdom.Attribute;
import org.jdom.Element;

import java.util.*;

/**
 * This class represents a GMMLShape, which can be a 
 * rectangle or ellips, depending of its type.
 */
public class GmmlShape extends GmmlGraphics
{
	private static final long serialVersionUID = 1L;

	private List attributes;
	double centerx;
	double centery;
	double width;
	double height;
	double rotation;
	
	int type = 0;
	// types:
	// 0 - rectangle
	// 1 - ellipse

	private final List typeMappings = Arrays.asList(new String[] {
			"Rectangle","Oval"
	});
	
	GmmlDrawing canvas;
	Color color;
	
	Element jdomElement;
	
	GmmlHandle handlecenter = new GmmlHandle(0, this);
	GmmlHandle handlex 		= new GmmlHandle(1, this);
	GmmlHandle handley 		= new GmmlHandle(2, this);

	/**
	 * Constructor for this class
	 * @param canvas - the GmmlDrawing this GmmlShape will be part of
	 */
	public GmmlShape(GmmlDrawing canvas)
	{
		this.canvas = canvas;
		
		canvas.addElement(handlecenter);
		canvas.addElement(handlex);
		canvas.addElement(handley);
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
	public GmmlShape(double x, double y, double width, double height, int type, Color color, double rotation, GmmlDrawing canvas)
	{
		this.centerx	= x;
		this.centery	= y;
		this.width 		= width;
		this.height 	= height;
		this.color 		= color;
		this.type 		= type;
		this.rotation 	= rotation;
		this.canvas		= canvas;

		setHandleLocation();
				
		canvas.addElement(handlecenter);
		canvas.addElement(handlex);
		canvas.addElement(handley);
	}
	
	/**
	 * Constructor for mapping a JDOM Element.
	 * @param e	- the GMML element which will be loaded as a GmmlShape
	 * @param canvas - the GmmlDrawing this GmmlShape will be part of
	 */
	public GmmlShape(Element e, GmmlDrawing canvas) {
		this.jdomElement = e;
		// List the attributes
		attributes = Arrays.asList(new String[] {
				"CenterX", "CenterY", "Width", "Height", 
				"Type","Color","Rotation"
		});
		mapAttributes(e);
				
		this.canvas = canvas;
		
		setHandleLocation();
		
		canvas.addElement(handlecenter);
		canvas.addElement(handlex);
		canvas.addElement(handley);
	}

	/**
	 * Set shape at the location specified
	 * @param x - new x coordinate
	 * @param y - new y coordinate
	 */
	public void setLocation(double x, double y)
	{
		centerx = x;
		centery = y;
		
		// Update JDOM Graphics element
		updateJdomGraphics();
	}
	
	/**
	 * Updates the JDom representation of the GMML file. 
	 */
	public void updateJdomGraphics() {
		if(jdomElement != null) {
			Element jdomGraphics = jdomElement.getChild("Graphics");
			if(jdomGraphics !=null) {
				jdomGraphics.setAttribute("CenterX", Integer.toString((int)centerx * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("CenterY", Integer.toString((int)centery * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("Width", Integer.toString((int)width * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("Height", Integer.toString((int)height * GmmlData.GMMLZOOM));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#adjustToZoom(double)
	 */
	protected void adjustToZoom(double factor)
	{
		centerx *= factor;
		centery *= factor;
		width 	*= factor;
		height	*= factor;
	}
	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#draw(java.awt.Graphics)
	 */
	protected void draw(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
	
		g2D.setStroke(new BasicStroke(1.0f));
		g2D.setColor(color);
		g2D.rotate(Math.toRadians(rotation), (centerx), (centery ));
		
		if (type == 0)
		{
			g2D.draw(new Rectangle2D.Double(centerx - width/2, centery - height/2, width, height));
		}
		else if (type == 1)
		{
			g2D.draw(new Ellipse2D.Double(centerx - width, centery - height, 2*width, 2*height));
		}
				
		setHandleLocation();
		// reset rotation
		g2D.rotate(-Math.toRadians(rotation), (centerx), (centery));
	}
	
	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#isContain(java.awt.geom.Point2D)
	 */
	protected boolean isContain(Point2D p)
	{
			Polygon pol = createContainingPolygon();
			isSelected = pol.contains(p);
			return isSelected;			
	}

	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#intersects(java.awt.geom.Rectangle2D.Double)
	 */
	protected boolean intersects(Rectangle2D.Double r)
	{
			Polygon pol = createContainingPolygon();
			isSelected = pol.intersects(r.x, r.y, r.width, r.height);
			return isSelected;
	}
	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#moveBy(double, double)
	 */
	protected void moveBy(double dx, double dy)
	{
		setLocation(centerx + dx, centery + dy);
		
		Polygon pol = createContainingPolygon();
		Iterator it = canvas.lineHandles.iterator();

		while (it.hasNext())
		{
			GmmlHandle h = (GmmlHandle) it.next();
			Point2D p = h.getCenterPoint();
			if (pol.contains(p))
			{
				h.moveBy(dx, dy);
			}
		}		
	}

	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#resizeX(double)
	 */
	protected void resizeX(double dx)
	{
		width += dx;
		
		// Update JDOM Graphics element
		updateJdomGraphics();
	}
	
	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#resizeY(double)
	 */
	protected void resizeY(double dy)
	{
		height -= dy;
		
		// Update JDOM Graphics element
		updateJdomGraphics();
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
					case 3: // Height
						this.height = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 4: // Type
						if(typeMappings.indexOf(value) > -1)
							this.type = typeMappings.indexOf(value);
						break;
					case 5: // Color
						this.color = GmmlColorConvertor.string2Color(value); break;
					case 6: // Rotation
						this.rotation = Double.parseDouble(value); break;
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
	 * Sets the handles at the correct location;
	 * one in the center, the other two on top, respectivily
	 * left border.
	 */
	private void setHandleLocation()
	{
			handlecenter.setLocation(centerx, centery);
			if (type == 0)
			{
				handlex.setLocation(centerx + width/2, centery);
				handley.setLocation(centerx, centery - height/2);
			}
			else if (type == 1)
			{
				handlex.setLocation(centerx + width, centery);
				handley.setLocation(centerx, centery - height);
			}
	}
	
	/**
	 * Creates a polygon containing the GmmlShape
	 */
	private Polygon createContainingPolygon()
	{
		double theta = Math.toRadians(rotation);
		double[] rot = new double[2];

		rot[0] = Math.cos(theta);
		rot[1] = Math.sin(theta);
	
		int[] x = new int[4];
		int[] y = new int[4];
			
		if (type == 0)
		{
			x[0]= (int)(( 0.5*width*rot[0] - 0.5*height*rot[1]) + centerx); //upper right
			x[1]= (int)(( 0.5*width*rot[0] + 0.5*height*rot[1]) + centerx); //lower right
			x[2]= (int)((-0.5*width*rot[0] + 0.5*height*rot[1]) + centerx); //lower left
			x[3]= (int)((-0.5*width*rot[0] - 0.5*height*rot[1]) + centerx); //upper left
			
			y[0]= (int)(( 0.5*width*rot[1] + 0.5*height*rot[0]) + centery); //upper right
			y[1]= (int)(( 0.5*width*rot[1] - 0.5*height*rot[0]) + centery); //lower right
			y[2]= (int)((-0.5*width*rot[1] - 0.5*height*rot[0]) + centery); //lower left
			y[3]= (int)((-0.5*width*rot[1] + 0.5*height*rot[0]) + centery); //upper left
		}
		else
		{
			x[0]= (int)(( width*rot[0] - height*rot[1]) + centerx); //upper right
			x[1]= (int)(( width*rot[0] + height*rot[1]) + centerx); //lower right
			x[2]= (int)((-width*rot[0] + height*rot[1]) + centerx); //lower left
			x[3]= (int)((-width*rot[0] - height*rot[1]) + centerx); //upper left

			y[0]= (int)(( width*rot[1] + height*rot[0]) + centery); //upper right
			y[1]= (int)(( width*rot[1] - height*rot[0]) + centery); //lower right
			y[2]= (int)((-width*rot[1] - height*rot[0]) + centery); //lower left
			y[3]= (int)((-width*rot[1] + height*rot[0]) + centery); //upper left
		}
		
		Polygon pol = new Polygon(x, y, 4);
		return pol;
	}
	
} //end of GmmlShape