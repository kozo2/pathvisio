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
	
	public GmmlGeneProduct(int inputx, int inputy, int inputwidth, int inputheight, String inputgeneID, String inputref) {
		x=inputx;
		y=inputy;
		width=inputwidth;
		height=inputheight;
		geneID=inputgeneID;
		ref=inputref;
	} //end of constructor
	
	
	//Contains without zoomfactor, for the connections for example
	public boolean contains(double linex, double liney) {
		if (x<=linex && linex<=x+width && y<=liney && liney<=y+height) {
			return true;
		}
		else {
			return false;
		}
	} //end of contains

	//Contains with zoomfactor, for the mouselistener
	public boolean contains(int mousex, int mousey, int zf) {
		if (x<=mousex*zf && mousex*zf<=x+width && y<=mousey*zf && mousey*zf<=y+height) {
			return true;
		}
		else {
			return false;
		}
	} //end of contains
	
	public void setLocation(int inputx, int inputy) {
		x = inputx;
		y = inputy;
	}
	
} //end of GmmlGeneProduct