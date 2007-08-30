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

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayEvent;
import org.pathvisio.model.PathwayListener;


/**
 * This class is a parent class for all graphics
 * that can be added to a VPathway.
 */
public abstract class Graphics extends VPathwayElement implements PathwayListener
{	
	protected PathwayElement gdata = null;
	
	public Graphics(VPathway canvas, PathwayElement o) {
		super(canvas);
		o.addListener(this);
		gdata = o;
		gdata.addListener(canvas);
	}
	
	public void select()
	{
		super.select();
		for (Handle h : getHandles())
		{
			h.show();
		}
	}
	
	public void deselect()
	{
		super.deselect();
		for (Handle h : getHandles())
		{
			h.hide();
		}
	}
	
	/**
	 * @deprecated use {@link #getPathwayElement()} instead
	 */
	public PathwayElement getGmmlData() {
		return gdata;
	}
	
	/**
	 * Gets the model representation (PathwayElement) of this class
	 * @return
	 */
	public PathwayElement getPathwayElement() {
		return gdata;
	}
	
	public final int getDrawingOrder() {
		int zorder = gdata.getZOrder();
		return getNaturalOrder() + (zorder <<= 32); 
	}
	
	protected abstract int getNaturalOrder();
	
	boolean listen = true;
	public void gmmlObjectModified(PathwayEvent e) {	
		if(listen) markDirty(); // mark everything dirty
	}
	
	public Area createVisualizationRegion() {
		return new Area(getVBounds());
	}
	
	/**
	 * Get the x-coordinate of the center point of this object
	 * adjusted to the current zoom factor

	 * @return the center x-coordinate
	 */
	public double getVCenterX() { return vFromM(gdata.getMCenterX()); }
	
	/**
	 * Get the y-coordinate of the center point of this object
	 * adjusted to the current zoom factor
	 * 
	 * @return the center y-coordinate
	 */
	public double getVCenterY() { return vFromM(gdata.getMCenterY()); }

	/**
	 * Get the x-coordinate of the left side of this object
	 * adjusted to the current zoom factor, but not taking into
	 * account rotation
	 * @note if you want the left side of the rotated object's boundary, 
	 * use {@link #getVShape(true)}.getX();
	 * @return
	 */
	public double getVLeft() { return vFromM(gdata.getMLeft()); }
	
	/**
	 * Get the width of this object
	 * adjusted to the current zoom factor, but not taking into
	 * account rotation
	 * @note if you want the width of the rotated object's boundary, 
	 * use {@link #getVShape(true)}.getWidth();
	 * @return
	 */
	public double getVWidth() { return vFromM(gdata.getMWidth());  }
	
	/**
	 * Get the y-coordinate of the top side of this object
	 * adjusted to the current zoom factor, but not taking into
	 * account rotation
	 * @note if you want the top side of the rotated object's boundary, 
	 * use {@link #getVShape(true)}.getY();
	 * @return
	 */
	public double getVTop() { return vFromM(gdata.getMTop()); }
	
	/**
	 * Get the height of this object
	 * adjusted to the current zoom factor, but not taking into
	 * account rotation
	 * @note if you want the height of the rotated object's boundary, 
	 * use {@link #getVShape(true)}.getY();
	 * @return
	 */
	public double getVHeight() { return vFromM(gdata.getMHeight()); }

	/**
	 * Get the direct view to model translation of this shape
	 * @param rotate Whether to take into account rotation or not
	 * @return
	 */
	abstract protected Shape getVShape(boolean rotate);
	
	/**
	 * Get the rectangle that represents the bounds of the shape's
	 * direct translation from model to view, without taking into
	 * account rotation.
	 * Default implementation is equivalent to <code>getVShape(false).getBounds2D();</code>
	 */
	protected Rectangle2D getVScaleRectangle() {
		return getVShape(false).getBounds2D();
	}
		
	/**
	 * Scales the object to the given rectangle, by taking into account
	 * the rotation (given rectangle will be rotated back before scaling)
	 * @param r
	 */
	protected abstract void setVScaleRectangle(Rectangle2D r);
	
	/**
	 * Default implementation returns the rotated shape.
	 * Subclasses may override (e.g. to include the stroke)
	 * @see {@link VPathwayElement#getVOutline()}
	 */
	protected Shape getVOutline() {
		return getVShape(true);
	}
	
	/**
	 * Returns the fontstyle to create a java.awt.Font
	 * @return the fontstyle, or Font.PLAIN if no font is available
	 */
	public int getVFontStyle() {
		int style = Font.PLAIN;
		if(gdata.getFontName() != null) {
			if(gdata.isBold()) {
				style |= Font.BOLD;
			}
			if(gdata.isItalic()) {
				style |= Font.ITALIC;
			}
		}
		return style;
	}
	
	protected void destroy() {
		super.destroy();
		gdata.removeListener(canvas);
		
		//View should not remove its model
//		Pathway parent = gdata.getParent();
//		if(parent != null) parent.remove(gdata);
	}
	
}