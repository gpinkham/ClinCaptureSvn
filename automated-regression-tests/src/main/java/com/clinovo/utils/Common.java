package com.clinovo.utils;

public class Common {

    public static String removeDoubleQuotes(String str) {
		// "str" --> str
    	return removeQuotes(str, "\"");
    }
    
    public static String removeSingleQuotes(String str) {
		// 'str' --> str
    	return removeQuotes(str, "'");	
    }
    
    public static String removeQuotes(String str, String quote) {
		// 'str' --> str
    	String result = str;
    	
    	if (result.startsWith(quote)) {
    		result = result.replaceFirst(quote, "");
    	}
    	
    	if (result.endsWith(quote)) {
    		result = (new StringBuilder(result)).deleteCharAt(result.length()-quote.length()).toString();
    	}
    	
    	return result;	
    }
}
