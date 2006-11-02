package visualization.plugins;

import java.util.HashMap;
import java.util.Random;

import gmmlVision.GmmlVision;
import graphics.GmmlGeneProduct;
import graphics.GmmlGraphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.jdom.Element;

import util.SwtUtils;
import visualization.Visualization;

import data.GmmlData;
import data.GmmlDataObject;

/**
 * Provides label for Gene Product
 * @author thomas
 *
 */
public class ColorByLinkPlugin extends VisualizationPlugin {	
	static final String NAME = "Graphical link color";
			
	HashMap<Integer, RGB> id2col;
	Random rnd;
	
	public ColorByLinkPlugin(Visualization v) {
		super(v);
		CONFIGURABLE = false;
		CAN_USE = DRAWING;
		GENERIC = true;
		
		id2col = new HashMap<Integer, RGB>();
		rnd = new Random();
	}

	public String getName() { return NAME; }
	
	public void createSidePanelComposite(Composite parent) { }

	public void draw(GmmlGraphics g, PaintEvent e, GC buffer) {
		GmmlDataObject gd = g.getGmmlData();
		int[] ids = parseIds(gd);
		if(ids[0] != 0) { //This is a shape
			drawShape(ids[0], g, e, buffer);
			return;
		}
		if(ids[1] != 0) {
			drawLineStart(ids[1], g, e, buffer);
		}
		if(ids[2] != 0) {
			drawLineEnd(ids[2], g, e, buffer);
		}
	}
	
	void drawLineStart(int id, GmmlGraphics g, PaintEvent e, GC buffer) {
		Region r = g.createVisualizationRegion();
		Rectangle bounds = r.getBounds();
		bounds.width = bounds.width/2;
		bounds.height = bounds.height/2;
		buffer.drawRectangle(bounds);
		r.intersect(bounds);
		
		buffer.setClipping(r);
		drawShape(id, g, e, buffer);
		Region none = null;
		buffer.setClipping(none);
		
		r.dispose();
	}
	
	void drawLineEnd(int id, GmmlGraphics g, PaintEvent e, GC buffer) {
		Region r = g.createVisualizationRegion();
		Rectangle bounds = r.getBounds();
		bounds.x = bounds.width/2;
		bounds.y = bounds.height/2;
		bounds.width /= 2;
		bounds.height /=2;
		r.intersect(bounds);
		
		buffer.setClipping(r);
		drawShape(id, g, e, buffer);
		Region none = null;
		buffer.setClipping(none);
		
		r.dispose();
	}
	
	void drawShape(int id, GmmlGraphics g, PaintEvent e, GC buffer) {
		GmmlDataObject gd = g.getGmmlData();
		RGB oldRGB = gd.getColor();
		gd.dontFireEventsOnce();
		gd.setColor(getRGB(id));
		g.draw(e, buffer);
		gd.dontFireEventsOnce();
		gd.setColor(oldRGB);
	}
	
	RGB getRGB(int id) {
		RGB rgb = id2col.get(id);
		if(rgb == null) {
			rgb = randomRGB();
			id2col.put(id, rgb);
		}
		return rgb;
	}
	
	RGB randomRGB() {
		return new RGB(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
	}
	
	int[] parseIds(GmmlDataObject gd) {
		int[] ids = new int[3];
		try { ids[0] = Integer.parseInt(gd.getGraphId()); } catch(NumberFormatException e) {}
		try { ids[1] = Integer.parseInt(gd.getStartGraphRef()); } catch(NumberFormatException e) {}
		try { ids[2] = Integer.parseInt(gd.getEndGraphRef()); } catch(NumberFormatException e) {}
		return ids;
	}
		
	public Composite getToolTipComposite(Composite parent, GmmlGraphics g) { return null; }
	
	public void updateSidePanel(GmmlGraphics g) {	}
}
