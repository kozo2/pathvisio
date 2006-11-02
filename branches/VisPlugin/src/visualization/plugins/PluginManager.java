package visualization.plugins;

import gmmlVision.GmmlVision;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.jdom.Element;

import visualization.Visualization;

public abstract class PluginManager {
	static final String NAME_METHOD = "getName";
	static final String PLUGIN_PKG = "visualization.plugins";
	
	static final Set<Class> plugins = new LinkedHashSet<Class>();
	
	public static VisualizationPlugin getInstance(Class pluginClass, Visualization v) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor c = pluginClass.getConstructor(new Class[] { Visualization.class });
		return (VisualizationPlugin)c.newInstance(new Object[] { v });
	}
		
	public static VisualizationPlugin instanceFromXML(Element xml, Visualization v) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		String className = xml.getAttributeValue(VisualizationPlugin.XML_ATTR_CLASS);
		
		if(className == null) throw new IllegalArgumentException(
				"Element has no '" + VisualizationPlugin.XML_ATTR_CLASS + "' attribute");
		
		Class pluginClass = Class.forName(className);
		VisualizationPlugin p = getInstance(pluginClass, v);
		p.loadXML(xml);
		return p;
	}
	
	public static Class[] getPlugins() {
		return plugins.toArray(new Class[plugins.size()]);
	}
	
	public static String[] getPluginNames() {
		String[] names = new String[plugins.size()];
		int i = 0;
		for(Class p : plugins) {
			names[i++] = getPluginName(p);
		}
		return names;
	}
	
	public static String getPluginName(Class pluginClass) {
		try {
			VisualizationPlugin p = getInstance(pluginClass, null);
			return p.getName();
		} catch(Exception e) {
			GmmlVision.log.error("Unable to get plugin name for " + pluginClass, e);
			return pluginClass.getName();
		}
	}
	
	public static void loadPlugins() throws IOException, ClassNotFoundException {	
		GmmlVision.log.trace("> Loading visualization plugins");
		Enumeration<URL> resources = 
			ClassLoader.getSystemClassLoader().getResources(PLUGIN_PKG.replace('.', '/'));
        while (resources.hasMoreElements()) {
        	URL url = resources.nextElement();
        	GmmlVision.log.trace("visualization.plugins package found in: ");
        	if(url.getProtocol().equals("jar")) loadFromJar(url);
        	else loadFromDir(url);
        }
  	}
	    
	static void loadFromDir(URL url) throws UnsupportedEncodingException, ClassNotFoundException {
		GmmlVision.log.trace("\tLoading from directory " + url);
		File directory = new File(URLDecoder.decode(url.getPath(), "UTF-8"));
		if (directory.exists()) {
            String[] files = directory.list(classFilter);
            for (String file : files)
            	addPlugin(Class.forName(PLUGIN_PKG + '.' + file.substring(0, file.length() - 6)));
        }
	}
	
	//TODO: test this
	static void loadFromJar(URL url) throws ClassNotFoundException, IOException {
		GmmlVision.log.trace("\tLoading from jar " + url);
		JarURLConnection conn = (JarURLConnection)url.openConnection();

		JarFile jfile = conn.getJarFile();
		Enumeration e = jfile.entries();
		while (e.hasMoreElements()) {
			ZipEntry entry = (ZipEntry)e.nextElement();
			GmmlVision.log.trace("Checking " + entry);
			String entryname = entry.getName();
			if(entryname.endsWith(".class")) {
				addPlugin(Class.forName(PLUGIN_PKG + '.' + entryname.substring(0, entryname.length() - 6)));
			}
		}
	}
	
	static void addPlugin(Class c) {
		GmmlVision.log.trace("\t\tTrying to add " + c);
		if(isPlugin(c)) {
			GmmlVision.log.trace("\t\t\t!> Adding " + c);
			plugins.add(c);
		}
	}
	
	static boolean isPlugin(Class c) {
		Class superClass = c;
		while((superClass = superClass.getSuperclass()) != null) {
			GmmlVision.log.trace("\t\t>" + c + " with superclass: " + superClass);
			if(superClass.equals(VisualizationPlugin.class)) return true;
		}
			
		return false;
	}
		
	static  FilenameFilter classFilter = new FilenameFilter() {
		public boolean accept(File f, String name) {
			return name.endsWith(".class");
		}
    };
}
