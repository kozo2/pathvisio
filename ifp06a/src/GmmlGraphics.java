import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JTable;


/**
 * This class is a parent class for all graphics
 * that can be added to a GmmlDrawing.
 */
abstract class GmmlGraphics extends JComponent
{
	boolean isSelected;
	
	/**
	 * Adjusts the GmmlGraphics object to the zoom
	 * specified in the drawing it is part of
	 * @param factor - the factor to scale the objects coordinates and measures with
	 */
	void adjustToZoom(double factor){}
	
	/**
	 * Draws GmmlGraphics
	 * @param g - the graphics object to use for drawing
	 * @param magnification - magnify the object to draw by this number
	 */
	void draw(Graphics g){}
	
	/**
	 * Moves GmmlGraphics object by specified increments
	 * @param dx - the value of x-increment
	 * @param dy - the value of y-increment
	 */
	void moveBy(double dx, double dy){}
	
	/**
	 * Resizes GmmlGraphics in x-direction
	 * @param dx - the value with wich to resize the object
	 */
	void resizeX(double dx){}
	
	/**
	 * Resizes GmmlGraphics in y-direction
	 * @param dx - the value with wich to resize the object
	 */
	void resizeY(double dy){}
	
	/**
	 * Moves the start of a line by numbers specified
	 * @param dx - the value of x-increment
	 * @param dy - the value of y-increment
	 */
	void moveLineStart(double dx, double dy){}
	
	/**
	 * Moves the start of a line by numbers specified
	 * @param dx - the value of x-increment
	 * @param dy - the value of y-increment
	 */	
	void moveLineEnd(double dx, double dy){}
	
	/**
	 * Updates GmmlGraphics object properties from the 
	 * table specified.
	 * @param t - the table to get the properties from
	 */
	void updateFromPropertyTable(JTable t){}	
	
	/**
	 * Determines whether a GmmlGraphics object intersects 
	 * the rectangle specified
	 * @param r - the rectangle to check
	 * @return True if the object intersects the rectangle, false otherwise
	 */
	abstract boolean intersects(Rectangle2D.Double r);
	
	/**
	 * Determines wheter a GmmlGraphics object contains
	 * the point specified
	 * @param point - the point to check
	 * @return True if the object contains the point, false otherwise
	 */
	abstract boolean isContain(Point2D point);
	
	/**
	 * Gets the GmmlGraphics object properties and returns them
	 * in a table
	 * @return a table containing the objects properties
	 */
	abstract JTable getPropertyTable();
}