package com.clinovo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.dao.SynonymDAO;
import com.clinovo.model.Dictionary;
import com.clinovo.model.Synonym;
import com.clinovo.service.SynonymService;

@Service("synonymService")
public class SynonymServiceImpl implements SynonymService {

	@Autowired
	SynonymDAO synonymDAO;

	public Synonym findSynonym(int synonymId) {
		return synonymDAO.findById(synonymId);
	}

	public Synonym findSynonym(String synonymName) {
		return synonymDAO.findByName(synonymName);
	}

	public List<Synonym> findAll() {
		return synonymDAO.findAll();
	}

	public Synonym saveSynonym(Synonym synon) {
		return synonymDAO.saveOrUpdate(synon);
	}

	public void deleteSynonym(Synonym synonym) {
		synonymDAO.deleteSynonym(synonym);
	}

	public List<Synonym> findSynonym(Dictionary dictionary) {
		return synonymDAO.findByDictionary(dictionary);
	}
}
