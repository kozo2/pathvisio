package org.pathvisio.visualization.plugins;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jdom.Element;
import org.pathvisio.debug.Logger;
import org.pathvisio.util.ColorConverter;
import org.pathvisio.view.GeneProduct;
import org.pathvisio.view.Graphics;
import org.pathvisio.visualization.Visualization;
import org.pathvisio.visualization.VisualizationMethod;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DataNodeLabel extends VisualizationMethod implements ActionListener {
	static final String DISPLAY_ID = "Identifier";
	static final String DISPLAY_LABEL = "Text label";
	static final String ACTION_APPEARANCE = "Appearance...";
	
	static final Font DEFAULT_FONT = new Font("Arial narrow", Font.PLAIN, 10);
			
	final static int ALIGN_CENTER = 0;
	final static int ALIGN_LEFT = 1;
	final static int ALIGN_RIGHT = 2;
	
	String display = DISPLAY_LABEL;
	
	boolean adaptFontSize;
	int align;
	
	Font font;
	Color fontColor;
	
	public DataNodeLabel(Visualization v) {
		super(v);
	    setIsConfigurable(true);
		setUseProvidedArea(true);
	}

	public String getDescription() {
		return "Draws a label";
	}

	public String getName() {
		return "Text label";
	}

	public JPanel getConfigurationPanel() {
		JPanel panel = new JPanel();
		FormLayout layout = new FormLayout(
				"pref, 4dlu, pref, 4dlu, pref, 8dlu, pref", 
				"pref"
		);
		panel.setLayout(layout);
		
		JRadioButton radioId = new JRadioButton(DISPLAY_ID);
		JRadioButton radioLabel = new JRadioButton(DISPLAY_LABEL);
		radioId.setActionCommand(DISPLAY_ID);
		radioLabel.setActionCommand(DISPLAY_LABEL);
		radioId.addActionListener(this);
		radioLabel.addActionListener(this);
		
		ButtonGroup group = new ButtonGroup();
		group.add(radioId);
		group.add(radioLabel);

		JButton appearance = new JButton(ACTION_APPEARANCE);
		appearance.setActionCommand(ACTION_APPEARANCE);
		appearance.addActionListener(this);
		
		CellConstraints cc = new CellConstraints();
		panel.add(new JLabel("Display: "), cc.xy(1, 1));
		panel.add(radioLabel, cc.xy(3, 1));
		panel.add(radioId, cc.xy(5, 1));
		panel.add(appearance, cc.xy(7, 1));
		
		//Initial values
		if(DISPLAY_ID.equals(display)) {
			radioId.setSelected(true);
		} else {
			radioLabel.setSelected(true);
		}
		return panel;
	}
	
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if(DISPLAY_ID.equals(action) || DISPLAY_LABEL.equals(action)) {
			setDisplayAttribute(action);
		} else if(ACTION_APPEARANCE.equals(action)) {
			JOptionPane.showMessageDialog(null, "Not implemented yet!");
		}
	}
	
	void setDisplayAttribute(String display) {
		this.display = display;
		modified();
	}
	
	void setAlignment(int alignMode) {
		align = alignMode;
		modified();
	}
	
	int getAlignment() { return align; }
	
	void setOverlay(boolean overlay) {
		setUseProvidedArea(!overlay);
		modified();
	}
	
	boolean getOverlay() { return !isUseProvidedArea(); }
	
	public void visualizeOnDrawing(Graphics g, Graphics2D g2d) {
		if(g instanceof GeneProduct) {
			String label = getLabelText((GeneProduct) g);
			if(label == null) {
				return;
			}
			
			Font f = getFont();
			
			Shape region;
			
			if(isUseProvidedArea()) {
				region = getVisualization().provideDrawArea(this, g);
			} else {
				region = g.createVisualizationRegion();
			}
			
			Rectangle area = region.getBounds();
			
			if(!getOverlay()) {
				g2d.setColor(Color.WHITE);
				g2d.fill(area);
			}
			g2d.setColor(Color.BLACK);
			g2d.draw(area);
			
			g2d.clip(region);
					
			if(adaptFontSize) {
				//TODO: adapt font size for awt
				//f = SwtUtils.adjustFontSize(f, new Dimension(area.width, area.height), label, g2d);
			}	
			g2d.setFont(f);
			
			g2d.setColor(getFontColor());
			
			Rectangle2D textSize = g2d.getFontMetrics().getStringBounds(label, g2d);
			
			switch(align) {
			case ALIGN_LEFT: 
				area.x += area.width - textSize.getWidth();
				break;
			case ALIGN_RIGHT:
				area.x += (int)(area.width / 2) - (int)(textSize.getWidth()/ 2);
			}
		
			TextLayout tl = new TextLayout(label, g2d.getFont(), g2d.getFontRenderContext());
			Rectangle2D tb = tl.getBounds();
			tl.draw(g2d, 	(int)area.getX() + (int)(area.getWidth() / 2) - (int)(tb.getWidth() / 2), 
					(int)area.getY() + (int)(area.getHeight() / 2) + (int)(tb.getHeight() / 2));
		}
	}
	
	public Component visualizeOnToolTip(Graphics g) {
		// TODO Auto-generated method stub
		return null;
	}
	
	void setAdaptFontSize(boolean adapt) {
		adaptFontSize = adapt;
		modified();
	}
	
	void setFont(Font f) {
		if(f != null) {
			font = f;
			modified();
		}
	}
	
	void setFontColor(Color fc) {
		fontColor = fc;
		modified();
	}
	
	Color getFontColor() { 
		return fontColor == null ? Color.BLACK : fontColor;
	}
	
	int getFontSize() {
		return getFont().getSize();
	}
	
	Font getFont() {
		return getFont(false);
	}
	
	Font getFont(boolean adjustZoom) {
		Font f = font == null ? DEFAULT_FONT : font;
		if(adjustZoom) {
			//int fs = (int)Math.ceil(Engine.getCurrent().getActiveVPathway().vFromM(f.getSize()) * 15);
			f = new Font(f.getName(), f.getStyle(), f.getSize());
		}
		return f;
	}
	
	private String getLabelText(GeneProduct g) {
		String text = g.getPathwayElement().getTextLabel();
		if(display != null) {
			if(DISPLAY_LABEL.equals(display)) {
				text = g.getPathwayElement().getTextLabel();
			} else if(DISPLAY_ID.equals(display)){
				text = g.getPathwayElement().getGeneID();
			}
		}
		return text;
	}

	static final String XML_ATTR_DISPLAY = "display";
	static final String XML_ATTR_ADAPT_FONT = "adjustFontSize";
	static final String XML_ATTR_FONTDATA = "font";
	static final String XML_ELM_FONTCOLOR = "font-color";
	static final String XML_ATTR_OVERLAY = "overlay";
	static final String XML_ATTR_ALIGN = "alignment";
	public Element toXML() {
		Element elm = super.toXML();
		elm.setAttribute(XML_ATTR_DISPLAY, display);
		elm.setAttribute(XML_ATTR_ADAPT_FONT, Boolean.toString(adaptFontSize));
		
		Font f = getFont();
		String style = "PLAIN";
		if(f.isBold() && f.isItalic()) style = "BOLDITALIC";
		else if (f.isBold()) style = "BOLD";
		else if (f.isItalic()) style = "ITALIC";
		String fs = f.getName() + "-" + style + "-" + f.getSize(); 
		elm.setAttribute(XML_ATTR_FONTDATA, fs);
		
		elm.addContent(ColorConverter.createColorElement(XML_ELM_FONTCOLOR, getFontColor()));
		elm.setAttribute(XML_ATTR_OVERLAY, Boolean.toString(getOverlay()));
		elm.setAttribute(XML_ATTR_ALIGN, Integer.toString(getAlignment()));
		return elm;
	}
	
	public void loadXML(Element xml) {
		super.loadXML(xml);
		
		String styleStr = xml.getAttributeValue(XML_ATTR_DISPLAY);
		String adaptStr = xml.getAttributeValue(XML_ATTR_ADAPT_FONT);
		String fontStr = xml.getAttributeValue(XML_ATTR_FONTDATA);
		String ovrStr = xml.getAttributeValue(XML_ATTR_OVERLAY);
		String alnStr = xml.getAttributeValue(XML_ATTR_ALIGN);
		Element fcElm = xml.getChild(XML_ELM_FONTCOLOR);
		try {
			if(styleStr != null) setDisplayAttribute(styleStr);
			if(adaptStr != null) adaptFontSize = Boolean.parseBoolean(adaptStr);
			if(fontStr != null) font = Font.decode(fontStr);
			if(ovrStr != null) setOverlay(Boolean.parseBoolean(ovrStr));
			if(fcElm != null) fontColor = ColorConverter.parseColorElement(fcElm);
			if(alnStr != null) align = Integer.parseInt(alnStr);
		} catch(NumberFormatException e) {
			Logger.log.error("Unable to load configuration for " + getName(), e);
		}
	}
}