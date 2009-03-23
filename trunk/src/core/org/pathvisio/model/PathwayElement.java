// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jdom.Document;
import org.pathvisio.biopax.BiopaxReferenceManager;
import org.pathvisio.model.GraphLink.GraphIdContainer;
import org.pathvisio.model.GraphLink.GraphRefContainer;

/**
 * PathwayElement is responsible for maintaining the data for all the individual
 * objects that can appear on a pwy (Lines, GeneProducts, Shapes, etc.)
 * 
 * All PathwayElements have an ObjectType. This ObjectType is specified at creation
 * time and can't be modified. To create a PathwayElement, 
 * use the createPathwayElement() function. This is a factory method
 * that returns a different implementation class depending on
 * the specified ObjectType. 
 *  
 * PathwayElements have a number of properties which consist of a 
 * key, value pair.
 * 
 * There are two types of properties: Static and Dynamic
 * Static properties are one of the properties 
 * 
 * Dynamic properties can have any String as key. Their value is
 * always of type String. Dynamic properties are not essential for
 * the functioning of PathVisio and can be used to
 * store arbitrary data. In GPML, dynamic properties are
 * stored in an <Attribute key="" value=""/> tag.
 * Internally, dynamic properties are stored in a Map<String, String>
 * 
 * Static properties must have a key from the PropertyType enum
 * Their value can be various types which can be 
 * obtained from PropertyType.type(). Static properties can
 * be queried with getStaticProperty (key) and 
 * setStaticProperty(key, value), but also specific accessors 
 * such as e.g. getTextLabel() and setTextLabel()
 * 
 * Internally, dynamic properties are stored in various 
 * fields of the PathwayElement Object.
 * The static properties are a union of all possible fields 
 * (e.g it has both start and endpoints for lines, 
 * and label text for labels) 
 * 
 * the setPropertyEx() and getPropertyEx() functions can be used
 * to access both dynamic and static properties 
 * from the same function. If key instanceof String then it's 
 * assumed the caller wants a dynamic
 * property, if key instanceof PropertyType then the static property 
 * is used.
 * 
 * most static properties cannot be set to null. Notable exceptions are graphId,
 * startGraphRef and endGraphRef.
 */
public class PathwayElement implements GraphIdContainer, Comparable<PathwayElement>
{
	//TreeMap has better performance than HashMap
	//in the (common) case where no attributes are present
	private Map<String, String> attributes = new TreeMap<String, String>();
	
	/**
	 * Get a map of arbitrary key / value pairs
	 * @deprecated should be private
	 */
	public Map<String, String> getAttributeMap() { return attributes; }
	
	/**
	 * get a set of all dynamic property keys
	 */
	public Set<String> getDynamicPropertyKeys()
	{
		return attributes.keySet();
	}
	
	/**
	 * set a dynamic property.
	 */
	public void setDynamicProperty (String key, String value)
	{
		attributes.put (key, value);
		fireObjectModifiedEvent(new PathwayEvent(PathwayElement.this,
				PathwayEvent.MODIFIED_GENERAL));
	}
	
	/**
	 * get a dynamic property
	 */
	public String getDynamicProperty (String key)
	{
		return attributes.get (key);
	}
	
	/**
	 * A comment in a pathway element: each 
	 * element can have zero or more comments with it, and
	 * each comment has a source and a text.
	 */
	public class Comment implements Cloneable
	{
		public Comment(String aComment, String aSource)
		{
			source = aSource;
			comment = aComment;
		}

		public Object clone() throws CloneNotSupportedException
		{
			return super.clone();
		}

		private String source;
		private String comment;
		
		public String getSource() { return source; }
		public String getComment() { return comment; }
		
		public void setSource(String s) {
			if(s != null && !source.equals(s)) {
				source = s;
				changed();
			}
		}
		
		public void setComment(String c) {
			if(c != null && !comment.equals(c)) {
				comment = c;
				changed();
			}
		}
		
		private void changed() {
			fireObjectModifiedEvent(new PathwayEvent(PathwayElement.this, PathwayEvent.MODIFIED_GENERAL));
		}
		
		public String toString() {
			String src = "";
			if(src != null && !"".equals(src)) {
				src = " (" + source + ")";
			}
			return comment + src;
		}
	}

	/**
	 * Represents a generic point in an coordinates.length dimensional space.
	 * The point is automatically a {@link GraphIdContainer} and therefore lines
	 * can link to the point.
	 * @see MPoint
	 * @see MAnchor
	 * @author thomas
	 *
	 */
	private abstract class GenericPoint implements Cloneable, GraphIdContainer
	{
		private double[] coordinates;

		private String graphId;

		GenericPoint(double[] coordinates)
		{
			this.coordinates = coordinates;
		}

		GenericPoint(GenericPoint p)
		{
			coordinates = new double[p.coordinates.length];
			for(int i = 0; i < coordinates.length; i++) {
				coordinates[i] = p.coordinates[i];
			}
			if (p.graphId != null)
				graphId = new String(p.graphId);
		}

		protected void moveBy(double[] delta)
		{
			for(int i = 0; i < coordinates.length; i++) {
				coordinates[i] += delta[i];
			}
			fireObjectModifiedEvent(new PathwayEvent(PathwayElement.this,
					PathwayEvent.MODIFIED_GENERAL));
		}

		protected void moveTo(double[] coordinates)
		{
			this.coordinates = coordinates;
			fireObjectModifiedEvent(new PathwayEvent(PathwayElement.this,
					PathwayEvent.MODIFIED_GENERAL));
		}
		
		protected void moveTo(GenericPoint p)
		{
			coordinates = p.coordinates;
			fireObjectModifiedEvent(new PathwayEvent(PathwayElement.this,
					PathwayEvent.MODIFIED_GENERAL));
		}

		protected double getCoordinate(int i) {
			return coordinates[i];
		}

		public String getGraphId()
		{
			return graphId;
		}

		public String setGeneratedGraphId()
		{
			setGraphId(parent.getUniqueGraphId());
			return graphId;
		}

		public void setGraphId(String v)
		{
			GraphLink.setGraphId(v, this, PathwayElement.this.parent);
			graphId = v;
			fireObjectModifiedEvent(new PathwayEvent(PathwayElement.this,
					PathwayEvent.MODIFIED_GENERAL));
		}

		public Object clone() throws CloneNotSupportedException
		{
			GenericPoint p = (GenericPoint) super.clone();
			if (graphId != null)
				p.graphId = new String(graphId);
			return p;
		}

		public Set<GraphRefContainer> getReferences()
		{
			return GraphLink.getReferences(this, parent);
		}

		public Pathway getPathway() {
			return parent;
		}
		
		/**
		 * @deprecated use {@link #getPathway()} instead
		 */
		public Pathway getGmmlData()
		{
			return parent;
		}

		public PathwayElement getParent()
		{
			return PathwayElement.this;
		}
	}
	
	/**
	 * This class represents the Line.Graphics.Point element in GPML.
	 * @author thomas
	 *
	 */
	public class MPoint extends GenericPoint implements GraphRefContainer
	{
		private String graphRef;
		private boolean relativeSet;
		
		public MPoint(double x, double y)
		{
			super(new double[] { x, y, 0, 0 });
		}

		MPoint(MPoint p)
		{
			super(p);
			if (p.graphRef != null)
				graphRef = new String(p.graphRef);
		}

		public void moveBy(double dx, double dy)
		{
			super.moveBy(new double[] { dx, dy, 0, 0 });
		}

		public void moveTo(double x, double y) 
		{
			super.moveTo(new double[] { x, y, 0, 0 });
		}
		
		public void setX(double nx)
		{
			if (nx != getX())
				moveBy(nx - getX(), 0);
		}

		public void setY(double ny)
		{
			if (ny != getY())
				moveBy(0, ny - getY());
		}

		public double getX()
		{
			if(isRelative()) {
				return getAbsolute().getX();
			} else {
				return getCoordinate(0);
			}
		}

		public double getY()
		{
			if(isRelative()) {
				return getAbsolute().getY();
			} else {
				return getCoordinate(1);
			}
		}

		protected double getRawX() {
			return getCoordinate(0);
		}
		
		protected double getRawY() {
			return getCoordinate(1);
		}
		
		public double getRelX() {
			return getCoordinate(2);
		}
		
		public double getRelY() {
			return getCoordinate(3);
		}
		
		private Point2D getAbsolute() {
			return getGraphIdContainer().toAbsoluteCoordinate(
					new Point2D.Double(getRelX(), getRelY())
			);
		}

		
		public void setRelativePosition(double rx, double ry) {
			moveTo(new double[] { getX(), getY(), rx, ry });
			relativeSet = true;
		}
		
		/**
		 * Checks if the position of this point should be stored
		 * as relative or absolute coordinates
		 * @return true if the coordinates are relative, false if not
		 */
		public boolean isRelative() {
			Pathway p = getPathway();
			if(p != null) {
				GraphIdContainer gc = getPathway().getGraphIdContainer(graphRef);				
				return gc != null;
			}
			return false;
		}
		
		/**
		 * Helper method for converting older GPML files without
		 * relative coordinates.
		 * @return true if {@link #setRelativePosition(double, double)} was called to
		 * set the relative coordinates, false if not.
		 */
		protected boolean relativeSet() {
			return relativeSet;
		}
		
		private GraphIdContainer getGraphIdContainer() {
			return getPathway().getGraphIdContainer(graphRef);
		}
		
		public String getGraphRef()
		{
			return graphRef;
		}
		
		/**
		 * Set a reference to another object with a graphId. If a parent is set,
		 * this will automatically deregister the previously held reference and
		 * register the new reference as necessary
		 * 
		 * @param v
		 *            reference to set.
		 */
		public void setGraphRef(String v)
		{
			if (graphRef != v)
			{
				if (parent != null)
				{
					if (graphRef != null)
					{
						parent.removeGraphRef(graphRef, this);
					}
					if (v != null)
					{
						parent.addGraphRef(v, this);
					}
				}
				graphRef = v;
				// fireObjectModifiedEvent(new PathwayEvent
				// (PathwayElement.this, PathwayEvent.MODIFIED_GENERAL));
			}
		}

		public Object clone() throws CloneNotSupportedException
		{
			MPoint p = (MPoint) super.clone();
			if (graphRef != null)
				p.graphRef = new String(graphRef);
			return p;
		}

		public Point2D toPoint2D() {
			return new Point2D.Double(getX(), getY());
		}

		/**
		 * Link to an object. Current absolute coordinates
		 * will be converted to relative coordinates based on the
		 * object to link to.
		 */
		public void linkTo(GraphIdContainer idc) {
			Point2D rel = idc.toRelativeCoordinate(toPoint2D());
			linkTo(idc, rel.getX(), rel.getY());
		}
		
		/**
		 * Link to an object using the given relative coordinates
		 */
		public void linkTo(GraphIdContainer idc, double relX, double relY) {
			String id = idc.getGraphId();
			if(id == null) id = idc.setGeneratedGraphId();
			setGraphRef(idc.getGraphId());
			setRelativePosition(relX, relY);
		}
		
		/** note that this may be called any number of times when this point is already unlinked */
		public void unlink() 
		{
			if (graphRef != null)
			{
				Point2D abs = getAbsolute();
				moveTo(abs.getX(), abs.getY());
				relativeSet = false;
				setGraphRef(null);
			}
		}

		public Point2D toAbsoluteCoordinate(Point2D p) {
			return new Point2D.Double(p.getX() + getX(), p.getY() + getY());
		}

		public Point2D toRelativeCoordinate(Point2D p) {
			return new Point2D.Double(p.getX() - getX(), p.getY() - getY());
		}
		
		/**
		 * Find out if this point is linked to an object.
		 * Returns true if a graphRef exists and is not an empty string
		 */
		public boolean isLinked() {
			String ref = getGraphRef();
			return ref != null && !"".equals(ref);
		}
	}
	
	/**
	 * This class represents the Line.Graphics.Anchor element in GPML
	 * @author thomas
	 *
	 */
	public class MAnchor extends GenericPoint {
		AnchorType shape = AnchorType.NONE;
		
		public MAnchor(double position) {
			super(new double[] { position });
		}
		
		public MAnchor(MAnchor a) {
			super(a);
			shape = a.shape;
		}
		
		public void setShape(AnchorType type) {
			if(!this.shape.equals(type) && type != null) {
				this.shape = type;
				fireObjectModifiedEvent(new PathwayEvent(
						PathwayElement.this, PathwayEvent.MODIFIED_GENERAL));
			}
		}
		
		public AnchorType getShape() {
			return shape;
		}
		
		public double getPosition() {
			return getCoordinate(0);
		}

		public void setPosition(double position) {
			if(position != getPosition()) {
				moveBy(position - getPosition());
			}
		}
		
		public void moveBy(double delta) {
			super.moveBy(new double[] { delta });
		}

		public Point2D toAbsoluteCoordinate(Point2D p) {
			Point2D l = ((MLine)getParent()).getConnectorShape().fromLineCoordinate(getPosition());
			return new Point2D.Double(p.getX() + l.getX(), p.getY() + l.getY());
		}

		public Point2D toRelativeCoordinate(Point2D p) {
			Point2D l = ((MLine)getParent()).getConnectorShape().fromLineCoordinate(getPosition());
			return new Point2D.Double(p.getX() - l.getX(), p.getY() - l.getY());
		}
	}
	
	private static final int M_INITIAL_SHAPE_SIZE = 30 * 15; // initial
																// Radius for
																// rect and oval

	private static final int M_INITIAL_BRACE_HEIGHT = 15 * 15;

	private static final int M_INITIAL_BRACE_WIDTH = 60 * 15;

	private static final int M_INITIAL_GENEPRODUCT_WIDTH = 80 * 15;

	private static final int M_INITIAL_GENEPRODUCT_HEIGHT = 20 * 15;

	// groups should be behind other graphics 
	// to allow background colors
	private static final int Z_ORDER_GROUP = 0x1000;
	// default order of geneproduct, label, shape and line determined
	// by GenMAPP legacy
	private static final int Z_ORDER_GENEPRODUCT = 0x8000;
	private static final int Z_ORDER_LABEL = 0x7000;
	private static final int Z_ORDER_SHAPE = 0x4000;
	private static final int Z_ORDER_LINE = 0x3000;
	// default order of uninteresting elements.
	private static final int Z_ORDER_DEFAULT = 0x0000;

	/**
	 * default z order for newly created objects
	 */
	private static int getDefaultZOrder(ObjectType value)
	{
		switch (value)
		{
		case SHAPE:
			return Z_ORDER_SHAPE;
		case STATE:
			return Z_ORDER_GENEPRODUCT + 10;
		case DATANODE:
			return Z_ORDER_GENEPRODUCT;
		case LABEL:
			return Z_ORDER_LABEL;
		case LINE:
			return Z_ORDER_LINE;
		case LEGEND:
		case INFOBOX:
		case MAPPINFO:
		case BIOPAX:
			return Z_ORDER_DEFAULT;
		case GROUP:
			return Z_ORDER_GROUP;
		default: 
			throw new IllegalArgumentException("Invalid object type " + value);
		}
	}
	
	/**
	 * Instantiate a pathway element.
	 * The required parameter objectType ensures only objects with a valid type
	 * can be created.
	 * 
	 * @param ot
	 *            Type of object, one of the ObjectType.* fields
	 */
	public static PathwayElement createPathwayElement(ObjectType ot) {
		PathwayElement e = null;
		switch (ot) {
		case GROUP:
			e = new MGroup();
			break;
		case LINE:
			e = new MLine();
			break;
		case STATE:
			e = new MState();
			break;
		default:
			e = new PathwayElement(ot);
			break;
		}
		return e;
	}
	
	protected PathwayElement(ObjectType ot)
	{
		/* set default value for transparency */
		if (ot == ObjectType.LINE || ot == ObjectType.LABEL)
		{
			fillColor = Color.WHITE;
		}
		else
		{
			fillColor = null;
		}
		objectType = ot;
		zOrder = getDefaultZOrder (ot);
	}
		
	int zOrder;
	
	public int getZOrder() 
	{
		return zOrder;
	}
	
	public void setZOrder(int z) {
		if(z != zOrder) {
			zOrder = z;
			fireObjectModifiedEvent(new PathwayEvent(this, PathwayEvent.MODIFIED_GENERAL));
		}
	}
	
	/**
	 * Parent of this object: may be null (for example, when object is in
	 * clipboard)
	 */
	private Pathway parent = null;

	public Pathway getParent()
	{
		return parent;
	}

	/**
	 * Set parent. Do not use this method directly! parent is set automatically
	 * when using Pathway.add/remove
	 * 
	 * This method takes care of graphref reference accounting.
	 * 
	 * @param v
	 *            the parent
	 */
	public void setParent(Pathway v)
	{
		if (v != parent)
		{
			if (parent != null)
			{
				for (MPoint p : mPoints)
				{
					if (p.getGraphRef() != null)
					{
						parent.removeGraphRef(p.getGraphRef(), p);
					}
				}
				if(getGroupRef() != null)
				{
					parent.removeGroupRef(getGroupRef(), this);
				}
				for (MAnchor a : anchors) {
					if (a.getGraphId() != null)
					{
						parent.removeId(a.getGraphId());
					}
				}
				if (graphId != null)
				{
					parent.removeId(graphId);
				}
				if (groupId != null)
				{
					parent.removeGroupId(groupId);
				}
				zOrder = parent.getMaxZOrder() + 1;
			}
			parent = v;
			if (v != null)
			{
				for (MPoint p : mPoints)
				{
					if (p.getGraphRef() != null)
					{
						v.addGraphRef(p.getGraphRef(), p);
					}
				}
				if(getGroupRef() != null)
				{
					v.addGroupRef(getGroupRef(), this);
				}
				for (MAnchor a : anchors) 
				{
					if(a.getGraphId() != null)
					{
						parent.addGraphId(a.getGraphId(), a);
					}
				}
				if (graphId != null)
				{
					parent.addGraphId(graphId, this);
				}
				if (groupId != null)
				{
					parent.addGroupId(groupId, this);
				}
			}
		}
	}

	/**
	 * Returns keys of available static properties and dynamic properties as an object list
	 */
	public Set<Object> getPropertyKeys()
	{
		Set<Object> keys = new HashSet<Object>();
		keys.addAll(getStaticPropertyKeys());
		keys.addAll(getDynamicPropertyKeys());
		return keys;
	}
	
	/**
	 * get all attributes, also the advanced ones
	 * @deprecated use getStaticPropertyKeys or preferably rewrite to use getPropertyKeys
	 */
	public List<PropertyType> getAttributes()
	{
		List<PropertyType> result = new ArrayList<PropertyType>();
		result.addAll (getStaticPropertyKeys());
		return result;
	}
	
	/**
	 * @deprecated PathwayElement doesn't distinguish between advanced / not advanced attributes anymore, 
	 * that distinction is made at the UI level.
	 */
	public List<PropertyType> getAttributes(boolean fAdvanced)
	{
		List<PropertyType> result = new ArrayList<PropertyType>();
		result.addAll (getStaticPropertyKeys());
		return result;
	}

	private static final Map<ObjectType, Set<PropertyType>> ALLOWED_PROPS;

	static {		
		Set<PropertyType> propsCommon = EnumSet.of(
				PropertyType.COMMENTS,
				PropertyType.GRAPHID,
				PropertyType.GROUPREF,
				PropertyType.BIOPAXREF,
				PropertyType.ZORDER
			);
		Set<PropertyType> propsCommonShape = EnumSet.of(
				PropertyType.CENTERX,
				PropertyType.CENTERY,
				PropertyType.WIDTH,
				PropertyType.HEIGHT,
				PropertyType.COLOR
			);
		ALLOWED_PROPS = new EnumMap<ObjectType, Set<PropertyType>>(ObjectType.class);
		{
			Set<PropertyType> propsMappinfo = EnumSet.of (
					PropertyType.COMMENTS,
					PropertyType.MAPINFONAME,
					PropertyType.ORGANISM,
					PropertyType.MAPINFO_DATASOURCE,
					PropertyType.VERSION,
					PropertyType.AUTHOR,
					PropertyType.MAINTAINED_BY,
					PropertyType.EMAIL,
					PropertyType.LAST_MODIFIED,
					PropertyType.AVAILABILITY,
					PropertyType.BOARDWIDTH,
					PropertyType.BOARDHEIGHT,
					PropertyType.WINDOWWIDTH,
					PropertyType.WINDOWHEIGHT
				);
			ALLOWED_PROPS.put (ObjectType.MAPPINFO, propsMappinfo);
		}
		{
			Set<PropertyType> propsState = EnumSet.of(
					PropertyType.RELX,
					PropertyType.RELY,
					PropertyType.WIDTH,
					PropertyType.HEIGHT,
					PropertyType.COLOR,
					PropertyType.FILLCOLOR,
					PropertyType.TRANSPARENT,
					PropertyType.TEXTLABEL,
					PropertyType.MODIFICATIONTYPE,
					PropertyType.LINESTYLE,
					PropertyType.GRAPHREF
				);
			propsState.addAll (propsCommon);
			ALLOWED_PROPS.put (ObjectType.STATE, propsState);
		}
		{			
			Set<PropertyType> propsShape = EnumSet.of(
					PropertyType.FILLCOLOR,
					PropertyType.SHAPETYPE,
					PropertyType.ROTATION,
					PropertyType.TRANSPARENT,
					PropertyType.LINESTYLE
				);
			propsShape.addAll (propsCommon);
			propsShape.addAll (propsCommonShape);
			ALLOWED_PROPS.put (ObjectType.SHAPE, propsShape);
		}
		{
			Set<PropertyType> propsDatanode = EnumSet.of (
					PropertyType.GENEID,
					PropertyType.DATASOURCE,
					PropertyType.TEXTLABEL,
					// PropertyType.XREF,
					PropertyType.BACKPAGEHEAD,
					PropertyType.TYPE
				);
			propsDatanode.addAll (propsCommon);
			propsDatanode.addAll (propsCommonShape);
			ALLOWED_PROPS.put (ObjectType.DATANODE, propsDatanode);
		}
		{
			Set<PropertyType> propsLine = EnumSet.of(
					PropertyType.COLOR,
					PropertyType.STARTX,
					PropertyType.STARTY,
					PropertyType.ENDX,
					PropertyType.ENDY,
					PropertyType.STARTLINETYPE,
					PropertyType.ENDLINETYPE,
					PropertyType.LINESTYLE,
					PropertyType.STARTGRAPHREF,
					PropertyType.ENDGRAPHREF
				);
			propsLine.addAll (propsCommon);
			ALLOWED_PROPS.put (ObjectType.LINE, propsLine);
		}
		{
			Set<PropertyType> propsLabel = EnumSet.of(
					PropertyType.GENMAPP_XREF,
					PropertyType.TEXTLABEL,
					PropertyType.FONTNAME,
					PropertyType.FONTWEIGHT,
					PropertyType.FONTSTYLE,
					PropertyType.FONTSIZE,
					PropertyType.OUTLINE
				);
			propsLabel.addAll (propsCommon);
			propsLabel.addAll (propsCommonShape);
			ALLOWED_PROPS.put (ObjectType.LABEL, propsLabel);
		}
		{			
			Set<PropertyType> propsGroup = EnumSet.of(						
					PropertyType.GROUPID,
					PropertyType.GROUPREF,
					PropertyType.BIOPAXREF,
					PropertyType.GROUPSTYLE,
					PropertyType.TEXTLABEL,
					PropertyType.COMMENTS,
					PropertyType.ZORDER
				);
			ALLOWED_PROPS.put (ObjectType.GROUP, propsGroup);
		}
		{			
			Set<PropertyType> propsInfobox = EnumSet.of(						
					PropertyType.CENTERX,
					PropertyType.CENTERY,
					PropertyType.ZORDER
				);
			ALLOWED_PROPS.put (ObjectType.INFOBOX, propsInfobox);
		}
		{			
			Set<PropertyType> propsLegend = EnumSet.of(						
					PropertyType.CENTERX,
					PropertyType.CENTERY,
					PropertyType.ZORDER
				);
			ALLOWED_PROPS.put (ObjectType.LEGEND, propsLegend);
		}
		{
			Set<PropertyType> propsBiopax = EnumSet.noneOf(PropertyType.class);
			ALLOWED_PROPS.put(ObjectType.BIOPAX, propsBiopax);
		}
	};
	
	
	
	/**
	 * get all attributes that are stored as static members.
	 */
	public Set<PropertyType> getStaticPropertyKeys()
	{
		return ALLOWED_PROPS.get (getObjectType());
	}
	
	/**
	 * Set dynamic or static properties at the same time
	 * Will be replaced with setProperty in the future.
	 */
	public void setPropertyEx (Object key, Object value)
	{
		if (key instanceof PropertyType)
		{
			setStaticProperty((PropertyType)key, value);
		}
		else if (key instanceof String)
		{
			setDynamicProperty((String)key, value.toString());
		}
		else
		{
			throw new IllegalArgumentException();
		}		
	}
	
	public Object getPropertyEx (Object key)
	{
		if (key instanceof PropertyType)
		{
			return getStaticProperty((PropertyType)key);
		}
		else if (key instanceof String)
		{
			return getDynamicProperty ((String)key);
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * @deprecated use setStaticProperty
	 */
	public void setProperty(PropertyType key, Object value)
	{
	}

	/**
	 * This works so that o.setNotes(x) is the equivalent of
	 * o.setProperty("Notes", x);
	 * 
	 * Value may be null in some cases, e.g. graphRef
	 * 
	 * @param key
	 * @param value
	 */
	public void setStaticProperty(PropertyType key, Object value)
	{
		if (!getStaticPropertyKeys().contains(key)) 
			throw new IllegalArgumentException("Property " + key.name() + " is not allowed for objects of type " + getObjectType());
		switch (key)
		{
		case COMMENTS:
			setComments((List<Comment>) value);
			break;
		case COLOR:
			setColor((Color) value);
			break;

		case CENTERX:
			setMCenterX((Double) value);
			break;
		case CENTERY:
			setMCenterY((Double) value);
			break;
		case WIDTH:
			setMWidth((Double) value);
			break;
		case HEIGHT:
			setMHeight((Double) value);
			break;

		case FILLCOLOR:
			setFillColor((Color) value);
			break;
		case SHAPETYPE:
			if(value instanceof ShapeType)
			{
				setShapeType((ShapeType)value);
			}
			else
			{
				setShapeType(ShapeType.fromOrdinal ((Integer) value));
			}
			break;
		case ROTATION:
			setRotation((Double) value);
			break;

		case STARTX:
			setMStartX((Double) value);
			break;
		case STARTY:
			setMStartY((Double) value);
			break;
		case ENDX:
			setMEndX((Double) value);
			break;
		case ENDY:
			setMEndY((Double) value);
			break;
		case ENDLINETYPE:
			if(value instanceof LineType)
				setEndLineType((LineType)value);
			else
				setEndLineType(LineType.fromOrdinal ((Integer) value));
			break;
		case STARTLINETYPE:
			if(value instanceof LineType)
				setStartLineType((LineType)value);
			else
				setStartLineType(LineType.fromOrdinal ((Integer) value));
			break;
		case OUTLINE:
			if(value instanceof OutlineType)
				setOutline((OutlineType)value);
			else
				setOutline(OutlineType.values()[(Integer) value]);
		    break;
		case LINESTYLE:
			setLineStyle((Integer) value);
			break;

		case ORIENTATION:
			setOrientation((Integer) value);
			break;

		case GENEID:
			setGeneID((String) value);
			break;
		case DATASOURCE:
			if (value instanceof DataSource)
			{
				setDataSource((DataSource) value);
			}
			else
			{
				setDataSource(DataSource.getByFullName((String)value));
			}			
			break;
		case GENMAPP_XREF:
			setGenMappXref((String) value);
			break;
		case BACKPAGEHEAD:
			setBackpageHead((String) value);
			break;
		case TYPE:
			setDataNodeType((String) value);
			break;

		case TEXTLABEL:
			setTextLabel((String) value);
			break;
		case FONTNAME:
			setFontName((String) value);
			break;
		case FONTWEIGHT:
			setBold((Boolean) value);
			break;
		case FONTSTYLE:
			setItalic((Boolean) value);
			break;
		case FONTSIZE:
			setMFontSize((Double) value);
			break;
		case MAPINFONAME:
			setMapInfoName((String) value);
			break;
		case ORGANISM:
			setOrganism((String) value);
			break;
		case MAPINFO_DATASOURCE:
			setMapInfoDataSource((String)value);
			break;
		case VERSION:
			setVersion((String) value);
			break;
		case AUTHOR:
			setAuthor((String) value);
			break;
		case MAINTAINED_BY:
			setMaintainer((String) value);
			break;
		case EMAIL:
			setEmail((String) value);
			break;
		case LAST_MODIFIED:
			setLastModified((String) value);
			break;
		case AVAILABILITY:
			setCopyright((String) value);
			break;
		case BOARDWIDTH:
			//ignore, board width is calculated automatically
			break;
		case BOARDHEIGHT:
			//ignore, board width is calculated automatically
			break;
		case WINDOWWIDTH:
			setWindowWidth((Double) value);
			break;
		case WINDOWHEIGHT:
			setWindowHeight((Double) value);
			break;

		case GRAPHID:
			setGraphId((String) value);
			break;
		case STARTGRAPHREF:
			setStartGraphRef((String) value);
			break;
		case ENDGRAPHREF:
			setEndGraphRef((String) value);
			break;
		case GROUPID:
			setGroupId((String) value);
			break;
		case GROUPREF:
			setGroupRef((String) value);
			break;
		case TRANSPARENT:
			setTransparent((Boolean) value);
			break;

		case BIOPAXREF:
			setBiopaxRefs((List<String>) value);
			break;
		case ZORDER:
			setZOrder((Integer)value);
			break;
		case GROUPSTYLE:
			if(value instanceof GroupStyle) {
				setGroupStyle((GroupStyle)value);
			} else {
				setGroupStyle(GroupStyle.fromName((String)value));
			}
		}
	}

	/**
	 * @deprecated use getStaticProperty
	 */
	public Object getProperty(PropertyType x)
	{
		return getStaticProperty(x);
	}
	
	public Object getStaticProperty(PropertyType key)
	{
		if (!getStaticPropertyKeys().contains(key)) 
			throw new IllegalArgumentException("Property " + key.name() + " is not allowed for objects of type " + getObjectType());
		Object result = null;
		switch (key)
		{
		case COMMENTS:
			result = getComments();
			break;
		case COLOR:
			result = getColor();
			break;

		case CENTERX:
			result = getMCenterX();
			break;
		case CENTERY:
			result = getMCenterY();
			break;
		case WIDTH:
			result = getMWidth();
			break;
		case HEIGHT:
			result = getMHeight();
			break;

		case FILLCOLOR:
			result = getFillColor();
			break;
		case SHAPETYPE:
			result = getShapeType();
			break;
		case ROTATION:
			result = getRotation();
			break;

		case STARTX:
			result = getMStartX();
			break;
		case STARTY:
			result = getMStartY();
			break;
		case ENDX:
			result = getMEndX();
			break;
		case ENDY:
			result = getMEndY();
			break;
		case ENDLINETYPE:
			result = getEndLineType();
			break;
		case STARTLINETYPE:
			result = getStartLineType();
			break;
		case OUTLINE:
			result = getOutline();
			break;
		case LINESTYLE:
			result = getLineStyle();
			break;

		case ORIENTATION:
			result = getOrientation();
			break;

		case GENEID:
			result = getGeneID();
			break;
		case DATASOURCE:
			result = getDataSource();
			break;
		case GENMAPP_XREF:
			result = getGenMappXref();
			break;
		case BACKPAGEHEAD:
			result = getBackpageHead();
			break;
		case TYPE:
			result = getDataNodeType();
			break;

		case TEXTLABEL:
			result = getTextLabel();
			break;
		case FONTNAME:
			result = getFontName();
			break;
		case FONTWEIGHT:
			result = isBold();
			break;
		case FONTSTYLE:
			result = isItalic();
			break;
		case FONTSIZE:
			result = getMFontSize();
			break;

		case MAPINFONAME:
			result = getMapInfoName();
			break;
		case ORGANISM:
			result = getOrganism();
			break;
		case MAPINFO_DATASOURCE:
			result = getMapInfoDataSource();
			break;
		case VERSION:
			result = getVersion();
			break;
		case AUTHOR:
			result = getAuthor();
			break;
		case MAINTAINED_BY:
			result = getMaintainer();
			break;
		case EMAIL:
			result = getEmail();
			break;
		case LAST_MODIFIED:
			result = getLastModified();
			break;
		case AVAILABILITY:
			result = getCopyright();
			break;
		case BOARDWIDTH:
			result = getMBoardSize()[0];
			break;
		case BOARDHEIGHT:
			result = getMBoardSize()[1];
			break;
		case WINDOWWIDTH:
			result = getWindowWidth();
			break;
		case WINDOWHEIGHT:
			result = getWindowHeight();
			break;

		case GRAPHID:
			result = getGraphId();
			break;
		case STARTGRAPHREF:
			result = getStartGraphRef();
			break;
		case ENDGRAPHREF:
			result = getEndGraphRef();
			break;
		case GROUPID:
			result = createGroupId();
			break;
		case GROUPREF:
			result = getGroupRef();
			break;
		case TRANSPARENT:
			result = isTransparent();
			break;

		case BIOPAXREF:
			result = getBiopaxRefs();
			break;
		case ZORDER:
			result = getZOrder();
			break;
		case GROUPSTYLE:
			result = getGroupStyle().toString();
			break;
		}

		return result;
	}

	/**
	 * Note: doesn't change parent, only fields
	 * 
	 * Used by UndoAction.
	 * 
	 * @param src
	 */
	public void copyValuesFrom(PathwayElement src)
	{
		attributes = new TreeMap<String, String>(src.attributes); // create copy
		author = src.author;
		copyright = src.copyright;
		backpageHead = src.backpageHead;
		mCenterx = src.mCenterx;
		mCentery = src.mCentery;
		zOrder =  src.zOrder;
		color = src.color;
		fillColor = src.fillColor;
		dataSource = src.dataSource;
		email = src.email;
		fBold = src.fBold;
		fItalic = src.fItalic;
		fontName = src.fontName;
		mFontSize = src.mFontSize;
		fStrikethru = src.fStrikethru;
		fUnderline = src.fUnderline;
		setGeneID = src.setGeneID;
		dataNodeType = src.dataNodeType;
		mHeight = src.mHeight;
		textLabel = src.textLabel;
		lastModified = src.lastModified;
		lineStyle = src.lineStyle;
		startLineType = src.startLineType;
		endLineType = src.endLineType;
		outline = src.outline;
		maintainer = src.maintainer;
		mapInfoDataSource = src.mapInfoDataSource;
		mapInfoName = src.mapInfoName;
		organism = src.organism;
		rotation = src.rotation;
		shapeType = src.shapeType;
		mPoints = new ArrayList<MPoint>();
		for (MPoint p : src.mPoints)
		{
			mPoints.add(new MPoint(p));
		}
		for (MAnchor a : src.anchors) {
			anchors.add(new MAnchor(a));
		}
		comments = new ArrayList<Comment>();
		for (Comment c : src.comments)
		{
			try
			{
				comments.add((Comment) c.clone());
			}
			catch (CloneNotSupportedException e)
			{
				assert (false);
				/* not going to happen */
			}
		}
		version = src.version;
		mWidth = src.mWidth;
		windowHeight = src.windowHeight;
		windowWidth = src.windowWidth;
		genmappxref = src.genmappxref;
		graphId = src.graphId;
		groupId = src.groupId;
		groupRef = src.groupRef;
		groupStyle = src.groupStyle;
		connectorType = src.connectorType;
		biopaxRefs = (List<String>)((ArrayList<String>)src.biopaxRefs).clone();
		if(src.biopax != null) {
			biopax = (Document)src.biopax.clone();			
		}
		fireObjectModifiedEvent(new PathwayEvent(this,
				PathwayEvent.MODIFIED_GENERAL));
	}

	/**
	 * Copy Object. The object will not be part of the same Pathway object, it's
	 * parent will be set to null.
	 * 
	 * No events will be sent to the parent of the original.
	 */
	public PathwayElement copy()
	{
		PathwayElement result = PathwayElement.createPathwayElement(objectType);
		result.copyValuesFrom(this);
		result.parent = null;
		return result;
	}

	protected ObjectType objectType = ObjectType.DATANODE;

	public ObjectType getObjectType()
	{
		return objectType;
	}

	// only for lines
	private List<MPoint> mPoints = Arrays.asList(new MPoint(0, 0), new MPoint(0, 0));

	public void setMPoints(List<MPoint> points) {
		if(points != null) {
			if(points.size() < 2) {
				throw new IllegalArgumentException("Points array should at least have two elements");
			}
			mPoints = points;
			fireObjectModifiedEvent(new PathwayEvent(this, PathwayEvent.MODIFIED_GENERAL));
		}
	}
	
	public MPoint getMStart()
	{
		return mPoints.get(0);
	}

	public void setMStart(MPoint p)
	{
		getMStart().moveTo(p);
	}

	public MPoint getMEnd()
	{
		return mPoints.get(mPoints.size() - 1);
	}

	public void setMEnd(MPoint p)
	{
		getMEnd().moveTo(p);
	}

	public List<MPoint> getMPoints()
	{
		return mPoints;
	}

	public double getMStartX()
	{
		return getMStart().getX();
	}

	public void setMStartX(double v)
	{
		getMStart().setX(v);
	}

	public double getMStartY()
	{
		return getMStart().getY();
	}

	public void setMStartY(double v)
	{
		getMStart().setY(v);
	}

	public double getMEndX()
	{
		return mPoints.get(mPoints.size() - 1).getX();
	}

	public void setMEndX(double v)
	{
		getMEnd().setX(v);
	}

	public double getMEndY()
	{
		return getMEnd().getY();
	}

	public void setMEndY(double v)
	{
		getMEnd().setY(v);
	}

	protected int lineStyle = LineStyle.SOLID;

	public int getLineStyle()
	{
		return lineStyle;
	}

	public void setLineStyle(int value)
	{
		if (lineStyle != value)
		{
			lineStyle = value;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected LineType endLineType = LineType.LINE;
	protected LineType startLineType = LineType.LINE;

	public LineType getStartLineType()
	{
		return startLineType == null ? LineType.LINE : startLineType;
	}

	public LineType getEndLineType()
	{
		return endLineType == null ? LineType.LINE : endLineType;
	}

	public void setStartLineType(LineType value)
	{
		if (startLineType != value)
		{
			startLineType = value;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	public void setEndLineType(LineType value)
	{
		if (endLineType != value)
		{
			endLineType = value;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}
	
	private ConnectorType connectorType = ConnectorType.STRAIGHT;
	
	public void setConnectorType(ConnectorType type) {
		if(connectorType == null) {
			throw new IllegalArgumentException();
		}
		if (!connectorType.equals(type))
		{
			connectorType = type;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}
	
	public ConnectorType getConnectorType() {
		return connectorType;
	}
	
//TODO: end of new elements
	protected List<MAnchor> anchors = new ArrayList<MAnchor>();
	
	/**
	 * Get the anchors for this line.
	 * @return A list with the anchors, or an empty list, if no anchors are defined
	 */
	public List<MAnchor> getMAnchors() {
		return anchors;
	}
	
	/**
	 * Add a new anchor to this line at the given position.
	 * @param position The relative position on the line, between 0 (start) to 1 (end).
	 */
	public MAnchor addMAnchor(double position) {
		if(position < 0 || position > 1) {
			throw new IllegalArgumentException(
					"Invalid position value '" + position + 
					"' must be between 0 and 1");
		}
		MAnchor anchor = new MAnchor(position);
		anchors.add(anchor);
		fireObjectModifiedEvent(new PathwayEvent(
				this, PathwayEvent.MODIFIED_GENERAL));
		return anchor;
	}
	
	/**
	 * Remove the given anchor
	 */
	public void removeMAnchor(MAnchor anchor) {
		anchors.remove(anchor);
		fireObjectModifiedEvent(new PathwayEvent(
				this, PathwayEvent.MODIFIED_GENERAL));
	}
	
	protected Color color = new Color(0, 0, 0);

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color v)
	{
		if (v == null)
			throw new IllegalArgumentException();
		if (color != v)
		{
			color = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	/**
	   a fillcolor of null is equivalent to transparent.
	 */
	protected Color fillColor = null;

	public Color getFillColor()
	{
		return fillColor;
	}

	public void setFillColor(Color v)
	{
		if (fillColor != v)
		{
			fillColor = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	/**
	   checks if fill color is equal to null or the alpha value is equal to 0.
	 */
	public boolean isTransparent()
	{
		return fillColor == null || fillColor.getAlpha() == 0;
	}

	/**
	   sets the alpha component of fillColor to 0 if true
	   sets the alpha component of fillColor to 255 if true
	 */
	public void setTransparent(boolean v)
	{
		if (isTransparent() != v)
		{
			if(fillColor == null) {
				fillColor = Color.WHITE;
			}
			int alpha = v ? 0 : 255;
			fillColor = new Color(
					fillColor.getRed(), 
					fillColor.getGreen(), 
					fillColor.getBlue(), 
					alpha);

			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	// general
	List<Comment> comments = new ArrayList<Comment>();

	public List<Comment> getComments()
	{
		return comments;
	}

	public void setComments(List<Comment> value)
	{
		if (comments != value)
		{
			comments = value;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	public void addComment(String comment, String source)
	{
		addComment(new Comment(comment, source));
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
		fireObjectModifiedEvent(new PathwayEvent(this,
				PathwayEvent.MODIFIED_GENERAL));
	}
	
	public void removeComment(Comment comment) {
		comments.remove(comment);
		fireObjectModifiedEvent(new PathwayEvent(this, PathwayEvent.MODIFIED_GENERAL));
	}

	/**
	 * Finds the first comment with a specific source
	 */
	public String findComment(String source)
	{
		for (Comment c : comments)
		{
			if (source.equals(c.source))
			{
				return c.comment;
			}
		}
		return null;
	}

	/** @deprecated */
	protected String comment = "";

	/** @deprecated */
	public String getComment()
	{
		return comment;
	}

	/** @deprecated */
	public void setComment(String v)
	{
		if (v == null)
			throw new IllegalArgumentException();
		if (comment != v)
		{
			comment = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String genmappxref = null;

	/**
	   access to the Label/Xref and DataNode/GenMAPPXref attributes
	   For backwards compatibility with GenMAPP only.
	 */
	public String getGenMappXref()
	{
		return genmappxref;
	}

	/**
	   access to the Label/Xref and DataNode/GenMAPPXref attributes
	   For backwards compatibility with GenMAPP only.
	 */
	public void setGenMappXref(String v)
	{
		if (genmappxref != v)
		{
			genmappxref = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String setGeneID = "";

	public String getGeneID()
	{
		return setGeneID;
	}

	public void setGeneID(String v)
	{
		if (v == null)
			throw new IllegalArgumentException();
		if (setGeneID != v)
		{
			setGeneID = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String backpageHead = null;

	public String getBackpageHead()
	{
		return backpageHead;
	}

	public void setBackpageHead(String v)
	{
		if (backpageHead != v)
		{
			backpageHead = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String dataNodeType = "Unknown";

	public String getDataNodeType()
	{
		return dataNodeType;
	}

	public void setDataNodeType(DataNodeType type) {
		setDataNodeType(type.getGpmlName());
	}
	
	public void setDataNodeType(String v)
	{
		if (v == null)
			throw new IllegalArgumentException();
		if (dataNodeType != v)
		{
			dataNodeType = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	/**
	 * The pathway datasource
	 */
	protected DataSource dataSource = null;

	public DataSource getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(DataSource v)
	{
		if (dataSource != v)
		{
			dataSource = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	/**
	 * returns GeneID and datasource combined in an Xref.
	 * Only meaningful for datanodes.
	 * 
	 * Same as 
	 * new Xref (
	 * 		pathwayElement.getGeneID(), 
	 * 		pathwayElement.getDataSource()
	 * );
	 */
	public Xref getXref()
	{
		//TODO: Store Xref by default, derive setGeneID and dataSource from it.
		return new Xref (setGeneID, dataSource);
	}
	
	/**
	 * SystemCode is a one- or two-letter abbreviation of datasource, used in
	 * the MappFormat but also in databases.
	 * @deprecated Use getDataSource().getSystemCode() instead.
	 */
	public String getSystemCode()
	{
		if (dataSource == null)
		{
			return null;
		}
		return dataSource.getSystemCode();
	}

	protected double mCenterx = 0;

	public double getMCenterX()
	{
		return mCenterx;
	}

	public void setMCenterX(double v)
	{
		if (mCenterx != v)
		{
			mCenterx = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected double mCentery = 0;

	public double getMCenterY()
	{
		return mCentery;
	}

	public void setMCenterY(double v)
	{
		if (mCentery != v)
		{
			mCentery = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected double mWidth = 0;

	public double getMWidth()
	{
		return mWidth;
	}

	public void setMWidth(double v)
	{
		if(mWidth < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + v);
		}
		if (mWidth != v)
		{
			mWidth = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected double mHeight = 0;

	public double getMHeight()
	{
		return mHeight;
	}
	
	public void setMHeight(double v)
	{
		if(mWidth < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + v);
		}
		if (mHeight != v)
		{
			mHeight = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	// starty for shapes
	public double getMTop()
	{
		return mCentery - mHeight / 2;
	}

	public void setMTop(double v)
	{
		mCentery = v + mHeight / 2;
		fireObjectModifiedEvent(new PathwayEvent(this,
				PathwayEvent.MODIFIED_GENERAL));
	}

	// startx for shapes
	public double getMLeft()
	{
		return mCenterx - mWidth / 2;
	}

	public void setMLeft(double v)
	{
		mCenterx = v + mWidth / 2;
		fireObjectModifiedEvent(new PathwayEvent(this,
				PathwayEvent.MODIFIED_GENERAL));
	}

	protected ShapeType shapeType = ShapeType.RECTANGLE;

	public ShapeType getShapeType()
	{
		return shapeType;
	}

	public void setShapeType(ShapeType v)
	{
		if (shapeType != v)
		{
			shapeType = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	public void setOrientation(int orientation)
	{
		switch (orientation)
		{
		case OrientationType.TOP:
			setRotation(0);
			break;
		case OrientationType.LEFT:
			setRotation(Math.PI * (3.0 / 2));
			break;
		case OrientationType.BOTTOM:
			setRotation(Math.PI);
			break;
		case OrientationType.RIGHT:
			setRotation(Math.PI / 2);
			break;
		}
	}

	public int getOrientation()
	{
		double r = rotation / Math.PI;
		if (r < 1.0 / 4 || r >= 7.0 / 4)
			return OrientationType.TOP;
		if (r > 5.0 / 4 && r <= 7.0 / 4)
			return OrientationType.LEFT;
		if (r > 3.0 / 4 && r <= 5.0 / 4)
			return OrientationType.BOTTOM;
		if (r > 1.0 / 4 && r <= 3.0 / 4)
			return OrientationType.RIGHT;
		return 0;
	}

	protected double rotation = 0; // in radians

	public double getRotation()
	{
		return rotation;
	}

	public void setRotation(double v)
	{
		if (rotation != v)
		{
			rotation = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}

	}

	/**
	 * Get the rectangular bounds of the object
	 * after rotation is applied
	 */
	public Rectangle2D getRBounds() {
		Rectangle2D bounds = getMBounds();
		AffineTransform t = new AffineTransform();
		t.rotate(getRotation(), getMCenterX(), getMCenterY());
		bounds = t.createTransformedShape(bounds).getBounds2D();
		return bounds;
	}
	
	/**
	 * Get the rectangular bounds of the object
	 * without rotation taken into accound
	 */
	public Rectangle2D getMBounds() {
		return new Rectangle2D.Double(getMLeft(), getMTop(), getMWidth(), getMHeight());
	}
		
	// for labels
	protected boolean fBold = false;

	public boolean isBold()
	{
		return fBold;
	}

	public void setBold(boolean v)
	{
		if (fBold != v)
		{
			fBold = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected boolean fStrikethru = false;

	public boolean isStrikethru()
	{
		return fStrikethru;
	}

	public void setStrikethru(boolean v)
	{
		if (fStrikethru != v)
		{
			fStrikethru = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected boolean fUnderline = false;

	public boolean isUnderline()
	{
		return fUnderline;
	}

	public void setUnderline(boolean v)
	{
		if (fUnderline != v)
		{
			fUnderline = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected boolean fItalic = false;

	public boolean isItalic()
	{
		return fItalic;
	}

	public void setItalic(boolean v)
	{
		if (fItalic != v)
		{
			fItalic = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String fontName = "Arial";

	public String getFontName()
	{
		return fontName;
	}

	public void setFontName(String v)
	{
		if (v == null)
			throw new IllegalArgumentException();
		if (fontName != v)
		{
			fontName = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String textLabel = "";

	public String getTextLabel()
	{
		return textLabel;
	}

	public void setTextLabel(String v)
	{
		if (v == null)
			throw new IllegalArgumentException();
		if (textLabel != v)
		{
			textLabel = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected double mFontSize = 10 * 15;

	public double getMFontSize()
	{
		return mFontSize;
	}

	public void setMFontSize(double v)
	{
		if (mFontSize != v)
		{
			mFontSize = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String mapInfoName = "untitled";

	public String getMapInfoName()
	{
		return mapInfoName;
	}

	/* 
	 * maximum length of pathway title. GenMAPP MAPP format imposes this limit,
	 * so we have it too to be backwards compatible.
	 */
	public static final int MAP_TITLE_MAX_LEN = 50;
	
	public void setMapInfoName(String v)
	{
		if (v == null)
			throw new IllegalArgumentException();
			
		if (mapInfoName != v)
		{
			if (v.length() > MAP_TITLE_MAX_LEN)
			{
				throw new IllegalArgumentException("Map info name exceeds maximum length of " + MAP_TITLE_MAX_LEN);				
			}
			else
			{
				mapInfoName = v;
			}
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String organism = null;

	public String getOrganism()
	{
		return organism;
	}

	public void setOrganism(String v)
	{
		if (organism != v)
		{
			organism = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String mapInfoDataSource = null;

	public String getMapInfoDataSource()
	{
		return mapInfoDataSource;
	}

	public void setMapInfoDataSource(String v)
	{
		if (mapInfoDataSource != v)
		{
			mapInfoDataSource = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected OutlineType outline = OutlineType.NONE;
	public void setOutline (OutlineType v)
	{
		if (outline != v)
		{
			outline = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}
	public OutlineType getOutline()
	{
		return outline;
	}
	
	protected String version = null;

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String v)
	{
		if (version != v)
		{
			version = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String author = null;

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String v)
	{
		if (author != v)
		{
			author = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String maintainer = null;

	public String getMaintainer()
	{
		return maintainer;
	}

	public void setMaintainer(String v)
	{
		if (maintainer != v)
		{
			maintainer = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String email = null;

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String v)
	{
		if (email != v)
		{
			email = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String copyright = null;

	public String getCopyright()
	{
		return copyright;
	}

	public void setCopyright(String v)
	{
		if (copyright != v)
		{
			copyright = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	protected String lastModified = null;

	public String getLastModified()
	{
		return lastModified;
	}

	public void setLastModified(String v)
	{
		if (lastModified != v)
		{
			lastModified = v;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}
	
	/**
	 * Calculates the drawing size on basis of the location and size of the
	 * containing pathway elements
	 * @return The drawing size
	 */
	public double[] getMBoardSize() {
		return parent.calculateMBoardSize();
	}
	
	public double getMBoardWidth()
	{
		return getMBoardSize()[0];
	}

	public double getMBoardHeight()
	{
		return getMBoardSize()[1];
	}

	protected double windowWidth;

	/**
	 * GenMAPP Legacy attribute maintained only for reverse compatibility
	 * reasons, no longer used by PathVisio
	 */
	public double getWindowWidth()
	{
		return windowWidth;
	}

	/**
	 * GenMAPP Legacy attribute maintained only for reverse compatibility
	 * reasons, no longer used by PathVisio
	 */
	public void setWindowWidth(double v)
	{
		if (windowWidth != v)
		{
			windowWidth = v;
			fireObjectModifiedEvent(new PathwayEvent(this, PathwayEvent.WINDOW));
		}
	}

	protected double windowHeight;

	/**
	 * GenMAPP Legacy attribute maintained only for reverse compatibility
	 * reasons, no longer used by PathVisio
	 */
	public double getWindowHeight()
	{
		return windowHeight;
	}

	/**
	 * GenMAPP Legacy attribute maintained only for reverse compatibility
	 * reasons, no longer used by PathVisio
	 */
	public void setWindowHeight(double v)
	{
		if (windowHeight != v)
		{
			windowHeight = v;
			fireObjectModifiedEvent(new PathwayEvent(this, PathwayEvent.WINDOW));
		}
	}

	/* AP20070508 */
	protected String groupId;

	protected String graphId;

	protected String groupRef;

	protected GroupStyle groupStyle;

	public String doGetGraphId()
	{
		return graphId;
	}

	public String getGroupRef()
	{
		return groupRef;
	}

	public void setGroupRef(String s)
	{
		if (groupRef == null || !groupRef.equals(s))
		{
			if (parent != null)
			{
				if (groupRef != null)
				{
					parent.removeGroupRef(groupRef, this);
				}
				// Check: move add before remove??
				if (s != null)
				{
					parent.addGroupRef(s, this);
				}
			}
			groupRef = s;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	public String getGroupId()
	{
		return groupId;
	}

	public String createGroupId()
	{
		if (groupId == null)
		{
			setGroupId(parent.getUniqueGroupId());
		}
		return groupId;
	}

	public void setGroupStyle(GroupStyle gs)
	{
		if(groupStyle != gs) {
			groupStyle = gs;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	public GroupStyle getGroupStyle()
	{
		if(groupStyle == null) {
			groupStyle = GroupStyle.NONE;
		}
		return groupStyle;
	}

	/**
	 * Set groupId. This id must be any string unique within the Pathway object
	 * 
	 * @see Pathway#getUniqueId()
	 */
	public void setGroupId(String w)
	{
		if (groupId == null || !groupId.equals(w))
		{
			if (parent != null)
			{
				if (groupId != null)
				{
					parent.removeId(groupId);
				}
				// Check: move add before remove??
				if (w != null)
				{
					parent.addGroupId(w, this);
				}
			}
			groupId = w;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}

	}
	
	/**
	 * Identifies the parent group of a child element via groupId and calls isChanged()
	 * 
	 */
	public void notifyParentGroup()
	{
		String ref = this.getGroupRef();
		if (ref != null)
		{ //identify group object and notify model change to trigger view update
			if (((MGroup) this.getParent().getGroupById(ref)) != null){
			((MGroup) this.getParent().getGroupById(ref)).isChanged();
			}
		}	
	}
	
	private String graphRef = null;
	
	/** graphRef property, used by Modification */
	public String getGraphRef()
	{
		return graphRef;
	}
	
	/** 
	 * set graphRef property, used by Modification
	 * The new graphRef should exist and point to an existing DataNode
	 */
	public void setGraphRef (String value)
	{
		// TODO: check that new graphRef exists and that it points to a DataNode
		if (!(graphRef == null ? value == null : graphRef.equals(value)))
		{
			graphRef = value;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}
	
	private double relX;
	
	/**
	 * relX property, used by Modification.
	 * Should normally be between -1.0 and 1.0, where 1.0 
	 * corresponds to the edge of the parent object
	 */
	public double getRelX()
	{
		return relX;
	}
	
	/**
	 * See getRelX
	 */
	public void setRelX(double value)
	{
		if (relX != value)
		{
			relX = value;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	private double relY;
	
	/**
	 * relX property, used by Modification.
	 * Should normally be between -1.0 and 1.0, where 1.0 
	 * corresponds to the edge of the parent object
	 */
	public double getRelY()
	{
		return relY;
	}
	
	/**
	 * See getRelX
	 */
	public void setRelY(double value)
	{
		if (relY != value)
		{
			relY = value;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	
	public String getGraphId()
	{
		return graphId;
	}

	/**
	 * Set graphId. This id must be any string unique within the Pathway object
	 * 
	 * @see Pathway#getUniqueId()
	 */
	public void setGraphId(String v)
	{
		GraphLink.setGraphId(v, this, parent);
		graphId = v;
		fireObjectModifiedEvent(new PathwayEvent(this,
				PathwayEvent.MODIFIED_GENERAL));
	}

	public String setGeneratedGraphId()
	{
		setGraphId(parent.getUniqueGraphId());
		return graphId;
	}

	public String getStartGraphRef()
	{
		return mPoints.get(0).getGraphRef();
	}

	public void setStartGraphRef(String ref)
	{
		MPoint start = mPoints.get(0);
		start.setGraphRef(ref);
	}

	public String getEndGraphRef()
	{
		return mPoints.get(mPoints.size() - 1).getGraphRef();
	}

	public void setEndGraphRef(String ref)
	{
		MPoint end = mPoints.get(mPoints.size() - 1);
		end.setGraphRef(ref);
	}

	protected Document biopax;

	public Document getBiopax()
	{
		return biopax;
	}

	BiopaxReferenceManager bpRefMgr;
	
	public BiopaxReferenceManager getBiopaxReferenceManager() {
		if(bpRefMgr == null) {
			bpRefMgr = new BiopaxReferenceManager(this);
		}
		return bpRefMgr;
	}
	
	public void setBiopax(Document bp)
	{
		biopax = bp;
		if(parent != null) parent.getBiopaxElementManager().refresh();
	}

	protected List<String> biopaxRefs = new ArrayList<String>();

	public List<String> getBiopaxRefs()
	{
		return biopaxRefs;
	}

	public void setBiopaxRefs(List<String> refs) {
		if(refs != null && !biopaxRefs.equals(refs)) {
			biopaxRefs = refs;
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}
	
	public void addBiopaxRef(String ref)
	{
		if (ref != null && !biopaxRefs.contains(ref))
		{
			biopaxRefs.add(ref);
			fireObjectModifiedEvent(new PathwayEvent(this,
					PathwayEvent.MODIFIED_GENERAL));
		}
	}

	public void removeBiopaxRef(String ref) 
	{
		if(ref != null) {
			boolean changed = biopaxRefs.remove(ref);
			if(changed) {
				fireObjectModifiedEvent(new PathwayEvent(this,
						PathwayEvent.MODIFIED_GENERAL));
			}
		}
	}
	
	public PathwayElement[] splitLine()
	{
		double centerX = (getMStartX() + getMEndX()) / 2;
		double centerY = (getMStartY() + getMEndY()) / 2;
		PathwayElement l1 = new PathwayElement(ObjectType.LINE);
		l1.copyValuesFrom(this);
		l1.setMStartX(getMStartX());
		l1.setMStartY(getMStartY());
		l1.setMEndX(centerX);
		l1.setMEndY(centerY);
		PathwayElement l2 = new PathwayElement(ObjectType.LINE);
		l2.copyValuesFrom(this);
		l2.setMStartX(centerX);
		l2.setMStartY(centerY);
		l2.setMEndX(getMEndX());
		l2.setMEndY(getMEndY());
		return new PathwayElement[] { l1, l2 };
	}

	int noFire = 0;

	public void dontFireEvents(int times)
	{
		noFire = times;
	}

	private List<PathwayElementListener> listeners = new ArrayList<PathwayElementListener>();

	public void addListener(PathwayElementListener v)
	{
		if(!listeners.contains(v)) listeners.add(v);
	}

	public void removeListener(PathwayElementListener v)
	{
		listeners.remove(v);
	}

	public void fireObjectModifiedEvent(PathwayEvent e)
	{
		if (noFire > 0)
		{
			noFire -= 1;
			return;
		}
		if (parent != null) parent.childModified(e);
		for (PathwayElementListener g : listeners)
		{
			g.gmmlObjectModified(e);
		}
	}

	/**
	 * This sets the object to a suitable default size.
	 * 
	 * This method is intended to be called right after the object is placed on
	 * the drawing with a click.
	 */
	public void setInitialSize()
	{

		switch (objectType)
		{
		case SHAPE:
			if (shapeType == ShapeType.BRACE)
			{
				setMWidth(M_INITIAL_BRACE_WIDTH);
				setMHeight(M_INITIAL_BRACE_HEIGHT);
			} else
			{
				setMWidth(M_INITIAL_SHAPE_SIZE);
				setMHeight(M_INITIAL_SHAPE_SIZE);
			}
			break;
		case DATANODE:
			setMWidth(M_INITIAL_GENEPRODUCT_WIDTH);
			setMHeight(M_INITIAL_GENEPRODUCT_HEIGHT);
			break;
		case LINE:
			setMEndX(getMStartX() + M_INITIAL_SHAPE_SIZE);
			setMEndY(getMStartY() + M_INITIAL_SHAPE_SIZE);
			break;
		}
	}

	public Set<GraphRefContainer> getReferences()
	{
		return GraphLink.getReferences(this, parent);
	}

	/**
	   @deprecated Use getParent() instead
	 */
	public Pathway getGmmlData()
	{
		return parent;
	}

	public int compareTo(PathwayElement o) {
		return getZOrder() - o.getZOrder();
	}

	public Point2D toAbsoluteCoordinate(Point2D p) {
		double x = p.getX();
		double y = p.getY();
		Rectangle2D bounds = getRBounds();
		//Scale
		if(bounds.getWidth() != 0) x *= bounds.getWidth() / 2;
		if(bounds.getHeight() != 0) y *= bounds.getHeight() / 2;
		//Translate
		x += bounds.getCenterX();
		y += bounds.getCenterY();
		return new Point2D.Double(x, y);
	}

	public Point2D toRelativeCoordinate(Point2D p) {
		double relX = p.getX();
		double relY = p.getY();
		Rectangle2D bounds = getRBounds();
		//Translate
		relX -= bounds.getCenterX();
		relY -= bounds.getCenterY();
		//Scalebounds.getCenterX();
		if(relX != 0 && bounds.getWidth() != 0) relX /= bounds.getWidth() / 2;
		if(relY != 0 && bounds.getHeight() != 0) relY /= bounds.getHeight() / 2;
		return new Point2D.Double(relX, relY);
	}
}
