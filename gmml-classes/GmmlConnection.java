import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D.*;
import java.awt.geom.AffineTransform;

class GmmlConnection {

	GmmlPathway pathway;
	int[][] Connection;
	double[][] anchorPoint;
	double dx;
	double dy;
	boolean test1;
	boolean test2;

	
	public GmmlConnection(GmmlPathway inputpathway){
		pathway = inputpathway;
		Connection = new int[pathway.lineCoord.length][5];
		 /** GmmlConnection checks for each line if it connects two 'shapes'
		   * and which shapes it connects.
		   * the shapetypes of the connected shapes are stored in the 
		   * last two columns of connec and Connections
		   * types:
		   *			0: rectangle from rects
		   *			1: rectangle form shape
		   *			2: ellipse form shape
		   *			3: anchorpoint
		   */

		double[][] tempAnchor = new double[2*pathway.lineCoord.length][2];
	 	int count=0;
		for (int i=0; i < pathway.lineCoord.length; i++){
			Connection[i][0]=i;
			test1=false;
			test2=false;			
			increase(i);
			int j = 0;
			while ((j < pathway.rects.length)&&(!test1&&!test2)) {
				checkRectangles(i,j);
				j++;
			}
			j=0;
			while ((j < pathway.shapeCoord.length)&&(!test1&&!test2)) {
				double w = pathway.shapeCoord[j][2];
				double h = pathway.shapeCoord[j][3];
				if (pathway.shapeType[j] == 0) {
					checkShapeRect(w,h,i,j);
				} else if (pathway.shapeType[j] == 1) {
					checkShapeEllip(w,h,i,j);
				}
				j++;
			}	
			if (!test1) {
				tempAnchor[count][0]=pathway.lineCoord[i][0];
				tempAnchor[count][1]=pathway.lineCoord[i][1];
				Connection[i][1] = count;
				Connection[i][3] = 3;
				count++;		
			}
			if (!test2) {
				tempAnchor[count][0]=pathway.lineCoord[i][2];
				tempAnchor[count][1]=pathway.lineCoord[i][3];
				Connection[i][2] = count;
				Connection[i][4] = 3;
				count++;
			}				
		}// end of for loop with lines
		anchorPoint = new double[count][2];
		for (int i = 0; i<count; i++) {
			tempAnchor[i][0]=anchorPoint[i][0];
			tempAnchor[i][1]=anchorPoint[i][1];
		}
		System.out.println("aantal ankerpunten: " + count);
	}// end of gmmlConnections()

	public void increase(int i){
		double theta=Math.atan(Math.abs((pathway.lineCoord[i][3]-pathway.lineCoord[i][1])/(pathway.lineCoord[i][2]-pathway.lineCoord[i][0])));
		dx=Math.cos(theta);
		dy=Math.sin(theta);
		if (pathway.lineCoord[i][0]>pathway.lineCoord[i][2]){
			dx=-dx;
		}
		if (pathway.lineCoord[i][1]>pathway.lineCoord[i][3]){
			dy=-dy;
		}
	}// end of increase
		
	public void checkRectangles(int i, int j){
		Rectangle temprect = pathway.rects[j];
		int n=0;
		while (!test1&&(n<25)){
			if (temprect.contains((pathway.lineCoord[i][0]-(n*dx)), (pathway.lineCoord[i][1]-(n*dy))) && (!test1)) {
				//System.out.println("Hit for 1 coord: "+j);
				Connection[i][1]=j;
				Connection[i][3]=0;
				test1=true;
			}
			n++;
		}
		n=0;	
		while (!test2&&(n<25)) {
			if (temprect.contains(pathway.lineCoord[i][2]+(n*dx), pathway.lineCoord[i][3]+(n*dy)) && (!test2)) {
				//System.out.println("Hit for 2 coord: "+j);
				Connection[i][2]=j;
				Connection[i][4]=0;										
				test2=true;
			}
			n++;
		}
	}// checkRectangles	
		
	public void checkShapeRect(double w, double h, int i, int j){	
		Rectangle2D tempshape = new Rectangle2D.Double(pathway.shapeCoord[j][0],pathway.shapeCoord[j][1],2*w,2*h);
		AffineTransform at = AffineTransform.getTranslateInstance(w/2, h/2);				
		at.rotate(Math.toRadians(pathway.shapeCoord[j][4]));					
		int n=0;		
		while (!test1&&(n<25)){
			if (tempshape.contains((pathway.lineCoord[i][0]-(n*dx)), (pathway.lineCoord[i][1]-(n*dy))) && (!test1)) {
				//System.out.println("Hit for 1 coord: "+j);						
				Connection[i][1]=j;
				Connection[i][3]=pathway.shapeType[j]+1;
				test1=true;
			}
			n++;
		}
		n=0;	
		while (!test2&&(n<25)) {
			if (tempshape.contains(pathway.lineCoord[i][2]+(n*dx), pathway.lineCoord[i][3]+(n*dy)) && (!test2)) {
				//System.out.println("Hit for 2 coord: "+j);
				Connection[i][2]=j;
				Connection[i][4]=pathway.shapeType[j]+1;				
				test2=true;
			}
			n++;
		}			
	}// end of checkShapesRect
		
	public void checkShapeEllip(double w, double h, int i, int j) {
		Ellipse2D tempshape = new Ellipse2D.Double(pathway.shapeCoord[j][0],pathway.shapeCoord[j][1],2*w,2*h);
		AffineTransform at = AffineTransform.getTranslateInstance(w/2, h/2);
		at.rotate(Math.toRadians(pathway.shapeCoord[j][4]));							int n=0;
		int m=0;
		while (!test1&&(m<25)){
			if (tempshape.contains((pathway.lineCoord[i][0]-(m*dx)), (pathway.lineCoord[i][1]-(m*dy)))) {
				//System.out.println("Hit for 1 coord: "+j);
				Connection[i][1]=j;
				Connection[i][3]=pathway.shapeType[j]+1;
				test1=true;
			}
			m++;
		}
		m=0;	
		while (!test2&&(m<25)) {
			if (tempshape.contains(pathway.lineCoord[i][2]+(m*dx), pathway.lineCoord[i][3]+(m*dy))){
				//System.out.println("Hit for 2 coord: "+j);
				Connection[i][2]=j;
				Connection[i][4]=pathway.shapeType[j]+1;				
				test2=true;
			}
			m++;
		}			
	}// end of checkShapes1

}// end of class