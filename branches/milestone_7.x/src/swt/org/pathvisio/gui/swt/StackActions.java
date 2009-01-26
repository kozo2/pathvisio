//	 PathVisio,
//	 a tool for data visualization and analysis using Biological Pathways
//	 Copyright 2006-2007 BiGCaT Bioinformatics
	//
//	 Licensed under the Apache License, Version 2.0 (the "License"); 
//	 you may not use this file except in compliance with the License. 
//	 You may obtain a copy of the License at 
//	 
//	 http://www.apache.org/licenses/LICENSE-2.0 
//	  
//	 Unless required by applicable law or agreed to in writing, software 
//	 distributed under the License is distributed on an "AS IS" BASIS, 
//	 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
//	 See the License for the specific language governing permissions and 
//	 limitations under the License.
	//

package org.pathvisio.gui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.pathvisio.Engine;
import org.pathvisio.util.Resources;
import org.pathvisio.view.StackType;
public class StackActions extends Action {
	MainWindowBase window;
	StackType type;
	
	public StackActions(StackType t, MainWindowBase w) {
		window = w;
		type = t;
		setToolTipText (type.getDescription());
		setImageDescriptor(ImageDescriptor.createFromURL(Resources.getResourceURL(type.getIcon())));
	}
	
	public void run () {
		Engine.getCurrent().getActiveVPathway().stackSelected(type);
	}
}