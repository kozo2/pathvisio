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
package org.pathvisio.gui.swing.dialogs;

import org.pathvisio.biopax.reflect.PublicationXRef;
import org.pathvisio.model.DictionaryManager;
import org.pathvisio.model.DictionaryValues;
import org.pathvisio.model.PropertyManager;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.TreeMap;
import java.util.Map;
import java.util.SortedMap;
import java.io.File;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Rebecca Fieldman
 */
public class DictValuesDialog extends OkCancelDialog {

	File file = new File("D:\\work\\dev\\PathVisio\\src\\gui\\org\\pathvisio\\gui\\swing\\panels\\DictionaryTest.xml"); //XXX make this dynamic!! this should be set
/*	final static String ADD = "Add Test";
	final static String REMOVE = "Remove Test";
	final static String PMID = "Pubmed ID Test";
	final static String TITLE = "Title Test";
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
*/
	DictionaryValues input;
	public DictValuesDialog(DictionaryValues vals, Frame frame, Component locationComp, boolean cancellable){
		super(frame, "Dictionary Entries", locationComp, true, cancellable);
		input = vals;

		setDialogComponent(createDialogPane());
		refresh();

		setSize(400, 300);
	}

	public DictValuesDialog(DictionaryValues vals, Frame frame, Component locationComp){
		this(vals, frame, locationComp, true);
	}

/*	private void setText(String text, JTextComponent field) {
		if(text != null && text.length() > 0) field.setText(text);
	}
*/
	protected void refresh() {
/*		setText(input.getPubmedId(), pmId);
		setText(input.getTitle(), title);
		setText(input.getSource(), source);
		setText(input.getYear(), year);
		setText(input.getAuthorString(), authors);
*/
	}

	protected void okPressed() {
/*		input.setPubmedId(pmId.getText());
		input.setTitle(title.getText());
		input.setSource(source.getText());
		input.setYear(year.getText());
		input.setAuthors(authors.getText());
*/
		super.okPressed();
	}

	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
	}

	protected Component createDialogPane(){
		try{

			DictionaryManager.processDictionaryXML(PropertyManager.getProperty("relatedGenes"), file);
		}catch(Exception e){
			e.printStackTrace();	
		}
		DictValuesDialogBuilder builder = new DictValuesDialogBuilder(file.getName());
		SortedMap<String, String> dictValues = DictionaryManager.getValues(PropertyManager.getProperty("relatedGenes"));
		for (Map.Entry<String, String> e : dictValues.entrySet()){
			builder.addBooleanField(e.getValue());
		}

		JPanel contents = builder.getPanel();
		contents.setLayout(new GridBagLayout());

/*		JLabel lblPmId = new JLabel(PMID);
		JLabel lblTitle = new JLabel(TITLE);
		JLabel lblSource = new JLabel(SOURCE);
		JLabel lblYear = new JLabel(YEAR);
		JLabel lblAuthors = new JLabel(AUTHORS);

		pmId = new JTextField();
		title = new JTextField();
		source = new JTextField();
		year = new JTextField();
*/
/*		final DefaultStyledDocument doc = new DefaultStyledDocument();
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
*/
/*		authors = new JTextPane(doc);

		JButton query = new JButton(QUERY);
		query.addActionListener(this);
		query.setToolTipText("Query publication information from PubMed");

		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = c.ipady = 5;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;
		c.weightx = 0;
		contents.add(lblPmId, c);
		contents.add(lblTitle, c);
		contents.add(lblYear, c);
		contents.add(lblSource, c);
		contents.add(lblAuthors, c);

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
*/
		return contents;
	}

	/**
	 * used to build the dialog
	 */
	protected static class DictValuesDialogBuilder
	{
		private DefaultFormBuilder m_builder;
		FormLayout m_layout;
		String m_fileName;

		DictValuesDialogBuilder(String fileName)
		{
			m_layout = new FormLayout("left:pref, 6dlu, 50dlu:grow, 4dlu, default");
			m_builder = new DefaultFormBuilder(m_layout);
			m_fileName = fileName;
		}

		JPanel getPanel()
		{
			return m_builder.getPanel();
		}

		private class BooleanFieldEditor implements ActionListener
		{
			private String m_value;
			private JCheckBox m_checkbox;

			BooleanFieldEditor(String value, JCheckBox cb)
			{
				m_value = value;
				m_checkbox = cb;
//				cb.setSelected(DictionaryManager.getBoolean(m_fileName, value));
			}

			public void actionPerformed(ActionEvent ae)
			{
//				DictionaryManager.setBoolean(m_fileName,m_value, m_checkbox.isSelected());
			}
		}

		void addBooleanField (String value)
		{
			JCheckBox cb = new JCheckBox (value);
			BooleanFieldEditor editor = new BooleanFieldEditor (value, cb);
			cb.addActionListener(editor);
			m_builder.append(cb);
			m_builder.nextLine();			
		}

	}

}
