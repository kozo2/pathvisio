package data;

import gmmlVision.GmmlVision;
import gmmlVision.GmmlVision.ApplicationEvent;
import gmmlVision.GmmlVision.ApplicationEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import util.FileUtils;
import visualization.VisualizationManager;
import visualization.colorset.ColorSetManager;
import data.GmmlGdb.IdCodePair;
import data.GmmlGex.CachedData.Data;
import data.ImportExprDataWizard.ImportInformation;
import data.ImportExprDataWizard.ImportPage;
import debug.StopWatch;

class RImporter {
    
    public RImporter(int[] cols, String file, String dbName, int head, int first, int id, int code) {  
        ImportInformation info = new ImportExprDataWizard.ImportInformation();
        ImportPage page;
        IProgressMonitor monitor;
        //setInfo(cols, file, dbName, head, first, id, code);
        info.setStringCols(cols);
        File txtFile = new File(file);
        info.setTxtFile(txtFile);
        info.dbName = dbName;
        info.headerRow = head;
        info.firstDataRow = first;
        info.idColumn = id;
        info.codeColumn = code;        
        GmmlGex.importFromTxt(info, page, monitor);
        //ImportRunnableWithProgress import = new GmmlGex.ImportRunnableWithProgress(info,page);
        //import.run(monitor);
    }
    
//    public void setInfo(int[] cols, String file, String dbName, int head, int first, int id, int code) {
//        info.setStringCols(cols);
//        File txtFile = new File(file);
//        info.setTxtFile(file);
//        info.dbName = dbName;
//        info.headerRow = head;
//        info.firstDataRow = first;
//        info.idColumn = id;
//        info.codeColumn = code;
//    }

    
        
        
    
    
    
    

}
