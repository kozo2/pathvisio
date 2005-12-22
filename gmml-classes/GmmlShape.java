import java.awt.Color;
import java.awt.Polygon;

public class GmmlShape {

double x,y,width,height,rotation;
int type;
Color color;

	public GmmlShape(double inputx, double inputy, double inputw, double inputh, int inputtype, String inputcolor, double inputrotation) {
		x=inputx;
		y=inputy;
		width=inputw;
		height=inputh;
		color=GmmlColor.convertColor(inputcolor);
		type=inputtype;
		rotation=inputrotation;		
		
	} //end of GmmlShape constructor
	
	public boolean contains(double linex,double liney) {
	
		if (type==0) {				
				double theta = Math.toRadians(rotation);
				double[] rot = new double[2];
				
				rot[0] = Math.cos(theta);
				rot[1] = Math.sin(theta);
				
				int[] xs = new int[4];
				int[] ys = new int[4];
				
				xs[1]= (int)(0.5*width*rot[0]-0.5*height*rot[1]); //upper right
				xs[2]= (int)(0.5*width*rot[0]+0.5*height*rot[1]); //lower right
				xs[3]= (int)(-0.5*width*rot[0]+0.5*height*rot[1]); //lower left
				xs[4]= (int)(-0.5*width*rot[0]-0.5*height*rot[1]); //upper left
				
				ys[1]= (int)(0.5*width*rot[1]+0.5*height*rot[0]); //upper right
				ys[1]= (int)(0.5*width*rot[1]-0.5*height*rot[0]); //lower right
				ys[1]= (int)(-0.5*width*rot[1]-0.5*height*rot[0]); //lower left
				ys[1]= (int)(-0.5*width*rot[1]+0.5*height*rot[0]); //upper left
				
				Polygon temp = new Polygon(xs,ys,4);
				
				if (temp.contains(linex,liney)) {
					return true;
				}
				else {
					return false;
				}
				
		}
		else {
			return false;
		}
		
	}
	

} //end of GmmlShape