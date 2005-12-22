import java.awt.*;
import java.awt.geom.Line2D;

public class GmmlLine {
	double startx, starty, endx, endy;
	int type, style;
	Color color;
	
	public GmmlLine (double inputstartx, double inputstarty, double inputendx, double inputendy, int inputtype, int inputstyle, Color inputcolor) {
		startx = inputstartx;
		starty = inputstarty;
		endx = inputendx;
		endy = inputendy;
		type = inputtype;
		style = inputstyle;
		color = inputcolor;
	}
	
	public boolean contains (double x, double y) {
		Line2D.Double templine = new Line2D.Double(startx, starty, endx, endy);
		boolean contains = templine.contains(x, y);
		return contains;
	}
	
	public boolean contains (double x, double y, double zf) {
		Line2D.Double templine = new Line2D.Double(startx, starty, endx, endy);
		boolean contains = templine.contains(x*zf, y*zf);
		return contains;
	}

	public void setLocation(double newstartx, double newstarty, double newendx, double newendy){
		startx = newstartx;
		starty = newstarty;
		endx = newendx;
		endy = newendy;
	}
}
	