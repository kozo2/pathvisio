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
package data;

import java.util.*;

public class UndoManager 
{
	private List<UndoAction> undoList = new ArrayList<UndoAction>();

	void newAddAction (GmmlDataObject affectedObject)
	{
		undoList.clear();		
	}
	
	void newChangeAction (GmmlDataObject affectedObject)
	{
		UndoAction a = new UndoAction ("Change object", UndoAction.UNDO_CHANGE, affectedObject);
		undoList.add(a);
	}
	
	void newRemoveAction (GmmlDataObject affectedObject)
	{
		undoList.clear();
	}
	
	void undo()
	{
		if (undoList.size() > 0)
		{
			UndoAction a = undoList.get(undoList.size()-1);
			a.undo();
			undoList.remove(a);
		}
	}
}