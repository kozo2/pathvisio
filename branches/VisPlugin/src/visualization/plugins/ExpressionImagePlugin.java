package visualization.plugins;

import gmmlVision.GmmlVision;
import graphics.GmmlGraphics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.jdom.Element;

import util.SwtUtils;
import visualization.Visualization;
import visualization.colorset.ColorSet;
import data.GmmlGex.Sample;
import data.GmmlGex.CachedData.Data;

public class ExpressionImagePlugin extends ExpressionColorPlugin {
	static final String NAME = "Colored image";
	static final String DESCRIPTION = 
		"This plugin displays one or more images on Gene Product objects and \n" +
		"colors the image(s) accoring to the expression value of the Gene Product.";
		
	static final RGB DEFAULT_TRANSPARENT = GmmlVision.TRANSPARENT_COLOR;
		
	List<URL> imageURLs = new ArrayList<URL>(Arrays.asList(new URL[] {
			GmmlVision.getResourceURL("images/protein.bmp"),
			GmmlVision.getResourceURL("images/mRNA.bmp") }));
	
	public ExpressionImagePlugin(Visualization v) {
		super(v);
		setDisplayOptions(DRAWING);
		setIsConfigurable(true);
		setIsGeneric(false);
		setUseProvidedArea(true);
	}

	public String getName() { return NAME; }
	public String getDescription() { return DESCRIPTION; }
	
	List<URL> getImageURLs() { return imageURLs; }
	
	void addImageURL(URL url) {
		if(!imageURLs.contains(url))imageURLs.add(url);
	}
	
	void removeImageURL(URL url) {
		if(url.getProtocol().equals("file")) imageURLs.remove(url);
	}
	
	protected void drawSample(ConfiguredSample s, Data data, Rectangle area, PaintEvent e, GC buffer) {
		if(data == null) return;
		ColorSet cs = s.getColorSet();

		RGB rgb = cs.getColor(data.getAverageSampleData(), s.getId());
		
		ImageSample is = (ImageSample)s;
		ImageData id = is.getImageData(new Point(area.width, area.height), rgb);
		Image image = new Image(e.display, id);
		
		buffer.drawImage(image, area.x, area.y);
		
		image.dispose();
	}
		
	protected ConfiguredSample createConfiguredSample(Sample s) {
		return new ImageSample(s);
	}
	
	protected ConfiguredSample createConfiguredSample(Element xml) throws Exception {
		return new ImageSample(xml);
	}
	
	Composite createOptionsComp(Composite parent) {
		return new Composite(parent, SWT.NULL);
	}
		
	protected SampleConfigComposite createSampleConfigComp(Composite parent) {
		return new ImageConfigComposite(parent, SWT.NULL);
	}
	
	protected class ImageConfigComposite extends SampleConfigComposite {		
		ListViewer imageList;
		CLabel colorLabel, imageLabel;
		Color replaceColor;
		Image image;
		Button aspectButton;
		
		public ImageConfigComposite(Composite parent, int style) {
			super(parent, style);
			createContents();
		}
		
		ImageSample getInput() {
			return (ImageSample)input;
		}
		
		void createContents() {
			setLayout(new FillLayout());
			Group group = new Group(this, SWT.NULL);
			group.setText("Image to display for this sample");
			group.setLayout(new GridLayout(2, false));
			
			Composite listComp = createListComp(group);
			listComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			Composite imageComp = createImageComp(group);
			imageComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			imageList.setInput(getImageURLs());
			setInput(null);
		}
		
		Composite createListComp(Composite parent) {
			Composite listComp = new Composite(parent, SWT.NULL);
			listComp.setLayout(new GridLayout());
			
			imageList = new ListViewer(listComp, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			imageList.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
			imageList.setContentProvider(new ArrayContentProvider());
			imageList.setLabelProvider(new LabelProvider() {
				public String getText(Object element) {
					return ((URL)element).toString();
				}
			});
			imageList.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					URL url = (URL)((IStructuredSelection)event.getSelection()).getFirstElement();
					getInput().setURL(url);
					refreshImage();
				}
			});
			
			Composite buttonComp = new Composite(listComp, SWT.NULL);
			buttonComp.setLayout(new RowLayout());
			final Button add = new Button(buttonComp, SWT.PUSH);
			add.setText("Add image...");
			final Button remove = new Button(buttonComp, SWT.PUSH);
			remove.setText("Remove image");
			
			SelectionAdapter buttonAdapter = new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if(e.widget == add) addImage();
					else removeImage();
				}
			};
			remove.addSelectionListener(buttonAdapter);
			add.addSelectionListener(buttonAdapter);
			return listComp;
		}

		Composite createImageComp(Composite parent) {
			Group imageGroup = new Group(parent, SWT.NULL);
			imageGroup.setLayout(new GridLayout());
			imageGroup.setText("Selected image");
			
			imageLabel = new CLabel(imageGroup, SWT.CENTER);
			GridData grid = new GridData(GridData.FILL_BOTH);
			grid.heightHint = grid.widthHint = 50;
			imageLabel.setLayoutData(grid);
			imageLabel.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					refreshImage();
				}
			});
			Composite buttons = new Composite(imageGroup, SWT.NULL);
			buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			buttons.setLayout(new GridLayout(3, false));
			
			aspectButton = new Button(buttons, SWT.CHECK);
			GridData span = new GridData();
			span.horizontalSpan = 3;
			aspectButton.setLayoutData(span);
			aspectButton.setText("Maintain aspect ratio");
			aspectButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					getInput().setMaintainAspect(aspectButton.getSelection());
					refreshImage();
				}
			});
						
			colorLabel = new CLabel(buttons, SWT.NULL);
			colorLabel.setLayoutData(SwtUtils.getColorLabelGrid());
			Button colorButton = new Button(buttons, SWT.PUSH);
			colorButton.setText("...");
			colorButton.setLayoutData(SwtUtils.getColorLabelGrid());
			colorButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					changeColorLabel();
				}
			});
			
			Label label = new Label(buttons, SWT.WRAP);
			label.setText("Color to replace with expression data color");
			return imageGroup;
		}
		
		void changeColorLabel() {
			ColorDialog cd = new ColorDialog(getShell());
			cd.setRGB(getInput().getReplaceColor());
			RGB rgb = cd.open();
			if(rgb != null) {
				getInput().setReplaceColor(rgb);
				setColorLabel();
			}
		}
		
		void setColorLabel() {
			RGB rgb = getInput().getReplaceColor();
			replaceColor = SwtUtils.changeColor(replaceColor, rgb, getDisplay());
			colorLabel.setBackground(replaceColor);
			refreshImage();
		}
		
		void refreshImage() {
			if(input == null) {
				image = null;
			} else {
				Point size = imageLabel.getSize();
				ImageData imgd = null;
				if(size.x > 0 && size.y > 0) {
					int b = 8;
					size.x -= size.x > b ? b : 0; 
					size.y -= size.y > b ? b : 0; 
					imgd = getInput().getImageData(size, imageLabel.getBackground().getRGB());
				}
				image = SwtUtils.changeImage(image, imgd, getDisplay());
			}
			imageLabel.setImage(image);
		}
		
		void addImage() {
			FileDialog fd = new FileDialog(getShell());
			String fn = fd.open();
			if(fn == null) return;
			try {
				new ImageData(fn);
				addImageURL(new File(fn).toURL());
				imageList.refresh();
			} catch(Exception e) {
				MessageDialog.openError(getShell(), "Unable to open image file", e.toString());
				GmmlVision.log.error("Unable to load image", e);
			}
		}
		
		void removeImage() {
			URL url = (URL)((IStructuredSelection)imageList.getSelection()).getFirstElement();
			removeImageURL(url);
		}
		
		public void setInput(ConfiguredSample s) {
			input = s;
			refresh();
		}
		
		public void refresh() {
			if(input == null) {
				setAllEnabled(false);
				refreshImage();
			}
			else {
				setAllEnabled(true);
				URL url = getInput().getURL();
				if(url != null) imageList.setSelection(new StructuredSelection(url));
				else imageList.setSelection(new StructuredSelection(imageList.getElementAt(0)));
				aspectButton.setSelection(getInput().getMaintainAspect());
				setColorLabel();
			}
		}
		
		public void setAllEnabled(boolean enable) {
			SwtUtils.setCompositeAndChildrenEnabled(this, enable);
		}
		
		public void dispose() {
			if(replaceColor != null && !replaceColor.isDisposed()) replaceColor.dispose();
			if(image != null && !image.isDisposed()) image.dispose();
			super.dispose();
		}
	}
	
	protected class ImageSample extends ConfiguredSample {
		ImageData cacheImageData;
		URL imageURL = imageURLs.get(0);
		RGB replaceColor = DEFAULT_TRANSPARENT;
		boolean aspectRatio = true;
		
		public ImageSample(int idSample, String name, int dataType) {
			super(idSample, name, dataType);
		}
		
		public ImageSample(Sample s) {
			super(s.getId(), s.getName(), s.getDataType());
		}
		
		public ImageSample(Element xml) throws Exception {
			super(xml);
		}
		
		public void setURL(URL url) { 
			imageURL = url;
			cacheImageData = null;
		}
		
		public URL getURL() { return imageURL; }
		
		public void setReplaceColor(RGB rgb) { if(rgb != null) replaceColor = rgb; }
		public RGB getReplaceColor() { return replaceColor; }
		public void setMaintainAspect(boolean maintain) { aspectRatio = maintain; }
		public boolean getMaintainAspect() { return aspectRatio;}
		
		public ImageData getImageData() {
			if(imageURL == null) return null;
			if(cacheImageData == null) {
				InputStream in = getInputStream(imageURL);
				cacheImageData = new ImageData(in);
			}
			return cacheImageData;
		}
		
		public ImageData getImageData(Point size) {
			return getImageData(size, null);
		}
		
		public ImageData getImageData(Point size, RGB replaceWith) {
			ImageData img = getImageData();
			if(img == null) return null;
			
			if(replaceWith != null) img = doReplaceColor(img, replaceWith);
			
			if(aspectRatio) {
				double r = (double)img.height / img.width;
				int min = Math.min(size.x, size.y);
				if(min == size.x) size.y = (int)(min * r);
				else size.x = (int)(min * r);
			}
			img = img.scaledTo(size.x, size.y);
			return img;
		}
		
		ImageData doReplaceColor(ImageData img, RGB replaceWith) {
			PaletteData pd = img.palette;
			if(pd.isDirect) {
				int np = pd.getPixel(replaceWith);
				int op = pd.getPixel(getReplaceColor());
				for(int x = 0; x < img.width; x++)
					for(int y = 0; y < img.height; y++)
						if(img.getPixel(x, y) == op) img.setPixel(x, y, np);
			} else {
				RGB[] rgbs = pd.getRGBs();
				int index = -1;
				for(int i = 0; i < rgbs.length; i++) {
					if(rgbs[i].equals(getReplaceColor())) {
						index = i;
						break;
					}
				}
				if(index < 0) return img;
				rgbs[index] = replaceWith;
				img.palette = new PaletteData(rgbs);
			}
			return img;
		}
		
		InputStream getInputStream(URL url) {
			try {
				URLConnection con = url.openConnection();
				return con.getInputStream();
			} catch(IOException e) {
				GmmlVision.log.error("Unable to open connection to image", e);
			}
			return null;
		}
		
	}
	
	public Composite getToolTipComposite(Composite parent, GmmlGraphics g) { return null; }
	public void createSidePanelComposite(Composite parent) { }
	public void updateSidePanel(GmmlGraphics g) { }

}
