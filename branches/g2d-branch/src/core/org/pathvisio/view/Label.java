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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import org.pathvisio.model.PathwayElement;

public class Label extends GraphicsShape
{
	private static final long serialVersionUID = 1L;
	
	public static final int M_INITIAL_FONTSIZE = 10 * 15;
	public static final int M_INITIAL_WIDTH = 80 * 15;
	public static final int M_INITIAL_HEIGHT = 20 * 15;
		
	double getFontSize()
	{
		return gdata.getMFontSize() * canvas.getZoomFactor();
	}
	
	void setFontSize(double v)
	{
		gdata.setMFontSize(v / canvas.getZoomFactor());
	}
				
	/**
	 * Constructor for this class
	 * @param canvas - the VPathway this label will be part of
	 */
	public Label(VPathway canvas, PathwayElement o)
	{
		super(canvas, o);
		setHandleLocation();
	}
	
	public int getDrawingOrder() {
		return VPathway.DRAW_ORDER_LABEL;
	}
	
	public String getLabelText() {
		return gdata.getTextLabel();
	}
	
	String prevText = "";
//	public void adjustWidthToText() {
//		if(gdata.getTextLabel().equals(prevText)) return;
//		
//		prevText = getLabelText();
//		
//		Point mts = mComputeTextSize();
//		
//		//Keep center location
//		double mWidth = mts.x;
//		double mHeight = mts.y;
//		
//		listen = false; //Disable listener
//		gdata.setMLeft(gdata.getMLeft() - (mWidth - gdata.getMWidth())/2);
//		gdata.setMTop(gdata.getMTop() - (mHeight - gdata.getMHeight())/2);
//		gdata.setMWidth(mWidth);
//		gdata.setMHeight(mHeight);
//		listen = true; //Enable listener
//		
//		setHandleLocation();
//	}
	
//	private Text t;
//	public void createTextControl()
//	{
//		Color background = canvas.getShell().getDisplay()
//		.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
//		
//		Composite textComposite = new Composite(canvas, SWT.NONE);
//		textComposite.setLayout(new GridLayout());
//		textComposite.setLocation(getVCenterX(), getVCenterY() - 10);
//		textComposite.setBackground(background);
//		
//		org.eclipse.swt.widgets.Label label = new org.eclipse.swt.widgets.Label(textComposite, SWT.CENTER);
//		label.setText("Specify label:");
//		label.setBackground(background);
//		t = new Text(textComposite, SWT.SINGLE | SWT.BORDER);
//		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		t.addSelectionListener(new SelectionAdapter() {
//			public void widgetDefaultSelected(SelectionEvent e) {
//				disposeTextControl();
//			}
//		});
//				
//		t.setFocus();
//		
//		Button b = new Button(textComposite, SWT.PUSH);
//		b.setText("OK");
//		b.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				disposeTextControl();
//			}
//		});
//		
//		textComposite.pack();
//	}
	
	Dimension mComputeTextSize(Graphics2D g) {
		Rectangle2D tb = null;
		if(g != null) {
			TextLayout tl = new TextLayout(gdata.getTextLabel(), g.getFont(), g.getFontRenderContext());
			tb = tl.getBounds();
		} else { //No graphics context, we can only guess...
			tb = new Rectangle2D.Double(0, 0, getVWidthDouble(), getVHeightDouble()); 
		}
		return new Dimension((int)tb.getWidth(), (int)tb.getHeight());
	}
	
//	protected void disposeTextControl()
//	{
//		gdata.setTextLabel(t.getText());
//		Composite c = t.getParent();
//		c.setVisible(false);
//		c.dispose();
//	}
		
	double getVFontSize()
	{
		return vFromM(gdata.getMFontSize());
	}
	
	Graphics2D g2d = null; //last Graphics2D for determining text size
	public void doDraw(Graphics2D g)
	{		
		g2d = g;
		
		if(isSelected()) {
			g.setColor(selectColor);
		} else {
			g.setColor(gdata.getColor());
		}
						
		g.setFont(new Font(gdata.getFontName(), getVFontStyle(), (int)getVFontSize()));
		
		Rectangle area = getVOutline().getBounds();
		
		String label = gdata.getTextLabel();
		TextLayout tl = new TextLayout(label, g.getFont(), g.getFontRenderContext());
		Rectangle2D tb = tl.getBounds();
		tl.draw(g, 	area.x + (int)(area.width / 2) - (int)(tb.getWidth() / 2), 
					area.y + (int)(area.height / 2) + (int)(tb.getHeight() / 2));		
	}
		
//	public void gmmlObjectModified(PathwayEvent e) {
//		if(listen) {
//			super.gmmlObjectModified(e);
//			adjustWidthToText();
//		}
//	}
	
	/**
	 * Outline of a label is determined by
	 * - position of the handles
	 * - size of the text
	 * Because the text can sometimes be larger than the handles
	 */
	protected Shape getVOutline()
	{
		int[] vx = new int[4];
		int[] vy = new int[4];
		
		int[] p = getVHandleLocation(handleNE).asIntArray();
		vx[0] = p[0]; vy[0] = p[1];
		p = getVHandleLocation(handleSE).asIntArray();
		vx[1] = p[0]; vy[1] = p[1];
		p = getVHandleLocation(handleSW).asIntArray();
		vx[2] = p[0]; vy[2] = p[1];
		p = getVHandleLocation(handleNW).asIntArray();
		vx[3] = p[0]; vy[3] = p[1];
		
		Polygon pol = new Polygon(vx, vy, 4);		
		Rectangle bounds = pol.getBounds();
		
		Dimension mq = mComputeTextSize(g2d);
		double vqx = vFromM(mq.getWidth());
		double vqy = vFromM(mq.getHeight());
		
		LinAlg.Point c = getVCenter();
		bounds.add(new Rectangle2D.Double(c.x - vqx / 2, c.y - vqy / 2, vqx, vqy)); 
		
		return bounds;
	}
	
}
