package org.pathvisio.preferences.swing;

import org.pathvisio.preferences.Preference;

public class SwingPreferences extends PreferenceCollection {

}

public enum SwingPreference implements Preference {

	String value;
	String defaultValue;
	
	
	SwingPreference(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public String getDefault() {
		return defaultValue;
	}

	public String getValue() {
		if(value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public void setDefault(String defValue) {
		defaultValue = defValue;
	}

	public void setValue(String newValue) {
		value = newValue;
	}
}