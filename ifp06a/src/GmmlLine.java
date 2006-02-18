import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Component;
import java.awt.Polygon;
import javax.swing.JPanel;
import javax.swing.JComponent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
 
 
public class GmmlLine extends GmmlGraphics
{
	int ID;
	
	double startx;
	double starty;
	double endx;
	double endy;
	double mx;
	double my;
	
	int style;
	int type;
	
	Color color;
	
	GmmlDrawing canvas;
	Line2D line;
	
	GmmlHandle handlecenter = new GmmlHandle(0, this);
	GmmlHandle handleStart	= new GmmlHandle(3, this);
	GmmlHandle handleEnd		= new GmmlHandle(4, this);
	
	public GmmlLine(GmmlDrawing canvas)
	{
		this.canvas = canvas;
		
		canvas.addElement(handlecenter);
		canvas.addElement(handleStart);
		canvas.addElement(handleEnd);
	}

	public GmmlLine(int x1, int y1, int x2, int y2, Color color, GmmlDrawing canvas)
	{
		startx 	= x1;
		starty 	= y1;
		endx 		= x2;
		endy 		= y2;
		
		this. color = color;
		
		line = new Line2D.Double(startx, starty, endx, endy);
		
		this.canvas = canvas;

		setHandleLocation();
		canvas.addElement(handlecenter);
		canvas.addElement(handleStart);
		canvas.addElement(handleEnd);	
	}

	public void constructLine()
	{
		line = new Line2D.Double(startx, starty, endx, endy);
	}
		
	protected void draw(Graphics g)
	{
		if(line!=null)
		{	
			Graphics2D g2D = (Graphics2D)g;
			g2D.setColor(color);
			float[] dash = {3.0f};	
			if (style == 0)
			{
				g2D.setStroke(new BasicStroke(1.0f));
			}
			else if (style == 1)
			{ 
				g2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			}			
	 		g2D.draw(this.line);
			
			if (type == 1)
			{
				drawArrowhead(g2D);
			}
			setHandleLocation();
		}
	}
	
	private void drawArrowhead(Graphics2D g2D) //2Do! clean up this mess.....
	{
		g2D.setColor(color);
		double angle = 25.0;
		double theta = Math.toRadians(180 - angle);
		double[] rot = new double[2];
		double[] p = new double[2];
		double[] q = new double[2];
		double a, b, norm;
		
		rot[0] = Math.cos(theta);
		rot[1] = Math.sin(theta);
		
		g2D.setStroke(new BasicStroke(1.0f));

		a = endx-startx;
		b = endy-starty;
		norm = 8/(Math.sqrt((a*a)+(b*b)));				
		p[0] = ( a*rot[0] + b*rot[1] ) * norm + endx;
		p[1] = (-a*rot[1] + b*rot[0] ) * norm + endy;
		q[0] = ( a*rot[0] - b*rot[1] ) * norm + endx;
		q[1] = ( a*rot[1] + b*rot[0] ) * norm + endy;
		int[] x = {(int) (endx),(int) (p[0]),(int) (q[0])};
		int[] y = {(int) (endy),(int) (p[1]),(int) (q[1])};
		Polygon arrowhead = new Polygon(x,y,3);
		g2D.draw(arrowhead);
		g2D.fill(arrowhead);
	}
  
	/* Checks whether a Point cuts the Line */
	protected boolean isContain(Point2D point)
	{
		BasicStroke stroke = new BasicStroke(10);
		Shape outline = stroke.createStrokedShape(line);
		isSelected = outline.contains(point);
		return isSelected;
	}
	
	/* Checks whether an area cuts the Line */
	public boolean intersects(Rectangle r)
	{
		BasicStroke stroke = new BasicStroke(10);
		Shape outline = stroke.createStrokedShape(line);
		if (outline.contains(r)) 
		{
			isSelected = true;
		}
    	else
    	{
	   	isSelected = false;
		}
		
    	return isSelected;
	}
	
	/* Methods for resizing Lines */
	public void setLine(double x1, double y1, double x2, double y2)
 	{
 		startx = x1;
		starty = y1;
		endx   = x2;
		endy   = y2;
		
		constructLine();
	}
 	
 	/* Methods for resizing Lines */
 	public void setLine(Point2D start, Point2D end)
 	{
 		startx = start.getX();
		starty = start.getY();
		endx   = end.getX();
		endy   = end.getY();
		
		constructLine();
 	}
	
	protected void moveBy(double dx, double dy)
	{
		setLine(startx + dx, starty + dy, endx + dx, endy + dy);
	}
	
	protected void resizeX(double dx){}
	protected void resizeY(double dy){}

	private void setHandleLocation()
	{
		handlecenter.setLocation((startx + endx)/2, (starty + endy)/2);
		handleStart.setLocation(startx, starty);
		handleEnd.setLocation(endx, endy);
	}
	
	protected void moveLineStart(double dx, double dy)
	{
		startx += dx;
		starty += dy;
		constructLine();
	}
	
	protected void moveLineEnd(double dx, double dy)
	{
		endx += dx;
		endy += dy;
		constructLine();
	}

} // end of classdsw
