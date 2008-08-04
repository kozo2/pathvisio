package org.pathvisio.cytoscape;

import giny.view.GraphView;
import giny.view.NodeView;

import java.util.HashMap;

import org.pathvisio.debug.Logger;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.PathwayElement;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroupManager;
import ding.view.DGraphView;
import ding.view.DingCanvas;

/**
 * Class that holds a Cytoscape edge that has a GPML representation, which is stored
 * as edge attributes
 * @author thomas
 *
 */
public class GpmlNode extends GpmlNetworkElement<CyNode> {
	HashMap<GraphView, Annotation> annotations = new HashMap<GraphView, Annotation>();
	
	/**
	 * Constructor for this class. Creates a new GpmlNode, based on the given
	 * node and PathwayElement
	 * @param parent
	 * @param pwElm
	 */
	public GpmlNode(CyNode parent, PathwayElement pwElm, AttributeMapper attributeMapper) {
		super(parent, pwElm, attributeMapper);
		String id = pwElm.getGraphId();
		if(id == null) {
			id = Integer.toHexString(parent.getRootGraphIndex());
		}
	}
	
	/**
	 * Creates a new GpmlNode based on the given node view. A GPML representation
	 * (PathwayElement of type DataNode) will automatically created based on the node view.
	 * @param parent
	 */
	public GpmlNode(NodeView view, AttributeMapper attributeMapper) {
		super((CyNode)view.getNode(), PathwayElement.createPathwayElement(
				CyGroupManager.isaGroup((CyNode)view.getNode()) ? 
						ObjectType.GROUP : ObjectType.DATANODE));
		pwElmOrig.setTextLabel(parent.getIdentifier());
		pwElmOrig.setInitialSize();
		//Set graphid to rootgraph index
		String id = Integer.toHexString(getParent().getRootGraphIndex());
		pwElmOrig.setGraphId(id);
		getParent().setIdentifier(id);
		resetToGpml(attributeMapper);
	}
	
	/**
	 * Show this node's annotations on the given view
	 * @param view
	 * @param visible
	 */
	public void showAnnotations(GraphView view, boolean visible) {
		Annotation a = annotations.get(view);
		if(a != null) {
			a.setVisible(visible);
		}
	}
	
	public boolean isAnnotation(GraphView view) {
		return annotations.containsKey(view);
	}
	
	public void addAnnotation(GraphView view) {
		if(annotations.containsKey(view)) return; //Annotation already added
		
		Logger.log.trace("Adding annotation for " + this);
		
		NodeView nv = view.getNodeView(parent);
		DGraphView dview = (DGraphView) view;
		DingCanvas aLayer = dview.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);
		
		Annotation a = null;
		
		switch(pwElmOrig.getObjectType()) {
		case ObjectType.SHAPE:
			a = new Shape(pwElmOrig, dview);
			break;
		case ObjectType.LABEL:
			a = new Label(pwElmOrig, dview);
			break;
		case ObjectType.LINE:
			a = new Line(pwElmOrig, dview);
			break;
		case ObjectType.LEGEND:
		case ObjectType.MAPPINFO:
		case ObjectType.INFOBOX:
			//Only hide the node
			view.hideGraphObject(nv);
			break;
		}
		if(a != null) {
			aLayer.add(a);
			view.hideGraphObject(nv);
			annotations.put(view, a);
		}
	}
	
	public void resetToGpml(AttributeMapper attributeMapper, GraphView view) {
		super.resetToGpml(attributeMapper, view);
		resetPosition(view);
	}
	
	protected void resetPosition(GraphView view) {
		NodeView nv = view.getNodeView(parent);
		if(nv == null) {
			Logger.log.trace("Null node view found in GpmlNode.resetPosition, a group?");
			return;
		}
		nv.setXPosition(GpmlPlugin.mToV(pwElmOrig.getMCenterX()), false);
		nv.setYPosition(GpmlPlugin.mToV(pwElmOrig.getMCenterY()), false);
	}
	
	protected void savePosition(GraphView view) {
		NodeView nv = (NodeView)view.getNodeView(parent);
		if(nv != null) { //View could be null, in case of hidden node
			getPwElmCy().setMCenterX(GpmlPlugin.vToM(nv.getXPosition()));
			getPwElmCy().setMCenterY(GpmlPlugin.vToM(nv.getYPosition()));
		}
	}

	public CyAttributes getCyAttributes() {
		return Cytoscape.getNodeAttributes();
	}

	public String getParentIdentifier() {
		return getParent().getIdentifier();
	}

	public void updateFromCytoscape(GraphView view, AttributeMapper attributeMapper) {
		super.updateFromCytoscape(view, attributeMapper);
		if(!isAnnotation(view)) {
			savePosition(view);
		}
	}
}
