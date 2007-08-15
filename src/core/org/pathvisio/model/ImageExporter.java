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
package org.pathvisio.model;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;
import org.pathvisio.view.VPathway;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class ImageExporter implements PathwayExporter {
	public static final String TYPE_PNG = "png";
	public static final String TYPE_TIFF = "tiff";
	public static final String TYPE_PDF = "pdf";
	public static final String TYPE_SVG = "svg";
	
	private String type;
	private String[] extensions;
	
	public ImageExporter(String type) {
		this.type = type;
	}
	
	public String[] getExtensions() {
		if(extensions == null) {
			extensions = new String[] { getDefaultExtension() };
		}
		return extensions;
	}
	
	public String getDefaultExtension() {
		return type;
	}

	public String getName() {
		return type.toUpperCase();
	}
	
	public void doExport(File file, Pathway pathway) throws ConverterException {		
		VPathway vPathway = new VPathway(null);
		vPathway.fromGmmlData(pathway);
		
		double width = vPathway.getVWidth();
		double height = vPathway.getVHeight();
		
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		Document svg = domImpl.createDocument ("http://www.w3.org/2000/svg", "svg", null);
		
		SVGGraphics2D svgG2d = new SVGGraphics2D(svg);
		svgG2d.setSVGCanvasSize(new Dimension((int)width, (int)height));
		vPathway.draw(svgG2d);
		Transcoder t = null;
		if			(type.equals(TYPE_SVG)) {
			try {
				Writer out = new FileWriter(file);			
				svgG2d.stream(out, true);
				out.flush();
				out.close();
			} catch(Exception e) {
				throw new ConverterException(e);
			}
			return;
		} else if	(type.equals(TYPE_PNG)) {
			t = new PNGTranscoder();
		} else if	(type.equals(TYPE_TIFF)) {
			t = new TIFFTranscoder();
		} else if	(type.equals(TYPE_PDF)) {
			try {
                 Class pdfClass = Class.forName("org.apache.fop.svg.PDFTranscoder");
                 t = (Transcoder)pdfClass.newInstance();
             } catch(Exception e) {
            	 noExporterException();
             }
		}
		if(t == null) noExporterException();

		svgG2d.getRoot(svg.getDocumentElement());
		t.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, java.awt.Color.WHITE);

		try {
			TranscoderInput input = new TranscoderInput(svg);

			// Create the transcoder output.
			OutputStream ostream = new FileOutputStream(file);
			TranscoderOutput output = new TranscoderOutput(ostream);

			// Save the image.
			t.transcode(input, output);
			
		    // Flush and close the stream.
	        ostream.flush();
	        ostream.close();
		} catch(Exception e) {
			throw new ConverterException(e);
		}
	}

	public void noExporterException() throws ConverterException {
		throw new ConverterException("No exporter for this image format");
	}
}
