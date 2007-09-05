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
package org.pathvisio.view.swing;

import java.awt.event.KeyEvent;

public class SwingKeyEvent extends org.pathvisio.view.KeyEvent {
	KeyEvent awtEvent;
	
	public SwingKeyEvent(KeyEvent e) {
		super(e.getSource(), convertKeyCode(e), convertType(e), e.getModifiers());
		awtEvent = e;
	}
	
	protected static int convertKeyCode(KeyEvent e) {
		if(e.getID() == KeyEvent.KEY_TYPED) {
			return e.getKeyChar();
		} else {
			switch(e.getKeyCode()) {
				case KeyEvent.VK_CONTROL:
					return org.pathvisio.view.KeyEvent.CTRL;
				case KeyEvent.VK_ALT:
					return org.pathvisio.view.KeyEvent.ALT;
				case KeyEvent.VK_DELETE:
					return org.pathvisio.view.KeyEvent.DEL;
				case KeyEvent.VK_SHIFT:
					return org.pathvisio.view.KeyEvent.SHIFT;
				case KeyEvent.VK_INSERT:
					return org.pathvisio.view.KeyEvent.INSERT;
				default:
					return e.getKeyCode();
			}
		}
	}
	
	protected static int convertType(KeyEvent e) {
		switch(e.getID()) {
		case KeyEvent.KEY_PRESSED:
			return org.pathvisio.view.KeyEvent.KEY_PRESSED;
		case KeyEvent.KEY_RELEASED:
			return org.pathvisio.view.KeyEvent.KEY_RELEASED;
		default:
			throw new IllegalArgumentException("KeyEvent type not supported");
		}
	}
}
