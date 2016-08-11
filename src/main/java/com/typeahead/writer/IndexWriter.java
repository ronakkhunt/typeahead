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
	
	IndexWriterService writerService;
	public IndexWriter(){
		writerService = new IndexWriterService();
	}
	
	public void writeIndex(Index index) {
		IndexWriterUtil writerUtil = new IndexWriterUtil(index);
		
		File indexDataMap = writerUtil.getDataMapFile();
		File fieldFSTMap = writerUtil.getFieldFSTMapFile();
		File mapping = writerUtil.getMappingFile();
		
		writerService.write(indexDataMap, index.getDataMap());
		writerService.write(fieldFSTMap, index.getFieldFSTMap());
		writerService.write(mapping, index.getMapping());
		
	}
}
