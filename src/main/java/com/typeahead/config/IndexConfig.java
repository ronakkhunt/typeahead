package com.typeahead.config;

import com.typeahead.index.Index;

/**
 * Wrapper class around {@link Index} class.
 * @author ronakkhunt
 *
 */
public class IndexConfig {
	Index index;
	
	public IndexConfig(String indexName) {
		index = new Index(indexName);
	}
	
	public Index getIndex() {
		return index;
	}
}
