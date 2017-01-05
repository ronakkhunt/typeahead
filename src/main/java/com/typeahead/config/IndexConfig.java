package com.typeahead.config;

import com.typeahead.index.Index;

/**
 * Wrapper class around {@link Index} class.
 * @author ronakkhunt
 *
 */
public class IndexConfig {
	private Index index;
	
	public IndexConfig(String indexName) {
		index = new Index(indexName);
	}
	
	public Index getIndex() {
		return index;
	}
	
	public IndexConfig addSearchField(String fieldName) {
		index.addFieldMapping(fieldName);
		return this;
	}
}
