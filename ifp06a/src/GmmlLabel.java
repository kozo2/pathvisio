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
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
/**
  *This class contains the labels. It contains a constructor, and the methods contains, setLocation and getHelpers
  */

public class GmmlLabel extends GmmlGraphics
{

	String text;
	String font;
	String fontWeight;
	String fontStyle;
	
	int x;
	int y;
	int width;
	int height;
	int fontSize;
	
	Color color;
	
	JPanel canvas;

	/**
	*Constructor
	*/
	public GmmlLabel()
	{
	}
	
	/**
	  *Constructor GmmlLabel has 4 doubles for the coordinates, 4 Strings for the text, 
	  *the font, the font weight and the font style, an int for the font 
	  *size and a color object for the color as input.
	  */
	public GmmlLabel (int x, int y, int width, int height, String text, String font, String fontWeight, 
		String fontStyle, int fontSize, Color color, JPanel canvas)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.font = font;
		this.fontWeight = fontWeight;
		this.fontStyle = fontStyle;
		this.fontSize = fontSize;
		this.color = color;
		this.canvas = canvas;
	}
	
	/**
	  *Method isContain uses the coordinates of a specific point (pointx, pointy) 
	  *to determine whether a label contains this point. 
	  *To do this, a 'real' rectangle object is formed, on which the normal contains method is used.
	  */	
	protected boolean isContain(Point p)
	{
		Rectangle rect = new Rectangle(x, y, width, height);
				return rect.contains(p);
	}
	
	/**
	  *Method setLocation changes the int x and y coordinate to the x and y that are arguments for this method
	  */	
	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	protected void moveBy(int dx, int dy)
	{
		setLocation(x + dx, y + dy);
	}
	
	protected void draw(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
		
		Font f = new Font(font, Font.PLAIN, fontSize);
		
		if (fontWeight.equalsIgnoreCase("bold"))
		{
			if (fontStyle.equalsIgnoreCase("italic"))
			{
				f = f.deriveFont(Font.BOLD+Font.ITALIC);
			}
			else
			{
				f = f.deriveFont(Font.BOLD);
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
		
		Rectangle label = new Rectangle(x - 2, y - textHeight, textWidth + 4, textHeight);
		g2D.setColor(Color.red);
		g2D.fill(label);
		
		g2D.setColor(color);
		g2D.drawString(text, x, y);
	
	}

}
