package gmmlVision;
import graphics.GmmlDrawingObject;
import graphics.GmmlGeneProduct;
import graphics.GmmlSelectionBox;
import graphics.GmmlSelectionBox.SelectionEvent;
import graphics.GmmlSelectionBox.SelectionListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import data.GmmlGdb;
import data.GmmlGex;
import data.GmmlGdb.IdCodePair;

/**
 * Backpage browser - side panel that shows the backpage information when a GeneProduct is double-clicked
 */
public class GmmlBpBrowser extends Composite implements SelectionListener {
	/**
	 * Directory containing HTML files needed to display the backpage information
	 */
	final static String BPDIR = "backpage";
	/**
	 * Header file, containing style information
	 */
	final static String HEADERFILE = "header.html";
	
	/**
	 * Header for the gene information in HTML format
	 */
	final static String bpHeader = "<H1>Gene information</H1><P>";
	/**
	 * Header for the expression information in HTML format
	 */
	final static String gexHeader = "<H1>Expression data</H1><P>";
	
	private String bpText;
	private String gexText;
	private String header;
	
	private Browser bpBrowser;
	
	private GmmlGeneProduct geneProduct;
	
	/**
	 * Constructor for this class
	 * @param parent	Parent {@link Composite} for the {@Browser} widget
	 * @param style		Style for the {@Browser} widget
	 */
	public GmmlBpBrowser(Composite parent, int style) {
		super(parent, style);
		
		initializeHeader(); //Load the header including style information
		setLayout(new FillLayout());
		bpBrowser = new Browser(this, style); //Set the Browser widget
		setGeneText(null);
		setGexText(null);
		
		GmmlSelectionBox.addListener(this);
	}
	
	public void setGeneProduct(final GmmlGeneProduct gp) 
	{ 
		if(geneProduct == gp) return;
		
		Thread fetchThread = new Thread() {
			public void run() {
				geneProduct = gp;
				if(gp == null) {
					setGeneText(null);
					setGexText(null);
					return;
				}
				// Get the backpage text
				String geneHeader = geneProduct.getGmmlData().getBackpageHead();
				String geneId = geneProduct.getID();
				String systemCode = geneProduct.getSystemCode();
				String bpText = geneHeader.equals("") ? geneHeader : "<H2>" + geneHeader + "</H2><P>";
				String bpInfo = GmmlGdb.getBpInfo(geneId, systemCode);
				bpText += bpInfo == null ? "<I>No gene information found</I>" : bpInfo;
				String crossRefText = getCrossRefText(geneId, systemCode);
				String gexText = GmmlGex.getDataString(new IdCodePair(geneId, systemCode));
				if (bpText != null) 	setGeneText(bpText);
				if (gexText != null)	setGexText(gexText + crossRefText);
				else 					setGexText("<I>No expression data found</I>");
			}
		};
		
		//Run in seperate thread so that this method can return
		fetchThread.start();
	}
		
	public String getCrossRefText(String id, String code) {
		List<IdCodePair> crfs = GmmlGdb.getCrossRefs(id, code);
		if(crfs.size() == 0) return "";
		StringBuilder crt = new StringBuilder("<H1>Cross references</H1><P>");
		for(IdCodePair cr : crfs)
			crt.append(cr.getId() + ", " + cr.getCode() + "<br>");
		return crt.toString();
	}
	
	/**
	 * Sets the text for the Gene information part of the browser. Will be prepended by a paragraph
	 * header as defined in {@link bpHeader}
	 * @param bpText	Text to display in HTML format
	 */
	public void setGeneText(String bpText) {
		if(bpText == null) { //In case no information has to be displayed
			this.bpText = bpHeader + "<I>No gene selected</I>";
		} else {
			this.bpText = bpHeader + bpText;
		}
		refresh();
	}
	
	/**
	 * Sets the text for the expression part of the browser. Will be prepended by a paragraph
	 * header as defined in {@link gexHeader}
	 * @param gexText	Text to display in HTML format
	 */
	public void setGexText(String gexText) {
		if(gexText != null) { //In case no information has to be displayed
			this.gexText = gexHeader + gexText;
		} else {
			this.gexText = "";
		}
		refresh();
	}
	
	/**
	 * Refreshes the text displayed in the browser
	 */
	public void refresh() {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				bpBrowser.setText(header + bpText + gexText);	
			}
		});
	}
	
	/**
	 * Reads the header of the HTML content displayed in the browser. This header is displayed in the
	 * file specified in the {@link HEADERFILE} field
	 */
	private void initializeHeader() {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(
						GmmlVision.getResourceURL(BPDIR + "/" + HEADERFILE).openStream()));
			String line;
			header = "";
			while((line = input.readLine()) != null) {
				header += line.trim();
			}
		} catch (Exception e) {
			GmmlVision.log.error("Unable to read header file for backpage browser: " + e.getMessage(), e);
		}
	}

	public void drawingEvent(SelectionEvent e) {
		switch(e.type) {
		case SelectionEvent.OBJECT_ADDED:
			//Just take the first GeneProduct in the selection
			for(GmmlDrawingObject o : e.selection) {
				if(o instanceof GmmlGeneProduct) {
					if(geneProduct != o) setGeneProduct((GmmlGeneProduct)o);
					break; //Selects the first, TODO: use setGmmlDataObjects
				}
			}
			break;
		case SelectionEvent.OBJECT_REMOVED:
			if(e.selection.size() != 0) break;
		case SelectionEvent.SELECTION_CLEARED:
			setGeneProduct(null);
			break;
		}
	}
}
