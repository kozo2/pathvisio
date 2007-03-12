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

import data.GmmlGdb;

public class WikiPathways {
	HashMap<String, String> cookie;
	String rpcURL;
	String pwName;
	String pwURL;
	
	public WikiPathways() {
		cookie = new HashMap<String, String>();
	}
	
	public static void main(String[] args)
	{		
		final GmmlVisionWindow window = GmmlVision.getWindow();
		GmmlVisionMain.initiate();
		
		final WikiPathways wiki = new WikiPathways();
		
		for(int i = 0; i < args.length - 1; i++) {
			//Check for parameters
			String a = args[i];
			if(a.startsWith("-")) {
				if		(a.equalsIgnoreCase("-c")) {
					String c = args[i+1];
					int sep = c.indexOf('=');
					if(sep <= 0 || sep >= c.length() - 1) {
						GmmlVision.log.error("Error in -c parameter");
						System.exit(-1);
					} else {
						GmmlVision.log.info("Parsed -c argument" + c);
						wiki.setCookie(c.substring(0, sep), c.substring(sep + 1));
					}
				}
				else if	(a.equalsIgnoreCase("-pwName")) {
					GmmlVision.log.info("Parsed -pwName argument" + args[i+1]);
					wiki.setPathwayName(args[i+1]);
				}
				else if	(a.equalsIgnoreCase("-pwUrl")) {
					GmmlVision.log.info("Parsed -pwUrl argument" + args[i+1]);
					wiki.setPathwayURL(args[i+1]);
				}
				else if	(a.equalsIgnoreCase("-rpcUrl")) {
					GmmlVision.log.info("Parsed -rpcUrl argument" + args[i+1]);
					wiki.setRpcURL(args[i+1]);
				}
				i++; //Skip next, was value for this option
			}
		}
		
		Thread thr = new Thread() {
			public void run() {
				window.setBlockOnOpen(true);
				window.open();
				
				GmmlGdb.close();				
				Display.getCurrent().dispose();
			}
		};
		thr.start();
		
		while(GmmlVision.getWindow().getShell() == null) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				//Swallow it
			}
		}
		
		//Open the pathway
		File gpmlFile = null;
		try {
			gpmlFile = GmmlVision.openPathway(new URL(wiki.pwURL));
		} catch(Exception e) {
			GmmlVision.log.error("Malformed url", e);
			e.printStackTrace();
			System.exit(-1);
		}
		
		//Wait for user to finish editing
		while(thr.isAlive()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				break;
			}
		}
		
		//Save the pathway back to the wiki
		if(gpmlFile != null) {
			final Shell shell = new Shell();
			final File fgpmlFile = gpmlFile;
			try {
				IRunnableWithProgress op = new IRunnableWithProgress() {
					public void run(IProgressMonitor m) throws InvocationTargetException, InterruptedException {
						try {
							m.beginTask("Saving pathway to wiki", IProgressMonitor.UNKNOWN);
							wiki.saveToWiki(fgpmlFile);
							m.done();
						} catch(Exception e) {
							throw new InvocationTargetException(e);
						}
					}
				};
				ProgressMonitorDialog pd = new ProgressMonitorDialog(shell);
				pd.run(true, false, op);
				MessageDialog.openInformation(shell, "Info", "Pathway saved to wiki");
				
			} catch (InvocationTargetException e) {
				// handle exception
				GmmlVision.log.error("Unable to save pathway to wiki", e.getCause());
				MessageDialog.openError(shell, "Unable to save pathway to wiki", e.getCause().getMessage());
			} catch (InterruptedException ie) {
				GmmlVision.log.error("Unable to save pathway to wiki", ie);
			}
		}
		//Close log stream
		GmmlVision.log.getStream().close();
	}

	protected void setCookie(String key, String value) {
		cookie.put(key, value);
	}
	
	protected void setPathwayName(String pathwayName) {
		pwName = pathwayName;
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
		Object[] params = new Object[]{ pwName, data64 };
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
