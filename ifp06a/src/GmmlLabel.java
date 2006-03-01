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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import org.jdom.Attribute;
import org.jdom.Element;

/**
  *This class contains the labels. It contains a constructor, and the methods contains, setLocation and getHelpers
  */

public class GmmlLabel extends GmmlGraphics
{
	private List attributes;
	
	String text				= "";
	String font				= "";
	String fontWeight		= "";
	String fontStyle		= "";
	
	double centerx;
	double centery;
	double width;
	double height;
	int fontSize;
	
	Color color;
	
	GmmlDrawing canvas;
	
	Element jdomElement;
	
	GmmlHandle handlecenter = new GmmlHandle(0, this);

	/**
	*Constructor
	*/
	public GmmlLabel(GmmlDrawing canvas)
	{
		this.canvas = canvas;
		canvas.addElement(handlecenter);
	}
	
	/**
	  *Constructor GmmlLabel has 4 doubles for the coordinates, 4 Strings for the text, 
	  *the font, the font weight and the font style, an int for the font 
	  *size and a color object for the color as input.
	  */
	public GmmlLabel (int x, int y, int width, int height, String text, String font, String fontWeight, 
		String fontStyle, int fontSize, Color color, GmmlDrawing canvas)
	{
		this.centerx  = x;
		this.centery = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.font = font;
		this.fontWeight = fontWeight;
		this.fontStyle = fontStyle;
		this.fontSize = fontSize;
		this.color = color;
		this.canvas = canvas;
		
		setHandleLocation();
		canvas.addElement(handlecenter);
	}
	
	/**
	 * Constructor for mapping a JDOM Element
	 */
	public GmmlLabel (Element e, GmmlDrawing canvas) {
		this.jdomElement = e;
		// List the attributes
		attributes = Arrays.asList(new String[] {
				"TextLabel", "CenterX", "CenterY", "Width","Height",
				"FontName","FontWeight","FontStyle","FontSize","Color" 
		});
		mapAttributes(e);
		
		this.canvas = canvas;
		
		setHandleLocation();
		canvas.addElement(handlecenter);
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
					case 0: // TextLabel
						this.text = value; break;
					case 1: // CenterX
						this.centerx = Double.parseDouble(value) / GmmlData.GMMLZOOM ; break;
					case 2: // CenterY
						this.centery = Double.parseDouble(value) / GmmlData.GMMLZOOM; break;
					case 3: // Width
						this.width = Double.parseDouble(value) / GmmlData.GMMLZOOM; break;
					case 4:	// Height
						this.height = Double.parseDouble(value) / GmmlData.GMMLZOOM; break;
					case 5: // FontName
						this.font = value; break;
					case 6: // FontWeight
						this.fontWeight = value; break;
					case 7: // FontStyle
						this.fontStyle = value; break;
					case 8: // FontSize
						this.fontSize = Integer.parseInt(value); break;
					case 9: // Color
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
	
	/**
	  *Method isContain uses the coordinates of a specific point (pointx, pointy) 
	  *to determine whether a label contains this point. 
	  *To do this, a 'real' rectangle object is formed, on which the normal contains method is used.
	  */	
	protected boolean isContain(Point2D p)
	{
		Rectangle2D rect = new Rectangle2D.Double(centerx - (width/2), centery - (height/2), width, height);
		isSelected = rect.contains(p);
		return isSelected;
	}
	
	/**
	  *Method setLocation changes the int x and y coordinate to the x and y that are arguments for this method
	  */	
	public void setLocation(double x, double y)
	{
		this.centerx = x;
		this.centery = y;
	}
	
	protected void moveBy(double dx, double dy)
	{
		setLocation(centerx  + dx, centery + dy);
	}
	
	protected void draw(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
		
		Font f = new Font(font, Font.PLAIN, fontSize);
		
		if (fontWeight.equalsIgnoreCase("bold"))
		{
			if (this.fontStyle.equalsIgnoreCase("italic"))
			{
				f = f.deriveFont(f.BOLD + f.ITALIC);
			}
			else
			{
				f = f.deriveFont(f.BOLD);
			}
		}
		else if (fontStyle.equalsIgnoreCase("italic"))
		{
			f = f.deriveFont(Font.ITALIC);
		}
		
		g2D.setFont(f);
		
		FontMetrics fm = g2D.getFontMetrics();
		int textWidth  = fm.stringWidth(text);
		int textHeight = fm.getHeight();
		
		g2D.setColor(color);
		g2D.drawString(text, (int) centerx - (textWidth/2) , (int)centery + (textHeight/2));
		
		setHandleLocation();
	}
	
	protected void resizeX(double dx){}
	protected void resizeY(double dy){}
	
	private void setHandleLocation()
	{
		handlecenter.setLocation(centerx, centery - height/2 - handlecenter.height/2);
	}
	
	protected boolean intersects(Rectangle2D.Double r)
	{
		isSelected = r.intersects(centerx - width/2, centery - height/2, width, height);
		return isSelected;
	}

} // end of class
