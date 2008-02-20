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
package org.pathvisio.gui.wikipathways;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.JOptionPane;

import org.pathvisio.debug.Logger;
import org.pathvisio.model.Pathway;

public class GenMappExporter {
	public static void main(String[] args) {
		try {
			if(args.length != 2) {
				throw new IllegalArgumentException(
						"Invalid number of arguments: " + args.length
				);
			}
			URL pwUrl = new URL(args[0]);
			String pwName = args[1];
			File mappFile = doExport(pwUrl, pwName);
			
			BasicService basicService = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
			basicService.showDocument(new URL("file://" + mappFile.getAbsolutePath()));
		} catch(Exception e) {
			Logger.log.error("Error converting to GenMAPP format", e);
			String message = "Unable to save GenMAPP file\n" + e.getClass() + ": " + e.getMessage(); 				
			if(e.getCause() instanceof SQLException) {
				message = "Exporting GenMAPP files is only supported on Windows";
			}
			JOptionPane.showMessageDialog(null, 
					message + "\nSee error log for datails", 
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
		System.exit(0);
	}

	/**
	 * Export to GenMAPP
	 */
	static File doExport(URL pwUrl, String pwName) throws Exception {
		//Load the pathway
		URLConnection con = pwUrl.openConnection();
		InputStreamReader reader = new InputStreamReader(con.getInputStream());
		Pathway pathway = new Pathway();
		pathway.readFromXml(reader, true);

		//Convert to temp file
		File mappFile = File.createTempFile(pwName, ".mapp");
		pathway.writeToMapp(mappFile);

		return mappFile;
	}
}
