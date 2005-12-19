/**********************************************************
/*
/*        The GMML Window class by Hakim 5/12/2005
/*
/*********************************************************/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
 
public class GmmlWindow {
  private JFrame f;
  GmmlPathway pathway;
  JTextField zoomField;
  GmmlDrawing drawing;
  
  public GmmlWindow() {
  	 f = new JFrame("The GMML Window");
	 javax.swing.JPopupMenu.setDefaultLightWeightPopupEnabled(false); //This line will fix the menu hiding behind the canvas.
    GmmlWindow.setJavaLookAndFeel();
    f.setSize(800, 600);
    Container content = f.getContentPane();
    content.setBackground(Color.white);
    buildMenu(); //Add the menu
    
    //Add a toolbar
    JToolBar toolBar = new JToolBar();
    addButtons(toolBar);
	 f.getContentPane().add(toolBar, BorderLayout.NORTH);

	 f.show();
    
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);
  }
  
  public void openPathway(String file) {
  		//Read a pathway and open it in the applet
      f.getContentPane().removeAll();
      buildMenu(); //Add the menu
    
		//Add a toolbar
		JToolBar toolBar = new JToolBar();
		addButtons(toolBar);
		f.getContentPane().add(toolBar, BorderLayout.NORTH);
	
   	//Add some content
	   GmmlReader reader = new GmmlReader(file);
		pathway = reader.getPathway();
		pathway.checkConnection();

		//Create a drawing (this is an extended JPanel class)
		drawing = new GmmlDrawing(pathway);
		
		//Try to make a scrolling area
		JScrollPane scroll = new JScrollPane(drawing);
		scroll.setVerticalScrollBar(scroll.createVerticalScrollBar());
		scroll.setHorizontalScrollBar(scroll.createHorizontalScrollBar());
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		f.getContentPane().add(scroll);
		
		drawing.init(); //init is applied on applet: Borderlayout, RectangleCanvas3 and a label are added.
		f.show();
  }
	
  public static void setJavaLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch(Exception e) {
      System.out.println("Error setting Java LAF: " + e);
    }
  }
  
  private void buildMenu() {
    //Create the menubar
    JMenuBar menubar = new JMenuBar();
    
    //Create the filemenu and it's items
    JMenu filemenu = new JMenu("File");
    JMenuItem openitem = new JMenuItem("Open");
    JMenuItem saveitem = new JMenuItem("Save");
    JMenuItem exititem = new JMenuItem("Exit");
	 
	 
	 //Add actions to the menu       
    openitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new XmlFilter());
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
          String file = chooser.getSelectedFile().getPath();
			 openPathway(file);
        }
      }
    });
	
    saveitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        //Empty
      }
    });

    exititem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    }); 
	
	 //Link the menu together
    filemenu.add(openitem);
    filemenu.add(saveitem);
    filemenu.add(exititem);
    menubar.add(filemenu);
    
    //Apply the menubar
    f.setJMenuBar(menubar);			
  }
  
  protected void addButtons(JToolBar toolBar) {
    JButton button = null; //Create an empty button
    //Button 1
    button = new JButton("Button1");
    button.setToolTipText("This is Button1");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("Button1 pressed!");
      }
    });
    toolBar.add(button);
    //Button 2
    button = new JButton("Button2");
    button.setToolTipText("This is Button2");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("XML Output");
        GmmlWriter output = new GmmlWriter(drawing.pathway);
        output.dumpToScreen();
      }
    });
    toolBar.add(button);
    
    //Input box
    zoomField = new JTextField(2);
    JLabel zoomFieldLabel = new JLabel("Zoom", SwingConstants.LEFT);
    toolBar.add(zoomFieldLabel);
    toolBar.add(zoomField);
    
    //Button 3
    button = new JButton("Apply");
    button.setToolTipText("Apply zoom");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	drawing.setZoom(Integer.parseInt(zoomField.getText()));
      }
    });
    toolBar.add(button);   
  }
}
class XmlFilter extends javax.swing.filechooser.FileFilter {
	public boolean accept (File f) {
		return f.getName ().toLowerCase ().endsWith (".xml")
			|| f.isDirectory ();
	}
	public String getDescription () {
		return "GMML Files (*.xml)";
	}
} // class XmlFilter

