// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2007 BiGCaT Bioinformatics
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
package org.pathvisio.visualization.plugins;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.jdom.Element;
import org.pathvisio.Engine;
import org.pathvisio.data.CachedData;
import org.pathvisio.data.GexManager;
import org.pathvisio.data.Sample;
import org.pathvisio.data.CachedData.Data;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.Xref;
import org.pathvisio.util.ColorConverter;
import org.pathvisio.view.GeneProduct;
import org.pathvisio.view.Graphics;
import org.pathvisio.visualization.Visualization;
import org.pathvisio.visualization.VisualizationMethod;
import org.pathvisio.visualization.colorset.ColorSet;

public class ColorByExpression extends VisualizationMethod {
	static final Color DEFAULT_TRANSPARENT = Engine.TRANSPARENT_COLOR;
	static final Color LINE_COLOR_DEFAULT = Color.BLACK;
	
	private List<ConfiguredSample> useSamples = new ArrayList<ConfiguredSample>();
	List<URL> imageURLs;

	private List<URL> defaultURLs() {
		return new ArrayList<URL>(Arrays.asList(new URL[] {
				Engine.getCurrent().getResourceURL("protein_hi.bmp"),
				Engine.getCurrent().getResourceURL("mRNA_hi.bmp") }));
	}
	
	public ColorByExpression(Visualization v, String registeredName) {
		super(v, registeredName);
		setIsConfigurable(true);
		setUseProvidedArea(true);
	}
	
	public Component visualizeOnToolTip(Graphics g) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Check whether advanced settings are used
	 */
	public boolean isAdvanced() {
		//Advanced when different colorsets or an image is specified
		//TODO: check for image
		return getSingleColorSet() == null;
	}
	
	/**
	 * Set a single colorset for all samples.
	 */
	public void setSingleColorSet(ColorSet cs) {
		for(ConfiguredSample s : useSamples) {
			s.setColorSet(cs);
		}
	}
	
	/**
	 * Get the single colorset that is used for all
	 * samples. Returns null when different colorsets are
	 * used.
	 */
	public ColorSet getSingleColorSet() {
		ColorSet cs = null;
		for(ConfiguredSample s : useSamples) {
			if(cs == null) {
				cs = s.getColorSet();
			} else {
				if(cs != s.getColorSet()) {
					return null;
				}
			}
		}
		return cs;
	}
	
	/**
	 * Get the configured sample for the given sample. Returns
	 * null when no configured sample is found.
	 */
	protected ConfiguredSample getConfiguredSample(Sample s) {
		for(ConfiguredSample cs : useSamples) {
			if(cs.getSample() != null && cs.getSample() == s) {
				return cs;
			}
		}
		return null;
	}
	
	public String getDescription() {
		return "Color DataNodes by their expression value";
	}

	public String getName() {
		return "Expression as color";
	}
	
	public JPanel getConfigurationPanel() {
		return new ColorByExpressionPanel(this);
	}
	
	public List<Sample> getSelectedSamples() {
		List<Sample> samples = new ArrayList<Sample>();
		
		for(ConfiguredSample cs : useSamples) {
			samples.add(cs.getSample());
		}
		return samples;
	}
	
	List<URL> getImageURLs() { 
		if(imageURLs == null) imageURLs = defaultURLs();
		return imageURLs;
	}
	
	void addImageURL(URL url) {
		if(!imageURLs.contains(url))imageURLs.add(url);
	}
	
	void removeImageURL(URL url) {
		if(url.getProtocol().equals("file")) imageURLs.remove(url);
	}
	
	void drawImage(ConfiguredSample is, Color rgb, Rectangle area, Graphics2D g2d) {
		Image img = is.getImage(rgb);
		if(img != null) {
			drawBackground(area, g2d);
			
			Dimension scaleTo = is.getScaleSize(new Dimension(area.width, area.height));
			Image simg = img.getScaledInstance(scaleTo.width, scaleTo.height, Image.SCALE_SMOOTH);
									
			int xs = area.width - scaleTo.width;
			int ys = area.height - scaleTo.height;
			g2d.drawImage(simg, area.x + xs / 2, area.y + ys / 2, null);
		}
	}
		
	void drawBackground(Rectangle area, Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.fill(area);
	}

	public void visualizeOnDrawing(Graphics g, Graphics2D g2d) {
		if(!(g instanceof GeneProduct)) return;
		if(useSamples.size() == 0) return; //Nothing to draw
		
		GeneProduct gp = (GeneProduct) g;
		
		Shape da = getVisualization().provideDrawArea(this, g);
		Rectangle area = da.getBounds();
		
		drawArea(gp, area, g2d);
		
//		Color c = gp.getPathwayElement().getColor();
//		g2d.setColor(c);
//		g2d.fill(area);
	
	}
	
	void drawArea(GeneProduct gp, Rectangle area, Graphics2D g2d) {
		int nr = useSamples.size();
		int left = area.width % nr; //Space left after dividing, give to last rectangle
		int w = area.width / nr;
		for(int i = 0; i < nr; i++) {
			Rectangle r = new Rectangle(
					area.x + w * i,
					area.y,
					w + ((i == nr - 1) ? left : 0), area.height);
			ConfiguredSample s = (ConfiguredSample)useSamples.get(i);
			Xref idc = new Xref(gp.getPathwayElement().getGeneID(), gp.getPathwayElement().getDataSource());
			CachedData cache = GexManager.getCurrent().getCurrentGex().getCachedData();
			if(cache == null) continue;
			
			if(s.getColorSet() == null) {
				Logger.log.trace("No colorset for sample " + s);
				continue; //No ColorSet for this sample
			}
			if(cache.hasData(idc)) 
				drawSample(s, idc, r, g2d);
			else 
				drawNoDataFound(s, area, g2d);
		}
	}
	
	void drawNoDataFound(ConfiguredSample s, Rectangle area, Graphics2D g2d) {
		ColorSet cs = s.getColorSet();
		drawColoredRectangle(area, cs.getColor(ColorSet.ID_COLOR_NO_DATA_FOUND), g2d);
	}
	
	void drawColoredRectangle(Rectangle r, Color c, Graphics2D g2d) {			
		g2d.setColor(c);
		g2d.fill(r);
		
		if(drawLine) {
			g2d.setColor(getLineColor());
			g2d.draw(r);
		}
	}
	
	Color lineColor;
	boolean drawLine = false;
	
	void drawSampleAvg(ConfiguredSample s, Xref idc, CachedData cache, Rectangle area, Graphics2D g2d) {
		ColorSet cs = s.getColorSet();
		Color rgb = cs.getColor(cache.getAverageSampleData(idc), s.getId());
		drawColoredRectangle(area, rgb, g2d);
	}
	
	void drawSampleBar(ConfiguredSample s, Xref idc, CachedData cache, Rectangle area, Graphics2D g2d) {
		ColorSet cs = s.getColorSet();
		List<Data> refdata = cache.getData(idc);
		int n = refdata.size();
		int left = area.height % n;
		int h = area.height / n;
		for(int i = 0; i < n; i++) {
			Color rgb = cs.getColor(refdata.get(i).getSampleData(), s.getId());
			Rectangle r = new Rectangle(
					area.x, area.y + i*h,
					area.width, h + (i == n-1 ? left : 0));
			drawColoredRectangle(r, rgb, g2d);
		}
	}
	
	void setLineColor(Color rgb) {
		if(rgb != null)	{
			lineColor = rgb;
			modified();
		}
	}
	
	Color getLineColor() { return lineColor == null ? LINE_COLOR_DEFAULT : lineColor; }
	
	void setDrawLine(boolean draw) {
		drawLine = draw;
		modified();
	}

	void drawSample(ConfiguredSample s, Xref idc, Rectangle area, Graphics2D g2d) {
		CachedData cache = GexManager.getCurrent().getCurrentGex().getCachedData();
		ColorSet cs = s.getColorSet();
		
		if(s.hasImage()) {
			Color rgb = cs.getColor(cache.getAverageSampleData(idc), s.getId());
			drawImage(s, rgb, area, g2d);
		} else {
			if(cache.hasMultipleData(idc)) {
				switch(s.getAmbigiousType()) {
				case ConfiguredSample.AMBIGIOUS_AVG:
					drawSampleAvg(s, idc, cache, area, g2d);
					break;
				case ConfiguredSample.AMBIGIOUS_BARS:
					drawSampleBar(s, idc, cache, area, g2d);
					break;
				}
			} else {
				HashMap<Integer, Object> data = cache.getSingleData(idc).getSampleData();
				Color rgb = cs.getColor(data, s.getId());
				drawColoredRectangle(area, rgb, g2d);
			}
		}
	}
	
	void setUseSamples(List<ConfiguredSample> samples) {
		useSamples = samples;
	}
	
	/**
	 * Add a sample to use for visualization
	 * @param s The sample to add
	 */
	void addUseSample(Sample s) {
		if(s != null) {
			if(!useSamples.contains(s)) useSamples.add(new ConfiguredSample(s));
			modified();
		}
	}
	
	/**
	 * Remove a sample from the samples that will be used for visualization
	 * @param s
	 */
	void removeUseSample(ConfiguredSample s) {
		if(s != null) {
			useSamples.remove(s);
			modified();
		}
	}
	
	public final Element toXML() {
		Element xml = super.toXML();
		saveAttributes(xml);
		for(ConfiguredSample s : useSamples) xml.addContent(s.toXML());
		return xml;
	}
	
	final static String XML_ELM_URL = "image";
	static final String XML_ATTR_DRAWLINE = "drawLine";
	static final String XML_ELM_LINECOLOR = "lineColor";
	
	/**
	 * Implement this method to save attributes to the XML element 
	 * that contain additional configuration of this plug-ins
	 * @param xml The XML element to save the attributes to
	 */
	void loadAttributes(Element xml) {
		for(Object o : xml.getChildren(XML_ELM_URL)) {
			try {
				URL url = new URL(((Element)o).getText());
				addImageURL(url);
			} catch(Exception e) {
				Logger.log.error("couldn't load image URL for plugin", e);
			}
		}
		try {
			lineColor = ColorConverter.parseColorElement(xml.getChild(XML_ELM_LINECOLOR));
			drawLine = Boolean.parseBoolean(xml.getAttributeValue(XML_ATTR_DRAWLINE));
		} catch(Exception e) {
			Logger.log.error("Unable to parse settings for plugin", e);
		}
	}
	
	/**
	 * Implement this method to save attributes to the XML element 
	 * that contain additional configuration of this plug-ins
	 * @param xml The XML element to save the attributes to
	 */
	void saveAttributes(Element xml) {
		for(URL url : getImageURLs()) {
			if(defaultURLs().contains(url)) continue; //Skip default urls
			Element elm = new Element(XML_ELM_URL);
			elm.setText(url.toString());
			xml.addContent(elm);
		}
		xml.setAttribute(XML_ATTR_DRAWLINE, Boolean.toString(drawLine));
		xml.addContent(ColorConverter.createColorElement(XML_ELM_LINECOLOR, getLineColor()));
	}
	
	public final void loadXML(Element xml) {
		super.loadXML(xml);
		loadAttributes(xml);
		for(Object o : xml.getChildren(ConfiguredSample.XML_ELEMENT)) {
			try {
				useSamples.add(new ConfiguredSample((Element)o));
			} catch(Exception e) {
				Logger.log.error("Unable to load plugin settings", e);
			}
		}	
	}
	
	/**
	 * This class stores the configuration for a sample that is selected for
	 * visualization. In this implementation, a color-set to use for visualization is stored.
	 * Extend this class to store additional configuration data.
	 * @author Thomas
	 *
	 */
	class ConfiguredSample {		
		public static final int AMBIGIOUS_AVG = 0;
		public static final int AMBIGIOUS_BARS = 1;

		ColorSet colorSet = null;
		int ambigious = AMBIGIOUS_BARS;
		
		BufferedImage cacheImage;
		URL imageURL;
		Color replaceColor = DEFAULT_TRANSPARENT;
		int tolerance; //range 0 - 255;

		Sample sample;

		int getAmbigiousType() { return ambigious; }
		
		void setAmbigiousType(int type) { 
			ambigious = type;
			modified();
		}
		
		public Sample getSample() {
			return sample;
		}
		
		public int getId() {
			return sample.getId();
		}
		
		final static String XML_ATTR_ASPECT = "maintain-aspect-ratio";
		final static String XML_ATTR_TOLERANCE = "tolerance";
		final static String XML_ATTR_IMAGE = "image-url";
		final static String XML_ATTR_REPLACE = "replace-color";
		final static String XML_ATTR_AMBIGIOUS = "ambigious";
				
		protected void saveAttributes(Element xml) {
			xml.setAttribute(XML_ATTR_AMBIGIOUS, Integer.toString(ambigious));xml.setAttribute(XML_ATTR_ASPECT, Boolean.toString(getMaintainAspect()));
			if(imageURL != null) {
				xml.setAttribute(XML_ATTR_ASPECT, "" + getMaintainAspect());
				xml.setAttribute(XML_ATTR_TOLERANCE, Integer.toString(getTolerance()));
				xml.setAttribute(XML_ATTR_IMAGE, imageURL.toString());
				xml.addContent(ColorConverter.createColorElement(XML_ATTR_REPLACE, getReplaceColor()));
			}
		}
		
		protected void loadAttributes(Element xml) {
			int amb = Integer.parseInt(xml.getAttributeValue(XML_ATTR_AMBIGIOUS));
			setAmbigiousType(amb);
			try {
				if(xml.getAttributeValue(XML_ATTR_IMAGE) != null) {
					setMaintainAspect(Boolean.parseBoolean(xml.getAttributeValue(XML_ATTR_ASPECT)));
					setTolerance(Integer.parseInt(xml.getAttributeValue(XML_ATTR_TOLERANCE)));
					setURL(new URL(xml.getAttributeValue(XML_ATTR_IMAGE)));
					setReplaceColor(ColorConverter.parseColorElement(xml.getChild(XML_ATTR_REPLACE)));
				}
			} catch(Exception e) {
				Logger.log.error("Unable to load plugin", e);
			}
		}
		
		static final String XML_ELEMENT = "sample";
		static final String XML_ATTR_ID = "id";
		static final String XML_ATTR_COLORSET = "colorset";
		
		private final Element toXML() {
			Element xml = new Element(XML_ELEMENT);
			xml.setAttribute(XML_ATTR_ID, Integer.toString(sample.getId()));
			xml.setAttribute(XML_ATTR_COLORSET, colorSet.getName());
			saveAttributes(xml);
			return xml;
		}
		
		private final void loadXML(Element xml) throws Exception {
			int id = Integer.parseInt(xml.getAttributeValue(XML_ATTR_ID));
			String csn = xml.getAttributeValue(XML_ATTR_COLORSET);
			sample = GexManager.getCurrent().getCurrentGex().getSamples().get(id);
			setColorSet(getVisualization().getManager().getColorSetManager().getColorSet(csn));
			loadAttributes(xml);
		}
		
		/**
		 * Create a configured sample based on an existing sample
		 * @param s The sample to base the configured sample on
		 */
		public ConfiguredSample(Sample s) {
			sample = s;
		}
		
		/**
		 * Create a configured sample from the information in the given XML element
		 * @param xml The XML element containing information to create the configured sample from
		 * @throws Exception
		 */
		public ConfiguredSample(Element xml) throws Exception {
			loadXML(xml);
		}
		
		/**
		 * Set the color-set to use for visualization of this sample
		 */
		protected void setColorSet(ColorSet cs) { 
			colorSet = cs;
			modified();
		}
		
		/**
		 * Get the color-set to use for visualization of this sample
		 * @return the color-set
		 */
		protected ColorSet getColorSet() {
			return colorSet;
		}
		
		/**
		 * Get the name of the color-sets that is selected for visualization
		 * @return The name of the selected color-set, or "no colorsets available", if no
		 * color-sets exist
		 */
		protected String getColorSetName() {
			ColorSet cs = getColorSet();
			return cs == null ? "no colorsets available" : cs.getName();
		}	

		boolean aspectRatio = true;
		
		public void setURL(URL url) { 
			imageURL = url;
			cacheImage = null;
			modified();
		}
		
		public URL getURL() { 
			return imageURL; 
		}
		
		public boolean hasImage() {
			return imageURL != null;
		}
		
		public void setReplaceColor(Color rgb) { 
			if(rgb != null) replaceColor = rgb;
			modified();
		}
		public Color getReplaceColor() { return replaceColor; }
		public void setMaintainAspect(boolean maintain) { 
			aspectRatio = maintain;
			modified();
		}
		public boolean getMaintainAspect() { return aspectRatio;}
		public void setTolerance(int tol) { 
			modified();
			tolerance = tol; 
		}
		public int getTolerance() { return tolerance; }
		
		public BufferedImage getImage() {
			if(imageURL == null) return null;
			if(cacheImage == null) {
				try {
					cacheImage = ImageIO.read(imageURL);
				} catch(IOException e) {
					Logger.log.error("Unable to load image", e);
					return null;
				}
			}
			return cacheImage.getSubimage(0, 0, cacheImage.getWidth(), cacheImage.getHeight());
		}
		
		public Image getImage(Dimension size) {
			return getImage(size, null);
		}
		
		public Image getImage(Color replaceWith) {
			Image img = getImage();
			if(img == null) return null;
			if(replaceWith != null) img = doReplaceColor(img, replaceColor, replaceWith, tolerance);
			return img;
		}
		
		public Image getImage(Dimension size, Color replaceWith) {
			Image img = getImage();
			if(img == null) return null;
			
			img = getImage(replaceWith);
			
			size = getScaleSize(size);
			
			img = img.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
			return img;
		}
		
		public Dimension getScaleSize(Dimension target) {
			if(aspectRatio) {
				BufferedImage img = getImage();
				double r = (double)img.getHeight() / img.getWidth();
				int min = (int)Math.min(target.getWidth(), target.getHeight());
				if(min == target.getWidth()) target.height = (int)(min * r);
				else target.width = (int)(min * r);
			}
			return target;
		}
		
		Image doReplaceColor(Image img, final Color oldColor, final Color newColor, final int tol) {
			RGBImageFilter f = new RGBImageFilter() {
				public int filterRGB(int x, int y, int rgb) {
					Color thisColor = new Color(rgb);
					if(compareColor(oldColor, thisColor, tol)) {
						return newColor.getRGB();
					}
					return rgb;
				}
			};
			ImageProducer pr = new FilteredImageSource(img.getSource(), f);
			return Toolkit.getDefaultToolkit().createImage(pr);
		}
		
		boolean compareColor(Color rgb1, Color rgb2, int tolerance) {
			return 	rgb2.getRed() >= rgb1.getRed() - tolerance &&
					rgb2.getRed() <= rgb1.getRed() + tolerance &&
					rgb2.getGreen() >= rgb1.getGreen() - tolerance &&
					rgb2.getGreen() <= rgb1.getGreen() + tolerance &&
					rgb2.getBlue() >= rgb1.getBlue() - tolerance &&
					rgb2.getBlue() <= rgb1.getBlue() + tolerance;
		}
		
		InputStream getInputStream(URL url) {
			try {
				URLConnection con = url.openConnection();
				return con.getInputStream();
			} catch(IOException e) {
				Logger.log.error("Unable to open connection to image", e);
			}
			return null;
		}
	}
}
