package com.typeahead.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.typeahead.config.IndexConfig;
import com.typeahead.constants.FileExtension;
import com.typeahead.constants.FileName;
import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.reader.services.IndexReaderService;
import com.typeahead.utils.FileUtil;
import com.typeahead.writer.IndexWriter;
import com.typeahead.writer.IndexWriterUtil;


/**
 * IndexReader is class used to create/read index from disk. 
 * @author ronakkhunt
 */
public class IndexReader {
	
	private IndexReaderService readerService;
	private IndexConfig indexConfig;
	
	public IndexReader(IndexConfig config) {
		readerService = new IndexReaderService();
		indexConfig = config;
	}
	
	/**
	 * Opens the existing {@link Index}
	 * @param indexName
	 * @return
	 * @throws IndexDoesNotExistException 
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public IndexReader openIndex() throws IndexDoesNotExistException, FileNotFoundException {
		Index index = indexConfig.getIndex();
		IndexWriterUtil writerUtil = new IndexWriterUtil(index);
		
		if(!writerUtil.doesIndexExist()) {
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
		
		
		IndexWriterUtil writerUtil = new IndexWriterUtil(indexConfig.getIndex());
		
		if(!writerUtil.doesIndexExist()) {
			try {
				return _createIndex();
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
	 * Created index using {@link IndexWriter} ans set it in {@link IndexReader}
	 * 
	 * NOTE: Ideally createIndex() is there in {@link IndexWriter}, but to define<br>
	 * {@link IndexReader#createOrOpenIndex()} we need to define this private method. 
	 * 
	 * @return
	 * @throws IndexAlreadyExistException
	 * @throws IOException
	 */
	private IndexReader _createIndex() throws IndexAlreadyExistException, IOException {
		IndexWriter writer = new IndexWriter(indexConfig);
		writer.createIndex();
		return this;
	}
	
	/**
	 * Read all the DataMap file relating to {@link Index#getDataMap()}<br>
	 * All these file are ending with extension {@link FileExtension#DATA_MAP}
	 * @param index
	 * @param writerUtil
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	private void _readDataMapFile(Index index, IndexWriterUtil writerUtil) throws FileNotFoundException {
		Map<String, Document> dataMap = new HashMap<String, Document>();
		
		//Reading all segment with FileExtension.DATA_MAP
		File[] segmentDirectories = FileUtil.getAllDirectories(index.getIndexDirectoryPath());
		
		for(File f: segmentDirectories) {
			
			File segmentFile = new File(f.getAbsolutePath() + 
					"/dataMap"+FileExtension.DATA_MAP.getExtension());
			//read segment file
			dataMap.putAll(readerService.read(segmentFile, HashMap.class));
			
			//read the FileName#DELETE_INDEX file. and remove all the IDs 
			//from the dataMap.
			String[]IDs = getDeletedDocumentIDsFromSegment(f);
			for(String id: IDs) {
				dataMap.remove(id);
			}
		}
		
		//Reading Individual all document file(s).
		File[] dataDocumentFiles = FileUtil.getAllFilesEndingWith(index.getIndexDirectoryPath(),
				FileExtension.DATA_MAP_DOCUMENT.getExtension());
		
		for(File f: dataDocumentFiles) {
			Document doc = readerService.read(f, Document.class);
			dataMap.put(doc.getId(), doc);
		}
		
		index.setDataMap(dataMap);
	}
	
	/**
	 * Read the FileName#DATA_MAP_DELETE file from given segment directory and return an array<br>
	 * {@link String} containing IDs of deleted documents.
	 * @param f
	 * @return
	 * @throws FileNotFoundException
	 */
	private String[] getDeletedDocumentIDsFromSegment(File f) throws FileNotFoundException {
		File dataMapDelFile = new File(f.getAbsolutePath() + "/" + FileName.DATA_MAP_DELETE.getName());
		
		//read above mentioned file only if it exist.
		if(dataMapDelFile.exists()){
			String commaSeparatedIDs = readerService.read(dataMapDelFile);
			return commaSeparatedIDs.split(",");
		}
		return new String[0];
	}
	
	public void setMergeFactor(int value) {
		Index index = indexConfig.getIndex();
		index.setMergeFactor(value);
	}
	
	public void setMapping(Map<String, String> mapping) {
		Index index = indexConfig.getIndex();
		index.setMapping(mapping);
	}

	public void close() {
		// TODO Need to implement this method
		// Idea is this method should releases all the in-memory resources.
		
	}
}
