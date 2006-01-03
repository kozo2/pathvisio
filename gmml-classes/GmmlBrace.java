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

public class GmmlBrace {
	
	double cX, cY, w, ppo;
	int or; //or is the orientation: 0=top, 1=right, 2=bottom, 3=left
	Color color;
	
	public GmmlBrace(double centerX, double centerY, double width, double ppo, int orientation, String color) {
		cX=centerX;
		cY=centerY;
		w=width;
		this.ppo=ppo;
		or=orientation;
		this.color=GmmlColor.convertStringToColor(color);
		
	} //end constructor GmmlBrace
	
	//Contains without zoomfactor, for the connections for example
	public boolean contains(double mousex, double mousey) {
		if (or==0 || or==2) {
			if (cX-0.5*w<=mousex&& mousex<=cX+0.5*w && cY-0.5*ppo<=mousey && mousey<=cY+0.5*ppo) {
				return true;
			}
			else {
				return false;
			}
		} //end if orientation
		else {
			if (cY-0.5*w<=mousey && mousey<=cY+0.5*w && cX-0.5*ppo<=mousex && mousex<=cX+0.5*ppo) {
				return true;
			}
			else {
				return false;
			}
		} // end else orientation
	} //end of contains

	//Contains with zoomfactor, for the mouselistener
	public boolean contains(double mousex, double mousey, double zoomfactor) {
		if (or==0 || or==2) {
			if (cX-0.5*w<=mousex*zoomfactor && mousex*zoomfactor<=cX+0.5*w && cY-0.5*ppo<=mousey*zoomfactor && mousey*zoomfactor<=cY+0.5*ppo) {
				return true;
			}
			else {
				return false;
			}
		} //end if orientation
		else {
			if (cY-0.5*w<=mousey*zoomfactor && mousey*zoomfactor<=cY+0.5*w && cX-0.5*ppo<=mousex*zoomfactor && mousex*zoomfactor<=cX+0.5*ppo) {
				return true;
			} 
			else {
				return false;
			}
		} // end else orientation

	} //end of contains
	
	public void setLocation(double centerX, double centerY) {
		cX = centerX;
		cY = centerY;
	}

} //end of GmmlBrace