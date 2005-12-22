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

	//Label
	GmmlLabel[] labels = new GmmlLabel[0];
	
	//Arc
	GmmlArc[] arcs = new GmmlArc[0];

	//Shape
	double[][] shapeCoord = new double[0][5];
	Color[] shapeColor = new Color[0];
	int[] shapeType = new int[0];
		
	//Attributes + notes element + comment element
	String[][] attributes = new String[0][2];
	String notes = new String();
	String comment = new String();

	public void setNotes(String n) {
		notes = n;
	}
	
	public void setComment(String c) {
		comment = c;
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
	
	public void addGeneProduct(int x, int y, int width, int height, String color, String geneID, String ref) {
		int length = geneProducts.length;
				
		//Resize part
		geneProducts = (GmmlGeneProduct[]) resizeArray(geneProducts, (length+1));
		GmmlGeneProduct temp = new GmmlGeneProduct(x,y,width,height,color,geneID,ref);
		geneProducts[length]=temp;
	}
	
	public void addLine(double sx, double sy, double ex, double ey, int style, int type, String scolor) {
		int length = lines.length;
		
		//RESIZE PART
		lines = (GmmlLine[]) resizeArray(lines, (length+1));
		Color color = GmmlColor.convertColor(scolor);
		lines[length] = new GmmlLine(sx, sy, ex, ey, style, type, color);
	}
	
	public void addLabel(int x, int y, int w, int h, String text, String color, String font, String weight, String style, int fontsize) {
		int length = labels.length;	

		//RESIZE PART
		labels = (GmmlLabel[]) resizeArray(labels, (length+1));

		labels[length] = new GmmlLabel(x, y, w, h, text, font, weight, style, fontsize, GmmlColor.convertColor(color));
	}
	
	public void addArc(double x, double y, double w, double h) {
		int length = arcs.length;
		
		//RESIZE PART
		arcs = (GmmlArc[]) resizeArray(arcs, (length+1));
		GmmlArc temp = new GmmlArc(x,y,w,h);
		arcs[length]=temp;
	}

	public void addLineShape(double sx, double sy, double ex, double ey) {
		//hier komt addLineShape
	}
	
	public void addBrace(int cX, int cY, int W, int PPO, String Or) {
		//hier komt addBrace.
	}

	public void addCellShape(double x, double y, double w, double h, double rotation) {
		//hier komt addCellShape
	}
	
	public void addCellComponent(double cx, double cy, int type) {
		//hier komt addCellComponent
	}
	
	public void addProteinComplex(double cx, double cy, int type) {
		//hier komt addCellComponent
	}		
	
	public void addShape(double x, double y, double w, double h, int type, String color, double rotation) {
		int length = shapeCoord.length;
		
		//RESIZE PART
		shapeCoord = (double[][]) resizeArray(shapeCoord, (length+1));
		// new array is [length+1][3 or Null]
  		for (int i=0; i<shapeCoord.length; i++) {
			if (shapeCoord[i] == null) {
     			shapeCoord[i] = new double[5];
			}
		}
		shapeColor = (Color[]) resizeArray(shapeColor, (length+1));
		shapeType = (int[]) resizeArray(shapeType, (length+1));
		
		shapeCoord[length][0] = x;
		shapeCoord[length][1] = y;
		shapeCoord[length][2] = w;
		shapeCoord[length][3] = h;
		shapeCoord[length][4] = rotation;
		shapeColor[length] = GmmlColor.convertColor(color);
		shapeType[length] = type;
	}
	
	public void setSize(int w, int h) {
		size[0] = w;
		size[1] = h;
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
