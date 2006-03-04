import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

class GmmlColorConvertor
{
	public static final List colorMappings = Arrays.asList(new String[]{
		"Aqua", "Black", "Blue", "Fuchsia", "Gray", "Green", "Lime",
		"Maroon", "Navy", "Olive", "Purple", "Red", "Silver", "Teal",
		"White", "Yellow"
	});
	
	public static final List rgbMappings = Arrays.asList(new double[][] {
		{0, 1, 1},		// aqua 
		{0, 0, 0},	 	// black
		{0, 0, 1}, 		// blue
		{1, 0, 1},		// fuchsia
		{.5, .5, .5,},	// gray
		{0, .5, 0}, 	// green
		{0, 1, 0},		// lime
		{.5, 0, 0},		// maroon
		{0, 0, .5},		// navy
		{.5, .5, 0},	// olive
		{.5, 0, .5},	// purple
		{1, 0, 0}, 		// red
		{.75, .75, .75},// silver
		{0, .5, .5}, 	// teal
		{1, 1, 1}		// white
	});
	
	private static final char[] hexadecimalMappings = {'1', '2', '3', '4', '5',
		'6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	/**
	 * Constructor for this class
	 */
	public GmmlColorConvertor()
	{
	}	
	
	 /**
	  * A convenience method to convert in the other direction, from a string
	   * of hexadecimal digits to an array of bytes.
	   **/
/*	 public static byte[] hexDecode(String s) throws IllegalArgumentException {
	    try {
	      int len = s.length();
	      byte[] r = new byte[len/2];
	      for(int i = 0; i < r.length; i++) {
	        int digit1 = s.charAt(i*2), digit2 = s.charAt(i*2 + 1);
	        if ((digit1 >= '0') && (digit1 <= '9')) digit1 -= '0';
	        else if ((digit1 >= 'a') && (digit1 <= 'f')) digit1 -= 'a' - 10;
	        if ((digit2 >= '0') && (digit2 <= '9')) digit2 -= '0';
	        else if ((digit2 >= 'a') && (digit2 <= 'f')) digit2 -= 'a' - 10;
	        r[i] = (byte)((digit1 << 4) + digit2);
	      }
	      return r;
	    }
	    catch (Exception e) {
	      throw new IllegalArgumentException("hexDecode(): invalid input");
	    }
	 }
	}*/
	
/**
	 * Check the format of String specified and then calls the 
	 * correct method to decode it
	 * @param strColor	- the String to convert to a color
	 * @return	a Color object
	 */
	public static Color string2Color(String strColor)
	{
		Color color = new Color(0);
		if(strColor.length() == 6)
		{
			boolean strColorIsHex = true;
			boolean found = false;
						
			for (int j = 0; (j < strColor.length()) && !found; j ++)
			{
				char x = strColor.charAt(j);
				found = false;
				for (int i = 0; (i < 16) && strColorIsHex; i ++)
				{
					if(x == hexadecimalMappings[i])
					{
						found = true;
					}
				}
				if (!found)
				{
					strColorIsHex = false;
				}
			}
			if (strColorIsHex)
			{
				int r = Integer.parseInt(strColor.substring(0, 2), 16);
				int g = Integer.parseInt(strColor.substring(2, 4), 16);
				int b = Integer.parseInt(strColor.substring(4, 6), 16);
								
				color = new Color((float)r/255, (float)g/255, (float)b/255);
			}
		}
		else {
			int index = colorMappings.indexOf(strColor);
			if (index > -1)
			{
				double[] c = (double[]) rgbMappings.get(index);
				color = new Color((float)c[0], (float)c[1], (float)c[2]);			
			}
			else 
			{
				color = Color.black;
			}
		}
		return color;
	}
}