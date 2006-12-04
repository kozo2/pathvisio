package data;

public class GmmlEvent 
{
	public static final int MODIFIED_GENERAL = 0;
	public static final int MODIFIED_SHAPE = 1;
	
	/**
	 * Sent to listeners of GmmlData when an object was deleted
	 */
	public static final int DELETED = 2;
	
	/**
	 * Sent to listeners of GmmlData when a new object was added
	 */
	public static final int ADDED = 3;
	
	public static final int PROPERTY = 4; // e.g. name change
	public static final int WINDOW = 5;
	
	private GmmlDataObject affectedData;
	public GmmlDataObject getAffectedData () { return affectedData; }
	
	private int type;
	public int getType() { return type; }
	
	public GmmlEvent (GmmlDataObject object, int t)
	{
		affectedData = object;
		type = t;
	}
}
