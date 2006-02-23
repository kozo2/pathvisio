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
import javax.swing.JPanel;

/**
  *This class contains the labels. It contains a constructor, and the methods contains, setLocation and getHelpers
  */

public class GmmlLabel extends GmmlGraphics
{
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
