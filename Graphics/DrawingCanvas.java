import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.image.*;

public class DrawingCanvas extends Canvas implements MouseListener, MouseMotionListener {
	RectArray ra;
	BufferedImage bi;
	Graphics2D big;
	
	//Holds the coordinates of the user's last mousePressed event.
	int[] lastx = new int[4];
	int[] lasty = new int[4];

	boolean firstTime = true;
	TexturePaint fillColor;
	Rectangle area; //area in which the rectangles are plotted.
	
	
	public void DrawingCanvas() {
		ra = new RectArray();
		setBackground(Color.white);
		addMouseMotionListener(this);
		addMouseListener(this);
		
		//Create buffered fill. Moet nog in for-loop komen!!
		bi = new BufferedImage(5,5,BufferedImage.TYPE_INT_RGB); //(int width, int height, int imageType)
		big = bi.createGraphics();
		big.setColor(Color.cyan);
		big.fillRect(0,0,7,7);
		Rectangle r = new Rectangle(0,0,5,5); //big and bi are only used to create the pattern
		fillColor = new TexturePaint(bi,r);
		big.dispose();
	} //end of DrawingCanvas()
	
	/*When the mouse is pressed, there is checked with a for-loop if one clicked inside of a rectangle.
	 *If that is not the case, pressOut is true. If one clicks in a rectangle, the mouseEvent and the
	 *number of the rectangle are being sent to updateLocation.
	 *in rectClickArray, the rects in which there was clicked are true.
	 */
	 
	boolean pressOut = false; //true when one pressed or dragged or released outside the rectangles, false otherwise.
	boolean[] rectClick = new boolean[4];		
	
	public void mousePressed(MouseEvent e){
		pressOut = false;
		int CS = 0;
		for (int i=0; i<4; i++) {
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
		if (CS==4) { //if CS is the same as the amount of rectangles, one didn't press inside a rectangle.
			pressOut = true;
		}
				
	} //end of mousePressed
	
	public void mouseDragged(MouseEvent e){
		for (int i=0; i<4; i++) {
			if(!pressOut && rectClick[i]){ //always mousePressed before mouseDragged -> pressOut true when start dragging outside of rect.
			 	updateLocation(i,e);
			} 
			else {  
				Drawing.label.setText("First position the cursor on the rectangle and then drag.");
			}
		}
	} //end of mouseDragged
	
	// Handles the event of a user releasing the mouse button. Sets pressOut back on false.
	public void mouseReleased(MouseEvent e){

		// Checks whether or not the cursor is inside of the rectangle when the user releases the mouse button.   
		for (int i=0; i<4; i++) {
			if(ra.rects[i].contains(e.getX(), e.getY())){
				updateLocation(i,e);
			} 
			else {
			Drawing.label.setText("First position the cursor on the rectangle and then drag.");
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
                   Drawing.label.setText("Rectangle "+i+" located at " +
                                                     ra.rects[i].getX() + ", " +
                                                    ra.rects[i].getY());
             } else {
                    Drawing.label.setText("Please don't try to "+
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

		// Draws and fills the newly positioned rectangle to the buffer.
		for (int i=0; i<4; i++){
			big.setStroke(new BasicStroke(8.0f));
			big.draw(ra.rects[i]);
			big.setPaint(fillColor);
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


//Vanaf hier weggooien als het klaar is.



//a new class RectangleCanvas3, which extends Canvas, is created.
//class RectangleCanvas3 extends Canvas implements MouseListener, MouseMotionListener{
		
 

        // True if the user pressed, dragged or released the mouse outside of the rectangle; false otherwise.
//	boolean pressOut = false;
//	boolean rect1click = false;
//	boolean rect2click = false;

 

        // Handles the event of the user pressing down the mouse button.
//	public void mousePressed(MouseEvent e){

//		last1_x = rect1.x - e.getX(); //last_x = position rect1 - position mouse when pressed
//		last1_y = rect1.y - e.getY();
		
//		last2_x = rect2.x - e.getX(); //last_x = position rect1 - position mouse when pressed
//		last2_y = rect2.y - e.getY();

//			pressOut = false;
//			rect1click=false;
//			rect2click=false;


      // Checks whether or not the cursor is inside of the rectangle while the user is pressing the mouse.
//		if(rect1.contains(e.getX(), e.getY())){ //if the user presses the mouses on a coordinate which is contained by rect
//			rect1click=true;
//			updateLocation1(e);
//		} else if (rect2.contains(e.getX(), e.getY())){ //if the user presses the mouses on a coordinate which is contained by rect
//			rect2click=true;
//			updateLocation2(e);
//		} else {
//		Rectangles3.label.setText("First position the cursor on the rectangle and then drag.");
//			pressOut = true;
//			rect1click=false;
//			rect2click=false;
//		}
//	} //end of mousePressed event

        // Handles the event of a user dragging the mouse while holding down the mouse button.
//	public void mouseDragged(MouseEvent e){

//		if(!pressOut && rect1click){ //always mousePressed before mouseDragged -> pressOut true when start dragging outside of rect.
//			 updateLocation1(e);
//		} else if (!pressOut && rect2click){
//			 updateLocation2(e);
//		} else {  
//		Rectangles3.label.setText("First position the cursor on the rectangle and then drag.");
//		}
//	}

        // Handles the event of a user releasing the mouse button. Sets pressOut back on false.
//	public void mouseReleased(MouseEvent e){

      // Checks whether or not the cursor is inside of the rectangle when the user releases the
		// mouse button.   
//		if(rect1.contains(e.getX(), e.getY())){
//			updateLocation1(e);
//		} else if (rect2.contains(e.getX(), e.getY())){
//			updateLocation2(e);
//		} else {
//		Rectangles3.label.setText("First position the cursor on the rectangle and then drag.");
//			
//		}
//	}

	// This method required by MouseListener, it does nothing
//	public void mouseMoved(MouseEvent e){}

        // These methods are required by MouseMotionListener, they do nothing
//	public void mouseClicked(MouseEvent e){}
//	public void mouseExited(MouseEvent e){}
//	public void mouseEntered(MouseEvent e){}
	
	//updateLocation
//	public void updateLocation1(MouseEvent e){

//		rect1.setLocation(last1_x + e.getX(), last1_y + e.getY());
                /*
                 * Updates the label to reflect the location of the
                 * current rectangle 
                 * if checkrect2 returns true; otherwise, returns error message.
                 */
  //              if (checkRect1()) { //true if rect1 is in area, false if rect1 is not in area, rect1 is put back intro area
 //                   Rectangles3.label.setText("Rectangle 1 located at " +
 //                                                    rect1.getX() + ", " +
//                                                     rect1.getY());
 //               } else {
 //                   Rectangles3.label.setText("Please don't try to "+
 //                                                    " drag outside the area.");
 //               }
//		repaint(); //The component will be repainted after all of the currently pending events have been dispatched
//	}
//public void updateLocation2(MouseEvent e){

//		rect2.setLocation(last2_x + e.getX(), last2_y + e.getY());
                /*
                 * Updates the label to reflect the location of the
                 * current rectangle 
                 * if checkrect2 returns true; otherwise, returns error message.
                 */
//                if (checkRect2()) { //true if rect2 is in area, false if rect2 is not in area, rect2 is put back intro area
  //                  Rectangles3.label.setText("Rectangle 2 located at " +
  //                                                   rect2.getX() + ", " +
 //                                                    rect2.getY());
  //              } else {
 //                   Rectangles3.label.setText("Please don't try to "+
 //                                                    " drag outside the area.");
 //               }
//		repaint(); //The component will be repainted after all of the currently pending events have been dispatched
//	}

//	public void paint(Graphics g){
 //               update(g);
//	}

//	public void update(Graphics g){
//		Graphics2D g2 = (Graphics2D)g;
                 
 //         	if(firstTime){
//                    Dimension dim = getSize(); //Size of frame f
//                    int w = dim.width;
//                    int h = dim.height;
 //                   area = new Rectangle(dim);
//		    bi = (BufferedImage)createImage(w, h);
//		    big = bi.createGraphics();
//		    rect1.setLocation(w/2-50, h/2-25); //the first time, rect2 is put in the middle of area/frame
//		    rect2.setLocation(0,0);
//		    big.setStroke(new BasicStroke(8.0f));
//		    firstTime = false;
//		} 

		// Clears the rectangle that was previously drawn.
//		big.setColor(Color.white);
//		big.clearRect(0, 0, area.width, area.height);

		// Draws and fills the newly positioned rectangle to the buffer.
//		big.setPaint(strokePolka);
//		big.draw(rect1);
//		big.setPaint(fillPolka);
//		big.fill(rect1);

//		big.setPaint(strokePolka);
	//	big.draw(rect2);
//		big.setPaint(fillPolka);
//		big.fill(rect2);


		// Draws the buffered image to the screen.
//		g2.drawImage(bi, 0, 0, this);
//	}
	/*
         * Checks if the rectangle is contained within the applet window.  If the rectangle
         * is not contained withing the applet window, it is redrawn so that it is adjacent
         * to the edge of the window and just inside the window.
	 */
//	boolean checkRect1(){
 //           if (area == null) {
 //               return false;
//            }
//		if(area.contains(rect1.x, rect1.y, 100, 50)){
//			return true;
//		}
		
//		int new1_x = rect1.x;
//		int new1_y = rect1.y;

//		if((rect1.x+100)>area.width){ //rect1 width is 100
//			new1_x = area.width-99;
//		}
//		if(rect1.x < 0){  
//			new1_x = -1;
//		}
//		if((rect1.y+50)>area.height){ //rect1 height is 50
//			new1_y = area.height-49; 
//		}
//		if(rect1.y < 0){  
//			new1_y = -1;
//		}
//		rect1.setLocation(new1_x, new1_y);
//		return false;
//	}
	
//	boolean checkRect2(){
//            if (area == null) {
//                return false;
 //           }
//		if(area.contains(rect2.x, rect2.y, 100, 50)){
//			return true;
//		}
		
//		int new2_x = rect2.x;
//		int new2_y = rect2.y;

//		if((rect2.x+100)>area.width){ //rect2 width is 100
//			new2_x = area.width-99;
//		}
//		if(rect2.x < 0){  
//			new2_x = -1;
//		}
//		if((rect2.y+50)>area.height){ //rect2 height is 50
//			new2_y = area.height-49; 
//		}
//		if(rect2.y < 0){  
//			new2_y = -1;
//		}
//		rect2.setLocation(new2_x, new2_y);
//		return false;
//	}


//} //end of RectangleCanvas3

