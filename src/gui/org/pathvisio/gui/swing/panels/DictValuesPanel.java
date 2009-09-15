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

import org.pathvisio.util.Resources;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.DictionaryValues;
import org.pathvisio.biopax.BiopaxReferenceManager;
import org.pathvisio.biopax.BiopaxElementManager;
import org.pathvisio.biopax.reflect.PublicationXRef;
import org.pathvisio.gui.swing.SwingEngine;
import org.pathvisio.gui.swing.dialogs.DictValuesDialog;
import org.pathvisio.debug.Logger;

import javax.swing.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.net.URL;
import java.util.*;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * @author Rebecca Fieldman
 */
public class DictValuesPanel extends PathwayElementPanel implements ActionListener {
	private static final String ADD = "Select More Values";
	private static final String REMOVE = "Remove";
//	private static final String EDIT = "Edit";
//	private static final URL IMG_EDIT= Resources.getResourceURL("edit.gif");
	private static final URL IMG_REMOVE = Resources.getResourceURL("cancel.gif");

	BiopaxReferenceManager refMgr;
	BiopaxElementManager elmMgr;

	java.util.List<PublicationXRef> xrefs;

	JScrollPane refPanel;
	JButton addBtn;

	final private SwingEngine swingEngine;

	public DictValuesPanel(SwingEngine swingEngine) 
	{
		this.swingEngine = swingEngine;
		setLayout(new BorderLayout(5, 5));
		xrefs = new ArrayList<PublicationXRef>();
		addBtn = new JButton(ADD);
		addBtn.setActionCommand(ADD);
		addBtn.addActionListener(this);
		JPanel addPnl = new JPanel();
		addPnl.add(addBtn);
		add(addPnl, BorderLayout.SOUTH);
	}

	boolean readonly = false;

	public void setReadOnly(boolean readonly) {
		super.setReadOnly(readonly);
		this.readonly = readonly;
		addBtn.setEnabled(!readonly);
	}

	public void setInput(PathwayElement e) {
		if(e != getInput()) {
			elmMgr = e.getParent().getBiopaxElementManager();
			refMgr = e.getBiopaxReferenceManager();
		}
		super.setInput(e);
	}


	private class XRefPanel extends JPanel implements HyperlinkListener, ActionListener {
		PublicationXRef xref;
		JPanel btnPanel;

		public XRefPanel(PublicationXRef xref) {
			this.xref = xref;
			setBackground(Color.WHITE);
			setLayout(new FormLayout(
					"2dlu, fill:[100dlu,min]:grow, 1dlu, pref, 2dlu", "2dlu, pref, 2dlu"
			));
			JTextPane txt = new JTextPane();
			txt.setContentType("text/html");
			txt.setEditable(false);
			txt.setText("<html>" + "<B>" +
					elmMgr.getOrdinal(xref) + ":</B> " +
					xref.toHTML() + "</html>"
			);
			txt.addHyperlinkListener(this);
			CellConstraints cc = new CellConstraints();
			add(txt, cc.xy(2, 2));

			btnPanel = new JPanel(new FormLayout("pref", "pref, pref"));
//			JButton btnEdit = new JButton();
//			btnEdit.setActionCommand(EDIT);
//			btnEdit.addActionListener(this);
//			btnEdit.setIcon(new ImageIcon(IMG_EDIT));
//			btnEdit.setBackground(Color.WHITE);
//			btnEdit.setBorder(null);
//			btnEdit.setToolTipText("Edit literature reference");

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

		public void hyperlinkUpdate(HyperlinkEvent e) {
			if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				swingEngine.openUrl(e.getURL());
			}
		}

		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			/*if(EDIT.equals(action)) {
				edit(xref);
			} else*/
			if (REMOVE.equals(action)) {
				DictValuesPanel.this.remove(xref);
			}
		}
	}

	public void refresh() {
		if(refPanel != null) remove(refPanel);

		xrefs = refMgr.getPublicationXRefs();

		DefaultFormBuilder b = new DefaultFormBuilder(
				new FormLayout("fill:pref:grow")
		);
		for(PublicationXRef xref : xrefs) {
			b.append(new XRefPanel(xref));
			b.nextLine();
		}
		JPanel p = b.getPanel();
		p.setBackground(Color.WHITE);
		refPanel = new JScrollPane(p);
		add(refPanel, BorderLayout.CENTER);
		validate();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(ADD)) {
			addPressed();
		}
	}

	private void edit(DictionaryValues vals) {
		if(vals != null) {
				DictValuesDialog d = new DictValuesDialog(vals, null, this, false);
				d.setVisible(true);
		}
		refresh();
	}

	private void remove(PublicationXRef xref) {
		refMgr.removeElementReference(xref);
		refresh();
	}

	private void addPressed() {
		DictionaryValues vals = new DictionaryValues();

		final DictValuesDialog d = new DictValuesDialog(vals, null, this);
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
		//	refMgr.addElementReference(xref); //XXX what to do here?
			refresh();
		}
	}
}
