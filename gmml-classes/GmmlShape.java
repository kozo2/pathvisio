/*
Copyright 2005 H.C. Achterberg, R.M.H. Besseling, I.Kaashoek, 
M.M.Palm, E.D Pelgrim, BiGCaT (http://www.BiGCaT.unimaas.nl/)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and 
limitations under the License.
*/

import java.awt.Color;
import java.awt.Polygon;

public class GmmlShape {

double x,y,width,height,rotation;
int type;
Color color;

	public GmmlShape(double x, double y, double width, double height, int type, String color, double rotation) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = GmmlColor.convertStringToColor(color);
		this.type = type;
		this.rotation = rotation;			
	} //end of GmmlShape constructor
	
	public boolean contains(double mousex, double mousey) {
	
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
				
				if (temp.contains(mousex, mousey)) {
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
	
	public void setLocation(double x, double y){
		this.x = x;
		this.y = y;
	}
	

} //end of GmmlShape