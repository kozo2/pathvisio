import java.awt.Color;

public class GmmlGeneProduct {
	int x, y, width, height;
	String geneID, ref;
	
	public GmmlGeneProduct(int inputx, int inputy, int inputwidth, int inputheight, String inputgeneID, String inputref) {
		x=inputx;
		y=inputy;
		width=inputwidth;
		height=inputheight;
		geneID=inputgeneID;
		ref=inputref;
	} //end of constructor
	
	
	//Contains without zoomfactor, for the connections for example
	public boolean contains(double linex, double liney) {
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
	
	public void setLocation(int inputx, int inputy) {
		x = inputx;
		y = inputy;
	}
	
} //end of GmmlGeneProduct