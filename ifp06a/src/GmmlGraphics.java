import java.awt.geom.Point2D;
import java.awt.event.*;
import java.awt.Graphics;
import javax.swing.JComponent;

abstract class GmmlGraphics extends JComponent
{
	boolean isSelected;
		
	void draw(Graphics g){}
	void moveBy(double dx, double dy){}
	void resizeX(double dx){}
	void resizeY(double dy){}
	void moveLineStart(double dx, double dy){}
	void moveLineEnd(double dx, double dy){}

	abstract boolean isContain(Point2D point);
	
}