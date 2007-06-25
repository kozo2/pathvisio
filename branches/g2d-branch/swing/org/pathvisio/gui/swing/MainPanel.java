package org.pathvisio.gui.swing;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.pathvisio.gui.swing.CommonActions.ExportAction;
import org.pathvisio.gui.swing.CommonActions.ImportAction;
import org.pathvisio.gui.swing.CommonActions.SaveAction;

public class MainPanel extends JPanel {
	private JMenuBar menuBar;
	private JToolBar toolBar;
	
	public MainPanel() {
		setLayout(new BorderLayout());
		
		menuBar = new JMenuBar();
		addMenuActions(menuBar);
		toolBar = new JToolBar();
		addToolBarActions(toolBar);
		
		add(toolBar, BorderLayout.NORTH);
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
	}
	
	public JMenuBar getMenuBar() {
		return menuBar;
	}
	
	public JToolBar getToolBar() {
		return toolBar;
	}
}

