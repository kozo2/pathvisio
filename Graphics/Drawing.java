import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.image.*;

public class Drawing extends Applet{

	static protected Label label;

	//init is used to form the applet later in the program.
	public void init(){
	
    	 //an integer array rectcoord is created.
		 int[][] rectCoord={
    		{0,0,100,50},
	   	{150,50,100,50},
	   	{0,100,100,50},
	   	{150,150,100,50},
		   {300,0,100,50}
	 	 };
		  
		 Color[] rectColors={Color.blue,Color.green,Color.yellow,Color.red,Color.pink};
		//Initialize the layout.
		setLayout(new BorderLayout());
		add(new DrawingCanvas(rectCoord,rectColors)); //Same as add(new DrawingCanvas(), BorderLayout.center); 
	//This label is used when the applet is just started
		label = new Label("Drag rectangles around within the area");
		add("South", label); //South: in the lowest part of the frame.
	} //end of init


	 //Main
    public static void main(String s[]) {
    			
    	  //Here the Frame is created. It is invisible.
        Frame f = new Frame("Drawing");
        //Windowlistener to stop the program when closing the window.
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        
	//An applet is opened.
	Applet applet = new Drawing();
	f.add("Center", applet); //Frame is put in the middle of the applet.
	applet.init(); //init is applied on applet: Borderlayout, RectangleCanvas3 and a label are added.
	f.pack(); //with pack everyting becomes visible and the required size of the frame is calculated.
	f.setSize(new Dimension(550,250)); //After that the frame gets a new dimension...
   f.show();
        
       
   } //end of main

} //end of Drawing


