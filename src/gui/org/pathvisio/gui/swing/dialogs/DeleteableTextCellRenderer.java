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

import org.pathvisio.util.Resources;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

/**
 * @author Rebecca Tang
 */
public class DeleteableTextCellRenderer implements TableCellRenderer, MouseListener {
	private static final String REMOVE_BUTTON_ACTION = "Remove";
	private static final URL REMOVE_BUTTON_IMG = Resources.getResourceURL("cancel.gif");

	private JTable m_table;
	private long m_lastClick;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
												   int row, int column) {

		JPanel panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(value == null ? "" : value.toString());
		panel.add(label, BorderLayout.CENTER);
		JButton btnRemove = new JButton();
		btnRemove.setActionCommand(REMOVE_BUTTON_ACTION);
		btnRemove.setIcon(new ImageIcon(REMOVE_BUTTON_IMG));
		btnRemove.setBackground(Color.WHITE);
		btnRemove.setBorder(null);
		btnRemove.setToolTipText("Remove this dictionary entry");
		panel.add(btnRemove, BorderLayout.EAST);
		table.addMouseListener(this);
		m_table = table;
		return panel;
	}

	private void remove(MouseEvent e){
		if (e.getWhen() > m_lastClick){
			int row = e.getY()/m_table.getRowHeight();
			((DictionaryTableModel)m_table.getModel()).removeValueAt(row);
			m_lastClick = e.getWhen();
		}
	}

	public void mouseClicked(MouseEvent e) {
		remove(e);
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}
