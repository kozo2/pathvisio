package org.wikipathways.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.bridgedb.Xref;
import org.pathvisio.indexer.DataNodeIndexer;

public class WikiPathwaysSearcher {
	Searcher searcher;
	public WikiPathwaysSearcher(IndexSearcher searcher) {
		this.searcher = searcher;
	}

	public List<SearchResult> query(Query query, int limit) throws IOException {
		TopDocs hits = searcher.search(query, null, limit);
		List<SearchResult> results = new ArrayList<SearchResult>();
		for(ScoreDoc sd : hits.scoreDocs) {
			results.add(new SearchResult(searcher.doc(sd.doc), sd.score));
		}
		return results;
	}

	public List<SearchResult> queryByXrefs(Collection<Xref> xrefs, int limit) throws IOException {
		BooleanQuery query = new BooleanQuery();
		for(Xref x : xrefs) {
			Query tq = null;
			if(x.getDataSource() == null) {
				tq = new TermQuery(new Term(DataNodeIndexer.FIELD_XID, x.getId()));
			} else {
				tq = new TermQuery(new Term(DataNodeIndexer.FIELD_XID_CODE,
						x.getId() + ":" + x.getDataSource().getSystemCode()));
			}
			query.add(tq, Occur.SHOULD);
		}

		final Set<String> ids = new HashSet<String>();
		final Set<String> idcodes = new HashSet<String>();
		for(Xref x : xrefs) {
			ids.add(x.getId());
			if(x.getDataSource() != null) {
				idcodes.add(x.getId() + ":" + x.getDataSource().getSystemCode());
			}
		}

		FieldFilter filter = new FieldFilter() {
			public boolean include(String name, String value) {
				//Only include x.id, x.id.database when they are part of the query
				if(DataNodeIndexer.FIELD_XID.equals(name)) {
					return ids.contains(value);
				}
				if(DataNodeIndexer.FIELD_XID_CODE.equals(name)) {
					return idcodes.contains(value);
				}
				return true;
			}
		};

		List<SearchResult> results = new ArrayList<SearchResult>();
		TopDocs hits = searcher.search(query, null, limit);
		for(ScoreDoc sd : hits.scoreDocs) {
			results.add(new SearchResult(searcher.doc(sd.doc), sd.score, filter));
		}
		return results;
	}

	public Set<String> listXrefs(String pathwaySource, String sysCode) throws IOException {
		Set<String> xrefs = new TreeSet<String>();

		TermQuery query = new TermQuery(new Term(
				DataNodeIndexer.FIELD_INDEXERID, DataNodeIndexer.class.getName() + pathwaySource)
		);
		TopDocs hits = searcher.search(query, null, 10000);
		for(ScoreDoc sd : hits.scoreDocs) {
			Document xrefDoc = searcher.doc(sd.doc);
			String[] idcodes = xrefDoc.getValues(DataNodeIndexer.FIELD_XID_CODE);
			if(idcodes == null) continue;
			for(String idcode : idcodes) {
				if(idcode.endsWith(":" + sysCode)) {
					xrefs.add(idcode.substring(0, idcode.lastIndexOf(':')));
				}
			}
		}
		return xrefs;
	}
}
