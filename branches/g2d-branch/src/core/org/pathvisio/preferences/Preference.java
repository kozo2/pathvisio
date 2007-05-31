package org.pathvisio.preferences;


public interface Preference {
	public String name();
	
	public String getDefault();
	
	public void setValue(String newValue);

	public String getValue();
}
