package visualization;

import gmmlVision.GmmlVision;
import gmmlVision.GmmlVision.PropertyEvent;
import gmmlVision.GmmlVision.PropertyListener;
import graphics.GmmlDrawing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

/**
 * Manages visualizations
 * @author thomas
 *
 */
public class VisualizationManager implements PropertyListener {
	static { GmmlVision.addPropertyListener(new VisualizationManager()); }
	
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
		GmmlDrawing d = GmmlVision.getDrawing();
		if(d != null) d.redraw();
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
		visComboItem.update();
	}
	
	public static void removeVisualization(int index) {
		if(index < 0 || index >= visualizations.size()) return; //Ignore wrong index
		visualizations.remove(index);
		visComboItem.update();
	}
	
	public static void removeVisualization(Visualization v) {
		visualizations.remove(v);
		visComboItem.update();
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
	
	static class VisComboItem extends ControlContribution {
		final String NONE = "no visualization";
		Combo visCombo;
		
		public VisComboItem(String id) {
			super(id);
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
	}
	
	public void propertyChanged(PropertyEvent e) {
		if(e.name == GmmlVision.PROPERTY_CLOSE_APPLICATION) {
			saveGeneric();
		}		
	}

}
