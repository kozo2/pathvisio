import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

class GmmlHandle extends GmmlGraphics
{
	GmmlGraphics parent;
	int width 	= 20;
	int height	= 20;
	
	public GmmlHandle()
	{
		
	}
	
	protected void draw(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
		g2D.draw(new Rectangle2D.Double(5, 5, width, height));
	}

	protected boolean isContain(Point p)
	{
		return false;
	}

	protected void moveBy(int dx, int dy)
	{
	}

	private void getParentCoordinates()
	{
	}

} // end of class
