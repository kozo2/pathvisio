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
import java.applet.Applet;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.Graphics2D.*;
import javax.swing.JPanel;

public class GmmlDrawing extends JPanel implements MouseListener, MouseMotionListener {
	double zf = 15; //zoomfactor
	GmmlPathway pathway;
	GmmlConnection connection;
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
	
	GmmlDrawing(GmmlPathway inputpathway, GmmlConnection inputconnection) {
		pathway = inputpathway;
		connection = inputconnection;
		
		rectsLength = pathway.geneProducts.length;
		rectClick = new boolean[rectsLength];
				
		setBackground(Color.white);
		addMouseMotionListener(this);
		addMouseListener(this);
		
		lastx = new int[rectsLength];
		lasty = new int[rectsLength];
		setPreferredSize(new Dimension((int)(pathway.size[0]/zf),(int)(pathway.size[1]/zf)));
		setSize(new Dimension((int)(pathway.size[0]/zf),(int)(pathway.size[1]/zf)));
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
			if(pathway.geneProducts[i].contains(e.getX()*zf, e.getY()*zf)){ //if the user presses the mouses on a coordinate which is contained by rect
				
				rectClick[i]=true;
				
				lastx[i] = (int) (pathway.geneProducts[i].x - e.getX()*zf); //lastx = position pathway.rects[i] - position mouse when pressed
				lasty[i] = (int) (pathway.geneProducts[i].y - e.getY()*zf);
				
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
		pathway.geneProducts[i].setLocation((int)(lastx[i] + e.getX()*zf), (int)(lasty[i] + e.getY()*zf));
                /*
                 * Updates the label to reflect the location of the
                 * current rectangle 
                 * if checkrect2 returns true; otherwise, returns error message.
                 */
                if (checkRect(i)) { //true if rect is in area, false if rect is not in area, rect is put back into area
                   label.setText("Rectangle "+i+" located at " +
                                                     pathway.geneProducts[i].x + ", " +
                                                    pathway.geneProducts[i].y);
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
		for(int i=0; i<pathway.shapes.length; i++) {
			drawShape(pathway.shapes[i]);
		}
		 
		//Draws lines
		for (int i=0; i<pathway.lines.length; i++) {
			drawLine(pathway.lines[i]);
		}
		
		//Draws lineshapes
		for (int i=0; i<pathway.lineshapes.length; i++) {
			drawLineShape(pathway.lineshapes[i]);
		}
		
		for (int i=0; i<connection.Connection.length; i++) {
			big.setColor(Color.orange);
			big.setStroke(new BasicStroke(2.0f));
//			System.out.println("Type 1: "+connection.Connection[i][3]);
//			System.out.println("Type 2: "+connection.Connection[i][4]);
			if (connection.Connection[i][3]==0 && connection.Connection[i][4]==0) {
				double x1 = pathway.geneProducts[connection.Connection[i][1]].x + 0.5 * pathway.geneProducts[connection.Connection[i][1]].width;
				double y1 = pathway.geneProducts[connection.Connection[i][1]].y + 0.5 * pathway.geneProducts[connection.Connection[i][1]].height;
				double x2 = pathway.geneProducts[connection.Connection[i][2]].x + 0.5 * pathway.geneProducts[connection.Connection[i][2]].width;
				double y2 = pathway.geneProducts[connection.Connection[i][2]].y + 0.5 * pathway.geneProducts[connection.Connection[i][2]].height;
				big.draw(new Line2D.Double(x1/zf,y1/zf,x2/zf,y2/zf));
			}
		}
		
		//Draw geneproducts
		for (int i=0; i<pathway.geneProducts.length; i++) {
			drawGeneProduct(pathway.geneProducts[i]);
		}
		
		//Draw text labels
		for (int i=0; i<pathway.labels.length; i++) {
			drawLabel(pathway.labels[i]);
		}
		
		//Draw arcs
		for (int i=0; i<pathway.arcs.length; i++) {
			drawArc(pathway.arcs[i]);
		}
		
		//Draw braces
		for (int i=0; i<pathway.braces.length; i++) {
			drawBrace(pathway.braces[i]);
		}
		
		// Draws the buffered image to the screen.
		g2.drawImage(bi, 0, 0, this);
		
	} //end of update
	
	public void drawShape (GmmlShape shape) {
		big.setStroke(new BasicStroke(1.0f));
		big.setColor(shape.color);
		big.rotate(Math.toRadians(shape.rotation), (shape.x/zf + shape.width/zf), (shape.y/zf + shape.height/zf));
		if (shape.type == 0) {
			big.draw(new Rectangle((int)(shape.x/zf + shape.width/(2*zf)),(int)(shape.y/zf + shape.height/(2*zf)),(int)(shape.width/zf),(int)(shape.height/zf)));
		} else if (shape.type == 1) {
			big.draw(new Ellipse2D.Double(shape.x/zf,shape.y/zf,2*shape.width/zf,2*shape.height/zf));
		}
		big.rotate(-Math.toRadians(shape.rotation),  (shape.x/zf + shape.width/zf), (shape.y/zf + shape.height/zf));  //reset rotation
	}
	
	public void drawLine (GmmlLine line) {
		big.setColor(line.color);
		float[] dash = {3.0f};
		if (line.style==0) {
			big.setStroke(new BasicStroke(1.0f));
		}
		else if (line.style==1){ 
			big.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
		}
		Line2D.Double drawline = new Line2D.Double(line.startx/zf,line.starty/zf,line.endx/zf,line.endy/zf);
		big.draw(drawline);
		if (line.type==1) {
			drawArrowHead(line);
		}
	}
	
	public void drawArrowHead (GmmlLine line) {
		//Creates arrowhead
		big.setColor(line.color);
		double angle = 25.0;
		double theta = Math.toRadians(180 - angle);
		double[] rot = new double[2];
		double[] p = new double[2];
		double[] q = new double[2];
		double a, b, norm;
		
		rot[0] = Math.cos(theta);
		rot[1] = Math.sin(theta);
		
		big.setStroke(new BasicStroke(1.0f));

		a = line.endx-line.startx;
		b = line.endy-line.starty;
		norm = 8/(Math.sqrt((a*a)+(b*b)));				
		p[0] = ( a*rot[0] + b*rot[1] ) * norm + line.endx/zf;
		p[1] = (-a*rot[1] + b*rot[0] ) * norm + line.endy/zf;
		q[0] = ( a*rot[0] - b*rot[1] ) * norm + line.endx/zf;
		q[1] = ( a*rot[1] + b*rot[0] ) * norm + line.endy/zf;
		int[] x = {(int) (line.endx/zf),(int) (p[0]),(int) (q[0])};
		int[] y = {(int) (line.endy/zf),(int) (p[1]),(int) (q[1])};
		Polygon arrowhead = new Polygon(x,y,3);
		big.draw(arrowhead);
		big.fill(arrowhead);
	}
	
	public void drawLineShape (GmmlLineShape lineshape) {
		big.setColor(lineshape.color);
		big.setStroke(new BasicStroke(1.0f));
		
		if (lineshape.type==0) {
			double x1 = lineshape.startx/zf;
			double x2 = lineshape.endx/zf;
			double y1 = lineshape.starty/zf;
			double y2 = lineshape.endy/zf;
			
			Line2D.Double drawline = new Line2D.Double(x1,y1,x2,y2);
			big.draw(drawline);
			
			double s  = Math.sqrt(((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1))) / 8;
			
			double capx1 = ((-y2 + y1)/s) + x2;
			double capy1 = (( x2 - x1)/s) + y2;
			double capx2 = (( y2 - y1)/s) + x2;
			double capy2 = ((-x2 + x1)/s) + y2;
			
			Line2D.Double drawcap = new Line2D.Double(capx1,capy1,capx2,capy2);
			big.draw(drawcap);
		}
	}
	
	// Draws the Geneproduct.
	public void drawGeneProduct (GmmlGeneProduct geneproduct) {
		big.setColor(Color.black);
		big.setStroke(new BasicStroke(2.0f));
		Rectangle rect = new Rectangle((int)(geneproduct.x/zf),(int)(geneproduct.y/zf),(int)(geneproduct.width/zf),(int)(geneproduct.height/zf));
		big.draw(rect);
				
		// Draws text on the newly positioned rectangles.
		Font gpfont = new Font("Arial", Font.PLAIN, (int)(150/zf));
		big.setFont(gpfont);
		
		big.setColor(Color.black);
		big.setStroke(new BasicStroke(1.0f));

		FontMetrics fm = big.getFontMetrics();
		
		int rectWidth = geneproduct.width;
		int rectHeight = geneproduct.height;
		int textWidth = fm.stringWidth(geneproduct.geneID);
		int fHeight = fm.getHeight();
				
		int x = (int)(geneproduct.x + (rectWidth  - zf * textWidth) / 2);
		int y = (int)(geneproduct.y + (rectHeight + zf * fHeight  ) / 2);
		
		big.drawString(geneproduct.geneID,(int)(x/zf),(int)(y/zf));
	}
	
	//Draw a label
	public void drawLabel (GmmlLabel label) {
		big.setColor(label.color);
		Font font = new Font(label.font, Font.PLAIN, (int) (label.fontSize*(15/zf)));
		if (label.fontWeight.equalsIgnoreCase("bold")) {
			if (label.fontStyle.equalsIgnoreCase("italic")) {
				font = font.deriveFont(Font.BOLD+Font.ITALIC);
			} else {
				font = font.deriveFont(Font.BOLD);
			}
		} else if (label.fontStyle.equalsIgnoreCase("italic")) {
			font = font.deriveFont(Font.ITALIC);
		} 
		
		big.setFont(font); 
		
		FontMetrics fm = big.getFontMetrics();
		int lfHeight = fm.getHeight();
		
		big.drawString(label.text,(int)(label.x/zf), (int)(label.y/zf+lfHeight));
	}
	
	//Draw an arc
	public void drawArc (GmmlArc arc) {
		big.setColor(arc.color);
		big.setStroke(new BasicStroke(2.0f));
			
		Arc2D.Double temparc = new Arc2D.Double((arc.x - arc.width)/zf, (arc.y - arc.height)/zf, 2*arc.width/zf, 2*arc.height/zf, 0, 180, 0);
		big.draw(temparc);
	}
	
	//Draw a brace
	public void drawBrace (GmmlBrace brace) {
		double cX  = brace.cX/zf;
		double cY  = brace.cY/zf;
		double w   = brace.w/zf;
		double ppo = brace.ppo/zf;
		int or = brace.or;
		
		Arc2D.Double[] arcsOfBrace = new Arc2D.Double[4]; //4 Arcs are used to create a brace
		Line2D.Double[] linesOfBrace = new Line2D.Double[2];; //2 Lines are used to creata a brace
		Line2D.Double[] lines = new Line2D.Double[2];
		
		linesOfBrace[0] = new Line2D.Double();
		linesOfBrace[1] = new Line2D.Double();
	
		for (int i=0; i<4; i++){
			arcsOfBrace[i] = new Arc2D.Double();
		}
		
		if (or==0) { //Orientation is top
			linesOfBrace[0].setLine(cX+0.5*ppo,cY,cX+0.5*w-0.5*ppo,cY); //line on the right
			linesOfBrace[1].setLine(cX-0.5*ppo,cY,cX-0.5*w+0.5*ppo,cY); //line on the left
			
			arcsOfBrace[0].setArc(cX-(0.5*w),cY,ppo,ppo,-180,-90,0); //arc on the left
			arcsOfBrace[1].setArc(cX-ppo,cY-ppo,ppo,ppo,-90,90,0); //left arc in the middle
			arcsOfBrace[2].setArc(cX,cY-ppo,ppo,ppo,-90,-90,0); //right arc in the middle
			arcsOfBrace[3].setArc(cX+(0.5*w)-ppo,cY,ppo,ppo,0,90,0); //arc on the right
		} // end of orientation is top
		
		else if (or==1) { //Orientation is right
			linesOfBrace[0].setLine(cX,cY+0.5*ppo,cX,cY+0.5*w-0.5*ppo); //line on the bottom
			linesOfBrace[1].setLine(cX,cY-0.5*ppo,cX,cY-0.5*w+0.5*ppo); //line on the top
			
			arcsOfBrace[0].setArc(cX-ppo,cY-(0.5*w),ppo,ppo,0,90,0); //arc on the top
			arcsOfBrace[1].setArc(cX,cY-ppo,ppo,ppo,-90,-90,0); //upper arc in the middle
			arcsOfBrace[2].setArc(cX,cY,ppo,ppo,90,90,0); //lower arc in the middle
			arcsOfBrace[3].setArc(cX-ppo,cY+(0.5*w)-ppo,ppo,ppo,0,-90,0); //arc on the bottom

		} // end of orientation is right
		
		else if (or==2) { //Orientation is bottom
			linesOfBrace[0].setLine(cX+0.5*ppo,cY,cX+0.5*w-0.5*ppo,cY); //line on the right
			linesOfBrace[1].setLine(cX-0.5*ppo,cY,cX-0.5*w+0.5*ppo,cY); //line on the left
			
			arcsOfBrace[0].setArc(cX-(0.5*w),cY-ppo,ppo,ppo,-180,90,0); //arc on the left
			arcsOfBrace[1].setArc(cX-ppo,cY,ppo,ppo,90,-90,0); //left arc in the middle
			arcsOfBrace[2].setArc(cX,cY,ppo,ppo,90,90,0); //right arc in the middle
			arcsOfBrace[3].setArc(cX+(0.5*w)-ppo,cY-ppo,ppo,ppo,0,-90,0); //arc on the right

		} // end of orientation is bottom
		
		else if (or==3) { //Orientation is left
			linesOfBrace[0].setLine(cX,cY+0.5*ppo,cX,cY+0.5*w-0.5*ppo); //line on the bottom
			linesOfBrace[1].setLine(cX,cY-0.5*ppo,cX,cY-0.5*w+0.5*ppo); //line on the top
			
			arcsOfBrace[0].setArc(cX,cY-(0.5*w),ppo,ppo,-180,-90,0); //arc on the top
			arcsOfBrace[1].setArc(cX-ppo,cY-ppo,ppo,ppo,-90,90,0); //upper arc in the middle
			arcsOfBrace[2].setArc(cX-ppo,cY,ppo,ppo,90,-90,0); //lower arc in the middle
			arcsOfBrace[3].setArc(cX,cY+(0.5*w)-ppo,ppo,ppo,-90,-90,0); //arc on the bottom

		} // end of orientation is left
		
		big.setColor(brace.color);
		big.setStroke(new BasicStroke(2.0f));
		
		big.draw(linesOfBrace[0]);
		big.draw(linesOfBrace[1]);
		big.draw(arcsOfBrace[0]);
		big.draw(arcsOfBrace[1]);
		big.draw(arcsOfBrace[2]);
		big.draw(arcsOfBrace[3]);
		
	} //end of drawBrace

	/*
    * Checks if the rectangle is contained within the applet window.  If the rectangle
    * is not contained withing the applet window, it is redrawn so that it is adjacent
    * to the edge of the window and just inside the window.
	 */
	 
	boolean checkRect(int i){
		if (area == null) {
			return false;
		}
		if(area.contains(pathway.geneProducts[i].x/zf, pathway.geneProducts[i].y/zf, pathway.geneProducts[i].width/zf, pathway.geneProducts[i].height/zf)){
			return true;
		}		
	
		int new_x = pathway.geneProducts[i].x;
		int new_y = pathway.geneProducts[i].y;

		if((pathway.geneProducts[i].x+pathway.geneProducts[i].width)/zf>area.width){
			new_x = (int)(area.width*zf-pathway.geneProducts[i].width+1);
		}
		if(pathway.geneProducts[i].x < 0){  
			new_x = -1;
		}
		if((pathway.geneProducts[i].y+pathway.geneProducts[i].height)/zf>area.height){
			new_y = (int)(area.height*zf-pathway.geneProducts[i].height+1); 
		}
		if(pathway.geneProducts[i].y < 0){  
			new_y = -1;
		}
		pathway.geneProducts[i].setLocation(new_x, new_y);
		return false;
	}
	
	public void setZoom(int z) {
		zf = (int) (15.0/(z/100.0));
		setPreferredSize(new Dimension((int)(pathway.size[0]/zf), (int)(pathway.size[1]/zf)));
		repaint();
	}
		
} //end of DrawingCanvas
