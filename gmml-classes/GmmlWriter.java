import org.jdom.JDOMException;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.output.XMLOutputter;
import org.jdom.*;
import org.jdom.Element;
import org.jdom.Attribute;
import java.io.IOException;
import java.util.*;
import java.io.FileWriter;
import java.io.File;

public class GmmlWriter {

	GmmlPathway pathway;
	Document doc;
	Element root;
	
	public GmmlWriter(GmmlPathway output) {
		pathway = output;
		buildDoc();
	}
	
	public void dumpToScreen() {
		try {
			XMLOutputter screendump = new XMLOutputter(Format.getPrettyFormat());
	     	screendump.output(doc, System.out);
   	}
    	catch (IOException e) {
      	System.err.println(e);
    	}
   }
   
	public void writeToFile(String filename) {
		try {
			XMLOutputter screendump = new XMLOutputter(Format.getPrettyFormat());
			File file = new File(filename);
			FileWriter writer = new FileWriter(file);
	     	screendump.output(doc, writer);
   	}
    	catch (IOException e) {
      	System.err.println(e);
    	}
   } 
   
   public void buildDoc() {
   	System.out.println("Building document...");
   	root = new Element("Pathway");
	   for (int i = 0; i < pathway.attributes.length; i++) {
	   	root.setAttribute(pathway.attributes[i][0], pathway.attributes[i][1]);
	   }
	   Element graphics = new Element("Graphics");
	   graphics.setAttribute("BoardHeight",Integer.toString(pathway.size[1]));
	   graphics.setAttribute("BoardWidth",Integer.toString(pathway.size[0]));
	   root.addContent(graphics);
	   fillRootElement();
	   doc = new Document(root);
	}
   public void fillRootElement () {
   	for (int i = 0; i < pathway.geneProducts.length; i++) {
	   	Element geneproduct = new Element("GeneProduct");
		   
		   geneproduct.setAttribute("GeneID",pathway.geneProducts[i].geneID);
		   
		   Element graphics = new Element("Graphics");
		   
		   double w, h, x, y, cx, cy;
		   
		   w = pathway.geneProducts[i].width;
		   h = pathway.geneProducts[i].height;
	   	x = pathway.geneProducts[i].x;
		   y = pathway.geneProducts[i].y;
		   cx = x + w/2;
		   cy = y + h/2;
		   
		   graphics.setAttribute("CenterX",Integer.toString((int)cx));
		   graphics.setAttribute("CenterY",Integer.toString((int)cy));
		   graphics.setAttribute("Width",Integer.toString((int)w));
		   graphics.setAttribute("Height",Integer.toString((int)h));
		   
		   geneproduct.addContent(graphics);
		   root.addContent(geneproduct);
	   }
	}

}