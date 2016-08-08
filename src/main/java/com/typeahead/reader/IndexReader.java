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
import com.typeahead.writer.IndexWriterUtil;


/**
 * IndexReader is class used to create/read index from disk. 
 * @author ronakkhunt
 */
public class IndexReader {
	
	static ObjectMapper mapper = new ObjectMapper();
	
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
		
		try {
			index.setDataMap(mapper.readValue(indexDataMap,  HashMap.class));
			index.setFieldFSTMap(mapper.readValue(fieldFSTMap, HashMap.class));
			index.recoverMapping(mapper.readValue(mapping, HashMap.class));
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return index;
	}
	
	public Index createOrOpenIndex(String indexName) {
		return null;
	}
}
