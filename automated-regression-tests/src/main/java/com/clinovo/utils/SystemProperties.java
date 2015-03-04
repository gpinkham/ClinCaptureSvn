package com.clinovo.utils;

import java.util.Map;

public class SystemProperties {
	
	private String allowCRFEvaluationValue = "";
	    
    private String evaluateWithContextValue = "";
    
    private String bioontologyURL = "";
	
	private String bioontologyAPIKey = "";
    
    private String autoCodeDictionaryName = "";
	
	public static SystemProperties fillSystemPropertiesFromTableRow(
			Map<String, String> row) {
		
		SystemProperties prop = new SystemProperties();
		
		if (row.get("Allow CRF evaluation") != null) {
			prop.setAllowCRFEvaluationValue(row.get("Allow CRF evaluation"));
    	}
		
    	if (row.get("Evaluate with context") != null) {
    		prop.setEvaluateWithContextValue(row.get("Evaluate with context"));
    	}
    	
    	if (row.get("Bioontology URL") != null) {
			prop.setBioontologyURL(row.get("Bioontology URL"));
    	}
		
    	if (row.get("Bioontology API key") != null) {
			prop.setBioontologyAPIKey(row.get("Bioontology API key"));
    	}
    	
    	if (row.get("Auto-code Dictionary Name") != null) {
    		prop.setAutoCodeDictionaryName(row.get("Auto-code Dictionary Name"));
    	}
    	
		return prop;
	}

	public String getEvaluateWithContextValue() {
		return evaluateWithContextValue;
	}

	public void setEvaluateWithContextValue(String evaluateWithContextValue) {
		this.evaluateWithContextValue = evaluateWithContextValue;
	}

	public String getAllowCRFEvaluationValue() {
		return allowCRFEvaluationValue;
	}

	public void setAllowCRFEvaluationValue(String allowCRFEvaluationValue) {
		this.allowCRFEvaluationValue = allowCRFEvaluationValue;
	}

	public String getBioontologyURL() {
		return bioontologyURL;
	}

	public void setBioontologyURL(String bioontologyURL) {
		this.bioontologyURL = bioontologyURL;
	}

	public String getBioontologyAPIKey() {
		return bioontologyAPIKey;
	}

	public void setBioontologyAPIKey(String bioontologyAPIKey) {
		this.bioontologyAPIKey = bioontologyAPIKey;
	}

	public String getAutoCodeDictionaryName() {
		return autoCodeDictionaryName;
	}

	public void setAutoCodeDictionaryName(String autoCodeDictionaryName) {
		this.autoCodeDictionaryName = autoCodeDictionaryName;
	}
}
