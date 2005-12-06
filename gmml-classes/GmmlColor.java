/**********************************************************
/*
/*        The GMML Color class by Hakim 5/12/2005
/*
/*********************************************************/

//TODO add something to find and use int colors! This is not supported atm! 
//And the Hex might make mistakes on 6 sized strings with random values

public class GmmlColor {
	double[] color = new double[3];
	
	//Constructor
	public static void main(String[] args) {
        //Main can be empty        
    }
   
	//GetColor method
	public double[] getColor() {
		return color;
	}
	
	//Store a color of unknown type
	public void storeColor(String scolor) {
		if(!storeStringColor(scolor)) {
			if(!storeHexColor(scolor)) {
				System.out.println("Not a valid color value!");
			} else {
				System.out.println("Found a hex color value.");
			}
		} else {
			System.out.println("Found a String color value.");
		}
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
				//Convert a string into an integer
				int r = Integer.parseInt( red.trim(), 16 /* radix */ );
				int g = Integer.parseInt( green.trim(), 16 /* radix */ );
				int b = Integer.parseInt( blue.trim(), 16 /* radix */ );
			
				//Make the desired 0.0 - 1.0 doubles out of the integers			
				color[0] = r / 255d;
				color[1] = g / 255d;
				color[2] = b / 255d;
				
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
			if (trimcolor == colortable[i][0]) {
				//Insert the color as doubles
				color[0] = Double.parseDouble(colortable[i][1]);
				color[1] = Double.parseDouble(colortable[i][2]);
				color[2] = Double.parseDouble(colortable[i][3]);
				
				return true;	//No errors
			}
		}
		return false;	//Not a recognised color name
	}
		
		
}