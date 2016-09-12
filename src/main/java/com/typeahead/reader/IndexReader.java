package com.typeahead.reader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.typeahead.config.IndexConfig;
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
	Index index;
	public IndexReader(IndexConfig config) {
		readerService = new IndexReaderService();
		index = config.getIndex();
	}
	
	/**
	 * Create new {@link Index}. 
	 * @param indexName
	 * @return
	 * @throws IndexAlreadyExistException
	 * @throws IOException 
	 */
	public IndexReader createIndex() throws IndexAlreadyExistException, IOException {
		IndexWriterUtil writerUtil = new IndexWriterUtil(index);
		writerUtil.createIndexFiles();
		return this;
	}
	
	/**
	 * Opens the existing {@link Index}
	 * @param indexName
	 * @return
	 * @throws IndexDoesNotExistException 
	 */
	@SuppressWarnings("unchecked")
	public IndexReader openIndex() throws IndexDoesNotExistException {
		
		IndexWriterUtil writerUtil = new IndexWriterUtil(index);
		
		if(!writerUtil.doesIndexExistance()) {
			throw new IndexDoesNotExistException("Index: "+index.getName()+" does not exist.");
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
		
		return this;
	}
	
	/**
	 * Created the {@link Index}, if it does not exist, otherwise opens the Existing {@link Index}
	 * @param indexName
	 * @return
	 * @throws IOException 
	 * @throws  
	 */
	public IndexReader createOrOpenIndex() throws  IOException {

		//TODO: This method use existing openIndex and createIndex API.
		// Need to verify integrity of this code.
		
		
		IndexWriterUtil writerUtil = new IndexWriterUtil(index);
		
		if(!writerUtil.doesIndexExistance()) {
			try {
				return createIndex();
			}catch(IndexAlreadyExistException ex){
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
		}else {
			try {
				return openIndex();
			} catch (IndexDoesNotExistException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this;
	}
	
	/**
	 * Delete {@link Index}
	 * @param indexName
	 * @throws IndexDoesNotExistException 
	 */
	public void deleteIndex() throws IndexDoesNotExistException {
		
		IndexWriterUtil writerUtil = new IndexWriterUtil(index );
		writerUtil.deleteIndexFiles();
	}
	
	public void setMapping(Map<String, String> mapping) {
		index.setMapping(mapping);
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
	
	public void setMergeFactor(int value) {
		index.setMergeFactor(value);
	}

	public void close() {
		// TODO Need to implement this method
		// Idea is this method should releases all the in-memory resources.
		
	}
}
