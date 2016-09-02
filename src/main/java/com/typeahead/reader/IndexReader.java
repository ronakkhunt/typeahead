package com.typeahead.reader;

import java.io.File;
import java.io.IOException;
import java.security.cert.Extension;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.typeahead.constants.FileExtension;
import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.reader.services.IndexReaderService;
import com.typeahead.utils.FileUtil;
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
	
	/**
	 * Create new {@link Index}. 
	 * @param indexName
	 * @return
	 * @throws IndexAlreadyExistException
	 * @throws IOException 
	 */
	public Index createIndex(String indexName) throws IndexAlreadyExistException, IOException {
		Index index = new Index(indexName);;
		IndexWriterUtil writerUtil = new IndexWriterUtil(index);
		
		writerUtil.createIndexFiles();
		
		return index;
	}
	
	/**
	 * Opens the existing {@link Index}
	 * @param indexName
	 * @return
	 * @throws IndexDoesNotExistException 
	 */
	@SuppressWarnings("unchecked")
	public Index openIndex(String indexName) throws IndexDoesNotExistException {
		
		Index index = new Index(indexName);
		IndexWriterUtil writerUtil = new IndexWriterUtil(index);
		
		if(!writerUtil.doesIndexExistance()) {
			throw new IndexDoesNotExistException("Index: "+indexName+" does not exist.");
		}	
		
		//Reading metadata first.
		File metadata = writerUtil.getMetadataFile();
		index.setMetadata(readerService.read(metadata, HashMap.class));
		
		//Reading dataMap files for Index
		_readDataMapFile(index, writerUtil);
		
		//Reading fstMap files for Index
		File fieldFSTMap = writerUtil.getFieldFSTMapFile();
		index.setFieldFSTMap(readerService.read(fieldFSTMap, HashMap.class));
		
		//Reading mapping file for Index
		File mapping = writerUtil.getMappingFile();
		index.recoverMapping(readerService.read(mapping, HashMap.class));
		
		return index;
	}
	
	/**
	 * Created the {@link Index}, if it does not exist, otherwise opens the Existing {@link Index}
	 * @param indexName
	 * @return
	 */
	public Index createOrOpenIndex(String indexName) {
		return null;
	}
	
	/**
	 * Delete {@link Index}
	 * @param indexName
	 * @throws IndexDoesNotExistException 
	 */
	public void deleteIndex(String indexName) throws IndexDoesNotExistException {
		
		Index index = new Index(indexName);
		IndexWriterUtil writerUtil = new IndexWriterUtil(index );
		
		writerUtil.deleteIndexFiles();
	}
	
	/**
	 * Read all the DataMap file relating to {@link Index#getDataMap()}<br>
	 * All these file are ending with extension {@link FileExtension#DATA_MAP}
	 * @param index
	 * @param writerUtil
	 */
	@SuppressWarnings("unchecked")
	private void _readDataMapFile(Index index, IndexWriterUtil writerUtil) {
		Map<String, Document> dataMap = new HashMap<String, Document>();
		
		File[] dataMapFiles = FileUtil.getAllFilesEndingWith(index.getIndexDirectoryPath(), 
				FileExtension.DATA_MAP.getExtension());
		
		for(File f: dataMapFiles) {
			dataMap.putAll(readerService.read(f, HashMap.class));
		}
		index.setDataMap(dataMap);
	}
}
