/*
Copyright 2005 H.C. Achterberg, R.M.H. Besseling, I.Kaashoek, 
M.M.Palm, E.D Pelgrim, BiGCaT (http://www.BiGCaT.unimaas.nl/)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and 
limitations under the License.
*/

import java.util.Vector;
import org.jdom.Attribute;

/** GmmlPathway 
  */
public class GmmlPathway {
	
	// pathway size...
	int width;
	int heigth;
	
	// pathway graphics...
	Vector geneProducts 	= new Vector();
	Vector lines 			= new Vector();
	Vector lineshapes 	= new Vector();
	Vector labels 			= new Vector();
	Vector arcs 			= new Vector();
	Vector braces 			= new Vector();
	Vector shapes			= new Vector();
		
	//Attributes + notes element + comment element
	Vector attributes 	= new Vector();
	String notes			= new String();
	String comment 		= new String();

	/** setNotes sets the attribute notes of a specified object.
	  */
	public void setNotes(String notes)
	{
		this.notes = notes;
	}

	/** setComment sets the attribute Comment of a specified object.
	  */
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	
	/** addAttribute adds an attribute to the array of string attributes.
	  * <BR>
	  * <DL><B>Parameters</B>
	  * <DD>Attribute a - the attribute to add
	  * </DL>
	  */
	public void addAttribute(Attribute a)
	{
		attributes.addElement(a);
	}

	/** addGeneProduct adds a geneproduct to the array of geneproducts.
	  * <BR>
	  * <DL><B>Parameters</B>
	  * <DD> GmmlGeneProduct gp - the GmmLGeneProduct to add
	  * </DL>
	  */	
	public void addGeneProduct(GmmlGeneProduct gp)
	{
		geneProducts.addElement(gp);
	}

	/** addLine adds a line to the array of lines.
	  * <BR>
	  * <DL><B>Parameters</B>
	  * <DD>GmmlLine - the GmmlLine to add
	  * </DL>
	  */		
	public void addLine(GmmlLine l)
	{
		lines.addElement(l);
	}

	/** addLabel adds a label to the array of labels.
	  * <BR>
	  * <DL><B>Parameters</B>
	  * <DD>GmmlLabel l - the GmmlLabel to add
	  * </DL>
	  */		
	public void addLabel(GmmlLabel l)
	{
		labels.addElement(l);
	}
	
	/** addArc adds an arc to the array of arcs.
	  * <BR>
	  * <DL><B>Parameters</B>
	  * <DD>GmmlArc a - the GmmlArc to add
	  */		
	public void addArc(GmmlArc a)
	{
		arcs.addElement(a);
	}
	
	/** addLineShape adds a lineshape to the array of lineshapes.
	  * <BR>
	  * <DL><B>Parameters</B>
	  * <DD>GmmlLineShape ls - the GmmlLineShape to add
	  */	
	public void addLineShape(GmmlLineShape ls)
	{
		lineshapes.addElement(ls);
	}
	
	/** addBrace adds a brace to the array of braces.
	  * <BR>
	  * <DL><B>Parameters</B>
	  * <DD>GmmlBrace b - the GmmlBrace to add
	  * </DL>
	  */		
	public void addBrace(GmmlBrace b)
	{
		braces.addElement(b);
	}

	public void addCellShape(double x, double y, double width, double height, double rotation) {
		//to do: make addCellShape
	}
	
	public void addCellComponent(double centerX, double centerY, int type) {
		//to do: make addCellComponent
	}
	
	public void addProteinComplex(double centerX, double centerY, int type) {
		//to do: make addCellComponent
	}		
	
	/** addShape adds a Shape to the array of shapes.
	  * <BR>
	  * <DL><B>Parameters</B>
	  * <DD>GmmlShape s - the GmmlShape to add
	  * </DL>
	  */		
	public void addShape(GmmlShape s)
	{
		shapes.add(s);
	}
	
	public void setSize(int width, int height)
	{
		this.width  = width;
		this.heigth = height;
	}
	
	/** echoAtt checks for stored attributes and prints those
	  */
	public void printAttributes()
	{
		System.out.println("Checking for stored attributes - number: "+ attributes.size());
		for(int i = 0; i < attributes.size(); i++)
		{
			Attribute a = (Attribute) attributes.get(i);
			System.out.println("Attribute name: " + a.getName() + "value: " + a.getValue());
		}
	}

} //end of GmmlPathway
