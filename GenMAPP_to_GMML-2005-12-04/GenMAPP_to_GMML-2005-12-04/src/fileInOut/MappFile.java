package fileInOut;

import java.io.*;
import java.sql.*;

public class MappFile {
    
    public static String database_after = ";DriverID=22;READONLY=true}";
    public static String database_before =
            "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
    // This method returns the OBJECTS data of the given .MAPP file as a Nx18 string array
    public static String[][] importMAPPObjects(String filename) {
        System.out.println(">> IMPORTING OBJECTS TABLE OF MAPP FILE '"+filename+"'");
        String database = database_before + filename + database_after;
        String[] headers = {"ID", "ObjKey", "SystemCode", "Type", "CenterX",
                "CenterY",
                "SecondX", "SecondY", "Width", "Height", "Rotation",
                "Color", "Label", "Head",
                "Remarks", "Image", "Links", "Notes"};
                String[][] result = null;

                try {
                    // Load Sun's jdbc-odbc driver
                    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                    
                    // Create the connection to the database
                    Connection con = DriverManager.getConnection(database, "", "");
                    
                    // Create a new sql statement
                    Statement s = con.createStatement();
                    // Count the rows of the column "Type" (has an instance for every object)
                    ResultSet r = s.executeQuery(
                            "SELECT COUNT(Type) AS rowcount FROM Objects");
                    r.next();
                    int nrRows = r.getInt("rowcount")+1;
                    // now create a nrRows*18 string array
                    result = new String[nrRows + 1][headers.length];
                    result[0] = headers;
                    // and fill it
                    for (int j = 0; j < headers.length; j++) {
                        r = s.executeQuery("SELECT " + headers[j] + " FROM Objects");
                        for (int i = 1; i < nrRows; i++) {
                            r.next();
                            result[i][j] = r.getString(1);
                            //GUI.GUIframe.textOut.append("added " + result[i][j] + " to " +
                            //        headers[j] + " at row " + i+"\n");
                        }
                    }
                    r.close();
                    con.close();
                } catch (SQLException ex) {
                    System.out.println("-> SQLException: "+ex.getMessage());
                    System.out.println("-> Could not import data from file '"+filename+"' due to an SQL exception \n"+ex.getMessage()+"\n");
                } catch (ClassNotFoundException cl_ex) {
                    System.out.println("-> Could not find the Sun JbdcObdcDriver\n");
                }
                return result;
    }
    
    // This method returns the INFO data of the given .MAPP file as a 1x16 string array
    public static String[][] importMAPPInfo(String filename) {
        System.out.println(">> IMPORTING INFO TABLE OF MAPP FILE '"+filename+"'");
        String database = database_before + filename + database_after;
        String[] headers = {"Title", "MAPP", "Version", "Author",
                "GeneDBVersion", "Maint", "Email", "Copyright",
                "Modify", "Remarks", "BoardWidth", "BoardHeight",
                "WindowWidth", "WindowHeight", "GeneDB", "Notes"};
                String[][] result = null;
                
                try {
                    // Load Sun's jdbc-odbc driver
                    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                    
                    // Create the connection to the database
                    Connection con = DriverManager.getConnection(database, "", "");
                    
                    // Create a new sql statement
                    Statement s = con.createStatement();
                    
                    // now create a nrRows*18 string array
                    result = new String[2][headers.length];
                    result[0] = headers;
                    ResultSet r = null;
                    // and fill it
                    for (int j = 0; j < headers.length; j++) {
                        r = s.executeQuery("SELECT " + headers[j] + " FROM Info");
                        r.next();
                        result[1][j] = r.getString(1);
                        //GUI.GUIframe.textOut.append("added " + result[1][j] + " to " + headers[j]);
                    }
                    r.close();
                    con.close();
                } catch (ClassNotFoundException cl_ex) {
                    System.out.println("-> Could not find the Sun JbdcObdcDriver\n");
                } catch (SQLException ex) {
                    System.out.println("-> SQLException: "+ex.getMessage());
                    System.out.println("-> Could not import data from file '"+filename+"' due to an SQL exception:\n"+ex.getMessage()+"\n");
                }
                return result;
                
    }
    
    // This method writes the MAPP data, given in the string[][] mappdata, to a csv-file with name filename
    public static void writetoCSV(String[][] mappdata, String filename) {
        System.out.println(">> WRITING MAPP DATA TO CSV FILE '"+filename+" '");
        // create a PrintWriter
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(filename));
        } catch (IOException ex) {
            System.out.println("-> IOException: "+ex.getMessage());
            System.out.println("-> could not write data to '"+filename+"' due to an IOException\n");
        }
        for (int i = 0; i < mappdata.length; i++) {
            out.print('"' + mappdata[i][0] + '"');
            for (int j = 1; j < mappdata[i].length; j++) {
                out.print("," + '"' + mappdata[i][j] + '"');
            }
            out.println();
        }
        out.close();
    }
}
