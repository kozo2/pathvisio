package org.pathvisio.gui.swing;

import java.awt.BorderLayout;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.pathvisio.gui.swing.CommonActions.ExportAction;
import org.pathvisio.gui.swing.CommonActions.ImportAction;
import org.pathvisio.gui.swing.CommonActions.SaveAction;
import org.pathvisio.view.swing.VPathwaySwing;

public class MainPanel extends JPanel {
	private JMenuBar menuBar;
	private JToolBar toolBar;
	private JScrollPane scrollPane;
	
	public MainPanel() {
		setLayout(new BorderLayout());
		
		menuBar = new JMenuBar();
		addMenuActions(menuBar);
		toolBar = new JToolBar();
		addToolBarActions(toolBar);
		
		add(toolBar, BorderLayout.NORTH);
		//menuBar will be added by container (JFrame or JApplet)
		
		scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	protected void addMenuActions(JMenuBar mb) {
		JMenu pathwayMenu = new JMenu("Pathway");
		pathwayMenu.add(new SaveAction());
		pathwayMenu.add(new ImportAction());
		pathwayMenu.add(new ExportAction());
		
		mb.add(pathwayMenu);
	}
	
	protected void addToolBarActions(JToolBar tb) {
		tb.add(new SaveAction());
		tb.add(new ImportAction());
		tb.add(new ExportAction());
		tb.addSeparator();
		
	}
	
	public JMenuBar getMenuBar() {
		return menuBar;
	}
	
	public JToolBar getToolBar() {
		return toolBar;
	}
	
	public void setPathway(VPathwaySwing vPathway) {
		scrollPane.setViewportView(vPathway);
	}
}

