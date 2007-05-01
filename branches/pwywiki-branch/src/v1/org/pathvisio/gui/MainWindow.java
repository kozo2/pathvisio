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
package org.pathvisio.gui;

import java.net.URL;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.pathvisio.Globals;
import org.pathvisio.data.DBConnector;
import org.pathvisio.data.Gdb;
import org.pathvisio.data.Gex;
import org.pathvisio.data.Gex.ExpressionDataEvent;
import org.pathvisio.data.Gex.ExpressionDataListener;
import org.pathvisio.gui.Engine.ApplicationEvent;
import org.pathvisio.gui.Engine.ApplicationEventListener;
import org.pathvisio.preferences.Preferences;
import org.pathvisio.search.PathwaySearchComposite;
import org.pathvisio.view.GeneProduct;
import org.pathvisio.view.VPathway;
import org.pathvisio.visualization.LegendPanel;
import org.pathvisio.visualization.VisualizationManager;

/**
 * This class is the main class in the GPML project. 
 * It acts as a container for pathwaydrawings and facilitates
 * loading, creating and saving drawings to and from GPML.
 */
public class MainWindow extends ApplicationWindow implements 
						ApplicationEventListener, ExpressionDataListener
{
	private static final long serialVersionUID = 1L;
	public static int ZOOM_TO_FIT = -1;
	
	private CommonActions.UndoAction undoAction = new CommonActions.UndoAction(this);	
	private CommonActions.NewAction newAction = new CommonActions.NewAction (this);
	private CommonActions.SvgExportAction svgExportAction = new CommonActions.SvgExportAction (this);
	private CommonActions.OpenAction openAction = new CommonActions.OpenAction (this);	
	private CommonActions.ImportAction importAction = new CommonActions.ImportAction (this);	
	private CommonActions.SaveAction saveAction = new CommonActions.SaveAction(this);	
	private CommonActions.SaveAsAction saveAsAction = new CommonActions.SaveAsAction (this);
	private CommonActions.ExportAction exportAction = new CommonActions.ExportAction (this);
	private CommonActions.CloseAction closeAction = new CommonActions.CloseAction(this);	
	private CommonActions.ExitAction exitAction = new CommonActions.ExitAction(this);
	private CommonActions.PreferencesAction preferencesAction = new CommonActions.PreferencesAction(this);
	private CommonActions.AboutAction aboutAction = new CommonActions.AboutAction(this);
	private CommonActions.CopyAction copyAction = new CommonActions.CopyAction(this);
	private CommonActions.HelpAction helpAction = new CommonActions.HelpAction(this);	
	private CommonActions.PasteAction pasteAction = new CommonActions.PasteAction(this);
	
	/**
	 * {@link Action} to select a Gene Database
	 */
	private class SelectGdbAction extends Action
	{
		MainWindow window;
		public SelectGdbAction(MainWindow w)
		{
			window = w;
			setText("Select &Gene Database");
			setToolTipText("Select Gene Database");
		}
		
		public void run () {			
			try {
				DBConnector dbcon = Gdb.getDBConnector();
				String dbName = dbcon.openChooseDbDialog(getShell());
				
				if(dbName == null) return;
				
				Gdb.connect(dbName);
				setStatus("Using Gene Database: '" + Engine.getPreferences().getString(Preferences.PREF_CURR_GDB) + "'");
				cacheExpressionData();
			} catch(Exception e) {
				String msg = "Failed to open Gene Database; " + e.getMessage();
				MessageDialog.openError (window.getShell(), "Error", 
						"Error: " + msg + "\n\n" + 
						"See the error log for details.");
				Engine.log.error(msg, e);
			}
		}
	}
	private SelectGdbAction selectGdbAction = new SelectGdbAction(this);
	
	/**
	 * Loads expression data for all {@link GeneProduct}s in the loaded pathway
	 */
	private void cacheExpressionData()
	{
		if(Engine.isDrawingOpen())
		{
			VPathway drawing = Engine.getDrawing();
			//Check for neccesary connections
			if(Gex.isConnected() && Gdb.isConnected())
			{
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
				try {
					dialog.run(true, true, Gex.createCacheRunnable(drawing.getMappIds(), drawing.getSystemCodes()));
					drawing.redraw();
				} catch(Exception e) {
					String msg = "while caching expression data: " + e.getMessage();					
					MessageDialog.openError (getShell(), "Error", 
							"Error: " + msg + "\n\n" + 
							"See the error log for details.");
					Engine.log.error(msg, e);
				}
			}
		}
	}	
					
	/**
	 * {@link Action} to switch between edit and view mode
	 */
	private class SwitchEditModeAction extends Action implements ApplicationEventListener
	{
		final String ttChecked = "Exit edit mode";
		final String ttUnChecked = "Switch to edit mode to edit the pathway content";
		MainWindow window;
		public SwitchEditModeAction (MainWindow w)
		{
			super("&Edit mode", IAction.AS_CHECK_BOX);
			setImageDescriptor(ImageDescriptor.createFromURL(Engine.getResourceURL("icons/edit.gif")));
			setToolTipText(ttUnChecked);
			window = w;
			
			Engine.addApplicationEventListener(this);
		}
		
				public void run () {
			if(Engine.isDrawingOpen())
			{
				VPathway drawing = Engine.getDrawing();
				//Switch to edit mode: show edit toolbar, show property table in sidebar
				drawing.setEditMode(isChecked());
				showEditActionsCI(isChecked());
				rightPanel.getTabFolder().setSelection(isChecked() ? 1 : 0);
				switchEditModeAction.setChecked(isChecked());
				getCoolBarManager().update(true);
			} else {//No gpml pathway loaded, deselect action and do nothing
				setChecked(false);
			}
		}
		
		public void setChecked(boolean check) {
			super.setChecked(check);
			setToolTipText(check ? ttChecked : ttUnChecked);
		}
		
		public void switchEditMode(boolean edit) {
			setChecked(edit);
			run();
			
		}

		public void applicationEvent(ApplicationEvent e) {
			if(e.type == ApplicationEvent.OPEN_PATHWAY) {
				Engine.getDrawing().setEditMode(isChecked());
			}
			else if(e.type == ApplicationEvent.NEW_PATHWAY) {
				switchEditMode(true);
			}
		}
	}
	private SwitchEditModeAction switchEditModeAction = new SwitchEditModeAction(this);
		
	/**
	 * Switch edit mode on or off. In edit mode the pathway drawing can be edited
	 * @param edit if true, edit mode is switched on, if false edit mode is switched off
	 */
	public void switchEditMode(boolean edit) {
		switchEditModeAction.setChecked(edit);
		switchEditModeAction.run();
	}
	
	/**
	 * {@link Action} to show or hide the right sidepanel
	 */
	public class ShowRightPanelAction extends Action
	{
		MainWindow window;
		public ShowRightPanelAction (MainWindow w)
		{
			super("Show &information panel", IAction.AS_CHECK_BOX);
			window = w;
			setChecked(true);
		}
		
		public void run() {
			if(isChecked()) rightPanel.show();
			else rightPanel.hide();
		}
	}
	public ShowRightPanelAction showRightPanelAction = new ShowRightPanelAction(this);
	
	/**
	 * {@link Action} to add a new element to the gpml pathway
	 */
	private class NewElementAction extends Action
	{
		MainWindow window;
		int element;
		
		/**
		 * Constructor for this class
		 * @param e	type of element this action adds; a {@link VPathway} field constant
		 */
		public NewElementAction (int e)
		{
			element = e;
		
			String toolTipText;
			URL imageURL = null;
			toolTipText = null;
			switch(element) {
			case VPathway.NEWLINE: 
				toolTipText = "Draw new line";
				imageURL = Engine.getResourceURL("icons/newline.gif");
				setChecked(false);
				break;
			case VPathway.NEWLINEARROW:
				toolTipText = "Draw new arrow";
				imageURL = Engine.getResourceURL("icons/newarrow.gif");
				setChecked(false);
				break;
			case VPathway.NEWLINEDASHED:
				toolTipText = "Draw new dashed line";
				imageURL = Engine.getResourceURL("icons/newdashedline.gif");
				setChecked(false);
				break;
			case VPathway.NEWLINEDASHEDARROW:
				toolTipText = "Draw new dashed arrow";
				imageURL = Engine.getResourceURL("icons/newdashedarrow.gif");
				setChecked(false);
				break;
			case VPathway.NEWLABEL:
				toolTipText = "Draw new label";
				imageURL = Engine.getResourceURL("icons/newlabel.gif");
				setChecked(false);
				break;
			case VPathway.NEWARC:
				toolTipText = "Draw new arc";
				imageURL = Engine.getResourceURL("icons/newarc.gif");
				setChecked(false);
				break;
			case VPathway.NEWBRACE:
				toolTipText = "Draw new brace";
				imageURL = Engine.getResourceURL("icons/newbrace.gif");
				setChecked(false);
				break;
			case VPathway.NEWGENEPRODUCT:
				toolTipText = "Draw new geneproduct";
				imageURL = Engine.getResourceURL("icons/newgeneproduct.gif");
				setChecked(false);
				break;
			case VPathway.NEWRECTANGLE:
				imageURL = Engine.getResourceURL("icons/newrectangle.gif");
				setChecked(false);
				break;
			case VPathway.NEWOVAL:
				toolTipText = "Draw new oval";
				imageURL = Engine.getResourceURL("icons/newoval.gif");
				setChecked(false);
				break;
			case VPathway.NEWTBAR:
				toolTipText = "Draw new TBar";
				imageURL = Engine.getResourceURL("icons/newtbar.gif");
				setChecked(false);
				break;
			case VPathway.NEWRECEPTORROUND:
				toolTipText = "Draw new round receptor";
				imageURL = Engine.getResourceURL("icons/newreceptorround.gif");
				setChecked(false);
				break;
			case VPathway.NEWRECEPTORSQUARE:
				toolTipText = "Draw new square receptor";
				imageURL = Engine.getResourceURL("icons/newreceptorsquare.gif");
				setChecked(false);
				break;
			case VPathway.NEWLIGANDROUND:
				toolTipText = "Draw new round ligand";
				imageURL = Engine.getResourceURL("icons/newligandround.gif");
				setChecked(false);
				break;
			case VPathway.NEWLIGANDSQUARE:
				toolTipText = "Draw new square ligand";
				imageURL = Engine.getResourceURL("icons/newligandsquare.gif");
				setChecked(false);
				break;
			case VPathway.NEWLINEMENU:
				setMenuCreator(new NewItemMenuCreator(VPathway.NEWLINEMENU));
				imageURL = Engine.getResourceURL("icons/newlinemenu.gif");
				toolTipText = "Draw new line or arrow";
				break;
			case VPathway.NEWLINESHAPEMENU:
				setMenuCreator(new NewItemMenuCreator(VPathway.NEWLINESHAPEMENU));
				imageURL = Engine.getResourceURL("icons/newlineshapemenu.gif");
				toolTipText = "Draw new ligand or receptor";
				break;
			}
			setToolTipText(toolTipText);
			setId("newItemAction");
			if(imageURL != null) setImageDescriptor(ImageDescriptor.createFromURL(imageURL));
		}
				
		public void run () {
			if(isChecked())
			{
				deselectNewItemActions();
				setChecked(true);
				Engine.getDrawing().setNewGraphics(element);
			}
			else
			{	
				Engine.getDrawing().setNewGraphics(VPathway.NEWNONE);
			}
		}
		
	}
	
	/**
	 * {@link IMenuCreator} that creates the drop down menus for 
	 * adding new line-type and -shape elements
	 */
	private class NewItemMenuCreator implements IMenuCreator {
		private Menu menu;
		int element;
		
		/**
		 * Constructor for this class
		 * @param e	type of menu to create; one of {@link VPathway}.NEWLINEMENU
		 * , {@link VPathway}.NEWLINESHAPEMENU
		 */
		public NewItemMenuCreator(int e) 
		{
			element = e;
		}
		
		public Menu getMenu(Menu parent) {
			return null;
		}

		public Menu getMenu(Control parent) {
			if (menu != null)
				menu.dispose();
			
			menu = new Menu(parent);
			Vector<Action> actions = new Vector<Action>();
			switch(element) {
			case VPathway.NEWLINEMENU:
				actions.add(new NewElementAction(VPathway.NEWLINE));
				actions.add(new NewElementAction(VPathway.NEWLINEARROW));
				actions.add(new NewElementAction(VPathway.NEWLINEDASHED));
				actions.add(new NewElementAction(VPathway.NEWLINEDASHEDARROW));
				break;
			case VPathway.NEWLINESHAPEMENU:
				actions.add(new NewElementAction(VPathway.NEWLIGANDROUND));
				actions.add(new NewElementAction(VPathway.NEWRECEPTORROUND));
				actions.add(new NewElementAction(VPathway.NEWLIGANDSQUARE));
				actions.add(new NewElementAction(VPathway.NEWRECEPTORSQUARE));
			}
			
			for (Action act : actions)
			{			
				addActionToMenu(menu, act);
			}

			return menu;
		}
		
		protected void addActionToMenu(Menu parent, Action a)
		{
			 ActionContributionItem item= new ActionContributionItem(a);
			 item.fill(parent, -1);
		}
		
		public void dispose() 
		{
			if (menu != null)  {
				menu.dispose();
				menu = null;
			}
		}
	}
	
	/**
	 * Deselects all {@link NewElementAction}s on the toolbar and sets 
	 * {@link VPathway}.newGraphics to {@link VPathway}.NEWNONE
	 */
	public void deselectNewItemActions()
	{
		IContributionItem[] items = editActionsCI.getToolBarManager().getItems();
		for(int i = 0; i < items.length; i++)
		{
			if(items[i] instanceof ActionContributionItem)
			{
				((ActionContributionItem)items[i]).getAction().setChecked(false);
			}
		}
		Engine.getDrawing().setNewGraphics(VPathway.NEWNONE);
	}
	
	// Elements of the coolbar
	ToolBarContributionItem commonActionsCI;
	ToolBarContributionItem editActionsCI;
	ToolBarContributionItem viewActionsCI;
	protected CoolBarManager createCoolBarManager(int style)
	{
		createCommonActionsCI();
		createEditActionsCI();
		createViewActionsCI();
		
		CoolBarManager coolBarManager = new CoolBarManager(style);
		coolBarManager.setLockLayout(true);
		
		coolBarManager.add(commonActionsCI);
		coolBarManager.add(viewActionsCI);
		return coolBarManager;
	}
	
	/**
	 * Creates element of the coolbar containing common actions as new, save etc.
	 */
	protected void createCommonActionsCI()
	{
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		toolBarManager.add(newAction);
		toolBarManager.add(openAction);
		toolBarManager.add(saveAction);
		commonActionsCI = new ToolBarContributionItem(toolBarManager, "CommonActions");
	}
	
	/**
	 * Creates element of the coolbar only shown in edit mode (new element actions)
	 */
	protected void createEditActionsCI()
	{
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);		
		toolBarManager.add(new NewElementAction(VPathway.NEWGENEPRODUCT));
		toolBarManager.add(new NewElementAction(VPathway.NEWLABEL));
		toolBarManager.add(new NewElementAction(VPathway.NEWLINEMENU));
		toolBarManager.add(new NewElementAction(VPathway.NEWRECTANGLE));
		toolBarManager.add(new NewElementAction(VPathway.NEWOVAL));
		toolBarManager.add(new NewElementAction(VPathway.NEWARC));
		toolBarManager.add(new NewElementAction(VPathway.NEWBRACE));
		toolBarManager.add(new NewElementAction(VPathway.NEWTBAR));
		toolBarManager.add(new NewElementAction(VPathway.NEWLINESHAPEMENU));
		
		editActionsCI = new ToolBarContributionItem(toolBarManager, "EditModeActions");
	}
	
	/**
	 * Creates element of the coolbar containing controls related to viewing a VPathway
	 */
	protected void createViewActionsCI()
	{
		final MainWindow window = this;
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		//Add zoomCombo
		toolBarManager.add(new ControlContribution("ZoomCombo") {
			protected Control createControl(Composite parent) {
				final Combo zoomCombo = new Combo(parent, SWT.DROP_DOWN);
				zoomCombo.setItems(new String[] { "200%", "100%", "75%", "50%", "Zoom to fit" });
				zoomCombo.setText("100%");
				zoomCombo.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						int pctZoom = 100;
						String zoomText = zoomCombo.getText().replace("%", "");
						try {
							pctZoom = Integer.parseInt(zoomText);
						} catch (Exception ex) { 
							if(zoomText.equals("Zoom to fit"))
									{ pctZoom = ZOOM_TO_FIT; } else { return; }
						}
						new CommonActions.ZoomAction(window, pctZoom).run();
					}
					public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
				});
				return zoomCombo;
			}
		});
		//Add swich to editmode
		toolBarManager.add(switchEditModeAction);
		
		viewActionsCI =  new ToolBarContributionItem(toolBarManager, "SwitchActions");
	}
	
	/**
	 * Shows or hides the editActionsCI
	 * @param show	true/false for either show or hide
	 */
	private void showEditActionsCI(boolean show)
	{
		if(show) {
			getCoolBarManager().insertAfter(viewActionsCI.getId(), editActionsCI);
		}
		else {
			getCoolBarManager().remove(editActionsCI);
		}
//		showVisualizationCI(!show); //Visualizations can show up in edit mode...
		getCoolBarManager().update(true);
	}
		
	protected StatusLineManager createStatusLineManager() {
		return super.createStatusLineManager();
	}

	/**
	 *Builds and ads a menu to the frame
	 */
	protected MenuManager createMenuManager()
	{
		MenuManager m = new MenuManager();
		MenuManager fileMenu = new MenuManager ("&File");
		fileMenu.add(newAction);
		fileMenu.add(openAction);
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);
		//fileMenu.add(closeAction);
		fileMenu.add(new Separator());
		fileMenu.add(importAction);
		fileMenu.add(exportAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);
		MenuManager editMenu = new MenuManager ("&Edit");
		editMenu.add(copyAction);
		editMenu.add(pasteAction);
		editMenu.add(new Separator());
		editMenu.add(switchEditModeAction);
		editMenu.add(preferencesAction);
		MenuManager viewMenu = new MenuManager ("&View");
		viewMenu.add(showRightPanelAction);
		MenuManager zoomMenu = new MenuManager("&Zoom");
		zoomMenu.add(new CommonActions.ZoomAction(this, 50));
		zoomMenu.add(new CommonActions.ZoomAction(this, 75));
		zoomMenu.add(new CommonActions.ZoomAction(this, 100));
		zoomMenu.add(new CommonActions.ZoomAction(this, 125));
		zoomMenu.add(new CommonActions.ZoomAction(this, 150));
		zoomMenu.add(new CommonActions.ZoomAction(this, 200));
		zoomMenu.add(new CommonActions.ZoomAction(this, ZOOM_TO_FIT));
		viewMenu.add(zoomMenu);
		MenuManager dataMenu = new MenuManager ("&Data");
		dataMenu.add(selectGdbAction);
		
		MenuManager helpMenu = new MenuManager ("&Help");
		helpMenu.add(aboutAction);
		helpMenu.add(helpAction);
		m.add(fileMenu);
		m.add(editMenu);
		m.add(viewMenu);
		m.add(dataMenu);
		m.add(helpMenu);
		return m;
	}
	
	public MainWindow()
	{
		this(null);
	}
	
	/**
	 *Constructor for the MainWindow class
	 *Initializes new MainWindow and sets properties for frame
	 */
	public MainWindow(Shell shell)
	{
		super(shell);
		
		addMenuBar();
		addStatusLine();
		addCoolBar(SWT.FLAT | SWT.LEFT);
		
		Engine.addApplicationEventListener(this);
		Gex.addListener(this);
	}
	
	public boolean close() {
		Engine.fireApplicationEvent(
				new ApplicationEvent(this, ApplicationEvent.CLOSE_APPLICATION));
		return super.close();
	}
	
	public ScrolledComposite sc;
	public BackpagePanel bpBrowser; //Browser for showing backpage information
	public PropertyPanel propertyTable;	//Table showing properties of Graphics objects
	SashForm sashForm; //SashForm containing the drawing area and sidebar
	TabbedSidePanel rightPanel; //side panel containing backbage browser and property editor
	PathwaySearchComposite pwSearchComposite; //Composite that handles pathway searches and displays results
	LegendPanel legend; //Legend to display colorset information
	protected Control createContents(Composite parent)
	{		
		Shell shell = parent.getShell();
		shell.setSize(800, 600);
		shell.setLocation(100, 100);
		
		shell.setText(Globals.APPLICATION_VERSION_NAME);
		
		GuiMain.loadImages(shell.getDisplay());
		
		shell.setImage(Engine.getImageRegistry().get("shell.icon"));
		
		Composite viewComposite = new Composite(parent, SWT.NULL);
		viewComposite.setLayout(new FillLayout());
		
		sashForm = new SashForm(viewComposite, SWT.HORIZONTAL);
		
		sc = new ScrolledComposite (sashForm, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc.setFocus();
		
		rightPanel = new TabbedSidePanel(sashForm, SWT.NULL);
		
		//rightPanel controls
		bpBrowser = new BackpagePanel(rightPanel.getTabFolder(), SWT.NONE);
		propertyTable = new PropertyPanel(
				rightPanel.getTabFolder(), SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		pwSearchComposite = new PathwaySearchComposite(rightPanel.getTabFolder(), SWT.NONE, this);
		legend = new LegendPanel(rightPanel.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		Composite visPanel = VisualizationManager.createSidePanel(rightPanel.getTabFolder());
		
		rightPanel.addTab(bpBrowser, "Backpage");
		rightPanel.addTab(propertyTable, "Properties");
		rightPanel.addTab(pwSearchComposite, "Pathway Search");
		rightPanel.addTab(legend, "Legend");
		rightPanel.addTab(visPanel, "Visualization");
		
		int sidePanelSize = Engine.getPreferences().getInt(Preferences.PREF_SIDEPANEL_SIZE);
		sashForm.setWeights(new int[] {100 - sidePanelSize, sidePanelSize});
		showRightPanelAction.setChecked(sidePanelSize > 0);
		
		rightPanel.getTabFolder().setSelection(0); //select backpage browser tab
		rightPanel.hideTab("Legend"); //hide legend on startup
		
		setStatus("Using Gene Database: '" + Engine.getPreferences().getString(Preferences.PREF_CURR_GDB) + "'");
				
		return parent;
		
	};
	
	public TabbedSidePanel getSidePanel() { return rightPanel; }
	
	public LegendPanel getLegend() { return legend; }
	
	public void showLegend(boolean show) {	
		if(show && Gex.isConnected()) {
			if(rightPanel.isVisible("Legend")) return; //Legend already visible, only refresh
			rightPanel.unhideTab("Legend", 0);
			rightPanel.selectTab("Legend");
		}
		
		else rightPanel.hideTab("Legend");
	}
			
	/**
	 * Creates a new empty drawing canvas
	 * @return the empty {@link VPathway}
	 */
	public VPathway createNewDrawing()
	{		
		return new VPathway(sc, SWT.NO_BACKGROUND);
	}
	
	public void applicationEvent(ApplicationEvent e) {
		VPathway drawing = null;
		switch(e.type) {
		case ApplicationEvent.NEW_PATHWAY:
			drawing = Engine.getDrawing();
			sc.setContent(drawing);
			break;
		case ApplicationEvent.OPEN_PATHWAY:
			drawing = Engine.getDrawing();
			sc.setContent(drawing);
			if(Gex.isConnected()) cacheExpressionData();
			break;	
		}
	}

	public void expressionDataEvent(ExpressionDataEvent e) {
		switch(e.type) {
		case ExpressionDataEvent.CONNECTION_CLOSED:
			showLegend(false);
			break;
		case ExpressionDataEvent.CONNECTION_OPENED:
			cacheExpressionData();
			showLegend(true);
			break;
		}
	}
} // end of class
