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
package org.pathvisio.visualization.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.pathvisio.visualization.colorset.ColorGradient;

/**
 * A JComboBox that renders gradients.
 * When using this class, do not add or remove items directly,
 * but use {@link #setGradients(List)}!
 * @author thomas
 *
 */
public class ColorGradientCombo extends JComboBox
{
	public ColorGradientCombo() {
		super();
		setRenderer(new ColorGradientRenderer());
	}
	
	HashMap<String, ColorGradient> id2gradient = new HashMap<String, ColorGradient>();
	
	public void setGradients(List<ColorGradient> gradients) {
		/*
		 * Dirty hack to make the combo work. It doesn't seem to fire/process
		 * selection events when we fill the combo with instances of ColorGradient
		 * directly. So we fill it with strings and lookup the ColorGradient
		 * object.
		 */
		id2gradient.clear();
		removeAllItems();
		for(ColorGradient g : gradients) {
			String id = g.hashCode() + "";
			id2gradient.put(id, g);
			addItem(id);
		}
	}
	
	public ColorGradient getGradient(String id) {
		return id2gradient.get(id);
	}
	
	public ColorGradient getSelectedGradient() {
		return id2gradient.get(getSelectedItem());
	}
	
	class ColorGradientRenderer extends JLabel implements ListCellRenderer 
	{
		private static final long serialVersionUID = 1L;
		ColorGradient current;
		Border b_selected = BorderFactory.createLineBorder(Color.BLACK, 3);
		Border b_unselected = BorderFactory.createLineBorder(Color.GRAY, 1);
		
		public ColorGradientRenderer() 
		{
			super();
			setOpaque(true);
			setBorder (UIManager.getBorder("List.noFocusBorder"));
			setPreferredSize(new Dimension(50, 30));
		}
		
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) 
		{
			setBorder(isSelected ? b_selected : b_unselected);
			String id = (String)value;
			current = id2gradient.get(id);
			return this;
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if(current != null) 
			{
				int b = getBorder() == b_selected ? 3 : 1;
				Dimension d = getSize();
				current.paintPreview(
						(Graphics2D)g.create(), new Rectangle(b, b, d.width - b*2, d.height - b*2)
				);
			}
		}
	}
}
