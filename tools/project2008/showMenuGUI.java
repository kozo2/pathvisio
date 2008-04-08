

//package components;

/*
 * SimpleTableDemo.java requires no other files.
 */

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.xmlrpc.XmlRpcException;
import org.pathvisio.data.DataException;
import org.pathvisio.model.ConverterException;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class showMenuGUI extends JPanel {
	
	public static void main(String[] args) {
		
		final String dbDir;
		  final File pwDir;
			
		  try {
			  dbDir = new String(args[0]);
			  pwDir = new File(args[1]);
			  
			  javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						createAndShowMenuGUI(dbDir,pwDir);
					}
				});

		  }
		  catch(ArrayIndexOutOfBoundsException e) {
			  System.out.println("String[] args not given!");
			  System.exit(0);
		  }
		
		
	}
	
	public static void createAndShowMenuGUI(final String dbDir,final File pwDir){
		
		// create a new frame
		JFrame frame = new JFrame("Main Menu");

		
		JLabel label = new JLabel("Main Menu");
		
        frame.getContentPane().add(label);
        
		// When click on exit, exit the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// set the size of the frame
		frame.setSize(400,300);
		
		// create a new panel
		//JPanel canvasTop=new JPanel();
		JPanel canvasButtons=canvasButtons(dbDir,pwDir);
		JPanel canvasCloseButton=canvasCloseButton();
		
		// add the canvas to the frame
		//frame.add(canvasTop, BorderLayout.NORTH);
		frame.add(canvasButtons, BorderLayout.CENTER);
		frame.add(canvasCloseButton, BorderLayout.SOUTH);
		
		
		// Show the frame
		frame.setVisible(true);
	}
	
	
	public static JPanel canvasButtons(final String dbDir,final File pwDir){
		
		// create a new panel
		JPanel canvasButtons = new JPanel();
		
		// create two new buttons, using the makeButton method
		JButton PomButton = TestFrames.makeButton("Pathway overlap Matrix");
		JButton GtdButton = TestFrames.makeButton("Go Term Distribution");
		JButton PugButton = TestFrames.makeButton("Percentage of used genes");
		JButton closeButton = TestFrames.makeButton("Close");
				
		// add the functionality to the Pathway overlap Matrix button
		PomButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						System.out.println("Go to Pathway overlap Matrix");
						showOverlapNewGUI.createAndShowOverlapGUI(dbDir,pwDir);
						}
					}
				);
		
		// add the functionality to the Go Term Distribution button
		GtdButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						System.out.println("Go to Go Term Distribution");
						System.out.println("not available");
						/*String[] arguments={"D:\\My Documents\\Tue\\BIGCAT\\",
										"D:\\My Documents\\Tue\\BIGCAT\\Rat",
										"D:\\My Documents\\Tue\\BIGCAT\\result.html"};
						try {
							LinkChecker.main(arguments);
						} catch (ConverterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (DataException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (XmlRpcException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/

						}
					}
				);
		
		// add the functionality to the Go Term Distribution button
		PugButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						//ShowPercentageGUI.createAndShowPercentageGUI();
						}
					}
				);
		
		// add the functionality to the close button
		closeButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						System.exit(0);
						}
					}
				);
		
		
		
		// add the buttons to the canvas
		canvasButtons.add(PomButton);
		canvasButtons.add(GtdButton);
		canvasButtons.add(PugButton);
		canvasButtons.add(closeButton);
		
		return canvasButtons;
	}
	
	public static JPanel canvasCloseButton(){
		// create a new panel
		JPanel canvasCloseButton = new JPanel();
		
		// create two new buttons, using the makeButton method
		JButton closeButton = TestFrames.makeButton("Close");

		// add the functionality to the close button
		closeButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						System.exit(0);
						}
					}
				);
		
		
		
		// add the buttons to the canvas
		canvasCloseButton.add(closeButton);	
		
		return canvasCloseButton;
	}

}






