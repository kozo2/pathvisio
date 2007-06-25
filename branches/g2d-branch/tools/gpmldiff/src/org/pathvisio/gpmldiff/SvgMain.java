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
package org.pathvisio.gpmldiff;

import java.awt.*;
import java.io.*;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.pathvisio.view.VPathway;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.ConverterException;

class SvgMain
{
	public void paint (Graphics2D g2d)
	{
		g2d.setPaint (Color.red);
		g2d.fill (new Rectangle (10, 10, 100, 100));
	}
	
	static public void main (String argv[]) throws IOException, ConverterException
	{
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument (svgNS, "svg", null);
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

		SvgMain svgMain = new SvgMain();
		svgMain.paint(svgGenerator);

		boolean useCSS = true;
		Writer out = new OutputStreamWriter (System.out, "UTF-8");
		svgGenerator.stream (out, useCSS);

		Pathway pwy = new Pathway();
		pwy.readFromXml (new File ("testcases/Simple1.1.gpml"), true);
		VPathway vpwy = new VPathway(null);
		vpwy.fromGmmlData(pwy);
		vpwy.draw (svgGenerator, null);
	}
}
