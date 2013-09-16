package com.clinovo.service;

import java.util.List;

import com.clinovo.model.Dictionary;
import com.clinovo.model.Synonym;

public interface SynonymService {

	List<Synonym> findAll();
	
	Synonym findSynonym(int synonymId);

	Synonym findSynonym(String synonymName);

	Synonym saveSynonym(Synonym synon);

	void deleteSynonym(Synonym synonym);

	List<Synonym> findSynonym(Dictionary findDictionary);


}
