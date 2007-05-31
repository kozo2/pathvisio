package org.pathvisio.view.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import org.pathvisio.view.VPathway;
import org.pathvisio.view.VPathwayWrapper;

public class VPathwaySwing extends JComponent implements VPathwayWrapper, MouseListener {
	VPathway child;
	
	public VPathwaySwing() {
		
	}
	
	public void setChild(VPathway c) {
		child = c;
	}
	
	public Rectangle getVBounds() {
		return getBounds();
	}

	public Dimension getVSize() {
		return getSize();
	}

	public void redraw() {
		repaint();
	}

	protected void paintComponent(Graphics g) {
		child.draw((Graphics2D)g);
	}
	
	public void redraw(Rectangle r) {
		repaint(r);
	}

	public void setVSize(Dimension size) {
		setSize(size.width, size.height);
	}

	public void setVSize(int w, int h) {
		setSize(w, h);
	}

	public void mouseClicked(MouseEvent arg0) {
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
