package org.pathvisio.gui.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.pathvisio.Engine;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.PathwayImporter;
import org.pathvisio.view.VPathwayWrapper;
import org.pathvisio.view.swing.VPathwaySwing;

public abstract class CommonActions {
	private static URL IMG_IMPORT = Engine.getResourceURL("icons/open.gif");
	private static URL IMG_EXPORT = Engine.getResourceURL("icons/save.gif");
	
	static class SaveAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			//TODO
		}
	}
	
	static class ImportAction extends AbstractAction {		
		public ImportAction() {
			super("Import", new ImageIcon(IMG_IMPORT));
			putValue(Action.SHORT_DESCRIPTION, "Import pathway");
			putValue(Action.LONG_DESCRIPTION, "Import a pathway from various file formats");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		}
		
		public void actionPerformed(ActionEvent e) {
				//Open file dialog
				JFileChooser jfc = new JFileChooser();
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.setDialogTitle("Import pathway");
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				
				for(final PathwayImporter imp : Engine.getPathwayImporters().values()) {
					FileFilter ff = new FileFilter() {
						public boolean accept(File f) {
							if(f.isDirectory()) return true;
							
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
							StringBuilder exts = new StringBuilder();
							for(String e : imp.getExtensions()) {
								exts.append(".");
								exts.append(e);
								exts.append(", ");
							}
							String str = exts.substring(0, exts.length() - 2);
							return imp.getName() + " (" + str + ")";
						}
					};
					jfc.addChoosableFileFilter(ff);
				}
				
				int status = jfc.showDialog((Component)e.getSource(), "Import");
				if(status == JFileChooser.APPROVE_OPTION) {
					try {
						Engine.importPathway(jfc.getSelectedFile(), new VPathwaySwing());
						Engine.getActiveVPathway().setEditMode(true);
					} catch(ConverterException ex) {
						SwingEngine.handleConverterException(SwingEngine.MSG_UNABLE_IMPORT, (Component)e.getSource(), ex);
					}
				}
		}
	}
	
	static class ExportAction extends AbstractAction {
		public ExportAction() {
			super("Export", new ImageIcon(IMG_EXPORT));
			putValue(Action.SHORT_DESCRIPTION, "Export pathway");
			putValue(Action.LONG_DESCRIPTION, "Export the pathway to various file formats");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
			
		}
		
		public void actionPerformed(ActionEvent e) {
			//TODO
		}
	}
}
