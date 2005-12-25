/*
Copyright 2005 H.C. Achterberg, R.M.H. Besseling, I.Kaashoek, 
M.M.Palm, E.D Pelgrim, BiGCaT (http://www.BiGCaT.unimaas.nl/)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and 
limitations under the License.
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

/**
 *        The GmmlWindow class is basicly the Graphic User Interface of the program as you see when you open it. The applet is started and the menu's, toolbar and drawing area are added.
 */
  
public class GmmlWindow {
  private JFrame f;
  GmmlConnection connection;
  GmmlPathway pathway;
  JTextField zoomField;
  GmmlDrawing drawing;
  
  /**
   * Create a new program window.
   */

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
  
  /**
   *  Load an xml into to the program and create a drawing of it.
   */
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
		connection = new GmmlConnection(pathway);

		//Create a drawing (this is an extended JPanel class)
		drawing = new GmmlDrawing(pathway, connection);
		
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
	
  private static void setJavaLookAndFeel() {
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
    JMenuItem saveitem = new JMenuItem("Save As...");
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
        if (drawing!=null) {
          JFileChooser chooser = new JFileChooser();
          chooser.setFileFilter(new XmlFilter());
          int returnVal = chooser.showSaveDialog(null);
          if(returnVal == JFileChooser.APPROVE_OPTION) {
            String file = chooser.getSelectedFile().getPath();
            if(!file.endsWith(".xml")) {
            	file = file+".xml";
	         }
	         
	         int confirmed = 1;
	         File tempfile = new File(file);
	         
	         if(tempfile.exists()) {
	         	String[] options = { "OK", "CANCEL" };
		         JOptionPane pane = new JOptionPane();
					confirmed = pane.showOptionDialog(null, "The selected file already exists, overwrite?", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
	         }  else {
	         	confirmed = 0;
	         }
		      
		      if (confirmed == 0) {
				   GmmlWriter output = new GmmlWriter(drawing.pathway);
   	         output.writeToFile(file);
	            System.out.println("Saved");
	         } else {
	         	System.out.println("Canceled");
		      }
          }
        } else {
          JOptionPane.showMessageDialog(null, "No GMML file loaded!", "error", JOptionPane.ERROR_MESSAGE);
        }
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
  
  private void addButtons(JToolBar toolBar) {
    JButton button = null; //Create an empty button
    //Button 1
    button = new JButton("Show XML");
    button.setToolTipText("Display output XML");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(drawing!=null) {
          System.out.println("XML Output");
          GmmlWriter output = new GmmlWriter(drawing.pathway);
          output.dumpToScreen();
        } else {
           JOptionPane.showMessageDialog(null, "No GMML file loaded!", "error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    toolBar.add(button);
    //Button 2
    button = new JButton("Unused");
    button.setToolTipText("Write this file to text.xml");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("Button is unused");
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

