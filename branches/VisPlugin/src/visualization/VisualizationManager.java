package visualization;

import gmmlVision.GmmlVision;
import gmmlVision.GmmlVision.ApplicationEvent;
import gmmlVision.GmmlVision.ApplicationEventListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import data.GmmlGex.ExpressionDataListener;

/**
 * Manages visualizations
 * @author thomas
 *
 */
public class VisualizationManager implements ApplicationEventListener {
	static { GmmlVision.addApplicationEventListener(new VisualizationManager()); }
	
	static final String XML_ELEMENT = "visualization-set";
		
	static final String FILENAME_GENERIC = "visualizations.xml";
	
	static final int CURRENT_NONE = -1;
	
	static List<Visualization> visualizations = new ArrayList<Visualization>();
	static int current;
		
	public static Visualization getCurrent() {
		if(current < 0 || current >= visualizations.size()) return null;
		return visualizations.get(current);
	}
	
	public static void setCurrent(int index) {
		current = index;
		firePropertyChange(
				new VisualizationEvent(null, VisualizationEvent.VISUALIZATION_SELECTED));
	}
	
	public static List<Visualization> getVisualizations() {
		return visualizations;
	}
	
	public static List<Visualization> getGenericVisualizations() {
		List<Visualization> generic = new ArrayList<Visualization>();
		for(Visualization v : visualizations) if(v.isGeneric()) generic.add(v);
		return generic;
	}
	
	public static String[] getNames() {
		String[] names = new String[visualizations.size()];
		for(int i = 0; i < names.length; i++) 
			names[i] = visualizations.get(i).getName();
		return names;
	}
	
	public static void addVisualization(Visualization v) {
		visualizations.add(v);
		firePropertyChange(
				new VisualizationEvent(null, VisualizationEvent.VISUALIZATION_ADDED));
	}
	
	public static void removeVisualization(int index) {
		if(index < 0 || index >= visualizations.size()) return; //Ignore wrong index
		visualizations.remove(index);
		firePropertyChange(
				new VisualizationEvent(null, VisualizationEvent.VISUALIZATION_REMOVED));
	}
	
	public static void removeVisualization(Visualization v) {
		removeVisualization(visualizations.indexOf(v));
	}
	
	public static String getNewName() {
		String prefix = "visualization";
		int i = 1;
		String name = prefix;
		while(nameExists(name)) name = prefix + "-" + i++;
		return name;
	}
	
	public static boolean nameExists(String name) {
		for(Visualization v : visualizations) 
			if(v.getName().equalsIgnoreCase(name)) return true;
		return false;
	}
	
	public void saveGeneric() {
		Document xmlDoc = new Document();
		Element root = new Element(XML_ELEMENT);

		for(Visualization v : visualizations) root.addContent(v.toXML());
		xmlDoc.addContent(root);
		
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		try {
			FileWriter fw = new FileWriter(getGenericFile());
			out.output(xmlDoc, fw);
			fw.close();
		} catch(IOException e) {
			GmmlVision.log.error("Unable to save visualization settings", e);
		}
	}
	
	public static void loadGeneric() {
		if(!getGenericFile().exists()) return; //No generic visualizations saved yet
		SAXBuilder parser = new SAXBuilder();
		try {
			Document doc = parser.build(getGenericFile());
			Element root = doc.getRootElement();
			for(Object o : root.getChildren(Visualization.XML_ELEMENT)) {
				visualizations.add(Visualization.fromXML((Element) o));				
			}
		} catch(Exception e) {
			GmmlVision.log.error("Unable to load visualization settinsg", e);
		}
	}
	
	static File getGenericFile() {
		return new File(GmmlVision.getApplicationDir(), FILENAME_GENERIC);
	}
	
	static VisComboItem visComboItem = new VisComboItem("VisualizationCombo");
	public static ContributionItem getComboItem() {
		return visComboItem;
	}
	
	static class VisComboItem extends ControlContribution implements VisualizationListener {
		final String NONE = "no visualization";
		Combo visCombo;
		
		public VisComboItem(String id) {
			super(id);
			addListener(this);
		}

		protected Control createControl(Composite parent) {
			Composite control = new Composite(parent, SWT.NULL);
			control.setLayout(new RowLayout(SWT.HORIZONTAL));
			
			Label label = new Label(control, SWT.LEFT);
			label.setText("Apply visualization: ");
			visCombo = new Combo(control, SWT.DROP_DOWN | SWT.READ_ONLY);
			visCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setCurrent(visCombo.getSelectionIndex() - 1);
				}
			});
			visCombo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setCurrent(visCombo.getSelectionIndex() - 1);
				}
			});
			update();
			return control;
		}
		
		public void update() {
			String previous = visCombo.getText();
			int select = 0;
			
			String[] visnames = getNames();
			String[] items = new String[visnames.length + 1];
			items[0] = NONE;
			for(int i = 1; i < items.length; i++) {
				items[i] = visnames[i-1];
				if(items[i].equals(previous)) select = i; 
			}
			visCombo.setItems(items);
			visCombo.select(select);
		}

		public void visualizationEvent(VisualizationEvent e) {
			switch(e.type) {
			case(VisualizationEvent.VISUALIZATION_ADDED):
			case(VisualizationEvent.VISUALIZATION_REMOVED):
			case(VisualizationEvent.VISUALIZATION_MODIFIED):
				update();
			}
			
		}
	}
	
	public void applicationEvent(ApplicationEvent e) {
		if(e.type == ApplicationEvent.CLOSE_APPLICATION) {
			saveGeneric();
		}		
	}

	static List<VisualizationListener> listeners = new ArrayList<VisualizationListener>();

	/**
	 * Add a {@link ExpressionDataListener}, that will be notified if an
	 * event related to visualizations occurs
	 * @param l The {@link ExpressionDataListener} to add
	 */
	public static void addListener(VisualizationListener l) {
		if(listeners == null) listeners = new ArrayList<VisualizationListener>();
		listeners.add(l);
	}

	/**
	 * Fire a {@link VisualizationEvent} to notify all {@link VisualizationListener}s registered
	 * to this class
	 * @param e
	 */
	public static void firePropertyChange(VisualizationEvent e) {
		for(VisualizationListener l : listeners) l.visualizationEvent(e);
	}

	public interface VisualizationListener {
		public void visualizationEvent(VisualizationEvent e);
	}

	public static class VisualizationEvent extends EventObject {
		private static final long serialVersionUID = 1L;
		public static final int COLORSET_ADDED = 0;
		public static final int COLORSET_REMOVED = 1;
		public static final int COLORSET_MODIFIED = 2;
		public static final int VISUALIZATION_ADDED = 3;
		public static final int VISUALIZATION_REMOVED = 4;
		public static final int VISUALIZATION_MODIFIED = 5;
		public static final int VISUALIZATION_SELECTED = 6;

		public Object source;
		public int type;

		public VisualizationEvent(Object source, int type) {
			super(source == null ? VisualizationManager.class : source);
			this.source = source;
			this.type = type;
		}
	}
	
}
