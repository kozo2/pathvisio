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
package org.pathvisio.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.pathvisio.model.GroupStyle;

/**
 * Keeps track of all GroupPainters.
 * @author thomas
 */
public class GroupPainterRegistry {
	private static Map<String, GroupPainter> painters = new HashMap<String, GroupPainter>();
	
	/**
	 * Register a painter that will be used for the given group style.
	 * @param name The name of the group style (use {@link GroupStyle#toString()}.
	 * @param painter The painter that will draw the group style
	 */
	public static void registerPainter(String name, GroupPainter painter) {
		painters.put(name, painter);
	}
	
	/**
	 * Get the registered painter for the given group style.
	 * @param name The name of the group style (use {@link GroupStyle#toString()}.
	 * @return The registered painter, or the default painter if no custom painters
	 * are registered for the given group style.
	 */
	public static GroupPainter getPainter(String name) {
		GroupPainter p = painters.get(name);
		if(p == null) p = defaultPainter;
		return p;
	}
	
	private static final int TRANSLUCENCY_LEVEL = (int) (255 * .10);
	
	private static GroupPainter defaultPainter = new GroupPainter() {
		public void drawGroup(Graphics2D g, Group group, int flags) {
			boolean mouseover = (flags & Group.FLAG_MOUSEOVER) != 0;
			boolean anchors = (flags & Group.FLAG_ANCHORSVISIBLE) != 0;
			boolean selected = (flags & Group.FLAG_SELECTED) != 0;
			
			// Draw group outline
			int sw = 1;
			Rectangle2D rect = group.getVBounds();
			//fill
			g.setColor(new Color(180, 180, 100, TRANSLUCENCY_LEVEL));
			g.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect
					.getWidth()
					, (int) rect.getHeight() );
			//border
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(sw, BasicStroke.CAP_SQUARE,
					BasicStroke.JOIN_MITER, 1, new float[] { 4, 2 }, 0));
			g.drawRect((int) rect.getX() , (int) rect.getY() , (int) rect
					.getWidth()
					- sw, (int) rect.getHeight() - sw);
			
			//Group highlight, on mouseover, linkanchors display and selection
			if(mouseover || anchors || selected) {
				//fill
				g.setColor(new Color(255, 0, 0, (int)(255 * .05)));
				g.fillRect((int) rect.getX(), (int) rect.getY(), 
						(int) rect.getWidth(), (int) rect.getHeight() );
				//border
				g.setColor(Color.GRAY);
				g.setStroke(new BasicStroke(sw, BasicStroke.CAP_SQUARE,
						BasicStroke.JOIN_MITER, 1, new float[] { 4, 2 }, 0));
				g.drawRect((int) rect.getX() , (int) rect.getY() , 
						(int) rect.getWidth() - sw, (int) rect.getHeight() - sw);
			}
		}
	};
	
	private static GroupPainter groupPainter = new GroupPainter() {
		public void drawGroup(Graphics2D g, Group group, int flags) {
			boolean mouseover = (flags & Group.FLAG_MOUSEOVER) != 0;
			boolean anchors = (flags & Group.FLAG_ANCHORSVISIBLE) != 0;
			boolean selected = (flags & Group.FLAG_SELECTED) != 0;
			
			Rectangle2D rect = group.getVBounds();
			
			//Group highlight, on mouseover, linkanchors display and selection
			if(mouseover || anchors || selected) {
				int sw = 1;
				//fill
				g.setColor(new Color(0, 0, 255, (int)(255 * .05)));
				g.fillRect((int) rect.getX(), (int) rect.getY(), 
						(int) rect.getWidth(), (int) rect.getHeight() );
				//border
				g.setColor(Color.GRAY);
				g.setStroke(new BasicStroke(sw, BasicStroke.CAP_SQUARE,
						BasicStroke.JOIN_MITER, 1, new float[] { 4, 2 }, 0));
				g.drawRect((int) rect.getX() , (int) rect.getY() , 
						(int) rect.getWidth() - sw, (int) rect.getHeight() - sw);
			}
			//User hint is drawn on mouseover, if it fits within the group bounds
			if(mouseover && !anchors) {
				//Draw a hint to tell the user that click selects group
				String hint = selected ? "Drag to move group" : "Click to select group";
				
				Rectangle2D tb = g.getFontMetrics().getStringBounds(hint, g);
					
				if(tb.getWidth() <= rect.getWidth()) {
					int yoffset = (int)rect.getY();
					int xoffset = (int)rect.getX() + (int)(rect.getWidth() / 2) - (int)(tb.getWidth() / 2);
					yoffset += (int)(rect.getHeight() / 2) + (int)(tb.getHeight() / 2);
					g.drawString(hint, xoffset, yoffset);
				}
			}
		}
	};
	
	//Register default painters
	static {
		registerPainter(GroupStyle.COMPLEX.toString(), defaultPainter);
		registerPainter(GroupStyle.NONE.toString(), defaultPainter);
		registerPainter(GroupStyle.GROUP.toString(), groupPainter);
	}
}
