package org.pathvisio.view;

import java.net.URL;

import org.pathvisio.Engine;
import org.pathvisio.model.DataNodeType;
import org.pathvisio.model.LineStyle;
import org.pathvisio.model.LineType;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.ShapeType;

public abstract class DefaultTemplates {
	
	static abstract class SingleElementTemplate implements Template {
		PathwayElement lastAdded;
		
		protected void addElement(PathwayElement e, Pathway p) {
			p.add(e);
			lastAdded = e;
		}
		
		/**
		 * Default implementation returns the view of the last added object
		 */
		public VPathwayElement getDragElement(VPathway vp) {
			if(lastAdded != null) {
				Graphics g = vp.getPathwayElementView(lastAdded);
				if(g == null) {
					throw new IllegalArgumentException(
							"Given VPathway doesn't contain last added element"
					);
				}
				return g;
			}
			return null; //No last object
		}
		
		public String getDescription() {
			return "Draw new " + getName();
		}
		
		public URL getIconLocation() {
			return  Engine.getCurrent().getResourceURL("icons/new" + getName().toLowerCase() + ".gif");
		}
	}
	public static class LineTemplate extends SingleElementTemplate {
		int style;
		LineType startType;
		LineType endType;
		
		public LineTemplate(int style, LineType startType, LineType endType) {
			this.style = style;
			this.startType = startType;
			this.endType = endType;
		}
		
		public PathwayElement[] addElements(Pathway p, double mx, double my) {
			PathwayElement e = new PathwayElement(ObjectType.LINE);
			e.setMStartX(mx);
			e.setMStartY(my);
			e.setMEndX(mx);
			e.setMEndY(my);
			e.setLineStyle(style);
			e.setStartLineType(startType);
			e.setEndLineType(endType);
			addElement(e, p);
			
			return new PathwayElement[] { e };
		}
		
		public VPathwayElement getDragElement(VPathway vp) {
			Line l = (Line)super.getDragElement(vp);
			return l.getEnd().getHandle();
		}

		public String getName() {
			String sn = "";
			if(style == LineStyle.DASHED) {
				sn = "dashed";
			}
			return sn + endType.getGpmlName();
		}
	}
	
	public static class LabelTemplate extends SingleElementTemplate {
		public PathwayElement[] addElements(Pathway p, double mx, double my) {
			PathwayElement e = new PathwayElement(ObjectType.LABEL);
			e.setMCenterX(mx);
			e.setMCenterY(my);
			e.setMWidth(Label.M_INITIAL_WIDTH);
			e.setMHeight(Label.M_INITIAL_HEIGHT);
			e.setMFontSize(Label.M_INITIAL_FONTSIZE);
			e.setGraphId(p.getUniqueId());
			e.setTextLabel("Label");
			addElement(e, p);
			
			return new PathwayElement[] { e };
		}
		public VPathwayElement getDragElement(VPathway vp) {
			return null; //Don't drag label on insert
		}
		
		public String getName() {
			return "Label";
		}
	}
	
	public static class DataNodeTemplate extends SingleElementTemplate {
		DataNodeType type;
		
		public DataNodeTemplate(DataNodeType type) {
			this.type = type;
		}
		
		public PathwayElement[] addElements(Pathway p, double mx, double my) {
			PathwayElement e = new PathwayElement(ObjectType.DATANODE);
			e.setMCenterX(mx);
			e.setMCenterY(my);
			e.setMWidth(1);
			e.setMHeight(1);
			e.setRotation(0);
			e.setGraphId(p.getUniqueId());
			e.setDataNodeType(type);
			e.setTextLabel(type.toString());
			addElement(e, p);
			return new PathwayElement[] { e };
		}
		
		public String getName() {
			return type.toString();
		}
	}
	
	public static class ShapeTemplate extends SingleElementTemplate {
		ShapeType type;
		
		public ShapeTemplate(ShapeType type) {
			this.type = type;
		}
		
		public PathwayElement[] addElements(Pathway p, double mx, double my) {
			PathwayElement e = new PathwayElement(ObjectType.SHAPE);
			e.setShapeType(type);
			e.setMCenterX(mx);
			e.setMCenterY(my);
			e.setMWidth(1);
			e.setMHeight(1);
			e.setRotation(0);
			e.setGraphId(p.getUniqueId());
			addElement(e, p);
			
			//brace
//			gdata.setOrientation(OrientationType.RIGHT);
			
			return new PathwayElement[] { e };
		}
		
		public VPathwayElement getDragElement(VPathway vp) {
			Shape s = (Shape)super.getDragElement(vp);
			return s.handleSE;
		}
		
		public String getName() {
			return type.toString();
		}
	}
	

	public static class InteractionTemplate implements Template {
		final static int OFFSET_LINE = 5 * 15;
		PathwayElement lastStartNode;
		PathwayElement lastEndNode;
		PathwayElement lastLine;
		
		LineType endType;
		LineType startType;
		int lineStyle;
		
		public InteractionTemplate() {
			endType = LineType.LINE;
			startType = LineType.LINE;
			lineStyle = LineStyle.SOLID;
		}
		
		public PathwayElement[] addElements(Pathway p, double mx, double my) {
			//Add two datanodes, connected by a line
			Template dnt = new DataNodeTemplate(DataNodeType.GENEPRODUCT);
			lastStartNode = dnt.addElements(p, mx, my)[0];
			
			lastStartNode.setInitialSize();
			
			lastEndNode = dnt.addElements(p, mx + 2 * lastStartNode.getMWidth(), my)[0];
			
			lastEndNode.setInitialSize();
			
			Template lnt = new LineTemplate(lineStyle, startType, endType);
			lastLine = lnt.addElements(p, mx, my)[0];
			lastLine.setMStartX(OFFSET_LINE + lastStartNode.getMLeft() + 
					lastStartNode.getMWidth());
			lastLine.setMStartY(lastStartNode.getMCenterY());
			lastLine.setMEndX(lastEndNode.getMLeft() - OFFSET_LINE);
			lastLine.setMEndY(lastEndNode.getMCenterY());
			lastLine.setStartGraphRef(lastStartNode.getGraphId());
			lastLine.setEndGraphRef(lastEndNode.getGraphId());
			
			return new PathwayElement[] { lastLine, lastStartNode, lastEndNode };
		}

		public VPathwayElement getDragElement(VPathway vp) {
			return null;
		}
		
		public String getName() {
			return "Interaction";
		}
		
		public String getDescription() {
			return "Draw new interaction (Two genes connected by a line)";
		}
		
		public URL getIconLocation() {
			return  Engine.getCurrent().getResourceURL("icons/newinteraction.gif");
		}
	}
}