package com.clinovo.utils;

import java.util.HashMap;
import java.util.Map;

public class Common {

	public static String removeType(String str) {
		// input1(R) --> input1
		return str.substring(0, str.indexOf("("));
    }
	
	public static String removeOrderAndType(String str) {
		// (1)input1(R) --> input1
		return str.replaceAll("\\(\\d+\\)|\\(\\w+\\)", "");
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
	
	public static boolean checkAllTrue(Map<String, Boolean> map) {
		boolean result = true;
		for (String key: map.keySet()) {
			result = result && map.get(key);
		}
		
		return result;
	}

	public static Map<String, String> getFieldNameToSectionNameMap(String sectionName,
			Map<String, String> values) {
		Map<String, String> result = new HashMap<String, String>();
		for (String key: values.keySet()) {
			result.put(key, sectionName);
		}
		
		return result;
	}

	public static boolean arrayContains(String[] array,	String key) {
		for (String el: array){
			if (key.equals(el)) return true;
		}
		
		return false;
	}
	
	/**
	 * Method remove values from map.
	 *
	 * @param map
	 *            Map<String, String>
	 * @param values
	 *				String[]
	 */
	public static void removeValuesFromMap(Map<String, String> map, String[] values) {
		for (String key: values) {
			map.remove(key);
		}
	}
	
	public static Map<String, String> getMapWithoutSomeValues(Map<String, String> map,
			String[] arrayOfParametersToSkip) {
		Map<String, String> result = new HashMap<String, String>();
		for (String key: map.keySet()){
			if (!Common.arrayContains(arrayOfParametersToSkip, key)) {
				result.put(key, map.get(key));
			}
		}
		return result;
	}
}
