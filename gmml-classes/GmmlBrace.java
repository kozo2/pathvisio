import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.JApplet;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;

public class GmmlBrace {

	Arc2D.Double[] arcsOfBrace = new Arc2D.Double[4]; //4 Arcs are used to create a brace
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
	
	public Line2D.Double[] getLine() {
		lines=linesOfBrace;
		return lines;
	} //end of getLine(int i)
} //end of GmmlBrace