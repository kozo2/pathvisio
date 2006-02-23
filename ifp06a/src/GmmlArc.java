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
import javax.swing.JPanel;
import java.awt.geom.Point2D;
import java.awt.BasicStroke;

public class GmmlArc extends GmmlGraphics
{
	double x;
	double y;
	double width;
	double height;
	double rotation;
	
	Color color;
	Arc2D arc;
	GmmlDrawing canvas;
	
	GmmlHandle handlecenter	= new GmmlHandle(0, this);
	GmmlHandle handlex		= new GmmlHandle(1, this);
	GmmlHandle handley		= new GmmlHandle(2, this);
	
	/**
	*Constructor
	*/
	public GmmlArc(GmmlDrawing canvas)
	{
		this.canvas = canvas;
		
		canvas.addElement(handlecenter);
		canvas.addElement(handlex);
		canvas.addElement(handley);
	}


	public GmmlArc(double x, double y, double width, double height, String color, double rotation, GmmlDrawing canvas)
	{
		this.x 			= x;
		this.y 			= y;
		this.width 		= width;
		this.height		= height;
		this.color 		= GmmlColor.convertStringToColor(color);
		this.rotation 	= Math.toDegrees(rotation);
		this.canvas 	= canvas;
		
		arc = new Arc2D.Double(x-width, y-height, 2*width, 2*height, 180-rotation, 180, 0);
		
		setHandleLocation();
		
		canvas.addElement(handlecenter);
		canvas.addElement(handlex);
		canvas.addElement(handley);
	}
	
	/**
	  *Method setLocation changes the double x and y coordinate to the x and y that are arguments for this method
	  */
	public void setLocation(double x, double y)
	{
		this.x = x;
		this.y = y;
		
		constructArc();
	}

	protected void draw(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
	
		g2D.setColor(color);
		g2D.setStroke(new BasicStroke(2.0f));
		
		g2D.draw(arc);
		
		setHandleLocation();
	}
	
	protected void moveBy(double dx, double dy)
	{
		setLocation(x + dx, y + dy);
	}
	
	protected boolean isContain(Point2D p)
	{
		isSelected =  arc.contains(p);
		return isSelected;
	}
	
	protected void resizeX(double dx)
	{
		width += dx;
		constructArc();
	}
	
	protected void resizeY(double dy)
	{
		height += dy;
		constructArc();
	}
	
	public void constructArc()
	{
		arc = new Arc2D.Double(x-width, y-height, 2*width, 2*height, 180-rotation, 180, 0);
	}
	
	public void setHandleLocation()
	{
		handlecenter.setLocation(x - width, y + height);
		handlex.setLocation(x + width, y - height/2);
		handley.setLocation(x + width/2, y + height);
	}
	
	protected boolean intersects(Rectangle2D.Double r)
	{
		isSelected = arc.intersects(r.x, r.y, r.width, r.height);
		return isSelected;

	}


} //end of GmmlArc
