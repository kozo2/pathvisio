// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package org.pathvisio.visualization.colorset;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.pathvisio.debug.Logger;
import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.util.ColorConverter;
import org.pathvisio.visualization.colorset.Criterion.CriterionException;

/**
 * This class represents a colorset, a set of criteria that can be evaluated and 
 * results in a color given a collection of data
 */
public class ColorSet 
{	
	/** constant to access the special fallback color, 
	 * for when none of the criteria are met. */
	public static final int ID_COLOR_NO_CRITERIA_MET = 1;
	/**
	 * constant to access the special fallback color,
	 * for when this gene could not be found in the gene database
	 */
	public static final int ID_COLOR_NO_GENE_FOUND = 2;
	/**
	 * constant to access the special fallback color,
	 * for when the gene could be found in the gene database,
	 * but no data is available for this gene in the dataset.
	 */
	public static final int ID_COLOR_NO_DATA_FOUND = 3;
	
	Color colorNoCriteriaMet = PreferenceManager.getCurrent().getColor(GlobalPreference.COLOR_NO_CRIT_MET);
	Color colorNoGeneFound = PreferenceManager.getCurrent().getColor(GlobalPreference.COLOR_NO_GENE_FOUND);
	Color colorNoDataFound = PreferenceManager.getCurrent().getColor(GlobalPreference.COLOR_NO_DATA_FOUND);
		
	/**
	 * A user can give each colorset a name
	 */
	String name;
	ColorSetManager colorSetMgr;
	
	public List<ColorSetObject> colorSetObjects = new ArrayList<ColorSetObject>();
		
	public ColorSetManager getColorSetManager() { return colorSetMgr; }
	
	/**
	 * Constructor of this class
	 * @param name		name of the colorset
	 */
	public ColorSet(String name) {
		this(name, null);
	}
	
	/**
	 * Create a color set with a unique name and the given colorset manager
	 */
	public ColorSet(ColorSetManager colorSetMgr) {
		this(colorSetMgr.getNewName(), colorSetMgr);
	}
	
	private ColorSet(String name, ColorSetManager colorSetMgr)
	{
		this.name = name;
		this.colorSetMgr = colorSetMgr;
	}
		
	public String getName() { return name; }
	
	public void setName(String n) { 
		name = n;
		fireModifiedEvent();
	}
	
	public void setColor(int id, Color rgb) {
		switch(id) {
		case ID_COLOR_NO_CRITERIA_MET:
			colorNoCriteriaMet = rgb;
			break;
		case ID_COLOR_NO_DATA_FOUND:
			colorNoDataFound = rgb;
			break;
		case ID_COLOR_NO_GENE_FOUND:
			colorNoGeneFound = rgb;
			break;
		}
		fireModifiedEvent();
	}
	
	/**
	 * Get one of the special case colors.
	 * @param id one of the ColorSet.ID_XXX constants
	 * @return
	 */
	public Color getColor(int id) {
		switch(id) {
		case ID_COLOR_NO_CRITERIA_MET:
			return colorNoCriteriaMet;
		case ID_COLOR_NO_DATA_FOUND:
			return colorNoDataFound;
		case ID_COLOR_NO_GENE_FOUND:
			return colorNoGeneFound;
		default: return null;
		}
	}
	
	/**
	 * Adds a new {@link ColorSetObject} to this colorset
	 * @param o the {@link ColorSetObject} to add
	 */
	public void addObject(ColorSetObject o)
	{
		if(o == null) throw new IllegalArgumentException("tying to add null");
		colorSetObjects.add(o);
		fireModifiedEvent();
	}
	
	/**
	 * Remove a ColorSetObject from this ColorSet.
	 * @param o
	 */
	public void removeObject(ColorSetObject o) 
	{
		colorSetObjects.remove(o);
		fireModifiedEvent();
	}
	
	/**
	 * Obtain all ColorSetObjects (Rules / Gradients) in this ColorSet.
	 */
	public List<ColorSetObject> getObjects() 
	{
		return colorSetObjects;
	}
	
	/**
	 * Get the color for the given expression data by evaluating all colorset objects
	 * @param data		the expression data to get the color for
	 * @param sampleId	the id of the sample that will be visualized
	 * @return	an {@link RGB} object representing the color for the given data
	 */
	public Color getColor(Map<Integer, Object> data, int sampleId)
	{
		if(data == null) return colorNoDataFound;
		Object value = data.get(sampleId);
		if(value == null || value.equals(Double.NaN)) return colorNoDataFound;
		
		Color rgb = colorNoCriteriaMet; //The color to return
		Iterator<ColorSetObject> it = colorSetObjects.iterator();
		//Evaluate all ColorSet objects, return when a valid color is found
		while(it.hasNext())
		{
			ColorSetObject gc = it.next();
			try{ 
				Color gcRgb = gc.getColor(data, sampleId);
				if(gcRgb != null) {
					return gcRgb;
				}
			} catch(CriterionException e) {
				Logger.log.error("ColorSetObject " + gc + " could not evaluate data: " + e.getMessage());
			}
		}
		return rgb;
	}
	
	public void paintPreview(Graphics2D g, Rectangle bounds) {
		double gSpace = colorSetObjects.size() > 1 ? 0.8 : 1; //80% to gradient
		
		ColorGradient gradient = getGradient();
		if(gradient != null) {
			Rectangle gBounds = new Rectangle(
					bounds.x, bounds.y, (int)(bounds.width * gSpace), bounds.height
			);
			gradient.paintPreview((Graphics2D)g.create(), gBounds, true);
			bounds = new Rectangle(
					bounds.x + gBounds.width, bounds.y, bounds.width - gBounds.width, bounds.height
			);
		}
		int x = bounds.x;
		int nr = gradient == null ? colorSetObjects.size() : colorSetObjects.size() - 1;
		if(nr > 0) {
			int w = bounds.width / nr;
			for(ColorSetObject cso : colorSetObjects) {
				if(cso != gradient) {
					cso.paintPreview((Graphics2D)g.create(), new Rectangle(x, bounds.y, w, bounds.height));
					x = x + w;
				}
			}
		} else {
			g.setColor(new Color(255, 255, 255, 128));
			g.fill(bounds);
		}
	}
	
	/**
	 * Get the gradient of this colorset. If the colorset contains
	 * multiple gradients, the first is returned.
	 */
	public ColorGradient getGradient() {
		for(ColorSetObject cso : colorSetObjects) {
			if(cso instanceof ColorGradient) {
				return (ColorGradient)cso;
			}
		}
		return null;
	}
	
	/**
	 * Set the gradient for this colorset. All existing gradients will
	 * be replaced. If the argument is null, all gradients will be removed.
	 */
	public void setGradient(ColorGradient gradient) {
		List<ColorGradient> rm = new ArrayList<ColorGradient>();
		for(ColorSetObject cso : colorSetObjects) {
			if(cso instanceof ColorGradient) {
				rm.add((ColorGradient)cso);
			}
		}
		Logger.log.trace("remove: " + rm);
		for(ColorGradient g : rm) colorSetObjects.remove(g);
		if(gradient != null) {
			addObject(gradient);
		}
		Logger.log.trace("" + colorSetObjects);
	}
	
	final static String XML_ELEMENT = "ColorSet";
	final static String XML_ATTR_NAME = "name";
	final static String XML_ELM_COLOR_NCM = "no-criteria-met";
	final static String XML_ELM_COLOR_NGF = "no-gene-found";
	final static String XML_ELM_COLOR_NDF = "no-data-found";
	
	public Element toXML() {
		Element elm = new Element(XML_ELEMENT);
		elm.setAttribute(XML_ATTR_NAME, name);
		
		elm.addContent(ColorConverter.createColorElement(XML_ELM_COLOR_NCM, colorNoCriteriaMet));
		elm.addContent(ColorConverter.createColorElement(XML_ELM_COLOR_NGF, colorNoGeneFound));
		elm.addContent(ColorConverter.createColorElement(XML_ELM_COLOR_NDF, colorNoDataFound));
		
		for(ColorSetObject cso : colorSetObjects)
			elm.addContent(cso.toXML());
		return elm;
	}
	
	public static ColorSet fromXML(Element e, ColorSetManager colorSetMgr) {
		ColorSet cs = new ColorSet(e.getAttributeValue(XML_ATTR_NAME), colorSetMgr);
		for(Object o : e.getChildren()) {
			Logger.log.trace("\tAdding " + o);
			try {
				Element elm = (Element) o;
				String name = elm.getName();
				if(name.equals(ColorGradient.XML_ELEMENT_NAME))
					cs.addObject(new ColorGradient(cs, elm));
				else if(name.equals(ColorRule.XML_ELEMENT_NAME))
					cs.addObject(new ColorRule(cs, elm));
				else if(name.equals(XML_ELM_COLOR_NCM))
					cs.setColor(ID_COLOR_NO_CRITERIA_MET, ColorConverter.parseColorElement(elm));
				else if(name.equals(XML_ELM_COLOR_NGF))
					cs.setColor(ID_COLOR_NO_GENE_FOUND, ColorConverter.parseColorElement(elm));
				else if(name.equals(XML_ELM_COLOR_NDF))
					cs.setColor(ID_COLOR_NO_DATA_FOUND, ColorConverter.parseColorElement(elm));
			} catch(Exception ex) {
				Logger.log.error("Unable to parse colorset xml", ex);
			}
		}
		return cs;
	}
	
	void fireModifiedEvent() {
		if(colorSetMgr != null) {
			colorSetMgr.fireColorSetEvent(
					new ColorSetEvent (this, ColorSetEvent.COLORSET_MODIFIED));
		}
	}
}
