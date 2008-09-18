//PathVisio,
//a tool for data visualization and analysis using Biological Pathways
//Copyright 2006-2007 BiGCaT Bioinformatics

//Licensed under the Apache License, Version 2.0 (the "License"); 
//you may not use this file except in compliance with the License. 
//You may obtain a copy of the License at 

//http://www.apache.org/licenses/LICENSE-2.0 

//Unless required by applicable law or agreed to in writing, software 
//distributed under the License is distributed on an "AS IS" BASIS, 
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
//See the License for the specific language governing permissions and 
//limitations under the License.

package org.pathvisio.gui.swing.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.pathvisio.Engine;
import org.pathvisio.biopax.BiopaxElementManager;
import org.pathvisio.biopax.BiopaxReferenceManager;
import org.pathvisio.biopax.reflect.PublicationXRef;
import org.pathvisio.debug.Logger;
import org.pathvisio.gui.swing.dialogs.PublicationXRefDialog;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.util.Resources;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class LitReferencePanel extends PathwayElementPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	static final String ADD = "New reference";
	static final String REMOVE = "Remove";
	static final String EDIT = "Edit";
	private static URL IMG_EDIT= Resources.getResourceURL("edit.gif");
	private static URL IMG_REMOVE = Resources.getResourceURL("cancel.gif");
	
	BiopaxReferenceManager refMgr;
	BiopaxElementManager elmMgr;
	
	List<PublicationXRef> xrefs;

	JScrollPane refPanel;
	JButton addBtn;
	
	public LitReferencePanel() {
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
		if(readonly) {
			//TODO:
		}
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
			JButton btnEdit = new JButton();
			btnEdit.setActionCommand(EDIT);
			btnEdit.addActionListener(this);
			btnEdit.setIcon(new ImageIcon(IMG_EDIT));
			btnEdit.setBackground(Color.WHITE);
			btnEdit.setBorder(null);
			btnEdit.setToolTipText("Edit literature reference");
			
			JButton btnRemove = new JButton();
			btnRemove.setActionCommand(REMOVE);
			btnRemove.addActionListener(this);
			btnRemove.setIcon(new ImageIcon(IMG_REMOVE));
			btnRemove.setBackground(Color.WHITE);
			btnRemove.setBorder(null);
			btnRemove.setToolTipText("Remove literature reference");
			
			MouseAdapter ma_highlight = new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					e.getComponent().setBackground(new Color(200, 200, 255));
				}
				public void mouseExited(MouseEvent e) {
					e.getComponent().setBackground(Color.WHITE);
				}
			};
			btnEdit.addMouseListener(ma_highlight);
			btnRemove.addMouseListener(ma_highlight);
			
			btnPanel.add(btnEdit, cc.xy(1, 1));
			btnPanel.add(btnRemove, cc.xy(1, 2));
			
			add(btnPanel, cc.xy(4, 2));
			btnPanel.setVisible(false);
			
			MouseAdapter ma_hide = new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					if(!readonly) btnPanel.setVisible(true);
				}
				public void mouseExited(MouseEvent e) {
					if(!contains(e.getPoint())) {
						btnPanel.setVisible(false);
					}
				}
			};
			addMouseListener(ma_hide);
			txt.addMouseListener(ma_hide);
		}
		
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				Engine.getCurrent().openUrl(e.getURL());
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if(EDIT.equals(action)) {
				edit(xref);
			} else if (REMOVE.equals(action)) {
				LitReferencePanel.this.remove(xref);
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

	private void edit(PublicationXRef xref) {
		if(xref != null) {
				PublicationXRefDialog d = new PublicationXRefDialog(xref, null, this, false);
				d.setVisible(true);
		}
		refresh();
	}

	private void remove(PublicationXRef xref) {
		refMgr.removeElementReference(xref);
		refresh();
	}

	private void addPressed() {
		PublicationXRef xref = new PublicationXRef();

		final PublicationXRefDialog d = new PublicationXRefDialog(xref, null, this);
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
		if(d.getExitCode().equals(PublicationXRefDialog.OK)) {
			refMgr.addElementReference(xref);
			refresh();
		}
	}
}
