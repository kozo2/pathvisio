package colorSet;
import gmmlVision.GmmlVision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.RGB;
import org.jdom.Element;

import preferences.GmmlPreferences;
import util.ColorConverter;


/**
 * This class represents a colorset, a set of criteria that can be evaluated and 
 * results in a color given a collection of data
 */
public class GmmlColorSet {	
	public RGB color_no_criteria_met = GmmlPreferences.getColorProperty(GmmlPreferences.PREF_COL_NO_CRIT_MET);
	public RGB color_no_gene_found = GmmlPreferences.getColorProperty(GmmlPreferences.PREF_COL_NO_GENE_FOUND);
	public RGB color_no_data_found = GmmlPreferences.getColorProperty(GmmlPreferences.PREF_COL_NO_DATA_FOUND);
		
	String name;
	
	public List<GmmlColorSetObject> colorSetObjects;
		
	/**
	 * Constructor of this class
	 * @param name		name of the colorset
	 */
	public GmmlColorSet(String name)
	{
		this.name = name;
		colorSetObjects = new ArrayList<GmmlColorSetObject>();
	}
		
	public String getName() { return name; }
	
	public void setName(String n) { name = n; }
	
	/**
	 * Adds a new {@link GmmlColorSetObject} to this colorset
	 * @param o the {@link GmmlColorSetObject} to add
	 */
	public void addObject(GmmlColorSetObject o)
	{
		colorSetObjects.add(o);
	}
	
	List<GmmlColorSetObject> getObjects() {
		return colorSetObjects;
	}
		
	/**
	 * Get the color for the given expression data by evaluating all colorset objects
	 * @param data		the expression data to get the color for
	 * @param sampleId	the id of the sample that will be visualized
	 * @return	an {@link RGB} object representing the color for the given data
	 */
	public RGB getColor(HashMap<Integer, Object> data, int sampleId)
	{
		RGB rgb = color_no_criteria_met; //The color to return
		Iterator it = colorSetObjects.iterator();
		//Evaluate all colorset objects, return when a valid color is found
		while(it.hasNext())
		{
			GmmlColorSetObject gc = (GmmlColorSetObject)it.next();
			RGB gcRgb = gc.getColor(data, sampleId);
			if(gcRgb != null)
			{
				return gcRgb;
			}
		}
		return rgb;
	}
	
	final static String XML_ELEMENT = "ColorSet";
	final static String XML_ATTR_NAME = "name";
	final static String XML_ELM_COLOR_NCM = "no criteria met";
	final static String XML_ELM_COLOR_NGF = "no gene found";
	final static String XML_ELM_COLOR_NDF = "no data found";
	
	public Element toXML() {
		Element elm = new Element(XML_ELEMENT);
		elm.setAttribute(XML_ATTR_NAME, name);
		
		elm.addContent(ColorConverter.createColorElement(XML_ELM_COLOR_NCM, color_no_criteria_met));
		elm.addContent(ColorConverter.createColorElement(XML_ELM_COLOR_NGF, color_no_gene_found));
		elm.addContent(ColorConverter.createColorElement(XML_ELM_COLOR_NDF, color_no_data_found));
		
		for(GmmlColorSetObject cso : colorSetObjects)
			elm.addContent(cso.toXML());
		return elm;
	}
	
	public static GmmlColorSet fromXML(Element e) {
		GmmlColorSet cs = new GmmlColorSet(e.getAttributeValue(XML_ATTR_NAME));
		for(Object o : e.getChildren()) {
			Element coe = (Element) o;
			String type = coe.getName();
			if(type.equals(GmmlColorGradient.XML_ELEMENT_NAME))
				cs.addObject(new GmmlColorGradient(cs, coe));
			else if(type.equals(GmmlColorCriterion.XML_ELEMENT_NAME))
				cs.addObject(new GmmlColorCriterion(cs, coe));
		}
		return cs;
	}
			
	static void printParseError(String criterion, Exception e) {
		GmmlVision.log.error("Unable to parse colorset data stored in " +
				"expression database: " + criterion, e);
		MessageDialog.openWarning(GmmlVision.getWindow().getShell(), 
					"Warning", "Unable to parse the colorset data in this expression dataset");
	}
}
