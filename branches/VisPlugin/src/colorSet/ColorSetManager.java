package colorSet;

import gmmlVision.GmmlVision;
import gmmlVision.GmmlVisionWindow;
import graphics.GmmlDrawing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import visualization.Visualization;

public class ColorSetManager {
	final static String XML_ELEMENT = "ColorSets";
	
	private static List<GmmlColorSet> colorSets = new ArrayList<GmmlColorSet>();
	
	private static int colorSetIndex = -1;
	
	/**
	 * Gets the {@link ColorSet}s used for the currently loaded Expression data
	 */
	public static List<GmmlColorSet> getColorSets() { return colorSets; }
	
	/**
	 * Set the index of the colorset to use
	 * @param colorSetIndex
	 */
	public static void setColorSetIndex(int _colorSetIndex)
	{
		GmmlVisionWindow window = GmmlVision.getWindow();
		colorSetIndex = _colorSetIndex;
//		if(colorSetIndex < 0)
//		{
//			window.showLegend(false);
//		} else {
//			window.showLegend(true);
//		}
		GmmlDrawing d = GmmlVision.getDrawing();
		if(d != null) { d.redraw(); }
	}
	
	public static void setColorSet(GmmlColorSet cs) {
		int ci = getColorSets().indexOf(cs);
		if(ci > -1) setColorSetIndex(ci);
	}
	
	/**
	 * Get the index of the currently used colorset
	 * @return
	 */
	public static int getColorSetIndex() { 
		return colorSetIndex;
	}
	
	/**
	 * Sets the {@link ColorSet}s used for the currently loaded Expression data
	 * @param colorSets {@link Vector} containing the {@link ColorSet} objects
	 */
	public static void setColorSets(Vector<GmmlColorSet> _colorSets)
	{
		colorSets = _colorSets;
	}
	
	public static boolean nameExists(String name) {
		for(GmmlColorSet cs : colorSets) 
			if(cs.getName().equalsIgnoreCase(name)) return true;
		return false;
	}
	
	public static String getNewName() {
		String prefix = "color set";
		int i = 1;
		String name = prefix;
		while(nameExists(name)) name = prefix + "-" + i++;
		return name;
	}
	
	public static void newColorSet(String name) {
		if(name == null) name = getNewName();
		colorSets.add(new GmmlColorSet(name));
		setColorSetIndex(colorSets.size() - 1);
	}
	
	/**
	 * Removes this {@link ColorSet}
	 * @param cs Colorset to remove
	 */
	public static void removeColorSet(GmmlColorSet cs) {
		if(colorSets.contains(cs)) {
			colorSets.remove(cs);
			if(colorSetIndex == 0 && colorSets.size() > 0) setColorSetIndex(colorSetIndex);
			else setColorSetIndex(colorSetIndex - 1);
		}
	}
	
	/**
	 * Removes this {@link ColorSet}
	 * @param i index of ColorSet to remove
	 */
	public static void removeColorSet(int i) {
		if(i > -1 && i < colorSets.size()) {
			removeColorSet(colorSets.get(i));
		}
	}
	
	/**
	 * Gets the names of all {@link GmmlColorSet}s used 
	 * @return
	 */
	public static String[] getColorSetNames()
	{
		String[] colorSetNames = new String[colorSets.size()];
		for(int i = 0; i < colorSetNames.length; i++)
		{
			colorSetNames[i] = ((GmmlColorSet)colorSets.get(i)).name;
		}
		return colorSetNames;
	}
		
	public static void save(OutputStream out) {
		Document doc = new Document();
		Element root = new Element(XML_ELEMENT);
		for(GmmlColorSet cs : colorSets) root.addContent(cs.toXML());
		doc.setRootElement(root);
		
		XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
		try {
			xmlout.output(doc, out);
		} catch(IOException e) {
			GmmlVision.log.error("Unable to save colorsets", e);
		}
	}
	
	public static void load(InputStream in) {
		colorSets.clear();
		if(in == null) return;
		
		SAXBuilder parser = new SAXBuilder();
		try {
			Document doc = parser.build(in);
			Element root = doc.getRootElement();
			for(Object o : root.getChildren(GmmlColorSet.XML_ELEMENT)) {
				colorSets.add(GmmlColorSet.fromXML((Element) o));				
			}
		} catch(Exception e) {
			GmmlVision.log.error("Unable to load colorsets", e);
		}
	}
}
