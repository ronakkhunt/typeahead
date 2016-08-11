package com.typeahead.index.services;

import java.util.List;
import java.util.Map;

import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.index.IndexState;
import com.typeahead.tokens.util.TokenUtils;

/**
 * Service layer containing core logic for removing data from {@link Index#fieldFSTMap}
 * @author ronakkhunt
 *
 */
public class IndexDeleteService {
	
	public void deleteDocument(Index index, Document document, String output) {
		List<String> fieldToBeIndexed = index._getMappedField();
		
		for(String field: fieldToBeIndexed){
			Map<String, Map<Character,IndexState>> fieldFSTMap = index.getFieldFSTMap(field);
			
			String dataToDelete = document.get(field);
			
			deleteField(index, fieldFSTMap, dataToDelete, output);
			System.out.println();
		}
	}

	private void deleteField(Index index,
			Map<String, Map<Character, IndexState>> fieldFSTMap,
			String data, String output) {
		
		String []data_tokens = TokenUtils.getTokens(data);
		
		for(String token: data_tokens){
			if (!"".equals(token)){
				deleteToken(index, fieldFSTMap, token, output);
			}
		}
		
	}

	private void deleteToken(Index index,
			Map<String, Map<Character, IndexState>> fieldFSTMap, String token,
			String output) {
		
		int l = token.length();
		String state = index.getRootString() + token;
		
		for(int i = 0; i < l; i++){
			char ch = token.charAt(i);
			int j = i + 1;
			
			String st = state.substring(0, j);
			
			if(fieldFSTMap.containsKey(st)) {
				if(fieldFSTMap.get(st).get(ch).output.contains(output)) {
					fieldFSTMap.get(st).get(ch).output.remove(output);
				}
			}
			
		}
	}
}
