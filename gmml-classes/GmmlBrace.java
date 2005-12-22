import java.awt.Color;

public class GmmlBrace {
	
	double cX, cY, w, ppo;
	int or; //or is the orientation: 0=top, 1=right, 2=bottom, 3=left
	Color color;
	
	public GmmlBrace(double inputcX, double inputcY, double inputw, double inputppo, int inputor, String inputcolor) {
		cX=inputcX;
		cY=inputcY;
		w=inputw;
		ppo=inputppo;
		or=inputor;
		color=GmmlColor.convertColor(inputcolor);
		
	} //end constructor GmmlBrace
	
	//Contains without zoomfactor, for the connections for example
	public boolean contains(double linex, double liney) {
		if (or==0 || or==2) {
			if (cX-0.5*w<=linex && linex<=cX+0.5*w && cY-0.5*ppo<=liney && liney<=cY+0.5*ppo) {
				return true;
			}
			else {
				return false;
			}
		} //end if orientation
		else {
			if (cY-0.5*w<=liney && liney<=cY+0.5*w && cX-0.5*ppo<=linex && linex<=cX+0.5*ppo) {
				return true;
			}
			else {
				return false;
			}
		} // end else orientation
	} //end of contains

	//Contains with zoomfactor, for the mouselistener
	public boolean contains(double mousex, double mousey, double zf) {
		if (or==0 || or==2) {
			if (cX-0.5*w<=mousex*zf && mousex*zf<=cX+0.5*w && cY-0.5*ppo<=mousey*zf && mousey*zf<=cY+0.5*ppo) {
				return true;
			}
			else {
				return false;
			}
		} //end if orientation
		else {
			if (cY-0.5*w<=mousey*zf && mousey*zf<=cY+0.5*w && cX-0.5*ppo<=mousex*zf && mousex*zf<=cX+0.5*ppo) {
				return true;
			} 
			else {
				return false;
			}
		} // end else orientation

	} //end of contains
	
	public void setLocation(double newx, double newy) {
		cX=newx;
		cY=newy;
	}

} //end of GmmlBrace