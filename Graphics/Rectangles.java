import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.image.*;

public class Rectangles extends Applet{

	static protected Label label;

	//init is used to form the applet later in the program.
	public void init(){
		//Initialize the layout.
		setLayout(new BorderLayout());
		add(new RectangleCanvas());
		//This label is used when the applet is just started
		label = new Label("Drag rectangle around within the area");
		add("South", label); //South: in the lowest part of the frame.
	}

    public static void main(String s[]) {
    	  //Here the Frame is created. It is invisible.
        Frame f = new Frame("Rectangles");
        //Windowlistener to stop the program when closing the window.
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        
	//An applet is opened.
	Applet applet = new Rectangles();
	f.add("Center", applet); //Frame is put in the middle of the applet.
	applet.init(); //init is applied on applet: Borderlayout, RectangleCanvas and a label are added.
	f.pack(); //with pack everyting becomes visible and the required size of the frame is calculated.
        f.setSize(new Dimension(550,250)); //After that the frame gets a new dimension...
        f.show();
    }

} //end of Rectangles

//a new class RectangleCanvas, which extends Canvas, is created.
class RectangleCanvas extends Canvas implements MouseListener, MouseMotionListener{

	//A rectangle is created.
   Rectangle rect = new Rectangle(0, 0, 100, 50); //Rectangle(int x, int y, int width, int height) with top-left corner (x,y)
   BufferedImage bi; 
	Graphics2D big;

   // Holds the coordinates of the user's last mousePressed event.
	int last_x, last_y;
	boolean firstTime = true;
	TexturePaint fillPolka, strokePolka;
   Rectangle area;

        // True if the user pressed, dragged or released the mouse outside of the rectangle; false otherwise.
	boolean pressOut = false;

	public RectangleCanvas(){ //method to color the rectangle
                setBackground(Color.white);
                addMouseMotionListener(this);
                addMouseListener(this);

		// Creates the fill texture paint pattern.
                bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB); //(int width, int height, int imageType)
                big = bi.createGraphics(); //Creates a Graphics2D, which can be used to draw into this BufferedImage
                big.setColor(Color.green);
                big.fillRect(0, 0, 7, 7);
                big.setColor(Color.yellow);
                big.fillOval(0, 0, 3, 3);
                Rectangle r = new Rectangle(0,0,5,5); //big and bi are only used to create the pattern
                fillPolka = new TexturePaint(bi, r);
		big.dispose();

		//Creates the stroke texture paint pattern.
                bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
		big = bi.createGraphics();		
		big.setColor(Color.cyan);
                big.fillRect(0, 0, 7, 7);
                big.setColor(Color.pink);
                big.fillOval(0, 0, 3, 3);
                r = new Rectangle(0,0,5,5);
                strokePolka = new TexturePaint(bi, r);
		big.dispose();
	} //end of rectangleCanvas

        // Handles the event of the user pressing down the mouse button.
	public void mousePressed(MouseEvent e){

		last_x = rect.x - e.getX(); //last_x = position rect - position mouse when pressed
		last_y = rect.y - e.getY();

      // Checks whether or not the cursor is inside of the rectangle while the user is pressing the mouse.
		if(rect.contains(e.getX(), e.getY())){ //if the user presses the mouses on a coordinate which is contained by rect
			updateLocation(e);
		} else {
		Rectangles.label.setText("First position the cursor on the rectangle and then drag.");
			pressOut = true;
		}
	} //end of mousePressed event

        // Handles the event of a user dragging the mouse while holding down the mouse button.
	public void mouseDragged(MouseEvent e){

		if(!pressOut){ //always mousePressed before mouseDragged -> pressOut true when start dragging outside of rect.
			 updateLocation(e);
		} else {  
		Rectangles.label.setText("First position the cursor on the rectangle and then drag.");
		}
	}

        // Handles the event of a user releasing the mouse button. Sets pressOut back on false.
	public void mouseReleased(MouseEvent e){

      // Checks whether or not the cursor is inside of the rectangle when the user releases the
		// mouse button.   
		if(rect.contains(e.getX(), e.getY())){
			updateLocation(e);
		} else {
		Rectangles.label.setText("First position the cursor on the rectangle and then drag.");
			pressOut = false;
		}
	}

	// This method required by MouseListener, it does nothing
	public void mouseMoved(MouseEvent e){}

        // These methods are required by MouseMotionListener, they do nothing
	public void mouseClicked(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	
	//updateLocation
	public void updateLocation(MouseEvent e){

		rect.setLocation(last_x + e.getX(), last_y + e.getY());
                /*
                 * Updates the label to reflect the location of the
                 * current rectangle 
                 * if checkRect returns true; otherwise, returns error message.
                 */
                if (checkRect()) { //true if rect is in area, false if rect is not in area, rect is put back intro area
                    Rectangles.label.setText("Rectangle located at " +
                                                     rect.getX() + ", " +
                                                     rect.getY());
                } else {
                    Rectangles.label.setText("Please don't try to "+
                                                     " drag outside the area.");
                }
		repaint(); //The component will be repainted after all of the currently pending events have been dispatched
	}

	public void paint(Graphics g){
                update(g);
	}

	public void update(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
                 
          	if(firstTime){
                    Dimension dim = getSize(); //Size of frame f
                    int w = dim.width;
                    int h = dim.height;
                    area = new Rectangle(dim);
		    bi = (BufferedImage)createImage(w, h);
		    big = bi.createGraphics();
		    rect.setLocation(w/2-50, h/2-25); //the first time, rect is put in the middle of area/frame
		    big.setStroke(new BasicStroke(8.0f));
		    firstTime = false;
		} 

		// Clears the rectangle that was previously drawn.
		big.setColor(Color.white);
		big.clearRect(0, 0, area.width, area.height);

		// Draws and fills the newly positioned rectangle to the buffer.
		big.setPaint(strokePolka);
		big.draw(rect);
		big.setPaint(fillPolka);
		big.fill(rect);

		// Draws the buffered image to the screen.
		g2.drawImage(bi, 0, 0, this);
	}
	/*
         * Checks if the rectangle is contained within the applet window.  If the rectangle
         * is not contained withing the applet window, it is redrawn so that it is adjacent
         * to the edge of the window and just inside the window.
	 */
	boolean checkRect(){
            if (area == null) {
                return false;
            }
		if(area.contains(rect.x, rect.y, 100, 50)){
			return true;
		}
		
		int new_x = rect.x;
		int new_y = rect.y;

		if((rect.x+100)>area.width){ //rect width is 100
			new_x = area.width-99;
		}
		if(rect.x < 0){  
			new_x = -1;
		}
		if((rect.y+50)>area.height){ //rect height is 50
			new_y = area.height-49; 
		}
		if(rect.y < 0){  
			new_y = -1;
		}
		rect.setLocation(new_x, new_y);
		return false;
	}

} //end of RectangleCanvas

