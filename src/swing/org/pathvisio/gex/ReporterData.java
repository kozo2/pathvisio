// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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
package org.pathvisio.gex;

import java.util.HashMap;
import java.util.Map;

import org.bridgedb.Xref;

/**
 * This class represents a row of cached expression data for a reporter in the dataset.
 * The data is stored in a {@link HashMap} where the keys are the samples and the value
 * is an object of class {@link String} or {@link Double} for text and numeric data respectively.
 * 
 * Setters are package private, external classes are not supposed to modify this data.
 */
public class ReporterData 
{
	Xref idc;
	int group;
	Map<Sample, Object> sampleData;

	/**
	 * Constructor for this class. Creates a new {@link ReporterData} object for the given reporter
	 * @param ref The IdCodePair that represents the reporter
	 * @param groupId An id that groups the expression data from duplicate reporters
	 */
	ReporterData(Xref ref, int groupId) {
		idc = ref;
		group = groupId;
		sampleData = new HashMap<Sample, Object>();
	}
	
	/** set the xref (reporter) for this row. */
	void setXref(Xref value) { idc = value; }
	
	/**
	 * Get the reporter this object contains data for
	 * @return The IdCodePair that represents the reporter this object contains data for
	 */
	public Xref getXref() { return idc; }
	
	/**
	 * Get the group id for this object
	 * @return a group id that can be used to distinguish identical reporters that occur more
	 * than once in the dataset
	 * The group id is usually derived from the line number of the row of data in the
	 * original data file.
	 */
	public int getGroup() { return group; }
	
	/**
	 * Get the data for each sample
	 * @return A {@link HashMap} that contains the data for each sample. The key is a Sample and value
	 * is an object of class {@link String} or {@link Double}, depending on the data type of the sample.
	 * @see Sample#getDataType()
	 * @see Sample#getId()
	 */
	public Map<Sample, Object> getSampleData() {
		return sampleData;
	}
	
	/**
	 * returns the same info as getSampleData(), but
	 * using the sample name instead of the sample object as key.
	 */
	public Map<String, Object> getByName()
	{
		Map<String, Object> result = new HashMap<String, Object>();
		for (Sample s : sampleData.keySet())
		{
			result.put (s.getName(), sampleData.get(s));
		}
		return result;
	}
	
	/**
	 * Get the cached data for the given sample (shortcut for getSampleData().get(key))
	 * @param key The {@link Sample} to get the data for
	 * @return An object of class {@link String} or {@link Double}, depending on the datatype of the sample.
	 * @see Sample#getDataType()
	 * @see Sample#getId()
	 */
	public Object getSampleData(Sample key)
	{
		return sampleData.get(key);
	}
	
	/**
	 * Set the data for the given sample. Data will be parsed to double if possible
	 * @param sampleId The id of the sample to set the data for
	 * @param data The {@link String} representation of the data to add
	 * @see SimpleGex#cacheData
	 */
	void setSampleData(Sample sample, String data) {
		Object parsedData = null;
		try { parsedData = Double.parseDouble(data); }
		catch(Exception e) { parsedData = data; }
		sampleData.put(sample, parsedData);
	}
	
	/** set sample directly as Object, it won't be parsed */
	void setSampleAsObject (Sample sample, Object data)
	{
		sampleData.put(sample, data);
	}
	
}