import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.JApplet;

public class GmmlPathway {
	//Geneproduct and line coordinates
	Rectangle[] rects = new Rectangle[0];
	double[][] lineCoord = new double[0][4];
	int[][] lineLayout = new int[0][2];
	int[] size = new int[2];
	String[] rectText = new String[0];
	
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
		//System.out.println("Adding attribute at "+attributes.length+" - attr: "+attribute+" - value: "+value);
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
	
	public void addRect(int x, int y, int w, int h) {
		//System.out.println("Adding rect nr: "+rectCoord.length+" - x: "+x+" - y: "+y+" - w: "+w+" - h: "+h);
		int length = rects.length;
		
		//RESIZE PART
		rects = (Rectangle[]) resizeArray(rects, (length+1));
		
		Rectangle temp = new Rectangle(x, y, w, h);
		rects[length] = temp;
	}
	
	public void addLine(double sx, double sy, double ex, double ey, int style, int type) {
		//System.out.println("Adding rect nr: "+lineCoord.length+" - x1: "+sx+" - y1: "+sy+" - x2: "+ex+" - y2: "+ey);
		int length = lineCoord.length;
		
		//RESIZE PART
		lineCoord = (double[][]) resizeArray(lineCoord, (length+1));
		// new array is [length+1][2 or Null]
  		for (int i=0; i<lineCoord.length; i++) {
			if (lineCoord[i] == null) {
     			lineCoord[i] = new double[4];
			}
		}
		
		//RESIZE PART
		lineLayout = (int[][]) resizeArray(lineLayout, (length+1));
		// new array is [length+1][2 or Null]
  		for (int i=0; i<lineLayout.length; i++) {
			if (lineLayout[i] == null) {
     			lineLayout[i] = new int[2];
			}
		}
		
		lineCoord[length][0] = sx;
		lineCoord[length][1] = sy;
		lineCoord[length][2] = ex;
		lineCoord[length][3] = ey;
		lineLayout[length][0] = style;
		lineLayout[length][1] = type;
	}
	
	public void addGeneProductText(String GPText) {
		int length = rectText.length;
		
		//RESIZE PART
		rectText = (String[]) resizeArray(rectText, (length+1));
		
		rectText[length] = GPText;
	}
	
	public void setSize(int h, int w) {
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
    	
} //end of GmmlPathway
