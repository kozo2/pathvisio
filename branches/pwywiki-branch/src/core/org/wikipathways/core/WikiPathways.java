package org.wikipathways.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcHttpClientConfig;
import org.apache.xmlrpc.client.XmlRpcHttpTransport;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;
import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig;
import org.apache.xmlrpc.util.HttpUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pathvisio.data.Gdb;
import org.pathvisio.gui.Engine;
import org.pathvisio.gui.GuiMain;
import org.pathvisio.gui.MainWindow;
import org.pathvisio.gui.Engine.ApplicationEvent;
import org.pathvisio.gui.Engine.ApplicationEventListener;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.util.SwtUtils.SimpleRunnableWithProgress;
import org.xml.sax.SAXException;

public class WikiPathways implements ApplicationEventListener {
	public static String SITE_NAME = "WikiPathways.org";
	HashMap<String, String> cookie;
	String rpcURL;
	String pwName;
	String pwSpecies;
	String pwURL;
	String user;
	boolean isNew;
	
	File localFile;
	
	public WikiPathways() {
		cookie = new HashMap<String, String>();
		Engine.addApplicationEventListener(this);
	}

	public static void main(String[] args)
	{			
		Display d = new Display();
		ProgressMonitorDialog pdstart = new ProgressMonitorDialog(new Shell(d, SWT.ON_TOP));
		try {
			pdstart.run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor m) throws InvocationTargetException, InterruptedException {
					m.beginTask("Starting PathVisio", IProgressMonitor.UNKNOWN);
					GuiMain.initiate();				
					m.done();
				}
			});
		} catch (Exception e) {
			e.printStackTrace(); //Log not started yet
		}
		d.dispose();
				
		//Parse comman-line arguments
		final WikiPathways wiki = new WikiPathways();
		for(int i = 0; i < args.length - 1; i++) {
			//Check for parameters
			String a = args[i];
			if(a.startsWith("-")) {
				if		(a.equalsIgnoreCase("-c")) {
					String c = args[i+1];
					int sep = c.indexOf('=');
					if(sep <= 0 || sep >= c.length() - 1) {
						error("Error in input parameters", new Exception("Error in -c parameter"));
						System.exit(-1);
					} else {
						Engine.log.trace("Parsed -c argument" + c);
						wiki.setCookie(c.substring(0, sep), c.substring(sep + 1));
					}
				}
				else if	(a.equalsIgnoreCase("-pwName")) {
					Engine.log.trace("Parsed -pwName argument" + args[i+1]);
					wiki.setPathwayName(args[i+1]);
				}
				else if	(a.equalsIgnoreCase("-pwUrl")) {
					Engine.log.trace("Parsed -pwUrl argument" + args[i+1]);
					wiki.setPathwayURL(args[i+1]);
				}
				else if	(a.equalsIgnoreCase("-rpcUrl")) {
					Engine.log.trace("Parsed -rpcUrl argument" + args[i+1]);
					wiki.setRpcURL(args[i+1]);
				}
				else if (a.equalsIgnoreCase("-pwSpecies")) {
					Engine.log.trace("Parsed -pwSpecies argument" + args[i+1]);
					wiki.setPathwaySpecies(args[i+1]);
				}
				else if (a.equalsIgnoreCase("-user")) {
					Engine.log.trace("Parsed -user argument" + args[i+1]);
					wiki.setUser(args[i+1]);
				}
				else if (a.equalsIgnoreCase("-new")) {
					Engine.log.trace("Parsed -new flag");
					String value = args[i+1];
					if(value.equalsIgnoreCase("true") || value.equals("1")) {
						wiki.setNew(true);
					}
				}
			}
		}
		
		final MainWindow window = Engine.getWindow();
		
		//Start PathVisio in a seperate thread
		Thread thr = new Thread() {
			public void run() {				
				window.setBlockOnOpen(true);
				wiki.addSaveButton(window);
				window.open();
			
				Display.getCurrent().dispose();
				Gdb.close();
			}
		};
		thr.start();
		
		//Wait for PathVisio to startup
		Engine.log.trace("\t> Waiting for window to open");
		while(window.getShell() == null) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				//Swallow it
			}
		}
		
		//Open pathway, or create new one
		if(wiki.isNew()) {//New pathway
			Engine.newPathway();
			window.getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					Pathway data = Engine.getGmmlData();
					PathwayElement info = data.getMappInfo();
					if(wiki.user != null) info.setAuthor(wiki.user);
					info.setMapInfoName(wiki.pwName);
				}
			});
		} else { //Open pathway
			Engine.log.trace("Opening pathway " + wiki.pwURL);
			final Shell wshell = Engine.getWindow().getShell();
			final SimpleRunnableWithProgress sp = new SimpleRunnableWithProgress(
					WikiPathways.class, "openPathwayURL", new Class[] {}, new Object[] {}, wiki);
			SimpleRunnableWithProgress.setMonitorInfo(
					"Downloading patwhay from " + SITE_NAME, IProgressMonitor.UNKNOWN);

			final ProgressMonitorDialog pd = new ProgressMonitorDialog(wshell);
			wshell.getDisplay().syncExec(new Runnable() {
				public void run() {
					try {
						pd.run(true, false, sp);
					} catch (Exception e) {
						error(wshell, "Unable to open pathway", e);
						Engine.exit();
						System.exit(-1);
					}
				}
			});
		}
		//Wait for user to finish editing
		while(window.getShell() != null && !window.getShell().isDisposed()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				break;
			}
		}
		Engine.log.trace("PathVisio window closed");
		
		//ApplicationEventListener will now save the pathway to the wiki
		
		//Close log stream
		Engine.log.getStream().close();
	}


	static void error(final String error, final Throwable e) {
		Display d = Display.getDefault();
		d.syncExec(new Runnable() {
			public void run() {
				Shell shell = new Shell();
				error(shell, error, e);
			}
		});
	}
	
	static void error(final Shell shell, final String error, final Throwable e) {
		Engine.log.error(error, e);
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				Throwable ex = e;
				if(ex instanceof InvocationTargetException) ex = ex.getCause();
				MessageDialog.openError(shell, error, ex != null ? ex.getMessage() : "No message specified");
			}
		});
	}
	
	protected void setCookie(String key, String value) {
		cookie.put(key, value);
	}
	
	public void openPathwayURL() throws MalformedURLException, IOException {
		localFile = Engine.openPathway(new URL(pwURL));
		Engine.switchEditMode(true);
	}
	
	protected void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	public boolean isNew() {
		return isNew;
	}
	
	protected File getLocalFile() { 
		if(localFile == null) {
			try {
				localFile = File.createTempFile("tmp", ".gpml");
			} catch(Exception e) {
				return null;
			}
		}
		return localFile;
	}
	
	protected void setPathwayName(String pathwayName) {
		pwName = pathwayName;
	}
	
	protected void setPathwaySpecies(String pathwaySpecies) {
		pwSpecies = pathwaySpecies;
	}
	
	protected void setPathwayURL(String pathwayURL) {
		pwURL = pathwayURL;
	}
	
	protected void setRpcURL(String rpcURL) {
		this.rpcURL = rpcURL;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	void addSaveButton(MainWindow w) {
		ToolBarContributionItem tc = (ToolBarContributionItem)w.getCoolBarManager().find("CommonActions");
		
		tc.getToolBarManager().add(new ControlContribution("SaveToWiki") {
			protected Control createControl(Composite parent) {
				final Button b = new Button(parent, SWT.PUSH);
				//b.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
				b.setText("Save to " + SITE_NAME);
				b.setToolTipText("Save current pathway as '" + pwName + "' on " + SITE_NAME);
				b.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						saveUI();
					}
				});
				return b;
			}
		});
	}
	
	boolean ovrChanged;
	
	protected void saveToWiki(String description) throws XmlRpcException, IOException, ConverterException {		
		//TODO: check if changed
		if(ovrChanged || Engine.getGmmlData().isChanged()) {
			ovrChanged = true; //In case we get an error, save changes next time
			File gpmlFile = getLocalFile();
			//Save current pathway to local file
			Engine.savePathway(gpmlFile);
			saveToWiki(description, gpmlFile);
			ovrChanged = false; //Save successfull, don't save next time
		} else {
			Engine.log.trace("No changes made, ignoring save");
			//Do nothing, no changes made
		}
	}
	
	protected void saveToWiki(String description, File gpmlFile) throws XmlRpcException, IOException {	
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL(rpcURL));
	
		XmlRpcClient client = new XmlRpcClient();
		XmlRpcCookieTransportFactory ctf = new XmlRpcCookieTransportFactory(client);
	
		XmlRpcCookieHttpTransport ct = (XmlRpcCookieHttpTransport)ctf.getTransport();
		for(String key : cookie.keySet()) {
			Engine.log.trace("Setting cookie: " + key + "=" + cookie.get(key));
			ct.addCookie(key, cookie.get(key));
		}
		
		client.setTransportFactory(ctf);
		client.setConfig(config);
		
		RandomAccessFile raf = new RandomAccessFile(gpmlFile, "r");
		byte[] data = new byte[(int)raf.length()];
		raf.readFully(data);
		byte[] data64 = Base64.encodeBase64(data);
		Object[] params = new Object[]{ pwName, pwSpecies, description, data64 };
				
		client.execute("WikiPathways.updatePathway", params);
	}

	protected void saveUI() {
		Shell shell = Engine.getWindow().getShell();

		String d = "New pathway";
		if(!isNew()) {
			//Dialog for modification description
			InputDialog dialog = new InputDialog(shell, "Save to " + SITE_NAME, "Please specify an edit summary (description of changes)", "", null);
			int status = dialog.open();
			if(status == InputDialog.CANCEL) {
				return;
			}
			d = dialog.getValue();
		}
		final String descr = d;

		//Progressbar for saving pathway
		try {
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor m) throws InvocationTargetException, InterruptedException {
					try {
						m.beginTask("Saving pathway to " + SITE_NAME, IProgressMonitor.UNKNOWN);
						saveToWiki(descr);
						m.done();
					} catch(Exception e) {
						throw new InvocationTargetException(e);
					}
				}
			};
			ProgressMonitorDialog pd2 = new ProgressMonitorDialog(shell);

			Engine.log.trace("Saving to wiki: " + System.currentTimeMillis());
			pd2.run(true, false, op);
			Engine.log.trace("Finished: " + System.currentTimeMillis());

			if(isNew()) {
				Engine.openWebPage(pwURL, "Opening pathway on " + SITE_NAME, "Unable to open pathway at " + pwURL);
			} else {
				MessageDialog.openInformation(shell, "Info", "Pathway saved to " + SITE_NAME + 
				", please press 'refresh' in your browser or hit F5 to refresh the pathway image");
			}
			setNew(false); //Saved, so not new anymore
		} catch (InvocationTargetException e) {
			// handle exception
			error(shell, "Unable to save pathway to wiki", e.getCause());
		} catch (InterruptedException ie) {
			error(shell, "Unable to save pathway to wiki", ie);
		}
	}

	public void applicationEvent(ApplicationEvent e) {
		switch(e.type) {
		case ApplicationEvent.CLOSE_APPLICATION:
			if(Engine.getGmmlData().isChanged()) {
				boolean doit = MessageDialog.openQuestion(Engine.getWindow().getShell(), "Save pathway?", 
						"Do you want to save the changes to " + pwName + " on " + SITE_NAME + "?");
				if(doit) {
					saveUI();
				}
			} else {
				//Silently close
			}
		}
	}

	static class XmlRpcCookieTransportFactory implements XmlRpcTransportFactory {
		private final XmlRpcCookieHttpTransport TRANSPORT;

		public XmlRpcCookieTransportFactory(XmlRpcClient pClient) {
			TRANSPORT = new XmlRpcCookieHttpTransport(pClient);
		 }
		
		public XmlRpcTransport getTransport() { return TRANSPORT; }
	}

	/** Implementation of an HTTP transport that supports sending cookies with the
	 * HTTP header, based on the {@link java.net.HttpURLConnection} class.
	 */
	public static class XmlRpcCookieHttpTransport extends XmlRpcHttpTransport {
		private static final String userAgent = USER_AGENT + " (Sun HTTP Transport, mod Thomas)";
		private static final String cookieHeader = "Cookie";
		private URLConnection conn;
		private HashMap<String, String> cookie;
		
		public XmlRpcCookieHttpTransport(XmlRpcClient pClient) {
			super(pClient, userAgent);
			cookie = new HashMap<String, String>();
		}

		public void addCookie(String key, String value) {
			cookie.put(key, value);
		}
		
		protected void setCookies() {
			String cookieString = null;
			for(String key : cookie.keySet()) {
				cookieString = (cookieString == null ? "" : cookieString + "; ") + key + "=" + cookie.get(key);
			}
			if(cookieString != null) {
				conn.setRequestProperty(cookieHeader, cookieString);
			}
		}
		
		public Object sendRequest(XmlRpcRequest pRequest) throws XmlRpcException {
			XmlRpcHttpClientConfig config = (XmlRpcHttpClientConfig) pRequest.getConfig();
			try {
				conn = config.getServerURL().openConnection();
				conn.setUseCaches(false);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				setCookies();
			} catch (IOException e) {
				throw new XmlRpcException("Failed to create URLConnection: " + e.getMessage(), e);
			}
			return super.sendRequest(pRequest);
		}

		protected void setRequestHeader(String pHeader, String pValue) {
			conn.setRequestProperty(pHeader, pValue);
			
		}

		protected void close() throws XmlRpcClientException {
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection) conn).disconnect();
			}
		}

		protected boolean isResponseGzipCompressed(XmlRpcStreamRequestConfig pConfig) {
			return HttpUtil.isUsingGzipEncoding(conn.getHeaderField("Content-Encoding"));
		}

		protected InputStream getInputStream() throws XmlRpcException {
			try {
				return conn.getInputStream();
			} catch (IOException e) {
				throw new XmlRpcException("Failed to create input stream: " + e.getMessage(), e);
			}
		}

		protected void writeRequest(ReqWriter pWriter) throws IOException, XmlRpcException, SAXException {
	        pWriter.write(conn.getOutputStream());
		}
	}
}
