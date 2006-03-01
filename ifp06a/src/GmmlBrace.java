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
  *This class contains the braces. It contains a constructor, and the methods contains, setLocation and getHelpers
  */

public class GmmlBrace extends GmmlGraphics
{
	private List attributes;
	double cX;
	double cY;
	double width;
	double ppo;
	
	int orientation; //orientation: 0=top, 1=right, 2=bottom, 3=left
	Color color;
	
	GmmlDrawing canvas;
	Element jdomElement;
	/**
	*Constructor
	*/
	public GmmlBrace()
	{
	}
	
	/**
	  *Constructor GmmlBrace has 4 doubles for the coordinates, an int for the orientation and a string for the color as input. Width is the longest side of the brace, ppo the shortest side. This input is assigned to the object brace, but no real brace is constructed. Orientation is 0 for top, 1 for right, 2 for bottom or 3 for left.  
	  */
	public GmmlBrace(double centerX, double centerY, double width, double ppo, int orientation, Color color, GmmlDrawing canvas)
	{
		cX = centerX;
		cY = centerY;
		this.width = width;
		this.ppo = ppo;
		this.orientation = orientation;
		this.color = color;
		this.canvas = canvas;
		
	} //end constructor GmmlBrace

	/**
	 * Constructor for mapping a JDOM Element
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
						this.cX = Double.parseDouble(value) / GmmlData.GMMLZOOM; break;
					case 1: // CenterY
						this.cY = Double.parseDouble(value) / GmmlData.GMMLZOOM; break;
					case 2: // Width
						this.width = Double.parseDouble(value) / GmmlData.GMMLZOOM; break;
					case 3: // PicPointOffset
						this.ppo = Double.parseDouble(value) / GmmlData.GMMLZOOM; break;
					case 4: // Orientation
						List orientationMapping = Arrays.asList(new String[] {
								"top", "right", "bottom", "left"
						});
						if(orientationMapping.indexOf(value) > -1)
							this.orientation = orientationMapping.indexOf(value);
						break;
					case 5: // Color
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
	  *Method setLocation changes the double centerX and centerY coordinate to the centerX and centerY that are arguments for this method
	  */
	public void setLocation(double centerX, double centerY)
	{
		cX = centerX;
		cY = centerY;
	}
	
	
	protected void draw(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
		
	}
	
	protected boolean isContain(Point2D p)
	{
		return false;
	}
	
	protected void moveBy(double dx, double dy)
	{
		setLocation(cX + dx, cY + dy);
	}
	
	protected void resizeX(double dx){}
	protected void resizeY(double dy){}
	
	protected boolean intersects(Rectangle2D.Double r)
	{
		return false;
	}
	
	
} //end of GmmlBrace