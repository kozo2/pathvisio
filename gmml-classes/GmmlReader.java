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
    System.out.println("End of document");
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
          //a very big if-elseif statement for fitlering all atrributes
          if ("Name".equalsIgnoreCase(attribute.getName())) {
	         pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Organism".equalsIgnoreCase(attribute.getName())) {
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Data-Source".equalsIgnoreCase(attribute.getName())) {
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Version".equalsIgnoreCase(attribute.getName())) {
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Author".equalsIgnoreCase(attribute.getName())) {
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Maintained-By".equalsIgnoreCase(attribute.getName())) {
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Email".equalsIgnoreCase(attribute.getName())) {
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Availability".equalsIgnoreCase(attribute.getName())) {
            pathway.addAttribute(attribute.getName(),attribute.getValue());
          } else if ("Last-Modified".equalsIgnoreCase(attribute.getName())) {
            pathway.addAttribute(attribute.getName(),attribute.getValue());         
          } else {
          	System.out.println("Ignored unknown an attribute! Attribute name: "+attribute.getName()+ "value : "+attribute.getValue());
          }
        } //If attribute
      } //while hasNext()
   
      //Get children
	   List children = e.getContent();
      Iterator iterator = children.iterator();
      while (iterator.hasNext()) {
        Object child = iterator.next();
        checkPathwayChilds(child, 1);
      }
      System.out.println("All data extracted, done...");
	 } else {
	   System.out.println("Found unsupported first level element!");
	 }
  }
  
  public void checkPathwayChilds(Object o, int depth) {
  	//Here the tree structure of gmml-files will be read in. 
   
    printSpaces(depth);
    
    if (o instanceof Element) {
      Element element = (Element) o;
      
      /*
      *From this point each element is checked if its name corresponds to a known gmml element
      *First it is checked if the element has the name "Graphics", 
      *if it does the program continues (after executing the lines within the if statement) to the next element and it doesn't check if the name of the element corresponds to any other known gmml elements
      *but when the name of the element is not equal to the string "Graphics" the program checks if the name corresponds to "GeneProduct"
      *The program keeps checking if the element corresponds to one of the known gmml elements until it does and then it moves on to the next element
      *The program checks the known gmml elements in this order: Graphics, GeneProduct, Line, LineShape, Arc, Label, Shape, CellShape, Brace, CellComponent, ProteinComplex
      */
      if("Graphics".equalsIgnoreCase(element.getName())) {
      	/*within this element "Graphics" there are two attributes: "BoardWidth" and "BoardHeight"
	      *in this part of the code the list of attributes is checked if an attribute has the name "BoardWidth" or "BoardHeight"
	      *if it does, the values of the attributes will be assigned to the variables "width" or "height" respectively
	      */
	      int height = 0;      	
	      int width = 0;
	      List attributes = element.getAttributes();
	      Iterator aiterator = attributes.iterator();
      	while (aiterator.hasNext()) {	/*
	      										*Here it is checked if the list of the attributes from Graphics still has an unchecked item
	      										*If it does it is checked if it's named boardwidth or boardheight
											      */
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
		   System.out.println("Setting size of the pathway to: '"+width+"'x'"+height+"'");
		   pathway.setSize(width, height);
		} //end If Graphics
		else if ("GeneProduct".equalsIgnoreCase(element.getName())) {
	      /*
	      *Within an element "GeneProduct" there are attributes and elements
	      *First the attributes will be obtained, this is done in the similar way as checking the attributes and elements that is explained above 
	      *Geneproduct contains the attributes: GeneID, Type, GeneProduct-Data-Source, Short-name, Xref, BackpageHead
	      */
	      String ref="";
	      String geneID="";
	      
	      List attributes = element.getAttributes();
	      Iterator aiterator = attributes.iterator();
      	while (aiterator.hasNext()) {
        		Object att = aiterator.next();
		      if (att instanceof Attribute) {
	            Attribute attribute = (Attribute) att;
					if("GeneID".equalsIgnoreCase(attribute.getName())) {
				      geneID = attribute.getValue();
		         } //end if GeneID
		         else if("Type".equalsIgnoreCase(attribute.getName())) {
		        		//System.out.println("Type");
		         } //end if Type
					else if("GeneProduct-Data-Source".equalsIgnoreCase(attribute.getName())) {
		        		//System.out.println("GeneProduct-Data-Source");
		         } //end if GeneProduct-Data-Source
					else if("Short-Name".equalsIgnoreCase(attribute.getName())) {
		        		//System.out.println("Short-Name");
		         } //end if Short-Name
					else if("Xref".equalsIgnoreCase(attribute.getName())) {
		        		ref = attribute.getValue(); 
		         } //end if Xref
					else if("BackpageHead".equalsIgnoreCase(attribute.getName())) {
		        		//System.out.println("BackpageHead");
		         } //end if BackpageHead
		      } //end if attribute
		   } //end while hasNext()
			
			/*
			*This element "GeneProduct" contains elements itself
			*those will be obtained here, again by listing them and then by checking the names of the content
			*Geneproduct contains three elements: Graphics, comment and notes.
			*/
			List children = element.getContent();
      	Iterator iterator = children.iterator();
      	while (iterator.hasNext()) {
        		Object child = iterator.next();
        		if (child instanceof Element) {
		        Element subelement = (Element) child;
		        if("Graphics".equalsIgnoreCase(subelement.getName())) {		       
				      /*Here some variables will be created where the attribute values will be stored  */  
		        		int x = 0;
				      int y = 0;
				      int cx = 0;
						int cy = 0;
						int width = 0;
						int height = 0;
						/*This element Graphics (of Geneproduct) contains 4 attributes:
		        		*CenterX, CenterY, width, height
						*these attributes specify a rectangle
						*/
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
						      } //end if height
					      } //end if attribute
					   } //end while hasNext()
					   
					   x = cx - (width/2);
					   y = cy - (height/2);
   				   pathway.addGeneProduct(x,y,width,height,geneID,ref);
		         } //end if graphics
		         else if ("Comment".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Comment");
				   }//end if Comment
				   else if ("Notes".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Notes");
				   }//end if Notes   
		      } //end if element   
		   } //end while hasNext()
		   
		} //end else if Geneproduct
	   
	   /*Here the program checks if the element is a "Line"
	   *Within this element "Line", the subelements "Graphics", "Comment", "Notes", the attributes "Style" and "Type" will be encountered
	   */ 
	   else if ("Line".equalsIgnoreCase(element.getName())) {
	   	/*Creating variables where the attribute values of the subelement "Graphics" and the values of the attributes "Type" and "Style" will be stored */
		   int style = 0;
		   int type= 0;
     		double sx = 0;
	      double sy = 0;
	      double ex = 0;
			double ey = 0;
			String stype;
			String sstyle;
			String color="";
						
			//Reading in subelements.
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
						      else if("StartY".equalsIgnoreCase(attribute.getName())) {
						      	sy = Integer.parseInt(attribute.getValue());
						      } //end if starty
						      else if("EndX".equalsIgnoreCase(attribute.getName())) {
						      	ex = Integer.parseInt(attribute.getValue());
						      } //end if endx
						      else if("EndY".equalsIgnoreCase(attribute.getName())) {
						      	ey = Integer.parseInt(attribute.getValue());
						      } //end if endy
						      else if("Color".equalsIgnoreCase(attribute.getName())) {
						      	color = attribute.getValue();
						      } //end if color
					      } //end if attribute					      
					   } //end while hasNext()
		        } //end if graphics
		        else if ("Comment".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Comment");
				  }//end if Comment
				  else if ("Notes".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Notes");
				   }//end if Notes
		      } //end if element
		   } //end while hasNext()
			
			//Reading in attributes
			List attributes = element.getAttributes();
			Iterator aiterator = attributes.iterator();
			while (aiterator.hasNext()) {
				Object att = aiterator.next();
				if (att instanceof Attribute) {
					Attribute attribute = (Attribute) att;
					/*The attribute "Style can have two different values: "Solid" or "broken"
					*These values will be stored in the variable "style" the value 0 stands for "Solid" the value 1 stands for "Broken"
					*/
					if("Style".equalsIgnoreCase(attribute.getName())) {
						sstyle = attribute.getValue();
						if("Solid".equalsIgnoreCase(sstyle)) {
					   	style = 0;
						}
						else if("Broken".equalsIgnoreCase(sstyle)) {
					   	style = 1;
						}
					}
					/*The attribute "Type" can have two different values: "Line" or "Arrow"
					*These values will be stored in the variable "type" the value 0 stands for "Line" the value 1 stands for "Arrow"
					*/
					if("Type".equalsIgnoreCase(attribute.getName())) {
						stype = attribute.getValue();
						if("Line".equalsIgnoreCase(stype)) {
							type = 0;
						}
						else if("Arrow".equalsIgnoreCase(stype)) {
						  	type = 1;
						}
					}
				} //end if attribute
			}//end while hasNext()
		   	
		   pathway.addLine(sx,sy,ex,ey, style, type, color);
		} //end else if Line
		
	   /*Here the program checks if the element is a "LineShape"
	   *Within this element "LineShape", the subelements "Graphics", "Comment", "Notes", and the attribute "Type" will be encountered
	   */		
		else if ("LineShape".equalsIgnoreCase(element.getName())) {
     		
		   /*In these variables the attributes of the subelement "Graphics" will be stored*/  
		   double sx = 0;
	      double sy = 0;
	      double ex = 0;
			double ey = 0;			
			/*In this variable the value of the attribute "Type" will be stored*/
			String stype;
						
			//Reading in subelements
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
						      else if("StartY".equalsIgnoreCase(attribute.getName())) {
						      	sy = Integer.parseInt(attribute.getValue());
						      } //end if starty
						      else if("EndX".equalsIgnoreCase(attribute.getName())) {
						      	ex = Integer.parseInt(attribute.getValue());
						      } //end if endx
						      else if("EndY".equalsIgnoreCase(attribute.getName())) {
						      	ey = Integer.parseInt(attribute.getValue());
						      } //end if endy
					      } //end if attribute					      
					   } //end while hasNext()
		        } //end if graphics
		        else if ("Comment".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Comment");
				  }//end if Comment
				  else if ("Notes".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Notes");
				   }//end if Notes
		      } //end if element
		   } //end while hasNext()
			
			//Reading in attributes
			List attributes = element.getAttributes();
			Iterator aiterator = attributes.iterator();
			while (aiterator.hasNext()) {
				Object att = aiterator.next();
				if (att instanceof Attribute) {
					Attribute attribute = (Attribute) att;
					
					/*The attribute "Type" can have five different values: "ReceptorRound", "ReceptorSquare", "LigandRound", "LigandSquare" or "Tbar"*/
					if("Type".equalsIgnoreCase(attribute.getName())) {
						stype = attribute.getValue();
						if("ReceptorRound".equalsIgnoreCase(stype)) {
							//System.out.println("ReceptorRound");
						}
						else if("ReceptorSquare".equalsIgnoreCase(stype)) {
						  	//System.out.println("ReceptorSquare");
						}
						else if("LigandRound".equalsIgnoreCase(stype)) {
						  	//System.out.println("LigandRound");
						}
						else if("LigandSquare".equalsIgnoreCase(stype)) {
						  	//System.out.println("LigandSquare");
						}
						else if("Tbar".equalsIgnoreCase(stype)) {
						  	//System.out.println("Tbar");
						}
					}//end if Type
				} //end if attribute
			}//end while hasNext()
		} //end else if LineShape
		
	   /*Here the program checks if the element is an "Arc"
	   *Within this element "Arc", the subelements "Graphics", "Comment" and "Notes"will be encountered
	   *"Arc" contains no direct attributes
	   */		
		else if ("Arc".equalsIgnoreCase(element.getName())) {
     		
		   /*In these variables the attributes of the subelement "Graphics" will be stored*/
		   double sx = 0;
	      double sy = 0;
	      double width = 0;
			double height = 0;
			double rotation = 0;
			String color="";
			
			//Reading in subelements
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
						      	sx = Double.parseDouble(attribute.getValue());
						      } //end if startx
						      else if("StartY".equalsIgnoreCase(attribute.getName())) {
						      	sy = Double.parseDouble(attribute.getValue());
						      } //end if starty
						      else if("Width".equalsIgnoreCase(attribute.getName())) {
						      	width = Double.parseDouble(attribute.getValue());
						      } //end if width
						      else if("Height".equalsIgnoreCase(attribute.getName())) {
						      	height = Double.parseDouble(attribute.getValue());
						      } //end if height
						      else if("Color".equalsIgnoreCase(attribute.getName())) {
						      	color = attribute.getValue();
							   } //end if color
							   else if("Rotation".equalsIgnoreCase(attribute.getName())) {
						      	rotation = Double.parseDouble(attribute.getValue());
							   } //end if Rotation
					      } //end if attribute					      
					   } //end while hasNext()
		        } //end if graphics
		        else if ("Comment".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Comment");
				  }//end if Comment
				  else if ("Notes".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Notes");
				   }//end if Notes
		      } //end if element
		   } //end while hasNext()
		   pathway.addArc(sx, sy, width, height, color);
		 }// end if Arc
		 
	   /*Here the program checks if the element is a "Label"
	   *Within this element "Label", the subelements "Graphics", "Comment" and "Notes" will be encountered
	   */		 
		else if ("Label".equalsIgnoreCase(element.getName())) {
			
		   /*In these variables the attributes of the subelement "Graphics" and the value of "TextLabel" will be stored*/
			String fname = "";
			String fstyle = "";
			String fweight = "";
			String text = "";
			String color = "";
			int cx = 0;
			int cy = 0;
			int x = 0;
			int y = 0;
			int h = 0;
			int w = 0;
			int fsize = 0;
			
			//Reading in attributes
			List attributes = element.getAttributes();
        	Iterator aiterator = attributes.iterator();
	      while (aiterator.hasNext()) {
	      	Object att = aiterator.next();
		      if (att instanceof Attribute) {
		      	Attribute attribute = (Attribute) att;
					if("TextLabel".equalsIgnoreCase(attribute.getName())) {
						text = attribute.getValue();
					}//end if TextLabel
				}//end if attribute
			}//end while hasNext()
			
			//Reading in subelements	
			List children = element.getContent();
      	Iterator iterator = children.iterator();
      	while (iterator.hasNext()) {
        		Object child = iterator.next();
        		if (child instanceof Element) {
		        Element subelement = (Element) child;
		        if("Graphics".equalsIgnoreCase(subelement.getName())) {
		        		List sattributes = subelement.getAttributes();
			        	Iterator saiterator = sattributes.iterator();
				      while (saiterator.hasNext()) {
				      	Object att = saiterator.next();
					      if (att instanceof Attribute) {
					      	Attribute attribute = (Attribute) att;
					      	if("CenterX".equalsIgnoreCase(attribute.getName())) {
						      	//System.out.println("CenterX");
							      cx = Integer.parseInt(attribute.getValue());
						      } //end if centerx
						      else if("CenterY".equalsIgnoreCase(attribute.getName())) {
						      	//System.out.println("CenterY");
							      cy = Integer.parseInt(attribute.getValue());
						      } //end if centery
						      else if("Width".equalsIgnoreCase(attribute.getName())) {
						      	//System.out.println("Width");
							      w = Integer.parseInt(attribute.getValue());
						      } //end if width
						      else if("Height".equalsIgnoreCase(attribute.getName())) {
						      	//System.out.println("Height");
							      h = Integer.parseInt(attribute.getValue());
						      } //end if height
						      else if("Color".equalsIgnoreCase(attribute.getName())) {
						      	//System.out.println("Color");
							      color = attribute.getValue();
						      } //end if fontname
						      else if("FontName".equalsIgnoreCase(attribute.getName())) {
						      	//System.out.println("FontName");
							      fname = attribute.getValue();
						      } //end if fontname
						      else if("FontStyle".equalsIgnoreCase(attribute.getName())) {
						      	//System.out.println("FontStyle");
							      fstyle = attribute.getValue();
						      } //end if fontstyle
						      else if("FontWeight".equalsIgnoreCase(attribute.getName())) {
						      	//System.out.println("FontWeight");
							      fweight = attribute.getValue();
						      } //end if fontweight
						      else if("FontSize".equalsIgnoreCase(attribute.getName())) {
						      	//System.out.println("FontSize");
							      fsize = Integer.parseInt(attribute.getValue());
						      } //end if fontsize
					      } //end if attribute					      
					   } //end while hasNext()
		        } //end if graphics
		        else if ("Comment".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Comment");
				  }//end if Comment
				  else if ("Notes".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Notes");
				   }//end if Notes
		      } //end if element
		   } //end while hasNext()
		   x = cx - (w/2);
		   y = cy - (h/2);
		   pathway.addLabel(x, y, w, h, text, color, fname, fweight, fstyle, fsize);
		 }// end if Label
		 
	   /*Here the program checks if the element is a "Shape"
	   *Within this element "Shape", the subelements "Graphics", "Notes", and the attribute "Type" will be encountered
	   */		 
		else if ("Shape".equalsIgnoreCase(element.getName())) {
     		
		   /*In these variables the attributes of the subelement "Graphics" and the value of the attribute "Type" will be stored*/		     
		   double cx = 0;
	      double cy = 0;
     		double x = 0;
	      double y = 0;
	      double w = 0;
			double h = 0;
     		double rotation = 0;
		   int type = 0;
	      String color = "";
			String stype;
						
			//Reading in subelements
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
					      	if("CenterX".equalsIgnoreCase(attribute.getName())) {
						      	cx = Double.parseDouble(attribute.getValue());
						      } //end if CenterX
						      else if("CenterY".equalsIgnoreCase(attribute.getName())) {
						      	cy = Double.parseDouble(attribute.getValue());
						      } //end if CenterY
						       else if("Width".equalsIgnoreCase(attribute.getName())) {
						      	w = Double.parseDouble(attribute.getValue());
						      } //end if Width
						      else if("Height".equalsIgnoreCase(attribute.getName())) {
						      	h = Double.parseDouble(attribute.getValue());
						      } //end if Height
						      else if("Color".equalsIgnoreCase(attribute.getName())) {
						      	color = attribute.getValue();
						      } //end if Color
						      else if("Rotation".equalsIgnoreCase(attribute.getName())) {
						      	rotation = Double.parseDouble(attribute.getValue());
						      } //end if Rotation
					      } //end if attribute					      
					   } //end while hasNext()
		        } //end if graphics
				  else if ("Notes".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Notes");
				   }//end if Notes
		      } //end if element
		   } //end while hasNext()
		   
		   //Reading in attributes
			List attributes = element.getAttributes();
			Iterator aiterator = attributes.iterator();
			while (aiterator.hasNext()) {
				Object att = aiterator.next();
				if (att instanceof Attribute) {
					Attribute attribute = (Attribute) att;

					if("Type".equalsIgnoreCase(attribute.getName())) {
						stype = attribute.getValue();
						if("Rectangle".equalsIgnoreCase(stype)) {
							type = 0;
						}//end if rectangle
						else if("Oval".equalsIgnoreCase(stype)) {
							type = 1;
						}//end if oval						
					}//end if type
				} //end if attribute
			}//end while hasNext()
			x = cx - w;
			y = cy - h;
			pathway.addShape(x, y, w, h, type, color, rotation);
		} //end else if Shape
		
	   /*Here the program checks if the element is a "CellShape"
	   *Within this element "CellShape", the subelements "Graphics" and "Notes" will be encountered
	   *"CellShape" contains no direct attributes
	   */		
		else if ("CellShape".equalsIgnoreCase(element.getName())) {
     		
		   /*In these variables the attributes of the subelement "Graphics" will be stored*/		     
		   double cx = 0;
	      double cy = 0;
     		double x = 0;
	      double y = 0;
	      double w = 0;
			double h = 0;
     		double rotation = 0;
						
			//Reading in subelements
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
					      	if("CenterX".equalsIgnoreCase(attribute.getName())) {
						      	cx = Double.parseDouble(attribute.getValue());
						      } //end if CenterX
						      else if("CenterY".equalsIgnoreCase(attribute.getName())) {
						      	cy = Double.parseDouble(attribute.getValue());
						      } //end if CenterY
						      else if("Width".equalsIgnoreCase(attribute.getName())) {
						      	w = Double.parseDouble(attribute.getValue());
						      } //end if Width
						      else if("Height".equalsIgnoreCase(attribute.getName())) {
						      	h = Double.parseDouble(attribute.getValue());
						      } //end if height
						      else if("Rotation".equalsIgnoreCase(attribute.getName())) {
						      	rotation = Double.parseDouble(attribute.getValue());
						      } //end if Rotation
					      } //end if attribute					      
					   } //end while hasNext()
		        } //end if graphics
				  else if ("Notes".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Notes");
				   }//end if Notes
		      } //end if element
		   } //end while hasNext()
			x = cx - w;
			y = cy - h;
			//pathway.addCellShape(x, y, w, h, rotation);
		} //end else if CellShape
		
	   /*Here the program checks if the element is a "Brace"
	   *Within this element "Brace", the subelements "Graphics" and "Notes" will be encountered
	   *"Brace" contains no direct attributes
	   */		
		else if ("Brace".equalsIgnoreCase(element.getName())) {
     		
		   /*In these variables the attributes of the subelement "Graphics" will be stored*/	     
		   double sx = 0;
	      double sy = 0;
	      double width = 0;
			double PicPointOffset = 0;
			int orientation=0;	
			String color="";
						
			//Reading in subelements
			List children = element.getContent();
      	Iterator iterator = children.iterator();
      	while (iterator.hasNext()) {
	      	Object child = iterator.next();
        		if (child instanceof Element) {
		        Element subelement = (Element) child;
		        if("Graphics".equalsIgnoreCase(subelement.getName())) {
		        		List attributes = subelement.getAttributes();
			        	Iterator aiterator = attributes.iterator();
			         System.out.println("Checking for graphics attributes");
				      while (aiterator.hasNext()) {
				      	Object att = aiterator.next();
					      if (att instanceof Attribute) {
					      	Attribute attribute = (Attribute) att;
					      	if("CenterX".equalsIgnoreCase(attribute.getName())) {
						      	sx = Double.parseDouble(attribute.getValue());
						      } //end if Centerx
						      else if("CenterY".equalsIgnoreCase(attribute.getName())) {
						      	sy = Double.parseDouble(attribute.getValue());
						      } //end if Centery
						      else if("Width".equalsIgnoreCase(attribute.getName())) {
						      	width = Double.parseDouble(attribute.getValue());
						      } //end if Width
						      else if("PicPointOffset".equalsIgnoreCase(attribute.getName())) {
						      	PicPointOffset = Double.parseDouble(attribute.getValue());
						      } //end if PicPointOffset
						      else if("Color".equalsIgnoreCase(attribute.getName())) {
						      	color = attribute.getValue();
							   }
						      else if("Orientation".equalsIgnoreCase(attribute.getName())) {
									if("top".equalsIgnoreCase(attribute.getValue())) {
										orientation=0;
									}
									else if("right".equalsIgnoreCase(attribute.getValue())) {
										orientation=1;
									}
									else if("bottom".equalsIgnoreCase(attribute.getValue())) {
										orientation=2;
									}
									else if("left".equalsIgnoreCase(attribute.getValue())) {
										orientation=3;
									}
						      } //end if orientation						      
					      } //end if attribute					      
					   } //end while hasNext()
		        } //end if graphics
				  else if ("Notes".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Notes");
				   }//end if Notes
		      } //end if element
		   } //end while hasNext()
		System.out.println("centerX: "+sx/15+" centerY: "+sy/15+" ppo: "+PicPointOffset/15+" orientation: "+orientation);
		pathway.addBrace(sx/15, sy/15, width/15, PicPointOffset/15, orientation, color);
		System.out.println("a brace is added");   
		} //end else if Brace				 

		/*Here the program checks if the element is a "CellComponent"
	   *Within this element "CellComponent", the subelements "Graphics", "Notes" and the attribute "Type" will be encountered
	   */
		else if ("CellComponent".equalsIgnoreCase(element.getName())) {
     		
		   /*In these variables the attributes of the subelement "Graphics" and the value of the attribute "Type" will be stored*/   		     
		   double cx = 0;
	      double cy = 0;
		   int type = 0;
			String stype;
			
			//Reading in subelements			
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
					      	if("CenterX".equalsIgnoreCase(attribute.getName())) {
						      	cx = Double.parseDouble(attribute.getValue());
						      } //end if CenterX
						      else if("CenterY".equalsIgnoreCase(attribute.getName())) {
						      	cy = Double.parseDouble(attribute.getValue());
						      } //end if CenterY
					      } //end if attribute					      
					   } //end while hasNext()
		        } //end if graphics
				  else if ("Notes".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Notes");
				   }//end if Notes
		      } //end if element
		   } //end while hasNext()
			
			//Reading in subelements
			List attributes = element.getAttributes();
			Iterator aiterator = attributes.iterator();
			while (aiterator.hasNext()) {
				Object att = aiterator.next();
				if (att instanceof Attribute) {
					Attribute attribute = (Attribute) att;

					if("Type".equalsIgnoreCase(attribute.getName())) {
						stype = attribute.getValue();
						if("OrganA".equalsIgnoreCase(stype)) {
							type = 0;
							//System.out.println("OrganA");
						}//end if OrganA
						else if("OrganB".equalsIgnoreCase(stype)) {
							type = 1;
						  	//System.out.println("OrganB");
						}//end if OrganB
						else if("OrganC".equalsIgnoreCase(stype)) {
							type = 2;
						  	//System.out.println("OrganC");
						}//end if OrganC
						else if("Ribosome".equalsIgnoreCase(stype)) {
							type = 3;
						  	//System.out.println("Ribosome");
						}//end if Ribosome						
					}//end if type
				} //end if attribute
			}//end while hasNext()
		} //end else if CellComponent
		
		/*Here the program checks if the element is a "ProteinComplex"
	   *Within this element "ProteinComplex", the subelements "Graphics" and "Notes"
	   *"ProteinComplex" contains no attributes
	   */		
		else if ("ProteinComplex".equalsIgnoreCase(element.getName())) {
     		
		   /*In these variables the attributes of the subelement "Graphics" will be stored*/		     
		   double cx = 0;
     		double cy = 0;
	      double w = 0;
						
			//Reading in subelements
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
					      	if("CenterX".equalsIgnoreCase(attribute.getName())) {
						      	cx = Double.parseDouble(attribute.getValue());
						      } //end if CenterX
						      else if("CenterY".equalsIgnoreCase(attribute.getName())) {
						      	cy = Double.parseDouble(attribute.getValue());
						      } //end if CenterY
						      else if("Width".equalsIgnoreCase(attribute.getName())) {
						      	w = Double.parseDouble(attribute.getValue());
						      } //end if Width
					      } //end if attribute					      
					   } //end while hasNext()
		        } //end if graphics
				  else if ("Notes".equalsIgnoreCase(subelement.getName())) {
      				//System.out.println("Notes");
				   }//end if Notes
		      } //end if element
		   } //end while hasNext()
		} //end else if ProteinComplex
		 
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
