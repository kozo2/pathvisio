/*
Copyright 2005 H.C. Achterberg, R.M.H. Besseling, I.Kaashoek, 
M.M.Palm, E.D Pelgrim, BiGCaT (http://www.BiGCaT.unimaas.nl/)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and 
limitations under the License.
*/

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

/**
 * The GmmlWriter class build a jdom document from a given pathway. It also include methods to dump a document to an output.
 */
 
public class GmmlWriter {

	GmmlPathway pathway;
	Document doc;
	Element root;
	
	public GmmlWriter(GmmlPathway output) {
		pathway = output;
		buildDoc();
	}
	
	/**
	  * This method will dump the document to the console as valid xml using the jdom package
	  */
	public void dumpToScreen() {
		try {
			XMLOutputter screendump = new XMLOutputter(Format.getPrettyFormat());
	     	screendump.output(doc, System.out);
   	}
    	catch (IOException e) {
      	System.err.println(e);
    	}
   }
   
   /**
     * This method will write the document to the given file using the jdom package.
     */
     
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
   
   /**
     * This method builds a document from the pathay, used only internally. Maybe make this private? Or make a public overloaded version that requires a pathway input?
     */
     
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
   private void fillRootElement () {
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