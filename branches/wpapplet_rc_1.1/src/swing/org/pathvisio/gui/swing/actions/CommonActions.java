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
package org.pathvisio.gui.swing.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.pathvisio.ApplicationEvent;
import org.pathvisio.Engine;
import org.pathvisio.Engine.ApplicationEventListener;
import org.pathvisio.biopax.BiopaxElementManager;
import org.pathvisio.biopax.BiopaxReferenceManager;
import org.pathvisio.biopax.reflect.PublicationXRef;
import org.pathvisio.gui.swing.SwingEngine;
import org.pathvisio.gui.swing.dialogs.PathwayElementDialog;
import org.pathvisio.gui.swing.dialogs.PublicationXRefDialog;
import org.pathvisio.gui.swt.NewElementAction;
import org.pathvisio.model.DataNodeType;
import org.pathvisio.model.LineStyle;
import org.pathvisio.model.LineType;
import org.pathvisio.model.OrderType;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.ShapeType;
import org.pathvisio.view.AlignType;
import org.pathvisio.view.DefaultTemplates;
import org.pathvisio.view.Graphics;
import org.pathvisio.view.SelectionBox;
import org.pathvisio.view.StackType;
import org.pathvisio.view.Template;
import org.pathvisio.view.VPathway;
import org.pathvisio.view.VPathwayElement;
import org.pathvisio.view.VPathwayEvent;
import org.pathvisio.view.VPathwayListener;
import org.pathvisio.view.ViewActions;
import org.pathvisio.view.ViewActions.CopyAction;
import org.pathvisio.view.ViewActions.PasteAction;
import org.pathvisio.view.ViewActions.UndoAction;

/**
 * A collection of {@link Action}s that may be used throughout the program (e.g. in
 * toolbars, menubars and right-click menu). These actions are registered to the proper
 * group in {@ViewActions} when a new {@link VPathway} is created.
 * @author thomas
 * @see {@link ViewActions}
 */
public class CommonActions implements ApplicationEventListener {
	private static URL IMG_SAVE = Engine.getCurrent().getResourceURL("icons/save.gif");
	private static URL IMG_SAVEAS = Engine.getCurrent().getResourceURL("icons/saveas.gif");
	private static URL IMG_IMPORT = Engine.getCurrent().getResourceURL("icons/import.gif");
	private static URL IMG_EXPORT = Engine.getCurrent().getResourceURL("icons/export.gif");
	
	public void applicationEvent(ApplicationEvent e) {
		if(e.getType() == ApplicationEvent.VPATHWAY_CREATED) {
			ViewActions va = ((VPathway)e.getSource()).getViewActions();
			va.registerToGroup(saveAction, 	ViewActions.GROUP_ENABLE_VPATHWAY_LOADED);
			va.registerToGroup(saveAsAction,	ViewActions.GROUP_ENABLE_VPATHWAY_LOADED);
			va.registerToGroup(importAction, 	ViewActions.GROUP_ENABLE_EDITMODE);
			va.registerToGroup(exportAction, 	ViewActions.GROUP_ENABLE_VPATHWAY_LOADED);
			va.registerToGroup(copyAction, 	ViewActions.GROUP_ENABLE_WHEN_SELECTION);
			va.registerToGroup(pasteAction, 	ViewActions.GROUP_ENABLE_VPATHWAY_LOADED);
			va.registerToGroup(pasteAction, 	ViewActions.GROUP_ENABLE_EDITMODE);
			va.registerToGroup(zoomActions, 	ViewActions.GROUP_ENABLE_VPATHWAY_LOADED);
			va.registerToGroup(alignActions, 	ViewActions.GROUP_ENABLE_EDITMODE);
			va.registerToGroup(alignActions, 	ViewActions.GROUP_ENABLE_WHEN_SELECTION);
			va.registerToGroup(stackActions, 	ViewActions.GROUP_ENABLE_EDITMODE);
			va.registerToGroup(stackActions, 	ViewActions.GROUP_ENABLE_WHEN_SELECTION);
			va.registerToGroup(newElementActions, ViewActions.GROUP_ENABLE_EDITMODE);
			va.registerToGroup(newElementActions, ViewActions.GROUP_ENABLE_VPATHWAY_LOADED);
			
			va.resetGroupStates();
		}
	}
	
	public final Action saveAction = new SaveAction();
	public final Action saveAsAction = new SaveAsAction();
	public final Action importAction = new ImportAction();
	public final Action exportAction = new ExportAction();
	
	public final Action copyAction = new CopyAction();
	public final Action pasteAction = new PasteAction();
	
	public final Action undoAction = new UndoAction();

	public final Action[] zoomActions = new Action[] {
			new ZoomAction(VPathway.ZOOM_TO_FIT),
			new ZoomAction(10),
			new ZoomAction(25),
			new ZoomAction(50),
			new ZoomAction(75),
			new ZoomAction(100),
			new ZoomAction(150),
			new ZoomAction(200)
	};
	
	public final Action[] alignActions = new Action[] {
			new AlignAction(AlignType.CENTERX),
			new AlignAction(AlignType.CENTERY),
//			new AlignAction(AlignType.LEFT),
//			new AlignAction(AlignType.RIGHT),
//			new AlignAction(AlignType.TOP),
			new AlignAction(AlignType.WIDTH),
			new AlignAction(AlignType.HEIGHT),
	};
	
	public final Action[] stackActions = new Action[] {
			new StackAction(StackType.CENTERX),
			new StackAction(StackType.CENTERY),
//			new StackAction(StackType.LEFT),
//			new StackAction(StackType.RIGHT),
//			new StackAction(StackType.TOP),
//			new StackAction(StackType.BOTTOM)
	};
		
	public final Action[][] newElementActions = new Action[][] {
			new Action[] { 
					new NewElementAction(new DefaultTemplates.DataNodeTemplate(DataNodeType.GENEPRODUCT)) 	
			},
			new Action[] { 
					new NewElementAction(new DefaultTemplates.DataNodeTemplate(DataNodeType.METABOLITE)) 	
			},
			new Action[] { 
					new NewElementAction(new DefaultTemplates.LabelTemplate())	
			},
			new Action[] { 	
					new NewElementAction(new DefaultTemplates.LineTemplate(
							LineStyle.SOLID, LineType.LINE, LineType.LINE)
					),
					new NewElementAction(new DefaultTemplates.LineTemplate(
							LineStyle.SOLID, LineType.LINE, LineType.ARROW)
					),
					new NewElementAction(new DefaultTemplates.LineTemplate(
							LineStyle.DASHED, LineType.LINE, LineType.LINE)
					),
					new NewElementAction(new DefaultTemplates.LineTemplate(
							LineStyle.SOLID, LineType.LINE, LineType.ARROW)
					),
			},
			new Action[] { 
					new NewElementAction(new DefaultTemplates.ShapeTemplate(ShapeType.RECTANGLE)) 
			},
			new Action[] { 
					new NewElementAction(new DefaultTemplates.ShapeTemplate(ShapeType.OVAL)) 
			},
			new Action[] { 
					new NewElementAction(new DefaultTemplates.ShapeTemplate(ShapeType.ARC)) 
			},
			new Action[] { 
					new NewElementAction(new DefaultTemplates.ShapeTemplate(ShapeType.BRACE)) 
			},
			new Action[] { 
					new NewElementAction(new DefaultTemplates.LineTemplate(
							LineStyle.SOLID, LineType.LINE, LineType.TBAR
					)) 
			},
			new Action[] {
					new NewElementAction(new DefaultTemplates.LineTemplate(
							LineStyle.SOLID, LineType.LINE, LineType.LIGAND_ROUND)
					),
					new NewElementAction(new DefaultTemplates.LineTemplate(
							LineStyle.SOLID, LineType.LINE, LineType.RECEPTOR_ROUND)
					),
					new NewElementAction(new DefaultTemplates.LineTemplate(
							LineStyle.DASHED, LineType.LINE, LineType.LIGAND_SQUARE)
					),
					new NewElementAction(new DefaultTemplates.LineTemplate(
							LineStyle.SOLID, LineType.LINE, LineType.RECEPTOR_SQUARE)
					),
			},
			new Action[] { 
					new NewElementAction(new DefaultTemplates.InteractionTemplate()) },
	};
	
	public final Action[] orderActions = new Action[] {
			new OrderAction(OrderType.TOP),
			//new OrderAction(OrderType.UP), //TODO: implement
			//new OrderAction(OrderType.DOWN),
			new OrderAction(OrderType.BOTTOM)
	};
	
	public CommonActions(Engine e) {
		e.addApplicationEventListener(this);
	}
					
	public static class ZoomAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		Component parent;
		double zoomFactor;
		
		public ZoomAction(double zf) {
			super();
			zoomFactor = zf;
			String descr = "Set zoom to " + (int)zf + "%";
			putValue(Action.NAME, toString());
			putValue(Action.SHORT_DESCRIPTION, descr);
			putValue(Action.LONG_DESCRIPTION, descr);
		}
		
		public void actionPerformed(ActionEvent e) {
			VPathway vPathway = Engine.getCurrent().getActiveVPathway();
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
	
	public static class SaveAsAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SaveAsAction() {
			super();
			putValue(Action.NAME, "Save as");
			putValue(Action.SMALL_ICON, new ImageIcon(IMG_SAVEAS));
			putValue(Action.SHORT_DESCRIPTION, "Save a local copy of the pathway");
			putValue(Action.LONG_DESCRIPTION, "Save a local copy of the pathway");
		}

		public void actionPerformed(ActionEvent e) {
			SwingEngine.getCurrent().savePathwayAs();
		}
	}
	
	public static class SaveAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SaveAction() {
			super();
			putValue(Action.NAME, "Save");
			putValue(Action.SMALL_ICON, new ImageIcon(IMG_SAVE));
			putValue(Action.SHORT_DESCRIPTION, "Save a local copy of the pathway");
			putValue(Action.LONG_DESCRIPTION, "Save a local copy of the pathway");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			SwingEngine.getCurrent().savePathway();
		}
	}
	
	public static class ImportAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ImportAction() {
			super();
			putValue(NAME, "Import");
			putValue(SMALL_ICON, new ImageIcon(IMG_IMPORT));
			putValue(Action.SHORT_DESCRIPTION, "Import pathway");
			putValue(Action.LONG_DESCRIPTION, "Import a pathway from various file formats");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		}
		
		public void actionPerformed(ActionEvent e) {
				SwingEngine.getCurrent().importPathway();
		}
	}
	
	public static class ExportAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ExportAction() {
			super();
			putValue(NAME, "Export");
			putValue(SMALL_ICON, new ImageIcon(IMG_EXPORT));
			putValue(Action.SHORT_DESCRIPTION, "Export pathway");
			putValue(Action.LONG_DESCRIPTION, "Export the pathway to various file formats");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		}
		
		public void actionPerformed(ActionEvent e) {
			SwingEngine.getCurrent().exportPathway();
		}
		
		public void setEnabled(boolean newValue) {
			super.setEnabled(newValue);
		}
	}
			
	public static class NewElementAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		Template template;
		
		public NewElementAction(Template template) {
			this.template = template;
			putValue(Action.SHORT_DESCRIPTION, template.getDescription());
			putValue(Action.LONG_DESCRIPTION, template.getDescription());
			if(template.getIconLocation() != null) {
				putValue(Action.SMALL_ICON, new ImageIcon(template.getIconLocation()));
			}
		}
			
		public void actionPerformed(ActionEvent e) {
			VPathway vp = Engine.getCurrent().getActiveVPathway();
			if(vp != null) {
				vp.setNewTemplate(template);
			}
		}
	}
	
	public static class StackAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		StackType type;
		
		public StackAction(StackType t) {
			super();
			putValue(NAME, t.getLabel());
			putValue(SMALL_ICON, new ImageIcon(Engine.getCurrent().getResourceURL(t.getIcon())));
			putValue(SHORT_DESCRIPTION, t.getDescription());
			type = t;
		}
		
		public void actionPerformed(ActionEvent e) {
			VPathway vp = Engine.getCurrent().getActiveVPathway();
			if(vp != null) vp.stackSelected(type);
		}
	}
	
	public static class AlignAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		AlignType type;

		public AlignAction(AlignType t) {
			super();
			putValue(NAME, t.getLabel());
			putValue(SMALL_ICON, new ImageIcon(Engine.getCurrent().getResourceURL(t.getIcon())));
			putValue(SHORT_DESCRIPTION, t.getDescription());
			type = t;
		}

		public void actionPerformed(ActionEvent e) {
			VPathway vp = Engine.getCurrent().getActiveVPathway();
			if(vp != null) vp.alignSelected(type);
		}
	}
	
	public static class OrderAction extends AbstractAction {
		OrderType type;
		
		public OrderAction(OrderType type) {
			this.type = type;
			putValue(NAME, type.getName());
			putValue(SHORT_DESCRIPTION, type.getDescription());
		}
		
		public void actionPerformed(ActionEvent e) {
			VPathway vp = Engine.getCurrent().getActiveVPathway();
			if(vp != null) {
				List<Graphics> selection = vp.getSelectedGraphics();
				for(Graphics g : selection) {
					g.getPathwayElement().setZOrder(type);
				}
			}
		}
	}
	
	private static abstract class PathwayElementDialogAction extends AbstractAction {
		VPathwayElement element;
		Component parent;
		
		public PathwayElementDialogAction(Component parent, VPathwayElement e) {
			super();
			this.parent = parent;
			element = e;
			//If the element is an empty selectionbox,
			//the an empty space on the drawing is clicked
			//Set element to mappinfo so the pathway properties
			//will show up
			if(element instanceof SelectionBox) {
				SelectionBox s = (SelectionBox)element;
				if(s.getSelection().size() == 0) {
					element = element.getDrawing().getMappInfo();
				}
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			if(element instanceof Graphics) {
				PathwayElement p = ((Graphics)element).getPathwayElement();
				PathwayElementDialog pd = PathwayElementDialog.getInstance(
						p, !element.getDrawing().isEditMode(), null, parent);
				if(pd != null) {
					pd.selectPathwayElementPanel(getSelectedPanel());
					pd.setVisible(true);
				}
			}
		}
				
		protected abstract String getSelectedPanel();
	}
	
	public static class AddLiteratureAction extends PathwayElementDialogAction {
		private static final long serialVersionUID = 1L;
		public AddLiteratureAction(Component parent, VPathwayElement e) {
			super(parent, e);
			putValue(Action.NAME, "Add literature reference");
			putValue(Action.SHORT_DESCRIPTION, "Add a literature reference to this element");
			setEnabled(e.getDrawing().isEditMode());
		}
		
		public void actionPerformed(ActionEvent e) {
			if(element instanceof Graphics) {
				PathwayElement pwElm = ((Graphics)element).getPathwayElement();
				BiopaxElementManager em = new BiopaxElementManager(pwElm.getParent());
				BiopaxReferenceManager m = new BiopaxReferenceManager(em, pwElm);
				PublicationXRef xref = new PublicationXRef();
				
				PublicationXRefDialog d = new PublicationXRefDialog(xref, null, parent);
				d.setVisible(true);
				if(d.getExitCode().equals(PublicationXRefDialog.OK)) {
					m.addElementReference(xref);		
				}
			}
		}
		
		protected String getSelectedPanel() {
			return null;
		}
	}
	
	public static class EditLiteratureAction extends PathwayElementDialogAction {
		private static final long serialVersionUID = 1L;

		public EditLiteratureAction(Component parent, VPathwayElement e) {
			super(parent, e);
			putValue(Action.NAME, "Edit literature references");
			putValue(Action.SHORT_DESCRIPTION, "Edit the literature references of this element");
			setEnabled(e.getDrawing().isEditMode());
		}
		
		protected String getSelectedPanel() {
			return PathwayElementDialog.TAB_LITERATURE;
		}
	}
	
	public static class PropertiesAction extends PathwayElementDialogAction {
		private static final long serialVersionUID = 1L;

		public PropertiesAction(Component parent, VPathwayElement e) {
			super(parent, e);
			putValue(Action.NAME, "Properties");
			putValue(Action.SHORT_DESCRIPTION, "View this element's properties");
		}
		
		protected String getSelectedPanel() {
			return PathwayElementDialog.TAB_COMMENTS;
		}
	}
}
