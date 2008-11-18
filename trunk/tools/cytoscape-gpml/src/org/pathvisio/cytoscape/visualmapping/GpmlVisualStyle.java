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
package org.pathvisio.cytoscape.visualmapping;

import java.awt.Color;
import java.util.HashMap;

import org.pathvisio.cytoscape.AttributeMapper;
import org.pathvisio.cytoscape.GpmlAnchorNode;
import org.pathvisio.cytoscape.GpmlHandler;
import org.pathvisio.cytoscape.GpmlNetworkElement;
import org.pathvisio.model.LineStyle;
import org.pathvisio.model.LineType;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.PropertyType;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.NodeAppearance;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

public class GpmlVisualStyle extends VisualStyle {
	public static final String NAME = "GPML";
	
	GpmlHandler gpmlHandler;
	AttributeMapper attrMapper;
	
	NodeAppearanceCalculator nac;
	EdgeAppearanceCalculator eac;
	
	public GpmlVisualStyle(GpmlHandler gpmlHandler, VisualStyle toCopy) {
		super(toCopy);
		setName(NAME);
		init(gpmlHandler);
	}
	
	public GpmlVisualStyle(GpmlHandler gpmlHandler) {
		super(NAME);
		init(gpmlHandler);
	}
	
	private void init(GpmlHandler gh) {
		this.gpmlHandler = gh;
		attrMapper = gpmlHandler.getAttributeMapper();
		
		nac = getNodeAppearanceCalculator();
		eac = getEdgeAppearanceCalculator();
		if(nac == null) {
			nac = new NodeAppearanceCalculator();
			setNodeAppearanceCalculator(nac);
		}
		if(eac == null) {
			eac = new EdgeAppearanceCalculator();
			setEdgeAppearanceCalculator(eac);
		}
		
		setColorMapping();
		setLabelMapping();
		setTypeMapping();
		setArrowMapping();
		setLineTypeMapping();
		setNodeShapeMapping();
	}
	
	void setNodeShapeMapping() {
		getNodeAppearanceCalculator().setNodeSizeLocked(false);
		NodeAppearance nd = nac.getDefaultAppearance();
		nd.set(VisualPropertyType.NODE_SHAPE, NodeShape.RECT);
		nd.set(VisualPropertyType.NODE_WIDTH, 80);
		nd.set(VisualPropertyType.NODE_HEIGHT, 20);
	}
	
	void setLineTypeMapping() {
		DiscreteMapping styleMapping = new DiscreteMapping(
				nac.getDefaultAppearance().get(VisualPropertyType.EDGE_LINE_STYLE),
				attrMapper.getMapping(PropertyType.LINESTYLE),
				ObjectMapping.EDGE_MAPPING
		);
		styleMapping.putMapValue("" + LineStyle.SOLID, cytoscape.visual.LineStyle.SOLID);
		styleMapping.putMapValue("" + LineStyle.DASHED, cytoscape.visual.LineStyle.LONG_DASH);
		eac.setCalculator(
				new BasicCalculator("GPML edge style", styleMapping, VisualPropertyType.EDGE_LINE_STYLE)
		);
	}
	
	void setArrowMapping() {
		DiscreteMapping srcMapping = new DiscreteMapping(
				nac.getDefaultAppearance().get(VisualPropertyType.EDGE_SRCARROW_SHAPE),
				attrMapper.getMapping(PropertyType.STARTLINETYPE),
				ObjectMapping.EDGE_MAPPING
		);
		setArrowMappings(srcMapping);
		eac.setCalculator(
				new BasicCalculator("GPML edge source shape", srcMapping, VisualPropertyType.EDGE_SRCARROW_SHAPE)
		);
		
		DiscreteMapping tgtMapping = new DiscreteMapping(
				nac.getDefaultAppearance().get(VisualPropertyType.EDGE_TGTARROW_SHAPE),
				attrMapper.getMapping(PropertyType.ENDLINETYPE),
				ObjectMapping.EDGE_MAPPING
		);
		setArrowMappings(tgtMapping);
		eac.setCalculator(
				new BasicCalculator("GPML edge target shape", tgtMapping, VisualPropertyType.EDGE_TGTARROW_SHAPE)
		);
		
		String colAttr = attrMapper.getMapping(PropertyType.COLOR);
		eac.setCalculator(
				new BasicCalculator(
						"GPML edge source color", 
						new GpmlColorMapper(
								eac.getDefaultAppearance().getColor(), colAttr
						),
						VisualPropertyType.EDGE_SRCARROW_COLOR
				)
		);
		eac.setCalculator(
				new BasicCalculator(
						"GPML edge target color", 
						new GpmlColorMapper(
								eac.getDefaultAppearance().getColor(), colAttr
						),
						VisualPropertyType.EDGE_TGTARROW_COLOR
				)
		);
	}
	
	private static HashMap<String, ArrowShape> attribute2arrow;
	private static HashMap<ArrowShape, String> arrow2attribute;
	
	public static HashMap<String, ArrowShape> getAttributeToArrow() {
		if(attribute2arrow == null) {
			attribute2arrow = new HashMap<String, ArrowShape>();
			attribute2arrow.put(LineType.LINE.getName(), ArrowShape.NONE);
			attribute2arrow.put(LineType.ARROW.getName(), ArrowShape.ARROW);
			attribute2arrow.put(LineType.LIGAND_ROUND.getName(), ArrowShape.CIRCLE);
			attribute2arrow.put(LineType.LIGAND_SQUARE.getName(), ArrowShape.DELTA);
			attribute2arrow.put(LineType.RECEPTOR.getName(), ArrowShape.DELTA);
			attribute2arrow.put(LineType.RECEPTOR_ROUND.getName(), ArrowShape.DELTA);
			attribute2arrow.put(LineType.RECEPTOR_SQUARE.getName(), ArrowShape.DELTA);
			attribute2arrow.put(LineType.TBAR.getName(), ArrowShape.T);
			
			attribute2arrow.put("mim-necessary-stimulation", ArrowShape.ARROW);
			attribute2arrow.put("mim-binding", ArrowShape.ARROW);
			attribute2arrow.put("mim-conversion", ArrowShape.ARROW);
			attribute2arrow.put("mim-stimulation", ArrowShape.ARROW);
			attribute2arrow.put("mim-catalysis", ArrowShape.ARROW);
			attribute2arrow.put("mim-inhibition", ArrowShape.ARROW);
			attribute2arrow.put("mim-cleavage", ArrowShape.ARROW);
		}
		return attribute2arrow;
	}
	
	public static HashMap<ArrowShape, String> getArrowToAttribute() {
		if(arrow2attribute == null) {
			arrow2attribute = new HashMap<ArrowShape, String>();
			HashMap<String, ArrowShape> attribute2arrow = getAttributeToArrow();
			for(String attribute : attribute2arrow.keySet()) {
				arrow2attribute.put(attribute2arrow.get(attribute), attribute);
			}
		}
		return arrow2attribute;
	}
	
	void setArrowMappings(DiscreteMapping mapping) {
		HashMap<String, ArrowShape> attribute2arrow = getAttributeToArrow();
		for(String attribute : attribute2arrow.keySet()) {
			mapping.putMapValue(attribute, attribute2arrow.get(attribute));
		}
	}
	
	void setTypeMapping() {
		DiscreteMapping typeMapping = new DiscreteMapping(
			nac.getDefaultAppearance().get(VisualPropertyType.NODE_SIZE),
			GpmlNetworkElement.ATTR_TYPE,
			ObjectMapping.NODE_MAPPING
		);
		typeMapping.putMapValue(ObjectType.GROUP, 5);
		typeMapping.putMapValue(GpmlAnchorNode.TYPE_ANCHOR, 5);
		
		nac.setCalculator(
				new BasicCalculator(
						GpmlNetworkElement.ATTR_TYPE,
						typeMapping,
						VisualPropertyType.NODE_SIZE
				)
		);
	}
	
	void setLabelMapping() {
		nac.setCalculator(
				new BasicCalculator(
						"canonicalName",
						new PassThroughMapping("", "canonicalName"),
						VisualPropertyType.NODE_LABEL
				)
		);
	}
	
	void setColorMapping() {
		//Default node color is white and semi-transparent
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR, Color.WHITE);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_OPACITY, 220);

		String colAttr = attrMapper.getMapping(PropertyType.COLOR);
		nac.setCalculator(
				new BasicCalculator(
						"GPML node border color", 
						new GpmlColorMapper(
								nac.getDefaultAppearance().getFillColor(), colAttr
						),
						VisualPropertyType.NODE_BORDER_COLOR
				)
		);
		eac.setCalculator(
				new BasicCalculator(
						"GPML edge color", 
						new GpmlColorMapper(
								eac.getDefaultAppearance().getColor(), colAttr
						),
						VisualPropertyType.EDGE_COLOR
				)
		);
						
	}
}
