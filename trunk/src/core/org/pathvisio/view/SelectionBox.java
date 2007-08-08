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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pathvisio.model.ObjectType;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.GraphLink.GraphRefContainer;
import org.pathvisio.model.PathwayElement.MPoint;

/**
 This class implements a selectionbox

 A selectionbox has two states: 1: while dragging (being created) and 2: when surrounding a selected area
 in Case 1, the handles can move freely, in case 2, they move in such a way that the aspect ratio is always maintained.
 
 It only stores the view coordinates, not the model coordinates.
 Therefore it is important to call adjustToZoom when the zoom pct has changed, so the view coordinates can be recalculated.
 */ 
public class SelectionBox extends VPathwayElement
{
	//Corner handles
	Handle handleNE;
	Handle handleSE;
	Handle handleSW;
	Handle handleNW;

	double vTop, vLeft, vWidth, vHeight;

	private static final long serialVersionUID = 1L;
		
	private ArrayList<VPathwayElement> selection;
	boolean isSelecting;
	boolean isVisible;

	public Handle[] getHandles()
	{
		return new Handle[]
		{
			handleNE, handleSE,
			handleSW, handleNW
		};
	}
	
	public SelectionBox(VPathway canvas)
	{
		super(canvas);
			
		selection = new ArrayList<VPathwayElement>();

		handleNE = new Handle(Handle.DIRECTION_FREE, this, canvas);
		handleSE = new Handle(Handle.DIRECTION_FREE, this, canvas);
		handleSW = new Handle(Handle.DIRECTION_FREE, this, canvas);
		handleNW = new Handle(Handle.DIRECTION_FREE, this, canvas);
	}	
	
	public int getDrawingOrder()
	{
		return VPathway.DRAW_ORDER_SELECTIONBOX;
	}
	
	public ArrayList<VPathwayElement> getSelection()
	{
		return selection;
	}

	public Rectangle getVOutline()
	{
		return new Rectangle ((int)vLeft, (int)vTop, (int)vWidth, (int)vHeight);
	}
	
	/**
	 * Add an object to the selection
	 * @param o
	 */
	public void addToSelection(VPathwayElement o)
	{
		if(o == this || selection.contains(o)) return; //Is selectionbox or already in selection
		if(o instanceof VPoint)
		{
			for(Line l : ((VPoint)o).getLines())
			{
				l.select();
				doAdd(l);
			}
		}
		else
		{
			o.select();
			doAdd(o);
		}
		fireSelectionEvent(new SelectionEvent(this, SelectionEvent.OBJECT_ADDED, o));
		if(isSelecting) return; //All we have to do if user is dragging selectionbox		 
	}
	
	private void doAdd(VPathwayElement o)
	{
		if(!selection.contains(o)) selection.add(o);
	}
	
	/**
	 * Remove an object from the selection
	 * @param o
	 */
	public void removeFromSelection(VPathwayElement o) {
		if(o == this) return;
		selection.remove(o); 
		o.deselect();
		fireSelectionEvent(new SelectionEvent(this, SelectionEvent.OBJECT_REMOVED, o));
		if(!isSelecting) fitToSelection();
	}
	
	/**
	 * Get the child object at the given coordinates (relative to canvas)
	 * @param p
	 * @return the child object or null if none is present at the given location
	 */
	public VPathwayElement getChild(Point2D p) {
		//First check selection
		for(VPathwayElement o : selection) {
			if(o.vContains(p)) return o;
		}
		//Nothing in selection, check all other objects
		for(VPathwayElement o : canvas.getDrawingObjects()) {
			if(o.vContains(p) && o != this)
				return o;
		}
		return null; //Nothing found
	}
	
	/**
	 * Removes or adds the object (if exists) at the given coordinates from the selection,
	 * depending on its selection-state
	 * @param p
	 */
	public void objectClicked(Point2D p) {
		VPathwayElement clicked = getChild(p);
		if(clicked == null) return; //Nothing clicked
		if(clicked.isSelected()) 	//Object is selected, remove
		{
			removeFromSelection(clicked);
		} 
		else 						//Object is not selected, add
		{
			addToSelection(clicked);
		}
	}
	
	/**
	 * Returns true if the selectionbox has multiple objects in its selection, false otherwise
	 */
	public boolean hasMultipleSelection() { return selection.size() > 1 ? true : false; }
	
	/**
	 * Resets the selectionbox (unselect selected objects, clear selection, reset rectangle
	 * to upperleft corner
	 */
	public void reset()
	{ 
		reset(0, 0, true);
	}
	
	
	/**
	 * Resets the selectionbox (unselect selected objects, clear selection, reset rectangle
	 * to upperleft corner
	 * @param clearSelection if true the selection is cleared
	 */
	public void reset(boolean clearSelection) { 
		reset(0, 0, clearSelection);
	}
	
	public void reset(double vStartX, double vStartY) {
		reset(vStartX, vStartY, true);
	}
	
	private void reset(double vStartX, double vStartY, boolean clearSelection) {
		if(clearSelection) {
			for(VPathwayElement o : selection) o.deselect();
			boolean hadObjects = selection.size() > 0;
			selection.clear();
			if(hadObjects) {
				fireSelectionEvent(
					new SelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
			}
		}
		
		vLeft = vStartX;
		vTop = vStartY;
		vWidth = 0;
		vHeight = 0;
	}

	/**
	 * Returns true if this selectionbox is in selecting state (selects containing objects when resized)
	 */
	public boolean isSelecting() { return isSelecting; }
	
	/**
	 * Start selecting
	 */
	public void startSelecting() {
		isSelecting = true;
		setHandleRestriction(false);
		show();
	}
	
	/**
	 * Stop selecting
	 */
	public void stopSelecting() {
		isSelecting = false;
		if(!hasMultipleSelection()) {
			if(selection.size() == 1) {
				VPathwayElement passTo = selection.get(0);
				reset();
				passTo.select();
			} else {
				reset();
			}
		} else {
			select();
			fitToSelection();
			setHandleRestriction(true);
		}
	}
	
	/**
	 * Sets movement direction restriction for this object's handles
	 * @param restrict if true, handle movement is restricted in XY direction,
	 * else handles can move freely
	 */
	private void setHandleRestriction(boolean restrict) {
		if(restrict) {
			handleNE.setDirection(Handle.DIRECTION_MINXY);
			handleSW.setDirection(Handle.DIRECTION_MINXY);
			handleNW.setDirection(Handle.DIRECTION_XY);
			handleSE.setDirection(Handle.DIRECTION_XY);
		} else {
			for(Handle h : getHandles()) 
				h.setDirection(Handle.DIRECTION_FREE); 
		}
	}
	
	public void select()
	{
		super.select();
		for (Handle h : getHandles())
		{
			h.show();
		}
		for(VPathwayElement o : selection) {
			o.select();
			for(Handle h : o.getHandles()) h.hide();
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
	 * Fit the size of this object to the selected objects
	 */
	public void fitToSelection() {
		if(selection.size() == 0) { //No objects in selection
			hide(); 
			return;
		}
		if(! hasMultipleSelection()) { //Only one object in selection, hide selectionbox
			VPathwayElement passTo = selection.get(0);
			hide(false);
			passTo.select();
			return;
		}

		Rectangle vr = null;
		for(VPathwayElement o : selection) {
			if(vr == null) vr = o.getVBounds();
			else vr.add(o.getVBounds());
			for(Handle h : o.getHandles()) h.hide();
		}

		vWidth = vr.width;
		vHeight = vr.height;
		vLeft = vr.x;
		vTop = vr.y;
		setHandleLocation();
	}

	/**
	 * Sets the handles at the correct location;
	 */
	protected void setHandleLocation()
	{
		handleNE.setMLocation(mFromV(vLeft + vWidth), mFromV(vTop));
		handleSE.setMLocation(mFromV(vLeft + vWidth), mFromV(vTop + vHeight));
		handleSW.setMLocation(mFromV(vLeft), mFromV(vTop + vHeight));
		handleNW.setMLocation(mFromV(vLeft), mFromV(vTop));
	}
			
	/**
	 * Show the selectionbox
	 */
	public void show() { 
		isVisible = true; 
		markDirty();
	}
	
	/**
	 * Hide the selectionbox
	 */
	public void hide() {
		hide(true);
	}
	
	public void hide(boolean reset) {
		for(Handle h : getHandles()) h.hide();
		isVisible = false;
		if(reset) reset();
	}
	
	/**
	 * Gets the corner handle (South east) for start dragging
	 */
	public Handle getCornerHandle()
	{
		return handleSE;
	}
	
	public void adjustToHandle(Handle h, double vnewx, double vnewy)
	{
		//Store original size and location before adjusting to handle
		double vWidthOld = vWidth;
		double vHeightOld = vHeight;
		double vLeftOld = vLeft;
		double vTopOld = vTop;

		double vdx = 0;
		double vdy = 0;
		double vdw = 0;
		double vdh = 0;
		if (h == handleNE || h == handleNW)
		{
			vdy = vnewy - vTop;
			vdh = -vdy;
		}
		if (h == handleSE || h == handleSW)
		{
			vdy = 0;
			vdh = vnewy - (vTop + vHeight);
		}
		if (h == handleSE || h == handleNE)
		{
			vdx = 0;
			vdw = vnewx - (vLeft + vWidth);			
		}
		if (h == handleSW || h == handleNW)
		{
			vdx = vnewx - vLeft;
			vdw = -vdx;
		}

		markDirty();

		vWidth += vdw;
		vHeight += vdh;
		vLeft += vdx;
		vTop += vdy;

		if(vWidth < 0)
		{
			negativeWidth(h);
		}
		if(vHeight < 0)
		{
			negativeHeight(h);
		}

		markDirty();
		setHandleLocation();
		
		if(isSelecting)
		{   //Selecting, so add containing objects to selection
			Rectangle vr = getVBounds();
			Rectangle2D.Double bounds = new Rectangle2D.Double(vr.x, vr.y, vr.width, vr.height);
			for(VPathwayElement o : canvas.getDrawingObjects())
			{
				if((o == this) || (o instanceof Handle)) continue;
				if(o.vIntersects(bounds) && !(o instanceof Group))
				{ 
					addToSelection(o);
				} else if(o.isSelected()) removeFromSelection(o);
			}
		}
		else
		{
			//Resizing, so resize child objects too
			double widthRatio = vWidth / vWidthOld;
			double heightRatio = vHeight / vHeightOld;
			//Scale all selected objects in x and y direction, treat points seperately
			Set<VPoint> points = new HashSet<VPoint>();
			for(VPathwayElement o : selection)
			{ 
				if(o instanceof Line)
				{
					points.addAll(((Line)o).getPoints());
				}
				else
				{ 
					Rectangle2D vr = o.getVScaleRectangle();
					double newObjectWidth = vr.getWidth() * widthRatio;
					double newObjectHeight = vr.getHeight() * heightRatio;
					double objectFromCenterX = (vr.getX() - (vLeftOld + vWidthOld / 2)) * widthRatio;
					double objectFromCenterY = (vr.getY() - (vTopOld + vHeightOld / 2)) * heightRatio;
 					o.setVScaleRectangle(
 						new Rectangle2D.Double(
 							(vLeft + vWidth / 2) + objectFromCenterX, 
 							(vTop + vHeight / 2) + objectFromCenterY, 
 							newObjectWidth, 
 							newObjectHeight));
				}
			}
			for(VPoint p : points) {
				double dx = (p.getVX() - (vLeftOld + vWidthOld / 2)) * widthRatio;
				double dy = (p.getVY() - (vTopOld + vHeightOld / 2)) * heightRatio;
				p.setVLocation(vLeft + vWidth / 2 + dx, vTop + vHeight / 2 + dy);
			}
		}
	}

	/**
	 * This method implements actions performed when the width of
	 * the object becomes negative after adjusting to a handle
	 * @param h	The handle this object adjusted to
	 */
	public void negativeWidth(Handle h)
	{
		Handle opposite = null;
		if(h == handleNE) opposite = handleNW;
		else if(h == handleSE) opposite = handleSW;
		else if(h == handleSW) opposite = handleSE;
		else if(h == handleNW) opposite = handleNE;
		assert (opposite != null);
		
		double vw = -vWidth;
		double vsx = vLeft - vw;
		vWidth = vw;
		vLeft = vsx;
		canvas.setPressedObject(opposite);
	}

	/**
	 * This method implements actions performed when the height of
	 * the object becomes negative after adjusting to a handle
	 * @param h	The handle this object adjusted to
	 */
	public void negativeHeight(Handle h)
	{
		Handle opposite = null;
		if(h == handleNE) opposite = handleSE;
		else if(h == handleSE) opposite = handleNE;
		else if(h == handleSW) opposite = handleNW;
		else if(h == handleNW) opposite = handleSW;
		assert (opposite != null);
		
		double ht = -vHeight;
		double sy = vTop - ht;
		vHeight = ht;
		vTop = sy;
		canvas.setPressedObject(opposite);
	}

	public void vMoveBy(double vdx, double vdy) 
	{
		vLeft += vdx;
		vTop += vdy;
		setHandleLocation();
		markDirty();

		//Move selected object and their references
		Set<GraphRefContainer> not = new HashSet<GraphRefContainer>(); //Will be moved by linking object
		Set<VPoint> points = new HashSet<VPoint>(); //Will not be moved by linking object
		
		for(VPathwayElement o : selection) 
		{
			if (o instanceof Graphics)
			{
				PathwayElement g = ((Graphics)o).getGmmlData();
				if(!(o instanceof Line)) {
					o.vMoveBy(vdx, vdy);
					not.addAll(g.getReferences());
				}
				if(g.getObjectType() == ObjectType.LINE) {
					points.addAll(((Line)o).getPoints());
				}
			}

		}
		
		for(GraphRefContainer ref : not)
		{
			if(ref instanceof MPoint)
			{
				points.remove(canvas.getPoint((MPoint)ref));
			}
		}
			
		for(VPoint p : points)
		{
			p.vMoveBy(vdx, vdy);
		}
	}
	
	public void doDraw(Graphics2D g)
	{
		if(isVisible)
		{
			int sw = 1;
			g.setStroke(new BasicStroke(sw, 
					BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 
					1, new float[] {1, 2}, 0));
			g.drawRect((int)vLeft, (int)vTop, (int)vWidth - sw, (int)vHeight - sw);
		}
	}
		
	public void adjustToZoom(double factor) { fitToSelection(); }
	
	private List<SelectionListener> listeners = new ArrayList<SelectionListener>();
	
	public void addListener(SelectionListener l)
	{
		if(!listeners.contains(l))
		{
			listeners.add(l);
		}
	}
	
	public void removeListener(SelectionListener l)
	{
		listeners.remove(l);
	}
	
	/**
	 * Fire a {@link SelectionEvent} to notify all {@link SelectionListener}s registered
	 * to this class
	 * @param e
	 */
	public void fireSelectionEvent(SelectionEvent e)
	{
		for(SelectionListener l : listeners)
		{
			l.selectionEvent(e);
		}
	}

	public interface SelectionListener
	{
		public void selectionEvent(SelectionEvent e);
	}

	public static class SelectionEvent extends EventObject
	{
		private static final long serialVersionUID = 1L;
		public static final int OBJECT_ADDED = 0;
		public static final int OBJECT_REMOVED = 1;
		public static final int SELECTION_CLEARED = 2;

		public SelectionBox source;
		public VPathwayElement affectedObject;
		public int type;
		public List<VPathwayElement> selection;

		public SelectionEvent(SelectionBox source, int type, VPathwayElement affectedObject)
		{
			super(source);
			this.source = source;
			this.type = type;
			this.selection = source.selection;
			this.affectedObject = affectedObject;
		}
		
		public SelectionEvent(SelectionBox source, int type)
		{
			this(source, type, null);
		}
	}	
	
}