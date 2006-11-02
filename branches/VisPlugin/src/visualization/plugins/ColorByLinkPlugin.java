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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.jdom.Element;

import util.SwtUtils;

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
	
	public ColorByLinkPlugin() {
		super();
		CONFIGURABLE = false;
		CAN_USE = DRAWING;
		GENERIC = true;
		
		id2col = new HashMap<Integer, RGB>();
		rnd = new Random();
	}

	public String getName() { return NAME; }
	
	public void createSidePanelComposite(Composite parent) { }

	public void draw(GmmlGraphics g, PaintEvent e, GC buffer) {
		RGB rgb = getRGB(g);
		if(rgb == null) return;
		
		GmmlDataObject gd = g.getGmmlData();
		RGB oldRGB = gd.getColor();
		gd.setColor(rgb);
		g.draw(e, buffer);
		gd.setColor(oldRGB);
	}
	
	RGB getRGB(GmmlGraphics g) {
		int colorId = -1;
		int[] ids = parseIds(g.getGmmlData());
		if			(ids[0] != 0) { //This is a reference object
			colorId = ids[0];
		} else if	(ids[1] != 0) {
			colorId = ids[1];
		} else if	(ids[2] != 0) { //TODO: half color
			colorId = ids[2];
		}
		if(colorId == -1) return null;
		
		if(id2col.containsKey(colorId)) return id2col.get(colorId);
		else {
			RGB rgb = getRandomRGB();
			id2col.put(colorId, rgb);
			return rgb;
		}
	}
	
	RGB getRandomRGB() {
		return new RGB(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
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
