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
package org.pathvisio.indexer;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.bridgedb.rdb.GdbProvider;
import org.pathvisio.model.Pathway;

/**
 * Abstract base class that can be implement to add information
 * for a pathway to the index. Classes that want to add custom
 * information to the index must extend this class. Ensures that required fields are stored.
 * @author thomas
 */
public abstract class IndexerBase {
	/**
	 * An identifier for the indexer instance that added the document.
	 * This is the class name, appended with the source attribute. This field
	 * is necessary for updating and removing documents generated by this indexer,
	 * since it's not possible to remove by query yet:
	 * http://www.gossamer-threads.com/lists/lucene/java-user/63208
	 */
	public static final String FIELD_INDEXERID = "indexerId";

	/**
	 * The source of a pathway (e.g. an url or file where the pathway
	 * is stored). This field should be unique for each pathway in the index.
	 */
	public static final String FIELD_SOURCE = "source";

	/**
	 * If the information is specific to a subset of pathway elements, this field stores the
	 * graphIds of those elements.
	 */
	public static final String FIELD_GRAPHID = "graphId";

	String source;
	Pathway pathway;
	private IndexWriter writer;
	GdbProvider gdbs;

	/**
	 * Create an indexer for the given pathway
	 * @param source The source of the pathway (e.g. a file or url)
	 * @param p The pathway to index
	 * @param w The IndexWriter to write the index to
	 */
	public IndexerBase(String source, Pathway p, IndexWriter w) {
		this.source = source;
		this.pathway = p;
		this.writer = w;
	}

	/**
	 * Set a synonym database provider
	 */
	public void setGdbProvider(GdbProvider gdbs) {
		this.gdbs = gdbs;
	}

	/**
	 * Get the synonym database provider that may be used
	 * to lookup cross references.
	 */
	protected GdbProvider getGdbProvider() {
		return gdbs;
	}

	/**
	 * Removes all information that was added by this indexer for the given pathway.
	 * The pathway is identified by the {@link #FIELD_SOURCE} field.
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public final void removePathway() throws CorruptIndexException, IOException {
		writer.deleteDocuments(new Term[] {
				new Term(FIELD_INDEXERID, getIndexerName())
		});
	}

	/**
	 * Updates or adds the pathway to the index
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public abstract void indexPathway() throws CorruptIndexException, IOException;

	protected final void addDocument(Document doc) throws CorruptIndexException, IOException {
		doc.add(new Field(FIELD_SOURCE, source, Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_INDEXERID, getIndexerName(), Field.Store.YES, Field.Index.UN_TOKENIZED));

		writer.addDocument(doc);
	}

	protected final void addDocument(Document doc, Analyzer analyzer) throws CorruptIndexException, IOException {
		doc.add(new Field(FIELD_SOURCE, source, Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_INDEXERID, getIndexerName(), Field.Store.YES, Field.Index.UN_TOKENIZED));

		writer.addDocument(doc, analyzer);
	}

	/**
	 * Get the name of the indexer (as stored in the {@link #FIELD_INDEXERID} field.
	 */
	protected final String getIndexerName() {
		return this.getClass().getName() + source;
	}
}
