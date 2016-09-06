package com.typeahead.constants;

public enum FileExtension {
	DATA_MAP(".dm"), FIELD_FST_MAP(".ffm"), MAPPING(".mapping"), METADATA(".metadata")
	;
	
	private String extension;
	
	private FileExtension(String extension) {
		this.extension = extension;
	}
	public String getExtension() {
		return this.extension;
	}
}