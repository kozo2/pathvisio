package org.pathvisio.view;

import java.util.EventObject;

public class VPathwayEvent extends EventObject {
	public static final int NEW_ELEMENT_ADDED = 0;
	public static final int EDIT_MODE_ON = 1;
	public static final int EDIT_MODE_OFF = 2;
	
	public VPathwayEvent(VPathway source, int type) {
		super(source);
	}

}
