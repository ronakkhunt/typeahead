package com.typeahead.constants;

public enum FileName {
	DATA_MAP_DELETE("dataMap", FileExtension.DELETE_INDEX)
	;
	
	private String name;
	
	private FileName(String name, FileExtension extension) {
		this.name = name + extension.getExtension();
	}
	
	public String getName() {
		return name;
	}
}
