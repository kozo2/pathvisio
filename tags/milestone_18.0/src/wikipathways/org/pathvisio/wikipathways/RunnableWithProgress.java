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
package org.pathvisio.wikipathways;

import org.pathvisio.util.ProgressKeeper;

public abstract class RunnableWithProgress<T> implements Runnable {
	ProgressKeeper p;
	T value;
	
	public ProgressKeeper getProgressKeeper() {
		if(p == null) p = new ProgressKeeper();
		return p;
	}
	public void setProgressKeeper(ProgressKeeper progress) {
		p = progress;
	}
	
	public void run() {
		value = excecuteCode();
	}
	
	public abstract T excecuteCode();
	
	public T get() {
		return value;
	}
}
