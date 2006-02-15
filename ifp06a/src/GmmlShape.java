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
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.BasicStroke;
import javax.swing.JPanel;
import java.awt.geom.Ellipse2D;
/**
  *This class contains the shapes. It contains a constructor, and the methods contains, setLocation and getHelpers
  */
public class GmmlShape extends GmmlGraphics {

	double centerx;
	double centery;
	double width;
	double height;
	double rotation;
	
	int type = 0;
	// types:
	// 0 - rectangle
	// 1 - ellipse
	
	JPanel canvas;
	Color color;
	
	/**
	 *Constructor
	 */
	public GmmlShape()
	{
	}
	
	/**
	  *Constructor GmmlShape has 4 doubles for the coordinates, an int for the type, 
	  *a double for the rotation and a color object for the color as input.
	  */
	public GmmlShape(double x, double y, double width, double height, int type, String color, double rotation, JPanel canvas)
	{
		this.centerx	= x;
		this.centery	= y;
		this.width 		= width;
		this.height 	= height;
		this.color 		= GmmlColor.convertStringToColor(color);
		this.type 		= type;
		this.rotation 	= rotation;
		this.canvas		= canvas;
	} //end of GmmlShape constructor
	
	/**
	 *Method setLocation changes the int x and y coordinate to the x and y that are arguments for this method
	 */
	public void setLocation(double x, double y)
	{
		centerx = x;
		centery = y;
	}

	protected void draw(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
	
		g2D.setStroke(new BasicStroke(1.0f));
		g2D.setColor(color);
		g2D.rotate(Math.toRadians(rotation), (centerx), (centery ));
		
		if (type == 0)
		{
			g2D.draw(new Rectangle2D.Double((centerx + width/2), (centery - height/2), width, height));
		}
		else if (type == 1)
		{
			g2D.draw(new Ellipse2D.Double(centerx - width, centery - height, 2*width, 2*height));
		}
		
		// reset rotation
		g2D.rotate(-Math.toRadians(rotation), (centerx), (centery));
	}		


	protected boolean isContain(Point p)
	{
		if (type== 0)
		{
			double theta = Math.toRadians(rotation);
			double[] rot = new double[2];
				
			rot[0] = Math.cos(theta);
			rot[1] = Math.sin(theta);
		
			int[] xs = new int[4];
			int[] ys = new int[4];
			
			xs[0]= (int)(( 0.5*width*rot[0] - 0.5*height*rot[1]) + centerx); //upper right
			xs[1]= (int)(( 0.5*width*rot[0] + 0.5*height*rot[1]) + centerx); //lower right
			xs[2]= (int)((-0.5*width*rot[0] + 0.5*height*rot[1]) + centerx); //lower left
			xs[3]= (int)((-0.5*width*rot[0] - 0.5*height*rot[1]) + centerx); //upper left
			
			ys[0]= (int)(( 0.5*width*rot[1] + 0.5*height*rot[0]) + centery); //upper right
			ys[1]= (int)(( 0.5*width*rot[1] - 0.5*height*rot[0]) + centery); //lower right
			ys[2]= (int)((-0.5*width*rot[1] - 0.5*height*rot[0]) + centery); //lower left
			ys[3]= (int)((-0.5*width*rot[1] + 0.5*height*rot[0]) + centery); //upper left
				
			Polygon pol= new Polygon(xs, ys, 4);
			
			if (pol.contains(p)) {
				return true;
			}
			else
			{
				return false;
			}
				
		}
		else {
			double theta = Math.toRadians(rotation);
			double[] rot = new double[2];
				
			rot[0] = Math.cos(theta);
			rot[1] = Math.sin(theta);
		
			int[] xs = new int[4];
			int[] ys = new int[4];
			
			xs[0]= (int)(( width*rot[0] - height*rot[1]) + centerx); //upper right
			xs[1]= (int)(( width*rot[0] + height*rot[1]) + centerx); //lower right
			xs[2]= (int)((-width*rot[0] + height*rot[1]) + centerx); //lower left
			xs[3]= (int)((-width*rot[0] - height*rot[1]) + centerx ); //upper left
			
			ys[0]= (int)(( width*rot[1] + height*rot[0]) + centery); //upper right
			ys[1]= (int)(( width*rot[1] - height*rot[0]) + centery); //lower right
			ys[2]= (int)((-width*rot[1] - height*rot[0]) + centery); //lower left
			ys[3]= (int)((-width*rot[1] + height*rot[0]) + centery); //upper left
				
			Polygon pol = new Polygon(xs, ys, 4);
			
			if (pol.contains(p))
			{
				return true;
			}
			else
			{
				return false;
			}
		}

	}

	protected void moveBy(int dx, int dy)
	{
		setLocation(centerx + dx, centery + dy);
	}

	

} //end of GmmlShape