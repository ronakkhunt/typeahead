package com.typeahead.writer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typeahead.index.Index;


/**
 * IndexWriter class is used to write index to the disk.
 * @author ronakkhunt
 *
 */
public class IndexWriter {
	ObjectMapper mapper;
	
	public IndexWriter(){
		mapper = new ObjectMapper();
	}
	
	public void writeIndex(Index index) {
		
		File indexDataMap = IndexWriterUtil.getDataMapFile(index);
		File fieldFSTMap = IndexWriterUtil.getFieldFSTMapFile(index);
		File mapping = IndexWriterUtil.getMappingFile(index);
		
		try {
			mapper.writeValue(indexDataMap, index.getDataMap());
			mapper.writeValue(fieldFSTMap, index.getFieldFSTMap());
			mapper.writeValue(mapping, index.getMapping());
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
