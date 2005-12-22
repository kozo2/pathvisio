import java.awt.Color;
import java.awt.Rectangle;

public class GmmlLabel {
	String text, font, fontWeight, fontStyle;
	int x, y, width, height, fontSize;
	Color color;
	
	public GmmlLabel (int inputx, int inputy, int inputwidth, int inputheight, String inputtext, String inputfont, String inputfontWeight, String inputfontStyle, int inputfontSize, Color inputcolor) {
		x = inputx;
		y = inputy;
		width = inputwidth;
		height = inputheight;
		text = inputtext;
		font = inputfont;
		fontWeight = inputfontWeight;
		fontStyle = inputfontStyle;
		fontSize = inputfontSize;
		color = inputcolor;
	}
	public boolean contains (double inputx, double inputy) {
		Rectangle rect = new Rectangle(x, y, width, height);
		boolean contains = rect.contains(inputx, inputy);
		return contains;
	}
	public boolean contains (double inputx, double inputy, double zf) {
		Rectangle rect = new Rectangle(x, y, width, height);
		boolean contains = rect.contains(inputx * zf, inputy * zf);
		return contains;
	}
	
	public void setLocation(int newx, int newy){
		x = newx;
		y = newy;
	}

}
		
	
	
	
	