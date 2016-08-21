package com.typeahead.writer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.reader.services.IndexReaderService;
import com.typeahead.utils.FileUtil;
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
		File metadata = writerUtil.getMetadataFile();
		
		writerService.write(indexDataMap, index.getDataMap());
		writerService.write(fieldFSTMap, index.getFieldFSTMap());
		writerService.write(mapping, index.getMapping());
		writerService.write(metadata, index.getMetadata());
	}
	
	/**
	 * Merge all types of segments related to {@link Index} like <br>
	 * {@link Index#getDataMap()}, {@link Index#getDataMap()}, {@link Index#getMapping()} etc.
	 * @param index
	 * @param startSegmentNumber
	 */
	public void mergeIndexData(Index index, int startSegmentNumber) {
		int mergeFactor = index.getMergePolicy().getMergeFactor();
		mergeDataMapFile(index, startSegmentNumber, mergeFactor);
//		for(int i = 0; i < mergeFactor; i++) {
//			read file segment with version startSegmentNumber and put all data in single object like HashMap
			
//			startSegmentNumber -= 1;
//		}
	}
	
	/**
	 * Merge segments related to {@link Index#getDataMap()}
	 * @param index
	 * @param startSegmentNumber
	 * @param mergeFactor
	 */
	@SuppressWarnings("unchecked")
	private void mergeDataMapFile(Index index, int startSegmentNumber, int mergeFactor) {
		int temp = startSegmentNumber;
		
		IndexWriterUtil writerUtil = new IndexWriterUtil(index);
		IndexReaderService readerService = new IndexReaderService();
		
		Map<String, Document> mergedMap = new HashMap<String, Document>();
		
		for(int i = 0; i < mergeFactor; i++) {
			File indexDataMap = writerUtil.getDataMapFile(startSegmentNumber);
			mergedMap.putAll( readerService.read(indexDataMap, HashMap.class) );
			
			//Once segment is merged, removing that segment from directory.
			//TODO: However this operation can be skipped to save time.
			FileUtil.deleteDirectoryRecursively(indexDataMap);
			startSegmentNumber--;
		}
		
		int newSegmentVersion =  temp / mergeFactor;
		
		File mergedSegmentFile = writerUtil.getDataMapFile(newSegmentVersion);
		writerService.write(mergedSegmentFile, mergedMap);
		
	}
}
