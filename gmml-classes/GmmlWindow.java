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
  
  public GmmlWindow() {
    GmmlWindow.setJavaLookAndFeel();
    f = new JFrame("The GMML Window");
    f.setSize(800, 600);
    Container content = f.getContentPane();
    content.setBackground(Color.white);
    buildMenu(); //Add the menu
    
    //Add a toolbar
    JToolBar toolBar = new JToolBar();
    addButtons(toolBar);
	 f.getContentPane().add(toolBar, BorderLayout.NORTH);

    //Add some content
    GmmlPathway pathway = new GmmlPathway();
    f.getContentPane().add(pathway);
    
    f.addWindowListener(new ExitListener());
    f.setVisible(true);
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
        //Empty
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
    JButton button = null;
    button = new JButton("Button1");
    button.setToolTipText("This is Button1");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("Button1 pressed!");
      }
    });
    toolBar.add(button);
  }
  public static void setJavaLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch(Exception e) {
      System.out.println("Error setting Java LAF: " + e);
    }
  }
}
