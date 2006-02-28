import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.awt.Graphics2D;


class GmmlSelectionBox extends GmmlGraphics
{
	double x;
	double y;

	Rectangle2D.Double r;
	GmmlDrawing canvas;
	
	public GmmlSelectionBox(GmmlDrawing d)
	{
		canvas = d;
		canvas.addElement(this);
	}	
	
	public void resize(double dx, double dy)
	{
		r = new Rectangle2D.Double(x, y, dx, dy);
	}
	
	protected void draw(Graphics g)
	{

		if(r != null && canvas.isSelecting)
		{
			Graphics2D g2D = (Graphics2D) g;			
				
			setDrawableRectangle();

			g2D.setColor(new Color(0f, 0f, 0.8f, 0.5f));
			g2D.fill(r);
			
			g2D.setStroke(new BasicStroke(1.0f));
			g2D.setColor(new Color(0f, 0f, 0.5f));
			g2D.draw(r);

		}
	}
		
	protected boolean isContain(Point2D p)
	{
		return false;
	}
	
	private void setDrawableRectangle()
	{
		double width  = r.width;
		double height = r.height;
		
		double x = r.x;
		double y = r.y;
		
		boolean changed = false;
		
      //Make sure rectangle width and height are positive.
      if (width < 0)
      {
	     	changed = true;
	      width = 0 - width;
         x = x - width + 1;
         if (x < 0)
         {
         	width += x;
            x = 0;
      	}
      }
      if (height < 0)
      {
      	changed = true;
	      height = 0 - height;
         y = y - height + 1;
         if (y < 0)
         {
         	height += y;
            y = 0;
         }
      }

      if (changed)
      {
      	r = new Rectangle2D.Double(x, y, width, height);
	   }
	}
   
   protected boolean intersects(Rectangle2D.Double r)
   {
   	return false;
   } 
   
   public void resetRectangle()
   {
   	r = new Rectangle2D.Double(0, 0, 0, 0);
   }
    
} // end of class