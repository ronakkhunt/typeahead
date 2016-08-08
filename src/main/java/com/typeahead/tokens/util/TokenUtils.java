package com.typeahead.tokens.util;

import java.util.StringTokenizer;
/**
 * Token util class to tokenize given data string
 * @author ronakkhunt
 *
 */
public class TokenUtils {
	
	public static String[] getTokens(String data){
		return TokenUtils.getTokens(data, " \t\n");
	}
	
	public static String[] getTokens(String data, String delims){
		StringTokenizer tokenizer = new StringTokenizer(data, delims);
		String [] tokens = new String[tokenizer.countTokens()];
		int i = 0;
		while(tokenizer.hasMoreTokens()){
			tokens[i++] = tokenizer.nextToken().toLowerCase();
		}
		return tokens;
	}
}
