import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;
import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * This class is the main class in the GMML project. 
 * It acts as a container for pathwaydrawings and facilitates
 * loading, creating and saving drawings to and from GMML.
 */
class GmmlVision extends JFrame
{
	private static final long serialVersionUID = 1L;
	GmmlDrawing drawing;
	GmmlData document;

	/**
	 *Constructor for thGmmlVision class
	 *Initializes new GmmlVision and sets properties for frame
	 */
	public GmmlVision()
	{
		super("GMML-Vision");
		setBackground(Color.gray);
		
		buildMenu();
		
		//This line will fix the menu hiding behind the canvas.
		javax.swing.JPopupMenu.setDefaultLightWeightPopupEnabled(false); 
		setJavaLookAndFeel();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(800, 600);
		setLocation(100, 100);
		setVisible(true);
	}

	/**
	 * Main method which will be carried out when running the program
	 */
	public static void main(String[] args)
	{
		new GmmlVision();
	}
	
	/**
	 *Builds and ads a menu to the GmmlVision frame
	 */
	private void buildMenu()
	{
		// initialize new menubar
		JMenuBar menubar = new JMenuBar();
				
		// define menus in menubar
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu viewMenu = new JMenu("View");
		JMenu helpMenu = new JMenu("Help");
		
		// define menu items for file menu
		JMenuItem newItem		= new JMenuItem("New");
		JMenuItem openItem 		= new JMenuItem("Open");
		JMenuItem saveItem 		= new JMenuItem("Save");
		JMenuItem saveAsItem	= new JMenuItem("Save as...");
		JMenuItem closeItem		= new JMenuItem("Close");
		JMenuItem exitItem	 	= new JMenuItem("Exit");
		
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
						
						if(tempfile.exists())
						{
							String[] options = { "OK", "CANCEL" };
							confirmed = JOptionPane.showOptionDialog(null, 
									"The selected file already exists, overwrite?", 
									"Warning", 
									JOptionPane.DEFAULT_OPTION, 
									JOptionPane.WARNING_MESSAGE, null, 
									options, options[0]);
						} 
						else
						{
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
				} 
				else
				{
					JOptionPane.showMessageDialog(null, "No GMML file loaded!", "error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		exitItem.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);	
				}				
			}
		);
		
		// add items to fileMenu
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(closeItem);
		fileMenu.add(exitItem);

		// TODO define menu items for edit menu.....
		JMenuItem propertyItem = new JMenuItem("Properties");
		propertyItem.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if(drawing != null)
					{
						if(drawing.selectedGraphics != null)
						{
							new GmmlPropertyInspector(drawing.selectedGraphics);
						}
						else
						{
							JOptionPane.showMessageDialog(null, "No GMMLGraphics selected!", 
									"error", JOptionPane.ERROR_MESSAGE);
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "No GMML file loaded!", 
								"error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		);
		
		// add items to editMenu
		editMenu.add(propertyItem);
		
		// define menu items for view menu
		JMenu zoomSubMenu = new JMenu("Zoom");
		
		JMenuItem zoom50  = new JMenuItem(" 50 %");
		JMenuItem zoom75  = new JMenuItem(" 75 %");
		JMenuItem zoom100 = new JMenuItem("100 %");
		JMenuItem zoom125 = new JMenuItem("125 %");
		JMenuItem zoom150 = new JMenuItem("150 %");
		JMenuItem zoom200 = new JMenuItem("200 %");
		
		// define actionListener for zoomitem
		zoom50.addActionListener(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					if (drawing != null)
					{
						drawing.setZoom(50);
					}
					else
					{
						JOptionPane.showMessageDialog(null, "No GMML file loaded!", 
								"error", JOptionPane.ERROR_MESSAGE);
					}					
				}
			}
		);
		
		// define actionListener for zoomitem
		zoom75.addActionListener(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					if (drawing != null)
					{
						drawing.setZoom(75);
					}
					else
					{
						JOptionPane.showMessageDialog(null, "No GMML file loaded!", 
								"error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		);
		
		// define actionListener for zoomitem
		zoom100.addActionListener(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					if (drawing != null)
					{
						drawing.setZoom(100);
					}
					else
					{
						JOptionPane.showMessageDialog(null, "No GMML file loaded!", 
								"error", JOptionPane.ERROR_MESSAGE);
					}					
				}
			}
		);
		
		// define actionListener for zoomitem
		zoom125.addActionListener(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					if (drawing != null)
					{
						drawing.setZoom(125);
					}
					else
					{
						JOptionPane.showMessageDialog(null, "No GMML file loaded!", 
								"error", JOptionPane.ERROR_MESSAGE);
					}				
				}
			}
		);
		
		// define actionListener for zoomitem
		zoom150.addActionListener(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					if (drawing != null)
					{
						drawing.setZoom(150);
					}
					else
					{
						JOptionPane.showMessageDialog(null, "No GMML file loaded!", 
								"error", JOptionPane.ERROR_MESSAGE);
					}				
				}
			}
		);
		
		// define actionListener for zoomitem
		zoom200.addActionListener(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					if (drawing != null)
					{
						drawing.setZoom(200);
					}
					else
					{
						JOptionPane.showMessageDialog(null, "No GMML file loaded!", 
								"error", JOptionPane.ERROR_MESSAGE);
					}				
				}
			}
		);
		
		
		
		zoomSubMenu.add(zoom50);
		zoomSubMenu.add(zoom75);
		zoomSubMenu.add(zoom100);
		zoomSubMenu.add(zoom125);
		zoomSubMenu.add(zoom150);
		zoomSubMenu.add(zoom200);
		
		viewMenu.add(zoomSubMenu);
		
		// define menu items for help menu
		JMenuItem aboutItem	= new JMenuItem("About");
		
		aboutItem.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					new GmmlAboutBox();
				}
			}
		);
		
		// add items to helpMenu
		helpMenu.add(aboutItem);
		
		// add menus to menubar
		menubar.add(fileMenu);
		menubar.add(editMenu);
		menubar.add(viewMenu);
		menubar.add(helpMenu);

		// add menubar to frame
		this.setJMenuBar(menubar);

	} // end buildMenu()
	

	/**
	 * Creates a new empty drawing and loads it in the frame 
	 */
	private void createNewDrawing()
	{
		GmmlDrawing d = new GmmlDrawing();
		d.addElement(new GmmlShape(600, 200, 100, 40, 0, Color.blue, 10, d));
		d.addElement(new GmmlLine(100, 100, 200, 200, Color.green, d));
		d.addElement(new GmmlGeneProduct(200, 200, 200, 80, "this is a very long id", "ref", Color.green, d));
		d.addElement(new GmmlLabel(200, 50, 100, 80, "testlabel", "Arial", "bold", "italic", 10, Color.black, d));
		d.addElement(new GmmlLineShape(300, 50, 200, 500, 0, Color.blue, d));
		d.addElement(new GmmlArc(50, 50, 200, 200, Color.red, 0, d));

		this.setContentPane(d);
		
		drawing = d;
	}
	
	/**
	 * Opens a GMML representation of a pathway or reaction and creates 
	 * a scrollpane of the drawing, which is loaded in the frame.
	 */
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
	}
	
	/**
	 * Sets Java Look and Feel to this frame.
	 */
	private static void setJavaLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch(Exception e) {
			System.out.println("Error setting Java LAF: " + e);
		}
	}
	
} // end of class