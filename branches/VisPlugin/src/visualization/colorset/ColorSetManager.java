package visualization.colorset;

import gmmlVision.GmmlVision;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import visualization.VisualizationManager;
import visualization.VisualizationManager.VisualizationEvent;
import data.GmmlGex;
import data.GmmlGex.ExpressionDataEvent;
import data.GmmlGex.ExpressionDataListener;

public class ColorSetManager implements ExpressionDataListener {
	final static String XML_ELEMENT = "ColorSets";

	private static List<ColorSet> colorSets = new ArrayList<ColorSet>();

	/**
	 * Gets the {@link ColorSet}s used for the currently loaded Expression data
	 */
	public static List<ColorSet> getColorSets() { return colorSets; }

	public static boolean nameExists(String name) {
		for(ColorSet cs : colorSets) 
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
		addColorSet(new ColorSet(name));
		
	}
	
	public static void addColorSet(ColorSet cs) {
		colorSets.add(cs);
		VisualizationManager.firePropertyChange(
				new VisualizationEvent(null, VisualizationEvent.COLORSET_ADDED));
	}

	/**
	 * Removes this {@link ColorSet}
	 * @param cs Colorset to remove
	 */
	public static void removeColorSet(ColorSet cs) {
		if(colorSets.contains(cs)) {
			colorSets.remove(cs);
			VisualizationManager.firePropertyChange(
					new VisualizationEvent(null, VisualizationEvent.COLORSET_REMOVED));
		}
	}
	
	public static ColorSet getColorSet(int index) {
		if(index >= 0 && index < colorSets.size())
			return colorSets.get(index);
		else return null;
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
	 * Gets the names of all {@link ColorSet}s used 
	 * @return
	 */
	public static String[] getColorSetNames()
	{
		String[] colorSetNames = new String[colorSets.size()];
		for(int i = 0; i < colorSetNames.length; i++)
		{
			colorSetNames[i] = ((ColorSet)colorSets.get(i)).getName();
		}
		return colorSetNames;
	}

	public static void save(OutputStream out) {
		Document doc = new Document();
		Element root = new Element(XML_ELEMENT);
		for(ColorSet cs : colorSets) root.addContent(cs.toXML());
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
			for(Object o : root.getChildren(ColorSet.XML_ELEMENT)) {
				addColorSet(ColorSet.fromXML((Element) o));				
			}
		} catch(Exception e) {
			GmmlVision.log.error("Unable to load colorsets", e);
		}
	}

	public void expressionDataEvent(ExpressionDataEvent e) {
		switch(e.type) {
		case ExpressionDataEvent.CONNECTION_OPENED:
			System.out.println("Connection opened, loading colorset");
			load(GmmlGex.getColorSetInput());
			break;
		case ExpressionDataEvent.CONNECTION_CLOSED:
			System.out.println("Connection closed, clearing colorset");
			colorSets.clear();
			break;
		}
	}
}
