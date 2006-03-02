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

import java.awt.geom.Arc2D;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jdom.Attribute;
import org.jdom.Element;

import java.awt.geom.Point2D;
import java.awt.BasicStroke;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * This class implements an arc and provides 
 * methods to resize and draw it
 */
public class GmmlArc extends GmmlGraphics
{
	private List attributes;
	double x;
	double y;
	double width;
	double height;
	double rotation;
	
	Color color;
	Arc2D arc;
	GmmlDrawing canvas;
	
	Element jdomElement;
	
	GmmlHandle handlecenter	= new GmmlHandle(0, this);
	GmmlHandle handlex		= new GmmlHandle(1, this);
	GmmlHandle handley		= new GmmlHandle(2, this);
	
	/**
	 * Constructor for this class
	 * <BR>
	 * <DL><B>Parameters<B>
	 * <DD>GmmlDrawing canvas	- this GmmlDrawing this arc will be part of
	 * <DL>
	 */
	public GmmlArc(GmmlDrawing canvas)
	{
		this.canvas = canvas;
		
		canvas.addElement(handlecenter);
		canvas.addElement(handlex);
		canvas.addElement(handley);
	}

	/**
	 * Constructor for this class
	 * <BR>
	 * <DL><B>Parameters<B>
	 * <DD>	Double x			- the arcs upper left x coordinate 
	 * <DD>	Double y			- the arcs upper left y coordinate
	 * <DD>	Double width		- the arcs widht
	 * <DD>	Double height		- the arcs height
	 * <DD>	Color color			- the color the arc will be painted
	 * <DD> Double rotation		- the angle at which the arc has to be rotated when drawing it 
	 * <DD> GmmlDrawig canvas	- the GmmlDrawing this arc will be part of
	 * <DL> 
	 */
	public GmmlArc(double x, double y, double width, double height, Color color, double rotation, GmmlDrawing canvas)
	{
		this.x 			= x;
		this.y 			= y;
		this.width 		= width;
		this.height		= height;
		this.color 		= color;
		this.rotation 	= Math.toDegrees(rotation);
		this.canvas 	= canvas;
		
		arc = new Arc2D.Double(x-width, y-height, 2*width, 2*height, 180-rotation, 180, 0);
		
		setHandleLocation();
		
		canvas.addElement(handlecenter);
		canvas.addElement(handlex);
		canvas.addElement(handley);
	}
	
	/**
	 * Constructor for mapping a JDOM Element.
	 * <BR>
	 * <DL><B>Parameters</B>
	 * <DD> Element e			- the GMML element which will be loaded as a GmmlShape
	 * <DD> GmmlDrawing canvas	- the GmmlDrawing this GmmlShape will be part of
	 * <DL>
	 */
	public GmmlArc(Element e, GmmlDrawing canvas) {
		// List the attributes
		attributes = Arrays.asList(new String[] {
				"StartX", "StartY", "Width",
				"Height","Color","Rotation"
		});
		mapAttributes(e);
				
		this.canvas = canvas;
		
		arc = new Arc2D.Double(x-width, y-height, 2*width, 2*height, 180-rotation, 180, 0);
		
		setHandleLocation();
		
		canvas.addElement(handlecenter);
		canvas.addElement(handlex);
		canvas.addElement(handley);
	}

	/**
	 * Maps attributes to internal variables.
	 * <BR>
	 * <DL><B>Parameters</B>
	 * <DD> Element e	- the element that will be loaded as a GmmlShape
	 * <DL>
	 */
	private void mapAttributes (Element e) {
		this.jdomElement = e;
		// Map attributes
		System.out.println("> Mapping element '" + e.getName()+ "'");
		Iterator it = e.getAttributes().iterator();
		while(it.hasNext()) {
			Attribute at = (Attribute)it.next();
			int index = attributes.indexOf(at.getName());
			String value = at.getValue();
			switch(index) {
					case 0: // StartX
						this.x = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 1: // StartY
						this.y = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 2: // Width
						this.width = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 3: // Height
						this.height = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 4: // Color
						this.color = GmmlColor.convertStringToColor(value); break;
					case 5: // Rotation
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
	 * Sets the location of this arc to the coordinate specified
	 * <BR>
	 * <DL><B>Parameters<B>
	 * <DD>Double x		- the new y coordinate
	 * <DD>Double y		- the new x coordinate
	 * <DL>
	 */
	public void setLocation(double x, double y)
	{
		this.x = x;
		this.y = y;
		
		constructArc();
	}

	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#draw(java.awt.Graphics)
	 */
	protected void draw(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
	
		g2D.setColor(color);
		g2D.setStroke(new BasicStroke(2.0f));
		
		g2D.draw(arc);
		
		setHandleLocation();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#moveBy(double, double)
	 */
	protected void moveBy(double dx, double dy)
	{
		setLocation(x + dx, y + dy);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#isContain(java.awt.geom.Point2D)
	 */
	protected boolean isContain(Point2D p)
	{
		isSelected =  arc.contains(p);
		return isSelected;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#resizeX(double)
	 */
	protected void resizeX(double dx)
	{
		width += dx;
		constructArc();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#resizeY(double)
	 */
	protected void resizeY(double dy)
	{
		height += dy;
		constructArc();
	}
	
	/**
	 * Constructs the internal arc of this class 
	 */
	public void constructArc()
	{
		arc = new Arc2D.Double(x-width, y-height, 2*width, 2*height, 180-rotation, 180, 0);
		
		// Update JDOM Graphics element
		updateJdomGraphics();
	}

	/**
	 * Updates the JDom representation of this arc
	 */
	public void updateJdomGraphics() {
		if(jdomElement != null) {
			Element jdomGraphics = jdomElement.getChild("Graphics");
			if(jdomGraphics !=null) {
				jdomGraphics.setAttribute("StartX", Integer.toString((int)x * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("StartY", Integer.toString((int)y * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("Width", Integer.toString((int)width * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("Height", Integer.toString((int)height * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("Rotation", Double.toString(rotation));
			}
		}
	}
	
	/**
	 * Sets this class's handles at the correct location
	 */
	public void setHandleLocation()
	{
		handlecenter.setLocation(x - width, y + height);
		handlex.setLocation(x + width, y - height/2);
		handley.setLocation(x + width/2, y + height);
	}
	
	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#intersects(java.awt.geom.Rectangle2D.Double)
	 */
	protected boolean intersects(Rectangle2D.Double r)
	{
		isSelected = arc.intersects(r.x, r.y, r.width, r.height);
		return isSelected;

	}


} //end of GmmlArc
