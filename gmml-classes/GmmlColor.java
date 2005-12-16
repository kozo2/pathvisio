/**********************************************************
/*
/*        The GMML Color class by Hakim 5/12/2005
/*
/*********************************************************/

import java.awt.*;

//TODO add something to find and use int colors! This is not supported atm! 

public class GmmlColor {
	float[] color = new float[3];
	
	//Constructor
	public GmmlColor() {
        //Main can be empty       
    }

	public GmmlColor(String input) {
        storeColor(input);        
    }
   
	//GetColor method returning a Color
	public Color getColor() {
		Color returncolor = new Color(color[0],color[1],color[2]);
		return returncolor;
	}
	
	//GetColor method returning a float[]
	public float[] getColorFloat() {
		return color;
	}
	
	//Store a color of unknown type
	public void storeColor(String scolor) {
		if(!storeStringColor(scolor)) {
			if(!storeHexColor(scolor)) {
				if(Integer.parseInt(scolor.trim()) != -1) {
					System.out.println("Not a valid color value!");
				} else {
					color[0] = 0;
					color[1] = 0;
					color[2] = 0;
				}
			}
		}
	}

	//Convert a string into a float[3] a color of unknown type
	public static float[] convertColorFloat(String scolor) {
		GmmlColor temp = new GmmlColor(scolor);
		float[] fcolor = temp.getColorFloat();
		return fcolor;
	}
	
	//Convert a string into a float[3] a color of unknown type
	public static Color convertColor(String scolor) {
		GmmlColor temp = new GmmlColor(scolor);
		Color returncolor = temp.getColor();
		return returncolor;
	}	

		
	//StoreColor method
	public boolean storeHexColor(String scolor) {
		//Trim the string and break it into bytes
		String trimcolor = scolor.trim();
		
		if(trimcolor.length()==6) {
			//Break appart the string
			String red = ""+trimcolor.charAt(0)+trimcolor.charAt(1);
			String green = ""+trimcolor.charAt(2)+trimcolor.charAt(3);
			String blue = ""+trimcolor.charAt(4)+trimcolor.charAt(5);
			
			try {
				//Convert a hex string into an integer
				int r = Integer.parseInt( red.trim(), 16 /* radix */ );
				int g = Integer.parseInt( green.trim(), 16 /* radix */ );
				int b = Integer.parseInt( blue.trim(), 16 /* radix */ );
			
				//Make the desired 0.0 - 1.0 doubles out of the integers			
				color[0] = r / 255f;
				color[1] = g / 255f;
				color[2] = b / 255f;
				
				return true;	//No errors
			}
			catch (NumberFormatException e) {}
			//Here the string is length 6 but the content is not propper Hex
		}
		
		return false;	//Not a propper Hex Value!
	}
	public boolean storeStringColor(String scolor) {
		//Color string table
		String[][] colortable = {
		{"Aqua","0","1","1"},
		{"Black","0","0","0"},
		{"Blue","0","0","1"},
		{"Fuchsia","1","0","1"},
		{"Gray","0.5","0.5","0.5"},
		{"Green","0","0.5","0"},
		{"Lime","0","1","0"},
		{"Maroon","0.5","0","0"},
		{"Navy","0","0","0.5"},
		{"Olive","0.5","0.5","0"},
		{"Purple","0.5","0","0.5"},
		{"Red","1","0","0"},
		{"Silver","0.75","0.75","0.75"},
		{"Teal","0","0.5","0.5"},
		{"White","1","1","1"},
		{"Yellow","1","1","0"}};

		String trimcolor = scolor.trim();
		
		//Test each known color name
		for(int i=0; i < 16; i++) { //The length of the table is 16 hardcoded
			if (trimcolor.equalsIgnoreCase(colortable[i][0])) {
				//Insert the color as doubles
				color[0] = Float.parseFloat(colortable[i][1]);
				color[1] = Float.parseFloat(colortable[i][2]);
				color[2] = Float.parseFloat(colortable[i][3]);
				
				return true;	//No errors
			}
		}
		return false;	//Not a recognised color name
	}
		
		
}