package org.pathvisio.gui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import javax.swing.filechooser.FileFilter;

import org.pathvisio.Engine;
import org.pathvisio.gui.swt.MainWindow;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.PathwayImporter;
import org.pathvisio.view.VPathway;
import org.pathvisio.view.swing.VPathwaySwing;

public abstract class CommonActions {
	private static URL IMG_IMPORT = Engine.getResourceURL("icons/open.gif");
	private static URL IMG_EXPORT = Engine.getResourceURL("icons/save.gif");
	private static URL IMG_COPY= Engine.getResourceURL("icons/save.gif");
	private static URL IMG_PASTE = Engine.getResourceURL("icons/save.gif");
	
	static class ZoomAction extends AbstractAction {
		Component parent;
		double zoomFactor;
		
		public ZoomAction(double zf) {
			zoomFactor = zf;
			String descr = "Set zoom to " + (int)zf + "%";
			putValue(Action.NAME, toString());
			putValue(Action.SHORT_DESCRIPTION, descr);
			putValue(Action.LONG_DESCRIPTION, descr);
		}
		
		public void actionPerformed(ActionEvent e) {
			VPathway vPathway = Engine.getActiveVPathway();
			if(vPathway != null) {
				vPathway.setPctZoom(zoomFactor);
			}
		}
		
		public String toString() {
			if(zoomFactor == VPathway.ZOOM_TO_FIT) {
				return "Fit to window";
			}
			return (int)zoomFactor + "%";
		}
	}
	
	static class SaveAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			//TODO
		}
	}
	
	static class ImportAction extends AbstractAction {
		MainPanel mainPanel;
		
		public ImportAction(MainPanel parent) {
			super("Import", new ImageIcon(IMG_IMPORT));
			mainPanel = parent;
			putValue(Action.SHORT_DESCRIPTION, "Import pathway");
			putValue(Action.LONG_DESCRIPTION, "Import a pathway from various file formats");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		}
		
		public void actionPerformed(ActionEvent e) {
				final Component component = (Component)e.getSource();
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
				
				int status = jfc.showDialog(component, "Import");
				if(status == JFileChooser.APPROVE_OPTION) {
					try {
						int totalWork = 1000;
						ProgressMonitor m = new ProgressMonitor(component, "Loading pathway", "Please wait while the pathway is being loaded", 0, 1000);
						m.setProgress(10);
						Engine.importPathway(jfc.getSelectedFile(), new VPathwaySwing(mainPanel));
						m.setProgress((int)(totalWork*2/3));
						Engine.getActiveVPathway().setEditMode(true);
						m.setProgress(totalWork);
					} catch(ConverterException ex) {
						SwingEngine.handleConverterException(SwingEngine.MSG_UNABLE_IMPORT, component, ex);
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
	
	static class CopyAction extends AbstractAction {
		public CopyAction() {
			super("Copy", new ImageIcon(IMG_COPY));
			String descr = "Copy selected pathway objects to clipboard";
			putValue(Action.SHORT_DESCRIPTION, descr);
			putValue(Action.LONG_DESCRIPTION, descr);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			Engine.getActiveVPathway().copyToClipboard();
		}		
	}
	
	static class PasteAction extends AbstractAction {
		public PasteAction() {
			super("Paste", new ImageIcon(IMG_PASTE));
			String descr = "Paste pathway elements from clipboard";
			putValue(Action.SHORT_DESCRIPTION, descr);
			putValue(Action.LONG_DESCRIPTION, descr);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			Engine.getActiveVPathway().pasteFromClipboad();
		}
	}
	
	static class NewElementAction extends AbstractAction {
		int element;
		public NewElementAction(int type) {
			super();
			element = type;
			
			String descr = "";
			URL imageURL = null;
			switch(element) {
			case VPathway.NEWLINE: 
				descr = "Draw new line";
				imageURL = Engine.getResourceURL("icons/newline.gif");
				break;
			case VPathway.NEWLINEARROW:
				descr = "Draw new arrow";
				imageURL = Engine.getResourceURL("icons/newarrow.gif");
				break;
			case VPathway.NEWLINEDASHED:
				descr = "Draw new dashed line";
				imageURL = Engine.getResourceURL("icons/newdashedline.gif");
				break;
			case VPathway.NEWLINEDASHEDARROW:
				descr = "Draw new dashed arrow";
				imageURL = Engine.getResourceURL("icons/newdashedarrow.gif");
				break;
			case VPathway.NEWLABEL:
				descr = "Draw new label";
				imageURL = Engine.getResourceURL("icons/newlabel.gif");
				break;
			case VPathway.NEWARC:
				descr = "Draw new arc";
				imageURL = Engine.getResourceURL("icons/newarc.gif");
				break;
			case VPathway.NEWBRACE:
				descr = "Draw new brace";
				imageURL = Engine.getResourceURL("icons/newbrace.gif");
				break;
			case VPathway.NEWGENEPRODUCT:
				descr = "Draw new data node";
				imageURL = Engine.getResourceURL("icons/newgeneproduct.gif");
				break;
			case VPathway.NEWRECTANGLE:
				descr = "Draw new rectangle";
				imageURL = Engine.getResourceURL("icons/newrectangle.gif");
				break;
			case VPathway.NEWOVAL:
				descr = "Draw new oval";
				imageURL = Engine.getResourceURL("icons/newoval.gif");
				break;
			case VPathway.NEWTBAR:
				descr = "Draw new TBar";
				imageURL = Engine.getResourceURL("icons/newtbar.gif");
				break;
			case VPathway.NEWRECEPTORROUND:
				descr = "Draw new round receptor";
				imageURL = Engine.getResourceURL("icons/newreceptorround.gif");
				break;
			case VPathway.NEWRECEPTORSQUARE:
				descr = "Draw new square receptor";
				imageURL = Engine.getResourceURL("icons/newreceptorsquare.gif");
				break;
			case VPathway.NEWLIGANDROUND:
				descr = "Draw new round ligand";
				imageURL = Engine.getResourceURL("icons/newligandround.gif");
				break;
			case VPathway.NEWLIGANDSQUARE:
				descr = "Draw new square ligand";
				imageURL = Engine.getResourceURL("icons/newligandsquare.gif");
				break;
			case VPathway.NEWLINEMENU:
				imageURL = Engine.getResourceURL("icons/newlinemenu.gif");
				descr = "Draw new line or arrow";
				break;
			case VPathway.NEWLINESHAPEMENU:
				imageURL = Engine.getResourceURL("icons/newlineshapemenu.gif");
				descr = "Draw new ligand or receptor";
				break;
			}
			putValue(Action.SHORT_DESCRIPTION, descr);
			putValue(Action.LONG_DESCRIPTION, descr);
			if(imageURL != null) {
				putValue(Action.SMALL_ICON, new ImageIcon(imageURL));
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			Engine.getActiveVPathway().setNewGraphics(element);
		}
	}
}
