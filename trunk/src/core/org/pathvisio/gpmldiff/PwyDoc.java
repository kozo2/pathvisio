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

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Collections;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;

/**
   Wrapper for org.pathvisio.model.Pathway that adds some extra
   functionality for gpmldiff
*/   
class PwyDoc
{
	Pathway pwy = null;
	File src = null;
	
	/**
	   return the wrapped Pathway.
	 */
	Pathway getPathway()
	{
		return pwy;
	}

	File getSourceFile ()
	{
		return src;
	}
	
	private	List<PathwayElement> elts = new ArrayList<PathwayElement>();

	public void add (PathwayElement value)
	{
		elts.add (value);
	}

	public void remove (PathwayElement value)
	{
		elts.remove (value);
	}
	
	/**
	   Return a list of all PwyElts contained in this documents
	*/
	public List<PathwayElement> getElts() { return elts; }
		
	/**
	   Construct a new PwyDoc from a certain file
	   Returns null if there is an  IO exception
	   TODO: We may want to pass on the exception?
	*/
	static public PwyDoc read(File f)
	{
		PwyDoc result = new PwyDoc();
		result.pwy = new Pathway();
		try
		{
			result.pwy.readFromXml (f, false);
		}
		catch (ConverterException e)
		{
			Logger.log.error ("Converter exception", e);
			return null;
		}
		
		for (PathwayElement e : result.pwy.getDataObjects())
		{
			result.add (e);
		}
		
		result.src = f;
		return result;
	}
	
	// make default constructor private
	private PwyDoc()
	{
	}
	
	public PwyDoc (Pathway _pwy)
	{
		assert (_pwy != null);
		pwy = _pwy;
		
		for (PathwayElement e : pwy.getDataObjects())
		{
			add (e);
		}
	}

	/**
	   Calculates a table with similarity scores.
	*/
	static public Map2D <PathwayElement, PathwayElement, Integer> getSimTable (PwyDoc oldDoc, PwyDoc newDoc, SimilarityFunction simFun)		
	{
		Map2D <PathwayElement, PathwayElement, Integer> result =
			new Map2D <PathwayElement, PathwayElement, Integer>(
				oldDoc.getElts(),
				newDoc.getElts()
				);

		for (PathwayElement oldElt : oldDoc.getElts())
		{
			for (PathwayElement newElt : newDoc.getElts())
			{
				result.set (oldElt, newElt, simFun.getSimScore(oldElt, newElt));
			}
		}

		return result;
	}

	static public void printSimTable (PwyDoc oldDoc, PwyDoc newDoc, SimilarityFunction simFun)
	{
		PrintStream out = System.out;

		// empty corner cell
		out.print ("\t"); 
		// column headers
		for (PathwayElement newElt : newDoc.getElts())
		{			
			out.print(PwyElt.summary(newElt));
			out.print("\t");
		}
		out.println();
		for (PathwayElement oldElt : oldDoc.getElts())
		{
			// row header.
			out.print (PwyElt.summary (oldElt));
			out.print ("\t");
			// row data
			for (PathwayElement newElt : newDoc.getElts())
			{
				out.print (simFun.getSimScore(oldElt, newElt));
				out.print ("\t");
			}
			out.println();
		}		
	}
	
	class MaxScoreComparator implements Comparator<PathwayElement>
	{
		Map <PathwayElement, Integer> scores;
		
		public MaxScoreComparator (Map<PathwayElement, Integer> scores)
		{
			this.scores = scores;
		}

		public int compare (PathwayElement a, PathwayElement b)
		{
			return scores.get (b) - scores.get(a);
		}
	}
	
	/**
	   Finds correspondence set with the lowest cost 

	   Originally I planned to use Dijkstra's algorithm as described
	   in the xmldiff whitepaper, but it turned out not to be
	   necessary, and a simple ad-hoc algorithm suffices (and is
	   probably faster)

	   Call this on the OLD doc
	   @param newDoc the New doc
	   @param simFun similarity function
	   @param costFun cost function (for deciding which correspondence set to choose)
	*/
	SearchNode findCorrespondence(PwyDoc newDoc, SimilarityFunction simFun, CostFunction costFun)
	{
		/*
		   Compare each old element with each new element and store
		   this in a scoring matrix.
		 */
		Map2D<PathwayElement, PathwayElement, Integer> scoreMatrix =
			getSimTable (this, newDoc, simFun);

		/*
		  We're going to calculate the maximum score for each row in
		  the table and store the results in this Map.
		 */		
		Map <PathwayElement, Integer> maxScores =
			new HashMap<PathwayElement, Integer>();
		
		for (PathwayElement oldElt : elts)
		{						
			int maxScore = 0;
			PathwayElement maxNewElt = null;
			for (PathwayElement newElt : newDoc.getElts())
			{
				int score = scoreMatrix.get (oldElt, newElt);
				if (score > maxScore)
				{
					maxScore = score;
				}				
			}
			maxScores.put (oldElt, maxScore);
		}

		/*
		  Now sort the elements descending by their maximum score.
		  The reasoning is that if the maximum score is high
		  (e.g. 100), this means that there is really no doubt that
		  they go together, so we want to get those done first and
		  then leave the doubtful ones last.

		  That way we prevent that a doubtful pair gets mixed up with
		  a perfect matching pair.
		 */
		Collections.sort (elts, new MaxScoreComparator (maxScores));

		/*
		  The results are recorded in a linear tree of SearchNodes.
		  This is perhaps a strange way to record the results, but it
		  works and it will make it easier to switch to a different
		  algorithm in the future without modifying the rest of the
		  project.
		 */
		SearchNode currentNode = null;
		
		for (PathwayElement oldElt : elts)
		{
			int maxScore = 0;
			PathwayElement maxNewElt = null;
			for (PathwayElement newElt : newDoc.getElts())
			{
				// if it's the first node, or if the newElt is not yet
				// in the searchpath
				if (currentNode == null || !currentNode.ancestryHasElt (newElt))
				{
					int score = simFun.getSimScore (oldElt, newElt);
					if (score > maxScore)
					{
						maxNewElt = newElt;
						maxScore = score;
					}
				}
			}
			// we have a cut-off at 60. Below this, an element is
			// considered to be deleted from the new Pathway
			if (maxNewElt != null && maxScore >= 60)
			{
				// add this pairing to the search tree.
				SearchNode newNode = new SearchNode (currentNode, oldElt, maxNewElt, 0);
				currentNode = newNode;
			}
		}
		return currentNode;
	}

	/**
	   Output the Diff after the best correspondence has been
	   calculated.  call this on the OLD doc.

	   @param result result of findcorrespondence
	   @param newPwy the new pathway compared against
	   @param out DiffOutputter that absorbs the results and puts it
	   somewhere depending on the type of DiffOutputter
	*/
	void writeResult (SearchNode result, PwyDoc newPwy, DiffOutputter out)
	{
		Set<PathwayElement> bothOld = new HashSet<PathwayElement>();
		Set<PathwayElement> bothNew = new HashSet<PathwayElement>();
				
		SearchNode current = result;
		while (current != null)
		{
			// check for modification
			PwyElt.writeModifications(current.getOldElt(), current.getNewElt(), out);
			bothOld.add (current.getOldElt());
			bothNew.add (current.getNewElt());
			current = current.getParent();
		}

		for (PathwayElement oldElt : elts)
		{
			// if the oldElt doesn't have a corresponding newElt...
			if (!bothOld.contains(oldElt))
			{
				assert (oldElt != null);
				// then we have a deletion
				out.delete (oldElt);
			}
		}

		for (PathwayElement newElt : newPwy.elts)
		{
			// if the newElt doesn't have a corresponding oldElt
			if (!bothNew.contains(newElt))
			{
				// then we have an insertion
				out.insert (newElt);
			}
		}
	}
}