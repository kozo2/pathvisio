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
package org.pathvisio.gui.swing.panels;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.pathvisio.debug.Logger;
import org.pathvisio.gui.swing.SwingEngine;
import org.pathvisio.gui.swing.dialogs.DictValuesDialog;
import org.pathvisio.gui.swing.propertypanel.TypedProperty;
import org.pathvisio.model.DictionaryEntry;
import org.pathvisio.model.DictionaryManager;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.util.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * @author Rebecca Fieldman
 */
public class DictValuesPanel extends PathwayElementPanel implements ActionListener {

	private static final String ADD = "Select Values";
	private static final String REMOVE = "Remove";
	private static final URL IMG_REMOVE = Resources.getResourceURL("cancel.gif");

	DictionaryManager dictionaryMgr;


	JScrollPane valuesPanel;
	JButton addBtn;
	TypedProperty property = null;

	final private SwingEngine swingEngine;

	public DictValuesPanel(SwingEngine swingEngine, TypedProperty property)
	{
		this.swingEngine = swingEngine;
		this.property = property;
		setLayout(new BorderLayout(5, 5));
		addBtn = new JButton(ADD);
		addBtn.setActionCommand(ADD);
		addBtn.addActionListener(this);
		JPanel addPnl = new JPanel();
		addPnl.add(addBtn);
		add(addPnl, BorderLayout.SOUTH);
	}

	boolean readonly = false;

	/*public void setReadOnly(boolean readonly) {
		super.setReadOnly(readonly);
		this.readonly = readonly;
		addBtn.setEnabled(!readonly);
	}*/

	public void setInput(PathwayElement e) {
		super.setInput(e);
	}


	private class SelectedValuePanel extends JPanel implements ActionListener {
		DictionaryEntry m_entry;
		JPanel btnPanel;

		public SelectedValuePanel(DictionaryEntry entry) {
			m_entry = entry;
			setBackground(Color.WHITE);
			setLayout(new FormLayout(
					"2dlu, fill:[100dlu,min]:grow, 1dlu, pref, 2dlu", "2dlu, pref, 2dlu"
			));
			JTextPane txt = new JTextPane();
			txt.setContentType("text/html");
			txt.setEditable(false);
			txt.setText("<html>" + entry.getName() + "</html>");
			CellConstraints cc = new CellConstraints();
			add(txt, cc.xy(2, 2));

			btnPanel = new JPanel(new FormLayout("pref", "pref, pref"));

			JButton btnRemove = new JButton();
			btnRemove.setActionCommand(REMOVE);
			btnRemove.addActionListener(this);
			btnRemove.setIcon(new ImageIcon(IMG_REMOVE));
			btnRemove.setBackground(Color.WHITE);
			btnRemove.setBorder(null);
			btnRemove.setToolTipText("Remove this dictionary entry");

			MouseAdapter maHighlight = new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					e.getComponent().setBackground(new Color(200, 200, 255));
				}
				public void mouseExited(MouseEvent e) {
					e.getComponent().setBackground(Color.WHITE);
				}
			};
//			btnEdit.addMouseListener(maHighlight);
			btnRemove.addMouseListener(maHighlight);

//			btnPanel.add(btnEdit, cc.xy(1, 1));
			btnPanel.add(btnRemove, cc.xy(1, 2));

			add(btnPanel, cc.xy(4, 2));
			btnPanel.setVisible(false);

			MouseAdapter maHide = new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					if(!readonly) btnPanel.setVisible(true);
				}
				public void mouseExited(MouseEvent e) {
					if(!contains(e.getPoint())) {
						btnPanel.setVisible(false);
					}
				}
			};
			addMouseListener(maHide);
			txt.addMouseListener(maHide);
		}

		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (REMOVE.equals(action)) {
				DictValuesPanel.this.remove(m_entry);
			}
		}
	}

	public void refresh() {
		if(valuesPanel != null) remove(valuesPanel);

//		List<DictionaryEntry> entries = dictionaryMgr.getSelectedEntries();

		DefaultFormBuilder b = new DefaultFormBuilder(
				new FormLayout("fill:pref:grow")
		);
		for(DictionaryEntry entry : getInput().getDictionaryEntries(property)) {
			b.append(new SelectedValuePanel(entry));
			b.nextLine();
		}

		JPanel p = b.getPanel();
		p.setBackground(Color.WHITE);
		valuesPanel = new JScrollPane(p);
		add(valuesPanel, BorderLayout.CENTER);
		validate();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(ADD)) {
			addPressed();
		}
	}


	private void remove(DictionaryEntry entry) {
		getInput().removeDictionaryEntry(property, entry);
		refresh();
	}

	private void addPressed(){
		final DictValuesDialog d = new DictValuesDialog(getInput().getDictionaryEntries(property), null, this, property);
		if(!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						d.setVisible(true);
					}
				});
			} catch (Exception e) {
				Logger.log.error("Unable to open dialog");
			}
		} else {
			d.setVisible(true);
		}
		if(d.getExitCode().equals(DictValuesDialog.OK)) {
			refresh();
		}
	}
}
