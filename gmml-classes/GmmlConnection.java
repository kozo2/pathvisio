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
		 /** GmmlConnection checks for each line if it connects two 'shapes'
		   * and which shapes it connects.
		   * the shapetypes of the connected shapes are stored in the 
		   * last two columns of Connections
		   * types:
		   *			0: rectangle from rects
		   *			1: rectangle form shape
		   *			2: ellipse form shape
		   *			3: anchorpoint
		   *			4: arc
		   *			5: label
		   *			6: brace
		   */

		pathway = inputpathway;
		Connection = new int[pathway.lines.length][5];
		double[][] tempAnchor = new double[2*pathway.lines.length][2];
	 	int count=0;
		for (int i=0; i < pathway.lines.length; i++){
			double x1 = pathway.lines[i].startx;
			double y1 = pathway.lines[i].starty;
			double x2 = pathway.lines[i].endx;
			double y2 = pathway.lines[i].endy;
			Connection[i][0]=i;
			test1=false;
			test2=false;			
			increase(i, x1, x2, y1, y2);
			int j = 0;
			while (!(test1&&test2)&&(j < pathway.geneProducts.length)) {
				checkGeneProduct(i,j,x1,y1,x2,y2);
				j++;
			}
			j=0;
			while (!(test1&&test2)&&(j < pathway.shapes.length)) {
				checkShape(i,j,x1,y1,x2,y2);
				j++;
			}
			j=0;
			while (!(test1&&test2)&&(j < pathway.arcs.length)){
				checkArc(i,j,x1,y1,x2,y2);
				j++;
			}
			j=0;			
			while (!(test1&&test2)&&(j < pathway.labels.length)){
				checkLabel(i,j,x1,y1,x2,y2);
				j++;
			}	
			j=0;
			while (!(test1&&test2)&&(j < pathway.braces.length)){
				checkBrace(i,j,x1,y1,x2,y2);
				j++;
			}
					
			if (!test1) {
				tempAnchor[count][0]=x1;
				tempAnchor[count][1]=y1;
				Connection[i][1] = count;
				Connection[i][3] = 3;
				count++;		
			}
			if (!test2) {
				tempAnchor[count][0]=x2;
				tempAnchor[count][1]=y2;
				Connection[i][2] = count;
				Connection[i][4] = 3;
				count++;
			}				
		}// end of for loop with lines
		double[][] anchorPoint = new double[count][2];
		for (int i = 0; i<count; i++) {
			tempAnchor[i][0]=anchorPoint[i][0];
			tempAnchor[i][1]=anchorPoint[i][1];
		}
		System.out.println("aantal ankerpunten: " + count);
	}// end of gmmlConnections()

	public void increase(int i, double x1, double y1, double x2, double y2){
		/** calculates the increase of line i
		  * this value is used to extend the lines
		  */
		  
		double theta=Math.atan(Math.abs((y2-y1)/(x2-x1)));
		dx=Math.cos(theta);
		dy=Math.sin(theta);
		if (x1>x2){
			dx=-dx;
		}
		if (y1>y2){
			dy=-dy;
		}
	}// end of increase
		
	public void checkGeneProduct(int i, int j, double x1, double y1, double x2, double y2){
		/** checks for connections of a line with geneproducts */
		int n=0;
		while (!test1&&(n<25)){
			if ((!test1)&&(pathway.geneProducts[j].contains(x1+n*dx,y1+n*dy))){
				Connection[i][1]=j;
				Connection[i][3]=0;
				test1=true;
			}
			n++;
		}
		n=0;
		while (!test2&&(n<25)){
			if ((!test2)&&(pathway.geneProducts[j].contains(x2-n*dx,y2-n*dy))){
				Connection[i][2]=j;
				Connection[i][4]=0;
				test2=true;
			}
			n++;
		}
	}// checkGeneProduct	
		
	public void checkShape(int i, int j, double x1, double y1, double x2, double y2){
		/** checks for connections of a line with shapes */
		int n=0;
		while (!test1&&(n<25)){
			if ((!test1)&&(pathway.shapes[j].contains(x1+n*dx,y1+n*dy))){
				Connection[i][1]=j;
				Connection[i][3]=0;
				test1=true;
			}
			n++;
		}
		n=0;
		while (!test2&&(n<25)){
			if ((!test2)&&(pathway.shapes[j].contains(x2-n*dx,y2-n*dy))){
				Connection[i][2]=j;
				Connection[i][4]=0;
				test2=true;
			}
			n++;
		}
	}// checkGeneProduct	
		
	public void checkArc(int i, int j, double x1, double y1, double x2, double y2){
		int n=0;
		while (!test1&&(n<25)){
			if ((!test1)&&(pathway.arcs[j].contains(x1+n*dx,y1+n*dy))){
				Connection[i][1]=j;
				Connection[i][3]=4;
				test1=true;
			}
			n++;
		}
		n=0;
		while (!test2&&(n<25)){
			if ((!test2)&&(pathway.arcs[j].contains(x2-n*dx,y2-n*dy))){
				Connection[i][2]=j;
				Connection[i][4]=4;
				test2=true;
			}
			n++;
		}
	}// end of checkArc
	
	public void checkLabel(int i, int j, double x1, double y1, double x2, double y2){
		int n=0;
		while (!test1&&(n<25)){
			if ((!test1)&&(pathway.labels[j].contains(x1+n*dx,y1+n*dy))){
				Connection[i][1]=j;
				Connection[i][3]=5;
				test1=true;
			}
			n++;
		}
		n=0;
		while (!test2&&(n<25)){
			if ((!test2)&&(pathway.labels[j].contains(x2-n*dx,y2-n*dy))){
				Connection[i][2]=j;
				Connection[i][4]=5;
				test2=true;
			}
			n++;
		}		
	}// end of checkLabel
	
	public void checkBrace(int i, int j, double x1, double y1, double x2, double y2){
		int n=0;
		while (!test1&&(n<25)){
			if ((!test1)&&(pathway.braces[j].contains(x1+n*dx,y1+n*dy))){
				Connection[i][1]=j;
				Connection[i][3]=6;
				test1=true;
			}
			n++;
		}
		n=0;
		while (!test2&&(n<25)){
			if ((!test2)&&(pathway.braces[j].contains(x2-n*dx,y2-n*dy))){
				Connection[i][2]=j;
				Connection[i][4]=6;
				test2=true;
			}
			n++;
		}		
	}// end of checkBrace			
			
}// end of class
			