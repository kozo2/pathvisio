package visualization;

import gmmlVision.GmmlVision;
import graphics.GmmlGraphics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;

import visualization.VisualizationManager.VisualizationEvent;
import visualization.plugins.PluginManager;
import visualization.plugins.VisualizationPlugin;

/**
 * Represents a set of configured visualization plugins
 * @author thomas
 *
 */
public class Visualization {
	public static final String XML_ELEMENT = "visualization";
	public static final String XML_ATTR_NAME = "name";
	
	String name;
	HashMap<Class, VisualizationPlugin> plugins;
	ArrayList<VisualizationPlugin> drawingOrder;
	
	public Visualization(String name) {
		initPlugins();
		this.name = name;
	}
	
	void initPlugins() {
		plugins = new HashMap<Class, VisualizationPlugin>();
		drawingOrder = new ArrayList<VisualizationPlugin>();
		for(Class c : PluginManager.getPlugins()) {
			try {
				VisualizationPlugin p = PluginManager.getInstance(c, this);
				plugins.put(c, p);
				drawingOrder.add(p);
			} catch(Exception e) {
				GmmlVision.log.error("Unable to create instance of plugin " + c, e);
			}
		}
	}

	public String getName() { return name; }
	public void setName(String name) { 
		this.name = name;
		fireVisualizationEvent(VisualizationEvent.VISUALIZATION_MODIFIED);
	}
	
	public final void fireVisualizationEvent(int type) {
		VisualizationManager.firePropertyChange(
				new VisualizationEvent(this, type));
	}
	
	public boolean isGeneric() {
		for(VisualizationPlugin p : plugins.values())
			if(p.isActive() && !p.isGeneric()) return false; //One or more active non-generic plugins, so not generic
		return true;
	}
		
	public Collection getPlugins() {
		return plugins.values();
	}
	
	public void activatePlugin(Class pluginClass, VisualizationPlugin plugin) {
		drawingOrder.remove(plugins.get(pluginClass));
		plugins.put(pluginClass, plugin);
		drawingOrder.add(plugin);
		plugin.setActive(true);
		fireVisualizationEvent(VisualizationEvent.VISUALIZATION_MODIFIED);
	}
			
	public void drawToObject(GmmlGraphics g, PaintEvent e, GC buffer) {
		for(VisualizationPlugin p : drawingOrder) 
			if(p.isActive()) p.draw(g, e, buffer);
	}
	
	public Region getReservedRegion(VisualizationPlugin p, GmmlGraphics g) {
		//Determine number of active plugins that to reserve a region
		int nrRes = 0;
		int index = 0;
		for(VisualizationPlugin pl : drawingOrder) {
			if(pl == p) index = nrRes;
			nrRes += (pl.isActive() && pl.isUseReservedRegion()) ? 1 : 0;
		}
		
		Region region = g.createVisualizationRegion();
		//Distribute space over plugins
		Rectangle bounds = region.getBounds();
		int w = bounds.width / nrRes;
		int leftSpace = bounds.width - w * nrRes;
		bounds.x += w * index;
		bounds.width = w + (index == nrRes - 1 ? leftSpace : 0);
		
		
		region.intersect(bounds);
		return region;
	}
	
	public static final int ORDER_UP = 1;
	public static final int ORDER_DOWN = -1;
	public static final int ORDER_FIRST = 2;
	public static final int ORDER_LAST = -2;
	
	public void setDrawingOrder(VisualizationPlugin plugin, int order) {
		int index = drawingOrder.indexOf(plugin);
		switch(order) {
		case ORDER_UP:
			if(index == 0) break;
			drawingOrder.remove(index);
			drawingOrder.add(index - 1, plugin);
			break;
		case ORDER_DOWN:
			if(index == drawingOrder.size() - 1) break;
			drawingOrder.remove(index);
			drawingOrder.add(index + 1, plugin);
			break;
		case ORDER_FIRST:
			drawingOrder.remove(index);
			drawingOrder.add(0, plugin);
			break;
		case ORDER_LAST:
			drawingOrder.remove(index);
			drawingOrder.add(plugin);
			break;
		}
		fireVisualizationEvent(VisualizationEvent.VISUALIZATION_MODIFIED);
	}
	
	public List<VisualizationPlugin> getPluginsSorted() {
		return drawingOrder;
	}
	
	public void updateSidePanel(GmmlGraphics g) {
		for(VisualizationPlugin p : drawingOrder) 
			if(p.isActive()) p.updateSidePanel(g);
	}
	
	public boolean usesToolTip() {
		for(VisualizationPlugin p : plugins.values())
			if(p.isActive() && p.isUseToolTip()) return true;
		return false;
	}
	
	public Shell getToolTip(Display display, GmmlGraphics g) {
		Shell tip = new Shell(display, SWT.ON_TOP | SWT.TOOL);  
		tip.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		tip.setLayout(new RowLayout());
		
		for(VisualizationPlugin p : drawingOrder) 
			if(p.isActive() && p.isUseToolTip()) p.getToolTipComposite(tip, g);
		
		return tip;
	}
	
	public Element toXML() {
		Element vis = new Element(XML_ELEMENT);
		vis.setAttribute(XML_ATTR_NAME, getName());
		for(VisualizationPlugin p : drawingOrder)
			if(p.isActive()) vis.addContent(p.toXML());
		return vis;
	}
	
	public static Visualization fromXML(Element xml) {
		String name = xml.getAttributeValue(XML_ATTR_NAME);
		if(name == null) name = VisualizationManager.getNewName();
		
		Visualization v = new Visualization(name);
		
		for(Object o : xml.getChildren(VisualizationPlugin.XML_ELEMENT)) {
			try {
				VisualizationPlugin p = PluginManager.instanceFromXML((Element)o, v);
				v.activatePlugin(p.getClass(), p);
			} catch(Exception e) {
				GmmlVision.log.error("Unable to load VisualizationPlugin", e);
			}
		}
		
		return v;
	}
}
