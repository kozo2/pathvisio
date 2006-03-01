import java.awt.*;
import java.awt.event.*;
import java.awt.Container;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.util.Vector;
import javax.swing.*;
import java.io.*;

import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.jdom.Document;

class GmmlVision extends JFrame
{
	GmmlDrawing drawing;
	GmmlData document;

	public static void main(String[] args)
	{
		new GmmlVision();
	}
	
	/**
	 *Constructor for this class
	 *Initializes new GmmlVision and sets properties for frame
	 */
	public GmmlVision()
	{
		super("GMML-Vision");
		setBackground(Color.gray);
		
		buildMenu();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(800, 600);
		setLocation(100, 100);
		setVisible(true);
		
		show();
	}
	
	
	/**
	 *Builds and ads the a menu in the GmmlVision class
	 */
	private void buildMenu()
	{
		// initialize new menubar
		JMenuBar menubar = new JMenuBar();
				
		// define menus in menubar
		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("Help");
		
		// define menu items for file menu
		JMenuItem newItem 	= new JMenuItem("New");
		JMenuItem openItem 	= new JMenuItem("Open");
		JMenuItem saveItem 	= new JMenuItem("Save");
		JMenuItem saveAsItem = new JMenuItem("Save as...");
		JMenuItem exitItem 	= new JMenuItem("Exit");
		
		// define actionListener for newItem
		newItem.addActionListener(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					createNewDrawing();
				}
			}
		);
		
		// define actionListener for openItem
		openItem.addActionListener(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					JFileChooser chooser = new JFileChooser();
					chooser.setFileFilter(new GmmlFilter());
					int returnVal = chooser.showOpenDialog(null);
					
					if(returnVal == JFileChooser.APPROVE_OPTION) 
					{
						String file = chooser.getSelectedFile().getPath();
						openPathway(file);
					}
				}
			}
		);
		
		// define actionListener for saveItem
		saveItem.addActionListener(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					// Overwrite the existing xml file
					document.writeToXML(document.xmlFile);
				}
			}
		);
		
		// define actionListener for saveAsItem
		saveAsItem.addActionListener(new ActionListener() 
				{
			public void actionPerformed(ActionEvent e) 
			{
				if (drawing!=null)
				{
					JFileChooser chooser = new JFileChooser();
					chooser.setFileFilter(new GmmlFilter());
					int returnVal = chooser.showSaveDialog(null);
					if(returnVal == JFileChooser.APPROVE_OPTION) 
					{
						String file = chooser.getSelectedFile().getPath();
						if(!file.endsWith(".xml")) 
						{
							file = file+".xml";
						}
						
						int confirmed = 1;
						File tempfile = new File(file);
						
						if(tempfile.exists()) {
							String[] options = { "OK", "CANCEL" };
							confirmed = JOptionPane.showOptionDialog(null, 
									"The selected file already exists, overwrite?", 
									"Warning", 
									JOptionPane.DEFAULT_OPTION, 
									JOptionPane.WARNING_MESSAGE, null, 
									options, options[0]);
						} else {
							confirmed = 0;
						}
						
						if (confirmed == 0) 
						{
							document.writeToXML(tempfile);
							System.out.println("Saved");
						} else {
							System.out.println("Canceled");
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "No GMML file loaded!", "error", JOptionPane.ERROR_MESSAGE);
				}
			}
				} );
		
		// add items to fileMenu
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(exitItem);

		// define menu items for help menu
		JMenuItem aboutItem	= new JMenuItem("About");
		
		// add items to helpMenu
		helpMenu.add(aboutItem);

		// add menus to menubar
		menubar.add(fileMenu);
		menubar.add(helpMenu);

		// add menubar to frame
		this.setJMenuBar(menubar);

	} // end buildMenu()
	
	private void createNewDrawing()
	{
		GmmlDrawing d = new GmmlDrawing();
		d.addElement(new GmmlShape(600, 200, 100, 40, 0, "blue", 10, d));
		d.addElement(new GmmlLine(100, 100, 200, 200, Color.green, d));
		d.addElement(new GmmlGeneProduct(200, 200, 200, 80, "this is a very long id", "ref", Color.green, d));
		d.addElement(new GmmlLabel(200, 50, 100, 80, "testlabel", "Arial", "bold", "italic", 10, Color.black, d));
		d.addElement(new GmmlLineShape(300, 50, 200, 500, 0, Color.blue, d));
		d.addElement(new GmmlArc(50, 50, 200, 200, "red", 0, d));

		this.setContentPane(d);
		
		show();
		drawing = d;
	}
	
	private void openPathway(String file)
	{
		// initialize new JDOM gmml representation and read the file
		document = new GmmlData(file);
		// get drawing from reader
		drawing = document.getDrawing();
		
		// create scrollpane
		JScrollPane scroll = new JScrollPane(drawing);
		scroll.setBackground(Color.gray);
		// set scrollpane
		scroll.setVerticalScrollBar(scroll.createVerticalScrollBar());
		scroll.setHorizontalScrollBar(scroll.createHorizontalScrollBar());
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		// add drawing to frame
		setContentPane(scroll);
		
		show();
	}
	
} // end of class