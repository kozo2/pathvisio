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

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLayeredPane;
import javax.swing.JComponent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
 
 
public class GmmlLine extends JComponent implements MouseListener, MouseMotionListener
{
	int startx;
	int starty;
	int endx;
	int endy;
	
	int mx;
	int my;
	
	int style; 	// 0: solid; 	1: dashed
	int type; 	// 0: line; 	1: arrow
	
	int ID;
	
	Color color;
	
	BasicStroke stroke = new BasicStroke(10);
	Line2D line;
	
	boolean isSelected = false;
	boolean isContainWhilePress = false;
 
 	// constructor
 	public GmmlLine(int ID)
	{
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public GmmlLine(int ID, int x1, int y1, int x2, int y2, int style, int type, Color color)
	{
		startx = x1;
		starty = y1;
		endx = x2;
		endy = y2;
		
		this.style = style;
		this.type = type;
		
		line = new Line2D.Double(startx, starty, endx, endy);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
 
 	protected void paintComponent(Graphics g) 
 	{
 		Graphics2D g2D = (Graphics2D)g;
 		if(this.isSelected)
 			g2D.setColor(Color.RED);
 		else
 			g2D.setColor(Color.BLACK);
 		g2D.setStroke(new BasicStroke(5.0f));
 		g2D.draw(this.line);
 	}
 
 
	public void mouseClicked(MouseEvent arg0) 
	{
	}
 	
	public void mouseEntered(MouseEvent arg0) 
	{
	}
 	
	public void mouseExited(MouseEvent arg0) 
	{
	}
 	
	public void mousePressed(MouseEvent m) 
	{
	}
 	
	public void mouseReleased(MouseEvent arg0) 
	{
	}
	
	public void mouseMoved(MouseEvent e)
	{
	}
	
	public void mouseDragged(MouseEvent m)
	{
	}
	
	/* Methods for resizing Lines */
	public void setLine(int x1, int y1, int x2, int y2)
 	{
 		startx = x1;
		starty = y1;
		endx   = x2;
		endy   = y2;
		this.repaint();
 	}
 }