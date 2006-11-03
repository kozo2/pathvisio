package colorSet;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import util.SwtUtils;

public class ColorSetComposite extends Composite {
	final int colorLabelSize = 15;
	GmmlColorSet colorSet;
	
	ListViewer objectList;
	
	Composite colorButtons;
	Color colorNCM, colorNGF, colorNDF;
	CLabel labelColorNCM, labelColorNGF, labelColorNDF;
	Combo colorSetCombo;
	Text nameText;
	
	public ColorSetComposite(Composite parent, int style) {
		super(parent, style);
		createContents();
	}
	
	
	public void setInput(GmmlColorSet cs) {
		colorSet = cs;
		if(colorSet == null) {
			setColorSetEnabled(false);
		} else {
			setColorSetEnabled(true);
			initColorLabels();
			initName();
			objectList.setInput(colorSet.getObjects().toArray());
		}
	}
	
	//TODO: also child composites
	void setColorSetEnabled(boolean enable) {
		for(Control c : colorButtons.getChildren()) c.setEnabled(enable);
		nameText.setEnabled(enable);
	}
	
	void initName() {
		nameText.setText(colorSet.getName());
	}
	
	void initColorLabels() {
		changeLabelColor(labelColorNCM, colorSet.color_no_criteria_met);
		changeLabelColor(labelColorNGF, colorSet.color_no_gene_found);
		changeLabelColor(labelColorNDF, colorSet.color_no_data_found);
	}
	
	public void refreshCombo() {
		colorSetCombo.setItems(ColorSetManager.getColorSetNames());
		int current = ColorSetManager.getColorSetIndex();
		colorSetCombo.select(current);
		colorSetCombo.layout();
	}

	void createContents() {
		setLayout(new GridLayout());
		Composite colorSetComp = createColorSetComposite(this);
		colorSetComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
				
		Group objectsGroup = new Group(this, SWT.NULL);
		objectsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		objectsGroup.setLayout(new GridLayout(2, false));
		
		objectList = new ListViewer(objectsGroup, SWT.SINGLE | SWT.READ_ONLY);
		objectList.setContentProvider(new ArrayContentProvider());
		objectList.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((GmmlColorSetObject)element).getName();
			}
		});
		
		new ObjectSettingsComposite(objectsGroup, SWT.NONE);

		refreshCombo();
		colorSetCombo.select(0);
	}

	RGB askColor(RGB current) {
		ColorDialog cd = new ColorDialog(getShell());
		cd.setRGB(current);
		return cd.open();
	}
	
	void changeColor(CLabel label) {
		RGB rgb = askColor(label.getBackground().getRGB());
		if(rgb == null) return;
		if		(label == labelColorNCM) colorSet.color_no_criteria_met = rgb;
		else if	(label == labelColorNGF) colorSet.color_no_gene_found = rgb;
		else if	(label == labelColorNDF) colorSet.color_no_data_found = rgb;
		
		changeLabelColor(label, rgb);
	}
	
	void changeLabelColor(CLabel label, RGB rgb) {
		Color c = null;
		if		(label == labelColorNCM) c = colorNCM;
		else if (label == labelColorNGF) c = colorNGF;
		else if (label == labelColorNDF) c = colorNDF;

		label.setBackground(SwtUtils.changeColor(c, rgb, getShell().getDisplay()));
	}
	
	void addColorSet() {
		ColorSetManager.newColorSet(null);
		refreshCombo();
		colorSetCombo.select(ColorSetManager.getColorSets().size() - 1);
	}
	
	void removeColorSet() {
		ColorSetManager.removeColorSet(colorSetCombo.getSelectionIndex());
		refreshCombo();
	}
	
	void modifyName(String newName) {
		if(!newName.equals("")) colorSet.setName(newName);
	}
	
	void colorSetSelected() {
		int index = colorSetCombo.getSelectionIndex();
		if(index == -1)
			setInput(null);
		else
			setInput(ColorSetManager.getColorSets().get(index));
	}
	
	Composite createColorSetComposite(Composite parent) {
		final Composite csComp = new Composite(parent, SWT.NULL);
		csComp.setLayout(new GridLayout(2, false));
		
		colorSetCombo = new Combo(csComp, SWT.SINGLE | SWT.READ_ONLY);
		colorSetCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				colorSetSelected();
			}
		});
			
		nameText = new Text(csComp, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				modifyName(nameText.getText());
			}
		});
		
		Composite buttons = new Composite(csComp, SWT.NULL);
		buttons.setLayout(new RowLayout(SWT.HORIZONTAL));
		final Button add = new Button(buttons, SWT.PUSH);
		add.setText("Add");
		final Button remove = new Button(buttons, SWT.PUSH);
		remove.setText("Remove");
		
		SelectionListener buttonAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(e.widget == add) addColorSet();
				else removeColorSet();
				csComp.layout();
			}
		};
		
		add.addSelectionListener(buttonAdapter);
		remove.addSelectionListener(buttonAdapter);
		
		colorButtons = createFixedColors(csComp);
		colorButtons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		return csComp;
	}
	
	Composite createFixedColors(Composite parent) {		
		Group buttonGroup = new Group(parent, SWT.NULL);
		buttonGroup.setLayout(new GridLayout(3, false));
		buttonGroup.setText("Colors");
		
		GridData colorLabelGrid = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		colorLabelGrid.widthHint = colorLabelGrid.heightHint = colorLabelSize;
		CLabel[] colorLabels = new CLabel[3];
		String[] names = new String[] {
				"No criteria met", "Gene not found", "No data found"
		};
		for(int i = 0; i < colorLabels.length; i++) {
			Composite comp = new Composite(buttonGroup, SWT.NULL);
			comp.setLayout(new GridLayout(3, false));
			final CLabel clabel = new CLabel(comp, SWT.SHADOW_IN);
			clabel.setLayoutData(colorLabelGrid);
			Button b = new Button(comp, SWT.PUSH);
			b.setLayoutData(colorLabelGrid);
			b.setText("...");;
			b.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					changeColor(clabel);
				}
			});
			Label label = new Label(comp, SWT.NULL);
			label.setText(names[i]);
			colorLabels[i] = clabel;
		}
		labelColorNCM = colorLabels[0]; colorNCM = labelColorNCM.getBackground();
		labelColorNGF = colorLabels[1]; colorNGF = labelColorNGF.getBackground();
		labelColorNDF = colorLabels[2]; colorNDF = labelColorNDF.getBackground();
				
		return buttonGroup;
	}
	
	class ObjectSettingsComposite extends Composite {
		StackLayout stack;
		GmmlColorSetObject input;
		
		public ObjectSettingsComposite(Composite parent, int style) {
			super(parent, style);
		}
		
		void createContents() {
			stack = new StackLayout();
			setLayout(stack);
						
			//Gradient
			new GmmlColorGradient.ColorGradientComposite(this, SWT.NULL);
			//Criterion
			new GmmlColorCriterion.ColorCriterionComposite(this, SWT.NULL);
		}		
	}
}
