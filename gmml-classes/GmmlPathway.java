import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.image.*;

public class GmmlPathway extends Applet{

	int[][] rectCoord = new int[0][4];
	
	//Attributes + notes element + comment element
	String[][] attributes = new String[0][2];
	String notes = new String();
	String comment = new String();
	
	static protected Label label;

	//init is used to form the applet later in the program.
	public void init(){

   	//an integer array rectcoord is created.
/*		int[][] rectCoord = {
	   	{0,0,100,50},
	   	{150,50,100,50},
		   {0,100,100,50},
		   {150,150,100,50},
			{300,0,100,50}
		}; */
		
		System.out.println("Checking for stored attributes - number: "+attributes.length);
		for(int i=0; i<attributes.length; i++) {
			System.out.println("Attribute name: "+attributes[i][0]+ "value : "+attributes[i][1]);
		}
		
		Color[] rectColors={Color.blue,Color.green,Color.yellow,Color.red,Color.pink};
		//Initialize the layout.
		setLayout(new BorderLayout());
		add(new DrawingCanvas(rectCoord,rectColors)); //Same as add(new DrawingCanvas(), BorderLayout.center); 
		//This label is used when the applet is just started
		label = new Label("Drag rectangles around within the area");
		add("South", label); //South: in the lowest part of the frame.
	} //end of init

	public void setNotes(String n) {
		notes = n;
	}
	
	public void setComment(String c) {
		comment = c;
	}
	
	public void addAttribute(String attribute, String value) {
		System.out.println("Adding attribute at "+attributes.length+" - attr: "+attribute+" - value: "+value);
		int length = attributes.length;
		
		//RESIZE PART
		attributes = (String[][]) resizeArray(attributes, (length+1));
		// new array is [length+1][2 or Null]
  		for (int i=0; i<attributes.length; i++) {
			if (attributes[i] == null) {
     			attributes[i] = new String[2];
			}
		}
		
      // new array is [length+1][2]
   
		System.out.println("New length at "+attributes.length);
		attributes[length][0] = attribute;
		attributes[length][1] = value;
	}
	
	public void addRectCoord(int x, int y, int w, int h) {
		System.out.println("Adding rect nr: "+rectCoord.length+" - x: "+x+" - y: "+y+" - w: "+w+" - h: "+h);
		int length = rectCoord.length;
		
		//RESIZE PART
		rectCoord = (int[][]) resizeArray(rectCoord, (length+1));
		// new array is [length+1][2 or Null]
  		for (int i=0; i<rectCoord.length; i++) {
			if (rectCoord[i] == null) {
     			rectCoord[i] = new int[4];
			}
		}
		
      // new array is [length+1][2]
   
		System.out.println("New length at "+attributes.length);
		rectCoord[length][0] = x;
		rectCoord[length][1] = y;
		rectCoord[length][2] = w;
		rectCoord[length][3] = h;
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
    	
} //end of Drawing
