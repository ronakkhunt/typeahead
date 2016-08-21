package com.typeahead.util;

import java.util.List;
import java.util.Map;

import com.typeahead.index.Document;
/**
 * Container class to keep test data and expected result for queries.
 * @author ronakkhunt
 *
 */
public class TestSet {
	List<Document> documents;
	Map<String, List<String>> queries;
	
	public List<Document> getDocuments() {
		return documents;
	}
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	public Map<String, List<String>> getQueries() {
		return queries;
	}
	public void setQueries(Map<String, List<String>> queries) {
		this.queries = queries;
	}
}
