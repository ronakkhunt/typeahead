package com.typeahead.reader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.reader.services.IndexReaderService;
import com.typeahead.writer.IndexWriterUtil;


/**
 * IndexReader is class used to create/read index from disk. 
 * @author ronakkhunt
 */
public class IndexReader {
	
	IndexReaderService readerService;
	
	public IndexReader() {
		readerService = new IndexReaderService();
	}
	
	public Index createIndex(String indexName) {
		return new Index(indexName);
	}
	
	@SuppressWarnings("unchecked")
	public Index openIndex(String indexName) {
		
		//TODO: this should throw "IndexDoesNotExist" Exception
		
		Index index = new Index(indexName);
		
		File indexDataMap = IndexWriterUtil.getDataMapFile(index);
		File fieldFSTMap = IndexWriterUtil.getFieldFSTMapFile(index);
		File mapping = IndexWriterUtil.getMappingFile(index);
		
		index.setDataMap(readerService.read(indexDataMap, HashMap.class));
		index.setFieldFSTMap(readerService.read(fieldFSTMap, HashMap.class));
		index.recoverMapping(readerService.read(mapping, HashMap.class));

		return index;
	}
	
	public Index createOrOpenIndex(String indexName) {
		return null;
	}
}
