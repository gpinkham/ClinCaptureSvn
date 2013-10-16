package com.clinovo.service.impl;

import java.util.Date;
import java.util.List;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinovo.dao.DictionaryDAO;
import com.clinovo.exception.CodeException;
import com.clinovo.model.Dictionary;
import com.clinovo.model.Status.DictionaryType;
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
	
	public Dictionary findByStudy(int study) {
		return dictionaryDAO.findByStudy(study);
	}

	public Dictionary saveDictionary(Dictionary dictionary) {
		return dictionaryDAO.saveOrUpdate(dictionary);
	}

	public void deleteDictionary(Dictionary dictionary) {
		dictionaryDAO.deleteDictionary(dictionary);
	}

	public Dictionary createDictionary(String dictionaryName, StudyBean study) throws CodeException {
		
		// check if dictionary with similar name exists
		if(doesDictionaryExist(dictionaryName)) {
			
			throw new CodeException("A dictionary with a similar name exists");
			
		} else {
			
			Dictionary dictionary = new Dictionary();
			
			dictionary.setStudy(study.getId());
			dictionary.setName(dictionaryName);
			dictionary.setDateCreated(new Date());
			dictionary.setType(DictionaryType.CUSTOM.ordinal());
			dictionary.setDescription("Automatically created by the system to support auto-coding");
			
			return saveDictionary(dictionary);
		}
		
	}

	private boolean doesDictionaryExist(String dictionaryName) {
		
		List<Dictionary> dictionaries = dictionaryDAO.findAll();
		
		for(Dictionary dictionary : dictionaries) {
			
			if(dictionary.getName().equals(dictionaryName)) {
				return true;
			}
		}
		
		return false;
	}
}
