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
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
/**
  *This class contains the braces. It contains a constructor, and the methods contains, setLocation and getHelpers
  */

public class GmmlBrace extends GmmlGraphics
{
	double cX;
	double cY;
	double width;
	double ppo;
	
	int orientation; //orientation: 0=top, 1=right, 2=bottom, 3=left
	Color color;
	
	GmmlDrawing canvas;
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
		return true;
	}
	
	protected void moveBy(double dx, double dy)
	{
		setLocation(cX + dx, cY + dy);
	}
	
	protected void resizeX(double dx){}
	protected void resizeY(double dy){}
	
} //end of GmmlBrace