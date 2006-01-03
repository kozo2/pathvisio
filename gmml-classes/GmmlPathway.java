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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.JApplet;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D.*;
import java.awt.geom.AffineTransform;

public class GmmlPathway {
	//Pathway
	int[] size = new int[2];
	
	//Geneproduct
	GmmlGeneProduct[] geneProducts = new GmmlGeneProduct[0];
		
	//Lines
	GmmlLine[] lines = new GmmlLine[0];
	
	//Lineshapes
	GmmlLineShape[] lineshapes = new GmmlLineShape[0];

	//Label
	GmmlLabel[] labels = new GmmlLabel[0];
	
	//Arc
	GmmlArc[] arcs = new GmmlArc[0];

	//Brace
	GmmlBrace[] braces = new GmmlBrace[0];

	//Shape
	GmmlShape[] shapes = new GmmlShape[0];
		
	//Attributes + notes element + comment element
	String[][] attributes = new String[0][2];
	String notes = new String();
	String comment = new String();

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public void addAttribute(String attribute, String value) {
		int length = attributes.length;
		
		//RESIZE PART
		attributes = (String[][]) resizeArray(attributes, (length+1));
		// new array is [length+1][2 or Null]
  		for (int i=0; i<attributes.length; i++) {
			if (attributes[i] == null) {
     			attributes[i] = new String[2];
			}
		}
		
		attributes[length][0] = attribute;
		attributes[length][1] = value;
	}
	
	public void addGeneProduct(int x, int y, int width, int height, String geneID, String ref) {
		int length = geneProducts.length;
				
		//Resize part
		geneProducts = (GmmlGeneProduct[]) resizeArray(geneProducts, (length+1));
		GmmlGeneProduct temp = new GmmlGeneProduct(x,y,width,height,geneID,ref);
		geneProducts[length]=temp;
	}
	
	public void addLine(double startx, double starty, double endx, double endy, int type, int style, String colorstring) {
		int length = lines.length;
		
		//RESIZE PART
		lines = (GmmlLine[]) resizeArray(lines, (length+1));
		Color color = GmmlColor.convertStringToColor(colorstring);
		lines[length] = new GmmlLine(startx, starty, endx, endy, style, type, color);
	}
	
	public void addLabel(int x, int y, int width, int height, String text, String color, String font, String weight, String style, int fontsize) {
		int length = labels.length;	

		//RESIZE PART
		labels = (GmmlLabel[]) resizeArray(labels, (length+1));

		labels[length] = new GmmlLabel(x, y, width, height, text, font, weight, style, fontsize, GmmlColor.convertStringToColor(color));
	}
	
	public void addArc(double x, double y, double width, double height, String color) {
		int length = arcs.length;
		
		//RESIZE PART
		arcs = (GmmlArc[]) resizeArray(arcs, (length+1));
		GmmlArc temp = new GmmlArc(x, y, width, height, color);
		arcs[length]=temp;
	}

	public void addLineShape(double startx, double starty, double endx, double endy, String scolor, int type) {
		int length = lineshapes.length;
		
		//RESIZE PART
		lineshapes = (GmmlLineShape[]) resizeArray(lineshapes, (length+1));
		Color color = GmmlColor.convertStringToColor(scolor);
		lineshapes[length] = new GmmlLineShape(startx, starty, endx, endy, type, color);
	}
	
	public void addBrace(double centerX, double centerY, double width, double ppo, int orientation, String color) {
		int length = braces.length;
				
		//Resize part
		braces = (GmmlBrace[]) resizeArray(braces, (length+1));
		braces[length] = new GmmlBrace(centerX,centerY,width,ppo,orientation,color);

	}

	public void addCellShape(double x, double y, double width, double height, double rotation) {
		//hier komt addCellShape
	}
	
	public void addCellComponent(double centerX, double centerY, int type) {
		//hier komt addCellComponent
	}
	
	public void addProteinComplex(double centerX, double centerY, int type) {
		//hier komt addCellComponent
	}		
	
	public void addShape(double x, double y, double width, double height, int type, String color, double rotation) {
		int length = shapes.length;
		
		//RESIZE PART
		shapes = (GmmlShape[]) resizeArray(shapes, (length+1));
		shapes[length] = new GmmlShape(x, y, width, height, type, color, rotation);
	}
	
	public void setSize(int width, int height) {
		size[0] = width;
		size[1] = height;
	}
	public void echoAtt() {
		System.out.println("Checking for stored attributes - number: "+attributes.length);
		for(int i=0; i<attributes.length; i++) {
			System.out.println("Attribute name: "+attributes[i][0]+ "value : "+attributes[i][1]);
      }
   }

    private static Object resizeArray (Object oldArray, int newSize) {
      int oldSize = java.lang.reflect.Array.getLength(oldArray);
      Class elementType = oldArray.getClass().getComponentType();
      Object newArray = java.lang.reflect.Array.newInstance(elementType,newSize);
      int preserveLength = Math.min(oldSize,newSize);
      if (preserveLength > 0)
          System.arraycopy (oldArray,0,newArray,0,preserveLength);
    	return newArray; 
	 }
	 
//	 public void echoConnections() {
//	 	System.out.println("Coord:");
//	   System.out.print("{{"+(int)lineCoord[0][0]+","+(int)lineCoord[0][1]+"}");
//		for(int i=1; i<lineCoord.length; i++) {
//		System.out.print(",{"+(int)lineCoord[i][0]+","+(int)lineCoord[i][1]+"}");
//		}
//		System.out.println("}");
//		
//	 	System.out.println("Link:");
//	   System.out.print("{{"+rectConnection[0][0]+","+rectConnection[0][1]+"}");
//	 	for(int i=1; i<rectConnection.length; i++) {
//		 System.out.print(",{"+rectConnection[i][0]+","+rectConnection[i][1]+"}");
//		}
//		System.out.println("}");
//		
//	 }

	
} //end of GmmlPathway
