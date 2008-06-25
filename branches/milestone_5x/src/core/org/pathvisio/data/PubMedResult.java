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
package org.pathvisio.data;

import java.util.ArrayList;
import java.util.List;

public class PubMedResult {
	private String id;
	private String title;
	private String source;
	private String year;
	private List<String> authors = new ArrayList<String>();
	
	public String getId() {
		return id;
	}
	
	void setId(String id) {
		this.id = id;
	}
		
	public String getSource() {
		return source;
	}
	
	void setSource(String source) {
		this.source = source;
	}
	
	public String getTitle() {
		return title;
	}
	
	void setTitle(String title) {
		this.title = title;
	}
	
	public String getYear() {
		return year;
	}
	
	void setYear(String year) {
		this.year = year;
	}
	
	public List<String> getAuthors() {
		return authors;
	}
	
	void addAuthor(String author) {
		if(!authors.contains(author)) {
			authors.add(author);
		}
	}
	
}
