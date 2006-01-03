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

public class GmmlGeneProduct {
	int x, y, width, height;
	String geneID, ref;
	
	public GmmlGeneProduct(int x, int y, int width, int height, String geneID, String ref) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.geneID = geneID;
		this.ref = ref;
	} //end of constructor
	
	
	//Contains without zoomfactor, for the connections for example
	public boolean contains(double mousex, double mousey) {
		if (x<=mousex && mousex<=x+width && y<=mousey && mousey<=y+height) {
			return true;
		}
		else {
			return false;
		}
	} //end of contains

	//Contains with zoomfactor, for the mouselistener
	public boolean contains(int mousex, int mousey, int zoomfactor) {
		if (x<=mousex*zoomfactor && mousex*zoomfactor<=x+width && y<=mousey*zoomfactor && mousey*zoomfactor<=y+height) {
			return true;
		}
		else {
			return false;
		}
	} //end of contains
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
} //end of GmmlGeneProduct