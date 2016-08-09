package com.typeahead.writer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typeahead.index.Index;
import com.typeahead.writer.services.IndexWriterService;


/**
 * IndexWriter class is used to write index to the disk.
 * @author ronakkhunt
 *
 */
public class IndexWriter {
	
	IndexWriterService writerServices;
	public IndexWriter(){
		writerServices = new IndexWriterService();
	}
	
	public void writeIndex(Index index) {
		
		File indexDataMap = IndexWriterUtil.getDataMapFile(index);
		File fieldFSTMap = IndexWriterUtil.getFieldFSTMapFile(index);
		File mapping = IndexWriterUtil.getMappingFile(index);
		
		writerServices.write(indexDataMap, index.getDataMap());
		writerServices.write(fieldFSTMap, index.getFieldFSTMap());
		writerServices.write(mapping, index.getMapping());
		
	}
}
