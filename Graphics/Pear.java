/*
* @(#)Pear.java   1.2 98/07/31
*/


import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

/*
 * This applet renders a pear, using Constructive Area Geometry (CSG) methods,
 * add, intersect, and subtract.
*/

public class Pear extends JApplet {

    Ellipse2D.Double circle, oval, leaf, stem;
    Area circ, ov, leaf1, leaf2, st1, st2;

    public void init() {
        circle = new Ellipse2D.Double();
        oval = new Ellipse2D.Double();
        leaf = new Ellipse2D.Double();
        stem = new Ellipse2D.Double();
        circ = new Area(circle);
        ov = new Area(oval);
        leaf1 = new Area(leaf);
        leaf2 = new Area(leaf);
        st1 = new Area(stem);
        st2 = new Area(stem);

        setBackground(Color.white);
    }

    public void paint (Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Dimension d = getSize();
        int w = d.width;
        int h = d.height;
        double ew = w/2;
        double eh = h/2;

        g2.setColor(Color.green);

        // Creates the first leaf by filling the intersection of two Area objects created from an ellipse.
        leaf.setFrame(ew-16, eh-29, 15.0, 15.0);
        leaf1 = new Area(leaf);
        leaf.setFrame(ew-14, eh-47, 30.0, 30.0);
        leaf2 = new Area(leaf); 
        leaf1.intersect(leaf2);   
        g2.fill(leaf1);   

        // Creates the second leaf.
        leaf.setFrame(ew+1, eh-29, 15.0, 15.0);
        leaf1 = new Area(leaf);
        leaf2.intersect(leaf1);
        g2.fill(leaf2);

        g2.setColor(Color.black);

        // Creates the stem by filling the Area resulting from the subtraction of two Area objects created from an ellipse.
        stem.setFrame(ew, eh-42, 40.0, 40.0);
        st1 = new Area(stem);
        stem.setFrame(ew+3, eh-47, 50.0, 50.0);
        st2 = new Area(stem);
        st1.subtract(st2);
        g2.fill(st1);

        g2.setColor(Color.yellow);

        // Creates the pear itself by filling the Area resulting from the union of two Area objects created by two different ellipses.
        circle.setFrame(ew-25, eh, 50.0, 50.0);
        oval.setFrame(ew-19, eh-20, 40.0, 70.0);
        circ = new Area(circle);
        ov = new Area(oval);
        circ.add(ov);
        g2.fill(circ);
    }

    public static void main(String s[]) {
        JFrame f = new JFrame("Pear");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        JApplet applet = new Pear();
        f.getContentPane().add("Center", applet);
        applet.init();
        f.pack();
        f.setSize(new Dimension(150,200));
        f.show();
    }

}
