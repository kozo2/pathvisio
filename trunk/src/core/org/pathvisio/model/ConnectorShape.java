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

import java.awt.geom.Point2D;

import org.pathvisio.view.LinAlg;
import org.pathvisio.view.LinAlg.Point;


/**
 * Implement this to provide a line shape for connectors
 * @author thomas
 *
 */
public interface ConnectorShape {
	
	public static final int AXIS_X = 0;
	public static final int AXIS_Y = 1;
	
	/**
	 * Force the connector to redraw it's path. The cache for segments,
	 * waypoints and shape.
	 * @param restrictions The ConnectorRestrictions that provides the start, end and
	 * preferred waypoints
	 */
	public void recalculateShape(ConnectorRestrictions restrictions);
	
	/**
	 * Get the shape that represents the connector path
	 */
	public java.awt.Shape getShape();
	
	/**
	 * Get the individual segments of the path
	 */
	public Segment[] getSegments();
	
	
	/**
	 * Get the waypoints through which the connector passes
	 */
	public WayPoint[] getWayPoints();
	
	/**
	 * Checks whether the waypoints as provided by the ConnectorRestrictions
	 * are valid and will be used to draw the connector path
	 * @return true if the waypoints are used, false if not
	 */
	public boolean hasValidWaypoints(ConnectorRestrictions restrictions);
	
	/**
	 * A waypoint, a point through which the connector passes. Each waypoint
	 * will have a handle in the view, so the user can modify it's position.
	 */
	public class WayPoint extends Point2D.Double {		
		public WayPoint(Point2D position) {
			super(position.getX(), position.getY());
		}
		
		public WayPoint(double x, double y) {
			super(x, y);
		}
	}
	
	/**
	 * A single segment of the connector path.
	 * @author thomas
	 */
	public class Segment {
		private Point2D start, end;
		
		protected Segment(Point2D start, Point2D end) {
			this.start = start;
			this.end = end;
		}
		
		public Point2D getMEnd() {
			return end;
		}
		
		public Point2D getMStart() {
			return start;
		}
		
		public void setMEnd(Point2D end) {
			this.end = end;
		}
		
		public void setMStart(Point2D start) {
			this.start = start;
		}
		
		public Point2D getMCenter() {
			return new Point2D.Double(
					start.getX() + (end.getX() - start.getX()) / 2,
					start.getY() + (end.getY() - start.getY()) / 2
			);
		}
		
		public double getMLength() {
			return LinAlg.distance(new Point(start), new Point(end));
		}
		
		public String toString() {
			return start + ", " + end;
		}
	}

	/**
	 * Translates a 1-dimensional line coordinate to a 2-dimensional
	 * view coordinate.
	 */
	public Point2D fromLineCoordinate(double l);
	
	/**
	 * Translates a 2-dimensional view coordinate to a 1-dimensional
	 * line coordinate.
	 */
	public double toLineCoordinate(Point2D v);
}