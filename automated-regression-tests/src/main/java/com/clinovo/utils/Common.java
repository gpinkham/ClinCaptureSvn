package com.clinovo.utils;

public class Common {

    public static String removeQuotes(String str) {
		// "str" --> str
    	String result = str;
    	
    	if (result.startsWith("\"")) {
    		result = result.replaceFirst("\"", "");
    	}
    	
    	if (result.endsWith("\"")) {
    		result = (new StringBuilder(result)).deleteCharAt(result.length()-1).toString();
    	}
    	
    	return result;	
    }
}
