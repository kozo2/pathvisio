import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

class GmmlDrawing extends JPanel implements MouseListener, MouseMotionListener
{	
	Vector shapes;
	
	GmmlGraphics pressedGraphics = null;	
	GmmlGraphics clickedGraphics = null;
	GmmlGraphics draggedGraphics = null;
	
	int previousX;
	int previousY;
	
	/**
	 *Constructor for this class
	 */	
	public GmmlDrawing()
	{
		shapes = new Vector();
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		this.setBackground(Color.white);
		setSize(800, 600);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Iterator it = shapes.iterator();
		while (it.hasNext())
		{
			GmmlGraphics gmmlg = (GmmlGraphics) it.next();
			gmmlg.draw(g);
		}
	}
	
	public void addElement(Object o)
	{
		shapes.addElement(o);
	}
	
	
	public void mousePressed(MouseEvent e)
	{
		if (draggedGraphics != null)
		{	
			// dragging in progress...
			return;
		}

		int x = e.getX();
		int y = e.getY();
		
		Point p = new Point(x, y);
		
		Iterator it = shapes.iterator();
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
		
		int x = e.getX();
		int y = e.getY();
		
		draggedGraphics.moveBy(x - previousX, y - previousY);
		draggedGraphics = null;
	}
	
	public void mouseClicked(MouseEvent e)
	{
	}
	
	public void mouseEntered(MouseEvent e)
	{
	}
	
	public void mouseExited(MouseEvent e)
	{
	}
	
	public void mouseDragged(MouseEvent e)
	{
		if (draggedGraphics == null)
		{
			return;
		}
		
		int x = e.getX();
		int y = e.getY();
		
		draggedGraphics.moveBy(x - previousX, y - previousY);
		
		previousX = x;
		previousY = y;

		repaint();
	}

	public void mouseMoved(MouseEvent e)
	{
	}	
	
} // end of class