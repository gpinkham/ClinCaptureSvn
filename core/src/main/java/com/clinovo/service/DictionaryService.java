package com.clinovo.service;

import java.util.List;

import com.clinovo.model.Dictionary;

public interface DictionaryService {

	public List<Dictionary> findAll();
	
	public Dictionary findDictionary(int dictionaryId);

	public Dictionary findDictionary(String dictionaryName);

	public Dictionary saveDictionary(Dictionary dictionary);

	public void deleteDictionary(Dictionary dictionary);

}
