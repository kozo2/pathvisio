// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2011 BiGCaT Bioinformatics
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
package org.pathvisio.cytoscape;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.pathvisio.core.model.Pathway;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.view.CyNetworkView;

/**
 * An AbstractGraphReader that uses Pathway.readFromXml to read GPML
 * and then uses @{link GpmlConverter} to turn it into a real
 * network with nodes and edges.
 * <p>
 * Can handle files and URLs. AbstracGraphReader handles firing 
 * appropriate Cytoscape Events, such as NETWORK_LOADED.
 */
public class GpmlReader extends AbstractGraphReader {
	GpmlConverter converter;
	GpmlHandler gpmlHandler;

	URLConnection urlCon;
	
	private boolean loadAsNetwork = false;
	
	public GpmlReader(String fileName, GpmlHandler gpmlHandler, boolean loadAsNetwork) {
		super(fileName);
		this.gpmlHandler = gpmlHandler;
		this.loadAsNetwork = loadAsNetwork;
	}

	public GpmlReader(URLConnection con, URL url, GpmlHandler gpmlHandler, boolean loadAsNetwork) {
		super(url.toString());
		urlCon = con;
		this.gpmlHandler = gpmlHandler;
		this.loadAsNetwork = loadAsNetwork;
	}

	public void read() throws IOException {
		
		try {
			Pathway pathway = new Pathway();
			if(urlCon != null) {
				pathway.readFromXml(urlCon.getInputStream(), true);
			} else {
				System.out.println("read pathway from file " + fileName);
				pathway.readFromXml(new File(fileName), true);
			}
			
			converter = new GpmlConverter(gpmlHandler, pathway, loadAsNetwork);
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new IOException(ex.getMessage());
		}
	}

	/*
	 * Calling layout after background/foreground canvas is ready to receive
	 * annotations
	 */
	public void doPostProcessing(CyNetwork network) {
		CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
		converter.layout(view);
		view.redrawGraph(true, false);
	}

	public int[] getEdgeIndicesArray() {
		return converter.getEdgeIndicesArray();
	}

	public int[] getNodeIndicesArray() {
		return converter.getNodeIndicesArray();
	}

	public String getNetworkName() {
		String pwName = converter.getPathway().getMappInfo().getMapInfoName();
		if(pwName == null) pwName = super.getNetworkName();
		if(loadAsNetwork) {
			pwName = pwName + "-network";
		}
		return pwName;
	}
}