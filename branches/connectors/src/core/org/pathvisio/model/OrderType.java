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
package org.pathvisio.model;

import java.awt.event.InputEvent;

import javax.swing.KeyStroke;

public enum OrderType {
	TOP("Bring to front", "Bring the element in front of all other elements of the same type",
			KeyStroke.getKeyStroke(']', InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)),
	BOTTOM("Send to back", "Send the element behind all other elements of the same type",
			KeyStroke.getKeyStroke('[', InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)),
	//UP("Move up", "Move up"),
	//DOWN("Move down", "Move down"),
	
	;
	
	private String description;
	private String name;
	private KeyStroke accelerator;
	
	private OrderType(String name, String description, KeyStroke accelerator) {
		this.name = name;
		this.description = description;
		this.accelerator = accelerator;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

	public Object getAcceleratorKey() {
		return accelerator;
	}
}