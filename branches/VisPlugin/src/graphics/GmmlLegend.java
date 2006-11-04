package graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import util.SwtUtils;
import visualization.VisualizationManager.VisualizationEvent;
import visualization.VisualizationManager.VisualizationListener;
import visualization.colorset.ColorSetManager;
import visualization.colorset.ColorCriterion;
import visualization.colorset.ColorGradient;
import visualization.colorset.ColorSet;
import visualization.colorset.ColorSetObject;
import visualization.colorset.ColorGradient.ColorValuePair;

public class GmmlLegend extends ScrolledComposite implements VisualizationListener {
	static final String FONT = "arial narrow";
	static final int FONTSIZE = 8;
	
	ColorSet colorSet;
	
	GradientCanvas gradients;
	CriteriaComposite criteria;
	
	Combo colorSetCombo;
		
	public GmmlLegend(Composite parent, int style)
	{
		super(parent, style);
		
		createContents();
	}
	
	public void setInput(ColorSet input) {
		colorSet = input;
		refreshContent();
	}
	
	public void refresh() {
		String[] names = ColorSetManager.getColorSetNames();
		if(names.length == 0) colorSetCombo.setEnabled(false);
		else {
			colorSetCombo.setItems(names);
			colorSetCombo.select(0);
		}
	}
	void refreshContent() {		
		criteria.refresh();
		gradients.refresh();
	}
	
	public void createContents() {	
		Composite contents = new Composite(this, SWT.NULL);
		setContent(contents);
		setExpandHorizontal(true);
		setExpandVertical(true);
		
		contents.setLayout(new GridLayout());
		
		Composite comboComp = createColorSetCombo(contents);
		comboComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite legendComp = createLegendComp(contents);
		legendComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
		setChildrenBackground(contents);
	}
	
	void setChildrenBackground(Composite comp) {
		comp.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		for(Control c : comp.getChildren()) {
			c.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			if(c instanceof Composite) setChildrenBackground((Composite) c);
		}
	}
	
	void setComboItems() {
		colorSetCombo.setItems(ColorSetManager.getColorSetNames());
	}
	
	Composite createColorSetCombo(Composite parent) {
		Composite comboComp = new Composite(parent, SWT.NULL);
		comboComp.setLayout(new GridLayout(2, false));
		Label label = new Label(comboComp, SWT.NULL);
		label.setText("Color set:");
		colorSetCombo = new Combo(comboComp, SWT.READ_ONLY);
		colorSetCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		colorSetCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setInput(ColorSetManager.getColorSets().get(colorSetCombo.getSelectionIndex()));
			}
		});
		
		return comboComp;
	}
	
	Composite createLegendComp(Composite parent) {
		Group legendGroup = new Group(this, SWT.NULL);
		legendGroup.setLayout(new GridLayout());
		legendGroup.setText("Legend");
		
		Group cg = new Group(legendGroup, SWT.SHADOW_IN);
		Group gg = new Group(legendGroup, SWT.SHADOW_IN);
		
		criteria = new CriteriaComposite(cg, SWT.NONE);
		gradients = new GradientCanvas(gg, SWT.NONE);

		cg.setLayoutData(new GridData(GridData.FILL_BOTH));
		gg.setLayoutData(new GridData(GridData.FILL_BOTH));

		cg.setText("Criteria");
		gg.setText("Gradients");
				
		gg.setLayout(new FillLayout());
		cg.setLayout(new FillLayout());
		
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		return legendGroup;
	}
			
	private class CriteriaComposite extends Composite
	{
		
		public CriteriaComposite(Composite parent, int style)
		{
			super(parent, style);
			setLayout(new GridLayout(2, false));
			setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		}
		
		final static int CLABEL_SIZE = 10;
		public void refresh()
		{
			for(Control c : getChildren()) c.dispose();
			
			Color c = null;
			Image image = null;

			if(colorSet == null) return;
			
			//Draw CLabel for every criterion
			for(ColorSetObject co : colorSet.colorSetObjects)
			{
				if(!(co instanceof ColorCriterion)) continue; //skip objects other than criretia
				ColorCriterion cc = (ColorCriterion)co;
				c = SwtUtils.changeColor(c, cc.getColor(), getDisplay());
				createCriterionLabel(cc.getName(), c);
			}

			//Draw label for special criteria ('no gene found', 'no criteria met')
			String[] specialLabels = {"No criteria met", "Gene not found", "No data found"};
			RGB[] specialColors = {colorSet.color_no_criteria_met, colorSet.color_no_gene_found,
					colorSet.color_no_data_found};

			for(int i = 0; i < specialColors.length; i++)
			{
				c = SwtUtils.changeColor(c, specialColors[i], getDisplay());
				createCriterionLabel(specialLabels[i], c);
			}

			//This label requires an image
			Label multipleData = new Label(this, SWT.LEFT | SWT.FLAT);
			multipleData.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			GridData clabelGrid = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			clabelGrid.widthHint = CLABEL_SIZE;
			clabelGrid.heightHint = CLABEL_SIZE;
			multipleData.setLayoutData(clabelGrid);

			if(image != null && !image.isDisposed()) image.dispose();

			image = new Image(getDisplay(), CLABEL_SIZE, CLABEL_SIZE);
			GC imageGc = new GC(image);
			imageGc.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
			imageGc.drawRectangle(1, 1, CLABEL_SIZE - 3, CLABEL_SIZE - 3);
			imageGc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			imageGc.drawRectangle(0, 0, CLABEL_SIZE - 1, CLABEL_SIZE - 1);
			multipleData.setImage(image);
			Label multipleDataLabel = new Label(this, SWT.LEFT);
			multipleDataLabel.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			multipleDataLabel.setText("Gene maps to multiple ids");

			if(c != null) c.dispose();
			if(imageGc != null) imageGc.dispose();
		}
			
		private void createCriterionLabel(String text, Color c)
		{
			GridData clabelGrid = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			clabelGrid.widthHint = CLABEL_SIZE;
			clabelGrid.heightHint = CLABEL_SIZE;
			
			CLabel cLabel = new CLabel(this, SWT.SHADOW_IN);
			Label label = new Label(this, SWT.LEFT);
			
			label.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			label.setText(text);
			
			cLabel.setBackground(c);
			cLabel.setLayoutData(clabelGrid);
		}
	}
	
	private class GradientCanvas extends Canvas implements PaintListener
	{		
		public GradientCanvas(Composite parent, int style)
		{
			super(parent, style);
			addPaintListener(this);
			setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		}
		
		public void refresh() {
			redraw();
		}
		
		int getNrGradients() {
			int n = 0;
			for(ColorSetObject co : colorSet.getObjects()) {
				if(co instanceof ColorGradient) n++;
			}
			return n;
		}
		
		public void paintControl (PaintEvent e)
		{
			if(colorSet == null) return;

			//Divide canvas in nr-gradients rows
			Point size = getSize();
			int n = getNrGradients();
			int i = 0;
			for(ColorSetObject co : colorSet.getObjects())
				if(co instanceof ColorGradient) {
					ColorGradient cg = (ColorGradient)co;
					Rectangle area = new Rectangle(
							0, i * size.y / n,
							size.x, size.y / n++);
					drawColorGradient(e, cg, area);
				}
		}
	}
		
		final static int MAX_BAR_HEIGHT = 35;
		final static int MARGIN_VERTICAL = 20;
		final static int MARGIN_HORIZONTAL = 10;
		final static int MARKER_LENGTH = 4;
		public void drawColorGradient(PaintEvent e, ColorGradient cg, Rectangle r)
		{
			Color c = null;
			RGB oldBackground = getBackground().getRGB();
			
			double[] minmax = cg.getMinMax();
			double min = minmax[0];
			double max = minmax[1];
			
			if((float)max == (float)min) {
				return;
			}
			
			// Get region to draw
			int yStart = r.y + MARGIN_VERTICAL;
			int barHeight = Math.min(r.height - MARGIN_VERTICAL, MAX_BAR_HEIGHT - MARGIN_VERTICAL);
			int start = r.x + MARGIN_HORIZONTAL;
			int end = r.width - MARGIN_HORIZONTAL;
			
			int n = end - start;
			
			// Fill squares with color cg.getColor()
			for(int i = start; i < end; i++) {
				double colorValue = min + (i-start) * (max - min) / n;
				RGB rgb = cg.getColor(colorValue);
				if(rgb != null) {
					c = SwtUtils.changeColor(c, rgb, e.display);
					e.gc.setBackground(c);
					e.gc.fillRectangle(i, yStart, 1, barHeight);
				}
			}
			
			Font f = new Font(e.display, FONT, FONTSIZE, SWT.NONE);
			e.gc.setFont(f);
			
			int markerCenter = yStart + barHeight;
			c = SwtUtils.changeColor(c, oldBackground, e.display);
			e.gc.setBackground(c);
			for(ColorValuePair cvp : cg.getColorValuePairs())
			{
				int x = (int)(start + (cvp.value - min) / (max - min) * (end - start));
				e.gc.drawLine(x, markerCenter - MARKER_LENGTH, x, markerCenter + MARKER_LENGTH);
				Point labelSize = e.gc.textExtent(Double.toString(cvp.value));
				e.gc.drawString(Double.toString(cvp.value), x - labelSize.x / 2, 
						markerCenter + labelSize.y / 2, true);
			}
						
			c.dispose();
			f.dispose();
		}

		public void visualizationEvent(VisualizationEvent e) {
			refresh();
		}
}
	
//	private class SampleComposite extends Composite
//	{
//		GmmlLegend legend;
//		Canvas sampleCanvas;
//		
//		public SampleComposite(Composite parent, int style)
//		{
//			super(parent, style);
//			setLayout(new GridLayout(1, false));
//			setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
//		}
//		
//		public void setLegend(GmmlLegend legend)
//		{
//			this.legend = legend;
//		}
//		
//		public void resetContents()
//		{			
//			Control[] controls = getChildren();
//			for(int i = 0; i < controls.length; i++)
//			{
//				controls[i].dispose();
//			}
//			
//			sampleCanvas = getSampleCanvas(this, SWT.NONE);
//
//			if(legend.colorSetObjects != null)
//			{				
//				int i = 0;
//				for(Sample s : colorSet.useSamples)
//				{
//					i++;
//					Label l = new Label(this, SWT.FLAT);
//					l.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
//					l.setText(i + ": " + s.getName());
//				}
//			}
//		}
//		
//		final static int MARGIN = 5;
//		final static int SAMPLE_IMAGE_HEIGHT = GmmlGeneProduct.INITIAL_HEIGHT;
//		
//		private Canvas getSampleCanvas(Composite parent, int style) {
//			final Canvas c = new Canvas(parent, style);
//			
//			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//			gd.heightHint = SAMPLE_IMAGE_HEIGHT + 1;
//			c.setLayoutData(gd);
//			
//			c.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
//			c.addPaintListener(new PaintListener() {
//				public void paintControl(PaintEvent e) {
//					GC gc = e.gc;
//					Font f = new Font(getDisplay(), FONT, FONTSIZE, SWT.NONE);
//					
//					int nr = colorSet.useSamples.size();
//				
//					gc.setFont(f);
//					String exampleId = "Gene";
//					Point stringSize = gc.textExtent(exampleId);
//					
//					Point p = ((Canvas)(e.widget)).getSize();
//					Rectangle drawArea = new Rectangle(0, 0, p.x, p.y);
//					drawArea.width = drawArea.width;
//					drawArea.height = SAMPLE_IMAGE_HEIGHT;
//					
//					Rectangle sampleArea = new Rectangle(0, 0, drawArea.width, drawArea.height);
//					sampleArea.width = (int)Math.ceil(GmmlGpColor.COLOR_AREA_RATIO * drawArea.width);
//					if(nr > 0) sampleArea.width -= sampleArea.width % nr;
//					
//					int stringSpace = drawArea.width - sampleArea.width;
//					
//					if(nr > 0) {
//						//If sample numbers don't fit, steal space from gene label if possible
//						Point sampleSize = gc.textExtent(Integer.toString(nr));
//						int sampleSpace = sampleArea.width / nr;
//						if(sampleSize.x > sampleSpace) { 		
//							if(stringSpace > stringSize.x) {
//								int steal = sampleSize.x * nr - sampleArea.width;
//								if(!(stringSpace - steal > stringSize.x)) steal = stringSpace - stringSize.x;
//								stringSpace -= steal;
//								sampleArea.width += steal;
//							}
//						}
//					}
//										
//					gc.drawString(exampleId, stringSpace / 2 - stringSize.x / 2, sampleArea.height / 2 - stringSize.y / 2 );
//					gc.drawRectangle(0, 0, stringSpace - 1, sampleArea.height);
//					
//					sampleArea.x += stringSpace - 1;
//					for(int i = 0; i < nr; i++)
//					{
//						Rectangle r = new Rectangle(sampleArea.x + i * sampleArea.width / nr,
//								sampleArea.y, sampleArea.width / nr, sampleArea.height);
//						gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
//						gc.drawRectangle(r.x, r.y, r.width, r.height);
//						Point numberSize = gc.textExtent(Integer.toString(i + 1));
//						gc.drawString(Integer.toString(i + 1), r.x + r.width / 2 - numberSize.x / 2,
//								r.height / 2 - numberSize.y / 2, true);
//					}
//
//					f.dispose();
//				}
//			});
//			return c;
//		}
//	}
