package com.typeahead.index.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.index.IndexState;
import com.typeahead.tokens.util.TokenUtils;

/**
 * Service layer containing core logic for Searching data for specific field from {@link Index#fieldFSTMap}
 * @author ronakkhunt
 *
 */
public class IndexSearchService {
	
	/**
	 * Return list of IDs of {@link Document} for matching result
	 * @param index
	 * @param field
	 * @param queryString
	 * @return
	 */
	public List<String> searchIDs(Index index, String field, String queryString) {
		
		Set<String> results_set = _searchIDs(index, field, queryString);
		
		List<String> result_ids = new ArrayList<String>();
		result_ids.addAll(results_set);
		return result_ids;
		
	}
	
	/**
	 * Returns list of {@link Document} for matching result
	 * @param index
	 * @param field
	 * @param queryString
	 * @return
	 */
	public List<Document> searchDocuments(Index index, String field, String queryString) {
		
		ArrayList<Document> result_list = new ArrayList<Document>();
		
		Set<String> results_set = _searchIDs(index, field, queryString);
		
		for(String id: results_set){
			result_list.add(index.getDataMap().get(id));
		}
		
		return result_list;
		
	}
	
	private Set<String> _searchIDs(Index index, String field, String queryString) {
		String []data_tokens = TokenUtils.getTokens(queryString);
		
		List<Set<String>> ids_list = new ArrayList<Set<String>>();
		
		for(String token: data_tokens){
			if(!"".equals(token)){
				ids_list.add(searchToken(index, field, token));
			}
		}
		
		Set<String> ans = new TreeSet<String>(ids_list.get(0));
		for(Set<String> set: ids_list){
			ans.retainAll(set);
		}
		return ans;
	}
	
	private Set<String> searchToken(Index index, String field, String token) {
		String word = "";
		if(word.length() < 1){
			return new HashSet<String>();
		}
		
		Map<String, Map<Character, IndexState>> fst = index.getFieldFSTMap(field);
		
		int l = word.length();
		String state = '|'+word;
		String cur_st = null;
		for(int i = 0; i < l; i++) {
			char ch = word.charAt(i);
			cur_st = state.substring(0, i+1);
			
			if(!fst.containsKey(cur_st)){
				return new HashSet<String>();
			}else{
				Map<Character, IndexState> dic = fst.get(cur_st);
				if(!dic.containsKey(ch)){
					return new HashSet<String>();
				}
			}
		}
		
		if( fst.get(cur_st).containsKey(word.charAt(l-1)) ){
			return fst.get(cur_st).get(word.charAt(l-1)).getOutput();
		}else{
			return new HashSet<String>(); 
		}
	}

}
