import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.image.*;
import java.awt.geom.*;

public class TestDrawingCanvas extends Canvas implements MouseListener, MouseMotionListener {
	RectArray ra;
	BufferedImage bi;
	Graphics2D big;
	Color[] rectcolors;
	//Color[] linecolors;
	double[][] layoutarray={{1,2},{2,1},{1,1},{2,2},{2,2},{1,1},{1,2},{2,1}};
	
	//Holds the coordinates of the user's last mousePressed event.
	int[] lastx;
	int[] lasty;
	
	//length of Coordlength = amount of rectangles
	int rectCoordLength;
	boolean[] rectClick;
	
	//Array with lines
	double[][] lineCoord;

	boolean firstTime = true;
	TexturePaint fillColor;
	Rectangle area; //area in which the rectangles are plotted.
	
	
	DrawingCanvas(int[][] rectCoord, Color[] rectColors, double[][] lines) {
		//to use the colors outside of the constructor
		rectcolors = new Color[rectColors.length];
		//linecolors = new Color[lineColors.length];
		for (int i=0; i<rectColors.length; i++){
			rectcolors[i]=rectColors[i];
		}
		//for (int i=0; i<lineColors.lengthe; i++){
		//	linecolors[i]=lineColors[i];
		//}
		//Creates the broken stroke pattern
		//bi = new BufferderImage(2, 2, BufferedImage.TYPE_INT_RGB);
		//big = bi.createGraphics;
		//big.setColor(Color.black);
		//big.fillRect(0, 0, 2, 2);
		//big.setColor(Color.white);
		//big.fillRect(0, 0, 2, 2);
		//r = new Rectangle(0, 0, 2, 2);
		//strokeBroken = new TexturePaint(bi, r);
		//big.dispose();
		
		//to use the length of rectCoord outside of the constructor
		rectCoordLength = rectCoord.length;
		System.out.println("There are "+rectCoordLength+" rectangles");
		rectClick = new boolean[rectCoordLength];
		//to use lines outside of the contructor
		lineCoord = lines;
		ra = new RectArray(rectCoord);
		
		setBackground(Color.white);
		addMouseMotionListener(this);
		addMouseListener(this);
		
		lastx = new int[rectCoordLength];
		lasty = new int[rectCoordLength];
		
	} //end of DrawingCanvas()
		
	/*When the mouse is pressed, there is checked with a for-loop if one clicked inside of a rectangle.
	 *If that is not the case, pressOut is true. If one clicks in a rectangle, the mouseEvent and the
	 *number of the rectangle are being sent to updateLocation.
	 *in rectClickArray, the rects in which there was clicked are true.
	 */
	 
	boolean pressOut = false; //true when one pressed or dragged or released outside the rectangles, false otherwise.		
	
	public void mousePressed(MouseEvent e){
		pressOut = false;
		int CS = 0;
		for (int i=0; i<rectCoordLength; i++) {
			if(ra.rects[i].contains(e.getX(), e.getY())){ //if the user presses the mouses on a coordinate which is contained by rect
				updateLocation(i,e);
				rectClick[i]=true;
				
				lastx[i] = ra.rects[i].x - e.getX(); //last_x = position ra.rects[i] - position mouse when pressed
				lasty[i] = ra.rects[i].y - e.getY();
			}
			else {
				CS++; //counter for in how many rectangles the mouse was not clicked.
				rectClick[i]=false;
			}
		}
		if (CS==rectCoordLength) { //if CS is the same as the amount of rectangles, one didn't press inside a rectangle.
			pressOut = true;
		}
				
	} //end of mousePressed
	
	public void mouseDragged(MouseEvent e){
		for (int i=0; i<rectCoordLength; i++) {
			if(!pressOut && rectClick[i]){ //always mousePressed before mouseDragged -> pressOut true when start dragging outside of rect.
			 	updateLocation(i,e);
			} 
			else {  
				GmmlPathway.label.setText("First position the cursor on the rectangle and then drag.");
			}
		}
	} //end of mouseDragged
	
	// Handles the event of a user releasing the mouse button. Sets pressOut back on false.
	public void mouseReleased(MouseEvent e){

		// Checks whether or not the cursor is inside of the rectangle when the user releases the mouse button.   
		for (int i=0; i<rectCoordLength; i++) {
			if(ra.rects[i].contains(e.getX(), e.getY())){
				updateLocation(i,e);
			} 	else {
				GmmlPathway.label.setText("First position the cursor on the rectangle and then drag.");
			}	
		}
	} //end of mouseReleased
	
	// This method required by MouseListener, it does nothing
	public void mouseMoved(MouseEvent e){}

   // These methods are required by MouseMotionListener, they do nothing
	public void mouseClicked(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	
	//updateLocation
	public void updateLocation(int i, MouseEvent e){
		ra.rects[i].setLocation(lastx[i] + e.getX(), lasty[i] + e.getY());
                /*
                 * Updates the label to reflect the location of the
                 * current rectangle 
                 * if checkrect2 returns true; otherwise, returns error message.
                 */
                if (checkRect(i)) { //true if rect is in area, false if rect is not in area, rect is put back into area
                   GmmlPathway.label.setText("Rectangle "+i+" located at " +
                                                     ra.rects[i].getX() + ", " +
                                                    ra.rects[i].getY());
             } else {
                    GmmlPathway.label.setText("Please don't try to "+
                                                    " drag outside the area.");
                }
		repaint(); //The component will be repainted after all of the currently pending events have been dispatched
	}
	
	public void paint(Graphics g){
		update(g);
	}

	public void update(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
                 
			if(firstTime){
				Dimension dim = getSize(); //Size of frame f
				int w = dim.width;
				int h = dim.height;
				area = new Rectangle(dim);
				bi = (BufferedImage)createImage(w, h);
				big = bi.createGraphics();
				big.setStroke(new BasicStroke(8.0f));
				firstTime = false;
			} 

		//Clears the rectangle that was previously drawn.
		big.setColor(Color.white);
		big.clearRect(0, 0, area.width, area.height);

		//Draws lines
		big.setColor(Color.black);
		
		for (int i=0; i<layoutarray.length-1; i++) {
			//System.out.println("i is" + i);
			//System.out.flush;
			float[] dash = {10.0f};
			if (layoutarray[i][0]==1) {
				big.setStroke(new BasicStroke(2.0f));
			}
			else if (layoutarray[i][0]==2){ 
			big.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			}
			big.draw(new Line2D.Double(lineCoord[i][0],lineCoord[i][1],lineCoord[i][2],lineCoord[i][3]));
		}
		
		for (int i=0; i<layoutarray.length-1; i++) {
			double[] p = new double[2];
			double[] q = new double[2];
			double a;
			double b;
			double normp;
			double normq;
			big.setStroke(new BasicStroke(2.0f));
			//if (lineCoord[i][5]==2) {
			if (layoutarray[i][1]==2) {
				a = lineCoord[i][2];
				b = lineCoord[i][3];
				normp = 1/(Math.sqrt((-a+b)*(-a+b)+(-a-b)*(-a-b)));
				normq = 1/(Math.sqrt((-a-b)*(-a-b)+(-b+a)*(-b+a)));
				p[0] = normp*(-a+b);
				p[1] = normp*(-a-b);
				q[0] = normq*(-a-b);
				q[1] = normq*(-b+a);
				big.draw(new Line2D.Double(p[0],p[1],q[0],q[1]));
			}
		}
		
		// Draws and fills the newly positioned rectangle to the buffer.
		for (int i=0; i<rectCoordLength; i++) {
			big.setColor(Color.blue);
			big.setStroke(new BasicStroke(2.0f));
			big.draw(ra.rects[i]);
			//big.setColor(rectcolors[i]);
			big.setColor(Color.orange);
			big.fill(ra.rects[i]);
		}
		
		// Draws the buffered image to the screen.
		g2.drawImage(bi, 0, 0, this);
		
	} //end of update
	
	/*
    * Checks if the rectangle is contained within the applet window.  If the rectangle
    * is not contained withing the applet window, it is redrawn so that it is adjacent
    * to the edge of the window and just inside the window.
	 */
	 
	boolean checkRect(int i){
		if (area == null) {
		return false;
		}
		if(area.contains(ra.rects[i].x, ra.rects[i].y, ra.rects[i].width, ra.rects[i].height)){
		return true;
		}		
	
		int new_x = ra.rects[i].x;
		int new_y = ra.rects[i].y;

		if((ra.rects[i].x+ra.rects[i].width)>area.width){
			new_x = area.width-ra.rects[i].width+1;
		}
		if(ra.rects[i].x < 0){  
			new_x = -1;
		}
		if((ra.rects[i].y+ra.rects[i].height)>area.height){
			new_y = area.height-ra.rects[i].height+1; 
		}
		if(ra.rects[i].y < 0){  
			new_y = -1;
		}
		ra.rects[i].setLocation(new_x, new_y);
		return false;
	}
		
} //end of DrawingCanvas
