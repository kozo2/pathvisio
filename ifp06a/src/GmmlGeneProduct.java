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

import java.awt.*;
import java.awt.Point;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
/**
  *This class contains the gene products. It contains a constructor, and the methods contains, setLocation and getHelpers
  */

public class GmmlGeneProduct extends GmmlGraphics
{
	
	int x;
	int y;
	int width;
	int height;

	Color color = Color.black;
	JPanel canvas;
	Rectangle2D rect;
	
	String geneID;
	String ref;
	
	boolean isSelected;
	BasicStroke stroke = new BasicStroke(10);

	/**
	*Constructor
	*/
	public GmmlGeneProduct()
	{
	}
	
	/**
	  *Constructor GmmlGeneProduct has 4 ints for the coordinates, 
	  *a string for the geneID, and a string for the reference as input. 
	  *This input is assigned to the object geneproduct, but no real rectangle object is constructed.
	  */
	public GmmlGeneProduct(int x, int y, int width, int height, String geneID, String ref, JPanel canvas){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.geneID = geneID;
		this.ref = ref;
		this.canvas = canvas;
		
		constructRectangle();
	}
	
	protected void draw(Graphics g)
	{
		if (rect != null)
		{
			System.out.println("draw");
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
			
			int strx = (int) (x + ((width - textwidth)/2));
			int stry = (int) (y  + ((height + textheight)/2));

			g2D.drawString(geneID, strx, stry);
		}
		else
		{
			System.out.println("GeneProduct rectangle not initialized");
		}
	}
	
	protected boolean isContain(Point point)
	{
		if (rect.contains(point)) 
		{
			isSelected = true;
	  	}
    	else
    	{
	    	isSelected = false;
		}
		
    	return isSelected;
	}	

	protected void moveBy(int dx, int dy)
	{
		setRectangle(x + dx, y + dy);
	}
	
	public void constructRectangle()
	{
		rect = new Rectangle2D.Double(x, y, width, height);
	}
	
	public void setRectangle(int newx, int newy)
	{
		x = newx;
		y = newy;
		
		constructRectangle();
	}
	
	
	

	
} //end of GmmlGeneProduct