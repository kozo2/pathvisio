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

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

import org.jdom.Attribute;
import org.jdom.Element;
/**
 * This class implements a geneproduct and 
 * provides methods to resize and draw it.
 */
public class GmmlGeneProduct extends GmmlGraphics
{
	private List attributes;
	
	double centerx;
	double centery;
	double width;
	double height;

	Color color;
	GmmlDrawing canvas;
	Rectangle2D rect;
	
	Element jdomElement;
	
	String geneID;
	String xref;

	GmmlHandle handlecenter = new GmmlHandle(0, this);
	GmmlHandle handlex 	= new GmmlHandle(1, this);
	GmmlHandle handley 	= new GmmlHandle(2, this);
	
	/**
	 * Constructor for this class
	 * <BR>
	 * <DL><B>Parameters<B>
	 * <DD> GmmlDrawing canvas	- this GmmlDrawing this geneproduct will be part of
	 * <DL>
	 */
	public GmmlGeneProduct(GmmlDrawing canvas)
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
	 * <DD>	Double x			- the geneproducts upper left x coordinate 
	 * <DD>	Double y			- the geneproducts upper left y coordinate
	 * <DD>	Double width		- the geneproducts widht
	 * <DD>	Double height		- the geneproducts height
	 * <DD> String geneID		- the geneID as it will be printed on screen
	 * <DD> String xref			- 
	 * <DD>	Color color			- the color the geneproduct will be painted
	 * <DD> GmmlDrawig canvas	- the GmmlDrawing this arc will be part of
	 * <DL> 
	 */
	public GmmlGeneProduct(double x, double y, double width, double height, String geneID, String xref, Color color, GmmlDrawing canvas){
		this.centerx = x;
		this.centery = y;
		this.width = width;
		this.height = height;
		this.geneID = geneID;
		this.xref = xref;
		this.color = color;
		this.canvas = canvas;
		
		constructRectangle();

		setHandleLocation();
	}
	
	/**
	 * Constructor for mapping a JDOM Element.
	 * <BR>
	 * <DL><B>Parameters</B>
	 * <DD> Element e			- the GMML element which will be loaded as a GmmlShape
	 * <DD> GmmlDrawing canvas	- the GmmlDrawing this GmmlShape will be part of
	 * <DL>
	 */
	public GmmlGeneProduct(Element e, GmmlDrawing canvas) {
		this.jdomElement = e;
		// List the attributes
		attributes = Arrays.asList(new String[] {
				"CenterX", "CenterY", "Width","Height",
				"GeneID","Xref","Color"
		});
		mapAttributes(e);
		
		this.canvas = canvas;
		
		constructRectangle();

		setHandleLocation();
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
					case 0: // CenterX
						this.centerx = Integer.parseInt(value) / GmmlData.GMMLZOOM ; break;
					case 1: // CenterY
						this.centery = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 2: // Width
						this.width = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 3:	// Height
						this.height = Integer.parseInt(value) / GmmlData.GMMLZOOM; break;
					case 4: // GeneID
						this.geneID = value; break;
					case 5: // Xref
						this.xref = value; break;
					case 6: // Color
						this.color = GmmlColor.convertStringToColor(value); break;
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
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#draw(java.awt.Graphics)
	 */
	protected void draw(Graphics g)
	{
		if (rect != null)
		{
			Graphics2D g2D = (Graphics2D)g;
			g2D.setColor(color);
			g2D.setStroke(new BasicStroke(2.0f));
			
			g2D.draw(rect);
			
			Font f = new Font("Arial", Font.PLAIN, 10);
			g2D.setFont(f);
			g2D.setStroke(new BasicStroke(1.0f));
			
			FontMetrics fm = g2D.getFontMetrics();
			int textwidth 	= fm.stringWidth(geneID);
						
			int strx = (int) (centerx - textwidth/2);
			int stry = (int) (centery);

			g2D.drawString(geneID, strx, stry);
			
			setHandleLocation();
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#isContain(java.awt.geom.Point2D)
	 */
	protected boolean isContain(Point2D point)
	{
		isSelected = rect.contains(point);
		return isSelected;
	}	

	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#moveBy(double, double)
	 */
	protected void moveBy(double dx, double dy)
	{
		setLocation(centerx + dx, centery + dy);

		BasicStroke stroke = new BasicStroke(20);
		Shape s = stroke.createStrokedShape(rect);

		Iterator it = canvas.lineHandles.iterator();

		while (it.hasNext())
		{
			GmmlHandle h = (GmmlHandle) it.next();
			Point2D p = h.getCenterPoint();
			if (s.contains(p))
			{
				h.moveBy(dx, dy);
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#resizeX(double)
	 */
	protected void resizeX(double dx)
	{
		width += dx;
		constructRectangle();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see GmmlGraphics#resizeY(double)
	 */
	protected void resizeY(double dy)
	{
		height 	-= dy;
		constructRectangle();
	}
	
	/**
	 * Constructs the internal rectangle of this class
	 */
	public void constructRectangle()
	{
		rect = new Rectangle2D.Double(centerx - width/2, centery - height/2, width, height);
		
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
				jdomGraphics.setAttribute("CenterX", Integer.toString((int)centerx * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("CenterY", Integer.toString((int)centery * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("Width", Integer.toString((int)width * GmmlData.GMMLZOOM));
				jdomGraphics.setAttribute("Height", Integer.toString((int)height * GmmlData.GMMLZOOM));
			}
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
		centerx = x;
		centery = y;
		
		constructRectangle();
	}
	
	/**
	 * Sets this class's handles at the correct location
	 */
	private void setHandleLocation()
	{
		handlecenter.setLocation(centerx, centery);
		handlex.setLocation(centerx + width/2, centery);
		handley.setLocation(centerx, centery - height/2);
	}	
	
	/*
	 * (non-Javadoc)
	 * @see GmmlGraphics#intersects(java.awt.geom.Rectangle2D.Double)
	 */
	protected boolean intersects(Rectangle2D.Double r)
	{
		isSelected = r.intersects(centerx - width/2, centery - height/2, width, height);
		return isSelected;
	}
} //end of GmmlGeneProduct