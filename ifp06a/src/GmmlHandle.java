import java.awt.Point;
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
	
	GmmlDrawing canvas;
	
	int width 	= 10;
	int height	= 10;
	
	Rectangle2D rect;
	boolean visible;
	
	public GmmlHandle(int t, GmmlGraphics par)
	{
		type = t;
		parent = par;	
	}
	
	protected void draw(Graphics g)
	{
//		System.out.println("draw");
		Graphics2D g2D = (Graphics2D)g;
		constructRectangle();
		g2D.setColor(Color.red);
		g2D.fill(rect);
	}

	protected boolean isContain(Point p)
	{
		return rect.contains(p);
	}
	
	protected void moveBy(int dx, int dy)
	{
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
} // end of class
