package org.pathvisio.cytoscape;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.pathvisio.model.LineStyle;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.ShapeType;
import org.pathvisio.view.ShapeRegistry;

import ding.view.DGraphView;
import ding.view.ViewportChangeListener;

public class Shape extends Annotation implements ViewportChangeListener {	
	
	public Shape(PathwayElement pwElm, DGraphView view) {
		super(pwElm, view);
	}

	public Rectangle2D.Double getVRectangle() {
		return new Rectangle2D.Double(getVLeft(), getVTop(), getVWidth(), getVHeight());
	}
	
	public java.awt.Shape getVOutline() {
		Rectangle2D.Double r = getVRectangle();
		r.width = r.width + 2;
		r.height = r.height + 2;
		AffineTransform f = new AffineTransform();
		f.rotate(pwElm.getRotation(), getVCenterX(), getVCenterY());
		java.awt.Shape outline = f.createTransformedShape(r);
		return outline;
	}
	
	public Rectangle getUnrotatedBounds() {
		java.awt.Shape outline = viewportTransform(getVOutline());
		Rectangle2D b = outline.getBounds();
		AffineTransform f = new AffineTransform();
		f.rotate(-pwElm.getRotation(), b.getCenterX(), b.getCenterY());
		return f.createTransformedShape(outline).getBounds();
	}
		
	public void doPaint(Graphics2D g2d) {
		//Rectangle b = relativeToBounds(getUnrotatedBounds()).getBounds();
		Rectangle b = relativeToBounds(viewportTransform(getVRectangle())).getBounds();
		
		Color fillcolor = pwElm.getFillColor();
		Color linecolor = pwElm.getColor();
		
		int sw = (int)Math.ceil(getStrokeWidth());
		int x = b.x;
		int y = b.y;
		int w = b.width - sw - 1;
		int h = b.height - sw - 1;
		int cx = x + w/2;
		int cy = y + h/2;
						
		java.awt.Shape s = null;

		if (pwElm.getShapeType() == null)
		{
			s = ShapeRegistry.getShape ("Default", x, y, w, h);
		}
		else
		{
			s = ShapeRegistry.getShape (
					pwElm.getShapeType().getName(),
					x, y, w, h);
		}

		AffineTransform t = new AffineTransform();
		t.rotate(pwElm.getRotation(), cx, cy);
		s = t.createTransformedShape(s);
		
		if (pwElm.getShapeType() == ShapeType.BRACE ||
				pwElm.getShapeType() == ShapeType.ARC)
			{
				// don't fill arcs or braces
				// TODO: this exception should disappear in the future,
				// when we've made sure all pathways on wikipathways have
				// transparent arcs and braces
			}
			else
			{
				// fill the rest
				if(!pwElm.isTransparent())
				{
					g2d.setColor(fillcolor);
					g2d.fill(s);
				}
			}

			g2d.setColor(linecolor);
			int ls = pwElm.getLineStyle();
			if (ls == LineStyle.SOLID)
			{
				g2d.setStroke(new BasicStroke());
			}
			else if (ls == LineStyle.DASHED)
			{ 
				g2d.setStroke	(new BasicStroke (
					  1, 
					  BasicStroke.CAP_SQUARE,
					  BasicStroke.JOIN_MITER, 
					  10, new float[] {4, 4}, 0));
			}
			
			g2d.draw(s);
	}
	
	
}
