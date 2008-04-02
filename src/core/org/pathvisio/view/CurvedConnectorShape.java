package org.pathvisio.view;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class CurvedConnectorShape extends ElbowConnectorShape {

	public Shape getShape(ConnectorRestrictions restrictions) {
		GeneralPath path = new GeneralPath();
		Segment[] segments = getSegments(restrictions);
		Segment first = segments[0];
		Segment last = segments[segments.length - 1];
		path.moveTo(
				(float)first.getStart().getX(), 
				(float)first.getStart().getY()
		);
		
		if(segments.length < 4) {
			path.curveTo(
					(float)first.getEnd().getX(),
					(float)first.getEnd().getY(),
					(float)last.getStart().getX(),
					(float)last.getStart().getY(),
					(float)last.getEnd().getX(),
					(float)last.getEnd().getY()
			);
		} else if(segments.length == 4) {
			
		} else {
			Segment middle = segments[2];
			Point2D center = new Point2D.Double(
					middle.getStart().getX() + (middle.getEnd().getX() - middle.getStart().getX()) / 2,
					middle.getStart().getY() + (middle.getEnd().getY() - middle.getStart().getY()) / 2
			);
			path.curveTo(
					(float)first.getEnd().getX(),
					(float)first.getEnd().getY(),
					(float)middle.getStart().getX(),
					(float)middle.getStart().getY(),
					(float)center.getX(),
					(float)center.getY()
			);
			path.curveTo(
					(float)segments[3].getStart().getX(),
					(float)segments[3].getStart().getY(),
					(float)last.getStart().getX(),
					(float)last.getStart().getY(),
					(float)last.getEnd().getX(),
					(float)last.getEnd().getY()
			);
		}
		return path;
	}
}
