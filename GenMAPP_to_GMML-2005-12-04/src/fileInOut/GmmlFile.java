package fileInOut;

import javax.xml.bind.*;
import gmml.*;
import java.io.*;

public class GmmlFile {
    
    // This metod writes the pathway p to an xml file and validates it
    public static void writeToXML(Pathway p, String filename) {
        System.out.println(">> MARSHALLING CONTENT TREE TO XML FILE '"+filename+"'");
        // Open/create a file
        FileWriter out = null;
        try {
            out = new FileWriter(filename);
            
        } catch (IOException ex) {
            System.out.println("-> IOException: "+ex.getMessage());
            System.out.println("-> file not written due to IOException:'"+ex.getMessage()+"\n");
        }
        
        try {
            JAXBContext jc = JAXBContext.newInstance("gmml");
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,"http://www.genmapp.org/Ferrante_2004/GMML.xsd");
            m.marshal(p,out);
            System.out.println(">> VALIDATING CONTENT TREE");
            Validator v = jc.createValidator();
            boolean valid = v.validateRoot(p);
            System.out.println("-> Validation ok?: "+valid);
        } catch( ValidationException ue ) {
        //    System.out.println("-> Caught ValidationException: '"+ue.getLinkedException()+"'\n Resulting xml file '"+filename+"' is not valid according to the GMML xsd schema\n" );
        } catch(JAXBException ex) {
            System.out.println("-> JAXBException: "+ ex.getMessage());
            System.out.println("-> file not written due to JAXBException:'"+ex.getMessage()+"'");
        }
        
        
    }
    
}
