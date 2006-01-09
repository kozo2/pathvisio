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
import java.awt.Rectangle;

public class GmmlLabel {
	String text, font, fontWeight, fontStyle;
	int x, y, width, height, fontSize;
	Color color;
	
	public GmmlLabel (int x, int y, int width, int height, String text, String font, String fontWeight, String fontStyle, int fontSize, Color color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.font = font;
		this.fontWeight = fontWeight;
		this.fontStyle = fontStyle;
		this.fontSize = fontSize;
		this.color = color;
	}
	public boolean contains (double mousex, double mousey) {
		Rectangle rect = new Rectangle(x, y, width, height);
		boolean contains = rect.contains(mousex, mousey);
		return contains;
	}
	public boolean contains (double mousex, double mousey, double zoomfactor) {
		Rectangle rect = new Rectangle(x, y, width, height);
		boolean contains = rect.contains(mousex * zoomfactor, mousey * zoomfactor);
		return contains;
	}
	
	public void setLocation(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Rectangle[] getHelpers(double zf) {
		Rectangle[] helpers = new Rectangle[1];
		
		helpers[0] = new Rectangle( (int)((x/zf) + (0.5*width/zf)) - 2, (int)((y/zf) + (0.5*height/zf)) - 2, 5, 5);
		
		return helpers;
	}

}
		
	
	
	
	