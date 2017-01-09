package com.typeahead.index;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/**
 * This class will be used to hold the state data for value in {@link Index}
 * @author ronakkhunt
 *
 */
public class IndexState {
	Set<String> output;
	
	String nextState;

	public IndexState(){
		output = new HashSet<String>();
	}
	public IndexState(Set<String> set, String nextState){
		this.output = set;
		this.nextState = nextState;
	}
	
	@Override
	public String toString(){
		return "["+nextState+" --> "+ Arrays.toString(output.toArray()) +"]";
	}
	
	public Set<String> getOutput() {
		return output;
	}
	
	public String getNextState() {
		return nextState;
	}
}
