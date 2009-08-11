// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2007 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package org.pathvisio.biopax.reflect;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.pathvisio.biopax.BiopaxElementManager;
import org.pathvisio.biopax.BiopaxReferenceManager;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayEvent;

public class Test extends TestCase {
	Pathway data;
	List<PathwayEvent> received;
	BiopaxElementManager elementManager;
	BiopaxReferenceManager pwRefManager;
	
	public void setUp()
	{
		data = new Pathway();
		PathwayElement o = new PathwayElement(ObjectType.DATANODE);
		data.add (o);
		
		elementManager = new BiopaxElementManager(data);
	}
	
	public void testProperties() {
		PublicationXRef xref = new PublicationXRef();
		
		//Check cardinality
		BiopaxProperty p1 = PropertyType.TITLE.getProperty("title 1");
		BiopaxProperty p2 = PropertyType.TITLE.getProperty("title 2");
		List<BiopaxProperty> properties = null;
		xref.addProperty(p1);
		properties = xref.getProperties(p1.getName());
		assertTrue(properties.size() == 1);
		xref.addProperty(p2);
		properties = xref.getProperties(p2.getName());
		assertTrue(properties.size() == 1);
		assertTrue(properties.get(0) == p2);
		
		
		//Add a valid property
		try {
			xref.addProperty(PropertyType.TITLE.getProperty("a title"));
		} catch(IllegalArgumentException e) {
			fail("Failed to add a valid property: " + e.getMessage());
		}
		//Add an invalid property
		try {
			xref.addProperty(new BiopaxProperty("doesntexist", "value", "datatype"));
			fail("Succeeded to add an invalid property");
		} catch(IllegalArgumentException e) { }
	}
		
	public void testReadWrite() {
		//Add to datanode
		PathwayElement pwElm = new PathwayElement(ObjectType.DATANODE);
		data.add(pwElm);
		BiopaxReferenceManager dnRefManager = new BiopaxReferenceManager(elementManager, pwElm);
		pwRefManager = new BiopaxReferenceManager(elementManager, data.getMappInfo());
		
		//Test 1:
		//Add two identical publication references, one to the pathway,
		//the other to the pathway element.
		//Expected behaviour:
		//The pathway should have 1 reference with two authors
		//There should only be one reference instance in the biopax element
		
		//Reference information
		String title = "title1";
		String author1 = "author1";
		String author2 = "author2";
		
		//Add to pathway element
		PublicationXRef xrefObject = new PublicationXRef();
		//Add one title and two authors
		xrefObject.setTitle(title);
		xrefObject.addAuthor(author1);
		xrefObject.addAuthor(author2);
		
		dnRefManager.addElementReference(xrefObject);
		
		//Add to pathway
		PublicationXRef xrefPathway = new PublicationXRef();
		//Add one title and two authors
		xrefPathway.setTitle(title);
		xrefPathway.addAuthor(author1);
		xrefPathway.addAuthor(author2);
		
		pwRefManager.addElementReference(xrefPathway);
		
		writeRead(data);
		
		List<PublicationXRef> references = pwRefManager.getPublicationXRefs();
		//There has to be one reference
		assertTrue("Should have one literature reference, has " + references.size(), references.size() == 1);
		//With two authors
		assertTrue("Two authors", references.get(0).getAuthors().size() == 2);
		
		//There should be one publicationxref element total
		int nrElm = elementManager.getElements().size();
		assertTrue("Should have one element total, has " + nrElm, nrElm == 1);
		
		
		//Test added 30-08, because of bug where biopax was lost after
		//saving/loading/saving sequence
		//Add another reference to Pathway
		xrefPathway = new PublicationXRef();
		//Add one title and one author
		xrefPathway.setTitle("title3");
		xrefPathway.addAuthor("author3");
		
		pwRefManager.addElementReference(xrefPathway);
		
		writeRead(data);
		
		references = pwRefManager.getPublicationXRefs();
		//There have to be two references now
		assertTrue("Two literature references, has " + references.size(), references.size() == 2);
		//Where the one we last added has one author
		PublicationXRef xref = (PublicationXRef)elementManager.getElement(xrefPathway.getId());
		assertTrue("One author", xref.getAuthors().size() == 1);
		
	}

	public void writeRead(Pathway data) {
		write();
		read();
	}
	
	public void read() {
		data = new Pathway();
		try {
			data.readFromXml(testfile(nrWrite - 1), true);
		} catch(ConverterException e) {
			fail("Unable to read a pathway: " + e.toString());
		}
		elementManager = new BiopaxElementManager(data);
		pwRefManager = new BiopaxReferenceManager(elementManager, data.getMappInfo());
	}
	
	int nrWrite = 0;
	private File testfile(int i) {
		return new File("testData/test-biopax-" + i + ".xml");
	}
	
	public void write() {
		try {
			data.writeToXml(testfile(nrWrite++), true);
		} catch(ConverterException e) {
			fail("Unable to write a pathway: " + e.toString());
		}
	}
}