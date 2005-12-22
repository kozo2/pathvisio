import java.awt.Color;

public class GmmlGeneProduct {
	int x, y, width, height;
	Color color;
	String text, ref;
	
	public GmmlGeneProduct(int inputx, int inputy, int inputwidth, int inputheight, Color inputcolor, String inputtext, String inputref) {
		x=inputx;
		y=inputy;
		width=inputwidth;
		height=inputheight;
		color=inputcolor;
		text=inputtext;
		ref=inputref;
	} //end of constructor
	
	
	//Contains without zoomfactor, for the connections for example
	public boolean contains(int linex, int liney) {
		if (x<=linex && linex<=x+width && y<=liney && liney<=y+height) {
			return true;
		}
		else {
			return false;
		}
	} //end of contains

	//Contains with zoomfactor, for the mouselistener
	public boolean contains(int mousex, int mousey, int zf) {
		if (x<=mousex*zf && mousex*zf<=x+width && y<=mousey*zf && mousey*zf<=y+height) {
			return true;
		}
		else {
			return false;
		}
		
	} //end of contains
	
} //end of GmmlGeneProduct