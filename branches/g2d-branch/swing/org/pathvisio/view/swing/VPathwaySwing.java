package org.pathvisio.view.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import org.pathvisio.view.VPathway;
import org.pathvisio.view.VPathwayWrapper;

public class VPathwaySwing extends JComponent implements VPathwayWrapper, MouseMotionListener, MouseListener, KeyListener {
	VPathway child;
	
	public VPathwaySwing() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
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
		//TODO: find out how to handle this one
	}

	public void mouseEntered(MouseEvent e) {
		child.mouseEnter(new SwingMouseEvent(e));		
	}

	public void mouseExited(MouseEvent e) {
		child.mouseExit(new SwingMouseEvent(e));
		
	}

	public void mousePressed(MouseEvent e) {
		child.mouseDown(new SwingMouseEvent(e));
	}

	public void mouseReleased(MouseEvent e) {
		child.mouseUp(new SwingMouseEvent(e));
	}

	public void keyPressed(KeyEvent e) {
		child.keyPressed(new SwingKeyEvent(e));
	}

	public void keyReleased(KeyEvent e) {
		child.keyReleased(new SwingKeyEvent(e));		
	}

	public void keyTyped(KeyEvent e) {
		// TODO: find out how to handle this one	
	}

	public void mouseDragged(MouseEvent e) {
		// TODO: find out how to handle this one, as mouseMove?
	}

	public void mouseMoved(MouseEvent e) {
		child.mouseMove(new SwingMouseEvent(e));
	}

	public VPathway createVPathway() {
		setChild(new VPathway(this));
		return child;
	}
}
