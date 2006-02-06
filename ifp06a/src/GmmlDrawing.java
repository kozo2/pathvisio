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

public class GmmlDrawing extends JPanel
{
	// set zoomfactor
	double zf = 15;
	
	// pathway of which a drawing will be created
	GmmlPathway pathway;
	
	BufferedImage bi;
	Graphics2D big;
	
	boolean firstTime = true;
	TexturePaint fillColor;
	Rectangle area; //area in which the rectangles are plotted.
	
	static protected Label label;
	
	// constructor for this class
	GmmlDrawing(GmmlPathway pathway)
	{
		this.pathway = pathway;
		
		setBackground(Color.white);
		
		setPreferredSize(new Dimension((int)(pathway.width/zf),(int)(pathway.heigth/zf)));
		setSize(new Dimension((int)(pathway.width/zf),(int)(pathway.heigth/zf)));
	}

	//init is used to form the JPanel later in the program.
	public void init()
	{
		
		//Dump the stored attributes to the screen.		
		System.out.println("Checking for stored attributes - number: "+pathway.attributes.size());
		pathway.printAttributes();
		
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
/*		if(!nonSelected) {
			for(int i=helpers.length-1; i>=0; i--) {
				if(helpers[i].contains(e.getX(), e.getY())) {
					slastx = (int) (helpers[i].x - e.getX());
					slasty = (int) (helpers[i].y - e.getY());
					clickedHelper = true;
					selectedHelper = i;
					updateHelper(e);
					return;
				}
			}
		}
		
		clickedHelper = false;
		nonSelected = true;
		int CS = 0;
		
		for (int i=pathway.shapes.size()-1; i>=0; i--) {
			if(pathway.shapes[i].contains(e.getX()*zf, e.getY()*zf)){ //if the user presses the mouses on a coordinate which is contained by rect
				
				mousePressedOnObject = true;
				clickedObjectNumber = i;		
				clickedObjectType = 7;
							
				slastx = (int) (pathway.shapes[i].x - e.getX()*zf); //lastx = position pathway.rects[i] - position mouse when pressed
				slasty = (int) (pathway.shapes[i].y - e.getY()*zf);
				
				updateLocation(e);
				
				//Test code for a possible new interactive way to handle objects, this should be closer to genmapp
				nonSelected = false;
				selectedObjectNumber = i;		
				selectedObjectType = 7;
				
				break;
			}
		}
		for (int i=pathway.arcs.length-1; i>=0; i--) {
			if(pathway.arcs[i].contains(e.getX()*zf, e.getY()*zf)){ //if the user presses the mouses on a coordinate which is contained by rect
				
				mousePressedOnObject = true;
				clickedObjectNumber = i;		
				clickedObjectType = 6;
							
				slastx = (int) (pathway.arcs[i].x - e.getX()*zf); //lastx = position pathway.rects[i] - position mouse when pressed
				slasty = (int) (pathway.arcs[i].y - e.getY()*zf);
				
				updateLocation(e);
				
				//Test code for a possible new interactive way to handle objects, this should be closer to genmapp
				nonSelected = false;
				selectedObjectNumber = i;		
				selectedObjectType = 6;
				
				break;
			}
		}
		for (int i=pathway.braces.length-1; i>=0; i--) {
			if(pathway.braces[i].contains(e.getX()*zf, e.getY()*zf)){ //if the user presses the mouses on a coordinate which is contained by rect
				
				mousePressedOnObject = true;
				clickedObjectNumber = i;		
				clickedObjectType = 5;
							
				slastx = (int) (pathway.braces[i].cX - e.getX()*zf); //lastx = position pathway.rects[i] - position mouse when pressed
				slasty = (int) (pathway.braces[i].cY - e.getY()*zf);
				
				updateLocation(e);
			
				//Test code for a possible new interactive way to handle objects, this should be closer to genmapp
				nonSelected = false;
				selectedObjectNumber = i;		
				selectedObjectType = 5;
				
				break;
			}
		}
		for (int i=pathway.lines.length-1; i>=0; i--) {
			if(pathway.lines[i].contains(e.getX()*zf, e.getY()*zf)){ //if the user presses the mouses on a coordinate which is contained by rect
				
				mousePressedOnObject = true;
				clickedObjectNumber = i;		
				clickedObjectType = 4;
							
				slastx = (int) (pathway.lines[i].startx - e.getX()*zf); //lastx = position pathway.rects[i] - position mouse when pressed
				slasty = (int) (pathway.lines[i].starty - e.getY()*zf);
				
				updateLocation(e);
				
				//Test code for a possible new interactive way to handle objects, this should be closer to genmapp
				nonSelected = false;
				selectedObjectNumber = i;		
				selectedObjectType = 4;
				
				break;
			}
		}

		for (int i=pathway.lineshapes.length-1; i>=0; i--) {
			if(pathway.lineshapes[i].contains(e.getX()*zf, e.getY()*zf)){ //if the user presses the mouses on a coordinate which is contained by rect
				
				mousePressedOnObject = true;
				clickedObjectNumber = i;		
				clickedObjectType = 3;
							
				slastx = (int) (pathway.lineshapes[i].startx - e.getX()*zf); //lastx = position pathway.rects[i] - position mouse when pressed
				slasty = (int) (pathway.lineshapes[i].starty - e.getY()*zf);
				
				updateLocation(e);
				
				//Test code for a possible new interactive way to handle objects, this should be closer to genmapp
				nonSelected = false;
				selectedObjectNumber = i;		
				selectedObjectType = 3;
				
				break;
			}
		}
		for (int i=pathway.geneProducts.length-1; i>=0; i--) {
			if(pathway.geneProducts[i].contains(e.getX()*zf, e.getY()*zf)){ //if the user presses the mouses on a coordinate which is contained by rect
				
				mousePressedOnObject = true;
				clickedObjectNumber = i;		
				clickedObjectType = 2;
							
				slastx = (int) (pathway.geneProducts[i].x - e.getX()*zf); //lastx = position pathway.rects[i] - position mouse when pressed
				slasty = (int) (pathway.geneProducts[i].y - e.getY()*zf);
				
				updateLocation(e);
				
				//Test code for a possible new interactive way to handle objects, this should be closer to genmapp
				nonSelected = false;
				selectedObjectNumber = i;		
				selectedObjectType = 2;
				
				break;
			}
		}
		for (int i=pathway.labels.length-1; i>=0; i--) {
			if(pathway.labels[i].contains(e.getX()*zf, e.getY()*zf)){ //if the user presses the mouses on a coordinate which is contained by rect
				
				mousePressedOnObject = true;
				clickedObjectNumber = i;		
				clickedObjectType = 1;
							
				slastx = (int) (pathway.labels[i].x - e.getX()*zf); //lastx = position pathway.rects[i] - position mouse when pressed
				slasty = (int) (pathway.labels[i].y - e.getY()*zf);
				
				updateLocation(e);
				
				//Test code for a possible new interactive way to handle objects, this should be closer to genmapp
				nonSelected = false;
				selectedObjectNumber = i;		
				selectedObjectType = 1;
				
				break;
			}
	}
		repaint();	*/
	} //end of mousePressed

} // end of class