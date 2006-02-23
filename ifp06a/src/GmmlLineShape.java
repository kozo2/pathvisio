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

//import java.awt.*;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.BasicStroke;
import javax.swing.JPanel;

/**
  *This class contains the lineshapes. It contains a constructor, and the methods contains, setLocation and getHelpers
  */
public class GmmlLineShape extends GmmlGraphics{
	
	double startx;
	double starty;
	double endx;
	double endy;
	
	int type; 

	GmmlDrawing canvas;
	Color color;

	GmmlHandle handlecenter = new GmmlHandle(0, this);
	GmmlHandle handleStart	= new GmmlHandle(3, this);
	GmmlHandle handleEnd		= new GmmlHandle(4, this);

	
	/**
	*Constructor
	*/
	public GmmlLineShape(GmmlDrawing canvas)
	{
		this.canvas = canvas;

		canvas.addElement(handlecenter);
		canvas.addElement(handleStart);
		canvas.addElement(handleEnd);
	}
	
	/**
	  *Constructor GmmlLineShape has 4 doubles for the coordinates, an int for the type and a color object for the color as input.
	  */
	public GmmlLineShape(double startx, double starty, double endx, double endy, int type, Color color, GmmlDrawing canvas)
	{
		this.startx = startx;
		this.starty = starty;
		this.endx 	= endx;
		this.endy 	= endy;
		this.type 	= type;
		this.color 	= color;
		this.canvas = canvas;
		
		canvas.addElement(handlecenter);
		canvas.addElement(handleStart);
		canvas.addElement(handleEnd);		
	}
	
	/**
	  *Method setLocation changes the int x and y coordinate to the x and y that are arguments for this method
	  */	
	public void setLocation(double x1, double y1, double x2, double y2)
	{
		startx = x1;
		starty = y1;
		endx	 = x2;
		endy	 = y2;
	}
	
	protected void draw(Graphics g)
	{
		//Types:
		// 0 - Tbar
		// 1 - Receptor round
		// 2 - Ligand round
		// 3 - Receptor square
		// 4 - Ligand square

		Graphics2D g2D = (Graphics2D)g;
		g2D.setColor(color);
		g2D.setStroke(new BasicStroke(1.0f));		

		double s = Math.sqrt(((endx-startx)*(endx-startx)) + ((endy - starty)*(endy - starty)));
		
		if (type == 0)
		{
			s /= 8;
			
			double capx1 = ((-endy + starty)/s) + endx;
			double capy1 = (( endx - startx)/s) + endy;
			double capx2 = (( endy - starty)/s) + endx;
			double capy2 = ((-endx + startx)/s) + endy;

			Line2D.Double l1 = new Line2D.Double(startx, starty, endx, endy);
			Line2D.Double l2 = new Line2D.Double(capx1, capy1, capx2, capy2);

			g2D.draw(l1);
			g2D.draw(l2);
		}
		
		else if (type == 1)
		{
			double dx = (endx - startx)/s;
			double dy = (endy - starty)/s;
			
			Line2D.Double l 					= new Line2D.Double(startx, starty, endx - (6*dx), endy - (6*dy));
			Ellipse2D.Double ligandround 	= new Ellipse2D.Double(endx - 5, endy - 5, 10, 10);
			
			g2D.draw(l);
			g2D.draw(ligandround);
			g2D.fill(ligandround);			
		}
		
		else if (type == 2)
		{
			double theta 	= Math.toDegrees(Math.atan((endx - startx)/(endy - starty)));
			double dx 		= (endx - startx)/s;
			double dy 		= (endy - starty)/s;	
			
			Line2D.Double l = new Line2D.Double(startx, starty, endx - (8*dx), endy - (8*dy));
			Arc2D.Double  a = new Arc2D.Double(startx - 8, endy - 8, 16, 16, theta + 180, -180, Arc2D.OPEN);
			
			g2D.draw(l);
			g2D.draw(a);
		}
		
		else if (type == 3)
		{
			s /= 8;
			
			double x3 		= endx - ((endx - startx)/s);
			double y3 		= endy - ((endy - starty)/s);
			double capx1 	= ((-endy + starty)/s) + x3;
			double capy1 	= (( endx - startx)/s) + y3;
			double capx2 	= (( endy - starty)/s) + x3;
			double capy2 	= ((-endx + startx)/s) + y3;			
			double rx1		= capx1 + 1.5*(endx - startx)/s;
			double ry1 		= capy1 + 1.5*(endy - starty)/s;
			double rx2 		= capx2 + 1.5*(endx - startx)/s;
			double ry2 		= capy2 + 1.5*(endy - starty)/s;
			
			
			Line2D.Double l 	= new Line2D.Double(startx, starty, x3, y3);		
			Line2D.Double cap = new Line2D.Double(capx1, capy1, capx2, capy2);
			Line2D.Double r1	= new Line2D.Double(capx1, capy1, rx1, ry1);
			Line2D.Double r2	= new Line2D.Double(capx2, capy2, rx2, ry2);

			g2D.draw(l);
			g2D.draw(cap);
			g2D.draw(r1);
			g2D.draw(r2);
		}
		else if (type == 4)
		{
			s /= 6;
			double x3 		= endx - ((endx - startx)/s);
			double y3 		= endy - ((endy - starty)/s);

			int[] polyx = new int[4];
			int[] polyy = new int[4];
			
			polyx[0] = (int) (((-endy + starty)/s) + x3);
			polyy[0] = (int) ((( endx - startx)/s) + y3);
			polyx[1] = (int) ((( endy - starty)/s) + x3);
			polyy[1] = (int) (((-endx + startx)/s) + y3);

			polyx[2] = (int) (polyx[1] + 1.5*(endx - startx)/s);
			polyy[2] = (int) (polyy[1] + 1.5*(endy - starty)/s);
			polyx[3] = (int) (polyx[0] + 1.5*(endx - startx)/s);
			polyy[3] = (int) (polyy[0] + 1.5*(endy - starty)/s);

			Line2D.Double l 	= new Line2D.Double(startx, starty, x3, y3);
			Polygon p 			= new Polygon(polyx, polyy, 4);
			
			g2D.draw(l);
			g2D.draw(p);
			g2D.fill(p);
		}
		setHandleLocation();
	}
	
	protected boolean isContain(Point2D point)
	{
		double s  = Math.sqrt(((endx-startx)*(endx-startx)) + ((endy-starty)*(endy-starty))) / 60;
		
		int[] x = new int[4];
		int[] y = new int[4];
			
		x[0] = (int)(((-endy + starty)/s) + endx);
		y[0] = (int)((( endx - startx)/s) + endy);
		x[1] = (int)((( endy - starty)/s) + endx);
		y[1] = (int)(((-endx + startx)/s) + endy);
		x[2] = (int)((( endy - starty)/s) + startx);
		y[2] = (int)(((-endx + startx)/s) + starty);
		x[3] = (int)(((-endy + starty)/s) + startx);
		y[3] = (int)((( endx - startx)/s) + starty);
			
		Polygon p = new Polygon(x, y, 4);
				
		isSelected = p.contains(point);
		return isSelected;
	}
	
	protected void moveBy(double dx, double dy)
	{
		setLocation(startx + dx, starty + dy, endx + dx, endy + dy);
	}
	
	protected void resizeX(double dx){}
	protected void resizeY(double dy){}

	protected void moveLineStart(double dx, double dy)
	{
		startx += dx;
		starty += dy;
//		constructLine();
	}
	
	protected void moveLineEnd(double dx, double dy)
	{
		endx += dx;
		endy += dy;
//		constructLine();
	}

	private void setHandleLocation()
	{
		handlecenter.setLocation((startx + endx)/2, (starty + endy)/2);
		handleStart.setLocation(startx, starty);
		handleEnd.setLocation(endx, endy);
	}

	protected boolean intersects(Rectangle2D.Double r)
	{
		double s  = Math.sqrt(((endx-startx)*(endx-startx)) + ((endy-starty)*(endy-starty))) / 60;
		
		int[] x = new int[4];
		int[] y = new int[4];
			
		x[0] = (int)(((-endy + starty)/s) + endx);
		y[0] = (int)((( endx - startx)/s) + endy);
		x[1] = (int)((( endy - starty)/s) + endx);
		y[1] = (int)(((-endx + startx)/s) + endy);
		x[2] = (int)((( endy - starty)/s) + startx);
		y[2] = (int)(((-endx + startx)/s) + starty);
		x[3] = (int)(((-endy + starty)/s) + startx);
		y[3] = (int)((( endx - startx)/s) + starty);
			
		Polygon p = new Polygon(x, y, 4);
				
		isSelected = p.intersects(r.x, r.y, r.width, r.height);
		return isSelected;
	}
}