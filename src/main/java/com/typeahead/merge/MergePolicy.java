package com.typeahead.merge;

import com.typeahead.index.Index;
import com.typeahead.writer.IndexWriter;


/**
 * This class is to define the merge policy to merge the index segments.
 * @author ronakkhunt
 *
 */
public class MergePolicy {
	private Integer mergeFactor;
	Integer maxDocCount;
	Index index;
	IndexWriter writer;
	public MergePolicy(Index index) {
		this.index = index;
		writer = new IndexWriter();
		this.mergeFactor = (Integer)index.getMetadata().get("mergeFactor");
		this.maxDocCount = index.getDataMap().size();
	}
	
	public void ensurePolicy() {
		int docCount = index.getDataMap().size();
		int newSegmentVesrion = docCount / mergeFactor;
		if(docCount % mergeFactor == 0) {
			flushIndex(newSegmentVesrion);
			ensure(docCount);
		}
	}
	public void ensure(int docCount) {
		int newSegmentVesrion = docCount / mergeFactor;
		index.getMetadata().put("version", newSegmentVesrion);
		
		if(newSegmentVesrion % mergeFactor == 0) {
			//merge starting from segment newSegmentVesrion
			writer.mergeIndexData(index, newSegmentVesrion);
			//then recurse again
			ensure(newSegmentVesrion);
		}
	}
	
	private void flushIndex(int newSegmentVersion) {
		IndexWriter writer = new IndexWriter();
		writer.writeIndex(index);
	}
	
	public Integer getMergeFactor() {
		return mergeFactor;
	}
	
}
