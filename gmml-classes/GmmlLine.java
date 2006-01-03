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

public class GmmlLine {
	double startx, starty, endx, endy;
	int type, style;
	Color color;
	
	public GmmlLine (double startx, double starty, double endx, double endy, int type, int style, Color color) {
		this.startx = startx;
		this.starty = starty;
		this.endx = endx;
		this.endy = endy;
		this.type = type;
		this.style = style;
		this.color = color;
	}
	
	public boolean contains (double x, double y) {
		Line2D.Double templine = new Line2D.Double(startx, starty, endx, endy);
		boolean contains = templine.contains(x, y);
		return contains;
	}
	
	public boolean contains (double x, double y, double zoomfactor) {
		Line2D.Double templine = new Line2D.Double(startx, starty, endx, endy);
		boolean contains = templine.contains(x * zoomfactor, y * zoomfactor);
		return contains;
	}

	public void setLocation(double startx, double starty, double endx, double endy){
		this.startx = startx;
		this.starty = starty;
		this.endx = endx;
		this.endy = endy;
	}
}
	