import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.JPanel;

public class GmmlDrawing extends JPanel implements MouseListener, MouseMotionListener {
	int zf = 15; //zoomfactor
	GmmlPathway pathway;
	BufferedImage bi;
	Graphics2D big;
	
	//Zoomfactor
	int zoom;
	
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
		setPreferredSize(new Dimension(pathway.size[0]/zf, pathway.size[1]/zf));
		setSize(new Dimension(pathway.size[0]/zf, pathway.size[1]/zf));
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
		//add("South", label); //South: in the lowest part of the frame.
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
			if(pathway.rects[i].contains(e.getX()*zf, e.getY()*zf)){ //if the user presses the mouses on a coordinate which is contained by rect
				
				rectClick[i]=true;
				
				lastx[i] = pathway.rects[i].x - e.getX()*zf; //lastx = position pathway.rects[i] - position mouse when pressed
				lasty[i] = pathway.rects[i].y - e.getY()*zf;
				
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
		pathway.rects[i].setLocation(lastx[i] + e.getX()*zf, lasty[i] + e.getY()*zf);
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

		//Draw shapes
		big.setStroke(new BasicStroke(1.0f));
		for(int i=0; i<pathway.shapeCoord.length-1; i++) {
			big.setColor(pathway.shapeColor[i]);
			if (pathway.shapeType[i] == 0) {
				big.draw(new Rectangle((int)pathway.shapeCoord[i][0]/zf+(int)pathway.shapeCoord[i][2]/(2*zf),(int)pathway.shapeCoord[i][1]/zf+(int)pathway.shapeCoord[i][3]/(2*zf),(int)pathway.shapeCoord[i][2]/zf,(int)pathway.shapeCoord[i][3]/zf));
			} else if (pathway.shapeType[i] == 1) {
				big.draw(new Ellipse2D.Double(pathway.shapeCoord[i][0]/zf,pathway.shapeCoord[i][1]/zf,2*pathway.shapeCoord[i][2]/zf,2*pathway.shapeCoord[i][3]/zf));

			}
		}

		//Draws lines
		big.setColor(Color.black);
		for (int i=0; i<pathway.lineLayout.length-1; i++) {
			big.setColor(Color.black);
			float[] dash = {3.0f};
			if (pathway.lineLayout[i][0]==0) {
				big.setStroke(new BasicStroke(1.0f));
			}
			else if (pathway.lineLayout[i][0]==1){ 
				big.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			}
			big.draw(new Line2D.Double(pathway.lineCoord[i][0]/zf,pathway.lineCoord[i][1]/zf,pathway.lineCoord[i][2]/zf,pathway.lineCoord[i][3]/zf));
		}
		for (int i=0; i<pathway.rectConnection.length; i++) {
			big.setColor(Color.green);
			big.setStroke(new BasicStroke(2.0f));
			double x1 = pathway.rects[pathway.rectConnection[i][1]].getX() + 0.5 * pathway.rects[pathway.rectConnection[i][1]].getWidth();
			double y1 = pathway.rects[pathway.rectConnection[i][1]].getY() + 0.5 * pathway.rects[pathway.rectConnection[i][1]].getHeight();
			double x2 = pathway.rects[pathway.rectConnection[i][2]].getX() + 0.5 * pathway.rects[pathway.rectConnection[i][2]].getWidth();
			double y2 = pathway.rects[pathway.rectConnection[i][2]].getY() + 0.5 * pathway.rects[pathway.rectConnection[i][2]].getHeight();
			big.draw(new Line2D.Double(x1/zf,y1/zf,x2/zf,y2/zf));
		}
		
		//Creates arrows
		//This doesn't have to be in the forloop so putting it here saves speed
		big.setColor(Color.black);
		double angle = 25.0;
		double theta = (180 - angle) / (180/Math.PI);
		double[] rot = new double[2];
		rot[0] = Math.cos(theta);
		rot[1] = Math.sin(theta);
		
		//The for loop
		for (int i=0; i<pathway.lineLayout.length-1; i++) {
			double[] p = new double[2];
			double[] q = new double[2];
			double a, b, norm;
			
			big.setStroke(new BasicStroke(1.0f));
			if (pathway.lineLayout[i][1]==1) {
				a = pathway.lineCoord[i][2]-pathway.lineCoord[i][0];
				b = pathway.lineCoord[i][3]-pathway.lineCoord[i][1];
				norm = 8/(Math.sqrt((a*a)+(b*b)));				
				p[0] = ( a*rot[0] + b*rot[1] ) * norm + pathway.lineCoord[i][2]/zf;
				p[1] = (-a*rot[1] + b*rot[0] ) * norm + pathway.lineCoord[i][3]/zf;
				q[0] = ( a*rot[0] - b*rot[1] ) * norm + pathway.lineCoord[i][2]/zf;
				q[1] = ( a*rot[1] + b*rot[0] ) * norm + pathway.lineCoord[i][3]/zf;
//				big.draw(new Line2D.Double(p[0],p[1],pathway.lineCoord[i][2],pathway.lineCoord[i][3]));
//				big.draw(new Line2D.Double(q[0],q[1],pathway.lineCoord[i][2],pathway.lineCoord[i][3]));
				int[] x = {(int) (pathway.lineCoord[i][2])/zf,(int) (p[0]),(int) (q[0])};
				int[] y = {(int) (pathway.lineCoord[i][3])/zf,(int) (p[1]),(int) (q[1])};
				Polygon arrowhead = new Polygon(x,y,3);
				big.draw(arrowhead);
				big.fill(arrowhead);
//				System.out.println(" a = " + pathway.lineCoord[i][0] +", "+ pathway.lineCoord[i][1] + " b = " + pathway.lineCoord[i][2] + ", "+ pathway.lineCoord[i][3] + " p = " + p[0] + ", " + p[1] +" q = " + q[0] + ", " + q[1]);
			}
		}

		// Draws and fills the newly positioned rectangle to the buffer.
		for (int i=0; i<rectsLength; i++) {
			big.setColor(Color.blue);
			big.setStroke(new BasicStroke(2.0f));
			Rectangle temp = new Rectangle((int)pathway.rects[i].getX()/zf,(int)pathway.rects[i].getY()/zf,(int)pathway.rects[i].getWidth()/zf,(int)pathway.rects[i].getHeight()/zf);
			big.draw(temp);
			//big.setColor(colors[i]);
			big.setColor(Color.orange);
			big.fill(temp);
		}
		
		// Draws text on the newly positioned rectangles.
		Font gpfont = new Font("Arial", Font.PLAIN, (150/zf));
		big.setFont(gpfont);
		FontMetrics fm = big.getFontMetrics();
		int fHeight = fm.getHeight();
		int textWidth;
		int rectWidth;
		int rectHeight;
		
		for (int i=0; i<pathway.rectText.length; i++) {
			big.setColor(Color.black);
			big.setStroke(new BasicStroke(1.0f));
			
			rectWidth = (int)pathway.rects[i].getWidth();
			rectHeight = (int)pathway.rects[i].getHeight();
			textWidth = fm.stringWidth(pathway.rectText[i]);
						
			int x = (int)pathway.rects[i].getX() + (rectWidth  - zf * textWidth) /2;
			int y = (int)pathway.rects[i].getY() + (rectHeight + zf * fHeight  ) /2;
			big.drawString(pathway.rectText[i],x/zf,y/zf);
		}
		
		// Draw text labels
		for (int i=0; i<pathway.labelCoord.length; i++) {
			big.setColor(pathway.labelColor[i]);
			Font font = new Font(pathway.labelFont[i][0], Font.PLAIN, pathway.labelFontSize[i]*(15/zf));
			if (pathway.labelFont[i][1].equalsIgnoreCase("bold")) {
				if (pathway.labelFont[i][2].equalsIgnoreCase("italic")) {
					font = font.deriveFont(Font.BOLD+Font.ITALIC);
				} else {
					font = font.deriveFont(Font.BOLD);
				}
			} else if (pathway.labelFont[i][2].equalsIgnoreCase("italic")) {
				font = font.deriveFont(Font.ITALIC);
			} 
			
			big.setFont(font); 
			
			FontMetrics lfm = big.getFontMetrics();
			int lfHeight = fm.getHeight();

			big.drawString(pathway.labelText[i],pathway.labelCoord[i][0]/zf, pathway.labelCoord[i][1]/zf+lfHeight);
		}
		
		// Draws arcs
		for (int i=0; i<pathway.arcs.length; i++) {
			big.setColor(Color.black);
			big.setStroke(new BasicStroke(2.0f));
			
			Arc2D.Double temp = new Arc2D.Double(pathway.arcs[i].x/zf, pathway.arcs[i].y/zf, pathway.arcs[i].width/zf, pathway.arcs[i].height/zf, pathway.arcs[i].start, pathway.arcs[i].extent, 0);
			big.draw(temp);
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
		if(area.contains(pathway.rects[i].x/zf, pathway.rects[i].y/zf, pathway.rects[i].width/zf, pathway.rects[i].height/zf)){
			return true;
		}		
	
		int new_x = pathway.rects[i].x;
		int new_y = pathway.rects[i].y;

		if((pathway.rects[i].x+pathway.rects[i].width)/zf>area.width){
			new_x = area.width*zf-pathway.rects[i].width+1;
		}
		if(pathway.rects[i].x < 0){  
			new_x = -1;
		}
		if((pathway.rects[i].y+pathway.rects[i].height)/zf>area.height){
			new_y = area.height*zf-pathway.rects[i].height+1; 
		}
		if(pathway.rects[i].y < 0){  
			new_y = -1;
		}
		pathway.rects[i].setLocation(new_x, new_y);
		return false;
	}
	
	public void setZoom(int z) {
		zf = (int) (15.0/(z/100.0));
		setPreferredSize(new Dimension(pathway.size[0]/zf, pathway.size[1]/zf));
		repaint();
	}
		
} //end of DrawingCanvas
