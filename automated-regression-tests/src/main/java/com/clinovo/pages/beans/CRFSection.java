package com.clinovo.pages.beans;

import java.util.Map;

public class CRFSection {
	private String name = "";
	private Map<String, String> fieldNameToValueMap;
	private String addRows = "";
	private String markComplete = "no";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public CRFSection(String name){
		this.name = name;
	}

	public String getAddRows() {
		return addRows;
	}

	public void setAddRows(String addRows) {
		this.addRows = addRows;
	}

	public Map<String, String> getFieldNameToValueMap() {
		return fieldNameToValueMap;
	}

	public void setFieldNameToValueMap(Map<String, String> fieldNameToValueMap) {
		this.fieldNameToValueMap = fieldNameToValueMap;
	}

	public String getMarkComplete() {
		return markComplete;
	}

	public void setMarkComplete(String markComplete) {
		this.markComplete = markComplete;
	}
}
