package com.typeahead.writer.services;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import com.typeahead.reader.services.IndexReaderService;

public class IndexWriterServiceTest {
	
	@Test
	public void appendTest() throws FileNotFoundException {
		IndexWriterService writerService = new IndexWriterService();
		IndexReaderService readerService = new 	IndexReaderService();
		
		String fileName = "_test_file.del";
		
		File file = new File(fileName);
		
		String dataToAppend = "1,";
		
		writerService.append(file, dataToAppend);
		
		String output = readerService.read(file);
		
		
		Assert.assertEquals("1,", output);
		
		dataToAppend = "2,3,";
		
		writerService.append(file, dataToAppend);
		
		output = readerService.read(file);
		
		
		Assert.assertEquals("1,2,3,", output);
		
		//cleaning the file.
		file.delete();
		
		
	}
}
