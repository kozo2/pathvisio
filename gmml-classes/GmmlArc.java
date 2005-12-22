import java.awt.geom.Arc2D;

public class GmmlArc {

double x,y,width,height;

	public GmmlArc(double inputx, double inputy, double inputw, double inputh) {
		x=inputx;
		y=inputy;
		width=inputw;
		height=inputh;
		
	} //end of constructor GmmlArc
	
	public boolean contains(int linex, int liney) {
		Arc2D.Double arc = new Arc2D.Double(x-width,y-height,2*width,2*height,0,180,0);
		
		if (arc.contains(linex,liney)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean contains(int mousex, int mousey, int zf) {
		Arc2D.Double arc = new Arc2D.Double(x-width,y-height,2*width,2*height,0,180,0);
		
		if (arc.contains(mousex*zf,mousey*zf)) {
			return true;
		}
		else {
			return false;
		}	
	}
	
} //end of GmmlArc