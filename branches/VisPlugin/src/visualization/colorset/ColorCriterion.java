package visualization.colorset;

import gmmlVision.GmmlVision;

import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jdom.Element;

import util.ColorConverter;
import util.SwtUtils;

public class ColorCriterion extends ColorSetObject {
	public static final String XML_ELEMENT_NAME = "ColorCriterion";
	
	Criterion criterion;
	
	public static final RGB INITIAL_COLOR = new RGB(255, 255, 255);
	private RGB color;
	public void setColor(RGB color) { 
		this.color = color;
		fireModifiedEvent();
	}
	
	public RGB getColor() { return color == null ? INITIAL_COLOR : color; }
	
	public Criterion getCriterion() { return criterion; }
	
	public ColorCriterion(ColorSet parent, String name) {
		super(parent, name);
		criterion = new Criterion();
	}

	public ColorCriterion(ColorSet parent, Element xml) {
		super(parent, xml);
	}
	
	RGB getColor(HashMap<Integer, Object> data, int idSample) {
		try {
			if(criterion.evaluate(data)) return color;
		} catch (Exception e) { 
			GmmlVision.log.error("Unable to evaluate expression '" + criterion.getExpression() + "'", e);
			//TODO: tell user that expression is incorrect
		}
		return null;
	}
	
	public String getXmlElementName() {
		return XML_ELEMENT_NAME;
	}
	
	protected void loadXML(Element xml) {
		super.loadXML(xml);
		try {
			String expression = xml.getAttributeValue(XML_ATTR_EXPRESSION);
			criterion = new Criterion();
			criterion.setExpression(expression);
		} catch(Exception e) {
			GmmlVision.log.error("Unable to load ColorCriterion", e);
		}
	}
	
	static final String XML_ELM_COLOR = "color";
	static final String XML_ATTR_EXPRESSION = "expression";
	public Element toXML() {
		Element elm = super.toXML();
		ColorConverter.createColorElement(XML_ELM_COLOR, getColor());
		elm.setAttribute(XML_ATTR_EXPRESSION, criterion.getExpression());
		
		return elm;
	}
		
	public static class ColorCriterionComposite extends ConfigComposite {
		final int colorLabelSize = 15;
		CriterionComposite critComp;
		CLabel colorLabel;
		
		public ColorCriterionComposite(Composite parent, int style) {
			super(parent, style);
		}
		
		void refresh() {
			super.refresh();
			critComp.refresh();
			setColorLabel(colorLabel, getInput() == null ? null : getInput().getColor());
		}
		
		ColorCriterion getInput() {
			return (ColorCriterion)input;
		}
		
		public boolean save() {
			if(input != null) try {
				critComp.saveToCriterion();
			} catch(Exception e) {
				MessageDialog.openError(getShell(), "Unable to save expression",
						"Invalid expression syntax: " + e.getMessage());
				return false;
			}
			return true;
		}
		
		public void setInput(ColorSetObject o) {
			super.setInput(o);
			if(o == null) critComp.setInput(null);
			else critComp.setInput(((ColorCriterion)o).getCriterion());
			refresh();
		}
		
		void askColor(CLabel label) {
			RGB rgb = new ColorDialog(getShell()).open();
			if(rgb != null) {
				setColorLabel(label, rgb);
				getInput().setColor(rgb);
			}
		}
			
		Color c;
		void setColorLabel(CLabel label, RGB rgb) {
			if(rgb == null) return;
			getInput().setColor(rgb);
			System.out.println("Previous color " + c);
			System.out.println("Previous label color " + label.getBackground());
			c = SwtUtils.changeColor(c, rgb, getShell().getDisplay());
			System.out.println("Setting bg to " + c);
			label.setBackground(c);
//			label.redraw();
//			label.layout(true);
			System.out.println(label.getBackground());
//			if(c != null) c.dispose();
		}
		
		void createContents() {
			setLayout(new GridLayout());
			
			Composite superComp = super.createNameComposite(this);
			superComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			Composite colorComp = createColorComp(this);
			colorComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					    
		    critComp = new CriterionComposite(this, null);
		    critComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		    critComp.fetchSymbolsFromGex();
		}
		
		Composite createColorComp(Composite parent) {
			Composite colorComp = new Composite(parent, SWT.NULL);
			colorComp.setLayout(new GridLayout(3, false));
			
			final GridData colorLabelGrid = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			colorLabelGrid.widthHint = colorLabelGrid.heightHint = colorLabelSize;
			
			Label label = new Label(colorComp, SWT.CENTER);
			label.setText("Color:");

			colorLabel = new CLabel(colorComp, SWT.SHADOW_IN);
			colorLabel.setLayoutData(colorLabelGrid);

			Button colorButton = new Button(colorComp, SWT.PUSH);
			colorButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					askColor(colorLabel);
				}
			});
			
			colorButton.setLayoutData(colorLabelGrid);
			colorButton.setText("...");
			
			return colorComp;
		}
	}
}
