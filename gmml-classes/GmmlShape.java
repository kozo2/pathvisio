import java.awt.Color;

public class GmmlShape {

double x,y,width,height;
int type;
Color color;

	public GmmlShape(double inputx, double inputy, double inputw, double inputh, int inputtype, String inputcolor, double inputrotation) {
		x=inputx;
		y=inputy;
		width=inputw;
		height=inputh;
		color=GmmlColor.convertColor(inputcolor);
		type=inputtype;
		
	} //end of GmmlShape constructor
	
	

} //end of GmmlShape