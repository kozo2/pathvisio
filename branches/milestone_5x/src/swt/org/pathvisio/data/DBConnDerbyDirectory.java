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
package org.pathvisio.data;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * user interface functions for directory-based Derby databases
 */
public class DBConnDerbyDirectory extends DataDerbyDirectory implements DBConnectorSwt
{
	public String openChooseDbDialog(Shell shell) {
		DirectoryDialog dd = DBConnectorUtils.createDirectoryDialog(this, shell);
		return dd.open();
	}

	public String openNewDbDialog(Shell shell, String defaultName) {
		DirectoryDialog dd = DBConnectorUtils.createDirectoryDialog(this, shell);
		if(defaultName != null) dd.setFilterPath(defaultName);
		return dd.open();
	}
}