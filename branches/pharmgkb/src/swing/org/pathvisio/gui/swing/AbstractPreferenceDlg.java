// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.pathvisio.gui.swing.propertypanel.PathwayTableModel;
import org.pathvisio.model.DictionaryManager;
import org.pathvisio.model.Property;
import org.pathvisio.model.PropertyClass;
import org.pathvisio.preferences.Preference;
import org.pathvisio.preferences.PreferenceManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	/** Helper class to build preference panel, based on a list of preferences and their types.
	 * Builds a panel and adds a textfield for a string, checkbox for a boolean, etc. */
	protected static class PreferencePanelBuilder
	{
		PreferenceManager prefs;
		private DefaultFormBuilder builder;
		FormLayout layout;
		
		PreferencePanelBuilder(PreferenceManager prefs)
		{
			layout = new FormLayout("left:pref, 6dlu, 50dlu:grow, 4dlu, default"); 
			builder = new DefaultFormBuilder(layout);
			this.prefs = prefs;
		}
		
		JPanel getPanel()
		{
			return builder.getPanel();
		}

		private class BooleanFieldEditor implements ActionListener
		{
			private Object p;
			private JCheckBox cb;
			
			BooleanFieldEditor(Object p, JCheckBox cb)
			{
				this.p = p;
				this.cb = cb;
				cb.setSelected(prefs.getBoolean(p));
			}

			public void actionPerformed(ActionEvent ae) 
			{
				prefs.setBoolean(p, cb.isSelected());
			}
		}

		void addBooleanField (Object p, String desc)
		{
			if (!(p instanceof Preference || p instanceof Property)) {
				throw new IllegalArgumentException("Key must be a property or preference");
			}
			JCheckBox cb = new JCheckBox (desc);
			BooleanFieldEditor editor = new BooleanFieldEditor (p, cb);
			cb.addActionListener(editor);
			builder.append (cb);
			builder.nextLine();
		}
		
		private class ColorFieldEditor implements ActionListener
		{
			private Preference p;
			private JLabel colorLabel;
			
			ColorFieldEditor (Preference p, JLabel colorLabel)
			{
				this.p = p;
				this.colorLabel = colorLabel;
				colorLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
				colorLabel.setOpaque(true);
				
				colorLabel.setForeground(prefs.getColor (p));
				colorLabel.setBackground(prefs.getColor (p));
			}

			public void actionPerformed (ActionEvent ae) 
			{
				Color newColor = JColorChooser.showDialog(null, "Choose a color", prefs.getColor(p));
				if (newColor != null)
				{
					colorLabel.setBackground(newColor);
					prefs.setColor(p, newColor);
				}
			}
		}
		
		void addColorField (Preference p, String desc)
		{
			JButton btnColor = new JButton("Change...");
			JLabel colorLabel = new JLabel("--");
			ColorFieldEditor editor = new ColorFieldEditor (p, colorLabel);
			btnColor.addActionListener(editor);
			builder.append (new JLabel (desc));
			builder.append (colorLabel);
			builder.append (btnColor);
			builder.nextLine();
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
			JTextField txt = new JTextField(8);
			IntegerFieldEditor editor = new IntegerFieldEditor (p, txt); 
			txt.addActionListener(editor);
			txt.getDocument().addDocumentListener(editor);
			builder.append (new JLabel (desc));
			builder.append (txt);
			builder.nextLine();
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
			JTextField txt = new JTextField(40);
			StringFieldEditor editor = new StringFieldEditor (p, txt); 
			txt.addActionListener(editor);
			builder.append (new JLabel (desc));
			builder.append (txt);
			builder.nextLine();
		}
		
		private class FileFieldEditor implements ActionListener, DocumentListener
		{
			private Object p;
			private JTextField txt;

			FileFieldEditor (Object p, JTextField txt, File f)
			{
				this.p = p;
				this.txt = txt;
				if (f != null){
					prefs.setFile (p, f);
					if (p instanceof Property){
						if (((Property)p).getType().equals(PropertyClass.DICTIONARY)){//dictionary files, set dictionary manager
							DictionaryManager.setDictionaryFile((Property)p, f);
						}
					}

				}
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
					if (p instanceof Property){
						if (((Property)p).getType().equals(PropertyClass.DICTIONARY)){//dictionary files, set dictionary manager
							DictionaryManager.setDictionaryFile((Property)p, result);
						}
					}
				}
			}

			private void update()
			{
				prefs.set (p, txt.getText());
				File result = new File(txt.getText());
				prefs.setFile (p, result);
				if (p instanceof Property){
					if (((Property)p).getType().equals(PropertyClass.DICTIONARY)){//dictionary files, set dictionary manager
						DictionaryManager.setDictionaryFile((Property)p, result);
					}
				}

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

		void addFileField (Object p, String desc, File f, boolean isDir)
		{
			if (!(p instanceof Preference || p instanceof Property)) {
				throw new IllegalArgumentException("Key must be a property or preference");
			}
			
			//TODO: do somethign with isDir
			JTextField txt = new JTextField(40);
			JButton btnBrowse = new JButton("Browse");
			FileFieldEditor editor = new FileFieldEditor (p, txt, f);
			btnBrowse.addActionListener(editor);
			txt.getDocument().addDocumentListener(editor);
			builder.append (new JLabel (desc));
			builder.append (txt);
			builder.append (btnBrowse);
			builder.nextLine();
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
	public void createAndShowGUI(SwingEngine swingEngine)
	{
		final JDialog dlg = new JDialog(swingEngine.getFrame(), "Preferences", true);
		dlg.setLayout (new BorderLayout());
		final PathwayTableModel model = swingEngine.getApplicationPanel().getModel();
		initPanels();
		
		DefaultMutableTreeNode top = createNodes();
		
		JPanel pnlButtons = new JPanel();
		JTree trCategories = new JTree(top);
		final JPanel pnlSettings = new JPanel();
				
		JButton btnOk = new JButton();
		btnOk.setText ("OK");
		
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				dlg.setVisible (false);
				dlg.dispose();
				model.updateModel();
				prefs.store(); // save values to prefs file
			}
		}
		);
		
		JButton btnCancel = new JButton();
		btnCancel.setText ("Cancel");
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				dlg.setVisible(false);
				dlg.dispose();
				//cancel, so restore old values from prefs file
				prefs.load();
			}
		}
		);

		dlg.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent we)
			{
				prefs.load();
			}
		});

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
				String title = tp.getLastPathComponent().toString();
				cards.show(pnlSettings, title);
			}
		});
		
		pnlButtons.add (btnOk);
		pnlButtons.add (btnCancel);
		
		dlg.add (new JScrollPane (pnlSettings), BorderLayout.CENTER);
		dlg.add (trCategories, BorderLayout.WEST);
		dlg.add (pnlButtons, BorderLayout.SOUTH);
		
		dlg.pack();
		dlg.setLocationRelativeTo(swingEngine.getFrame());
		dlg.setVisible (true);
	}
}
