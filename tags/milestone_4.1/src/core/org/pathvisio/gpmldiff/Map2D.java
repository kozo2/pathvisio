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
package org.pathvisio.gpmldiff;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Map2D <S, T, U>
{
	Map<S, Integer> rows;
	Map<T, Integer> cols;
	U[][] cell;

	void set (S row, T col, U value)
	{
		int rowInt = rows.get (row);
		int colInt = cols.get (col);
		cell[rowInt][colInt] = value;
	}
	
	U get (S row, T col)
	{
		int rowInt = rows.get (row);
		int colInt = cols.get (col);
		return cell[rowInt][colInt];
	}

	/**
	   Note: doubles in the collection are discarded!
	 */
	Map2D (Collection<S> _rows, Collection<T> _cols)
	{
		rows = new HashMap <S, Integer>();
		cols = new HashMap <T, Integer>();
		int i = 0;
		for (S s : _rows)
		{
			rows.put (s, i);
			i++;
		}
		i = 0;
		for (T t : _cols)
		{
			cols.put (t, i);
			i++;
		}
		// Generate cell array as Object[][], not possible to do U[][] directly.
		// this will give an unavoidable compiler warning.
		cell = (U[][]) new Object[rows.size()][cols.size()];
	}
	
}