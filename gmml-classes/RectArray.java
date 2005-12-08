import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.image.*;

public class RectArray {
	
	Rectangle[] rects;
	 
	RectArray (int [][] rectCoord) {
		rects = new Rectangle[rectCoord.length];
		createRectArray(rectCoord); //rectCoord comes from Main.
	}
		
	public void createRectArray(int[][] rectCoord) {
		for (int j = 0; j < rectCoord.length; j++) {
			System.out.println("Creating rectangle "+j);
			Rectangle temp = new Rectangle(rectCoord[j][0],rectCoord[j][1],rectCoord[j][2],rectCoord[j][3]);
			rects[j] = temp;
		}
	}	
}