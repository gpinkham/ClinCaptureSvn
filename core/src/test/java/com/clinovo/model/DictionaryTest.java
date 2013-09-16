package com.clinovo.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.clinovo.model.Status.DictionaryType;

public class DictionaryTest {

	@Test
	public void testThatNewDictionaryHasUnknownType() {
		
		assertEquals(DictionaryType.getType(new Dictionary().getType()), DictionaryType.UNKNOWN);
	}
	
	@Test
	public void testThatDictionaryUpdatesTypeWhenSet() {
		
		Dictionary dictionary = new Dictionary();
		dictionary.setType(DictionaryType.CUSTOM.ordinal());
		
		assertEquals(DictionaryType.CUSTOM,  DictionaryType.getType(dictionary.getType()));
	}
}
