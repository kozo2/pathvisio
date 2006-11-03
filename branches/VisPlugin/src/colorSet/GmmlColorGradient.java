package colorSet;

import gmmlVision.GmmlVision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jdom.Element;

import util.ColorConverter;

/**
 * This class represent a color gradient used for data visualization
 */
public class GmmlColorGradient extends GmmlColorSetObject {
	public static final String XML_ELEMENT_NAME = "ColorGradient";

	private ArrayList<ColorValuePair> colorValuePairs;
	
	/**
	 * Constructor for this class
	 * @param parent 		colorset this gradient belongs to
	 * @param name 			name of the gradient
	 */
	public GmmlColorGradient(GmmlColorSet parent, String name)
	{
		super(parent, name);
		getColorValuePairs();
	}
		
	public GmmlColorGradient(GmmlColorSet parent, Element xml) {
		super(parent, xml);
	}
	
	/**
	 * Get the the colors and corresponding values used in this gradient as {@link ColorValuePair}
	 * @return ArrayList containing the ColorValuePairs
	 */
	public ArrayList<ColorValuePair> getColorValuePairs() 
	{ 
		if(colorValuePairs == null) {//Not initialized yet, use defaults
			colorValuePairs = new ArrayList<ColorValuePair>();
			colorValuePairs.add(new ColorValuePair(new RGB(0,255,0), -1));
			colorValuePairs.add(new ColorValuePair(new RGB(255,255,0), 0));
			colorValuePairs.add(new ColorValuePair(new RGB(255,0,0), 1));
		}
		return colorValuePairs;
	}
	/**
	 * Add a {@link ColorValuePair} to this gradient
	 */
	public void addColorValuePair(ColorValuePair cvp)
	{
		if(colorValuePairs == null) { 
			colorValuePairs = new ArrayList<ColorValuePair>();
		}
		colorValuePairs.add(cvp);
	}
	/**
	 * Remove a {@link ColorValuePair} from this gradient
	 */
	public void removeColorValuePair(ColorValuePair cvp)
	{
		if(colorValuePairs == null || !colorValuePairs.contains(cvp)) return;
		colorValuePairs.remove(cvp);
	}
			
	/**
	 * get the color of the gradient for this value
	 * @param value
	 * @return	{@link RGB} containing the color information for the corresponding value
	 * or null if the value does not have a valid color for this gradient
	 */
	public RGB getColor(double value)
	{
		double[] minmax = getMinMax(); //Get the minimum and maximum values of the gradient
		double valueStart = 0;
		double valueEnd = 0;
		RGB colorStart = null;
		RGB colorEnd = null;
		Collections.sort(colorValuePairs);
		//If value is larger/smaller than max/min then set the value to max/min
		//TODO: make this optional
		if(value < minmax[0]) value = minmax[0]; else if(value > minmax[1]) value = minmax[1];
		
		//Find what colors the value is in between
		for(int i = 0; i < colorValuePairs.size() - 1; i++)
		{
			ColorValuePair cvp = colorValuePairs.get(i);
			ColorValuePair cvpNext = colorValuePairs.get(i + 1);
			if(value >= cvp.value && value <= cvpNext.value)
			{
				valueStart = cvp.value;
				colorStart = cvp.color;
				valueEnd = cvpNext.value;
				colorEnd = cvpNext.color;
				break;
			}
		}
		if(colorStart == null || colorEnd == null) return null; //Check if the values/colors are found
		// Interpolate to find the color belonging to the given value
		double alpha = (value - valueStart) / (valueEnd - valueStart);
		double red = colorStart.red + alpha*(colorEnd.red - colorStart.red);
		double green = colorStart.green + alpha*(colorEnd.green - colorStart.green);
		double blue = colorStart.blue + alpha*(colorEnd.blue - colorStart.blue);
		RGB rgb = null;
		
		//Try to create an RGB, if the color values are not valid (outside 0 to 255)
		//This method returns null
		try {
			rgb = new RGB((int)red, (int)green, (int)blue);
		} catch (Exception e) { 
			GmmlVision.log.error("GmmlColorGradient:getColor: " + 
					red + "," + green + "," +blue + ", for value " + value, e);
		}
		return rgb;
	}
	
	public RGB getColor(HashMap<Integer, Object> data, int idSample)
	{
		try {
			double value = (Double)data.get(idSample); //Try to get the data
			return getColor(value);
		} catch(NullPointerException ne) { //No data available
			GmmlVision.log.error("GmmlColorGradient:getColor: No data to calculate color", ne);
		} catch(ClassCastException ce) { //Data not of type double
		} catch(Exception e) { //Any other exception
			GmmlVision.log.error("GmmlColorGradient:getColor", e);
		}
		return null; //If anything goes wrong, return null
	}
	
	String getXmlElementName() {
		return XML_ELEMENT_NAME;
	}
	
	public Element toXML() {
		Element elm = super.toXML();
		for(ColorValuePair cvp : colorValuePairs)
			elm.addContent(cvp.toXML());
		return elm;
	}
	
	protected void loadXML(Element xml) {
		super.loadXML(xml);
		colorValuePairs = new ArrayList<ColorValuePair>();
		for(Object o : xml.getChildren(ColorValuePair.XML_ELEMENT))
			colorValuePairs.add(new ColorValuePair((Element) o));
	}
	
	/**
	 * Find the minimum and maximum values used in this gradient
	 * @return a double[] of length 2 with respecively the minimum and maximum values
	 */
	public double[] getMinMax()
	{
		double[] minmax = new double[] { Double.MAX_VALUE, Double.MIN_VALUE };
		for(ColorValuePair cvp : colorValuePairs)
		{
			minmax[0] = Math.min(cvp.value, minmax[0]);
			minmax[1] = Math.max(cvp.value, minmax[1]);
		}
		return minmax;
	}
	
	/**
	 * This class contains a color and its corresponding value used for the {@link GmmlColorGradient}
	 */
	public class ColorValuePair implements Comparable<ColorValuePair> {
		static final String XML_ELEMENT = "color-value";
		static final String XML_ATTR_VALUE = "value";
		static final String XML_ELM_COLOR = "color";
		public RGB color;
		public double value;
		public ColorValuePair(RGB color, double value)
		{
			this.color = color;
			this.value = value;
		}
		
		public ColorValuePair(Element xml) {
			Object o = xml.getChildren(XML_ELM_COLOR).get(0);
			color = ColorConverter.parseColorElement((Element)o);
			value = Double.parseDouble(XML_ATTR_VALUE);
		}
		
		public int compareTo(ColorValuePair o)
		{
			return (int)(value - o.value);
		}
		
		public Element toXML() {
			Element elm = new Element(XML_ELEMENT);
			elm.setAttribute(XML_ATTR_VALUE, Double.toString(value));
			elm.addContent(ColorConverter.createColorElement(XML_ELM_COLOR, color));
			return elm;
		}
	}
	
	public static class ColorGradientComposite extends ConfigComposite {		
		public ColorGradientComposite(Composite parent, int style) {
			super(parent, style);
			createContents();
		}
		
		GmmlColorGradient getInput() {
			return (GmmlColorGradient)input;
		}
		
		void refresh() {
			super.refresh();
		}
				
		void createContents() {
			setLayout(new GridLayout());
			createNameComposite(this);
		}
	}
}
