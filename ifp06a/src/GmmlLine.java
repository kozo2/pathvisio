import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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
	
	int startx;
	int starty;
	int endx;
	int endy;
	int mx;
	int my;
	
	int style;
	int type;
	
	Color color;
	
	JPanel canvas;
	BasicStroke stroke = new BasicStroke(10);
	Line2D line;
	
	boolean isSelected = false;
	boolean isContainWhilePress = false;
 

	public GmmlLine(JPanel canvas)
	{
		super();
		this.canvas = canvas;
		this.setOpaque(false);
		this.setRequestFocusEnabled(true);
		canvas.add(this);
	}

	public GmmlLine(int x1, int y1, int x2, int y2, JPanel canvas)
	{
		super();
		
		startx 	= x1;
		starty 	= y1;
		endx 		= x2;
		endy 		= y2;
		
		line = new Line2D.Double(startx, starty, endx, endy);
		
		this.canvas = canvas;
		this.setOpaque(false);
		this.setRequestFocusEnabled(true);
		canvas.add(this);
		updateUI();
	}

	public GmmlLine(int id)
	{
		ID = id;
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
		}
		else
		{
			System.out.println("Line not drawn");
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
	protected boolean isContain(Point point)
	{
		Shape outline = stroke.createStrokedShape(line);
		if (outline.contains(point)) 
		{
			isSelected = true;
	  	}
    	else
    	{
	    	isSelected = false;
		}
		
    	return isSelected;
	}
	
	public boolean intersects(Rectangle r)
	{
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
	
	public void setLine(int x1, int y1, int x2, int y2)
 	{
 		startx = x1;
		starty = y1;
		endx   = x2;
		endy   = y2;
		
		constructLine();
	}
 	
 	public void setLine(Point start, Point end)
 	{
 		startx = (int)start.getX();
		starty = (int)start.getY();
		endx   = (int)end.getX();
		endy   = (int)end.getY();
		
		constructLine();
 	}
	
	protected void moveBy(int dx, int dy)
	{
		setLine(startx + dx, starty + dy, endx + dx, endy + dy);
	}
}
