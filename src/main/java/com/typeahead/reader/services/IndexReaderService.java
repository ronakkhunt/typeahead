package com.typeahead.reader.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typeahead.index.Index;
/**
 * Service layer to de-couple low-level Serialization functionality for reading {@link Index} related data to disk.
 * As of now, this is using jackson for serialization
 * @author ronakkhunt
 *
 */
public class IndexReaderService {
	
	ObjectMapper mapper;
	
	public IndexReaderService() {
		mapper = new ObjectMapper();
	}
	
	public <T> T read(File file, Class<T> clazz) {
		try {
			return mapper.readValue(file,  clazz);
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
		return null;
	}
	
	public String read(File file) throws FileNotFoundException {
		FileReader reader = new FileReader(file);
		BufferedReader bReader = new BufferedReader(reader);
		try {
			return bReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
