package com.typeahead.writer.services;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typeahead.index.Index;
/**
 * Service layer to de-couple low-level Serialization functionality for writing {@link Index} related data to disk.
 * As of now, this is using jackson for serialization
 * @author ronakkhunt
 *
 */
public class IndexWriterService {
	ObjectMapper mapper;
	
	public IndexWriterService() {
		mapper  = new ObjectMapper();
	}
	public void write(File file, Object value) {
		try {
			mapper.writeValue(file, value);
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
