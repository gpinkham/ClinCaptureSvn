package com.clinovo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinovo.dao.DictionaryDAO;
import com.clinovo.model.Dictionary;
import com.clinovo.service.DictionaryService;

@Transactional
@Service("dictionaryService")
public class DictionaryServiceImpl implements DictionaryService {

	@Autowired DictionaryDAO dictionaryDAO;
	
	public List<Dictionary> findAll() {
		return dictionaryDAO.findAll();
	}
	
	public Dictionary findDictionary(int dictionaryId) {
		return dictionaryDAO.findById(dictionaryId);
	}

	public Dictionary findDictionary(String dictionaryName) {
		return dictionaryDAO.findByName(dictionaryName);
	}

	public Dictionary saveDictionary(Dictionary dictionary) {
		return dictionaryDAO.saveOrUpdate(dictionary);
	}

	public void deleteDictionary(Dictionary dictionary) {
		dictionaryDAO.deleteDictionary(dictionary);
	}
}
