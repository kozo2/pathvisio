package org.pathvisio.gui.swing;

import java.io.File;

import javax.swing.JFrame;

import org.pathvisio.model.ConverterException;
import org.pathvisio.model.Pathway;
import org.pathvisio.view.VPathway;
import org.pathvisio.view.swing.VPathwaySwing;

public class GuiMain {

	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("PathVisio...swing it baby!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(testcase());

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	static VPathwaySwing testcase() {
		String pwf = "test.gpml";
		
		VPathwaySwing svp = new VPathwaySwing();
		VPathway vp = new VPathway(svp);
		svp.setChild(vp);

		try { 
			Pathway p = new Pathway();
			p.readFromXml(new File(pwf), true);
			vp.fromGmmlData(p);
		} catch(ConverterException e) {		
			e.printStackTrace();
		}

		return svp;
	}
}
