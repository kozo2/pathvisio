import org.jdom.JDOMException;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.*;
import org.jdom.Element;
import org.jdom.Attribute;
import java.io.IOException;
import java.util.*;
import org.apache.xerces.parsers.SAXParser;


public class GmmlReader {
  public static GmmlPathway read() {
    System.out.println("changed argument to default: Hs_G13_Signaling_Pathway.xml");

    GmmlPathway pathway = read("Hs_G13_Signaling_Pathway.xml");
    return pathway;
  }
  public static GmmlPathway read(String args) {
    System.out.println("Start reading the XML file");
  	 GmmlPathway pathway = new GmmlPathway();
	   
    SAXBuilder builder = new SAXBuilder(false);
                                    //  ^^^^^
                                    // Turn off validation
      
    // command line should offer URIs or file names
    try {
      Document doc = builder.build(args);
      // If there are no well-formedness or validity errors, 
      // then no exception is thrown.
      System.out.println(args + " is not validated.");
      pathway = checkGMML(doc, 0);
    }
    // indicates a well-formedness or validity error
    catch (JDOMException e) { 
      System.out.println(args + " is not valid.");
      System.out.println(e.getMessage());
    }  
    catch (IOException e) { 
      System.out.println("Could not check " + args);
      System.out.println(" because " + e.getMessage());
    }  
    return pathway;
  }

    private static Object resizeArray (Object oldArray, int newSize) {
        int oldSize = java.lang.reflect.Array.getLength(oldArray);
        Class elementType = oldArray.getClass().getComponentType();
        Object newArray = java.lang.reflect.Array.newInstance(
              elementType,newSize);
        int preserveLength = Math.min(oldSize,newSize);
        if (preserveLength > 0)
            System.arraycopy (oldArray,0,newArray,0,preserveLength);
        return newArray; 
    }
    
  public static GmmlPathway checkGMML(Object o, int depth) {
    GmmlPathway pathway = new GmmlPathway();
    
    printSpaces(depth);
    
    if (o instanceof Element) {
      Element element = (Element) o;
      System.out.println("Element: " + element.getName());
      pathway = checkPathway(element);
    }
    else if (o instanceof Document) {
      System.out.println("Document");
      Document doc = (Document) o;
      List children = doc.getContent();
      Iterator iterator = children.iterator();
      while (iterator.hasNext()) {
        Object child = iterator.next();
        pathway = checkGMML(child, depth+1);
      }
    }
    else {  // This really shouldn't happen
      System.out.println("Unexpected type: " + o.getClass());
    }
    
    return pathway;
  }


  private static void printSpaces(int n) {
    
    for (int i = 0; i < n; i++) {
      System.out.print(' '); 
    }
    
  }
  
  private static GmmlPathway checkPathway (Element e) {
    //Create an empty pathway
    GmmlPathway pathway = new GmmlPathway();
    
    String name = e.getName();
    //We only want a pathway in the document root
    if (name.equalsIgnoreCase("Pathway")) {
      System.out.println("Found a pathway, extracting data...");
      //Get attributes
      List attributes = e.getAttributes();
      Iterator aiterator = attributes.iterator();
      while (aiterator.hasNext()) {
        Object att = aiterator.next();
        if (att instanceof Attribute) {
	       Attribute attribute = (Attribute) att;
          //System.out.println("Attribute name: "+attribute.getName()+ "value : "+attribute.getValue());
          //Make a very big if-elseif statement for fitlering all atrributes TODO!
          if ("Name".equalsIgnoreCase(attribute.getName())) {
          	System.out.println("Found attribute Name: "+attribute.getValue());
	         pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Organism".equalsIgnoreCase(attribute.getName())) {
            System.out.println("Found attribute Organism: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Data-Source".equalsIgnoreCase(attribute.getName())) {
            System.out.println("Found attribute Data-Source: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Version".equalsIgnoreCase(attribute.getName())) {
            System.out.println("Found attribute Version: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Author".equalsIgnoreCase(attribute.getName())) {
            System.out.println("Found attribute Author: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Maintained-By".equalsIgnoreCase(attribute.getName())) {
            System.out.println("Found attribute Maintained-By: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Email".equalsIgnoreCase(attribute.getName())) {
            System.out.println("Found attribute Email: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Availability".equalsIgnoreCase(attribute.getName())) {
            System.out.println("Found attribute Availability: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Last-Modified".equalsIgnoreCase(attribute.getName())) {
            System.out.println("Found attribute Last-Modified: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());         
          } else {
          	System.out.println("Ignored unknown an attribute! Attribute name: "+attribute.getName()+ "value : "+attribute.getValue());
          }
        }  
      }
   
      //Get children
	   List children = e.getContent();
      Iterator iterator = children.iterator();
      while (iterator.hasNext()) {
        Object child = iterator.next();
        checkPathwayChilds(child, 1, pathway);
      }
	 } else {
	   System.out.println("Found unsupported first level element!");
	 }
	 
	 return pathway;
  }
  
  public static void checkPathwayChilds(Object o, int depth, GmmlPathway pathway) {
   
    printSpaces(depth);
    
    if (o instanceof Element) {
      Element element = (Element) o;
      System.out.println("Element: " + element.getName());
      if("Graphics".equalsIgnoreCase(element.getName())) {
      	int height = 0;      	
	      int width = 0;
	      List attributes = element.getAttributes();
	      Iterator aiterator = attributes.iterator();
      	while (aiterator.hasNext()) {
        		Object att = aiterator.next();
		      if (att instanceof Attribute) {
	            Attribute attribute = (Attribute) att;
	            if("BoardWidth".equalsIgnoreCase(attribute.getName())) {
	            	width = Integer.parseInt(attribute.getValue());
		         }
	            if("BoardHeight".equalsIgnoreCase(attribute.getName())) {
	            	height = Integer.parseInt(attribute.getValue());
		         }
		      } 
		   }
		   System.out.println("Trying to resize pathway to: '"+width+"'x'"+height+"'");
		   pathway.resize(width/10, height/10);
		} else if ("GeneProduct".equalsIgnoreCase(element.getName())) {
			System.out.println("Geneproduct not implemented yet");
			List children = element.getContent();
      	Iterator iterator = children.iterator();
      	while (iterator.hasNext()) {
        		Object child = iterator.next();
        		if (child instanceof Element) {
		        Element subelement = (Element) child;
		        if("Graphics".equalsIgnoreCase(subelement.getName())) {
		        		System.out.println("Found GP grapgics");
		        		int x = 0;
				      int y = 0;
				      int cx = 0;
						int cy = 0;
						int width = 0;
						int height = 0;

		        		List attributes = subelement.getAttributes();
			        	Iterator aiterator = attributes.iterator();
				      while (aiterator.hasNext()) {
				      	Object att = aiterator.next();
					      if (att instanceof Attribute) {
					      	Attribute attribute = (Attribute) att;
					      	if("CenterX".equalsIgnoreCase(attribute.getName())) {
						      	cx = Integer.parseInt(attribute.getValue());
						      }
						      if("CenterY".equalsIgnoreCase(attribute.getName())) {
						      	cy = Integer.parseInt(attribute.getValue());
						      }
						      if("Width".equalsIgnoreCase(attribute.getName())) {
						      	width = Integer.parseInt(attribute.getValue());
						      }
						      if("Height".equalsIgnoreCase(attribute.getName())) {
						      	height = Integer.parseInt(attribute.getValue());
						      }
					      }
					   }
					   x = cx - (width/2);
					   
					   y = cy - (height/2);
					   pathway.addRectCoord(x/10,y/10,width/10,height/10);
		        }
		      }

		   }
		}
		
		
	 /*List children = element.getContent();
      Iterator iterator = children.iterator();
      while (iterator.hasNext()) {
        Object child = iterator.next();
        listPathwayChilds(child, depth+1);
      }
      */
    }
    else if (o instanceof Document) {
      System.out.println("Document");
      Document doc = (Document) o;
      List children = doc.getContent();
      Iterator iterator = children.iterator();
      while (iterator.hasNext()) {
        Object child = iterator.next();
        //listPathwayChilds(child, depth+1);
      }
    }
    else if (o instanceof Comment) {
      System.out.println("Comment");
    }
    else if (o instanceof CDATA) {
      System.out.println("CDATA section");
      // CDATA is a subclass of Text so this test must come
      // before the test for Text.
    }
    else if (o instanceof Text) {
      Text text = (Text) o;
      if(!"".equalsIgnoreCase(text.getTextNormalize())) {
        printSpaces(depth);
	     System.out.println("Text: "+text.getTextNormalize());
	   }
    }
    else if (o instanceof EntityRef) {
      System.out.println("Entity reference");
    }
    else if (o instanceof ProcessingInstruction) {
      System.out.println("Processing Instruction");
    }
    else {  // This really shouldn't happen
      System.out.println("Unexpected type: " + o.getClass());
    }
    
  }
}
