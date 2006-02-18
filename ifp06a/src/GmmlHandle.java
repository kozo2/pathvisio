import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

class GmmlHandle extends GmmlGraphics
{
	GmmlGraphics parent;
	
	double centerx;
	double centery;
	
	int type = 0;
	// types:
	// 0: center
	// 1: x
	// 2: y
	// 3: line handle 
	
	GmmlDrawing canvas;
	
	int width 	= 10;
	int height	= 10;
	
	Rectangle2D rect;
	boolean visible;
	
	public GmmlHandle(int t, GmmlGraphics par)
	{
		type = t;
		parent = par;

		constructRectangle();
	}
	
	protected void draw(Graphics g)
	{
		if (parent.isSelected)
		{
			Graphics2D g2D = (Graphics2D)g;
			constructRectangle();
			g2D.setColor(Color.red);
			g2D.fill(rect);
		}
	}

	protected boolean isContain(Point2D p)
	{
		return rect.contains(p);
	}
	
	protected void moveBy(double dx, double dy)
	{
		if (type == 0)
		{
			parent.moveBy(dx, dy);
		}
		if (type == 1)
		{
			parent.resizeX(dx);
		}
		if (type == 2)
		{
			parent.resizeY(dy);
		}
		if (type == 3)
		{
			parent.moveLineStart(dx, dy);
		}
		if (type == 4)
		{
			parent.moveLineEnd(dx, dy);
		}
	}
	
	public void setLocation(double x, double y)
	{
		centerx = x;
		centery = y;
	}
	
	private void constructRectangle()
	{
		rect = new Rectangle2D.Double(centerx - width/2, centery - height/2, width, height);
	}
	
	public Point2D getCenterPoint()
	{
		Point2D p = new Point2D.Double(centerx, centery);
		return p;
	}
	
	protected void resizeX(double dx){}
	protected void resizeY(double dy){}
} // end of class


