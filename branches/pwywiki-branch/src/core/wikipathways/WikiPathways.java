package wikipathways;

import gmmlVision.GmmlVision;
import gmmlVision.GmmlVisionMain;
import gmmlVision.GmmlVisionWindow;

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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.SAXException;

import util.SwtUtils.SimpleRunnableWithProgress;
import data.GmmlGdb;

public class WikiPathways {
	public static String SITE_NAME = "WikiPathways.org";
	HashMap<String, String> cookie;
	String rpcURL;
	String pwName;
	String pwSpecies;
	String pwURL;
	
	File localFile;
	
	public WikiPathways() {
		cookie = new HashMap<String, String>();
	}
	
	public static void main(String[] args)
	{	
		//Setup PathVisio
		final GmmlVisionWindow window = GmmlVision.getWindow();
		GmmlVisionMain.initiate();
				
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
						GmmlVision.log.trace("Parsed -c argument" + c);
						wiki.setCookie(c.substring(0, sep), c.substring(sep + 1));
					}
				}
				else if	(a.equalsIgnoreCase("-pwName")) {
					GmmlVision.log.trace("Parsed -pwName argument" + args[i+1]);
					wiki.setPathwayName(args[i+1]);
				}
				else if	(a.equalsIgnoreCase("-pwUrl")) {
					GmmlVision.log.trace("Parsed -pwUrl argument" + args[i+1]);
					wiki.setPathwayURL(args[i+1]);
				}
				else if	(a.equalsIgnoreCase("-rpcUrl")) {
					GmmlVision.log.trace("Parsed -rpcUrl argument" + args[i+1]);
					wiki.setRpcURL(args[i+1]);
				}
				else if (a.equalsIgnoreCase("-pwSpecies")) {
					GmmlVision.log.trace("Parsed -pwSpecies argument" + args[i+1]);
					wiki.setPathwaySpecies(args[i+1]);
				}
				i++; //Skip next, was value for this option
			}
		}
		
		//Start PathVisio in a seperate thread
		Thread thr = new Thread() {
			public void run() {
				window.setBlockOnOpen(true);
				window.open();
				
				GmmlGdb.close();				
				Display.getCurrent().dispose();
			}
		};
		thr.start();
		
		//Wait for PathVisio to startup
		while(window.getShell() == null) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				//Swallow it
			}
		}
		
		//Open the pathway
		Shell wshell = GmmlVision.getWindow().getShell();
		
		final SimpleRunnableWithProgress sp = new SimpleRunnableWithProgress(
				WikiPathways.class, "openPathwayURL", new Class[] {}, new Object[] {}, wiki);
		SimpleRunnableWithProgress.setMonitorInfo(
				"Downloading patwhay from " + SITE_NAME, IProgressMonitor.UNKNOWN);
		
		final ProgressMonitorDialog pd1 = new ProgressMonitorDialog(wshell);
		wshell.getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					pd1.run(true, false, sp);
				} catch (Exception e) {
					//Exception handled by SimpleRunnableWithProgress
					GmmlVision.exit();
					System.exit(-1);
				}
			}
		});
				
		//Wait for user to finish editing
		while(thr.isAlive()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				break;
			}
		}
		
		//Save the pathway back to the wiki
		if(wiki.getLocalFile() != null) {
			final Shell shell = new Shell();
			final File fgpmlFile = wiki.getLocalFile();
			try {
				IRunnableWithProgress op = new IRunnableWithProgress() {
					public void run(IProgressMonitor m) throws InvocationTargetException, InterruptedException {
						try {
							m.beginTask("Saving pathway to " + SITE_NAME, IProgressMonitor.UNKNOWN);
							wiki.saveToWiki(fgpmlFile);
							m.done();
						} catch(Exception e) {
							throw new InvocationTargetException(e);
						}
					}
				};
				ProgressMonitorDialog pd2 = new ProgressMonitorDialog(shell);
				pd2.run(true, false, op);
				MessageDialog.openInformation(shell, "Info", "Pathway saved to " + SITE_NAME + 
						", please press 'refresh' or hit F5 in your browser to refresh the pathway image");
				
			} catch (InvocationTargetException e) {
				// handle exception
				error(shell, "Unable to save pathway to wiki", e.getCause());
			} catch (InterruptedException ie) {
				error(shell, "Unable to save pathway to wiki", ie);
			}
		}
		//Close log stream
		GmmlVision.log.getStream().close();
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
		GmmlVision.log.error(error, e);
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				Throwable ex = e;
				if(ex instanceof InvocationTargetException) ex = ex.getCause();
				MessageDialog.openError(shell, error, e != null ? e.getMessage() : "No message specified");
			}
		});
	}
	
	protected void setCookie(String key, String value) {
		cookie.put(key, value);
	}
	
	public void openPathwayURL() throws MalformedURLException, IOException {
		localFile = GmmlVision.openPathway(new URL(pwURL));
	}
	
	protected File getLocalFile() { return localFile; }
	
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
	
	protected void saveToWiki(File gpmlFile) throws XmlRpcException, IOException {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL(rpcURL));
		
		XmlRpcClient client = new XmlRpcClient();
		XmlRpcCookieTransportFactory ctf = new XmlRpcCookieTransportFactory(client);
		
		XmlRpcCookieHttpTransport ct = (XmlRpcCookieHttpTransport)ctf.getTransport();
		for(String key : cookie.keySet()) {
			ct.addCookie(key, cookie.get(key));
		}
		
		client.setTransportFactory(ctf);
		client.setConfig(config);
		
		RandomAccessFile raf = new RandomAccessFile(gpmlFile, "r");
		byte[] data = new byte[(int)raf.length()];
		raf.readFully(data);
		byte[] data64 = Base64.encodeBase64(data);
		Object[] params = new Object[]{ pwName, pwSpecies, data64 };
		client.execute("WikiPathways.updatePathway", params);
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
		private static final String userAgent = USER_AGENT + " (Sun HTTP Transport)";
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
				cookieString = cookieString + "; " + key + "=" + cookie.get(key);
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
