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

import java.awt.*;
import java.awt.geom.Line2D;

public class GmmlLineShape {
	double startx, starty, endx, endy;
	int type;
	Color color;
	
	public GmmlLineShape (double inputstartx, double inputstarty, double inputendx, double inputendy, Color inputcolor, int inputtype) {
		startx = inputstartx;
		starty = inputstarty;
		endx = inputendx;
		endy = inputendy;
		type = inputtype;
		color = inputcolor;
	}
	
	public boolean contains (double x, double y) {
		Line2D.Double templine = new Line2D.Double(startx, starty, endx, endy);
		boolean contains = templine.contains(x, y);
		return contains;
	}
	
	public boolean contains (double x, double y, double zf) {
		Line2D.Double templine = new Line2D.Double(startx, starty, endx, endy);
		boolean contains = templine.contains(x*zf, y*zf);
		return contains;
	}

	public void setLocation(double newstartx, double newstarty, double newendx, double newendy){
		startx = newstartx;
		starty = newstarty;
		endx = newendx;
		endy = newendy;
	}
}