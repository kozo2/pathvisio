package org.pathvisio.gui.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.pathvisio.gui.swing.CommonActions.CopyAction;
import org.pathvisio.gui.swing.CommonActions.ExportAction;
import org.pathvisio.gui.swing.CommonActions.ImportAction;
import org.pathvisio.gui.swing.CommonActions.NewElementAction;
import org.pathvisio.gui.swing.CommonActions.PasteAction;
import org.pathvisio.gui.swing.CommonActions.SaveAction;
import org.pathvisio.gui.swing.CommonActions.ZoomAction;
import org.pathvisio.view.VPathway;
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
		pathwayMenu.add(new ImportAction(this));
		pathwayMenu.add(new ExportAction());
		
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(new CopyAction());
		editMenu.add(new PasteAction());
		
		JMenu viewMenu = new JMenu("View");
		JMenu zoomMenu = new JMenu("Zoom");
		viewMenu.add(zoomMenu);
		zoomMenu.add(new ZoomAction(VPathway.ZOOM_TO_FIT));
		zoomMenu.add(new ZoomAction(10));
		zoomMenu.add(new ZoomAction(25));
		zoomMenu.add(new ZoomAction(50));
		zoomMenu.add(new ZoomAction(75));
		zoomMenu.add(new ZoomAction(100));
		zoomMenu.add(new ZoomAction(150));
		zoomMenu.add(new ZoomAction(200));
		
		mb.add(pathwayMenu);
		mb.add(editMenu);
		mb.add(viewMenu);
	}
	
	protected void addToolBarActions(JToolBar tb) {
		tb.add(new SaveAction());
		tb.add(new ImportAction(this));
		tb.add(new ExportAction());
		tb.addSeparator();
		tb.add(new CopyAction());
		tb.add(new PasteAction());
		tb.addSeparator();
		
		tb.addSeparator();
		JComboBox combo = new JComboBox(new Object[] { 
				new ZoomAction(VPathway.ZOOM_TO_FIT),
				new ZoomAction(10),
				new ZoomAction(25),
				new ZoomAction(50),
				new ZoomAction(75),
				new ZoomAction(100),
				new ZoomAction(150),
				new ZoomAction(200)
		} );
		combo.setEditable(true);
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox combo = (JComboBox)e.getSource();
				Object s = combo.getSelectedItem();
				if(s instanceof ZoomAction) {
					((ZoomAction)s).actionPerformed(e);
				} else if(s instanceof String) {
					String zs = (String)s;
					try {
						double zf = Double.parseDouble(zs);
						new ZoomAction(zf).actionPerformed(e);
					} catch(Exception ex) {
						//Ignore bad input
					}
				}
			}
		});
		tb.add(combo);
		tb.addSeparator();
		
		tb.add(new NewElementAction(VPathway.NEWGENEPRODUCT));
		tb.add(new NewElementAction(VPathway.NEWLABEL));
		tb.add(new NewElementAction(VPathway.NEWLINEMENU));
		tb.add(new NewElementAction(VPathway.NEWRECTANGLE));
		tb.add(new NewElementAction(VPathway.NEWOVAL));
		tb.add(new NewElementAction(VPathway.NEWARC));
		tb.add(new NewElementAction(VPathway.NEWBRACE));
		tb.add(new NewElementAction(VPathway.NEWTBAR));
		tb.add(new NewElementAction(VPathway.NEWLINESHAPEMENU));
				
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
	
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
}

