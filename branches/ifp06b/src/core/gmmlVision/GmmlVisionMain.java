package gmmlVision;

import java.io.File;
import java.io.PrintStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import preferences.GmmlPreferences;
import visualization.VisualizationManager;
import visualization.plugins.PluginManager;
import data.GmmlGdb;
import data.GmmlGex;

/**
 * This class contains the main method and is responsible for initiating 
 * the application by setting up the user interface and creating all necessary objects
 */
public class GmmlVisionMain {
	
	/**
	 * Main method which will be carried out when running the program
	 */
	public static void main(String[] args)
	{
		boolean debugHandles = false;
		for(String a : args) {
			if(		a.equalsIgnoreCase("--MonitorHandles") ||
					a.equalsIgnoreCase("-mh")) {
				debugHandles = true;
			}
			else if(a.equalsIgnoreCase("--UseR") ||
					a.equalsIgnoreCase("-ur")) {
				GmmlVision.USE_R = true;
			}
		}
		
		//Setup the application window
		GmmlVisionWindow window = null;
		if(debugHandles)	window = GmmlVision.getSleakWindow();
		else				window = GmmlVision.getWindow();
		
		initiate();
		
		window.setBlockOnOpen(true);
		window.setPWF("C:\\Documents and Settings\\s040778\\Pathvisio-Data\\pathways\\MAPPs\\Hs_Apoptosis_plus.gpml");
                window.setDbName("C:\\Documents And Settings\\s040778\\Pathvisio-Data\\gene databases\\Rn_39_34i.pgdb");
                window.open();
                
               
		//Perform exit operations
		//TODO: implement PropertyChangeListener and fire exit property when closing
		// make classes themself responsible for closing when exit property is changed
		GmmlGex.close();
		GmmlGdb.close();
		//Close log stream
		GmmlVision.log.getStream().close();
		
		Display.getCurrent().dispose();
	}
	
	/**
	 * Initiates some objects used by the program
	 */
	public static void initiate() {
		//initiate logger
		try { 
			GmmlVision.log.setStream(new PrintStream(
					GmmlVision.getPreferences().getString(GmmlPreferences.PREF_FILES_LOG))); 
		} catch(Exception e) {}
		GmmlVision.log.setLogLevel(true, true, true, true, true, true);//Modify this to adjust log level
		
		//initiate Gene database (to load previously used gdb)
		GmmlGdb.init();
		
		//load visualizations and plugins
		loadVisualizations();
		
		//create data directories if they don't exist yet
		createDataDirectories();
		
		//register listeners for static classes
		registerListeners();
				
		//NOTE: ImageRegistry will be initiated in "createContents" of GmmlVisionWindow,
		//since the window has to be opened first (need an active Display)
	}
	
	/**
	 * Creates data directories stored in preferences (if not exist)
	 */
	static void createDataDirectories() {
		String[] dirPrefs = new String[] {
				GmmlPreferences.PREF_DIR_EXPR,
				GmmlPreferences.PREF_DIR_GDB,
				GmmlPreferences.PREF_DIR_PWFILES,
				GmmlPreferences.PREF_DIR_RDATA,
		};
		for(String pref : dirPrefs) {
			File dir = new File(GmmlVision.getPreferences().getString(pref));
			if(!dir.exists()) dir.mkdir();
		}
	}
	
			
	static void registerListeners() {
		VisualizationManager vmgr = new VisualizationManager();
		GmmlGex gex = new GmmlGex();
		
		GmmlVision.addApplicationEventListener(vmgr);
		GmmlVision.addApplicationEventListener(gex);
	}
	
	static void loadVisualizations() {
		//load visualization plugins
		try {
			PluginManager.loadPlugins();
		} catch (Throwable e) {
			GmmlVision.log.error("When loading visualization plugins", e);
		}
		
		VisualizationManager.loadGeneric();
	}
	
	/**
	 * Loads images used throughout the applications into an {@link ImageRegistry}
	 */
	static void loadImages(Display display)
	{
		ClassLoader cl = GmmlVisionMain.class.getClassLoader();
	
		ImageRegistry imageRegistry = new ImageRegistry(display);
		
		// Labels for color by expressiondata (mRNA and Protein)
		ImageData img = new ImageData(cl.getResourceAsStream("images/mRNA.bmp"));
		img.transparentPixel = img.palette.getPixel(GmmlVision.TRANSPARENT_COLOR);
		imageRegistry.put("data.mRNA",
				new Image(display, img));
		img = new ImageData(cl.getResourceAsStream("images/protein.bmp"));
		img.transparentPixel = img.palette.getPixel(GmmlVision.TRANSPARENT_COLOR);
		imageRegistry.put("data.protein",
				new Image(display, img));
		imageRegistry.put("sidepanel.minimize",
				ImageDescriptor.createFromURL(cl.getResource("icons/minimize.gif")));
		imageRegistry.put("sidepanel.hide",
				ImageDescriptor.createFromURL(cl.getResource("icons/close.gif")));
		imageRegistry.put("shell.icon", 
				ImageDescriptor.createFromURL(cl.getResource("images/bigcateye.gif")));
		imageRegistry.put("about.logo",
				ImageDescriptor.createFromURL(cl.getResource("images/logo.jpg")));
						imageRegistry.put("checkbox.unchecked",
				ImageDescriptor.createFromURL(cl.getResource("icons/unchecked.gif")));
		imageRegistry.put("checkbox.unavailable",
				ImageDescriptor.createFromURL(cl.getResource("icons/unchecked_unavailable.gif")));
		imageRegistry.put("checkbox.checked",
				ImageDescriptor.createFromURL(cl.getResource("icons/checked.gif")));
		GmmlVision.setImageRegistry(imageRegistry);
	}
	
}

