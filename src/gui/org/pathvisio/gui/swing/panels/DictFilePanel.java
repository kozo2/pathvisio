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
import org.pathvisio.preferences.GlobalPreference;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.net.URL;
import java.io.File;


/**
 * @author Rebecca Tang
 */
public class DictFilePanel extends PathwayElementPanel implements ActionListener {

	//TODO XXX if selected new file, then wipe out all selected values on values panel

//	protected static final String ADD = "Test Dictionary File Panel";
//	protected static final String REMOVE = "Remove Dictionary File";
	private static final URL IMG_REMOVE = Resources.getResourceURL("cancel.gif");
	File m_dictionaryFile = new File(GlobalPreference.getApplicationDir(),"Dictionary.xml"); // this will be populated from properties file file

	JPanel m_buttonPanel;
	JScrollPane cmtPanel;

	public DictFilePanel() {
		setLayout(new BorderLayout(5, 5));

		JTextField txt = new JTextField(40);
		txt.setText(m_dictionaryFile.getAbsolutePath()); // intial value
		JButton btnBrowse = new JButton("Browse");
		FileFieldEditor editor = new FileFieldEditor(txt);
		btnBrowse.addActionListener(editor);
		txt.getDocument().addDocumentListener(editor);
		m_buttonPanel = new JPanel();
		m_buttonPanel.setLayout(new BoxLayout(m_buttonPanel, BoxLayout.LINE_AXIS));
		m_buttonPanel.setAlignmentY(0);
		m_buttonPanel.add(Box.createHorizontalGlue());


		m_buttonPanel.add (new JLabel ("Select Dictionary File: "));
		m_buttonPanel.add (txt);
		m_buttonPanel.add (btnBrowse);

		add(m_buttonPanel, BorderLayout.PAGE_START);
	}

	public void setReadOnly(boolean readonly) {
		super.setReadOnly(readonly);
		setChildrenEnabled(m_buttonPanel, !readonly);
	}

	public void actionPerformed(ActionEvent e) {
/*		if(e.getActionCommand().equals(ADD)) {
			getInput().addComment("Type your comment here", "");
		}
*/
		refresh();
	}

	public void refresh() {
/*		if(cmtPanel != null) remove(cmtPanel);

		DefaultFormBuilder b = new DefaultFormBuilder(
				new FormLayout("fill:pref:grow")
		);
		FileEditor firstEditor = null;
		for(PathwayElement.File f : getInput()) {
			FileEditor ce = new FileEditor(f);
			if(firstEditor == null) firstEditor = ce;
			b.append(ce);
			b.nextLine();
		}
		if(getInput().getComments().size() == 0) {
			FileEditor ce = new FileEditor(null);
			firstEditor = ce;
			b.append(ce);
			b.nextLine();
		}
		JPanel p = b.getPanel();
		cmtPanel = new JScrollPane(p,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
		add(cmtPanel, BorderLayout.CENTER);
		validate();*/
	}

	private class FileFieldEditor extends JPanel implements ActionListener, DocumentListener {
		//PathwayElement.DictionaryFile file;
		File m_file;
		JPanel m_btnPanel;
		JTextField m_txt;

		public FileFieldEditor(JTextField txt) {
            m_txt = txt;
			setBackground(Color.WHITE);
/*			setLayout(new FormLayout(
					"2dlu, fill:[100dlu,min]:grow, 1dlu, pref, 2dlu", "2dlu, pref, 2dlu"
			));
			txt = new JTextPane();
			txt.setText(comment == null ? "Type your comment here" : comment.getComment());
			txt.setBorder(BorderFactory.createEtchedBorder());
			txt.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					update();
				}
				public void insertUpdate(DocumentEvent e) {
					update();
				}
				public void removeUpdate(DocumentEvent e) {
					update();
				}
				void update() {
					if(comment == null) {
						comment = getInput().new Comment(txt.getText(), "");
						getInput().addComment(comment);
					} else {
						comment.setComment(txt.getText());
					}
				}
			});
			*/
			/*txt.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(comment == null) {
						txt.selectAll();
					}
				}
			});
			*/
//			CellConstraints cc = new CellConstraints();
//			dd(txt, cc.xy(2, 2));

//			btnPanel = new JPanel(new FormLayout("pref", "pref"));
//			JButton btnRemove = new JButton();
//			btnRemove.setActionCommand(REMOVE);
//			btnRemove.addActionListener(this);
//			btnRemove.setIcon(new ImageIcon(IMG_REMOVE));
/*			btnRemove.setBackground(Color.WHITE);
			btnRemove.setBorder(null);
			btnRemove.setToolTipText("Remove comment");

			MouseAdapter maHighlight = new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					e.getComponent().setBackground(new Color(200, 200, 255));
				}
				public void mouseExited(MouseEvent e) {
					e.getComponent().setBackground(Color.WHITE);
				}
			};
			btnRemove.addMouseListener(maHighlight);

			btnPanel.add(btnRemove, cc.xy(1, 1));

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
			*/
		}

		public void actionPerformed(ActionEvent ae)
		{
			JFileChooser jfc = new JFileChooser();
			if (jfc.showDialog(null, "Choose") == JFileChooser.APPROVE_OPTION)
			{
				File result = jfc.getSelectedFile();
				m_txt.setText("" + result);
				m_file = result;
			}
		}

		private void update()
		{
			System.out.print("abc");
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


}

