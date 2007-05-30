package org.pathvisio.view;

import java.awt.Point;
import java.awt.Rectangle;

public abstract interface VPathwayWrapper {		
	public void redraw();
	public void redraw(Rectangle r);
	public void setVSize(Point size);
	public void setVSize(int w, int h);
	public Point getVSize();
	public Rectangle getVBounds();
	
}
