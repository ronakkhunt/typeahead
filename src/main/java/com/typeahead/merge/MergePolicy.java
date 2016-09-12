package com.typeahead.merge;

import com.typeahead.config.IndexConfig;
import com.typeahead.index.Index;
import com.typeahead.writer.IndexWriter;


/**
 * This class is to define the merge policy to merge the index segments.
 * @author ronakkhunt
 *
 */
public class MergePolicy {
	
	Index index;
	IndexWriter writer;
	
	public MergePolicy(IndexWriter writer) {
		this.index = writer.getIndexConfig().getIndex();
		this.writer = writer;
	}
	
	public void ensurePolicy() {
		int docCount = index.getDataMap().size();
		int mergeFactor = getMergeFactor();
		int newSegmentVesrion = docCount / mergeFactor ;
		if(docCount % mergeFactor == 0) {
			flushIndex(newSegmentVesrion);
			ensure(docCount);
		}
	}
	public void ensure(int docCount) {
		int mergeFactor = getMergeFactor();
		int newSegmentVesrion = docCount / mergeFactor;
		index.getMetadata().put("version", newSegmentVesrion);
		
		if(newSegmentVesrion % mergeFactor == 0) {
			//merge starting from segment newSegmentVesrion
			writer.mergeIndexData(index, newSegmentVesrion);
			//then recurse again
			ensure(newSegmentVesrion);
		}
	}
	
	/**
	 * TODO: this methods need to be moved to {@link IndexWriter}
	 * Used before closing the index, to write remaining data onto files.
	 * It will use last version + 1 to create last segment
	 */
	public void flushIndex() {
		flushIndex(index.getVersion() + 1);
	}
	
	/**
	 * TODO: this methods need to be moved to {@link IndexWriter}
	 * Flushed/Writes data onto disk, creating file with given version.
	 * @param newSegmentVersion
	 */
	public void flushIndex(int newSegmentVersion) {
		index.setVersion(newSegmentVersion);
		writer.writeIndex();
	}
	
	public Integer getMergeFactor() {
		return (Integer) index.getMetadata().get("mergeFactor");
	}
}
