package org.pathvisio.view;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of connector shapes. This class allows you to register
 * a custom connector shape.
 * @author thomas
 *
 */
public abstract class ConnectorShapeRegistry {
	private static Map<String, ConnectorShape> shapes = new HashMap<String, ConnectorShape>();
	
	static {
		shapes.put("straight", new StraightConnectorShape());
		shapes.put("elbow", new ElbowConnectorShape());
		shapes.put("curved", new CurvedConnectorShape());
	}
	
	public static void registerShape(String name, ConnectorShape shape) {
		if(name == null || shape == null) {
			throw new IllegalArgumentException("null argument provided");
		}
		shapes.put(name, shape);
	}
	
	public static ConnectorShape getShape(String name) {
		return shapes.get(name);
	}
}
