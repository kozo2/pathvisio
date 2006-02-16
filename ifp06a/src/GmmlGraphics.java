import java.awt.Point;
import java.awt.Graphics;
import javax.swing.JComponent;

abstract class GmmlGraphics //extends JComponent
{
	abstract void draw(Graphics g);
	abstract void moveBy(int dx, int dy);
	abstract boolean isContain(Point point);
}