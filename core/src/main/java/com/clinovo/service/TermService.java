package com.clinovo.service;

import java.util.List;

import com.clinovo.model.Dictionary;
import com.clinovo.model.Term;

public interface TermService {

	List<Term> findAll();
	
	Term findTerm(int termId);

	Term findTerm(String preferredName);

	Term findTermByCode(String code);

	List<Term> findTerm(Dictionary dictionary);

	Term saveTerm(Term term);

	void deleteTerm(Term term);

}
