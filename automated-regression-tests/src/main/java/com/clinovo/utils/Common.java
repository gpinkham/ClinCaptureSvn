package com.clinovo.utils;

public class Common {

	public static String removeType(String str) {
		// input1(R) --> input1
		return str.substring(0, str.indexOf("("));
    }
	
	public static String removeOrderAndType(String str) {
		// (1)input1(R) --> input1
		return str.replaceAll("\\(\\d+\\)|\\(\\w\\)", "");
    }
	
	public static String getType(String str) {
		// (1)input1(R) --> R
		return str.replaceAll("\\(\\d+\\)", "").replace(removeOrderAndType(str)+"(", "").replace(")", "");
    }
	
    public static String removeDoubleQuotes(String str) {
		// "str" --> str
    	return removeQuotes(str, "\"");
    }
    
    public static String removeSingleQuotes(String str) {
		// 'str' --> str
    	return removeQuotes(str, "'");	
    }
    
    public static String removeQuotes(String str, String quote) {
		// 'quote'str'quote' --> str
    	String result = str;
    	
    	if (result.startsWith(quote)) {
    		result = result.replaceFirst(quote, "");
    	}
    	
    	if (result.endsWith(quote)) {
    		result = (new StringBuilder(result)).deleteCharAt(result.length()-quote.length()).toString();
    	}
    	
    	return result;	
    }

	public static void waitABit(int milSec) {
		try {
			Thread.sleep(milSec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
