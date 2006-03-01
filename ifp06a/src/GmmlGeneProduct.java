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
  *This class contains the gene products. It contains a constructor, and the methods contains, setLocation and getHelpers
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
	*Constructor
	*/
	public GmmlGeneProduct(GmmlDrawing canvas)
	{
		this.canvas = canvas;

		canvas.addElement(handlecenter);
		canvas.addElement(handlex);
		canvas.addElement(handley);
	}
	
	/**
	  *Constructor GmmlGeneProduct has 4 ints for the coordinates, 
	  *a string for the geneID, and a string for the reference as input. 
	  *This input is assigned to the object geneproduct, but no real rectangle object is constructed.
	  */
	public GmmlGeneProduct(int x, int y, int width, int height, String geneID, String xref, Color color, GmmlDrawing canvas){
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
	 * Constructor for mapping a JDOM Element
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
	 * Maps attributes to internal variables
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
			int textheight = fm.getHeight();
			
			int strx = (int) (centerx - textwidth/2);
			int stry = (int) (centery);

			g2D.drawString(geneID, strx, stry);
			
			setHandleLocation();
		}
	}
	
	protected boolean isContain(Point2D point)
	{
		isSelected = rect.contains(point);
		return isSelected;
	}	

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
	
	protected void resizeX(double dx)
	{
		width += dx;
		constructRectangle();
	}
	
	protected void resizeY(double dy)
	{
		height 	-= dy;
		constructRectangle();
	}
	
	public void constructRectangle()
	{
		rect = new Rectangle2D.Double(centerx - width/2, centery - height/2, width, height);
		
		// Update JDOM Graphics element
		updateJdomGraphics();
	}
	
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
	
	public void setLocation(double newx, double newy)
	{
		centerx = newx;
		centery = newy;
		
		constructRectangle();
	}
	
	private void setHandleLocation()
	{
		handlecenter.setLocation(centerx, centery);
		handlex.setLocation(centerx + width/2, centery);
		handley.setLocation(centerx, centery - height/2);
	}	
	
	protected boolean intersects(Rectangle2D.Double r)
	{
		isSelected = r.intersects(centerx - width/2, centery - height/2, width, height);
		return isSelected;
	}
} //end of GmmlGeneProduct