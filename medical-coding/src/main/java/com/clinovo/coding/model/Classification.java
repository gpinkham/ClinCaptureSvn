package com.clinovo.coding.model;

public class Classification {
	
	private String id;
	private String term;
	private String code;
	private String dictionary;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTerm() {
		
		return this.term;
	}

	public void setTerm(String name) {
		
		this.term = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDictionary() {
		
		return this.dictionary;
	}
	
	public void setDictionary(String dictionary) {
		this.dictionary = dictionary;
	}
}
