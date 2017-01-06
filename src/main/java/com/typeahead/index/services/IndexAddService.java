package com.typeahead.index.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.index.IndexState;
import com.typeahead.tokens.util.TokenUtils;

/**
 * Service layer containing core logic for indexing data into {@link Index#fieldFSTMap}
 * @author ronakkhunt
 *
 */
public class IndexAddService {
	
	public void indexDocument(Index index, Document document, String output) {
		List<String> fieldToBeIndexed = index.getMappedField();
		
		for(String field: fieldToBeIndexed){
			Map<String, Map<Character,IndexState>> fieldFSTMap = index.getFieldFSTMap(field);
			
			String dataToIndex = document.get(field);
			
			indexField(index, fieldFSTMap, dataToIndex, output);
		}
	}
	
	private void indexField(Index index,
			Map<String, Map<Character,IndexState>> fieldFSTMap,
			String data, String output) {
		
		String []data_tokens = TokenUtils.getTokens(data);
		
		for(String token: data_tokens){
			if (!"".equals(token)){
				indexToken(index, fieldFSTMap, token, output);
			}
		}
	}
	
	private void indexToken(Index index, Map<String, Map<Character,IndexState>> fieldFSTMap, String token, String output) {
		
		int l = token.length();
		String state = index.getRootString() + token;
		
		
		for(int i = 0; i < l; i++){
			char ch = token.charAt(i);
			int  j = i + 2;
			
			String st = state.substring(0, i+1);
			String nst = state.substring(0, j);
			
			if(!fieldFSTMap.containsKey(st) ){
				Set<String> idSet = new HashSet<String>();
				idSet.add(output);
				Map<Character, IndexState> valueMap = new HashMap<Character, IndexState>();
				valueMap.put(ch, new IndexState(idSet, nst));
				fieldFSTMap.put(st, valueMap);
			}else{
				Map<Character, IndexState> valueMap = fieldFSTMap.get(st);
				
				if(valueMap.containsKey(ch)){
					valueMap.get(ch).getOutput().add(output);
				}else{
					Set<String> idSet = new HashSet<String>();
					idSet.add(output);
					valueMap.put(ch, new IndexState(idSet, nst));
				}
			}
		}

	}
	
}
