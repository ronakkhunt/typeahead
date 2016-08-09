package com.typeahead;

import com.typeahead.reader.IndexReader;
import com.typeahead.reader.services.IndexReaderService;
import com.typeahead.writer.IndexWriter;

/**
 * This class is used to maintain the list of the To-Do task.
 * @author ronakkhunt
 *
 */
public class ToDo {
	
	/**
	 * 3) Index Merging policy
	 * 
	 * When should we write the index onto disk?
	 */
	
	/**
	 * 2) Serializer for index data in {@link IndexReaderService} and {@link IndexWriterService}
	 * 
	 * Write now using Jackson to serialize the index data. Need to change this to proper
	 * Faster method
	 */
	
	/**
	 * 1) API design for Index class itself.
	 * 
	 * this class has so many getter/setter which should not be exposed to end-user, but is required 
	 * for now.
	 */
	
}
