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
package graphics;
/*
Copyright 2005 H.C. Achterberg, R.M.H. Besseling, I.Kaashoek, 
M.M.Palm, E.D Pelgrim, BiGCaT (http://www.BiGCaT.unimaas.nl/)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and 
limitations under the License.
*/


import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import util.SwtUtils;
import data.GmmlDataObject;
import data.GmmlEvent;

public class GmmlLabel extends GmmlGraphicsShape
{
	private static final long serialVersionUID = 1L;
	
	public static final int INITIAL_FONTSIZE = 10;
	public static final int INITIAL_WIDTH = 80;
	public static final int INITIAL_HEIGHT = 20;
		
	double getFontSize()
	{
		return gdata.getFontSize() * canvas.getZoomFactor();
	}
	
	void setFontSize(double v)
	{
		gdata.setFontSize(v / canvas.getZoomFactor());
	}
				
	/**
	 * Constructor for this class
	 * @param canvas - the GmmlDrawing this label will be part of
	 */
	public GmmlLabel(GmmlDrawing canvas, GmmlDataObject o)
	{
		super(canvas, o);
		drawingOrder = GmmlDrawing.DRAW_ORDER_LABEL;
		setHandleLocation();
	}
	
	public String getLabelText() {
		return gdata.getLabelText();
	}
	
	String prevText = "";
	public void adjustWidthToText() {
		if(gdata.getLabelText().equals(prevText)) return;
		
		prevText = getLabelText();
		
		Point ts = computeTextSize();
		
		//Keep center location
		double nWidth = ts.x + 10 * getDrawing().getZoomFactor();
		double nHeight = ts.y + 10 * getDrawing().getZoomFactor();
		
		listen = false; //Disable listener
		gdata.setLeft(gdata.getLeft() - (nWidth - gdata.getWidth())/2);
		gdata.setTop(gdata.getTop() - (nHeight - gdata.getHeight())/2);
		gdata.setWidth(nWidth);
		gdata.setHeight(nHeight);
		listen = true; //Enable listener
		
		setHandleLocation();
	}
	
	private Text t;
	public void createTextControl()
	{
		Color background = canvas.getShell().getDisplay()
		.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
		
		Composite textComposite = new Composite(canvas, SWT.NONE);
		textComposite.setLayout(new GridLayout());
		textComposite.setLocation(getCenterX(), getCenterY() - 10);
		textComposite.setBackground(background);
		
		Label label = new Label(textComposite, SWT.CENTER);
		label.setText("Specify label:");
		label.setBackground(background);
		t = new Text(textComposite, SWT.SINGLE | SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		t.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				disposeTextControl();
			}
		});
				
		t.setFocus();
		
		Button b = new Button(textComposite, SWT.PUSH);
		b.setText("OK");
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				disposeTextControl();
			}
		});
		
		textComposite.pack();
	}
	
	Point computeTextSize() {
		GC gc = new GC(canvas.getDisplay());
		Font f = new Font(canvas.getDisplay(), 
				gdata.getFontName(), 
				(int)gdata.getFontSize(), getFontStyle());
		gc.setFont (f);
		Point ts = gc.textExtent(gdata.getLabelText());
		f.dispose();
		gc.dispose();
		
		return ts;
	}
	
	protected void disposeTextControl()
	{
		gdata.setLabelText(t.getText());
		Composite c = t.getParent();
		c.setVisible(false);
		c.dispose();
	}
	
	protected void adjustToZoom(double factor)
	{
		listen = false;
		super.adjustToZoom(factor);
		gdata.setFontSize(gdata.getFontSize() * factor);
		listen = true;		
	}

	private int getFontStyle() {
		int style = SWT.NONE;
		
		if (gdata.isBold())
		{
			style |= SWT.BOLD;
		}
		
		if (gdata.isItalic())
		{
			style |= SWT.ITALIC;
		}
		return style;
	}
	
	public void draw(PaintEvent e, GC buffer)
	{
		int style = getFontStyle();
		
		Font f = new Font(e.display, gdata.getFontName(), (int)gdata.getFontSize(), style);
		
		buffer.setFont (f);
		
		Point textSize = buffer.textExtent (gdata.getLabelText());
		
		Color c = null;
		if (isSelected())
		{
			c = SwtUtils.changeColor(c, selectColor, e.display);
		}
		else 
		{
			c = SwtUtils.changeColor(c, gdata.getColor(), e.display);
		}
		buffer.setForeground (c);
		
		buffer.drawString (gdata.getLabelText(), 
			(int) getCenterX() - (textSize.x / 2) , 
			(int) getCenterY() - (textSize.y / 2), true);
		
		f.dispose();
		c.dispose();
		
	}
	
	protected void draw(PaintEvent e)
	{
		draw(e, e.gc);
	}	
	
	public void gmmlObjectModified(GmmlEvent e) {
		if(listen) {
			super.gmmlObjectModified(e);
			adjustWidthToText();
		}
	}
	
	/**
	 * Outline of a label is determined by
	 * - position of the handles
	 * - size of the text
	 * Because the text can sometimes be larger than the handles
	 */
	protected Shape getOutline()
	{
		int[] x = new int[4];
		int[] y = new int[4];
		
		int[] p = getHandleLocation(handleNE).asIntArray();
		x[0] = p[0]; y[0] = p[1];
		p = getHandleLocation(handleSE).asIntArray();
		x[1] = p[0]; y[1] = p[1];
		p = getHandleLocation(handleSW).asIntArray();
		x[2] = p[0]; y[2] = p[1];
		p = getHandleLocation(handleNW).asIntArray();
		x[3] = p[0]; y[3] = p[1];
		
		Polygon pol = new Polygon(x, y, 4);
		
		Rectangle bounds = pol.getBounds();
		
		Point q = computeTextSize();
		util.LinAlg.Point c = getCenter();
		bounds.add(new Rectangle2D.Double(c.x - q.x / 2, c.y - q.y / 2, q.x, q.y)); 
		
		return bounds;
	}
	
}