/**********************************************************
/*
/*        The GMML Graphics class by Hakim 5/12/2005
/*
/*********************************************************/

//WORK IN PROGRESS!

public class GmmlGraphics {

	//Coordinates
	int[] boardsize = new int[2];		//Board sizes (width + height)
	int[] windowsize = new int[2];	//Window sizes (width + height)
	int[] mapinfo = new int[2]; 		//MapInfo left + top
	int[] center = new int[2];			//Center (x + y)
	int[] size = new int[2];			//Sizes (width + height)
	int[] start = new int[2];			//Start point (x + y)
	int[] end = new int[2];				//End point (x + y)
	double rotation;						//Rotation
	String orientation;					//Orientation
	//Fonts
	String fontname;    					//The font to use
	int fontsize;       					//The size
	String fontweight;  					//regular or bold
	String fontstyle;   					//normal, italic, underscore or strikethru	
	//GMML color
	GmmlColor color;

	//Constructor
	public void main(String input) {
        //Empty
   }
	
	//Boardsize functions
	public void storeBoardsize (int[] bs) {
		boardsize[0] = bs[0];
		boardsize[1] = bs[1];
	}
	public int[] getBoardsize {
		return boardsize;
	}
	
	//Windowsize functions
	public void storeWindowsize (int[] ws) {
		windowsize[0] = ws[0];
		windowsize[1] = ws[1];
	}
	public int[] getWindowsize {
		return windowsize;
	}
	
	//Mapinfo functions
	public void storeMapinfo (int[] mi) {
		mapinfo[0] = mi[0];
		mapinfo[1] = mi[1];
	}
	public int[] getMapinfo {
		return mapinfo;
	}
	
	//Center functions
	public void storeCenter (int[] c) {
		center[0] = c[0];
		center[1] = c[1];
	}
	public int[] getCenter {
		return center;
	}
	
	//Size functions
	public void storeSize (int[] s) {
		size[0] = s[0];
		size[1] = s[1];
	}
	public int[] getSize {
		return size;
	}
}
	