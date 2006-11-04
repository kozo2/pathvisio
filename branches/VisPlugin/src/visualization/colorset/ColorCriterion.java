package visualization.colorset;

import gmmlVision.GmmlVision;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.jdom.Element;


import util.ColorConverter;
import util.SwtUtils;
import visualization.colorset.ColorGradient.ColorValuePair;

public class ColorCriterion extends ColorSetObject {
	public static final String XML_ELEMENT_NAME = "ColorCriterion";
	
	Criterion criterion = new Criterion();
	
	public static final RGB INITIAL_COLOR = new RGB(255, 255, 255);
	private RGB color;
	public void setColor(RGB color) { this.color = color; }
	public RGB getColor() { return color == null ? INITIAL_COLOR : color; }
	
	public Criterion getCriterion() { return criterion; }
	
	public ColorCriterion(ColorSet parent, String name) {
		super(parent, name);
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
		
		public ColorCriterionComposite(Composite parent, int style) {
			super(parent, style);
		}
		
		void refresh() {
			super.refresh();
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
		}
		
		void changeColor(CLabel label) {
			RGB rgb = new ColorDialog(getShell()).open();
			if(rgb != null) {
				label.setBackground(
						SwtUtils.changeColor(
								label.getBackground(), rgb, getShell().getDisplay()));
				getInput().setColor(rgb);
			}
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
			
			Label colorLabel = new Label(colorComp, SWT.CENTER);
			colorLabel.setText("Color:");

			final CLabel color = new CLabel(colorComp, SWT.SHADOW_IN);
			color.setLayoutData(colorLabelGrid);

			Button colorButton = new Button(colorComp, SWT.PUSH);
			colorButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					changeColor(color);
				}
			});
			
			colorButton.setLayoutData(colorLabelGrid);
			colorButton.setText("...");
			
			return colorComp;
		}
	}
}
