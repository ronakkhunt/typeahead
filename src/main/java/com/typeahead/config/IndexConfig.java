package com.typeahead.config;

import com.typeahead.index.Index;

public class IndexConfig {
	Index index;
	
	public IndexConfig(String indexName) {
		index = new Index(indexName);
	}
	
	public Index getIndex() {
		return index;
	}
}
