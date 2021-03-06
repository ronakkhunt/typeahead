package com.typeahead.merge;

import com.typeahead.index.Document;
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
		int currentMergeLevel = 1;
		int newSegmentVersion = docCount / mergeFactor ;
		if(docCount % mergeFactor == 0) {
			writer.flushIndex(newSegmentVersion, currentMergeLevel);
			ensure(docCount, currentMergeLevel);
		}
	}
	public void ensure(int docCount, int mergeLevel) {
		int mergeFactor = getMergeFactor();
		int maxMergeLevel = getMaxMergeLevel();
		int newSegmentVersion = docCount / mergeFactor;
		index.setVersion(newSegmentVersion);
		
		if(newSegmentVersion % mergeFactor == 0 && mergeLevel <= maxMergeLevel) {
			//merge starting from segment newSegmentVesrion
			writer.mergeIndexData(newSegmentVersion, mergeLevel);
			//then recurse again
			ensure(newSegmentVersion, mergeLevel + 1);
		}
	}
	
	/**
	 * Returns segment number, in which the given search {@link Document} is lying at current Situation.<br>
	 * Returns {@link null} if {@link Document} is not merged yet. 
	 * @param maxLevel
	 * @param mergeFactor
	 * @param totalDocumentCount
	 * @param searchDocumentSequenceNumber
	 */
	public static String getSegmentNumber(int maxLevel, int mergeFactor, int totalDocumentCount, Long searchDocumentSequenceNumber) {
		
		 // To search based on 0-based index, removing 1 from the value.
		searchDocumentSequenceNumber--;
		
		int currentLevel = maxLevel;
		
		while(currentLevel != 0) {
			//maxDocumentAtCurrentLevel: Maximum number document possible in one segment, at current level
			int maxDocumentAtCurrentLevel = (int) Math.pow(mergeFactor, currentLevel);
			
			//If total number of document is greater than maxDocumentAtCurrentLevel, that means
			//we have merged that document in previous segment of same level.
			if(totalDocumentCount >= maxDocumentAtCurrentLevel){

				//closestMergedDocumentSequenceNumberAtCurrentLevel: Sequence number of Document which got merge in last
				//segment of current level
				int closestMergedDocumentSequenceNumberAtCurrentLevel = (totalDocumentCount/maxDocumentAtCurrentLevel)*maxDocumentAtCurrentLevel;
				
				/**
				 * this means, document we are searching for is in the segment of currentlevel
				 * and that segment number will be searchDocumentSequenceNumber/maxDocumentAtCurrentLevel
				 * 
				 * For example,
				 * 
				 * mergeFactor = 10
				 * maxLevel = 3
				 * totalDocumentCount = 530
				 * searchDocumentSequenceNumber = 474 (475 -1 = 474)// after decrement by one it will be 477.
				 * 
				 * At this point,
				 * closestMergedDocumentSequenceNumberAtCurrentLevel = 500
				 * currentLevel = 2
				 * maxDocumentAtCurrentLevel = 100
				 * 
				 * according to below calculation, answer will be: 2.5
				 */

				if(closestMergedDocumentSequenceNumberAtCurrentLevel >= searchDocumentSequenceNumber) {
					return (currentLevel+"."+ ((searchDocumentSequenceNumber/maxDocumentAtCurrentLevel) + 1));
					
				}else{
					currentLevel--;
					searchDocumentSequenceNumber = searchDocumentSequenceNumber - closestMergedDocumentSequenceNumberAtCurrentLevel;
					totalDocumentCount = totalDocumentCount - closestMergedDocumentSequenceNumberAtCurrentLevel;
				}
			}else{
				currentLevel--;
			}
			
		}
		return null;
	}
	
	public Integer getMergeFactor() {
		return (Integer) index.getMetadata().get("mergeFactor");
	}
	public Integer getMaxMergeLevel() {
		return (Integer) index.getMetadata().get("maxMergeLevel");
	}
}
