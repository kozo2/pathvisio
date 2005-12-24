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

import java.awt.geom.Arc2D;
import java.awt.Color;

public class GmmlArc {

double x,y,width,height;
Color color;

	public GmmlArc(double inputx, double inputy, double inputw, double inputh, String inputcolor) {
		x=inputx;
		y=inputy;
		width=inputw;
		height=inputh;
		color=GmmlColor.convertColor(inputcolor);
		
	} //end of constructor GmmlArc
	
	public boolean contains(double linex, double liney) {
		Arc2D.Double arc = new Arc2D.Double(x-width,y-height,2*width,2*height,0,180,0);
		
		if (arc.contains(linex,liney)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean contains(double mousex, double mousey, double zf) {
		Arc2D.Double arc = new Arc2D.Double(x-width,y-height,2*width,2*height,0,180,0);
		
		if (arc.contains(mousex*zf,mousey*zf)) {
			return true;
		}
		else {
			return false;
		}	
	}
	
	public void setLocation(double newx, double newy) {
		x=newx;
		y=newy;
	}
	
} //end of GmmlArc
