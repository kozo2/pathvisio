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

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.pathvisio.gui.swing.propertypanel.TypedProperty;
import org.pathvisio.model.DictionaryEntry;
import org.pathvisio.model.DictionaryManager;
import org.pathvisio.model.Property;
import org.pathvisio.model.PropertyManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Rebecca Tang
 */
public class DictValuesDialog extends OkCancelDialog {

	private static java.util.List<DictionaryEntry> selectedEntries;

	public DictValuesDialog(java.util.List<DictionaryEntry> entries, Frame frame, Component locationComp, TypedProperty property){
		super(frame, "Dictionary Entries", locationComp, true);
		selectedEntries = entries;
		Component p = createDialogPane(property);
		setDialogComponent(p);

		setSize(400, 300);
	}


	protected void okPressed() {
		super.okPressed();
	}

	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
	}

	protected Component createDialogPane(TypedProperty property){
		Property dictProp = PropertyManager.getProperty(((Property)(property.getType())).getId());
		java.util.List<DictionaryEntry> dictValues = DictionaryManager.getValues(dictProp);
		if (dictValues == null || dictValues.isEmpty()){
			File file = DictionaryManager.getDictionaryFile(dictProp);
			try{
				DictionaryManager.processDictionaryXML(dictProp, file);
			}catch(Exception e){
				e.printStackTrace();
			}
			dictValues = DictionaryManager.getValues(dictProp);
		}
		DictValuesDialogBuilder builder = new DictValuesDialogBuilder();
		for (DictionaryEntry entry : dictValues){
			if (!selectedEntries.contains(entry)){ // don't display if selected
				builder.addBooleanField(entry);
			}
		}

		JPanel contents = builder.getPanel();

		return contents;
	}

	/**
	 * used to build the dialog
	 */
	protected static class DictValuesDialogBuilder
	{
		private DefaultFormBuilder m_builder;
		FormLayout m_layout;

		DictValuesDialogBuilder()
		{
			m_layout = new FormLayout("left:pref");
					//new FormLayout("left:pref, 6dlu, 50dlu:grow, 4dlu, default");
			m_builder = new DefaultFormBuilder(m_layout);
		}

		JPanel getPanel()
		{
			return m_builder.getPanel();
		}

		private class BooleanFieldEditor implements ActionListener
		{
			private DictionaryEntry m_entry;
			private JCheckBox m_checkbox;

			BooleanFieldEditor(DictionaryEntry d, JCheckBox cb)
			{
				m_entry = d;
				m_checkbox = cb;
			}

			public void actionPerformed(ActionEvent ae)
			{
				selectedEntries.add(m_entry);
			}
		}

		void addBooleanField (DictionaryEntry d)
		{
			JCheckBox cb = new JCheckBox (d.getName());
			BooleanFieldEditor editor = new BooleanFieldEditor (d, cb);
			cb.addActionListener(editor);

			m_builder.append(cb);
			m_builder.nextLine();			
		}

	}
}
