//import java.awt.;
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

class GmmlDrawing extends JPanel implements MouseListener, MouseMotionListener, EventListener
{	
	Vector graphics;
	Vector lineHandles;
	
	GmmlGraphics pressedGraphics = null;	
	GmmlGraphics clickedGraphics = null;
	GmmlGraphics draggedGraphics = null;
	
	double previousX;
	double previousY;
	
	boolean selecting = false;
	
	/**
	 *Constructor for this class
	 */	
	public GmmlDrawing()
	{
		graphics		= new Vector();
		lineHandles	= new Vector();

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
	}
	
	public void addElement(Object o)
	{
		graphics.addElement(o);
		if(o instanceof GmmlHandle)
		{
			GmmlHandle h = (GmmlHandle)o;
			if(h.type == 3 || h.type == 4)
			{
				lineHandles.addElement(h);
			}
		}
	}
	
	private void addLineHandle(GmmlHandle h)
	{
		lineHandles.addElement(h);
	}
	

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
		
		Iterator it = graphics.iterator();
		while (it.hasNext())
		{
			GmmlGraphics g = (GmmlGraphics) it.next();
			if (g.isContain(p))
			{
				pressedGraphics = g;
				break;
			}
		}
		
		if (pressedGraphics == null)
		{
			return;
		}
		else
		{
			// start dragging
			previousX = x;
			previousY = y;
			
			draggedGraphics = pressedGraphics;
			pressedGraphics = null;
		}
	}
	
	public void mouseReleased(MouseEvent e)
	{
		if (draggedGraphics == null)
		{
			return;
		}
		
		double x = e.getX();
		double y = e.getY();
		
		draggedGraphics.moveBy(x - previousX, y - previousY);
		draggedGraphics = null;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		if (draggedGraphics != null)
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
		
		repaint();
	}
	
	public void mouseEntered(MouseEvent e)
	{
	}
	
	public void mouseExited(MouseEvent e)
	{
	}
	
	public void mouseDragged(MouseEvent e)
	{
		if (draggedGraphics != null)
		{
			double x = e.getX();
			double y = e.getY();
			
			draggedGraphics.moveBy(x - previousX, y - previousY);
			
			previousX = x;
			previousY = y;
	
			repaint();
		}
	}

	public void mouseMoved(MouseEvent e)
	{
	}	
	
	public void actionPerformed(ActionEvent e)
	{
	}
	
	private void startSelect(Point2D p)
	{
	}
		
} // end of class