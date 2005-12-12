/**********************************************************
/*
/*        The GMML Window class by Hakim 5/12/2005
/*
/*********************************************************/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GmmlWindow {
  private JFrame f;
  GmmlPathway pathway;
  JTextField colorField;
  
  public GmmlWindow() {
  	 f = new JFrame("The GMML Window");
	 javax.swing.JPopupMenu.setDefaultLightWeightPopupEnabled(false); //This line will fix the menu hiding behind the canvas.
    GmmlWindow.setJavaLookAndFeel();
    f.setSize(1280, 1024);
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
	   //System.out.println("Trying to start the GmmlReader for file: "+file);
	   GmmlReader reader = new GmmlReader(file);
		pathway = reader.getPathway();
    
		f.getContentPane().add(pathway);
		pathway.init(); //init is applied on applet: Borderlayout, RectangleCanvas3 and a label are added.
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
        //TODO! Make a filter for XML only.
        // Note: source for ExampleFileFilter can be found in FileChooserDemo,
        // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
        //ExampleFileFilter filter = new ExampleFileFilter();
        //filter.addExtension("jpg");
        //filter.addExtension("gif");
        //filter.setDescription("JPG & GIF Images");
        //chooser.setFileFilter(filter);
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
        System.out.println("Button2 is now pressed!");
      }
    });
    toolBar.add(button);
    
    //Input box
    colorField = new JTextField(2);
    JLabel colorFieldLabel = new JLabel("Color", SwingConstants.LEFT);
    toolBar.add(colorFieldLabel);
    toolBar.add(colorField);
    
    //Button 3
    button = new JButton("Apply");
    button.setToolTipText("Apply color");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	String input = colorField.getText();
      	//pathway.defaultcolor = GmmlColor.convertColor(input);
	      //pathway.updateColor();
      }
    });
    toolBar.add(button);   
  }

}

