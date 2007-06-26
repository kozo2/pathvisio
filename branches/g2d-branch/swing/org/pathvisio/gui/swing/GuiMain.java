package org.pathvisio.gui.swing;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.pathvisio.Engine;

public class GuiMain {

	private static void createAndShowGUI() {
		GuiInit.init();
		
		//Create and set up the window.
		JFrame frame = new JFrame("PathVisio...swing it baby!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		MainPanel mainPanel = SwingEngine.getApplicationPanel();
		frame.add(mainPanel);
		frame.setJMenuBar(mainPanel.getMenuBar());
		frame.setSize(800, 600);
		
		try {
		    UIManager.setLookAndFeel(
		        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			Engine.log.error("Unable to load native look and feel", ex);
		}
		
		//Display the window.
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
