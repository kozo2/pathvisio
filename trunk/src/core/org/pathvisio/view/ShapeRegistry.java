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
package org.pathvisio.view;

import java.util.Map;
import java.util.HashMap;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import org.pathvisio.debug.Logger;

/**
   The Shape registry stores all arrow heads and shapes

   at this moment the shape registry initializes itself,
   by calling  registerShape() on BasicShapes, GenMAPPShapes and MIMShapes.
 */

class ShapeRegistry
{
	private static Shape defaultShape = null;
	private static ArrowShape defaultArrow = null;

	private static Map <String, Shape> shapeMap = new HashMap <String, Shape>();
	private static Map <String, ArrowShape> arrowMap = new HashMap <String, ArrowShape>();

	static
	{
		GeneralPath temp = new GeneralPath();
		temp.moveTo (-50,-50);
		temp.lineTo (50,-50);
		temp.lineTo (50,50);
		temp.lineTo (-50,50);
		temp.closePath ();
		temp.moveTo (-30,-30);
		temp.lineTo (30,30);
		temp.moveTo (-30,30);
		temp.lineTo (30,-30);		
		defaultArrow = new ArrowShape (temp, ArrowShape.OPEN);

		temp = new GeneralPath();
		temp.moveTo (0,0);
		temp.lineTo (10,0);
		temp.lineTo (10,10);
		temp.lineTo (0,10);
		temp.closePath ();
		temp.moveTo (2,2);
		temp.lineTo (8,8);
		temp.moveTo (2,8);
		temp.lineTo (8,2);
		defaultShape = temp;

		BasicShapes.registerShapes();
		GenMAPPShapes.registerShapes();
	}

     /**
	   Add a shape to the registry.
	 */
	static public void registerShape (String key, Shape sh)
	{
		shapeMap.put (key, sh);
	}

	static public void registerArrow (String key, Shape sh, int fillType)
	{
		arrowMap.put (key, new ArrowShape (sh, fillType));
	}

	/**
	   Returns a named arrow head. The shape is normalized so that it
	   fits with a line that goes along the positive x-axis.  The tip
	   of the arrow head is in 0,0.
	 */
	public static ArrowShape getArrow(String name)
	{
		ArrowShape sh = arrowMap.get (name);
		if (sh == null)
		{
			sh = defaultArrow;
		}
		return sh;
		// TODO: here we return a reference to the object on the
		// registry itself we should really return a clone, although
		// in practice this is not a problem since we do a affine
		// transform immediately after.
	}

	/**
	   Returns a named shape, scaled in such a way that it has a
	   bounding rect equal to x, y, w, h.
	 */
	public static Shape getShape (String name, double x, double y, double w, double h)
	{
		Shape sh = shapeMap.get (name);
		if (sh == null)
		{
			sh = defaultShape;
			// This is probably not what the user wants.
			// log this as an error
			Logger.log.error ("Unknown Shape " + name + " was requested");
		}
		// now scale the path so it has proper w and h.
		Rectangle r = sh.getBounds();
		AffineTransform at = new AffineTransform();
		at.translate (x - r.x, y - r.y);
		at.scale (w / r.width, h / r.height);
		return at.createTransformedShape (sh);
	}
}