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
package org.pathvisio.gui.swing.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.jdesktop.swingworker.SwingWorker;
import org.pathvisio.biopax.reflect.PublicationXRef;
import org.pathvisio.data.PubMedQuery;
import org.pathvisio.data.PubMedResult;
import org.pathvisio.gui.swing.progress.ProgressDialog;
import org.pathvisio.gui.swing.progress.SwingProgressKeeper;
import org.pathvisio.util.ProgressKeeper;

public class PublicationXRefDialog extends OkCancelDialog {
	private static final long serialVersionUID = 1L;

	final static String ADD = "Add";
	final static String REMOVE = "Remove";
	final static String PMID = "Pubmed ID";
	final static String TITLE = "Title";
	final static String SOURCE = "Source";
	final static String YEAR = "Year";
	final static String AUTHORS = "Authors (separate with " + PublicationXRef.AUTHOR_SEP + ")";
	final static String QUERY = "Query PubMed";
	
	PublicationXRef input;
	JTextField pmId;
	JTextField title;
	JTextField source;
	JTextField year;
	JTextPane authors;
	
	public PublicationXRefDialog(PublicationXRef xref, Frame frame, Component locationComp, boolean cancellable) {
		super(frame, "Literature reference properties", locationComp, true, cancellable);
		input = xref;
		
		setDialogComponent(createDialogPane());
		refresh();
		
		setSize(400, 300);
	}
	
	public PublicationXRefDialog(PublicationXRef xref, Frame frame, Component locationComp) {
		this(xref, frame, locationComp, true);
	}
	
	private void setText(String text, JTextComponent field) {
		if(text != null && text.length() > 0) field.setText(text);
	}
	
	protected void refresh() {
		setText(input.getPubmedId(), pmId);
		setText(input.getTitle(), title);
		setText(input.getSource(), source);
		setText(input.getYear(), year);
		setText(input.getAuthorString(), authors);
	}
	
	protected void okPressed() {
		input.setPubmedId(pmId.getText());
		input.setTitle(title.getText());
		input.setSource(source.getText());
		input.setYear(year.getText());
		input.setAuthors(authors.getText());
		super.okPressed();
	}
	
	protected void queryPressed() {
		final PubMedQuery pmq = new PubMedQuery(pmId.getText());
		final SwingProgressKeeper pk = new SwingProgressKeeper(ProgressKeeper.PROGRESS_UNKNOWN);
		ProgressDialog d = new ProgressDialog(
				JOptionPane.getFrameForComponent(this), 
				"", pk, true, true);
				
		SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
			protected Void doInBackground() throws Exception {
				pk.setTaskName("Querying PubMed");
				pmq.execute();
				pk.finished();
				return null;
			}
		};
		
		sw.execute();
		
		d.setVisible(true);
		
		PubMedResult pmr = pmq.getResult();
		if(pmr != null) {
			title.setText(pmr.getTitle());
			year.setText(pmr.getYear());
			source.setText(pmr.getSource());
			authors.setText(PublicationXRef.createAuthorString(pmr.getAuthors()));
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(QUERY.equals(e.getActionCommand())) {
			queryPressed();
		}
		super.actionPerformed(e);
	}
	
	protected Component createDialogPane() {
		JPanel contents = new JPanel();
		contents.setLayout(new GridBagLayout());
		
		JLabel lbl_pmId = new JLabel(PMID);
		JLabel lbl_title = new JLabel(TITLE);
		JLabel lbl_source = new JLabel(SOURCE);
		JLabel lbl_year = new JLabel(YEAR);
		JLabel lbl_authors = new JLabel(AUTHORS);
		
		pmId = new JTextField();
		title = new JTextField();
		source = new JTextField();
		year = new JTextField();
		final DefaultStyledDocument doc = new DefaultStyledDocument();
		doc.setDocumentFilter(new DocumentFilter() {
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				string = replaceSeparators(string);
				super.insertString(fb, offset, string, attr);
				highlight((StyledDocument)fb.getDocument());
			}
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
				text = replaceSeparators(text);
				super.replace(fb, offset, length, text, attrs);
				highlight((StyledDocument)fb.getDocument());
			}
			String replaceSeparators(String authors) {
				return authors.replaceAll(PublicationXRef.AUTHOR_SEP, PublicationXRef.AUTHOR_SEP + "\n");
			}
			void highlight(StyledDocument doc) {
				SimpleAttributeSet clean = new SimpleAttributeSet();
				doc.setCharacterAttributes(0, doc.getLength(), clean, true);
				SimpleAttributeSet sep = new SimpleAttributeSet();
				sep.addAttribute(StyleConstants.ColorConstants.Foreground, Color.RED);
				sep.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
			    
				String text = authors.getText();
				Pattern p = Pattern.compile(PublicationXRef.AUTHOR_SEP);
			    Matcher m = p.matcher(text);
			    while(m.find()) {
			    	doc.setCharacterAttributes(m.start(), 1, sep, true);
			    }	
			}
		});
		
		authors = new JTextPane(doc);
		
		JButton query = new JButton(QUERY);
		query.addActionListener(this);
		query.setToolTipText("Query publication information from PubMed");
		
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = c.ipady = 5;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;
		c.weightx = 0;
		contents.add(lbl_pmId, c);
		contents.add(lbl_title, c);
		contents.add(lbl_year, c);
		contents.add(lbl_source, c);
		contents.add(lbl_authors, c);
				
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		contents.add(pmId, c);
		contents.add(title, c);
		contents.add(year, c);
		contents.add(source, c);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		contents.add(new JScrollPane(authors), c);
		
		c.gridx = 2;
		c.fill = GridBagConstraints.NONE;
		contents.add(query);
		
		return contents;
	}
}
