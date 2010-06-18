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

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.pathvisio.model.ConnectorShape.WayPoint;
import org.pathvisio.model.GraphLink.GraphIdContainer;

/**
 * Line specific implementation of methods that calculate derived
 * coordinates that are not stored in GPML directly
 * @author thomas
 */
public class MLine extends PathwayElement implements ConnectorRestrictions {
	ConnectorShape shape;
	
	public MLine() {
		super(ObjectType.LINE);
	}
	
	public ConnectorShape getConnectorShape() {
		String type = getConnectorType().getName();
		
		//Recreate the ConnectorShape when it's null or when the type
		//doesn't match the implementing class
		if(shape == null || !shape.getClass().equals(ConnectorShapeFactory.getImplementingClass(type))) {
			shape = ConnectorShapeFactory.createConnectorShape(getConnectorType().getName());
			shape.recalculateShape(this);
		}
		return shape;
	}
	
	public double getMCenterX()
	{
		double start = getMStart().getX();
		double end = getMEnd().getX();
		return start + (end - start) / 2;
	}
	
	public double getMCenterY()
	{
		double start = getMStart().getY();
		double end = getMEnd().getY();
		return start + (end - start) / 2;
	}
	
	public double getMLeft()
	{
		double start = getMStart().getX();
		double end = getMEnd().getX();
		return Math.min(start, end);
	}
	
	public double getMWidth()
	{
		double start = getMStart().getX();
		double end = getMEnd().getX();
		return Math.abs(start-end);
	}
	
	public double getMHeight()
	{
		double start = getMStart().getY();
		double end = getMEnd().getY();
		return Math.abs(start-end);
	}	
	
	public double getMTop()
	{
		double start = getMStart().getY();
		double end = getMEnd().getY();
		return Math.min(start, end);
	}
	
	/**
	 * Sets the position of the top side
	 * of the rectangular bounds of the line
	 */
	public void setMTop(double v) {
		if(getDirectionY() > 0) {
			setMStartY(v);
		} else {
			setMEndY(v);
		}
	}
	
	/**
	 * Sets the position of the left side
	 * of the rectangular bounds of the line
	 */
	public void setMLeft(double v) {
		if(getDirectionX() > 0) {
			setMStartX(v);
		} else {
			setMEndX(v);
		}
	}
	
	/**
	 * Sets the x position of the center of the line
	 */
	public void setMCenterX(double v) {
		double dx = v - getMCenterX();
		setMStartX(getMStartX() + dx);
		setMEndX(getMEndX() + dx);
	}
	
	/**
	 * Sets the y position of the center of the line
	 */
	public void setMCenterY(double v) {
		double dy = v - getMCenterY();
		setMStartY(getMStartY() + dy);
		setMEndY(getMEndY() + dy);
	}
	
	
	private int getDirectionX() {
		return (int)Math.signum(getMEndX() - getMStartX());
	}
	
	private int getDirectionY() {
		return (int)Math.signum(getMEndY() - getMStartY());
	}

	public Point2D getEndPoint() {
		return getMEnd().toPoint2D();
	}
	
	public Point2D getStartPoint() {
		return getMStart().toPoint2D();
	}
	
	public List<Point2D> getPoints() {
		List<Point2D> pts = new ArrayList<Point2D>();
		for(MPoint p : getMPoints()) {
			pts.add(p.toPoint2D());
		}
		return pts;
	}
	
	private GraphIdContainer getStartElement() {
		Pathway parent = getParent();
		if(parent != null) {
			return parent.getGraphIdContainer(getStartGraphRef());
		}
		return null;
	}
	
	private GraphIdContainer getEndElement() {
		Pathway parent = getParent();
		if(parent != null) {
			return parent.getGraphIdContainer(getEndGraphRef());
		}
		return null;
	}
	
	public int getStartSide() {
		int side = SIDE_WEST;
		
		GraphIdContainer e = getStartElement();
		if(e != null) {
			if(e instanceof PathwayElement) {
				side = getSide(getMStart().getRelX(), getMStart().getRelY());
			} else if(e instanceof MAnchor) {
				side = getOppositeSide(
						getSide(getMStartX(), getMStartY(), getMEndX(), getMEndY())
				);
			}
		}
		return side;
	}

	public int getEndSide() {
		int side = SIDE_EAST;
		
		GraphIdContainer e = getEndElement();
		if(e != null) {
			if(e instanceof PathwayElement) {
				side = getSide(getMEnd().getRelX(), getMEnd().getRelY());
			} else if(e instanceof MAnchor) {
				side = getOppositeSide(
						getSide(getMEndX(), getMEndY(), getMStartX(), getMStartY())
				);
			}
		}
		return side;
	}
	
	public void adjustWayPointPreferences(WayPoint[] waypoints) {
		List<MPoint> mpoints = getMPoints();
		for(int i = 0; i < waypoints.length; i++) {
			WayPoint wp = waypoints[i];
			MPoint mp = mpoints.get(i + 1);
			if(mp.getX() != wp.getX() || mp.getY() != wp.getY()) {
				dontFireEvents(1);
				mp.moveTo(wp.getX(), wp.getY());
			}
		}
	}
	
	public void resetWayPointPreferences() {
		List<MPoint> mps = getMPoints();
		while(mps.size() > 2) {
			mps.remove(mps.size() - 2);
		}
	}
	
	public WayPoint[] getWayPointPreferences() {
		List<MPoint> pts = getMPoints();
		WayPoint[] wps = new WayPoint[pts.size() - 2];
		for(int i = 0; i < wps.length; i++) {
			wps[i] = new WayPoint(pts.get(i + 1).toPoint2D());
		}
		return wps;
	}
	
	/**
	 * Get the side of the given pathway element to which
	 * the x and y coordinates connect
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param e The element to find the side of
	 * @return One of the SIDE_* constants
	 */
	private static int getSide(double x, double y, double cx, double cy) {
		return getSide(x - cx, y - cy);
	}
	
	private static int getSide(double relX, double relY) {
		int direction = 0;
		if(Math.abs(relX) > Math.abs(relY)) {
			if(relX > 0) {
				direction = SIDE_EAST;
			} else {
				direction = SIDE_WEST;
			}
		} else {
			if(relY > 0) {
				direction = SIDE_SOUTH;
			} else {
				direction = SIDE_NORTH;
			}
		}
		return direction;
	}
	
	private int getOppositeSide(int side) {
		switch(side) {
		case SIDE_EAST:
			return SIDE_WEST;
		case SIDE_WEST:
			return SIDE_EAST;
		case SIDE_NORTH:
			return SIDE_SOUTH;
		case SIDE_SOUTH:
			return SIDE_NORTH;
		}
		return -1;
	}
	
	public Shape mayCross(Point2D point) {
		Pathway parent = getParent();
		Rectangle2D rect = null;
		if(parent != null) {
			for(PathwayElement e : parent.getDataObjects()) {
				ObjectType ot = e.getObjectType();
				if(ot == ObjectType.SHAPE || ot == ObjectType.DATANODE || ot == ObjectType.LABEL) {
					Rectangle2D b = e.getMBounds();
					if(b.contains(point)) {
						if(rect == null) rect = b;
						else rect.add(b);
					}
				}
			}
		}
		return rect;
	}
}