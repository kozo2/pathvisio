package org.pathvisio.model;

import java.awt.Shape;

import org.pathvisio.view.ShapeRegistry;

public class AbstractShape implements IShape
{
	private String name;
	private String mappName;
	private boolean isResizeable;
	private boolean isRotatable;
	
	public AbstractShape (String name, String mappName, boolean isResizeable, boolean isRotatable)
	{
		this.name = name;
		this.mappName = mappName;
		this.isRotatable = isRotatable;
		this.isResizeable = isResizeable;
	}
	
	@Override
	public String getMappName()
	{
		return mappName;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public Shape getShape(double x, double y, double w, double h)
	{
		return ShapeRegistry.getShape(name, x, y, w, h);		
	}

	@Override
	public boolean isResizeable()
	{
		return isResizeable;
	}

	@Override
	public boolean isRotatable()
	{
		return isRotatable;
	}
	
	public String toString()
	{
		return name;
	}

}
