// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2007 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package org.pathvisio.gui.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import java.util.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import org.pathvisio.preferences.Preference;
import org.pathvisio.preferences.PreferenceManager;

/**
 * Global dialog for setting the user preferences.
 */
abstract public class AbstractPreferenceDlg 
{
	private DefaultMutableTreeNode createNodes()
	{
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Preferences");
		
		List<String> panelTitles = new ArrayList<String>();
		panelTitles.addAll (panels.keySet());
		
		Collections.sort (panelTitles);

		for (String title : panelTitles)
		{	
			top.add (new DefaultMutableTreeNode (title));
		}
		
		return top;
	}
	
	private Map <String, JPanel> panels = new HashMap <String, JPanel>();
	
	public void addPanel (String title, JPanel panel)
	{
		panels.put (title, panel);
	}
	
	abstract protected void initPanels();
	
	protected PreferencePanelBuilder createBuilder ()
	{
		return new PreferencePanelBuilder (prefs);
	}
	
	protected static class PreferencePanelBuilder
	{
		private JPanel result = new JPanel();
		
		PreferenceManager prefs;
		
		PreferencePanelBuilder(PreferenceManager prefs)
		{
			result.setLayout(new FlowLayout());
			this.prefs = prefs;
		}
		
		JPanel getPanel()
		{
			return result;
		}

		private class BooleanFieldEditor implements ActionListener
		{
			private Preference p;
			private JCheckBox cb;
			
			BooleanFieldEditor(Preference p, JCheckBox cb)
			{
				this.p = p;
				this.cb = cb;
				cb.setSelected (prefs.getBoolean (p));
			}

			public void actionPerformed(ActionEvent ae) 
			{
				prefs.setBoolean(p, cb.isSelected());
			}
		}

		void addBooleanField (Preference p, String desc)
		{
			JCheckBox cb = new JCheckBox (desc);
			BooleanFieldEditor editor = new BooleanFieldEditor (p, cb);
			cb.addActionListener(editor);
			result.add (cb);
		}
		
		private class ColorFieldEditor implements ActionListener
		{
			private Preference p;
			private JButton btn;
			
			ColorFieldEditor (Preference p, JButton btn)
			{
				this.p = p;
				this.btn = btn;
				btn.setBackground(prefs.getColor (p));
			}

			public void actionPerformed (ActionEvent ae) 
			{
				Color newColor = JColorChooser.showDialog(null, "Choose a color", prefs.getColor(p));
				if (newColor != null)
				{
					btn.setBackground(newColor);
					prefs.setColor(p, newColor);
				}
			}
		}
		
		void addColorField (Preference p, String desc)
		{
			result.add (new JLabel (desc));
			JButton btnColor = new JButton();
			ColorFieldEditor editor = new ColorFieldEditor (p, btnColor);
			btnColor.addActionListener(editor);
			result.add (btnColor);
		}
		
		private class IntegerFieldEditor implements ActionListener, DocumentListener
		{
			private Preference p;
			private JTextField txt;
			
			IntegerFieldEditor (Preference p, JTextField txt)
			{
				this.txt = txt;
				this.p = p;
				txt.setText(prefs.get(p));
			}

			private void update()
			{
				try
				{
					prefs.set (p, "" + Integer.parseInt (txt.getText()));
				}
				catch (NumberFormatException e)
				{
					// ignore, we just leave the old value
				}
			}
			
			public void actionPerformed(ActionEvent e) 
			{
				update();
			}

			public void changedUpdate(DocumentEvent de)
			{
				update();
			}

			public void insertUpdate(DocumentEvent de) 
			{
				update();
			}

			public void removeUpdate(DocumentEvent de) 
			{
				update();
			}
		}
		
		void addIntegerField (Preference p, String desc, int min, int max)
		{
			//TODO: handle min / max
			result.add (new JLabel (desc));
			JTextField txt = new JTextField(8);
			IntegerFieldEditor editor = new IntegerFieldEditor (p, txt); 
			txt.addActionListener(editor);
			txt.getDocument().addDocumentListener(editor);
			result.add (txt);
		}

		private class StringFieldEditor implements ActionListener
		{
			private Preference p;
			private JTextField txt;
			
			StringFieldEditor (Preference p, JTextField txt)
			{
				this.txt = txt;
				this.p = p;
				txt.setText (prefs.get(p));
			}

			public void actionPerformed(ActionEvent e) 
			{
				prefs.set (p, txt.getText());
			}
		}
		
		void addStringField (Preference p, String desc)
		{
			result.add (new JLabel (desc));
			JTextField txt = new JTextField(40);
			StringFieldEditor editor = new StringFieldEditor (p, txt); 
			txt.addActionListener(editor);
			result.add (txt);
		}
		
		private class FileFieldEditor implements ActionListener, DocumentListener
		{
			private Preference p;
			private JTextField txt;
			
			FileFieldEditor (Preference p, JTextField txt)
			{
				this.p = p;
				this.txt = txt;
				txt.setText (prefs.get(p));
			}

			public void actionPerformed(ActionEvent ae) 
			{
				JFileChooser jfc = new JFileChooser();
				if (jfc.showDialog(null, "Choose") == JFileChooser.APPROVE_OPTION)
				{
					File result = jfc.getSelectedFile();
					txt.setText("" + result);
					prefs.setFile (p, result);
				}
			}

			private void update()
			{
				prefs.set (p, txt.getText());
			}
			
			public void changedUpdate(DocumentEvent de) 
			{
				update();
			}

			public void insertUpdate(DocumentEvent de) 
			{
				update();
			}

			public void removeUpdate(DocumentEvent de)
			{
				update();
			}
		}
		
		void addFileField (Preference p, String desc, boolean isDir)
		{
			//TODO: do somethign with isDir
			result.add (new JLabel (desc));
			JTextField txt = new JTextField(40);
			JButton btnBrowse = new JButton("Browse");
			FileFieldEditor editor = new FileFieldEditor (p, txt); 
			btnBrowse.addActionListener(editor);
			txt.getDocument().addDocumentListener(editor);
			result.add (txt);
			result.add (btnBrowse);
		}
		
	}
	
	PreferenceManager prefs;
	
	public AbstractPreferenceDlg (PreferenceManager prefs)
	{
		this.prefs = prefs;
	}
	
	/**
	 * call this to open the dialog
	 */
	public void createAndShowGUI()
	{
		final JFrame frame = new JFrame();
		frame.setLayout (new BorderLayout());
		
		initPanels();
		
		DefaultMutableTreeNode top = createNodes();
		
		JPanel pnlButtons = new JPanel();
		JTree trCategories = new JTree(top);
		final JPanel pnlSettings = new JPanel();
				
		JButton OkBtn = new JButton();
		OkBtn.setText ("OK");
		
		OkBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				frame.setVisible (false);
				frame.dispose();
			}
		}
		);
		
		JButton btnCancel = new JButton();
		btnCancel.setText ("Cancel");
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				frame.setVisible(false);
				frame.dispose();
			}
		}
		);
		
		final CardLayout cards = new CardLayout();
		pnlSettings.setLayout (cards);
		for (String title : panels.keySet())
		{
			pnlSettings.add (panels.get(title), title);
		}

		trCategories.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e) 
			{
				TreePath tp = e.getPath();
				String title = ((TreeNode)tp.getLastPathComponent()).toString();
				cards.show(pnlSettings, title);
			}
		});
		
		pnlButtons.add (OkBtn);
		pnlButtons.add (btnCancel);
		
		frame.add (pnlSettings, BorderLayout.CENTER);
		frame.add (trCategories, BorderLayout.WEST);
		frame.add (pnlButtons, BorderLayout.SOUTH);
		
		frame.setSize (500,380);
		frame.setTitle ("preferences");
		frame.setVisible (true);
	}
}
