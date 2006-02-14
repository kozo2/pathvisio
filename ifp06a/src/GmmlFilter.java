import java.io.*;

class GmmlFilter extends javax.swing.filechooser.FileFilter 
{
	public GmmlFilter()
	{
	}

	public boolean accept(File f) 
	{
		return f.getName ().toLowerCase ().endsWith (".xml") || f.isDirectory ();
	}
	
	public String getDescription () 
	{
		return "GMML Files (*.xml)";
	}
}
