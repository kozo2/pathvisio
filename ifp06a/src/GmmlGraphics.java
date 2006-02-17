import java.awt.Point;
import java.awt.event.*;
import java.awt.Graphics;
import javax.swing.JComponent;

abstract class GmmlGraphics extends JComponent //implements MouseListener, MouseMotionListener
{
	boolean isSelected;
		
	abstract void draw(Graphics g);

	abstract void moveBy(double dx, double dy);

	abstract void resizeX(double dx);
	abstract void resizeY(double dy);

	abstract boolean isContain(Point point);
	
	void moveLineStart(double dx, double dy){}
	void moveLineEnd(double dx, double dy){}
	
}