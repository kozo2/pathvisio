//import java.awt.;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

class GmmlDrawing extends JPanel implements MouseListener, MouseMotionListener, EventListener
{	
	Vector drawingObjects;
	Vector graphics;
	Vector handles;
	Vector lineHandles;

	Vector selectedGraphics;
	
	GmmlGraphics pressedGraphics = null;	
	GmmlGraphics clickedGraphics = null;
	GmmlGraphics draggedGraphics = null;
	
	GmmlSelectionBox s; 
	
	boolean isSelecting;
		
	double previousX;
	double previousY;
	
	boolean selecting = false;
	
	/**
	 *Constructor for this class
	 */	
	public GmmlDrawing()
	{
		drawingObjects		= new Vector();
		graphics				= new Vector();
		handles				= new Vector();
		lineHandles			= new Vector();
		selectedGraphics	= new Vector();
		
		s = new GmmlSelectionBox(this);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		this.setBackground(Color.white);
		setSize(800, 600);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Iterator it = graphics.iterator();	
		while (it.hasNext())
		{
			GmmlGraphics gmmlg = (GmmlGraphics) it.next();
			gmmlg.draw(g);
		}

		it = handles.iterator();
		while (it.hasNext())
		{
			GmmlHandle h = (GmmlHandle) it.next();
			h.draw(g);
		}

		it = lineHandles.iterator();
		while (it.hasNext())
		{
			GmmlHandle h = (GmmlHandle) it.next();
			h.draw(g);
		}		
	}
	
	public void addElement(Object o)
	{
		drawingObjects.addElement(o);

		if(o instanceof GmmlHandle)
		{
			GmmlHandle h = (GmmlHandle)o;
			if(h.type == 3 || h.type == 4)
			{
				lineHandles.addElement(h);
			}
			else 
			{
				handles.addElement(h);
			}	
		}
		else
		{
			GmmlGraphics g = (GmmlGraphics) o;
			graphics.addElement(g);
			
		}
	}
	
/*	private void addLineHandle(GmmlHandle h)
	{
		lineHandles.addElement(h);
	}*/
	

	public void mousePressed(MouseEvent e)
	{
		if (draggedGraphics != null)
		{	
			// dragging in progress...
			return;
		}

		double x = e.getX();
		double y = e.getY();
		
		Point2D p = new Point2D.Double(x, y);
		
		Iterator it = drawingObjects.iterator();
		while (it.hasNext())
		{
			GmmlGraphics g = (GmmlGraphics) it.next();
			if (g.isContain(p))
			{
				pressedGraphics = g;
				break;
			}
		}
		repaint();
		
		if (pressedGraphics != null)
		{
			// start dragging
			isSelecting = false;
						
			previousX = x;
			previousY = y;
			
			draggedGraphics = pressedGraphics;
			pressedGraphics = null;
		}
		
		else if (pressedGraphics == null)
		{
			// start selecting
			isSelecting = true;
			initSelection(p);
		}
	}
	
	public void mouseReleased(MouseEvent e)
	{
		if (isSelecting)
		{
			Rectangle2D.Double r = s.r;
			s.r = null;
			Iterator it = graphics.iterator();

			while (it.hasNext())
			{
				GmmlGraphics g = (GmmlGraphics) it.next();
				if (g.intersects(r))
				{
					selectedGraphics.addElement(g);
				}
				
			}			
			isSelecting = false;
			repaint();
		}
		
		else if (draggedGraphics == null)
		{
			return;
		}
		
		else
		{
			double x = e.getX();
			double y = e.getY();
			
			draggedGraphics.moveBy(x - previousX, y - previousY);
			draggedGraphics = null;
		}
	}
	
	public void mouseClicked(MouseEvent e)
	{
/*		if (draggedGraphics != null)
		{	
			// dragging in progress...
			return;
		}

		double x = e.getX();
		double y = e.getY();
		
		Point2D p = new Point2D.Double(x, y);
		
		Iterator it = graphics.iterator();
		
		boolean graphicsFound = false;
		while (it.hasNext() && !graphicsFound)
		{
			GmmlGraphics g = (GmmlGraphics) it.next();
			g.isContain(p);
		}
		repaint();*/
	}
	
	public void mouseEntered(MouseEvent e)
	{
	}
	
	public void mouseExited(MouseEvent e)
	{
	}
	
	public void mouseDragged(MouseEvent e)
	{
		if (!selectedGraphics.isEmpty())
		{
			double x = e.getX();
			double y = e.getY();
			
			Iterator it = selectedGraphics.iterator();
			while (it.hasNext())
			{
				GmmlGraphics g = (GmmlGraphics) it.next();
				g.isSelected = true;
				g.moveBy(x - previousX, y - previousY);
			}
			previousX = x;
			previousY = y;			
		}
		
		if (draggedGraphics != null)
		{
			double x = e.getX();
			double y = e.getY();
			
			draggedGraphics.moveBy(x - previousX, y - previousY);
			
			previousX = x;
			previousY = y;
	
			repaint();
		}
						
		if (isSelecting)
		{
			s.resize(e.getX() - s.x, e.getY() - s.y);
			repaint();
		}
	}

	public void mouseMoved(MouseEvent e)
	{
	}	
	
	public void actionPerformed(ActionEvent e)
	{
	}
	
	private void initSelection(Point2D p)
	{
		selectedGraphics.clear();
		s.resetRectangle();
		s.x = p.getX();
		s.y = p.getY();		
	}
		
} // end of class