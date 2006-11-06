package visualization.plugins;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import graphics.GmmlGeneProduct;
import graphics.GmmlGraphics;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import data.GmmlGex;
import data.GmmlGdb.IdCodePair;
import data.GmmlGex.Sample;
import data.GmmlGex.CachedData.Data;

import util.SwtUtils;
import visualization.Visualization;
import visualization.VisualizationManager;
import visualization.colorset.ColorSet;
import visualization.colorset.ColorSetManager;

public class ExpressionColorPlugin extends VisualizationPlugin {
	static final String NAME = "Color by expression";
	
	List<ConfiguredSample> useSamples;
	
	
	public ExpressionColorPlugin(Visualization v) {
		super(v);
		setDisplayOptions(DRAWING);
		setIsConfigurable(true);
		setIsGeneric(false);
		setUseReservedArea(true);
		
		useSamples = new ArrayList<ConfiguredSample>();
	}

	public String getName() {
		return NAME;
	}
	
	public void draw(GmmlGraphics g, PaintEvent e, GC buffer) {
		if(!(g instanceof GmmlGeneProduct)) return;
		if(useSamples.size() == 0) return; //Nothing to draw
		
		GmmlGeneProduct gp = (GmmlGeneProduct) g;
		
		Region region = getVisualization().getReservedRegion(this, g);
		Rectangle area = region.getBounds();
		
		int nr = useSamples.size();
		int w = area.width / nr;
		for(int i = 0; i < nr; i++) {
			Rectangle r = new Rectangle(
					area.x + w * i,
					area.y,
					w, area.height);
			ConfiguredSample s = useSamples.get(i);
			Data data = GmmlGex.getCachedData(new IdCodePair(gp.getID(), gp.getSystemCode()));
			
			if(data == null) {
				drawNoDataFound(s, area, e, buffer);
			} else if(data.hasMultipleData()) {
				switch(s.getAmbigiousType()) {
				case ConfiguredSample.AMBIGIOUS_AVG:
					drawSampleAvg(s, data, r, e, buffer);
					break;
				case ConfiguredSample.AMBIGIOUS_BARS:
//					drawSampleBar(gp, s, e, buffer, r);
					break;
				}
			} else drawSample(s, data, r, e, buffer);
		}
		
		region.dispose();
	}
	
	public void drawNoDataFound(ConfiguredSample s, Rectangle area, PaintEvent e, GC buffer) {
		ColorSet cs = s.getColorSet();
		if(cs != null) {
			drawColoredRectangle(area, cs.getColor(ColorSet.ID_COLOR_NO_DATA_FOUND), e, buffer);
		}
	}
	
	public void drawSample(ConfiguredSample s, Data data, Rectangle area, PaintEvent e, GC buffer) {
		ColorSet cs = s.getColorSet();
		if(cs != null) {
			RGB rgb = cs.getColor(data.getSampleData(), s.getId());
			drawColoredRectangle(area, rgb, e, buffer);
		}
	}
	
	public void drawSampleAvg(ConfiguredSample s, Data data, Rectangle area, PaintEvent e, GC buffer) {
		ColorSet cs = s.getColorSet();
		if(cs != null) {
			RGB rgb = cs.getColor(data.getAverageSampleData(), s.getId());
			drawColoredRectangle(area, rgb, e, buffer);
		}
	}
	
	void drawColoredRectangle(Rectangle r, RGB rgb, PaintEvent e, GC buffer) {
		Color c = null;
		
		c = SwtUtils.changeColor(c, rgb, e.display);
//		buffer.setForeground(e.display.getSystemColor(SWT.COLOR_GRAY));
		buffer.setBackground(c);
		
		buffer.fillRectangle(r);
		buffer.drawRectangle(r);
		
		c.dispose();
	}
	
	void addUseSample(Sample s) {
		if(s != null) {
			useSamples.add(new ConfiguredSample(s));
			fireModifiedEvent();
		}
	}
	
	void addUseSamples(IStructuredSelection selection) {
		Iterator it = selection.iterator();
		while(it.hasNext()) {
			useSamples.add(new ConfiguredSample((Sample)it.next()));
		}
		fireModifiedEvent();
	}
	
	void removeUseSamples(IStructuredSelection selection) {
		Iterator it = selection.iterator();
		while(it.hasNext()) {
			useSamples.remove((ConfiguredSample)it.next());
		}
		fireModifiedEvent();
	}
	
	void removeUseSample(ConfiguredSample s) {
		if(s != null) {
			useSamples.remove(s);
			fireModifiedEvent();
		}
	}
	
	ListViewer useSampleList;
	SampleConfigComposite sampleConfigComp;
	protected Composite createConfigComposite(Composite parent) {
		Composite config = new Composite(parent, SWT.NULL);
		config.setLayout(new GridLayout());
		
		Composite listsComp = createSampleLists(config);
		listsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		sampleConfigComp = new SampleConfigComposite(config, SWT.NULL);
		GridData span = new GridData(GridData.FILL_HORIZONTAL);
		span.horizontalSpan = 2;
		sampleConfigComp.setLayoutData(span);
		return config;
	}
	
	Composite createSampleLists(Composite parent) {
		Composite listsComp = new Composite(parent, SWT.NULL);
		listsComp.setLayout(new GridLayout(3, false));
		
		Label sampleLabel = new Label(listsComp, SWT.NULL);
		sampleLabel.setText("Available samples:");
		GridData span = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		span.horizontalSpan = 2;
		sampleLabel.setLayoutData(span);
		
		Label useSampleLabel = new Label(listsComp, SWT.NULL);
		useSampleLabel.setText("Selected samples");
		useSampleLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		LabelProvider listLabelProvider = new LabelProvider() {
			public String getText(Object element) {
				return ((Sample)element).getName();
			}
		};
		
		final ListViewer sampleList = new ListViewer(listsComp, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		sampleList.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		sampleList.setContentProvider(new ArrayContentProvider());
		sampleList.setLabelProvider(listLabelProvider);
		sampleList.setInput(GmmlGex.getSamples(Types.REAL));
		
		Composite buttons = new Composite(listsComp, SWT.NULL);
		buttons.setLayout(new RowLayout(SWT.VERTICAL));
		final Button add = new Button(buttons, SWT.PUSH);
		add.setText(">");
		final Button remove = new Button(buttons, SWT.PUSH);
		remove.setText("<");
		
		SelectionListener buttonListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(e.widget == add) {
					addUseSamples((IStructuredSelection)sampleList.getSelection());
				} else {
					removeUseSamples((IStructuredSelection)useSampleList.getSelection());
				}
				useSampleList.refresh();
			}
		};
		
		add.addSelectionListener(buttonListener);
		remove.addSelectionListener(buttonListener);
		
		useSampleList = new ListViewer(listsComp, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		useSampleList.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		useSampleList.setContentProvider(new ArrayContentProvider());
		useSampleList.setLabelProvider(listLabelProvider);
		useSampleList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				sampleConfigComp.setInput(getSelectedUseSample());
			}
			
		});
		useSampleList.setInput(useSamples);
		
		return listsComp;
	}
		
	ConfiguredSample getSelectedUseSample() {
		return (ConfiguredSample)
		((IStructuredSelection)useSampleList.getSelection()).getFirstElement();
	}
	
	class SampleConfigComposite extends Composite {
		ConfiguredSample input;
		Combo colorSetCombo;
		Button radioBar;
		Button radioAvg;
		
		public SampleConfigComposite(Composite parent, int style) {
			super(parent, style);
			createContents();
		}
		
		void createContents() {
			setLayout(new FillLayout());
			Group group = new Group(this, SWT.NULL);
			group.setText("Selected sample confguration");
			group.setLayout(new GridLayout());
			
			Composite comboComp = createComboComp(group);
			comboComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			Composite ambComp = createAmbigiousComp(group);
			ambComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			setInput(null);
		}
		
		Composite createComboComp(Composite parent) {
			Composite comboComp = new Composite(parent, SWT.NULL);
		
			comboComp.setLayout(new GridLayout(2, false));
			Label label = new Label(comboComp, SWT.NULL);
			label.setText("Colorset to apply:");
			colorSetCombo = new Combo(comboComp, SWT.READ_ONLY);
			colorSetCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			colorSetCombo.setItems(ColorSetManager.getColorSetNames());
			colorSetCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					changeColorSet(colorSetCombo.getSelectionIndex());
				}
			});
			colorSetCombo.select(0);
			
			return comboComp;
		}
		
		Composite createAmbigiousComp(Composite parent) {
			Group ambGroup = new Group(parent, SWT.NULL);
			ambGroup.setText("How to treat ambigious reporters?");
			
			ambGroup.setLayout(new RowLayout(SWT.VERTICAL));
			radioAvg = new Button(ambGroup, SWT.RADIO);
			radioAvg.setText("Use average value for color");
			radioBar = new Button(ambGroup, SWT.RADIO);
			radioBar.setText("Divide in horizontal bars");
			SelectionListener listener = new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int type;
					if(e.widget == radioAvg) type = ConfiguredSample.AMBIGIOUS_AVG;
					else type = ConfiguredSample.AMBIGIOUS_BARS;
					if(input != null) input.setAmbigiousType(type);
				}
			};
			radioAvg.addSelectionListener(listener);
			radioBar.addSelectionListener(listener);
			radioAvg.setSelection(true);
			
			return ambGroup;
		}
		
		void changeColorSet(int index) {
			if(input != null) 
				input.setColorSetIndex(index);
		}
		
		public void setInput(ConfiguredSample s) {
			input = s;
			refresh();
		}
		
		public void refresh() {
			if(input == null) setAllEnabled(false);
			else {
				setAllEnabled(true);
				colorSetCombo.select(input.getColorSetIndex());
				boolean avg = input.getAmbigiousType() == ConfiguredSample.AMBIGIOUS_AVG;
				radioAvg.setSelection(avg);
				radioBar.setSelection(!avg);
			}
		}
		
		public void setAllEnabled(boolean enable) {
			SwtUtils.setCompositeAndChildrenEnabled(this, enable);
		}
	}
	
	class ConfiguredSample extends Sample {
		public static final int AMBIGIOUS_AVG = 0;
		public static final int AMBIGIOUS_BARS = 1;
		
		int ambigious;
		int colorSetIndex = 0;
		
		public ConfiguredSample(int idSample, String name, int dataType) {
			super(idSample, name, dataType);
		}
		
		public ConfiguredSample(Sample s) {
			super(s.getId(), s.getName(), s.getDataType());
		}
		
		public void setColorSetIndex(int index) { colorSetIndex = index; }
		
		public ColorSet getColorSet() { return ColorSetManager.getColorSet(colorSetIndex); }
		
		public int getColorSetIndex() { return colorSetIndex; }
		
		public int getAmbigiousType() { return ambigious; }
		
		public void setAmbigiousType(int type) { ambigious = type; }
	}
	
	public Composite getToolTipComposite(Composite parent, GmmlGraphics g) { return null; }
	public void createSidePanelComposite(Composite parent) { }
	public void updateSidePanel(GmmlGraphics g) { }

}
