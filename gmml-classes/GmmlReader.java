import org.jdom.JDOMException;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.*;
import org.jdom.Element;
import org.jdom.Attribute;
import java.io.IOException;
import java.util.*;


public class GmmlReader {
  GmmlPathway pathway;
  
  public GmmlReader(String file) {
    //Create the pathway
    pathway = new GmmlPathway(); 
    
    
    System.out.println("Start reading the XML file: "+file);
	   
    SAXBuilder builder = new SAXBuilder(false);
                                    //  ^^^^^
                                    // Turn off validation
      
    // command line should offer URIs or file names
    try {
      Document doc = builder.build(file);
      // If there are no well-formedness or validity errors, 
      // then no exception is thrown.
      System.out.println(file + " is not validated.");
      checkGMML(doc, 0);
    }
    // indicates a well-formedness or validity error
    catch (JDOMException e) { 
      System.out.println(file + " is not valid.");
      System.out.println(e.getMessage());
    }  
    catch (IOException e) { 
      System.out.println("Could not check " + file);
      System.out.println(" because " + e.getMessage());
    }  
  }
  public GmmlPathway getPathway() {
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
    
  public void checkGMML(Object o, int depth) { 
    printSpaces(depth);
    
    if (o instanceof Element) {
      Element element = (Element) o;
      System.out.println("Element: " + element.getName());
      checkPathway(element);
    }
    else if (o instanceof Document) {
      System.out.println("Document");
      Document doc = (Document) o;
      List children = doc.getContent();
      Iterator iterator = children.iterator();
      while (iterator.hasNext()) {
        Object child = iterator.next();
        checkGMML(child, depth+1);
      }
    }
    else {  // This really shouldn't happen
      System.out.println("Unexpected type: " + o.getClass());
    }
  }


  private void printSpaces(int n) {
    
    for (int i = 0; i < n; i++) {
      System.out.print(' '); 
    }
    
  }
  
  private void checkPathway (Element e) {
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
          //Make a very big if-elseif statement for fitlering all atrributes TODO!
          if ("Name".equalsIgnoreCase(attribute.getName())) {
          	//System.out.println("Found attribute Name: "+attribute.getValue());
	         pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Organism".equalsIgnoreCase(attribute.getName())) {
            //System.out.println("Found attribute Organism: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Data-Source".equalsIgnoreCase(attribute.getName())) {
            //System.out.println("Found attribute Data-Source: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Version".equalsIgnoreCase(attribute.getName())) {
            //System.out.println("Found attribute Version: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Author".equalsIgnoreCase(attribute.getName())) {
            //System.out.println("Found attribute Author: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Maintained-By".equalsIgnoreCase(attribute.getName())) {
            //System.out.println("Found attribute Maintained-By: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Email".equalsIgnoreCase(attribute.getName())) {
            //System.out.println("Found attribute Email: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Availability".equalsIgnoreCase(attribute.getName())) {
            //System.out.println("Found attribute Availability: "+attribute.getValue());
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Last-Modified".equalsIgnoreCase(attribute.getName())) {
            //System.out.println("Found attribute Last-Modified: "+attribute.getValue());
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
        checkPathwayChilds(child, 1);
      }
	 } else {
	   System.out.println("Found unsupported first level element!");
	 }
  }
  
  public void checkPathwayChilds(Object o, int depth) {
   
    printSpaces(depth);
    
    if (o instanceof Element) {
      Element element = (Element) o;
      //System.out.println("Element: " + element.getName());
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
		         } //end if BoardWidth
	            else if("BoardHeight".equalsIgnoreCase(attribute.getName())) {
	            	height = Integer.parseInt(attribute.getValue());
		         } //end if BoardHeight
		      } //end if attribute
		   } //end while hasNext()
		   System.out.println("Trying to resize pathway to: '"+width/15+"'x'"+height/15+"'");
		   pathway.setSize(width/15, height/15);
		} //If Graphics
		else if ("GeneProduct".equalsIgnoreCase(element.getName())) {
			//System.out.println("Geneproduct not fully implemented yet");
	      List attributes = element.getAttributes();
	      Iterator aiterator = attributes.iterator();
      	while (aiterator.hasNext()) {
        		Object att = aiterator.next();
		      if (att instanceof Attribute) {
	            Attribute attribute = (Attribute) att;
	            //System.out.println("Found an attribute of type:" + attribute.getName() + "  with the value: "+attribute.getValue() );
					if("GeneID".equalsIgnoreCase(attribute.getName())) {
		        		//System.out.println("Found name : "+attribute.getValue());
				      pathway.addGeneProductText(attribute.getValue());
		         } //end if GeneID
		      } //end if attribute
		   } //end while hasNext()
			List children = element.getContent();
      	Iterator iterator = children.iterator();
      	while (iterator.hasNext()) {
        		Object child = iterator.next();
        		if (child instanceof Element) {
		        Element subelement = (Element) child;
		        if("Graphics".equalsIgnoreCase(subelement.getName())) {
		        		//System.out.println("Found GP grapgics");
		        		int x = 0;
				      int y = 0;
				      int cx = 0;
						int cy = 0;
						int width = 0;
						int height = 0;

		        		List sattributes = subelement.getAttributes();
			        	Iterator saiterator = sattributes.iterator();
				      while (saiterator.hasNext()) {
				      	Object att = saiterator.next();
					      if (att instanceof Attribute) {
					      	Attribute attribute = (Attribute) att;
					      	if("CenterX".equalsIgnoreCase(attribute.getName())) {
						      	cx = Integer.parseInt(attribute.getValue());
						      } //end if centerx
						      else if("CenterY".equalsIgnoreCase(attribute.getName())) {
						      	cy = Integer.parseInt(attribute.getValue());
						      } //end if centery
						      else if("Width".equalsIgnoreCase(attribute.getName())) {
						      	width = Integer.parseInt(attribute.getValue());
						      } //end if width
						      else if("Height".equalsIgnoreCase(attribute.getName())) {
						      	height = Integer.parseInt(attribute.getValue());
						      } //end if heigh
					      } //end if attribute
					   } //end while hasNext()
					   x = cx - (width/2);
					   
					   y = cy - (height/2);
					   pathway.addRect(x/15,y/15,width/15,height/15);
		        } //end if graphics
		      } //end if element
		   } //end while hasNext()
		} //end else if Geneproduct
	   else if ("Line".equalsIgnoreCase(element.getName())) {
	   	int style = 0;
		   int type= 0;
     		int sx = 0;
	      int sy = 0;
	      int ex = 0;
			int ey = 0;
			String stype;
			String sstyle;
						
			//System.out.println("Line not fully not implemented yet");
			List children = element.getContent();
      	Iterator iterator = children.iterator();
      	while (iterator.hasNext()) {
        		Object child = iterator.next();
        		if (child instanceof Element) {
		        Element subelement = (Element) child;
		        if("Graphics".equalsIgnoreCase(subelement.getName())) {
		        		List attributes = subelement.getAttributes();
			        	Iterator aiterator = attributes.iterator();
				      while (aiterator.hasNext()) {
				      	Object att = aiterator.next();
					      if (att instanceof Attribute) {
					      	Attribute attribute = (Attribute) att;
					      	if("StartX".equalsIgnoreCase(attribute.getName())) {
						      	sx = Integer.parseInt(attribute.getValue());
						      } //end if startx
						      if("StartY".equalsIgnoreCase(attribute.getName())) {
						      	sy = Integer.parseInt(attribute.getValue());
						      } //end if starty
						      if("EndX".equalsIgnoreCase(attribute.getName())) {
						      	ex = Integer.parseInt(attribute.getValue());
						      } //end if endx
						      if("EndY".equalsIgnoreCase(attribute.getName())) {
						      	ey = Integer.parseInt(attribute.getValue());
						      } //end if endy
					      } //end if attribute
					   } //end while hasNext()
		        } //end if graphics
		      } //end if element
		   } //end while hasNext()
			List attributes = element.getAttributes();
			Iterator aiterator = attributes.iterator();
			while (aiterator.hasNext()) {
				Object att = aiterator.next();
				if (att instanceof Attribute) {
					Attribute attribute = (Attribute) att;
					if("Style".equalsIgnoreCase(attribute.getName())) {
						sstyle = attribute.getValue();
						if("Solid".equalsIgnoreCase(sstyle)) {
					   	style = 0;
						}
						if("Broken".equalsIgnoreCase(sstyle)) {
					   	style = 1;
						}
					}
					if("Type".equalsIgnoreCase(attribute.getName())) {
						stype = attribute.getValue();
						if("Line".equalsIgnoreCase(stype)) {
							type = 0;
						}
						if("Arrow".equalsIgnoreCase(stype)) {
						  	type = 1;
						}
					}
				} //end if attribute
			}//end while hasNext()
		   	
		   pathway.addLine(sx/15,sy/15,ex/15,ey/15, style, type);
		} //end else if Line

    } //end if element
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
