package com.typeahead.search;

import java.util.List;

import com.typeahead.config.IndexConfig;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.index.services.IndexSearchService;

/**
 * Class providing search functionality for {@link Index}
 * @author ronakkhunt
 *
 */
public class IndexSearcher {
	
	IndexConfig indexConfig;
	public IndexSearcher(IndexConfig config) {
		indexConfig = config;
	}
	
	/**
	 * Returns {@link List} of matching {@link Document}s
	 * @param queryString
	 * @return
	 */
	public List<Document> search(String field, String queryString) {
		IndexSearchService searchService = new IndexSearchService();
		return searchService.searchDocuments(indexConfig.getIndex(), field, queryString);
	}
}
