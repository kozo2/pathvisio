import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.JPanel;

public class GmmlDrawing extends JPanel implements MouseListener, MouseMotionListener {
	GmmlPathway pathway;
	BufferedImage bi;
	Graphics2D big;
	
	
	//Holds the coordinates of the user's last mousePressed event.
	int[] lastx;
	int[] lasty;
	
	//length of Coordlength = amount of rectangles
	int rectsLength;
	boolean[] rectClick;
	
	boolean firstTime = true;
	TexturePaint fillColor;
	Rectangle area; //area in which the rectangles are plotted.
	
	static protected Label label;
	
	GmmlDrawing(GmmlPathway inputpathway) {
		pathway = inputpathway;
		
		rectsLength = pathway.rects.length;
		rectClick = new boolean[rectsLength];
				
		setBackground(Color.white);
		addMouseMotionListener(this);
		addMouseListener(this);
		
		lastx = new int[rectsLength];
		lasty = new int[rectsLength];
	} //end of GmmlDrawing(inputpathway)

	//init is used to form the JPanel later in the program.
	public void init(){
		//Dump the stored attributes to the screen.		
		System.out.println("Checking for stored attributes - number: "+pathway.attributes.length);
		for(int i=0; i<pathway.attributes.length; i++) {
			System.out.println("Attribute name: "+pathway.attributes[i][0]+ "value : "+pathway.attributes[i][1]);
		}
		
		//Initialize the layout.
		setLayout(new BorderLayout());
		
		//This label is used when the applet is just started
		label = new Label("Drag rectangles around within the area");
		add("South", label); //South: in the lowest part of the frame.
	} //end of init
	
	/*When the mouse is pressed, there is checked with a for-loop if one clicked inside of a rectangle.
	 *If that is not the case, pressOut is true. If one clicks in a rectangle, the mouseEvent and the
	 *number of the rectangle are being sent to updateLocation.
	 *in rectClickArray, the rects in which there was clicked are true.
	 */
	 
	boolean pressOut = false; //true when one pressed or dragged or released outside the rectangles, false otherwise.		
	
	public void mousePressed(MouseEvent e){
		pressOut = false;
		int CS = 0;
		for (int i=rectsLength-1; i>=0; i--) {
			if(pathway.rects[i].contains(e.getX(), e.getY())){ //if the user presses the mouses on a coordinate which is contained by rect
				
				rectClick[i]=true;
				
				lastx[i] = pathway.rects[i].x - e.getX(); //lastx = position pathway.rects[i] - position mouse when pressed
				lasty[i] = pathway.rects[i].y - e.getY();
				
				updateLocation(i,e);
			}
			else {
				CS++; //counter for in how many rectangles the mouse was not clicked.
				rectClick[i]=false;
			}
			if(rectClick[i]) {
				break;
			}
		}
		if (CS==rectsLength) { //if CS is the same as the amount of rectangles, one didn't press inside a rectangle.
			pressOut = true;
		}
				
	} //end of mousePressed
	
	public void mouseDragged(MouseEvent e){
		for (int i=0; i<rectsLength; i++) {
			if(!pressOut && rectClick[i]){ //always mousePressed before mouseDragged -> pressOut true when start dragging outside of rect.
			 	updateLocation(i,e);
			} 
			else {  
				label.setText("First position the cursor on the rectangle and then drag.");
			}
		}
	} //end of mouseDragged
	
	// Handles the event of a user releasing the mouse button. Sets pressOut back on false.
	public void mouseReleased(MouseEvent e){

		// Checks whether or not the cursor is inside of the rectangle when the user releases the mouse button.   
		for (int i=rectsLength-1; i>=0; i--) {
			rectClick[i]=false;
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
		pathway.rects[i].setLocation(lastx[i] + e.getX(), lasty[i] + e.getY());
                /*
                 * Updates the label to reflect the location of the
                 * current rectangle 
                 * if checkrect2 returns true; otherwise, returns error message.
                 */
                if (checkRect(i)) { //true if rect is in area, false if rect is not in area, rect is put back into area
                   label.setText("Rectangle "+i+" located at " +
                                                     pathway.rects[i].getX() + ", " +
                                                    pathway.rects[i].getY());
             } else {
                    label.setText("Please don't try to "+
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
		for (int i=0; i<pathway.lineLayout.length-1; i++) {
			//System.out.println("i is" + i);
			float[] dash = {3.0f};
			if (pathway.lineLayout[i][0]==0) {
				big.setStroke(new BasicStroke(2.0f));
			}
			else if (pathway.lineLayout[i][0]==1){ 
			big.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			}
			big.draw(new Line2D.Double(pathway.lineCoord[i][0],pathway.lineCoord[i][1],pathway.lineCoord[i][2],pathway.lineCoord[i][3]));
		}
		
		//Creates arrows
		for (int i=0; i<pathway.lineLayout.length-1; i++) {
			double[] p = new double[2];
			double[] q = new double[2];
			double[] a = new double[2];
			double[] b = new double[2];
			double[] rot = new double[2];
			double[] ptemp = new double[2];
			double[] qtemp = new double[2];
			double alfa = 30.0;
			//double a;
			//double b;
			//double normp;
			//double normq;
			
			big.setStroke(new BasicStroke(2.0f));
			if (pathway.lineLayout[i][1]==1) {
				a[0] = pathway.lineCoord[i][0]; 
				a[1] = pathway.lineCoord[i][1];
				b[0] = pathway.lineCoord[i][2]; 
				b[1] = pathway.lineCoord[i][3];
				double theta = Math.atan(((b[0]-a[0])/(b[1]-a[1])));			
				rot[0] = (b[0]-a[0])*Math.cos(theta)+(b[1]-a[1])*Math.sin(theta);
				rot[1] = (b[0]-a[0])*(-1)*(Math.sin(theta))+(b[1]-a[1])*Math.cos(theta);
				ptemp[0] = rot[0]-6*Math.cos(alfa);
				ptemp[1] = rot[1]+6*Math.sin(alfa);
				qtemp[0] = rot[0]-6*Math.cos(alfa);
				qtemp[1] = rot[1]-6*Math.sin(alfa);
				p[0] = (ptemp[0]*Math.cos(-theta)+ptemp[1]*Math.sin(-theta))+b[0];
				p[1] = (ptemp[0]*(-1)*(Math.sin(-theta))+ptemp[1]*Math.cos(-theta))+b[1];
				q[0] = (qtemp[0]*Math.cos(-theta)+qtemp[1]*Math.sin(-theta))+b[0];
				q[1] = (qtemp[0]*(-1)*(Math.sin(-theta))+qtemp[1]*Math.cos(-theta))+b[1];				
				//a = pathway.lineCoord[i][3]-pathway.lineCoord[i][0];
				//b = pathway.lineCoord[i][4]-pathway.lineCoord[i][1];
				//normp = 10/(Math.sqrt((-a+b)*(-a+b)+(-a-b)*(-a-b)));
				//normq = 10/(Math.sqrt((-a-b)*(-a-b)+(-b+a)*(-b+a)));				
				//p[0] = pathway.lineCoord[i][2]-normp*(a-b);
				//p[1] = pathway.lineCoord[i][3]-normp*(a+b);
				//q[0] = pathway.lineCoord[i][2]-normq*(a+b);
				//q[1] = pathway.lineCoord[i][3]-normq*(-a+b);
				big.draw(new Line2D.Double(p[0],p[1],pathway.lineCoord[i][2],pathway.lineCoord[i][3]));
				big.draw(new Line2D.Double(q[0],q[1],pathway.lineCoord[i][2],pathway.lineCoord[i][3]));
				int[] x = {(int) pathway.lineCoord[i][2],(int) p[0],(int) q[0]};
				int[] y = {(int) pathway.lineCoord[i][3],(int) p[1],(int) q[1]};
				Polygon arrowhead = new Polygon(x,y,3);
				big.draw(arrowhead);
				big.fill(arrowhead);
				System.out.println(" a = " + a + " b = " + b + " p = " + p[0] + ", " + p[1] +" q = " + q[0] + ", " + q[1]);
			}
		}

		// Draws and fills the newly positioned rectangle to the buffer.
		for (int i=0; i<rectsLength; i++) {
			big.setColor(Color.blue);
			big.setStroke(new BasicStroke(2.0f));
			big.draw(pathway.rects[i]);
			//big.setColor(colors[i]);
			big.setColor(Color.orange);
			big.fill(pathway.rects[i]);
		}
		
		// Draws text on the newly positioned rectangles.
		for (int i=0; i<pathway.rectText.length; i++) {
			big.setColor(Color.black);
			big.setStroke(new BasicStroke(2.0f));
			int x = (int)pathway.rects[i].getX();
			int y = (int)pathway.rects[i].getY();
			big.drawString(pathway.rectText[i],x,y);
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
		if(area.contains(pathway.rects[i].x, pathway.rects[i].y, pathway.rects[i].width, pathway.rects[i].height)){
		return true;
		}		
	
		int new_x = pathway.rects[i].x;
		int new_y = pathway.rects[i].y;

		if((pathway.rects[i].x+pathway.rects[i].width)>area.width){
			new_x = area.width-pathway.rects[i].width+1;
		}
		if(pathway.rects[i].x < 0){  
			new_x = -1;
		}
		if((pathway.rects[i].y+pathway.rects[i].height)>area.height){
			new_y = area.height-pathway.rects[i].height+1; 
		}
		if(pathway.rects[i].y < 0){  
			new_y = -1;
		}
		pathway.rects[i].setLocation(new_x, new_y);
		return false;
	}
		
} //end of DrawingCanvas
