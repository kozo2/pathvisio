import org.jdom.JDOMException;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.*;
import org.jdom.Element;
import org.jdom.Attribute;
import java.io.IOException;
import java.util.*;

/**
*	This class handles GMML file-input
*/

public class GmmlReader
{
	private GmmlPathway pathway;
	
	/**
	*	Constructor for this class
	*	<BR>
	*	<DL><B>Parameters</B>
	*	<DD>String File - File that has to be read
	*	</DL>	
	*/
	public GmmlReader(String file)
	{
		// Create the pathway
		pathway = new GmmlPathway();
		
		System.out.println("Start reading the XML file: " + file);
		SAXBuilder builder  = new SAXBuilder(false);
		
		// try to read the file; if an error occurs, catch the exception and print feedback
		try
		{
			Document doc = builder.build(file);
			
			// if no error occurs, the file is valid
			System.out.println(file +"is valid");
			
			// now, we can start to read the GMML
			// start with depth 0
			readGMML(doc, 0);
		}		
		catch(JDOMException e)
		{
			System.out.println(file + " is invalid.");
			System.out.println(e.getMessage());
		}
		catch(IOException e)
		{
			System.out.println("Could not check " + file);
			System.out.println(e.getMessage());
		}	
	}// end public GmmlReader(String file)
	
	// method to question private property pathway
	public GmmlPathway getPathway()
	{
		return pathway;
	} // end public GmmlPathway getPathway()

	
	// Method to resize an array
	// When appending an element to an array, first resize it.
	private static Object resizeArray (Object oldArray, int newSize)
	{
		int oldSize = java.lang.reflect.Array.getLength(oldArray);
		Class elementType = oldArray.getClass().getComponentType();
		Object newArray = java.lang.reflect.Array.newInstance(elementType,newSize);
		int preserveLength = Math.min(oldSize,newSize);
		
		if (preserveLength > 0)
		{
			System.arraycopy(oldArray,0,newArray,0,preserveLength);
		}
		return newArray; 
	} // end private static Object resizeArray (Object oldArray, int newSize)

	private void readGMML(Object o, int depth)
	{
		// check wether the argument o is an xml Element
		if (o instanceof Element)
		{
			// convert 0 to Element
			Element element = (Element) o; 
			System.out.println("Element: " + element.getName());
			// now we know o is an xml Element, we can checkout 
			// the pathway it is describing
			readPathway(element);
		}
		
		// check wether the argument o is  a document
		else if (o instanceof Document)
		{
			// convert o to Document
			Document doc = (Document) o;
			System.out.println("Document");
			
			// The document is an .xml-document consisting of elements.
			//	Get the elements and execute readGMML for each 
			// element in the list obtained
			List childelements = doc.getContent();
			Iterator it = childelements.iterator();
			while (it.hasNext())
			{
				Object child = it.next();
				readGMML(child, depth ++);
			}
		}
		else // worst case...
		{
			System.out.println("Unexpected type: " + o.getClass());
		}
		
		System.out.println("Reached end of document");
	} // end public void checkGMML(Object o, int depth)
	
	// Method to read pathway attributes; when the attributes are read, 
	// the child elements are identified and added to the pathway object
	private void readPathway(Element e)
	{
		String name = e.getName();

		// The string array pathwayAttributes containes all known pathway attributes
		String[] pathwayAttributes = {"name", "organism", "data-source", "version", "author",
			"maintained-by", "email", "availbability", "last-modified"};

		// check if we're reading a pathway
		if (name.equalsIgnoreCase("pathway"))
		{
			System.out.println("Found a pathway, extracting data...");
			
			// Get the attributes of this pathway and check their validity
			List attributes = e.getAttributes();
			Iterator it = attributes.iterator();
			
			while (it.hasNext())
			{
				// check out next object
				Object o = it.next();
				if(o instanceof Attribute)
				{
					// the object is an pathway attribute
					Attribute a = (Attribute) o;
					String aName = a.getName();
					String aValue = a.getValue();
					// determine whether a is a known attribute
					boolean  aknown = false;
					for (int i = 0; ((i < pathwayAttributes.length) && (!aknown)); i++)
					{
						if (aName.equalsIgnoreCase(pathwayAttributes[i]))
						{
							// attribute is known
							aknown = true;
							// add attribute
							pathway.addAttribute(aName, aValue);
						
						} // end if
					} // end for
					if (!aknown)
					{
						// unknown attribute found
						System.out.println("Ignored unknown an attribute! Attribute name: " + aName + "; value: " +  aValue);
					} // end if
					
				} // end if(o instanceof Attribute)
			} // end while (it.hasNext())
			
			// get child objects and readGMML for each of them
			List children = e.getContent();
			it = children.iterator();
		
			while (it.hasNext())
			{
				//check out next object
				Object o = it.next();
				readPathwayChilds(o, 1);
			}
			
			// now, all pathway data is extracted
			System.out.println("All data extracted, done...");
			
		} // end if (name.equalsIgnoreCase("pathway"))
		
		else // first object is not a pathway
		{
			System.out.println("Unsupported first level element found!");
		}
	} // end private void readPathway(Element e)

	private void readPathwayChilds(Object o, int depth)
	{
		// check if the object is an xml Element
		if (o instanceof Element)
		{
			Element e = (Element) o;
			String eName = e.getName();
			
			// Check element name; if it corresponds to a known gmml element get element attributes
			// Check the known gmml elements in this order: 
			// Graphics, GeneProduct, Line, LineShape, Arc, Label, Shape, CellShape, Brace, CellComponent, ProteinComplex
			
			if("graphics".equalsIgnoreCase(eName))
			{
				int[] pathwaydims = checkGraphicsAttributes(e);
				pathway.setSize(pathwaydims[0], pathwaydims[1]);
				System.out.println("Pathway dimensions set to " + pathwaydims[0] + ", " + pathwaydims[1]);
			}
			else if ("geneproduct".equalsIgnoreCase(eName))
			{
				checkGeneproductAttributes(e);		
			}
			else if ("line".equalsIgnoreCase(eName))
			{
				checkLineAttributes(e);
			}
			else if ("lineshape".equalsIgnoreCase(eName))
			{
				checkLineShapeAttributes(e);
			}
			else if ("arc".equalsIgnoreCase(eName))
			{
				checkArcAttributes(e);
			}
			else if ("label".equalsIgnoreCase(eName))
			{
				checkLabelAttributes(e);
			}
			else if ("shape".equalsIgnoreCase(eName))
			{
				checkShapeAttributes(e);			
			}
			else if ("cellshape".equalsIgnoreCase(eName))
			{
				checkCellShapeAttributes(e); // to be implemented!
			}
			else if ("brace".equalsIgnoreCase(eName))
			{
				checkBraceAttributes(e);
			}
			else if ("cellcomponent".equalsIgnoreCase(eName))
			{
				checkCellComponentAttributes(e);	// to be implemented!		
			}			
			else if ("proteincomplex".equalsIgnoreCase(eName))
			{
				checkProteinComplexAttributes(e);// to be implemented!			
			}			
						
		} // end if (o instanceof Element)

			
			
	} // end private void readPathwayChilds(Object o, int depth)
	
	// method to check graphics attributes
	// graphics attributes are boardwidth and boardheight, which are returned
	private int[] checkGraphicsAttributes(Element e)
	{
		List attributes = e.getAttributes();
		Iterator it = attributes.iterator();
		
		int width = 0;
		int height = 0;
		
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute a = (Attribute) o;
				String aName = a.getName();
				String aValue = a.getValue();
				
				if ("boardwidth".equalsIgnoreCase(aName))
				{
					width = Integer.parseInt(aValue);
				} // end if
				else if ("boardheight".equalsIgnoreCase(aName))
				{
					height = Integer.parseInt(aValue);
				} // end else if
			} // end if
		} // end while

		int[] dim = {width, height};		
		return dim;
	} // private int[] checkGraphicsAttributes(Element e)

	// method to check geneproduct attributes
	private void checkGeneproductAttributes(Element e)
	{
		String ref = "";
		String geneID = "";
		
		List a = e.getAttributes();
		Iterator it = a.iterator();
		while(it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute at = (Attribute) o;
				String aName = at.getName();
				String aValue = at.getValue();
				
				if ("geneid".equalsIgnoreCase(aName))
				{
					geneID = aValue;
				}
				else if ("type".equalsIgnoreCase(aName))
				{
					// to be implemented				
				}
				else if ("geneproduct-data-source".equalsIgnoreCase(aName))
				{
					// to be implemented
				}
				else if ("xref".equalsIgnoreCase(aName))
				{
					ref = aValue;
				}
				else if ("backpagehead".equalsIgnoreCase(aName))
				{
					// to be implemented				
				}
			} // end if
		} // end while
		
		// the geneproduct element contains more elements itself
		// obtain child elements and check names
		// possible child elements are graphics, comment and notes
		
		List child = e.getContent();
		it = child.iterator();

		while (it.hasNext())
		{
			Object o = it.next();
			
			// check if child element is an xml Element
			if (o instanceof Element)
			{
				Element el = (Element) o;
				String eName = el.getName();
				
				if ("graphics".equalsIgnoreCase(eName))
				{
					checkGeneProductGraphicsAttributes(ref, geneID, el);
				}
				else if ("comment".equalsIgnoreCase(eName))
				{
					// to be implemented
				}
				else if ("notes".equalsIgnoreCase(eName))
				{
					// to be implemented
				}
			} // end if
		} // end while
		
		
	} // end private void checkGeneproductAttributes(Element e)
	
	
	// method...
	private void checkGeneProductGraphicsAttributes(String ref, String geneID, Element e)
	{
			int x = 0;
			int y = 0;
			int cx = 0;
			int cy = 0;
			int width = 0;
			int height = 0;
			
			List alist = e.getAttributes();
			Iterator it = alist.iterator();
			while (it.hasNext())
			{
				Object o = it.next();
				if (o instanceof Attribute)
				{
					Attribute a = (Attribute) o;
					String aName = a.getName();
					String aValue = a.getValue();
					
					if ("centerx".equalsIgnoreCase(aName))
					{
						cx = Integer.parseInt(aValue);
					}
					else if ("centery".equalsIgnoreCase(aName))
					{
						cy = Integer.parseInt(aValue);
					}
					else if ("width".equalsIgnoreCase(aName))
					{
						width = Integer.parseInt(aValue);
					}
					else if ("height".equalsIgnoreCase(aName))
					{
						height = Integer.parseInt(aValue);
					}
				} // end if
			} // end while
			
			// graphics wil be defined from left upper corner
			x = cx - (width/2);
			y = cy - (height/2);
			
			// now, a gene product can be added to the pathway
			pathway.addGeneProduct(x, y, width, height, geneID, ref);
			
	} // end private void checkGeneProductSubGraphics(String ref, String geneID)

	private void checkLineAttributes(Element e)
	{
		int style = 0;
		int type  = 0;
		
		List alist = e.getAttributes();
		Iterator it = alist.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute a = (Attribute) o;
				String aName = a.getName();
				String aValue = a.getValue();
				if ("style".equalsIgnoreCase(aName))
				{
					if ("solid".equalsIgnoreCase(aValue))
					{
						style = 0;						
					}
					else if ("broken".equalsIgnoreCase(aValue))
					{
						style = 1;
					}
				} // end if style
				else if ("type".equalsIgnoreCase(aName))
				{
					if ("line".equalsIgnoreCase(aValue))
					{
						type = 0;
					}
					else if ("arrow".equalsIgnoreCase(aValue))
					{
						type = 1;
					}					
				} // end else if type
			} // end if o instance of 
		} // end while it.hasNext
		
		// a line element has sub elements; check them
		List children = e.getContent();
		it = children.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Element)
			{			
				Element sube = (Element) o;
				String subeName = sube.getName();
				
				if ("graphics".equalsIgnoreCase(subeName))
				{	
					checkLineGraphicsAttributes(style, type, sube);
				} // end if "graphics..."
				else if ("comment".equalsIgnoreCase(subeName))
				{
					// to be implemented
				}
				else if ("notes".equalsIgnoreCase(subeName))
				{
					// to be implemented
				}
			} // end if o instance of...
		} // end while
		
	} // end private void checkLineAttributes(Element e)

	private void checkLineGraphicsAttributes(int style, int type, Element e)
	{
		int sx = 0;
		int sy = 0;
		int ex = 0;
		int ey = 0;

		String color = "";
		
		List alist = e.getAttributes();
		Iterator it = alist.iterator();
		
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute a = (Attribute) o;
				String aName = a.getName();
				String aValue = a.getValue();
										
				if ("startx".equalsIgnoreCase(aName))
				{
					sx = Integer.parseInt(aValue);
				}
				else if ("starty".equalsIgnoreCase(aName))
				{
					sy = Integer.parseInt(aValue);
				}
				else if ("endx".equalsIgnoreCase(aName))
		 		{
					ex = Integer.parseInt(aValue);
				}
				else if ("endy".equalsIgnoreCase(aName))
				{
					ey = Integer.parseInt(aValue);
				}
				else if ("color".equalsIgnoreCase(aName))
				{
					color = aValue;
				}
			} // end if 
		}// end while
		
		// line attributes complete, add line to pathway
		pathway.addLine(sx, sy, ex, ey, style, type, color);
		
	} // end private void checkLineGraphicsAttributes()

	private void checkLineShapeAttributes(Element e)
	{
		int type  = 0;
		
		List alist = e.getAttributes();
		Iterator it = alist.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute a = (Attribute) o;
				String aName = a.getName();
				String aValue = a.getValue();
				if ("type".equalsIgnoreCase(aName))
				{
					if ("tbar".equalsIgnoreCase(aValue))
					{
						type = 0;						
					}
					else if ("receptorbound".equalsIgnoreCase(aValue))
					{
						type = 1;
					}
					else if ("ligandbound".equalsIgnoreCase(aValue))
					{
						type = 2;
					}
					else if ("receptorsquare".equalsIgnoreCase(aValue))
					{
						type = 3;
					}
					else if ("ligandsquare".equalsIgnoreCase(aValue))
					{
						type = 4;
					}
				} // end if style
			} // end if o instance of 
		} // end while it.hasNext
		
		// a lineshape element has sub elements; check them
		List children = e.getContent();
		it = children.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Element)
			{			
				Element sube = (Element) o;
				String subeName = sube.getName();
				
				if ("graphics".equalsIgnoreCase(subeName))
				{	
					checkLineShapeGraphicsAttributes(type, sube);
				} // end if "graphics..."
				else if ("comment".equalsIgnoreCase(subeName))
				{
					// to be implemented
				}
				else if ("notes".equalsIgnoreCase(subeName))
				{
					// to be implemented
				}
			} // end if o instance of...
		} // end while
	
	} // end private void checkLineShapeAttributes(e)
	
	private void checkLineShapeGraphicsAttributes(int type, Element e)
	{
		double sx = 0;
		double sy = 0;
		double ex = 0;
		double ey = 0;

		String color = "";
		
		List alist = e.getAttributes();
		Iterator it = alist.iterator();
		
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute a = (Attribute) o;
				String aName = a.getName();
				String aValue = a.getValue();
										
				if ("startx".equalsIgnoreCase(aName))
				{
					sx = Double.parseDouble(aValue);
				}
				else if ("starty".equalsIgnoreCase(aName))
				{
					sy = Double.parseDouble(aValue);
				}
				else if ("endx".equalsIgnoreCase(aName))
		 		{
					ex = Double.parseDouble(aValue);
				}
				else if ("endy".equalsIgnoreCase(aName))
				{
					ey = Double.parseDouble(aValue);
				}
				else if ("color".equalsIgnoreCase(aName))
				{
					color = aValue;
				}
			} // end if 
		}// end while
		
		// line attributes complete, add lineshape to pathway
		pathway.addLineShape(sx, sy, ex, ey, color, type);

	} // end private void checkLineShapeGraphicsAttributes(style, sube)

	private void checkArcAttributes(Element e)
	{
		List childlist = e.getContent();
		Iterator childit = childlist.iterator();
		
		while (childit.hasNext())
		{
			Object oc = childit.next();
			if (oc instanceof Element)
			{
				Element el = (Element) oc;
				String eName = el.getName();
								
				if ("graphics".equalsIgnoreCase(eName))
				{
					checkArcGraphicsAttributes(el);
				} // end if graphics
				else if ("comment".equalsIgnoreCase(eName)) 
				{
					// to be implemented
				}//end if Comment
				else if ("notes".equalsIgnoreCase(eName)) 
				{	
					// to be implemented
				}
			} // end if
		} // end while
			
	} // end private void checkArcAttributes(Element e)

	private void checkArcGraphicsAttributes(Element e)
	{
		double sx = 0;
		double sy = 0;
		double width = 0;
		double height = 0;
		double rotation = 0;
		
		String color = "";
		
		List alist = e.getAttributes();
		Iterator it = alist.iterator();
		
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute a = (Attribute) o;
				String aName = a.getName();
				String aValue = a.getValue();
										
				if ("startx".equalsIgnoreCase(aName))
				{
					sx = Double.parseDouble(aValue);
				}
				else if ("starty".equalsIgnoreCase(aName))
				{
					sy = Double.parseDouble(aValue);
				}
				else if ("width".equalsIgnoreCase(aName))
		 		{
					width = Double.parseDouble(aValue);
				}
				else if ("height".equalsIgnoreCase(aName))
				{
					height = Double.parseDouble(aValue);
				}
				else if ("color".equalsIgnoreCase(aName))
				{
					color = aValue;
				}
				else if ("rotation".equalsIgnoreCase(aName))
				{
					rotation = Double.parseDouble(aValue);
				}				
			} // end if 
		}// end while
		
		// arc attributes complete, add arc to pathway
		pathway.addArc(sx, sy, width, height, color, rotation);

	} // end private void checkArcGraphicsAttributes(Element e)
											
	private void checkLabelAttributes(Element e)
	{
		String text = "";
		
		List alist = e.getAttributes();
		Iterator it = alist.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute a = (Attribute) o;
				String aName = a.getName();
				String aValue = a.getValue();
				if ("textlabel".equalsIgnoreCase(aName))
				{
					text = aValue;
				} // end if 
			} // end if o instance of 
		} // end while it.hasNext

		// a label element has sub elements; check them
		List children = e.getContent();
		it = children.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Element)
			{			
				Element sube = (Element) o;
				String subeName = sube.getName();
				
				if ("graphics".equalsIgnoreCase(subeName))
				{	
					checkLabelGraphicsAttributes(text, sube);
				} // end if "graphics..."
				else if ("comment".equalsIgnoreCase(subeName))
				{
					// to be implemented
				}
				else if ("notes".equalsIgnoreCase(subeName))
				{
					// to be implemented
				}
			} // end if o instance of...
		} // end while
				
	} // end private void checkLabelAttributes(Element e)
	
	private void checkLabelGraphicsAttributes(String text, Element e)
	{
		String fontname = "";
		String fontstyle = "";
		String fontweight = "";
		String color = "";
		
		int fontsize = 0;		
		int cx = 0;
		int cy = 0;
		int width = 0;
		int height = 0;
		int rotation = 0;
				
		List alist = e.getAttributes();
		Iterator it = alist.iterator();
		
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute a = (Attribute) o;
				String aName = a.getName();
				String aValue = a.getValue();
										
				if ("centerx".equalsIgnoreCase(aName))
				{
					cx = Integer.parseInt(aValue);
				}
				else if ("centery".equalsIgnoreCase(aName))
				{
					cy = Integer.parseInt(aValue);
				}
				else if ("width".equalsIgnoreCase(aName))
		 		{
					width = Integer.parseInt(aValue);
				}
				else if ("height".equalsIgnoreCase(aName))
				{
					height = Integer.parseInt(aValue);
				}
				else if ("color".equalsIgnoreCase(aName))
				{
					color = aValue;
				}
				else if ("fontname".equalsIgnoreCase(aName))
				{
					fontname = aValue;
				}
				else if ("fontstyle".equalsIgnoreCase(aName))
				{
					fontstyle = aValue;
				}
				else if ("fontweight".equalsIgnoreCase(aName))
				{
					fontweight = aValue;
				}
				else if ("fontsize".equalsIgnoreCase(aName))
				{
					fontsize = Integer.parseInt(aValue);
				}				
			} // end if 
		}// end while
		
		int x = cx - (width/2);
		int y = cy - (height/2);
		// arc attributes complete, add arc to pathway
		pathway.addLabel(x, y, width, height, text, color, fontname, fontweight, fontstyle, fontsize);
		
	}
	
	private void checkShapeAttributes(Element e)
	{
		int type = 0;
		
		List alist = e.getAttributes();
		Iterator it = alist.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute a = (Attribute) o;
				String aName = a.getName();
				String aValue = a.getValue();
				
				if ("type".equalsIgnoreCase(aName))
				{
					if ("rectangle".equalsIgnoreCase(aValue))
					{
						type = 0;
					}
					else if ("oval".equalsIgnoreCase(aValue))
					{
						type = 1;
					}
				} // end if 
			} // end if o instance of 
		} // end while it.hasNext

		// a shape element has sub elements; check them
		List children = e.getContent();
		it = children.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Element)
			{			
				Element sube = (Element) o;
				String subeName = sube.getName();
				
				if ("graphics".equalsIgnoreCase(subeName))
				{	
					checkShapeGraphicsAttributes(type, sube);
				} // end if "graphics..."
				else if ("comment".equalsIgnoreCase(subeName))
				{
					// to be implemented
				}
				else if ("notes".equalsIgnoreCase(subeName))
				{
					// to be implemented
				}
			} // end if o instance of...
		} // end while
		
	} // end private void checkShapeAttributes(Element e)

	private void checkShapeGraphicsAttributes(int type, Element e)
	{
		double cx = 0;
		double cy = 0;
		double width = 0;
		double height = 0;
		double rotation = 0;
		
		String color = "";
		
		List alist = e.getAttributes();
		Iterator it = alist.iterator();
		
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute a = (Attribute) o;
				String aName = a.getName();
				String aValue = a.getValue();
										
				if ("centerx".equalsIgnoreCase(aName))
				{
					cx = Double.parseDouble(aValue);
				}
				else if ("centery".equalsIgnoreCase(aName))
				{
					cy = Double.parseDouble(aValue);
				}
				else if ("width".equalsIgnoreCase(aName))
		 		{
					width = Double.parseDouble(aValue);
				}
				else if ("height".equalsIgnoreCase(aName))
				{
					height = Double.parseDouble(aValue);
				}
				else if ("color".equalsIgnoreCase(aName))
				{
					color = aValue;
				}
				else if ("rotation".equalsIgnoreCase(aName))
				{
					rotation = Double.parseDouble(aValue);
				}				
			} // end if 
		}// end while

		double x = cx - width;
		double y = cy - height;
		
		// arc attributes complete, add arc to pathway
		pathway.addShape(x, y, width, height, type, color, rotation);

	} // end private void checkShapeGraphicsAttributes(int type, Element e)

	private void checkCellShapeAttributes(Element e)
	{
		// to be implemented!	
	} // end private void checkCellShapeAttributes(Element e)
	
	private void checkBraceAttributes(Element e)
	{
		// a brace element has sub elements; check them
		List children = e.getContent();
		Iterator it = children.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Element)
			{			
				Element sube = (Element) o;
				String subeName = sube.getName();
				
				if ("graphics".equalsIgnoreCase(subeName))
				{	
					checkBraceGraphicsAttributes(sube);
				} // end if "graphics..."
				else if ("comment".equalsIgnoreCase(subeName))
				{
					// to be implemented
				}
				else if ("notes".equalsIgnoreCase(subeName))
				{
					// to be implemented
				}
			} // end if o instance of...
		} // end while

	} // end private void checkBraceAttributes(Element e)

	private void checkBraceGraphicsAttributes(Element e)
	{
		double cx = 0;
		double cy = 0;
		double width = 0;
		double height = 0;
		double picpointOffset = 0;
		
		int orientation = 0;
			
		String color = "";
		
		List alist = e.getAttributes();
		Iterator it = alist.iterator();
		
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute a = (Attribute) o;
				String aName = a.getName();
				String aValue = a.getValue();
										
				if ("centerx".equalsIgnoreCase(aName))
				{
					cx = Double.parseDouble(aValue);
				}
				else if ("centery".equalsIgnoreCase(aName))
				{
					cy = Double.parseDouble(aValue);
				}
				else if ("width".equalsIgnoreCase(aName))
		 		{
					width = Double.parseDouble(aValue);
				}
				else if ("height".equalsIgnoreCase(aName))
				{
					height = Double.parseDouble(aValue);
				}
				else if ("color".equalsIgnoreCase(aName))
				{
					color = aValue;
				}
				else if ("picpointoffset".equalsIgnoreCase(aName))
				{
					picpointOffset = Double.parseDouble(aValue);
				}
				else if ("color".equalsIgnoreCase(aName))
				{
					color = aValue;
				}
				else if ("orientation".equalsIgnoreCase(aName))
				{
					if ("top".equalsIgnoreCase(aValue))
					{
						orientation = 0;
					}
					else if ("right".equalsIgnoreCase(aValue))
					{
						orientation = 1;
					}
					else if ("bottom".equalsIgnoreCase(aValue))
					{
						orientation = 2;
					}
					else if ("top".equalsIgnoreCase(aValue))
					{	
						orientation = 3;
					}
				}									
			} // end if 
		}// end while

		// brace attributes complete, add arc to pathway
		pathway.addBrace(cx, cy, width, picpointOffset, orientation, color);

	} // end private void checkBraceGraphicsAttributes(Element e)
	
	
	private void checkCellComponentAttributes(Element e)
	{
		int type = 0;
		
		List alist = e.getAttributes();
		Iterator it = alist.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Attribute)
			{
				Attribute a = (Attribute) o;
				String aName = a.getName();
				String aValue = a.getValue();
				
				if ("type".equalsIgnoreCase(aName))
				{
					if ("organc".equalsIgnoreCase(aValue))
					{
						type = 0;
					}
					else if ("organb".equalsIgnoreCase(aValue))
					{
						type = 1;
					}
					else if ("organc".equalsIgnoreCase(aValue))
					{
						type = 2;
					}
					else if ("ribosome".equalsIgnoreCase(aValue))
					{
						type = 2;
					}
				} // end if 
			} // end if o instance of 
		} // end while it.hasNext

		// a label element has sub elements; check them
		List children = e.getContent();
		it = children.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Element)
			{			
				Element sube = (Element) o;
				String subeName = sube.getName();
				
				if ("graphics".equalsIgnoreCase(subeName))
				{	
					checkCellComponentGraphicsAttributes(type, sube);
				} // end if "graphics..."
				else if ("comment".equalsIgnoreCase(subeName))
				{
					// to be implemented
				}
				else if ("notes".equalsIgnoreCase(subeName))
				{
					// to be implemented
				}
			} // end if o instance of...
		} // end while

	} // end private void checkCellComponentAttributes(Element e)

	private void checkCellComponentGraphicsAttributes(int type, Element e)
	{
		// to be implemented
		// cellshape object has not been implemented yet!	
		
	} // end private void checkCellComponentGraphicsAttributes(int type, Element e)
	
	private void checkProteinComplexAttributes(Element e)
	{
		// to be implemented!
		// proteincomplex object has not been implemented yet!
	} // end private void checkProteinComplexAttributes(Element e)
	
} // end of class GmmlReader2
