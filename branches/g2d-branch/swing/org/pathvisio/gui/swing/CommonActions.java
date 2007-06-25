package org.pathvisio.gui.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.pathvisio.Engine;
import org.pathvisio.model.PathwayImporter;

public abstract class CommonActions {
	static class SaveAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			//TODO
		}
	}
	
	static class ImportAction extends AbstractAction {
		Component parent;
		
		public void actionPerformed(ActionEvent e) {
				//Open file dialog
				JFileChooser jfc = new JFileChooser();
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.setDialogTitle("Import pathway");
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				
				for(final PathwayImporter imp : Engine.getPathwayImporters().values()) {
					FileFilter ff = new FileFilter() {
						public boolean accept(File f) {
							String fn = f.toString();
							int i = fn.lastIndexOf('.');
							if(i > 0) {
								String ext = fn.substring(i + 1);
								for(String impExt : imp.getExtensions()) {
									if(impExt.equalsIgnoreCase(ext)) {
										return true;
									}
								}
							}
							return false;
						}

						public String getDescription() {
							return imp.getName();
						}
					};
					jfc.addChoosableFileFilter(ff);
				}
				
				int status = jfc.showDialog((Component)e.getSource(), "Import");
				if(status == JFileChooser.APPROVE_OPTION) {
					jfc.getFileFilter();
				}
		}
	}
	
	static class ExportAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			//TODO
		}
	}
}
