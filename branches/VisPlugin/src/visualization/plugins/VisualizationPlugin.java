package visualization.plugins;

import gmmlVision.GmmlVision;
import graphics.GmmlGraphics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;

import visualization.Visualization;

public abstract class VisualizationPlugin {
	public static String XML_ELEMENT = "plugin";
	public static String XML_ATTR_CLASS = "class";
	private static String XML_ATTR_SIDEPANEL = "useSidePanel";
	private static String XML_ATTR_TOOLTIP = "useToolTip";
	private static String XML_ATTR_DRAWING = "useDrawingObject";
	
	private String[] rep_names = new String[] {"Drawing object", "Side panel", "Tooltip"};
	protected static final int SIDEPANEL = 2;
	protected static final int TOOLTIP = 4;
	protected static final int DRAWING = 8;
	
	private int DISPLAY_OPT; //Which representations possible (SIDEPANEL | TOOLTIP | DRAWING)
	private boolean CONFIGURABLE; //Configurable (if true, override createConfigComposite)
	private boolean GENERIC; //For generic use, or expression dataset specific
	private boolean USE_RESERVED_REGION; //Does this plugin use reserved region in GmmlGraphicsObject
	
	private boolean dialogCompleted = true;
	private boolean isActive;
	
	private boolean useDrawingObject;
	private boolean useSidePanel;
	private boolean useToolTip;
	
	private Visualization visualization;
	
	public VisualizationPlugin(Visualization v) {
		visualization = v;
	}
	
	protected Visualization getVisualization() { return visualization; }
	
	public abstract String getName();
	
	public abstract void draw(GmmlGraphics g, PaintEvent e, GC buffer);
	public abstract void updateSidePanel(GmmlGraphics g);
	public abstract Composite getToolTipComposite(Composite parent, GmmlGraphics g);
	
	public abstract void createSidePanelComposite(Composite parent);
			
	protected Composite createConfigComposite(Composite parent) {
		return new Composite(parent, SWT.NULL); //Empty composite
	}
	
	public void openConfigDialog(Shell shell) {
		if(!CONFIGURABLE) return; //Not configurable, so don't open config dialog
		ApplicationWindow d = new ConfigurationDialog(shell);
		d.open();
	}
	
	protected void setDialogCompleted(boolean completed) {
		dialogCompleted = completed;
	}
	
	public String[] getRepresentations() {
		List<String> reps = new ArrayList<String>();
		if(canDrawingObject()) 	reps.add(rep_names[0]);
		if(canSidePanel()) 		reps.add(rep_names[1]);
		if(canToolTip()) 	reps.add(rep_names[2]);
		return reps.toArray(new String[reps.size()]);
	}
	
	public Element toXML() {
		Element elm = new Element(XML_ELEMENT);
		elm.setAttribute(XML_ATTR_CLASS, getClass().getCanonicalName());
		elm.setAttribute(XML_ATTR_DRAWING, Boolean.toString(isUseDrawingObject()));
		elm.setAttribute(XML_ATTR_SIDEPANEL, Boolean.toString(isUseSidePanel()));
		elm.setAttribute(XML_ATTR_TOOLTIP, Boolean.toString(isUseToolTip()));
		return elm;
	}
	
	public void loadXML(Element xml) {
		String drw = xml.getAttributeValue(XML_ATTR_DRAWING);
		String sp = xml.getAttributeValue(XML_ATTR_SIDEPANEL);
		String tt = xml.getAttributeValue(XML_ATTR_TOOLTIP);
		try {
			setUseDrawingObject(Boolean.parseBoolean(drw));
			setUseSidePanel(Boolean.parseBoolean(sp));
			setUseToolTip(Boolean.parseBoolean(tt));
		} catch(Exception e) {
			GmmlVision.log.error("Unable to parse settings for VisualizationPlugin", e);
		}
	}
	
	public boolean isUseSidePanel() { return useSidePanel; }
	public boolean isUseToolTip() { return useToolTip; }
	public boolean isUseDrawingObject() { return useDrawingObject; }
	
	public final boolean isActive() { return isActive; }
	public final void setActive(boolean active) { isActive = active; }

	public void setUseSidePanel(boolean use) { 
		if(canSidePanel()) useSidePanel = use; 
	}
	public void setUseToolTip(boolean use) { ;
		if(canToolTip()) useToolTip = use;
	}
	public void setUseDrawingObject(boolean use) { 
		if(canDrawingObject()) useDrawingObject = use; 
	}
	
	public final boolean canSidePanel() { return (DISPLAY_OPT & SIDEPANEL) != 0; }
	public final boolean canToolTip() { return (DISPLAY_OPT & TOOLTIP) != 0; }
	public final boolean canDrawingObject() { return (DISPLAY_OPT & DRAWING) != 0; }
	
	/**
	 * Specify where this plugin can be displayed.
	 * One of:<BR><UL>
	 * <LI><CODE>DRAWING</CODE>: this plugin implements visualization on drawing objects
	 * <LI><CODE>TOOLTIP</CODE>: this plugins implements visualization in the tooltip showed
	 * when hovering over GeneProducts
	 * <LI><CODE>SIDEPANEL</CODE>: this plugin implements visualization to be displayed in the side panel
	 * </UL><BR>
	 * When multiple visualization options are implemented, 
	 * use bitwise OR (e.g. <CODE>SIDEPANEL | DRAWING</CODE>)
	 * @param options
	 */
	protected void setDisplayOptions(int options) {
		DISPLAY_OPT = options;
	}
	
	protected void setUseReservedArea(boolean use) {
		USE_RESERVED_REGION = use;
	}
	
	protected void setIsConfigurable(boolean configurable) {
		CONFIGURABLE = configurable;
	}
	
	protected void setIsGeneric(boolean generic) {
		GENERIC = generic;
	}
	
	public final boolean isGeneric() { return GENERIC; }
	public final boolean isConfigurable() { return CONFIGURABLE; }
	public final boolean isUseReservedRegion() { return USE_RESERVED_REGION; }
				
	private class ConfigurationDialog extends ApplicationWindow {
		public ConfigurationDialog(Shell shell) {
			super(shell);
			setBlockOnOpen(true);
		}
		
		public Control createContents(Composite parent) {
			Composite contents = new Composite(parent, SWT.NULL);
			contents.setLayout(new GridLayout());
			
			Composite config = createConfigComposite(contents);
			config.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			Composite buttonComp = createButtonComposite(contents);
			buttonComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			return contents;
		}
		
		public Composite createButtonComposite(Composite parent) {
			Composite comp = new Composite(parent, SWT.NULL);
			comp.setLayout(new GridLayout(2, false));
			Button cancel = new Button(comp, SWT.PUSH);
			cancel.setText(" Cancel ");
			cancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					close();
				}
			});
			
			Button ok = new Button(comp, SWT.PUSH);
			ok.setText(" Ok ");
			ok.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					if(dialogCompleted) close();
				}
			});
			
			return comp;
		}
	}
}
