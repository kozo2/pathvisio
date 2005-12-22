/*import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.JApplet;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;*/

public class GmmlBrace {
	
	double cX, cY, w, ppo;
	int or; //or is the orientation: 0=top, 1=right, 2=bottom, 3=left
	
	public GmmlBrace(double inputcX, double inputcY, double inputw, double inputppo, int inputor) {
		cX=inputcX;
		cY=inputcY;
		w=inputw;
		ppo=inputppo;
		or=inputor;
		
	} //end constructor GmmlBrace
	
	//Contains without zoomfactor, for the connections for example
	public boolean contains(double linex, double liney) {
		if (or==0 || or==2) {
			if (cX-0.5*w<=linex && linex<=cX+0.5*w && cY-0.5*ppo<=liney && liney<=cY+0.5*ppo) {
				return true;
			}
			else {
				return false;
			}
		} //end if orientation
		else {
			if (cY-0.5*w<=liney && liney<=cY+0.5*w && cX-0.5*ppo<=linex && linex<=cX+0.5*ppo) {
				return true;
			}
			else {
				return false;
			}
		} // end else orientation
	} //end of contains

	//Contains with zoomfactor, for the mouselistener
	public boolean contains(double mousex, double mousey, double zf) {
		if (or==0 || or==2) {
			if (cX-0.5*w<=mousex*zf && mousex*zf<=cX+0.5*w && cY-0.5*ppo<=mousey*zf && mousey*zf<=cY+0.5*ppo) {
				return true;
			}
			else {
				return false;
			}
		} //end if orientation
		else {
			if (cY-0.5*w<=mousey*zf && mousey*zf<=cY+0.5*w && cX-0.5*ppo<=mousex*zf && mousex*zf<=cX+0.5*ppo) {
				return true;
			} 
			else {
				return false;
			}
		} // end else orientation

	} //end of contains


	/*Arc2D.Double[] arcsOfBrace = new Arc2D.Double[4]; //4 Arcs are used to create a brace
	Line2D.Double[] linesOfBrace = new Line2D.Double[2];; //2 Lines are used to creata a brace
	Line2D.Double[] lines = new Line2D.Double[2];
	
	public GmmlBrace (double cX, double cY, double w, double ppo, int or){
		for (int i=0; i<2; i++){
			linesOfBrace[i] = new Line2D.Double();
		}
	
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
	
	} //end of constructor GmmlBrace
	*/
	
	

} //end of GmmlBrace