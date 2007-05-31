package org.pathvisio.view.swt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.pathvisio.view.VPathway;
import org.pathvisio.view.VPathwayWrapper;

public class VPathwaySWT extends Canvas implements VPathwayWrapper, PaintListener {
	final SWTGraphics2DRenderer renderer = new SWTGraphics2DRenderer();
	
	private VPathway child;
	
	public VPathwaySWT(Composite parent, int style) {
		super(parent, style);
	}

	public void setChild(VPathway c) {
		child = c;
		addPaintListener(this);
		addMouseListener(child);
		addMouseMoveListener(child);
		addMouseTrackListener(child);
		addKeyListener(child);
	}
	
	public void redraw(Rectangle r) {
		redraw(r.x, r.y, r.width, r.height, false);
	}

	public void setVSize(Dimension size) {
		setVSize(size.width, size.height);
	}

	public void setVSize(int w, int h) {
		setSize(w, h);
	}
	
	public Dimension getVSize() {
		org.eclipse.swt.graphics.Point p = getSize();
		return new Dimension(p.x, p.y);
	}
	
	public Rectangle getVBounds() {
		org.eclipse.swt.graphics.Rectangle b = getBounds();
		return new Rectangle(b.x, b.y, b.width, b.height);
	}
	
	public void paintControl(PaintEvent e) {
		GC gc = e.gc; // gets the SWT graphics context from the event

		gc.setClipping(e.x, e.y, e.width, e.height);
		
		renderer.prepareRendering(gc); // prepares the Graphics2D renderer

		Graphics2D g2d = renderer.getGraphics2D();
		g2d.setBackground(new Color(255, 255, 255));
		g2d.setColor(new Color(0, 0, 0));
		
		child.draw(g2d, new Rectangle(e.x, e.y, e.width, e.height));
		
		renderer.render(gc);
	}
}

/*
Canvas canvas = new Canvas(shell, SWT.NO_BACKGROUND);
final Graphics2DRenderer renderer = new Graphics2DRenderer();

canvas.addPaintListener(new PaintListener() {
  public void paintControl(PaintEvent e) {
    Point controlSize = ((Control) e.getSource()).getSize();

    GC gc = e.gc; // gets the SWT graphics context from the event

    renderer.prepareRendering(gc); // prepares the Graphics2D renderer

    // gets the Graphics2D context and switch on the antialiasing
    Graphics2D g2d = renderer.getGraphics2D();
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // paints the background with a color gradient
    g2d.setPaint(new GradientPaint(0.0f, 0.0f, java.awt.Color.yellow,
      (float) controlSize.x, (float) controlSize.y, java.awt.Color.white));
    g2d.fillRect(0, 0, controlSize.x, controlSize.y);

    // draws rotated text
    g2d.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16));
    g2d.setColor(java.awt.Color.blue);

    g2d.translate(controlSize.x / 2, controlSize.y / 2);
    int nbOfSlices = 18;
    for (int i = 0; i < nbOfSlices; i++) {
      g2d.drawString("Angle = " + (i * 360 / nbOfSlices) + "\u00B0", 30, 0);
      g2d.rotate(-2 * Math.PI / nbOfSlices);
    }

    // now that we are done with Java 2D, renders Graphics2D operation
    // on the SWT graphics context
    renderer.render(gc);

    // now we can continue with pure SWT paint operations
    gc.drawOval(0, 0, controlSize.x, controlSize.y);
  }
});
*/