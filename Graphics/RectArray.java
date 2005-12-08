import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.image.*;

public class RectArray {
	
	RectArray rectArray = new RectArray();
	Rectangle[] rects = new Rectangle[4];
 	Rectangle rect = new Rectangle();
	 
	public void RectArray (int [][] rectCoord) {
		createRectArray(rectCoord); //rectCoord comes from Main.
	}
		
	public void createRectArray(int[][] rectCoord) {
		for (int j = 0; j < 4; j++) {
			rects[j].setBounds(rectCoord[j][1],rectCoord[j][2],rectCoord[j][3],rectCoord[j][4]);
		}
	}	
}