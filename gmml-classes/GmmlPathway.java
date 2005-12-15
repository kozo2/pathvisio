import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.JApplet;
import java.awt.geom.Arc2D;

public class GmmlPathway {
	//Pathway
	int[] size = new int[2];
	
	//Geneproduct
	Rectangle[] rects = new Rectangle[0];
	String[] rectText = new String[0];
		
	//Lines
	double[][] lineCoord = new double[0][4];
	int[][] lineLayout = new int[0][2];
	Arc2D.Double[] arcs = new Arc2D.Double[0];
	int[][] rectConnection = new int[0][3];
	
	//Label
	int[][] labelCoord = new int[0][4];
	Color[] labelColor = new Color[0];
	String[][] labelFont = new String[0][3];
	int[] labelFontSize = new int[0];
	String[] labelText = new String[0];
		
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
		// new array is [length+1][4 or Null]
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
	
	public void addLabel(int x, int y, int w, int h, String text, String color, String font, String weight, String style, int fontsize) {
		int length = labelCoord.length;	

		//RESIZE PART
		labelColor = (Color[]) resizeArray(labelColor, (length+1));

		//RESIZE PART
		labelFontSize = (int[]) resizeArray(labelFontSize, (length+1));
		
		//RESIZE PART
		labelText = (String[]) resizeArray(labelText, (length+1));

		//RESIZE PART
		labelCoord = (int[][]) resizeArray(labelCoord, (length+1));
		// new array is [length+1][4 or Null]
  		for (int i=0; i<labelCoord.length; i++) {
			if (labelCoord[i] == null) {
     			labelCoord[i] = new int[4];
			}
		}
		
		//RESIZE PART
		labelFont = (String[][]) resizeArray(labelFont, (length+1));
		// new array is [length+1][3 or Null]
  		for (int i=0; i<labelFont.length; i++) {
			if (labelFont[i] == null) {
     			labelFont[i] = new String[3];
			}
		}
		
		System.out.println("Storing label "+length+" in pathway...");
		labelColor[length] = GmmlColor.convertColor(color);
		labelFontSize[length] = fontsize;
		labelCoord[length][0] = x;
		labelCoord[length][1] = y;
		labelCoord[length][2] = h;
		labelCoord[length][3] = w;
		labelFont[length][0] = font;
		labelFont[length][1] = weight;
		labelFont[length][2] = style;
		labelText[length] = text;
	}
	
	public void addGeneProductText(String gpText) {
		int length = rectText.length;
		
		//RESIZE PART
		rectText = (String[]) resizeArray(rectText, (length+1));
		
		rectText[length] = gpText;
	}
	
	public void addArc(double x, double y, double w, double h) {
		int length = arcs.length;
		
		//RESIZE PART
		arcs = (Arc2D.Double[]) resizeArray(arcs, (length+1));
		
		Arc2D.Double temp = new Arc2D.Double(0);
		temp.setArc(x-w,y-h,2*w,2*h,0,180,0);
		arcs[length] = temp;

	}
	
	public void addBrace(int cX, int cY, int W, int PPO, String Or) {
		//hier komt addBrace.
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

	public void checkConnection(){
	/* for each point of each line the corresponding rectangle is searched
	 * these rectangles are saved in connection
	 * the first point is the first rectangle
	 * the second point is the second rectangle
	 */
	 	int[][] connec = new int[lineCoord.length][2];
	 	int count = 0;
		for (int i=0; i < lineCoord.length; i++){
			double theta=Math.atan(Math.abs((lineCoord[i][3]-lineCoord[i][1])/(lineCoord[i][2]-lineCoord[i][0])));
			double dx=Math.cos(theta);
			double dy=Math.sin(theta);
			boolean test1=false;
			boolean test2=false;
			if (lineCoord[i][0]>lineCoord[i][2]){
				dx=-dx;
			}
			if (lineCoord[i][1]>lineCoord[i][3]){
				dy=-dy;
			}
			for (int j=0; j < rects.length; j++){
				Rectangle temprectj=rects[j];
				for (int n=0; n < 15; n++){
					if (temprectj.contains((lineCoord[i][0]-(n*dx)), (lineCoord[i][1]-(n*dy)))) {
						connec[i][0]=j;
						test1=true;
						n=15;
					}
				}	
				for (int n=0; n < 15; n++){	
					if (temprectj.contains(lineCoord[i][2]+(n*dx), lineCoord[i][3]+(n*dy))){
						connec[i][1]=j;				
						test2=true;
						n=15;
					}
				}
				if (test1 && test2) {
					j = rects.length;
				}
			} //end for loop that searches the rectangles
			if (!test1 || !test2) {
				connec[i][0]=-1;
				connec[i][1]=-1;
				count=count+1;
			}
		}
		int n = 0;
		int[][] tempConnection=new int[connec.length-count][3];
		for (int i = 0; i < connec.length; i++) {
			if (connec[i][0]!=-1 && connec[i][1]!=-1) {
				System.out.println("TEST 1: rectangle " + connec[i][0] + " is connecte to " + connec[i][1] + " by line " + i);
				tempConnection[n][0]= i;
				tempConnection[n][1]= connec[i][0];
				tempConnection[n][2]= connec[i][1];
				n=n+1;		
			}
		}
	rectConnection = tempConnection;
	for (int i = 0; i<rectConnection.length; i++){
		System.out.println("TEST 2: rectangle " + rectConnection[i][1] + " is connected to " + rectConnection[i][2] + " by line " + rectConnection[i][0]);
	}
	}//end of checkconnection
    	
} //end of GmmlPathway
