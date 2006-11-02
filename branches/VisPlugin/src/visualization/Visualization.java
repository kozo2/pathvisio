package visualization;

import gmmlVision.GmmlVision;
import graphics.GmmlGraphics;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
	HashMap<Class, VisualizationPlugin> activePlugins;
	
	public Visualization(String name) {
		activePlugins = new HashMap<Class, VisualizationPlugin>();
		this.name = name;
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public boolean isGeneric() {
		for(VisualizationPlugin p : activePlugins.values())
			if(!p.isGeneric()) return false; //One or more non-generic plugins, so not generic
		return true;
	}
	
	public VisualizationPlugin getActivePlugin(Class pluginClass) { 
		return activePlugins.get(pluginClass); 
	}
	
	public Collection getActivePlugins() { return activePlugins.keySet(); }
	
	public boolean isActivePlugin(Class pluginClass) {
		return activePlugins.containsKey(pluginClass);
	}

	public void activatePlugin(Class pluginClass, VisualizationPlugin plugin) {
		if(!activePlugins.containsKey(pluginClass))
			activePlugins.put(pluginClass, plugin);
	}
	
	public void activatePlugin(Class pluginClass) throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if(!activePlugins.containsKey(pluginClass))
			activePlugins.put(pluginClass, PluginManager.getInstance(pluginClass));
	}
	
	public void deactivatePlugin(Class pluginClass) {
		activePlugins.remove(pluginClass);
	}
	
	public void drawToObject(GmmlGraphics g, PaintEvent e, GC buffer) {
		for(VisualizationPlugin p : activePlugins.values()) 
			p.draw(g, e, buffer);
	}
	
//	public Region getReservedRegion(VisualizationPlugin p, GmmlGraphics g) {
//		//Determine number of plugins that to reserve a region
//		int nrRes = 0;
//		for(VisualizationPlugin pl : activePlugins.values()) 
//			nrRes += pl.isUseReservedRegion() ? 1 : 0;
//		
//		Region region = g.createVisualizationRegion();
//		//Distribute space over plugins
//		Rectangle bounds = region.getBounds();
//		double pw = bounds.width / nrRes;
//		//We need some kind of order in plugins!!!!!!
//	}
	
	public static final int ORDER_UP = 1;
	public static final int ORDER_DOWN = -1;
	public static final int ORDER_FIRST = 2;
	public static final int ORDER_LAST = -2;
	
	public void setDrawingOrder(Class pluginClass, int order) {
		VisualizationPlugin p = getActivePlugin(pluginClass);
		
		List<VisualizationPlugin> sorted = getActivePluginsSorted();
		
		switch(order) {
		case ORDER_UP:
		case ORDER_DOWN:
			int index = sorted.indexOf(p);
			int other = sorted.get(index - order).getDrawingOrder();
			p.setDrawingOrder(other + order);
		case ORDER_FIRST:
			p.setDrawingOrder(sorted.get(0).getDrawingOrder() + 1);
		case ORDER_LAST:
			p.setDrawingOrder(sorted.get(sorted.size() - 1).getDrawingOrder() - 1);
		}
		assignDrawingOrders();
	}
	
	private List<VisualizationPlugin> getActivePluginsSorted() {
		List<VisualizationPlugin> sorted = 
			new ArrayList<VisualizationPlugin>(activePlugins.values());
		Collections.sort(sorted);
		return sorted;
	}
	
	private void assignDrawingOrders() {
		List<VisualizationPlugin> sorted = getActivePluginsSorted();
		int size = sorted.size();
		for(int i = 0; i < size; i++) sorted.get(i).setDrawingOrder(size + 1 - i);
	}
	
	public void updateSidePanel(GmmlGraphics g) {
		for(VisualizationPlugin p : activePlugins.values()) 
			p.updateSidePanel(g);
	}
	
	public Shell getToolTip(Display display, GmmlGraphics g) {
		Shell tip = new Shell(display, SWT.ON_TOP | SWT.TOOL);  
		tip.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		tip.setLayout(new RowLayout());
		
		for(VisualizationPlugin p : activePlugins.values()) 
			if(p.isUseToolTip()) p.getToolTipComposite(tip, g);
		
		return tip;
	}
	
	public Element toXML() {
		Element vis = new Element(XML_ELEMENT);
		vis.setAttribute(XML_ATTR_NAME, getName());
		for(VisualizationPlugin p : activePlugins.values())
			vis.addContent(p.toXML());
		return vis;
	}
	
	public static Visualization fromXML(Element xml) {
		String name = xml.getAttributeValue(XML_ATTR_NAME);
		if(name == null) name = VisualizationManager.getNewName();
		
		Visualization v = new Visualization(name);
		
		for(Object o : xml.getChildren(VisualizationPlugin.XML_ELEMENT)) {
			try {
				VisualizationPlugin p = PluginManager.instanceFromXML((Element)o);
				v.activatePlugin(p.getClass(), p);
			} catch(Exception e) {
				GmmlVision.log.error("Unable to load VisualizationPlugin", e);
			}
		}
		
		return v;
	}
}
