package converter;

import java.lang.*;
import java.math.*;
import java.util.*;

public class ConvertType
{
    
    public static BigInteger stringToBigInt(String s) {
        double d = Double.parseDouble(s);
        BigInteger b = BigInteger.valueOf((long)d);
        return b;
    }
    
    // checking out
    // http://java.sun.com/j2se/1.4.2/docs/api/java/math/BigDecimal.html
    // it looks like one can just give BigDecimal constructor a string and
    // let it run.  Let's try that.
    public static BigDecimal stringToBigDec( String s)
    {
        double d = Double.parseDouble(s);
        BigDecimal b = BigDecimal.valueOf((long)d);
        return b;
    }


    public static java.math.BigDecimal str2bd( String bar)
    {
   	BigDecimal mybd = new BigDecimal( bar); 
	return mybd;
    }

    public static String decToHex(String s)
    {
	// NOTE: this method converts the integer to hexadecimal which can
	// represent a 24 bit number (the colors in GenMAPP are encoded in
	// RGB) I added padding of the hexadecimal number, because the
	// validator doesn't accept a one digit value (eg 10 -> A gives a
	// validation error)

        int i = Integer.parseInt(s);
        String hexstring = Integer.toHexString(i);

        // padding
        if (hexstring.length()<2) hexstring="00000"+hexstring;
            else if (hexstring.length()<3) hexstring="0000"+hexstring;
            else if (hexstring.length()<4) hexstring="000"+hexstring;
            else if (hexstring.length()<5) hexstring="00"+hexstring;
            else if (hexstring.length()<6) hexstring="0"+hexstring;
        
        return hexstring;
    }
}
