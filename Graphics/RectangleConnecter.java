import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;


public class RectangleConnecter {

	public static void main(String[] args) {
	ConnectionCheck connectioncheck= new ConnectionCheck();
	for (int i=0 ; i<connectioncheck.connectors.length ; i++) {
	System.out.println(" van blok " + connectioncheck.connectors[i][0]+ " naar blok " + connectioncheck.connectors[i][1]);
		}
	}
}

class ConnectionCheck{

	// rectangles contains the constructors of a graphics object rechtangle
	int[][] rectangles={{0,0,25,25},{30,40,15,30},{30,80,20,20},{60,60,30,15},{100,0,40,50}};
	// linepoints contains the coordinates of the lines
	int[][][] linepoints={{{13,13},{38,55}},{{13,13},{40,90}},{{38,55},{40,90}},{{40,90},{75,68}},{{120,25},{38,55}},{{75,68},{13,13}},{{120,25},{160,25}}};
	// connectors will store the rectangles that are connected by a set of points
	int[][] connectors=new int[linepoints.length][2];
	
	public void checkconnection(){
		// finds the blocks that are connected by the line trhough two points
		for (int i = 0; i < linepoints.length; i++) {
			for (int j = 0; j < rectangles.length; j++) {
				// a temporary rectangle temprectj is created
				Rectangle temprectj=new Rectangle(rectangles[j][0],rectangles[j][1],rectangles[j][2],rectangles[j][3]);
				if (temprectj.contains(linepoints[i][0][0], linepoints[i][0][1])) {
					for (int k = 0; k < rectangles.length; k++) {
						Rectangle temprectk=new Rectangle(rectangles[k][0],rectangles[k][1],rectangles[k][2],rectangles[k][3]);
						if (k!=j && temprectk.contains(linepoints[i][1][0],linepoints[i][1][1])) {
							connectors[i][0]=j;
							connectors[i][1]=k;
							}//close if
						}//close for k
					}//close if
				}//close for j
			}//close for i
		//return connectors;
	}//close checkconnection
}


	