package com.typeahead.async;

import java.io.File;

import com.typeahead.index.Document;
import com.typeahead.writer.services.IndexWriterService;

/**
 * To Flush the document onto disk Asynchronously.
 * @author ronakkhunt
 *
 */
public class FlushDocumentAsync implements Runnable {

	File file;
	Document document;
	IndexWriterService writerService;
	
	public FlushDocumentAsync(File file, Document doc, IndexWriterService writerService) {
		this.file = file;
		this.document = doc;
		this.writerService = writerService;
	}
	public void run() {
		writerService.write(file, document);
	}

}
